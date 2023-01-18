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

public class SweepingEdgeEnchantment
extends Enchantment {
    public SweepingEdgeEnchantment(Enchantment.Rarity $$0, EquipmentSlot ... $$1) {
        super($$0, EnchantmentCategory.WEAPON, $$1);
    }

    @Override
    public int getMinCost(int $$0) {
        return 5 + ($$0 - 1) * 9;
    }

    @Override
    public int getMaxCost(int $$0) {
        return this.getMinCost($$0) + 15;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    public static float getSweepingDamageRatio(int $$0) {
        return 1.0f - 1.0f / (float)($$0 + 1);
    }
}