/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Iterator
 */
package net.minecraft.world.inventory;

import java.util.Iterator;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;

public class CraftingContainer
implements Container,
StackedContentsCompatible {
    private final NonNullList<ItemStack> items;
    private final int width;
    private final int height;
    private final AbstractContainerMenu menu;

    public CraftingContainer(AbstractContainerMenu $$0, int $$1, int $$2) {
        this.items = NonNullList.withSize($$1 * $$2, ItemStack.EMPTY);
        this.menu = $$0;
        this.width = $$1;
        this.height = $$2;
    }

    @Override
    public int getContainerSize() {
        return this.items.size();
    }

    @Override
    public boolean isEmpty() {
        Iterator iterator = this.items.iterator();
        while (iterator.hasNext()) {
            ItemStack $$0 = (ItemStack)iterator.next();
            if ($$0.isEmpty()) continue;
            return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int $$0) {
        if ($$0 >= this.getContainerSize()) {
            return ItemStack.EMPTY;
        }
        return this.items.get($$0);
    }

    @Override
    public ItemStack removeItemNoUpdate(int $$0) {
        return ContainerHelper.takeItem(this.items, $$0);
    }

    @Override
    public ItemStack removeItem(int $$0, int $$1) {
        ItemStack $$2 = ContainerHelper.removeItem(this.items, $$0, $$1);
        if (!$$2.isEmpty()) {
            this.menu.slotsChanged(this);
        }
        return $$2;
    }

    @Override
    public void setItem(int $$0, ItemStack $$1) {
        this.items.set($$0, $$1);
        this.menu.slotsChanged(this);
    }

    @Override
    public void setChanged() {
    }

    @Override
    public boolean stillValid(Player $$0) {
        return true;
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    @Override
    public void fillStackedContents(StackedContents $$0) {
        Iterator iterator = this.items.iterator();
        while (iterator.hasNext()) {
            ItemStack $$1 = (ItemStack)iterator.next();
            $$0.accountSimpleStack($$1);
        }
    }
}