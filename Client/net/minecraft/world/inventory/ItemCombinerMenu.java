/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.function.BiConsumer
 *  javax.annotation.Nullable
 */
package net.minecraft.world.inventory;

import java.util.List;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public abstract class ItemCombinerMenu
extends AbstractContainerMenu {
    private static final int INVENTORY_SLOTS_PER_ROW = 9;
    private static final int INVENTORY_SLOTS_PER_COLUMN = 3;
    protected final ContainerLevelAccess access;
    protected final Player player;
    protected final Container inputSlots;
    private final List<Integer> inputSlotIndexes;
    protected final ResultContainer resultSlots = new ResultContainer();
    private final int resultSlotIndex;

    protected abstract boolean mayPickup(Player var1, boolean var2);

    protected abstract void onTake(Player var1, ItemStack var2);

    protected abstract boolean isValidBlock(BlockState var1);

    public ItemCombinerMenu(@Nullable MenuType<?> $$0, int $$1, Inventory $$2, ContainerLevelAccess $$3) {
        super($$0, $$1);
        this.access = $$3;
        this.player = $$2.player;
        ItemCombinerMenuSlotDefinition $$4 = this.createInputSlotDefinitions();
        this.inputSlots = this.createContainer($$4.getNumOfInputSlots());
        this.inputSlotIndexes = $$4.getInputSlotIndexes();
        this.resultSlotIndex = $$4.getResultSlotIndex();
        this.createInputSlots($$4);
        this.createResultSlot($$4);
        this.createInventorySlots($$2);
    }

    private void createInputSlots(ItemCombinerMenuSlotDefinition $$0) {
        for (final ItemCombinerMenuSlotDefinition.SlotDefinition $$1 : $$0.getSlots()) {
            this.addSlot(new Slot(this.inputSlots, $$1.slotIndex(), $$1.x(), $$1.y()){

                @Override
                public boolean mayPlace(ItemStack $$0) {
                    return $$1.mayPlace().test((Object)$$0);
                }
            });
        }
    }

    private void createResultSlot(ItemCombinerMenuSlotDefinition $$0) {
        this.addSlot(new Slot(this.resultSlots, $$0.getResultSlot().slotIndex(), $$0.getResultSlot().x(), $$0.getResultSlot().y()){

            @Override
            public boolean mayPlace(ItemStack $$0) {
                return false;
            }

            @Override
            public boolean mayPickup(Player $$0) {
                return ItemCombinerMenu.this.mayPickup($$0, this.hasItem());
            }

            @Override
            public void onTake(Player $$0, ItemStack $$1) {
                ItemCombinerMenu.this.onTake($$0, $$1);
            }
        });
    }

    private void createInventorySlots(Inventory $$0) {
        for (int $$1 = 0; $$1 < 3; ++$$1) {
            for (int $$2 = 0; $$2 < 9; ++$$2) {
                this.addSlot(new Slot($$0, $$2 + $$1 * 9 + 9, 8 + $$2 * 18, 84 + $$1 * 18));
            }
        }
        for (int $$3 = 0; $$3 < 9; ++$$3) {
            this.addSlot(new Slot($$0, $$3, 8 + $$3 * 18, 142));
        }
    }

    public abstract void createResult();

    protected abstract ItemCombinerMenuSlotDefinition createInputSlotDefinitions();

    private SimpleContainer createContainer(int $$0) {
        return new SimpleContainer($$0){

            @Override
            public void setChanged() {
                super.setChanged();
                ItemCombinerMenu.this.slotsChanged(this);
            }
        };
    }

    @Override
    public void slotsChanged(Container $$0) {
        super.slotsChanged($$0);
        if ($$0 == this.inputSlots) {
            this.createResult();
        }
    }

    @Override
    public void removed(Player $$0) {
        super.removed($$0);
        this.access.execute((BiConsumer<Level, BlockPos>)((BiConsumer)($$1, $$2) -> this.clearContainer($$0, this.inputSlots)));
    }

    @Override
    public boolean stillValid(Player $$0) {
        return this.access.evaluate(($$1, $$2) -> {
            if (!this.isValidBlock($$1.getBlockState((BlockPos)$$2))) {
                return false;
            }
            return $$0.distanceToSqr((double)$$2.getX() + 0.5, (double)$$2.getY() + 0.5, (double)$$2.getZ() + 0.5) <= 64.0;
        }, true);
    }

    @Override
    public ItemStack quickMoveStack(Player $$0, int $$1) {
        ItemStack $$2 = ItemStack.EMPTY;
        Slot $$3 = (Slot)this.slots.get($$1);
        if ($$3 != null && $$3.hasItem()) {
            int $$7;
            ItemStack $$4 = $$3.getItem();
            $$2 = $$4.copy();
            int $$5 = this.getInventorySlotStart();
            int $$6 = this.getUseRowEnd();
            if ($$1 == this.getResultSlot()) {
                if (!this.moveItemStackTo($$4, $$5, $$6, true)) {
                    return ItemStack.EMPTY;
                }
                $$3.onQuickCraft($$4, $$2);
            } else if (this.inputSlotIndexes.contains((Object)$$1) ? !this.moveItemStackTo($$4, $$5, $$6, false) : (this.canMoveIntoInputSlots($$4) && $$1 >= this.getInventorySlotStart() && $$1 < this.getUseRowEnd() ? !this.moveItemStackTo($$4, $$7 = this.getSlotToQuickMoveTo($$2), $$7 + 1, false) : ($$1 >= this.getInventorySlotStart() && $$1 < this.getInventorySlotEnd() ? !this.moveItemStackTo($$4, this.getUseRowStart(), this.getUseRowEnd(), false) : $$1 >= this.getUseRowStart() && $$1 < this.getUseRowEnd() && !this.moveItemStackTo($$4, this.getInventorySlotStart(), this.getInventorySlotEnd(), false)))) {
                return ItemStack.EMPTY;
            }
            if ($$4.isEmpty()) {
                $$3.set(ItemStack.EMPTY);
            } else {
                $$3.setChanged();
            }
            if ($$4.getCount() == $$2.getCount()) {
                return ItemStack.EMPTY;
            }
            $$3.onTake($$0, $$4);
        }
        return $$2;
    }

    protected boolean canMoveIntoInputSlots(ItemStack $$0) {
        return true;
    }

    public int getSlotToQuickMoveTo(ItemStack $$0) {
        return this.inputSlots.isEmpty() ? 0 : (Integer)this.inputSlotIndexes.get(0);
    }

    public int getResultSlot() {
        return this.resultSlotIndex;
    }

    private int getInventorySlotStart() {
        return this.getResultSlot() + 1;
    }

    private int getInventorySlotEnd() {
        return this.getInventorySlotStart() + 27;
    }

    private int getUseRowStart() {
        return this.getInventorySlotEnd();
    }

    private int getUseRowEnd() {
        return this.getUseRowStart() + 9;
    }
}