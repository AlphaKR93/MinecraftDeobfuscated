/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  java.lang.Float
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

public class LeaveVineDecorator
extends TreeDecorator {
    public static final Codec<LeaveVineDecorator> CODEC = Codec.floatRange((float)0.0f, (float)1.0f).fieldOf("probability").xmap(LeaveVineDecorator::new, $$0 -> Float.valueOf((float)$$0.probability)).codec();
    private final float probability;

    @Override
    protected TreeDecoratorType<?> type() {
        return TreeDecoratorType.LEAVE_VINE;
    }

    public LeaveVineDecorator(float $$0) {
        this.probability = $$0;
    }

    @Override
    public void place(TreeDecorator.Context $$0) {
        RandomSource $$1 = $$0.random();
        $$0.leaves().forEach($$2 -> {
            Vec3i $$6;
            Vec3i $$5;
            Vec3i $$4;
            Vec3i $$3;
            if ($$1.nextFloat() < this.probability && $$0.isAir((BlockPos)($$3 = $$2.west()))) {
                LeaveVineDecorator.addHangingVine((BlockPos)$$3, VineBlock.EAST, $$0);
            }
            if ($$1.nextFloat() < this.probability && $$0.isAir((BlockPos)($$4 = $$2.east()))) {
                LeaveVineDecorator.addHangingVine((BlockPos)$$4, VineBlock.WEST, $$0);
            }
            if ($$1.nextFloat() < this.probability && $$0.isAir((BlockPos)($$5 = $$2.north()))) {
                LeaveVineDecorator.addHangingVine((BlockPos)$$5, VineBlock.SOUTH, $$0);
            }
            if ($$1.nextFloat() < this.probability && $$0.isAir((BlockPos)($$6 = $$2.south()))) {
                LeaveVineDecorator.addHangingVine((BlockPos)$$6, VineBlock.NORTH, $$0);
            }
        });
    }

    private static void addHangingVine(BlockPos $$0, BooleanProperty $$1, TreeDecorator.Context $$2) {
        $$2.placeVine((BlockPos)$$0, $$1);
        $$0 = ((BlockPos)$$0).below();
        for (int $$3 = 4; $$2.isAir((BlockPos)$$0) && $$3 > 0; --$$3) {
            $$2.placeVine((BlockPos)$$0, $$1);
            $$0 = ((BlockPos)$$0).below();
        }
    }
}