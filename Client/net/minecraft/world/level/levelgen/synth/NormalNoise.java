/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.doubles.DoubleArrayList
 *  it.unimi.dsi.fastutil.doubles.DoubleList
 *  it.unimi.dsi.fastutil.doubles.DoubleListIterator
 *  java.lang.Deprecated
 *  java.lang.Double
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.StringBuilder
 *  java.util.List
 */
package net.minecraft.world.level.levelgen.synth;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleListIterator;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;

public class NormalNoise {
    private static final double INPUT_FACTOR = 1.0181268882175227;
    private static final double TARGET_DEVIATION = 0.3333333333333333;
    private final double valueFactor;
    private final PerlinNoise first;
    private final PerlinNoise second;
    private final double maxValue;
    private final NoiseParameters parameters;

    @Deprecated
    public static NormalNoise createLegacyNetherBiome(RandomSource $$0, NoiseParameters $$1) {
        return new NormalNoise($$0, $$1, false);
    }

    public static NormalNoise create(RandomSource $$0, int $$1, double ... $$2) {
        return NormalNoise.create($$0, new NoiseParameters($$1, (DoubleList)new DoubleArrayList($$2)));
    }

    public static NormalNoise create(RandomSource $$0, NoiseParameters $$1) {
        return new NormalNoise($$0, $$1, true);
    }

    private NormalNoise(RandomSource $$0, NoiseParameters $$1, boolean $$2) {
        int $$3 = $$1.firstOctave;
        DoubleList $$4 = $$1.amplitudes;
        this.parameters = $$1;
        if ($$2) {
            this.first = PerlinNoise.create($$0, $$3, $$4);
            this.second = PerlinNoise.create($$0, $$3, $$4);
        } else {
            this.first = PerlinNoise.createLegacyForLegacyNetherBiome($$0, $$3, $$4);
            this.second = PerlinNoise.createLegacyForLegacyNetherBiome($$0, $$3, $$4);
        }
        int $$5 = Integer.MAX_VALUE;
        int $$6 = Integer.MIN_VALUE;
        DoubleListIterator $$7 = $$4.iterator();
        while ($$7.hasNext()) {
            int $$8 = $$7.nextIndex();
            double $$9 = $$7.nextDouble();
            if ($$9 == 0.0) continue;
            $$5 = Math.min((int)$$5, (int)$$8);
            $$6 = Math.max((int)$$6, (int)$$8);
        }
        this.valueFactor = 0.16666666666666666 / NormalNoise.expectedDeviation($$6 - $$5);
        this.maxValue = (this.first.maxValue() + this.second.maxValue()) * this.valueFactor;
    }

    public double maxValue() {
        return this.maxValue;
    }

    private static double expectedDeviation(int $$0) {
        return 0.1 * (1.0 + 1.0 / (double)($$0 + 1));
    }

    public double getValue(double $$0, double $$1, double $$2) {
        double $$3 = $$0 * 1.0181268882175227;
        double $$4 = $$1 * 1.0181268882175227;
        double $$5 = $$2 * 1.0181268882175227;
        return (this.first.getValue($$0, $$1, $$2) + this.second.getValue($$3, $$4, $$5)) * this.valueFactor;
    }

    public NoiseParameters parameters() {
        return this.parameters;
    }

    @VisibleForTesting
    public void parityConfigString(StringBuilder $$0) {
        $$0.append("NormalNoise {");
        $$0.append("first: ");
        this.first.parityConfigString($$0);
        $$0.append(", second: ");
        this.second.parityConfigString($$0);
        $$0.append("}");
    }

    public record NoiseParameters(int firstOctave, DoubleList amplitudes) {
        public static final Codec<NoiseParameters> DIRECT_CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)Codec.INT.fieldOf("firstOctave").forGetter(NoiseParameters::firstOctave), (App)Codec.DOUBLE.listOf().fieldOf("amplitudes").forGetter(NoiseParameters::amplitudes)).apply((Applicative)$$0, NoiseParameters::new));
        public static final Codec<Holder<NoiseParameters>> CODEC = RegistryFileCodec.create(Registries.NOISE, DIRECT_CODEC);

        public NoiseParameters(int $$0, List<Double> $$1) {
            this($$0, (DoubleList)new DoubleArrayList($$1));
        }

        public NoiseParameters(int $$0, double $$12, double ... $$2) {
            this($$0, (DoubleList)Util.make(new DoubleArrayList($$2), $$1 -> $$1.add(0, $$12)));
        }
    }
}