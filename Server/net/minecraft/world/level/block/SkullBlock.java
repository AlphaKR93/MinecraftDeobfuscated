/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SkullBlock
extends AbstractSkullBlock {
    public static final int MAX = RotationSegment.getMaxSegmentIndex();
    private static final int ROTATIONS = MAX + 1;
    public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;
    protected static final VoxelShape SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 8.0, 12.0);
    protected static final VoxelShape PIGLIN_SHAPE = Block.box(3.0, 0.0, 3.0, 13.0, 8.0, 13.0);

    protected SkullBlock(Type $$0, BlockBehaviour.Properties $$1) {
        super($$0, $$1);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(ROTATION, 0));
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        if (this.getType() == Types.PIGLIN) {
            return PIGLIN_SHAPE;
        }
        return SHAPE;
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        return Shapes.empty();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        return (BlockState)this.defaultBlockState().setValue(ROTATION, RotationSegment.convertToSegment($$0.getRotation()));
    }

    @Override
    public BlockState rotate(BlockState $$0, Rotation $$1) {
        return (BlockState)$$0.setValue(ROTATION, $$1.rotate($$0.getValue(ROTATION), ROTATIONS));
    }

    @Override
    public BlockState mirror(BlockState $$0, Mirror $$1) {
        return (BlockState)$$0.setValue(ROTATION, $$1.mirror($$0.getValue(ROTATION), ROTATIONS));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(ROTATION);
    }

    public static interface Type {
    }

    public static enum Types implements Type
    {
        SKELETON,
        WITHER_SKELETON,
        PLAYER,
        ZOMBIE,
        CREEPER,
        PIGLIN,
        DRAGON;

    }
}