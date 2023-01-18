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

public class ChestMenu
extends AbstractContainerMenu {
    private static final int SLOTS_PER_ROW = 9;
    private final Container container;
    private final int containerRows;

    private ChestMenu(MenuType<?> $$0, int $$1, Inventory $$2, int $$3) {
        this($$0, $$1, $$2, new SimpleContainer(9 * $$3), $$3);
    }

    public static ChestMenu oneRow(int $$0, Inventory $$1) {
        return new ChestMenu(MenuType.GENERIC_9x1, $$0, $$1, 1);
    }

    public static ChestMenu twoRows(int $$0, Inventory $$1) {
        return new ChestMenu(MenuType.GENERIC_9x2, $$0, $$1, 2);
    }

    public static ChestMenu threeRows(int $$0, Inventory $$1) {
        return new ChestMenu(MenuType.GENERIC_9x3, $$0, $$1, 3);
    }

    public static ChestMenu fourRows(int $$0, Inventory $$1) {
        return new ChestMenu(MenuType.GENERIC_9x4, $$0, $$1, 4);
    }

    public static ChestMenu fiveRows(int $$0, Inventory $$1) {
        return new ChestMenu(MenuType.GENERIC_9x5, $$0, $$1, 5);
    }

    public static ChestMenu sixRows(int $$0, Inventory $$1) {
        return new ChestMenu(MenuType.GENERIC_9x6, $$0, $$1, 6);
    }

    public static ChestMenu threeRows(int $$0, Inventory $$1, Container $$2) {
        return new ChestMenu(MenuType.GENERIC_9x3, $$0, $$1, $$2, 3);
    }

    public static ChestMenu sixRows(int $$0, Inventory $$1, Container $$2) {
        return new ChestMenu(MenuType.GENERIC_9x6, $$0, $$1, $$2, 6);
    }

    public ChestMenu(MenuType<?> $$0, int $$1, Inventory $$2, Container $$3, int $$4) {
        super($$0, $$1);
        ChestMenu.checkContainerSize($$3, $$4 * 9);
        this.container = $$3;
        this.containerRows = $$4;
        $$3.startOpen($$2.player);
        int $$5 = (this.containerRows - 4) * 18;
        for (int $$6 = 0; $$6 < this.containerRows; ++$$6) {
            for (int $$7 = 0; $$7 < 9; ++$$7) {
                this.addSlot(new Slot($$3, $$7 + $$6 * 9, 8 + $$7 * 18, 18 + $$6 * 18));
            }
        }
        for (int $$8 = 0; $$8 < 3; ++$$8) {
            for (int $$9 = 0; $$9 < 9; ++$$9) {
                this.addSlot(new Slot($$2, $$9 + $$8 * 9 + 9, 8 + $$9 * 18, 103 + $$8 * 18 + $$5));
            }
        }
        for (int $$10 = 0; $$10 < 9; ++$$10) {
            this.addSlot(new Slot($$2, $$10, 8 + $$10 * 18, 161 + $$5));
        }
    }

    @Override
    public boolean stillValid(Player $$0) {
        return this.container.stillValid($$0);
    }

    @Override
    public ItemStack quickMoveStack(Player $$0, int $$1) {
        ItemStack $$2 = ItemStack.EMPTY;
        Slot $$3 = (Slot)this.slots.get($$1);
        if ($$3 != null && $$3.hasItem()) {
            ItemStack $$4 = $$3.getItem();
            $$2 = $$4.copy();
            if ($$1 < this.containerRows * 9 ? !this.moveItemStackTo($$4, this.containerRows * 9, this.slots.size(), true) : !this.moveItemStackTo($$4, 0, this.containerRows * 9, false)) {
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
        this.container.stopOpen($$0);
    }

    public Container getContainer() {
        return this.container;
    }

    public int getRowCount() {
        return this.containerRows;
    }
}