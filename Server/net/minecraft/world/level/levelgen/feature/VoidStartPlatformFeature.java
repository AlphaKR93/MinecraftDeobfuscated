/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class VoidStartPlatformFeature
extends Feature<NoneFeatureConfiguration> {
    private static final BlockPos PLATFORM_OFFSET = new BlockPos(8, 3, 8);
    private static final ChunkPos PLATFORM_ORIGIN_CHUNK = new ChunkPos(PLATFORM_OFFSET);
    private static final int PLATFORM_RADIUS = 16;
    private static final int PLATFORM_RADIUS_CHUNKS = 1;

    public VoidStartPlatformFeature(Codec<NoneFeatureConfiguration> $$0) {
        super($$0);
    }

    private static int checkerboardDistance(int $$0, int $$1, int $$2, int $$3) {
        return Math.max((int)Math.abs((int)($$0 - $$2)), (int)Math.abs((int)($$1 - $$3)));
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> $$0) {
        WorldGenLevel $$1 = $$0.level();
        ChunkPos $$2 = new ChunkPos($$0.origin());
        if (VoidStartPlatformFeature.checkerboardDistance($$2.x, $$2.z, VoidStartPlatformFeature.PLATFORM_ORIGIN_CHUNK.x, VoidStartPlatformFeature.PLATFORM_ORIGIN_CHUNK.z) > 1) {
            return true;
        }
        BlockPos $$3 = PLATFORM_OFFSET.atY($$0.origin().getY() + PLATFORM_OFFSET.getY());
        BlockPos.MutableBlockPos $$4 = new BlockPos.MutableBlockPos();
        for (int $$5 = $$2.getMinBlockZ(); $$5 <= $$2.getMaxBlockZ(); ++$$5) {
            for (int $$6 = $$2.getMinBlockX(); $$6 <= $$2.getMaxBlockX(); ++$$6) {
                if (VoidStartPlatformFeature.checkerboardDistance($$3.getX(), $$3.getZ(), $$6, $$5) > 16) continue;
                $$4.set($$6, $$3.getY(), $$5);
                if ($$4.equals($$3)) {
                    $$1.setBlock($$4, Blocks.COBBLESTONE.defaultBlockState(), 2);
                    continue;
                }
                $$1.setBlock($$4, Blocks.STONE.defaultBlockState(), 2);
            }
        }
        return true;
    }
}