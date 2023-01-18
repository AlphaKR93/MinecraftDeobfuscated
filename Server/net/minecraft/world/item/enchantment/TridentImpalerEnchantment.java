/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class TridentImpalerEnchantment
extends Enchantment {
    public TridentImpalerEnchantment(Enchantment.Rarity $$0, EquipmentSlot ... $$1) {
        super($$0, EnchantmentCategory.TRIDENT, $$1);
    }

    @Override
    public int getMinCost(int $$0) {
        return 1 + ($$0 - 1) * 8;
    }

    @Override
    public int getMaxCost(int $$0) {
        return this.getMinCost($$0) + 20;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public float getDamageBonus(int $$0, MobType $$1) {
        if ($$1 == MobType.WATER) {
            return (float)$$0 * 2.5f;
        }
        return 0.0f;
    }
}