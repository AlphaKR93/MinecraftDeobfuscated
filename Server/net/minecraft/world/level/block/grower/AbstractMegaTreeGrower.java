/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block.grower;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public abstract class AbstractMegaTreeGrower
extends AbstractTreeGrower {
    @Override
    public boolean growTree(ServerLevel $$0, ChunkGenerator $$1, BlockPos $$2, BlockState $$3, RandomSource $$4) {
        for (int $$5 = 0; $$5 >= -1; --$$5) {
            for (int $$6 = 0; $$6 >= -1; --$$6) {
                if (!AbstractMegaTreeGrower.isTwoByTwoSapling($$3, $$0, $$2, $$5, $$6)) continue;
                return this.placeMega($$0, $$1, $$2, $$3, $$4, $$5, $$6);
            }
        }
        return super.growTree($$0, $$1, $$2, $$3, $$4);
    }

    @Nullable
    protected abstract ResourceKey<ConfiguredFeature<?, ?>> getConfiguredMegaFeature(RandomSource var1);

    public boolean placeMega(ServerLevel $$0, ChunkGenerator $$1, BlockPos $$2, BlockState $$3, RandomSource $$4, int $$5, int $$6) {
        ResourceKey<ConfiguredFeature<?, ?>> $$7 = this.getConfiguredMegaFeature($$4);
        if ($$7 == null) {
            return false;
        }
        Holder $$8 = (Holder)$$0.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE).getHolder($$7).orElse(null);
        if ($$8 == null) {
            return false;
        }
        ConfiguredFeature $$9 = (ConfiguredFeature)((Object)$$8.value());
        BlockState $$10 = Blocks.AIR.defaultBlockState();
        $$0.setBlock($$2.offset($$5, 0, $$6), $$10, 4);
        $$0.setBlock($$2.offset($$5 + 1, 0, $$6), $$10, 4);
        $$0.setBlock($$2.offset($$5, 0, $$6 + 1), $$10, 4);
        $$0.setBlock($$2.offset($$5 + 1, 0, $$6 + 1), $$10, 4);
        if ($$9.place($$0, $$1, $$4, $$2.offset($$5, 0, $$6))) {
            return true;
        }
        $$0.setBlock($$2.offset($$5, 0, $$6), $$3, 4);
        $$0.setBlock($$2.offset($$5 + 1, 0, $$6), $$3, 4);
        $$0.setBlock($$2.offset($$5, 0, $$6 + 1), $$3, 4);
        $$0.setBlock($$2.offset($$5 + 1, 0, $$6 + 1), $$3, 4);
        return false;
    }

    public static boolean isTwoByTwoSapling(BlockState $$0, BlockGetter $$1, BlockPos $$2, int $$3, int $$4) {
        Block $$5 = $$0.getBlock();
        return $$1.getBlockState($$2.offset($$3, 0, $$4)).is($$5) && $$1.getBlockState($$2.offset($$3 + 1, 0, $$4)).is($$5) && $$1.getBlockState($$2.offset($$3, 0, $$4 + 1)).is($$5) && $$1.getBlockState($$2.offset($$3 + 1, 0, $$4 + 1)).is($$5);
    }
}