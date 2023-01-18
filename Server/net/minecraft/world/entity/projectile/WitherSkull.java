/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Boolean
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class WitherSkull
extends AbstractHurtingProjectile {
    private static final EntityDataAccessor<Boolean> DATA_DANGEROUS = SynchedEntityData.defineId(WitherSkull.class, EntityDataSerializers.BOOLEAN);

    public WitherSkull(EntityType<? extends WitherSkull> $$0, Level $$1) {
        super((EntityType<? extends AbstractHurtingProjectile>)$$0, $$1);
    }

    public WitherSkull(Level $$0, LivingEntity $$1, double $$2, double $$3, double $$4) {
        super(EntityType.WITHER_SKULL, $$1, $$2, $$3, $$4, $$0);
    }

    @Override
    protected float getInertia() {
        return this.isDangerous() ? 0.73f : super.getInertia();
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    public float getBlockExplosionResistance(Explosion $$0, BlockGetter $$1, BlockPos $$2, BlockState $$3, FluidState $$4, float $$5) {
        if (this.isDangerous() && WitherBoss.canDestroy($$3)) {
            return Math.min((float)0.8f, (float)$$5);
        }
        return $$5;
    }

    @Override
    protected void onHitEntity(EntityHitResult $$0) {
        boolean $$5;
        super.onHitEntity($$0);
        if (this.level.isClientSide) {
            return;
        }
        Entity $$1 = $$0.getEntity();
        Entity $$2 = this.getOwner();
        if ($$2 instanceof LivingEntity) {
            LivingEntity $$3 = (LivingEntity)$$2;
            boolean $$4 = $$1.hurt(DamageSource.witherSkull(this, $$3), 8.0f);
            if ($$4) {
                if ($$1.isAlive()) {
                    this.doEnchantDamageEffects($$3, $$1);
                } else {
                    $$3.heal(5.0f);
                }
            }
        } else {
            $$5 = $$1.hurt(DamageSource.MAGIC, 5.0f);
        }
        if ($$5 && $$1 instanceof LivingEntity) {
            int $$6 = 0;
            if (this.level.getDifficulty() == Difficulty.NORMAL) {
                $$6 = 10;
            } else if (this.level.getDifficulty() == Difficulty.HARD) {
                $$6 = 40;
            }
            if ($$6 > 0) {
                ((LivingEntity)$$1).addEffect(new MobEffectInstance(MobEffects.WITHER, 20 * $$6, 1), this.getEffectSource());
            }
        }
    }

    @Override
    protected void onHit(HitResult $$0) {
        super.onHit($$0);
        if (!this.level.isClientSide) {
            this.level.explode((Entity)this, this.getX(), this.getY(), this.getZ(), 1.0f, false, Level.ExplosionInteraction.MOB);
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
    protected void defineSynchedData() {
        this.entityData.define(DATA_DANGEROUS, false);
    }

    public boolean isDangerous() {
        return this.entityData.get(DATA_DANGEROUS);
    }

    public void setDangerous(boolean $$0) {
        this.entityData.set(DATA_DANGEROUS, $$0);
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }
}