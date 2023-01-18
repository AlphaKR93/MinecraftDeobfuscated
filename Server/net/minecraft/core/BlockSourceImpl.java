/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.core;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BlockSourceImpl
implements BlockSource {
    private final ServerLevel level;
    private final BlockPos pos;

    public BlockSourceImpl(ServerLevel $$0, BlockPos $$1) {
        this.level = $$0;
        this.pos = $$1;
    }

    @Override
    public ServerLevel getLevel() {
        return this.level;
    }

    @Override
    public double x() {
        return (double)this.pos.getX() + 0.5;
    }

    @Override
    public double y() {
        return (double)this.pos.getY() + 0.5;
    }

    @Override
    public double z() {
        return (double)this.pos.getZ() + 0.5;
    }

    @Override
    public BlockPos getPos() {
        return this.pos;
    }

    @Override
    public BlockState getBlockState() {
        return this.level.getBlockState(this.pos);
    }

    @Override
    public <T extends BlockEntity> T getEntity() {
        return (T)this.level.getBlockEntity(this.pos);
    }
}