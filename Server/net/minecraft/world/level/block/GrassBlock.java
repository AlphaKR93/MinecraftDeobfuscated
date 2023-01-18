/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.Optional
 */
package net.minecraft.world.level.block;

import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.SpreadingSnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class GrassBlock
extends SpreadingSnowyDirtBlock
implements BonemealableBlock {
    public GrassBlock(BlockBehaviour.Properties $$0) {
        super($$0);
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
        Vec3i $$4 = $$2.above();
        BlockState $$5 = Blocks.GRASS.defaultBlockState();
        Optional<Holder.Reference<PlacedFeature>> $$6 = $$0.registryAccess().registryOrThrow(Registries.PLACED_FEATURE).getHolder(VegetationPlacements.GRASS_BONEMEAL);
        block0: for (int $$7 = 0; $$7 < 128; ++$$7) {
            Holder $$13;
            Vec3i $$8 = $$4;
            for (int $$9 = 0; $$9 < $$7 / 16; ++$$9) {
                if (!$$0.getBlockState((BlockPos)((BlockPos)($$8 = ((BlockPos)$$8).offset($$1.nextInt(3) - 1, ($$1.nextInt(3) - 1) * $$1.nextInt(3) / 2, $$1.nextInt(3) - 1))).below()).is(this) || $$0.getBlockState((BlockPos)$$8).isCollisionShapeFullBlock($$0, (BlockPos)$$8)) continue block0;
            }
            BlockState $$10 = $$0.getBlockState((BlockPos)$$8);
            if ($$10.is($$5.getBlock()) && $$1.nextInt(10) == 0) {
                ((BonemealableBlock)((Object)$$5.getBlock())).performBonemeal($$0, $$1, (BlockPos)$$8, $$10);
            }
            if (!$$10.isAir()) continue;
            if ($$1.nextInt(8) == 0) {
                List<ConfiguredFeature<?, ?>> $$11 = ((Biome)$$0.getBiome((BlockPos)$$8).value()).getGenerationSettings().getFlowerFeatures();
                if ($$11.isEmpty()) continue;
                Holder<PlacedFeature> $$12 = ((RandomPatchConfiguration)((ConfiguredFeature)((Object)$$11.get(0))).config()).feature();
            } else {
                if (!$$6.isPresent()) continue;
                $$13 = (Holder)$$6.get();
            }
            ((PlacedFeature)((Object)$$13.value())).place($$0, $$0.getChunkSource().getGenerator(), $$1, (BlockPos)$$8);
        }
    }
}