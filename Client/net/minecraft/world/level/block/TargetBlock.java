/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.level.block;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class TargetBlock
extends Block {
    private static final IntegerProperty OUTPUT_POWER = BlockStateProperties.POWER;
    private static final int ACTIVATION_TICKS_ARROWS = 20;
    private static final int ACTIVATION_TICKS_OTHER = 8;

    public TargetBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(OUTPUT_POWER, 0));
    }

    @Override
    public void onProjectileHit(Level $$0, BlockState $$1, BlockHitResult $$2, Projectile $$3) {
        int $$4 = TargetBlock.updateRedstoneOutput($$0, $$1, $$2, $$3);
        Entity $$5 = $$3.getOwner();
        if ($$5 instanceof ServerPlayer) {
            ServerPlayer $$6 = (ServerPlayer)$$5;
            $$6.awardStat(Stats.TARGET_HIT);
            CriteriaTriggers.TARGET_BLOCK_HIT.trigger($$6, $$3, $$2.getLocation(), $$4);
        }
    }

    private static int updateRedstoneOutput(LevelAccessor $$0, BlockState $$1, BlockHitResult $$2, Entity $$3) {
        int $$5;
        int $$4 = TargetBlock.getRedstoneStrength($$2, $$2.getLocation());
        int n = $$5 = $$3 instanceof AbstractArrow ? 20 : 8;
        if (!$$0.getBlockTicks().hasScheduledTick($$2.getBlockPos(), $$1.getBlock())) {
            TargetBlock.setOutputPower($$0, $$1, $$4, $$2.getBlockPos(), $$5);
        }
        return $$4;
    }

    private static int getRedstoneStrength(BlockHitResult $$0, Vec3 $$1) {
        double $$9;
        Direction $$2 = $$0.getDirection();
        double $$3 = Math.abs((double)(Mth.frac($$1.x) - 0.5));
        double $$4 = Math.abs((double)(Mth.frac($$1.y) - 0.5));
        double $$5 = Math.abs((double)(Mth.frac($$1.z) - 0.5));
        Direction.Axis $$6 = $$2.getAxis();
        if ($$6 == Direction.Axis.Y) {
            double $$7 = Math.max((double)$$3, (double)$$5);
        } else if ($$6 == Direction.Axis.Z) {
            double $$8 = Math.max((double)$$3, (double)$$4);
        } else {
            $$9 = Math.max((double)$$4, (double)$$5);
        }
        return Math.max((int)1, (int)Mth.ceil(15.0 * Mth.clamp((0.5 - $$9) / 0.5, 0.0, 1.0)));
    }

    private static void setOutputPower(LevelAccessor $$0, BlockState $$1, int $$2, BlockPos $$3, int $$4) {
        $$0.setBlock($$3, (BlockState)$$1.setValue(OUTPUT_POWER, $$2), 3);
        $$0.scheduleTick($$3, $$1.getBlock(), $$4);
    }

    @Override
    public void tick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        if ($$0.getValue(OUTPUT_POWER) != 0) {
            $$1.setBlock($$2, (BlockState)$$0.setValue(OUTPUT_POWER, 0), 3);
        }
    }

    @Override
    public int getSignal(BlockState $$0, BlockGetter $$1, BlockPos $$2, Direction $$3) {
        return $$0.getValue(OUTPUT_POWER);
    }

    @Override
    public boolean isSignalSource(BlockState $$0) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(OUTPUT_POWER);
    }

    @Override
    public void onPlace(BlockState $$0, Level $$1, BlockPos $$2, BlockState $$3, boolean $$4) {
        if ($$1.isClientSide() || $$0.is($$3.getBlock())) {
            return;
        }
        if ($$0.getValue(OUTPUT_POWER) > 0 && !$$1.getBlockTicks().hasScheduledTick($$2, this)) {
            $$1.setBlock($$2, (BlockState)$$0.setValue(OUTPUT_POWER, 0), 18);
        }
    }
}