/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TrappedChestBlockEntity
extends ChestBlockEntity {
    public TrappedChestBlockEntity(BlockPos $$0, BlockState $$1) {
        super(BlockEntityType.TRAPPED_CHEST, $$0, $$1);
    }

    @Override
    protected void signalOpenCount(Level $$0, BlockPos $$1, BlockState $$2, int $$3, int $$4) {
        super.signalOpenCount($$0, $$1, $$2, $$3, $$4);
        if ($$3 != $$4) {
            Block $$5 = $$2.getBlock();
            $$0.updateNeighborsAt($$1, $$5);
            $$0.updateNeighborsAt((BlockPos)$$1.below(), $$5);
        }
    }
}