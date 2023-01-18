/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Float
 *  java.lang.Object
 */
package net.minecraft.world.level.levelgen.carver;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CarverDebugSettings;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;

public class CanyonCarverConfiguration
extends CarverConfiguration {
    public static final Codec<CanyonCarverConfiguration> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)CarverConfiguration.CODEC.forGetter($$0 -> $$0), (App)FloatProvider.CODEC.fieldOf("vertical_rotation").forGetter($$0 -> $$0.verticalRotation), (App)CanyonShapeConfiguration.CODEC.fieldOf("shape").forGetter($$0 -> $$0.shape)).apply((Applicative)$$02, CanyonCarverConfiguration::new));
    public final FloatProvider verticalRotation;
    public final CanyonShapeConfiguration shape;

    public CanyonCarverConfiguration(float $$0, HeightProvider $$1, FloatProvider $$2, VerticalAnchor $$3, CarverDebugSettings $$4, HolderSet<Block> $$5, FloatProvider $$6, CanyonShapeConfiguration $$7) {
        super($$0, $$1, $$2, $$3, $$4, $$5);
        this.verticalRotation = $$6;
        this.shape = $$7;
    }

    public CanyonCarverConfiguration(CarverConfiguration $$0, FloatProvider $$1, CanyonShapeConfiguration $$2) {
        this($$0.probability, $$0.y, $$0.yScale, $$0.lavaLevel, $$0.debugSettings, $$0.replaceable, $$1, $$2);
    }

    public static class CanyonShapeConfiguration {
        public static final Codec<CanyonShapeConfiguration> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)FloatProvider.CODEC.fieldOf("distance_factor").forGetter($$0 -> $$0.distanceFactor), (App)FloatProvider.CODEC.fieldOf("thickness").forGetter($$0 -> $$0.thickness), (App)ExtraCodecs.NON_NEGATIVE_INT.fieldOf("width_smoothness").forGetter($$0 -> $$0.widthSmoothness), (App)FloatProvider.CODEC.fieldOf("horizontal_radius_factor").forGetter($$0 -> $$0.horizontalRadiusFactor), (App)Codec.FLOAT.fieldOf("vertical_radius_default_factor").forGetter($$0 -> Float.valueOf((float)$$0.verticalRadiusDefaultFactor)), (App)Codec.FLOAT.fieldOf("vertical_radius_center_factor").forGetter($$0 -> Float.valueOf((float)$$0.verticalRadiusCenterFactor))).apply((Applicative)$$02, CanyonShapeConfiguration::new));
        public final FloatProvider distanceFactor;
        public final FloatProvider thickness;
        public final int widthSmoothness;
        public final FloatProvider horizontalRadiusFactor;
        public final float verticalRadiusDefaultFactor;
        public final float verticalRadiusCenterFactor;

        public CanyonShapeConfiguration(FloatProvider $$0, FloatProvider $$1, int $$2, FloatProvider $$3, float $$4, float $$5) {
            this.widthSmoothness = $$2;
            this.horizontalRadiusFactor = $$3;
            this.verticalRadiusDefaultFactor = $$4;
            this.verticalRadiusCenterFactor = $$5;
            this.distanceFactor = $$0;
            this.thickness = $$1;
        }
    }
}