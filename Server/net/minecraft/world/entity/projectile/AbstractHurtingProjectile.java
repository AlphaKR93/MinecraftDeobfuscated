/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Double
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Predicate
 */
package net.minecraft.world.entity.projectile;

import java.util.function.Predicate;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractHurtingProjectile
extends Projectile {
    public double xPower;
    public double yPower;
    public double zPower;

    protected AbstractHurtingProjectile(EntityType<? extends AbstractHurtingProjectile> $$0, Level $$1) {
        super((EntityType<? extends Projectile>)$$0, $$1);
    }

    public AbstractHurtingProjectile(EntityType<? extends AbstractHurtingProjectile> $$0, double $$1, double $$2, double $$3, double $$4, double $$5, double $$6, Level $$7) {
        this($$0, $$7);
        this.moveTo($$1, $$2, $$3, this.getYRot(), this.getXRot());
        this.reapplyPosition();
        double $$8 = Math.sqrt((double)($$4 * $$4 + $$5 * $$5 + $$6 * $$6));
        if ($$8 != 0.0) {
            this.xPower = $$4 / $$8 * 0.1;
            this.yPower = $$5 / $$8 * 0.1;
            this.zPower = $$6 / $$8 * 0.1;
        }
    }

    public AbstractHurtingProjectile(EntityType<? extends AbstractHurtingProjectile> $$0, LivingEntity $$1, double $$2, double $$3, double $$4, Level $$5) {
        this($$0, $$1.getX(), $$1.getY(), $$1.getZ(), $$2, $$3, $$4, $$5);
        this.setOwner($$1);
        this.setRot($$1.getYRot(), $$1.getXRot());
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double $$0) {
        double $$1 = this.getBoundingBox().getSize() * 4.0;
        if (Double.isNaN((double)$$1)) {
            $$1 = 4.0;
        }
        return $$0 < ($$1 *= 64.0) * $$1;
    }

    @Override
    public void tick() {
        HitResult $$1;
        Entity $$0 = this.getOwner();
        if (!this.level.isClientSide && ($$0 != null && $$0.isRemoved() || !this.level.hasChunkAt(this.blockPosition()))) {
            this.discard();
            return;
        }
        super.tick();
        if (this.shouldBurn()) {
            this.setSecondsOnFire(1);
        }
        if (($$1 = ProjectileUtil.getHitResult(this, (Predicate<Entity>)((Predicate)this::canHitEntity))).getType() != HitResult.Type.MISS) {
            this.onHit($$1);
        }
        this.checkInsideBlocks();
        Vec3 $$2 = this.getDeltaMovement();
        double $$3 = this.getX() + $$2.x;
        double $$4 = this.getY() + $$2.y;
        double $$5 = this.getZ() + $$2.z;
        ProjectileUtil.rotateTowardsMovement(this, 0.2f);
        float $$6 = this.getInertia();
        if (this.isInWater()) {
            for (int $$7 = 0; $$7 < 4; ++$$7) {
                float $$8 = 0.25f;
                this.level.addParticle(ParticleTypes.BUBBLE, $$3 - $$2.x * 0.25, $$4 - $$2.y * 0.25, $$5 - $$2.z * 0.25, $$2.x, $$2.y, $$2.z);
            }
            $$6 = 0.8f;
        }
        this.setDeltaMovement($$2.add(this.xPower, this.yPower, this.zPower).scale($$6));
        this.level.addParticle(this.getTrailParticle(), $$3, $$4 + 0.5, $$5, 0.0, 0.0, 0.0);
        this.setPos($$3, $$4, $$5);
    }

    @Override
    protected boolean canHitEntity(Entity $$0) {
        return super.canHitEntity($$0) && !$$0.noPhysics;
    }

    protected boolean shouldBurn() {
        return true;
    }

    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.SMOKE;
    }

    protected float getInertia() {
        return 0.95f;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        super.addAdditionalSaveData($$0);
        $$0.put("power", this.newDoubleList(this.xPower, this.yPower, this.zPower));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$0) {
        ListTag $$1;
        super.readAdditionalSaveData($$0);
        if ($$0.contains("power", 9) && ($$1 = $$0.getList("power", 6)).size() == 3) {
            this.xPower = $$1.getDouble(0);
            this.yPower = $$1.getDouble(1);
            this.zPower = $$1.getDouble(2);
        }
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public float getPickRadius() {
        return 1.0f;
    }

    @Override
    public boolean hurt(DamageSource $$0, float $$1) {
        if (this.isInvulnerableTo($$0)) {
            return false;
        }
        this.markHurt();
        Entity $$2 = $$0.getEntity();
        if ($$2 != null) {
            if (!this.level.isClientSide) {
                Vec3 $$3 = $$2.getLookAngle();
                this.setDeltaMovement($$3);
                this.xPower = $$3.x * 0.1;
                this.yPower = $$3.y * 0.1;
                this.zPower = $$3.z * 0.1;
                this.setOwner($$2);
            }
            return true;
        }
        return false;
    }

    @Override
    public float getLightLevelDependentMagicValue() {
        return 1.0f;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        Entity $$0 = this.getOwner();
        int $$1 = $$0 == null ? 0 : $$0.getId();
        return new ClientboundAddEntityPacket(this.getId(), this.getUUID(), this.getX(), this.getY(), this.getZ(), this.getXRot(), this.getYRot(), this.getType(), $$1, new Vec3(this.xPower, this.yPower, this.zPower), 0.0);
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket $$0) {
        super.recreateFromPacket($$0);
        double $$1 = $$0.getXa();
        double $$2 = $$0.getYa();
        double $$3 = $$0.getZa();
        double $$4 = Math.sqrt((double)($$1 * $$1 + $$2 * $$2 + $$3 * $$3));
        if ($$4 != 0.0) {
            this.xPower = $$1 / $$4 * 0.1;
            this.yPower = $$2 / $$4 * 0.1;
            this.zPower = $$3 / $$4 * 0.1;
        }
    }
}