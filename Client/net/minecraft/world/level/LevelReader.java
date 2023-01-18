/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Deprecated
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level;

import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;

public interface LevelReader
extends BlockAndTintGetter,
CollisionGetter,
BiomeManager.NoiseBiomeSource {
    @Nullable
    public ChunkAccess getChunk(int var1, int var2, ChunkStatus var3, boolean var4);

    @Deprecated
    public boolean hasChunk(int var1, int var2);

    public int getHeight(Heightmap.Types var1, int var2, int var3);

    public int getSkyDarken();

    public BiomeManager getBiomeManager();

    default public Holder<Biome> getBiome(BlockPos $$0) {
        return this.getBiomeManager().getBiome($$0);
    }

    default public Stream<BlockState> getBlockStatesIfLoaded(AABB $$0) {
        int $$6;
        int $$1 = Mth.floor($$0.minX);
        int $$2 = Mth.floor($$0.maxX);
        int $$3 = Mth.floor($$0.minY);
        int $$4 = Mth.floor($$0.maxY);
        int $$5 = Mth.floor($$0.minZ);
        if (this.hasChunksAt($$1, $$3, $$5, $$2, $$4, $$6 = Mth.floor($$0.maxZ))) {
            return this.getBlockStates($$0);
        }
        return Stream.empty();
    }

    @Override
    default public int getBlockTint(BlockPos $$0, ColorResolver $$1) {
        return $$1.getColor(this.getBiome($$0).value(), $$0.getX(), $$0.getZ());
    }

    @Override
    default public Holder<Biome> getNoiseBiome(int $$0, int $$1, int $$2) {
        ChunkAccess $$3 = this.getChunk(QuartPos.toSection($$0), QuartPos.toSection($$2), ChunkStatus.BIOMES, false);
        if ($$3 != null) {
            return $$3.getNoiseBiome($$0, $$1, $$2);
        }
        return this.getUncachedNoiseBiome($$0, $$1, $$2);
    }

    public Holder<Biome> getUncachedNoiseBiome(int var1, int var2, int var3);

    public boolean isClientSide();

    @Deprecated
    public int getSeaLevel();

    public DimensionType dimensionType();

    @Override
    default public int getMinBuildHeight() {
        return this.dimensionType().minY();
    }

    @Override
    default public int getHeight() {
        return this.dimensionType().height();
    }

    default public BlockPos getHeightmapPos(Heightmap.Types $$0, BlockPos $$1) {
        return new BlockPos($$1.getX(), this.getHeight($$0, $$1.getX(), $$1.getZ()), $$1.getZ());
    }

    default public boolean isEmptyBlock(BlockPos $$0) {
        return this.getBlockState($$0).isAir();
    }

    default public boolean canSeeSkyFromBelowWater(BlockPos $$0) {
        if ($$0.getY() >= this.getSeaLevel()) {
            return this.canSeeSky($$0);
        }
        Vec3i $$1 = new BlockPos($$0.getX(), this.getSeaLevel(), $$0.getZ());
        if (!this.canSeeSky((BlockPos)$$1)) {
            return false;
        }
        $$1 = ((BlockPos)$$1).below();
        while ($$1.getY() > $$0.getY()) {
            BlockState $$2 = this.getBlockState((BlockPos)$$1);
            if ($$2.getLightBlock(this, (BlockPos)$$1) > 0 && !$$2.getMaterial().isLiquid()) {
                return false;
            }
            $$1 = ((BlockPos)$$1).below();
        }
        return true;
    }

    default public float getPathfindingCostFromLightLevels(BlockPos $$0) {
        return this.getLightLevelDependentMagicValue($$0) - 0.5f;
    }

    @Deprecated
    default public float getLightLevelDependentMagicValue(BlockPos $$0) {
        float $$1 = (float)this.getMaxLocalRawBrightness($$0) / 15.0f;
        float $$2 = $$1 / (4.0f - 3.0f * $$1);
        return Mth.lerp(this.dimensionType().ambientLight(), $$2, 1.0f);
    }

    default public int getDirectSignal(BlockPos $$0, Direction $$1) {
        return this.getBlockState($$0).getDirectSignal(this, $$0, $$1);
    }

    default public ChunkAccess getChunk(BlockPos $$0) {
        return this.getChunk(SectionPos.blockToSectionCoord($$0.getX()), SectionPos.blockToSectionCoord($$0.getZ()));
    }

    default public ChunkAccess getChunk(int $$0, int $$1) {
        return this.getChunk($$0, $$1, ChunkStatus.FULL, true);
    }

    default public ChunkAccess getChunk(int $$0, int $$1, ChunkStatus $$2) {
        return this.getChunk($$0, $$1, $$2, true);
    }

    @Override
    @Nullable
    default public BlockGetter getChunkForCollisions(int $$0, int $$1) {
        return this.getChunk($$0, $$1, ChunkStatus.EMPTY, false);
    }

    default public boolean isWaterAt(BlockPos $$0) {
        return this.getFluidState($$0).is(FluidTags.WATER);
    }

    default public boolean containsAnyLiquid(AABB $$0) {
        int $$1 = Mth.floor($$0.minX);
        int $$2 = Mth.ceil($$0.maxX);
        int $$3 = Mth.floor($$0.minY);
        int $$4 = Mth.ceil($$0.maxY);
        int $$5 = Mth.floor($$0.minZ);
        int $$6 = Mth.ceil($$0.maxZ);
        BlockPos.MutableBlockPos $$7 = new BlockPos.MutableBlockPos();
        for (int $$8 = $$1; $$8 < $$2; ++$$8) {
            for (int $$9 = $$3; $$9 < $$4; ++$$9) {
                for (int $$10 = $$5; $$10 < $$6; ++$$10) {
                    BlockState $$11 = this.getBlockState($$7.set($$8, $$9, $$10));
                    if ($$11.getFluidState().isEmpty()) continue;
                    return true;
                }
            }
        }
        return false;
    }

    default public int getMaxLocalRawBrightness(BlockPos $$0) {
        return this.getMaxLocalRawBrightness($$0, this.getSkyDarken());
    }

    default public int getMaxLocalRawBrightness(BlockPos $$0, int $$1) {
        if ($$0.getX() < -30000000 || $$0.getZ() < -30000000 || $$0.getX() >= 30000000 || $$0.getZ() >= 30000000) {
            return 15;
        }
        return this.getRawBrightness($$0, $$1);
    }

    @Deprecated
    default public boolean hasChunkAt(int $$0, int $$1) {
        return this.hasChunk(SectionPos.blockToSectionCoord($$0), SectionPos.blockToSectionCoord($$1));
    }

    @Deprecated
    default public boolean hasChunkAt(BlockPos $$0) {
        return this.hasChunkAt($$0.getX(), $$0.getZ());
    }

    @Deprecated
    default public boolean hasChunksAt(BlockPos $$0, BlockPos $$1) {
        return this.hasChunksAt($$0.getX(), $$0.getY(), $$0.getZ(), $$1.getX(), $$1.getY(), $$1.getZ());
    }

    @Deprecated
    default public boolean hasChunksAt(int $$0, int $$1, int $$2, int $$3, int $$4, int $$5) {
        if ($$4 < this.getMinBuildHeight() || $$1 >= this.getMaxBuildHeight()) {
            return false;
        }
        return this.hasChunksAt($$0, $$2, $$3, $$5);
    }

    @Deprecated
    default public boolean hasChunksAt(int $$0, int $$1, int $$2, int $$3) {
        int $$4 = SectionPos.blockToSectionCoord($$0);
        int $$5 = SectionPos.blockToSectionCoord($$2);
        int $$6 = SectionPos.blockToSectionCoord($$1);
        int $$7 = SectionPos.blockToSectionCoord($$3);
        for (int $$8 = $$4; $$8 <= $$5; ++$$8) {
            for (int $$9 = $$6; $$9 <= $$7; ++$$9) {
                if (this.hasChunk($$8, $$9)) continue;
                return false;
            }
        }
        return true;
    }

    public RegistryAccess registryAccess();

    public FeatureFlagSet enabledFeatures();

    default public <T> HolderLookup<T> holderLookup(ResourceKey<? extends Registry<? extends T>> $$0) {
        Registry $$1 = this.registryAccess().registryOrThrow($$0);
        return $$1.asLookup().filterFeatures(this.enabledFeatures());
    }
}