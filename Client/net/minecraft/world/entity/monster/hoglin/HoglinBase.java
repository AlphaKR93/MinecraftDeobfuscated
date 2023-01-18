/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.world.entity.monster.hoglin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;

public interface HoglinBase {
    public static final int ATTACK_ANIMATION_DURATION = 10;

    public int getAttackAnimationRemainingTicks();

    public static boolean hurtAndThrowTarget(LivingEntity $$0, LivingEntity $$1) {
        float $$4;
        float $$2 = (float)$$0.getAttributeValue(Attributes.ATTACK_DAMAGE);
        if (!$$0.isBaby() && (int)$$2 > 0) {
            float $$3 = $$2 / 2.0f + (float)$$0.level.random.nextInt((int)$$2);
        } else {
            $$4 = $$2;
        }
        boolean $$5 = $$1.hurt(DamageSource.mobAttack($$0), $$4);
        if ($$5) {
            $$0.doEnchantDamageEffects($$0, $$1);
            if (!$$0.isBaby()) {
                HoglinBase.throwTarget($$0, $$1);
            }
        }
        return $$5;
    }

    public static void throwTarget(LivingEntity $$0, LivingEntity $$1) {
        double $$3;
        double $$2 = $$0.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
        double $$4 = $$2 - ($$3 = $$1.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
        if ($$4 <= 0.0) {
            return;
        }
        double $$5 = $$1.getX() - $$0.getX();
        double $$6 = $$1.getZ() - $$0.getZ();
        float $$7 = $$0.level.random.nextInt(21) - 10;
        double $$8 = $$4 * (double)($$0.level.random.nextFloat() * 0.5f + 0.2f);
        Vec3 $$9 = new Vec3($$5, 0.0, $$6).normalize().scale($$8).yRot($$7);
        double $$10 = $$4 * (double)$$0.level.random.nextFloat() * 0.5;
        $$1.push($$9.x, $$10, $$9.z);
        $$1.hurtMarked = true;
    }
}