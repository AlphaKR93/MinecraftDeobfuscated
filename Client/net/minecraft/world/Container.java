/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.util.Set
 *  java.util.function.Predicate
 */
package net.minecraft.world;

import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.world.Clearable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface Container
extends Clearable {
    public static final int LARGE_MAX_STACK_SIZE = 64;

    public int getContainerSize();

    public boolean isEmpty();

    public ItemStack getItem(int var1);

    public ItemStack removeItem(int var1, int var2);

    public ItemStack removeItemNoUpdate(int var1);

    public void setItem(int var1, ItemStack var2);

    default public int getMaxStackSize() {
        return 64;
    }

    public void setChanged();

    public boolean stillValid(Player var1);

    default public void startOpen(Player $$0) {
    }

    default public void stopOpen(Player $$0) {
    }

    default public boolean canPlaceItem(int $$0, ItemStack $$1) {
        return true;
    }

    default public int countItem(Item $$0) {
        int $$1 = 0;
        for (int $$2 = 0; $$2 < this.getContainerSize(); ++$$2) {
            ItemStack $$3 = this.getItem($$2);
            if (!$$3.getItem().equals($$0)) continue;
            $$1 += $$3.getCount();
        }
        return $$1;
    }

    default public boolean hasAnyOf(Set<Item> $$0) {
        return this.hasAnyMatching((Predicate<ItemStack>)((Predicate)$$1 -> !$$1.isEmpty() && $$0.contains((Object)$$1.getItem())));
    }

    default public boolean hasAnyMatching(Predicate<ItemStack> $$0) {
        for (int $$1 = 0; $$1 < this.getContainerSize(); ++$$1) {
            ItemStack $$2 = this.getItem($$1);
            if (!$$0.test((Object)$$2)) continue;
            return true;
        }
        return false;
    }
}