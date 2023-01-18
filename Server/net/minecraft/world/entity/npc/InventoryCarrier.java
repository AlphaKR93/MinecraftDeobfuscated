/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 */
package net.minecraft.world.entity.npc;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

public interface InventoryCarrier {
    public static final String TAG_INVENTORY = "Inventory";

    public SimpleContainer getInventory();

    public static void pickUpItem(Mob $$0, InventoryCarrier $$1, ItemEntity $$2) {
        ItemStack $$3 = $$2.getItem();
        if ($$0.wantsToPickUp($$3)) {
            SimpleContainer $$4 = $$1.getInventory();
            boolean $$5 = $$4.canAddItem($$3);
            if (!$$5) {
                return;
            }
            $$0.onItemPickup($$2);
            int $$6 = $$3.getCount();
            ItemStack $$7 = $$4.addItem($$3);
            $$0.take($$2, $$6 - $$7.getCount());
            if ($$7.isEmpty()) {
                $$2.discard();
            } else {
                $$3.setCount($$7.getCount());
            }
        }
    }

    default public void readInventoryFromTag(CompoundTag $$0) {
        if ($$0.contains(TAG_INVENTORY, 9)) {
            this.getInventory().fromTag($$0.getList(TAG_INVENTORY, 10));
        }
    }

    default public void writeInventoryToTag(CompoundTag $$0) {
        $$0.put(TAG_INVENTORY, this.getInventory().createTag());
    }
}