/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.level.redstone;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.NeighborUpdater;

public class InstantNeighborUpdater
implements NeighborUpdater {
    private final Level level;

    public InstantNeighborUpdater(Level $$0) {
        this.level = $$0;
    }

    @Override
    public void shapeUpdate(Direction $$0, BlockState $$1, BlockPos $$2, BlockPos $$3, int $$4, int $$5) {
        NeighborUpdater.executeShapeUpdate(this.level, $$0, $$1, $$2, $$3, $$4, $$5 - 1);
    }

    @Override
    public void neighborChanged(BlockPos $$0, Block $$1, BlockPos $$2) {
        BlockState $$3 = this.level.getBlockState($$0);
        this.neighborChanged($$3, $$0, $$1, $$2, false);
    }

    @Override
    public void neighborChanged(BlockState $$0, BlockPos $$1, Block $$2, BlockPos $$3, boolean $$4) {
        NeighborUpdater.executeUpdate(this.level, $$0, $$1, $$2, $$3, $$4);
    }
}