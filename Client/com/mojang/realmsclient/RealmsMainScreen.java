/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.common.util.concurrent.RateLimiter
 *  com.mojang.logging.LogUtils
 *  java.lang.Boolean
 *  java.lang.InterruptedException
 *  java.lang.Long
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.Thread
 *  java.lang.Throwable
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.Set
 *  java.util.concurrent.TimeUnit
 *  java.util.concurrent.locks.ReentrantLock
 *  java.util.function.Predicate
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package com.mojang.realmsclient;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import com.mojang.math.Axis;
import com.mojang.realmsclient.KeyCombo;
import com.mojang.realmsclient.client.Ping;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.PingResult;
import com.mojang.realmsclient.dto.RealmsNews;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerPlayerList;
import com.mojang.realmsclient.dto.RegionPingResult;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsDataFetcher;
import com.mojang.realmsclient.gui.RealmsNewsManager;
import com.mojang.realmsclient.gui.RealmsServerList;
import com.mojang.realmsclient.gui.screens.RealmsClientOutdatedScreen;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsCreateRealmScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsParentalConsentScreen;
import com.mojang.realmsclient.gui.screens.RealmsPendingInvitesScreen;
import com.mojang.realmsclient.gui.task.DataFetcher;
import com.mojang.realmsclient.util.RealmsPersistence;
import com.mojang.realmsclient.util.RealmsTextureManager;
import com.mojang.realmsclient.util.task.GetServerDetailsTask;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import org.slf4j.Logger;

