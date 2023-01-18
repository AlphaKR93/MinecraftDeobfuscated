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
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class AttackDamageMobEffect
extends MobEffect {
    protected final double multiplier;

    protected AttackDamageMobEffect(MobEffectCategory $$0, int $$1, double $$2) {
        super($$0, $$1);
        this.multiplier = $$2;
    }

    @Override
    public double getAttributeModifierValue(int $$0, AttributeModifier $$1) {
        return this.multiplier * (double)($$0 + 1);
    }
}