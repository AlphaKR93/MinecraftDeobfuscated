/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  java.lang.Boolean
 *  java.lang.Byte
 *  java.lang.Float
 *  java.lang.IllegalArgumentException
 *  java.lang.Integer
 *  java.lang.Iterable
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.Collection
 *  java.util.ConcurrentModificationException
 *  java.util.EnumMap
 *  java.util.Iterator
 *  java.util.List
 *  java.util.Map
 *  java.util.Optional
 *  java.util.UUID
 *  java.util.function.Consumer
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world.entity;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.BlockUtil;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.protocol.game.ClientboundTakeItemEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.RiderShieldingMount;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.FrostWalkerEnchantment;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HoneyBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.PowderSnowBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import org.slf4j.Logger;

public abstract class LivingEntity
extends Entity {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final UUID SPEED_MODIFIER_SPRINTING_UUID = UUID.fromString((String)"662A6B8D-DA3E-4C1C-8813-96EA6097278D");
    private static final UUID SPEED_MODIFIER_SOUL_SPEED_UUID = UUID.fromString((String)"87f46a96-686f-4796-b035-22e16ee9e038");
    private static final UUID SPEED_MODIFIER_POWDER_SNOW_UUID = UUID.fromString((String)"1eaf83ff-7207-4596-b37a-d7a07b3ec4ce");
    private static final AttributeModifier SPEED_MODIFIER_SPRINTING = new AttributeModifier(SPEED_MODIFIER_SPRINTING_UUID, "Sprinting speed boost", (double)0.3f, AttributeModifier.Operation.MULTIPLY_TOTAL);
    public static final int HAND_SLOTS = 2;
    public static final int ARMOR_SLOTS = 4;
    public static final int EQUIPMENT_SLOT_OFFSET = 98;
    public static final int ARMOR_SLOT_OFFSET = 100;
    public static final int SWING_DURATION = 6;
    public static final int PLAYER_HURT_EXPERIENCE_TIME = 100;
    private static final int DAMAGE_SOURCE_TIMEOUT = 40;
    public static final double MIN_MOVEMENT_DISTANCE = 0.003;
    public static final double DEFAULT_BASE_GRAVITY = 0.08;
    public static final int DEATH_DURATION = 20;
    private static final int WAIT_TICKS_BEFORE_ITEM_USE_EFFECTS = 7;
    private static final int TICKS_PER_ELYTRA_FREE_FALL_EVENT = 10;
    private static final int FREE_FALL_EVENTS_PER_ELYTRA_BREAK = 2;
    public static final int USE_ITEM_INTERVAL = 4;
    private static final double MAX_LINE_OF_SIGHT_TEST_RANGE = 128.0;
    protected static final int LIVING_ENTITY_FLAG_IS_USING = 1;
    protected static final int LIVING_ENTITY_FLAG_OFF_HAND = 2;
    protected static final int LIVING_ENTITY_FLAG_SPIN_ATTACK = 4;
    protected static final EntityDataAccessor<Byte> DATA_LIVING_ENTITY_FLAGS = SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Float> DATA_HEALTH_ID = SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_EFFECT_COLOR_ID = SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_EFFECT_AMBIENCE_ID = SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_ARROW_COUNT_ID = SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_STINGER_COUNT_ID = SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<BlockPos>> SLEEPING_POS_ID = SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    protected static final float DEFAULT_EYE_HEIGHT = 1.74f;
    protected static final EntityDimensions SLEEPING_DIMENSIONS = EntityDimensions.fixed(0.2f, 0.2f);
    public static final float EXTRA_RENDER_CULLING_SIZE_WITH_BIG_HAT = 0.5f;
    private final AttributeMap attributes;
    private final CombatTracker combatTracker = new CombatTracker(this);
    private final Map<MobEffect, MobEffectInstance> activeEffects = Maps.newHashMap();
    private final NonNullList<ItemStack> lastHandItemStacks = NonNullList.withSize(2, ItemStack.EMPTY);
    private final NonNullList<ItemStack> lastArmorItemStacks = NonNullList.withSize(4, ItemStack.EMPTY);
    public boolean swinging;
    private boolean discardFriction = false;
    public InteractionHand swingingArm;
    public int swingTime;
    public int removeArrowTime;
    public int removeStingerTime;
    public int hurtTime;
    public int hurtDuration;
    public float hurtDir;
    public int deathTime;
    public float oAttackAnim;
    public float attackAnim;
    protected int attackStrengthTicker;
    public float animationSpeedOld;
    public float animationSpeed;
    public float animationPosition;
    public final int invulnerableDuration = 20;
    public final float timeOffs;
    public final float rotA;
    public float yBodyRot;
    public float yBodyRotO;
    public float yHeadRot;
    public float yHeadRotO;
    public float flyingSpeed = 0.02f;
    @Nullable
    protected Player lastHurtByPlayer;
    protected int lastHurtByPlayerTime;
    protected boolean dead;
    protected int noActionTime;
    protected float oRun;
    protected float run;
    protected float animStep;
    protected float animStepO;
    protected float rotOffs;
    protected int deathScore;
    protected float lastHurt;
    protected boolean jumping;
    public float xxa;
    public float yya;
    public float zza;
    protected int lerpSteps;
    protected double lerpX;
    protected double lerpY;
    protected double lerpZ;
    protected double lerpYRot;
    protected double lerpXRot;
    protected double lyHeadRot;
    protected int lerpHeadSteps;
    private boolean effectsDirty = true;
    @Nullable
    private LivingEntity lastHurtByMob;
    private int lastHurtByMobTimestamp;
    private LivingEntity lastHurtMob;
    private int lastHurtMobTimestamp;
    private float speed;
    private int noJumpDelay;
    private float absorptionAmount;
    protected ItemStack useItem = ItemStack.EMPTY;
    protected int useItemRemaining;
    protected int fallFlyTicks;
    private BlockPos lastPos;
    private Optional<BlockPos> lastClimbablePos = Optional.empty();
    @Nullable
    private DamageSource lastDamageSource;
    private long lastDamageStamp;
    protected int autoSpinAttackTicks;
    private float swimAmount;
    private float swimAmountO;
    protected Brain<?> brain;
    private boolean skipDropExperience;

    protected LivingEntity(EntityType<? extends LivingEntity> $$0, Level $$1) {
        super($$0, $$1);
        this.attributes = new AttributeMap(DefaultAttributes.getSupplier($$0));
        this.setHealth(this.getMaxHealth());
        this.blocksBuilding = true;
        this.rotA = (float)((Math.random() + 1.0) * (double)0.01f);
        this.reapplyPosition();
        this.timeOffs = (float)Math.random() * 12398.0f;
        this.setYRot((float)(Math.random() * 6.2831854820251465));
        this.yHeadRot = this.getYRot();
        this.maxUpStep = 0.6f;
        NbtOps $$2 = NbtOps.INSTANCE;
        this.brain = this.makeBrain(new Dynamic((DynamicOps)$$2, (Object)((Tag)$$2.createMap((Map)ImmutableMap.of((Object)$$2.createString("memories"), (Object)((Tag)$$2.emptyMap()))))));
    }

    public Brain<?> getBrain() {
        return this.brain;
    }

    protected Brain.Provider<?> brainProvider() {
        return Brain.provider(ImmutableList.of(), ImmutableList.of());
    }

    protected Brain<?> makeBrain(Dynamic<?> $$0) {
        return this.brainProvider().makeBrain($$0);
    }

    @Override
    public void kill() {
        this.hurt(DamageSource.OUT_OF_WORLD, Float.MAX_VALUE);
    }

    public boolean canAttackType(EntityType<?> $$0) {
        return true;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_LIVING_ENTITY_FLAGS, (byte)0);
        this.entityData.define(DATA_EFFECT_COLOR_ID, 0);
        this.entityData.define(DATA_EFFECT_AMBIENCE_ID, false);
        this.entityData.define(DATA_ARROW_COUNT_ID, 0);
        this.entityData.define(DATA_STINGER_COUNT_ID, 0);
        this.entityData.define(DATA_HEALTH_ID, Float.valueOf((float)1.0f));
        this.entityData.define(SLEEPING_POS_ID, Optional.empty());
    }

    public static AttributeSupplier.Builder createLivingAttributes() {
        return AttributeSupplier.builder().add(Attributes.MAX_HEALTH).add(Attributes.KNOCKBACK_RESISTANCE).add(Attributes.MOVEMENT_SPEED).add(Attributes.ARMOR).add(Attributes.ARMOR_TOUGHNESS);
    }

    @Override
    protected void checkFallDamage(double $$0, boolean $$1, BlockState $$2, BlockPos $$3) {
        if (!this.isInWater()) {
            this.updateInWaterStateAndDoWaterCurrentPushing();
        }
        if (!this.level.isClientSide && $$1 && this.fallDistance > 0.0f) {
            this.removeSoulSpeed();
            this.tryAddSoulSpeed();
        }
        if (!this.level.isClientSide && this.fallDistance > 3.0f && $$1) {
            float $$4 = Mth.ceil(this.fallDistance - 3.0f);
            if (!$$2.isAir()) {
                double $$5 = Math.min((double)(0.2f + $$4 / 15.0f), (double)2.5);
                int $$6 = (int)(150.0 * $$5);
                ((ServerLevel)this.level).sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, $$2), this.getX(), this.getY(), this.getZ(), $$6, 0.0, 0.0, 0.0, 0.15f);
            }
        }
        super.checkFallDamage($$0, $$1, $$2, $$3);
    }

    public boolean canBreatheUnderwater() {
        return this.getMobType() == MobType.UNDEAD;
    }

    public float getSwimAmount(float $$0) {
        return Mth.lerp($$0, this.swimAmountO, this.swimAmount);
    }

    @Override
    public void baseTick() {
        this.oAttackAnim = this.attackAnim;
        if (this.firstTick) {
            this.getSleepingPos().ifPresent(this::setPosToBed);
        }
        if (this.canSpawnSoulSpeedParticle()) {
            this.spawnSoulSpeedParticle();
        }
        super.baseTick();
        this.level.getProfiler().push("livingEntityBaseTick");
        if (this.fireImmune() || this.level.isClientSide) {
            this.clearFire();
        }
        if (this.isAlive()) {
            BlockPos $$9;
            boolean $$0 = this instanceof Player;
            if (!this.level.isClientSide) {
                double $$2;
                double $$1;
                if (this.isInWall()) {
                    this.hurt(DamageSource.IN_WALL, 1.0f);
                } else if ($$0 && !this.level.getWorldBorder().isWithinBounds(this.getBoundingBox()) && ($$1 = this.level.getWorldBorder().getDistanceToBorder(this) + this.level.getWorldBorder().getDamageSafeZone()) < 0.0 && ($$2 = this.level.getWorldBorder().getDamagePerBlock()) > 0.0) {
                    this.hurt(DamageSource.IN_WALL, Math.max((int)1, (int)Mth.floor(-$$1 * $$2)));
                }
            }
            if (this.isEyeInFluid(FluidTags.WATER) && !this.level.getBlockState(new BlockPos(this.getX(), this.getEyeY(), this.getZ())).is(Blocks.BUBBLE_COLUMN)) {
                boolean $$3;
                boolean bl = $$3 = !this.canBreatheUnderwater() && !MobEffectUtil.hasWaterBreathing(this) && (!$$0 || !((Player)this).getAbilities().invulnerable);
                if ($$3) {
                    this.setAirSupply(this.decreaseAirSupply(this.getAirSupply()));
                    if (this.getAirSupply() == -20) {
                        this.setAirSupply(0);
                        Vec3 $$4 = this.getDeltaMovement();
                        for (int $$5 = 0; $$5 < 8; ++$$5) {
                            double $$6 = this.random.nextDouble() - this.random.nextDouble();
                            double $$7 = this.random.nextDouble() - this.random.nextDouble();
                            double $$8 = this.random.nextDouble() - this.random.nextDouble();
                            this.level.addParticle(ParticleTypes.BUBBLE, this.getX() + $$6, this.getY() + $$7, this.getZ() + $$8, $$4.x, $$4.y, $$4.z);
                        }
                        this.hurt(DamageSource.DROWN, 2.0f);
                    }
                }
                if (!this.level.isClientSide && this.isPassenger() && this.getVehicle() != null && !this.getVehicle().rideableUnderWater()) {
                    this.stopRiding();
                }
            } else if (this.getAirSupply() < this.getMaxAirSupply()) {
                this.setAirSupply(this.increaseAirSupply(this.getAirSupply()));
            }
            if (!this.level.isClientSide && !Objects.equal((Object)this.lastPos, (Object)($$9 = this.blockPosition()))) {
                this.lastPos = $$9;
                this.onChangedBlock($$9);
            }
        }
        if (this.isAlive() && (this.isInWaterRainOrBubble() || this.isInPowderSnow)) {
            this.extinguishFire();
        }
        if (this.hurtTime > 0) {
            --this.hurtTime;
        }
        if (this.invulnerableTime > 0 && !(this instanceof ServerPlayer)) {
            --this.invulnerableTime;
        }
        if (this.isDeadOrDying() && this.level.shouldTickDeath(this)) {
            this.tickDeath();
        }
        if (this.lastHurtByPlayerTime > 0) {
            --this.lastHurtByPlayerTime;
        } else {
            this.lastHurtByPlayer = null;
        }
        if (this.lastHurtMob != null && !this.lastHurtMob.isAlive()) {
            this.lastHurtMob = null;
        }
        if (this.lastHurtByMob != null) {
            if (!this.lastHurtByMob.isAlive()) {
                this.setLastHurtByMob(null);
            } else if (this.tickCount - this.lastHurtByMobTimestamp > 100) {
                this.setLastHurtByMob(null);
            }
        }
        this.tickEffects();
        this.animStepO = this.animStep;
        this.yBodyRotO = this.yBodyRot;
        this.yHeadRotO = this.yHeadRot;
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
        this.level.getProfiler().pop();
    }

    public boolean canSpawnSoulSpeedParticle() {
        return this.tickCount % 5 == 0 && this.getDeltaMovement().x != 0.0 && this.getDeltaMovement().z != 0.0 && !this.isSpectator() && EnchantmentHelper.hasSoulSpeed(this) && this.onSoulSpeedBlock();
    }

    protected void spawnSoulSpeedParticle() {
        Vec3 $$0 = this.getDeltaMovement();
        this.level.addParticle(ParticleTypes.SOUL, this.getX() + (this.random.nextDouble() - 0.5) * (double)this.getBbWidth(), this.getY() + 0.1, this.getZ() + (this.random.nextDouble() - 0.5) * (double)this.getBbWidth(), $$0.x * -0.2, 0.1, $$0.z * -0.2);
        float $$1 = this.random.nextFloat() * 0.4f + this.random.nextFloat() > 0.9f ? 0.6f : 0.0f;
        this.playSound(SoundEvents.SOUL_ESCAPE, $$1, 0.6f + this.random.nextFloat() * 0.4f);
    }

    protected boolean onSoulSpeedBlock() {
        return this.level.getBlockState(this.getBlockPosBelowThatAffectsMyMovement()).is(BlockTags.SOUL_SPEED_BLOCKS);
    }

    @Override
    protected float getBlockSpeedFactor() {
        if (this.onSoulSpeedBlock() && EnchantmentHelper.getEnchantmentLevel(Enchantments.SOUL_SPEED, this) > 0) {
            return 1.0f;
        }
        return super.getBlockSpeedFactor();
    }

    protected boolean shouldRemoveSoulSpeed(BlockState $$0) {
        return !$$0.isAir() || this.isFallFlying();
    }

    protected void removeSoulSpeed() {
        AttributeInstance $$0 = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if ($$0 == null) {
            return;
        }
        if ($$0.getModifier(SPEED_MODIFIER_SOUL_SPEED_UUID) != null) {
            $$0.removeModifier(SPEED_MODIFIER_SOUL_SPEED_UUID);
        }
    }

    protected void tryAddSoulSpeed() {
        int $$02;
        if (!this.getBlockStateOnLegacy().isAir() && ($$02 = EnchantmentHelper.getEnchantmentLevel(Enchantments.SOUL_SPEED, this)) > 0 && this.onSoulSpeedBlock()) {
            AttributeInstance $$1 = this.getAttribute(Attributes.MOVEMENT_SPEED);
            if ($$1 == null) {
                return;
            }
            $$1.addTransientModifier(new AttributeModifier(SPEED_MODIFIER_SOUL_SPEED_UUID, "Soul speed boost", (double)(0.03f * (1.0f + (float)$$02 * 0.35f)), AttributeModifier.Operation.ADDITION));
            if (this.getRandom().nextFloat() < 0.04f) {
                ItemStack $$2 = this.getItemBySlot(EquipmentSlot.FEET);
                $$2.hurtAndBreak(1, this, $$0 -> $$0.broadcastBreakEvent(EquipmentSlot.FEET));
            }
        }
    }

    protected void removeFrost() {
        AttributeInstance $$0 = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if ($$0 == null) {
            return;
        }
        if ($$0.getModifier(SPEED_MODIFIER_POWDER_SNOW_UUID) != null) {
            $$0.removeModifier(SPEED_MODIFIER_POWDER_SNOW_UUID);
        }
    }

    protected void tryAddFrost() {
        int $$0;
        if (!this.getBlockStateOnLegacy().isAir() && ($$0 = this.getTicksFrozen()) > 0) {
            AttributeInstance $$1 = this.getAttribute(Attributes.MOVEMENT_SPEED);
            if ($$1 == null) {
                return;
            }
            float $$2 = -0.05f * this.getPercentFrozen();
            $$1.addTransientModifier(new AttributeModifier(SPEED_MODIFIER_POWDER_SNOW_UUID, "Powder snow slow", (double)$$2, AttributeModifier.Operation.ADDITION));
        }
    }

    protected void onChangedBlock(BlockPos $$0) {
        int $$1 = EnchantmentHelper.getEnchantmentLevel(Enchantments.FROST_WALKER, this);
        if ($$1 > 0) {
            FrostWalkerEnchantment.onEntityMoved(this, this.level, $$0, $$1);
        }
        if (this.shouldRemoveSoulSpeed(this.getBlockStateOnLegacy())) {
            this.removeSoulSpeed();
        }
        this.tryAddSoulSpeed();
    }

    public boolean isBaby() {
        return false;
    }

    public float getScale() {
        return this.isBaby() ? 0.5f : 1.0f;
    }

    protected boolean isAffectedByFluids() {
        return true;
    }

    @Override
    public boolean rideableUnderWater() {
        return false;
    }

    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime >= 20 && !this.level.isClientSide() && !this.isRemoved()) {
            this.level.broadcastEntityEvent(this, (byte)60);
            this.remove(Entity.RemovalReason.KILLED);
        }
    }

    public boolean shouldDropExperience() {
        return !this.isBaby();
    }

    protected boolean shouldDropLoot() {
        return !this.isBaby();
    }

    protected int decreaseAirSupply(int $$0) {
        int $$1 = EnchantmentHelper.getRespiration(this);
        if ($$1 > 0 && this.random.nextInt($$1 + 1) > 0) {
            return $$0;
        }
        return $$0 - 1;
    }

    protected int increaseAirSupply(int $$0) {
        return Math.min((int)($$0 + 4), (int)this.getMaxAirSupply());
    }

    public int getExperienceReward() {
        return 0;
    }

    protected boolean isAlwaysExperienceDropper() {
        return false;
    }

    public RandomSource getRandom() {
        return this.random;
    }

    @Nullable
    public LivingEntity getLastHurtByMob() {
        return this.lastHurtByMob;
    }

    public int getLastHurtByMobTimestamp() {
        return this.lastHurtByMobTimestamp;
    }

    public void setLastHurtByPlayer(@Nullable Player $$0) {
        this.lastHurtByPlayer = $$0;
        this.lastHurtByPlayerTime = this.tickCount;
    }

    public void setLastHurtByMob(@Nullable LivingEntity $$0) {
        this.lastHurtByMob = $$0;
        this.lastHurtByMobTimestamp = this.tickCount;
    }

    @Nullable
    public LivingEntity getLastHurtMob() {
        return this.lastHurtMob;
    }

    public int getLastHurtMobTimestamp() {
        return this.lastHurtMobTimestamp;
    }

    public void setLastHurtMob(Entity $$0) {
        this.lastHurtMob = $$0 instanceof LivingEntity ? (LivingEntity)$$0 : null;
        this.lastHurtMobTimestamp = this.tickCount;
    }

    public int getNoActionTime() {
        return this.noActionTime;
    }

    public void setNoActionTime(int $$0) {
        this.noActionTime = $$0;
    }

    public boolean shouldDiscardFriction() {
        return this.discardFriction;
    }

    public void setDiscardFriction(boolean $$0) {
        this.discardFriction = $$0;
    }

    protected boolean doesEmitEquipEvent(EquipmentSlot $$0) {
        return true;
    }

    public void onEquipItem(EquipmentSlot $$0, ItemStack $$1, ItemStack $$2) {
        boolean $$3;
        boolean bl = $$3 = $$2.isEmpty() && $$1.isEmpty();
        if ($$3 || ItemStack.isSame($$1, $$2) || this.firstTick) {
            return;
        }
        if ($$0.getType() == EquipmentSlot.Type.ARMOR) {
            this.playEquipSound($$2);
        }
        if (this.doesEmitEquipEvent($$0)) {
            this.gameEvent(GameEvent.EQUIP);
        }
    }

    protected void playEquipSound(ItemStack $$0) {
        if ($$0.isEmpty() || this.isSpectator()) {
            return;
        }
        SoundEvent $$1 = $$0.getEquipSound();
        if ($$1 != null) {
            this.playSound($$1, 1.0f, 1.0f);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        $$0.putFloat("Health", this.getHealth());
        $$0.putShort("HurtTime", (short)this.hurtTime);
        $$0.putInt("HurtByTimestamp", this.lastHurtByMobTimestamp);
        $$0.putShort("DeathTime", (short)this.deathTime);
        $$0.putFloat("AbsorptionAmount", this.getAbsorptionAmount());
        $$0.put("Attributes", this.getAttributes().save());
        if (!this.activeEffects.isEmpty()) {
            ListTag $$12 = new ListTag();
            for (MobEffectInstance $$2 : this.activeEffects.values()) {
                $$12.add($$2.save(new CompoundTag()));
            }
            $$0.put("ActiveEffects", $$12);
        }
        $$0.putBoolean("FallFlying", this.isFallFlying());
        this.getSleepingPos().ifPresent($$1 -> {
            $$0.putInt("SleepingX", $$1.getX());
            $$0.putInt("SleepingY", $$1.getY());
            $$0.putInt("SleepingZ", $$1.getZ());
        });
        DataResult<Tag> $$3 = this.brain.serializeStart(NbtOps.INSTANCE);
        $$3.resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).ifPresent($$1 -> $$0.put("Brain", (Tag)$$1));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$0) {
        this.setAbsorptionAmount($$0.getFloat("AbsorptionAmount"));
        if ($$0.contains("Attributes", 9) && this.level != null && !this.level.isClientSide) {
            this.getAttributes().load($$0.getList("Attributes", 10));
        }
        if ($$0.contains("ActiveEffects", 9)) {
            ListTag $$1 = $$0.getList("ActiveEffects", 10);
            for (int $$2 = 0; $$2 < $$1.size(); ++$$2) {
                CompoundTag $$3 = $$1.getCompound($$2);
                MobEffectInstance $$4 = MobEffectInstance.load($$3);
                if ($$4 == null) continue;
                this.activeEffects.put((Object)$$4.getEffect(), (Object)$$4);
            }
        }
        if ($$0.contains("Health", 99)) {
            this.setHealth($$0.getFloat("Health"));
        }
        this.hurtTime = $$0.getShort("HurtTime");
        this.deathTime = $$0.getShort("DeathTime");
        this.lastHurtByMobTimestamp = $$0.getInt("HurtByTimestamp");
        if ($$0.contains("Team", 8)) {
            boolean $$7;
            String $$5 = $$0.getString("Team");
            PlayerTeam $$6 = this.level.getScoreboard().getPlayerTeam($$5);
            boolean bl = $$7 = $$6 != null && this.level.getScoreboard().addPlayerToTeam(this.getStringUUID(), $$6);
            if (!$$7) {
                LOGGER.warn("Unable to add mob to team \"{}\" (that team probably doesn't exist)", (Object)$$5);
            }
        }
        if ($$0.getBoolean("FallFlying")) {
            this.setSharedFlag(7, true);
        }
        if ($$0.contains("SleepingX", 99) && $$0.contains("SleepingY", 99) && $$0.contains("SleepingZ", 99)) {
            BlockPos $$8 = new BlockPos($$0.getInt("SleepingX"), $$0.getInt("SleepingY"), $$0.getInt("SleepingZ"));
            this.setSleepingPos($$8);
            this.entityData.set(DATA_POSE, Pose.SLEEPING);
            if (!this.firstTick) {
                this.setPosToBed($$8);
            }
        }
        if ($$0.contains("Brain", 10)) {
            this.brain = this.makeBrain(new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)$$0.get("Brain")));
        }
    }

    protected void tickEffects() {
        Iterator $$0 = this.activeEffects.keySet().iterator();
        try {
            while ($$0.hasNext()) {
                MobEffect $$1 = (MobEffect)$$0.next();
                MobEffectInstance $$2 = (MobEffectInstance)this.activeEffects.get((Object)$$1);
                if (!$$2.tick(this, () -> this.onEffectUpdated($$2, true, null))) {
                    if (this.level.isClientSide) continue;
                    $$0.remove();
                    this.onEffectRemoved($$2);
                    continue;
                }
                if ($$2.getDuration() % 600 != 0) continue;
                this.onEffectUpdated($$2, false, null);
            }
        }
        catch (ConcurrentModificationException $$1) {
            // empty catch block
        }
        if (this.effectsDirty) {
            if (!this.level.isClientSide) {
                this.updateInvisibilityStatus();
                this.updateGlowingStatus();
            }
            this.effectsDirty = false;
        }
        int $$3 = this.entityData.get(DATA_EFFECT_COLOR_ID);
        boolean $$4 = this.entityData.get(DATA_EFFECT_AMBIENCE_ID);
        if ($$3 > 0) {
            boolean $$6;
            if (this.isInvisible()) {
                boolean $$5 = this.random.nextInt(15) == 0;
            } else {
                $$6 = this.random.nextBoolean();
            }
            if ($$4) {
                $$6 &= this.random.nextInt(5) == 0;
            }
            if ($$6 && $$3 > 0) {
                double $$7 = (double)($$3 >> 16 & 0xFF) / 255.0;
                double $$8 = (double)($$3 >> 8 & 0xFF) / 255.0;
                double $$9 = (double)($$3 >> 0 & 0xFF) / 255.0;
                this.level.addParticle($$4 ? ParticleTypes.AMBIENT_ENTITY_EFFECT : ParticleTypes.ENTITY_EFFECT, this.getRandomX(0.5), this.getRandomY(), this.getRandomZ(0.5), $$7, $$8, $$9);
            }
        }
    }

    protected void updateInvisibilityStatus() {
        if (this.activeEffects.isEmpty()) {
            this.removeEffectParticles();
            this.setInvisible(false);
        } else {
            Collection $$0 = this.activeEffects.values();
            this.entityData.set(DATA_EFFECT_AMBIENCE_ID, LivingEntity.areAllEffectsAmbient((Collection<MobEffectInstance>)$$0));
            this.entityData.set(DATA_EFFECT_COLOR_ID, PotionUtils.getColor((Collection<MobEffectInstance>)$$0));
            this.setInvisible(this.hasEffect(MobEffects.INVISIBILITY));
        }
    }

    private void updateGlowingStatus() {
        boolean $$0 = this.isCurrentlyGlowing();
        if (this.getSharedFlag(6) != $$0) {
            this.setSharedFlag(6, $$0);
        }
    }

    public double getVisibilityPercent(@Nullable Entity $$0) {
        double $$1 = 1.0;
        if (this.isDiscrete()) {
            $$1 *= 0.8;
        }
        if (this.isInvisible()) {
            float $$2 = this.getArmorCoverPercentage();
            if ($$2 < 0.1f) {
                $$2 = 0.1f;
            }
            $$1 *= 0.7 * (double)$$2;
        }
        if ($$0 != null) {
            ItemStack $$3 = this.getItemBySlot(EquipmentSlot.HEAD);
            EntityType<?> $$4 = $$0.getType();
            if ($$4 == EntityType.SKELETON && $$3.is(Items.SKELETON_SKULL) || $$4 == EntityType.ZOMBIE && $$3.is(Items.ZOMBIE_HEAD) || $$4 == EntityType.PIGLIN && $$3.is(Items.PIGLIN_HEAD) || $$4 == EntityType.PIGLIN_BRUTE && $$3.is(Items.PIGLIN_HEAD) || $$4 == EntityType.CREEPER && $$3.is(Items.CREEPER_HEAD)) {
                $$1 *= 0.5;
            }
        }
        return $$1;
    }

    public boolean canAttack(LivingEntity $$0) {
        if ($$0 instanceof Player && this.level.getDifficulty() == Difficulty.PEACEFUL) {
            return false;
        }
        return $$0.canBeSeenAsEnemy();
    }

    public boolean canAttack(LivingEntity $$0, TargetingConditions $$1) {
        return $$1.test(this, $$0);
    }

    public boolean canBeSeenAsEnemy() {
        return !this.isInvulnerable() && this.canBeSeenByAnyone();
    }

    public boolean canBeSeenByAnyone() {
        return !this.isSpectator() && this.isAlive();
    }

    public static boolean areAllEffectsAmbient(Collection<MobEffectInstance> $$0) {
        for (MobEffectInstance $$1 : $$0) {
            if (!$$1.isVisible() || $$1.isAmbient()) continue;
            return false;
        }
        return true;
    }

    protected void removeEffectParticles() {
        this.entityData.set(DATA_EFFECT_AMBIENCE_ID, false);
        this.entityData.set(DATA_EFFECT_COLOR_ID, 0);
    }

    public boolean removeAllEffects() {
        if (this.level.isClientSide) {
            return false;
        }
        Iterator $$0 = this.activeEffects.values().iterator();
        boolean $$1 = false;
        while ($$0.hasNext()) {
            this.onEffectRemoved((MobEffectInstance)$$0.next());
            $$0.remove();
            $$1 = true;
        }
        return $$1;
    }

    public Collection<MobEffectInstance> getActiveEffects() {
        return this.activeEffects.values();
    }

    public Map<MobEffect, MobEffectInstance> getActiveEffectsMap() {
        return this.activeEffects;
    }

    public boolean hasEffect(MobEffect $$0) {
        return this.activeEffects.containsKey((Object)$$0);
    }

    @Nullable
    public MobEffectInstance getEffect(MobEffect $$0) {
        return (MobEffectInstance)this.activeEffects.get((Object)$$0);
    }

    public final boolean addEffect(MobEffectInstance $$0) {
        return this.addEffect($$0, null);
    }

    public boolean addEffect(MobEffectInstance $$0, @Nullable Entity $$1) {
        if (!this.canBeAffected($$0)) {
            return false;
        }
        MobEffectInstance $$2 = (MobEffectInstance)this.activeEffects.get((Object)$$0.getEffect());
        if ($$2 == null) {
            this.activeEffects.put((Object)$$0.getEffect(), (Object)$$0);
            this.onEffectAdded($$0, $$1);
            return true;
        }
        if ($$2.update($$0)) {
            this.onEffectUpdated($$2, true, $$1);
            return true;
        }
        return false;
    }

    public boolean canBeAffected(MobEffectInstance $$0) {
        MobEffect $$1;
        return this.getMobType() != MobType.UNDEAD || ($$1 = $$0.getEffect()) != MobEffects.REGENERATION && $$1 != MobEffects.POISON;
    }

    public void forceAddEffect(MobEffectInstance $$0, @Nullable Entity $$1) {
        if (!this.canBeAffected($$0)) {
            return;
        }
        MobEffectInstance $$2 = (MobEffectInstance)this.activeEffects.put((Object)$$0.getEffect(), (Object)$$0);
        if ($$2 == null) {
            this.onEffectAdded($$0, $$1);
        } else {
            this.onEffectUpdated($$0, true, $$1);
        }
    }

    public boolean isInvertedHealAndHarm() {
        return this.getMobType() == MobType.UNDEAD;
    }

    @Nullable
    public MobEffectInstance removeEffectNoUpdate(@Nullable MobEffect $$0) {
        return (MobEffectInstance)this.activeEffects.remove((Object)$$0);
    }

    public boolean removeEffect(MobEffect $$0) {
        MobEffectInstance $$1 = this.removeEffectNoUpdate($$0);
        if ($$1 != null) {
            this.onEffectRemoved($$1);
            return true;
        }
        return false;
    }

    protected void onEffectAdded(MobEffectInstance $$0, @Nullable Entity $$1) {
        this.effectsDirty = true;
        if (!this.level.isClientSide) {
            $$0.getEffect().addAttributeModifiers(this, this.getAttributes(), $$0.getAmplifier());
        }
    }

    protected void onEffectUpdated(MobEffectInstance $$0, boolean $$1, @Nullable Entity $$2) {
        this.effectsDirty = true;
        if ($$1 && !this.level.isClientSide) {
            MobEffect $$3 = $$0.getEffect();
            $$3.removeAttributeModifiers(this, this.getAttributes(), $$0.getAmplifier());
            $$3.addAttributeModifiers(this, this.getAttributes(), $$0.getAmplifier());
        }
    }

    protected void onEffectRemoved(MobEffectInstance $$0) {
        this.effectsDirty = true;
        if (!this.level.isClientSide) {
            $$0.getEffect().removeAttributeModifiers(this, this.getAttributes(), $$0.getAmplifier());
        }
    }

    public void heal(float $$0) {
        float $$1 = this.getHealth();
        if ($$1 > 0.0f) {
            this.setHealth($$1 + $$0);
        }
    }

    public float getHealth() {
        return this.entityData.get(DATA_HEALTH_ID).floatValue();
    }

    public void setHealth(float $$0) {
        this.entityData.set(DATA_HEALTH_ID, Float.valueOf((float)Mth.clamp($$0, 0.0f, this.getMaxHealth())));
    }

    public boolean isDeadOrDying() {
        return this.getHealth() <= 0.0f;
    }

    @Override
    public boolean hurt(DamageSource $$0, float $$1) {
        boolean $$19;
        if (this.isInvulnerableTo($$0)) {
            return false;
        }
        if (this.level.isClientSide) {
            return false;
        }
        if (this.isDeadOrDying()) {
            return false;
        }
        if ($$0.isFire() && this.hasEffect(MobEffects.FIRE_RESISTANCE)) {
            return false;
        }
        if (this.isSleeping() && !this.level.isClientSide) {
            this.stopSleeping();
        }
        this.noActionTime = 0;
        float $$2 = $$1;
        boolean $$3 = false;
        float $$4 = 0.0f;
        if ($$1 > 0.0f && this.isDamageSourceBlocked($$0)) {
            Entity $$5;
            this.hurtCurrentlyUsedShield($$1);
            $$4 = $$1;
            $$1 = 0.0f;
            if (!$$0.isProjectile() && ($$5 = $$0.getDirectEntity()) instanceof LivingEntity) {
                LivingEntity $$6 = (LivingEntity)$$5;
                this.blockUsingShield($$6);
            }
            $$3 = true;
        }
        this.animationSpeed = 1.5f;
        boolean $$7 = true;
        if ((float)this.invulnerableTime > 10.0f) {
            if ($$1 <= this.lastHurt) {
                return false;
            }
            this.actuallyHurt($$0, $$1 - this.lastHurt);
            this.lastHurt = $$1;
            $$7 = false;
        } else {
            this.lastHurt = $$1;
            this.invulnerableTime = 20;
            this.actuallyHurt($$0, $$1);
            this.hurtTime = this.hurtDuration = 10;
        }
        if ($$0.isDamageHelmet() && !this.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
            this.hurtHelmet($$0, $$1);
            $$1 *= 0.75f;
        }
        this.hurtDir = 0.0f;
        Entity $$8 = $$0.getEntity();
        if ($$8 != null) {
            Wolf $$9;
            if ($$8 instanceof LivingEntity && !$$0.isNoAggro()) {
                this.setLastHurtByMob((LivingEntity)$$8);
            }
            if ($$8 instanceof Player) {
                this.lastHurtByPlayerTime = 100;
                this.lastHurtByPlayer = (Player)$$8;
            } else if ($$8 instanceof Wolf && ($$9 = (Wolf)$$8).isTame()) {
                this.lastHurtByPlayerTime = 100;
                LivingEntity $$10 = $$9.getOwner();
                this.lastHurtByPlayer = $$10 != null && $$10.getType() == EntityType.PLAYER ? (Player)$$10 : null;
            }
        }
        if ($$7) {
            if ($$3) {
                this.level.broadcastEntityEvent(this, (byte)29);
            } else if ($$0 instanceof EntityDamageSource && ((EntityDamageSource)$$0).isThorns()) {
                this.level.broadcastEntityEvent(this, (byte)33);
            } else {
                int $$15;
                if ($$0 == DamageSource.DROWN) {
                    int $$11 = 36;
                } else if ($$0.isFire()) {
                    int $$12 = 37;
                } else if ($$0 == DamageSource.SWEET_BERRY_BUSH) {
                    int $$13 = 44;
                } else if ($$0 == DamageSource.FREEZE) {
                    int $$14 = 57;
                } else {
                    $$15 = 2;
                }
                this.level.broadcastEntityEvent(this, (byte)$$15);
            }
            if ($$0 != DamageSource.DROWN && (!$$3 || $$1 > 0.0f)) {
                this.markHurt();
            }
            if ($$8 != null && !$$0.isExplosion()) {
                double $$16 = $$8.getX() - this.getX();
                double $$17 = $$8.getZ() - this.getZ();
                while ($$16 * $$16 + $$17 * $$17 < 1.0E-4) {
                    $$16 = (Math.random() - Math.random()) * 0.01;
                    $$17 = (Math.random() - Math.random()) * 0.01;
                }
                this.hurtDir = (float)(Mth.atan2($$17, $$16) * 57.2957763671875 - (double)this.getYRot());
                this.knockback(0.4f, $$16, $$17);
            } else {
                this.hurtDir = (int)(Math.random() * 2.0) * 180;
            }
        }
        if (this.isDeadOrDying()) {
            if (!this.checkTotemDeathProtection($$0)) {
                SoundEvent $$18 = this.getDeathSound();
                if ($$7 && $$18 != null) {
                    this.playSound($$18, this.getSoundVolume(), this.getVoicePitch());
                }
                this.die($$0);
            }
        } else if ($$7) {
            this.playHurtSound($$0);
        }
        boolean bl = $$19 = !$$3 || $$1 > 0.0f;
        if ($$19) {
            this.lastDamageSource = $$0;
            this.lastDamageStamp = this.level.getGameTime();
        }
        if (this instanceof ServerPlayer) {
            CriteriaTriggers.ENTITY_HURT_PLAYER.trigger((ServerPlayer)this, $$0, $$2, $$1, $$3);
            if ($$4 > 0.0f && $$4 < 3.4028235E37f) {
                ((ServerPlayer)this).awardStat(Stats.DAMAGE_BLOCKED_BY_SHIELD, Math.round((float)($$4 * 10.0f)));
            }
        }
        if ($$8 instanceof ServerPlayer) {
            CriteriaTriggers.PLAYER_HURT_ENTITY.trigger((ServerPlayer)$$8, this, $$0, $$2, $$1, $$3);
        }
        return $$19;
    }

    protected void blockUsingShield(LivingEntity $$0) {
        $$0.blockedByShield(this);
    }

    protected void blockedByShield(LivingEntity $$0) {
        $$0.knockback(0.5, $$0.getX() - this.getX(), $$0.getZ() - this.getZ());
    }

    private boolean checkTotemDeathProtection(DamageSource $$0) {
        if ($$0.isBypassInvul()) {
            return false;
        }
        ItemStack $$1 = null;
        for (InteractionHand $$2 : InteractionHand.values()) {
            ItemStack $$3 = this.getItemInHand($$2);
            if (!$$3.is(Items.TOTEM_OF_UNDYING)) continue;
            $$1 = $$3.copy();
            $$3.shrink(1);
            break;
        }
        if ($$1 != null) {
            if (this instanceof ServerPlayer) {
                ServerPlayer $$4 = (ServerPlayer)this;
                $$4.awardStat(Stats.ITEM_USED.get(Items.TOTEM_OF_UNDYING));
                CriteriaTriggers.USED_TOTEM.trigger($$4, $$1);
            }
            this.setHealth(1.0f);
            this.removeAllEffects();
            this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
            this.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
            this.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
            this.level.broadcastEntityEvent(this, (byte)35);
        }
        return $$1 != null;
    }

    @Nullable
    public DamageSource getLastDamageSource() {
        if (this.level.getGameTime() - this.lastDamageStamp > 40L) {
            this.lastDamageSource = null;
        }
        return this.lastDamageSource;
    }

    protected void playHurtSound(DamageSource $$0) {
        SoundEvent $$1 = this.getHurtSound($$0);
        if ($$1 != null) {
            this.playSound($$1, this.getSoundVolume(), this.getVoicePitch());
        }
    }

    public boolean isDamageSourceBlocked(DamageSource $$0) {
        Vec3 $$4;
        AbstractArrow $$3;
        Entity $$1 = $$0.getDirectEntity();
        boolean $$2 = false;
        if ($$1 instanceof AbstractArrow && ($$3 = (AbstractArrow)$$1).getPierceLevel() > 0) {
            $$2 = true;
        }
        if (!$$0.isBypassArmor() && this.isBlocking() && !$$2 && ($$4 = $$0.getSourcePosition()) != null) {
            Vec3 $$5 = this.getViewVector(1.0f);
            Vec3 $$6 = $$4.vectorTo(this.position()).normalize();
            $$6 = new Vec3($$6.x, 0.0, $$6.z);
            if ($$6.dot($$5) < 0.0) {
                return true;
            }
        }
        return false;
    }

    private void breakItem(ItemStack $$0) {
        if (!$$0.isEmpty()) {
            if (!this.isSilent()) {
                this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ITEM_BREAK, this.getSoundSource(), 0.8f, 0.8f + this.level.random.nextFloat() * 0.4f, false);
            }
            this.spawnItemParticles($$0, 5);
        }
    }

    public void die(DamageSource $$0) {
        if (this.isRemoved() || this.dead) {
            return;
        }
        Entity $$1 = $$0.getEntity();
        LivingEntity $$2 = this.getKillCredit();
        if (this.deathScore >= 0 && $$2 != null) {
            $$2.awardKillScore(this, this.deathScore, $$0);
        }
        if (this.isSleeping()) {
            this.stopSleeping();
        }
        if (!this.level.isClientSide && this.hasCustomName()) {
            LOGGER.info("Named entity {} died: {}", (Object)this, (Object)this.getCombatTracker().getDeathMessage().getString());
        }
        this.dead = true;
        this.getCombatTracker().recheckStatus();
        if (this.level instanceof ServerLevel) {
            if ($$1 == null || $$1.wasKilled((ServerLevel)this.level, this)) {
                this.gameEvent(GameEvent.ENTITY_DIE);
                this.dropAllDeathLoot($$0);
                this.createWitherRose($$2);
            }
            this.level.broadcastEntityEvent(this, (byte)3);
        }
        this.setPose(Pose.DYING);
    }

    protected void createWitherRose(@Nullable LivingEntity $$0) {
        if (this.level.isClientSide) {
            return;
        }
        boolean $$1 = false;
        if ($$0 instanceof WitherBoss) {
            if (this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                BlockPos $$2 = this.blockPosition();
                BlockState $$3 = Blocks.WITHER_ROSE.defaultBlockState();
                if (this.level.getBlockState($$2).isAir() && $$3.canSurvive(this.level, $$2)) {
                    this.level.setBlock($$2, $$3, 3);
                    $$1 = true;
                }
            }
            if (!$$1) {
                ItemEntity $$4 = new ItemEntity(this.level, this.getX(), this.getY(), this.getZ(), new ItemStack(Items.WITHER_ROSE));
                this.level.addFreshEntity($$4);
            }
        }
    }

    protected void dropAllDeathLoot(DamageSource $$0) {
        boolean $$4;
        int $$3;
        Entity $$1 = $$0.getEntity();
        if ($$1 instanceof Player) {
            int $$2 = EnchantmentHelper.getMobLooting((LivingEntity)$$1);
        } else {
            $$3 = 0;
        }
        boolean bl = $$4 = this.lastHurtByPlayerTime > 0;
        if (this.shouldDropLoot() && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            this.dropFromLootTable($$0, $$4);
            this.dropCustomDeathLoot($$0, $$3, $$4);
        }
        this.dropEquipment();
        this.dropExperience();
    }

    protected void dropEquipment() {
    }

    protected void dropExperience() {
        if (this.level instanceof ServerLevel && !this.wasExperienceConsumed() && (this.isAlwaysExperienceDropper() || this.lastHurtByPlayerTime > 0 && this.shouldDropExperience() && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT))) {
            ExperienceOrb.award((ServerLevel)this.level, this.position(), this.getExperienceReward());
        }
    }

    protected void dropCustomDeathLoot(DamageSource $$0, int $$1, boolean $$2) {
    }

    public ResourceLocation getLootTable() {
        return this.getType().getDefaultLootTable();
    }

    protected void dropFromLootTable(DamageSource $$0, boolean $$1) {
        ResourceLocation $$2 = this.getLootTable();
        LootTable $$3 = this.level.getServer().getLootTables().get($$2);
        LootContext.Builder $$4 = this.createLootContext($$1, $$0);
        $$3.getRandomItems($$4.create(LootContextParamSets.ENTITY), (Consumer<ItemStack>)((Consumer)this::spawnAtLocation));
    }

    protected LootContext.Builder createLootContext(boolean $$0, DamageSource $$1) {
        LootContext.Builder $$2 = new LootContext.Builder((ServerLevel)this.level).withRandom(this.random).withParameter(LootContextParams.THIS_ENTITY, this).withParameter(LootContextParams.ORIGIN, this.position()).withParameter(LootContextParams.DAMAGE_SOURCE, $$1).withOptionalParameter(LootContextParams.KILLER_ENTITY, $$1.getEntity()).withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, $$1.getDirectEntity());
        if ($$0 && this.lastHurtByPlayer != null) {
            $$2 = $$2.withParameter(LootContextParams.LAST_DAMAGE_PLAYER, this.lastHurtByPlayer).withLuck(this.lastHurtByPlayer.getLuck());
        }
        return $$2;
    }

    public void knockback(double $$0, double $$1, double $$2) {
        if (($$0 *= 1.0 - this.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)) <= 0.0) {
            return;
        }
        this.hasImpulse = true;
        Vec3 $$3 = this.getDeltaMovement();
        Vec3 $$4 = new Vec3($$1, 0.0, $$2).normalize().scale($$0);
        this.setDeltaMovement($$3.x / 2.0 - $$4.x, this.onGround ? Math.min((double)0.4, (double)($$3.y / 2.0 + $$0)) : $$3.y, $$3.z / 2.0 - $$4.z);
    }

    @Nullable
    protected SoundEvent getHurtSound(DamageSource $$0) {
        return SoundEvents.GENERIC_HURT;
    }

    @Nullable
    protected SoundEvent getDeathSound() {
        return SoundEvents.GENERIC_DEATH;
    }

    private SoundEvent getFallDamageSound(int $$0) {
        return $$0 > 4 ? this.getFallSounds().big() : this.getFallSounds().small();
    }

    public void skipDropExperience() {
        this.skipDropExperience = true;
    }

    public boolean wasExperienceConsumed() {
        return this.skipDropExperience;
    }

    protected Vec3 getMeleeAttackReferencePosition() {
        Entity entity = this.getVehicle();
        if (entity instanceof RiderShieldingMount) {
            RiderShieldingMount $$0 = (RiderShieldingMount)((Object)entity);
            return this.position().add(0.0, $$0.getRiderShieldingHeight(), 0.0);
        }
        return this.position();
    }

    public Fallsounds getFallSounds() {
        return new Fallsounds(SoundEvents.GENERIC_SMALL_FALL, SoundEvents.GENERIC_BIG_FALL);
    }

    protected SoundEvent getDrinkingSound(ItemStack $$0) {
        return $$0.getDrinkingSound();
    }

    public SoundEvent getEatingSound(ItemStack $$0) {
        return $$0.getEatingSound();
    }

    @Override
    public void setOnGround(boolean $$0) {
        super.setOnGround($$0);
        if ($$0) {
            this.lastClimbablePos = Optional.empty();
        }
    }

    public Optional<BlockPos> getLastClimbablePos() {
        return this.lastClimbablePos;
    }

    public boolean onClimbable() {
        if (this.isSpectator()) {
            return false;
        }
        BlockPos $$0 = this.blockPosition();
        BlockState $$1 = this.getFeetBlockState();
        if ($$1.is(BlockTags.CLIMBABLE)) {
            this.lastClimbablePos = Optional.of((Object)$$0);
            return true;
        }
        if ($$1.getBlock() instanceof TrapDoorBlock && this.trapdoorUsableAsLadder($$0, $$1)) {
            this.lastClimbablePos = Optional.of((Object)$$0);
            return true;
        }
        return false;
    }

    private boolean trapdoorUsableAsLadder(BlockPos $$0, BlockState $$1) {
        BlockState $$2;
        return $$1.getValue(TrapDoorBlock.OPEN) != false && ($$2 = this.level.getBlockState((BlockPos)$$0.below())).is(Blocks.LADDER) && $$2.getValue(LadderBlock.FACING) == $$1.getValue(TrapDoorBlock.FACING);
    }

    @Override
    public boolean isAlive() {
        return !this.isRemoved() && this.getHealth() > 0.0f;
    }

    @Override
    public boolean causeFallDamage(float $$0, float $$1, DamageSource $$2) {
        boolean $$3 = super.causeFallDamage($$0, $$1, $$2);
        int $$4 = this.calculateFallDamage($$0, $$1);
        if ($$4 > 0) {
            this.playSound(this.getFallDamageSound($$4), 1.0f, 1.0f);
            this.playBlockFallSound();
            this.hurt($$2, $$4);
            return true;
        }
        return $$3;
    }

    protected int calculateFallDamage(float $$0, float $$1) {
        MobEffectInstance $$2 = this.getEffect(MobEffects.JUMP);
        float $$3 = $$2 == null ? 0.0f : (float)($$2.getAmplifier() + 1);
        return Mth.ceil(($$0 - 3.0f - $$3) * $$1);
    }

    protected void playBlockFallSound() {
        int $$2;
        int $$1;
        if (this.isSilent()) {
            return;
        }
        int $$0 = Mth.floor(this.getX());
        BlockState $$3 = this.level.getBlockState(new BlockPos($$0, $$1 = Mth.floor(this.getY() - (double)0.2f), $$2 = Mth.floor(this.getZ())));
        if (!$$3.isAir()) {
            SoundType $$4 = $$3.getSoundType();
            this.playSound($$4.getFallSound(), $$4.getVolume() * 0.5f, $$4.getPitch() * 0.75f);
        }
    }

    @Override
    public void animateHurt() {
        this.hurtTime = this.hurtDuration = 10;
        this.hurtDir = 0.0f;
    }

    public int getArmorValue() {
        return Mth.floor(this.getAttributeValue(Attributes.ARMOR));
    }

    protected void hurtArmor(DamageSource $$0, float $$1) {
    }

    protected void hurtHelmet(DamageSource $$0, float $$1) {
    }

    protected void hurtCurrentlyUsedShield(float $$0) {
    }

    protected float getDamageAfterArmorAbsorb(DamageSource $$0, float $$1) {
        if (!$$0.isBypassArmor()) {
            this.hurtArmor($$0, $$1);
            $$1 = CombatRules.getDamageAfterAbsorb($$1, this.getArmorValue(), (float)this.getAttributeValue(Attributes.ARMOR_TOUGHNESS));
        }
        return $$1;
    }

    protected float getDamageAfterMagicAbsorb(DamageSource $$0, float $$1) {
        int $$2;
        int $$3;
        float $$4;
        float $$5;
        float $$6;
        if ($$0.isBypassMagic()) {
            return $$1;
        }
        if (this.hasEffect(MobEffects.DAMAGE_RESISTANCE) && $$0 != DamageSource.OUT_OF_WORLD && ($$6 = ($$5 = $$1) - ($$1 = Math.max((float)(($$4 = $$1 * (float)($$3 = 25 - ($$2 = (this.getEffect(MobEffects.DAMAGE_RESISTANCE).getAmplifier() + 1) * 5))) / 25.0f), (float)0.0f))) > 0.0f && $$6 < 3.4028235E37f) {
            if (this instanceof ServerPlayer) {
                ((ServerPlayer)this).awardStat(Stats.DAMAGE_RESISTED, Math.round((float)($$6 * 10.0f)));
            } else if ($$0.getEntity() instanceof ServerPlayer) {
                ((ServerPlayer)$$0.getEntity()).awardStat(Stats.DAMAGE_DEALT_RESISTED, Math.round((float)($$6 * 10.0f)));
            }
        }
        if ($$1 <= 0.0f) {
            return 0.0f;
        }
        if ($$0.isBypassEnchantments()) {
            return $$1;
        }
        int $$7 = EnchantmentHelper.getDamageProtection(this.getArmorSlots(), $$0);
        if ($$7 > 0) {
            $$1 = CombatRules.getDamageAfterMagicAbsorb($$1, $$7);
        }
        return $$1;
    }

    protected void actuallyHurt(DamageSource $$0, float $$1) {
        if (this.isInvulnerableTo($$0)) {
            return;
        }
        $$1 = this.getDamageAfterArmorAbsorb($$0, $$1);
        float $$2 = $$1 = this.getDamageAfterMagicAbsorb($$0, $$1);
        $$1 = Math.max((float)($$1 - this.getAbsorptionAmount()), (float)0.0f);
        this.setAbsorptionAmount(this.getAbsorptionAmount() - ($$2 - $$1));
        float $$3 = $$2 - $$1;
        if ($$3 > 0.0f && $$3 < 3.4028235E37f && $$0.getEntity() instanceof ServerPlayer) {
            ((ServerPlayer)$$0.getEntity()).awardStat(Stats.DAMAGE_DEALT_ABSORBED, Math.round((float)($$3 * 10.0f)));
        }
        if ($$1 == 0.0f) {
            return;
        }
        float $$4 = this.getHealth();
        this.setHealth($$4 - $$1);
        this.getCombatTracker().recordDamage($$0, $$4, $$1);
        this.setAbsorptionAmount(this.getAbsorptionAmount() - $$1);
        this.gameEvent(GameEvent.ENTITY_DAMAGE);
    }

    public CombatTracker getCombatTracker() {
        return this.combatTracker;
    }

    @Nullable
    public LivingEntity getKillCredit() {
        if (this.combatTracker.getKiller() != null) {
            return this.combatTracker.getKiller();
        }
        if (this.lastHurtByPlayer != null) {
            return this.lastHurtByPlayer;
        }
        if (this.lastHurtByMob != null) {
            return this.lastHurtByMob;
        }
        return null;
    }

    public final float getMaxHealth() {
        return (float)this.getAttributeValue(Attributes.MAX_HEALTH);
    }

    public final int getArrowCount() {
        return this.entityData.get(DATA_ARROW_COUNT_ID);
    }

    public final void setArrowCount(int $$0) {
        this.entityData.set(DATA_ARROW_COUNT_ID, $$0);
    }

    public final int getStingerCount() {
        return this.entityData.get(DATA_STINGER_COUNT_ID);
    }

    public final void setStingerCount(int $$0) {
        this.entityData.set(DATA_STINGER_COUNT_ID, $$0);
    }

    private int getCurrentSwingDuration() {
        if (MobEffectUtil.hasDigSpeed(this)) {
            return 6 - (1 + MobEffectUtil.getDigSpeedAmplification(this));
        }
        if (this.hasEffect(MobEffects.DIG_SLOWDOWN)) {
            return 6 + (1 + this.getEffect(MobEffects.DIG_SLOWDOWN).getAmplifier()) * 2;
        }
        return 6;
    }

    public void swing(InteractionHand $$0) {
        this.swing($$0, false);
    }

    public void swing(InteractionHand $$0, boolean $$1) {
        if (!this.swinging || this.swingTime >= this.getCurrentSwingDuration() / 2 || this.swingTime < 0) {
            this.swingTime = -1;
            this.swinging = true;
            this.swingingArm = $$0;
            if (this.level instanceof ServerLevel) {
                ClientboundAnimatePacket $$2 = new ClientboundAnimatePacket(this, $$0 == InteractionHand.MAIN_HAND ? 0 : 3);
                ServerChunkCache $$3 = ((ServerLevel)this.level).getChunkSource();
                if ($$1) {
                    $$3.broadcastAndSend(this, $$2);
                } else {
                    $$3.broadcast(this, $$2);
                }
            }
        }
    }

    @Override
    public void handleEntityEvent(byte $$0) {
        switch ($$0) {
            case 2: 
            case 33: 
            case 36: 
            case 37: 
            case 44: 
            case 57: {
                DamageSource $$5;
                this.animationSpeed = 1.5f;
                this.invulnerableTime = 20;
                this.hurtTime = this.hurtDuration = 10;
                this.hurtDir = 0.0f;
                if ($$0 == 33) {
                    this.playSound(SoundEvents.THORNS_HIT, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
                }
                if ($$0 == 37) {
                    DamageSource $$1 = DamageSource.ON_FIRE;
                } else if ($$0 == 36) {
                    DamageSource $$2 = DamageSource.DROWN;
                } else if ($$0 == 44) {
                    DamageSource $$3 = DamageSource.SWEET_BERRY_BUSH;
                } else if ($$0 == 57) {
                    DamageSource $$4 = DamageSource.FREEZE;
                } else {
                    $$5 = DamageSource.GENERIC;
                }
                SoundEvent $$6 = this.getHurtSound($$5);
                if ($$6 != null) {
                    this.playSound($$6, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
                }
                this.hurt(DamageSource.GENERIC, 0.0f);
                this.lastDamageSource = $$5;
                this.lastDamageStamp = this.level.getGameTime();
                break;
            }
            case 3: {
                SoundEvent $$7 = this.getDeathSound();
                if ($$7 != null) {
                    this.playSound($$7, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
                }
                if (this instanceof Player) break;
                this.setHealth(0.0f);
                this.die(DamageSource.GENERIC);
                break;
            }
            case 30: {
                this.playSound(SoundEvents.SHIELD_BREAK, 0.8f, 0.8f + this.level.random.nextFloat() * 0.4f);
                break;
            }
            case 29: {
                this.playSound(SoundEvents.SHIELD_BLOCK, 1.0f, 0.8f + this.level.random.nextFloat() * 0.4f);
                break;
            }
            case 46: {
                int $$8 = 128;
                for (int $$9 = 0; $$9 < 128; ++$$9) {
                    double $$10 = (double)$$9 / 127.0;
                    float $$11 = (this.random.nextFloat() - 0.5f) * 0.2f;
                    float $$12 = (this.random.nextFloat() - 0.5f) * 0.2f;
                    float $$13 = (this.random.nextFloat() - 0.5f) * 0.2f;
                    double $$14 = Mth.lerp($$10, this.xo, this.getX()) + (this.random.nextDouble() - 0.5) * (double)this.getBbWidth() * 2.0;
                    double $$15 = Mth.lerp($$10, this.yo, this.getY()) + this.random.nextDouble() * (double)this.getBbHeight();
                    double $$16 = Mth.lerp($$10, this.zo, this.getZ()) + (this.random.nextDouble() - 0.5) * (double)this.getBbWidth() * 2.0;
                    this.level.addParticle(ParticleTypes.PORTAL, $$14, $$15, $$16, $$11, $$12, $$13);
                }
                break;
            }
            case 47: {
                this.breakItem(this.getItemBySlot(EquipmentSlot.MAINHAND));
                break;
            }
            case 48: {
                this.breakItem(this.getItemBySlot(EquipmentSlot.OFFHAND));
                break;
            }
            case 49: {
                this.breakItem(this.getItemBySlot(EquipmentSlot.HEAD));
                break;
            }
            case 50: {
                this.breakItem(this.getItemBySlot(EquipmentSlot.CHEST));
                break;
            }
            case 51: {
                this.breakItem(this.getItemBySlot(EquipmentSlot.LEGS));
                break;
            }
            case 52: {
                this.breakItem(this.getItemBySlot(EquipmentSlot.FEET));
                break;
            }
            case 54: {
                HoneyBlock.showJumpParticles(this);
                break;
            }
            case 55: {
                this.swapHandItems();
                break;
            }
            case 60: {
                this.makePoofParticles();
                break;
            }
            default: {
                super.handleEntityEvent($$0);
            }
        }
    }

    private void makePoofParticles() {
        for (int $$0 = 0; $$0 < 20; ++$$0) {
            double $$1 = this.random.nextGaussian() * 0.02;
            double $$2 = this.random.nextGaussian() * 0.02;
            double $$3 = this.random.nextGaussian() * 0.02;
            this.level.addParticle(ParticleTypes.POOF, this.getRandomX(1.0), this.getRandomY(), this.getRandomZ(1.0), $$1, $$2, $$3);
        }
    }

    private void swapHandItems() {
        ItemStack $$0 = this.getItemBySlot(EquipmentSlot.OFFHAND);
        this.setItemSlot(EquipmentSlot.OFFHAND, this.getItemBySlot(EquipmentSlot.MAINHAND));
        this.setItemSlot(EquipmentSlot.MAINHAND, $$0);
    }

    @Override
    protected void outOfWorld() {
        this.hurt(DamageSource.OUT_OF_WORLD, 4.0f);
    }

    protected void updateSwingTime() {
        int $$0 = this.getCurrentSwingDuration();
        if (this.swinging) {
            ++this.swingTime;
            if (this.swingTime >= $$0) {
                this.swingTime = 0;
                this.swinging = false;
            }
        } else {
            this.swingTime = 0;
        }
        this.attackAnim = (float)this.swingTime / (float)$$0;
    }

    @Nullable
    public AttributeInstance getAttribute(Attribute $$0) {
        return this.getAttributes().getInstance($$0);
    }

    public double getAttributeValue(Holder<Attribute> $$0) {
        return this.getAttributeValue($$0.value());
    }

    public double getAttributeValue(Attribute $$0) {
        return this.getAttributes().getValue($$0);
    }

    public double getAttributeBaseValue(Holder<Attribute> $$0) {
        return this.getAttributeBaseValue($$0.value());
    }

    public double getAttributeBaseValue(Attribute $$0) {
        return this.getAttributes().getBaseValue($$0);
    }

    public AttributeMap getAttributes() {
        return this.attributes;
    }

    public MobType getMobType() {
        return MobType.UNDEFINED;
    }

    public ItemStack getMainHandItem() {
        return this.getItemBySlot(EquipmentSlot.MAINHAND);
    }

    public ItemStack getOffhandItem() {
        return this.getItemBySlot(EquipmentSlot.OFFHAND);
    }

    public boolean isHolding(Item $$0) {
        return this.isHolding((Predicate<ItemStack>)((Predicate)$$1 -> $$1.is($$0)));
    }

    public boolean isHolding(Predicate<ItemStack> $$0) {
        return $$0.test((Object)this.getMainHandItem()) || $$0.test((Object)this.getOffhandItem());
    }

    public ItemStack getItemInHand(InteractionHand $$0) {
        if ($$0 == InteractionHand.MAIN_HAND) {
            return this.getItemBySlot(EquipmentSlot.MAINHAND);
        }
        if ($$0 == InteractionHand.OFF_HAND) {
            return this.getItemBySlot(EquipmentSlot.OFFHAND);
        }
        throw new IllegalArgumentException("Invalid hand " + $$0);
    }

    public void setItemInHand(InteractionHand $$0, ItemStack $$1) {
        if ($$0 == InteractionHand.MAIN_HAND) {
            this.setItemSlot(EquipmentSlot.MAINHAND, $$1);
        } else if ($$0 == InteractionHand.OFF_HAND) {
            this.setItemSlot(EquipmentSlot.OFFHAND, $$1);
        } else {
            throw new IllegalArgumentException("Invalid hand " + $$0);
        }
    }

    public boolean hasItemInSlot(EquipmentSlot $$0) {
        return !this.getItemBySlot($$0).isEmpty();
    }

    @Override
    public abstract Iterable<ItemStack> getArmorSlots();

    public abstract ItemStack getItemBySlot(EquipmentSlot var1);

    @Override
    public abstract void setItemSlot(EquipmentSlot var1, ItemStack var2);

    protected void verifyEquippedItem(ItemStack $$0) {
        CompoundTag $$1 = $$0.getTag();
        if ($$1 != null) {
            $$0.getItem().verifyTagAfterLoad($$1);
        }
    }

    public float getArmorCoverPercentage() {
        Iterable<ItemStack> $$0 = this.getArmorSlots();
        int $$1 = 0;
        int $$2 = 0;
        for (ItemStack $$3 : $$0) {
            if (!$$3.isEmpty()) {
                ++$$2;
            }
            ++$$1;
        }
        return $$1 > 0 ? (float)$$2 / (float)$$1 : 0.0f;
    }

    @Override
    public void setSprinting(boolean $$0) {
        super.setSprinting($$0);
        AttributeInstance $$1 = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if ($$1.getModifier(SPEED_MODIFIER_SPRINTING_UUID) != null) {
            $$1.removeModifier(SPEED_MODIFIER_SPRINTING);
        }
        if ($$0) {
            $$1.addTransientModifier(SPEED_MODIFIER_SPRINTING);
        }
    }

    protected float getSoundVolume() {
        return 1.0f;
    }

    public float getVoicePitch() {
        if (this.isBaby()) {
            return (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.5f;
        }
        return (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f;
    }

    protected boolean isImmobile() {
        return this.isDeadOrDying();
    }

    @Override
    public void push(Entity $$0) {
        if (!this.isSleeping()) {
            super.push($$0);
        }
    }

    private void dismountVehicle(Entity $$0) {
        Vec3 $$4;
        if (this.isRemoved()) {
            Vec3 $$1 = this.position();
        } else if ($$0.isRemoved() || this.level.getBlockState($$0.blockPosition()).is(BlockTags.PORTALS)) {
            double $$2 = Math.max((double)this.getY(), (double)$$0.getY());
            Vec3 $$3 = new Vec3(this.getX(), $$2, this.getZ());
        } else {
            $$4 = $$0.getDismountLocationForPassenger(this);
        }
        this.dismountTo($$4.x, $$4.y, $$4.z);
    }

    @Override
    public boolean shouldShowName() {
        return this.isCustomNameVisible();
    }

    protected float getJumpPower() {
        return 0.42f * this.getBlockJumpFactor();
    }

    public double getJumpBoostPower() {
        return this.hasEffect(MobEffects.JUMP) ? (double)(0.1f * (float)(this.getEffect(MobEffects.JUMP).getAmplifier() + 1)) : 0.0;
    }

    protected void jumpFromGround() {
        double $$0 = (double)this.getJumpPower() + this.getJumpBoostPower();
        Vec3 $$1 = this.getDeltaMovement();
        this.setDeltaMovement($$1.x, $$0, $$1.z);
        if (this.isSprinting()) {
            float $$2 = this.getYRot() * ((float)Math.PI / 180);
            this.setDeltaMovement(this.getDeltaMovement().add(-Mth.sin($$2) * 0.2f, 0.0, Mth.cos($$2) * 0.2f));
        }
        this.hasImpulse = true;
    }

    protected void goDownInWater() {
        this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.04f, 0.0));
    }

    protected void jumpInLiquid(TagKey<Fluid> $$0) {
        this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.04f, 0.0));
    }

    protected float getWaterSlowDown() {
        return 0.8f;
    }

    public boolean canStandOnFluid(FluidState $$0) {
        return false;
    }

    public void travel(Vec3 $$0) {
        if (this.isEffectiveAi() || this.isControlledByLocalInstance()) {
            boolean $$2;
            double $$1 = 0.08;
            boolean bl = $$2 = this.getDeltaMovement().y <= 0.0;
            if ($$2 && this.hasEffect(MobEffects.SLOW_FALLING)) {
                $$1 = 0.01;
                this.resetFallDistance();
            }
            FluidState $$3 = this.level.getFluidState(this.blockPosition());
            if (this.isInWater() && this.isAffectedByFluids() && !this.canStandOnFluid($$3)) {
                double $$4 = this.getY();
                float $$5 = this.isSprinting() ? 0.9f : this.getWaterSlowDown();
                float $$6 = 0.02f;
                float $$7 = EnchantmentHelper.getDepthStrider(this);
                if ($$7 > 3.0f) {
                    $$7 = 3.0f;
                }
                if (!this.onGround) {
                    $$7 *= 0.5f;
                }
                if ($$7 > 0.0f) {
                    $$5 += (0.54600006f - $$5) * $$7 / 3.0f;
                    $$6 += (this.getSpeed() - $$6) * $$7 / 3.0f;
                }
                if (this.hasEffect(MobEffects.DOLPHINS_GRACE)) {
                    $$5 = 0.96f;
                }
                this.moveRelative($$6, $$0);
                this.move(MoverType.SELF, this.getDeltaMovement());
                Vec3 $$8 = this.getDeltaMovement();
                if (this.horizontalCollision && this.onClimbable()) {
                    $$8 = new Vec3($$8.x, 0.2, $$8.z);
                }
                this.setDeltaMovement($$8.multiply($$5, 0.8f, $$5));
                Vec3 $$9 = this.getFluidFallingAdjustedMovement($$1, $$2, this.getDeltaMovement());
                this.setDeltaMovement($$9);
                if (this.horizontalCollision && this.isFree($$9.x, $$9.y + (double)0.6f - this.getY() + $$4, $$9.z)) {
                    this.setDeltaMovement($$9.x, 0.3f, $$9.z);
                }
            } else if (this.isInLava() && this.isAffectedByFluids() && !this.canStandOnFluid($$3)) {
                double $$10 = this.getY();
                this.moveRelative(0.02f, $$0);
                this.move(MoverType.SELF, this.getDeltaMovement());
                if (this.getFluidHeight(FluidTags.LAVA) <= this.getFluidJumpThreshold()) {
                    this.setDeltaMovement(this.getDeltaMovement().multiply(0.5, 0.8f, 0.5));
                    Vec3 $$11 = this.getFluidFallingAdjustedMovement($$1, $$2, this.getDeltaMovement());
                    this.setDeltaMovement($$11);
                } else {
                    this.setDeltaMovement(this.getDeltaMovement().scale(0.5));
                }
                if (!this.isNoGravity()) {
                    this.setDeltaMovement(this.getDeltaMovement().add(0.0, -$$1 / 4.0, 0.0));
                }
                Vec3 $$12 = this.getDeltaMovement();
                if (this.horizontalCollision && this.isFree($$12.x, $$12.y + (double)0.6f - this.getY() + $$10, $$12.z)) {
                    this.setDeltaMovement($$12.x, 0.3f, $$12.z);
                }
            } else if (this.isFallFlying()) {
                double $$22;
                double $$23;
                float $$24;
                this.checkSlowFallDistance();
                Vec3 $$13 = this.getDeltaMovement();
                Vec3 $$14 = this.getLookAngle();
                float $$15 = this.getXRot() * ((float)Math.PI / 180);
                double $$16 = Math.sqrt((double)($$14.x * $$14.x + $$14.z * $$14.z));
                double $$17 = $$13.horizontalDistance();
                double $$18 = $$14.length();
                double $$19 = Math.cos((double)$$15);
                $$19 = $$19 * $$19 * Math.min((double)1.0, (double)($$18 / 0.4));
                $$13 = this.getDeltaMovement().add(0.0, $$1 * (-1.0 + $$19 * 0.75), 0.0);
                if ($$13.y < 0.0 && $$16 > 0.0) {
                    double $$20 = $$13.y * -0.1 * $$19;
                    $$13 = $$13.add($$14.x * $$20 / $$16, $$20, $$14.z * $$20 / $$16);
                }
                if ($$15 < 0.0f && $$16 > 0.0) {
                    double $$21 = $$17 * (double)(-Mth.sin($$15)) * 0.04;
                    $$13 = $$13.add(-$$14.x * $$21 / $$16, $$21 * 3.2, -$$14.z * $$21 / $$16);
                }
                if ($$16 > 0.0) {
                    $$13 = $$13.add(($$14.x / $$16 * $$17 - $$13.x) * 0.1, 0.0, ($$14.z / $$16 * $$17 - $$13.z) * 0.1);
                }
                this.setDeltaMovement($$13.multiply(0.99f, 0.98f, 0.99f));
                this.move(MoverType.SELF, this.getDeltaMovement());
                if (this.horizontalCollision && !this.level.isClientSide && ($$24 = (float)(($$23 = $$17 - ($$22 = this.getDeltaMovement().horizontalDistance())) * 10.0 - 3.0)) > 0.0f) {
                    this.playSound(this.getFallDamageSound((int)$$24), 1.0f, 1.0f);
                    this.hurt(DamageSource.FLY_INTO_WALL, $$24);
                }
                if (this.onGround && !this.level.isClientSide) {
                    this.setSharedFlag(7, false);
                }
            } else {
                BlockPos $$25 = this.getBlockPosBelowThatAffectsMyMovement();
                float $$26 = this.level.getBlockState($$25).getBlock().getFriction();
                float $$27 = this.onGround ? $$26 * 0.91f : 0.91f;
                Vec3 $$28 = this.handleRelativeFrictionAndCalculateMovement($$0, $$26);
                double $$29 = $$28.y;
                if (this.hasEffect(MobEffects.LEVITATION)) {
                    $$29 += (0.05 * (double)(this.getEffect(MobEffects.LEVITATION).getAmplifier() + 1) - $$28.y) * 0.2;
                    this.resetFallDistance();
                } else if (!this.level.isClientSide || this.level.hasChunkAt($$25)) {
                    if (!this.isNoGravity()) {
                        $$29 -= $$1;
                    }
                } else {
                    $$29 = this.getY() > (double)this.level.getMinBuildHeight() ? -0.1 : 0.0;
                }
                if (this.shouldDiscardFriction()) {
                    this.setDeltaMovement($$28.x, $$29, $$28.z);
                } else {
                    this.setDeltaMovement($$28.x * (double)$$27, $$29 * (double)0.98f, $$28.z * (double)$$27);
                }
            }
        }
        this.calculateEntityAnimation(this, this instanceof FlyingAnimal);
    }

    public void calculateEntityAnimation(LivingEntity $$0, boolean $$1) {
        double $$4;
        double $$3;
        $$0.animationSpeedOld = $$0.animationSpeed;
        double $$2 = $$0.getX() - $$0.xo;
        float $$5 = (float)Math.sqrt((double)($$2 * $$2 + ($$3 = $$1 ? $$0.getY() - $$0.yo : 0.0) * $$3 + ($$4 = $$0.getZ() - $$0.zo) * $$4)) * 4.0f;
        if ($$5 > 1.0f) {
            $$5 = 1.0f;
        }
        $$0.animationSpeed += ($$5 - $$0.animationSpeed) * 0.4f;
        $$0.animationPosition += $$0.animationSpeed;
    }

    public Vec3 handleRelativeFrictionAndCalculateMovement(Vec3 $$0, float $$1) {
        this.moveRelative(this.getFrictionInfluencedSpeed($$1), $$0);
        this.setDeltaMovement(this.handleOnClimbable(this.getDeltaMovement()));
        this.move(MoverType.SELF, this.getDeltaMovement());
        Vec3 $$2 = this.getDeltaMovement();
        if ((this.horizontalCollision || this.jumping) && (this.onClimbable() || this.getFeetBlockState().is(Blocks.POWDER_SNOW) && PowderSnowBlock.canEntityWalkOnPowderSnow(this))) {
            $$2 = new Vec3($$2.x, 0.2, $$2.z);
        }
        return $$2;
    }

    public Vec3 getFluidFallingAdjustedMovement(double $$0, boolean $$1, Vec3 $$2) {
        if (!this.isNoGravity() && !this.isSprinting()) {
            double $$4;
            if ($$1 && Math.abs((double)($$2.y - 0.005)) >= 0.003 && Math.abs((double)($$2.y - $$0 / 16.0)) < 0.003) {
                double $$3 = -0.003;
            } else {
                $$4 = $$2.y - $$0 / 16.0;
            }
            return new Vec3($$2.x, $$4, $$2.z);
        }
        return $$2;
    }

    private Vec3 handleOnClimbable(Vec3 $$0) {
        if (this.onClimbable()) {
            this.resetFallDistance();
            float $$1 = 0.15f;
            double $$2 = Mth.clamp($$0.x, (double)-0.15f, (double)0.15f);
            double $$3 = Mth.clamp($$0.z, (double)-0.15f, (double)0.15f);
            double $$4 = Math.max((double)$$0.y, (double)-0.15f);
            if ($$4 < 0.0 && !this.getFeetBlockState().is(Blocks.SCAFFOLDING) && this.isSuppressingSlidingDownLadder() && this instanceof Player) {
                $$4 = 0.0;
            }
            $$0 = new Vec3($$2, $$4, $$3);
        }
        return $$0;
    }

    private float getFrictionInfluencedSpeed(float $$0) {
        if (this.onGround) {
            return this.getSpeed() * (0.21600002f / ($$0 * $$0 * $$0));
        }
        return this.flyingSpeed;
    }

    public float getSpeed() {
        return this.speed;
    }

    public void setSpeed(float $$0) {
        this.speed = $$0;
    }

    public boolean doHurtTarget(Entity $$0) {
        this.setLastHurtMob($$0);
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        this.updatingUsingItem();
        this.updateSwimAmount();
        if (!this.level.isClientSide) {
            int $$1;
            int $$0 = this.getArrowCount();
            if ($$0 > 0) {
                if (this.removeArrowTime <= 0) {
                    this.removeArrowTime = 20 * (30 - $$0);
                }
                --this.removeArrowTime;
                if (this.removeArrowTime <= 0) {
                    this.setArrowCount($$0 - 1);
                }
            }
            if (($$1 = this.getStingerCount()) > 0) {
                if (this.removeStingerTime <= 0) {
                    this.removeStingerTime = 20 * (30 - $$1);
                }
                --this.removeStingerTime;
                if (this.removeStingerTime <= 0) {
                    this.setStingerCount($$1 - 1);
                }
            }
            this.detectEquipmentUpdates();
            if (this.tickCount % 20 == 0) {
                this.getCombatTracker().recheckStatus();
            }
            if (this.isSleeping() && !this.checkBedExists()) {
                this.stopSleeping();
            }
        }
        if (!this.isRemoved()) {
            this.aiStep();
        }
        double $$2 = this.getX() - this.xo;
        double $$3 = this.getZ() - this.zo;
        float $$4 = (float)($$2 * $$2 + $$3 * $$3);
        float $$5 = this.yBodyRot;
        float $$6 = 0.0f;
        this.oRun = this.run;
        float $$7 = 0.0f;
        if ($$4 > 0.0025000002f) {
            $$7 = 1.0f;
            $$6 = (float)Math.sqrt((double)$$4) * 3.0f;
            float $$8 = (float)Mth.atan2($$3, $$2) * 57.295776f - 90.0f;
            float $$9 = Mth.abs(Mth.wrapDegrees(this.getYRot()) - $$8);
            $$5 = 95.0f < $$9 && $$9 < 265.0f ? $$8 - 180.0f : $$8;
        }
        if (this.attackAnim > 0.0f) {
            $$5 = this.getYRot();
        }
        if (!this.onGround) {
            $$7 = 0.0f;
        }
        this.run += ($$7 - this.run) * 0.3f;
        this.level.getProfiler().push("headTurn");
        $$6 = this.tickHeadTurn($$5, $$6);
        this.level.getProfiler().pop();
        this.level.getProfiler().push("rangeChecks");
        while (this.getYRot() - this.yRotO < -180.0f) {
            this.yRotO -= 360.0f;
        }
        while (this.getYRot() - this.yRotO >= 180.0f) {
            this.yRotO += 360.0f;
        }
        while (this.yBodyRot - this.yBodyRotO < -180.0f) {
            this.yBodyRotO -= 360.0f;
        }
        while (this.yBodyRot - this.yBodyRotO >= 180.0f) {
            this.yBodyRotO += 360.0f;
        }
        while (this.getXRot() - this.xRotO < -180.0f) {
            this.xRotO -= 360.0f;
        }
        while (this.getXRot() - this.xRotO >= 180.0f) {
            this.xRotO += 360.0f;
        }
        while (this.yHeadRot - this.yHeadRotO < -180.0f) {
            this.yHeadRotO -= 360.0f;
        }
        while (this.yHeadRot - this.yHeadRotO >= 180.0f) {
            this.yHeadRotO += 360.0f;
        }
        this.level.getProfiler().pop();
        this.animStep += $$6;
        this.fallFlyTicks = this.isFallFlying() ? ++this.fallFlyTicks : 0;
        if (this.isSleeping()) {
            this.setXRot(0.0f);
        }
    }

    private void detectEquipmentUpdates() {
        Map<EquipmentSlot, ItemStack> $$0 = this.collectEquipmentChanges();
        if ($$0 != null) {
            this.handleHandSwap($$0);
            if (!$$0.isEmpty()) {
                this.handleEquipmentChanges($$0);
            }
        }
    }

    /*
     * WARNING - void declaration
     */
    @Nullable
    private Map<EquipmentSlot, ItemStack> collectEquipmentChanges() {
        EnumMap $$0 = null;
        block4: for (EquipmentSlot $$1 : EquipmentSlot.values()) {
            void $$4;
            switch ($$1.getType()) {
                case HAND: {
                    ItemStack $$2 = this.getLastHandItem($$1);
                    break;
                }
                case ARMOR: {
                    ItemStack $$3 = this.getLastArmorItem($$1);
                    break;
                }
                default: {
                    continue block4;
                }
            }
            ItemStack $$5 = this.getItemBySlot($$1);
            if (!this.equipmentHasChanged((ItemStack)$$4, $$5)) continue;
            if ($$0 == null) {
                $$0 = Maps.newEnumMap(EquipmentSlot.class);
            }
            $$0.put((Object)$$1, (Object)$$5);
            if (!$$4.isEmpty()) {
                this.getAttributes().removeAttributeModifiers($$4.getAttributeModifiers($$1));
            }
            if ($$5.isEmpty()) continue;
            this.getAttributes().addTransientAttributeModifiers($$5.getAttributeModifiers($$1));
        }
        return $$0;
    }

    public boolean equipmentHasChanged(ItemStack $$0, ItemStack $$1) {
        return !ItemStack.matches($$1, $$0);
    }

    private void handleHandSwap(Map<EquipmentSlot, ItemStack> $$0) {
        ItemStack $$1 = (ItemStack)$$0.get((Object)EquipmentSlot.MAINHAND);
        ItemStack $$2 = (ItemStack)$$0.get((Object)EquipmentSlot.OFFHAND);
        if ($$1 != null && $$2 != null && ItemStack.matches($$1, this.getLastHandItem(EquipmentSlot.OFFHAND)) && ItemStack.matches($$2, this.getLastHandItem(EquipmentSlot.MAINHAND))) {
            ((ServerLevel)this.level).getChunkSource().broadcast(this, new ClientboundEntityEventPacket(this, 55));
            $$0.remove((Object)EquipmentSlot.MAINHAND);
            $$0.remove((Object)EquipmentSlot.OFFHAND);
            this.setLastHandItem(EquipmentSlot.MAINHAND, $$1.copy());
            this.setLastHandItem(EquipmentSlot.OFFHAND, $$2.copy());
        }
    }

    private void handleEquipmentChanges(Map<EquipmentSlot, ItemStack> $$0) {
        ArrayList $$1 = Lists.newArrayListWithCapacity((int)$$0.size());
        $$0.forEach((arg_0, arg_1) -> this.lambda$handleEquipmentChanges$5((List)$$1, arg_0, arg_1));
        ((ServerLevel)this.level).getChunkSource().broadcast(this, new ClientboundSetEquipmentPacket(this.getId(), (List<Pair<EquipmentSlot, ItemStack>>)$$1));
    }

    private ItemStack getLastArmorItem(EquipmentSlot $$0) {
        return this.lastArmorItemStacks.get($$0.getIndex());
    }

    private void setLastArmorItem(EquipmentSlot $$0, ItemStack $$1) {
        this.lastArmorItemStacks.set($$0.getIndex(), $$1);
    }

    private ItemStack getLastHandItem(EquipmentSlot $$0) {
        return this.lastHandItemStacks.get($$0.getIndex());
    }

    private void setLastHandItem(EquipmentSlot $$0, ItemStack $$1) {
        this.lastHandItemStacks.set($$0.getIndex(), $$1);
    }

    protected float tickHeadTurn(float $$0, float $$1) {
        boolean $$4;
        float $$2 = Mth.wrapDegrees($$0 - this.yBodyRot);
        this.yBodyRot += $$2 * 0.3f;
        float $$3 = Mth.wrapDegrees(this.getYRot() - this.yBodyRot);
        boolean bl = $$4 = $$3 < -90.0f || $$3 >= 90.0f;
        if ($$3 < -75.0f) {
            $$3 = -75.0f;
        }
        if ($$3 >= 75.0f) {
            $$3 = 75.0f;
        }
        this.yBodyRot = this.getYRot() - $$3;
        if ($$3 * $$3 > 2500.0f) {
            this.yBodyRot += $$3 * 0.2f;
        }
        if ($$4) {
            $$1 *= -1.0f;
        }
        return $$1;
    }

    public void aiStep() {
        if (this.noJumpDelay > 0) {
            --this.noJumpDelay;
        }
        if (this.isControlledByLocalInstance()) {
            this.lerpSteps = 0;
            this.syncPacketPositionCodec(this.getX(), this.getY(), this.getZ());
        }
        if (this.lerpSteps > 0) {
            double $$0 = this.getX() + (this.lerpX - this.getX()) / (double)this.lerpSteps;
            double $$1 = this.getY() + (this.lerpY - this.getY()) / (double)this.lerpSteps;
            double $$2 = this.getZ() + (this.lerpZ - this.getZ()) / (double)this.lerpSteps;
            double $$3 = Mth.wrapDegrees(this.lerpYRot - (double)this.getYRot());
            this.setYRot(this.getYRot() + (float)$$3 / (float)this.lerpSteps);
            this.setXRot(this.getXRot() + (float)(this.lerpXRot - (double)this.getXRot()) / (float)this.lerpSteps);
            --this.lerpSteps;
            this.setPos($$0, $$1, $$2);
            this.setRot(this.getYRot(), this.getXRot());
        } else if (!this.isEffectiveAi()) {
            this.setDeltaMovement(this.getDeltaMovement().scale(0.98));
        }
        if (this.lerpHeadSteps > 0) {
            this.yHeadRot += (float)Mth.wrapDegrees(this.lyHeadRot - (double)this.yHeadRot) / (float)this.lerpHeadSteps;
            --this.lerpHeadSteps;
        }
        Vec3 $$4 = this.getDeltaMovement();
        double $$5 = $$4.x;
        double $$6 = $$4.y;
        double $$7 = $$4.z;
        if (Math.abs((double)$$4.x) < 0.003) {
            $$5 = 0.0;
        }
        if (Math.abs((double)$$4.y) < 0.003) {
            $$6 = 0.0;
        }
        if (Math.abs((double)$$4.z) < 0.003) {
            $$7 = 0.0;
        }
        this.setDeltaMovement($$5, $$6, $$7);
        this.level.getProfiler().push("ai");
        if (this.isImmobile()) {
            this.jumping = false;
            this.xxa = 0.0f;
            this.zza = 0.0f;
        } else if (this.isEffectiveAi()) {
            this.level.getProfiler().push("newAi");
            this.serverAiStep();
            this.level.getProfiler().pop();
        }
        this.level.getProfiler().pop();
        this.level.getProfiler().push("jump");
        if (this.jumping && this.isAffectedByFluids()) {
            double $$9;
            if (this.isInLava()) {
                double $$8 = this.getFluidHeight(FluidTags.LAVA);
            } else {
                $$9 = this.getFluidHeight(FluidTags.WATER);
            }
            boolean $$10 = this.isInWater() && $$9 > 0.0;
            double $$11 = this.getFluidJumpThreshold();
            if ($$10 && (!this.onGround || $$9 > $$11)) {
                this.jumpInLiquid(FluidTags.WATER);
            } else if (this.isInLava() && (!this.onGround || $$9 > $$11)) {
                this.jumpInLiquid(FluidTags.LAVA);
            } else if ((this.onGround || $$10 && $$9 <= $$11) && this.noJumpDelay == 0) {
                this.jumpFromGround();
                this.noJumpDelay = 10;
            }
        } else {
            this.noJumpDelay = 0;
        }
        this.level.getProfiler().pop();
        this.level.getProfiler().push("travel");
        this.xxa *= 0.98f;
        this.zza *= 0.98f;
        this.updateFallFlying();
        AABB $$12 = this.getBoundingBox();
        this.travel(new Vec3(this.xxa, this.yya, this.zza));
        this.level.getProfiler().pop();
        this.level.getProfiler().push("freezing");
        boolean $$13 = this.getType().is(EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES);
        if (!this.level.isClientSide && !this.isDeadOrDying()) {
            int $$14 = this.getTicksFrozen();
            if (this.isInPowderSnow && this.canFreeze()) {
                this.setTicksFrozen(Math.min((int)this.getTicksRequiredToFreeze(), (int)($$14 + 1)));
            } else {
                this.setTicksFrozen(Math.max((int)0, (int)($$14 - 2)));
            }
        }
        this.removeFrost();
        this.tryAddFrost();
        if (!this.level.isClientSide && this.tickCount % 40 == 0 && this.isFullyFrozen() && this.canFreeze()) {
            int $$15 = $$13 ? 5 : 1;
            this.hurt(DamageSource.FREEZE, $$15);
        }
        this.level.getProfiler().pop();
        this.level.getProfiler().push("push");
        if (this.autoSpinAttackTicks > 0) {
            --this.autoSpinAttackTicks;
            this.checkAutoSpinAttack($$12, this.getBoundingBox());
        }
        this.pushEntities();
        this.level.getProfiler().pop();
        if (!this.level.isClientSide && this.isSensitiveToWater() && this.isInWaterRainOrBubble()) {
            this.hurt(DamageSource.DROWN, 1.0f);
        }
    }

    public boolean isSensitiveToWater() {
        return false;
    }

    private void updateFallFlying() {
        boolean $$02 = this.getSharedFlag(7);
        if ($$02 && !this.onGround && !this.isPassenger() && !this.hasEffect(MobEffects.LEVITATION)) {
            ItemStack $$1 = this.getItemBySlot(EquipmentSlot.CHEST);
            if ($$1.is(Items.ELYTRA) && ElytraItem.isFlyEnabled($$1)) {
                $$02 = true;
                int $$2 = this.fallFlyTicks + 1;
                if (!this.level.isClientSide && $$2 % 10 == 0) {
                    int $$3 = $$2 / 10;
                    if ($$3 % 2 == 0) {
                        $$1.hurtAndBreak(1, this, $$0 -> $$0.broadcastBreakEvent(EquipmentSlot.CHEST));
                    }
                    this.gameEvent(GameEvent.ELYTRA_GLIDE);
                }
            } else {
                $$02 = false;
            }
        } else {
            $$02 = false;
        }
        if (!this.level.isClientSide) {
            this.setSharedFlag(7, $$02);
        }
    }

    protected void serverAiStep() {
    }

    protected void pushEntities() {
        if (this.level.isClientSide()) {
            this.level.getEntities(EntityTypeTest.forClass(Player.class), this.getBoundingBox(), EntitySelector.pushableBy(this)).forEach(this::doPush);
            return;
        }
        List<Entity> $$0 = this.level.getEntities(this, this.getBoundingBox(), EntitySelector.pushableBy(this));
        if (!$$0.isEmpty()) {
            int $$1 = this.level.getGameRules().getInt(GameRules.RULE_MAX_ENTITY_CRAMMING);
            if ($$1 > 0 && $$0.size() > $$1 - 1 && this.random.nextInt(4) == 0) {
                int $$2 = 0;
                for (int $$3 = 0; $$3 < $$0.size(); ++$$3) {
                    if (((Entity)$$0.get($$3)).isPassenger()) continue;
                    ++$$2;
                }
                if ($$2 > $$1 - 1) {
                    this.hurt(DamageSource.CRAMMING, 6.0f);
                }
            }
            for (int $$4 = 0; $$4 < $$0.size(); ++$$4) {
                Entity $$5 = (Entity)$$0.get($$4);
                this.doPush($$5);
            }
        }
    }

    protected void checkAutoSpinAttack(AABB $$0, AABB $$1) {
        AABB $$2 = $$0.minmax($$1);
        List $$3 = this.level.getEntities(this, $$2);
        if (!$$3.isEmpty()) {
            for (int $$4 = 0; $$4 < $$3.size(); ++$$4) {
                Entity $$5 = (Entity)$$3.get($$4);
                if (!($$5 instanceof LivingEntity)) continue;
                this.doAutoAttackOnTouch((LivingEntity)$$5);
                this.autoSpinAttackTicks = 0;
                this.setDeltaMovement(this.getDeltaMovement().scale(-0.2));
                break;
            }
        } else if (this.horizontalCollision) {
            this.autoSpinAttackTicks = 0;
        }
        if (!this.level.isClientSide && this.autoSpinAttackTicks <= 0) {
            this.setLivingEntityFlag(4, false);
        }
    }

    protected void doPush(Entity $$0) {
        $$0.push(this);
    }

    protected void doAutoAttackOnTouch(LivingEntity $$0) {
    }

    public boolean isAutoSpinAttack() {
        return (this.entityData.get(DATA_LIVING_ENTITY_FLAGS) & 4) != 0;
    }

    @Override
    public void stopRiding() {
        Entity $$0 = this.getVehicle();
        super.stopRiding();
        if ($$0 != null && $$0 != this.getVehicle() && !this.level.isClientSide) {
            this.dismountVehicle($$0);
        }
    }

    @Override
    public void rideTick() {
        super.rideTick();
        this.oRun = this.run;
        this.run = 0.0f;
        this.resetFallDistance();
    }

    @Override
    public void lerpTo(double $$0, double $$1, double $$2, float $$3, float $$4, int $$5, boolean $$6) {
        this.lerpX = $$0;
        this.lerpY = $$1;
        this.lerpZ = $$2;
        this.lerpYRot = $$3;
        this.lerpXRot = $$4;
        this.lerpSteps = $$5;
    }

    @Override
    public void lerpHeadTo(float $$0, int $$1) {
        this.lyHeadRot = $$0;
        this.lerpHeadSteps = $$1;
    }

    public void setJumping(boolean $$0) {
        this.jumping = $$0;
    }

    public void onItemPickup(ItemEntity $$0) {
        Player $$1;
        Player player = $$1 = $$0.getThrower() != null ? this.level.getPlayerByUUID($$0.getThrower()) : null;
        if ($$1 instanceof ServerPlayer) {
            CriteriaTriggers.THROWN_ITEM_PICKED_UP_BY_ENTITY.trigger((ServerPlayer)$$1, $$0.getItem(), this);
        }
    }

    public void take(Entity $$0, int $$1) {
        if (!$$0.isRemoved() && !this.level.isClientSide && ($$0 instanceof ItemEntity || $$0 instanceof AbstractArrow || $$0 instanceof ExperienceOrb)) {
            ((ServerLevel)this.level).getChunkSource().broadcast($$0, new ClientboundTakeItemEntityPacket($$0.getId(), this.getId(), $$1));
        }
    }

    public boolean hasLineOfSight(Entity $$0) {
        if ($$0.level != this.level) {
            return false;
        }
        Vec3 $$1 = new Vec3(this.getX(), this.getEyeY(), this.getZ());
        Vec3 $$2 = new Vec3($$0.getX(), $$0.getEyeY(), $$0.getZ());
        if ($$2.distanceTo($$1) > 128.0) {
            return false;
        }
        return this.level.clip(new ClipContext($$1, $$2, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() == HitResult.Type.MISS;
    }

    @Override
    public float getViewYRot(float $$0) {
        if ($$0 == 1.0f) {
            return this.yHeadRot;
        }
        return Mth.lerp($$0, this.yHeadRotO, this.yHeadRot);
    }

    public float getAttackAnim(float $$0) {
        float $$1 = this.attackAnim - this.oAttackAnim;
        if ($$1 < 0.0f) {
            $$1 += 1.0f;
        }
        return this.oAttackAnim + $$1 * $$0;
    }

    public boolean isEffectiveAi() {
        return !this.level.isClientSide;
    }

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    @Override
    public boolean isPushable() {
        return this.isAlive() && !this.isSpectator() && !this.onClimbable();
    }

    @Override
    public float getYHeadRot() {
        return this.yHeadRot;
    }

    @Override
    public void setYHeadRot(float $$0) {
        this.yHeadRot = $$0;
    }

    @Override
    public void setYBodyRot(float $$0) {
        this.yBodyRot = $$0;
    }

    @Override
    protected Vec3 getRelativePortalPosition(Direction.Axis $$0, BlockUtil.FoundRectangle $$1) {
        return LivingEntity.resetForwardDirectionOfRelativePortalPosition(super.getRelativePortalPosition($$0, $$1));
    }

    public static Vec3 resetForwardDirectionOfRelativePortalPosition(Vec3 $$0) {
        return new Vec3($$0.x, $$0.y, 0.0);
    }

    public float getAbsorptionAmount() {
        return this.absorptionAmount;
    }

    public void setAbsorptionAmount(float $$0) {
        if ($$0 < 0.0f) {
            $$0 = 0.0f;
        }
        this.absorptionAmount = $$0;
    }

    public void onEnterCombat() {
    }

    public void onLeaveCombat() {
    }

    protected void updateEffectVisibility() {
        this.effectsDirty = true;
    }

    public abstract HumanoidArm getMainArm();

    public boolean isUsingItem() {
        return (this.entityData.get(DATA_LIVING_ENTITY_FLAGS) & 1) > 0;
    }

    public InteractionHand getUsedItemHand() {
        return (this.entityData.get(DATA_LIVING_ENTITY_FLAGS) & 2) > 0 ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
    }

    private void updatingUsingItem() {
        if (this.isUsingItem()) {
            if (ItemStack.isSame(this.getItemInHand(this.getUsedItemHand()), this.useItem)) {
                this.useItem = this.getItemInHand(this.getUsedItemHand());
                this.updateUsingItem(this.useItem);
            } else {
                this.stopUsingItem();
            }
        }
    }

    protected void updateUsingItem(ItemStack $$0) {
        $$0.onUseTick(this.level, this, this.getUseItemRemainingTicks());
        if (this.shouldTriggerItemUseEffects()) {
            this.triggerItemUseEffects($$0, 5);
        }
        if (--this.useItemRemaining == 0 && !this.level.isClientSide && !$$0.useOnRelease()) {
            this.completeUsingItem();
        }
    }

    private boolean shouldTriggerItemUseEffects() {
        int $$0 = this.getUseItemRemainingTicks();
        FoodProperties $$1 = this.useItem.getItem().getFoodProperties();
        boolean $$2 = $$1 != null && $$1.isFastFood();
        return ($$2 |= $$0 <= this.useItem.getUseDuration() - 7) && $$0 % 4 == 0;
    }

    private void updateSwimAmount() {
        this.swimAmountO = this.swimAmount;
        this.swimAmount = this.isVisuallySwimming() ? Math.min((float)1.0f, (float)(this.swimAmount + 0.09f)) : Math.max((float)0.0f, (float)(this.swimAmount - 0.09f));
    }

    protected void setLivingEntityFlag(int $$0, boolean $$1) {
        int $$2 = this.entityData.get(DATA_LIVING_ENTITY_FLAGS).byteValue();
        $$2 = $$1 ? ($$2 |= $$0) : ($$2 &= ~$$0);
        this.entityData.set(DATA_LIVING_ENTITY_FLAGS, (byte)$$2);
    }

    public void startUsingItem(InteractionHand $$0) {
        ItemStack $$1 = this.getItemInHand($$0);
        if ($$1.isEmpty() || this.isUsingItem()) {
            return;
        }
        this.useItem = $$1;
        this.useItemRemaining = $$1.getUseDuration();
        if (!this.level.isClientSide) {
            this.setLivingEntityFlag(1, true);
            this.setLivingEntityFlag(2, $$0 == InteractionHand.OFF_HAND);
            this.gameEvent(GameEvent.ITEM_INTERACT_START);
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> $$0) {
        super.onSyncedDataUpdated($$0);
        if (SLEEPING_POS_ID.equals($$0)) {
            if (this.level.isClientSide) {
                this.getSleepingPos().ifPresent(this::setPosToBed);
            }
        } else if (DATA_LIVING_ENTITY_FLAGS.equals($$0) && this.level.isClientSide) {
            if (this.isUsingItem() && this.useItem.isEmpty()) {
                this.useItem = this.getItemInHand(this.getUsedItemHand());
                if (!this.useItem.isEmpty()) {
                    this.useItemRemaining = this.useItem.getUseDuration();
                }
            } else if (!this.isUsingItem() && !this.useItem.isEmpty()) {
                this.useItem = ItemStack.EMPTY;
                this.useItemRemaining = 0;
            }
        }
    }

    @Override
    public void lookAt(EntityAnchorArgument.Anchor $$0, Vec3 $$1) {
        super.lookAt($$0, $$1);
        this.yHeadRotO = this.yHeadRot;
        this.yBodyRotO = this.yBodyRot = this.yHeadRot;
    }

    protected void triggerItemUseEffects(ItemStack $$0, int $$1) {
        if ($$0.isEmpty() || !this.isUsingItem()) {
            return;
        }
        if ($$0.getUseAnimation() == UseAnim.DRINK) {
            this.playSound(this.getDrinkingSound($$0), 0.5f, this.level.random.nextFloat() * 0.1f + 0.9f);
        }
        if ($$0.getUseAnimation() == UseAnim.EAT) {
            this.spawnItemParticles($$0, $$1);
            this.playSound(this.getEatingSound($$0), 0.5f + 0.5f * (float)this.random.nextInt(2), (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
        }
    }

    private void spawnItemParticles(ItemStack $$0, int $$1) {
        for (int $$2 = 0; $$2 < $$1; ++$$2) {
            Vec3 $$3 = new Vec3(((double)this.random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
            $$3 = $$3.xRot(-this.getXRot() * ((float)Math.PI / 180));
            $$3 = $$3.yRot(-this.getYRot() * ((float)Math.PI / 180));
            double $$4 = (double)(-this.random.nextFloat()) * 0.6 - 0.3;
            Vec3 $$5 = new Vec3(((double)this.random.nextFloat() - 0.5) * 0.3, $$4, 0.6);
            $$5 = $$5.xRot(-this.getXRot() * ((float)Math.PI / 180));
            $$5 = $$5.yRot(-this.getYRot() * ((float)Math.PI / 180));
            $$5 = $$5.add(this.getX(), this.getEyeY(), this.getZ());
            this.level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, $$0), $$5.x, $$5.y, $$5.z, $$3.x, $$3.y + 0.05, $$3.z);
        }
    }

    protected void completeUsingItem() {
        if (this.level.isClientSide && !this.isUsingItem()) {
            return;
        }
        InteractionHand $$0 = this.getUsedItemHand();
        if (!this.useItem.equals(this.getItemInHand($$0))) {
            this.releaseUsingItem();
            return;
        }
        if (!this.useItem.isEmpty() && this.isUsingItem()) {
            this.triggerItemUseEffects(this.useItem, 16);
            ItemStack $$1 = this.useItem.finishUsingItem(this.level, this);
            if ($$1 != this.useItem) {
                this.setItemInHand($$0, $$1);
            }
            this.stopUsingItem();
        }
    }

    public ItemStack getUseItem() {
        return this.useItem;
    }

    public int getUseItemRemainingTicks() {
        return this.useItemRemaining;
    }

    public int getTicksUsingItem() {
        if (this.isUsingItem()) {
            return this.useItem.getUseDuration() - this.getUseItemRemainingTicks();
        }
        return 0;
    }

    public void releaseUsingItem() {
        if (!this.useItem.isEmpty()) {
            this.useItem.releaseUsing(this.level, this, this.getUseItemRemainingTicks());
            if (this.useItem.useOnRelease()) {
                this.updatingUsingItem();
            }
        }
        this.stopUsingItem();
    }

    public void stopUsingItem() {
        if (!this.level.isClientSide) {
            boolean $$0 = this.isUsingItem();
            this.setLivingEntityFlag(1, false);
            if ($$0) {
                this.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
            }
        }
        this.useItem = ItemStack.EMPTY;
        this.useItemRemaining = 0;
    }

    public boolean isBlocking() {
        if (!this.isUsingItem() || this.useItem.isEmpty()) {
            return false;
        }
        Item $$0 = this.useItem.getItem();
        if ($$0.getUseAnimation(this.useItem) != UseAnim.BLOCK) {
            return false;
        }
        return $$0.getUseDuration(this.useItem) - this.useItemRemaining >= 5;
    }

    public boolean isSuppressingSlidingDownLadder() {
        return this.isShiftKeyDown();
    }

    public boolean isFallFlying() {
        return this.getSharedFlag(7);
    }

    @Override
    public boolean isVisuallySwimming() {
        return super.isVisuallySwimming() || !this.isFallFlying() && this.hasPose(Pose.FALL_FLYING);
    }

    public int getFallFlyingTicks() {
        return this.fallFlyTicks;
    }

    public boolean randomTeleport(double $$0, double $$1, double $$2, boolean $$3) {
        double $$4 = this.getX();
        double $$5 = this.getY();
        double $$6 = this.getZ();
        double $$7 = $$1;
        boolean $$8 = false;
        Level $$10 = this.level;
        Vec3i $$9 = new BlockPos($$0, $$7, $$2);
        if ($$10.hasChunkAt((BlockPos)$$9)) {
            boolean $$11 = false;
            while (!$$11 && $$9.getY() > $$10.getMinBuildHeight()) {
                Vec3i $$12 = $$9.below();
                BlockState $$13 = $$10.getBlockState((BlockPos)$$12);
                if ($$13.getMaterial().blocksMotion()) {
                    $$11 = true;
                    continue;
                }
                $$7 -= 1.0;
                $$9 = $$12;
            }
            if ($$11) {
                this.teleportTo($$0, $$7, $$2);
                if ($$10.noCollision(this) && !$$10.containsAnyLiquid(this.getBoundingBox())) {
                    $$8 = true;
                }
            }
        }
        if (!$$8) {
            this.teleportTo($$4, $$5, $$6);
            return false;
        }
        if ($$3) {
            $$10.broadcastEntityEvent(this, (byte)46);
        }
        if (this instanceof PathfinderMob) {
            ((PathfinderMob)this).getNavigation().stop();
        }
        return true;
    }

    public boolean isAffectedByPotions() {
        return true;
    }

    public boolean attackable() {
        return true;
    }

    public void setRecordPlayingNearby(BlockPos $$0, boolean $$1) {
    }

    public boolean canTakeItem(ItemStack $$0) {
        return false;
    }

    @Override
    public EntityDimensions getDimensions(Pose $$0) {
        return $$0 == Pose.SLEEPING ? SLEEPING_DIMENSIONS : super.getDimensions($$0).scale(this.getScale());
    }

    public ImmutableList<Pose> getDismountPoses() {
        return ImmutableList.of((Object)((Object)Pose.STANDING));
    }

    public AABB getLocalBoundsForPose(Pose $$0) {
        EntityDimensions $$1 = this.getDimensions($$0);
        return new AABB(-$$1.width / 2.0f, 0.0, -$$1.width / 2.0f, $$1.width / 2.0f, $$1.height, $$1.width / 2.0f);
    }

    public Optional<BlockPos> getSleepingPos() {
        return this.entityData.get(SLEEPING_POS_ID);
    }

    public void setSleepingPos(BlockPos $$0) {
        this.entityData.set(SLEEPING_POS_ID, Optional.of((Object)$$0));
    }

    public void clearSleepingPos() {
        this.entityData.set(SLEEPING_POS_ID, Optional.empty());
    }

    public boolean isSleeping() {
        return this.getSleepingPos().isPresent();
    }

    public void startSleeping(BlockPos $$0) {
        BlockState $$1;
        if (this.isPassenger()) {
            this.stopRiding();
        }
        if (($$1 = this.level.getBlockState($$0)).getBlock() instanceof BedBlock) {
            this.level.setBlock($$0, (BlockState)$$1.setValue(BedBlock.OCCUPIED, true), 3);
        }
        this.setPose(Pose.SLEEPING);
        this.setPosToBed($$0);
        this.setSleepingPos($$0);
        this.setDeltaMovement(Vec3.ZERO);
        this.hasImpulse = true;
    }

    private void setPosToBed(BlockPos $$0) {
        this.setPos((double)$$0.getX() + 0.5, (double)$$0.getY() + 0.6875, (double)$$0.getZ() + 0.5);
    }

    private boolean checkBedExists() {
        return (Boolean)this.getSleepingPos().map($$0 -> this.level.getBlockState((BlockPos)$$0).getBlock() instanceof BedBlock).orElse((Object)false);
    }

    public void stopSleeping() {
        this.getSleepingPos().filter(this.level::hasChunkAt).ifPresent($$0 -> {
            BlockState $$1 = this.level.getBlockState((BlockPos)$$0);
            if ($$1.getBlock() instanceof BedBlock) {
                Direction $$2 = $$1.getValue(BedBlock.FACING);
                this.level.setBlock((BlockPos)$$0, (BlockState)$$1.setValue(BedBlock.OCCUPIED, false), 3);
                Vec3 $$3 = (Vec3)BedBlock.findStandUpPosition(this.getType(), this.level, $$0, $$2, this.getYRot()).orElseGet(() -> {
                    Vec3i $$1 = $$0.above();
                    return new Vec3((double)$$1.getX() + 0.5, (double)$$1.getY() + 0.1, (double)$$1.getZ() + 0.5);
                });
                Vec3 $$4 = Vec3.atBottomCenterOf($$0).subtract($$3).normalize();
                float $$5 = (float)Mth.wrapDegrees(Mth.atan2($$4.z, $$4.x) * 57.2957763671875 - 90.0);
                this.setPos($$3.x, $$3.y, $$3.z);
                this.setYRot($$5);
                this.setXRot(0.0f);
            }
        });
        Vec3 $$02 = this.position();
        this.setPose(Pose.STANDING);
        this.setPos($$02.x, $$02.y, $$02.z);
        this.clearSleepingPos();
    }

    @Nullable
    public Direction getBedOrientation() {
        BlockPos $$0 = (BlockPos)this.getSleepingPos().orElse(null);
        return $$0 != null ? BedBlock.getBedOrientation(this.level, $$0) : null;
    }

    @Override
    public boolean isInWall() {
        return !this.isSleeping() && super.isInWall();
    }

    @Override
    protected final float getEyeHeight(Pose $$0, EntityDimensions $$1) {
        return $$0 == Pose.SLEEPING ? 0.2f : this.getStandingEyeHeight($$0, $$1);
    }

    protected float getStandingEyeHeight(Pose $$0, EntityDimensions $$1) {
        return super.getEyeHeight($$0, $$1);
    }

    public ItemStack getProjectile(ItemStack $$0) {
        return ItemStack.EMPTY;
    }

    public ItemStack eat(Level $$0, ItemStack $$1) {
        if ($$1.isEdible()) {
            $$0.playSound(null, this.getX(), this.getY(), this.getZ(), this.getEatingSound($$1), SoundSource.NEUTRAL, 1.0f, 1.0f + ($$0.random.nextFloat() - $$0.random.nextFloat()) * 0.4f);
            this.addEatEffect($$1, $$0, this);
            if (!(this instanceof Player) || !((Player)this).getAbilities().instabuild) {
                $$1.shrink(1);
            }
            this.gameEvent(GameEvent.EAT);
        }
        return $$1;
    }

    private void addEatEffect(ItemStack $$0, Level $$1, LivingEntity $$2) {
        Item $$3 = $$0.getItem();
        if ($$3.isEdible()) {
            List<Pair<MobEffectInstance, Float>> $$4 = $$3.getFoodProperties().getEffects();
            for (Pair $$5 : $$4) {
                if ($$1.isClientSide || $$5.getFirst() == null || !($$1.random.nextFloat() < ((Float)$$5.getSecond()).floatValue())) continue;
                $$2.addEffect(new MobEffectInstance((MobEffectInstance)$$5.getFirst()));
            }
        }
    }

    private static byte entityEventForEquipmentBreak(EquipmentSlot $$0) {
        switch ($$0) {
            case MAINHAND: {
                return 47;
            }
            case OFFHAND: {
                return 48;
            }
            case HEAD: {
                return 49;
            }
            case CHEST: {
                return 50;
            }
            case FEET: {
                return 52;
            }
            case LEGS: {
                return 51;
            }
        }
        return 47;
    }

    public void broadcastBreakEvent(EquipmentSlot $$0) {
        this.level.broadcastEntityEvent(this, LivingEntity.entityEventForEquipmentBreak($$0));
    }

    public void broadcastBreakEvent(InteractionHand $$0) {
        this.broadcastBreakEvent($$0 == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        if (this.getItemBySlot(EquipmentSlot.HEAD).is(Items.DRAGON_HEAD)) {
            float $$0 = 0.5f;
            return this.getBoundingBox().inflate(0.5, 0.5, 0.5);
        }
        return super.getBoundingBoxForCulling();
    }

    public static EquipmentSlot getEquipmentSlotForItem(ItemStack $$0) {
        Item $$1 = $$0.getItem();
        if ($$0.is(Items.CARVED_PUMPKIN) || $$1 instanceof BlockItem && ((BlockItem)$$1).getBlock() instanceof AbstractSkullBlock) {
            return EquipmentSlot.HEAD;
        }
        if ($$1 instanceof ArmorItem) {
            return ((ArmorItem)$$1).getSlot();
        }
        if ($$0.is(Items.ELYTRA)) {
            return EquipmentSlot.CHEST;
        }
        if ($$0.is(Items.SHIELD)) {
            return EquipmentSlot.OFFHAND;
        }
        return EquipmentSlot.MAINHAND;
    }

    private static SlotAccess createEquipmentSlotAccess(LivingEntity $$0, EquipmentSlot $$12) {
        if ($$12 == EquipmentSlot.HEAD || $$12 == EquipmentSlot.MAINHAND || $$12 == EquipmentSlot.OFFHAND) {
            return SlotAccess.forEquipmentSlot($$0, $$12);
        }
        return SlotAccess.forEquipmentSlot($$0, $$12, (Predicate<ItemStack>)((Predicate)$$1 -> $$1.isEmpty() || Mob.getEquipmentSlotForItem($$1) == $$12));
    }

    @Nullable
    private static EquipmentSlot getEquipmentSlot(int $$0) {
        if ($$0 == 100 + EquipmentSlot.HEAD.getIndex()) {
            return EquipmentSlot.HEAD;
        }
        if ($$0 == 100 + EquipmentSlot.CHEST.getIndex()) {
            return EquipmentSlot.CHEST;
        }
        if ($$0 == 100 + EquipmentSlot.LEGS.getIndex()) {
            return EquipmentSlot.LEGS;
        }
        if ($$0 == 100 + EquipmentSlot.FEET.getIndex()) {
            return EquipmentSlot.FEET;
        }
        if ($$0 == 98) {
            return EquipmentSlot.MAINHAND;
        }
        if ($$0 == 99) {
            return EquipmentSlot.OFFHAND;
        }
        return null;
    }

    @Override
    public SlotAccess getSlot(int $$0) {
        EquipmentSlot $$1 = LivingEntity.getEquipmentSlot($$0);
        if ($$1 != null) {
            return LivingEntity.createEquipmentSlotAccess(this, $$1);
        }
        return super.getSlot($$0);
    }

    @Override
    public boolean canFreeze() {
        if (this.isSpectator()) {
            return false;
        }
        boolean $$0 = !this.getItemBySlot(EquipmentSlot.HEAD).is(ItemTags.FREEZE_IMMUNE_WEARABLES) && !this.getItemBySlot(EquipmentSlot.CHEST).is(ItemTags.FREEZE_IMMUNE_WEARABLES) && !this.getItemBySlot(EquipmentSlot.LEGS).is(ItemTags.FREEZE_IMMUNE_WEARABLES) && !this.getItemBySlot(EquipmentSlot.FEET).is(ItemTags.FREEZE_IMMUNE_WEARABLES);
        return $$0 && super.canFreeze();
    }

    @Override
    public boolean isCurrentlyGlowing() {
        return !this.level.isClientSide() && this.hasEffect(MobEffects.GLOWING) || super.isCurrentlyGlowing();
    }

    @Override
    public float getVisualRotationYInDegrees() {
        return this.yBodyRot;
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket $$0) {
        double $$1 = $$0.getX();
        double $$2 = $$0.getY();
        double $$3 = $$0.getZ();
        float $$4 = $$0.getYRot();
        float $$5 = $$0.getXRot();
        this.syncPacketPositionCodec($$1, $$2, $$3);
        this.yBodyRot = $$0.getYHeadRot();
        this.yHeadRot = $$0.getYHeadRot();
        this.yBodyRotO = this.yBodyRot;
        this.yHeadRotO = this.yHeadRot;
        this.setId($$0.getId());
        this.setUUID($$0.getUUID());
        this.absMoveTo($$1, $$2, $$3, $$4, $$5);
        this.setDeltaMovement($$0.getXa(), $$0.getYa(), $$0.getZa());
    }

    public boolean canDisableShield() {
        return this.getMainHandItem().getItem() instanceof AxeItem;
    }

    private /* synthetic */ void lambda$handleEquipmentChanges$5(List $$0, EquipmentSlot $$1, ItemStack $$2) {
        ItemStack $$3 = $$2.copy();
        $$0.add((Object)Pair.of((Object)((Object)$$1), (Object)$$3));
        switch ($$1.getType()) {
            case HAND: {
                this.setLastHandItem($$1, $$3);
                break;
            }
            case ARMOR: {
                this.setLastArmorItem($$1, $$3);
            }
        }
    }

    public record Fallsounds(SoundEvent small, SoundEvent big) {
    }
}