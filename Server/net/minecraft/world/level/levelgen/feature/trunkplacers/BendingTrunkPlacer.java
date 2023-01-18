/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.function.BiConsumer
 */
package net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.google.common.collect.Lists;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

public class BendingTrunkPlacer
extends TrunkPlacer {
    public static final Codec<BendingTrunkPlacer> CODEC = RecordCodecBuilder.create($$02 -> BendingTrunkPlacer.trunkPlacerParts($$02).and($$02.group((App)ExtraCodecs.POSITIVE_INT.optionalFieldOf("min_height_for_leaves", (Object)1).forGetter($$0 -> $$0.minHeightForLeaves), (App)IntProvider.codec(1, 64).fieldOf("bend_length").forGetter($$0 -> $$0.bendLength))).apply((Applicative)$$02, BendingTrunkPlacer::new));
    private final int minHeightForLeaves;
    private final IntProvider bendLength;

    public BendingTrunkPlacer(int $$0, int $$1, int $$2, int $$3, IntProvider $$4) {
        super($$0, $$1, $$2);
        this.minHeightForLeaves = $$3;
        this.bendLength = $$4;
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return TrunkPlacerType.BENDING_TRUNK_PLACER;
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader $$0, BiConsumer<BlockPos, BlockState> $$1, RandomSource $$2, int $$3, BlockPos $$4, TreeConfiguration $$5) {
        Direction $$6 = Direction.Plane.HORIZONTAL.getRandomDirection($$2);
        int $$7 = $$3 - 1;
        BlockPos.MutableBlockPos $$8 = $$4.mutable();
        Vec3i $$9 = $$8.below();
        BendingTrunkPlacer.setDirtAt($$0, $$1, $$2, (BlockPos)$$9, $$5);
        ArrayList $$10 = Lists.newArrayList();
        for (int $$11 = 0; $$11 <= $$7; ++$$11) {
            if ($$11 + 1 >= $$7 + $$2.nextInt(2)) {
                $$8.move($$6);
            }
            if (TreeFeature.validTreePos($$0, $$8)) {
                this.placeLog($$0, $$1, $$2, $$8, $$5);
            }
            if ($$11 >= this.minHeightForLeaves) {
                $$10.add((Object)new FoliagePlacer.FoliageAttachment($$8.immutable(), 0, false));
            }
            $$8.move(Direction.UP);
        }
        int $$12 = this.bendLength.sample($$2);
        for (int $$13 = 0; $$13 <= $$12; ++$$13) {
            if (TreeFeature.validTreePos($$0, $$8)) {
                this.placeLog($$0, $$1, $$2, $$8, $$5);
            }
            $$10.add((Object)new FoliagePlacer.FoliageAttachment($$8.immutable(), 0, false));
            $$8.move($$6);
        }
        return $$10;
    }
}