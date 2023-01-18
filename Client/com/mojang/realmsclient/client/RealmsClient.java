/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.lang.Boolean
 *  java.lang.CharSequence
 *  java.lang.IllegalArgumentException
 *  java.lang.Long
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.System
 *  java.lang.Throwable
 *  java.net.URI
 *  java.net.URISyntaxException
 *  java.util.Locale
 *  java.util.UUID
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package com.mojang.realmsclient.client;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClientConfig;
import com.mojang.realmsclient.client.RealmsError;
import com.mojang.realmsclient.client.Request;
import com.mojang.realmsclient.dto.BackupList;
import com.mojang.realmsclient.dto.GuardedSerializer;
import com.mojang.realmsclient.dto.Ops;
import com.mojang.realmsclient.dto.PendingInvite;
import com.mojang.realmsclient.dto.PendingInvitesList;
import com.mojang.realmsclient.dto.PingResult;
import com.mojang.realmsclient.dto.PlayerInfo;
import com.mojang.realmsclient.dto.RealmsDescriptionDto;
import com.mojang.realmsclient.dto.RealmsNews;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerAddress;
import com.mojang.realmsclient.dto.RealmsServerList;
import com.mojang.realmsclient.dto.RealmsServerPlayerLists;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.dto.RealmsWorldResetDto;
import com.mojang.realmsclient.dto.ServerActivityList;
import com.mojang.realmsclient.dto.Subscription;
import com.mojang.realmsclient.dto.UploadInfo;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.dto.WorldTemplatePaginatedList;
import com.mojang.realmsclient.exception.RealmsHttpException;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.util.WorldGenerationInfo;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import org.slf4j.Logger;

public class RealmsClient {
    public static Environment currentEnvironment = Environment.PRODUCTION;
    private static boolean initialized;
    private static final Logger LOGGER;
    private final String sessionId;
    private final String username;
    private final Minecraft minecraft;
    private static final String WORLDS_RESOURCE_PATH = "worlds";
    private static final String INVITES_RESOURCE_PATH = "invites";
    private static final String MCO_RESOURCE_PATH = "mco";
    private static final String SUBSCRIPTION_RESOURCE = "subscriptions";
    private static final String ACTIVITIES_RESOURCE = "activities";
    private static final String OPS_RESOURCE = "ops";
    private static final String REGIONS_RESOURCE = "regions/ping/stat";
    private static final String TRIALS_RESOURCE = "trial";
    private static final String PATH_INITIALIZE = "/$WORLD_ID/initialize";
    private static final String PATH_GET_ACTIVTIES = "/$WORLD_ID";
    private static final String PATH_GET_LIVESTATS = "/liveplayerlist";
    private static final String PATH_GET_SUBSCRIPTION = "/$WORLD_ID";
    private static final String PATH_OP = "/$WORLD_ID/$PROFILE_UUID";
    private static final String PATH_PUT_INTO_MINIGAMES_MODE = "/minigames/$MINIGAME_ID/$WORLD_ID";
    private static final String PATH_AVAILABLE = "/available";
    private static final String PATH_TEMPLATES = "/templates/$WORLD_TYPE";
    private static final String PATH_WORLD_JOIN = "/v1/$ID/join/pc";
    private static final String PATH_WORLD_GET = "/$ID";
    private static final String PATH_WORLD_INVITES = "/$WORLD_ID";
    private static final String PATH_WORLD_UNINVITE = "/$WORLD_ID/invite/$UUID";
    private static final String PATH_PENDING_INVITES_COUNT = "/count/pending";
    private static final String PATH_PENDING_INVITES = "/pending";
    private static final String PATH_ACCEPT_INVITE = "/accept/$INVITATION_ID";
    private static final String PATH_REJECT_INVITE = "/reject/$INVITATION_ID";
    private static final String PATH_UNINVITE_MYSELF = "/$WORLD_ID";
    private static final String PATH_WORLD_UPDATE = "/$WORLD_ID";
    private static final String PATH_SLOT = "/$WORLD_ID/slot/$SLOT_ID";
    private static final String PATH_WORLD_OPEN = "/$WORLD_ID/open";
    private static final String PATH_WORLD_CLOSE = "/$WORLD_ID/close";
    private static final String PATH_WORLD_RESET = "/$WORLD_ID/reset";
    private static final String PATH_DELETE_WORLD = "/$WORLD_ID";
    private static final String PATH_WORLD_BACKUPS = "/$WORLD_ID/backups";
    private static final String PATH_WORLD_DOWNLOAD = "/$WORLD_ID/slot/$SLOT_ID/download";
    private static final String PATH_WORLD_UPLOAD = "/$WORLD_ID/backups/upload";
    private static final String PATH_CLIENT_COMPATIBLE = "/client/compatible";
    private static final String PATH_TOS_AGREED = "/tos/agreed";
    private static final String PATH_NEWS = "/v1/news";
    private static final String PATH_STAGE_AVAILABLE = "/stageAvailable";
    private static final GuardedSerializer GSON;

