/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SculkBehaviour;
import net.minecraft.world.level.block.SculkShriekerBlock;
import net.minecraft.world.level.block.SculkSpreader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;

public class SculkBlock
extends DropExperienceBlock
implements SculkBehaviour {
    public SculkBlock(BlockBehaviour.Properties $$0) {
        super($$0, ConstantInt.of(1));
    }

    @Override
    public int attemptUseCharge(SculkSpreader.ChargeCursor $$0, LevelAccessor $$1, BlockPos $$2, RandomSource $$3, SculkSpreader $$4, boolean $$5) {
        int $$6 = $$0.getCharge();
        if ($$6 == 0 || $$3.nextInt($$4.chargeDecayRate()) != 0) {
            return $$6;
        }
        BlockPos $$7 = $$0.getPos();
        boolean $$8 = $$7.closerThan($$2, $$4.noGrowthRadius());
        if ($$8 || !SculkBlock.canPlaceGrowth($$1, $$7)) {
            if ($$3.nextInt($$4.additionalDecayRate()) != 0) {
                return $$6;
            }
            return $$6 - ($$8 ? 1 : SculkBlock.getDecayPenalty($$4, $$7, $$2, $$6));
        }
        int $$9 = $$4.growthSpawnCost();
        if ($$3.nextInt($$9) < $$6) {
            Vec3i $$10 = $$7.above();
            BlockState $$11 = this.getRandomGrowthState($$1, (BlockPos)$$10, $$3, $$4.isWorldGeneration());
            $$1.setBlock((BlockPos)$$10, $$11, 3);
            $$1.playSound(null, $$7, $$11.getSoundType().getPlaceSound(), SoundSource.BLOCKS, 1.0f, 1.0f);
        }
        return Math.max((int)0, (int)($$6 - $$9));
    }

    private static int getDecayPenalty(SculkSpreader $$0, BlockPos $$1, BlockPos $$2, int $$3) {
        int $$4 = $$0.noGrowthRadius();
        float $$5 = Mth.square((float)Math.sqrt((double)$$1.distSqr($$2)) - (float)$$4);
        int $$6 = Mth.square(24 - $$4);
        float $$7 = Math.min((float)1.0f, (float)($$5 / (float)$$6));
        return Math.max((int)1, (int)((int)((float)$$3 * $$7 * 0.5f)));
    }

    private BlockState getRandomGrowthState(LevelAccessor $$0, BlockPos $$1, RandomSource $$2, boolean $$3) {
        BlockState $$5;
        if ($$2.nextInt(11) == 0) {
            BlockState $$4 = (BlockState)Blocks.SCULK_SHRIEKER.defaultBlockState().setValue(SculkShriekerBlock.CAN_SUMMON, $$3);
        } else {
            $$5 = Blocks.SCULK_SENSOR.defaultBlockState();
        }
        if ($$5.hasProperty(BlockStateProperties.WATERLOGGED) && !$$0.getFluidState($$1).isEmpty()) {
            return (BlockState)$$5.setValue(BlockStateProperties.WATERLOGGED, true);
        }
        return $$5;
    }

    private static boolean canPlaceGrowth(LevelAccessor $$0, BlockPos $$1) {
        BlockState $$2 = $$0.getBlockState((BlockPos)$$1.above());
        if (!($$2.isAir() || $$2.is(Blocks.WATER) && $$2.getFluidState().is(Fluids.WATER))) {
            return false;
        }
        int $$3 = 0;
        for (BlockPos $$4 : BlockPos.betweenClosed($$1.offset(-4, 0, -4), $$1.offset(4, 2, 4))) {
            BlockState $$5 = $$0.getBlockState($$4);
            if ($$5.is(Blocks.SCULK_SENSOR) || $$5.is(Blocks.SCULK_SHRIEKER)) {
                ++$$3;
            }
            if ($$3 <= 2) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean canChangeBlockStateOnSpread() {
        return false;
    }
}