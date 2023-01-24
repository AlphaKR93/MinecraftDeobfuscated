/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.item.enchantment;

import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

public class ProtectionEnchantment
extends Enchantment {
    public final Type type;

    public ProtectionEnchantment(Enchantment.Rarity $$0, Type $$1, EquipmentSlot ... $$2) {
        super($$0, $$1 == Type.FALL ? EnchantmentCategory.ARMOR_FEET : EnchantmentCategory.ARMOR, $$2);
        this.type = $$1;
    }

    @Override
    public int getMinCost(int $$0) {
        return this.type.getMinCost() + ($$0 - 1) * this.type.getLevelCost();
    }

    @Override
    public int getMaxCost(int $$0) {
        return this.getMinCost($$0) + this.type.getLevelCost();
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }

    @Override
    public int getDamageProtection(int $$0, DamageSource $$1) {
        if ($$1.isBypassInvul()) {
            return 0;
        }
        if (this.type == Type.ALL) {
            return $$0;
        }
        if (this.type == Type.FIRE && $$1.isFire()) {
            return $$0 * 2;
        }
        if (this.type == Type.FALL && $$1.isFall()) {
            return $$0 * 3;
        }
        if (this.type == Type.EXPLOSION && $$1.isExplosion()) {
            return $$0 * 2;
        }
        if (this.type == Type.PROJECTILE && $$1.isProjectile()) {
            return $$0 * 2;
        }
        return 0;
    }

    @Override
    public boolean checkCompatibility(Enchantment $$0) {
        if ($$0 instanceof ProtectionEnchantment) {
            ProtectionEnchantment $$1 = (ProtectionEnchantment)$$0;
            if (this.type == $$1.type) {
                return false;
            }
            return this.type == Type.FALL || $$1.type == Type.FALL;
        }
        return super.checkCompatibility($$0);
    }

    public static int getFireAfterDampener(LivingEntity $$0, int $$1) {
        int $$2 = EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_PROTECTION, $$0);
        if ($$2 > 0) {
            $$1 -= Mth.floor((float)$$1 * ((float)$$2 * 0.15f));
        }
        return $$1;
    }

    public static double getExplosionKnockbackAfterDampener(LivingEntity $$0, double $$1) {
        int $$2 = EnchantmentHelper.getEnchantmentLevel(Enchantments.BLAST_PROTECTION, $$0);
        if ($$2 > 0) {
            $$1 *= Mth.clamp(1.0 - (double)$$2 * 0.15, 0.0, 1.0);
        }
        return $$1;
    }

    public static enum Type {
        ALL(1, 11),
        FIRE(10, 8),
        FALL(5, 6),
        EXPLOSION(5, 8),
        PROJECTILE(3, 6);

        private final int minCost;
        private final int levelCost;

        private Type(int $$0, int $$1) {
            this.minCost = $$0;
            this.levelCost = $$1;
        }

        public int getMinCost() {
            return this.minCost;
        }

        public int getLevelCost() {
            return this.levelCost;
        }
    }
}