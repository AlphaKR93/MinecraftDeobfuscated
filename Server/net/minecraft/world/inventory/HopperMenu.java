/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class HopperMenu
extends AbstractContainerMenu {
    public static final int CONTAINER_SIZE = 5;
    private final Container hopper;

    public HopperMenu(int $$0, Inventory $$1) {
        this($$0, $$1, new SimpleContainer(5));
    }

    public HopperMenu(int $$0, Inventory $$1, Container $$2) {
        super(MenuType.HOPPER, $$0);
        this.hopper = $$2;
        HopperMenu.checkContainerSize($$2, 5);
        $$2.startOpen($$1.player);
        int $$3 = 51;
        for (int $$4 = 0; $$4 < 5; ++$$4) {
            this.addSlot(new Slot($$2, $$4, 44 + $$4 * 18, 20));
        }
        for (int $$5 = 0; $$5 < 3; ++$$5) {
            for (int $$6 = 0; $$6 < 9; ++$$6) {
                this.addSlot(new Slot($$1, $$6 + $$5 * 9 + 9, 8 + $$6 * 18, $$5 * 18 + 51));
            }
        }
        for (int $$7 = 0; $$7 < 9; ++$$7) {
            this.addSlot(new Slot($$1, $$7, 8 + $$7 * 18, 109));
        }
    }

    @Override
    public boolean stillValid(Player $$0) {
        return this.hopper.stillValid($$0);
    }

    @Override
    public ItemStack quickMoveStack(Player $$0, int $$1) {
        ItemStack $$2 = ItemStack.EMPTY;
        Slot $$3 = (Slot)this.slots.get($$1);
        if ($$3 != null && $$3.hasItem()) {
            ItemStack $$4 = $$3.getItem();
            $$2 = $$4.copy();
            if ($$1 < this.hopper.getContainerSize() ? !this.moveItemStackTo($$4, this.hopper.getContainerSize(), this.slots.size(), true) : !this.moveItemStackTo($$4, 0, this.hopper.getContainerSize(), false)) {
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
        this.hopper.stopOpen($$0);
    }
}