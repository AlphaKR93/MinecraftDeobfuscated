/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class LavaCauldronBlock
extends AbstractCauldronBlock {
    public LavaCauldronBlock(BlockBehaviour.Properties $$0) {
        super($$0, CauldronInteraction.LAVA);
    }

    @Override
    protected double getContentHeight(BlockState $$0) {
        return 0.9375;
    }

    @Override
    public boolean isFull(BlockState $$0) {
        return true;
    }

    @Override
    public void entityInside(BlockState $$0, Level $$1, BlockPos $$2, Entity $$3) {
        if (this.isEntityInsideContent($$0, $$2, $$3)) {
            $$3.lavaHurt();
        }
    }

    @Override
    public int getAnalogOutputSignal(BlockState $$0, Level $$1, BlockPos $$2) {
        return 3;
    }
}