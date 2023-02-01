/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.lang.Float
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  javax.annotation.Nullable
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class FogRenderer {
    private static final int WATER_FOG_DISTANCE = 96;
    private static final List<MobEffectFogFunction> MOB_EFFECT_FOG = Lists.newArrayList((Object[])new MobEffectFogFunction[]{new BlindnessFogFunction(), new DarknessFogFunction()});
    public static final float BIOME_FOG_TRANSITION_TIME = 5000.0f;
    private static float fogRed;
    private static float fogGreen;
    private static float fogBlue;
    private static int targetBiomeFog;
    private static int previousBiomeFog;
    private static long biomeChangedTime;

    public static void setupColor(Camera $$0, float $$1, ClientLevel $$2, int $$32, float $$42) {
        float $$44;
        LivingEntity $$422;
        FogType $$52 = $$0.getFluidInCamera();
        Entity $$6 = $$0.getEntity();
        if ($$52 == FogType.WATER) {
            long $$7 = Util.getMillis();
            int $$8 = ((Biome)$$2.getBiome(new BlockPos($$0.getPosition())).value()).getWaterFogColor();
            if (biomeChangedTime < 0L) {
                targetBiomeFog = $$8;
                previousBiomeFog = $$8;
                biomeChangedTime = $$7;
            }
            int $$9 = targetBiomeFog >> 16 & 0xFF;
            int $$10 = targetBiomeFog >> 8 & 0xFF;
            int $$11 = targetBiomeFog & 0xFF;
            int $$12 = previousBiomeFog >> 16 & 0xFF;
            int $$13 = previousBiomeFog >> 8 & 0xFF;
            int $$14 = previousBiomeFog & 0xFF;
            float $$15 = Mth.clamp((float)($$7 - biomeChangedTime) / 5000.0f, 0.0f, 1.0f);
            float $$16 = Mth.lerp($$15, $$12, $$9);
            float $$17 = Mth.lerp($$15, $$13, $$10);
            float $$18 = Mth.lerp($$15, $$14, $$11);
            fogRed = $$16 / 255.0f;
            fogGreen = $$17 / 255.0f;
            fogBlue = $$18 / 255.0f;
            if (targetBiomeFog != $$8) {
                targetBiomeFog = $$8;
                previousBiomeFog = Mth.floor($$16) << 16 | Mth.floor($$17) << 8 | Mth.floor($$18);
                biomeChangedTime = $$7;
            }
        } else if ($$52 == FogType.LAVA) {
            fogRed = 0.6f;
            fogGreen = 0.1f;
            fogBlue = 0.0f;
            biomeChangedTime = -1L;
        } else if ($$52 == FogType.POWDER_SNOW) {
            fogRed = 0.623f;
            fogGreen = 0.734f;
            fogBlue = 0.785f;
            biomeChangedTime = -1L;
            RenderSystem.clearColor(fogRed, fogGreen, fogBlue, 0.0f);
        } else {
            float $$35;
            float $$19 = 0.25f + 0.75f * (float)$$32 / 32.0f;
            $$19 = 1.0f - (float)Math.pow((double)$$19, (double)0.25);
            Vec3 $$20 = $$2.getSkyColor($$0.getPosition(), $$1);
            float $$21 = (float)$$20.x;
            float $$22 = (float)$$20.y;
            float $$23 = (float)$$20.z;
            float $$24 = Mth.clamp(Mth.cos($$2.getTimeOfDay($$1) * ((float)Math.PI * 2)) * 2.0f + 0.5f, 0.0f, 1.0f);
            BiomeManager $$25 = $$2.getBiomeManager();
            Vec3 $$26 = $$0.getPosition().subtract(2.0, 2.0, 2.0).scale(0.25);
            Vec3 $$27 = CubicSampler.gaussianSampleVec3($$26, ($$3, $$4, $$5) -> $$2.effects().getBrightnessDependentFogColor(Vec3.fromRGB24($$25.getNoiseBiomeAtQuart($$3, $$4, $$5).value().getFogColor()), $$24));
            fogRed = (float)$$27.x();
            fogGreen = (float)$$27.y();
            fogBlue = (float)$$27.z();
            if ($$32 >= 4) {
                float[] $$31;
                float $$28 = Mth.sin($$2.getSunAngle($$1)) > 0.0f ? -1.0f : 1.0f;
                Vector3f $$29 = new Vector3f($$28, 0.0f, 0.0f);
                float $$30 = $$0.getLookVector().dot((Vector3fc)$$29);
                if ($$30 < 0.0f) {
                    $$30 = 0.0f;
                }
                if ($$30 > 0.0f && ($$31 = $$2.effects().getSunriseColor($$2.getTimeOfDay($$1), $$1)) != null) {
                    fogRed = fogRed * (1.0f - ($$30 *= $$31[3])) + $$31[0] * $$30;
                    fogGreen = fogGreen * (1.0f - $$30) + $$31[1] * $$30;
                    fogBlue = fogBlue * (1.0f - $$30) + $$31[2] * $$30;
                }
            }
            fogRed += ($$21 - fogRed) * $$19;
            fogGreen += ($$22 - fogGreen) * $$19;
            fogBlue += ($$23 - fogBlue) * $$19;
            float $$322 = $$2.getRainLevel($$1);
            if ($$322 > 0.0f) {
                float $$33 = 1.0f - $$322 * 0.5f;
                float $$34 = 1.0f - $$322 * 0.4f;
                fogRed *= $$33;
                fogGreen *= $$33;
                fogBlue *= $$34;
            }
            if (($$35 = $$2.getThunderLevel($$1)) > 0.0f) {
                float $$36 = 1.0f - $$35 * 0.5f;
                fogRed *= $$36;
                fogGreen *= $$36;
                fogBlue *= $$36;
            }
            biomeChangedTime = -1L;
        }
        float $$37 = ((float)$$0.getPosition().y - (float)$$2.getMinBuildHeight()) * $$2.getLevelData().getClearColorScale();
        MobEffectFogFunction $$38 = FogRenderer.getPriorityFogFunction($$6, $$1);
        if ($$38 != null) {
            LivingEntity $$39 = (LivingEntity)$$6;
            $$37 = $$38.getModifiedVoidDarkness($$39, $$39.getEffect($$38.getMobEffect()), $$37, $$1);
        }
        if ($$37 < 1.0f && $$52 != FogType.LAVA && $$52 != FogType.POWDER_SNOW) {
            if ($$37 < 0.0f) {
                $$37 = 0.0f;
            }
            $$37 *= $$37;
            fogRed *= $$37;
            fogGreen *= $$37;
            fogBlue *= $$37;
        }
        if ($$42 > 0.0f) {
            fogRed = fogRed * (1.0f - $$42) + fogRed * 0.7f * $$42;
            fogGreen = fogGreen * (1.0f - $$42) + fogGreen * 0.6f * $$42;
            fogBlue = fogBlue * (1.0f - $$42) + fogBlue * 0.6f * $$42;
        }
        if ($$52 == FogType.WATER) {
            if ($$6 instanceof LocalPlayer) {
                float $$40 = ((LocalPlayer)$$6).getWaterVision();
            } else {
                float $$41 = 1.0f;
            }
        } else if ($$6 instanceof LivingEntity && ($$422 = (LivingEntity)$$6).hasEffect(MobEffects.NIGHT_VISION) && !$$422.hasEffect(MobEffects.DARKNESS)) {
            float $$43 = GameRenderer.getNightVisionScale($$422, $$1);
        } else {
            $$44 = 0.0f;
        }
        if (fogRed != 0.0f && fogGreen != 0.0f && fogBlue != 0.0f) {
            float $$45 = Math.min((float)(1.0f / fogRed), (float)Math.min((float)(1.0f / fogGreen), (float)(1.0f / fogBlue)));
            fogRed = fogRed * (1.0f - $$44) + fogRed * $$45 * $$44;
            fogGreen = fogGreen * (1.0f - $$44) + fogGreen * $$45 * $$44;
            fogBlue = fogBlue * (1.0f - $$44) + fogBlue * $$45 * $$44;
        }
        RenderSystem.clearColor(fogRed, fogGreen, fogBlue, 0.0f);
    }

    public static void setupNoFog() {
        RenderSystem.setShaderFogStart(Float.MAX_VALUE);
    }

    @Nullable
    private static MobEffectFogFunction getPriorityFogFunction(Entity $$0, float $$1) {
        if ($$0 instanceof LivingEntity) {
            LivingEntity $$22 = (LivingEntity)$$0;
            return (MobEffectFogFunction)MOB_EFFECT_FOG.stream().filter($$2 -> $$2.isEnabled($$22, $$1)).findFirst().orElse(null);
        }
        return null;
    }

    public static void setupFog(Camera $$0, FogMode $$1, float $$2, boolean $$3, float $$4) {
        FogType $$5 = $$0.getFluidInCamera();
        Entity $$6 = $$0.getEntity();
        FogData $$7 = new FogData($$1);
        MobEffectFogFunction $$8 = FogRenderer.getPriorityFogFunction($$6, $$4);
        if ($$5 == FogType.LAVA) {
            if ($$6.isSpectator()) {
                $$7.start = -8.0f;
                $$7.end = $$2 * 0.5f;
            } else if ($$6 instanceof LivingEntity && ((LivingEntity)$$6).hasEffect(MobEffects.FIRE_RESISTANCE)) {
                $$7.start = 0.0f;
                $$7.end = 3.0f;
            } else {
                $$7.start = 0.25f;
                $$7.end = 1.0f;
            }
        } else if ($$5 == FogType.POWDER_SNOW) {
            if ($$6.isSpectator()) {
                $$7.start = -8.0f;
                $$7.end = $$2 * 0.5f;
            } else {
                $$7.start = 0.0f;
                $$7.end = 2.0f;
            }
        } else if ($$8 != null) {
            LivingEntity $$9 = (LivingEntity)$$6;
            MobEffectInstance $$10 = $$9.getEffect($$8.getMobEffect());
            if ($$10 != null) {
                $$8.setupFog($$7, $$9, $$10, $$2, $$4);
            }
        } else if ($$5 == FogType.WATER) {
            $$7.start = -8.0f;
            $$7.end = 96.0f;
            if ($$6 instanceof LocalPlayer) {
                LocalPlayer $$11 = (LocalPlayer)$$6;
                $$7.end *= Math.max((float)0.25f, (float)$$11.getWaterVision());
                Holder $$12 = $$11.level.getBiome($$11.blockPosition());
                if ($$12.is(BiomeTags.HAS_CLOSER_WATER_FOG)) {
                    $$7.end *= 0.85f;
                }
            }
            if ($$7.end > $$2) {
                $$7.end = $$2;
                $$7.shape = FogShape.CYLINDER;
            }
        } else if ($$3) {
            $$7.start = $$2 * 0.05f;
            $$7.end = Math.min((float)$$2, (float)192.0f) * 0.5f;
        } else if ($$1 == FogMode.FOG_SKY) {
            $$7.start = 0.0f;
            $$7.end = $$2;
            $$7.shape = FogShape.CYLINDER;
        } else {
            float $$13 = Mth.clamp($$2 / 10.0f, 4.0f, 64.0f);
            $$7.start = $$2 - $$13;
            $$7.end = $$2;
            $$7.shape = FogShape.CYLINDER;
        }
        RenderSystem.setShaderFogStart($$7.start);
        RenderSystem.setShaderFogEnd($$7.end);
        RenderSystem.setShaderFogShape($$7.shape);
    }

    public static void levelFogColor() {
        RenderSystem.setShaderFogColor(fogRed, fogGreen, fogBlue);
    }

    static {
        targetBiomeFog = -1;
        previousBiomeFog = -1;
        biomeChangedTime = -1L;
    }

    static interface MobEffectFogFunction {
        public MobEffect getMobEffect();

        public void setupFog(FogData var1, LivingEntity var2, MobEffectInstance var3, float var4, float var5);

        default public boolean isEnabled(LivingEntity $$0, float $$1) {
            return $$0.hasEffect(this.getMobEffect());
        }

        default public float getModifiedVoidDarkness(LivingEntity $$0, MobEffectInstance $$1, float $$2, float $$3) {
            MobEffectInstance $$4 = $$0.getEffect(this.getMobEffect());
            if ($$4 != null) {
                $$2 = $$4.endsWithin(19) ? 1.0f - (float)$$4.getDuration() / 20.0f : 0.0f;
            }
            return $$2;
        }
    }

    static class FogData {
        public final FogMode mode;
        public float start;
        public float end;
        public FogShape shape = FogShape.SPHERE;

        public FogData(FogMode $$0) {
            this.mode = $$0;
        }
    }

    public static enum FogMode {
        FOG_SKY,
        FOG_TERRAIN;

    }

    static class BlindnessFogFunction
    implements MobEffectFogFunction {
        BlindnessFogFunction() {
        }

        @Override
        public MobEffect getMobEffect() {
            return MobEffects.BLINDNESS;
        }

        @Override
        public void setupFog(FogData $$0, LivingEntity $$1, MobEffectInstance $$2, float $$3, float $$4) {
            float $$5;
            float f = $$5 = $$2.isInfiniteDuration() ? 5.0f : Mth.lerp(Math.min((float)1.0f, (float)((float)$$2.getDuration() / 20.0f)), $$3, 5.0f);
            if ($$0.mode == FogMode.FOG_SKY) {
                $$0.start = 0.0f;
                $$0.end = $$5 * 0.8f;
            } else {
                $$0.start = $$5 * 0.25f;
                $$0.end = $$5;
            }
        }
    }

    static class DarknessFogFunction
    implements MobEffectFogFunction {
        DarknessFogFunction() {
        }

        @Override
        public MobEffect getMobEffect() {
            return MobEffects.DARKNESS;
        }

        @Override
        public void setupFog(FogData $$0, LivingEntity $$1, MobEffectInstance $$2, float $$3, float $$4) {
            if ($$2.getFactorData().isEmpty()) {
                return;
            }
            float $$5 = Mth.lerp(((MobEffectInstance.FactorData)$$2.getFactorData().get()).getFactor($$1, $$4), $$3, 15.0f);
            $$0.start = $$0.mode == FogMode.FOG_SKY ? 0.0f : $$5 * 0.75f;
            $$0.end = $$5;
        }

        @Override
        public float getModifiedVoidDarkness(LivingEntity $$0, MobEffectInstance $$1, float $$2, float $$3) {
            if ($$1.getFactorData().isEmpty()) {
                return 0.0f;
            }
            return 1.0f - ((MobEffectInstance.FactorData)$$1.getFactorData().get()).getFactor($$0, $$3);
        }
    }
}