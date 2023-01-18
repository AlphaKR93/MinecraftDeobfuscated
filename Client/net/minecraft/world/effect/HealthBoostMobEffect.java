/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;

public class HealthBoostMobEffect
extends MobEffect {
    public HealthBoostMobEffect(MobEffectCategory $$0, int $$1) {
        super($$0, $$1);
    }

    @Override
    public void removeAttributeModifiers(LivingEntity $$0, AttributeMap $$1, int $$2) {
        super.removeAttributeModifiers($$0, $$1, $$2);
        if ($$0.getHealth() > $$0.getMaxHealth()) {
            $$0.setHealth($$0.getMaxHealth());
        }
    }
}