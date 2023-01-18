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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GameMasterBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.phys.BlockHitResult;

public class StructureBlock
extends BaseEntityBlock
implements GameMasterBlock {
    public static final EnumProperty<StructureMode> MODE = BlockStateProperties.STRUCTUREBLOCK_MODE;

    protected StructureBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(MODE, StructureMode.LOAD));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos $$0, BlockState $$1) {
        return new StructureBlockEntity($$0, $$1);
    }

    @Override
    public InteractionResult use(BlockState $$0, Level $$1, BlockPos $$2, Player $$3, InteractionHand $$4, BlockHitResult $$5) {
        BlockEntity $$6 = $$1.getBlockEntity($$2);
        if ($$6 instanceof StructureBlockEntity) {
            return ((StructureBlockEntity)$$6).usedBy($$3) ? InteractionResult.sidedSuccess($$1.isClientSide) : InteractionResult.PASS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void setPlacedBy(Level $$0, BlockPos $$1, BlockState $$2, @Nullable LivingEntity $$3, ItemStack $$4) {
        BlockEntity $$5;
        if ($$0.isClientSide) {
            return;
        }
        if ($$3 != null && ($$5 = $$0.getBlockEntity($$1)) instanceof StructureBlockEntity) {
            ((StructureBlockEntity)$$5).createdBy($$3);
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState $$0) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(MODE);
    }

    @Override
    public void neighborChanged(BlockState $$0, Level $$1, BlockPos $$2, Block $$3, BlockPos $$4, boolean $$5) {
        if (!($$1 instanceof ServerLevel)) {
            return;
        }
        BlockEntity $$6 = $$1.getBlockEntity($$2);
        if (!($$6 instanceof StructureBlockEntity)) {
            return;
        }
        StructureBlockEntity $$7 = (StructureBlockEntity)$$6;
        boolean $$8 = $$1.hasNeighborSignal($$2);
        boolean $$9 = $$7.isPowered();
        if ($$8 && !$$9) {
            $$7.setPowered(true);
            this.trigger((ServerLevel)$$1, $$7);
        } else if (!$$8 && $$9) {
            $$7.setPowered(false);
        }
    }

    private void trigger(ServerLevel $$0, StructureBlockEntity $$1) {
        switch ($$1.getMode()) {
            case SAVE: {
                $$1.saveStructure(false);
                break;
            }
            case LOAD: {
                $$1.loadStructure($$0, false);
                break;
            }
            case CORNER: {
                $$1.unloadStructure();
                break;
            }
            case DATA: {
                break;
            }
        }
    }
}