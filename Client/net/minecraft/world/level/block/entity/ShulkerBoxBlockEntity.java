/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.List
 *  java.util.stream.IntStream
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block.entity;

import java.util.List;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ShulkerBoxBlockEntity
extends RandomizableContainerBlockEntity
implements WorldlyContainer {
    public static final int COLUMNS = 9;
    public static final int ROWS = 3;
    public static final int CONTAINER_SIZE = 27;
    public static final int EVENT_SET_OPEN_COUNT = 1;
    public static final int OPENING_TICK_LENGTH = 10;
    public static final float MAX_LID_HEIGHT = 0.5f;
    public static final float MAX_LID_ROTATION = 270.0f;
    public static final String ITEMS_TAG = "Items";
    private static final int[] SLOTS = IntStream.range((int)0, (int)27).toArray();
    private NonNullList<ItemStack> itemStacks = NonNullList.withSize(27, ItemStack.EMPTY);
    private int openCount;
    private AnimationStatus animationStatus = AnimationStatus.CLOSED;
    private float progress;
    private float progressOld;
    @Nullable
    private final DyeColor color;

    public ShulkerBoxBlockEntity(@Nullable DyeColor $$0, BlockPos $$1, BlockState $$2) {
        super(BlockEntityType.SHULKER_BOX, $$1, $$2);
        this.color = $$0;
    }

    public ShulkerBoxBlockEntity(BlockPos $$0, BlockState $$1) {
        super(BlockEntityType.SHULKER_BOX, $$0, $$1);
        this.color = ShulkerBoxBlock.getColorFromBlock($$1.getBlock());
    }

    public static void tick(Level $$0, BlockPos $$1, BlockState $$2, ShulkerBoxBlockEntity $$3) {
        $$3.updateAnimation($$0, $$1, $$2);
    }

    private void updateAnimation(Level $$0, BlockPos $$1, BlockState $$2) {
        this.progressOld = this.progress;
        switch (this.animationStatus) {
            case CLOSED: {
                this.progress = 0.0f;
                break;
            }
            case OPENING: {
                this.progress += 0.1f;
                if (this.progress >= 1.0f) {
                    this.animationStatus = AnimationStatus.OPENED;
                    this.progress = 1.0f;
                    ShulkerBoxBlockEntity.doNeighborUpdates($$0, $$1, $$2);
                }
                this.moveCollidedEntities($$0, $$1, $$2);
                break;
            }
            case CLOSING: {
                this.progress -= 0.1f;
                if (!(this.progress <= 0.0f)) break;
                this.animationStatus = AnimationStatus.CLOSED;
                this.progress = 0.0f;
                ShulkerBoxBlockEntity.doNeighborUpdates($$0, $$1, $$2);
                break;
            }
            case OPENED: {
                this.progress = 1.0f;
            }
        }
    }

    public AnimationStatus getAnimationStatus() {
        return this.animationStatus;
    }

    public AABB getBoundingBox(BlockState $$0) {
        return Shulker.getProgressAabb($$0.getValue(ShulkerBoxBlock.FACING), 0.5f * this.getProgress(1.0f));
    }

    private void moveCollidedEntities(Level $$0, BlockPos $$1, BlockState $$2) {
        if (!($$2.getBlock() instanceof ShulkerBoxBlock)) {
            return;
        }
        Direction $$3 = $$2.getValue(ShulkerBoxBlock.FACING);
        AABB $$4 = Shulker.getProgressDeltaAabb($$3, this.progressOld, this.progress).move($$1);
        List $$5 = $$0.getEntities(null, $$4);
        if ($$5.isEmpty()) {
            return;
        }
        for (int $$6 = 0; $$6 < $$5.size(); ++$$6) {
            Entity $$7 = (Entity)$$5.get($$6);
            if ($$7.getPistonPushReaction() == PushReaction.IGNORE) continue;
            $$7.move(MoverType.SHULKER_BOX, new Vec3(($$4.getXsize() + 0.01) * (double)$$3.getStepX(), ($$4.getYsize() + 0.01) * (double)$$3.getStepY(), ($$4.getZsize() + 0.01) * (double)$$3.getStepZ()));
        }
    }

    @Override
    public int getContainerSize() {
        return this.itemStacks.size();
    }

    @Override
    public boolean triggerEvent(int $$0, int $$1) {
        if ($$0 == 1) {
            this.openCount = $$1;
            if ($$1 == 0) {
                this.animationStatus = AnimationStatus.CLOSING;
                ShulkerBoxBlockEntity.doNeighborUpdates(this.getLevel(), this.worldPosition, this.getBlockState());
            }
            if ($$1 == 1) {
                this.animationStatus = AnimationStatus.OPENING;
                ShulkerBoxBlockEntity.doNeighborUpdates(this.getLevel(), this.worldPosition, this.getBlockState());
            }
            return true;
        }
        return super.triggerEvent($$0, $$1);
    }

    private static void doNeighborUpdates(Level $$0, BlockPos $$1, BlockState $$2) {
        $$2.updateNeighbourShapes($$0, $$1, 3);
    }

    @Override
    public void startOpen(Player $$0) {
        if (!this.remove && !$$0.isSpectator()) {
            if (this.openCount < 0) {
                this.openCount = 0;
            }
            ++this.openCount;
            this.level.blockEvent(this.worldPosition, this.getBlockState().getBlock(), 1, this.openCount);
            if (this.openCount == 1) {
                this.level.gameEvent($$0, GameEvent.CONTAINER_OPEN, this.worldPosition);
                this.level.playSound(null, this.worldPosition, SoundEvents.SHULKER_BOX_OPEN, SoundSource.BLOCKS, 0.5f, this.level.random.nextFloat() * 0.1f + 0.9f);
            }
        }
    }

    @Override
    public void stopOpen(Player $$0) {
        if (!this.remove && !$$0.isSpectator()) {
            --this.openCount;
            this.level.blockEvent(this.worldPosition, this.getBlockState().getBlock(), 1, this.openCount);
            if (this.openCount <= 0) {
                this.level.gameEvent($$0, GameEvent.CONTAINER_CLOSE, this.worldPosition);
                this.level.playSound(null, this.worldPosition, SoundEvents.SHULKER_BOX_CLOSE, SoundSource.BLOCKS, 0.5f, this.level.random.nextFloat() * 0.1f + 0.9f);
            }
        }
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.shulkerBox");
    }

    @Override
    public void load(CompoundTag $$0) {
        super.load($$0);
        this.loadFromTag($$0);
    }

    @Override
    protected void saveAdditional(CompoundTag $$0) {
        super.saveAdditional($$0);
        if (!this.trySaveLootTable($$0)) {
            ContainerHelper.saveAllItems($$0, this.itemStacks, false);
        }
    }

    public void loadFromTag(CompoundTag $$0) {
        this.itemStacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable($$0) && $$0.contains(ITEMS_TAG, 9)) {
            ContainerHelper.loadAllItems($$0, this.itemStacks);
        }
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.itemStacks;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> $$0) {
        this.itemStacks = $$0;
    }

    @Override
    public int[] getSlotsForFace(Direction $$0) {
        return SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int $$0, ItemStack $$1, @Nullable Direction $$2) {
        return !(Block.byItem($$1.getItem()) instanceof ShulkerBoxBlock);
    }

    @Override
    public boolean canTakeItemThroughFace(int $$0, ItemStack $$1, Direction $$2) {
        return true;
    }

    public float getProgress(float $$0) {
        return Mth.lerp($$0, this.progressOld, this.progress);
    }

    @Nullable
    public DyeColor getColor() {
        return this.color;
    }

    @Override
    protected AbstractContainerMenu createMenu(int $$0, Inventory $$1) {
        return new ShulkerBoxMenu($$0, $$1, this);
    }

    public boolean isClosed() {
        return this.animationStatus == AnimationStatus.CLOSED;
    }

    public static enum AnimationStatus {
        CLOSED,
        OPENING,
        OPENED,
        CLOSING;

    }
}