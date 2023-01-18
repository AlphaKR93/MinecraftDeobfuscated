/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class SmallFireball
extends Fireball {
    public SmallFireball(EntityType<? extends SmallFireball> $$0, Level $$1) {
        super((EntityType<? extends Fireball>)$$0, $$1);
    }

    public SmallFireball(Level $$0, LivingEntity $$1, double $$2, double $$3, double $$4) {
        super((EntityType<? extends Fireball>)EntityType.SMALL_FIREBALL, $$1, $$2, $$3, $$4, $$0);
    }

    public SmallFireball(Level $$0, double $$1, double $$2, double $$3, double $$4, double $$5, double $$6) {
        super((EntityType<? extends Fireball>)EntityType.SMALL_FIREBALL, $$1, $$2, $$3, $$4, $$5, $$6, $$0);
    }

    @Override
    protected void onHitEntity(EntityHitResult $$0) {
        super.onHitEntity($$0);
        if (this.level.isClientSide) {
            return;
        }
        Entity $$1 = $$0.getEntity();
        Entity $$2 = this.getOwner();
        int $$3 = $$1.getRemainingFireTicks();
        $$1.setSecondsOnFire(5);
        if (!$$1.hurt(DamageSource.fireball(this, $$2), 5.0f)) {
            $$1.setRemainingFireTicks($$3);
        } else if ($$2 instanceof LivingEntity) {
            this.doEnchantDamageEffects((LivingEntity)$$2, $$1);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult $$0) {
        Vec3i $$2;
        super.onHitBlock($$0);
        if (this.level.isClientSide) {
            return;
        }
        Entity $$1 = this.getOwner();
        if ((!($$1 instanceof Mob) || this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) && this.level.isEmptyBlock((BlockPos)($$2 = $$0.getBlockPos().relative($$0.getDirection())))) {
            this.level.setBlockAndUpdate((BlockPos)$$2, BaseFireBlock.getState(this.level, (BlockPos)$$2));
        }
    }

    @Override
    protected void onHit(HitResult $$0) {
        super.onHit($$0);
        if (!this.level.isClientSide) {
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
}