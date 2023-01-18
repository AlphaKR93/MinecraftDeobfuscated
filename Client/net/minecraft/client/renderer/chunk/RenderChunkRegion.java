/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.client.renderer.chunk;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;

public class RenderChunkRegion
implements BlockAndTintGetter {
    private final int centerX;
    private final int centerZ;
    protected final RenderChunk[][] chunks;
    protected final Level level;

    RenderChunkRegion(Level $$0, int $$1, int $$2, RenderChunk[][] $$3) {
        this.level = $$0;
        this.centerX = $$1;
        this.centerZ = $$2;
        this.chunks = $$3;
    }

    @Override
    public BlockState getBlockState(BlockPos $$0) {
        int $$1 = SectionPos.blockToSectionCoord($$0.getX()) - this.centerX;
        int $$2 = SectionPos.blockToSectionCoord($$0.getZ()) - this.centerZ;
        return this.chunks[$$1][$$2].getBlockState($$0);
    }

    @Override
    public FluidState getFluidState(BlockPos $$0) {
        int $$1 = SectionPos.blockToSectionCoord($$0.getX()) - this.centerX;
        int $$2 = SectionPos.blockToSectionCoord($$0.getZ()) - this.centerZ;
        return this.chunks[$$1][$$2].getBlockState($$0).getFluidState();
    }

    @Override
    public float getShade(Direction $$0, boolean $$1) {
        return this.level.getShade($$0, $$1);
    }

    @Override
    public LevelLightEngine getLightEngine() {
        return this.level.getLightEngine();
    }

    @Override
    @Nullable
    public BlockEntity getBlockEntity(BlockPos $$0) {
        int $$1 = SectionPos.blockToSectionCoord($$0.getX()) - this.centerX;
        int $$2 = SectionPos.blockToSectionCoord($$0.getZ()) - this.centerZ;
        return this.chunks[$$1][$$2].getBlockEntity($$0);
    }

    @Override
    public int getBlockTint(BlockPos $$0, ColorResolver $$1) {
        return this.level.getBlockTint($$0, $$1);
    }

    @Override
    public int getMinBuildHeight() {
        return this.level.getMinBuildHeight();
    }

    @Override
    public int getHeight() {
        return this.level.getHeight();
    }
}