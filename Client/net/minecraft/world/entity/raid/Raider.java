/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.lang.Boolean
 *  java.lang.Enum
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.EnumSet
 *  java.util.List
 *  java.util.Objects
 *  java.util.Optional
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.raid;

import com.google.common.collect.Lists;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.PathfindToRaidGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raids;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;

public abstract class Raider
extends PatrollingMonster {
    protected static final EntityDataAccessor<Boolean> IS_CELEBRATING = SynchedEntityData.defineId(Raider.class, EntityDataSerializers.BOOLEAN);
    static final Predicate<ItemEntity> ALLOWED_ITEMS = $$0 -> !$$0.hasPickUpDelay() && $$0.isAlive() && ItemStack.matches($$0.getItem(), Raid.getLeaderBannerInstance());
    @Nullable
    protected Raid raid;
    private int wave;
    private boolean canJoinRaid;
    private int ticksOutsideRaid;

    protected Raider(EntityType<? extends Raider> $$0, Level $$1) {
        super((EntityType<? extends PatrollingMonster>)$$0, $$1);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new ObtainRaidLeaderBannerGoal(this, this));
        this.goalSelector.addGoal(3, new PathfindToRaidGoal<Raider>(this));
        this.goalSelector.addGoal(4, new RaiderMoveThroughVillageGoal(this, 1.05f, 1));
        this.goalSelector.addGoal(5, new RaiderCelebration(this));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_CELEBRATING, false);
    }

    public abstract void applyRaidBuffs(int var1, boolean var2);

    public boolean canJoinRaid() {
        return this.canJoinRaid;
    }

    public void setCanJoinRaid(boolean $$0) {
        this.canJoinRaid = $$0;
    }

    @Override
    public void aiStep() {
        if (this.level instanceof ServerLevel && this.isAlive()) {
            Raid $$0 = this.getCurrentRaid();
            if (this.canJoinRaid()) {
                if ($$0 == null) {
                    Raid $$1;
                    if (this.level.getGameTime() % 20L == 0L && ($$1 = ((ServerLevel)this.level).getRaidAt(this.blockPosition())) != null && Raids.canJoinRaid(this, $$1)) {
                        $$1.joinRaid($$1.getGroupsSpawned(), this, null, true);
                    }
                } else {
                    LivingEntity $$2 = this.getTarget();
                    if ($$2 != null && ($$2.getType() == EntityType.PLAYER || $$2.getType() == EntityType.IRON_GOLEM)) {
                        this.noActionTime = 0;
                    }
                }
            }
        }
        super.aiStep();
    }

    @Override
    protected void updateNoActionTime() {
        this.noActionTime += 2;
    }

    @Override
    public void die(DamageSource $$0) {
        if (this.level instanceof ServerLevel) {
            Entity $$1 = $$0.getEntity();
            Raid $$2 = this.getCurrentRaid();
            if ($$2 != null) {
                if (this.isPatrolLeader()) {
                    $$2.removeLeader(this.getWave());
                }
                if ($$1 != null && $$1.getType() == EntityType.PLAYER) {
                    $$2.addHeroOfTheVillage($$1);
                }
                $$2.removeFromRaid(this, false);
            }
            if (this.isPatrolLeader() && $$2 == null && ((ServerLevel)this.level).getRaidAt(this.blockPosition()) == null) {
                ItemStack $$3 = this.getItemBySlot(EquipmentSlot.HEAD);
                Player $$4 = null;
                Entity $$5 = $$1;
                if ($$5 instanceof Player) {
                    $$4 = (Player)$$5;
                } else if ($$5 instanceof Wolf) {
                    Wolf $$6 = (Wolf)$$5;
                    LivingEntity $$7 = $$6.getOwner();
                    if ($$6.isTame() && $$7 instanceof Player) {
                        $$4 = (Player)$$7;
                    }
                }
                if (!$$3.isEmpty() && ItemStack.matches($$3, Raid.getLeaderBannerInstance()) && $$4 != null) {
                    MobEffectInstance $$8 = $$4.getEffect(MobEffects.BAD_OMEN);
                    int $$9 = 1;
                    if ($$8 != null) {
                        $$9 += $$8.getAmplifier();
                        $$4.removeEffectNoUpdate(MobEffects.BAD_OMEN);
                    } else {
                        --$$9;
                    }
                    $$9 = Mth.clamp($$9, 0, 4);
                    MobEffectInstance $$10 = new MobEffectInstance(MobEffects.BAD_OMEN, 120000, $$9, false, false, true);
                    if (!this.level.getGameRules().getBoolean(GameRules.RULE_DISABLE_RAIDS)) {
                        $$4.addEffect($$10);
                    }
                }
            }
        }
        super.die($$0);
    }

    @Override
    public boolean canJoinPatrol() {
        return !this.hasActiveRaid();
    }

    public void setCurrentRaid(@Nullable Raid $$0) {
        this.raid = $$0;
    }

    @Nullable
    public Raid getCurrentRaid() {
        return this.raid;
    }

    public boolean hasActiveRaid() {
        return this.getCurrentRaid() != null && this.getCurrentRaid().isActive();
    }

    public void setWave(int $$0) {
        this.wave = $$0;
    }

    public int getWave() {
        return this.wave;
    }

    public boolean isCelebrating() {
        return this.entityData.get(IS_CELEBRATING);
    }

    public void setCelebrating(boolean $$0) {
        this.entityData.set(IS_CELEBRATING, $$0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        super.addAdditionalSaveData($$0);
        $$0.putInt("Wave", this.wave);
        $$0.putBoolean("CanJoinRaid", this.canJoinRaid);
        if (this.raid != null) {
            $$0.putInt("RaidId", this.raid.getId());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$0) {
        super.readAdditionalSaveData($$0);
        this.wave = $$0.getInt("Wave");
        this.canJoinRaid = $$0.getBoolean("CanJoinRaid");
        if ($$0.contains("RaidId", 3)) {
            if (this.level instanceof ServerLevel) {
                this.raid = ((ServerLevel)this.level).getRaids().get($$0.getInt("RaidId"));
            }
            if (this.raid != null) {
                this.raid.addWaveMob(this.wave, this, false);
                if (this.isPatrolLeader()) {
                    this.raid.setLeader(this.wave, this);
                }
            }
        }
    }

    @Override
    protected void pickUpItem(ItemEntity $$0) {
        boolean $$2;
        ItemStack $$1 = $$0.getItem();
        boolean bl = $$2 = this.hasActiveRaid() && this.getCurrentRaid().getLeader(this.getWave()) != null;
        if (this.hasActiveRaid() && !$$2 && ItemStack.matches($$1, Raid.getLeaderBannerInstance())) {
            EquipmentSlot $$3 = EquipmentSlot.HEAD;
            ItemStack $$4 = this.getItemBySlot($$3);
            double $$5 = this.getEquipmentDropChance($$3);
            if (!$$4.isEmpty() && (double)Math.max((float)(this.random.nextFloat() - 0.1f), (float)0.0f) < $$5) {
                this.spawnAtLocation($$4);
            }
            this.onItemPickup($$0);
            this.setItemSlot($$3, $$1);
            this.take($$0, $$1.getCount());
            $$0.discard();
            this.getCurrentRaid().setLeader(this.getWave(), this);
            this.setPatrolLeader(true);
        } else {
            super.pickUpItem($$0);
        }
    }

    @Override
    public boolean removeWhenFarAway(double $$0) {
        if (this.getCurrentRaid() == null) {
            return super.removeWhenFarAway($$0);
        }
        return false;
    }

    @Override
    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence() || this.getCurrentRaid() != null;
    }

    public int getTicksOutsideRaid() {
        return this.ticksOutsideRaid;
    }

    public void setTicksOutsideRaid(int $$0) {
        this.ticksOutsideRaid = $$0;
    }

    @Override
    public boolean hurt(DamageSource $$0, float $$1) {
        if (this.hasActiveRaid()) {
            this.getCurrentRaid().updateBossbar();
        }
        return super.hurt($$0, $$1);
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor $$0, DifficultyInstance $$1, MobSpawnType $$2, @Nullable SpawnGroupData $$3, @Nullable CompoundTag $$4) {
        this.setCanJoinRaid(this.getType() != EntityType.WITCH || $$2 != MobSpawnType.NATURAL);
        return super.finalizeSpawn($$0, $$1, $$2, $$3, $$4);
    }

    public abstract SoundEvent getCelebrateSound();

    public static class ObtainRaidLeaderBannerGoal<T extends Raider>
    extends Goal {
        private final T mob;
        final /* synthetic */ Raider this$0;

        public ObtainRaidLeaderBannerGoal(T $$1) {
            this.this$0 = $$0;
            this.mob = $$1;
            this.setFlags((EnumSet<Goal.Flag>)EnumSet.of((Enum)Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            List $$2;
            Raid $$0 = ((Raider)this.mob).getCurrentRaid();
            if (!((Raider)this.mob).hasActiveRaid() || ((Raider)this.mob).getCurrentRaid().isOver() || !((PatrollingMonster)this.mob).canBeLeader() || ItemStack.matches(((Mob)this.mob).getItemBySlot(EquipmentSlot.HEAD), Raid.getLeaderBannerInstance())) {
                return false;
            }
            Raider $$1 = $$0.getLeader(((Raider)this.mob).getWave());
            if (!($$1 != null && $$1.isAlive() || ($$2 = ((Raider)this.mob).level.getEntitiesOfClass(ItemEntity.class, ((Entity)this.mob).getBoundingBox().inflate(16.0, 8.0, 16.0), ALLOWED_ITEMS)).isEmpty())) {
                return ((Mob)this.mob).getNavigation().moveTo((Entity)$$2.get(0), (double)1.15f);
            }
            return false;
        }

        @Override
        public void tick() {
            List $$0;
            if (((Mob)this.mob).getNavigation().getTargetPos().closerToCenterThan(((Entity)this.mob).position(), 1.414) && !($$0 = ((Raider)this.mob).level.getEntitiesOfClass(ItemEntity.class, ((Entity)this.mob).getBoundingBox().inflate(4.0, 4.0, 4.0), ALLOWED_ITEMS)).isEmpty()) {
                ((Raider)this.mob).pickUpItem((ItemEntity)$$0.get(0));
            }
        }
    }

    static class RaiderMoveThroughVillageGoal
    extends Goal {
        private final Raider raider;
        private final double speedModifier;
        private BlockPos poiPos;
        private final List<BlockPos> visited = Lists.newArrayList();
        private final int distanceToPoi;
        private boolean stuck;

        public RaiderMoveThroughVillageGoal(Raider $$0, double $$1, int $$2) {
            this.raider = $$0;
            this.speedModifier = $$1;
            this.distanceToPoi = $$2;
            this.setFlags((EnumSet<Goal.Flag>)EnumSet.of((Enum)Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            this.updateVisited();
            return this.isValidRaid() && this.hasSuitablePoi() && this.raider.getTarget() == null;
        }

        private boolean isValidRaid() {
            return this.raider.hasActiveRaid() && !this.raider.getCurrentRaid().isOver();
        }

        private boolean hasSuitablePoi() {
            ServerLevel $$02 = (ServerLevel)this.raider.level;
            BlockPos $$1 = this.raider.blockPosition();
            Optional<BlockPos> $$2 = $$02.getPoiManager().getRandom((Predicate<Holder<PoiType>>)((Predicate)$$0 -> $$0.is(PoiTypes.HOME)), (Predicate<BlockPos>)((Predicate)this::hasNotVisited), PoiManager.Occupancy.ANY, $$1, 48, this.raider.random);
            if (!$$2.isPresent()) {
                return false;
            }
            this.poiPos = ((BlockPos)$$2.get()).immutable();
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            if (this.raider.getNavigation().isDone()) {
                return false;
            }
            return this.raider.getTarget() == null && !this.poiPos.closerToCenterThan(this.raider.position(), this.raider.getBbWidth() + (float)this.distanceToPoi) && !this.stuck;
        }

        @Override
        public void stop() {
            if (this.poiPos.closerToCenterThan(this.raider.position(), this.distanceToPoi)) {
                this.visited.add((Object)this.poiPos);
            }
        }

        @Override
        public void start() {
            super.start();
            this.raider.setNoActionTime(0);
            this.raider.getNavigation().moveTo(this.poiPos.getX(), this.poiPos.getY(), this.poiPos.getZ(), this.speedModifier);
            this.stuck = false;
        }

        @Override
        public void tick() {
            if (this.raider.getNavigation().isDone()) {
                Vec3 $$0 = Vec3.atBottomCenterOf(this.poiPos);
                Vec3 $$1 = DefaultRandomPos.getPosTowards(this.raider, 16, 7, $$0, 0.3141592741012573);
                if ($$1 == null) {
                    $$1 = DefaultRandomPos.getPosTowards(this.raider, 8, 7, $$0, 1.5707963705062866);
                }
                if ($$1 == null) {
                    this.stuck = true;
                    return;
                }
                this.raider.getNavigation().moveTo($$1.x, $$1.y, $$1.z, this.speedModifier);
            }
        }

        private boolean hasNotVisited(BlockPos $$0) {
            for (BlockPos $$1 : this.visited) {
                if (!Objects.equals((Object)$$0, (Object)$$1)) continue;
                return false;
            }
            return true;
        }

        private void updateVisited() {
            if (this.visited.size() > 2) {
                this.visited.remove(0);
            }
        }
    }

    public class RaiderCelebration
    extends Goal {
        private final Raider mob;

        RaiderCelebration(Raider $$1) {
            this.mob = $$1;
            this.setFlags((EnumSet<Goal.Flag>)EnumSet.of((Enum)Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            Raid $$0 = this.mob.getCurrentRaid();
            return this.mob.isAlive() && this.mob.getTarget() == null && $$0 != null && $$0.isLoss();
        }

        @Override
        public void start() {
            this.mob.setCelebrating(true);
            super.start();
        }

        @Override
        public void stop() {
            this.mob.setCelebrating(false);
            super.stop();
        }

        @Override
        public void tick() {
            if (!this.mob.isSilent() && this.mob.random.nextInt(this.adjustedTickDelay(100)) == 0) {
                Raider.this.playSound(Raider.this.getCelebrateSound(), Raider.this.getSoundVolume(), Raider.this.getVoicePitch());
            }
            if (!this.mob.isPassenger() && this.mob.random.nextInt(this.adjustedTickDelay(50)) == 0) {
                this.mob.getJumpControl().jump();
            }
            super.tick();
        }
    }

    protected class HoldGroundAttackGoal
    extends Goal {
        private final Raider mob;
        private final float hostileRadiusSqr;
        public final TargetingConditions shoutTargeting = TargetingConditions.forNonCombat().range(8.0).ignoreLineOfSight().ignoreInvisibilityTesting();

        public HoldGroundAttackGoal(AbstractIllager $$1, float $$2) {
            this.mob = $$1;
            this.hostileRadiusSqr = $$2 * $$2;
            this.setFlags((EnumSet<Goal.Flag>)EnumSet.of((Enum)Goal.Flag.MOVE, (Enum)Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity $$0 = this.mob.getLastHurtByMob();
            return this.mob.getCurrentRaid() == null && this.mob.isPatrolling() && this.mob.getTarget() != null && !this.mob.isAggressive() && ($$0 == null || $$0.getType() != EntityType.PLAYER);
        }

        @Override
        public void start() {
            super.start();
            this.mob.getNavigation().stop();
            List $$0 = this.mob.level.getNearbyEntities(Raider.class, this.shoutTargeting, this.mob, this.mob.getBoundingBox().inflate(8.0, 8.0, 8.0));
            for (Raider $$1 : $$0) {
                $$1.setTarget(this.mob.getTarget());
            }
        }

        @Override
        public void stop() {
            super.stop();
            LivingEntity $$0 = this.mob.getTarget();
            if ($$0 != null) {
                List $$1 = this.mob.level.getNearbyEntities(Raider.class, this.shoutTargeting, this.mob, this.mob.getBoundingBox().inflate(8.0, 8.0, 8.0));
                for (Raider $$2 : $$1) {
                    $$2.setTarget($$0);
                    $$2.setAggressive(true);
                }
                this.mob.setAggressive(true);
            }
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity $$0 = this.mob.getTarget();
            if ($$0 == null) {
                return;
            }
            if (this.mob.distanceToSqr($$0) > (double)this.hostileRadiusSqr) {
                this.mob.getLookControl().setLookAt($$0, 30.0f, 30.0f);
                if (this.mob.random.nextInt(50) == 0) {
                    this.mob.playAmbientSound();
                }
            } else {
                this.mob.setAggressive(true);
            }
            super.tick();
        }
    }
}