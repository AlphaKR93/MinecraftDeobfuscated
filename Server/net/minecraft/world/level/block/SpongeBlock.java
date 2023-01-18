/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.LinkedList
 */
package net.minecraft.world.level.block;

import com.google.common.collect.Lists;
import java.util.LinkedList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;

public class SpongeBlock
extends Block {
    public static final int MAX_DEPTH = 6;
    public static final int MAX_COUNT = 64;

    protected SpongeBlock(BlockBehaviour.Properties $$0) {
        super($$0);
    }

    @Override
    public void onPlace(BlockState $$0, Level $$1, BlockPos $$2, BlockState $$3, boolean $$4) {
        if ($$3.is($$0.getBlock())) {
            return;
        }
        this.tryAbsorbWater($$1, $$2);
    }

    @Override
    public void neighborChanged(BlockState $$0, Level $$1, BlockPos $$2, Block $$3, BlockPos $$4, boolean $$5) {
        this.tryAbsorbWater($$1, $$2);
        super.neighborChanged($$0, $$1, $$2, $$3, $$4, $$5);
    }

    protected void tryAbsorbWater(Level $$0, BlockPos $$1) {
        if (this.removeWaterBreadthFirstSearch($$0, $$1)) {
            $$0.setBlock($$1, Blocks.WET_SPONGE.defaultBlockState(), 2);
            $$0.levelEvent(2001, $$1, Block.getId(Blocks.WATER.defaultBlockState()));
        }
    }

    private boolean removeWaterBreadthFirstSearch(Level $$0, BlockPos $$1) {
        LinkedList $$2 = Lists.newLinkedList();
        $$2.add(new Tuple<BlockPos, Integer>($$1, 0));
        int $$3 = 0;
        while (!$$2.isEmpty()) {
            Tuple $$4 = (Tuple)$$2.poll();
            BlockPos $$5 = (BlockPos)$$4.getA();
            int $$6 = (Integer)$$4.getB();
            for (Direction $$7 : Direction.values()) {
                Vec3i $$8 = $$5.relative($$7);
                BlockState $$9 = $$0.getBlockState((BlockPos)$$8);
                FluidState $$10 = $$0.getFluidState((BlockPos)$$8);
                Material $$11 = $$9.getMaterial();
                if (!$$10.is(FluidTags.WATER)) continue;
                if ($$9.getBlock() instanceof BucketPickup && !((BucketPickup)((Object)$$9.getBlock())).pickupBlock($$0, (BlockPos)$$8, $$9).isEmpty()) {
                    ++$$3;
                    if ($$6 >= 6) continue;
                    $$2.add(new Tuple<Vec3i, Integer>($$8, $$6 + 1));
                    continue;
                }
                if ($$9.getBlock() instanceof LiquidBlock) {
                    $$0.setBlock((BlockPos)$$8, Blocks.AIR.defaultBlockState(), 3);
                    ++$$3;
                    if ($$6 >= 6) continue;
                    $$2.add(new Tuple<Vec3i, Integer>($$8, $$6 + 1));
                    continue;
                }
                if ($$11 != Material.WATER_PLANT && $$11 != Material.REPLACEABLE_WATER_PLANT) continue;
                BlockEntity $$12 = $$9.hasBlockEntity() ? $$0.getBlockEntity((BlockPos)$$8) : null;
                SpongeBlock.dropResources($$9, $$0, (BlockPos)$$8, $$12);
                $$0.setBlock((BlockPos)$$8, Blocks.AIR.defaultBlockState(), 3);
                ++$$3;
                if ($$6 >= 6) continue;
                $$2.add(new Tuple<Vec3i, Integer>($$8, $$6 + 1));
            }
            if ($$3 <= 64) continue;
            break;
        }
        return $$3 > 0;
    }
}