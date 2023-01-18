/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 */
package net.minecraft.world.entity.projectile;

import java.util.List;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class DragonFireball
extends AbstractHurtingProjectile {
    public static final float SPLASH_RANGE = 4.0f;

    public DragonFireball(EntityType<? extends DragonFireball> $$0, Level $$1) {
        super((EntityType<? extends AbstractHurtingProjectile>)$$0, $$1);
    }

    public DragonFireball(Level $$0, LivingEntity $$1, double $$2, double $$3, double $$4) {
        super(EntityType.DRAGON_FIREBALL, $$1, $$2, $$3, $$4, $$0);
    }

    @Override
    protected void onHit(HitResult $$0) {
        super.onHit($$0);
        if ($$0.getType() == HitResult.Type.ENTITY && this.ownedBy(((EntityHitResult)$$0).getEntity())) {
            return;
        }
        if (!this.level.isClientSide) {
            List $$1 = this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(4.0, 2.0, 4.0));
            AreaEffectCloud $$2 = new AreaEffectCloud(this.level, this.getX(), this.getY(), this.getZ());
            Entity $$3 = this.getOwner();
            if ($$3 instanceof LivingEntity) {
                $$2.setOwner((LivingEntity)$$3);
            }
            $$2.setParticle(ParticleTypes.DRAGON_BREATH);
            $$2.setRadius(3.0f);
            $$2.setDuration(600);
            $$2.setRadiusPerTick((7.0f - $$2.getRadius()) / (float)$$2.getDuration());
            $$2.addEffect(new MobEffectInstance(MobEffects.HARM, 1, 1));
            if (!$$1.isEmpty()) {
                for (LivingEntity $$4 : $$1) {
                    double $$5 = this.distanceToSqr($$4);
                    if (!($$5 < 16.0)) continue;
                    $$2.setPos($$4.getX(), $$4.getY(), $$4.getZ());
                    break;
                }
            }
            this.level.levelEvent(2006, this.blockPosition(), this.isSilent() ? -1 : 1);
            this.level.addFreshEntity($$2);
            this.discard();
        }
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean hurt(DamageSource $$0, float $$1) {
        return false;
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.DRAGON_BREATH;
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }
}