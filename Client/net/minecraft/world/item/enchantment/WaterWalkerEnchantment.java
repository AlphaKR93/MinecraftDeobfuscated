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

public class WaterWalkerEnchantment
extends Enchantment {
    public WaterWalkerEnchantment(Enchantment.Rarity $$0, EquipmentSlot ... $$1) {
        super($$0, EnchantmentCategory.ARMOR_FEET, $$1);
    }

    @Override
    public int getMinCost(int $$0) {
        return $$0 * 10;
    }

    @Override
    public int getMaxCost(int $$0) {
        return this.getMinCost($$0) + 15;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean checkCompatibility(Enchantment $$0) {
        return super.checkCompatibility($$0) && $$0 != Enchantments.FROST_WALKER;
    }
}