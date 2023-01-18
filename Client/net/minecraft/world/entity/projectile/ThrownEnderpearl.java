/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.projectile;

import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class ThrownEnderpearl
extends ThrowableItemProjectile {
    public ThrownEnderpearl(EntityType<? extends ThrownEnderpearl> $$0, Level $$1) {
        super((EntityType<? extends ThrowableItemProjectile>)$$0, $$1);
    }

    public ThrownEnderpearl(Level $$0, LivingEntity $$1) {
        super((EntityType<? extends ThrowableItemProjectile>)EntityType.ENDER_PEARL, $$1, $$0);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.ENDER_PEARL;
    }

    @Override
    protected void onHitEntity(EntityHitResult $$0) {
        super.onHitEntity($$0);
        $$0.getEntity().hurt(DamageSource.thrown(this, this.getOwner()), 0.0f);
    }

    @Override
    protected void onHit(HitResult $$0) {
        super.onHit($$0);
        for (int $$1 = 0; $$1 < 32; ++$$1) {
            this.level.addParticle(ParticleTypes.PORTAL, this.getX(), this.getY() + this.random.nextDouble() * 2.0, this.getZ(), this.random.nextGaussian(), 0.0, this.random.nextGaussian());
        }
        if (!this.level.isClientSide && !this.isRemoved()) {
            Entity $$2 = this.getOwner();
            if ($$2 instanceof ServerPlayer) {
                ServerPlayer $$3 = (ServerPlayer)$$2;
                if ($$3.connection.isAcceptingMessages() && $$3.level == this.level && !$$3.isSleeping()) {
                    Endermite $$4;
                    if (this.random.nextFloat() < 0.05f && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING) && ($$4 = EntityType.ENDERMITE.create(this.level)) != null) {
                        $$4.moveTo($$2.getX(), $$2.getY(), $$2.getZ(), $$2.getYRot(), $$2.getXRot());
                        this.level.addFreshEntity($$4);
                    }
                    if ($$2.isPassenger()) {
                        $$3.dismountTo(this.getX(), this.getY(), this.getZ());
                    } else {
                        $$2.teleportTo(this.getX(), this.getY(), this.getZ());
                    }
                    $$2.resetFallDistance();
                    $$2.hurt(DamageSource.FALL, 5.0f);
                }
            } else if ($$2 != null) {
                $$2.teleportTo(this.getX(), this.getY(), this.getZ());
                $$2.resetFallDistance();
            }
            this.discard();
        }
    }

    @Override
    public void tick() {
        Entity $$0 = this.getOwner();
        if ($$0 instanceof Player && !$$0.isAlive()) {
            this.discard();
        } else {
            super.tick();
        }
    }

    @Override
    @Nullable
    public Entity changeDimension(ServerLevel $$0) {
        Entity $$1 = this.getOwner();
        if ($$1 != null && $$1.level.dimension() != $$0.dimension()) {
            this.setOwner(null);
        }
        return super.changeDimension($$0);
    }
}