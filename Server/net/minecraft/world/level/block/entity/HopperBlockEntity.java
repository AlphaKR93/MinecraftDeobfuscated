/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Iterator
 *  java.util.List
 *  java.util.function.BooleanSupplier
 *  java.util.stream.Collectors
 *  java.util.stream.IntStream
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block.entity;

import java.util.Iterator;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.HopperMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;

public class HopperBlockEntity
extends RandomizableContainerBlockEntity
implements Hopper {
    public static final int MOVE_ITEM_SPEED = 8;
    public static final int HOPPER_CONTAINER_SIZE = 5;
    private NonNullList<ItemStack> items = NonNullList.withSize(5, ItemStack.EMPTY);
    private int cooldownTime = -1;
    private long tickedGameTime;

    public HopperBlockEntity(BlockPos $$0, BlockState $$1) {
        super(BlockEntityType.HOPPER, $$0, $$1);
    }

    @Override
    public void load(CompoundTag $$0) {
        super.load($$0);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable($$0)) {
            ContainerHelper.loadAllItems($$0, this.items);
        }
        this.cooldownTime = $$0.getInt("TransferCooldown");
    }

    @Override
    protected void saveAdditional(CompoundTag $$0) {
        super.saveAdditional($$0);
        if (!this.trySaveLootTable($$0)) {
            ContainerHelper.saveAllItems($$0, this.items);
        }
        $$0.putInt("TransferCooldown", this.cooldownTime);
    }

    @Override
    public int getContainerSize() {
        return this.items.size();
    }

    @Override
    public ItemStack removeItem(int $$0, int $$1) {
        this.unpackLootTable(null);
        return ContainerHelper.removeItem(this.getItems(), $$0, $$1);
    }

    @Override
    public void setItem(int $$0, ItemStack $$1) {
        this.unpackLootTable(null);
        this.getItems().set($$0, $$1);
        if ($$1.getCount() > this.getMaxStackSize()) {
            $$1.setCount(this.getMaxStackSize());
        }
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.hopper");
    }

    public static void pushItemsTick(Level $$0, BlockPos $$1, BlockState $$2, HopperBlockEntity $$3) {
        --$$3.cooldownTime;
        $$3.tickedGameTime = $$0.getGameTime();
        if (!$$3.isOnCooldown()) {
            $$3.setCooldown(0);
            HopperBlockEntity.tryMoveItems($$0, $$1, $$2, $$3, () -> HopperBlockEntity.suckInItems($$0, $$3));
        }
    }

    private static boolean tryMoveItems(Level $$0, BlockPos $$1, BlockState $$2, HopperBlockEntity $$3, BooleanSupplier $$4) {
        if ($$0.isClientSide) {
            return false;
        }
        if (!$$3.isOnCooldown() && $$2.getValue(HopperBlock.ENABLED).booleanValue()) {
            boolean $$5 = false;
            if (!$$3.isEmpty()) {
                $$5 = HopperBlockEntity.ejectItems($$0, $$1, $$2, $$3);
            }
            if (!$$3.inventoryFull()) {
                $$5 |= $$4.getAsBoolean();
            }
            if ($$5) {
                $$3.setCooldown(8);
                HopperBlockEntity.setChanged($$0, $$1, $$2);
                return true;
            }
        }
        return false;
    }

    private boolean inventoryFull() {
        Iterator iterator = this.items.iterator();
        while (iterator.hasNext()) {
            ItemStack $$0 = (ItemStack)iterator.next();
            if (!$$0.isEmpty() && $$0.getCount() == $$0.getMaxStackSize()) continue;
            return false;
        }
        return true;
    }

    private static boolean ejectItems(Level $$0, BlockPos $$1, BlockState $$2, Container $$3) {
        Container $$4 = HopperBlockEntity.getAttachedContainer($$0, $$1, $$2);
        if ($$4 == null) {
            return false;
        }
        Direction $$5 = $$2.getValue(HopperBlock.FACING).getOpposite();
        if (HopperBlockEntity.isFullContainer($$4, $$5)) {
            return false;
        }
        for (int $$6 = 0; $$6 < $$3.getContainerSize(); ++$$6) {
            if ($$3.getItem($$6).isEmpty()) continue;
            ItemStack $$7 = $$3.getItem($$6).copy();
            ItemStack $$8 = HopperBlockEntity.addItem($$3, $$4, $$3.removeItem($$6, 1), $$5);
            if ($$8.isEmpty()) {
                $$4.setChanged();
                return true;
            }
            $$3.setItem($$6, $$7);
        }
        return false;
    }

    private static IntStream getSlots(Container $$0, Direction $$1) {
        if ($$0 instanceof WorldlyContainer) {
            return IntStream.of((int[])((WorldlyContainer)$$0).getSlotsForFace($$1));
        }
        return IntStream.range((int)0, (int)$$0.getContainerSize());
    }

    private static boolean isFullContainer(Container $$0, Direction $$12) {
        return HopperBlockEntity.getSlots($$0, $$12).allMatch($$1 -> {
            ItemStack $$2 = $$0.getItem($$1);
            return $$2.getCount() >= $$2.getMaxStackSize();
        });
    }

    private static boolean isEmptyContainer(Container $$0, Direction $$12) {
        return HopperBlockEntity.getSlots($$0, $$12).allMatch($$1 -> $$0.getItem($$1).isEmpty());
    }

    public static boolean suckInItems(Level $$0, Hopper $$1) {
        Container $$2 = HopperBlockEntity.getSourceContainer($$0, $$1);
        if ($$2 != null) {
            Direction $$32 = Direction.DOWN;
            if (HopperBlockEntity.isEmptyContainer($$2, $$32)) {
                return false;
            }
            return HopperBlockEntity.getSlots($$2, $$32).anyMatch($$3 -> HopperBlockEntity.tryTakeInItemFromSlot($$1, $$2, $$3, $$32));
        }
        for (ItemEntity $$4 : HopperBlockEntity.getItemsAtAndAbove($$0, $$1)) {
            if (!HopperBlockEntity.addItem($$1, $$4)) continue;
            return true;
        }
        return false;
    }

    private static boolean tryTakeInItemFromSlot(Hopper $$0, Container $$1, int $$2, Direction $$3) {
        ItemStack $$4 = $$1.getItem($$2);
        if (!$$4.isEmpty() && HopperBlockEntity.canTakeItemFromContainer($$1, $$4, $$2, $$3)) {
            ItemStack $$5 = $$4.copy();
            ItemStack $$6 = HopperBlockEntity.addItem($$1, $$0, $$1.removeItem($$2, 1), null);
            if ($$6.isEmpty()) {
                $$1.setChanged();
                return true;
            }
            $$1.setItem($$2, $$5);
        }
        return false;
    }

    public static boolean addItem(Container $$0, ItemEntity $$1) {
        boolean $$2 = false;
        ItemStack $$3 = $$1.getItem().copy();
        ItemStack $$4 = HopperBlockEntity.addItem(null, $$0, $$3, null);
        if ($$4.isEmpty()) {
            $$2 = true;
            $$1.discard();
        } else {
            $$1.setItem($$4);
        }
        return $$2;
    }

    public static ItemStack addItem(@Nullable Container $$0, Container $$1, ItemStack $$2, @Nullable Direction $$3) {
        if ($$1 instanceof WorldlyContainer && $$3 != null) {
            WorldlyContainer $$4 = (WorldlyContainer)$$1;
            int[] $$5 = $$4.getSlotsForFace($$3);
            for (int $$6 = 0; $$6 < $$5.length && !$$2.isEmpty(); ++$$6) {
                $$2 = HopperBlockEntity.tryMoveInItem($$0, $$1, $$2, $$5[$$6], $$3);
            }
        } else {
            int $$7 = $$1.getContainerSize();
            for (int $$8 = 0; $$8 < $$7 && !$$2.isEmpty(); ++$$8) {
                $$2 = HopperBlockEntity.tryMoveInItem($$0, $$1, $$2, $$8, $$3);
            }
        }
        return $$2;
    }

    private static boolean canPlaceItemInContainer(Container $$0, ItemStack $$1, int $$2, @Nullable Direction $$3) {
        if (!$$0.canPlaceItem($$2, $$1)) {
            return false;
        }
        return !($$0 instanceof WorldlyContainer) || ((WorldlyContainer)$$0).canPlaceItemThroughFace($$2, $$1, $$3);
    }

    private static boolean canTakeItemFromContainer(Container $$0, ItemStack $$1, int $$2, Direction $$3) {
        return !($$0 instanceof WorldlyContainer) || ((WorldlyContainer)$$0).canTakeItemThroughFace($$2, $$1, $$3);
    }

    private static ItemStack tryMoveInItem(@Nullable Container $$0, Container $$1, ItemStack $$2, int $$3, @Nullable Direction $$4) {
        ItemStack $$5 = $$1.getItem($$3);
        if (HopperBlockEntity.canPlaceItemInContainer($$1, $$2, $$3, $$4)) {
            boolean $$6 = false;
            boolean $$7 = $$1.isEmpty();
            if ($$5.isEmpty()) {
                $$1.setItem($$3, $$2);
                $$2 = ItemStack.EMPTY;
                $$6 = true;
            } else if (HopperBlockEntity.canMergeItems($$5, $$2)) {
                int $$8 = $$2.getMaxStackSize() - $$5.getCount();
                int $$9 = Math.min((int)$$2.getCount(), (int)$$8);
                $$2.shrink($$9);
                $$5.grow($$9);
                boolean bl = $$6 = $$9 > 0;
            }
            if ($$6) {
                HopperBlockEntity $$10;
                if ($$7 && $$1 instanceof HopperBlockEntity && !($$10 = (HopperBlockEntity)$$1).isOnCustomCooldown()) {
                    int $$11 = 0;
                    if ($$0 instanceof HopperBlockEntity) {
                        HopperBlockEntity $$12 = (HopperBlockEntity)$$0;
                        if ($$10.tickedGameTime >= $$12.tickedGameTime) {
                            $$11 = 1;
                        }
                    }
                    $$10.setCooldown(8 - $$11);
                }
                $$1.setChanged();
            }
        }
        return $$2;
    }

    @Nullable
    private static Container getAttachedContainer(Level $$0, BlockPos $$1, BlockState $$2) {
        Direction $$3 = $$2.getValue(HopperBlock.FACING);
        return HopperBlockEntity.getContainerAt($$0, (BlockPos)$$1.relative($$3));
    }

    @Nullable
    private static Container getSourceContainer(Level $$0, Hopper $$1) {
        return HopperBlockEntity.getContainerAt($$0, $$1.getLevelX(), $$1.getLevelY() + 1.0, $$1.getLevelZ());
    }

    public static List<ItemEntity> getItemsAtAndAbove(Level $$0, Hopper $$1) {
        return (List)$$1.getSuckShape().toAabbs().stream().flatMap($$2 -> $$0.getEntitiesOfClass(ItemEntity.class, $$2.move($$1.getLevelX() - 0.5, $$1.getLevelY() - 0.5, $$1.getLevelZ() - 0.5), EntitySelector.ENTITY_STILL_ALIVE).stream()).collect(Collectors.toList());
    }

    @Nullable
    public static Container getContainerAt(Level $$0, BlockPos $$1) {
        return HopperBlockEntity.getContainerAt($$0, (double)$$1.getX() + 0.5, (double)$$1.getY() + 0.5, (double)$$1.getZ() + 0.5);
    }

    @Nullable
    private static Container getContainerAt(Level $$0, double $$1, double $$2, double $$3) {
        List<Entity> $$9;
        BlockEntity $$8;
        Container $$4 = null;
        BlockPos $$5 = new BlockPos($$1, $$2, $$3);
        BlockState $$6 = $$0.getBlockState($$5);
        Block $$7 = $$6.getBlock();
        if ($$7 instanceof WorldlyContainerHolder) {
            $$4 = ((WorldlyContainerHolder)((Object)$$7)).getContainer($$6, $$0, $$5);
        } else if ($$6.hasBlockEntity() && ($$8 = $$0.getBlockEntity($$5)) instanceof Container && ($$4 = (Container)((Object)$$8)) instanceof ChestBlockEntity && $$7 instanceof ChestBlock) {
            $$4 = ChestBlock.getContainer((ChestBlock)$$7, $$6, $$0, $$5, true);
        }
        if ($$4 == null && !($$9 = $$0.getEntities((Entity)null, new AABB($$1 - 0.5, $$2 - 0.5, $$3 - 0.5, $$1 + 0.5, $$2 + 0.5, $$3 + 0.5), EntitySelector.CONTAINER_ENTITY_SELECTOR)).isEmpty()) {
            $$4 = (Container)$$9.get($$0.random.nextInt($$9.size()));
        }
        return $$4;
    }

    private static boolean canMergeItems(ItemStack $$0, ItemStack $$1) {
        if (!$$0.is($$1.getItem())) {
            return false;
        }
        if ($$0.getDamageValue() != $$1.getDamageValue()) {
            return false;
        }
        if ($$0.getCount() > $$0.getMaxStackSize()) {
            return false;
        }
        return ItemStack.tagMatches($$0, $$1);
    }

    @Override
    public double getLevelX() {
        return (double)this.worldPosition.getX() + 0.5;
    }

    @Override
    public double getLevelY() {
        return (double)this.worldPosition.getY() + 0.5;
    }

    @Override
    public double getLevelZ() {
        return (double)this.worldPosition.getZ() + 0.5;
    }

    private void setCooldown(int $$0) {
        this.cooldownTime = $$0;
    }

    private boolean isOnCooldown() {
        return this.cooldownTime > 0;
    }

    private boolean isOnCustomCooldown() {
        return this.cooldownTime > 8;
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> $$0) {
        this.items = $$0;
    }

    public static void entityInside(Level $$0, BlockPos $$1, BlockState $$2, Entity $$3, HopperBlockEntity $$4) {
        if ($$3 instanceof ItemEntity && Shapes.joinIsNotEmpty(Shapes.create($$3.getBoundingBox().move(-$$1.getX(), -$$1.getY(), -$$1.getZ())), $$4.getSuckShape(), BooleanOp.AND)) {
            HopperBlockEntity.tryMoveItems($$0, $$1, $$2, $$4, () -> HopperBlockEntity.addItem($$4, (ItemEntity)$$3));
        }
    }

    @Override
    protected AbstractContainerMenu createMenu(int $$0, Inventory $$1) {
        return new HopperMenu($$0, $$1, this);
    }
}