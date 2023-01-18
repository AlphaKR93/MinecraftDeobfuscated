/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Iterator
 *  java.util.List
 *  java.util.stream.Collectors
 *  javax.annotation.Nullable
 */
package net.minecraft.world;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class SimpleContainer
implements Container,
StackedContentsCompatible {
    private final int size;
    private final NonNullList<ItemStack> items;
    @Nullable
    private List<ContainerListener> listeners;

    public SimpleContainer(int $$0) {
        this.size = $$0;
        this.items = NonNullList.withSize($$0, ItemStack.EMPTY);
    }

    public SimpleContainer(ItemStack ... $$0) {
        this.size = $$0.length;
        this.items = NonNullList.of(ItemStack.EMPTY, $$0);
    }

    public void addListener(ContainerListener $$0) {
        if (this.listeners == null) {
            this.listeners = Lists.newArrayList();
        }
        this.listeners.add((Object)$$0);
    }

    public void removeListener(ContainerListener $$0) {
        if (this.listeners != null) {
            this.listeners.remove((Object)$$0);
        }
    }

    @Override
    public ItemStack getItem(int $$0) {
        if ($$0 < 0 || $$0 >= this.items.size()) {
            return ItemStack.EMPTY;
        }
        return this.items.get($$0);
    }

    public List<ItemStack> removeAllItems() {
        List $$02 = (List)this.items.stream().filter($$0 -> !$$0.isEmpty()).collect(Collectors.toList());
        this.clearContent();
        return $$02;
    }

    @Override
    public ItemStack removeItem(int $$0, int $$1) {
        ItemStack $$2 = ContainerHelper.removeItem(this.items, $$0, $$1);
        if (!$$2.isEmpty()) {
            this.setChanged();
        }
        return $$2;
    }

    public ItemStack removeItemType(Item $$0, int $$1) {
        ItemStack $$2 = new ItemStack($$0, 0);
        for (int $$3 = this.size - 1; $$3 >= 0; --$$3) {
            ItemStack $$4 = this.getItem($$3);
            if (!$$4.getItem().equals($$0)) continue;
            int $$5 = $$1 - $$2.getCount();
            ItemStack $$6 = $$4.split($$5);
            $$2.grow($$6.getCount());
            if ($$2.getCount() == $$1) break;
        }
        if (!$$2.isEmpty()) {
            this.setChanged();
        }
        return $$2;
    }

    public ItemStack addItem(ItemStack $$0) {
        ItemStack $$1 = $$0.copy();
        this.moveItemToOccupiedSlotsWithSameType($$1);
        if ($$1.isEmpty()) {
            return ItemStack.EMPTY;
        }
        this.moveItemToEmptySlots($$1);
        if ($$1.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return $$1;
    }

    public boolean canAddItem(ItemStack $$0) {
        boolean $$1 = false;
        Iterator iterator = this.items.iterator();
        while (iterator.hasNext()) {
            ItemStack $$2 = (ItemStack)iterator.next();
            if (!$$2.isEmpty() && (!ItemStack.isSameItemSameTags($$2, $$0) || $$2.getCount() >= $$2.getMaxStackSize())) continue;
            $$1 = true;
            break;
        }
        return $$1;
    }

    @Override
    public ItemStack removeItemNoUpdate(int $$0) {
        ItemStack $$1 = this.items.get($$0);
        if ($$1.isEmpty()) {
            return ItemStack.EMPTY;
        }
        this.items.set($$0, ItemStack.EMPTY);
        return $$1;
    }

    @Override
    public void setItem(int $$0, ItemStack $$1) {
        this.items.set($$0, $$1);
        if (!$$1.isEmpty() && $$1.getCount() > this.getMaxStackSize()) {
            $$1.setCount(this.getMaxStackSize());
        }
        this.setChanged();
    }

    @Override
    public int getContainerSize() {
        return this.size;
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
    public void setChanged() {
        if (this.listeners != null) {
            for (ContainerListener $$0 : this.listeners) {
                $$0.containerChanged(this);
            }
        }
    }

    @Override
    public boolean stillValid(Player $$0) {
        return true;
    }

    @Override
    public void clearContent() {
        this.items.clear();
        this.setChanged();
    }

    @Override
    public void fillStackedContents(StackedContents $$0) {
        Iterator iterator = this.items.iterator();
        while (iterator.hasNext()) {
            ItemStack $$1 = (ItemStack)iterator.next();
            $$0.accountStack($$1);
        }
    }

    public String toString() {
        return ((List)this.items.stream().filter($$0 -> !$$0.isEmpty()).collect(Collectors.toList())).toString();
    }

    private void moveItemToEmptySlots(ItemStack $$0) {
        for (int $$1 = 0; $$1 < this.size; ++$$1) {
            ItemStack $$2 = this.getItem($$1);
            if (!$$2.isEmpty()) continue;
            this.setItem($$1, $$0.copy());
            $$0.setCount(0);
            return;
        }
    }

    private void moveItemToOccupiedSlotsWithSameType(ItemStack $$0) {
        for (int $$1 = 0; $$1 < this.size; ++$$1) {
            ItemStack $$2 = this.getItem($$1);
            if (!ItemStack.isSameItemSameTags($$2, $$0)) continue;
            this.moveItemsBetweenStacks($$0, $$2);
            if (!$$0.isEmpty()) continue;
            return;
        }
    }

    private void moveItemsBetweenStacks(ItemStack $$0, ItemStack $$1) {
        int $$2 = Math.min((int)this.getMaxStackSize(), (int)$$1.getMaxStackSize());
        int $$3 = Math.min((int)$$0.getCount(), (int)($$2 - $$1.getCount()));
        if ($$3 > 0) {
            $$1.grow($$3);
            $$0.shrink($$3);
            this.setChanged();
        }
    }

    public void fromTag(ListTag $$0) {
        this.clearContent();
        for (int $$1 = 0; $$1 < $$0.size(); ++$$1) {
            ItemStack $$2 = ItemStack.of($$0.getCompound($$1));
            if ($$2.isEmpty()) continue;
            this.addItem($$2);
        }
    }

    public ListTag createTag() {
        ListTag $$0 = new ListTag();
        for (int $$1 = 0; $$1 < this.getContainerSize(); ++$$1) {
            ItemStack $$2 = this.getItem($$1);
            if ($$2.isEmpty()) continue;
            $$0.add($$2.save(new CompoundTag()));
        }
        return $$0;
    }
}