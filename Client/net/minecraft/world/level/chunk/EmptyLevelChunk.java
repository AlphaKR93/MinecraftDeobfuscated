/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.chunk;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class EmptyLevelChunk
extends LevelChunk {
    private final Holder<Biome> biome;

    public EmptyLevelChunk(Level $$0, ChunkPos $$1, Holder<Biome> $$2) {
        super($$0, $$1);
        this.biome = $$2;
    }

    @Override
    public BlockState getBlockState(BlockPos $$0) {
        return Blocks.VOID_AIR.defaultBlockState();
    }

    @Override
    @Nullable
    public BlockState setBlockState(BlockPos $$0, BlockState $$1, boolean $$2) {
        return null;
    }

    @Override
    public FluidState getFluidState(BlockPos $$0) {
        return Fluids.EMPTY.defaultFluidState();
    }

    @Override
    public int getLightEmission(BlockPos $$0) {
        return 0;
    }

    @Override
    @Nullable
    public BlockEntity getBlockEntity(BlockPos $$0, LevelChunk.EntityCreationType $$1) {
        return null;
    }

    @Override
    public void addAndRegisterBlockEntity(BlockEntity $$0) {
    }

    @Override
    public void setBlockEntity(BlockEntity $$0) {
    }

    @Override
    public void removeBlockEntity(BlockPos $$0) {
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public boolean isYSpaceEmpty(int $$0, int $$1) {
        return true;
    }

    @Override
    public ChunkHolder.FullChunkStatus getFullStatus() {
        return ChunkHolder.FullChunkStatus.BORDER;
    }

    @Override
    public Holder<Biome> getNoiseBiome(int $$0, int $$1, int $$2) {
        return this.biome;
    }
}