/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class NetherrackBlock
extends Block
implements BonemealableBlock {
    public NetherrackBlock(BlockBehaviour.Properties $$0) {
        super($$0);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader $$0, BlockPos $$1, BlockState $$2, boolean $$3) {
        if (!$$0.getBlockState((BlockPos)$$1.above()).propagatesSkylightDown($$0, $$1)) {
            return false;
        }
        for (BlockPos $$4 : BlockPos.betweenClosed($$1.offset(-1, -1, -1), $$1.offset(1, 1, 1))) {
            if (!$$0.getBlockState($$4).is(BlockTags.NYLIUM)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isBonemealSuccess(Level $$0, RandomSource $$1, BlockPos $$2, BlockState $$3) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel $$0, RandomSource $$1, BlockPos $$2, BlockState $$3) {
        boolean $$4 = false;
        boolean $$5 = false;
        for (BlockPos $$6 : BlockPos.betweenClosed($$2.offset(-1, -1, -1), $$2.offset(1, 1, 1))) {
            BlockState $$7 = $$0.getBlockState($$6);
            if ($$7.is(Blocks.WARPED_NYLIUM)) {
                $$5 = true;
            }
            if ($$7.is(Blocks.CRIMSON_NYLIUM)) {
                $$4 = true;
            }
            if (!$$5 || !$$4) continue;
            break;
        }
        if ($$5 && $$4) {
            $$0.setBlock($$2, $$1.nextBoolean() ? Blocks.WARPED_NYLIUM.defaultBlockState() : Blocks.CRIMSON_NYLIUM.defaultBlockState(), 3);
        } else if ($$5) {
            $$0.setBlock($$2, Blocks.WARPED_NYLIUM.defaultBlockState(), 3);
        } else if ($$4) {
            $$0.setBlock($$2, Blocks.CRIMSON_NYLIUM.defaultBlockState(), 3);
        }
    }
}