/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.NetherFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.lighting.LayerLightEngine;

public class NyliumBlock
extends Block
implements BonemealableBlock {
    protected NyliumBlock(BlockBehaviour.Properties $$0) {
        super($$0);
    }

    private static boolean canBeNylium(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        Vec3i $$3 = $$2.above();
        BlockState $$4 = $$1.getBlockState((BlockPos)$$3);
        int $$5 = LayerLightEngine.getLightBlockInto($$1, $$0, $$2, $$4, (BlockPos)$$3, Direction.UP, $$4.getLightBlock($$1, (BlockPos)$$3));
        return $$5 < $$1.getMaxLightLevel();
    }

    @Override
    public void randomTick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        if (!NyliumBlock.canBeNylium($$0, $$1, $$2)) {
            $$1.setBlockAndUpdate($$2, Blocks.NETHERRACK.defaultBlockState());
        }
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader $$0, BlockPos $$1, BlockState $$2, boolean $$3) {
        return $$0.getBlockState((BlockPos)$$1.above()).isAir();
    }

    @Override
    public boolean isBonemealSuccess(Level $$0, RandomSource $$1, BlockPos $$2, BlockState $$3) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel $$0, RandomSource $$1, BlockPos $$2, BlockState $$3) {
        BlockState $$4 = $$0.getBlockState($$2);
        Vec3i $$5 = $$2.above();
        ChunkGenerator $$6 = $$0.getChunkSource().getGenerator();
        Registry<ConfiguredFeature<?, ?>> $$7 = $$0.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE);
        if ($$4.is(Blocks.CRIMSON_NYLIUM)) {
            this.place($$7, NetherFeatures.CRIMSON_FOREST_VEGETATION_BONEMEAL, $$0, $$6, $$1, (BlockPos)$$5);
        } else if ($$4.is(Blocks.WARPED_NYLIUM)) {
            this.place($$7, NetherFeatures.WARPED_FOREST_VEGETATION_BONEMEAL, $$0, $$6, $$1, (BlockPos)$$5);
            this.place($$7, NetherFeatures.NETHER_SPROUTS_BONEMEAL, $$0, $$6, $$1, (BlockPos)$$5);
            if ($$1.nextInt(8) == 0) {
                this.place($$7, NetherFeatures.TWISTING_VINES_BONEMEAL, $$0, $$6, $$1, (BlockPos)$$5);
            }
        }
    }

    private void place(Registry<ConfiguredFeature<?, ?>> $$0, ResourceKey<ConfiguredFeature<?, ?>> $$1, ServerLevel $$2, ChunkGenerator $$3, RandomSource $$42, BlockPos $$5) {
        $$0.getHolder($$1).ifPresent($$4 -> ((ConfiguredFeature)((Object)((Object)$$4.value()))).place($$2, $$3, $$42, $$5));
    }
}