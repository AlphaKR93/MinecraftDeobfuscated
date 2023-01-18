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

public class AbsoptionMobEffect
extends MobEffect {
    protected AbsoptionMobEffect(MobEffectCategory $$0, int $$1) {
        super($$0, $$1);
    }

    @Override
    public void removeAttributeModifiers(LivingEntity $$0, AttributeMap $$1, int $$2) {
        $$0.setAbsorptionAmount($$0.getAbsorptionAmount() - (float)(4 * ($$2 + 1)));
        super.removeAttributeModifiers($$0, $$1, $$2);
    }

    @Override
    public void addAttributeModifiers(LivingEntity $$0, AttributeMap $$1, int $$2) {
        $$0.setAbsorptionAmount($$0.getAbsorptionAmount() + (float)(4 * ($$2 + 1)));
        super.addAttributeModifiers($$0, $$1, $$2);
    }
}