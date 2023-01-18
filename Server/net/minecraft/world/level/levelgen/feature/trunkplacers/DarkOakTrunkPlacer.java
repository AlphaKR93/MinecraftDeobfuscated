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
 *  java.util.function.BiConsumer
 */
package net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.google.common.collect.Lists;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

public class DarkOakTrunkPlacer
extends TrunkPlacer {
    public static final Codec<DarkOakTrunkPlacer> CODEC = RecordCodecBuilder.create($$0 -> DarkOakTrunkPlacer.trunkPlacerParts($$0).apply((Applicative)$$0, DarkOakTrunkPlacer::new));

    public DarkOakTrunkPlacer(int $$0, int $$1, int $$2) {
        super($$0, $$1, $$2);
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return TrunkPlacerType.DARK_OAK_TRUNK_PLACER;
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader $$0, BiConsumer<BlockPos, BlockState> $$1, RandomSource $$2, int $$3, BlockPos $$4, TreeConfiguration $$5) {
        ArrayList $$6 = Lists.newArrayList();
        Vec3i $$7 = $$4.below();
        DarkOakTrunkPlacer.setDirtAt($$0, $$1, $$2, (BlockPos)$$7, $$5);
        DarkOakTrunkPlacer.setDirtAt($$0, $$1, $$2, (BlockPos)((BlockPos)$$7).east(), $$5);
        DarkOakTrunkPlacer.setDirtAt($$0, $$1, $$2, (BlockPos)((BlockPos)$$7).south(), $$5);
        DarkOakTrunkPlacer.setDirtAt($$0, $$1, $$2, (BlockPos)((BlockPos)((BlockPos)$$7).south()).east(), $$5);
        Direction $$8 = Direction.Plane.HORIZONTAL.getRandomDirection($$2);
        int $$9 = $$3 - $$2.nextInt(4);
        int $$10 = 2 - $$2.nextInt(3);
        int $$11 = $$4.getX();
        int $$12 = $$4.getY();
        int $$13 = $$4.getZ();
        int $$14 = $$11;
        int $$15 = $$13;
        int $$16 = $$12 + $$3 - 1;
        for (int $$17 = 0; $$17 < $$3; ++$$17) {
            int $$18;
            BlockPos $$19;
            if ($$17 >= $$9 && $$10 > 0) {
                $$14 += $$8.getStepX();
                $$15 += $$8.getStepZ();
                --$$10;
            }
            if (!TreeFeature.isAirOrLeaves($$0, $$19 = new BlockPos($$14, $$18 = $$12 + $$17, $$15))) continue;
            this.placeLog($$0, $$1, $$2, $$19, $$5);
            this.placeLog($$0, $$1, $$2, (BlockPos)$$19.east(), $$5);
            this.placeLog($$0, $$1, $$2, (BlockPos)$$19.south(), $$5);
            this.placeLog($$0, $$1, $$2, (BlockPos)((BlockPos)$$19.east()).south(), $$5);
        }
        $$6.add((Object)new FoliagePlacer.FoliageAttachment(new BlockPos($$14, $$16, $$15), 0, true));
        for (int $$20 = -1; $$20 <= 2; ++$$20) {
            for (int $$21 = -1; $$21 <= 2; ++$$21) {
                if ($$20 >= 0 && $$20 <= 1 && $$21 >= 0 && $$21 <= 1 || $$2.nextInt(3) > 0) continue;
                int $$22 = $$2.nextInt(3) + 2;
                for (int $$23 = 0; $$23 < $$22; ++$$23) {
                    this.placeLog($$0, $$1, $$2, new BlockPos($$11 + $$20, $$16 - $$23 - 1, $$13 + $$21), $$5);
                }
                $$6.add((Object)new FoliagePlacer.FoliageAttachment(new BlockPos($$14 + $$20, $$16, $$15 + $$21), 0, false));
            }
        }
        return $$6;
    }
}