/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 */
package net.minecraft.world.item.enchantment;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class DamageEnchantment
extends Enchantment {
    public static final int ALL = 0;
    public static final int UNDEAD = 1;
    public static final int ARTHROPODS = 2;
    private static final String[] NAMES = new String[]{"all", "undead", "arthropods"};
    private static final int[] MIN_COST = new int[]{1, 5, 5};
    private static final int[] LEVEL_COST = new int[]{11, 8, 8};
    private static final int[] LEVEL_COST_SPAN = new int[]{20, 20, 20};
    public final int type;

    public DamageEnchantment(Enchantment.Rarity $$0, int $$1, EquipmentSlot ... $$2) {
        super($$0, EnchantmentCategory.WEAPON, $$2);
        this.type = $$1;
    }

    @Override
    public int getMinCost(int $$0) {
        return MIN_COST[this.type] + ($$0 - 1) * LEVEL_COST[this.type];
    }

    @Override
    public int getMaxCost(int $$0) {
        return this.getMinCost($$0) + LEVEL_COST_SPAN[this.type];
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public float getDamageBonus(int $$0, MobType $$1) {
        if (this.type == 0) {
            return 1.0f + (float)Math.max((int)0, (int)($$0 - 1)) * 0.5f;
        }
        if (this.type == 1 && $$1 == MobType.UNDEAD) {
            return (float)$$0 * 2.5f;
        }
        if (this.type == 2 && $$1 == MobType.ARTHROPOD) {
            return (float)$$0 * 2.5f;
        }
        return 0.0f;
    }

    @Override
    public boolean checkCompatibility(Enchantment $$0) {
        return !($$0 instanceof DamageEnchantment);
    }

    @Override
    public boolean canEnchant(ItemStack $$0) {
        if ($$0.getItem() instanceof AxeItem) {
            return true;
        }
        return super.canEnchant($$0);
    }

    @Override
    public void doPostAttack(LivingEntity $$0, Entity $$1, int $$2) {
        if ($$1 instanceof LivingEntity) {
            LivingEntity $$3 = (LivingEntity)$$1;
            if (this.type == 2 && $$2 > 0 && $$3.getMobType() == MobType.ARTHROPOD) {
                int $$4 = 20 + $$0.getRandom().nextInt(10 * $$2);
                $$3.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, $$4, 3));
            }
        }
    }
}