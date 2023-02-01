/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Class
 *  java.lang.Enum
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.EnumSet
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.monster;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.ClimbOnTopOfPowderSnowGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.InfestedBlock;
import net.minecraft.world.level.block.state.BlockState;

public class Silverfish
extends Monster {
    @Nullable
    private SilverfishWakeUpFriendsGoal friendsGoal;

    public Silverfish(EntityType<? extends Silverfish> $$0, Level $$1) {
        super((EntityType<? extends Monster>)$$0, $$1);
    }

    @Override
    protected void registerGoals() {
        this.friendsGoal = new SilverfishWakeUpFriendsGoal(this);
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(1, new ClimbOnTopOfPowderSnowGoal(this, this.level));
        this.goalSelector.addGoal(3, this.friendsGoal);
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(5, new SilverfishMergeWithStoneGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]).setAlertOthers(new Class[0]));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<Player>((Mob)this, Player.class, true));
    }

    @Override
    public double getMyRidingOffset() {
        return 0.1;
    }

    @Override
    protected float getStandingEyeHeight(Pose $$0, EntityDimensions $$1) {
        return 0.13f;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 8.0).add(Attributes.MOVEMENT_SPEED, 0.25).add(Attributes.ATTACK_DAMAGE, 1.0);
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.EVENTS;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SILVERFISH_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource $$0) {
        return SoundEvents.SILVERFISH_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SILVERFISH_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos $$0, BlockState $$1) {
        this.playSound(SoundEvents.SILVERFISH_STEP, 0.15f, 1.0f);
    }

    @Override
    public boolean hurt(DamageSource $$0, float $$1) {
        if (this.isInvulnerableTo($$0)) {
            return false;
        }
        if (($$0 instanceof EntityDamageSource || $$0 == DamageSource.MAGIC) && this.friendsGoal != null) {
            this.friendsGoal.notifyHurt();
        }
        return super.hurt($$0, $$1);
    }

    @Override
    public void tick() {
        this.yBodyRot = this.getYRot();
        super.tick();
    }

    @Override
    public void setYBodyRot(float $$0) {
        this.setYRot($$0);
        super.setYBodyRot($$0);
    }

    @Override
    public float getWalkTargetValue(BlockPos $$0, LevelReader $$1) {
        if (InfestedBlock.isCompatibleHostBlock($$1.getBlockState((BlockPos)$$0.below()))) {
            return 10.0f;
        }
        return super.getWalkTargetValue($$0, $$1);
    }

    public static boolean checkSilverfishSpawnRules(EntityType<Silverfish> $$0, LevelAccessor $$1, MobSpawnType $$2, BlockPos $$3, RandomSource $$4) {
        if (Silverfish.checkAnyLightMonsterSpawnRules($$0, $$1, $$2, $$3, $$4)) {
            Player $$5 = $$1.getNearestPlayer((double)$$3.getX() + 0.5, (double)$$3.getY() + 0.5, (double)$$3.getZ() + 0.5, 5.0, true);
            return $$5 == null;
        }
        return false;
    }

    @Override
    public MobType getMobType() {
        return MobType.ARTHROPOD;
    }

    static class SilverfishWakeUpFriendsGoal
    extends Goal {
        private final Silverfish silverfish;
        private int lookForFriends;

        public SilverfishWakeUpFriendsGoal(Silverfish $$0) {
            this.silverfish = $$0;
        }

        public void notifyHurt() {
            if (this.lookForFriends == 0) {
                this.lookForFriends = this.adjustedTickDelay(20);
            }
        }

        @Override
        public boolean canUse() {
            return this.lookForFriends > 0;
        }

        @Override
        public void tick() {
            --this.lookForFriends;
            if (this.lookForFriends <= 0) {
                Level $$0 = this.silverfish.level;
                RandomSource $$1 = this.silverfish.getRandom();
                BlockPos $$2 = this.silverfish.blockPosition();
                int $$3 = 0;
                block0: while ($$3 <= 5 && $$3 >= -5) {
                    int $$4 = 0;
                    while ($$4 <= 10 && $$4 >= -10) {
                        int $$5 = 0;
                        while ($$5 <= 10 && $$5 >= -10) {
                            BlockPos $$6 = $$2.offset($$4, $$3, $$5);
                            BlockState $$7 = $$0.getBlockState($$6);
                            Block $$8 = $$7.getBlock();
                            if ($$8 instanceof InfestedBlock) {
                                if ($$0.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                                    $$0.destroyBlock($$6, true, this.silverfish);
                                } else {
                                    $$0.setBlock($$6, ((InfestedBlock)$$8).hostStateByInfested($$0.getBlockState($$6)), 3);
                                }
                                if ($$1.nextBoolean()) break block0;
                            }
                            $$5 = ($$5 <= 0 ? 1 : 0) - $$5;
                        }
                        $$4 = ($$4 <= 0 ? 1 : 0) - $$4;
                    }
                    $$3 = ($$3 <= 0 ? 1 : 0) - $$3;
                }
            }
        }
    }

    static class SilverfishMergeWithStoneGoal
    extends RandomStrollGoal {
        @Nullable
        private Direction selectedDirection;
        private boolean doMerge;

        public SilverfishMergeWithStoneGoal(Silverfish $$0) {
            super($$0, 1.0, 10);
            this.setFlags((EnumSet<Goal.Flag>)EnumSet.of((Enum)Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (this.mob.getTarget() != null) {
                return false;
            }
            if (!this.mob.getNavigation().isDone()) {
                return false;
            }
            RandomSource $$0 = this.mob.getRandom();
            if (this.mob.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) && $$0.nextInt(SilverfishMergeWithStoneGoal.reducedTickDelay(10)) == 0) {
                this.selectedDirection = Direction.getRandom($$0);
                Vec3i $$1 = new BlockPos(this.mob.getX(), this.mob.getY() + 0.5, this.mob.getZ()).relative(this.selectedDirection);
                BlockState $$2 = this.mob.level.getBlockState((BlockPos)$$1);
                if (InfestedBlock.isCompatibleHostBlock($$2)) {
                    this.doMerge = true;
                    return true;
                }
            }
            this.doMerge = false;
            return super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            if (this.doMerge) {
                return false;
            }
            return super.canContinueToUse();
        }

        @Override
        public void start() {
            if (!this.doMerge) {
                super.start();
                return;
            }
            Level $$0 = this.mob.level;
            Vec3i $$1 = new BlockPos(this.mob.getX(), this.mob.getY() + 0.5, this.mob.getZ()).relative(this.selectedDirection);
            BlockState $$2 = $$0.getBlockState((BlockPos)$$1);
            if (InfestedBlock.isCompatibleHostBlock($$2)) {
                $$0.setBlock((BlockPos)$$1, InfestedBlock.infestedStateByHost($$2), 3);
                this.mob.spawnAnim();
                this.mob.discard();
            }
        }
    }
}