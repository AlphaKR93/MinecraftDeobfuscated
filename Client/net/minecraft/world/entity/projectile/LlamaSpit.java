/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Predicate
 */
package net.minecraft.world.entity.projectile;

import java.util.function.Predicate;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class LlamaSpit
extends Projectile {
    public LlamaSpit(EntityType<? extends LlamaSpit> $$0, Level $$1) {
        super((EntityType<? extends Projectile>)$$0, $$1);
    }

    public LlamaSpit(Level $$0, Llama $$1) {
        this((EntityType<? extends LlamaSpit>)EntityType.LLAMA_SPIT, $$0);
        this.setOwner($$1);
        this.setPos($$1.getX() - (double)($$1.getBbWidth() + 1.0f) * 0.5 * (double)Mth.sin($$1.yBodyRot * ((float)Math.PI / 180)), $$1.getEyeY() - (double)0.1f, $$1.getZ() + (double)($$1.getBbWidth() + 1.0f) * 0.5 * (double)Mth.cos($$1.yBodyRot * ((float)Math.PI / 180)));
    }

    @Override
    public void tick() {
        super.tick();
        Vec3 $$0 = this.getDeltaMovement();
        HitResult $$1 = ProjectileUtil.getHitResult(this, (Predicate<Entity>)((Predicate)this::canHitEntity));
        this.onHit($$1);
        double $$2 = this.getX() + $$0.x;
        double $$3 = this.getY() + $$0.y;
        double $$4 = this.getZ() + $$0.z;
        this.updateRotation();
        float $$5 = 0.99f;
        float $$6 = 0.06f;
        if (this.level.getBlockStates(this.getBoundingBox()).noneMatch(BlockBehaviour.BlockStateBase::isAir)) {
            this.discard();
            return;
        }
        if (this.isInWaterOrBubble()) {
            this.discard();
            return;
        }
        this.setDeltaMovement($$0.scale(0.99f));
        if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.06f, 0.0));
        }
        this.setPos($$2, $$3, $$4);
    }

    @Override
    protected void onHitEntity(EntityHitResult $$0) {
        super.onHitEntity($$0);
        Entity $$1 = this.getOwner();
        if ($$1 instanceof LivingEntity) {
            $$0.getEntity().hurt(DamageSource.indirectMobAttack(this, (LivingEntity)$$1).setProjectile(), 1.0f);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult $$0) {
        super.onHitBlock($$0);
        if (!this.level.isClientSide) {
            this.discard();
        }
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket $$0) {
        super.recreateFromPacket($$0);
        double $$1 = $$0.getXa();
        double $$2 = $$0.getYa();
        double $$3 = $$0.getZa();
        for (int $$4 = 0; $$4 < 7; ++$$4) {
            double $$5 = 0.4 + 0.1 * (double)$$4;
            this.level.addParticle(ParticleTypes.SPIT, this.getX(), this.getY(), this.getZ(), $$1 * $$5, $$2, $$3 * $$5);
        }
        this.setDeltaMovement($$1, $$2, $$3);
    }
}