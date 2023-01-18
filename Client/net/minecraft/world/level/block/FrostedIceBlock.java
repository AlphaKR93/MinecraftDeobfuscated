/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class FrostedIceBlock
extends IceBlock {
    public static final int MAX_AGE = 3;
    public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
    private static final int NEIGHBORS_TO_AGE = 4;
    private static final int NEIGHBORS_TO_MELT = 2;

    public FrostedIceBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(AGE, 0));
    }

    @Override
    public void randomTick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        this.tick($$0, $$1, $$2, $$3);
    }

    @Override
    public void tick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        if (($$3.nextInt(3) == 0 || this.fewerNeigboursThan($$1, $$2, 4)) && $$1.getMaxLocalRawBrightness($$2) > 11 - $$0.getValue(AGE) - $$0.getLightBlock($$1, $$2) && this.slightlyMelt($$0, $$1, $$2)) {
            BlockPos.MutableBlockPos $$4 = new BlockPos.MutableBlockPos();
            for (Direction $$5 : Direction.values()) {
                $$4.setWithOffset((Vec3i)$$2, $$5);
                BlockState $$6 = $$1.getBlockState($$4);
                if (!$$6.is(this) || this.slightlyMelt($$6, $$1, $$4)) continue;
                $$1.scheduleTick($$4, this, Mth.nextInt($$3, 20, 40));
            }
            return;
        }
        $$1.scheduleTick($$2, this, Mth.nextInt($$3, 20, 40));
    }

    private boolean slightlyMelt(BlockState $$0, Level $$1, BlockPos $$2) {
        int $$3 = $$0.getValue(AGE);
        if ($$3 < 3) {
            $$1.setBlock($$2, (BlockState)$$0.setValue(AGE, $$3 + 1), 2);
            return false;
        }
        this.melt($$0, $$1, $$2);
        return true;
    }

    @Override
    public void neighborChanged(BlockState $$0, Level $$1, BlockPos $$2, Block $$3, BlockPos $$4, boolean $$5) {
        if ($$3.defaultBlockState().is(this) && this.fewerNeigboursThan($$1, $$2, 2)) {
            this.melt($$0, $$1, $$2);
        }
        super.neighborChanged($$0, $$1, $$2, $$3, $$4, $$5);
    }

    private boolean fewerNeigboursThan(BlockGetter $$0, BlockPos $$1, int $$2) {
        int $$3 = 0;
        BlockPos.MutableBlockPos $$4 = new BlockPos.MutableBlockPos();
        for (Direction $$5 : Direction.values()) {
            $$4.setWithOffset((Vec3i)$$1, $$5);
            if (!$$0.getBlockState($$4).is(this) || ++$$3 < $$2) continue;
            return false;
        }
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(AGE);
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter $$0, BlockPos $$1, BlockState $$2) {
        return ItemStack.EMPTY;
    }
}