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

public class SwiftSneakEnchantment
extends Enchantment {
    public SwiftSneakEnchantment(Enchantment.Rarity $$0, EquipmentSlot ... $$1) {
        super($$0, EnchantmentCategory.ARMOR_LEGS, $$1);
    }

    @Override
    public int getMinCost(int $$0) {
        return $$0 * 25;
    }

    @Override
    public int getMaxCost(int $$0) {
        return this.getMinCost($$0) + 50;
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }

    @Override
    public boolean isTradeable() {
        return false;
    }

    @Override
    public boolean isDiscoverable() {
        return false;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }
}