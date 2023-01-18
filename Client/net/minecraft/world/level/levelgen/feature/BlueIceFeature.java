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
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.material.Material;

public class BlueIceFeature
extends Feature<NoneFeatureConfiguration> {
    public BlueIceFeature(Codec<NoneFeatureConfiguration> $$0) {
        super($$0);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> $$0) {
        BlockPos $$1 = $$0.origin();
        WorldGenLevel $$2 = $$0.level();
        RandomSource $$3 = $$0.random();
        if ($$1.getY() > $$2.getSeaLevel() - 1) {
            return false;
        }
        if (!$$2.getBlockState($$1).is(Blocks.WATER) && !$$2.getBlockState((BlockPos)$$1.below()).is(Blocks.WATER)) {
            return false;
        }
        boolean $$4 = false;
        for (Direction $$5 : Direction.values()) {
            if ($$5 == Direction.DOWN || !$$2.getBlockState((BlockPos)$$1.relative($$5)).is(Blocks.PACKED_ICE)) continue;
            $$4 = true;
            break;
        }
        if (!$$4) {
            return false;
        }
        $$2.setBlock($$1, Blocks.BLUE_ICE.defaultBlockState(), 2);
        block1: for (int $$6 = 0; $$6 < 200; ++$$6) {
            BlockPos $$9;
            BlockState $$10;
            int $$7 = $$3.nextInt(5) - $$3.nextInt(6);
            int $$8 = 3;
            if ($$7 < 2) {
                $$8 += $$7 / 2;
            }
            if ($$8 < 1 || ($$10 = $$2.getBlockState($$9 = $$1.offset($$3.nextInt($$8) - $$3.nextInt($$8), $$7, $$3.nextInt($$8) - $$3.nextInt($$8)))).getMaterial() != Material.AIR && !$$10.is(Blocks.WATER) && !$$10.is(Blocks.PACKED_ICE) && !$$10.is(Blocks.ICE)) continue;
            for (Direction $$11 : Direction.values()) {
                BlockState $$12 = $$2.getBlockState((BlockPos)$$9.relative($$11));
                if (!$$12.is(Blocks.BLUE_ICE)) continue;
                $$2.setBlock($$9, Blocks.BLUE_ICE.defaultBlockState(), 2);
                continue block1;
            }
        }
        return true;
    }
}