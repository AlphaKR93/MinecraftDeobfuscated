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
 *  java.util.stream.Stream
 */
package net.minecraft.world.level.levelgen.placement;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

public class RandomOffsetPlacement
extends PlacementModifier {
    public static final Codec<RandomOffsetPlacement> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)IntProvider.codec(-16, 16).fieldOf("xz_spread").forGetter($$0 -> $$0.xzSpread), (App)IntProvider.codec(-16, 16).fieldOf("y_spread").forGetter($$0 -> $$0.ySpread)).apply((Applicative)$$02, RandomOffsetPlacement::new));
    private final IntProvider xzSpread;
    private final IntProvider ySpread;

    public static RandomOffsetPlacement of(IntProvider $$0, IntProvider $$1) {
        return new RandomOffsetPlacement($$0, $$1);
    }

    public static RandomOffsetPlacement vertical(IntProvider $$0) {
        return new RandomOffsetPlacement(ConstantInt.of(0), $$0);
    }

    public static RandomOffsetPlacement horizontal(IntProvider $$0) {
        return new RandomOffsetPlacement($$0, ConstantInt.of(0));
    }

    private RandomOffsetPlacement(IntProvider $$0, IntProvider $$1) {
        this.xzSpread = $$0;
        this.ySpread = $$1;
    }

    @Override
    public Stream<BlockPos> getPositions(PlacementContext $$0, RandomSource $$1, BlockPos $$2) {
        int $$3 = $$2.getX() + this.xzSpread.sample($$1);
        int $$4 = $$2.getY() + this.ySpread.sample($$1);
        int $$5 = $$2.getZ() + this.xzSpread.sample($$1);
        return Stream.of((Object)new BlockPos($$3, $$4, $$5));
    }

    @Override
    public PlacementModifierType<?> type() {
        return PlacementModifierType.RANDOM_OFFSET;
    }
}