    public static RealmsClient create() {
        Minecraft $$0 = Minecraft.getInstance();
        return RealmsClient.create($$0);
    }

    public static RealmsClient create(Minecraft $$0) {
        String $$1 = $$0.getUser().getName();
        String $$2 = $$0.getUser().getSessionId();
        if (!initialized) {
            initialized = true;
            String $$3 = System.getenv((String)"realms.environment");
            if ($$3 == null) {
                $$3 = System.getProperty((String)"realms.environment");
            }
            if ($$3 != null) {
                if ("LOCAL".equals((Object)$$3)) {
                    RealmsClient.switchToLocal();
                } else if ("STAGE".equals((Object)$$3)) {
                    RealmsClient.switchToStage();
                }
            }
        }
        return new RealmsClient($$2, $$1, $$0);
    }

    public static void switchToStage() {
        currentEnvironment = Environment.STAGE;
    }

    public static void switchToProd() {
        currentEnvironment = Environment.PRODUCTION;
    }

    public static void switchToLocal() {
        currentEnvironment = Environment.LOCAL;
    }

    public RealmsClient(String $$0, String $$1, Minecraft $$2) {
        this.sessionId = $$0;
        this.username = $$1;
        this.minecraft = $$2;
        RealmsClientConfig.setProxy($$2.getProxy());
    }

    public RealmsServerList listWorlds() throws RealmsServiceException {
        String $$0 = this.url(WORLDS_RESOURCE_PATH);
        String $$1 = this.execute(Request.get($$0));
        return RealmsServerList.parse($$1);
    }

    public RealmsServer getOwnWorld(long $$0) throws RealmsServiceException {
        String $$1 = this.url(WORLDS_RESOURCE_PATH + PATH_WORLD_GET.replace((CharSequence)"$ID", (CharSequence)String.valueOf((long)$$0)));
        String $$2 = this.execute(Request.get($$1));
        return RealmsServer.parse($$2);
    }

    public ServerActivityList getActivity(long $$0) throws RealmsServiceException {
        String $$1 = this.url(ACTIVITIES_RESOURCE + "/$WORLD_ID".replace((CharSequence)"$WORLD_ID", (CharSequence)String.valueOf((long)$$0)));
        String $$2 = this.execute(Request.get($$1));
        return ServerActivityList.parse($$2);
    }

    public RealmsServerPlayerLists getLiveStats() throws RealmsServiceException {
        String $$0 = this.url("activities/liveplayerlist");
        String $$1 = this.execute(Request.get($$0));
        return RealmsServerPlayerLists.parse($$1);
    }

    public RealmsServerAddress join(long $$0) throws RealmsServiceException {
        String $$1 = this.url(WORLDS_RESOURCE_PATH + PATH_WORLD_JOIN.replace((CharSequence)"$ID", (CharSequence)("" + $$0)));
        String $$2 = this.execute(Request.get($$1, 5000, 30000));
        return RealmsServerAddress.parse($$2);
    }

