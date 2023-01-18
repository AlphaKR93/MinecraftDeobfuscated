/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.world.inventory;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public interface ContainerListener {
    public void slotChanged(AbstractContainerMenu var1, int var2, ItemStack var3);

    public void dataChanged(AbstractContainerMenu var1, int var2, int var3);
}