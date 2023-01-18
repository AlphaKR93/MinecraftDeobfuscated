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
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SculkBehaviour;
import net.minecraft.world.level.block.SculkShriekerBlock;
import net.minecraft.world.level.block.SculkSpreader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.SculkPatchConfiguration;

public class SculkPatchFeature
extends Feature<SculkPatchConfiguration> {
    public SculkPatchFeature(Codec<SculkPatchConfiguration> $$0) {
        super($$0);
    }

    @Override
    public boolean place(FeaturePlaceContext<SculkPatchConfiguration> $$0) {
        BlockPos $$2;
        WorldGenLevel $$1 = $$0.level();
        if (!this.canSpreadFrom($$1, $$2 = $$0.origin())) {
            return false;
        }
        SculkPatchConfiguration $$3 = $$0.config();
        RandomSource $$4 = $$0.random();
        SculkSpreader $$5 = SculkSpreader.createWorldGenSpreader();
        int $$6 = $$3.spreadRounds() + $$3.growthRounds();
        for (int $$7 = 0; $$7 < $$6; ++$$7) {
            for (int $$8 = 0; $$8 < $$3.chargeCount(); ++$$8) {
                $$5.addCursors($$2, $$3.amountPerCharge());
            }
            boolean $$9 = $$7 < $$3.spreadRounds();
            for (int $$10 = 0; $$10 < $$3.spreadAttempts(); ++$$10) {
                $$5.updateCursors($$1, $$2, $$4, $$9);
            }
            $$5.clear();
        }
        Vec3i $$11 = $$2.below();
        if ($$4.nextFloat() <= $$3.catalystChance() && $$1.getBlockState((BlockPos)$$11).isCollisionShapeFullBlock($$1, (BlockPos)$$11)) {
            $$1.setBlock($$2, Blocks.SCULK_CATALYST.defaultBlockState(), 3);
        }
        int $$12 = $$3.extraRareGrowths().sample($$4);
        for (int $$13 = 0; $$13 < $$12; ++$$13) {
            BlockPos $$14 = $$2.offset($$4.nextInt(5) - 2, 0, $$4.nextInt(5) - 2);
            if (!$$1.getBlockState($$14).isAir() || !$$1.getBlockState((BlockPos)$$14.below()).isFaceSturdy($$1, (BlockPos)$$14.below(), Direction.UP)) continue;
            $$1.setBlock($$14, (BlockState)Blocks.SCULK_SHRIEKER.defaultBlockState().setValue(SculkShriekerBlock.CAN_SUMMON, true), 3);
        }
        return true;
    }

    private boolean canSpreadFrom(LevelAccessor $$0, BlockPos $$12) {
        block5: {
            block4: {
                BlockState $$2 = $$0.getBlockState($$12);
                if ($$2.getBlock() instanceof SculkBehaviour) {
                    return true;
                }
                if ($$2.isAir()) break block4;
                if (!$$2.is(Blocks.WATER) || !$$2.getFluidState().isSource()) break block5;
            }
            return Direction.stream().map($$12::relative).anyMatch($$1 -> $$0.getBlockState((BlockPos)$$1).isCollisionShapeFullBlock($$0, (BlockPos)$$1));
        }
        return false;
    }
}