    public void initializeWorld(long $$0, String $$1, String $$2) throws RealmsServiceException {
        RealmsDescriptionDto $$3 = new RealmsDescriptionDto($$1, $$2);
        String $$4 = this.url(WORLDS_RESOURCE_PATH + PATH_INITIALIZE.replace((CharSequence)"$WORLD_ID", (CharSequence)String.valueOf((long)$$0)));
        String $$5 = GSON.toJson($$3);
        this.execute(Request.post($$4, $$5, 5000, 10000));
    }

    public Boolean mcoEnabled() throws RealmsServiceException {
        String $$0 = this.url("mco/available");
        String $$1 = this.execute(Request.get($$0));
        return Boolean.valueOf((String)$$1);
    }

    public Boolean stageAvailable() throws RealmsServiceException {
        String $$0 = this.url("mco/stageAvailable");
        String $$1 = this.execute(Request.get($$0));
        return Boolean.valueOf((String)$$1);
    }

    /*
     * WARNING - void declaration
     */
    public CompatibleVersionResponse clientCompatible() throws RealmsServiceException {
        void $$4;
        String $$0 = this.url("mco/client/compatible");
        String $$1 = this.execute(Request.get($$0));
        try {
            CompatibleVersionResponse $$2 = CompatibleVersionResponse.valueOf($$1);
        }
        catch (IllegalArgumentException $$3) {
            throw new RealmsServiceException(500, "Could not check compatible version, got response: " + $$1);
        }
        return $$4;
    }

    public void uninvite(long $$0, String $$1) throws RealmsServiceException {
        String $$2 = this.url(INVITES_RESOURCE_PATH + PATH_WORLD_UNINVITE.replace((CharSequence)"$WORLD_ID", (CharSequence)String.valueOf((long)$$0)).replace((CharSequence)"$UUID", (CharSequence)$$1));
        this.execute(Request.delete($$2));
    }

    public void uninviteMyselfFrom(long $$0) throws RealmsServiceException {
        String $$1 = this.url(INVITES_RESOURCE_PATH + "/$WORLD_ID".replace((CharSequence)"$WORLD_ID", (CharSequence)String.valueOf((long)$$0)));
        this.execute(Request.delete($$1));
    }

    public RealmsServer invite(long $$0, String $$1) throws RealmsServiceException {
        PlayerInfo $$2 = new PlayerInfo();
        $$2.setName($$1);
        String $$3 = this.url(INVITES_RESOURCE_PATH + "/$WORLD_ID".replace((CharSequence)"$WORLD_ID", (CharSequence)String.valueOf((long)$$0)));
        String $$4 = this.execute(Request.post($$3, GSON.toJson($$2)));
        return RealmsServer.parse($$4);
    }

    public BackupList backupsFor(long $$0) throws RealmsServiceException {
        String $$1 = this.url(WORLDS_RESOURCE_PATH + PATH_WORLD_BACKUPS.replace((CharSequence)"$WORLD_ID", (CharSequence)String.valueOf((long)$$0)));
        String $$2 = this.execute(Request.get($$1));
        return BackupList.parse($$2);
    }

    public void update(long $$0, String $$1, String $$2) throws RealmsServiceException {
        RealmsDescriptionDto $$3 = new RealmsDescriptionDto($$1, $$2);
        String $$4 = this.url(WORLDS_RESOURCE_PATH + "/$WORLD_ID".replace((CharSequence)"$WORLD_ID", (CharSequence)String.valueOf((long)$$0)));
        this.execute(Request.post($$4, GSON.toJson($$3)));
    }

    public void updateSlot(long $$0, int $$1, RealmsWorldOptions $$2) throws RealmsServiceException {
        String $$3 = this.url(WORLDS_RESOURCE_PATH + PATH_SLOT.replace((CharSequence)"$WORLD_ID", (CharSequence)String.valueOf((long)$$0)).replace((CharSequence)"$SLOT_ID", (CharSequence)String.valueOf((int)$$1)));
        String $$4 = $$2.toJson();
        this.execute(Request.post($$3, $$4));
    }

