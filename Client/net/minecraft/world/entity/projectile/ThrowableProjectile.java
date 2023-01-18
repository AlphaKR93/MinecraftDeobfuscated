/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Double
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Predicate
 */
package net.minecraft.world.entity.projectile;

import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public abstract class ThrowableProjectile
extends Projectile {
    protected ThrowableProjectile(EntityType<? extends ThrowableProjectile> $$0, Level $$1) {
        super((EntityType<? extends Projectile>)$$0, $$1);
    }

    protected ThrowableProjectile(EntityType<? extends ThrowableProjectile> $$0, double $$1, double $$2, double $$3, Level $$4) {
        this($$0, $$4);
        this.setPos($$1, $$2, $$3);
    }

    protected ThrowableProjectile(EntityType<? extends ThrowableProjectile> $$0, LivingEntity $$1, Level $$2) {
        this($$0, $$1.getX(), $$1.getEyeY() - (double)0.1f, $$1.getZ(), $$2);
        this.setOwner($$1);
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
        float $$12;
        super.tick();
        HitResult $$0 = ProjectileUtil.getHitResult(this, (Predicate<Entity>)((Predicate)this::canHitEntity));
        boolean $$1 = false;
        if ($$0.getType() == HitResult.Type.BLOCK) {
            BlockPos $$2 = ((BlockHitResult)$$0).getBlockPos();
            BlockState $$3 = this.level.getBlockState($$2);
            if ($$3.is(Blocks.NETHER_PORTAL)) {
                this.handleInsidePortal($$2);
                $$1 = true;
            } else if ($$3.is(Blocks.END_GATEWAY)) {
                BlockEntity $$4 = this.level.getBlockEntity($$2);
                if ($$4 instanceof TheEndGatewayBlockEntity && TheEndGatewayBlockEntity.canEntityTeleport(this)) {
                    TheEndGatewayBlockEntity.teleportEntity(this.level, $$2, $$3, this, (TheEndGatewayBlockEntity)$$4);
                }
                $$1 = true;
            }
        }
        if ($$0.getType() != HitResult.Type.MISS && !$$1) {
            this.onHit($$0);
        }
        this.checkInsideBlocks();
        Vec3 $$5 = this.getDeltaMovement();
        double $$6 = this.getX() + $$5.x;
        double $$7 = this.getY() + $$5.y;
        double $$8 = this.getZ() + $$5.z;
        this.updateRotation();
        if (this.isInWater()) {
            for (int $$9 = 0; $$9 < 4; ++$$9) {
                float $$10 = 0.25f;
                this.level.addParticle(ParticleTypes.BUBBLE, $$6 - $$5.x * 0.25, $$7 - $$5.y * 0.25, $$8 - $$5.z * 0.25, $$5.x, $$5.y, $$5.z);
            }
            float $$11 = 0.8f;
        } else {
            $$12 = 0.99f;
        }
        this.setDeltaMovement($$5.scale($$12));
        if (!this.isNoGravity()) {
            Vec3 $$13 = this.getDeltaMovement();
            this.setDeltaMovement($$13.x, $$13.y - (double)this.getGravity(), $$13.z);
        }
        this.setPos($$6, $$7, $$8);
    }

    protected float getGravity() {
        return 0.03f;
    }
}