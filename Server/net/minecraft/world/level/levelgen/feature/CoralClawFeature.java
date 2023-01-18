/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.stream.Stream
 */
package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.CoralFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class CoralClawFeature
extends CoralFeature {
    public CoralClawFeature(Codec<NoneFeatureConfiguration> $$0) {
        super($$0);
    }

    @Override
    protected boolean placeFeature(LevelAccessor $$0, RandomSource $$1, BlockPos $$2, BlockState $$3) {
        if (!this.placeCoralBlock($$0, $$1, $$2, $$3)) {
            return false;
        }
        Direction $$4 = Direction.Plane.HORIZONTAL.getRandomDirection($$1);
        int $$5 = $$1.nextInt(2) + 2;
        List $$6 = Util.toShuffledList(Stream.of((Object[])new Direction[]{$$4, $$4.getClockWise(), $$4.getCounterClockWise()}), $$1);
        List $$7 = $$6.subList(0, $$5);
        block0: for (Direction $$8 : $$7) {
            int $$15;
            Direction $$14;
            BlockPos.MutableBlockPos $$9 = $$2.mutable();
            int $$10 = $$1.nextInt(2) + 1;
            $$9.move($$8);
            if ($$8 == $$4) {
                Direction $$11 = $$4;
                int $$12 = $$1.nextInt(3) + 2;
            } else {
                $$9.move(Direction.UP);
                Direction[] $$13 = new Direction[]{$$8, Direction.UP};
                $$14 = Util.getRandom($$13, $$1);
                $$15 = $$1.nextInt(3) + 3;
            }
            for (int $$16 = 0; $$16 < $$10 && this.placeCoralBlock($$0, $$1, $$9, $$3); ++$$16) {
                $$9.move($$14);
            }
            $$9.move($$14.getOpposite());
            $$9.move(Direction.UP);
            for (int $$17 = 0; $$17 < $$15; ++$$17) {
                $$9.move($$4);
                if (!this.placeCoralBlock($$0, $$1, $$9, $$3)) continue block0;
                if (!($$1.nextFloat() < 0.25f)) continue;
                $$9.move(Direction.UP);
            }
        }
        return true;
    }
}