    public boolean switchSlot(long $$0, int $$1) throws RealmsServiceException {
        String $$2 = this.url(WORLDS_RESOURCE_PATH + PATH_SLOT.replace((CharSequence)"$WORLD_ID", (CharSequence)String.valueOf((long)$$0)).replace((CharSequence)"$SLOT_ID", (CharSequence)String.valueOf((int)$$1)));
        String $$3 = this.execute(Request.put($$2, ""));
        return Boolean.valueOf((String)$$3);
    }

    public void restoreWorld(long $$0, String $$1) throws RealmsServiceException {
        String $$2 = this.url(WORLDS_RESOURCE_PATH + PATH_WORLD_BACKUPS.replace((CharSequence)"$WORLD_ID", (CharSequence)String.valueOf((long)$$0)), "backupId=" + $$1);
        this.execute(Request.put($$2, "", 40000, 600000));
    }

    public WorldTemplatePaginatedList fetchWorldTemplates(int $$0, int $$1, RealmsServer.WorldType $$2) throws RealmsServiceException {
        String $$3 = this.url(WORLDS_RESOURCE_PATH + PATH_TEMPLATES.replace((CharSequence)"$WORLD_TYPE", (CharSequence)$$2.toString()), String.format((Locale)Locale.ROOT, (String)"page=%d&pageSize=%d", (Object[])new Object[]{$$0, $$1}));
        String $$4 = this.execute(Request.get($$3));
        return WorldTemplatePaginatedList.parse($$4);
    }

    public Boolean putIntoMinigameMode(long $$0, String $$1) throws RealmsServiceException {
        String $$2 = PATH_PUT_INTO_MINIGAMES_MODE.replace((CharSequence)"$MINIGAME_ID", (CharSequence)$$1).replace((CharSequence)"$WORLD_ID", (CharSequence)String.valueOf((long)$$0));
        String $$3 = this.url(WORLDS_RESOURCE_PATH + $$2);
        return Boolean.valueOf((String)this.execute(Request.put($$3, "")));
    }

    public Ops op(long $$0, String $$1) throws RealmsServiceException {
        String $$2 = PATH_OP.replace((CharSequence)"$WORLD_ID", (CharSequence)String.valueOf((long)$$0)).replace((CharSequence)"$PROFILE_UUID", (CharSequence)$$1);
        String $$3 = this.url(OPS_RESOURCE + $$2);
        return Ops.parse(this.execute(Request.post($$3, "")));
    }

    public Ops deop(long $$0, String $$1) throws RealmsServiceException {
        String $$2 = PATH_OP.replace((CharSequence)"$WORLD_ID", (CharSequence)String.valueOf((long)$$0)).replace((CharSequence)"$PROFILE_UUID", (CharSequence)$$1);
        String $$3 = this.url(OPS_RESOURCE + $$2);
        return Ops.parse(this.execute(Request.delete($$3)));
    }

    public Boolean open(long $$0) throws RealmsServiceException {
        String $$1 = this.url(WORLDS_RESOURCE_PATH + PATH_WORLD_OPEN.replace((CharSequence)"$WORLD_ID", (CharSequence)String.valueOf((long)$$0)));
        String $$2 = this.execute(Request.put($$1, ""));
        return Boolean.valueOf((String)$$2);
    }

    public Boolean close(long $$0) throws RealmsServiceException {
        String $$1 = this.url(WORLDS_RESOURCE_PATH + PATH_WORLD_CLOSE.replace((CharSequence)"$WORLD_ID", (CharSequence)String.valueOf((long)$$0)));
        String $$2 = this.execute(Request.put($$1, ""));
        return Boolean.valueOf((String)$$2);
    }

