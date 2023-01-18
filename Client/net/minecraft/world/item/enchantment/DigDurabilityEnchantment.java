/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.item.enchantment;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class DigDurabilityEnchantment
extends Enchantment {
    protected DigDurabilityEnchantment(Enchantment.Rarity $$0, EquipmentSlot ... $$1) {
        super($$0, EnchantmentCategory.BREAKABLE, $$1);
    }

    @Override
    public int getMinCost(int $$0) {
        return 5 + ($$0 - 1) * 8;
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
    public boolean canEnchant(ItemStack $$0) {
        if ($$0.isDamageableItem()) {
            return true;
        }
        return super.canEnchant($$0);
    }

    public static boolean shouldIgnoreDurabilityDrop(ItemStack $$0, int $$1, RandomSource $$2) {
        if ($$0.getItem() instanceof ArmorItem && $$2.nextFloat() < 0.6f) {
            return false;
        }
        return $$2.nextInt($$1 + 1) > 0;
    }
}