/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Class
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.monster;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class Ravager
extends Raider {
    private static final Predicate<Entity> NO_RAVAGER_AND_ALIVE = $$0 -> $$0.isAlive() && !($$0 instanceof Ravager);
    private static final double BASE_MOVEMENT_SPEED = 0.3;
    private static final double ATTACK_MOVEMENT_SPEED = 0.35;
    private static final int STUNNED_COLOR = 8356754;
    private static final double STUNNED_COLOR_BLUE = 0.5725490196078431;
    private static final double STUNNED_COLOR_GREEN = 0.5137254901960784;
    private static final double STUNNED_COLOR_RED = 0.4980392156862745;
    private static final int ATTACK_DURATION = 10;
    public static final int STUN_DURATION = 40;
    private int attackTick;
    private int stunnedTick;
    private int roarTick;

    public Ravager(EntityType<? extends Ravager> $$0, Level $$1) {
        super((EntityType<? extends Raider>)$$0, $$1);
        this.maxUpStep = 1.0f;
        this.xpReward = 20;
        this.setPathfindingMalus(BlockPathTypes.LEAVES, 0.0f);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(4, new RavagerMeleeAttackGoal());
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.4));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0f));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0f));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this, Raider.class).setAlertOthers(new Class[0]));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<Player>((Mob)this, Player.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<AbstractVillager>((Mob)this, AbstractVillager.class, true, (Predicate<LivingEntity>)((Predicate)$$0 -> !$$0.isBaby())));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<IronGolem>((Mob)this, IronGolem.class, true));
    }

    @Override
    protected void updateControlFlags() {
        boolean $$0 = !(this.getControllingPassenger() instanceof Mob) || this.getControllingPassenger().getType().is(EntityTypeTags.RAIDERS);
        boolean $$1 = !(this.getVehicle() instanceof Boat);
        this.goalSelector.setControlFlag(Goal.Flag.MOVE, $$0);
        this.goalSelector.setControlFlag(Goal.Flag.JUMP, $$0 && $$1);
        this.goalSelector.setControlFlag(Goal.Flag.LOOK, $$0);
        this.goalSelector.setControlFlag(Goal.Flag.TARGET, $$0);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 100.0).add(Attributes.MOVEMENT_SPEED, 0.3).add(Attributes.KNOCKBACK_RESISTANCE, 0.75).add(Attributes.ATTACK_DAMAGE, 12.0).add(Attributes.ATTACK_KNOCKBACK, 1.5).add(Attributes.FOLLOW_RANGE, 32.0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        super.addAdditionalSaveData($$0);
        $$0.putInt("AttackTick", this.attackTick);
        $$0.putInt("StunTick", this.stunnedTick);
        $$0.putInt("RoarTick", this.roarTick);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$0) {
        super.readAdditionalSaveData($$0);
        this.attackTick = $$0.getInt("AttackTick");
        this.stunnedTick = $$0.getInt("StunTick");
        this.roarTick = $$0.getInt("RoarTick");
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return SoundEvents.RAVAGER_CELEBRATE;
    }

    @Override
    public int getMaxHeadYRot() {
        return 45;
    }

    @Override
    public double getPassengersRidingOffset() {
        return 2.1;
    }

    @Override
    @Nullable
    public Entity getControllingPassenger() {
        Entity $$0 = this.getFirstPassenger();
        return $$0 != null && this.canBeControlledBy($$0) ? $$0 : null;
    }

    private boolean canBeControlledBy(Entity $$0) {
        return !this.isNoAi() && $$0 instanceof LivingEntity;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.isAlive()) {
            return;
        }
        if (this.isImmobile()) {
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0);
        } else {
            double $$0 = this.getTarget() != null ? 0.35 : 0.3;
            double $$1 = this.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue();
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(Mth.lerp(0.1, $$1, $$0));
        }
        if (this.horizontalCollision && this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            boolean $$2 = false;
            AABB $$3 = this.getBoundingBox().inflate(0.2);
            for (BlockPos $$4 : BlockPos.betweenClosed(Mth.floor($$3.minX), Mth.floor($$3.minY), Mth.floor($$3.minZ), Mth.floor($$3.maxX), Mth.floor($$3.maxY), Mth.floor($$3.maxZ))) {
                BlockState $$5 = this.level.getBlockState($$4);
                Block $$6 = $$5.getBlock();
                if (!($$6 instanceof LeavesBlock)) continue;
                $$2 = this.level.destroyBlock($$4, true, this) || $$2;
            }
            if (!$$2 && this.onGround) {
                this.jumpFromGround();
            }
        }
        if (this.roarTick > 0) {
            --this.roarTick;
            if (this.roarTick == 10) {
                this.roar();
            }
        }
        if (this.attackTick > 0) {
            --this.attackTick;
        }
        if (this.stunnedTick > 0) {
            --this.stunnedTick;
            this.stunEffect();
            if (this.stunnedTick == 0) {
                this.playSound(SoundEvents.RAVAGER_ROAR, 1.0f, 1.0f);
                this.roarTick = 20;
            }
        }
    }

    private void stunEffect() {
        if (this.random.nextInt(6) == 0) {
            double $$0 = this.getX() - (double)this.getBbWidth() * Math.sin((double)(this.yBodyRot * ((float)Math.PI / 180))) + (this.random.nextDouble() * 0.6 - 0.3);
            double $$1 = this.getY() + (double)this.getBbHeight() - 0.3;
            double $$2 = this.getZ() + (double)this.getBbWidth() * Math.cos((double)(this.yBodyRot * ((float)Math.PI / 180))) + (this.random.nextDouble() * 0.6 - 0.3);
            this.level.addParticle(ParticleTypes.ENTITY_EFFECT, $$0, $$1, $$2, 0.4980392156862745, 0.5137254901960784, 0.5725490196078431);
        }
    }

    @Override
    protected boolean isImmobile() {
        return super.isImmobile() || this.attackTick > 0 || this.stunnedTick > 0 || this.roarTick > 0;
    }

    @Override
    public boolean hasLineOfSight(Entity $$0) {
        if (this.stunnedTick > 0 || this.roarTick > 0) {
            return false;
        }
        return super.hasLineOfSight($$0);
    }

    @Override
    protected void blockedByShield(LivingEntity $$0) {
        if (this.roarTick == 0) {
            if (this.random.nextDouble() < 0.5) {
                this.stunnedTick = 40;
                this.playSound(SoundEvents.RAVAGER_STUNNED, 1.0f, 1.0f);
                this.level.broadcastEntityEvent(this, (byte)39);
                $$0.push(this);
            } else {
                this.strongKnockback($$0);
            }
            $$0.hurtMarked = true;
        }
    }

    private void roar() {
        if (this.isAlive()) {
            List $$0 = this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(4.0), NO_RAVAGER_AND_ALIVE);
            for (LivingEntity $$1 : $$0) {
                if (!($$1 instanceof AbstractIllager)) {
                    $$1.hurt(DamageSource.mobAttack(this), 6.0f);
                }
                this.strongKnockback($$1);
            }
            Vec3 $$2 = this.getBoundingBox().getCenter();
            for (int $$3 = 0; $$3 < 40; ++$$3) {
                double $$4 = this.random.nextGaussian() * 0.2;
                double $$5 = this.random.nextGaussian() * 0.2;
                double $$6 = this.random.nextGaussian() * 0.2;
                this.level.addParticle(ParticleTypes.POOF, $$2.x, $$2.y, $$2.z, $$4, $$5, $$6);
            }
            this.gameEvent(GameEvent.ENTITY_ROAR);
        }
    }

    private void strongKnockback(Entity $$0) {
        double $$1 = $$0.getX() - this.getX();
        double $$2 = $$0.getZ() - this.getZ();
        double $$3 = Math.max((double)($$1 * $$1 + $$2 * $$2), (double)0.001);
        $$0.push($$1 / $$3 * 4.0, 0.2, $$2 / $$3 * 4.0);
    }

    @Override
    public void handleEntityEvent(byte $$0) {
        if ($$0 == 4) {
            this.attackTick = 10;
            this.playSound(SoundEvents.RAVAGER_ATTACK, 1.0f, 1.0f);
        } else if ($$0 == 39) {
            this.stunnedTick = 40;
        }
        super.handleEntityEvent($$0);
    }

    public int getAttackTick() {
        return this.attackTick;
    }

    public int getStunnedTick() {
        return this.stunnedTick;
    }

    public int getRoarTick() {
        return this.roarTick;
    }

    @Override
    public boolean doHurtTarget(Entity $$0) {
        this.attackTick = 10;
        this.level.broadcastEntityEvent(this, (byte)4);
        this.playSound(SoundEvents.RAVAGER_ATTACK, 1.0f, 1.0f);
        return super.doHurtTarget($$0);
    }

    @Override
    @Nullable
    protected SoundEvent getAmbientSound() {
        return SoundEvents.RAVAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource $$0) {
        return SoundEvents.RAVAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.RAVAGER_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos $$0, BlockState $$1) {
        this.playSound(SoundEvents.RAVAGER_STEP, 0.15f, 1.0f);
    }

    @Override
    public boolean checkSpawnObstruction(LevelReader $$0) {
        return !$$0.containsAnyLiquid(this.getBoundingBox());
    }

    @Override
    public void applyRaidBuffs(int $$0, boolean $$1) {
    }

    @Override
    public boolean canBeLeader() {
        return false;
    }

    class RavagerMeleeAttackGoal
    extends MeleeAttackGoal {
        public RavagerMeleeAttackGoal() {
            super(Ravager.this, 1.0, true);
        }

        @Override
        protected double getAttackReachSqr(LivingEntity $$0) {
            float $$1 = Ravager.this.getBbWidth() - 0.1f;
            return $$1 * 2.0f * ($$1 * 2.0f) + $$0.getBbWidth();
        }
    }
}