    public Boolean resetWorldWithSeed(long $$0, WorldGenerationInfo $$1) throws RealmsServiceException {
        RealmsWorldResetDto $$2 = new RealmsWorldResetDto($$1.getSeed(), -1L, $$1.getLevelType().getDtoIndex(), $$1.shouldGenerateStructures());
        String $$3 = this.url(WORLDS_RESOURCE_PATH + PATH_WORLD_RESET.replace((CharSequence)"$WORLD_ID", (CharSequence)String.valueOf((long)$$0)));
        String $$4 = this.execute(Request.post($$3, GSON.toJson($$2), 30000, 80000));
        return Boolean.valueOf((String)$$4);
    }

    public Boolean resetWorldWithTemplate(long $$0, String $$1) throws RealmsServiceException {
        RealmsWorldResetDto $$2 = new RealmsWorldResetDto(null, Long.valueOf((String)$$1), -1, false);
        String $$3 = this.url(WORLDS_RESOURCE_PATH + PATH_WORLD_RESET.replace((CharSequence)"$WORLD_ID", (CharSequence)String.valueOf((long)$$0)));
        String $$4 = this.execute(Request.post($$3, GSON.toJson($$2), 30000, 80000));
        return Boolean.valueOf((String)$$4);
    }

    public Subscription subscriptionFor(long $$0) throws RealmsServiceException {
        String $$1 = this.url(SUBSCRIPTION_RESOURCE + "/$WORLD_ID".replace((CharSequence)"$WORLD_ID", (CharSequence)String.valueOf((long)$$0)));
        String $$2 = this.execute(Request.get($$1));
        return Subscription.parse($$2);
    }

    public int pendingInvitesCount() throws RealmsServiceException {
        return this.pendingInvites().pendingInvites.size();
    }

    public PendingInvitesList pendingInvites() throws RealmsServiceException {
        String $$0 = this.url("invites/pending");
        String $$1 = this.execute(Request.get($$0));
        PendingInvitesList $$2 = PendingInvitesList.parse($$1);
        $$2.pendingInvites.removeIf(this::isBlocked);
        return $$2;
    }

    private boolean isBlocked(PendingInvite $$0) {
        try {
            UUID $$1 = UUID.fromString((String)$$0.worldOwnerUuid);
            return this.minecraft.getPlayerSocialManager().isBlocked($$1);
        }
        catch (IllegalArgumentException $$2) {
            return false;
        }
    }

    public void acceptInvitation(String $$0) throws RealmsServiceException {
        String $$1 = this.url(INVITES_RESOURCE_PATH + PATH_ACCEPT_INVITE.replace((CharSequence)"$INVITATION_ID", (CharSequence)$$0));
        this.execute(Request.put($$1, ""));
    }

    public WorldDownload requestDownloadInfo(long $$0, int $$1) throws RealmsServiceException {
        String $$2 = this.url(WORLDS_RESOURCE_PATH + PATH_WORLD_DOWNLOAD.replace((CharSequence)"$WORLD_ID", (CharSequence)String.valueOf((long)$$0)).replace((CharSequence)"$SLOT_ID", (CharSequence)String.valueOf((int)$$1)));
        String $$3 = this.execute(Request.get($$2));
        return WorldDownload.parse($$3);
    }

    @Nullable
    public UploadInfo requestUploadInfo(long $$0, @Nullable String $$1) throws RealmsServiceException {
        String $$2 = this.url(WORLDS_RESOURCE_PATH + PATH_WORLD_UPLOAD.replace((CharSequence)"$WORLD_ID", (CharSequence)String.valueOf((long)$$0)));
        return UploadInfo.parse(this.execute(Request.put($$2, UploadInfo.createRequest($$1))));
    }

    public void rejectInvitation(String $$0) throws RealmsServiceException {
        String $$1 = this.url(INVITES_RESOURCE_PATH + PATH_REJECT_INVITE.replace((CharSequence)"$INVITATION_ID", (CharSequence)$$0));
        this.execute(Request.put($$1, ""));
    }

