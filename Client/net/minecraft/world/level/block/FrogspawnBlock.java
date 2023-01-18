/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.level.block;

import com.google.common.annotations.VisibleForTesting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FrogspawnBlock
extends Block {
    private static final int MIN_TADPOLES_SPAWN = 2;
    private static final int MAX_TADPOLES_SPAWN = 5;
    private static final int DEFAULT_MIN_HATCH_TICK_DELAY = 3600;
    private static final int DEFAULT_MAX_HATCH_TICK_DELAY = 12000;
    protected static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 1.5, 16.0);
    private static int minHatchTickDelay = 3600;
    private static int maxHatchTickDelay = 12000;

    public FrogspawnBlock(BlockBehaviour.Properties $$0) {
        super($$0);
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return SHAPE;
    }

    @Override
    public boolean canSurvive(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        return FrogspawnBlock.mayPlaceOn($$1, (BlockPos)$$2.below());
    }

    @Override
    public void onPlace(BlockState $$0, Level $$1, BlockPos $$2, BlockState $$3, boolean $$4) {
        $$1.scheduleTick($$2, this, FrogspawnBlock.getFrogspawnHatchDelay($$1.getRandom()));
    }

    private static int getFrogspawnHatchDelay(RandomSource $$0) {
        return $$0.nextInt(minHatchTickDelay, maxHatchTickDelay);
    }

    @Override
    public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
        if (!this.canSurvive($$0, $$3, $$4)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    public void tick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        if (!this.canSurvive($$0, $$1, $$2)) {
            this.destroyBlock($$1, $$2);
            return;
        }
        this.hatchFrogspawn($$1, $$2, $$3);
    }

    @Override
    public void entityInside(BlockState $$0, Level $$1, BlockPos $$2, Entity $$3) {
        if ($$3.getType().equals(EntityType.FALLING_BLOCK)) {
            this.destroyBlock($$1, $$2);
        }
    }

    private static boolean mayPlaceOn(BlockGetter $$0, BlockPos $$1) {
        FluidState $$2 = $$0.getFluidState($$1);
        FluidState $$3 = $$0.getFluidState((BlockPos)$$1.above());
        return $$2.getType() == Fluids.WATER && $$3.getType() == Fluids.EMPTY;
    }

    private void hatchFrogspawn(ServerLevel $$0, BlockPos $$1, RandomSource $$2) {
        this.destroyBlock($$0, $$1);
        $$0.playSound(null, $$1, SoundEvents.FROGSPAWN_HATCH, SoundSource.BLOCKS, 1.0f, 1.0f);
        this.spawnTadpoles($$0, $$1, $$2);
    }

    private void destroyBlock(Level $$0, BlockPos $$1) {
        $$0.destroyBlock($$1, false);
    }

    private void spawnTadpoles(ServerLevel $$0, BlockPos $$1, RandomSource $$2) {
        int $$3 = $$2.nextInt(2, 6);
        for (int $$4 = 1; $$4 <= $$3; ++$$4) {
            Tadpole $$5 = EntityType.TADPOLE.create($$0);
            if ($$5 == null) continue;
            double $$6 = (double)$$1.getX() + this.getRandomTadpolePositionOffset($$2);
            double $$7 = (double)$$1.getZ() + this.getRandomTadpolePositionOffset($$2);
            int $$8 = $$2.nextInt(1, 361);
            $$5.moveTo($$6, (double)$$1.getY() - 0.5, $$7, $$8, 0.0f);
            $$5.setPersistenceRequired();
            $$0.addFreshEntity($$5);
        }
    }

    private double getRandomTadpolePositionOffset(RandomSource $$0) {
        double $$1 = Tadpole.HITBOX_WIDTH / 2.0f;
        return Mth.clamp($$0.nextDouble(), $$1, 1.0 - $$1);
    }

    @VisibleForTesting
    public static void setHatchDelay(int $$0, int $$1) {
        minHatchTickDelay = $$0;
        maxHatchTickDelay = $$1;
    }

    @VisibleForTesting
    public static void setDefaultHatchDelay() {
        minHatchTickDelay = 3600;
        maxHatchTickDelay = 12000;
    }
}