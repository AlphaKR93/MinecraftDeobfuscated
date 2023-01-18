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
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class UnderwaterMagmaConfiguration
implements FeatureConfiguration {
    public static final Codec<UnderwaterMagmaConfiguration> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)Codec.intRange((int)0, (int)512).fieldOf("floor_search_range").forGetter($$0 -> $$0.floorSearchRange), (App)Codec.intRange((int)0, (int)64).fieldOf("placement_radius_around_floor").forGetter($$0 -> $$0.placementRadiusAroundFloor), (App)Codec.floatRange((float)0.0f, (float)1.0f).fieldOf("placement_probability_per_valid_position").forGetter($$0 -> Float.valueOf((float)$$0.placementProbabilityPerValidPosition))).apply((Applicative)$$02, UnderwaterMagmaConfiguration::new));
    public final int floorSearchRange;
    public final int placementRadiusAroundFloor;
    public final float placementProbabilityPerValidPosition;

    public UnderwaterMagmaConfiguration(int $$0, int $$1, float $$2) {
        this.floorSearchRange = $$0;
        this.placementRadiusAroundFloor = $$1;
        this.placementProbabilityPerValidPosition = $$2;
    }
}