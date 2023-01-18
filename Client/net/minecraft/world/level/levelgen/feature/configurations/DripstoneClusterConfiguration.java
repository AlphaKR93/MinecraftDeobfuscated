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
package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class DripstoneClusterConfiguration
implements FeatureConfiguration {
    public static final Codec<DripstoneClusterConfiguration> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)Codec.intRange((int)1, (int)512).fieldOf("floor_to_ceiling_search_range").forGetter($$0 -> $$0.floorToCeilingSearchRange), (App)IntProvider.codec(1, 128).fieldOf("height").forGetter($$0 -> $$0.height), (App)IntProvider.codec(1, 128).fieldOf("radius").forGetter($$0 -> $$0.radius), (App)Codec.intRange((int)0, (int)64).fieldOf("max_stalagmite_stalactite_height_diff").forGetter($$0 -> $$0.maxStalagmiteStalactiteHeightDiff), (App)Codec.intRange((int)1, (int)64).fieldOf("height_deviation").forGetter($$0 -> $$0.heightDeviation), (App)IntProvider.codec(0, 128).fieldOf("dripstone_block_layer_thickness").forGetter($$0 -> $$0.dripstoneBlockLayerThickness), (App)FloatProvider.codec(0.0f, 2.0f).fieldOf("density").forGetter($$0 -> $$0.density), (App)FloatProvider.codec(0.0f, 2.0f).fieldOf("wetness").forGetter($$0 -> $$0.wetness), (App)Codec.floatRange((float)0.0f, (float)1.0f).fieldOf("chance_of_dripstone_column_at_max_distance_from_center").forGetter($$0 -> Float.valueOf((float)$$0.chanceOfDripstoneColumnAtMaxDistanceFromCenter)), (App)Codec.intRange((int)1, (int)64).fieldOf("max_distance_from_edge_affecting_chance_of_dripstone_column").forGetter($$0 -> $$0.maxDistanceFromEdgeAffectingChanceOfDripstoneColumn), (App)Codec.intRange((int)1, (int)64).fieldOf("max_distance_from_center_affecting_height_bias").forGetter($$0 -> $$0.maxDistanceFromCenterAffectingHeightBias)).apply((Applicative)$$02, DripstoneClusterConfiguration::new));
    public final int floorToCeilingSearchRange;
    public final IntProvider height;
    public final IntProvider radius;
    public final int maxStalagmiteStalactiteHeightDiff;
    public final int heightDeviation;
    public final IntProvider dripstoneBlockLayerThickness;
    public final FloatProvider density;
    public final FloatProvider wetness;
    public final float chanceOfDripstoneColumnAtMaxDistanceFromCenter;
    public final int maxDistanceFromEdgeAffectingChanceOfDripstoneColumn;
    public final int maxDistanceFromCenterAffectingHeightBias;

    public DripstoneClusterConfiguration(int $$0, IntProvider $$1, IntProvider $$2, int $$3, int $$4, IntProvider $$5, FloatProvider $$6, FloatProvider $$7, float $$8, int $$9, int $$10) {
        this.floorToCeilingSearchRange = $$0;
        this.height = $$1;
        this.radius = $$2;
        this.maxStalagmiteStalactiteHeightDiff = $$3;
        this.heightDeviation = $$4;
        this.dripstoneBlockLayerThickness = $$5;
        this.density = $$6;
        this.wetness = $$7;
        this.chanceOfDripstoneColumnAtMaxDistanceFromCenter = $$8;
        this.maxDistanceFromEdgeAffectingChanceOfDripstoneColumn = $$9;
        this.maxDistanceFromCenterAffectingHeightBias = $$10;
    }
}