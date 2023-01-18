/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class BookItem
extends Item {
    public BookItem(Item.Properties $$0) {
        super($$0);
    }

    @Override
    public boolean isEnchantable(ItemStack $$0) {
        return $$0.getCount() == 1;
    }

    @Override
    public int getEnchantmentValue() {
        return 1;
    }
}