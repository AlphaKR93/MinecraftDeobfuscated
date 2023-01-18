/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.function.BiConsumer
 */
package net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

public class GiantTrunkPlacer
extends TrunkPlacer {
    public static final Codec<GiantTrunkPlacer> CODEC = RecordCodecBuilder.create($$0 -> GiantTrunkPlacer.trunkPlacerParts($$0).apply((Applicative)$$0, GiantTrunkPlacer::new));

    public GiantTrunkPlacer(int $$0, int $$1, int $$2) {
        super($$0, $$1, $$2);
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return TrunkPlacerType.GIANT_TRUNK_PLACER;
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader $$0, BiConsumer<BlockPos, BlockState> $$1, RandomSource $$2, int $$3, BlockPos $$4, TreeConfiguration $$5) {
        Vec3i $$6 = $$4.below();
        GiantTrunkPlacer.setDirtAt($$0, $$1, $$2, (BlockPos)$$6, $$5);
        GiantTrunkPlacer.setDirtAt($$0, $$1, $$2, (BlockPos)((BlockPos)$$6).east(), $$5);
        GiantTrunkPlacer.setDirtAt($$0, $$1, $$2, (BlockPos)((BlockPos)$$6).south(), $$5);
        GiantTrunkPlacer.setDirtAt($$0, $$1, $$2, (BlockPos)((BlockPos)((BlockPos)$$6).south()).east(), $$5);
        BlockPos.MutableBlockPos $$7 = new BlockPos.MutableBlockPos();
        for (int $$8 = 0; $$8 < $$3; ++$$8) {
            this.placeLogIfFreeWithOffset($$0, $$1, $$2, $$7, $$5, $$4, 0, $$8, 0);
            if ($$8 >= $$3 - 1) continue;
            this.placeLogIfFreeWithOffset($$0, $$1, $$2, $$7, $$5, $$4, 1, $$8, 0);
            this.placeLogIfFreeWithOffset($$0, $$1, $$2, $$7, $$5, $$4, 1, $$8, 1);
            this.placeLogIfFreeWithOffset($$0, $$1, $$2, $$7, $$5, $$4, 0, $$8, 1);
        }
        return ImmutableList.of((Object)new FoliagePlacer.FoliageAttachment((BlockPos)$$4.above($$3), 0, true));
    }

    private void placeLogIfFreeWithOffset(LevelSimulatedReader $$0, BiConsumer<BlockPos, BlockState> $$1, RandomSource $$2, BlockPos.MutableBlockPos $$3, TreeConfiguration $$4, BlockPos $$5, int $$6, int $$7, int $$8) {
        $$3.setWithOffset($$5, $$6, $$7, $$8);
        this.placeLogIfFree($$0, $$1, $$2, $$3, $$4);
    }
}