/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AttachedStemBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FarmBlock
extends Block {
    public static final IntegerProperty MOISTURE = BlockStateProperties.MOISTURE;
    protected static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 15.0, 16.0);
    public static final int MAX_MOISTURE = 7;

    protected FarmBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(MOISTURE, 0));
    }

    @Override
    public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
        if ($$1 == Direction.UP && !$$0.canSurvive($$3, $$4)) {
            $$3.scheduleTick($$4, this, 1);
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    public boolean canSurvive(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        BlockState $$3 = $$1.getBlockState((BlockPos)$$2.above());
        return !$$3.getMaterial().isSolid() || $$3.getBlock() instanceof FenceGateBlock || $$3.getBlock() instanceof MovingPistonBlock;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        if (!this.defaultBlockState().canSurvive($$0.getLevel(), $$0.getClickedPos())) {
            return Blocks.DIRT.defaultBlockState();
        }
        return super.getStateForPlacement($$0);
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState $$0) {
        return true;
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return SHAPE;
    }

    @Override
    public void tick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        if (!$$0.canSurvive($$1, $$2)) {
            FarmBlock.turnToDirt($$0, $$1, $$2);
        }
    }

    @Override
    public void randomTick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        int $$4 = $$0.getValue(MOISTURE);
        if (FarmBlock.isNearWater($$1, $$2) || $$1.isRainingAt((BlockPos)$$2.above())) {
            if ($$4 < 7) {
                $$1.setBlock($$2, (BlockState)$$0.setValue(MOISTURE, 7), 2);
            }
        } else if ($$4 > 0) {
            $$1.setBlock($$2, (BlockState)$$0.setValue(MOISTURE, $$4 - 1), 2);
        } else if (!FarmBlock.isUnderCrops($$1, $$2)) {
            FarmBlock.turnToDirt($$0, $$1, $$2);
        }
    }

    @Override
    public void fallOn(Level $$0, BlockState $$1, BlockPos $$2, Entity $$3, float $$4) {
        if (!$$0.isClientSide && $$0.random.nextFloat() < $$4 - 0.5f && $$3 instanceof LivingEntity && ($$3 instanceof Player || $$0.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) && $$3.getBbWidth() * $$3.getBbWidth() * $$3.getBbHeight() > 0.512f) {
            FarmBlock.turnToDirt($$1, $$0, $$2);
        }
        super.fallOn($$0, $$1, $$2, $$3, $$4);
    }

    public static void turnToDirt(BlockState $$0, Level $$1, BlockPos $$2) {
        $$1.setBlockAndUpdate($$2, FarmBlock.pushEntitiesUp($$0, Blocks.DIRT.defaultBlockState(), $$1, $$2));
    }

    private static boolean isUnderCrops(BlockGetter $$0, BlockPos $$1) {
        Block $$2 = $$0.getBlockState((BlockPos)$$1.above()).getBlock();
        return $$2 instanceof CropBlock || $$2 instanceof StemBlock || $$2 instanceof AttachedStemBlock;
    }

    private static boolean isNearWater(LevelReader $$0, BlockPos $$1) {
        for (BlockPos $$2 : BlockPos.betweenClosed($$1.offset(-4, 0, -4), $$1.offset(4, 1, 4))) {
            if (!$$0.getFluidState($$2).is(FluidTags.WATER)) continue;
            return true;
        }
        return false;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(MOISTURE);
    }

    @Override
    public boolean isPathfindable(BlockState $$0, BlockGetter $$1, BlockPos $$2, PathComputationType $$3) {
        return false;
    }
}