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
import net.minecraft.world.item.enchantment.Enchantments;

public class LootBonusEnchantment
extends Enchantment {
    protected LootBonusEnchantment(Enchantment.Rarity $$0, EnchantmentCategory $$1, EquipmentSlot ... $$2) {
        super($$0, $$1, $$2);
    }

    @Override
    public int getMinCost(int $$0) {
        return 15 + ($$0 - 1) * 9;
    }

    @Override
    public int getMaxCost(int $$0) {
        return super.getMinCost($$0) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean checkCompatibility(Enchantment $$0) {
        return super.checkCompatibility($$0) && $$0 != Enchantments.SILK_TOUCH;
    }
}