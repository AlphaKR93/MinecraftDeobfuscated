/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Boolean
 *  java.lang.Class
 *  java.lang.Enum
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.EnumSet
 *  java.util.List
 *  java.util.Optional
 *  java.util.UUID
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.monster;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class EnderMan
extends Monster
implements NeutralMob {
    private static final UUID SPEED_MODIFIER_ATTACKING_UUID = UUID.fromString((String)"020E0DFB-87AE-4653-9556-831010E291A0");
    private static final AttributeModifier SPEED_MODIFIER_ATTACKING = new AttributeModifier(SPEED_MODIFIER_ATTACKING_UUID, "Attacking speed boost", (double)0.15f, AttributeModifier.Operation.ADDITION);
    private static final int DELAY_BETWEEN_CREEPY_STARE_SOUND = 400;
    private static final int MIN_DEAGGRESSION_TIME = 600;
    private static final EntityDataAccessor<Optional<BlockState>> DATA_CARRY_STATE = SynchedEntityData.defineId(EnderMan.class, EntityDataSerializers.BLOCK_STATE);
    private static final EntityDataAccessor<Boolean> DATA_CREEPY = SynchedEntityData.defineId(EnderMan.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_STARED_AT = SynchedEntityData.defineId(EnderMan.class, EntityDataSerializers.BOOLEAN);
    private int lastStareSound = Integer.MIN_VALUE;
    private int targetChangeTime;
    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
    private int remainingPersistentAngerTime;
    @Nullable
    private UUID persistentAngerTarget;

    public EnderMan(EntityType<? extends EnderMan> $$0, Level $$1) {
        super((EntityType<? extends Monster>)$$0, $$1);
        this.maxUpStep = 1.0f;
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0f);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new EndermanFreezeWhenLookedAt(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal((PathfinderMob)this, 1.0, 0.0f));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(10, new EndermanLeaveBlockGoal(this));
        this.goalSelector.addGoal(11, new EndermanTakeBlockGoal(this));
        this.targetSelector.addGoal(1, new EndermanLookForPlayerGoal(this, (Predicate<LivingEntity>)((Predicate)this::isAngryAt)));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this, new Class[0]));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<Endermite>((Mob)this, Endermite.class, true, false));
        this.targetSelector.addGoal(4, new ResetUniversalAngerTargetGoal<EnderMan>(this, false));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 40.0).add(Attributes.MOVEMENT_SPEED, 0.3f).add(Attributes.ATTACK_DAMAGE, 7.0).add(Attributes.FOLLOW_RANGE, 64.0);
    }

    @Override
    public void setTarget(@Nullable LivingEntity $$0) {
        super.setTarget($$0);
        AttributeInstance $$1 = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if ($$0 == null) {
            this.targetChangeTime = 0;
            this.entityData.set(DATA_CREEPY, false);
            this.entityData.set(DATA_STARED_AT, false);
            $$1.removeModifier(SPEED_MODIFIER_ATTACKING);
        } else {
            this.targetChangeTime = this.tickCount;
            this.entityData.set(DATA_CREEPY, true);
            if (!$$1.hasModifier(SPEED_MODIFIER_ATTACKING)) {
                $$1.addTransientModifier(SPEED_MODIFIER_ATTACKING);
            }
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_CARRY_STATE, Optional.empty());
        this.entityData.define(DATA_CREEPY, false);
        this.entityData.define(DATA_STARED_AT, false);
    }

    @Override
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
    }

    @Override
    public void setRemainingPersistentAngerTime(int $$0) {
        this.remainingPersistentAngerTime = $$0;
    }

    @Override
    public int getRemainingPersistentAngerTime() {
        return this.remainingPersistentAngerTime;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID $$0) {
        this.persistentAngerTarget = $$0;
    }

    @Override
    @Nullable
    public UUID getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }

    public void playStareSound() {
        if (this.tickCount >= this.lastStareSound + 400) {
            this.lastStareSound = this.tickCount;
            if (!this.isSilent()) {
                this.level.playLocalSound(this.getX(), this.getEyeY(), this.getZ(), SoundEvents.ENDERMAN_STARE, this.getSoundSource(), 2.5f, 1.0f, false);
            }
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> $$0) {
        if (DATA_CREEPY.equals($$0) && this.hasBeenStaredAt() && this.level.isClientSide) {
            this.playStareSound();
        }
        super.onSyncedDataUpdated($$0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        super.addAdditionalSaveData($$0);
        BlockState $$1 = this.getCarriedBlock();
        if ($$1 != null) {
            $$0.put("carriedBlockState", NbtUtils.writeBlockState($$1));
        }
        this.addPersistentAngerSaveData($$0);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$0) {
        super.readAdditionalSaveData($$0);
        BlockState $$1 = null;
        if ($$0.contains("carriedBlockState", 10) && ($$1 = NbtUtils.readBlockState(this.level.holderLookup(Registries.BLOCK), $$0.getCompound("carriedBlockState"))).isAir()) {
            $$1 = null;
        }
        this.setCarriedBlock($$1);
        this.readPersistentAngerSaveData(this.level, $$0);
    }

    boolean isLookingAtMe(Player $$0) {
        ItemStack $$1 = $$0.getInventory().armor.get(3);
        if ($$1.is(Blocks.CARVED_PUMPKIN.asItem())) {
            return false;
        }
        Vec3 $$2 = $$0.getViewVector(1.0f).normalize();
        Vec3 $$3 = new Vec3(this.getX() - $$0.getX(), this.getEyeY() - $$0.getEyeY(), this.getZ() - $$0.getZ());
        double $$4 = $$3.length();
        double $$5 = $$2.dot($$3 = $$3.normalize());
        if ($$5 > 1.0 - 0.025 / $$4) {
            return $$0.hasLineOfSight(this);
        }
        return false;
    }

    @Override
    protected float getStandingEyeHeight(Pose $$0, EntityDimensions $$1) {
        return 2.55f;
    }

    @Override
    public void aiStep() {
        if (this.level.isClientSide) {
            for (int $$0 = 0; $$0 < 2; ++$$0) {
                this.level.addParticle(ParticleTypes.PORTAL, this.getRandomX(0.5), this.getRandomY() - 0.25, this.getRandomZ(0.5), (this.random.nextDouble() - 0.5) * 2.0, -this.random.nextDouble(), (this.random.nextDouble() - 0.5) * 2.0);
            }
        }
        this.jumping = false;
        if (!this.level.isClientSide) {
            this.updatePersistentAnger((ServerLevel)this.level, true);
        }
        super.aiStep();
    }

    @Override
    public boolean isSensitiveToWater() {
        return true;
    }

    @Override
    protected void customServerAiStep() {
        float $$0;
        if (this.level.isDay() && this.tickCount >= this.targetChangeTime + 600 && ($$0 = this.getLightLevelDependentMagicValue()) > 0.5f && this.level.canSeeSky(this.blockPosition()) && this.random.nextFloat() * 30.0f < ($$0 - 0.4f) * 2.0f) {
            this.setTarget(null);
            this.teleport();
        }
        super.customServerAiStep();
    }

    protected boolean teleport() {
        if (this.level.isClientSide() || !this.isAlive()) {
            return false;
        }
        double $$0 = this.getX() + (this.random.nextDouble() - 0.5) * 64.0;
        double $$1 = this.getY() + (double)(this.random.nextInt(64) - 32);
        double $$2 = this.getZ() + (this.random.nextDouble() - 0.5) * 64.0;
        return this.teleport($$0, $$1, $$2);
    }

    boolean teleportTowards(Entity $$0) {
        Vec3 $$1 = new Vec3(this.getX() - $$0.getX(), this.getY(0.5) - $$0.getEyeY(), this.getZ() - $$0.getZ());
        $$1 = $$1.normalize();
        double $$2 = 16.0;
        double $$3 = this.getX() + (this.random.nextDouble() - 0.5) * 8.0 - $$1.x * 16.0;
        double $$4 = this.getY() + (double)(this.random.nextInt(16) - 8) - $$1.y * 16.0;
        double $$5 = this.getZ() + (this.random.nextDouble() - 0.5) * 8.0 - $$1.z * 16.0;
        return this.teleport($$3, $$4, $$5);
    }

    private boolean teleport(double $$0, double $$1, double $$2) {
        BlockPos.MutableBlockPos $$3 = new BlockPos.MutableBlockPos($$0, $$1, $$2);
        while ($$3.getY() > this.level.getMinBuildHeight() && !this.level.getBlockState($$3).getMaterial().blocksMotion()) {
            $$3.move(Direction.DOWN);
        }
        BlockState $$4 = this.level.getBlockState($$3);
        boolean $$5 = $$4.getMaterial().blocksMotion();
        boolean $$6 = $$4.getFluidState().is(FluidTags.WATER);
        if (!$$5 || $$6) {
            return false;
        }
        Vec3 $$7 = this.position();
        boolean $$8 = this.randomTeleport($$0, $$1, $$2, true);
        if ($$8) {
            this.level.gameEvent(GameEvent.TELEPORT, $$7, GameEvent.Context.of(this));
            if (!this.isSilent()) {
                this.level.playSound(null, this.xo, this.yo, this.zo, SoundEvents.ENDERMAN_TELEPORT, this.getSoundSource(), 1.0f, 1.0f);
                this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0f, 1.0f);
            }
        }
        return $$8;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.isCreepy() ? SoundEvents.ENDERMAN_SCREAM : SoundEvents.ENDERMAN_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource $$0) {
        return SoundEvents.ENDERMAN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENDERMAN_DEATH;
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource $$0, int $$1, boolean $$2) {
        super.dropCustomDeathLoot($$0, $$1, $$2);
        BlockState $$3 = this.getCarriedBlock();
        if ($$3 != null) {
            ItemStack $$4 = new ItemStack(Items.DIAMOND_AXE);
            $$4.enchant(Enchantments.SILK_TOUCH, 1);
            LootContext.Builder $$5 = new LootContext.Builder((ServerLevel)this.level).withRandom(this.level.getRandom()).withParameter(LootContextParams.ORIGIN, this.position()).withParameter(LootContextParams.TOOL, $$4).withOptionalParameter(LootContextParams.THIS_ENTITY, this);
            List<ItemStack> $$6 = $$3.getDrops($$5);
            for (ItemStack $$7 : $$6) {
                this.spawnAtLocation($$7);
            }
        }
    }

    public void setCarriedBlock(@Nullable BlockState $$0) {
        this.entityData.set(DATA_CARRY_STATE, Optional.ofNullable((Object)$$0));
    }

    @Nullable
    public BlockState getCarriedBlock() {
        return (BlockState)this.entityData.get(DATA_CARRY_STATE).orElse(null);
    }

    @Override
    public boolean hurt(DamageSource $$0, float $$1) {
        if (this.isInvulnerableTo($$0)) {
            return false;
        }
        if ($$0 instanceof IndirectEntityDamageSource) {
            boolean $$4;
            Entity $$2 = $$0.getDirectEntity();
            if ($$2 instanceof ThrownPotion) {
                boolean $$3 = this.hurtWithCleanWater($$0, (ThrownPotion)$$2, $$1);
            } else {
                $$4 = false;
            }
            for (int $$5 = 0; $$5 < 64; ++$$5) {
                if (!this.teleport()) continue;
                return true;
            }
            return $$4;
        }
        boolean $$6 = super.hurt($$0, $$1);
        if (!this.level.isClientSide() && !($$0.getEntity() instanceof LivingEntity) && this.random.nextInt(10) != 0) {
            this.teleport();
        }
        return $$6;
    }

    private boolean hurtWithCleanWater(DamageSource $$0, ThrownPotion $$1, float $$2) {
        boolean $$6;
        ItemStack $$3 = $$1.getItem();
        Potion $$4 = PotionUtils.getPotion($$3);
        List<MobEffectInstance> $$5 = PotionUtils.getMobEffects($$3);
        boolean bl = $$6 = $$4 == Potions.WATER && $$5.isEmpty();
        if ($$6) {
            return super.hurt($$0, $$2);
        }
        return false;
    }

    public boolean isCreepy() {
        return this.entityData.get(DATA_CREEPY);
    }

    public boolean hasBeenStaredAt() {
        return this.entityData.get(DATA_STARED_AT);
    }

    public void setBeingStaredAt() {
        this.entityData.set(DATA_STARED_AT, true);
    }

    @Override
    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence() || this.getCarriedBlock() != null;
    }

    static class EndermanFreezeWhenLookedAt
    extends Goal {
        private final EnderMan enderman;
        @Nullable
        private LivingEntity target;

        public EndermanFreezeWhenLookedAt(EnderMan $$0) {
            this.enderman = $$0;
            this.setFlags((EnumSet<Goal.Flag>)EnumSet.of((Enum)Goal.Flag.JUMP, (Enum)Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            this.target = this.enderman.getTarget();
            if (!(this.target instanceof Player)) {
                return false;
            }
            double $$0 = this.target.distanceToSqr(this.enderman);
            if ($$0 > 256.0) {
                return false;
            }
            return this.enderman.isLookingAtMe((Player)this.target);
        }

        @Override
        public void start() {
            this.enderman.getNavigation().stop();
        }

        @Override
        public void tick() {
            this.enderman.getLookControl().setLookAt(this.target.getX(), this.target.getEyeY(), this.target.getZ());
        }
    }

    static class EndermanLeaveBlockGoal
    extends Goal {
        private final EnderMan enderman;

        public EndermanLeaveBlockGoal(EnderMan $$0) {
            this.enderman = $$0;
        }

        @Override
        public boolean canUse() {
            if (this.enderman.getCarriedBlock() == null) {
                return false;
            }
            if (!this.enderman.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                return false;
            }
            return this.enderman.getRandom().nextInt(EndermanLeaveBlockGoal.reducedTickDelay(2000)) == 0;
        }

        @Override
        public void tick() {
            RandomSource $$0 = this.enderman.getRandom();
            Level $$1 = this.enderman.level;
            int $$2 = Mth.floor(this.enderman.getX() - 1.0 + $$0.nextDouble() * 2.0);
            int $$3 = Mth.floor(this.enderman.getY() + $$0.nextDouble() * 2.0);
            int $$4 = Mth.floor(this.enderman.getZ() - 1.0 + $$0.nextDouble() * 2.0);
            BlockPos $$5 = new BlockPos($$2, $$3, $$4);
            BlockState $$6 = $$1.getBlockState($$5);
            Vec3i $$7 = $$5.below();
            BlockState $$8 = $$1.getBlockState((BlockPos)$$7);
            BlockState $$9 = this.enderman.getCarriedBlock();
            if ($$9 == null) {
                return;
            }
            if (this.canPlaceBlock($$1, $$5, $$9 = Block.updateFromNeighbourShapes($$9, this.enderman.level, $$5), $$6, $$8, (BlockPos)$$7)) {
                $$1.setBlock($$5, $$9, 3);
                $$1.gameEvent(GameEvent.BLOCK_PLACE, $$5, GameEvent.Context.of(this.enderman, $$9));
                this.enderman.setCarriedBlock(null);
            }
        }

        private boolean canPlaceBlock(Level $$0, BlockPos $$1, BlockState $$2, BlockState $$3, BlockState $$4, BlockPos $$5) {
            return $$3.isAir() && !$$4.isAir() && !$$4.is(Blocks.BEDROCK) && $$4.isCollisionShapeFullBlock($$0, $$5) && $$2.canSurvive($$0, $$1) && $$0.getEntities(this.enderman, AABB.unitCubeFromLowerCorner(Vec3.atLowerCornerOf($$1))).isEmpty();
        }
    }

    static class EndermanTakeBlockGoal
    extends Goal {
        private final EnderMan enderman;

        public EndermanTakeBlockGoal(EnderMan $$0) {
            this.enderman = $$0;
        }

        @Override
        public boolean canUse() {
            if (this.enderman.getCarriedBlock() != null) {
                return false;
            }
            if (!this.enderman.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                return false;
            }
            return this.enderman.getRandom().nextInt(EndermanTakeBlockGoal.reducedTickDelay(20)) == 0;
        }

        @Override
        public void tick() {
            RandomSource $$0 = this.enderman.getRandom();
            Level $$1 = this.enderman.level;
            int $$2 = Mth.floor(this.enderman.getX() - 2.0 + $$0.nextDouble() * 4.0);
            int $$3 = Mth.floor(this.enderman.getY() + $$0.nextDouble() * 3.0);
            int $$4 = Mth.floor(this.enderman.getZ() - 2.0 + $$0.nextDouble() * 4.0);
            BlockPos $$5 = new BlockPos($$2, $$3, $$4);
            BlockState $$6 = $$1.getBlockState($$5);
            Vec3 $$7 = new Vec3((double)this.enderman.getBlockX() + 0.5, (double)$$3 + 0.5, (double)this.enderman.getBlockZ() + 0.5);
            Vec3 $$8 = new Vec3((double)$$2 + 0.5, (double)$$3 + 0.5, (double)$$4 + 0.5);
            BlockHitResult $$9 = $$1.clip(new ClipContext($$7, $$8, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, this.enderman));
            boolean $$10 = $$9.getBlockPos().equals($$5);
            if ($$6.is(BlockTags.ENDERMAN_HOLDABLE) && $$10) {
                $$1.removeBlock($$5, false);
                $$1.gameEvent(GameEvent.BLOCK_DESTROY, $$5, GameEvent.Context.of(this.enderman, $$6));
                this.enderman.setCarriedBlock($$6.getBlock().defaultBlockState());
            }
        }
    }

    static class EndermanLookForPlayerGoal
    extends NearestAttackableTargetGoal<Player> {
        private final EnderMan enderman;
        @Nullable
        private Player pendingTarget;
        private int aggroTime;
        private int teleportTime;
        private final TargetingConditions startAggroTargetConditions;
        private final TargetingConditions continueAggroTargetConditions = TargetingConditions.forCombat().ignoreLineOfSight();
        private final Predicate<LivingEntity> isAngerInducing;

        public EndermanLookForPlayerGoal(EnderMan $$0, @Nullable Predicate<LivingEntity> $$12) {
            super($$0, Player.class, 10, false, false, $$12);
            this.enderman = $$0;
            this.isAngerInducing = $$1 -> $$0.isLookingAtMe((Player)$$1) || $$0.isAngryAt((LivingEntity)$$1);
            this.startAggroTargetConditions = TargetingConditions.forCombat().range(this.getFollowDistance()).selector(this.isAngerInducing);
        }

        @Override
        public boolean canUse() {
            this.pendingTarget = this.enderman.level.getNearestPlayer(this.startAggroTargetConditions, this.enderman);
            return this.pendingTarget != null;
        }

        @Override
        public void start() {
            this.aggroTime = this.adjustedTickDelay(5);
            this.teleportTime = 0;
            this.enderman.setBeingStaredAt();
        }

        @Override
        public void stop() {
            this.pendingTarget = null;
            super.stop();
        }

        @Override
        public boolean canContinueToUse() {
            if (this.pendingTarget != null) {
                if (!this.isAngerInducing.test((Object)this.pendingTarget)) {
                    return false;
                }
                this.enderman.lookAt(this.pendingTarget, 10.0f, 10.0f);
                return true;
            }
            if (this.target != null && this.continueAggroTargetConditions.test(this.enderman, this.target)) {
                return true;
            }
            return super.canContinueToUse();
        }

        @Override
        public void tick() {
            if (this.enderman.getTarget() == null) {
                super.setTarget(null);
            }
            if (this.pendingTarget != null) {
                if (--this.aggroTime <= 0) {
                    this.target = this.pendingTarget;
                    this.pendingTarget = null;
                    super.start();
                }
            } else {
                if (this.target != null && !this.enderman.isPassenger()) {
                    if (this.enderman.isLookingAtMe((Player)this.target)) {
                        if (this.target.distanceToSqr(this.enderman) < 16.0) {
                            this.enderman.teleport();
                        }
                        this.teleportTime = 0;
                    } else if (this.target.distanceToSqr(this.enderman) > 256.0 && this.teleportTime++ >= this.adjustedTickDelay(30) && this.enderman.teleportTowards(this.target)) {
                        this.teleportTime = 0;
                    }
                }
                super.tick();
            }
        }
    }
}