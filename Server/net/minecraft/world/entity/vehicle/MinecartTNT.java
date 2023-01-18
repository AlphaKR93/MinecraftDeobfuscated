/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.vehicle;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class MinecartTNT
extends AbstractMinecart {
    private static final byte EVENT_PRIME = 10;
    private int fuse = -1;

    public MinecartTNT(EntityType<? extends MinecartTNT> $$0, Level $$1) {
        super($$0, $$1);
    }

    public MinecartTNT(Level $$0, double $$1, double $$2, double $$3) {
        super(EntityType.TNT_MINECART, $$0, $$1, $$2, $$3);
    }

    @Override
    public AbstractMinecart.Type getMinecartType() {
        return AbstractMinecart.Type.TNT;
    }

    @Override
    public BlockState getDefaultDisplayBlockState() {
        return Blocks.TNT.defaultBlockState();
    }

    @Override
    public void tick() {
        double $$0;
        super.tick();
        if (this.fuse > 0) {
            --this.fuse;
            this.level.addParticle(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.5, this.getZ(), 0.0, 0.0, 0.0);
        } else if (this.fuse == 0) {
            this.explode(this.getDeltaMovement().horizontalDistanceSqr());
        }
        if (this.horizontalCollision && ($$0 = this.getDeltaMovement().horizontalDistanceSqr()) >= (double)0.01f) {
            this.explode($$0);
        }
    }

    @Override
    public boolean hurt(DamageSource $$0, float $$1) {
        AbstractArrow $$3;
        Entity $$2 = $$0.getDirectEntity();
        if ($$2 instanceof AbstractArrow && ($$3 = (AbstractArrow)$$2).isOnFire()) {
            DamageSource $$4 = DamageSource.explosion(this, $$0.getEntity());
            this.explode($$4, $$3.getDeltaMovement().lengthSqr());
        }
        return super.hurt($$0, $$1);
    }

    @Override
    public void destroy(DamageSource $$0) {
        double $$1 = this.getDeltaMovement().horizontalDistanceSqr();
        if ($$0.isFire() || $$0.isExplosion() || $$1 >= (double)0.01f) {
            if (this.fuse < 0) {
                this.primeFuse();
                this.fuse = this.random.nextInt(20) + this.random.nextInt(20);
            }
            return;
        }
        super.destroy($$0);
    }

    @Override
    protected Item getDropItem() {
        return Items.TNT_MINECART;
    }

    protected void explode(double $$0) {
        this.explode(null, $$0);
    }

    protected void explode(@Nullable DamageSource $$0, double $$1) {
        if (!this.level.isClientSide) {
            double $$2 = Math.sqrt((double)$$1);
            if ($$2 > 5.0) {
                $$2 = 5.0;
            }
            this.level.explode(this, $$0, null, this.getX(), this.getY(), this.getZ(), (float)(4.0 + this.random.nextDouble() * 1.5 * $$2), false, Level.ExplosionInteraction.TNT);
            this.discard();
        }
    }

    @Override
    public boolean causeFallDamage(float $$0, float $$1, DamageSource $$2) {
        if ($$0 >= 3.0f) {
            float $$3 = $$0 / 10.0f;
            this.explode($$3 * $$3);
        }
        return super.causeFallDamage($$0, $$1, $$2);
    }

    @Override
    public void activateMinecart(int $$0, int $$1, int $$2, boolean $$3) {
        if ($$3 && this.fuse < 0) {
            this.primeFuse();
        }
    }

    @Override
    public void handleEntityEvent(byte $$0) {
        if ($$0 == 10) {
            this.primeFuse();
        } else {
            super.handleEntityEvent($$0);
        }
    }

    public void primeFuse() {
        this.fuse = 80;
        if (!this.level.isClientSide) {
            this.level.broadcastEntityEvent(this, (byte)10);
            if (!this.isSilent()) {
                this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0f, 1.0f);
            }
        }
    }

    public int getFuse() {
        return this.fuse;
    }

    public boolean isPrimed() {
        return this.fuse > -1;
    }

    @Override
    public float getBlockExplosionResistance(Explosion $$0, BlockGetter $$1, BlockPos $$2, BlockState $$3, FluidState $$4, float $$5) {
        if (this.isPrimed() && ($$3.is(BlockTags.RAILS) || $$1.getBlockState((BlockPos)$$2.above()).is(BlockTags.RAILS))) {
            return 0.0f;
        }
        return super.getBlockExplosionResistance($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    public boolean shouldBlockExplode(Explosion $$0, BlockGetter $$1, BlockPos $$2, BlockState $$3, float $$4) {
        if (this.isPrimed() && ($$3.is(BlockTags.RAILS) || $$1.getBlockState((BlockPos)$$2.above()).is(BlockTags.RAILS))) {
            return false;
        }
        return super.shouldBlockExplode($$0, $$1, $$2, $$3, $$4);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag $$0) {
        super.readAdditionalSaveData($$0);
        if ($$0.contains("TNTFuse", 99)) {
            this.fuse = $$0.getInt("TNTFuse");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag $$0) {
        super.addAdditionalSaveData($$0);
        $$0.putInt("TNTFuse", this.fuse);
    }
}