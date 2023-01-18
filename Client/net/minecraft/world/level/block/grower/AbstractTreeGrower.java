/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block.grower;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public abstract class AbstractTreeGrower {
    @Nullable
    protected abstract ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource var1, boolean var2);

    public boolean growTree(ServerLevel $$0, ChunkGenerator $$1, BlockPos $$2, BlockState $$3, RandomSource $$4) {
        ResourceKey<ConfiguredFeature<?, ?>> $$5 = this.getConfiguredFeature($$4, this.hasFlowers($$0, $$2));
        if ($$5 == null) {
            return false;
        }
        Holder $$6 = (Holder)$$0.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE).getHolder($$5).orElse(null);
        if ($$6 == null) {
            return false;
        }
        ConfiguredFeature $$7 = (ConfiguredFeature)((Object)$$6.value());
        BlockState $$8 = $$0.getFluidState($$2).createLegacyBlock();
        $$0.setBlock($$2, $$8, 4);
        if ($$7.place($$0, $$1, $$4, $$2)) {
            if ($$0.getBlockState($$2) == $$8) {
                $$0.sendBlockUpdated($$2, $$3, $$8, 2);
            }
            return true;
        }
        $$0.setBlock($$2, $$3, 4);
        return false;
    }

    private boolean hasFlowers(LevelAccessor $$0, BlockPos $$1) {
        for (BlockPos $$2 : BlockPos.MutableBlockPos.betweenClosed((BlockPos)((BlockPos)((BlockPos)$$1.below()).north(2)).west(2), (BlockPos)((BlockPos)((BlockPos)$$1.above()).south(2)).east(2))) {
            if (!$$0.getBlockState($$2).is(BlockTags.FLOWERS)) continue;
            return true;
        }
        return false;
    }
}