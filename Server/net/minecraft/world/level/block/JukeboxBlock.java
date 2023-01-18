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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

public class JukeboxBlock
extends BaseEntityBlock {
    public static final BooleanProperty HAS_RECORD = BlockStateProperties.HAS_RECORD;

    protected JukeboxBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(HAS_RECORD, false));
    }

    @Override
    public void setPlacedBy(Level $$0, BlockPos $$1, BlockState $$2, @Nullable LivingEntity $$3, ItemStack $$4) {
        super.setPlacedBy($$0, $$1, $$2, $$3, $$4);
        CompoundTag $$5 = BlockItem.getBlockEntityData($$4);
        if ($$5 != null && $$5.contains("RecordItem")) {
            $$0.setBlock($$1, (BlockState)$$2.setValue(HAS_RECORD, true), 2);
        }
    }

    @Override
    public InteractionResult use(BlockState $$0, Level $$1, BlockPos $$2, Player $$3, InteractionHand $$4, BlockHitResult $$5) {
        if ($$0.getValue(HAS_RECORD).booleanValue()) {
            this.dropRecording($$1, $$2);
            $$0 = (BlockState)$$0.setValue(HAS_RECORD, false);
            $$1.gameEvent(GameEvent.JUKEBOX_STOP_PLAY, $$2, GameEvent.Context.of($$0));
            $$1.setBlock($$2, $$0, 2);
            $$1.gameEvent(GameEvent.BLOCK_CHANGE, $$2, GameEvent.Context.of($$3, $$0));
            return InteractionResult.sidedSuccess($$1.isClientSide);
        }
        return InteractionResult.PASS;
    }

    public void setRecord(@Nullable Entity $$0, LevelAccessor $$1, BlockPos $$2, BlockState $$3, ItemStack $$4) {
        BlockEntity $$5 = $$1.getBlockEntity($$2);
        if ($$5 instanceof JukeboxBlockEntity) {
            JukeboxBlockEntity $$6 = (JukeboxBlockEntity)$$5;
            $$6.setRecord($$4.copy());
            $$6.playRecord();
            $$1.setBlock($$2, (BlockState)$$3.setValue(HAS_RECORD, true), 2);
            $$1.gameEvent(GameEvent.BLOCK_CHANGE, $$2, GameEvent.Context.of($$0, $$3));
        }
    }

    private void dropRecording(Level $$0, BlockPos $$1) {
        if ($$0.isClientSide) {
            return;
        }
        BlockEntity $$2 = $$0.getBlockEntity($$1);
        if (!($$2 instanceof JukeboxBlockEntity)) {
            return;
        }
        JukeboxBlockEntity $$3 = (JukeboxBlockEntity)$$2;
        ItemStack $$4 = $$3.getRecord();
        if ($$4.isEmpty()) {
            return;
        }
        $$0.levelEvent(1010, $$1, 0);
        $$3.clearContent();
        float $$5 = 0.7f;
        double $$6 = (double)($$0.random.nextFloat() * 0.7f) + (double)0.15f;
        double $$7 = (double)($$0.random.nextFloat() * 0.7f) + 0.06000000238418579 + 0.6;
        double $$8 = (double)($$0.random.nextFloat() * 0.7f) + (double)0.15f;
        ItemStack $$9 = $$4.copy();
        ItemEntity $$10 = new ItemEntity($$0, (double)$$1.getX() + $$6, (double)$$1.getY() + $$7, (double)$$1.getZ() + $$8, $$9);
        $$10.setDefaultPickUpDelay();
        $$0.addFreshEntity($$10);
    }

    @Override
    public void onRemove(BlockState $$0, Level $$1, BlockPos $$2, BlockState $$3, boolean $$4) {
        if ($$0.is($$3.getBlock())) {
            return;
        }
        this.dropRecording($$1, $$2);
        super.onRemove($$0, $$1, $$2, $$3, $$4);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos $$0, BlockState $$1) {
        return new JukeboxBlockEntity($$0, $$1);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState $$0) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState $$0, Level $$1, BlockPos $$2) {
        Item $$4;
        BlockEntity $$3 = $$1.getBlockEntity($$2);
        if ($$3 instanceof JukeboxBlockEntity && ($$4 = ((JukeboxBlockEntity)$$3).getRecord().getItem()) instanceof RecordItem) {
            return ((RecordItem)$$4).getAnalogOutput();
        }
        return 0;
    }

    @Override
    public RenderShape getRenderShape(BlockState $$0) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(HAS_RECORD);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level $$0, BlockState $$1, BlockEntityType<T> $$2) {
        if ($$1.getValue(HAS_RECORD).booleanValue()) {
            return JukeboxBlock.createTickerHelper($$2, BlockEntityType.JUKEBOX, JukeboxBlockEntity::playRecordTick);
        }
        return null;
    }
}