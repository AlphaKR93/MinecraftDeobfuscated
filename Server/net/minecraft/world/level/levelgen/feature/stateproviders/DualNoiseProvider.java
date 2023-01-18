/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Float
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.ArrayList
 *  java.util.List
 */
package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.google.common.collect.Lists;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.InclusiveRange;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraft.world.level.levelgen.feature.stateproviders.NoiseProvider;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class DualNoiseProvider
extends NoiseProvider {
    public static final Codec<DualNoiseProvider> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)InclusiveRange.codec(Codec.INT, 1, 64).fieldOf("variety").forGetter($$0 -> $$0.variety), (App)NormalNoise.NoiseParameters.DIRECT_CODEC.fieldOf("slow_noise").forGetter($$0 -> $$0.slowNoiseParameters), (App)ExtraCodecs.POSITIVE_FLOAT.fieldOf("slow_scale").forGetter($$0 -> Float.valueOf((float)$$0.slowScale))).and(DualNoiseProvider.noiseProviderCodec($$02)).apply((Applicative)$$02, DualNoiseProvider::new));
    private final InclusiveRange<Integer> variety;
    private final NormalNoise.NoiseParameters slowNoiseParameters;
    private final float slowScale;
    private final NormalNoise slowNoise;

    public DualNoiseProvider(InclusiveRange<Integer> $$0, NormalNoise.NoiseParameters $$1, float $$2, long $$3, NormalNoise.NoiseParameters $$4, float $$5, List<BlockState> $$6) {
        super($$3, $$4, $$5, $$6);
        this.variety = $$0;
        this.slowNoiseParameters = $$1;
        this.slowScale = $$2;
        this.slowNoise = NormalNoise.create(new WorldgenRandom(new LegacyRandomSource($$3)), $$1);
    }

    @Override
    protected BlockStateProviderType<?> type() {
        return BlockStateProviderType.DUAL_NOISE_PROVIDER;
    }

    @Override
    public BlockState getState(RandomSource $$0, BlockPos $$1) {
        double $$2 = this.getSlowNoiseValue($$1);
        int $$3 = (int)Mth.clampedMap($$2, -1.0, 1.0, (double)this.variety.minInclusive().intValue(), (double)(this.variety.maxInclusive() + 1));
        ArrayList $$4 = Lists.newArrayListWithCapacity((int)$$3);
        for (int $$5 = 0; $$5 < $$3; ++$$5) {
            $$4.add((Object)this.getRandomState((List<BlockState>)this.states, this.getSlowNoiseValue($$1.offset($$5 * 54545, 0, $$5 * 34234))));
        }
        return this.getRandomState((List<BlockState>)$$4, $$1, this.scale);
    }

    protected double getSlowNoiseValue(BlockPos $$0) {
        return this.slowNoise.getValue((float)$$0.getX() * this.slowScale, (float)$$0.getY() * this.slowScale, (float)$$0.getZ() * this.slowScale);
    }
}