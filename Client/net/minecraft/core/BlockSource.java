/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.core;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public interface BlockSource
extends Position {
    @Override
    public double x();

    @Override
    public double y();

    @Override
    public double z();

    public BlockPos getPos();

    public BlockState getBlockState();

    public <T extends BlockEntity> T getEntity();

    public ServerLevel getLevel();
}