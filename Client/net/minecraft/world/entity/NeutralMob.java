/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Objects
 *  java.util.UUID
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity;

import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

public interface NeutralMob {
    public static final String TAG_ANGER_TIME = "AngerTime";
    public static final String TAG_ANGRY_AT = "AngryAt";

    public int getRemainingPersistentAngerTime();

    public void setRemainingPersistentAngerTime(int var1);

    @Nullable
    public UUID getPersistentAngerTarget();

    public void setPersistentAngerTarget(@Nullable UUID var1);

    public void startPersistentAngerTimer();

    default public void addPersistentAngerSaveData(CompoundTag $$0) {
        $$0.putInt(TAG_ANGER_TIME, this.getRemainingPersistentAngerTime());
        if (this.getPersistentAngerTarget() != null) {
            $$0.putUUID(TAG_ANGRY_AT, this.getPersistentAngerTarget());
        }
    }

    default public void readPersistentAngerSaveData(Level $$0, CompoundTag $$1) {
        this.setRemainingPersistentAngerTime($$1.getInt(TAG_ANGER_TIME));
        if (!($$0 instanceof ServerLevel)) {
            return;
        }
        if (!$$1.hasUUID(TAG_ANGRY_AT)) {
            this.setPersistentAngerTarget(null);
            return;
        }
        UUID $$2 = $$1.getUUID(TAG_ANGRY_AT);
        this.setPersistentAngerTarget($$2);
        Entity $$3 = ((ServerLevel)$$0).getEntity($$2);
        if ($$3 == null) {
            return;
        }
        if ($$3 instanceof Mob) {
            this.setLastHurtByMob((Mob)$$3);
        }
        if ($$3.getType() == EntityType.PLAYER) {
            this.setLastHurtByPlayer((Player)$$3);
        }
    }

    default public void updatePersistentAnger(ServerLevel $$0, boolean $$1) {
        LivingEntity $$2 = this.getTarget();
        UUID $$3 = this.getPersistentAngerTarget();
        if (($$2 == null || $$2.isDeadOrDying()) && $$3 != null && $$0.getEntity($$3) instanceof Mob) {
            this.stopBeingAngry();
            return;
        }
        if ($$2 != null && !Objects.equals((Object)$$3, (Object)$$2.getUUID())) {
            this.setPersistentAngerTarget($$2.getUUID());
            this.startPersistentAngerTimer();
        }
        if (!(this.getRemainingPersistentAngerTime() <= 0 || $$2 != null && $$2.getType() == EntityType.PLAYER && $$1)) {
            this.setRemainingPersistentAngerTime(this.getRemainingPersistentAngerTime() - 1);
            if (this.getRemainingPersistentAngerTime() == 0) {
                this.stopBeingAngry();
            }
        }
    }

    default public boolean isAngryAt(LivingEntity $$0) {
        if (!this.canAttack($$0)) {
            return false;
        }
        if ($$0.getType() == EntityType.PLAYER && this.isAngryAtAllPlayers($$0.level)) {
            return true;
        }
        return $$0.getUUID().equals((Object)this.getPersistentAngerTarget());
    }

    default public boolean isAngryAtAllPlayers(Level $$0) {
        return $$0.getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER) && this.isAngry() && this.getPersistentAngerTarget() == null;
    }

    default public boolean isAngry() {
        return this.getRemainingPersistentAngerTime() > 0;
    }

    default public void playerDied(Player $$0) {
        if (!$$0.level.getGameRules().getBoolean(GameRules.RULE_FORGIVE_DEAD_PLAYERS)) {
            return;
        }
        if (!$$0.getUUID().equals((Object)this.getPersistentAngerTarget())) {
            return;
        }
        this.stopBeingAngry();
    }

    default public void forgetCurrentTargetAndRefreshUniversalAnger() {
        this.stopBeingAngry();
        this.startPersistentAngerTimer();
    }

    default public void stopBeingAngry() {
        this.setLastHurtByMob(null);
        this.setPersistentAngerTarget(null);
        this.setTarget(null);
        this.setRemainingPersistentAngerTime(0);
    }

    @Nullable
    public LivingEntity getLastHurtByMob();

    public void setLastHurtByMob(@Nullable LivingEntity var1);

    public void setLastHurtByPlayer(@Nullable Player var1);

    public void setTarget(@Nullable LivingEntity var1);

    public boolean canAttack(LivingEntity var1);

    @Nullable
    public LivingEntity getTarget();
}