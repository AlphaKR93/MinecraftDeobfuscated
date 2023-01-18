/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.AutoCloseable
 *  java.lang.Math
 *  java.lang.Object
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.minecraft.client.renderer;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.dimension.DimensionType;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class LightTexture
implements AutoCloseable {
    public static final int FULL_BRIGHT = 0xF000F0;
    public static final int FULL_SKY = 0xF00000;
    public static final int FULL_BLOCK = 240;
    private final DynamicTexture lightTexture;
    private final NativeImage lightPixels;
    private final ResourceLocation lightTextureLocation;
    private boolean updateLightTexture;
    private float blockLightRedFlicker;
    private final GameRenderer renderer;
    private final Minecraft minecraft;

    public LightTexture(GameRenderer $$0, Minecraft $$1) {
        this.renderer = $$0;
        this.minecraft = $$1;
        this.lightTexture = new DynamicTexture(16, 16, false);
        this.lightTextureLocation = this.minecraft.getTextureManager().register("light_map", this.lightTexture);
        this.lightPixels = this.lightTexture.getPixels();
        for (int $$2 = 0; $$2 < 16; ++$$2) {
            for (int $$3 = 0; $$3 < 16; ++$$3) {
                this.lightPixels.setPixelRGBA($$3, $$2, -1);
            }
        }
        this.lightTexture.upload();
    }

    public void close() {
        this.lightTexture.close();
    }

    public void tick() {
        this.blockLightRedFlicker += (float)((Math.random() - Math.random()) * Math.random() * Math.random() * 0.1);
        this.blockLightRedFlicker *= 0.9f;
        this.updateLightTexture = true;
    }

    public void turnOffLightLayer() {
        RenderSystem.setShaderTexture(2, 0);
    }

    public void turnOnLightLayer() {
        RenderSystem.setShaderTexture(2, this.lightTextureLocation);
        this.minecraft.getTextureManager().bindForSetup(this.lightTextureLocation);
        RenderSystem.texParameter(3553, 10241, 9729);
        RenderSystem.texParameter(3553, 10240, 9729);
    }

    private float getDarknessGamma(float $$0) {
        MobEffectInstance $$1;
        if (this.minecraft.player.hasEffect(MobEffects.DARKNESS) && ($$1 = this.minecraft.player.getEffect(MobEffects.DARKNESS)) != null && $$1.getFactorData().isPresent()) {
            return ((MobEffectInstance.FactorData)$$1.getFactorData().get()).getFactor(this.minecraft.player, $$0);
        }
        return 0.0f;
    }

    private float calculateDarknessScale(LivingEntity $$0, float $$1, float $$2) {
        float $$3 = 0.45f * $$1;
        return Math.max((float)0.0f, (float)(Mth.cos(((float)$$0.tickCount - $$2) * (float)Math.PI * 0.025f) * $$3));
    }

    public void updateLightTexture(float $$0) {
        float $$11;
        float $$4;
        if (!this.updateLightTexture) {
            return;
        }
        this.updateLightTexture = false;
        this.minecraft.getProfiler().push("lightTex");
        ClientLevel $$1 = this.minecraft.level;
        if ($$1 == null) {
            return;
        }
        float $$2 = $$1.getSkyDarken(1.0f);
        if ($$1.getSkyFlashTime() > 0) {
            float $$3 = 1.0f;
        } else {
            $$4 = $$2 * 0.95f + 0.05f;
        }
        float $$5 = this.minecraft.options.darknessEffectScale().get().floatValue();
        float $$6 = this.getDarknessGamma($$0) * $$5;
        float $$7 = this.calculateDarknessScale(this.minecraft.player, $$6, $$0) * $$5;
        float $$8 = this.minecraft.player.getWaterVision();
        if (this.minecraft.player.hasEffect(MobEffects.NIGHT_VISION)) {
            float $$9 = GameRenderer.getNightVisionScale(this.minecraft.player, $$0);
        } else if ($$8 > 0.0f && this.minecraft.player.hasEffect(MobEffects.CONDUIT_POWER)) {
            float $$10 = $$8;
        } else {
            $$11 = 0.0f;
        }
        Vector3f $$12 = new Vector3f($$2, $$2, 1.0f).lerp((Vector3fc)new Vector3f(1.0f, 1.0f, 1.0f), 0.35f);
        float $$13 = this.blockLightRedFlicker + 1.5f;
        Vector3f $$14 = new Vector3f();
        for (int $$15 = 0; $$15 < 16; ++$$15) {
            for (int $$16 = 0; $$16 < 16; ++$$16) {
                float $$26;
                float $$18;
                float $$17 = LightTexture.getBrightness($$1.dimensionType(), $$15) * $$4;
                float $$19 = $$18 = LightTexture.getBrightness($$1.dimensionType(), $$16) * $$13;
                float $$20 = $$18 * (($$18 * 0.6f + 0.4f) * 0.6f + 0.4f);
                float $$21 = $$18 * ($$18 * $$18 * 0.6f + 0.4f);
                $$14.set($$19, $$20, $$21);
                boolean $$22 = $$1.effects().forceBrightLightmap();
                if ($$22) {
                    $$14.lerp((Vector3fc)new Vector3f(0.99f, 1.12f, 1.0f), 0.25f);
                    LightTexture.clampColor($$14);
                } else {
                    Vector3f $$23 = new Vector3f((Vector3fc)$$12).mul($$17);
                    $$14.add((Vector3fc)$$23);
                    $$14.lerp((Vector3fc)new Vector3f(0.75f, 0.75f, 0.75f), 0.04f);
                    if (this.renderer.getDarkenWorldAmount($$0) > 0.0f) {
                        float $$24 = this.renderer.getDarkenWorldAmount($$0);
                        Vector3f $$25 = new Vector3f((Vector3fc)$$14).mul(0.7f, 0.6f, 0.6f);
                        $$14.lerp((Vector3fc)$$25, $$24);
                    }
                }
                if ($$11 > 0.0f && ($$26 = Math.max((float)$$14.x(), (float)Math.max((float)$$14.y(), (float)$$14.z()))) < 1.0f) {
                    float $$27 = 1.0f / $$26;
                    Vector3f $$28 = new Vector3f((Vector3fc)$$14).mul($$27);
                    $$14.lerp((Vector3fc)$$28, $$11);
                }
                if (!$$22) {
                    if ($$7 > 0.0f) {
                        $$14.add(-$$7, -$$7, -$$7);
                    }
                    LightTexture.clampColor($$14);
                }
                float $$29 = this.minecraft.options.gamma().get().floatValue();
                Vector3f $$30 = new Vector3f(this.notGamma($$14.x), this.notGamma($$14.y), this.notGamma($$14.z));
                $$14.lerp((Vector3fc)$$30, Math.max((float)0.0f, (float)($$29 - $$6)));
                $$14.lerp((Vector3fc)new Vector3f(0.75f, 0.75f, 0.75f), 0.04f);
                LightTexture.clampColor($$14);
                $$14.mul(255.0f);
                int $$31 = 255;
                int $$32 = (int)$$14.x();
                int $$33 = (int)$$14.y();
                int $$34 = (int)$$14.z();
                this.lightPixels.setPixelRGBA($$16, $$15, 0xFF000000 | $$34 << 16 | $$33 << 8 | $$32);
            }
        }
        this.lightTexture.upload();
        this.minecraft.getProfiler().pop();
    }

    private static void clampColor(Vector3f $$0) {
        $$0.set(Mth.clamp($$0.x, 0.0f, 1.0f), Mth.clamp($$0.y, 0.0f, 1.0f), Mth.clamp($$0.z, 0.0f, 1.0f));
    }

    private float notGamma(float $$0) {
        float $$1 = 1.0f - $$0;
        return 1.0f - $$1 * $$1 * $$1 * $$1;
    }

    public static float getBrightness(DimensionType $$0, int $$1) {
        float $$2 = (float)$$1 / 15.0f;
        float $$3 = $$2 / (4.0f - 3.0f * $$2);
        return Mth.lerp($$0.ambientLight(), $$3, 1.0f);
    }

    public static int pack(int $$0, int $$1) {
        return $$0 << 4 | $$1 << 20;
    }

    public static int block(int $$0) {
        return $$0 >> 4 & 0xFFFF;
    }

    public static int sky(int $$0) {
        return $$0 >> 20 & 0xFFFF;
    }
}