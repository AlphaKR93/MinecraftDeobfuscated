/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class FireAspectEnchantment
extends Enchantment {
    protected FireAspectEnchantment(Enchantment.Rarity $$0, EquipmentSlot ... $$1) {
        super($$0, EnchantmentCategory.WEAPON, $$1);
    }

    @Override
    public int getMinCost(int $$0) {
        return 10 + 20 * ($$0 - 1);
    }

    @Override
    public int getMaxCost(int $$0) {
        return super.getMinCost($$0) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }
}