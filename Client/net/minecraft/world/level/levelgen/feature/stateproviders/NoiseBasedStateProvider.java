/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Products$P3
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Mu
 *  java.lang.Float
 *  java.lang.Long
 *  java.lang.Object
 */
package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public abstract class NoiseBasedStateProvider
extends BlockStateProvider {
    protected final long seed;
    protected final NormalNoise.NoiseParameters parameters;
    protected final float scale;
    protected final NormalNoise noise;

    protected static <P extends NoiseBasedStateProvider> Products.P3<RecordCodecBuilder.Mu<P>, Long, NormalNoise.NoiseParameters, Float> noiseCodec(RecordCodecBuilder.Instance<P> $$02) {
        return $$02.group((App)Codec.LONG.fieldOf("seed").forGetter($$0 -> $$0.seed), (App)NormalNoise.NoiseParameters.DIRECT_CODEC.fieldOf("noise").forGetter($$0 -> $$0.parameters), (App)ExtraCodecs.POSITIVE_FLOAT.fieldOf("scale").forGetter($$0 -> Float.valueOf((float)$$0.scale)));
    }

    protected NoiseBasedStateProvider(long $$0, NormalNoise.NoiseParameters $$1, float $$2) {
        this.seed = $$0;
        this.parameters = $$1;
        this.scale = $$2;
        this.noise = NormalNoise.create(new WorldgenRandom(new LegacyRandomSource($$0)), $$1);
    }

    protected double getNoiseValue(BlockPos $$0, double $$1) {
        return this.noise.getValue((double)$$0.getX() * $$1, (double)$$0.getY() * $$1, (double)$$0.getZ() * $$1);
    }
}