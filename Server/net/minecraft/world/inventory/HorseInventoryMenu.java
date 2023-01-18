/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class HorseInventoryMenu
extends AbstractContainerMenu {
    private final Container horseContainer;
    private final AbstractHorse horse;

    public HorseInventoryMenu(int $$0, Inventory $$1, Container $$2, final AbstractHorse $$3) {
        super(null, $$0);
        this.horseContainer = $$2;
        this.horse = $$3;
        int $$4 = 3;
        $$2.startOpen($$1.player);
        int $$5 = -18;
        this.addSlot(new Slot($$2, 0, 8, 18){

            @Override
            public boolean mayPlace(ItemStack $$0) {
                return $$0.is(Items.SADDLE) && !this.hasItem() && $$3.isSaddleable();
            }

            @Override
            public boolean isActive() {
                return $$3.isSaddleable();
            }
        });
        this.addSlot(new Slot($$2, 1, 8, 36){

            @Override
            public boolean mayPlace(ItemStack $$0) {
                return $$3.isArmor($$0);
            }

            @Override
            public boolean isActive() {
                return $$3.canWearArmor();
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });
        if (this.hasChest($$3)) {
            for (int $$6 = 0; $$6 < 3; ++$$6) {
                for (int $$7 = 0; $$7 < ((AbstractChestedHorse)$$3).getInventoryColumns(); ++$$7) {
                    this.addSlot(new Slot($$2, 2 + $$7 + $$6 * ((AbstractChestedHorse)$$3).getInventoryColumns(), 80 + $$7 * 18, 18 + $$6 * 18));
                }
            }
        }
        for (int $$8 = 0; $$8 < 3; ++$$8) {
            for (int $$9 = 0; $$9 < 9; ++$$9) {
                this.addSlot(new Slot($$1, $$9 + $$8 * 9 + 9, 8 + $$9 * 18, 102 + $$8 * 18 + -18));
            }
        }
        for (int $$10 = 0; $$10 < 9; ++$$10) {
            this.addSlot(new Slot($$1, $$10, 8 + $$10 * 18, 142));
        }
    }

    @Override
    public boolean stillValid(Player $$0) {
        return !this.horse.hasInventoryChanged(this.horseContainer) && this.horseContainer.stillValid($$0) && this.horse.isAlive() && this.horse.distanceTo($$0) < 8.0f;
    }

    private boolean hasChest(AbstractHorse $$0) {
        return $$0 instanceof AbstractChestedHorse && ((AbstractChestedHorse)$$0).hasChest();
    }

    @Override
    public ItemStack quickMoveStack(Player $$0, int $$1) {
        ItemStack $$2 = ItemStack.EMPTY;
        Slot $$3 = (Slot)this.slots.get($$1);
        if ($$3 != null && $$3.hasItem()) {
            ItemStack $$4 = $$3.getItem();
            $$2 = $$4.copy();
            int $$5 = this.horseContainer.getContainerSize();
            if ($$1 < $$5) {
                if (!this.moveItemStackTo($$4, $$5, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.getSlot(1).mayPlace($$4) && !this.getSlot(1).hasItem()) {
                if (!this.moveItemStackTo($$4, 1, 2, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.getSlot(0).mayPlace($$4)) {
                if (!this.moveItemStackTo($$4, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if ($$5 <= 2 || !this.moveItemStackTo($$4, 2, $$5, false)) {
                int $$7;
                int $$6 = $$5;
                int $$8 = $$7 = $$6 + 27;
                int $$9 = $$8 + 9;
                if ($$1 >= $$8 && $$1 < $$9 ? !this.moveItemStackTo($$4, $$6, $$7, false) : ($$1 >= $$6 && $$1 < $$7 ? !this.moveItemStackTo($$4, $$8, $$9, false) : !this.moveItemStackTo($$4, $$8, $$7, false))) {
                    return ItemStack.EMPTY;
                }
                return ItemStack.EMPTY;
            }
            if ($$4.isEmpty()) {
                $$3.set(ItemStack.EMPTY);
            } else {
                $$3.setChanged();
            }
        }
        return $$2;
    }

    @Override
    public void removed(Player $$0) {
        super.removed($$0);
        this.horseContainer.stopOpen($$0);
    }
}