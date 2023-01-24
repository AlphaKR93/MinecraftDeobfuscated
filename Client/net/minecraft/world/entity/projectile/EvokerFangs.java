/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.UUID
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.projectile;

import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.level.Level;

public class EvokerFangs
extends Entity
implements TraceableEntity {
    public static final int ATTACK_DURATION = 20;
    public static final int LIFE_OFFSET = 2;
    public static final int ATTACK_TRIGGER_TICKS = 14;
    private int warmupDelayTicks;
    private boolean sentSpikeEvent;
    private int lifeTicks = 22;
    private boolean clientSideAttackStarted;
    @Nullable
    private LivingEntity owner;
    @Nullable
    private UUID ownerUUID;

    public EvokerFangs(EntityType<? extends EvokerFangs> $$0, Level $$1) {
        super($$0, $$1);
    }

    public EvokerFangs(Level $$0, double $$1, double $$2, double $$3, float $$4, int $$5, LivingEntity $$6) {
        this((EntityType<? extends EvokerFangs>)EntityType.EVOKER_FANGS, $$0);
        this.warmupDelayTicks = $$5;
        this.setOwner($$6);
        this.setYRot($$4 * 57.295776f);
        this.setPos($$1, $$2, $$3);
    }

    @Override
    protected void defineSynchedData() {
    }

    public void setOwner(@Nullable LivingEntity $$0) {
        this.owner = $$0;
        this.ownerUUID = $$0 == null ? null : $$0.getUUID();
    }

    @Override
    @Nullable
    public LivingEntity getOwner() {
        Entity $$0;
        if (this.owner == null && this.ownerUUID != null && this.level instanceof ServerLevel && ($$0 = ((ServerLevel)this.level).getEntity(this.ownerUUID)) instanceof LivingEntity) {
            this.owner = (LivingEntity)$$0;
        }
        return this.owner;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag $$0) {
        this.warmupDelayTicks = $$0.getInt("Warmup");
        if ($$0.hasUUID("Owner")) {
            this.ownerUUID = $$0.getUUID("Owner");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag $$0) {
        $$0.putInt("Warmup", this.warmupDelayTicks);
        if (this.ownerUUID != null) {
            $$0.putUUID("Owner", this.ownerUUID);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide) {
            if (this.clientSideAttackStarted) {
                --this.lifeTicks;
                if (this.lifeTicks == 14) {
                    for (int $$0 = 0; $$0 < 12; ++$$0) {
                        double $$1 = this.getX() + (this.random.nextDouble() * 2.0 - 1.0) * (double)this.getBbWidth() * 0.5;
                        double $$2 = this.getY() + 0.05 + this.random.nextDouble();
                        double $$3 = this.getZ() + (this.random.nextDouble() * 2.0 - 1.0) * (double)this.getBbWidth() * 0.5;
                        double $$4 = (this.random.nextDouble() * 2.0 - 1.0) * 0.3;
                        double $$5 = 0.3 + this.random.nextDouble() * 0.3;
                        double $$6 = (this.random.nextDouble() * 2.0 - 1.0) * 0.3;
                        this.level.addParticle(ParticleTypes.CRIT, $$1, $$2 + 1.0, $$3, $$4, $$5, $$6);
                    }
                }
            }
        } else if (--this.warmupDelayTicks < 0) {
            if (this.warmupDelayTicks == -8) {
                List $$7 = this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.2, 0.0, 0.2));
                for (LivingEntity $$8 : $$7) {
                    this.dealDamageTo($$8);
                }
            }
            if (!this.sentSpikeEvent) {
                this.level.broadcastEntityEvent(this, (byte)4);
                this.sentSpikeEvent = true;
            }
            if (--this.lifeTicks < 0) {
                this.discard();
            }
        }
    }

    private void dealDamageTo(LivingEntity $$0) {
        LivingEntity $$1 = this.getOwner();
        if (!$$0.isAlive() || $$0.isInvulnerable() || $$0 == $$1) {
            return;
        }
        if ($$1 == null) {
            $$0.hurt(DamageSource.MAGIC, 6.0f);
        } else {
            if ($$1.isAlliedTo($$0)) {
                return;
            }
            $$0.hurt(DamageSource.indirectMagic(this, $$1), 6.0f);
        }
    }

    @Override
    public void handleEntityEvent(byte $$0) {
        super.handleEntityEvent($$0);
        if ($$0 == 4) {
            this.clientSideAttackStarted = true;
            if (!this.isSilent()) {
                this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.EVOKER_FANGS_ATTACK, this.getSoundSource(), 1.0f, this.random.nextFloat() * 0.2f + 0.85f, false);
            }
        }
    }

    public float getAnimationProgress(float $$0) {
        if (!this.clientSideAttackStarted) {
            return 0.0f;
        }
        int $$1 = this.lifeTicks - 2;
        if ($$1 <= 0) {
            return 1.0f;
        }
        return 1.0f - ((float)$$1 - $$0) / 20.0f;
    }
}