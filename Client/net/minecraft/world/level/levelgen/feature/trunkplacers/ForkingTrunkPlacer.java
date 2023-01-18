/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.OptionalInt
 *  java.util.function.BiConsumer
 */
package net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.google.common.collect.Lists;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

public class ForkingTrunkPlacer
extends TrunkPlacer {
    public static final Codec<ForkingTrunkPlacer> CODEC = RecordCodecBuilder.create($$0 -> ForkingTrunkPlacer.trunkPlacerParts($$0).apply((Applicative)$$0, ForkingTrunkPlacer::new));

    public ForkingTrunkPlacer(int $$0, int $$1, int $$2) {
        super($$0, $$1, $$2);
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return TrunkPlacerType.FORKING_TRUNK_PLACER;
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader $$0, BiConsumer<BlockPos, BlockState> $$1, RandomSource $$2, int $$3, BlockPos $$4, TreeConfiguration $$5) {
        ForkingTrunkPlacer.setDirtAt($$0, $$1, $$2, (BlockPos)$$4.below(), $$5);
        ArrayList $$6 = Lists.newArrayList();
        Direction $$7 = Direction.Plane.HORIZONTAL.getRandomDirection($$2);
        int $$8 = $$3 - $$2.nextInt(4) - 1;
        int $$9 = 3 - $$2.nextInt(3);
        BlockPos.MutableBlockPos $$10 = new BlockPos.MutableBlockPos();
        int $$11 = $$4.getX();
        int $$12 = $$4.getZ();
        OptionalInt $$13 = OptionalInt.empty();
        for (int $$14 = 0; $$14 < $$3; ++$$14) {
            int $$15 = $$4.getY() + $$14;
            if ($$14 >= $$8 && $$9 > 0) {
                $$11 += $$7.getStepX();
                $$12 += $$7.getStepZ();
                --$$9;
            }
            if (!this.placeLog($$0, $$1, $$2, $$10.set($$11, $$15, $$12), $$5)) continue;
            $$13 = OptionalInt.of((int)($$15 + 1));
        }
        if ($$13.isPresent()) {
            $$6.add((Object)new FoliagePlacer.FoliageAttachment(new BlockPos($$11, $$13.getAsInt(), $$12), 1, false));
        }
        $$11 = $$4.getX();
        $$12 = $$4.getZ();
        Direction $$16 = Direction.Plane.HORIZONTAL.getRandomDirection($$2);
        if ($$16 != $$7) {
            int $$17 = $$8 - $$2.nextInt(2) - 1;
            int $$18 = 1 + $$2.nextInt(3);
            $$13 = OptionalInt.empty();
            for (int $$19 = $$17; $$19 < $$3 && $$18 > 0; ++$$19, --$$18) {
                if ($$19 < 1) continue;
                int $$20 = $$4.getY() + $$19;
                if (!this.placeLog($$0, $$1, $$2, $$10.set($$11 += $$16.getStepX(), $$20, $$12 += $$16.getStepZ()), $$5)) continue;
                $$13 = OptionalInt.of((int)($$20 + 1));
            }
            if ($$13.isPresent()) {
                $$6.add((Object)new FoliagePlacer.FoliageAttachment(new BlockPos($$11, $$13.getAsInt(), $$12), 0, false));
            }
        }
        return $$6;
    }
}