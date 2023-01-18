/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.entity.projectile;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class SpectralArrow
extends AbstractArrow {
    private int duration = 200;

    public SpectralArrow(EntityType<? extends SpectralArrow> $$0, Level $$1) {
        super((EntityType<? extends AbstractArrow>)$$0, $$1);
    }

    public SpectralArrow(Level $$0, LivingEntity $$1) {
        super(EntityType.SPECTRAL_ARROW, $$1, $$0);
    }

    public SpectralArrow(Level $$0, double $$1, double $$2, double $$3) {
        super(EntityType.SPECTRAL_ARROW, $$1, $$2, $$3, $$0);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide && !this.inGround) {
            this.level.addParticle(ParticleTypes.INSTANT_EFFECT, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
        }
    }

    @Override
    protected ItemStack getPickupItem() {
        return new ItemStack(Items.SPECTRAL_ARROW);
    }

    @Override
    protected void doPostHurtEffects(LivingEntity $$0) {
        super.doPostHurtEffects($$0);
        MobEffectInstance $$1 = new MobEffectInstance(MobEffects.GLOWING, this.duration, 0);
        $$0.addEffect($$1, this.getEffectSource());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$0) {
        super.readAdditionalSaveData($$0);
        if ($$0.contains("Duration")) {
            this.duration = $$0.getInt("Duration");
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        super.addAdditionalSaveData($$0);
        $$0.putInt("Duration", this.duration);
    }
}