/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BambooLeaves;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BambooStalkBlock
extends Block
implements BonemealableBlock {
    protected static final float SMALL_LEAVES_AABB_OFFSET = 3.0f;
    protected static final float LARGE_LEAVES_AABB_OFFSET = 5.0f;
    protected static final float COLLISION_AABB_OFFSET = 1.5f;
    protected static final VoxelShape SMALL_SHAPE = Block.box(5.0, 0.0, 5.0, 11.0, 16.0, 11.0);
    protected static final VoxelShape LARGE_SHAPE = Block.box(3.0, 0.0, 3.0, 13.0, 16.0, 13.0);
    protected static final VoxelShape COLLISION_SHAPE = Block.box(6.5, 0.0, 6.5, 9.5, 16.0, 9.5);
    public static final IntegerProperty AGE = BlockStateProperties.AGE_1;
    public static final EnumProperty<BambooLeaves> LEAVES = BlockStateProperties.BAMBOO_LEAVES;
    public static final IntegerProperty STAGE = BlockStateProperties.STAGE;
    public static final int MAX_HEIGHT = 16;
    public static final int STAGE_GROWING = 0;
    public static final int STAGE_DONE_GROWING = 1;
    public static final int AGE_THIN_BAMBOO = 0;
    public static final int AGE_THICK_BAMBOO = 1;

    public BambooStalkBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(AGE, 0)).setValue(LEAVES, BambooLeaves.NONE)).setValue(STAGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(AGE, LEAVES, STAGE);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        return true;
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        VoxelShape $$4 = $$0.getValue(LEAVES) == BambooLeaves.LARGE ? LARGE_SHAPE : SMALL_SHAPE;
        Vec3 $$5 = $$0.getOffset($$1, $$2);
        return $$4.move($$5.x, $$5.y, $$5.z);
    }

    @Override
    public boolean isPathfindable(BlockState $$0, BlockGetter $$1, BlockPos $$2, PathComputationType $$3) {
        return false;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        Vec3 $$4 = $$0.getOffset($$1, $$2);
        return COLLISION_SHAPE.move($$4.x, $$4.y, $$4.z);
    }

    @Override
    public boolean isCollisionShapeFullBlock(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        return false;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        FluidState $$1 = $$0.getLevel().getFluidState($$0.getClickedPos());
        if (!$$1.isEmpty()) {
            return null;
        }
        BlockState $$2 = $$0.getLevel().getBlockState((BlockPos)$$0.getClickedPos().below());
        if ($$2.is(BlockTags.BAMBOO_PLANTABLE_ON)) {
            if ($$2.is(Blocks.BAMBOO_SAPLING)) {
                return (BlockState)this.defaultBlockState().setValue(AGE, 0);
            }
            if ($$2.is(Blocks.BAMBOO)) {
                int $$3 = $$2.getValue(AGE) > 0 ? 1 : 0;
                return (BlockState)this.defaultBlockState().setValue(AGE, $$3);
            }
            BlockState $$4 = $$0.getLevel().getBlockState((BlockPos)$$0.getClickedPos().above());
            if ($$4.is(Blocks.BAMBOO)) {
                return (BlockState)this.defaultBlockState().setValue(AGE, $$4.getValue(AGE));
            }
            return Blocks.BAMBOO_SAPLING.defaultBlockState();
        }
        return null;
    }

    @Override
    public void tick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        if (!$$0.canSurvive($$1, $$2)) {
            $$1.destroyBlock($$2, true);
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState $$0) {
        return $$0.getValue(STAGE) == 0;
    }

    @Override
    public void randomTick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        int $$4;
        if ($$0.getValue(STAGE) != 0) {
            return;
        }
        if ($$3.nextInt(3) == 0 && $$1.isEmptyBlock((BlockPos)$$2.above()) && $$1.getRawBrightness((BlockPos)$$2.above(), 0) >= 9 && ($$4 = this.getHeightBelowUpToMax($$1, $$2) + 1) < 16) {
            this.growBamboo($$0, $$1, $$2, $$3, $$4);
        }
    }

    @Override
    public boolean canSurvive(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        return $$1.getBlockState((BlockPos)$$2.below()).is(BlockTags.BAMBOO_PLANTABLE_ON);
    }

    @Override
    public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
        if (!$$0.canSurvive($$3, $$4)) {
            $$3.scheduleTick($$4, this, 1);
        }
        if ($$1 == Direction.UP && $$2.is(Blocks.BAMBOO) && $$2.getValue(AGE) > $$0.getValue(AGE)) {
            $$3.setBlock($$4, (BlockState)$$0.cycle(AGE), 2);
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader $$0, BlockPos $$1, BlockState $$2, boolean $$3) {
        int $$5;
        int $$4 = this.getHeightAboveUpToMax($$0, $$1);
        return $$4 + ($$5 = this.getHeightBelowUpToMax($$0, $$1)) + 1 < 16 && $$0.getBlockState((BlockPos)$$1.above($$4)).getValue(STAGE) != 1;
    }

    @Override
    public boolean isBonemealSuccess(Level $$0, RandomSource $$1, BlockPos $$2, BlockState $$3) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel $$0, RandomSource $$1, BlockPos $$2, BlockState $$3) {
        int $$4 = this.getHeightAboveUpToMax($$0, $$2);
        int $$5 = this.getHeightBelowUpToMax($$0, $$2);
        int $$6 = $$4 + $$5 + 1;
        int $$7 = 1 + $$1.nextInt(2);
        for (int $$8 = 0; $$8 < $$7; ++$$8) {
            Vec3i $$9 = $$2.above($$4);
            BlockState $$10 = $$0.getBlockState((BlockPos)$$9);
            if ($$6 >= 16 || $$10.getValue(STAGE) == 1 || !$$0.isEmptyBlock((BlockPos)((BlockPos)$$9).above())) {
                return;
            }
            this.growBamboo($$10, $$0, (BlockPos)$$9, $$1, $$6);
            ++$$4;
            ++$$6;
        }
    }

    @Override
    public float getDestroyProgress(BlockState $$0, Player $$1, BlockGetter $$2, BlockPos $$3) {
        if ($$1.getMainHandItem().getItem() instanceof SwordItem) {
            return 1.0f;
        }
        return super.getDestroyProgress($$0, $$1, $$2, $$3);
    }

    protected void growBamboo(BlockState $$0, Level $$1, BlockPos $$2, RandomSource $$3, int $$4) {
        BlockState $$5 = $$1.getBlockState((BlockPos)$$2.below());
        Vec3i $$6 = $$2.below(2);
        BlockState $$7 = $$1.getBlockState((BlockPos)$$6);
        BambooLeaves $$8 = BambooLeaves.NONE;
        if ($$4 >= 1) {
            if (!$$5.is(Blocks.BAMBOO) || $$5.getValue(LEAVES) == BambooLeaves.NONE) {
                $$8 = BambooLeaves.SMALL;
            } else if ($$5.is(Blocks.BAMBOO) && $$5.getValue(LEAVES) != BambooLeaves.NONE) {
                $$8 = BambooLeaves.LARGE;
                if ($$7.is(Blocks.BAMBOO)) {
                    $$1.setBlock((BlockPos)$$2.below(), (BlockState)$$5.setValue(LEAVES, BambooLeaves.SMALL), 3);
                    $$1.setBlock((BlockPos)$$6, (BlockState)$$7.setValue(LEAVES, BambooLeaves.NONE), 3);
                }
            }
        }
        int $$9 = $$0.getValue(AGE) == 1 || $$7.is(Blocks.BAMBOO) ? 1 : 0;
        int $$10 = $$4 >= 11 && $$3.nextFloat() < 0.25f || $$4 == 15 ? 1 : 0;
        $$1.setBlock((BlockPos)$$2.above(), (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(AGE, $$9)).setValue(LEAVES, $$8)).setValue(STAGE, $$10), 3);
    }

    protected int getHeightAboveUpToMax(BlockGetter $$0, BlockPos $$1) {
        int $$2;
        for ($$2 = 0; $$2 < 16 && $$0.getBlockState((BlockPos)$$1.above($$2 + 1)).is(Blocks.BAMBOO); ++$$2) {
        }
        return $$2;
    }

    protected int getHeightBelowUpToMax(BlockGetter $$0, BlockPos $$1) {
        int $$2;
        for ($$2 = 0; $$2 < 16 && $$0.getBlockState((BlockPos)$$1.below($$2 + 1)).is(Blocks.BAMBOO); ++$$2) {
        }
        return $$2;
    }
}