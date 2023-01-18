/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Byte
 *  java.lang.Class
 *  java.lang.Enum
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.EnumSet
 *  javax.annotation.Nullable
 *  net.minecraft.world.entity.LivingEntity
 */
package net.minecraft.world.entity.monster;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;

public class Vex
extends Monster {
    public static final float FLAP_DEGREES_PER_TICK = 45.836624f;
    public static final int TICKS_PER_FLAP = Mth.ceil(3.9269907f);
    protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(Vex.class, EntityDataSerializers.BYTE);
    private static final int FLAG_IS_CHARGING = 1;
    private static final double RIDING_OFFSET = 0.4;
    @Nullable
    Mob owner;
    @Nullable
    private BlockPos boundOrigin;
    private boolean hasLimitedLife;
    private int limitedLifeTicks;

    public Vex(EntityType<? extends Vex> $$0, Level $$1) {
        super((EntityType<? extends Monster>)$$0, $$1);
        this.moveControl = new VexMoveControl(this);
        this.xpReward = 3;
    }

    protected float getStandingEyeHeight(Pose $$0, EntityDimensions $$1) {
        return $$1.height - 0.28125f;
    }

    public boolean isFlapping() {
        return this.tickCount % TICKS_PER_FLAP == 0;
    }

    public void move(MoverType $$0, Vec3 $$1) {
        super.move($$0, $$1);
        this.checkInsideBlocks();
    }

    @Override
    public void tick() {
        this.noPhysics = true;
        super.tick();
        this.noPhysics = false;
        this.setNoGravity(true);
        if (this.hasLimitedLife && --this.limitedLifeTicks <= 0) {
            this.limitedLifeTicks = 20;
            this.hurt(DamageSource.STARVE, 1.0f);
        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(4, new VexChargeAttackGoal());
        this.goalSelector.addGoal(8, new VexRandomMoveGoal());
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0f, 1.0f));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0f));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, Raider.class).setAlertOthers(new Class[0]));
        this.targetSelector.addGoal(2, new VexCopyOwnerTargetGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<Player>((Mob)this, Player.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 14.0).add(Attributes.ATTACK_DAMAGE, 4.0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FLAGS_ID, (byte)0);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$0) {
        super.readAdditionalSaveData($$0);
        if ($$0.contains("BoundX")) {
            this.boundOrigin = new BlockPos($$0.getInt("BoundX"), $$0.getInt("BoundY"), $$0.getInt("BoundZ"));
        }
        if ($$0.contains("LifeTicks")) {
            this.setLimitedLife($$0.getInt("LifeTicks"));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        super.addAdditionalSaveData($$0);
        if (this.boundOrigin != null) {
            $$0.putInt("BoundX", this.boundOrigin.getX());
            $$0.putInt("BoundY", this.boundOrigin.getY());
            $$0.putInt("BoundZ", this.boundOrigin.getZ());
        }
        if (this.hasLimitedLife) {
            $$0.putInt("LifeTicks", this.limitedLifeTicks);
        }
    }

    @Nullable
    public Mob getOwner() {
        return this.owner;
    }

    @Nullable
    public BlockPos getBoundOrigin() {
        return this.boundOrigin;
    }

    public void setBoundOrigin(@Nullable BlockPos $$0) {
        this.boundOrigin = $$0;
    }

    private boolean getVexFlag(int $$0) {
        byte $$1 = this.entityData.get(DATA_FLAGS_ID);
        return ($$1 & $$0) != 0;
    }

    private void setVexFlag(int $$0, boolean $$1) {
        int $$2 = this.entityData.get(DATA_FLAGS_ID).byteValue();
        $$2 = $$1 ? ($$2 |= $$0) : ($$2 &= ~$$0);
        this.entityData.set(DATA_FLAGS_ID, (byte)($$2 & 0xFF));
    }

    public boolean isCharging() {
        return this.getVexFlag(1);
    }

    public void setIsCharging(boolean $$0) {
        this.setVexFlag(1, $$0);
    }

    public void setOwner(Mob $$0) {
        this.owner = $$0;
    }

    public void setLimitedLife(int $$0) {
        this.hasLimitedLife = true;
        this.limitedLifeTicks = $$0;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.VEX_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.VEX_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource $$0) {
        return SoundEvents.VEX_HURT;
    }

    public float getLightLevelDependentMagicValue() {
        return 1.0f;
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor $$0, DifficultyInstance $$1, MobSpawnType $$2, @Nullable SpawnGroupData $$3, @Nullable CompoundTag $$4) {
        RandomSource $$5 = $$0.getRandom();
        this.populateDefaultEquipmentSlots($$5, $$1);
        this.populateDefaultEquipmentEnchantments($$5, $$1);
        return super.finalizeSpawn($$0, $$1, $$2, $$3, $$4);
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource $$0, DifficultyInstance $$1) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0f);
    }

    public double getMyRidingOffset() {
        return 0.4;
    }

    class VexMoveControl
    extends MoveControl {
        public VexMoveControl(Vex $$0) {
            super($$0);
        }

        @Override
        public void tick() {
            if (this.operation != MoveControl.Operation.MOVE_TO) {
                return;
            }
            Vec3 $$0 = new Vec3(this.wantedX - Vex.this.getX(), this.wantedY - Vex.this.getY(), this.wantedZ - Vex.this.getZ());
            double $$1 = $$0.length();
            if ($$1 < Vex.this.getBoundingBox().getSize()) {
                this.operation = MoveControl.Operation.WAIT;
                Vex.this.setDeltaMovement(Vex.this.getDeltaMovement().scale(0.5));
            } else {
                Vex.this.setDeltaMovement(Vex.this.getDeltaMovement().add($$0.scale(this.speedModifier * 0.05 / $$1)));
                if (Vex.this.getTarget() == null) {
                    Vec3 $$2 = Vex.this.getDeltaMovement();
                    Vex.this.setYRot(-((float)Mth.atan2($$2.x, $$2.z)) * 57.295776f);
                    Vex.this.yBodyRot = Vex.this.getYRot();
                } else {
                    double $$3 = Vex.this.getTarget().getX() - Vex.this.getX();
                    double $$4 = Vex.this.getTarget().getZ() - Vex.this.getZ();
                    Vex.this.setYRot(-((float)Mth.atan2($$3, $$4)) * 57.295776f);
                    Vex.this.yBodyRot = Vex.this.getYRot();
                }
            }
        }
    }

    class VexChargeAttackGoal
    extends Goal {
        public VexChargeAttackGoal() {
            this.setFlags((EnumSet<Goal.Flag>)EnumSet.of((Enum)Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            LivingEntity $$0 = Vex.this.getTarget();
            if ($$0 != null && $$0.isAlive() && !Vex.this.getMoveControl().hasWanted() && Vex.this.random.nextInt(VexChargeAttackGoal.reducedTickDelay(7)) == 0) {
                return Vex.this.distanceToSqr((Entity)$$0) > 4.0;
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return Vex.this.getMoveControl().hasWanted() && Vex.this.isCharging() && Vex.this.getTarget() != null && Vex.this.getTarget().isAlive();
        }

        @Override
        public void start() {
            LivingEntity $$0 = Vex.this.getTarget();
            if ($$0 != null) {
                Vec3 $$1 = $$0.getEyePosition();
                Vex.this.moveControl.setWantedPosition($$1.x, $$1.y, $$1.z, 1.0);
            }
            Vex.this.setIsCharging(true);
            Vex.this.playSound(SoundEvents.VEX_CHARGE, 1.0f, 1.0f);
        }

        @Override
        public void stop() {
            Vex.this.setIsCharging(false);
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity $$0 = Vex.this.getTarget();
            if ($$0 == null) {
                return;
            }
            if (Vex.this.getBoundingBox().intersects($$0.getBoundingBox())) {
                Vex.this.doHurtTarget((Entity)$$0);
                Vex.this.setIsCharging(false);
            } else {
                double $$1 = Vex.this.distanceToSqr((Entity)$$0);
                if ($$1 < 9.0) {
                    Vec3 $$2 = $$0.getEyePosition();
                    Vex.this.moveControl.setWantedPosition($$2.x, $$2.y, $$2.z, 1.0);
                }
            }
        }
    }

    class VexRandomMoveGoal
    extends Goal {
        public VexRandomMoveGoal() {
            this.setFlags((EnumSet<Goal.Flag>)EnumSet.of((Enum)Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return !Vex.this.getMoveControl().hasWanted() && Vex.this.random.nextInt(VexRandomMoveGoal.reducedTickDelay(7)) == 0;
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }

        @Override
        public void tick() {
            BlockPos $$0 = Vex.this.getBoundOrigin();
            if ($$0 == null) {
                $$0 = Vex.this.blockPosition();
            }
            for (int $$1 = 0; $$1 < 3; ++$$1) {
                BlockPos $$2 = $$0.offset(Vex.this.random.nextInt(15) - 7, Vex.this.random.nextInt(11) - 5, Vex.this.random.nextInt(15) - 7);
                if (!Vex.this.level.isEmptyBlock($$2)) continue;
                Vex.this.moveControl.setWantedPosition((double)$$2.getX() + 0.5, (double)$$2.getY() + 0.5, (double)$$2.getZ() + 0.5, 0.25);
                if (Vex.this.getTarget() != null) break;
                Vex.this.getLookControl().setLookAt((double)$$2.getX() + 0.5, (double)$$2.getY() + 0.5, (double)$$2.getZ() + 0.5, 180.0f, 20.0f);
                break;
            }
        }
    }

    class VexCopyOwnerTargetGoal
    extends TargetGoal {
        private final TargetingConditions copyOwnerTargeting;

        public VexCopyOwnerTargetGoal(PathfinderMob $$0) {
            super($$0, false);
            this.copyOwnerTargeting = TargetingConditions.forNonCombat().ignoreLineOfSight().ignoreInvisibilityTesting();
        }

        @Override
        public boolean canUse() {
            return Vex.this.owner != null && Vex.this.owner.getTarget() != null && this.canAttack(Vex.this.owner.getTarget(), this.copyOwnerTargeting);
        }

        @Override
        public void start() {
            Vex.this.setTarget(Vex.this.owner.getTarget());
            super.start();
        }
    }
}