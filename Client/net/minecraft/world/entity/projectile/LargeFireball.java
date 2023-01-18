/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class LargeFireball
extends Fireball {
    private int explosionPower = 1;

    public LargeFireball(EntityType<? extends LargeFireball> $$0, Level $$1) {
        super((EntityType<? extends Fireball>)$$0, $$1);
    }

    public LargeFireball(Level $$0, LivingEntity $$1, double $$2, double $$3, double $$4, int $$5) {
        super((EntityType<? extends Fireball>)EntityType.FIREBALL, $$1, $$2, $$3, $$4, $$0);
        this.explosionPower = $$5;
    }

    @Override
    protected void onHit(HitResult $$0) {
        super.onHit($$0);
        if (!this.level.isClientSide) {
            boolean $$1 = this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
            this.level.explode((Entity)this, this.getX(), this.getY(), this.getZ(), (float)this.explosionPower, $$1, Level.ExplosionInteraction.MOB);
            this.discard();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult $$0) {
        super.onHitEntity($$0);
        if (this.level.isClientSide) {
            return;
        }
        Entity $$1 = $$0.getEntity();
        Entity $$2 = this.getOwner();
        $$1.hurt(DamageSource.fireball(this, $$2), 6.0f);
        if ($$2 instanceof LivingEntity) {
            this.doEnchantDamageEffects((LivingEntity)$$2, $$1);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        super.addAdditionalSaveData($$0);
        $$0.putByte("ExplosionPower", (byte)this.explosionPower);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$0) {
        super.readAdditionalSaveData($$0);
        if ($$0.contains("ExplosionPower", 99)) {
            this.explosionPower = $$0.getByte("ExplosionPower");
        }
    }
}