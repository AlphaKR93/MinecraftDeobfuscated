/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.util.List
 *  java.util.function.Predicate
 */
package net.minecraft.world;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public class ContainerHelper {
    public static ItemStack removeItem(List<ItemStack> $$0, int $$1, int $$2) {
        if ($$1 < 0 || $$1 >= $$0.size() || ((ItemStack)$$0.get($$1)).isEmpty() || $$2 <= 0) {
            return ItemStack.EMPTY;
        }
        return ((ItemStack)$$0.get($$1)).split($$2);
    }

    public static ItemStack takeItem(List<ItemStack> $$0, int $$1) {
        if ($$1 < 0 || $$1 >= $$0.size()) {
            return ItemStack.EMPTY;
        }
        return (ItemStack)$$0.set($$1, (Object)ItemStack.EMPTY);
    }

    public static CompoundTag saveAllItems(CompoundTag $$0, NonNullList<ItemStack> $$1) {
        return ContainerHelper.saveAllItems($$0, $$1, true);
    }

    public static CompoundTag saveAllItems(CompoundTag $$0, NonNullList<ItemStack> $$1, boolean $$2) {
        ListTag $$3 = new ListTag();
        for (int $$4 = 0; $$4 < $$1.size(); ++$$4) {
            ItemStack $$5 = $$1.get($$4);
            if ($$5.isEmpty()) continue;
            CompoundTag $$6 = new CompoundTag();
            $$6.putByte("Slot", (byte)$$4);
            $$5.save($$6);
            $$3.add($$6);
        }
        if (!$$3.isEmpty() || $$2) {
            $$0.put("Items", $$3);
        }
        return $$0;
    }

    public static void loadAllItems(CompoundTag $$0, NonNullList<ItemStack> $$1) {
        ListTag $$2 = $$0.getList("Items", 10);
        for (int $$3 = 0; $$3 < $$2.size(); ++$$3) {
            CompoundTag $$4 = $$2.getCompound($$3);
            int $$5 = $$4.getByte("Slot") & 0xFF;
            if ($$5 < 0 || $$5 >= $$1.size()) continue;
            $$1.set($$5, ItemStack.of($$4));
        }
    }

    public static int clearOrCountMatchingItems(Container $$0, Predicate<ItemStack> $$1, int $$2, boolean $$3) {
        int $$4 = 0;
        for (int $$5 = 0; $$5 < $$0.getContainerSize(); ++$$5) {
            ItemStack $$6 = $$0.getItem($$5);
            int $$7 = ContainerHelper.clearOrCountMatchingItems($$6, $$1, $$2 - $$4, $$3);
            if ($$7 > 0 && !$$3 && $$6.isEmpty()) {
                $$0.setItem($$5, ItemStack.EMPTY);
            }
            $$4 += $$7;
        }
        return $$4;
    }

    public static int clearOrCountMatchingItems(ItemStack $$0, Predicate<ItemStack> $$1, int $$2, boolean $$3) {
        if ($$0.isEmpty() || !$$1.test((Object)$$0)) {
            return 0;
        }
        if ($$3) {
            return $$0.getCount();
        }
        int $$4 = $$2 < 0 ? $$0.getCount() : Math.min((int)$$2, (int)$$0.getCount());
        $$0.shrink($$4);
        return $$4;
    }
}