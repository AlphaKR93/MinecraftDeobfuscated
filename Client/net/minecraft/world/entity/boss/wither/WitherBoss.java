/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  java.lang.Class
 *  java.lang.Enum
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.EnumSet
 *  java.util.List
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.boss.wither;

import com.google.common.collect.ImmutableList;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.PowerableMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class WitherBoss
extends Monster
implements PowerableMob,
RangedAttackMob {
    private static final EntityDataAccessor<Integer> DATA_TARGET_A = SynchedEntityData.defineId(WitherBoss.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_TARGET_B = SynchedEntityData.defineId(WitherBoss.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_TARGET_C = SynchedEntityData.defineId(WitherBoss.class, EntityDataSerializers.INT);
    private static final List<EntityDataAccessor<Integer>> DATA_TARGETS = ImmutableList.of(DATA_TARGET_A, DATA_TARGET_B, DATA_TARGET_C);
    private static final EntityDataAccessor<Integer> DATA_ID_INV = SynchedEntityData.defineId(WitherBoss.class, EntityDataSerializers.INT);
    private static final int INVULNERABLE_TICKS = 220;
    private final float[] xRotHeads = new float[2];
    private final float[] yRotHeads = new float[2];
    private final float[] xRotOHeads = new float[2];
    private final float[] yRotOHeads = new float[2];
    private final int[] nextHeadUpdate = new int[2];
    private final int[] idleHeadUpdates = new int[2];
    private int destroyBlocksTick;
    private final ServerBossEvent bossEvent = (ServerBossEvent)new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS).setDarkenScreen(true);
    private static final Predicate<LivingEntity> LIVING_ENTITY_SELECTOR = $$0 -> $$0.getMobType() != MobType.UNDEAD && $$0.attackable();
    private static final TargetingConditions TARGETING_CONDITIONS = TargetingConditions.forCombat().range(20.0).selector(LIVING_ENTITY_SELECTOR);

    public WitherBoss(EntityType<? extends WitherBoss> $$0, Level $$1) {
        super((EntityType<? extends Monster>)$$0, $$1);
        this.moveControl = new FlyingMoveControl(this, 10, false);
        this.setHealth(this.getMaxHealth());
        this.xpReward = 50;
    }

    @Override
    protected PathNavigation createNavigation(Level $$0) {
        FlyingPathNavigation $$1 = new FlyingPathNavigation(this, $$0);
        $$1.setCanOpenDoors(false);
        $$1.setCanFloat(true);
        $$1.setCanPassDoors(true);
        return $$1;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new WitherDoNothingGoal());
        this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.0, 40, 20.0f));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomFlyingGoal(this, 1.0));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<LivingEntity>(this, LivingEntity.class, 0, false, false, LIVING_ENTITY_SELECTOR));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_TARGET_A, 0);
        this.entityData.define(DATA_TARGET_B, 0);
        this.entityData.define(DATA_TARGET_C, 0);
        this.entityData.define(DATA_ID_INV, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        super.addAdditionalSaveData($$0);
        $$0.putInt("Invul", this.getInvulnerableTicks());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$0) {
        super.readAdditionalSaveData($$0);
        this.setInvulnerableTicks($$0.getInt("Invul"));
        if (this.hasCustomName()) {
            this.bossEvent.setName(this.getDisplayName());
        }
    }

    @Override
    public void setCustomName(@Nullable Component $$0) {
        super.setCustomName($$0);
        this.bossEvent.setName(this.getDisplayName());
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.WITHER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource $$0) {
        return SoundEvents.WITHER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.WITHER_DEATH;
    }

    @Override
    public void aiStep() {
        Entity $$1;
        Vec3 $$0 = this.getDeltaMovement().multiply(1.0, 0.6, 1.0);
        if (!this.level.isClientSide && this.getAlternativeTarget(0) > 0 && ($$1 = this.level.getEntity(this.getAlternativeTarget(0))) != null) {
            double $$2 = $$0.y;
            if (this.getY() < $$1.getY() || !this.isPowered() && this.getY() < $$1.getY() + 5.0) {
                $$2 = Math.max((double)0.0, (double)$$2);
                $$2 += 0.3 - $$2 * (double)0.6f;
            }
            $$0 = new Vec3($$0.x, $$2, $$0.z);
            Vec3 $$3 = new Vec3($$1.getX() - this.getX(), 0.0, $$1.getZ() - this.getZ());
            if ($$3.horizontalDistanceSqr() > 9.0) {
                Vec3 $$4 = $$3.normalize();
                $$0 = $$0.add($$4.x * 0.3 - $$0.x * 0.6, 0.0, $$4.z * 0.3 - $$0.z * 0.6);
            }
        }
        this.setDeltaMovement($$0);
        if ($$0.horizontalDistanceSqr() > 0.05) {
            this.setYRot((float)Mth.atan2($$0.z, $$0.x) * 57.295776f - 90.0f);
        }
        super.aiStep();
        for (int $$5 = 0; $$5 < 2; ++$$5) {
            this.yRotOHeads[$$5] = this.yRotHeads[$$5];
            this.xRotOHeads[$$5] = this.xRotHeads[$$5];
        }
        for (int $$6 = 0; $$6 < 2; ++$$6) {
            int $$7 = this.getAlternativeTarget($$6 + 1);
            Entity $$8 = null;
            if ($$7 > 0) {
                $$8 = this.level.getEntity($$7);
            }
            if ($$8 != null) {
                double $$9 = this.getHeadX($$6 + 1);
                double $$10 = this.getHeadY($$6 + 1);
                double $$11 = this.getHeadZ($$6 + 1);
                double $$12 = $$8.getX() - $$9;
                double $$13 = $$8.getEyeY() - $$10;
                double $$14 = $$8.getZ() - $$11;
                double $$15 = Math.sqrt((double)($$12 * $$12 + $$14 * $$14));
                float $$16 = (float)(Mth.atan2($$14, $$12) * 57.2957763671875) - 90.0f;
                float $$17 = (float)(-(Mth.atan2($$13, $$15) * 57.2957763671875));
                this.xRotHeads[$$6] = this.rotlerp(this.xRotHeads[$$6], $$17, 40.0f);
                this.yRotHeads[$$6] = this.rotlerp(this.yRotHeads[$$6], $$16, 10.0f);
                continue;
            }
            this.yRotHeads[$$6] = this.rotlerp(this.yRotHeads[$$6], this.yBodyRot, 10.0f);
        }
        boolean $$18 = this.isPowered();
        for (int $$19 = 0; $$19 < 3; ++$$19) {
            double $$20 = this.getHeadX($$19);
            double $$21 = this.getHeadY($$19);
            double $$22 = this.getHeadZ($$19);
            this.level.addParticle(ParticleTypes.SMOKE, $$20 + this.random.nextGaussian() * (double)0.3f, $$21 + this.random.nextGaussian() * (double)0.3f, $$22 + this.random.nextGaussian() * (double)0.3f, 0.0, 0.0, 0.0);
            if (!$$18 || this.level.random.nextInt(4) != 0) continue;
            this.level.addParticle(ParticleTypes.ENTITY_EFFECT, $$20 + this.random.nextGaussian() * (double)0.3f, $$21 + this.random.nextGaussian() * (double)0.3f, $$22 + this.random.nextGaussian() * (double)0.3f, 0.7f, 0.7f, 0.5);
        }
        if (this.getInvulnerableTicks() > 0) {
            for (int $$23 = 0; $$23 < 3; ++$$23) {
                this.level.addParticle(ParticleTypes.ENTITY_EFFECT, this.getX() + this.random.nextGaussian(), this.getY() + (double)(this.random.nextFloat() * 3.3f), this.getZ() + this.random.nextGaussian(), 0.7f, 0.7f, 0.9f);
            }
        }
    }

    @Override
    protected void customServerAiStep() {
        if (this.getInvulnerableTicks() > 0) {
            int $$0 = this.getInvulnerableTicks() - 1;
            this.bossEvent.setProgress(1.0f - (float)$$0 / 220.0f);
            if ($$0 <= 0) {
                this.level.explode((Entity)this, this.getX(), this.getEyeY(), this.getZ(), 7.0f, false, Level.ExplosionInteraction.MOB);
                if (!this.isSilent()) {
                    this.level.globalLevelEvent(1023, this.blockPosition(), 0);
                }
            }
            this.setInvulnerableTicks($$0);
            if (this.tickCount % 10 == 0) {
                this.heal(10.0f);
            }
            return;
        }
        super.customServerAiStep();
        for (int $$1 = 1; $$1 < 3; ++$$1) {
            int $$7;
            if (this.tickCount < this.nextHeadUpdate[$$1 - 1]) continue;
            this.nextHeadUpdate[$$1 - 1] = this.tickCount + 10 + this.random.nextInt(10);
            if (this.level.getDifficulty() == Difficulty.NORMAL || this.level.getDifficulty() == Difficulty.HARD) {
                int n = $$1 - 1;
                int n2 = this.idleHeadUpdates[n];
                this.idleHeadUpdates[n] = n2 + 1;
                if (n2 > 15) {
                    float $$2 = 10.0f;
                    float $$3 = 5.0f;
                    double $$4 = Mth.nextDouble(this.random, this.getX() - 10.0, this.getX() + 10.0);
                    double $$5 = Mth.nextDouble(this.random, this.getY() - 5.0, this.getY() + 5.0);
                    double $$6 = Mth.nextDouble(this.random, this.getZ() - 10.0, this.getZ() + 10.0);
                    this.performRangedAttack($$1 + 1, $$4, $$5, $$6, true);
                    this.idleHeadUpdates[$$1 - 1] = 0;
                }
            }
            if (($$7 = this.getAlternativeTarget($$1)) > 0) {
                LivingEntity $$8 = (LivingEntity)this.level.getEntity($$7);
                if ($$8 == null || !this.canAttack($$8) || this.distanceToSqr($$8) > 900.0 || !this.hasLineOfSight($$8)) {
                    this.setAlternativeTarget($$1, 0);
                    continue;
                }
                this.performRangedAttack($$1 + 1, $$8);
                this.nextHeadUpdate[$$1 - 1] = this.tickCount + 40 + this.random.nextInt(20);
                this.idleHeadUpdates[$$1 - 1] = 0;
                continue;
            }
            List $$9 = this.level.getNearbyEntities(LivingEntity.class, TARGETING_CONDITIONS, this, this.getBoundingBox().inflate(20.0, 8.0, 20.0));
            if ($$9.isEmpty()) continue;
            LivingEntity $$10 = (LivingEntity)$$9.get(this.random.nextInt($$9.size()));
            this.setAlternativeTarget($$1, $$10.getId());
        }
        if (this.getTarget() != null) {
            this.setAlternativeTarget(0, this.getTarget().getId());
        } else {
            this.setAlternativeTarget(0, 0);
        }
        if (this.destroyBlocksTick > 0) {
            --this.destroyBlocksTick;
            if (this.destroyBlocksTick == 0 && this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                int $$11 = Mth.floor(this.getY());
                int $$12 = Mth.floor(this.getX());
                int $$13 = Mth.floor(this.getZ());
                boolean $$14 = false;
                for (int $$15 = -1; $$15 <= 1; ++$$15) {
                    for (int $$16 = -1; $$16 <= 1; ++$$16) {
                        for (int $$17 = 0; $$17 <= 3; ++$$17) {
                            int $$18 = $$12 + $$15;
                            int $$19 = $$11 + $$17;
                            int $$20 = $$13 + $$16;
                            BlockPos $$21 = new BlockPos($$18, $$19, $$20);
                            BlockState $$22 = this.level.getBlockState($$21);
                            if (!WitherBoss.canDestroy($$22)) continue;
                            $$14 = this.level.destroyBlock($$21, true, this) || $$14;
                        }
                    }
                }
                if ($$14) {
                    this.level.levelEvent(null, 1022, this.blockPosition(), 0);
                }
            }
        }
        if (this.tickCount % 20 == 0) {
            this.heal(1.0f);
        }
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
    }

    public static boolean canDestroy(BlockState $$0) {
        return !$$0.isAir() && !$$0.is(BlockTags.WITHER_IMMUNE);
    }

    public void makeInvulnerable() {
        this.setInvulnerableTicks(220);
        this.bossEvent.setProgress(0.0f);
        this.setHealth(this.getMaxHealth() / 3.0f);
    }

    @Override
    public void makeStuckInBlock(BlockState $$0, Vec3 $$1) {
    }

    @Override
    public void startSeenByPlayer(ServerPlayer $$0) {
        super.startSeenByPlayer($$0);
        this.bossEvent.addPlayer($$0);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer $$0) {
        super.stopSeenByPlayer($$0);
        this.bossEvent.removePlayer($$0);
    }

    private double getHeadX(int $$0) {
        if ($$0 <= 0) {
            return this.getX();
        }
        float $$1 = (this.yBodyRot + (float)(180 * ($$0 - 1))) * ((float)Math.PI / 180);
        float $$2 = Mth.cos($$1);
        return this.getX() + (double)$$2 * 1.3;
    }

    private double getHeadY(int $$0) {
        if ($$0 <= 0) {
            return this.getY() + 3.0;
        }
        return this.getY() + 2.2;
    }

    private double getHeadZ(int $$0) {
        if ($$0 <= 0) {
            return this.getZ();
        }
        float $$1 = (this.yBodyRot + (float)(180 * ($$0 - 1))) * ((float)Math.PI / 180);
        float $$2 = Mth.sin($$1);
        return this.getZ() + (double)$$2 * 1.3;
    }

    private float rotlerp(float $$0, float $$1, float $$2) {
        float $$3 = Mth.wrapDegrees($$1 - $$0);
        if ($$3 > $$2) {
            $$3 = $$2;
        }
        if ($$3 < -$$2) {
            $$3 = -$$2;
        }
        return $$0 + $$3;
    }

    private void performRangedAttack(int $$0, LivingEntity $$1) {
        this.performRangedAttack($$0, $$1.getX(), $$1.getY() + (double)$$1.getEyeHeight() * 0.5, $$1.getZ(), $$0 == 0 && this.random.nextFloat() < 0.001f);
    }

    private void performRangedAttack(int $$0, double $$1, double $$2, double $$3, boolean $$4) {
        if (!this.isSilent()) {
            this.level.levelEvent(null, 1024, this.blockPosition(), 0);
        }
        double $$5 = this.getHeadX($$0);
        double $$6 = this.getHeadY($$0);
        double $$7 = this.getHeadZ($$0);
        double $$8 = $$1 - $$5;
        double $$9 = $$2 - $$6;
        double $$10 = $$3 - $$7;
        WitherSkull $$11 = new WitherSkull(this.level, this, $$8, $$9, $$10);
        $$11.setOwner(this);
        if ($$4) {
            $$11.setDangerous(true);
        }
        $$11.setPosRaw($$5, $$6, $$7);
        this.level.addFreshEntity($$11);
    }

    @Override
    public void performRangedAttack(LivingEntity $$0, float $$1) {
        this.performRangedAttack(0, $$0);
    }

    @Override
    public boolean hurt(DamageSource $$0, float $$1) {
        Entity $$2;
        if (this.isInvulnerableTo($$0)) {
            return false;
        }
        if ($$0 == DamageSource.DROWN || $$0.getEntity() instanceof WitherBoss) {
            return false;
        }
        if (this.getInvulnerableTicks() > 0 && $$0 != DamageSource.OUT_OF_WORLD) {
            return false;
        }
        if (this.isPowered() && ($$2 = $$0.getDirectEntity()) instanceof AbstractArrow) {
            return false;
        }
        Entity $$3 = $$0.getEntity();
        if ($$3 != null && !($$3 instanceof Player) && $$3 instanceof LivingEntity && ((LivingEntity)$$3).getMobType() == this.getMobType()) {
            return false;
        }
        if (this.destroyBlocksTick <= 0) {
            this.destroyBlocksTick = 20;
        }
        int $$4 = 0;
        while ($$4 < this.idleHeadUpdates.length) {
            int n = $$4++;
            this.idleHeadUpdates[n] = this.idleHeadUpdates[n] + 3;
        }
        return super.hurt($$0, $$1);
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource $$0, int $$1, boolean $$2) {
        super.dropCustomDeathLoot($$0, $$1, $$2);
        ItemEntity $$3 = this.spawnAtLocation(Items.NETHER_STAR);
        if ($$3 != null) {
            $$3.setExtendedLifetime();
        }
    }

    @Override
    public void checkDespawn() {
        if (this.level.getDifficulty() == Difficulty.PEACEFUL && this.shouldDespawnInPeaceful()) {
            this.discard();
            return;
        }
        this.noActionTime = 0;
    }

    @Override
    public boolean causeFallDamage(float $$0, float $$1, DamageSource $$2) {
        return false;
    }

    @Override
    public boolean addEffect(MobEffectInstance $$0, @Nullable Entity $$1) {
        return false;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 300.0).add(Attributes.MOVEMENT_SPEED, 0.6f).add(Attributes.FLYING_SPEED, 0.6f).add(Attributes.FOLLOW_RANGE, 40.0).add(Attributes.ARMOR, 4.0);
    }

    public float getHeadYRot(int $$0) {
        return this.yRotHeads[$$0];
    }

    public float getHeadXRot(int $$0) {
        return this.xRotHeads[$$0];
    }

    public int getInvulnerableTicks() {
        return this.entityData.get(DATA_ID_INV);
    }

    public void setInvulnerableTicks(int $$0) {
        this.entityData.set(DATA_ID_INV, $$0);
    }

    public int getAlternativeTarget(int $$0) {
        return (Integer)this.entityData.get((EntityDataAccessor)DATA_TARGETS.get($$0));
    }

    public void setAlternativeTarget(int $$0, int $$1) {
        this.entityData.set((EntityDataAccessor)DATA_TARGETS.get($$0), $$1);
    }

    @Override
    public boolean isPowered() {
        return this.getHealth() <= this.getMaxHealth() / 2.0f;
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    @Override
    protected boolean canRide(Entity $$0) {
        return false;
    }

    @Override
    public boolean canChangeDimensions() {
        return false;
    }

    @Override
    public boolean canBeAffected(MobEffectInstance $$0) {
        if ($$0.getEffect() == MobEffects.WITHER) {
            return false;
        }
        return super.canBeAffected($$0);
    }

    class WitherDoNothingGoal
    extends Goal {
        public WitherDoNothingGoal() {
            this.setFlags((EnumSet<Goal.Flag>)EnumSet.of((Enum)Goal.Flag.MOVE, (Enum)Goal.Flag.JUMP, (Enum)Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return WitherBoss.this.getInvulnerableTicks() > 0;
        }
    }
}