/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.level;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.BlockColumn;

public final class NoiseColumn
implements BlockColumn {
    private final int minY;
    private final BlockState[] column;

    public NoiseColumn(int $$0, BlockState[] $$1) {
        this.minY = $$0;
        this.column = $$1;
    }

    @Override
    public BlockState getBlock(int $$0) {
        int $$1 = $$0 - this.minY;
        if ($$1 < 0 || $$1 >= this.column.length) {
            return Blocks.AIR.defaultBlockState();
        }
        return this.column[$$1];
    }

    @Override
    public void setBlock(int $$0, BlockState $$1) {
        int $$2 = $$0 - this.minY;
        if ($$2 < 0 || $$2 >= this.column.length) {
            throw new IllegalArgumentException("Outside of column height: " + $$0);
        }
        this.column[$$2] = $$1;
    }
}