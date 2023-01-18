/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.level.block;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class HalfTransparentBlock
extends Block {
    protected HalfTransparentBlock(BlockBehaviour.Properties $$0) {
        super($$0);
    }

    @Override
    public boolean skipRendering(BlockState $$0, BlockState $$1, Direction $$2) {
        if ($$1.is(this)) {
            return true;
        }
        return super.skipRendering($$0, $$1, $$2);
    }
}