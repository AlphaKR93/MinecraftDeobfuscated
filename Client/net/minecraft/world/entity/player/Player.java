/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.mojang.authlib.GameProfile
 *  com.mojang.datafixers.util.Either
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.DynamicOps
 *  java.lang.Byte
 *  java.lang.Float
 *  java.lang.Integer
 *  java.lang.Iterable
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.Collection
 *  java.util.List
 *  java.util.Map
 *  java.util.Optional
 *  java.util.OptionalInt
 *  java.util.function.Predicate
 *  java.util.function.UnaryOperator
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world.entity.player;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DynamicOps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.Container;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Strider;
import net.minecraft.world.entity.monster.warden.WardenSpawnTracker;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;
import org.slf4j.Logger;

public abstract class Player
extends LivingEntity {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final int MAX_NAME_LENGTH = 16;
    public static final int MAX_HEALTH = 20;
    public static final int SLEEP_DURATION = 100;
    public static final int WAKE_UP_DURATION = 10;
    public static final int ENDER_SLOT_OFFSET = 200;
    public static final float CROUCH_BB_HEIGHT = 1.5f;
    public static final float SWIMMING_BB_WIDTH = 0.6f;
    public static final float SWIMMING_BB_HEIGHT = 0.6f;
    public static final float DEFAULT_EYE_HEIGHT = 1.62f;
    public static final EntityDimensions STANDING_DIMENSIONS = EntityDimensions.scalable(0.6f, 1.8f);
    private static final Map<Pose, EntityDimensions> POSES = ImmutableMap.builder().put((Object)Pose.STANDING, (Object)STANDING_DIMENSIONS).put((Object)Pose.SLEEPING, (Object)SLEEPING_DIMENSIONS).put((Object)Pose.FALL_FLYING, (Object)EntityDimensions.scalable(0.6f, 0.6f)).put((Object)Pose.SWIMMING, (Object)EntityDimensions.scalable(0.6f, 0.6f)).put((Object)Pose.SPIN_ATTACK, (Object)EntityDimensions.scalable(0.6f, 0.6f)).put((Object)Pose.CROUCHING, (Object)EntityDimensions.scalable(0.6f, 1.5f)).put((Object)Pose.DYING, (Object)EntityDimensions.fixed(0.2f, 0.2f)).build();
    private static final int FLY_ACHIEVEMENT_SPEED = 25;
    private static final EntityDataAccessor<Float> DATA_PLAYER_ABSORPTION_ID = SynchedEntityData.defineId(Player.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_SCORE_ID = SynchedEntityData.defineId(Player.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Byte> DATA_PLAYER_MODE_CUSTOMISATION = SynchedEntityData.defineId(Player.class, EntityDataSerializers.BYTE);
    protected static final EntityDataAccessor<Byte> DATA_PLAYER_MAIN_HAND = SynchedEntityData.defineId(Player.class, EntityDataSerializers.BYTE);
    protected static final EntityDataAccessor<CompoundTag> DATA_SHOULDER_LEFT = SynchedEntityData.defineId(Player.class, EntityDataSerializers.COMPOUND_TAG);
    protected static final EntityDataAccessor<CompoundTag> DATA_SHOULDER_RIGHT = SynchedEntityData.defineId(Player.class, EntityDataSerializers.COMPOUND_TAG);
    private long timeEntitySatOnShoulder;
    private final Inventory inventory = new Inventory(this);
    protected PlayerEnderChestContainer enderChestInventory = new PlayerEnderChestContainer();
    public final InventoryMenu inventoryMenu;
    public AbstractContainerMenu containerMenu;
    protected FoodData foodData = new FoodData();
    protected int jumpTriggerTime;
    public float oBob;
    public float bob;
    public int takeXpDelay;
    public double xCloakO;
    public double yCloakO;
    public double zCloakO;
    public double xCloak;
    public double yCloak;
    public double zCloak;
    private int sleepCounter;
    protected boolean wasUnderwater;
    private final Abilities abilities = new Abilities();
    public int experienceLevel;
    public int totalExperience;
    public float experienceProgress;
    protected int enchantmentSeed;
    protected final float defaultFlySpeed = 0.02f;
    private int lastLevelUpTime;
    private final GameProfile gameProfile;
    private boolean reducedDebugInfo;
    private ItemStack lastItemInMainHand = ItemStack.EMPTY;
    private final ItemCooldowns cooldowns = this.createItemCooldowns();
    private Optional<GlobalPos> lastDeathLocation = Optional.empty();
    @Nullable
    public FishingHook fishing;

    public Player(Level $$0, BlockPos $$1, float $$2, GameProfile $$3) {
        super((EntityType<? extends LivingEntity>)EntityType.PLAYER, $$0);
        this.setUUID(UUIDUtil.getOrCreatePlayerUUID($$3));
        this.gameProfile = $$3;
        this.inventoryMenu = new InventoryMenu(this.inventory, !$$0.isClientSide, this);
        this.containerMenu = this.inventoryMenu;
        this.moveTo((double)$$1.getX() + 0.5, $$1.getY() + 1, (double)$$1.getZ() + 0.5, $$2, 0.0f);
        this.rotOffs = 180.0f;
    }

    public boolean blockActionRestricted(Level $$0, BlockPos $$1, GameType $$2) {
        if (!$$2.isBlockPlacingRestricted()) {
            return false;
        }
        if ($$2 == GameType.SPECTATOR) {
            return true;
        }
        if (this.mayBuild()) {
            return false;
        }
        ItemStack $$3 = this.getMainHandItem();
        return $$3.isEmpty() || !$$3.hasAdventureModeBreakTagForBlock($$0.registryAccess().registryOrThrow(Registries.BLOCK), new BlockInWorld($$0, $$1, false));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes().add(Attributes.ATTACK_DAMAGE, 1.0).add(Attributes.MOVEMENT_SPEED, 0.1f).add(Attributes.ATTACK_SPEED).add(Attributes.LUCK);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_PLAYER_ABSORPTION_ID, Float.valueOf((float)0.0f));
        this.entityData.define(DATA_SCORE_ID, 0);
        this.entityData.define(DATA_PLAYER_MODE_CUSTOMISATION, (byte)0);
        this.entityData.define(DATA_PLAYER_MAIN_HAND, (byte)1);
        this.entityData.define(DATA_SHOULDER_LEFT, new CompoundTag());
        this.entityData.define(DATA_SHOULDER_RIGHT, new CompoundTag());
    }

    @Override
    public void tick() {
        this.noPhysics = this.isSpectator();
        if (this.isSpectator()) {
            this.onGround = false;
        }
        if (this.takeXpDelay > 0) {
            --this.takeXpDelay;
        }
        if (this.isSleeping()) {
            ++this.sleepCounter;
            if (this.sleepCounter > 100) {
                this.sleepCounter = 100;
            }
            if (!this.level.isClientSide && this.level.isDay()) {
                this.stopSleepInBed(false, true);
            }
        } else if (this.sleepCounter > 0) {
            ++this.sleepCounter;
            if (this.sleepCounter >= 110) {
                this.sleepCounter = 0;
            }
        }
        this.updateIsUnderwater();
        super.tick();
        if (!this.level.isClientSide && this.containerMenu != null && !this.containerMenu.stillValid(this)) {
            this.closeContainer();
            this.containerMenu = this.inventoryMenu;
        }
        this.moveCloak();
        if (!this.level.isClientSide) {
            this.foodData.tick(this);
            this.awardStat(Stats.PLAY_TIME);
            this.awardStat(Stats.TOTAL_WORLD_TIME);
            if (this.isAlive()) {
                this.awardStat(Stats.TIME_SINCE_DEATH);
            }
            if (this.isDiscrete()) {
                this.awardStat(Stats.CROUCH_TIME);
            }
            if (!this.isSleeping()) {
                this.awardStat(Stats.TIME_SINCE_REST);
            }
        }
        int $$0 = 29999999;
        double $$1 = Mth.clamp(this.getX(), -2.9999999E7, 2.9999999E7);
        double $$2 = Mth.clamp(this.getZ(), -2.9999999E7, 2.9999999E7);
        if ($$1 != this.getX() || $$2 != this.getZ()) {
            this.setPos($$1, this.getY(), $$2);
        }
        ++this.attackStrengthTicker;
        ItemStack $$3 = this.getMainHandItem();
        if (!ItemStack.matches(this.lastItemInMainHand, $$3)) {
            if (!ItemStack.isSame(this.lastItemInMainHand, $$3)) {
                this.resetAttackStrengthTicker();
            }
            this.lastItemInMainHand = $$3.copy();
        }
        this.turtleHelmetTick();
        this.cooldowns.tick();
        this.updatePlayerPose();
    }

    public boolean isSecondaryUseActive() {
        return this.isShiftKeyDown();
    }

    protected boolean wantsToStopRiding() {
        return this.isShiftKeyDown();
    }

    protected boolean isStayingOnGroundSurface() {
        return this.isShiftKeyDown();
    }

    protected boolean updateIsUnderwater() {
        this.wasUnderwater = this.isEyeInFluid(FluidTags.WATER);
        return this.wasUnderwater;
    }

    private void turtleHelmetTick() {
        ItemStack $$0 = this.getItemBySlot(EquipmentSlot.HEAD);
        if ($$0.is(Items.TURTLE_HELMET) && !this.isEyeInFluid(FluidTags.WATER)) {
            this.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 200, 0, false, false, true));
        }
    }

    protected ItemCooldowns createItemCooldowns() {
        return new ItemCooldowns();
    }

    private void moveCloak() {
        this.xCloakO = this.xCloak;
        this.yCloakO = this.yCloak;
        this.zCloakO = this.zCloak;
        double $$0 = this.getX() - this.xCloak;
        double $$1 = this.getY() - this.yCloak;
        double $$2 = this.getZ() - this.zCloak;
        double $$3 = 10.0;
        if ($$0 > 10.0) {
            this.xCloakO = this.xCloak = this.getX();
        }
        if ($$2 > 10.0) {
            this.zCloakO = this.zCloak = this.getZ();
        }
        if ($$1 > 10.0) {
            this.yCloakO = this.yCloak = this.getY();
        }
        if ($$0 < -10.0) {
            this.xCloakO = this.xCloak = this.getX();
        }
        if ($$2 < -10.0) {
            this.zCloakO = this.zCloak = this.getZ();
        }
        if ($$1 < -10.0) {
            this.yCloakO = this.yCloak = this.getY();
        }
        this.xCloak += $$0 * 0.25;
        this.zCloak += $$2 * 0.25;
        this.yCloak += $$1 * 0.25;
    }

    protected void updatePlayerPose() {
        Pose $$8;
        Pose $$5;
        if (!this.canEnterPose(Pose.SWIMMING)) {
            return;
        }
        if (this.isFallFlying()) {
            Pose $$0 = Pose.FALL_FLYING;
        } else if (this.isSleeping()) {
            Pose $$1 = Pose.SLEEPING;
        } else if (this.isSwimming()) {
            Pose $$2 = Pose.SWIMMING;
        } else if (this.isAutoSpinAttack()) {
            Pose $$3 = Pose.SPIN_ATTACK;
        } else if (this.isShiftKeyDown() && !this.abilities.flying) {
            Pose $$4 = Pose.CROUCHING;
        } else {
            $$5 = Pose.STANDING;
        }
        if (this.isSpectator() || this.isPassenger() || this.canEnterPose($$5)) {
            void $$6 = $$5;
        } else if (this.canEnterPose(Pose.CROUCHING)) {
            Pose $$7 = Pose.CROUCHING;
        } else {
            $$8 = Pose.SWIMMING;
        }
        this.setPose($$8);
    }

    @Override
    public int getPortalWaitTime() {
        return this.abilities.invulnerable ? 1 : 80;
    }

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.PLAYER_SWIM;
    }

    @Override
    protected SoundEvent getSwimSplashSound() {
        return SoundEvents.PLAYER_SPLASH;
    }

    @Override
    protected SoundEvent getSwimHighSpeedSplashSound() {
        return SoundEvents.PLAYER_SPLASH_HIGH_SPEED;
    }

    @Override
    public int getDimensionChangingDelay() {
        return 10;
    }

    @Override
    public void playSound(SoundEvent $$0, float $$1, float $$2) {
        this.level.playSound(this, this.getX(), this.getY(), this.getZ(), $$0, this.getSoundSource(), $$1, $$2);
    }

    public void playNotifySound(SoundEvent $$0, SoundSource $$1, float $$2, float $$3) {
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.PLAYERS;
    }

    @Override
    protected int getFireImmuneTicks() {
        return 20;
    }

    @Override
    public void handleEntityEvent(byte $$0) {
        if ($$0 == 9) {
            this.completeUsingItem();
        } else if ($$0 == 23) {
            this.reducedDebugInfo = false;
        } else if ($$0 == 22) {
            this.reducedDebugInfo = true;
        } else if ($$0 == 43) {
            this.addParticlesAroundSelf(ParticleTypes.CLOUD);
        } else {
            super.handleEntityEvent($$0);
        }
    }

    private void addParticlesAroundSelf(ParticleOptions $$0) {
        for (int $$1 = 0; $$1 < 5; ++$$1) {
            double $$2 = this.random.nextGaussian() * 0.02;
            double $$3 = this.random.nextGaussian() * 0.02;
            double $$4 = this.random.nextGaussian() * 0.02;
            this.level.addParticle($$0, this.getRandomX(1.0), this.getRandomY() + 1.0, this.getRandomZ(1.0), $$2, $$3, $$4);
        }
    }

    protected void closeContainer() {
        this.containerMenu = this.inventoryMenu;
    }

    protected void doCloseContainer() {
    }

    @Override
    public void rideTick() {
        if (!this.level.isClientSide && this.wantsToStopRiding() && this.isPassenger()) {
            this.stopRiding();
            this.setShiftKeyDown(false);
            return;
        }
        double $$0 = this.getX();
        double $$1 = this.getY();
        double $$2 = this.getZ();
        super.rideTick();
        this.oBob = this.bob;
        this.bob = 0.0f;
        this.checkRidingStatistics(this.getX() - $$0, this.getY() - $$1, this.getZ() - $$2);
    }

    @Override
    protected void serverAiStep() {
        super.serverAiStep();
        this.updateSwingTime();
        this.yHeadRot = this.getYRot();
    }

    @Override
    public void aiStep() {
        float $$1;
        if (this.jumpTriggerTime > 0) {
            --this.jumpTriggerTime;
        }
        if (this.level.getDifficulty() == Difficulty.PEACEFUL && this.level.getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION)) {
            if (this.getHealth() < this.getMaxHealth() && this.tickCount % 20 == 0) {
                this.heal(1.0f);
            }
            if (this.foodData.needsFood() && this.tickCount % 10 == 0) {
                this.foodData.setFoodLevel(this.foodData.getFoodLevel() + 1);
            }
        }
        this.inventory.tick();
        this.oBob = this.bob;
        super.aiStep();
        this.flyingSpeed = 0.02f;
        if (this.isSprinting()) {
            this.flyingSpeed += 0.006f;
        }
        this.setSpeed((float)this.getAttributeValue(Attributes.MOVEMENT_SPEED));
        if (!this.onGround || this.isDeadOrDying() || this.isSwimming()) {
            float $$0 = 0.0f;
        } else {
            $$1 = Math.min((float)0.1f, (float)((float)this.getDeltaMovement().horizontalDistance()));
        }
        this.bob += ($$1 - this.bob) * 0.4f;
        if (this.getHealth() > 0.0f && !this.isSpectator()) {
            AABB $$3;
            if (this.isPassenger() && !this.getVehicle().isRemoved()) {
                AABB $$2 = this.getBoundingBox().minmax(this.getVehicle().getBoundingBox()).inflate(1.0, 0.0, 1.0);
            } else {
                $$3 = this.getBoundingBox().inflate(1.0, 0.5, 1.0);
            }
            List $$4 = this.level.getEntities(this, $$3);
            ArrayList $$5 = Lists.newArrayList();
            for (int $$6 = 0; $$6 < $$4.size(); ++$$6) {
                Entity $$7 = (Entity)$$4.get($$6);
                if ($$7.getType() == EntityType.EXPERIENCE_ORB) {
                    $$5.add((Object)$$7);
                    continue;
                }
                if ($$7.isRemoved()) continue;
                this.touch($$7);
            }
            if (!$$5.isEmpty()) {
                this.touch((Entity)Util.getRandom($$5, this.random));
            }
        }
        this.playShoulderEntityAmbientSound(this.getShoulderEntityLeft());
        this.playShoulderEntityAmbientSound(this.getShoulderEntityRight());
        if (!this.level.isClientSide && (this.fallDistance > 0.5f || this.isInWater()) || this.abilities.flying || this.isSleeping() || this.isInPowderSnow) {
            this.removeEntitiesOnShoulder();
        }
    }

    private void playShoulderEntityAmbientSound(@Nullable CompoundTag $$02) {
        if (!($$02 == null || $$02.contains("Silent") && $$02.getBoolean("Silent") || this.level.random.nextInt(200) != 0)) {
            String $$1 = $$02.getString("id");
            EntityType.byString($$1).filter($$0 -> $$0 == EntityType.PARROT).ifPresent($$0 -> {
                if (!Parrot.imitateNearbyMobs(this.level, this)) {
                    this.level.playSound(null, this.getX(), this.getY(), this.getZ(), Parrot.getAmbient(this.level, this.level.random), this.getSoundSource(), 1.0f, Parrot.getPitch(this.level.random));
                }
            });
        }
    }

    private void touch(Entity $$0) {
        $$0.playerTouch(this);
    }

    public int getScore() {
        return this.entityData.get(DATA_SCORE_ID);
    }

    public void setScore(int $$0) {
        this.entityData.set(DATA_SCORE_ID, $$0);
    }

    public void increaseScore(int $$0) {
        int $$1 = this.getScore();
        this.entityData.set(DATA_SCORE_ID, $$1 + $$0);
    }

    public void startAutoSpinAttack(int $$0) {
        this.autoSpinAttackTicks = $$0;
        if (!this.level.isClientSide) {
            this.removeEntitiesOnShoulder();
            this.setLivingEntityFlag(4, true);
        }
    }

    @Override
    public void die(DamageSource $$0) {
        super.die($$0);
        this.reapplyPosition();
        if (!this.isSpectator()) {
            this.dropAllDeathLoot($$0);
        }
        if ($$0 != null) {
            this.setDeltaMovement(-Mth.cos((this.hurtDir + this.getYRot()) * ((float)Math.PI / 180)) * 0.1f, 0.1f, -Mth.sin((this.hurtDir + this.getYRot()) * ((float)Math.PI / 180)) * 0.1f);
        } else {
            this.setDeltaMovement(0.0, 0.1, 0.0);
        }
        this.awardStat(Stats.DEATHS);
        this.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_DEATH));
        this.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
        this.clearFire();
        this.setSharedFlagOnFire(false);
        this.setLastDeathLocation((Optional<GlobalPos>)Optional.of((Object)GlobalPos.of(this.level.dimension(), this.blockPosition())));
    }

    @Override
    protected void dropEquipment() {
        super.dropEquipment();
        if (!this.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
            this.destroyVanishingCursedItems();
            this.inventory.dropAll();
        }
    }

    protected void destroyVanishingCursedItems() {
        for (int $$0 = 0; $$0 < this.inventory.getContainerSize(); ++$$0) {
            ItemStack $$1 = this.inventory.getItem($$0);
            if ($$1.isEmpty() || !EnchantmentHelper.hasVanishingCurse($$1)) continue;
            this.inventory.removeItemNoUpdate($$0);
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource $$0) {
        if ($$0 == DamageSource.ON_FIRE) {
            return SoundEvents.PLAYER_HURT_ON_FIRE;
        }
        if ($$0 == DamageSource.DROWN) {
            return SoundEvents.PLAYER_HURT_DROWN;
        }
        if ($$0 == DamageSource.SWEET_BERRY_BUSH) {
            return SoundEvents.PLAYER_HURT_SWEET_BERRY_BUSH;
        }
        if ($$0 == DamageSource.FREEZE) {
            return SoundEvents.PLAYER_HURT_FREEZE;
        }
        return SoundEvents.PLAYER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PLAYER_DEATH;
    }

    @Nullable
    public ItemEntity drop(ItemStack $$0, boolean $$1) {
        return this.drop($$0, false, $$1);
    }

    @Nullable
    public ItemEntity drop(ItemStack $$0, boolean $$1, boolean $$2) {
        if ($$0.isEmpty()) {
            return null;
        }
        if (this.level.isClientSide) {
            this.swing(InteractionHand.MAIN_HAND);
        }
        double $$3 = this.getEyeY() - (double)0.3f;
        ItemEntity $$4 = new ItemEntity(this.level, this.getX(), $$3, this.getZ(), $$0);
        $$4.setPickUpDelay(40);
        if ($$2) {
            $$4.setThrower(this.getUUID());
        }
        if ($$1) {
            float $$5 = this.random.nextFloat() * 0.5f;
            float $$6 = this.random.nextFloat() * ((float)Math.PI * 2);
            $$4.setDeltaMovement(-Mth.sin($$6) * $$5, 0.2f, Mth.cos($$6) * $$5);
        } else {
            float $$7 = 0.3f;
            float $$8 = Mth.sin(this.getXRot() * ((float)Math.PI / 180));
            float $$9 = Mth.cos(this.getXRot() * ((float)Math.PI / 180));
            float $$10 = Mth.sin(this.getYRot() * ((float)Math.PI / 180));
            float $$11 = Mth.cos(this.getYRot() * ((float)Math.PI / 180));
            float $$12 = this.random.nextFloat() * ((float)Math.PI * 2);
            float $$13 = 0.02f * this.random.nextFloat();
            $$4.setDeltaMovement((double)(-$$10 * $$9 * 0.3f) + Math.cos((double)$$12) * (double)$$13, -$$8 * 0.3f + 0.1f + (this.random.nextFloat() - this.random.nextFloat()) * 0.1f, (double)($$11 * $$9 * 0.3f) + Math.sin((double)$$12) * (double)$$13);
        }
        return $$4;
    }

    public float getDestroySpeed(BlockState $$0) {
        float $$1 = this.inventory.getDestroySpeed($$0);
        if ($$1 > 1.0f) {
            int $$2 = EnchantmentHelper.getBlockEfficiency(this);
            ItemStack $$3 = this.getMainHandItem();
            if ($$2 > 0 && !$$3.isEmpty()) {
                $$1 += (float)($$2 * $$2 + 1);
            }
        }
        if (MobEffectUtil.hasDigSpeed(this)) {
            $$1 *= 1.0f + (float)(MobEffectUtil.getDigSpeedAmplification(this) + 1) * 0.2f;
        }
        if (this.hasEffect(MobEffects.DIG_SLOWDOWN)) {
            float $$7;
            switch (this.getEffect(MobEffects.DIG_SLOWDOWN).getAmplifier()) {
                case 0: {
                    float $$4 = 0.3f;
                    break;
                }
                case 1: {
                    float $$5 = 0.09f;
                    break;
                }
                case 2: {
                    float $$6 = 0.0027f;
                    break;
                }
                default: {
                    $$7 = 8.1E-4f;
                }
            }
            $$1 *= $$7;
        }
        if (this.isEyeInFluid(FluidTags.WATER) && !EnchantmentHelper.hasAquaAffinity(this)) {
            $$1 /= 5.0f;
        }
        if (!this.onGround) {
            $$1 /= 5.0f;
        }
        return $$1;
    }

    public boolean hasCorrectToolForDrops(BlockState $$0) {
        return !$$0.requiresCorrectToolForDrops() || this.inventory.getSelected().isCorrectToolForDrops($$0);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$0) {
        super.readAdditionalSaveData($$0);
        this.setUUID(UUIDUtil.getOrCreatePlayerUUID(this.gameProfile));
        ListTag $$1 = $$0.getList("Inventory", 10);
        this.inventory.load($$1);
        this.inventory.selected = $$0.getInt("SelectedItemSlot");
        this.sleepCounter = $$0.getShort("SleepTimer");
        this.experienceProgress = $$0.getFloat("XpP");
        this.experienceLevel = $$0.getInt("XpLevel");
        this.totalExperience = $$0.getInt("XpTotal");
        this.enchantmentSeed = $$0.getInt("XpSeed");
        if (this.enchantmentSeed == 0) {
            this.enchantmentSeed = this.random.nextInt();
        }
        this.setScore($$0.getInt("Score"));
        this.foodData.readAdditionalSaveData($$0);
        this.abilities.loadSaveData($$0);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(this.abilities.getWalkingSpeed());
        if ($$0.contains("EnderItems", 9)) {
            this.enderChestInventory.fromTag($$0.getList("EnderItems", 10));
        }
        if ($$0.contains("ShoulderEntityLeft", 10)) {
            this.setShoulderEntityLeft($$0.getCompound("ShoulderEntityLeft"));
        }
        if ($$0.contains("ShoulderEntityRight", 10)) {
            this.setShoulderEntityRight($$0.getCompound("ShoulderEntityRight"));
        }
        if ($$0.contains("LastDeathLocation", 10)) {
            this.setLastDeathLocation((Optional<GlobalPos>)GlobalPos.CODEC.parse((DynamicOps)NbtOps.INSTANCE, (Object)$$0.get("LastDeathLocation")).resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$02) {
        super.addAdditionalSaveData($$02);
        $$02.putInt("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());
        $$02.put("Inventory", this.inventory.save(new ListTag()));
        $$02.putInt("SelectedItemSlot", this.inventory.selected);
        $$02.putShort("SleepTimer", (short)this.sleepCounter);
        $$02.putFloat("XpP", this.experienceProgress);
        $$02.putInt("XpLevel", this.experienceLevel);
        $$02.putInt("XpTotal", this.totalExperience);
        $$02.putInt("XpSeed", this.enchantmentSeed);
        $$02.putInt("Score", this.getScore());
        this.foodData.addAdditionalSaveData($$02);
        this.abilities.addSaveData($$02);
        $$02.put("EnderItems", this.enderChestInventory.createTag());
        if (!this.getShoulderEntityLeft().isEmpty()) {
            $$02.put("ShoulderEntityLeft", this.getShoulderEntityLeft());
        }
        if (!this.getShoulderEntityRight().isEmpty()) {
            $$02.put("ShoulderEntityRight", this.getShoulderEntityRight());
        }
        this.getLastDeathLocation().flatMap($$0 -> GlobalPos.CODEC.encodeStart((DynamicOps)NbtOps.INSTANCE, $$0).resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0))).ifPresent($$1 -> $$02.put("LastDeathLocation", (Tag)$$1));
    }

    @Override
    public boolean isInvulnerableTo(DamageSource $$0) {
        if (super.isInvulnerableTo($$0)) {
            return true;
        }
        if ($$0 == DamageSource.DROWN) {
            return !this.level.getGameRules().getBoolean(GameRules.RULE_DROWNING_DAMAGE);
        }
        if ($$0.isFall()) {
            return !this.level.getGameRules().getBoolean(GameRules.RULE_FALL_DAMAGE);
        }
        if ($$0.isFire()) {
            return !this.level.getGameRules().getBoolean(GameRules.RULE_FIRE_DAMAGE);
        }
        if ($$0 == DamageSource.FREEZE) {
            return !this.level.getGameRules().getBoolean(GameRules.RULE_FREEZE_DAMAGE);
        }
        return false;
    }

    @Override
    public boolean hurt(DamageSource $$0, float $$1) {
        if (this.isInvulnerableTo($$0)) {
            return false;
        }
        if (this.abilities.invulnerable && !$$0.isBypassInvul()) {
            return false;
        }
        this.noActionTime = 0;
        if (this.isDeadOrDying()) {
            return false;
        }
        if (!this.level.isClientSide) {
            this.removeEntitiesOnShoulder();
        }
        if ($$0.scalesWithDifficulty()) {
            if (this.level.getDifficulty() == Difficulty.PEACEFUL) {
                $$1 = 0.0f;
            }
            if (this.level.getDifficulty() == Difficulty.EASY) {
                $$1 = Math.min((float)($$1 / 2.0f + 1.0f), (float)$$1);
            }
            if (this.level.getDifficulty() == Difficulty.HARD) {
                $$1 = $$1 * 3.0f / 2.0f;
            }
        }
        if ($$1 == 0.0f) {
            return false;
        }
        return super.hurt($$0, $$1);
    }

    @Override
    protected void blockUsingShield(LivingEntity $$0) {
        super.blockUsingShield($$0);
        if ($$0.canDisableShield()) {
            this.disableShield(true);
        }
    }

    @Override
    public boolean canBeSeenAsEnemy() {
        return !this.getAbilities().invulnerable && super.canBeSeenAsEnemy();
    }

    public boolean canHarmPlayer(Player $$0) {
        Team $$1 = this.getTeam();
        Team $$2 = $$0.getTeam();
        if ($$1 == null) {
            return true;
        }
        if (!$$1.isAlliedTo($$2)) {
            return true;
        }
        return $$1.isAllowFriendlyFire();
    }

    @Override
    protected void hurtArmor(DamageSource $$0, float $$1) {
        this.inventory.hurtArmor($$0, $$1, Inventory.ALL_ARMOR_SLOTS);
    }

    @Override
    protected void hurtHelmet(DamageSource $$0, float $$1) {
        this.inventory.hurtArmor($$0, $$1, Inventory.HELMET_SLOT_ONLY);
    }

    @Override
    protected void hurtCurrentlyUsedShield(float $$0) {
        if (!this.useItem.is(Items.SHIELD)) {
            return;
        }
        if (!this.level.isClientSide) {
            this.awardStat(Stats.ITEM_USED.get(this.useItem.getItem()));
        }
        if ($$0 >= 3.0f) {
            int $$12 = 1 + Mth.floor($$0);
            InteractionHand $$2 = this.getUsedItemHand();
            this.useItem.hurtAndBreak($$12, this, $$1 -> $$1.broadcastBreakEvent($$2));
            if (this.useItem.isEmpty()) {
                if ($$2 == InteractionHand.MAIN_HAND) {
                    this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                } else {
                    this.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
                }
                this.useItem = ItemStack.EMPTY;
                this.playSound(SoundEvents.SHIELD_BREAK, 0.8f, 0.8f + this.level.random.nextFloat() * 0.4f);
            }
        }
    }

    @Override
    protected void actuallyHurt(DamageSource $$0, float $$1) {
        if (this.isInvulnerableTo($$0)) {
            return;
        }
        $$1 = this.getDamageAfterArmorAbsorb($$0, $$1);
        float $$2 = $$1 = this.getDamageAfterMagicAbsorb($$0, $$1);
        $$1 = Math.max((float)($$1 - this.getAbsorptionAmount()), (float)0.0f);
        this.setAbsorptionAmount(this.getAbsorptionAmount() - ($$2 - $$1));
        float $$3 = $$2 - $$1;
        if ($$3 > 0.0f && $$3 < 3.4028235E37f) {
            this.awardStat(Stats.DAMAGE_ABSORBED, Math.round((float)($$3 * 10.0f)));
        }
        if ($$1 == 0.0f) {
            return;
        }
        this.causeFoodExhaustion($$0.getFoodExhaustion());
        float $$4 = this.getHealth();
        this.setHealth(this.getHealth() - $$1);
        this.getCombatTracker().recordDamage($$0, $$4, $$1);
        if ($$1 < 3.4028235E37f) {
            this.awardStat(Stats.DAMAGE_TAKEN, Math.round((float)($$1 * 10.0f)));
        }
    }

    @Override
    protected boolean onSoulSpeedBlock() {
        return !this.abilities.flying && super.onSoulSpeedBlock();
    }

    public boolean isTextFilteringEnabled() {
        return false;
    }

    public void openTextEdit(SignBlockEntity $$0) {
    }

    public void openMinecartCommandBlock(BaseCommandBlock $$0) {
    }

    public void openCommandBlock(CommandBlockEntity $$0) {
    }

    public void openStructureBlock(StructureBlockEntity $$0) {
    }

    public void openJigsawBlock(JigsawBlockEntity $$0) {
    }

    public void openHorseInventory(AbstractHorse $$0, Container $$1) {
    }

    public OptionalInt openMenu(@Nullable MenuProvider $$0) {
        return OptionalInt.empty();
    }

    public void sendMerchantOffers(int $$0, MerchantOffers $$1, int $$2, int $$3, boolean $$4, boolean $$5) {
    }

    public void openItemGui(ItemStack $$0, InteractionHand $$1) {
    }

    public InteractionResult interactOn(Entity $$0, InteractionHand $$1) {
        if (this.isSpectator()) {
            if ($$0 instanceof MenuProvider) {
                this.openMenu((MenuProvider)((Object)$$0));
            }
            return InteractionResult.PASS;
        }
        ItemStack $$2 = this.getItemInHand($$1);
        ItemStack $$3 = $$2.copy();
        InteractionResult $$4 = $$0.interact(this, $$1);
        if ($$4.consumesAction()) {
            if (this.abilities.instabuild && $$2 == this.getItemInHand($$1) && $$2.getCount() < $$3.getCount()) {
                $$2.setCount($$3.getCount());
            }
            return $$4;
        }
        if (!$$2.isEmpty() && $$0 instanceof LivingEntity) {
            InteractionResult $$5;
            if (this.abilities.instabuild) {
                $$2 = $$3;
            }
            if (($$5 = $$2.interactLivingEntity(this, (LivingEntity)$$0, $$1)).consumesAction()) {
                if ($$2.isEmpty() && !this.abilities.instabuild) {
                    this.setItemInHand($$1, ItemStack.EMPTY);
                }
                return $$5;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public double getMyRidingOffset() {
        return -0.35;
    }

    @Override
    public void removeVehicle() {
        super.removeVehicle();
        this.boardingCooldown = 0;
    }

    @Override
    protected boolean isImmobile() {
        return super.isImmobile() || this.isSleeping();
    }

    @Override
    public boolean isAffectedByFluids() {
        return !this.abilities.flying;
    }

    @Override
    protected Vec3 maybeBackOffFromEdge(Vec3 $$0, MoverType $$1) {
        if (!this.abilities.flying && $$0.y <= 0.0 && ($$1 == MoverType.SELF || $$1 == MoverType.PLAYER) && this.isStayingOnGroundSurface() && this.isAboveGround()) {
            double $$2 = $$0.x;
            double $$3 = $$0.z;
            double $$4 = 0.05;
            while ($$2 != 0.0 && this.level.noCollision(this, this.getBoundingBox().move($$2, -this.maxUpStep, 0.0))) {
                if ($$2 < 0.05 && $$2 >= -0.05) {
                    $$2 = 0.0;
                    continue;
                }
                if ($$2 > 0.0) {
                    $$2 -= 0.05;
                    continue;
                }
                $$2 += 0.05;
            }
            while ($$3 != 0.0 && this.level.noCollision(this, this.getBoundingBox().move(0.0, -this.maxUpStep, $$3))) {
                if ($$3 < 0.05 && $$3 >= -0.05) {
                    $$3 = 0.0;
                    continue;
                }
                if ($$3 > 0.0) {
                    $$3 -= 0.05;
                    continue;
                }
                $$3 += 0.05;
            }
            while ($$2 != 0.0 && $$3 != 0.0 && this.level.noCollision(this, this.getBoundingBox().move($$2, -this.maxUpStep, $$3))) {
                $$2 = $$2 < 0.05 && $$2 >= -0.05 ? 0.0 : ($$2 > 0.0 ? ($$2 -= 0.05) : ($$2 += 0.05));
                if ($$3 < 0.05 && $$3 >= -0.05) {
                    $$3 = 0.0;
                    continue;
                }
                if ($$3 > 0.0) {
                    $$3 -= 0.05;
                    continue;
                }
                $$3 += 0.05;
            }
            $$0 = new Vec3($$2, $$0.y, $$3);
        }
        return $$0;
    }

    private boolean isAboveGround() {
        return this.onGround || this.fallDistance < this.maxUpStep && !this.level.noCollision(this, this.getBoundingBox().move(0.0, this.fallDistance - this.maxUpStep, 0.0));
    }

    public void attack(Entity $$0) {
        float $$3;
        if (!$$0.isAttackable()) {
            return;
        }
        if ($$0.skipAttackInteraction(this)) {
            return;
        }
        float $$1 = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        if ($$0 instanceof LivingEntity) {
            float $$2 = EnchantmentHelper.getDamageBonus(this.getMainHandItem(), ((LivingEntity)$$0).getMobType());
        } else {
            $$3 = EnchantmentHelper.getDamageBonus(this.getMainHandItem(), MobType.UNDEFINED);
        }
        float $$4 = this.getAttackStrengthScale(0.5f);
        $$3 *= $$4;
        this.resetAttackStrengthTicker();
        if (($$1 *= 0.2f + $$4 * $$4 * 0.8f) > 0.0f || $$3 > 0.0f) {
            ItemStack $$11;
            boolean $$5 = $$4 > 0.9f;
            boolean $$6 = false;
            int $$7 = 0;
            $$7 += EnchantmentHelper.getKnockbackBonus(this);
            if (this.isSprinting() && $$5) {
                this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_KNOCKBACK, this.getSoundSource(), 1.0f, 1.0f);
                ++$$7;
                $$6 = true;
            }
            boolean $$8 = $$5 && this.fallDistance > 0.0f && !this.onGround && !this.onClimbable() && !this.isInWater() && !this.hasEffect(MobEffects.BLINDNESS) && !this.isPassenger() && $$0 instanceof LivingEntity;
            boolean bl = $$8 = $$8 && !this.isSprinting();
            if ($$8) {
                $$1 *= 1.5f;
            }
            $$1 += $$3;
            boolean $$9 = false;
            double $$10 = this.walkDist - this.walkDistO;
            if ($$5 && !$$8 && !$$6 && this.onGround && $$10 < (double)this.getSpeed() && ($$11 = this.getItemInHand(InteractionHand.MAIN_HAND)).getItem() instanceof SwordItem) {
                $$9 = true;
            }
            float $$12 = 0.0f;
            boolean $$13 = false;
            int $$14 = EnchantmentHelper.getFireAspect(this);
            if ($$0 instanceof LivingEntity) {
                $$12 = ((LivingEntity)$$0).getHealth();
                if ($$14 > 0 && !$$0.isOnFire()) {
                    $$13 = true;
                    $$0.setSecondsOnFire(1);
                }
            }
            Vec3 $$15 = $$0.getDeltaMovement();
            boolean $$16 = $$0.hurt(DamageSource.playerAttack(this), $$1);
            if ($$16) {
                if ($$7 > 0) {
                    if ($$0 instanceof LivingEntity) {
                        ((LivingEntity)$$0).knockback((float)$$7 * 0.5f, Mth.sin(this.getYRot() * ((float)Math.PI / 180)), -Mth.cos(this.getYRot() * ((float)Math.PI / 180)));
                    } else {
                        $$0.push(-Mth.sin(this.getYRot() * ((float)Math.PI / 180)) * (float)$$7 * 0.5f, 0.1, Mth.cos(this.getYRot() * ((float)Math.PI / 180)) * (float)$$7 * 0.5f);
                    }
                    this.setDeltaMovement(this.getDeltaMovement().multiply(0.6, 1.0, 0.6));
                    this.setSprinting(false);
                }
                if ($$9) {
                    float $$17 = 1.0f + EnchantmentHelper.getSweepingDamageRatio(this) * $$1;
                    List $$18 = this.level.getEntitiesOfClass(LivingEntity.class, $$0.getBoundingBox().inflate(1.0, 0.25, 1.0));
                    for (LivingEntity $$19 : $$18) {
                        if ($$19 == this || $$19 == $$0 || this.isAlliedTo($$19) || $$19 instanceof ArmorStand && ((ArmorStand)$$19).isMarker() || !(this.distanceToSqr($$19) < 9.0)) continue;
                        $$19.knockback(0.4f, Mth.sin(this.getYRot() * ((float)Math.PI / 180)), -Mth.cos(this.getYRot() * ((float)Math.PI / 180)));
                        $$19.hurt(DamageSource.playerAttack(this), $$17);
                    }
                    this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, this.getSoundSource(), 1.0f, 1.0f);
                    this.sweepAttack();
                }
                if ($$0 instanceof ServerPlayer && $$0.hurtMarked) {
                    ((ServerPlayer)$$0).connection.send(new ClientboundSetEntityMotionPacket($$0));
                    $$0.hurtMarked = false;
                    $$0.setDeltaMovement($$15);
                }
                if ($$8) {
                    this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, this.getSoundSource(), 1.0f, 1.0f);
                    this.crit($$0);
                }
                if (!$$8 && !$$9) {
                    if ($$5) {
                        this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_STRONG, this.getSoundSource(), 1.0f, 1.0f);
                    } else {
                        this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_WEAK, this.getSoundSource(), 1.0f, 1.0f);
                    }
                }
                if ($$3 > 0.0f) {
                    this.magicCrit($$0);
                }
                this.setLastHurtMob($$0);
                if ($$0 instanceof LivingEntity) {
                    EnchantmentHelper.doPostHurtEffects((LivingEntity)$$0, this);
                }
                EnchantmentHelper.doPostDamageEffects(this, $$0);
                ItemStack $$20 = this.getMainHandItem();
                Entity $$21 = $$0;
                if ($$0 instanceof EnderDragonPart) {
                    $$21 = ((EnderDragonPart)$$0).parentMob;
                }
                if (!this.level.isClientSide && !$$20.isEmpty() && $$21 instanceof LivingEntity) {
                    $$20.hurtEnemy((LivingEntity)$$21, this);
                    if ($$20.isEmpty()) {
                        this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                    }
                }
                if ($$0 instanceof LivingEntity) {
                    float $$22 = $$12 - ((LivingEntity)$$0).getHealth();
                    this.awardStat(Stats.DAMAGE_DEALT, Math.round((float)($$22 * 10.0f)));
                    if ($$14 > 0) {
                        $$0.setSecondsOnFire($$14 * 4);
                    }
                    if (this.level instanceof ServerLevel && $$22 > 2.0f) {
                        int $$23 = (int)((double)$$22 * 0.5);
                        ((ServerLevel)this.level).sendParticles(ParticleTypes.DAMAGE_INDICATOR, $$0.getX(), $$0.getY(0.5), $$0.getZ(), $$23, 0.1, 0.0, 0.1, 0.2);
                    }
                }
                this.causeFoodExhaustion(0.1f);
            } else {
                this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_NODAMAGE, this.getSoundSource(), 1.0f, 1.0f);
                if ($$13) {
                    $$0.clearFire();
                }
            }
        }
    }

    @Override
    protected void doAutoAttackOnTouch(LivingEntity $$0) {
        this.attack($$0);
    }

    public void disableShield(boolean $$0) {
        float $$1 = 0.25f + (float)EnchantmentHelper.getBlockEfficiency(this) * 0.05f;
        if ($$0) {
            $$1 += 0.75f;
        }
        if (this.random.nextFloat() < $$1) {
            this.getCooldowns().addCooldown(Items.SHIELD, 100);
            this.stopUsingItem();
            this.level.broadcastEntityEvent(this, (byte)30);
        }
    }

    public void crit(Entity $$0) {
    }

    public void magicCrit(Entity $$0) {
    }

    public void sweepAttack() {
        double $$0 = -Mth.sin(this.getYRot() * ((float)Math.PI / 180));
        double $$1 = Mth.cos(this.getYRot() * ((float)Math.PI / 180));
        if (this.level instanceof ServerLevel) {
            ((ServerLevel)this.level).sendParticles(ParticleTypes.SWEEP_ATTACK, this.getX() + $$0, this.getY(0.5), this.getZ() + $$1, 0, $$0, 0.0, $$1, 0.0);
        }
    }

    public void respawn() {
    }

    @Override
    public void remove(Entity.RemovalReason $$0) {
        super.remove($$0);
        this.inventoryMenu.removed(this);
        if (this.containerMenu != null && this.hasContainerOpen()) {
            this.doCloseContainer();
        }
    }

    public boolean isLocalPlayer() {
        return false;
    }

    public GameProfile getGameProfile() {
        return this.gameProfile;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public Abilities getAbilities() {
        return this.abilities;
    }

    public void updateTutorialInventoryAction(ItemStack $$0, ItemStack $$1, ClickAction $$2) {
    }

    public boolean hasContainerOpen() {
        return this.containerMenu != this.inventoryMenu;
    }

    public Either<BedSleepingProblem, Unit> startSleepInBed(BlockPos $$0) {
        this.startSleeping($$0);
        this.sleepCounter = 0;
        return Either.right((Object)((Object)Unit.INSTANCE));
    }

    public void stopSleepInBed(boolean $$0, boolean $$1) {
        super.stopSleeping();
        if (this.level instanceof ServerLevel && $$1) {
            ((ServerLevel)this.level).updateSleepingPlayerList();
        }
        this.sleepCounter = $$0 ? 0 : 100;
    }

    @Override
    public void stopSleeping() {
        this.stopSleepInBed(true, true);
    }

    public static Optional<Vec3> findRespawnPositionAndUseSpawnBlock(ServerLevel $$0, BlockPos $$1, float $$2, boolean $$3, boolean $$4) {
        BlockState $$5 = $$0.getBlockState($$1);
        Block $$6 = $$5.getBlock();
        if ($$6 instanceof RespawnAnchorBlock && ($$3 || $$5.getValue(RespawnAnchorBlock.CHARGE) > 0) && RespawnAnchorBlock.canSetSpawn($$0)) {
            Optional<Vec3> $$7 = RespawnAnchorBlock.findStandUpPosition(EntityType.PLAYER, $$0, $$1);
            if (!$$3 && !$$4 && $$7.isPresent()) {
                $$0.setBlock($$1, (BlockState)$$5.setValue(RespawnAnchorBlock.CHARGE, $$5.getValue(RespawnAnchorBlock.CHARGE) - 1), 3);
            }
            return $$7;
        }
        if ($$6 instanceof BedBlock && BedBlock.canSetSpawn($$0)) {
            return BedBlock.findStandUpPosition(EntityType.PLAYER, $$0, $$1, $$5.getValue(BedBlock.FACING), $$2);
        }
        if (!$$3) {
            return Optional.empty();
        }
        boolean $$8 = $$6.isPossibleToRespawnInThis();
        boolean $$9 = $$0.getBlockState((BlockPos)$$1.above()).getBlock().isPossibleToRespawnInThis();
        if ($$8 && $$9) {
            return Optional.of((Object)new Vec3((double)$$1.getX() + 0.5, (double)$$1.getY() + 0.1, (double)$$1.getZ() + 0.5));
        }
        return Optional.empty();
    }

    public boolean isSleepingLongEnough() {
        return this.isSleeping() && this.sleepCounter >= 100;
    }

    public int getSleepTimer() {
        return this.sleepCounter;
    }

    public void displayClientMessage(Component $$0, boolean $$1) {
    }

    public void awardStat(ResourceLocation $$0) {
        this.awardStat(Stats.CUSTOM.get($$0));
    }

    public void awardStat(ResourceLocation $$0, int $$1) {
        this.awardStat(Stats.CUSTOM.get($$0), $$1);
    }

    public void awardStat(Stat<?> $$0) {
        this.awardStat($$0, 1);
    }

    public void awardStat(Stat<?> $$0, int $$1) {
    }

    public void resetStat(Stat<?> $$0) {
    }

    public int awardRecipes(Collection<Recipe<?>> $$0) {
        return 0;
    }

    public void awardRecipesByKey(ResourceLocation[] $$0) {
    }

    public int resetRecipes(Collection<Recipe<?>> $$0) {
        return 0;
    }

    @Override
    public void jumpFromGround() {
        super.jumpFromGround();
        this.awardStat(Stats.JUMP);
        if (this.isSprinting()) {
            this.causeFoodExhaustion(0.2f);
        } else {
            this.causeFoodExhaustion(0.05f);
        }
    }

    @Override
    public void travel(Vec3 $$0) {
        double $$1 = this.getX();
        double $$2 = this.getY();
        double $$3 = this.getZ();
        if (this.isSwimming() && !this.isPassenger()) {
            double $$5;
            double $$4 = this.getLookAngle().y;
            double d = $$5 = $$4 < -0.2 ? 0.085 : 0.06;
            if ($$4 <= 0.0 || this.jumping || !this.level.getBlockState(new BlockPos(this.getX(), this.getY() + 1.0 - 0.1, this.getZ())).getFluidState().isEmpty()) {
                Vec3 $$6 = this.getDeltaMovement();
                this.setDeltaMovement($$6.add(0.0, ($$4 - $$6.y) * $$5, 0.0));
            }
        }
        if (this.abilities.flying && !this.isPassenger()) {
            double $$7 = this.getDeltaMovement().y;
            float $$8 = this.flyingSpeed;
            this.flyingSpeed = this.abilities.getFlyingSpeed() * (float)(this.isSprinting() ? 2 : 1);
            super.travel($$0);
            Vec3 $$9 = this.getDeltaMovement();
            this.setDeltaMovement($$9.x, $$7 * 0.6, $$9.z);
            this.flyingSpeed = $$8;
            this.resetFallDistance();
            this.setSharedFlag(7, false);
        } else {
            super.travel($$0);
        }
        this.checkMovementStatistics(this.getX() - $$1, this.getY() - $$2, this.getZ() - $$3);
    }

    @Override
    public void updateSwimming() {
        if (this.abilities.flying) {
            this.setSwimming(false);
        } else {
            super.updateSwimming();
        }
    }

    protected boolean freeAt(BlockPos $$0) {
        return !this.level.getBlockState($$0).isSuffocating(this.level, $$0);
    }

    @Override
    public float getSpeed() {
        return (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED);
    }

    public void checkMovementStatistics(double $$0, double $$1, double $$2) {
        if (this.isPassenger()) {
            return;
        }
        if (this.isSwimming()) {
            int $$3 = Math.round((float)((float)Math.sqrt((double)($$0 * $$0 + $$1 * $$1 + $$2 * $$2)) * 100.0f));
            if ($$3 > 0) {
                this.awardStat(Stats.SWIM_ONE_CM, $$3);
                this.causeFoodExhaustion(0.01f * (float)$$3 * 0.01f);
            }
        } else if (this.isEyeInFluid(FluidTags.WATER)) {
            int $$4 = Math.round((float)((float)Math.sqrt((double)($$0 * $$0 + $$1 * $$1 + $$2 * $$2)) * 100.0f));
            if ($$4 > 0) {
                this.awardStat(Stats.WALK_UNDER_WATER_ONE_CM, $$4);
                this.causeFoodExhaustion(0.01f * (float)$$4 * 0.01f);
            }
        } else if (this.isInWater()) {
            int $$5 = Math.round((float)((float)Math.sqrt((double)($$0 * $$0 + $$2 * $$2)) * 100.0f));
            if ($$5 > 0) {
                this.awardStat(Stats.WALK_ON_WATER_ONE_CM, $$5);
                this.causeFoodExhaustion(0.01f * (float)$$5 * 0.01f);
            }
        } else if (this.onClimbable()) {
            if ($$1 > 0.0) {
                this.awardStat(Stats.CLIMB_ONE_CM, (int)Math.round((double)($$1 * 100.0)));
            }
        } else if (this.onGround) {
            int $$6 = Math.round((float)((float)Math.sqrt((double)($$0 * $$0 + $$2 * $$2)) * 100.0f));
            if ($$6 > 0) {
                if (this.isSprinting()) {
                    this.awardStat(Stats.SPRINT_ONE_CM, $$6);
                    this.causeFoodExhaustion(0.1f * (float)$$6 * 0.01f);
                } else if (this.isCrouching()) {
                    this.awardStat(Stats.CROUCH_ONE_CM, $$6);
                    this.causeFoodExhaustion(0.0f * (float)$$6 * 0.01f);
                } else {
                    this.awardStat(Stats.WALK_ONE_CM, $$6);
                    this.causeFoodExhaustion(0.0f * (float)$$6 * 0.01f);
                }
            }
        } else if (this.isFallFlying()) {
            int $$7 = Math.round((float)((float)Math.sqrt((double)($$0 * $$0 + $$1 * $$1 + $$2 * $$2)) * 100.0f));
            this.awardStat(Stats.AVIATE_ONE_CM, $$7);
        } else {
            int $$8 = Math.round((float)((float)Math.sqrt((double)($$0 * $$0 + $$2 * $$2)) * 100.0f));
            if ($$8 > 25) {
                this.awardStat(Stats.FLY_ONE_CM, $$8);
            }
        }
    }

    private void checkRidingStatistics(double $$0, double $$1, double $$2) {
        int $$3;
        if (this.isPassenger() && ($$3 = Math.round((float)((float)Math.sqrt((double)($$0 * $$0 + $$1 * $$1 + $$2 * $$2)) * 100.0f))) > 0) {
            Entity $$4 = this.getVehicle();
            if ($$4 instanceof AbstractMinecart) {
                this.awardStat(Stats.MINECART_ONE_CM, $$3);
            } else if ($$4 instanceof Boat) {
                this.awardStat(Stats.BOAT_ONE_CM, $$3);
            } else if ($$4 instanceof Pig) {
                this.awardStat(Stats.PIG_ONE_CM, $$3);
            } else if ($$4 instanceof AbstractHorse) {
                this.awardStat(Stats.HORSE_ONE_CM, $$3);
            } else if ($$4 instanceof Strider) {
                this.awardStat(Stats.STRIDER_ONE_CM, $$3);
            }
        }
    }

    @Override
    public boolean causeFallDamage(float $$0, float $$1, DamageSource $$2) {
        if (this.abilities.mayfly) {
            return false;
        }
        if ($$0 >= 2.0f) {
            this.awardStat(Stats.FALL_ONE_CM, (int)Math.round((double)((double)$$0 * 100.0)));
        }
        return super.causeFallDamage($$0, $$1, $$2);
    }

    public boolean tryToStartFallFlying() {
        ItemStack $$0;
        if (!this.onGround && !this.isFallFlying() && !this.isInWater() && !this.hasEffect(MobEffects.LEVITATION) && ($$0 = this.getItemBySlot(EquipmentSlot.CHEST)).is(Items.ELYTRA) && ElytraItem.isFlyEnabled($$0)) {
            this.startFallFlying();
            return true;
        }
        return false;
    }

    public void startFallFlying() {
        this.setSharedFlag(7, true);
    }

    public void stopFallFlying() {
        this.setSharedFlag(7, true);
        this.setSharedFlag(7, false);
    }

    @Override
    protected void doWaterSplashEffect() {
        if (!this.isSpectator()) {
            super.doWaterSplashEffect();
        }
    }

    @Override
    public LivingEntity.Fallsounds getFallSounds() {
        return new LivingEntity.Fallsounds(SoundEvents.PLAYER_SMALL_FALL, SoundEvents.PLAYER_BIG_FALL);
    }

    @Override
    public boolean wasKilled(ServerLevel $$0, LivingEntity $$1) {
        this.awardStat(Stats.ENTITY_KILLED.get($$1.getType()));
        return true;
    }

    @Override
    public void makeStuckInBlock(BlockState $$0, Vec3 $$1) {
        if (!this.abilities.flying) {
            super.makeStuckInBlock($$0, $$1);
        }
    }

    public void giveExperiencePoints(int $$0) {
        this.increaseScore($$0);
        this.experienceProgress += (float)$$0 / (float)this.getXpNeededForNextLevel();
        this.totalExperience = Mth.clamp(this.totalExperience + $$0, 0, Integer.MAX_VALUE);
        while (this.experienceProgress < 0.0f) {
            float $$1 = this.experienceProgress * (float)this.getXpNeededForNextLevel();
            if (this.experienceLevel > 0) {
                this.giveExperienceLevels(-1);
                this.experienceProgress = 1.0f + $$1 / (float)this.getXpNeededForNextLevel();
                continue;
            }
            this.giveExperienceLevels(-1);
            this.experienceProgress = 0.0f;
        }
        while (this.experienceProgress >= 1.0f) {
            this.experienceProgress = (this.experienceProgress - 1.0f) * (float)this.getXpNeededForNextLevel();
            this.giveExperienceLevels(1);
            this.experienceProgress /= (float)this.getXpNeededForNextLevel();
        }
    }

    public int getEnchantmentSeed() {
        return this.enchantmentSeed;
    }

    public void onEnchantmentPerformed(ItemStack $$0, int $$1) {
        this.experienceLevel -= $$1;
        if (this.experienceLevel < 0) {
            this.experienceLevel = 0;
            this.experienceProgress = 0.0f;
            this.totalExperience = 0;
        }
        this.enchantmentSeed = this.random.nextInt();
    }

    public void giveExperienceLevels(int $$0) {
        this.experienceLevel += $$0;
        if (this.experienceLevel < 0) {
            this.experienceLevel = 0;
            this.experienceProgress = 0.0f;
            this.totalExperience = 0;
        }
        if ($$0 > 0 && this.experienceLevel % 5 == 0 && (float)this.lastLevelUpTime < (float)this.tickCount - 100.0f) {
            float $$1 = this.experienceLevel > 30 ? 1.0f : (float)this.experienceLevel / 30.0f;
            this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_LEVELUP, this.getSoundSource(), $$1 * 0.75f, 1.0f);
            this.lastLevelUpTime = this.tickCount;
        }
    }

    public int getXpNeededForNextLevel() {
        if (this.experienceLevel >= 30) {
            return 112 + (this.experienceLevel - 30) * 9;
        }
        if (this.experienceLevel >= 15) {
            return 37 + (this.experienceLevel - 15) * 5;
        }
        return 7 + this.experienceLevel * 2;
    }

    public void causeFoodExhaustion(float $$0) {
        if (this.abilities.invulnerable) {
            return;
        }
        if (!this.level.isClientSide) {
            this.foodData.addExhaustion($$0);
        }
    }

    public Optional<WardenSpawnTracker> getWardenSpawnTracker() {
        return Optional.empty();
    }

    public FoodData getFoodData() {
        return this.foodData;
    }

    public boolean canEat(boolean $$0) {
        return this.abilities.invulnerable || $$0 || this.foodData.needsFood();
    }

    public boolean isHurt() {
        return this.getHealth() > 0.0f && this.getHealth() < this.getMaxHealth();
    }

    public boolean mayBuild() {
        return this.abilities.mayBuild;
    }

    public boolean mayUseItemAt(BlockPos $$0, Direction $$1, ItemStack $$2) {
        if (this.abilities.mayBuild) {
            return true;
        }
        Vec3i $$3 = $$0.relative($$1.getOpposite());
        BlockInWorld $$4 = new BlockInWorld(this.level, (BlockPos)$$3, false);
        return $$2.hasAdventureModePlaceTagForBlock(this.level.registryAccess().registryOrThrow(Registries.BLOCK), $$4);
    }

    @Override
    public int getExperienceReward() {
        if (this.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) || this.isSpectator()) {
            return 0;
        }
        int $$0 = this.experienceLevel * 7;
        if ($$0 > 100) {
            return 100;
        }
        return $$0;
    }

    @Override
    protected boolean isAlwaysExperienceDropper() {
        return true;
    }

    @Override
    public boolean shouldShowName() {
        return true;
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return !this.abilities.flying && (!this.onGround || !this.isDiscrete()) ? Entity.MovementEmission.ALL : Entity.MovementEmission.NONE;
    }

    public void onUpdateAbilities() {
    }

    @Override
    public Component getName() {
        return Component.literal(this.gameProfile.getName());
    }

    public PlayerEnderChestContainer getEnderChestInventory() {
        return this.enderChestInventory;
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot $$0) {
        if ($$0 == EquipmentSlot.MAINHAND) {
            return this.inventory.getSelected();
        }
        if ($$0 == EquipmentSlot.OFFHAND) {
            return this.inventory.offhand.get(0);
        }
        if ($$0.getType() == EquipmentSlot.Type.ARMOR) {
            return this.inventory.armor.get($$0.getIndex());
        }
        return ItemStack.EMPTY;
    }

    @Override
    protected boolean doesEmitEquipEvent(EquipmentSlot $$0) {
        return $$0.getType() == EquipmentSlot.Type.ARMOR;
    }

    @Override
    public void setItemSlot(EquipmentSlot $$0, ItemStack $$1) {
        this.verifyEquippedItem($$1);
        if ($$0 == EquipmentSlot.MAINHAND) {
            this.onEquipItem($$0, this.inventory.items.set(this.inventory.selected, $$1), $$1);
        } else if ($$0 == EquipmentSlot.OFFHAND) {
            this.onEquipItem($$0, this.inventory.offhand.set(0, $$1), $$1);
        } else if ($$0.getType() == EquipmentSlot.Type.ARMOR) {
            this.onEquipItem($$0, this.inventory.armor.set($$0.getIndex(), $$1), $$1);
        }
    }

    public boolean addItem(ItemStack $$0) {
        return this.inventory.add($$0);
    }

    @Override
    public Iterable<ItemStack> getHandSlots() {
        return Lists.newArrayList((Object[])new ItemStack[]{this.getMainHandItem(), this.getOffhandItem()});
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return this.inventory.armor;
    }

    public boolean setEntityOnShoulder(CompoundTag $$0) {
        if (this.isPassenger() || !this.onGround || this.isInWater() || this.isInPowderSnow) {
            return false;
        }
        if (this.getShoulderEntityLeft().isEmpty()) {
            this.setShoulderEntityLeft($$0);
            this.timeEntitySatOnShoulder = this.level.getGameTime();
            return true;
        }
        if (this.getShoulderEntityRight().isEmpty()) {
            this.setShoulderEntityRight($$0);
            this.timeEntitySatOnShoulder = this.level.getGameTime();
            return true;
        }
        return false;
    }

    protected void removeEntitiesOnShoulder() {
        if (this.timeEntitySatOnShoulder + 20L < this.level.getGameTime()) {
            this.respawnEntityOnShoulder(this.getShoulderEntityLeft());
            this.setShoulderEntityLeft(new CompoundTag());
            this.respawnEntityOnShoulder(this.getShoulderEntityRight());
            this.setShoulderEntityRight(new CompoundTag());
        }
    }

    private void respawnEntityOnShoulder(CompoundTag $$02) {
        if (!this.level.isClientSide && !$$02.isEmpty()) {
            EntityType.create($$02, this.level).ifPresent($$0 -> {
                if ($$0 instanceof TamableAnimal) {
                    ((TamableAnimal)$$0).setOwnerUUID(this.uuid);
                }
                $$0.setPos(this.getX(), this.getY() + (double)0.7f, this.getZ());
                ((ServerLevel)this.level).addWithUUID((Entity)$$0);
            });
        }
    }

    @Override
    public abstract boolean isSpectator();

    @Override
    public boolean isSwimming() {
        return !this.abilities.flying && !this.isSpectator() && super.isSwimming();
    }

    public abstract boolean isCreative();

    @Override
    public boolean isPushedByFluid() {
        return !this.abilities.flying;
    }

    public Scoreboard getScoreboard() {
        return this.level.getScoreboard();
    }

    @Override
    public Component getDisplayName() {
        MutableComponent $$0 = PlayerTeam.formatNameForTeam(this.getTeam(), this.getName());
        return this.decorateDisplayNameComponent($$0);
    }

    private MutableComponent decorateDisplayNameComponent(MutableComponent $$0) {
        String $$12 = this.getGameProfile().getName();
        return $$0.withStyle((UnaryOperator<Style>)((UnaryOperator)$$1 -> $$1.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tell " + $$12 + " ")).withHoverEvent(this.createHoverEvent()).withInsertion($$12)));
    }

    @Override
    public String getScoreboardName() {
        return this.getGameProfile().getName();
    }

    @Override
    public float getStandingEyeHeight(Pose $$0, EntityDimensions $$1) {
        switch ($$0) {
            case SWIMMING: 
            case FALL_FLYING: 
            case SPIN_ATTACK: {
                return 0.4f;
            }
            case CROUCHING: {
                return 1.27f;
            }
        }
        return 1.62f;
    }

    @Override
    public void setAbsorptionAmount(float $$0) {
        if ($$0 < 0.0f) {
            $$0 = 0.0f;
        }
        this.getEntityData().set(DATA_PLAYER_ABSORPTION_ID, Float.valueOf((float)$$0));
    }

    @Override
    public float getAbsorptionAmount() {
        return this.getEntityData().get(DATA_PLAYER_ABSORPTION_ID).floatValue();
    }

    public boolean isModelPartShown(PlayerModelPart $$0) {
        return (this.getEntityData().get(DATA_PLAYER_MODE_CUSTOMISATION) & $$0.getMask()) == $$0.getMask();
    }

    @Override
    public SlotAccess getSlot(int $$0) {
        if ($$0 >= 0 && $$0 < this.inventory.items.size()) {
            return SlotAccess.forContainer(this.inventory, $$0);
        }
        int $$1 = $$0 - 200;
        if ($$1 >= 0 && $$1 < this.enderChestInventory.getContainerSize()) {
            return SlotAccess.forContainer(this.enderChestInventory, $$1);
        }
        return super.getSlot($$0);
    }

    public boolean isReducedDebugInfo() {
        return this.reducedDebugInfo;
    }

    public void setReducedDebugInfo(boolean $$0) {
        this.reducedDebugInfo = $$0;
    }

    @Override
    public void setRemainingFireTicks(int $$0) {
        super.setRemainingFireTicks(this.abilities.invulnerable ? Math.min((int)$$0, (int)1) : $$0);
    }

    @Override
    public HumanoidArm getMainArm() {
        return this.entityData.get(DATA_PLAYER_MAIN_HAND) == 0 ? HumanoidArm.LEFT : HumanoidArm.RIGHT;
    }

    public void setMainArm(HumanoidArm $$0) {
        this.entityData.set(DATA_PLAYER_MAIN_HAND, (byte)($$0 != HumanoidArm.LEFT ? 1 : 0));
    }

    public CompoundTag getShoulderEntityLeft() {
        return this.entityData.get(DATA_SHOULDER_LEFT);
    }

    protected void setShoulderEntityLeft(CompoundTag $$0) {
        this.entityData.set(DATA_SHOULDER_LEFT, $$0);
    }

    public CompoundTag getShoulderEntityRight() {
        return this.entityData.get(DATA_SHOULDER_RIGHT);
    }

    protected void setShoulderEntityRight(CompoundTag $$0) {
        this.entityData.set(DATA_SHOULDER_RIGHT, $$0);
    }

    public float getCurrentItemAttackStrengthDelay() {
        return (float)(1.0 / this.getAttributeValue(Attributes.ATTACK_SPEED) * 20.0);
    }

    public float getAttackStrengthScale(float $$0) {
        return Mth.clamp(((float)this.attackStrengthTicker + $$0) / this.getCurrentItemAttackStrengthDelay(), 0.0f, 1.0f);
    }

    public void resetAttackStrengthTicker() {
        this.attackStrengthTicker = 0;
    }

    public ItemCooldowns getCooldowns() {
        return this.cooldowns;
    }

    @Override
    protected float getBlockSpeedFactor() {
        return this.abilities.flying || this.isFallFlying() ? 1.0f : super.getBlockSpeedFactor();
    }

    public float getLuck() {
        return (float)this.getAttributeValue(Attributes.LUCK);
    }

    public boolean canUseGameMasterBlocks() {
        return this.abilities.instabuild && this.getPermissionLevel() >= 2;
    }

    @Override
    public boolean canTakeItem(ItemStack $$0) {
        EquipmentSlot $$1 = Mob.getEquipmentSlotForItem($$0);
        return this.getItemBySlot($$1).isEmpty();
    }

    @Override
    public EntityDimensions getDimensions(Pose $$0) {
        return (EntityDimensions)POSES.getOrDefault((Object)$$0, (Object)STANDING_DIMENSIONS);
    }

    @Override
    public ImmutableList<Pose> getDismountPoses() {
        return ImmutableList.of((Object)((Object)Pose.STANDING), (Object)((Object)Pose.CROUCHING), (Object)((Object)Pose.SWIMMING));
    }

    @Override
    public ItemStack getProjectile(ItemStack $$0) {
        if (!($$0.getItem() instanceof ProjectileWeaponItem)) {
            return ItemStack.EMPTY;
        }
        Predicate<ItemStack> $$1 = ((ProjectileWeaponItem)$$0.getItem()).getSupportedHeldProjectiles();
        ItemStack $$2 = ProjectileWeaponItem.getHeldProjectile(this, $$1);
        if (!$$2.isEmpty()) {
            return $$2;
        }
        $$1 = ((ProjectileWeaponItem)$$0.getItem()).getAllSupportedProjectiles();
        for (int $$3 = 0; $$3 < this.inventory.getContainerSize(); ++$$3) {
            ItemStack $$4 = this.inventory.getItem($$3);
            if (!$$1.test((Object)$$4)) continue;
            return $$4;
        }
        return this.abilities.instabuild ? new ItemStack(Items.ARROW) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack eat(Level $$0, ItemStack $$1) {
        this.getFoodData().eat($$1.getItem(), $$1);
        this.awardStat(Stats.ITEM_USED.get($$1.getItem()));
        $$0.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_BURP, SoundSource.PLAYERS, 0.5f, $$0.random.nextFloat() * 0.1f + 0.9f);
        if (this instanceof ServerPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)this, $$1);
        }
        return super.eat($$0, $$1);
    }

    @Override
    protected boolean shouldRemoveSoulSpeed(BlockState $$0) {
        return this.abilities.flying || super.shouldRemoveSoulSpeed($$0);
    }

    @Override
    public Vec3 getRopeHoldPosition(float $$0) {
        double $$1 = 0.22 * (this.getMainArm() == HumanoidArm.RIGHT ? -1.0 : 1.0);
        float $$2 = Mth.lerp($$0 * 0.5f, this.getXRot(), this.xRotO) * ((float)Math.PI / 180);
        float $$3 = Mth.lerp($$0, this.yBodyRotO, this.yBodyRot) * ((float)Math.PI / 180);
        if (this.isFallFlying() || this.isAutoSpinAttack()) {
            float $$11;
            Vec3 $$4 = this.getViewVector($$0);
            Vec3 $$5 = this.getDeltaMovement();
            double $$6 = $$5.horizontalDistanceSqr();
            double $$7 = $$4.horizontalDistanceSqr();
            if ($$6 > 0.0 && $$7 > 0.0) {
                double $$8 = ($$5.x * $$4.x + $$5.z * $$4.z) / Math.sqrt((double)($$6 * $$7));
                double $$9 = $$5.x * $$4.z - $$5.z * $$4.x;
                float $$10 = (float)(Math.signum((double)$$9) * Math.acos((double)$$8));
            } else {
                $$11 = 0.0f;
            }
            return this.getPosition($$0).add(new Vec3($$1, -0.11, 0.85).zRot(-$$11).xRot(-$$2).yRot(-$$3));
        }
        if (this.isVisuallySwimming()) {
            return this.getPosition($$0).add(new Vec3($$1, 0.2, -0.15).xRot(-$$2).yRot(-$$3));
        }
        double $$12 = this.getBoundingBox().getYsize() - 1.0;
        double $$13 = this.isCrouching() ? -0.2 : 0.07;
        return this.getPosition($$0).add(new Vec3($$1, $$12, $$13).yRot(-$$3));
    }

    @Override
    public boolean isAlwaysTicking() {
        return true;
    }

    public boolean isScoping() {
        return this.isUsingItem() && this.getUseItem().is(Items.SPYGLASS);
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    public Optional<GlobalPos> getLastDeathLocation() {
        return this.lastDeathLocation;
    }

    public void setLastDeathLocation(Optional<GlobalPos> $$0) {
        this.lastDeathLocation = $$0;
    }

    public static enum BedSleepingProblem {
        NOT_POSSIBLE_HERE,
        NOT_POSSIBLE_NOW(Component.translatable("block.minecraft.bed.no_sleep")),
        TOO_FAR_AWAY(Component.translatable("block.minecraft.bed.too_far_away")),
        OBSTRUCTED(Component.translatable("block.minecraft.bed.obstructed")),
        OTHER_PROBLEM,
        NOT_SAFE(Component.translatable("block.minecraft.bed.not_safe"));

        @Nullable
        private final Component message;

        private BedSleepingProblem() {
            this.message = null;
        }

        private BedSleepingProblem(Component $$0) {
            this.message = $$0;
        }

        @Nullable
        public Component getMessage() {
            return this.message;
        }
    }
}