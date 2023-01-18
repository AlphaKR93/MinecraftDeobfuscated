/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.hash.Hashing
 *  com.mojang.authlib.GameProfile
 *  java.lang.CharSequence
 *  java.lang.Float
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Locale
 *  javax.annotation.Nullable
 */
package net.minecraft.client.player;

import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.HttpTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;

public abstract class AbstractClientPlayer
extends Player {
    private static final String SKIN_URL_TEMPLATE = "http://skins.minecraft.net/MinecraftSkins/%s.png";
    @Nullable
    private PlayerInfo playerInfo;
    public float elytraRotX;
    public float elytraRotY;
    public float elytraRotZ;
    public final ClientLevel clientLevel;

    public AbstractClientPlayer(ClientLevel $$0, GameProfile $$1) {
        super($$0, $$0.getSharedSpawnPos(), $$0.getSharedSpawnAngle(), $$1);
        this.clientLevel = $$0;
    }

    @Override
    public boolean isSpectator() {
        PlayerInfo $$0 = this.getPlayerInfo();
        return $$0 != null && $$0.getGameMode() == GameType.SPECTATOR;
    }

    @Override
    public boolean isCreative() {
        PlayerInfo $$0 = this.getPlayerInfo();
        return $$0 != null && $$0.getGameMode() == GameType.CREATIVE;
    }

    public boolean isCapeLoaded() {
        return this.getPlayerInfo() != null;
    }

    @Nullable
    protected PlayerInfo getPlayerInfo() {
        if (this.playerInfo == null) {
            this.playerInfo = Minecraft.getInstance().getConnection().getPlayerInfo(this.getUUID());
        }
        return this.playerInfo;
    }

    public boolean isSkinLoaded() {
        PlayerInfo $$0 = this.getPlayerInfo();
        return $$0 != null && $$0.isSkinLoaded();
    }

    public ResourceLocation getSkinTextureLocation() {
        PlayerInfo $$0 = this.getPlayerInfo();
        return $$0 == null ? DefaultPlayerSkin.getDefaultSkin(this.getUUID()) : $$0.getSkinLocation();
    }

    @Nullable
    public ResourceLocation getCloakTextureLocation() {
        PlayerInfo $$0 = this.getPlayerInfo();
        return $$0 == null ? null : $$0.getCapeLocation();
    }

    public boolean isElytraLoaded() {
        return this.getPlayerInfo() != null;
    }

    @Nullable
    public ResourceLocation getElytraTextureLocation() {
        PlayerInfo $$0 = this.getPlayerInfo();
        return $$0 == null ? null : $$0.getElytraLocation();
    }

    public static void registerSkinTexture(ResourceLocation $$0, String $$1) {
        TextureManager $$2 = Minecraft.getInstance().getTextureManager();
        AbstractTexture $$3 = $$2.getTexture($$0, MissingTextureAtlasSprite.getTexture());
        if ($$3 == MissingTextureAtlasSprite.getTexture()) {
            $$3 = new HttpTexture(null, String.format((Locale)Locale.ROOT, (String)SKIN_URL_TEMPLATE, (Object[])new Object[]{StringUtil.stripColor($$1)}), DefaultPlayerSkin.getDefaultSkin(UUIDUtil.createOfflinePlayerUUID($$1)), true, null);
            $$2.register($$0, $$3);
        }
    }

    public static ResourceLocation getSkinLocation(String $$0) {
        return new ResourceLocation("skins/" + Hashing.sha1().hashUnencodedChars((CharSequence)StringUtil.stripColor($$0)));
    }

    public String getModelName() {
        PlayerInfo $$0 = this.getPlayerInfo();
        return $$0 == null ? DefaultPlayerSkin.getSkinModelName(this.getUUID()) : $$0.getModelName();
    }

    public float getFieldOfViewModifier() {
        float $$0 = 1.0f;
        if (this.getAbilities().flying) {
            $$0 *= 1.1f;
        }
        if (this.getAbilities().getWalkingSpeed() == 0.0f || Float.isNaN((float)($$0 *= ((float)this.getAttributeValue(Attributes.MOVEMENT_SPEED) / this.getAbilities().getWalkingSpeed() + 1.0f) / 2.0f)) || Float.isInfinite((float)$$0)) {
            $$0 = 1.0f;
        }
        ItemStack $$1 = this.getUseItem();
        if (this.isUsingItem()) {
            if ($$1.is(Items.BOW)) {
                int $$2 = this.getTicksUsingItem();
                float $$3 = (float)$$2 / 20.0f;
                $$3 = $$3 > 1.0f ? 1.0f : ($$3 *= $$3);
                $$0 *= 1.0f - $$3 * 0.15f;
            } else if (Minecraft.getInstance().options.getCameraType().isFirstPerson() && this.isScoping()) {
                return 0.1f;
            }
        }
        return Mth.lerp(Minecraft.getInstance().options.fovEffectScale().get().floatValue(), 1.0f, $$0);
    }
}