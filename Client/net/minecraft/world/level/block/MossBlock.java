/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.CaveFeatures;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class MossBlock
extends Block
implements BonemealableBlock {
    public MossBlock(BlockBehaviour.Properties $$0) {
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
    public void performBonemeal(ServerLevel $$02, RandomSource $$1, BlockPos $$2, BlockState $$32) {
        $$02.registryAccess().registry(Registries.CONFIGURED_FEATURE).flatMap($$0 -> $$0.getHolder(CaveFeatures.MOSS_PATCH_BONEMEAL)).ifPresent($$3 -> ((ConfiguredFeature)((Object)((Object)$$3.value()))).place($$02, $$02.getChunkSource().getGenerator(), $$1, (BlockPos)$$2.above()));
    }
}