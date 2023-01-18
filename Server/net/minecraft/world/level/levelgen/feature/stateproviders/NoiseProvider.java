/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Products$P4
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Mu
 *  java.lang.Float
 *  java.lang.Long
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 */
package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraft.world.level.levelgen.feature.stateproviders.NoiseBasedStateProvider;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class NoiseProvider
extends NoiseBasedStateProvider {
    public static final Codec<NoiseProvider> CODEC = RecordCodecBuilder.create($$0 -> NoiseProvider.noiseProviderCodec($$0).apply((Applicative)$$0, NoiseProvider::new));
    protected final List<BlockState> states;

    protected static <P extends NoiseProvider> Products.P4<RecordCodecBuilder.Mu<P>, Long, NormalNoise.NoiseParameters, Float, List<BlockState>> noiseProviderCodec(RecordCodecBuilder.Instance<P> $$02) {
        return NoiseProvider.noiseCodec($$02).and((App)Codec.list(BlockState.CODEC).fieldOf("states").forGetter($$0 -> $$0.states));
    }

    public NoiseProvider(long $$0, NormalNoise.NoiseParameters $$1, float $$2, List<BlockState> $$3) {
        super($$0, $$1, $$2);
        this.states = $$3;
    }

    @Override
    protected BlockStateProviderType<?> type() {
        return BlockStateProviderType.NOISE_PROVIDER;
    }

    @Override
    public BlockState getState(RandomSource $$0, BlockPos $$1) {
        return this.getRandomState(this.states, $$1, this.scale);
    }

    protected BlockState getRandomState(List<BlockState> $$0, BlockPos $$1, double $$2) {
        double $$3 = this.getNoiseValue($$1, $$2);
        return this.getRandomState($$0, $$3);
    }

    protected BlockState getRandomState(List<BlockState> $$0, double $$1) {
        double $$2 = Mth.clamp((1.0 + $$1) / 2.0, 0.0, 0.9999);
        return (BlockState)$$0.get((int)($$2 * (double)$$0.size()));
    }
}