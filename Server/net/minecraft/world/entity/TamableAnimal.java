/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Byte
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.Throwable
 *  java.util.Optional
 *  java.util.UUID
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity;

import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.Team;

public abstract class TamableAnimal
extends Animal
implements OwnableEntity {
    protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(TamableAnimal.class, EntityDataSerializers.BYTE);
    protected static final EntityDataAccessor<Optional<UUID>> DATA_OWNERUUID_ID = SynchedEntityData.defineId(TamableAnimal.class, EntityDataSerializers.OPTIONAL_UUID);
    private boolean orderedToSit;

    protected TamableAnimal(EntityType<? extends TamableAnimal> $$0, Level $$1) {
        super((EntityType<? extends Animal>)$$0, $$1);
        this.reassessTameGoals();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FLAGS_ID, (byte)0);
        this.entityData.define(DATA_OWNERUUID_ID, Optional.empty());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        super.addAdditionalSaveData($$0);
        if (this.getOwnerUUID() != null) {
            $$0.putUUID("Owner", this.getOwnerUUID());
        }
        $$0.putBoolean("Sitting", this.orderedToSit);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$0) {
        UUID $$3;
        super.readAdditionalSaveData($$0);
        if ($$0.hasUUID("Owner")) {
            UUID $$1 = $$0.getUUID("Owner");
        } else {
            String $$2 = $$0.getString("Owner");
            $$3 = OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), $$2);
        }
        if ($$3 != null) {
            try {
                this.setOwnerUUID($$3);
                this.setTame(true);
            }
            catch (Throwable $$4) {
                this.setTame(false);
            }
        }
        this.orderedToSit = $$0.getBoolean("Sitting");
        this.setInSittingPose(this.orderedToSit);
    }

    @Override
    public boolean canBeLeashed(Player $$0) {
        return !this.isLeashed();
    }

    protected void spawnTamingParticles(boolean $$0) {
        SimpleParticleType $$1 = ParticleTypes.HEART;
        if (!$$0) {
            $$1 = ParticleTypes.SMOKE;
        }
        for (int $$2 = 0; $$2 < 7; ++$$2) {
            double $$3 = this.random.nextGaussian() * 0.02;
            double $$4 = this.random.nextGaussian() * 0.02;
            double $$5 = this.random.nextGaussian() * 0.02;
            this.level.addParticle($$1, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), $$3, $$4, $$5);
        }
    }

    @Override
    public void handleEntityEvent(byte $$0) {
        if ($$0 == 7) {
            this.spawnTamingParticles(true);
        } else if ($$0 == 6) {
            this.spawnTamingParticles(false);
        } else {
            super.handleEntityEvent($$0);
        }
    }

    public boolean isTame() {
        return (this.entityData.get(DATA_FLAGS_ID) & 4) != 0;
    }

    public void setTame(boolean $$0) {
        byte $$1 = this.entityData.get(DATA_FLAGS_ID);
        if ($$0) {
            this.entityData.set(DATA_FLAGS_ID, (byte)($$1 | 4));
        } else {
            this.entityData.set(DATA_FLAGS_ID, (byte)($$1 & 0xFFFFFFFB));
        }
        this.reassessTameGoals();
    }

    protected void reassessTameGoals() {
    }

    public boolean isInSittingPose() {
        return (this.entityData.get(DATA_FLAGS_ID) & 1) != 0;
    }

    public void setInSittingPose(boolean $$0) {
        byte $$1 = this.entityData.get(DATA_FLAGS_ID);
        if ($$0) {
            this.entityData.set(DATA_FLAGS_ID, (byte)($$1 | 1));
        } else {
            this.entityData.set(DATA_FLAGS_ID, (byte)($$1 & 0xFFFFFFFE));
        }
    }

    @Override
    @Nullable
    public UUID getOwnerUUID() {
        return (UUID)this.entityData.get(DATA_OWNERUUID_ID).orElse(null);
    }

    public void setOwnerUUID(@Nullable UUID $$0) {
        this.entityData.set(DATA_OWNERUUID_ID, Optional.ofNullable((Object)$$0));
    }

    public void tame(Player $$0) {
        this.setTame(true);
        this.setOwnerUUID($$0.getUUID());
        if ($$0 instanceof ServerPlayer) {
            CriteriaTriggers.TAME_ANIMAL.trigger((ServerPlayer)$$0, this);
        }
    }

    @Override
    @Nullable
    public LivingEntity getOwner() {
        try {
            UUID $$0 = this.getOwnerUUID();
            if ($$0 == null) {
                return null;
            }
            return this.level.getPlayerByUUID($$0);
        }
        catch (IllegalArgumentException $$1) {
            return null;
        }
    }

    @Override
    public boolean canAttack(LivingEntity $$0) {
        if (this.isOwnedBy($$0)) {
            return false;
        }
        return super.canAttack($$0);
    }

    public boolean isOwnedBy(LivingEntity $$0) {
        return $$0 == this.getOwner();
    }

    public boolean wantsToAttack(LivingEntity $$0, LivingEntity $$1) {
        return true;
    }

    @Override
    public Team getTeam() {
        LivingEntity $$0;
        if (this.isTame() && ($$0 = this.getOwner()) != null) {
            return $$0.getTeam();
        }
        return super.getTeam();
    }

    @Override
    public boolean isAlliedTo(Entity $$0) {
        if (this.isTame()) {
            LivingEntity $$1 = this.getOwner();
            if ($$0 == $$1) {
                return true;
            }
            if ($$1 != null) {
                return $$1.isAlliedTo($$0);
            }
        }
        return super.isAlliedTo($$0);
    }

    @Override
    public void die(DamageSource $$0) {
        if (!this.level.isClientSide && this.level.getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES) && this.getOwner() instanceof ServerPlayer) {
            this.getOwner().sendSystemMessage(this.getCombatTracker().getDeathMessage());
        }
        super.die($$0);
    }

    public boolean isOrderedToSit() {
        return this.orderedToSit;
    }

    public void setOrderedToSit(boolean $$0) {
        this.orderedToSit = $$0;
    }
}