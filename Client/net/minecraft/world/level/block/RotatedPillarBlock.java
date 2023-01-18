/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.level.block;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class RotatedPillarBlock
extends Block {
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;

    public RotatedPillarBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)this.defaultBlockState().setValue(AXIS, Direction.Axis.Y));
    }

    @Override
    public BlockState rotate(BlockState $$0, Rotation $$1) {
        return RotatedPillarBlock.rotatePillar($$0, $$1);
    }

    public static BlockState rotatePillar(BlockState $$0, Rotation $$1) {
        switch ($$1) {
            case COUNTERCLOCKWISE_90: 
            case CLOCKWISE_90: {
                switch ($$0.getValue(AXIS)) {
                    case X: {
                        return (BlockState)$$0.setValue(AXIS, Direction.Axis.Z);
                    }
                    case Z: {
                        return (BlockState)$$0.setValue(AXIS, Direction.Axis.X);
                    }
                }
                return $$0;
            }
        }
        return $$0;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(AXIS);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        return (BlockState)this.defaultBlockState().setValue(AXIS, $$0.getClickedFace().getAxis());
    }
}