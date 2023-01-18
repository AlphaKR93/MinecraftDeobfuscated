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
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class FaceAttachedHorizontalDirectionalBlock
extends HorizontalDirectionalBlock {
    public static final EnumProperty<AttachFace> FACE = BlockStateProperties.ATTACH_FACE;

    protected FaceAttachedHorizontalDirectionalBlock(BlockBehaviour.Properties $$0) {
        super($$0);
    }

    @Override
    public boolean canSurvive(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        return FaceAttachedHorizontalDirectionalBlock.canAttach($$1, $$2, FaceAttachedHorizontalDirectionalBlock.getConnectedDirection($$0).getOpposite());
    }

    public static boolean canAttach(LevelReader $$0, BlockPos $$1, Direction $$2) {
        Vec3i $$3 = $$1.relative($$2);
        return $$0.getBlockState((BlockPos)$$3).isFaceSturdy($$0, (BlockPos)$$3, $$2.getOpposite());
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        for (Direction $$1 : $$0.getNearestLookingDirections()) {
            BlockState $$3;
            if ($$1.getAxis() == Direction.Axis.Y) {
                BlockState $$2 = (BlockState)((BlockState)this.defaultBlockState().setValue(FACE, $$1 == Direction.UP ? AttachFace.CEILING : AttachFace.FLOOR)).setValue(FACING, $$0.getHorizontalDirection());
            } else {
                $$3 = (BlockState)((BlockState)this.defaultBlockState().setValue(FACE, AttachFace.WALL)).setValue(FACING, $$1.getOpposite());
            }
            if (!$$3.canSurvive($$0.getLevel(), $$0.getClickedPos())) continue;
            return $$3;
        }
        return null;
    }

    @Override
    public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
        if (FaceAttachedHorizontalDirectionalBlock.getConnectedDirection($$0).getOpposite() == $$1 && !$$0.canSurvive($$3, $$4)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5);
    }

    protected static Direction getConnectedDirection(BlockState $$0) {
        switch ($$0.getValue(FACE)) {
            case CEILING: {
                return Direction.DOWN;
            }
            case FLOOR: {
                return Direction.UP;
            }
        }
        return $$0.getValue(FACING);
    }
}