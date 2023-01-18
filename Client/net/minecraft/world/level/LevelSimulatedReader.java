/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.util.Optional
 *  java.util.function.Predicate
 */
package net.minecraft.world.level;

import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;

public interface LevelSimulatedReader {
    public boolean isStateAtPosition(BlockPos var1, Predicate<BlockState> var2);

    public boolean isFluidAtPosition(BlockPos var1, Predicate<FluidState> var2);

    public <T extends BlockEntity> Optional<T> getBlockEntity(BlockPos var1, BlockEntityType<T> var2);

    public BlockPos getHeightmapPos(Heightmap.Types var1, BlockPos var2);
}