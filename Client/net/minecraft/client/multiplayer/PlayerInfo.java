/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.collect.Maps
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.minecraft.MinecraftProfileTexture$Type
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Map
 *  javax.annotation.Nullable
 */
package net.minecraft.client.multiplayer;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.RemoteChatSession;
import net.minecraft.network.chat.SignedMessageValidator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.PlayerTeam;

public class PlayerInfo {
    private final GameProfile profile;
    private final Map<MinecraftProfileTexture.Type, ResourceLocation> textureLocations = Maps.newEnumMap(MinecraftProfileTexture.Type.class);
    private GameType gameMode = GameType.DEFAULT_MODE;
    private int latency;
    private boolean pendingTextures;
    @Nullable
    private String skinModel;
    @Nullable
    private Component tabListDisplayName;
    @Nullable
    private RemoteChatSession chatSession;
    private SignedMessageValidator messageValidator;

    public PlayerInfo(GameProfile $$0, boolean $$1) {
        this.profile = $$0;
        this.messageValidator = PlayerInfo.fallbackMessageValidator($$1);
    }

    public GameProfile getProfile() {
        return this.profile;
    }

    @Nullable
    public RemoteChatSession getChatSession() {
        return this.chatSession;
    }

    public SignedMessageValidator getMessageValidator() {
        return this.messageValidator;
    }

    public boolean hasVerifiableChat() {
        return this.chatSession != null;
    }

    protected void setChatSession(RemoteChatSession $$0) {
        this.chatSession = $$0;
        this.messageValidator = $$0.createMessageValidator();
    }

    protected void clearChatSession(boolean $$0) {
        this.chatSession = null;
        this.messageValidator = PlayerInfo.fallbackMessageValidator($$0);
    }

    private static SignedMessageValidator fallbackMessageValidator(boolean $$0) {
        return $$0 ? SignedMessageValidator.REJECT_ALL : SignedMessageValidator.ACCEPT_UNSIGNED;
    }

    public GameType getGameMode() {
        return this.gameMode;
    }

    protected void setGameMode(GameType $$0) {
        this.gameMode = $$0;
    }

    public int getLatency() {
        return this.latency;
    }

    protected void setLatency(int $$0) {
        this.latency = $$0;
    }

    public boolean isCapeLoaded() {
        return this.getCapeLocation() != null;
    }

    public boolean isSkinLoaded() {
        return this.getSkinLocation() != null;
    }

    public String getModelName() {
        if (this.skinModel == null) {
            return DefaultPlayerSkin.getSkinModelName(this.profile.getId());
        }
        return this.skinModel;
    }

    public ResourceLocation getSkinLocation() {
        this.registerTextures();
        return (ResourceLocation)MoreObjects.firstNonNull((Object)((ResourceLocation)this.textureLocations.get((Object)MinecraftProfileTexture.Type.SKIN)), (Object)DefaultPlayerSkin.getDefaultSkin(this.profile.getId()));
    }

    @Nullable
    public ResourceLocation getCapeLocation() {
        this.registerTextures();
        return (ResourceLocation)this.textureLocations.get((Object)MinecraftProfileTexture.Type.CAPE);
    }

    @Nullable
    public ResourceLocation getElytraLocation() {
        this.registerTextures();
        return (ResourceLocation)this.textureLocations.get((Object)MinecraftProfileTexture.Type.ELYTRA);
    }

    @Nullable
    public PlayerTeam getTeam() {
        return Minecraft.getInstance().level.getScoreboard().getPlayersTeam(this.getProfile().getName());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void registerTextures() {
        PlayerInfo playerInfo = this;
        synchronized (playerInfo) {
            if (!this.pendingTextures) {
                this.pendingTextures = true;
                Minecraft.getInstance().getSkinManager().registerSkins(this.profile, ($$0, $$1, $$2) -> {
                    this.textureLocations.put((Object)$$0, (Object)$$1);
                    if ($$0 == MinecraftProfileTexture.Type.SKIN) {
                        this.skinModel = $$2.getMetadata("model");
                        if (this.skinModel == null) {
                            this.skinModel = "default";
                        }
                    }
                }, true);
            }
        }
    }

    public void setTabListDisplayName(@Nullable Component $$0) {
        this.tabListDisplayName = $$0;
    }

    @Nullable
    public Component getTabListDisplayName() {
        return this.tabListDisplayName;
    }
}