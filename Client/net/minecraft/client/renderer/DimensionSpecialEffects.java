/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 *  java.lang.Float
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.client.renderer;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.Vec3;

public abstract class DimensionSpecialEffects {
    private static final Object2ObjectMap<ResourceLocation, DimensionSpecialEffects> EFFECTS = (Object2ObjectMap)Util.make(new Object2ObjectArrayMap(), $$0 -> {
        OverworldEffects $$1 = new OverworldEffects();
        $$0.defaultReturnValue((Object)$$1);
        $$0.put((Object)BuiltinDimensionTypes.OVERWORLD_EFFECTS, (Object)$$1);
        $$0.put((Object)BuiltinDimensionTypes.NETHER_EFFECTS, (Object)new NetherEffects());
        $$0.put((Object)BuiltinDimensionTypes.END_EFFECTS, (Object)new EndEffects());
    });
    private final float[] sunriseCol = new float[4];
    private final float cloudLevel;
    private final boolean hasGround;
    private final SkyType skyType;
    private final boolean forceBrightLightmap;
    private final boolean constantAmbientLight;

    public DimensionSpecialEffects(float $$0, boolean $$1, SkyType $$2, boolean $$3, boolean $$4) {
        this.cloudLevel = $$0;
        this.hasGround = $$1;
        this.skyType = $$2;
        this.forceBrightLightmap = $$3;
        this.constantAmbientLight = $$4;
    }

    public static DimensionSpecialEffects forType(DimensionType $$0) {
        return (DimensionSpecialEffects)EFFECTS.get((Object)$$0.effectsLocation());
    }

    @Nullable
    public float[] getSunriseColor(float $$0, float $$1) {
        float $$2 = 0.4f;
        float $$3 = Mth.cos($$0 * ((float)Math.PI * 2)) - 0.0f;
        float $$4 = -0.0f;
        if ($$3 >= -0.4f && $$3 <= 0.4f) {
            float $$5 = ($$3 - -0.0f) / 0.4f * 0.5f + 0.5f;
            float $$6 = 1.0f - (1.0f - Mth.sin($$5 * (float)Math.PI)) * 0.99f;
            $$6 *= $$6;
            this.sunriseCol[0] = $$5 * 0.3f + 0.7f;
            this.sunriseCol[1] = $$5 * $$5 * 0.7f + 0.2f;
            this.sunriseCol[2] = $$5 * $$5 * 0.0f + 0.2f;
            this.sunriseCol[3] = $$6;
            return this.sunriseCol;
        }
        return null;
    }

    public float getCloudHeight() {
        return this.cloudLevel;
    }

    public boolean hasGround() {
        return this.hasGround;
    }

    public abstract Vec3 getBrightnessDependentFogColor(Vec3 var1, float var2);

    public abstract boolean isFoggyAt(int var1, int var2);

    public SkyType skyType() {
        return this.skyType;
    }

    public boolean forceBrightLightmap() {
        return this.forceBrightLightmap;
    }

    public boolean constantAmbientLight() {
        return this.constantAmbientLight;
    }

    public static enum SkyType {
        NONE,
        NORMAL,
        END;

    }

    public static class OverworldEffects
    extends DimensionSpecialEffects {
        public static final int CLOUD_LEVEL = 192;

        public OverworldEffects() {
            super(192.0f, true, SkyType.NORMAL, false, false);
        }

        @Override
        public Vec3 getBrightnessDependentFogColor(Vec3 $$0, float $$1) {
            return $$0.multiply($$1 * 0.94f + 0.06f, $$1 * 0.94f + 0.06f, $$1 * 0.91f + 0.09f);
        }

        @Override
        public boolean isFoggyAt(int $$0, int $$1) {
            return false;
        }
    }

    public static class NetherEffects
    extends DimensionSpecialEffects {
        public NetherEffects() {
            super(Float.NaN, true, SkyType.NONE, false, true);
        }

        @Override
        public Vec3 getBrightnessDependentFogColor(Vec3 $$0, float $$1) {
            return $$0;
        }

        @Override
        public boolean isFoggyAt(int $$0, int $$1) {
            return true;
        }
    }

    public static class EndEffects
    extends DimensionSpecialEffects {
        public EndEffects() {
            super(Float.NaN, false, SkyType.END, true, false);
        }

        @Override
        public Vec3 getBrightnessDependentFogColor(Vec3 $$0, float $$1) {
            return $$0.scale(0.15f);
        }

        @Override
        public boolean isFoggyAt(int $$0, int $$1) {
            return false;
        }

        @Override
        @Nullable
        public float[] getSunriseColor(float $$0, float $$1) {
            return null;
        }
    }
}