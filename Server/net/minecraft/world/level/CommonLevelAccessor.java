/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.Optional
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.EntityGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface CommonLevelAccessor
extends EntityGetter,
LevelReader,
LevelSimulatedRW {
    @Override
    default public <T extends BlockEntity> Optional<T> getBlockEntity(BlockPos $$0, BlockEntityType<T> $$1) {
        return LevelReader.super.getBlockEntity($$0, $$1);
    }

    @Override
    default public List<VoxelShape> getEntityCollisions(@Nullable Entity $$0, AABB $$1) {
        return EntityGetter.super.getEntityCollisions($$0, $$1);
    }

    @Override
    default public boolean isUnobstructed(@Nullable Entity $$0, VoxelShape $$1) {
        return EntityGetter.super.isUnobstructed($$0, $$1);
    }

    @Override
    default public BlockPos getHeightmapPos(Heightmap.Types $$0, BlockPos $$1) {
        return LevelReader.super.getHeightmapPos($$0, $$1);
    }
}