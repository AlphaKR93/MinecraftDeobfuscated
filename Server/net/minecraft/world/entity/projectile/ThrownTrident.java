/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Boolean
 *  java.lang.Byte
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.projectile;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class ThrownTrident
extends AbstractArrow {
    private static final EntityDataAccessor<Byte> ID_LOYALTY = SynchedEntityData.defineId(ThrownTrident.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> ID_FOIL = SynchedEntityData.defineId(ThrownTrident.class, EntityDataSerializers.BOOLEAN);
    private ItemStack tridentItem = new ItemStack(Items.TRIDENT);
    private boolean dealtDamage;
    public int clientSideReturnTridentTickCount;

    public ThrownTrident(EntityType<? extends ThrownTrident> $$0, Level $$1) {
        super((EntityType<? extends AbstractArrow>)$$0, $$1);
    }

    public ThrownTrident(Level $$0, LivingEntity $$1, ItemStack $$2) {
        super(EntityType.TRIDENT, $$1, $$0);
        this.tridentItem = $$2.copy();
        this.entityData.set(ID_LOYALTY, (byte)EnchantmentHelper.getLoyalty($$2));
        this.entityData.set(ID_FOIL, $$2.hasFoil());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ID_LOYALTY, (byte)0);
        this.entityData.define(ID_FOIL, false);
    }

    @Override
    public void tick() {
        if (this.inGroundTime > 4) {
            this.dealtDamage = true;
        }
        Entity $$0 = this.getOwner();
        byte $$1 = this.entityData.get(ID_LOYALTY);
        if ($$1 > 0 && (this.dealtDamage || this.isNoPhysics()) && $$0 != null) {
            if (!this.isAcceptibleReturnOwner()) {
                if (!this.level.isClientSide && this.pickup == AbstractArrow.Pickup.ALLOWED) {
                    this.spawnAtLocation(this.getPickupItem(), 0.1f);
                }
                this.discard();
            } else {
                this.setNoPhysics(true);
                Vec3 $$2 = $$0.getEyePosition().subtract(this.position());
                this.setPosRaw(this.getX(), this.getY() + $$2.y * 0.015 * (double)$$1, this.getZ());
                if (this.level.isClientSide) {
                    this.yOld = this.getY();
                }
                double $$3 = 0.05 * (double)$$1;
                this.setDeltaMovement(this.getDeltaMovement().scale(0.95).add($$2.normalize().scale($$3)));
                if (this.clientSideReturnTridentTickCount == 0) {
                    this.playSound(SoundEvents.TRIDENT_RETURN, 10.0f, 1.0f);
                }
                ++this.clientSideReturnTridentTickCount;
            }
        }
        super.tick();
    }

    private boolean isAcceptibleReturnOwner() {
        Entity $$0 = this.getOwner();
        if ($$0 == null || !$$0.isAlive()) {
            return false;
        }
        return !($$0 instanceof ServerPlayer) || !$$0.isSpectator();
    }

    @Override
    protected ItemStack getPickupItem() {
        return this.tridentItem.copy();
    }

    public boolean isFoil() {
        return this.entityData.get(ID_FOIL);
    }

    @Override
    @Nullable
    protected EntityHitResult findHitEntity(Vec3 $$0, Vec3 $$1) {
        if (this.dealtDamage) {
            return null;
        }
        return super.findHitEntity($$0, $$1);
    }

    @Override
    protected void onHitEntity(EntityHitResult $$0) {
        LightningBolt $$10;
        BlockPos $$9;
        Entity $$4;
        Entity $$1 = $$0.getEntity();
        float $$2 = 8.0f;
        if ($$1 instanceof LivingEntity) {
            LivingEntity $$3 = (LivingEntity)$$1;
            $$2 += EnchantmentHelper.getDamageBonus(this.tridentItem, $$3.getMobType());
        }
        DamageSource $$5 = DamageSource.trident(this, ($$4 = this.getOwner()) == null ? this : $$4);
        this.dealtDamage = true;
        SoundEvent $$6 = SoundEvents.TRIDENT_HIT;
        if ($$1.hurt($$5, $$2)) {
            if ($$1.getType() == EntityType.ENDERMAN) {
                return;
            }
            if ($$1 instanceof LivingEntity) {
                LivingEntity $$7 = (LivingEntity)$$1;
                if ($$4 instanceof LivingEntity) {
                    EnchantmentHelper.doPostHurtEffects($$7, $$4);
                    EnchantmentHelper.doPostDamageEffects((LivingEntity)$$4, $$7);
                }
                this.doPostHurtEffects($$7);
            }
        }
        this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01, -0.1, -0.01));
        float $$8 = 1.0f;
        if (this.level instanceof ServerLevel && this.level.isThundering() && this.isChanneling() && this.level.canSeeSky($$9 = $$1.blockPosition()) && ($$10 = EntityType.LIGHTNING_BOLT.create(this.level)) != null) {
            $$10.moveTo(Vec3.atBottomCenterOf($$9));
            $$10.setCause($$4 instanceof ServerPlayer ? (ServerPlayer)$$4 : null);
            this.level.addFreshEntity($$10);
            $$6 = SoundEvents.TRIDENT_THUNDER;
            $$8 = 5.0f;
        }
        this.playSound($$6, $$8, 1.0f);
    }

    public boolean isChanneling() {
        return EnchantmentHelper.hasChanneling(this.tridentItem);
    }

    @Override
    protected boolean tryPickup(Player $$0) {
        return super.tryPickup($$0) || this.isNoPhysics() && this.ownedBy($$0) && $$0.getInventory().add(this.getPickupItem());
    }

    @Override
    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.TRIDENT_HIT_GROUND;
    }

    @Override
    public void playerTouch(Player $$0) {
        if (this.ownedBy($$0) || this.getOwner() == null) {
            super.playerTouch($$0);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$0) {
        super.readAdditionalSaveData($$0);
        if ($$0.contains("Trident", 10)) {
            this.tridentItem = ItemStack.of($$0.getCompound("Trident"));
        }
        this.dealtDamage = $$0.getBoolean("DealtDamage");
        this.entityData.set(ID_LOYALTY, (byte)EnchantmentHelper.getLoyalty(this.tridentItem));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        super.addAdditionalSaveData($$0);
        $$0.put("Trident", this.tridentItem.save(new CompoundTag()));
        $$0.putBoolean("DealtDamage", this.dealtDamage);
    }

    @Override
    public void tickDespawn() {
        byte $$0 = this.entityData.get(ID_LOYALTY);
        if (this.pickup != AbstractArrow.Pickup.ALLOWED || $$0 <= 0) {
            super.tickDespawn();
        }
    }

    @Override
    protected float getWaterInertia() {
        return 0.99f;
    }

    @Override
    public boolean shouldRender(double $$0, double $$1, double $$2) {
        return true;
    }
}