/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Collection
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block;

import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.SculkSpreader;
import net.minecraft.world.level.block.SculkVeinBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

public interface SculkBehaviour {
    public static final SculkBehaviour DEFAULT = new SculkBehaviour(){

        @Override
        public boolean attemptSpreadVein(LevelAccessor $$0, BlockPos $$1, BlockState $$2, @Nullable Collection<Direction> $$3, boolean $$4) {
            if ($$3 == null) {
                return ((SculkVeinBlock)Blocks.SCULK_VEIN).getSameSpaceSpreader().spreadAll($$0.getBlockState($$1), $$0, $$1, $$4) > 0L;
            }
            if (!$$3.isEmpty()) {
                if ($$2.isAir() || $$2.getFluidState().is(Fluids.WATER)) {
                    return SculkVeinBlock.regrow($$0, $$1, $$2, $$3);
                }
                return false;
            }
            return SculkBehaviour.super.attemptSpreadVein($$0, $$1, $$2, $$3, $$4);
        }

        @Override
        public int attemptUseCharge(SculkSpreader.ChargeCursor $$0, LevelAccessor $$1, BlockPos $$2, RandomSource $$3, SculkSpreader $$4, boolean $$5) {
            return $$0.getDecayDelay() > 0 ? $$0.getCharge() : 0;
        }

        @Override
        public int updateDecayDelay(int $$0) {
            return Math.max((int)($$0 - 1), (int)0);
        }
    };

    default public byte getSculkSpreadDelay() {
        return 1;
    }

    default public void onDischarged(LevelAccessor $$0, BlockState $$1, BlockPos $$2, RandomSource $$3) {
    }

    default public boolean depositCharge(LevelAccessor $$0, BlockPos $$1, RandomSource $$2) {
        return false;
    }

    default public boolean attemptSpreadVein(LevelAccessor $$0, BlockPos $$1, BlockState $$2, @Nullable Collection<Direction> $$3, boolean $$4) {
        return ((MultifaceBlock)Blocks.SCULK_VEIN).getSpreader().spreadAll($$2, $$0, $$1, $$4) > 0L;
    }

    default public boolean canChangeBlockStateOnSpread() {
        return true;
    }

    default public int updateDecayDelay(int $$0) {
        return 1;
    }

    public int attemptUseCharge(SculkSpreader.ChargeCursor var1, LevelAccessor var2, BlockPos var3, RandomSource var4, SculkSpreader var5, boolean var6);
}