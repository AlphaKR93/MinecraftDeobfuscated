/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;

public interface LevelWriter {
    public boolean setBlock(BlockPos var1, BlockState var2, int var3, int var4);

    default public boolean setBlock(BlockPos $$0, BlockState $$1, int $$2) {
        return this.setBlock($$0, $$1, $$2, 512);
    }

    public boolean removeBlock(BlockPos var1, boolean var2);

    default public boolean destroyBlock(BlockPos $$0, boolean $$1) {
        return this.destroyBlock($$0, $$1, null);
    }

    default public boolean destroyBlock(BlockPos $$0, boolean $$1, @Nullable Entity $$2) {
        return this.destroyBlock($$0, $$1, $$2, 512);
    }

    public boolean destroyBlock(BlockPos var1, boolean var2, @Nullable Entity var3, int var4);

    default public boolean addFreshEntity(Entity $$0) {
        return false;
    }
}