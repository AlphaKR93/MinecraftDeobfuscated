/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ForwardingList
 *  java.lang.Object
 *  java.util.List
 */
package net.minecraft.client.player.inventory;

import com.google.common.collect.ForwardingList;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class Hotbar
extends ForwardingList<ItemStack> {
    private final NonNullList<ItemStack> items = NonNullList.withSize(Inventory.getSelectionSize(), ItemStack.EMPTY);

    protected List<ItemStack> delegate() {
        return this.items;
    }

    public ListTag createTag() {
        ListTag $$0 = new ListTag();
        for (ItemStack $$1 : this.delegate()) {
            $$0.add($$1.save(new CompoundTag()));
        }
        return $$0;
    }

    public void fromTag(ListTag $$0) {
        List<ItemStack> $$1 = this.delegate();
        for (int $$2 = 0; $$2 < $$1.size(); ++$$2) {
            $$1.set($$2, (Object)ItemStack.of($$0.getCompound($$2)));
        }
    }

    public boolean isEmpty() {
        for (ItemStack $$0 : this.delegate()) {
            if ($$0.isEmpty()) continue;
            return false;
        }
        return true;
    }
}