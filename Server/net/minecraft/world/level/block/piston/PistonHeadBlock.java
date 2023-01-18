/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Arrays
 */
package net.minecraft.world.level.block.piston;

import java.util.Arrays;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PistonHeadBlock
extends DirectionalBlock {
    public static final EnumProperty<PistonType> TYPE = BlockStateProperties.PISTON_TYPE;
    public static final BooleanProperty SHORT = BlockStateProperties.SHORT;
    public static final float PLATFORM = 4.0f;
    protected static final VoxelShape EAST_AABB = Block.box(12.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape WEST_AABB = Block.box(0.0, 0.0, 0.0, 4.0, 16.0, 16.0);
    protected static final VoxelShape SOUTH_AABB = Block.box(0.0, 0.0, 12.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape NORTH_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 4.0);
    protected static final VoxelShape UP_AABB = Block.box(0.0, 12.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape DOWN_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 4.0, 16.0);
    protected static final float AABB_OFFSET = 2.0f;
    protected static final float EDGE_MIN = 6.0f;
    protected static final float EDGE_MAX = 10.0f;
    protected static final VoxelShape UP_ARM_AABB = Block.box(6.0, -4.0, 6.0, 10.0, 12.0, 10.0);
    protected static final VoxelShape DOWN_ARM_AABB = Block.box(6.0, 4.0, 6.0, 10.0, 20.0, 10.0);
    protected static final VoxelShape SOUTH_ARM_AABB = Block.box(6.0, 6.0, -4.0, 10.0, 10.0, 12.0);
    protected static final VoxelShape NORTH_ARM_AABB = Block.box(6.0, 6.0, 4.0, 10.0, 10.0, 20.0);
    protected static final VoxelShape EAST_ARM_AABB = Block.box(-4.0, 6.0, 6.0, 12.0, 10.0, 10.0);
    protected static final VoxelShape WEST_ARM_AABB = Block.box(4.0, 6.0, 6.0, 20.0, 10.0, 10.0);
    protected static final VoxelShape SHORT_UP_ARM_AABB = Block.box(6.0, 0.0, 6.0, 10.0, 12.0, 10.0);
    protected static final VoxelShape SHORT_DOWN_ARM_AABB = Block.box(6.0, 4.0, 6.0, 10.0, 16.0, 10.0);
    protected static final VoxelShape SHORT_SOUTH_ARM_AABB = Block.box(6.0, 6.0, 0.0, 10.0, 10.0, 12.0);
    protected static final VoxelShape SHORT_NORTH_ARM_AABB = Block.box(6.0, 6.0, 4.0, 10.0, 10.0, 16.0);
    protected static final VoxelShape SHORT_EAST_ARM_AABB = Block.box(0.0, 6.0, 6.0, 12.0, 10.0, 10.0);
    protected static final VoxelShape SHORT_WEST_ARM_AABB = Block.box(4.0, 6.0, 6.0, 16.0, 10.0, 10.0);
    private static final VoxelShape[] SHAPES_SHORT = PistonHeadBlock.makeShapes(true);
    private static final VoxelShape[] SHAPES_LONG = PistonHeadBlock.makeShapes(false);

    private static VoxelShape[] makeShapes(boolean $$0) {
        return (VoxelShape[])Arrays.stream((Object[])Direction.values()).map($$1 -> PistonHeadBlock.calculateShape($$1, $$0)).toArray(VoxelShape[]::new);
    }

    private static VoxelShape calculateShape(Direction $$0, boolean $$1) {
        switch ($$0) {
            default: {
                return Shapes.or(DOWN_AABB, $$1 ? SHORT_DOWN_ARM_AABB : DOWN_ARM_AABB);
            }
            case UP: {
                return Shapes.or(UP_AABB, $$1 ? SHORT_UP_ARM_AABB : UP_ARM_AABB);
            }
            case NORTH: {
                return Shapes.or(NORTH_AABB, $$1 ? SHORT_NORTH_ARM_AABB : NORTH_ARM_AABB);
            }
            case SOUTH: {
                return Shapes.or(SOUTH_AABB, $$1 ? SHORT_SOUTH_ARM_AABB : SOUTH_ARM_AABB);
            }
            case WEST: {
                return Shapes.or(WEST_AABB, $$1 ? SHORT_WEST_ARM_AABB : WEST_ARM_AABB);
            }
            case EAST: 
        }
        return Shapes.or(EAST_AABB, $$1 ? SHORT_EAST_ARM_AABB : EAST_ARM_AABB);
    }

    public PistonHeadBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(TYPE, PistonType.DEFAULT)).setValue(SHORT, false));
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState $$0) {
        return true;
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return ($$0.getValue(SHORT) != false ? SHAPES_SHORT : SHAPES_LONG)[$$0.getValue(FACING).ordinal()];
    }

    private boolean isFittingBase(BlockState $$0, BlockState $$1) {
        Block $$2 = $$0.getValue(TYPE) == PistonType.DEFAULT ? Blocks.PISTON : Blocks.STICKY_PISTON;
        return $$1.is($$2) && $$1.getValue(PistonBaseBlock.EXTENDED) != false && $$1.getValue(FACING) == $$0.getValue(FACING);
    }

    @Override
    public void playerWillDestroy(Level $$0, BlockPos $$1, BlockState $$2, Player $$3) {
        Vec3i $$4;
        if (!$$0.isClientSide && $$3.getAbilities().instabuild && this.isFittingBase($$2, $$0.getBlockState((BlockPos)($$4 = $$1.relative($$2.getValue(FACING).getOpposite()))))) {
            $$0.destroyBlock((BlockPos)$$4, false);
        }
        super.playerWillDestroy($$0, $$1, $$2, $$3);
    }

    @Override
    public void onRemove(BlockState $$0, Level $$1, BlockPos $$2, BlockState $$3, boolean $$4) {
        if ($$0.is($$3.getBlock())) {
            return;
        }
        super.onRemove($$0, $$1, $$2, $$3, $$4);
        Vec3i $$5 = $$2.relative($$0.getValue(FACING).getOpposite());
        if (this.isFittingBase($$0, $$1.getBlockState((BlockPos)$$5))) {
            $$1.destroyBlock((BlockPos)$$5, true);
        }
    }

    @Override
    public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
        if ($$1.getOpposite() == $$0.getValue(FACING) && !$$0.canSurvive($$3, $$4)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    public boolean canSurvive(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        BlockState $$3 = $$1.getBlockState((BlockPos)$$2.relative($$0.getValue(FACING).getOpposite()));
        return this.isFittingBase($$0, $$3) || $$3.is(Blocks.MOVING_PISTON) && $$3.getValue(FACING) == $$0.getValue(FACING);
    }

    @Override
    public void neighborChanged(BlockState $$0, Level $$1, BlockPos $$2, Block $$3, BlockPos $$4, boolean $$5) {
        if ($$0.canSurvive($$1, $$2)) {
            $$1.neighborChanged((BlockPos)$$2.relative($$0.getValue(FACING).getOpposite()), $$3, $$4);
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter $$0, BlockPos $$1, BlockState $$2) {
        return new ItemStack($$2.getValue(TYPE) == PistonType.STICKY ? Blocks.STICKY_PISTON : Blocks.PISTON);
    }

    @Override
    public BlockState rotate(BlockState $$0, Rotation $$1) {
        return (BlockState)$$0.setValue(FACING, $$1.rotate($$0.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState $$0, Mirror $$1) {
        return $$0.rotate($$1.getRotation($$0.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(FACING, TYPE, SHORT);
    }

    @Override
    public boolean isPathfindable(BlockState $$0, BlockGetter $$1, BlockPos $$2, PathComputationType $$3) {
        return false;
    }
}