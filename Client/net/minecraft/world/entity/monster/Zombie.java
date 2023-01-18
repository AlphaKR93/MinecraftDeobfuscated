/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Boolean
 *  java.lang.Class
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.time.LocalDate
 *  java.time.temporal.ChronoField
 *  java.time.temporal.TemporalField
 *  java.util.List
 *  java.util.UUID
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.monster;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreakDoorGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveThroughVillageGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RemoveBlockGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class Zombie
extends Monster {
    private static final UUID SPEED_MODIFIER_BABY_UUID = UUID.fromString((String)"B9766B59-9566-4402-BC1F-2EE2A276D836");
    private static final AttributeModifier SPEED_MODIFIER_BABY = new AttributeModifier(SPEED_MODIFIER_BABY_UUID, "Baby speed boost", 0.5, AttributeModifier.Operation.MULTIPLY_BASE);
    private static final EntityDataAccessor<Boolean> DATA_BABY_ID = SynchedEntityData.defineId(Zombie.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_SPECIAL_TYPE_ID = SynchedEntityData.defineId(Zombie.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_DROWNED_CONVERSION_ID = SynchedEntityData.defineId(Zombie.class, EntityDataSerializers.BOOLEAN);
    public static final float ZOMBIE_LEADER_CHANCE = 0.05f;
    public static final int REINFORCEMENT_ATTEMPTS = 50;
    public static final int REINFORCEMENT_RANGE_MAX = 40;
    public static final int REINFORCEMENT_RANGE_MIN = 7;
    protected static final float BABY_EYE_HEIGHT_ADJUSTMENT = 0.81f;
    private static final float BREAK_DOOR_CHANCE = 0.1f;
    private static final Predicate<Difficulty> DOOR_BREAKING_PREDICATE = $$0 -> $$0 == Difficulty.HARD;
    private final BreakDoorGoal breakDoorGoal = new BreakDoorGoal(this, DOOR_BREAKING_PREDICATE);
    private boolean canBreakDoors;
    private int inWaterTime;
    private int conversionTime;

    public Zombie(EntityType<? extends Zombie> $$0, Level $$1) {
        super((EntityType<? extends Monster>)$$0, $$1);
    }

    public Zombie(Level $$0) {
        this((EntityType<? extends Zombie>)EntityType.ZOMBIE, $$0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(4, new ZombieAttackTurtleEggGoal((PathfinderMob)this, 1.0, 3));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.addBehaviourGoals();
    }

    protected void addBehaviourGoals() {
        this.goalSelector.addGoal(2, new ZombieAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(6, new MoveThroughVillageGoal(this, 1.0, true, 4, this::canBreakDoors));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]).setAlertOthers(ZombifiedPiglin.class));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<Player>((Mob)this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<AbstractVillager>((Mob)this, AbstractVillager.class, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<IronGolem>((Mob)this, IronGolem.class, true));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<Turtle>(this, Turtle.class, 10, true, false, Turtle.BABY_ON_LAND_SELECTOR));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.FOLLOW_RANGE, 35.0).add(Attributes.MOVEMENT_SPEED, 0.23f).add(Attributes.ATTACK_DAMAGE, 3.0).add(Attributes.ARMOR, 2.0).add(Attributes.SPAWN_REINFORCEMENTS_CHANCE);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(DATA_BABY_ID, false);
        this.getEntityData().define(DATA_SPECIAL_TYPE_ID, 0);
        this.getEntityData().define(DATA_DROWNED_CONVERSION_ID, false);
    }

    public boolean isUnderWaterConverting() {
        return this.getEntityData().get(DATA_DROWNED_CONVERSION_ID);
    }

    public boolean canBreakDoors() {
        return this.canBreakDoors;
    }

    public void setCanBreakDoors(boolean $$0) {
        if (this.supportsBreakDoorGoal() && GoalUtils.hasGroundPathNavigation(this)) {
            if (this.canBreakDoors != $$0) {
                this.canBreakDoors = $$0;
                ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors($$0);
                if ($$0) {
                    this.goalSelector.addGoal(1, this.breakDoorGoal);
                } else {
                    this.goalSelector.removeGoal(this.breakDoorGoal);
                }
            }
        } else if (this.canBreakDoors) {
            this.goalSelector.removeGoal(this.breakDoorGoal);
            this.canBreakDoors = false;
        }
    }

    protected boolean supportsBreakDoorGoal() {
        return true;
    }

    @Override
    public boolean isBaby() {
        return this.getEntityData().get(DATA_BABY_ID);
    }

    @Override
    public int getExperienceReward() {
        if (this.isBaby()) {
            this.xpReward = (int)((double)this.xpReward * 2.5);
        }
        return super.getExperienceReward();
    }

    @Override
    public void setBaby(boolean $$0) {
        this.getEntityData().set(DATA_BABY_ID, $$0);
        if (this.level != null && !this.level.isClientSide) {
            AttributeInstance $$1 = this.getAttribute(Attributes.MOVEMENT_SPEED);
            $$1.removeModifier(SPEED_MODIFIER_BABY);
            if ($$0) {
                $$1.addTransientModifier(SPEED_MODIFIER_BABY);
            }
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> $$0) {
        if (DATA_BABY_ID.equals($$0)) {
            this.refreshDimensions();
        }
        super.onSyncedDataUpdated($$0);
    }

    protected boolean convertsInWater() {
        return true;
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide && this.isAlive() && !this.isNoAi()) {
            if (this.isUnderWaterConverting()) {
                --this.conversionTime;
                if (this.conversionTime < 0) {
                    this.doUnderWaterConversion();
                }
            } else if (this.convertsInWater()) {
                if (this.isEyeInFluid(FluidTags.WATER)) {
                    ++this.inWaterTime;
                    if (this.inWaterTime >= 600) {
                        this.startUnderWaterConversion(300);
                    }
                } else {
                    this.inWaterTime = -1;
                }
            }
        }
        super.tick();
    }

    @Override
    public void aiStep() {
        if (this.isAlive()) {
            boolean $$0;
            boolean bl = $$0 = this.isSunSensitive() && this.isSunBurnTick();
            if ($$0) {
                ItemStack $$1 = this.getItemBySlot(EquipmentSlot.HEAD);
                if (!$$1.isEmpty()) {
                    if ($$1.isDamageableItem()) {
                        $$1.setDamageValue($$1.getDamageValue() + this.random.nextInt(2));
                        if ($$1.getDamageValue() >= $$1.getMaxDamage()) {
                            this.broadcastBreakEvent(EquipmentSlot.HEAD);
                            this.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
                        }
                    }
                    $$0 = false;
                }
                if ($$0) {
                    this.setSecondsOnFire(8);
                }
            }
        }
        super.aiStep();
    }

    private void startUnderWaterConversion(int $$0) {
        this.conversionTime = $$0;
        this.getEntityData().set(DATA_DROWNED_CONVERSION_ID, true);
    }

    protected void doUnderWaterConversion() {
        this.convertToZombieType(EntityType.DROWNED);
        if (!this.isSilent()) {
            this.level.levelEvent(null, 1040, this.blockPosition(), 0);
        }
    }

    protected void convertToZombieType(EntityType<? extends Zombie> $$0) {
        Zombie $$1 = this.convertTo($$0, true);
        if ($$1 != null) {
            $$1.handleAttributes($$1.level.getCurrentDifficultyAt($$1.blockPosition()).getSpecialMultiplier());
            $$1.setCanBreakDoors($$1.supportsBreakDoorGoal() && this.canBreakDoors());
        }
    }

    protected boolean isSunSensitive() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource $$0, float $$1) {
        if (!super.hurt($$0, $$1)) {
            return false;
        }
        if (!(this.level instanceof ServerLevel)) {
            return false;
        }
        ServerLevel $$2 = (ServerLevel)this.level;
        LivingEntity $$3 = this.getTarget();
        if ($$3 == null && $$0.getEntity() instanceof LivingEntity) {
            $$3 = (LivingEntity)$$0.getEntity();
        }
        if ($$3 != null && this.level.getDifficulty() == Difficulty.HARD && (double)this.random.nextFloat() < this.getAttributeValue(Attributes.SPAWN_REINFORCEMENTS_CHANCE) && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
            int $$4 = Mth.floor(this.getX());
            int $$5 = Mth.floor(this.getY());
            int $$6 = Mth.floor(this.getZ());
            Zombie $$7 = new Zombie(this.level);
            for (int $$8 = 0; $$8 < 50; ++$$8) {
                int $$9 = $$4 + Mth.nextInt(this.random, 7, 40) * Mth.nextInt(this.random, -1, 1);
                int $$10 = $$5 + Mth.nextInt(this.random, 7, 40) * Mth.nextInt(this.random, -1, 1);
                int $$11 = $$6 + Mth.nextInt(this.random, 7, 40) * Mth.nextInt(this.random, -1, 1);
                BlockPos $$12 = new BlockPos($$9, $$10, $$11);
                EntityType<?> $$13 = $$7.getType();
                SpawnPlacements.Type $$14 = SpawnPlacements.getPlacementType($$13);
                if (!NaturalSpawner.isSpawnPositionOk($$14, this.level, $$12, $$13) || !SpawnPlacements.checkSpawnRules($$13, $$2, MobSpawnType.REINFORCEMENT, $$12, this.level.random)) continue;
                $$7.setPos($$9, $$10, $$11);
                if (this.level.hasNearbyAlivePlayer($$9, $$10, $$11, 7.0) || !this.level.isUnobstructed($$7) || !this.level.noCollision($$7) || this.level.containsAnyLiquid($$7.getBoundingBox())) continue;
                $$7.setTarget($$3);
                $$7.finalizeSpawn($$2, this.level.getCurrentDifficultyAt($$7.blockPosition()), MobSpawnType.REINFORCEMENT, null, null);
                $$2.addFreshEntityWithPassengers($$7);
                this.getAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE).addPermanentModifier(new AttributeModifier("Zombie reinforcement caller charge", -0.05f, AttributeModifier.Operation.ADDITION));
                $$7.getAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE).addPermanentModifier(new AttributeModifier("Zombie reinforcement callee charge", -0.05f, AttributeModifier.Operation.ADDITION));
                break;
            }
        }
        return true;
    }

    @Override
    public boolean doHurtTarget(Entity $$0) {
        boolean $$1 = super.doHurtTarget($$0);
        if ($$1) {
            float $$2 = this.level.getCurrentDifficultyAt(this.blockPosition()).getEffectiveDifficulty();
            if (this.getMainHandItem().isEmpty() && this.isOnFire() && this.random.nextFloat() < $$2 * 0.3f) {
                $$0.setSecondsOnFire(2 * (int)$$2);
            }
        }
        return $$1;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ZOMBIE_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource $$0) {
        return SoundEvents.ZOMBIE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ZOMBIE_DEATH;
    }

    protected SoundEvent getStepSound() {
        return SoundEvents.ZOMBIE_STEP;
    }

    @Override
    protected void playStepSound(BlockPos $$0, BlockState $$1) {
        this.playSound(this.getStepSound(), 0.15f, 1.0f);
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource $$0, DifficultyInstance $$1) {
        super.populateDefaultEquipmentSlots($$0, $$1);
        float f = $$0.nextFloat();
        float f2 = this.level.getDifficulty() == Difficulty.HARD ? 0.05f : 0.01f;
        if (f < f2) {
            int $$2 = $$0.nextInt(3);
            if ($$2 == 0) {
                this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
            } else {
                this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SHOVEL));
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        super.addAdditionalSaveData($$0);
        $$0.putBoolean("IsBaby", this.isBaby());
        $$0.putBoolean("CanBreakDoors", this.canBreakDoors());
        $$0.putInt("InWaterTime", this.isInWater() ? this.inWaterTime : -1);
        $$0.putInt("DrownedConversionTime", this.isUnderWaterConverting() ? this.conversionTime : -1);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$0) {
        super.readAdditionalSaveData($$0);
        this.setBaby($$0.getBoolean("IsBaby"));
        this.setCanBreakDoors($$0.getBoolean("CanBreakDoors"));
        this.inWaterTime = $$0.getInt("InWaterTime");
        if ($$0.contains("DrownedConversionTime", 99) && $$0.getInt("DrownedConversionTime") > -1) {
            this.startUnderWaterConversion($$0.getInt("DrownedConversionTime"));
        }
    }

    @Override
    public boolean wasKilled(ServerLevel $$0, LivingEntity $$1) {
        boolean $$2 = super.wasKilled($$0, $$1);
        if (($$0.getDifficulty() == Difficulty.NORMAL || $$0.getDifficulty() == Difficulty.HARD) && $$1 instanceof Villager) {
            Villager $$3 = (Villager)$$1;
            if ($$0.getDifficulty() != Difficulty.HARD && this.random.nextBoolean()) {
                return $$2;
            }
            ZombieVillager $$4 = $$3.convertTo(EntityType.ZOMBIE_VILLAGER, false);
            if ($$4 != null) {
                $$4.finalizeSpawn($$0, $$0.getCurrentDifficultyAt($$4.blockPosition()), MobSpawnType.CONVERSION, new ZombieGroupData(false, true), null);
                $$4.setVillagerData($$3.getVillagerData());
                $$4.setGossips($$3.getGossips().store(NbtOps.INSTANCE));
                $$4.setTradeOffers($$3.getOffers().createTag());
                $$4.setVillagerXp($$3.getVillagerXp());
                if (!this.isSilent()) {
                    $$0.levelEvent(null, 1026, this.blockPosition(), 0);
                }
                $$2 = false;
            }
        }
        return $$2;
    }

    @Override
    protected float getStandingEyeHeight(Pose $$0, EntityDimensions $$1) {
        return this.isBaby() ? 0.93f : 1.74f;
    }

    @Override
    public boolean canHoldItem(ItemStack $$0) {
        if ($$0.is(Items.EGG) && this.isBaby() && this.isPassenger()) {
            return false;
        }
        return super.canHoldItem($$0);
    }

    @Override
    public boolean wantsToPickUp(ItemStack $$0) {
        if ($$0.is(Items.GLOW_INK_SAC)) {
            return false;
        }
        return super.wantsToPickUp($$0);
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor $$0, DifficultyInstance $$1, MobSpawnType $$2, @Nullable SpawnGroupData $$3, @Nullable CompoundTag $$4) {
        RandomSource $$5 = $$0.getRandom();
        $$3 = super.finalizeSpawn($$0, $$1, $$2, $$3, $$4);
        float $$6 = $$1.getSpecialMultiplier();
        this.setCanPickUpLoot($$5.nextFloat() < 0.55f * $$6);
        if ($$3 == null) {
            $$3 = new ZombieGroupData(Zombie.getSpawnAsBabyOdds($$5), true);
        }
        if ($$3 instanceof ZombieGroupData) {
            ZombieGroupData $$7 = (ZombieGroupData)$$3;
            if ($$7.isBaby) {
                this.setBaby(true);
                if ($$7.canSpawnJockey) {
                    Chicken $$10;
                    if ((double)$$5.nextFloat() < 0.05) {
                        List $$8 = $$0.getEntitiesOfClass(Chicken.class, this.getBoundingBox().inflate(5.0, 3.0, 5.0), EntitySelector.ENTITY_NOT_BEING_RIDDEN);
                        if (!$$8.isEmpty()) {
                            Chicken $$9 = (Chicken)$$8.get(0);
                            $$9.setChickenJockey(true);
                            this.startRiding($$9);
                        }
                    } else if ((double)$$5.nextFloat() < 0.05 && ($$10 = EntityType.CHICKEN.create(this.level)) != null) {
                        $$10.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0f);
                        $$10.finalizeSpawn($$0, $$1, MobSpawnType.JOCKEY, null, null);
                        $$10.setChickenJockey(true);
                        this.startRiding($$10);
                        $$0.addFreshEntity($$10);
                    }
                }
            }
            this.setCanBreakDoors(this.supportsBreakDoorGoal() && $$5.nextFloat() < $$6 * 0.1f);
            this.populateDefaultEquipmentSlots($$5, $$1);
            this.populateDefaultEquipmentEnchantments($$5, $$1);
        }
        if (this.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
            LocalDate $$11 = LocalDate.now();
            int $$12 = $$11.get((TemporalField)ChronoField.DAY_OF_MONTH);
            int $$13 = $$11.get((TemporalField)ChronoField.MONTH_OF_YEAR);
            if ($$13 == 10 && $$12 == 31 && $$5.nextFloat() < 0.25f) {
                this.setItemSlot(EquipmentSlot.HEAD, new ItemStack($$5.nextFloat() < 0.1f ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN));
                this.armorDropChances[EquipmentSlot.HEAD.getIndex()] = 0.0f;
            }
        }
        this.handleAttributes($$6);
        return $$3;
    }

    public static boolean getSpawnAsBabyOdds(RandomSource $$0) {
        return $$0.nextFloat() < 0.05f;
    }

    protected void handleAttributes(float $$0) {
        this.randomizeReinforcementsChance();
        this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).addPermanentModifier(new AttributeModifier("Random spawn bonus", this.random.nextDouble() * (double)0.05f, AttributeModifier.Operation.ADDITION));
        double $$1 = this.random.nextDouble() * 1.5 * (double)$$0;
        if ($$1 > 1.0) {
            this.getAttribute(Attributes.FOLLOW_RANGE).addPermanentModifier(new AttributeModifier("Random zombie-spawn bonus", $$1, AttributeModifier.Operation.MULTIPLY_TOTAL));
        }
        if (this.random.nextFloat() < $$0 * 0.05f) {
            this.getAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE).addPermanentModifier(new AttributeModifier("Leader zombie bonus", this.random.nextDouble() * 0.25 + 0.5, AttributeModifier.Operation.ADDITION));
            this.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(new AttributeModifier("Leader zombie bonus", this.random.nextDouble() * 3.0 + 1.0, AttributeModifier.Operation.MULTIPLY_TOTAL));
            this.setCanBreakDoors(this.supportsBreakDoorGoal());
        }
    }

    protected void randomizeReinforcementsChance() {
        this.getAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE).setBaseValue(this.random.nextDouble() * (double)0.1f);
    }

    @Override
    public double getMyRidingOffset() {
        return this.isBaby() ? 0.0 : -0.45;
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource $$0, int $$1, boolean $$2) {
        ItemStack $$5;
        Creeper $$4;
        super.dropCustomDeathLoot($$0, $$1, $$2);
        Entity $$3 = $$0.getEntity();
        if ($$3 instanceof Creeper && ($$4 = (Creeper)$$3).canDropMobsSkull() && !($$5 = this.getSkull()).isEmpty()) {
            $$4.increaseDroppedSkulls();
            this.spawnAtLocation($$5);
        }
    }

    protected ItemStack getSkull() {
        return new ItemStack(Items.ZOMBIE_HEAD);
    }

    class ZombieAttackTurtleEggGoal
    extends RemoveBlockGoal {
        ZombieAttackTurtleEggGoal(PathfinderMob $$0, double $$1, int $$2) {
            super(Blocks.TURTLE_EGG, $$0, $$1, $$2);
        }

        @Override
        public void playDestroyProgressSound(LevelAccessor $$0, BlockPos $$1) {
            $$0.playSound(null, $$1, SoundEvents.ZOMBIE_DESTROY_EGG, SoundSource.HOSTILE, 0.5f, 0.9f + Zombie.this.random.nextFloat() * 0.2f);
        }

        @Override
        public void playBreakSound(Level $$0, BlockPos $$1) {
            $$0.playSound(null, $$1, SoundEvents.TURTLE_EGG_BREAK, SoundSource.BLOCKS, 0.7f, 0.9f + $$0.random.nextFloat() * 0.2f);
        }

        @Override
        public double acceptedDistance() {
            return 1.14;
        }
    }

    public static class ZombieGroupData
    implements SpawnGroupData {
        public final boolean isBaby;
        public final boolean canSpawnJockey;

        public ZombieGroupData(boolean $$0, boolean $$1) {
            this.isBaby = $$0;
            this.canSpawnJockey = $$1;
        }
    }
}