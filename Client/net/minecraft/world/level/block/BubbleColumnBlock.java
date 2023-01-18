/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Optional
 */
package net.minecraft.world.level.block;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BubbleColumnBlock
extends Block
implements BucketPickup {
    public static final BooleanProperty DRAG_DOWN = BlockStateProperties.DRAG;
    private static final int CHECK_PERIOD = 5;

    public BubbleColumnBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(DRAG_DOWN, true));
    }

    @Override
    public void entityInside(BlockState $$0, Level $$1, BlockPos $$2, Entity $$3) {
        BlockState $$4 = $$1.getBlockState((BlockPos)$$2.above());
        if ($$4.isAir()) {
            $$3.onAboveBubbleCol($$0.getValue(DRAG_DOWN));
            if (!$$1.isClientSide) {
                ServerLevel $$5 = (ServerLevel)$$1;
                for (int $$6 = 0; $$6 < 2; ++$$6) {
                    $$5.sendParticles(ParticleTypes.SPLASH, (double)$$2.getX() + $$1.random.nextDouble(), $$2.getY() + 1, (double)$$2.getZ() + $$1.random.nextDouble(), 1, 0.0, 0.0, 0.0, 1.0);
                    $$5.sendParticles(ParticleTypes.BUBBLE, (double)$$2.getX() + $$1.random.nextDouble(), $$2.getY() + 1, (double)$$2.getZ() + $$1.random.nextDouble(), 1, 0.0, 0.01, 0.0, 0.2);
                }
            }
        } else {
            $$3.onInsideBubbleColumn($$0.getValue(DRAG_DOWN));
        }
    }

    @Override
    public void tick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        BubbleColumnBlock.updateColumn($$1, $$2, $$0, $$1.getBlockState((BlockPos)$$2.below()));
    }

    @Override
    public FluidState getFluidState(BlockState $$0) {
        return Fluids.WATER.getSource(false);
    }

    public static void updateColumn(LevelAccessor $$0, BlockPos $$1, BlockState $$2) {
        BubbleColumnBlock.updateColumn($$0, $$1, $$0.getBlockState($$1), $$2);
    }

    public static void updateColumn(LevelAccessor $$0, BlockPos $$1, BlockState $$2, BlockState $$3) {
        if (!BubbleColumnBlock.canExistIn($$2)) {
            return;
        }
        BlockState $$4 = BubbleColumnBlock.getColumnState($$3);
        $$0.setBlock($$1, $$4, 2);
        BlockPos.MutableBlockPos $$5 = $$1.mutable().move(Direction.UP);
        while (BubbleColumnBlock.canExistIn($$0.getBlockState($$5))) {
            if (!$$0.setBlock($$5, $$4, 2)) {
                return;
            }
            $$5.move(Direction.UP);
        }
    }

    private static boolean canExistIn(BlockState $$0) {
        return $$0.is(Blocks.BUBBLE_COLUMN) || $$0.is(Blocks.WATER) && $$0.getFluidState().getAmount() >= 8 && $$0.getFluidState().isSource();
    }

    private static BlockState getColumnState(BlockState $$0) {
        if ($$0.is(Blocks.BUBBLE_COLUMN)) {
            return $$0;
        }
        if ($$0.is(Blocks.SOUL_SAND)) {
            return (BlockState)Blocks.BUBBLE_COLUMN.defaultBlockState().setValue(DRAG_DOWN, false);
        }
        if ($$0.is(Blocks.MAGMA_BLOCK)) {
            return (BlockState)Blocks.BUBBLE_COLUMN.defaultBlockState().setValue(DRAG_DOWN, true);
        }
        return Blocks.WATER.defaultBlockState();
    }

    @Override
    public void animateTick(BlockState $$0, Level $$1, BlockPos $$2, RandomSource $$3) {
        double $$4 = $$2.getX();
        double $$5 = $$2.getY();
        double $$6 = $$2.getZ();
        if ($$0.getValue(DRAG_DOWN).booleanValue()) {
            $$1.addAlwaysVisibleParticle(ParticleTypes.CURRENT_DOWN, $$4 + 0.5, $$5 + 0.8, $$6, 0.0, 0.0, 0.0);
            if ($$3.nextInt(200) == 0) {
                $$1.playLocalSound($$4, $$5, $$6, SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_AMBIENT, SoundSource.BLOCKS, 0.2f + $$3.nextFloat() * 0.2f, 0.9f + $$3.nextFloat() * 0.15f, false);
            }
        } else {
            $$1.addAlwaysVisibleParticle(ParticleTypes.BUBBLE_COLUMN_UP, $$4 + 0.5, $$5, $$6 + 0.5, 0.0, 0.04, 0.0);
            $$1.addAlwaysVisibleParticle(ParticleTypes.BUBBLE_COLUMN_UP, $$4 + (double)$$3.nextFloat(), $$5 + (double)$$3.nextFloat(), $$6 + (double)$$3.nextFloat(), 0.0, 0.04, 0.0);
            if ($$3.nextInt(200) == 0) {
                $$1.playLocalSound($$4, $$5, $$6, SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundSource.BLOCKS, 0.2f + $$3.nextFloat() * 0.2f, 0.9f + $$3.nextFloat() * 0.15f, false);
            }
        }
    }

    @Override
    public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
        $$3.scheduleTick($$4, Fluids.WATER, Fluids.WATER.getTickDelay($$3));
        if (!$$0.canSurvive($$3, $$4) || $$1 == Direction.DOWN || $$1 == Direction.UP && !$$2.is(Blocks.BUBBLE_COLUMN) && BubbleColumnBlock.canExistIn($$2)) {
            $$3.scheduleTick($$4, this, 5);
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    public boolean canSurvive(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        BlockState $$3 = $$1.getBlockState((BlockPos)$$2.below());
        return $$3.is(Blocks.BUBBLE_COLUMN) || $$3.is(Blocks.MAGMA_BLOCK) || $$3.is(Blocks.SOUL_SAND);
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return Shapes.empty();
    }

    @Override
    public RenderShape getRenderShape(BlockState $$0) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(DRAG_DOWN);
    }

    @Override
    public ItemStack pickupBlock(LevelAccessor $$0, BlockPos $$1, BlockState $$2) {
        $$0.setBlock($$1, Blocks.AIR.defaultBlockState(), 11);
        return new ItemStack(Items.WATER_BUCKET);
    }

    @Override
    public Optional<SoundEvent> getPickupSound() {
        return Fluids.WATER.getPickupSound();
    }
}