/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ShulkerBoxSlot
extends Slot {
    public ShulkerBoxSlot(Container $$0, int $$1, int $$2, int $$3) {
        super($$0, $$1, $$2, $$3);
    }

    @Override
    public boolean mayPlace(ItemStack $$0) {
        return $$0.getItem().canFitInsideContainerItems();
    }
}