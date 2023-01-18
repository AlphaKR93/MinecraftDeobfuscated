/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.Optional
 *  java.util.UUID
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 */
package net.minecraft.world.effect;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

public class MobEffect {
    private final Map<Attribute, AttributeModifier> attributeModifiers = Maps.newHashMap();
    private final MobEffectCategory category;
    private final int color;
    @Nullable
    private String descriptionId;
    private Supplier<MobEffectInstance.FactorData> factorDataFactory = () -> null;

    @Nullable
    public static MobEffect byId(int $$0) {
        return (MobEffect)BuiltInRegistries.MOB_EFFECT.byId($$0);
    }

    public static int getId(MobEffect $$0) {
        return BuiltInRegistries.MOB_EFFECT.getId($$0);
    }

    public static int getIdFromNullable(@Nullable MobEffect $$0) {
        return BuiltInRegistries.MOB_EFFECT.getId($$0);
    }

    protected MobEffect(MobEffectCategory $$0, int $$1) {
        this.category = $$0;
        this.color = $$1;
    }

    public Optional<MobEffectInstance.FactorData> createFactorData() {
        return Optional.ofNullable((Object)((MobEffectInstance.FactorData)this.factorDataFactory.get()));
    }

    public void applyEffectTick(LivingEntity $$0, int $$1) {
        if (this == MobEffects.REGENERATION) {
            if ($$0.getHealth() < $$0.getMaxHealth()) {
                $$0.heal(1.0f);
            }
        } else if (this == MobEffects.POISON) {
            if ($$0.getHealth() > 1.0f) {
                $$0.hurt(DamageSource.MAGIC, 1.0f);
            }
        } else if (this == MobEffects.WITHER) {
            $$0.hurt(DamageSource.WITHER, 1.0f);
        } else if (this == MobEffects.HUNGER && $$0 instanceof Player) {
            ((Player)$$0).causeFoodExhaustion(0.005f * (float)($$1 + 1));
        } else if (this == MobEffects.SATURATION && $$0 instanceof Player) {
            if (!$$0.level.isClientSide) {
                ((Player)$$0).getFoodData().eat($$1 + 1, 1.0f);
            }
        } else if (this == MobEffects.HEAL && !$$0.isInvertedHealAndHarm() || this == MobEffects.HARM && $$0.isInvertedHealAndHarm()) {
            $$0.heal(Math.max((int)(4 << $$1), (int)0));
        } else if (this == MobEffects.HARM && !$$0.isInvertedHealAndHarm() || this == MobEffects.HEAL && $$0.isInvertedHealAndHarm()) {
            $$0.hurt(DamageSource.MAGIC, 6 << $$1);
        }
    }

    public void applyInstantenousEffect(@Nullable Entity $$0, @Nullable Entity $$1, LivingEntity $$2, int $$3, double $$4) {
        if (this == MobEffects.HEAL && !$$2.isInvertedHealAndHarm() || this == MobEffects.HARM && $$2.isInvertedHealAndHarm()) {
            int $$5 = (int)($$4 * (double)(4 << $$3) + 0.5);
            $$2.heal($$5);
        } else if (this == MobEffects.HARM && !$$2.isInvertedHealAndHarm() || this == MobEffects.HEAL && $$2.isInvertedHealAndHarm()) {
            int $$6 = (int)($$4 * (double)(6 << $$3) + 0.5);
            if ($$0 == null) {
                $$2.hurt(DamageSource.MAGIC, $$6);
            } else {
                $$2.hurt(DamageSource.indirectMagic($$0, $$1), $$6);
            }
        } else {
            this.applyEffectTick($$2, $$3);
        }
    }

    public boolean isDurationEffectTick(int $$0, int $$1) {
        if (this == MobEffects.REGENERATION) {
            int $$2 = 50 >> $$1;
            if ($$2 > 0) {
                return $$0 % $$2 == 0;
            }
            return true;
        }
        if (this == MobEffects.POISON) {
            int $$3 = 25 >> $$1;
            if ($$3 > 0) {
                return $$0 % $$3 == 0;
            }
            return true;
        }
        if (this == MobEffects.WITHER) {
            int $$4 = 40 >> $$1;
            if ($$4 > 0) {
                return $$0 % $$4 == 0;
            }
            return true;
        }
        return this == MobEffects.HUNGER;
    }

    public boolean isInstantenous() {
        return false;
    }

    protected String getOrCreateDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = Util.makeDescriptionId("effect", BuiltInRegistries.MOB_EFFECT.getKey(this));
        }
        return this.descriptionId;
    }

    public String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }

    public Component getDisplayName() {
        return Component.translatable(this.getDescriptionId());
    }

    public MobEffectCategory getCategory() {
        return this.category;
    }

    public int getColor() {
        return this.color;
    }

    public MobEffect addAttributeModifier(Attribute $$0, String $$1, double $$2, AttributeModifier.Operation $$3) {
        AttributeModifier $$4 = new AttributeModifier(UUID.fromString((String)$$1), (Supplier<String>)((Supplier)this::getDescriptionId), $$2, $$3);
        this.attributeModifiers.put((Object)$$0, (Object)$$4);
        return this;
    }

    public MobEffect setFactorDataFactory(Supplier<MobEffectInstance.FactorData> $$0) {
        this.factorDataFactory = $$0;
        return this;
    }

    public Map<Attribute, AttributeModifier> getAttributeModifiers() {
        return this.attributeModifiers;
    }

    public void removeAttributeModifiers(LivingEntity $$0, AttributeMap $$1, int $$2) {
        for (Map.Entry $$3 : this.attributeModifiers.entrySet()) {
            AttributeInstance $$4 = $$1.getInstance((Attribute)$$3.getKey());
            if ($$4 == null) continue;
            $$4.removeModifier((AttributeModifier)$$3.getValue());
        }
    }

    public void addAttributeModifiers(LivingEntity $$0, AttributeMap $$1, int $$2) {
        for (Map.Entry $$3 : this.attributeModifiers.entrySet()) {
            AttributeInstance $$4 = $$1.getInstance((Attribute)$$3.getKey());
            if ($$4 == null) continue;
            AttributeModifier $$5 = (AttributeModifier)$$3.getValue();
            $$4.removeModifier($$5);
            $$4.addPermanentModifier(new AttributeModifier($$5.getId(), this.getDescriptionId() + " " + $$2, this.getAttributeModifierValue($$2, $$5), $$5.getOperation()));
        }
    }

    public double getAttributeModifierValue(int $$0, AttributeModifier $$1) {
        return $$1.getAmount() * (double)($$0 + 1);
    }

    public boolean isBeneficial() {
        return this.category == MobEffectCategory.BENEFICIAL;
    }
}