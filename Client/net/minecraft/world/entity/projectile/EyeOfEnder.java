/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Double
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.entity.projectile;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class EyeOfEnder
extends Entity
implements ItemSupplier {
    private static final EntityDataAccessor<ItemStack> DATA_ITEM_STACK = SynchedEntityData.defineId(EyeOfEnder.class, EntityDataSerializers.ITEM_STACK);
    private double tx;
    private double ty;
    private double tz;
    private int life;
    private boolean surviveAfterDeath;

    public EyeOfEnder(EntityType<? extends EyeOfEnder> $$0, Level $$1) {
        super($$0, $$1);
    }

    public EyeOfEnder(Level $$0, double $$1, double $$2, double $$3) {
        this((EntityType<? extends EyeOfEnder>)EntityType.EYE_OF_ENDER, $$0);
        this.setPos($$1, $$2, $$3);
    }

    public void setItem(ItemStack $$02) {
        if (!$$02.is(Items.ENDER_EYE) || $$02.hasTag()) {
            this.getEntityData().set(DATA_ITEM_STACK, Util.make($$02.copy(), $$0 -> $$0.setCount(1)));
        }
    }

    private ItemStack getItemRaw() {
        return this.getEntityData().get(DATA_ITEM_STACK);
    }

    @Override
    public ItemStack getItem() {
        ItemStack $$0 = this.getItemRaw();
        return $$0.isEmpty() ? new ItemStack(Items.ENDER_EYE) : $$0;
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(DATA_ITEM_STACK, ItemStack.EMPTY);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double $$0) {
        double $$1 = this.getBoundingBox().getSize() * 4.0;
        if (Double.isNaN((double)$$1)) {
            $$1 = 4.0;
        }
        return $$0 < ($$1 *= 64.0) * $$1;
    }

    public void signalTo(BlockPos $$0) {
        double $$5;
        double $$1 = $$0.getX();
        int $$2 = $$0.getY();
        double $$3 = $$0.getZ();
        double $$4 = $$1 - this.getX();
        double $$6 = Math.sqrt((double)($$4 * $$4 + ($$5 = $$3 - this.getZ()) * $$5));
        if ($$6 > 12.0) {
            this.tx = this.getX() + $$4 / $$6 * 12.0;
            this.tz = this.getZ() + $$5 / $$6 * 12.0;
            this.ty = this.getY() + 8.0;
        } else {
            this.tx = $$1;
            this.ty = $$2;
            this.tz = $$3;
        }
        this.life = 0;
        this.surviveAfterDeath = this.random.nextInt(5) > 0;
    }

    @Override
    public void lerpMotion(double $$0, double $$1, double $$2) {
        this.setDeltaMovement($$0, $$1, $$2);
        if (this.xRotO == 0.0f && this.yRotO == 0.0f) {
            double $$3 = Math.sqrt((double)($$0 * $$0 + $$2 * $$2));
            this.setYRot((float)(Mth.atan2($$0, $$2) * 57.2957763671875));
            this.setXRot((float)(Mth.atan2($$1, $$3) * 57.2957763671875));
            this.yRotO = this.getYRot();
            this.xRotO = this.getXRot();
        }
    }

    @Override
    public void tick() {
        super.tick();
        Vec3 $$0 = this.getDeltaMovement();
        double $$1 = this.getX() + $$0.x;
        double $$2 = this.getY() + $$0.y;
        double $$3 = this.getZ() + $$0.z;
        double $$4 = $$0.horizontalDistance();
        this.setXRot(Projectile.lerpRotation(this.xRotO, (float)(Mth.atan2($$0.y, $$4) * 57.2957763671875)));
        this.setYRot(Projectile.lerpRotation(this.yRotO, (float)(Mth.atan2($$0.x, $$0.z) * 57.2957763671875)));
        if (!this.level.isClientSide) {
            double $$5 = this.tx - $$1;
            double $$6 = this.tz - $$3;
            float $$7 = (float)Math.sqrt((double)($$5 * $$5 + $$6 * $$6));
            float $$8 = (float)Mth.atan2($$6, $$5);
            double $$9 = Mth.lerp(0.0025, $$4, (double)$$7);
            double $$10 = $$0.y;
            if ($$7 < 1.0f) {
                $$9 *= 0.8;
                $$10 *= 0.8;
            }
            int $$11 = this.getY() < this.ty ? 1 : -1;
            $$0 = new Vec3(Math.cos((double)$$8) * $$9, $$10 + ((double)$$11 - $$10) * (double)0.015f, Math.sin((double)$$8) * $$9);
            this.setDeltaMovement($$0);
        }
        float $$12 = 0.25f;
        if (this.isInWater()) {
            for (int $$13 = 0; $$13 < 4; ++$$13) {
                this.level.addParticle(ParticleTypes.BUBBLE, $$1 - $$0.x * 0.25, $$2 - $$0.y * 0.25, $$3 - $$0.z * 0.25, $$0.x, $$0.y, $$0.z);
            }
        } else {
            this.level.addParticle(ParticleTypes.PORTAL, $$1 - $$0.x * 0.25 + this.random.nextDouble() * 0.6 - 0.3, $$2 - $$0.y * 0.25 - 0.5, $$3 - $$0.z * 0.25 + this.random.nextDouble() * 0.6 - 0.3, $$0.x, $$0.y, $$0.z);
        }
        if (!this.level.isClientSide) {
            this.setPos($$1, $$2, $$3);
            ++this.life;
            if (this.life > 80 && !this.level.isClientSide) {
                this.playSound(SoundEvents.ENDER_EYE_DEATH, 1.0f, 1.0f);
                this.discard();
                if (this.surviveAfterDeath) {
                    this.level.addFreshEntity(new ItemEntity(this.level, this.getX(), this.getY(), this.getZ(), this.getItem()));
                } else {
                    this.level.levelEvent(2003, this.blockPosition(), 0);
                }
            }
        } else {
            this.setPosRaw($$1, $$2, $$3);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        ItemStack $$1 = this.getItemRaw();
        if (!$$1.isEmpty()) {
            $$0.put("Item", $$1.save(new CompoundTag()));
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$0) {
        ItemStack $$1 = ItemStack.of($$0.getCompound("Item"));
        this.setItem($$1);
    }

    @Override
    public float getLightLevelDependentMagicValue() {
        return 1.0f;
    }

    @Override
    public boolean isAttackable() {
        return false;
    }
}