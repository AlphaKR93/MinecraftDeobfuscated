/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Maps
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.Dynamic
 *  java.lang.Boolean
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Arrays
 *  java.util.List
 *  java.util.Map
 *  java.util.Optional
 *  java.util.function.IntFunction
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 *  org.joml.Vector3f
 */
package net.minecraft.world.entity.animal.axolotl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LerpingModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.animal.axolotl.AxolotlAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class Axolotl
extends Animal
implements LerpingModel,
VariantHolder<Variant>,
Bucketable {
    public static final int TOTAL_PLAYDEAD_TIME = 200;
    protected static final ImmutableList<? extends SensorType<? extends Sensor<? super Axolotl>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_ADULT, SensorType.HURT_BY, SensorType.AXOLOTL_ATTACKABLES, SensorType.AXOLOTL_TEMPTATIONS);
    protected static final ImmutableList<? extends MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.BREED_TARGET, MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER, MemoryModuleType.LOOK_TARGET, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.ATTACK_TARGET, MemoryModuleType.ATTACK_COOLING_DOWN, MemoryModuleType.NEAREST_VISIBLE_ADULT, (Object[])new MemoryModuleType[]{MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.PLAY_DEAD_TICKS, MemoryModuleType.NEAREST_ATTACKABLE, MemoryModuleType.TEMPTING_PLAYER, MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, MemoryModuleType.IS_TEMPTED, MemoryModuleType.HAS_HUNTING_COOLDOWN, MemoryModuleType.IS_PANICKING});
    private static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(Axolotl.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_PLAYING_DEAD = SynchedEntityData.defineId(Axolotl.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.defineId(Axolotl.class, EntityDataSerializers.BOOLEAN);
    public static final double PLAYER_REGEN_DETECTION_RANGE = 20.0;
    public static final int RARE_VARIANT_CHANCE = 1200;
    private static final int AXOLOTL_TOTAL_AIR_SUPPLY = 6000;
    public static final String VARIANT_TAG = "Variant";
    private static final int REHYDRATE_AIR_SUPPLY = 1800;
    private static final int REGEN_BUFF_MAX_DURATION = 2400;
    private final Map<String, Vector3f> modelRotationValues = Maps.newHashMap();
    private static final int REGEN_BUFF_BASE_DURATION = 100;

    public Axolotl(EntityType<? extends Axolotl> $$0, Level $$1) {
        super((EntityType<? extends Animal>)$$0, $$1);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0f);
        this.moveControl = new AxolotlMoveControl(this);
        this.lookControl = new AxolotlLookControl(this, 20);
        this.maxUpStep = 1.0f;
    }

    @Override
    public Map<String, Vector3f> getModelRotationValues() {
        return this.modelRotationValues;
    }

    @Override
    public float getWalkTargetValue(BlockPos $$0, LevelReader $$1) {
        return 0.0f;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_VARIANT, 0);
        this.entityData.define(DATA_PLAYING_DEAD, false);
        this.entityData.define(FROM_BUCKET, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        super.addAdditionalSaveData($$0);
        $$0.putInt(VARIANT_TAG, this.getVariant().getId());
        $$0.putBoolean("FromBucket", this.fromBucket());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$0) {
        super.readAdditionalSaveData($$0);
        this.setVariant(Variant.byId($$0.getInt(VARIANT_TAG)));
        this.setFromBucket($$0.getBoolean("FromBucket"));
    }

    @Override
    public void playAmbientSound() {
        if (this.isPlayingDead()) {
            return;
        }
        super.playAmbientSound();
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor $$0, DifficultyInstance $$1, MobSpawnType $$2, @Nullable SpawnGroupData $$3, @Nullable CompoundTag $$4) {
        boolean $$5 = false;
        if ($$2 == MobSpawnType.BUCKET) {
            return $$3;
        }
        RandomSource $$6 = $$0.getRandom();
        if ($$3 instanceof AxolotlGroupData) {
            if (((AxolotlGroupData)$$3).getGroupSize() >= 2) {
                $$5 = true;
            }
        } else {
            $$3 = new AxolotlGroupData(Variant.getCommonSpawnVariant($$6), Variant.getCommonSpawnVariant($$6));
        }
        this.setVariant(((AxolotlGroupData)$$3).getVariant($$6));
        if ($$5) {
            this.setAge(-24000);
        }
        return super.finalizeSpawn($$0, $$1, $$2, $$3, $$4);
    }

    @Override
    public void baseTick() {
        int $$0 = this.getAirSupply();
        super.baseTick();
        if (!this.isNoAi()) {
            this.handleAirSupply($$0);
        }
    }

    protected void handleAirSupply(int $$0) {
        if (this.isAlive() && !this.isInWaterRainOrBubble()) {
            this.setAirSupply($$0 - 1);
            if (this.getAirSupply() == -20) {
                this.setAirSupply(0);
                this.hurt(DamageSource.DRY_OUT, 2.0f);
            }
        } else {
            this.setAirSupply(this.getMaxAirSupply());
        }
    }

    public void rehydrate() {
        int $$0 = this.getAirSupply() + 1800;
        this.setAirSupply(Math.min((int)$$0, (int)this.getMaxAirSupply()));
    }

    @Override
    public int getMaxAirSupply() {
        return 6000;
    }

    @Override
    public Variant getVariant() {
        return Variant.byId(this.entityData.get(DATA_VARIANT));
    }

    @Override
    public void setVariant(Variant $$0) {
        this.entityData.set(DATA_VARIANT, $$0.getId());
    }

    private static boolean useRareVariant(RandomSource $$0) {
        return $$0.nextInt(1200) == 0;
    }

    @Override
    public boolean checkSpawnObstruction(LevelReader $$0) {
        return $$0.isUnobstructed(this);
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public MobType getMobType() {
        return MobType.WATER;
    }

    public void setPlayingDead(boolean $$0) {
        this.entityData.set(DATA_PLAYING_DEAD, $$0);
    }

    public boolean isPlayingDead() {
        return this.entityData.get(DATA_PLAYING_DEAD);
    }

    @Override
    public boolean fromBucket() {
        return this.entityData.get(FROM_BUCKET);
    }

    @Override
    public void setFromBucket(boolean $$0) {
        this.entityData.set(FROM_BUCKET, $$0);
    }

    @Override
    @Nullable
    public AgeableMob getBreedOffspring(ServerLevel $$0, AgeableMob $$1) {
        Axolotl $$2 = EntityType.AXOLOTL.create($$0);
        if ($$2 != null) {
            Variant $$4;
            if (Axolotl.useRareVariant(this.random)) {
                Variant $$3 = Variant.getRareSpawnVariant(this.random);
            } else {
                $$4 = this.random.nextBoolean() ? this.getVariant() : ((Axolotl)$$1).getVariant();
            }
            $$2.setVariant($$4);
            $$2.setPersistenceRequired();
        }
        return $$2;
    }

    @Override
    public double getMeleeAttackRangeSqr(LivingEntity $$0) {
        return 1.5 + (double)$$0.getBbWidth() * 2.0;
    }

    @Override
    public boolean isFood(ItemStack $$0) {
        return $$0.is(ItemTags.AXOLOTL_TEMPT_ITEMS);
    }

    @Override
    public boolean canBeLeashed(Player $$0) {
        return true;
    }

    @Override
    protected void customServerAiStep() {
        this.level.getProfiler().push("axolotlBrain");
        this.getBrain().tick((ServerLevel)this.level, this);
        this.level.getProfiler().pop();
        this.level.getProfiler().push("axolotlActivityUpdate");
        AxolotlAi.updateActivity(this);
        this.level.getProfiler().pop();
        if (!this.isNoAi()) {
            Optional<Integer> $$0 = this.getBrain().getMemory(MemoryModuleType.PLAY_DEAD_TICKS);
            this.setPlayingDead($$0.isPresent() && (Integer)$$0.get() > 0);
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 14.0).add(Attributes.MOVEMENT_SPEED, 1.0).add(Attributes.ATTACK_DAMAGE, 2.0);
    }

    @Override
    protected PathNavigation createNavigation(Level $$0) {
        return new AmphibiousPathNavigation(this, $$0);
    }

    @Override
    public boolean doHurtTarget(Entity $$0) {
        boolean $$1 = $$0.hurt(DamageSource.mobAttack(this), (int)this.getAttributeValue(Attributes.ATTACK_DAMAGE));
        if ($$1) {
            this.doEnchantDamageEffects(this, $$0);
            this.playSound(SoundEvents.AXOLOTL_ATTACK, 1.0f, 1.0f);
        }
        return $$1;
    }

    @Override
    public boolean hurt(DamageSource $$0, float $$1) {
        float $$2 = this.getHealth();
        if (!(this.level.isClientSide || this.isNoAi() || this.level.random.nextInt(3) != 0 || !((float)this.level.random.nextInt(3) < $$1) && !($$2 / this.getMaxHealth() < 0.5f) || !($$1 < $$2) || !this.isInWater() || $$0.getEntity() == null && $$0.getDirectEntity() == null || this.isPlayingDead())) {
            this.brain.setMemory(MemoryModuleType.PLAY_DEAD_TICKS, 200);
        }
        return super.hurt($$0, $$1);
    }

    @Override
    protected float getStandingEyeHeight(Pose $$0, EntityDimensions $$1) {
        return $$1.height * 0.655f;
    }

    @Override
    public int getMaxHeadXRot() {
        return 1;
    }

    @Override
    public int getMaxHeadYRot() {
        return 1;
    }

    @Override
    public InteractionResult mobInteract(Player $$0, InteractionHand $$1) {
        return (InteractionResult)((Object)Bucketable.bucketMobPickup($$0, $$1, this).orElse((Object)super.mobInteract($$0, $$1)));
    }

    @Override
    public void saveToBucketTag(ItemStack $$0) {
        Bucketable.saveDefaultDataToBucketTag(this, $$0);
        CompoundTag $$1 = $$0.getOrCreateTag();
        $$1.putInt(VARIANT_TAG, this.getVariant().getId());
        $$1.putInt("Age", this.getAge());
        Brain<Axolotl> $$2 = this.getBrain();
        if ($$2.hasMemoryValue(MemoryModuleType.HAS_HUNTING_COOLDOWN)) {
            $$1.putLong("HuntingCooldown", $$2.getTimeUntilExpiry(MemoryModuleType.HAS_HUNTING_COOLDOWN));
        }
    }

    @Override
    public void loadFromBucketTag(CompoundTag $$0) {
        Bucketable.loadDefaultDataFromBucketTag(this, $$0);
        this.setVariant(Variant.byId($$0.getInt(VARIANT_TAG)));
        if ($$0.contains("Age")) {
            this.setAge($$0.getInt("Age"));
        }
        if ($$0.contains("HuntingCooldown")) {
            this.getBrain().setMemoryWithExpiry(MemoryModuleType.HAS_HUNTING_COOLDOWN, true, $$0.getLong("HuntingCooldown"));
        }
    }

    @Override
    public ItemStack getBucketItemStack() {
        return new ItemStack(Items.AXOLOTL_BUCKET);
    }

    @Override
    public SoundEvent getPickupSound() {
        return SoundEvents.BUCKET_FILL_AXOLOTL;
    }

    @Override
    public boolean canBeSeenAsEnemy() {
        return !this.isPlayingDead() && super.canBeSeenAsEnemy();
    }

    public static void onStopAttacking(Axolotl $$0, LivingEntity $$1) {
        Entity $$4;
        DamageSource $$3;
        Level $$2 = $$0.level;
        if ($$1.isDeadOrDying() && ($$3 = $$1.getLastDamageSource()) != null && ($$4 = $$3.getEntity()) != null && $$4.getType() == EntityType.PLAYER) {
            Player $$5 = (Player)$$4;
            List $$6 = $$2.getEntitiesOfClass(Player.class, $$0.getBoundingBox().inflate(20.0));
            if ($$6.contains((Object)$$5)) {
                $$0.applySupportingEffects($$5);
            }
        }
    }

    public void applySupportingEffects(Player $$0) {
        MobEffectInstance $$1 = $$0.getEffect(MobEffects.REGENERATION);
        if ($$1 != null && $$1.endsWithin(2399)) {
            int $$2 = Math.min((int)2400, (int)(100 + $$1.getDuration()));
            $$0.addEffect(new MobEffectInstance(MobEffects.REGENERATION, $$2, 0), this);
        }
        $$0.removeEffect(MobEffects.DIG_SLOWDOWN);
    }

    @Override
    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence() || this.fromBucket();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource $$0) {
        return SoundEvents.AXOLOTL_HURT;
    }

    @Override
    @Nullable
    protected SoundEvent getDeathSound() {
        return SoundEvents.AXOLOTL_DEATH;
    }

    @Override
    @Nullable
    protected SoundEvent getAmbientSound() {
        return this.isInWater() ? SoundEvents.AXOLOTL_IDLE_WATER : SoundEvents.AXOLOTL_IDLE_AIR;
    }

    @Override
    protected SoundEvent getSwimSplashSound() {
        return SoundEvents.AXOLOTL_SPLASH;
    }

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.AXOLOTL_SWIM;
    }

    protected Brain.Provider<Axolotl> brainProvider() {
        return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> $$0) {
        return AxolotlAi.makeBrain(this.brainProvider().makeBrain($$0));
    }

    public Brain<Axolotl> getBrain() {
        return super.getBrain();
    }

    @Override
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        DebugPackets.sendEntityBrain(this);
    }

    @Override
    public void travel(Vec3 $$0) {
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), $$0);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
        } else {
            super.travel($$0);
        }
    }

    @Override
    protected void usePlayerItem(Player $$0, InteractionHand $$1, ItemStack $$2) {
        if ($$2.is(Items.TROPICAL_FISH_BUCKET)) {
            $$0.setItemInHand($$1, new ItemStack(Items.WATER_BUCKET));
        } else {
            super.usePlayerItem($$0, $$1, $$2);
        }
    }

    @Override
    public boolean removeWhenFarAway(double $$0) {
        return !this.fromBucket() && !this.hasCustomName();
    }

    public static boolean checkAxolotlSpawnRules(EntityType<? extends LivingEntity> $$0, ServerLevelAccessor $$1, MobSpawnType $$2, BlockPos $$3, RandomSource $$4) {
        return $$1.getBlockState((BlockPos)$$3.below()).is(BlockTags.AXOLOTLS_SPAWNABLE_ON);
    }

    static class AxolotlMoveControl
    extends SmoothSwimmingMoveControl {
        private final Axolotl axolotl;

        public AxolotlMoveControl(Axolotl $$0) {
            super($$0, 85, 10, 0.1f, 0.5f, false);
            this.axolotl = $$0;
        }

        @Override
        public void tick() {
            if (!this.axolotl.isPlayingDead()) {
                super.tick();
            }
        }
    }

    class AxolotlLookControl
    extends SmoothSwimmingLookControl {
        public AxolotlLookControl(Axolotl $$0, int $$1) {
            super($$0, $$1);
        }

        @Override
        public void tick() {
            if (!Axolotl.this.isPlayingDead()) {
                super.tick();
            }
        }
    }

    public static enum Variant implements StringRepresentable
    {
        LUCY(0, "lucy", true),
        WILD(1, "wild", true),
        GOLD(2, "gold", true),
        CYAN(3, "cyan", true),
        BLUE(4, "blue", false);

        private static final IntFunction<Variant> BY_ID;
        public static final Codec<Variant> CODEC;
        private final int id;
        private final String name;
        private final boolean common;

        private Variant(int $$0, String $$1, boolean $$2) {
            this.id = $$0;
            this.name = $$1;
            this.common = $$2;
        }

        public int getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        public static Variant byId(int $$0) {
            return (Variant)BY_ID.apply($$0);
        }

        public static Variant getCommonSpawnVariant(RandomSource $$0) {
            return Variant.getSpawnVariant($$0, true);
        }

        public static Variant getRareSpawnVariant(RandomSource $$0) {
            return Variant.getSpawnVariant($$0, false);
        }

        private static Variant getSpawnVariant(RandomSource $$0, boolean $$12) {
            Variant[] $$2 = (Variant[])Arrays.stream((Object[])Variant.values()).filter($$1 -> $$1.common == $$12).toArray(Variant[]::new);
            return Util.getRandom($$2, $$0);
        }

        static {
            BY_ID = ByIdMap.continuous(Variant::getId, Variant.values(), ByIdMap.OutOfBoundsStrategy.ZERO);
            CODEC = StringRepresentable.fromEnum((Supplier<E[]>)((Supplier)Variant::values));
        }
    }

    public static class AxolotlGroupData
    extends AgeableMob.AgeableMobGroupData {
        public final Variant[] types;

        public AxolotlGroupData(Variant ... $$0) {
            super(false);
            this.types = $$0;
        }

        public Variant getVariant(RandomSource $$0) {
            return this.types[$$0.nextInt(this.types.length)];
        }
    }
}