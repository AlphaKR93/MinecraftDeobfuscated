/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class DesertWellFeature
extends Feature<NoneFeatureConfiguration> {
    private static final BlockStatePredicate IS_SAND = BlockStatePredicate.forBlock(Blocks.SAND);
    private final BlockState sandSlab = Blocks.SANDSTONE_SLAB.defaultBlockState();
    private final BlockState sandstone = Blocks.SANDSTONE.defaultBlockState();
    private final BlockState water = Blocks.WATER.defaultBlockState();

    public DesertWellFeature(Codec<NoneFeatureConfiguration> $$0) {
        super($$0);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> $$0) {
        WorldGenLevel $$1 = $$0.level();
        Vec3i $$2 = $$0.origin();
        $$2 = ((BlockPos)$$2).above();
        while ($$1.isEmptyBlock((BlockPos)$$2) && $$2.getY() > $$1.getMinBuildHeight() + 2) {
            $$2 = ((BlockPos)$$2).below();
        }
        if (!IS_SAND.test($$1.getBlockState((BlockPos)$$2))) {
            return false;
        }
        for (int $$3 = -2; $$3 <= 2; ++$$3) {
            for (int $$4 = -2; $$4 <= 2; ++$$4) {
                if (!$$1.isEmptyBlock(((BlockPos)$$2).offset($$3, -1, $$4)) || !$$1.isEmptyBlock(((BlockPos)$$2).offset($$3, -2, $$4))) continue;
                return false;
            }
        }
        for (int $$5 = -1; $$5 <= 0; ++$$5) {
            for (int $$6 = -2; $$6 <= 2; ++$$6) {
                for (int $$7 = -2; $$7 <= 2; ++$$7) {
                    $$1.setBlock(((BlockPos)$$2).offset($$6, $$5, $$7), this.sandstone, 2);
                }
            }
        }
        $$1.setBlock((BlockPos)$$2, this.water, 2);
        for (Direction $$8 : Direction.Plane.HORIZONTAL) {
            $$1.setBlock((BlockPos)((BlockPos)$$2).relative($$8), this.water, 2);
        }
        for (int $$9 = -2; $$9 <= 2; ++$$9) {
            for (int $$10 = -2; $$10 <= 2; ++$$10) {
                if ($$9 != -2 && $$9 != 2 && $$10 != -2 && $$10 != 2) continue;
                $$1.setBlock(((BlockPos)$$2).offset($$9, 1, $$10), this.sandstone, 2);
            }
        }
        $$1.setBlock(((BlockPos)$$2).offset(2, 1, 0), this.sandSlab, 2);
        $$1.setBlock(((BlockPos)$$2).offset(-2, 1, 0), this.sandSlab, 2);
        $$1.setBlock(((BlockPos)$$2).offset(0, 1, 2), this.sandSlab, 2);
        $$1.setBlock(((BlockPos)$$2).offset(0, 1, -2), this.sandSlab, 2);
        for (int $$11 = -1; $$11 <= 1; ++$$11) {
            for (int $$12 = -1; $$12 <= 1; ++$$12) {
                if ($$11 == 0 && $$12 == 0) {
                    $$1.setBlock(((BlockPos)$$2).offset($$11, 4, $$12), this.sandstone, 2);
                    continue;
                }
                $$1.setBlock(((BlockPos)$$2).offset($$11, 4, $$12), this.sandSlab, 2);
            }
        }
        for (int $$13 = 1; $$13 <= 3; ++$$13) {
            $$1.setBlock(((BlockPos)$$2).offset(-1, $$13, -1), this.sandstone, 2);
            $$1.setBlock(((BlockPos)$$2).offset(-1, $$13, 1), this.sandstone, 2);
            $$1.setBlock(((BlockPos)$$2).offset(1, $$13, -1), this.sandstone, 2);
            $$1.setBlock(((BlockPos)$$2).offset(1, $$13, 1), this.sandstone, 2);
        }
        return true;
    }
}