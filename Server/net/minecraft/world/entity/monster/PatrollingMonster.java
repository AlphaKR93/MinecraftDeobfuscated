/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Enum
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.EnumSet
 *  java.util.List
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.monster;

import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

public abstract class PatrollingMonster
extends Monster {
    @Nullable
    private BlockPos patrolTarget;
    private boolean patrolLeader;
    private boolean patrolling;

    protected PatrollingMonster(EntityType<? extends PatrollingMonster> $$0, Level $$1) {
        super((EntityType<? extends Monster>)$$0, $$1);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(4, new LongDistancePatrolGoal<PatrollingMonster>(this, 0.7, 0.595));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        super.addAdditionalSaveData($$0);
        if (this.patrolTarget != null) {
            $$0.put("PatrolTarget", NbtUtils.writeBlockPos(this.patrolTarget));
        }
        $$0.putBoolean("PatrolLeader", this.patrolLeader);
        $$0.putBoolean("Patrolling", this.patrolling);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$0) {
        super.readAdditionalSaveData($$0);
        if ($$0.contains("PatrolTarget")) {
            this.patrolTarget = NbtUtils.readBlockPos($$0.getCompound("PatrolTarget"));
        }
        this.patrolLeader = $$0.getBoolean("PatrolLeader");
        this.patrolling = $$0.getBoolean("Patrolling");
    }

    @Override
    public double getMyRidingOffset() {
        return -0.45;
    }

    public boolean canBeLeader() {
        return true;
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor $$0, DifficultyInstance $$1, MobSpawnType $$2, @Nullable SpawnGroupData $$3, @Nullable CompoundTag $$4) {
        if ($$2 != MobSpawnType.PATROL && $$2 != MobSpawnType.EVENT && $$2 != MobSpawnType.STRUCTURE && $$0.getRandom().nextFloat() < 0.06f && this.canBeLeader()) {
            this.patrolLeader = true;
        }
        if (this.isPatrolLeader()) {
            this.setItemSlot(EquipmentSlot.HEAD, Raid.getLeaderBannerInstance());
            this.setDropChance(EquipmentSlot.HEAD, 2.0f);
        }
        if ($$2 == MobSpawnType.PATROL) {
            this.patrolling = true;
        }
        return super.finalizeSpawn($$0, $$1, $$2, $$3, $$4);
    }

    public static boolean checkPatrollingMonsterSpawnRules(EntityType<? extends PatrollingMonster> $$0, LevelAccessor $$1, MobSpawnType $$2, BlockPos $$3, RandomSource $$4) {
        if ($$1.getBrightness(LightLayer.BLOCK, $$3) > 8) {
            return false;
        }
        return PatrollingMonster.checkAnyLightMonsterSpawnRules($$0, $$1, $$2, $$3, $$4);
    }

    @Override
    public boolean removeWhenFarAway(double $$0) {
        return !this.patrolling || $$0 > 16384.0;
    }

    public void setPatrolTarget(BlockPos $$0) {
        this.patrolTarget = $$0;
        this.patrolling = true;
    }

    public BlockPos getPatrolTarget() {
        return this.patrolTarget;
    }

    public boolean hasPatrolTarget() {
        return this.patrolTarget != null;
    }

    public void setPatrolLeader(boolean $$0) {
        this.patrolLeader = $$0;
        this.patrolling = true;
    }

    public boolean isPatrolLeader() {
        return this.patrolLeader;
    }

    public boolean canJoinPatrol() {
        return true;
    }

    public void findPatrolTarget() {
        this.patrolTarget = this.blockPosition().offset(-500 + this.random.nextInt(1000), 0, -500 + this.random.nextInt(1000));
        this.patrolling = true;
    }

    protected boolean isPatrolling() {
        return this.patrolling;
    }

    protected void setPatrolling(boolean $$0) {
        this.patrolling = $$0;
    }

    public static class LongDistancePatrolGoal<T extends PatrollingMonster>
    extends Goal {
        private static final int NAVIGATION_FAILED_COOLDOWN = 200;
        private final T mob;
        private final double speedModifier;
        private final double leaderSpeedModifier;
        private long cooldownUntil;

        public LongDistancePatrolGoal(T $$0, double $$1, double $$2) {
            this.mob = $$0;
            this.speedModifier = $$1;
            this.leaderSpeedModifier = $$2;
            this.cooldownUntil = -1L;
            this.setFlags((EnumSet<Goal.Flag>)EnumSet.of((Enum)Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            boolean $$0 = ((PatrollingMonster)this.mob).level.getGameTime() < this.cooldownUntil;
            return ((PatrollingMonster)this.mob).isPatrolling() && ((Mob)this.mob).getTarget() == null && !((Entity)this.mob).isVehicle() && ((PatrollingMonster)this.mob).hasPatrolTarget() && !$$0;
        }

        @Override
        public void start() {
        }

        @Override
        public void stop() {
        }

        @Override
        public void tick() {
            boolean $$0 = ((PatrollingMonster)this.mob).isPatrolLeader();
            PathNavigation $$1 = ((Mob)this.mob).getNavigation();
            if ($$1.isDone()) {
                List<PatrollingMonster> $$2 = this.findPatrolCompanions();
                if (((PatrollingMonster)this.mob).isPatrolling() && $$2.isEmpty()) {
                    ((PatrollingMonster)this.mob).setPatrolling(false);
                } else if (!$$0 || !((PatrollingMonster)this.mob).getPatrolTarget().closerToCenterThan(((Entity)this.mob).position(), 10.0)) {
                    Vec3 $$3 = Vec3.atBottomCenterOf(((PatrollingMonster)this.mob).getPatrolTarget());
                    Vec3 $$4 = ((Entity)this.mob).position();
                    Vec3 $$5 = $$4.subtract($$3);
                    $$3 = $$5.yRot(90.0f).scale(0.4).add($$3);
                    Vec3 $$6 = $$3.subtract($$4).normalize().scale(10.0).add($$4);
                    BlockPos $$7 = new BlockPos($$6);
                    if (!$$1.moveTo(($$7 = ((PatrollingMonster)this.mob).level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, $$7)).getX(), $$7.getY(), $$7.getZ(), $$0 ? this.leaderSpeedModifier : this.speedModifier)) {
                        this.moveRandomly();
                        this.cooldownUntil = ((PatrollingMonster)this.mob).level.getGameTime() + 200L;
                    } else if ($$0) {
                        for (PatrollingMonster $$8 : $$2) {
                            $$8.setPatrolTarget($$7);
                        }
                    }
                } else {
                    ((PatrollingMonster)this.mob).findPatrolTarget();
                }
            }
        }

        private List<PatrollingMonster> findPatrolCompanions() {
            return ((PatrollingMonster)this.mob).level.getEntitiesOfClass(PatrollingMonster.class, ((Entity)this.mob).getBoundingBox().inflate(16.0), $$0 -> $$0.canJoinPatrol() && !$$0.is((Entity)this.mob));
        }

        private boolean moveRandomly() {
            RandomSource $$0 = ((LivingEntity)this.mob).getRandom();
            BlockPos $$1 = ((PatrollingMonster)this.mob).level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ((Entity)this.mob).blockPosition().offset(-8 + $$0.nextInt(16), 0, -8 + $$0.nextInt(16)));
            return ((Mob)this.mob).getNavigation().moveTo($$1.getX(), $$1.getY(), $$1.getZ(), this.speedModifier);
        }
    }
}