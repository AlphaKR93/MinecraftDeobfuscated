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
import net.minecraft.world.item.Tier;

public class TieredItem
extends Item {
    private final Tier tier;

    public TieredItem(Tier $$0, Item.Properties $$1) {
        super($$1.defaultDurability($$0.getUses()));
        this.tier = $$0;
    }

    public Tier getTier() {
        return this.tier;
    }

    @Override
    public int getEnchantmentValue() {
        return this.tier.getEnchantmentValue();
    }

    @Override
    public boolean isValidRepairItem(ItemStack $$0, ItemStack $$1) {
        return this.tier.getRepairIngredient().test($$1) || super.isValidRepairItem($$0, $$1);
    }
}