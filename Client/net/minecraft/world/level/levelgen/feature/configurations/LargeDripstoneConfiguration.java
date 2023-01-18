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

public class LargeDripstoneConfiguration
implements FeatureConfiguration {
    public static final Codec<LargeDripstoneConfiguration> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)Codec.intRange((int)1, (int)512).fieldOf("floor_to_ceiling_search_range").orElse((Object)30).forGetter($$0 -> $$0.floorToCeilingSearchRange), (App)IntProvider.codec(1, 60).fieldOf("column_radius").forGetter($$0 -> $$0.columnRadius), (App)FloatProvider.codec(0.0f, 20.0f).fieldOf("height_scale").forGetter($$0 -> $$0.heightScale), (App)Codec.floatRange((float)0.1f, (float)1.0f).fieldOf("max_column_radius_to_cave_height_ratio").forGetter($$0 -> Float.valueOf((float)$$0.maxColumnRadiusToCaveHeightRatio)), (App)FloatProvider.codec(0.1f, 10.0f).fieldOf("stalactite_bluntness").forGetter($$0 -> $$0.stalactiteBluntness), (App)FloatProvider.codec(0.1f, 10.0f).fieldOf("stalagmite_bluntness").forGetter($$0 -> $$0.stalagmiteBluntness), (App)FloatProvider.codec(0.0f, 2.0f).fieldOf("wind_speed").forGetter($$0 -> $$0.windSpeed), (App)Codec.intRange((int)0, (int)100).fieldOf("min_radius_for_wind").forGetter($$0 -> $$0.minRadiusForWind), (App)Codec.floatRange((float)0.0f, (float)5.0f).fieldOf("min_bluntness_for_wind").forGetter($$0 -> Float.valueOf((float)$$0.minBluntnessForWind))).apply((Applicative)$$02, LargeDripstoneConfiguration::new));
    public final int floorToCeilingSearchRange;
    public final IntProvider columnRadius;
    public final FloatProvider heightScale;
    public final float maxColumnRadiusToCaveHeightRatio;
    public final FloatProvider stalactiteBluntness;
    public final FloatProvider stalagmiteBluntness;
    public final FloatProvider windSpeed;
    public final int minRadiusForWind;
    public final float minBluntnessForWind;

    public LargeDripstoneConfiguration(int $$0, IntProvider $$1, FloatProvider $$2, float $$3, FloatProvider $$4, FloatProvider $$5, FloatProvider $$6, int $$7, float $$8) {
        this.floorToCeilingSearchRange = $$0;
        this.columnRadius = $$1;
        this.heightScale = $$2;
        this.maxColumnRadiusToCaveHeightRatio = $$3;
        this.stalactiteBluntness = $$4;
        this.stalagmiteBluntness = $$5;
        this.windSpeed = $$6;
        this.minRadiusForWind = $$7;
        this.minBluntnessForWind = $$8;
    }
}