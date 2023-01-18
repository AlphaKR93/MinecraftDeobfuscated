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
import net.minecraft.core.FrontAndTop;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.GameMasterBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.BlockHitResult;

public class JigsawBlock
extends Block
implements EntityBlock,
GameMasterBlock {
    public static final EnumProperty<FrontAndTop> ORIENTATION = BlockStateProperties.ORIENTATION;

    protected JigsawBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(ORIENTATION, FrontAndTop.NORTH_UP));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(ORIENTATION);
    }

    @Override
    public BlockState rotate(BlockState $$0, Rotation $$1) {
        return (BlockState)$$0.setValue(ORIENTATION, $$1.rotation().rotate($$0.getValue(ORIENTATION)));
    }

    @Override
    public BlockState mirror(BlockState $$0, Mirror $$1) {
        return (BlockState)$$0.setValue(ORIENTATION, $$1.rotation().rotate($$0.getValue(ORIENTATION)));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        Direction $$3;
        Direction $$1 = $$0.getClickedFace();
        if ($$1.getAxis() == Direction.Axis.Y) {
            Direction $$2 = $$0.getHorizontalDirection().getOpposite();
        } else {
            $$3 = Direction.UP;
        }
        return (BlockState)this.defaultBlockState().setValue(ORIENTATION, FrontAndTop.fromFrontAndTop($$1, $$3));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos $$0, BlockState $$1) {
        return new JigsawBlockEntity($$0, $$1);
    }

    @Override
    public InteractionResult use(BlockState $$0, Level $$1, BlockPos $$2, Player $$3, InteractionHand $$4, BlockHitResult $$5) {
        BlockEntity $$6 = $$1.getBlockEntity($$2);
        if ($$6 instanceof JigsawBlockEntity && $$3.canUseGameMasterBlocks()) {
            $$3.openJigsawBlock((JigsawBlockEntity)$$6);
            return InteractionResult.sidedSuccess($$1.isClientSide);
        }
        return InteractionResult.PASS;
    }

    public static boolean canAttach(StructureTemplate.StructureBlockInfo $$0, StructureTemplate.StructureBlockInfo $$1) {
        Direction $$2 = JigsawBlock.getFrontFacing($$0.state);
        Direction $$3 = JigsawBlock.getFrontFacing($$1.state);
        Direction $$4 = JigsawBlock.getTopFacing($$0.state);
        Direction $$5 = JigsawBlock.getTopFacing($$1.state);
        JigsawBlockEntity.JointType $$6 = (JigsawBlockEntity.JointType)JigsawBlockEntity.JointType.byName($$0.nbt.getString("joint")).orElseGet(() -> $$2.getAxis().isHorizontal() ? JigsawBlockEntity.JointType.ALIGNED : JigsawBlockEntity.JointType.ROLLABLE);
        boolean $$7 = $$6 == JigsawBlockEntity.JointType.ROLLABLE;
        return $$2 == $$3.getOpposite() && ($$7 || $$4 == $$5) && $$0.nbt.getString("target").equals((Object)$$1.nbt.getString("name"));
    }

    public static Direction getFrontFacing(BlockState $$0) {
        return $$0.getValue(ORIENTATION).front();
    }

    public static Direction getTopFacing(BlockState $$0) {
        return $$0.getValue(ORIENTATION).top();
    }
}