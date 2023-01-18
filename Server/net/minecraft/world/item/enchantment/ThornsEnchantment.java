/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Map$Entry
 */
package net.minecraft.world.item.enchantment;

import java.util.Map;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

public class ThornsEnchantment
extends Enchantment {
    private static final float CHANCE_PER_LEVEL = 0.15f;

    public ThornsEnchantment(Enchantment.Rarity $$0, EquipmentSlot ... $$1) {
        super($$0, EnchantmentCategory.ARMOR_CHEST, $$1);
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
        return 3;
    }

    @Override
    public boolean canEnchant(ItemStack $$0) {
        if ($$0.getItem() instanceof ArmorItem) {
            return true;
        }
        return super.canEnchant($$0);
    }

    @Override
    public void doPostHurt(LivingEntity $$0, Entity $$12, int $$2) {
        RandomSource $$3 = $$0.getRandom();
        Map.Entry<EquipmentSlot, ItemStack> $$4 = EnchantmentHelper.getRandomItemWith(Enchantments.THORNS, $$0);
        if (ThornsEnchantment.shouldHit($$2, $$3)) {
            if ($$12 != null) {
                $$12.hurt(DamageSource.thorns($$0), ThornsEnchantment.getDamage($$2, $$3));
            }
            if ($$4 != null) {
                ((ItemStack)$$4.getValue()).hurtAndBreak(2, $$0, $$1 -> $$1.broadcastBreakEvent((EquipmentSlot)((Object)((Object)$$4.getKey()))));
            }
        }
    }

    public static boolean shouldHit(int $$0, RandomSource $$1) {
        if ($$0 <= 0) {
            return false;
        }
        return $$1.nextFloat() < 0.15f * (float)$$0;
    }

    public static int getDamage(int $$0, RandomSource $$1) {
        if ($$0 > 10) {
            return $$0 - 10;
        }
        return 1 + $$1.nextInt(4);
    }
}