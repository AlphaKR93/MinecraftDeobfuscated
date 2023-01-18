/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.level.levelgen.placement;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.placement.RepeatingPlacement;

public class NoiseThresholdCountPlacement
extends RepeatingPlacement {
    public static final Codec<NoiseThresholdCountPlacement> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)Codec.DOUBLE.fieldOf("noise_level").forGetter($$0 -> $$0.noiseLevel), (App)Codec.INT.fieldOf("below_noise").forGetter($$0 -> $$0.belowNoise), (App)Codec.INT.fieldOf("above_noise").forGetter($$0 -> $$0.aboveNoise)).apply((Applicative)$$02, NoiseThresholdCountPlacement::new));
    private final double noiseLevel;
    private final int belowNoise;
    private final int aboveNoise;

    private NoiseThresholdCountPlacement(double $$0, int $$1, int $$2) {
        this.noiseLevel = $$0;
        this.belowNoise = $$1;
        this.aboveNoise = $$2;
    }

    public static NoiseThresholdCountPlacement of(double $$0, int $$1, int $$2) {
        return new NoiseThresholdCountPlacement($$0, $$1, $$2);
    }

    @Override
    protected int count(RandomSource $$0, BlockPos $$1) {
        double $$2 = Biome.BIOME_INFO_NOISE.getValue((double)$$1.getX() / 200.0, (double)$$1.getZ() / 200.0, false);
        return $$2 < this.noiseLevel ? this.belowNoise : this.aboveNoise;
    }

    @Override
    public PlacementModifierType<?> type() {
        return PlacementModifierType.NOISE_THRESHOLD_COUNT;
    }
}