    public void agreeToTos() throws RealmsServiceException {
        String $$0 = this.url("mco/tos/agreed");
        this.execute(Request.post($$0, ""));
    }

    public RealmsNews getNews() throws RealmsServiceException {
        String $$0 = this.url("mco/v1/news");
        String $$1 = this.execute(Request.get($$0, 5000, 10000));
        return RealmsNews.parse($$1);
    }

    public void sendPingResults(PingResult $$0) throws RealmsServiceException {
        String $$1 = this.url(REGIONS_RESOURCE);
        this.execute(Request.post($$1, GSON.toJson($$0)));
    }

    public Boolean trialAvailable() throws RealmsServiceException {
        String $$0 = this.url(TRIALS_RESOURCE);
        String $$1 = this.execute(Request.get($$0));
        return Boolean.valueOf((String)$$1);
    }

    public void deleteWorld(long $$0) throws RealmsServiceException {
        String $$1 = this.url(WORLDS_RESOURCE_PATH + "/$WORLD_ID".replace((CharSequence)"$WORLD_ID", (CharSequence)String.valueOf((long)$$0)));
        this.execute(Request.delete($$1));
    }

    private String url(String $$0) {
        return this.url($$0, null);
    }

    private String url(String $$0, @Nullable String $$1) {
        try {
            return new URI(RealmsClient.currentEnvironment.protocol, RealmsClient.currentEnvironment.baseUrl, "/" + $$0, $$1, null).toASCIIString();
        }
        catch (URISyntaxException $$2) {
            throw new IllegalArgumentException($$0, (Throwable)$$2);
        }
    }

    private String execute(Request<?> $$0) throws RealmsServiceException {
        $$0.cookie("sid", this.sessionId);
        $$0.cookie("user", this.username);
        $$0.cookie("version", SharedConstants.getCurrentVersion().getName());
        try {
            int $$1 = $$0.responseCode();
            if ($$1 == 503 || $$1 == 277) {
                int $$2 = $$0.getRetryAfterHeader();
                throw new RetryCallException($$2, $$1);
            }
            String $$3 = $$0.text();
            if ($$1 < 200 || $$1 >= 300) {
                if ($$1 == 401) {
                    String $$4 = $$0.getHeader("WWW-Authenticate");
                    LOGGER.info("Could not authorize you against Realms server: {}", (Object)$$4);
                    throw new RealmsServiceException($$1, $$4);
                }
                RealmsError $$5 = RealmsError.parse($$3);
                if ($$5 != null) {
                    LOGGER.error("Realms http code: {} -  error code: {} -  message: {} - raw body: {}", new Object[]{$$1, $$5.getErrorCode(), $$5.getErrorMessage(), $$3});
                    throw new RealmsServiceException($$1, $$3, $$5);
                }
                LOGGER.error("Realms http code: {} - raw body (message failed to parse): {}", (Object)$$1, (Object)$$3);
                String $$6 = RealmsClient.getHttpCodeDescription($$1);
                throw new RealmsServiceException($$1, $$6);
            }
            return $$3;
        }
        catch (RealmsHttpException $$7) {
            throw new RealmsServiceException(500, "Could not connect to Realms: " + $$7.getMessage());
        }
    }

    private static String getHttpCodeDescription(int $$0) {
        return switch ($$0) {
            case 429 -> I18n.get("mco.errorMessage.serviceBusy", new Object[0]);
            default -> "Unknown error";
        };
    }

    static {
        LOGGER = LogUtils.getLogger();
        GSON = new GuardedSerializer();
    }

    public static enum Environment {
        PRODUCTION("pc.realms.minecraft.net", "https"),
        STAGE("pc-stage.realms.minecraft.net", "https"),
        LOCAL("localhost:8080", "http");

        public String baseUrl;
        public String protocol;

        private Environment(String $$0, String $$1) {
            this.baseUrl = $$0;
            this.protocol = $$1;
        }
    }

    public static enum CompatibleVersionResponse {
        COMPATIBLE,
        OUTDATED,
        OTHER;

    }
}