public class RealmsMainScreen
extends RealmsScreen {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceLocation ON_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/on_icon.png");
    private static final ResourceLocation OFF_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/off_icon.png");
    private static final ResourceLocation EXPIRED_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/expired_icon.png");
    private static final ResourceLocation EXPIRES_SOON_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/expires_soon_icon.png");
    private static final ResourceLocation LEAVE_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/leave_icon.png");
    private static final ResourceLocation INVITATION_ICONS_LOCATION = new ResourceLocation("realms", "textures/gui/realms/invitation_icons.png");
    private static final ResourceLocation INVITE_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/invite_icon.png");
    static final ResourceLocation WORLDICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/world_icon.png");
    private static final ResourceLocation LOGO_LOCATION = new ResourceLocation("realms", "textures/gui/title/realms.png");
    private static final ResourceLocation CONFIGURE_LOCATION = new ResourceLocation("realms", "textures/gui/realms/configure_icon.png");
    private static final ResourceLocation NEWS_LOCATION = new ResourceLocation("realms", "textures/gui/realms/news_icon.png");
    private static final ResourceLocation POPUP_LOCATION = new ResourceLocation("realms", "textures/gui/realms/popup.png");
    private static final ResourceLocation DARKEN_LOCATION = new ResourceLocation("realms", "textures/gui/realms/darken.png");
    static final ResourceLocation CROSS_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/cross_icon.png");
    private static final ResourceLocation TRIAL_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/trial_icon.png");
    static final ResourceLocation BUTTON_LOCATION = new ResourceLocation("minecraft", "textures/gui/widgets.png");
    static final Component NO_PENDING_INVITES_TEXT = Component.translatable("mco.invites.nopending");
    static final Component PENDING_INVITES_TEXT = Component.translatable("mco.invites.pending");
    static final List<Component> TRIAL_MESSAGE_LINES = ImmutableList.of((Object)Component.translatable("mco.trial.message.line1"), (Object)Component.translatable("mco.trial.message.line2"));
    static final Component SERVER_UNITIALIZED_TEXT = Component.translatable("mco.selectServer.uninitialized");
    static final Component SUBSCRIPTION_EXPIRED_TEXT = Component.translatable("mco.selectServer.expiredList");
    static final Component SUBSCRIPTION_RENEW_TEXT = Component.translatable("mco.selectServer.expiredRenew");
    static final Component TRIAL_EXPIRED_TEXT = Component.translatable("mco.selectServer.expiredTrial");
    static final Component SUBSCRIPTION_CREATE_TEXT = Component.translatable("mco.selectServer.expiredSubscribe");
    static final Component SELECT_MINIGAME_PREFIX = Component.translatable("mco.selectServer.minigame").append(CommonComponents.SPACE);
    private static final Component POPUP_TEXT = Component.translatable("mco.selectServer.popup");
    private static final Component SERVER_EXPIRED_TOOLTIP = Component.translatable("mco.selectServer.expired");
    private static final Component SERVER_EXPIRES_SOON_TOOLTIP = Component.translatable("mco.selectServer.expires.soon");
    private static final Component SERVER_EXPIRES_IN_DAY_TOOLTIP = Component.translatable("mco.selectServer.expires.day");
    private static final Component SERVER_OPEN_TOOLTIP = Component.translatable("mco.selectServer.open");
    private static final Component SERVER_CLOSED_TOOLTIP = Component.translatable("mco.selectServer.closed");
    private static final Component LEAVE_SERVER_TOOLTIP = Component.translatable("mco.selectServer.leave");
    private static final Component CONFIGURE_SERVER_TOOLTIP = Component.translatable("mco.selectServer.configureRealm");
    private static final Component NEWS_TOOLTIP = Component.translatable("mco.news");
    static final Component UNITIALIZED_WORLD_NARRATION = Component.translatable("gui.narrate.button", SERVER_UNITIALIZED_TEXT);
    static final Component TRIAL_TEXT = CommonComponents.joinLines(TRIAL_MESSAGE_LINES);
    private static List<ResourceLocation> teaserImages = ImmutableList.of();
    @Nullable
    private DataFetcher.Subscription dataSubscription;
    private RealmsServerList serverList;
    static boolean overrideConfigure;
    private static int lastScrollYPosition;
    static volatile boolean hasParentalConsent;
    static volatile boolean checkedParentalConsent;
    static volatile boolean checkedClientCompatability;
    @Nullable
    static Screen realmsGenericErrorScreen;
    private static boolean regionsPinged;
    private final RateLimiter inviteNarrationLimiter;
    private boolean dontSetConnectedToRealms;
    final Screen lastScreen;
    RealmSelectionList realmSelectionList;
    private boolean realmsSelectionListAdded;
    private Button playButton;
    private Button backButton;
    private Button renewButton;
    private Button configureButton;
    private Button leaveButton;
    private List<RealmsServer> realmsServers = ImmutableList.of();
    volatile int numberOfPendingInvites;
    int animTick;
    private boolean hasFetchedServers;
    boolean popupOpenedByUser;
    private boolean justClosedPopup;
    private volatile boolean trialsAvailable;
    private volatile boolean createdTrial;
    private volatile boolean showingPopup;
    volatile boolean hasUnreadNews;
    @Nullable
    volatile String newsLink;
    private int carouselIndex;
    private int carouselTick;
    private boolean hasSwitchedCarouselImage;
    private List<KeyCombo> keyCombos;
    long lastClickTime;
    private ReentrantLock connectLock = new ReentrantLock();
    private MultiLineLabel formattedPopup = MultiLineLabel.EMPTY;
    HoveredElement hoveredElement;
    private Button showPopupButton;
    private PendingInvitesButton pendingInvitesButton;
    private Button newsButton;
    private Button createTrialButton;
    private Button buyARealmButton;
    private Button closeButton;

    public RealmsMainScreen(Screen $$0) {
        super(GameNarrator.NO_TITLE);
        this.lastScreen = $$0;
        this.inviteNarrationLimiter = RateLimiter.create((double)0.01666666753590107);
    }

    private boolean shouldShowMessageInList() {
        if (!RealmsMainScreen.hasParentalConsent() || !this.hasFetchedServers) {
            return false;
        }
        if (this.trialsAvailable && !this.createdTrial) {
            return true;
        }
        for (RealmsServer $$0 : this.realmsServers) {
            if (!$$0.ownerUUID.equals((Object)this.minecraft.getUser().getUuid())) continue;
            return false;
        }
        return true;
    }

    public boolean shouldShowPopup() {
        if (!RealmsMainScreen.hasParentalConsent() || !this.hasFetchedServers) {
            return false;
        }
        if (this.popupOpenedByUser) {
            return true;
        }
        return this.realmsServers.isEmpty();
    }

    @Override
    public void init() {
        this.keyCombos = Lists.newArrayList((Object[])new KeyCombo[]{new KeyCombo(new char[]{'3', '2', '1', '4', '5', '6'}, () -> {
            overrideConfigure = !overrideConfigure;
        }), new KeyCombo(new char[]{'9', '8', '7', '1', '2', '3'}, () -> {
            if (RealmsClient.currentEnvironment == RealmsClient.Environment.STAGE) {
                this.switchToProd();
            } else {
                this.switchToStage();
            }
        }), new KeyCombo(new char[]{'9', '8', '7', '4', '5', '6'}, () -> {
            if (RealmsClient.currentEnvironment == RealmsClient.Environment.LOCAL) {
                this.switchToProd();
            } else {
                this.switchToLocal();
            }
        })});
        if (realmsGenericErrorScreen != null) {
            this.minecraft.setScreen(realmsGenericErrorScreen);
            return;
        }
        this.connectLock = new ReentrantLock();
        if (checkedClientCompatability && !RealmsMainScreen.hasParentalConsent()) {
            this.checkParentalConsent();
        }
        this.checkClientCompatability();
        if (!this.dontSetConnectedToRealms) {
            this.minecraft.setConnectedToRealms(false);
        }
        this.showingPopup = false;
        this.addTopButtons();
        this.realmSelectionList = new RealmSelectionList();
        if (lastScrollYPosition != -1) {
            this.realmSelectionList.setScrollAmount(lastScrollYPosition);
        }
        this.addWidget(this.realmSelectionList);
        this.realmsSelectionListAdded = true;
        this.setInitialFocus(this.realmSelectionList);
        this.addMiddleButtons();
        this.addBottomButtons();
        this.updateButtonStates(null);
        this.formattedPopup = MultiLineLabel.create(this.font, (FormattedText)POPUP_TEXT, 100);
        RealmsNewsManager $$0 = this.minecraft.realmsDataFetcher().newsManager;
        this.hasUnreadNews = $$0.hasUnreadNews();
        this.newsLink = $$0.newsLink();
        if (this.serverList == null) {
            this.serverList = new RealmsServerList(this.minecraft);
        }
        if (this.dataSubscription != null) {
            this.dataSubscription.forceUpdate();
        }
    }

    private static boolean hasParentalConsent() {
        return checkedParentalConsent && hasParentalConsent;
    }

    public void addTopButtons() {
        this.pendingInvitesButton = this.addRenderableWidget(new PendingInvitesButton());
        this.newsButton = this.addRenderableWidget(new NewsButton());
        this.showPopupButton = this.addRenderableWidget(Button.builder(Component.translatable("mco.selectServer.purchase"), $$0 -> {
            this.popupOpenedByUser = !this.popupOpenedByUser;
        }).bounds(this.width - 90, 6, 80, 20).build());
    }

    public void addMiddleButtons() {
        this.createTrialButton = this.addRenderableWidget(Button.builder(Component.translatable("mco.selectServer.trial"), $$0 -> {
            if (!this.trialsAvailable || this.createdTrial) {
                return;
            }
            Util.getPlatform().openUri("https://aka.ms/startjavarealmstrial");
            this.minecraft.setScreen(this.lastScreen);
        }).bounds(this.width / 2 + 52, this.popupY0() + 137 - 20, 98, 20).build());
        this.buyARealmButton = this.addRenderableWidget(Button.builder(Component.translatable("mco.selectServer.buy"), $$0 -> Util.getPlatform().openUri("https://aka.ms/BuyJavaRealms")).bounds(this.width / 2 + 52, this.popupY0() + 160 - 20, 98, 20).build());
        this.closeButton = this.addRenderableWidget(new CloseButton());
    }

    public void addBottomButtons() {
        this.configureButton = this.addRenderableWidget(Button.builder(Component.translatable("mco.selectServer.configure"), $$0 -> this.configureClicked(this.getSelectedServer())).bounds(this.width / 2 - 190, this.height - 32, 90, 20).build());
        this.leaveButton = this.addRenderableWidget(Button.builder(Component.translatable("mco.selectServer.leave"), $$0 -> this.leaveClicked(this.getSelectedServer())).bounds(this.width / 2 - 190, this.height - 32, 90, 20).build());
        this.playButton = this.addRenderableWidget(Button.builder(Component.translatable("mco.selectServer.play"), $$0 -> this.play(this.getSelectedServer(), this)).bounds(this.width / 2 - 93, this.height - 32, 90, 20).build());
        this.backButton = this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, $$0 -> {
            if (!this.justClosedPopup) {
                this.minecraft.setScreen(this.lastScreen);
            }
        }).bounds(this.width / 2 + 4, this.height - 32, 90, 20).build());
        this.renewButton = this.addRenderableWidget(Button.builder(Component.translatable("mco.selectServer.expiredRenew"), $$0 -> this.onRenew(this.getSelectedServer())).bounds(this.width / 2 + 100, this.height - 32, 90, 20).build());
    }

    void updateButtonStates(@Nullable RealmsServer $$0) {
        boolean $$1;
        this.backButton.active = true;
        if (!RealmsMainScreen.hasParentalConsent() || !this.hasFetchedServers) {
            RealmsMainScreen.hideWidgets(this.playButton, this.renewButton, this.configureButton, this.createTrialButton, this.buyARealmButton, this.closeButton, this.newsButton, this.pendingInvitesButton, this.showPopupButton, this.leaveButton);
            return;
        }
        this.playButton.visible = true;
        this.playButton.active = this.shouldPlayButtonBeActive($$0) && !this.shouldShowPopup();
        this.renewButton.visible = this.shouldRenewButtonBeActive($$0);
        this.configureButton.visible = this.shouldConfigureButtonBeVisible($$0);
        this.leaveButton.visible = this.shouldLeaveButtonBeVisible($$0);
        this.createTrialButton.visible = $$1 = this.shouldShowPopup() && this.trialsAvailable && !this.createdTrial;
        this.createTrialButton.active = $$1;
        this.buyARealmButton.visible = this.shouldShowPopup();
        this.closeButton.visible = this.shouldShowPopup() && this.popupOpenedByUser;
        this.renewButton.active = !this.shouldShowPopup();
        this.configureButton.active = !this.shouldShowPopup();
        this.leaveButton.active = !this.shouldShowPopup();
        this.newsButton.active = true;
        this.newsButton.visible = this.newsLink != null;
        this.pendingInvitesButton.active = true;
        this.pendingInvitesButton.visible = true;
        this.showPopupButton.active = !this.shouldShowPopup();
    }

    private boolean shouldShowPopupButton() {
        return (!this.shouldShowPopup() || this.popupOpenedByUser) && RealmsMainScreen.hasParentalConsent() && this.hasFetchedServers;
    }

    boolean shouldPlayButtonBeActive(@Nullable RealmsServer $$0) {
        return $$0 != null && !$$0.expired && $$0.state == RealmsServer.State.OPEN;
    }

    private boolean shouldRenewButtonBeActive(@Nullable RealmsServer $$0) {
        return $$0 != null && $$0.expired && this.isSelfOwnedServer($$0);
    }

    private boolean shouldConfigureButtonBeVisible(@Nullable RealmsServer $$0) {
        return $$0 != null && this.isSelfOwnedServer($$0);
    }

    private boolean shouldLeaveButtonBeVisible(@Nullable RealmsServer $$0) {
        return $$0 != null && !this.isSelfOwnedServer($$0);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.pendingInvitesButton != null) {
            this.pendingInvitesButton.tick();
        }
        this.justClosedPopup = false;
        ++this.animTick;
        boolean $$0 = RealmsMainScreen.hasParentalConsent();
        if (this.dataSubscription == null && $$0) {
            this.dataSubscription = this.initDataFetcher(this.minecraft.realmsDataFetcher());
        } else if (this.dataSubscription != null && !$$0) {
            this.dataSubscription = null;
        }
        if (this.dataSubscription != null) {
            this.dataSubscription.tick();
        }
        if (this.shouldShowPopup()) {
            ++this.carouselTick;
        }
        if (this.showPopupButton != null) {
            this.showPopupButton.active = this.showPopupButton.visible = this.shouldShowPopupButton();
        }
    }

    private DataFetcher.Subscription initDataFetcher(RealmsDataFetcher $$02) {
        DataFetcher.Subscription $$12 = $$02.dataFetcher.createSubscription();
        $$12.subscribe($$02.serverListUpdateTask, $$0 -> {
            boolean $$4;
            List<RealmsServer> $$1 = this.serverList.updateServersList((List<RealmsServer>)$$0);
            RealmsServer $$2 = this.getSelectedServer();
            ServerEntry $$3 = null;
            this.realmSelectionList.clear();
            boolean bl = $$4 = !this.hasFetchedServers;
            if ($$4) {
                this.hasFetchedServers = true;
            }
            boolean $$5 = false;
            for (RealmsServer $$6 : $$1) {
                if (!this.isSelfOwnedNonExpiredServer($$6)) continue;
                $$5 = true;
            }
            this.realmsServers = $$1;
            if (this.shouldShowMessageInList()) {
                this.realmSelectionList.addEntry(new TrialEntry());
            }
            for (RealmsServer $$7 : this.realmsServers) {
                ServerEntry $$8 = new ServerEntry($$7);
                this.realmSelectionList.addEntry($$8);
                if ($$2 == null || $$2.id != $$7.id) continue;
                $$3 = $$8;
            }
            if (!regionsPinged && $$5) {
                regionsPinged = true;
                this.pingRegions();
            }
            if ($$4) {
                this.updateButtonStates(null);
            } else {
                this.realmSelectionList.setSelected($$3);
            }
        });
        $$12.subscribe($$02.pendingInvitesTask, $$0 -> {
            this.numberOfPendingInvites = $$0;
            if (this.numberOfPendingInvites > 0 && this.inviteNarrationLimiter.tryAcquire(1)) {
                this.minecraft.getNarrator().sayNow(Component.translatable("mco.configure.world.invite.narration", this.numberOfPendingInvites));
            }
        });
        $$12.subscribe($$02.trialAvailabilityTask, $$0 -> {
            if (this.createdTrial) {
                return;
            }
            if ($$0 != this.trialsAvailable && this.shouldShowPopup()) {
                this.trialsAvailable = $$0;
                this.showingPopup = false;
            } else {
                this.trialsAvailable = $$0;
            }
        });
        $$12.subscribe($$02.liveStatsTask, $$0 -> {
            block0: for (RealmsServerPlayerList $$1 : $$0.servers) {
                for (RealmsServer $$2 : this.realmsServers) {
                    if ($$2.id != $$1.serverId) continue;
                    $$2.updateServerPing($$1);
                    continue block0;
                }
            }
        });
        $$12.subscribe($$02.newsTask, $$1 -> {
            $$0.newsManager.updateUnreadNews((RealmsNews)$$1);
            this.hasUnreadNews = $$0.newsManager.hasUnreadNews();
            this.newsLink = $$0.newsManager.newsLink();
            this.updateButtonStates(null);
        });
        return $$12;
    }

    void refreshFetcher() {
        if (this.dataSubscription != null) {
            this.dataSubscription.reset();
        }
    }

    private void pingRegions() {
        new Thread(() -> {
            List<RegionPingResult> $$0 = Ping.pingAllRegions();
            RealmsClient $$1 = RealmsClient.create();
            PingResult $$2 = new PingResult();
            $$2.pingResults = $$0;
            $$2.worldIds = this.getOwnedNonExpiredWorldIds();
            try {
                $$1.sendPingResults($$2);
            }
            catch (Throwable $$3) {
                LOGGER.warn("Could not send ping result to Realms: ", $$3);
            }
        }).start();
    }

    private List<Long> getOwnedNonExpiredWorldIds() {
        ArrayList $$0 = Lists.newArrayList();
        for (RealmsServer $$1 : this.realmsServers) {
            if (!this.isSelfOwnedNonExpiredServer($$1)) continue;
            $$0.add((Object)$$1.id);
        }
        return $$0;
    }

    public void setCreatedTrial(boolean $$0) {
        this.createdTrial = $$0;
    }

    void onRenew(@Nullable RealmsServer $$0) {
        if ($$0 != null) {
            String $$1 = "https://aka.ms/ExtendJavaRealms?subscriptionId=" + $$0.remoteSubscriptionId + "&profileId=" + this.minecraft.getUser().getUuid() + "&ref=" + ($$0.expiredTrial ? "expiredTrial" : "expiredRealm");
            this.minecraft.keyboardHandler.setClipboard($$1);
            Util.getPlatform().openUri($$1);
        }
    }

    private void checkClientCompatability() {
        if (!checkedClientCompatability) {
            checkedClientCompatability = true;
            new Thread("MCO Compatability Checker #1"){

                public void run() {
                    RealmsClient $$0 = RealmsClient.create();
                    try {
                        RealmsClient.CompatibleVersionResponse $$1 = $$0.clientCompatible();
                        if ($$1 != RealmsClient.CompatibleVersionResponse.COMPATIBLE) {
                            realmsGenericErrorScreen = new RealmsClientOutdatedScreen(RealmsMainScreen.this.lastScreen);
                            RealmsMainScreen.this.minecraft.execute(() -> RealmsMainScreen.this.minecraft.setScreen(realmsGenericErrorScreen));
                            return;
                        }
                        RealmsMainScreen.this.checkParentalConsent();
                    }
                    catch (RealmsServiceException $$2) {
                        checkedClientCompatability = false;
                        LOGGER.error("Couldn't connect to realms", (Throwable)$$2);
                        if ($$2.httpResultCode == 401) {
                            realmsGenericErrorScreen = new RealmsGenericErrorScreen(Component.translatable("mco.error.invalid.session.title"), Component.translatable("mco.error.invalid.session.message"), RealmsMainScreen.this.lastScreen);
                            RealmsMainScreen.this.minecraft.execute(() -> RealmsMainScreen.this.minecraft.setScreen(realmsGenericErrorScreen));
                        }
                        RealmsMainScreen.this.minecraft.execute(() -> RealmsMainScreen.this.minecraft.setScreen(new RealmsGenericErrorScreen($$2, RealmsMainScreen.this.lastScreen)));
                    }
                }
            }.start();
        }
    }

    void checkParentalConsent() {
        new Thread("MCO Compatability Checker #1"){

            public void run() {
                RealmsClient $$0 = RealmsClient.create();
                try {
                    Boolean $$1 = $$0.mcoEnabled();
                    if ($$1.booleanValue()) {
                        LOGGER.info("Realms is available for this user");
                        hasParentalConsent = true;
                    } else {
                        LOGGER.info("Realms is not available for this user");
                        hasParentalConsent = false;
                        RealmsMainScreen.this.minecraft.execute(() -> RealmsMainScreen.this.minecraft.setScreen(new RealmsParentalConsentScreen(RealmsMainScreen.this.lastScreen)));
                    }
                    checkedParentalConsent = true;
                }
                catch (RealmsServiceException $$2) {
                    LOGGER.error("Couldn't connect to realms", (Throwable)$$2);
                    RealmsMainScreen.this.minecraft.execute(() -> RealmsMainScreen.this.minecraft.setScreen(new RealmsGenericErrorScreen($$2, RealmsMainScreen.this.lastScreen)));
                }
            }
        }.start();
    }

    private void switchToStage() {
        if (RealmsClient.currentEnvironment != RealmsClient.Environment.STAGE) {
            new Thread("MCO Stage Availability Checker #1"){

                public void run() {
                    RealmsClient $$0 = RealmsClient.create();
                    try {
                        Boolean $$1 = $$0.stageAvailable();
                        if ($$1.booleanValue()) {
                            RealmsClient.switchToStage();
                            LOGGER.info("Switched to stage");
                            RealmsMainScreen.this.refreshFetcher();
                        }
                    }
                    catch (RealmsServiceException $$2) {
                        LOGGER.error("Couldn't connect to Realms: {}", (Object)$$2.toString());
                    }
                }
            }.start();
        }
    }

    private void switchToLocal() {
        if (RealmsClient.currentEnvironment != RealmsClient.Environment.LOCAL) {
            new Thread("MCO Local Availability Checker #1"){

                public void run() {
                    RealmsClient $$0 = RealmsClient.create();
                    try {
                        Boolean $$1 = $$0.stageAvailable();
                        if ($$1.booleanValue()) {
                            RealmsClient.switchToLocal();
                            LOGGER.info("Switched to local");
                            RealmsMainScreen.this.refreshFetcher();
                        }
                    }
                    catch (RealmsServiceException $$2) {
                        LOGGER.error("Couldn't connect to Realms: {}", (Object)$$2.toString());
                    }
                }
            }.start();
        }
    }

    private void switchToProd() {
        RealmsClient.switchToProd();
        this.refreshFetcher();
    }

    void configureClicked(@Nullable RealmsServer $$0) {
        if ($$0 != null && (this.minecraft.getUser().getUuid().equals((Object)$$0.ownerUUID) || overrideConfigure)) {
            this.saveListScrollPosition();
            this.minecraft.setScreen(new RealmsConfigureWorldScreen(this, $$0.id));
        }
    }

    void leaveClicked(@Nullable RealmsServer $$0) {
        if ($$0 != null && !this.minecraft.getUser().getUuid().equals((Object)$$0.ownerUUID)) {
            this.saveListScrollPosition();
            MutableComponent $$12 = Component.translatable("mco.configure.world.leave.question.line1");
            MutableComponent $$2 = Component.translatable("mco.configure.world.leave.question.line2");
            this.minecraft.setScreen(new RealmsLongConfirmationScreen($$1 -> this.leaveServer($$1, $$0), RealmsLongConfirmationScreen.Type.Info, $$12, $$2, true));
        }
    }

    private void saveListScrollPosition() {
        lastScrollYPosition = (int)this.realmSelectionList.getScrollAmount();
    }

    @Nullable
    private RealmsServer getSelectedServer() {
        if (this.realmSelectionList == null) {
            return null;
        }
        Entry $$0 = (Entry)this.realmSelectionList.getSelected();
        return $$0 != null ? $$0.getServer() : null;
    }

    private void leaveServer(boolean $$0, final RealmsServer $$1) {
        if ($$0) {
            new Thread("Realms-leave-server"){

                public void run() {
                    try {
                        RealmsClient $$0 = RealmsClient.create();
                        $$0.uninviteMyselfFrom($$1.id);
                        RealmsMainScreen.this.minecraft.execute(() -> RealmsMainScreen.this.removeServer($$1));
                    }
                    catch (RealmsServiceException $$12) {
                        LOGGER.error("Couldn't configure world");
                        RealmsMainScreen.this.minecraft.execute(() -> RealmsMainScreen.this.minecraft.setScreen(new RealmsGenericErrorScreen($$12, (Screen)RealmsMainScreen.this)));
                    }
                }
            }.start();
        }
        this.minecraft.setScreen(this);
    }

    void removeServer(RealmsServer $$0) {
        this.realmsServers = this.serverList.removeItem($$0);
        this.realmSelectionList.children().removeIf($$1 -> {
            RealmsServer $$2 = $$1.getServer();
            return $$2 != null && $$2.id == $$0.id;
        });
        this.realmSelectionList.setSelected((Entry)null);
        this.updateButtonStates(null);
        this.playButton.active = false;
    }

    public void resetScreen() {
        if (this.realmSelectionList != null) {
            this.realmSelectionList.setSelected((Entry)null);
        }
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if ($$0 == 256) {
            this.keyCombos.forEach(KeyCombo::reset);
            this.onClosePopup();
            return true;
        }
        return super.keyPressed($$0, $$1, $$2);
    }

    void onClosePopup() {
        if (this.shouldShowPopup() && this.popupOpenedByUser) {
            this.popupOpenedByUser = false;
        } else {
            this.minecraft.setScreen(this.lastScreen);
        }
    }

    @Override
    public boolean charTyped(char $$0, int $$12) {
        this.keyCombos.forEach($$1 -> $$1.keyPressed($$0));
        return true;
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.hoveredElement = HoveredElement.NONE;
        this.renderBackground($$0);
        this.realmSelectionList.render($$0, $$1, $$2, $$3);
        this.drawRealmsLogo($$0, this.width / 2 - 50, 7);
        if (RealmsClient.currentEnvironment == RealmsClient.Environment.STAGE) {
            this.renderStage($$0);
        }
        if (RealmsClient.currentEnvironment == RealmsClient.Environment.LOCAL) {
            this.renderLocal($$0);
        }
        if (this.shouldShowPopup()) {
            this.drawPopup($$0);
        } else {
            if (this.showingPopup) {
                this.updateButtonStates(null);
                if (!this.realmsSelectionListAdded) {
                    this.addWidget(this.realmSelectionList);
                    this.realmsSelectionListAdded = true;
                }
                this.playButton.active = this.shouldPlayButtonBeActive(this.getSelectedServer());
            }
            this.showingPopup = false;
        }
        super.render($$0, $$1, $$2, $$3);
        if (this.trialsAvailable && !this.createdTrial && this.shouldShowPopup()) {
            RenderSystem.setShaderTexture(0, TRIAL_ICON_LOCATION);
            int $$4 = 8;
            int $$5 = 8;
            int $$6 = 0;
            if ((Util.getMillis() / 800L & 1L) == 1L) {
                $$6 = 8;
            }
            GuiComponent.blit($$0, this.createTrialButton.getX() + this.createTrialButton.getWidth() - 8 - 4, this.createTrialButton.getY() + this.createTrialButton.getHeight() / 2 - 4, 0.0f, $$6, 8, 8, 8, 16);
        }
    }

    private void drawRealmsLogo(PoseStack $$0, int $$1, int $$2) {
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
        RenderSystem.setShaderTexture(0, LOGO_LOCATION);
        $$0.pushPose();
        $$0.scale(0.5f, 0.5f, 0.5f);
        GuiComponent.blit($$0, $$1 * 2, $$2 * 2 - 5, 0.0f, 0.0f, 200, 50, 200, 50);
        $$0.popPose();
    }

    @Override
    public boolean mouseClicked(double $$0, double $$1, int $$2) {
        if (this.isOutsidePopup($$0, $$1) && this.popupOpenedByUser) {
            this.popupOpenedByUser = false;
            this.justClosedPopup = true;
            return true;
        }
        return super.mouseClicked($$0, $$1, $$2);
    }

    private boolean isOutsidePopup(double $$0, double $$1) {
        int $$2 = this.popupX0();
        int $$3 = this.popupY0();
        return $$0 < (double)($$2 - 5) || $$0 > (double)($$2 + 315) || $$1 < (double)($$3 - 5) || $$1 > (double)($$3 + 171);
    }

    private void drawPopup(PoseStack $$0) {
        int $$1 = this.popupX0();
        int $$2 = this.popupY0();
        if (!this.showingPopup) {
            this.carouselIndex = 0;
            this.carouselTick = 0;
            this.hasSwitchedCarouselImage = true;
            this.updateButtonStates(null);
            if (this.realmsSelectionListAdded) {
                this.removeWidget(this.realmSelectionList);
                this.realmsSelectionListAdded = false;
            }
            this.minecraft.getNarrator().sayNow(POPUP_TEXT);
        }
        if (this.hasFetchedServers) {
            this.showingPopup = true;
        }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 0.7f);
        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, DARKEN_LOCATION);
        boolean $$3 = false;
        int $$4 = 32;
        GuiComponent.blit($$0, 0, 32, 0.0f, 0.0f, this.width, this.height - 40 - 32, 310, 166);
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, POPUP_LOCATION);
        GuiComponent.blit($$0, $$1, $$2, 0.0f, 0.0f, 310, 166, 310, 166);
        if (!teaserImages.isEmpty()) {
            RenderSystem.setShaderTexture(0, (ResourceLocation)teaserImages.get(this.carouselIndex));
            GuiComponent.blit($$0, $$1 + 7, $$2 + 7, 0.0f, 0.0f, 195, 152, 195, 152);
            if (this.carouselTick % 95 < 5) {
                if (!this.hasSwitchedCarouselImage) {
                    this.carouselIndex = (this.carouselIndex + 1) % teaserImages.size();
                    this.hasSwitchedCarouselImage = true;
                }
            } else {
                this.hasSwitchedCarouselImage = false;
            }
        }
        this.formattedPopup.renderLeftAlignedNoShadow($$0, this.width / 2 + 52, $$2 + 7, 10, 0x4C4C4C);
    }

    int popupX0() {
        return (this.width - 310) / 2;
    }

    int popupY0() {
        return this.height / 2 - 80;
    }

    void drawInvitationPendingIcon(PoseStack $$0, int $$1, int $$2, int $$3, int $$4, boolean $$5, boolean $$6) {
        boolean $$20;
        boolean $$14;
        boolean $$9;
        int $$7 = this.numberOfPendingInvites;
        boolean $$8 = this.inPendingInvitationArea($$1, $$2);
        boolean bl = $$9 = $$6 && $$5;
        if ($$9) {
            float $$10 = 0.25f + (1.0f + Mth.sin((float)this.animTick * 0.5f)) * 0.25f;
            int $$11 = 0xFF000000 | (int)($$10 * 64.0f) << 16 | (int)($$10 * 64.0f) << 8 | (int)($$10 * 64.0f) << 0;
            this.fillGradient($$0, $$3 - 2, $$4 - 2, $$3 + 18, $$4 + 18, $$11, $$11);
            $$11 = 0xFF000000 | (int)($$10 * 255.0f) << 16 | (int)($$10 * 255.0f) << 8 | (int)($$10 * 255.0f) << 0;
            this.fillGradient($$0, $$3 - 2, $$4 - 2, $$3 + 18, $$4 - 1, $$11, $$11);
            this.fillGradient($$0, $$3 - 2, $$4 - 2, $$3 - 1, $$4 + 18, $$11, $$11);
            this.fillGradient($$0, $$3 + 17, $$4 - 2, $$3 + 18, $$4 + 18, $$11, $$11);
            this.fillGradient($$0, $$3 - 2, $$4 + 17, $$3 + 18, $$4 + 18, $$11, $$11);
        }
        RenderSystem.setShaderTexture(0, INVITE_ICON_LOCATION);
        boolean $$12 = $$6 && $$5;
        float $$13 = $$12 ? 16.0f : 0.0f;
        GuiComponent.blit($$0, $$3, $$4 - 6, $$13, 0.0f, 15, 25, 31, 25);
        boolean bl2 = $$14 = $$6 && $$7 != 0;
        if ($$14) {
            int $$15 = (Math.min((int)$$7, (int)6) - 1) * 8;
            int $$16 = (int)(Math.max((float)0.0f, (float)Math.max((float)Mth.sin((float)(10 + this.animTick) * 0.57f), (float)Mth.cos((float)this.animTick * 0.35f))) * -6.0f);
            RenderSystem.setShaderTexture(0, INVITATION_ICONS_LOCATION);
            float $$17 = $$8 ? 8.0f : 0.0f;
            GuiComponent.blit($$0, $$3 + 4, $$4 + 4 + $$16, $$15, $$17, 8, 8, 48, 16);
        }
        int $$18 = $$1 + 12;
        int $$19 = $$2;
        boolean bl3 = $$20 = $$6 && $$8;
        if ($$20) {
            Component $$21 = $$7 == 0 ? NO_PENDING_INVITES_TEXT : PENDING_INVITES_TEXT;
            int $$22 = this.font.width($$21);
            this.fillGradient($$0, $$18 - 3, $$19 - 3, $$18 + $$22 + 3, $$19 + 8 + 3, -1073741824, -1073741824);
            this.font.drawShadow($$0, $$21, (float)$$18, (float)$$19, -1);
        }
    }

    private boolean inPendingInvitationArea(double $$0, double $$1) {
        int $$2 = this.width / 2 + 50;
        int $$3 = this.width / 2 + 66;
        int $$4 = 11;
        int $$5 = 23;
        if (this.numberOfPendingInvites != 0) {
            $$2 -= 3;
            $$3 += 3;
            $$4 -= 5;
            $$5 += 5;
        }
        return (double)$$2 <= $$0 && $$0 <= (double)$$3 && (double)$$4 <= $$1 && $$1 <= (double)$$5;
    }

    public void play(@Nullable RealmsServer $$0, Screen $$1) {
        if ($$0 != null) {
            try {
                if (!this.connectLock.tryLock(1L, TimeUnit.SECONDS)) {
                    return;
                }
                if (this.connectLock.getHoldCount() > 1) {
                    return;
                }
            }
            catch (InterruptedException $$2) {
                return;
            }
            this.dontSetConnectedToRealms = true;
            this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen($$1, new GetServerDetailsTask(this, $$1, $$0, this.connectLock)));
        }
    }

    boolean isSelfOwnedServer(RealmsServer $$0) {
        return $$0.ownerUUID != null && $$0.ownerUUID.equals((Object)this.minecraft.getUser().getUuid());
    }

    private boolean isSelfOwnedNonExpiredServer(RealmsServer $$0) {
        return this.isSelfOwnedServer($$0) && !$$0.expired;
    }

    void drawExpired(PoseStack $$0, int $$1, int $$2, int $$3, int $$4) {
        RenderSystem.setShaderTexture(0, EXPIRED_ICON_LOCATION);
        GuiComponent.blit($$0, $$1, $$2, 0.0f, 0.0f, 10, 28, 10, 28);
        if ($$3 >= $$1 && $$3 <= $$1 + 9 && $$4 >= $$2 && $$4 <= $$2 + 27 && $$4 < this.height - 40 && $$4 > 32 && !this.shouldShowPopup()) {
            this.setTooltipForNextRenderPass(SERVER_EXPIRED_TOOLTIP);
        }
    }

    void drawExpiring(PoseStack $$0, int $$1, int $$2, int $$3, int $$4, int $$5) {
        RenderSystem.setShaderTexture(0, EXPIRES_SOON_ICON_LOCATION);
        if (this.animTick % 20 < 10) {
            GuiComponent.blit($$0, $$1, $$2, 0.0f, 0.0f, 10, 28, 20, 28);
        } else {
            GuiComponent.blit($$0, $$1, $$2, 10.0f, 0.0f, 10, 28, 20, 28);
        }
        if ($$3 >= $$1 && $$3 <= $$1 + 9 && $$4 >= $$2 && $$4 <= $$2 + 27 && $$4 < this.height - 40 && $$4 > 32 && !this.shouldShowPopup()) {
            if ($$5 <= 0) {
                this.setTooltipForNextRenderPass(SERVER_EXPIRES_SOON_TOOLTIP);
            } else if ($$5 == 1) {
                this.setTooltipForNextRenderPass(SERVER_EXPIRES_IN_DAY_TOOLTIP);
            } else {
                this.setTooltipForNextRenderPass(Component.translatable("mco.selectServer.expires.days", $$5));
            }
        }
    }

    void drawOpen(PoseStack $$0, int $$1, int $$2, int $$3, int $$4) {
        RenderSystem.setShaderTexture(0, ON_ICON_LOCATION);
        GuiComponent.blit($$0, $$1, $$2, 0.0f, 0.0f, 10, 28, 10, 28);
        if ($$3 >= $$1 && $$3 <= $$1 + 9 && $$4 >= $$2 && $$4 <= $$2 + 27 && $$4 < this.height - 40 && $$4 > 32 && !this.shouldShowPopup()) {
            this.setTooltipForNextRenderPass(SERVER_OPEN_TOOLTIP);
        }
    }

    void drawClose(PoseStack $$0, int $$1, int $$2, int $$3, int $$4) {
        RenderSystem.setShaderTexture(0, OFF_ICON_LOCATION);
        GuiComponent.blit($$0, $$1, $$2, 0.0f, 0.0f, 10, 28, 10, 28);
        if ($$3 >= $$1 && $$3 <= $$1 + 9 && $$4 >= $$2 && $$4 <= $$2 + 27 && $$4 < this.height - 40 && $$4 > 32 && !this.shouldShowPopup()) {
            this.setTooltipForNextRenderPass(SERVER_CLOSED_TOOLTIP);
        }
    }

    void drawLeave(PoseStack $$0, int $$1, int $$2, int $$3, int $$4) {
        boolean $$5 = false;
        if ($$3 >= $$1 && $$3 <= $$1 + 28 && $$4 >= $$2 && $$4 <= $$2 + 28 && $$4 < this.height - 40 && $$4 > 32 && !this.shouldShowPopup()) {
            $$5 = true;
        }
        RenderSystem.setShaderTexture(0, LEAVE_ICON_LOCATION);
        float $$6 = $$5 ? 28.0f : 0.0f;
        GuiComponent.blit($$0, $$1, $$2, $$6, 0.0f, 28, 28, 56, 28);
        if ($$5) {
            this.setTooltipForNextRenderPass(LEAVE_SERVER_TOOLTIP);
            this.hoveredElement = HoveredElement.LEAVE;
        }
    }

    void drawConfigure(PoseStack $$0, int $$1, int $$2, int $$3, int $$4) {
        boolean $$5 = false;
        if ($$3 >= $$1 && $$3 <= $$1 + 28 && $$4 >= $$2 && $$4 <= $$2 + 28 && $$4 < this.height - 40 && $$4 > 32 && !this.shouldShowPopup()) {
            $$5 = true;
        }
        RenderSystem.setShaderTexture(0, CONFIGURE_LOCATION);
        float $$6 = $$5 ? 28.0f : 0.0f;
        GuiComponent.blit($$0, $$1, $$2, $$6, 0.0f, 28, 28, 56, 28);
        if ($$5) {
            this.setTooltipForNextRenderPass(CONFIGURE_SERVER_TOOLTIP);
            this.hoveredElement = HoveredElement.CONFIGURE;
        }
    }

    void renderNews(PoseStack $$0, int $$1, int $$2, boolean $$3, int $$4, int $$5, boolean $$6, boolean $$7) {
        boolean $$8 = false;
        if ($$1 >= $$4 && $$1 <= $$4 + 20 && $$2 >= $$5 && $$2 <= $$5 + 20) {
            $$8 = true;
        }
        RenderSystem.setShaderTexture(0, NEWS_LOCATION);
        if (!$$7) {
            RenderSystem.setShaderColor(0.5f, 0.5f, 0.5f, 1.0f);
        }
        boolean $$9 = $$7 && $$6;
        float $$10 = $$9 ? 20.0f : 0.0f;
        GuiComponent.blit($$0, $$4, $$5, $$10, 0.0f, 20, 20, 40, 20);
        if ($$8 && $$7) {
            this.setTooltipForNextRenderPass(NEWS_TOOLTIP);
        }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        if ($$3 && $$7) {
            int $$11 = $$8 ? 0 : (int)(Math.max((float)0.0f, (float)Math.max((float)Mth.sin((float)(10 + this.animTick) * 0.57f), (float)Mth.cos((float)this.animTick * 0.35f))) * -6.0f);
            RenderSystem.setShaderTexture(0, INVITATION_ICONS_LOCATION);
            GuiComponent.blit($$0, $$4 + 10, $$5 + 2 + $$11, 40.0f, 0.0f, 8, 8, 48, 16);
        }
    }

    private void renderLocal(PoseStack $$0) {
        String $$1 = "LOCAL!";
        $$0.pushPose();
        $$0.translate(this.width / 2 - 25, 20.0f, 0.0f);
        $$0.mulPose(Axis.ZP.rotationDegrees(-20.0f));
        $$0.scale(1.5f, 1.5f, 1.5f);
        this.font.draw($$0, "LOCAL!", 0.0f, 0.0f, 0x7FFF7F);
        $$0.popPose();
    }

    private void renderStage(PoseStack $$0) {
        String $$1 = "STAGE!";
        $$0.pushPose();
        $$0.translate(this.width / 2 - 25, 20.0f, 0.0f);
        $$0.mulPose(Axis.ZP.rotationDegrees(-20.0f));
        $$0.scale(1.5f, 1.5f, 1.5f);
        this.font.draw($$0, "STAGE!", 0.0f, 0.0f, -256);
        $$0.popPose();
    }

    public RealmsMainScreen newScreen() {
        RealmsMainScreen $$0 = new RealmsMainScreen(this.lastScreen);
        $$0.init(this.minecraft, this.width, this.height);
        return $$0;
    }

    public static void updateTeaserImages(ResourceManager $$02) {
        Set $$1 = $$02.listResources("textures/gui/images", (Predicate<ResourceLocation>)((Predicate)$$0 -> $$0.getPath().endsWith(".png"))).keySet();
        teaserImages = $$1.stream().filter($$0 -> $$0.getNamespace().equals((Object)"realms")).toList();
    }

    private void pendingButtonPress(Button $$0) {
        this.minecraft.setScreen(new RealmsPendingInvitesScreen(this.lastScreen));
    }

    static {
        lastScrollYPosition = -1;
    }

    class RealmSelectionList
    extends RealmsObjectSelectionList<Entry> {
        public RealmSelectionList() {
            super(RealmsMainScreen.this.width, RealmsMainScreen.this.height, 32, RealmsMainScreen.this.height - 40, 36);
        }

        @Override
        public boolean keyPressed(int $$0, int $$1, int $$2) {
            if ($$0 == 257 || $$0 == 32 || $$0 == 335) {
                Entry $$3 = (Entry)this.getSelected();
                if ($$3 == null) {
                    return super.keyPressed($$0, $$1, $$2);
                }
                return $$3.mouseClicked(0.0, 0.0, 0);
            }
            return super.keyPressed($$0, $$1, $$2);
        }

        @Override
        public boolean mouseClicked(double $$0, double $$1, int $$2) {
            if ($$2 == 0 && $$0 < (double)this.getScrollbarPosition() && $$1 >= (double)this.y0 && $$1 <= (double)this.y1) {
                int $$3 = RealmsMainScreen.this.realmSelectionList.getRowLeft();
                int $$4 = this.getScrollbarPosition();
                int $$5 = (int)Math.floor((double)($$1 - (double)this.y0)) - this.headerHeight + (int)this.getScrollAmount() - 4;
                int $$6 = $$5 / this.itemHeight;
                if ($$0 >= (double)$$3 && $$0 <= (double)$$4 && $$6 >= 0 && $$5 >= 0 && $$6 < this.getItemCount()) {
                    this.itemClicked($$5, $$6, $$0, $$1, this.width);
                    this.selectItem($$6);
                }
                return true;
            }
            return super.mouseClicked($$0, $$1, $$2);
        }

        @Override
        public void setSelected(@Nullable Entry $$0) {
            super.setSelected($$0);
            if ($$0 != null) {
                RealmsMainScreen.this.updateButtonStates($$0.getServer());
            } else {
                RealmsMainScreen.this.updateButtonStates(null);
            }
        }

        @Override
        public void itemClicked(int $$0, int $$1, double $$2, double $$3, int $$4) {
            Entry $$5 = (Entry)this.getEntry($$1);
            if ($$5 instanceof TrialEntry) {
                RealmsMainScreen.this.popupOpenedByUser = true;
                return;
            }
            RealmsServer $$6 = $$5.getServer();
            if ($$6 == null) {
                return;
            }
            if ($$6.state == RealmsServer.State.UNINITIALIZED) {
                Minecraft.getInstance().setScreen(new RealmsCreateRealmScreen($$6, RealmsMainScreen.this));
                return;
            }
            if (RealmsMainScreen.this.hoveredElement == HoveredElement.CONFIGURE) {
                RealmsMainScreen.this.configureClicked($$6);
            } else if (RealmsMainScreen.this.hoveredElement == HoveredElement.LEAVE) {
                RealmsMainScreen.this.leaveClicked($$6);
            } else if (RealmsMainScreen.this.hoveredElement == HoveredElement.EXPIRED) {
                RealmsMainScreen.this.onRenew($$6);
            } else if (RealmsMainScreen.this.shouldPlayButtonBeActive($$6)) {
                if (Util.getMillis() - RealmsMainScreen.this.lastClickTime < 250L && this.isSelectedItem($$1)) {
                    RealmsMainScreen.this.play($$6, RealmsMainScreen.this);
                }
                RealmsMainScreen.this.lastClickTime = Util.getMillis();
            }
        }

        @Override
        public int getMaxPosition() {
            return this.getItemCount() * 36;
        }

        @Override
        public int getRowWidth() {
            return 300;
        }
    }

    class PendingInvitesButton
    extends Button {
        public PendingInvitesButton() {
            super(RealmsMainScreen.this.width / 2 + 47, 6, 22, 22, CommonComponents.EMPTY, RealmsMainScreen.this::pendingButtonPress, DEFAULT_NARRATION);
        }

        public void tick() {
            this.setMessage(RealmsMainScreen.this.numberOfPendingInvites == 0 ? NO_PENDING_INVITES_TEXT : PENDING_INVITES_TEXT);
        }

        @Override
        public void renderButton(PoseStack $$0, int $$1, int $$2, float $$3) {
            RealmsMainScreen.this.drawInvitationPendingIcon($$0, $$1, $$2, this.getX(), this.getY(), this.isHoveredOrFocused(), this.active);
        }
    }

    class NewsButton
    extends Button {
        public NewsButton() {
            super(RealmsMainScreen.this.width - 115, 6, 20, 20, Component.translatable("mco.news"), $$12 -> {
                if ($$0.newsLink == null) {
                    return;
                }
                RealmsMainScreen.this.minecraft.setScreen(new ConfirmLinkScreen($$1 -> {
                    if ($$1) {
                        Util.getPlatform().openUri($$0.newsLink);
                    }
                    RealmsMainScreen.this.minecraft.setScreen(RealmsMainScreen.this);
                }, $$0.newsLink, true));
                if ($$0.hasUnreadNews) {
                    RealmsPersistence.RealmsPersistenceData $$2 = RealmsPersistence.readFile();
                    $$2.hasUnreadNews = false;
                    $$0.hasUnreadNews = false;
                    RealmsPersistence.writeFile($$2);
                }
            }, DEFAULT_NARRATION);
        }

        @Override
        public void renderButton(PoseStack $$0, int $$1, int $$2, float $$3) {
            RealmsMainScreen.this.renderNews($$0, $$1, $$2, RealmsMainScreen.this.hasUnreadNews, this.getX(), this.getY(), this.isHoveredOrFocused(), this.active);
        }
    }

    class CloseButton
    extends Button {
        public CloseButton() {
            super(RealmsMainScreen.this.popupX0() + 4, RealmsMainScreen.this.popupY0() + 4, 12, 12, Component.translatable("mco.selectServer.close"), $$1 -> RealmsMainScreen.this.onClosePopup(), DEFAULT_NARRATION);
        }

        @Override
        public void renderButton(PoseStack $$0, int $$1, int $$2, float $$3) {
            RenderSystem.setShaderTexture(0, CROSS_ICON_LOCATION);
            float $$4 = this.isHoveredOrFocused() ? 12.0f : 0.0f;
            CloseButton.blit($$0, this.getX(), this.getY(), 0.0f, $$4, 12, 12, 12, 24);
            if (this.isMouseOver($$1, $$2)) {
                RealmsMainScreen.this.setTooltipForNextRenderPass(this.getMessage());
            }
        }
    }

    abstract class Entry
    extends ObjectSelectionList.Entry<Entry> {
        Entry() {
        }

        @Nullable
        public abstract RealmsServer getServer();
    }

    static enum HoveredElement {
        NONE,
        EXPIRED,
        LEAVE,
        CONFIGURE;

    }

    class TrialEntry
    extends Entry {
        TrialEntry() {
        }

        @Override
        public void render(PoseStack $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
            this.renderTrialItem($$0, $$1, $$3, $$2, $$6, $$7);
        }

        @Override
        public boolean mouseClicked(double $$0, double $$1, int $$2) {
            RealmsMainScreen.this.popupOpenedByUser = true;
            return true;
        }

        private void renderTrialItem(PoseStack $$0, int $$1, int $$2, int $$3, int $$4, int $$5) {
            int $$6 = $$3 + 8;
            int $$7 = 0;
            boolean $$8 = false;
            if ($$2 <= $$4 && $$4 <= (int)RealmsMainScreen.this.realmSelectionList.getScrollAmount() && $$3 <= $$5 && $$5 <= $$3 + 32) {
                $$8 = true;
            }
            int $$9 = 0x7FFF7F;
            if ($$8 && !RealmsMainScreen.this.shouldShowPopup()) {
                $$9 = 6077788;
            }
            for (Component $$10 : TRIAL_MESSAGE_LINES) {
                GuiComponent.drawCenteredString($$0, RealmsMainScreen.this.font, $$10, RealmsMainScreen.this.width / 2, $$6 + $$7, $$9);
                $$7 += 10;
            }
        }

        @Override
        public Component getNarration() {
            return TRIAL_TEXT;
        }

        @Override
        @Nullable
        public RealmsServer getServer() {
            return null;
        }
    }

    class ServerEntry
    extends Entry {
        private static final int SKIN_HEAD_LARGE_WIDTH = 36;
        private final RealmsServer serverData;

        public ServerEntry(RealmsServer $$0) {
            this.serverData = $$0;
        }

        @Override
        public void render(PoseStack $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
            this.renderMcoServerItem(this.serverData, $$0, $$3, $$2, $$6, $$7);
        }

        @Override
        public boolean mouseClicked(double $$0, double $$1, int $$2) {
            if (this.serverData.state == RealmsServer.State.UNINITIALIZED) {
                RealmsMainScreen.this.minecraft.setScreen(new RealmsCreateRealmScreen(this.serverData, RealmsMainScreen.this));
            }
            return true;
        }

        private void renderMcoServerItem(RealmsServer $$0, PoseStack $$1, int $$2, int $$3, int $$4, int $$5) {
            this.renderLegacy($$0, $$1, $$2 + 36, $$3, $$4, $$5);
        }

        private void renderLegacy(RealmsServer $$0, PoseStack $$1, int $$2, int $$3, int $$4, int $$5) {
            if ($$0.state == RealmsServer.State.UNINITIALIZED) {
                RenderSystem.setShaderTexture(0, WORLDICON_LOCATION);
                GuiComponent.blit($$1, $$2 + 10, $$3 + 6, 0.0f, 0.0f, 40, 20, 40, 20);
                float $$6 = 0.5f + (1.0f + Mth.sin((float)RealmsMainScreen.this.animTick * 0.25f)) * 0.25f;
                int $$7 = 0xFF000000 | (int)(127.0f * $$6) << 16 | (int)(255.0f * $$6) << 8 | (int)(127.0f * $$6);
                GuiComponent.drawCenteredString($$1, RealmsMainScreen.this.font, SERVER_UNITIALIZED_TEXT, $$2 + 10 + 40 + 75, $$3 + 12, $$7);
                return;
            }
            int $$8 = 225;
            int $$9 = 2;
            if ($$0.expired) {
                RealmsMainScreen.this.drawExpired($$1, $$2 + 225 - 14, $$3 + 2, $$4, $$5);
            } else if ($$0.state == RealmsServer.State.CLOSED) {
                RealmsMainScreen.this.drawClose($$1, $$2 + 225 - 14, $$3 + 2, $$4, $$5);
            } else if (RealmsMainScreen.this.isSelfOwnedServer($$0) && $$0.daysLeft < 7) {
                RealmsMainScreen.this.drawExpiring($$1, $$2 + 225 - 14, $$3 + 2, $$4, $$5, $$0.daysLeft);
            } else if ($$0.state == RealmsServer.State.OPEN) {
                RealmsMainScreen.this.drawOpen($$1, $$2 + 225 - 14, $$3 + 2, $$4, $$5);
            }
            if (!RealmsMainScreen.this.isSelfOwnedServer($$0) && !overrideConfigure) {
                RealmsMainScreen.this.drawLeave($$1, $$2 + 225, $$3 + 2, $$4, $$5);
            } else {
                RealmsMainScreen.this.drawConfigure($$1, $$2 + 225, $$3 + 2, $$4, $$5);
            }
            if (!"0".equals((Object)$$0.serverPing.nrOfPlayers)) {
                String $$10 = ChatFormatting.GRAY + $$0.serverPing.nrOfPlayers;
                RealmsMainScreen.this.font.draw($$1, $$10, (float)($$2 + 207 - RealmsMainScreen.this.font.width($$10)), (float)($$3 + 3), 0x808080);
                if ($$4 >= $$2 + 207 - RealmsMainScreen.this.font.width($$10) && $$4 <= $$2 + 207 && $$5 >= $$3 + 1 && $$5 <= $$3 + 10 && $$5 < RealmsMainScreen.this.height - 40 && $$5 > 32 && !RealmsMainScreen.this.shouldShowPopup()) {
                    RealmsMainScreen.this.setTooltipForNextRenderPass(Component.literal($$0.serverPing.playerList));
                }
            }
            if (RealmsMainScreen.this.isSelfOwnedServer($$0) && $$0.expired) {
                Component $$14;
                Component $$13;
                RenderSystem.enableBlend();
                RenderSystem.setShaderTexture(0, BUTTON_LOCATION);
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                if ($$0.expiredTrial) {
                    Component $$11 = TRIAL_EXPIRED_TEXT;
                    Component $$12 = SUBSCRIPTION_CREATE_TEXT;
                } else {
                    $$13 = SUBSCRIPTION_EXPIRED_TEXT;
                    $$14 = SUBSCRIPTION_RENEW_TEXT;
                }
                int $$15 = RealmsMainScreen.this.font.width($$14) + 17;
                int $$16 = 16;
                int $$17 = $$2 + RealmsMainScreen.this.font.width($$13) + 8;
                int $$18 = $$3 + 13;
                boolean $$19 = false;
                if ($$4 >= $$17 && $$4 < $$17 + $$15 && $$5 > $$18 && $$5 <= $$18 + 16 && $$5 < RealmsMainScreen.this.height - 40 && $$5 > 32 && !RealmsMainScreen.this.shouldShowPopup()) {
                    $$19 = true;
                    RealmsMainScreen.this.hoveredElement = HoveredElement.EXPIRED;
                }
                int $$20 = $$19 ? 2 : 1;
                GuiComponent.blit($$1, $$17, $$18, 0.0f, 46 + $$20 * 20, $$15 / 2, 8, 256, 256);
                GuiComponent.blit($$1, $$17 + $$15 / 2, $$18, 200 - $$15 / 2, 46 + $$20 * 20, $$15 / 2, 8, 256, 256);
                GuiComponent.blit($$1, $$17, $$18 + 8, 0.0f, 46 + $$20 * 20 + 12, $$15 / 2, 8, 256, 256);
                GuiComponent.blit($$1, $$17 + $$15 / 2, $$18 + 8, 200 - $$15 / 2, 46 + $$20 * 20 + 12, $$15 / 2, 8, 256, 256);
                RenderSystem.disableBlend();
                int $$21 = $$3 + 11 + 5;
                int $$22 = $$19 ? 0xFFFFA0 : 0xFFFFFF;
                RealmsMainScreen.this.font.draw($$1, $$13, (float)($$2 + 2), (float)($$21 + 1), 15553363);
                GuiComponent.drawCenteredString($$1, RealmsMainScreen.this.font, $$14, $$17 + $$15 / 2, $$21 + 1, $$22);
            } else {
                if ($$0.worldType == RealmsServer.WorldType.MINIGAME) {
                    int $$23 = 0xCCAC5C;
                    int $$24 = RealmsMainScreen.this.font.width(SELECT_MINIGAME_PREFIX);
                    RealmsMainScreen.this.font.draw($$1, SELECT_MINIGAME_PREFIX, (float)($$2 + 2), (float)($$3 + 12), 0xCCAC5C);
                    RealmsMainScreen.this.font.draw($$1, $$0.getMinigameName(), (float)($$2 + 2 + $$24), (float)($$3 + 12), 0x6C6C6C);
                } else {
                    RealmsMainScreen.this.font.draw($$1, $$0.getDescription(), (float)($$2 + 2), (float)($$3 + 12), 0x6C6C6C);
                }
                if (!RealmsMainScreen.this.isSelfOwnedServer($$0)) {
                    RealmsMainScreen.this.font.draw($$1, $$0.owner, (float)($$2 + 2), (float)($$3 + 12 + 11), 0x4C4C4C);
                }
            }
            RealmsMainScreen.this.font.draw($$1, $$0.getName(), (float)($$2 + 2), (float)($$3 + 1), 0xFFFFFF);
            RealmsTextureManager.withBoundFace($$0.ownerUUID, () -> PlayerFaceRenderer.draw($$1, $$2 - 36, $$3, 32));
        }

        @Override
        public Component getNarration() {
            if (this.serverData.state == RealmsServer.State.UNINITIALIZED) {
                return UNITIALIZED_WORLD_NARRATION;
            }
            return Component.translatable("narrator.select", this.serverData.name);
        }

        @Override
        @Nullable
        public RealmsServer getServer() {
            return this.serverData;
        }
    }
}