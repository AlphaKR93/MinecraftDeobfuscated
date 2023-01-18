/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.objects.Object2DoubleArrayMap
 *  it.unimi.dsi.fastutil.objects.Object2DoubleMap
 *  java.lang.Boolean
 *  java.lang.Byte
 *  java.lang.Deprecated
 *  java.lang.Double
 *  java.lang.Exception
 *  java.lang.Float
 *  java.lang.FunctionalInterface
 *  java.lang.IllegalStateException
 *  java.lang.Integer
 *  java.lang.Iterable
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.Throwable
 *  java.util.ArrayList
 *  java.util.Arrays
 *  java.util.Collection
 *  java.util.Collections
 *  java.util.HashSet
 *  java.util.List
 *  java.util.Locale
 *  java.util.Optional
 *  java.util.Set
 *  java.util.UUID
 *  java.util.concurrent.atomic.AtomicInteger
 *  java.util.function.BiConsumer
 *  java.util.function.Predicate
 *  java.util.function.UnaryOperator
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2DoubleArrayMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.BlockUtil;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.VecDeltaCodec;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.Nameable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.HoneyBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntityInLevelCallback;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import org.slf4j.Logger;

public abstract class Entity
implements Nameable,
EntityAccess,
CommandSource {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String ID_TAG = "id";
    public static final String PASSENGERS_TAG = "Passengers";
    private static final AtomicInteger ENTITY_COUNTER = new AtomicInteger();
    private static final List<ItemStack> EMPTY_LIST = Collections.emptyList();
    public static final int BOARDING_COOLDOWN = 60;
    public static final int TOTAL_AIR_SUPPLY = 300;
    public static final int MAX_ENTITY_TAG_COUNT = 1024;
    public static final double DELTA_AFFECTED_BY_BLOCKS_BELOW = 0.5000001;
    public static final float BREATHING_DISTANCE_BELOW_EYES = 0.11111111f;
    public static final int BASE_TICKS_REQUIRED_TO_FREEZE = 140;
    public static final int FREEZE_HURT_FREQUENCY = 40;
    private static final AABB INITIAL_AABB = new AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    private static final double WATER_FLOW_SCALE = 0.014;
    private static final double LAVA_FAST_FLOW_SCALE = 0.007;
    private static final double LAVA_SLOW_FLOW_SCALE = 0.0023333333333333335;
    public static final String UUID_TAG = "UUID";
    private static double viewScale = 1.0;
    private final EntityType<?> type;
    private int id = ENTITY_COUNTER.incrementAndGet();
    public boolean blocksBuilding;
    private ImmutableList<Entity> passengers = ImmutableList.of();
    protected int boardingCooldown;
    @Nullable
    private Entity vehicle;
    public Level level;
    public double xo;
    public double yo;
    public double zo;
    private Vec3 position;
    private BlockPos blockPosition;
    private ChunkPos chunkPosition;
    private Vec3 deltaMovement = Vec3.ZERO;
    private float yRot;
    private float xRot;
    public float yRotO;
    public float xRotO;
    private AABB bb = INITIAL_AABB;
    protected boolean onGround;
    public boolean horizontalCollision;
    public boolean verticalCollision;
    public boolean verticalCollisionBelow;
    public boolean minorHorizontalCollision;
    public boolean hurtMarked;
    protected Vec3 stuckSpeedMultiplier = Vec3.ZERO;
    @Nullable
    private RemovalReason removalReason;
    public static final float DEFAULT_BB_WIDTH = 0.6f;
    public static final float DEFAULT_BB_HEIGHT = 1.8f;
    public float walkDistO;
    public float walkDist;
    public float moveDist;
    public float flyDist;
    public float fallDistance;
    private float nextStep = 1.0f;
    public double xOld;
    public double yOld;
    public double zOld;
    public float maxUpStep;
    public boolean noPhysics;
    protected final RandomSource random = RandomSource.create();
    public int tickCount;
    private int remainingFireTicks = -this.getFireImmuneTicks();
    protected boolean wasTouchingWater;
    protected Object2DoubleMap<TagKey<Fluid>> fluidHeight = new Object2DoubleArrayMap(2);
    protected boolean wasEyeInWater;
    private final Set<TagKey<Fluid>> fluidOnEyes = new HashSet();
    public int invulnerableTime;
    protected boolean firstTick = true;
    protected final SynchedEntityData entityData;
    protected static final EntityDataAccessor<Byte> DATA_SHARED_FLAGS_ID = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.BYTE);
    protected static final int FLAG_ONFIRE = 0;
    private static final int FLAG_SHIFT_KEY_DOWN = 1;
    private static final int FLAG_SPRINTING = 3;
    private static final int FLAG_SWIMMING = 4;
    private static final int FLAG_INVISIBLE = 5;
    protected static final int FLAG_GLOWING = 6;
    protected static final int FLAG_FALL_FLYING = 7;
    private static final EntityDataAccessor<Integer> DATA_AIR_SUPPLY_ID = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<Component>> DATA_CUSTOM_NAME = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.OPTIONAL_COMPONENT);
    private static final EntityDataAccessor<Boolean> DATA_CUSTOM_NAME_VISIBLE = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_SILENT = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_NO_GRAVITY = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Pose> DATA_POSE = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.POSE);
    private static final EntityDataAccessor<Integer> DATA_TICKS_FROZEN = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.INT);
    private EntityInLevelCallback levelCallback = EntityInLevelCallback.NULL;
    private final VecDeltaCodec packetPositionCodec = new VecDeltaCodec();
    public boolean noCulling;
    public boolean hasImpulse;
    private int portalCooldown;
    protected boolean isInsidePortal;
    protected int portalTime;
    protected BlockPos portalEntrancePos;
    private boolean invulnerable;
    protected UUID uuid = Mth.createInsecureUUID(this.random);
    protected String stringUUID = this.uuid.toString();
    private boolean hasGlowingTag;
    private final Set<String> tags = Sets.newHashSet();
    private final double[] pistonDeltas = new double[]{0.0, 0.0, 0.0};
    private long pistonDeltasGameTime;
    private EntityDimensions dimensions;
    private float eyeHeight;
    public boolean isInPowderSnow;
    public boolean wasInPowderSnow;
    public boolean wasOnFire;
    private float crystalSoundIntensity;
    private int lastCrystalSoundPlayTick;
    private boolean hasVisualFire;
    @Nullable
    private BlockState feetBlockState = null;

    public Entity(EntityType<?> $$0, Level $$1) {
        this.type = $$0;
        this.level = $$1;
        this.dimensions = $$0.getDimensions();
        this.position = Vec3.ZERO;
        this.blockPosition = BlockPos.ZERO;
        this.chunkPosition = ChunkPos.ZERO;
        this.entityData = new SynchedEntityData(this);
        this.entityData.define(DATA_SHARED_FLAGS_ID, (byte)0);
        this.entityData.define(DATA_AIR_SUPPLY_ID, this.getMaxAirSupply());
        this.entityData.define(DATA_CUSTOM_NAME_VISIBLE, false);
        this.entityData.define(DATA_CUSTOM_NAME, Optional.empty());
        this.entityData.define(DATA_SILENT, false);
        this.entityData.define(DATA_NO_GRAVITY, false);
        this.entityData.define(DATA_POSE, Pose.STANDING);
        this.entityData.define(DATA_TICKS_FROZEN, 0);
        this.defineSynchedData();
        this.setPos(0.0, 0.0, 0.0);
        this.eyeHeight = this.getEyeHeight(Pose.STANDING, this.dimensions);
    }

    public boolean isColliding(BlockPos $$0, BlockState $$1) {
        VoxelShape $$2 = $$1.getCollisionShape(this.level, $$0, CollisionContext.of(this));
        VoxelShape $$3 = $$2.move($$0.getX(), $$0.getY(), $$0.getZ());
        return Shapes.joinIsNotEmpty($$3, Shapes.create(this.getBoundingBox()), BooleanOp.AND);
    }

    public int getTeamColor() {
        Team $$0 = this.getTeam();
        if ($$0 != null && $$0.getColor().getColor() != null) {
            return $$0.getColor().getColor();
        }
        return 0xFFFFFF;
    }

    public boolean isSpectator() {
        return false;
    }

    public final void unRide() {
        if (this.isVehicle()) {
            this.ejectPassengers();
        }
        if (this.isPassenger()) {
            this.stopRiding();
        }
    }

    public void syncPacketPositionCodec(double $$0, double $$1, double $$2) {
        this.packetPositionCodec.setBase(new Vec3($$0, $$1, $$2));
    }

    public VecDeltaCodec getPositionCodec() {
        return this.packetPositionCodec;
    }

    public EntityType<?> getType() {
        return this.type;
    }

    @Override
    public int getId() {
        return this.id;
    }

    public void setId(int $$0) {
        this.id = $$0;
    }

    public Set<String> getTags() {
        return this.tags;
    }

    public boolean addTag(String $$0) {
        if (this.tags.size() >= 1024) {
            return false;
        }
        return this.tags.add((Object)$$0);
    }

    public boolean removeTag(String $$0) {
        return this.tags.remove((Object)$$0);
    }

    public void kill() {
        this.remove(RemovalReason.KILLED);
        this.gameEvent(GameEvent.ENTITY_DIE);
    }

    public final void discard() {
        this.remove(RemovalReason.DISCARDED);
    }

    protected abstract void defineSynchedData();

    public SynchedEntityData getEntityData() {
        return this.entityData;
    }

    public boolean equals(Object $$0) {
        if ($$0 instanceof Entity) {
            return ((Entity)$$0).id == this.id;
        }
        return false;
    }

    public int hashCode() {
        return this.id;
    }

    public void remove(RemovalReason $$0) {
        this.setRemoved($$0);
    }

    public void onClientRemoval() {
    }

    public void setPose(Pose $$0) {
        this.entityData.set(DATA_POSE, $$0);
    }

    public Pose getPose() {
        return this.entityData.get(DATA_POSE);
    }

    public boolean hasPose(Pose $$0) {
        return this.getPose() == $$0;
    }

    public boolean closerThan(Entity $$0, double $$1) {
        return this.position().closerThan($$0.position(), $$1);
    }

    public boolean closerThan(Entity $$0, double $$1, double $$2) {
        double $$3 = $$0.getX() - this.getX();
        double $$4 = $$0.getY() - this.getY();
        double $$5 = $$0.getZ() - this.getZ();
        return Mth.lengthSquared($$3, $$5) < Mth.square($$1) && Mth.square($$4) < Mth.square($$2);
    }

    protected void setRot(float $$0, float $$1) {
        this.setYRot($$0 % 360.0f);
        this.setXRot($$1 % 360.0f);
    }

    public final void setPos(Vec3 $$0) {
        this.setPos($$0.x(), $$0.y(), $$0.z());
    }

    public void setPos(double $$0, double $$1, double $$2) {
        this.setPosRaw($$0, $$1, $$2);
        this.setBoundingBox(this.makeBoundingBox());
    }

    protected AABB makeBoundingBox() {
        return this.dimensions.makeBoundingBox(this.position);
    }

    protected void reapplyPosition() {
        this.setPos(this.position.x, this.position.y, this.position.z);
    }

    public void turn(double $$0, double $$1) {
        float $$2 = (float)$$1 * 0.15f;
        float $$3 = (float)$$0 * 0.15f;
        this.setXRot(this.getXRot() + $$2);
        this.setYRot(this.getYRot() + $$3);
        this.setXRot(Mth.clamp(this.getXRot(), -90.0f, 90.0f));
        this.xRotO += $$2;
        this.yRotO += $$3;
        this.xRotO = Mth.clamp(this.xRotO, -90.0f, 90.0f);
        if (this.vehicle != null) {
            this.vehicle.onPassengerTurned(this);
        }
    }

    public void tick() {
        this.baseTick();
    }

    public void baseTick() {
        this.level.getProfiler().push("entityBaseTick");
        this.feetBlockState = null;
        if (this.isPassenger() && this.getVehicle().isRemoved()) {
            this.stopRiding();
        }
        if (this.boardingCooldown > 0) {
            --this.boardingCooldown;
        }
        this.walkDistO = this.walkDist;
        this.xRotO = this.getXRot();
        this.yRotO = this.getYRot();
        this.handleNetherPortal();
        if (this.canSpawnSprintParticle()) {
            this.spawnSprintParticle();
        }
        this.wasInPowderSnow = this.isInPowderSnow;
        this.isInPowderSnow = false;
        this.updateInWaterStateAndDoFluidPushing();
        this.updateFluidOnEyes();
        this.updateSwimming();
        if (this.level.isClientSide) {
            this.clearFire();
        } else if (this.remainingFireTicks > 0) {
            if (this.fireImmune()) {
                this.setRemainingFireTicks(this.remainingFireTicks - 4);
                if (this.remainingFireTicks < 0) {
                    this.clearFire();
                }
            } else {
                if (this.remainingFireTicks % 20 == 0 && !this.isInLava()) {
                    this.hurt(DamageSource.ON_FIRE, 1.0f);
                }
                this.setRemainingFireTicks(this.remainingFireTicks - 1);
            }
            if (this.getTicksFrozen() > 0) {
                this.setTicksFrozen(0);
                this.level.levelEvent(null, 1009, this.blockPosition, 1);
            }
        }
        if (this.isInLava()) {
            this.lavaHurt();
            this.fallDistance *= 0.5f;
        }
        this.checkOutOfWorld();
        if (!this.level.isClientSide) {
            this.setSharedFlagOnFire(this.remainingFireTicks > 0);
        }
        this.firstTick = false;
        this.level.getProfiler().pop();
    }

    public void setSharedFlagOnFire(boolean $$0) {
        this.setSharedFlag(0, $$0 || this.hasVisualFire);
    }

    public void checkOutOfWorld() {
        if (this.getY() < (double)(this.level.getMinBuildHeight() - 64)) {
            this.outOfWorld();
        }
    }

    public void setPortalCooldown() {
        this.portalCooldown = this.getDimensionChangingDelay();
    }

    public boolean isOnPortalCooldown() {
        return this.portalCooldown > 0;
    }

    protected void processPortalCooldown() {
        if (this.isOnPortalCooldown()) {
            --this.portalCooldown;
        }
    }

    public int getPortalWaitTime() {
        return 0;
    }

    public void lavaHurt() {
        if (this.fireImmune()) {
            return;
        }
        this.setSecondsOnFire(15);
        if (this.hurt(DamageSource.LAVA, 4.0f)) {
            this.playSound(SoundEvents.GENERIC_BURN, 0.4f, 2.0f + this.random.nextFloat() * 0.4f);
        }
    }

    public void setSecondsOnFire(int $$0) {
        int $$1 = $$0 * 20;
        if (this instanceof LivingEntity) {
            $$1 = ProtectionEnchantment.getFireAfterDampener((LivingEntity)this, $$1);
        }
        if (this.remainingFireTicks < $$1) {
            this.setRemainingFireTicks($$1);
        }
    }

    public void setRemainingFireTicks(int $$0) {
        this.remainingFireTicks = $$0;
    }

    public int getRemainingFireTicks() {
        return this.remainingFireTicks;
    }

    public void clearFire() {
        this.setRemainingFireTicks(0);
    }

    protected void outOfWorld() {
        this.discard();
    }

    public boolean isFree(double $$0, double $$1, double $$2) {
        return this.isFree(this.getBoundingBox().move($$0, $$1, $$2));
    }

    private boolean isFree(AABB $$0) {
        return this.level.noCollision(this, $$0) && !this.level.containsAnyLiquid($$0);
    }

    public void setOnGround(boolean $$0) {
        this.onGround = $$0;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    public void move(MoverType $$02, Vec3 $$1) {
        MovementEmission $$11;
        Vec3 $$2;
        double $$3;
        if (this.noPhysics) {
            this.setPos(this.getX() + $$1.x, this.getY() + $$1.y, this.getZ() + $$1.z);
            return;
        }
        this.wasOnFire = this.isOnFire();
        if ($$02 == MoverType.PISTON && ($$1 = this.limitPistonMovement($$1)).equals(Vec3.ZERO)) {
            return;
        }
        this.level.getProfiler().push("move");
        if (this.stuckSpeedMultiplier.lengthSqr() > 1.0E-7) {
            $$1 = $$1.multiply(this.stuckSpeedMultiplier);
            this.stuckSpeedMultiplier = Vec3.ZERO;
            this.setDeltaMovement(Vec3.ZERO);
        }
        if (($$3 = ($$2 = this.collide($$1 = this.maybeBackOffFromEdge($$1, $$02))).lengthSqr()) > 1.0E-7) {
            BlockHitResult $$4;
            if (this.fallDistance != 0.0f && $$3 >= 1.0 && ($$4 = this.level.clip(new ClipContext(this.position(), this.position().add($$2), ClipContext.Block.FALLDAMAGE_RESETTING, ClipContext.Fluid.WATER, this))).getType() != HitResult.Type.MISS) {
                this.resetFallDistance();
            }
            this.setPos(this.getX() + $$2.x, this.getY() + $$2.y, this.getZ() + $$2.z);
        }
        this.level.getProfiler().pop();
        this.level.getProfiler().push("rest");
        boolean $$5 = !Mth.equal($$1.x, $$2.x);
        boolean $$6 = !Mth.equal($$1.z, $$2.z);
        this.horizontalCollision = $$5 || $$6;
        this.verticalCollision = $$1.y != $$2.y;
        this.verticalCollisionBelow = this.verticalCollision && $$1.y < 0.0;
        this.minorHorizontalCollision = this.horizontalCollision ? this.isHorizontalCollisionMinor($$2) : false;
        this.onGround = this.verticalCollision && $$1.y < 0.0;
        BlockPos $$7 = this.getOnPosLegacy();
        BlockState $$8 = this.level.getBlockState($$7);
        this.checkFallDamage($$2.y, this.onGround, $$8, $$7);
        if (this.isRemoved()) {
            this.level.getProfiler().pop();
            return;
        }
        if (this.horizontalCollision) {
            Vec3 $$9 = this.getDeltaMovement();
            this.setDeltaMovement($$5 ? 0.0 : $$9.x, $$9.y, $$6 ? 0.0 : $$9.z);
        }
        Block $$10 = $$8.getBlock();
        if ($$1.y != $$2.y) {
            $$10.updateEntityAfterFallOn(this.level, this);
        }
        if (this.onGround) {
            $$10.stepOn(this.level, $$7, $$8, this);
        }
        if (($$11 = this.getMovementEmission()).emitsAnything() && !this.isPassenger()) {
            boolean $$15;
            double $$12 = $$2.x;
            double $$13 = $$2.y;
            double $$14 = $$2.z;
            this.flyDist += (float)($$2.length() * 0.6);
            boolean bl = $$15 = $$8.is(BlockTags.CLIMBABLE) || $$8.is(Blocks.POWDER_SNOW);
            if (!$$15) {
                $$13 = 0.0;
            }
            this.walkDist += (float)$$2.horizontalDistance() * 0.6f;
            this.moveDist += (float)Math.sqrt((double)($$12 * $$12 + $$13 * $$13 + $$14 * $$14)) * 0.6f;
            if (this.moveDist > this.nextStep && !$$8.isAir()) {
                this.nextStep = this.nextStep();
                if (this.isInWater()) {
                    if ($$11.emitsSounds()) {
                        Entity $$16 = this.isVehicle() && this.getControllingPassenger() != null ? this.getControllingPassenger() : this;
                        float $$17 = $$16 == this ? 0.35f : 0.4f;
                        Vec3 $$18 = $$16.getDeltaMovement();
                        float $$19 = Math.min((float)1.0f, (float)((float)Math.sqrt((double)($$18.x * $$18.x * (double)0.2f + $$18.y * $$18.y + $$18.z * $$18.z * (double)0.2f)) * $$17));
                        this.playSwimSound($$19);
                    }
                    if ($$11.emitsEvents()) {
                        this.gameEvent(GameEvent.SWIM);
                    }
                } else {
                    if ($$11.emitsSounds()) {
                        this.playAmethystStepSound($$8);
                        this.playStepSound($$7, $$8);
                    }
                    if ($$11.emitsEvents() && (this.onGround || $$1.y == 0.0 || this.isInPowderSnow || $$15)) {
                        this.level.gameEvent(GameEvent.STEP, this.position, GameEvent.Context.of(this, this.getBlockStateOn()));
                    }
                }
            } else if ($$8.isAir()) {
                this.processFlappingMovement();
            }
        }
        this.tryCheckInsideBlocks();
        float $$20 = this.getBlockSpeedFactor();
        this.setDeltaMovement(this.getDeltaMovement().multiply($$20, 1.0, $$20));
        if (this.level.getBlockStatesIfLoaded(this.getBoundingBox().deflate(1.0E-6)).noneMatch($$0 -> $$0.is(BlockTags.FIRE) || $$0.is(Blocks.LAVA))) {
            if (this.remainingFireTicks <= 0) {
                this.setRemainingFireTicks(-this.getFireImmuneTicks());
            }
            if (this.wasOnFire && (this.isInPowderSnow || this.isInWaterRainOrBubble())) {
                this.playEntityOnFireExtinguishedSound();
            }
        }
        if (this.isOnFire() && (this.isInPowderSnow || this.isInWaterRainOrBubble())) {
            this.setRemainingFireTicks(-this.getFireImmuneTicks());
        }
        this.level.getProfiler().pop();
    }

    protected boolean isHorizontalCollisionMinor(Vec3 $$0) {
        return false;
    }

    protected void tryCheckInsideBlocks() {
        try {
            this.checkInsideBlocks();
        }
        catch (Throwable $$0) {
            CrashReport $$1 = CrashReport.forThrowable($$0, "Checking entity block collision");
            CrashReportCategory $$2 = $$1.addCategory("Entity being checked for collision");
            this.fillCrashReportCategory($$2);
            throw new ReportedException($$1);
        }
    }

    protected void playEntityOnFireExtinguishedSound() {
        this.playSound(SoundEvents.GENERIC_EXTINGUISH_FIRE, 0.7f, 1.6f + (this.random.nextFloat() - this.random.nextFloat()) * 0.4f);
    }

    public void extinguishFire() {
        if (!this.level.isClientSide && this.wasOnFire) {
            this.playEntityOnFireExtinguishedSound();
        }
        this.clearFire();
    }

    protected void processFlappingMovement() {
        if (this.isFlapping()) {
            this.onFlap();
            if (this.getMovementEmission().emitsEvents()) {
                this.gameEvent(GameEvent.FLAP);
            }
        }
    }

    @Deprecated
    public BlockPos getOnPosLegacy() {
        return this.getOnPos(0.2f);
    }

    public BlockPos getOnPos() {
        return this.getOnPos(1.0E-5f);
    }

    private BlockPos getOnPos(float $$0) {
        Vec3i $$5;
        BlockState $$6;
        int $$3;
        int $$2;
        int $$1 = Mth.floor(this.position.x);
        BlockPos $$4 = new BlockPos($$1, $$2 = Mth.floor(this.position.y - (double)$$0), $$3 = Mth.floor(this.position.z));
        if (this.level.getBlockState($$4).isAir() && (($$6 = this.level.getBlockState((BlockPos)($$5 = $$4.below()))).is(BlockTags.FENCES) || $$6.is(BlockTags.WALLS) || $$6.getBlock() instanceof FenceGateBlock)) {
            return $$5;
        }
        return $$4;
    }

    protected float getBlockJumpFactor() {
        float $$0 = this.level.getBlockState(this.blockPosition()).getBlock().getJumpFactor();
        float $$1 = this.level.getBlockState(this.getBlockPosBelowThatAffectsMyMovement()).getBlock().getJumpFactor();
        return (double)$$0 == 1.0 ? $$1 : $$0;
    }

    protected float getBlockSpeedFactor() {
        BlockState $$0 = this.level.getBlockState(this.blockPosition());
        float $$1 = $$0.getBlock().getSpeedFactor();
        if ($$0.is(Blocks.WATER) || $$0.is(Blocks.BUBBLE_COLUMN)) {
            return $$1;
        }
        return (double)$$1 == 1.0 ? this.level.getBlockState(this.getBlockPosBelowThatAffectsMyMovement()).getBlock().getSpeedFactor() : $$1;
    }

    protected BlockPos getBlockPosBelowThatAffectsMyMovement() {
        return new BlockPos(this.position.x, this.getBoundingBox().minY - 0.5000001, this.position.z);
    }

    protected Vec3 maybeBackOffFromEdge(Vec3 $$0, MoverType $$1) {
        return $$0;
    }

    protected Vec3 limitPistonMovement(Vec3 $$0) {
        if ($$0.lengthSqr() <= 1.0E-7) {
            return $$0;
        }
        long $$1 = this.level.getGameTime();
        if ($$1 != this.pistonDeltasGameTime) {
            Arrays.fill((double[])this.pistonDeltas, (double)0.0);
            this.pistonDeltasGameTime = $$1;
        }
        if ($$0.x != 0.0) {
            double $$2 = this.applyPistonMovementRestriction(Direction.Axis.X, $$0.x);
            return Math.abs((double)$$2) <= (double)1.0E-5f ? Vec3.ZERO : new Vec3($$2, 0.0, 0.0);
        }
        if ($$0.y != 0.0) {
            double $$3 = this.applyPistonMovementRestriction(Direction.Axis.Y, $$0.y);
            return Math.abs((double)$$3) <= (double)1.0E-5f ? Vec3.ZERO : new Vec3(0.0, $$3, 0.0);
        }
        if ($$0.z != 0.0) {
            double $$4 = this.applyPistonMovementRestriction(Direction.Axis.Z, $$0.z);
            return Math.abs((double)$$4) <= (double)1.0E-5f ? Vec3.ZERO : new Vec3(0.0, 0.0, $$4);
        }
        return Vec3.ZERO;
    }

    private double applyPistonMovementRestriction(Direction.Axis $$0, double $$1) {
        int $$2 = $$0.ordinal();
        double $$3 = Mth.clamp($$1 + this.pistonDeltas[$$2], -0.51, 0.51);
        $$1 = $$3 - this.pistonDeltas[$$2];
        this.pistonDeltas[$$2] = $$3;
        return $$1;
    }

    private Vec3 collide(Vec3 $$0) {
        boolean $$7;
        AABB $$1 = this.getBoundingBox();
        List $$2 = this.level.getEntityCollisions(this, $$1.expandTowards($$0));
        Vec3 $$3 = $$0.lengthSqr() == 0.0 ? $$0 : Entity.collideBoundingBox(this, $$0, $$1, this.level, (List<VoxelShape>)$$2);
        boolean $$4 = $$0.x != $$3.x;
        boolean $$5 = $$0.y != $$3.y;
        boolean $$6 = $$0.z != $$3.z;
        boolean bl = $$7 = this.onGround || $$5 && $$0.y < 0.0;
        if (this.maxUpStep > 0.0f && $$7 && ($$4 || $$6)) {
            Vec3 $$10;
            Vec3 $$8 = Entity.collideBoundingBox(this, new Vec3($$0.x, this.maxUpStep, $$0.z), $$1, this.level, (List<VoxelShape>)$$2);
            Vec3 $$9 = Entity.collideBoundingBox(this, new Vec3(0.0, this.maxUpStep, 0.0), $$1.expandTowards($$0.x, 0.0, $$0.z), this.level, (List<VoxelShape>)$$2);
            if ($$9.y < (double)this.maxUpStep && ($$10 = Entity.collideBoundingBox(this, new Vec3($$0.x, 0.0, $$0.z), $$1.move($$9), this.level, (List<VoxelShape>)$$2).add($$9)).horizontalDistanceSqr() > $$8.horizontalDistanceSqr()) {
                $$8 = $$10;
            }
            if ($$8.horizontalDistanceSqr() > $$3.horizontalDistanceSqr()) {
                return $$8.add(Entity.collideBoundingBox(this, new Vec3(0.0, -$$8.y + $$0.y, 0.0), $$1.move($$8), this.level, (List<VoxelShape>)$$2));
            }
        }
        return $$3;
    }

    public static Vec3 collideBoundingBox(@Nullable Entity $$0, Vec3 $$1, AABB $$2, Level $$3, List<VoxelShape> $$4) {
        boolean $$7;
        ImmutableList.Builder $$5 = ImmutableList.builderWithExpectedSize((int)($$4.size() + 1));
        if (!$$4.isEmpty()) {
            $$5.addAll($$4);
        }
        WorldBorder $$6 = $$3.getWorldBorder();
        boolean bl = $$7 = $$0 != null && $$6.isInsideCloseToBorder($$0, $$2.expandTowards($$1));
        if ($$7) {
            $$5.add((Object)$$6.getCollisionShape());
        }
        $$5.addAll($$3.getBlockCollisions($$0, $$2.expandTowards($$1)));
        return Entity.collideWithShapes($$1, $$2, (List<VoxelShape>)$$5.build());
    }

    private static Vec3 collideWithShapes(Vec3 $$0, AABB $$1, List<VoxelShape> $$2) {
        boolean $$6;
        if ($$2.isEmpty()) {
            return $$0;
        }
        double $$3 = $$0.x;
        double $$4 = $$0.y;
        double $$5 = $$0.z;
        if ($$4 != 0.0 && ($$4 = Shapes.collide(Direction.Axis.Y, $$1, $$2, $$4)) != 0.0) {
            $$1 = $$1.move(0.0, $$4, 0.0);
        }
        boolean bl = $$6 = Math.abs((double)$$3) < Math.abs((double)$$5);
        if ($$6 && $$5 != 0.0 && ($$5 = Shapes.collide(Direction.Axis.Z, $$1, $$2, $$5)) != 0.0) {
            $$1 = $$1.move(0.0, 0.0, $$5);
        }
        if ($$3 != 0.0) {
            $$3 = Shapes.collide(Direction.Axis.X, $$1, $$2, $$3);
            if (!$$6 && $$3 != 0.0) {
                $$1 = $$1.move($$3, 0.0, 0.0);
            }
        }
        if (!$$6 && $$5 != 0.0) {
            $$5 = Shapes.collide(Direction.Axis.Z, $$1, $$2, $$5);
        }
        return new Vec3($$3, $$4, $$5);
    }

    protected float nextStep() {
        return (int)this.moveDist + 1;
    }

    protected SoundEvent getSwimSound() {
        return SoundEvents.GENERIC_SWIM;
    }

    protected SoundEvent getSwimSplashSound() {
        return SoundEvents.GENERIC_SPLASH;
    }

    protected SoundEvent getSwimHighSpeedSplashSound() {
        return SoundEvents.GENERIC_SPLASH;
    }

    protected void checkInsideBlocks() {
        AABB $$0 = this.getBoundingBox();
        BlockPos $$1 = new BlockPos($$0.minX + 1.0E-7, $$0.minY + 1.0E-7, $$0.minZ + 1.0E-7);
        BlockPos $$2 = new BlockPos($$0.maxX - 1.0E-7, $$0.maxY - 1.0E-7, $$0.maxZ - 1.0E-7);
        if (this.level.hasChunksAt($$1, $$2)) {
            BlockPos.MutableBlockPos $$3 = new BlockPos.MutableBlockPos();
            for (int $$4 = $$1.getX(); $$4 <= $$2.getX(); ++$$4) {
                for (int $$5 = $$1.getY(); $$5 <= $$2.getY(); ++$$5) {
                    for (int $$6 = $$1.getZ(); $$6 <= $$2.getZ(); ++$$6) {
                        $$3.set($$4, $$5, $$6);
                        BlockState $$7 = this.level.getBlockState($$3);
                        try {
                            $$7.entityInside(this.level, $$3, this);
                            this.onInsideBlock($$7);
                            continue;
                        }
                        catch (Throwable $$8) {
                            CrashReport $$9 = CrashReport.forThrowable($$8, "Colliding entity with block");
                            CrashReportCategory $$10 = $$9.addCategory("Block being collided with");
                            CrashReportCategory.populateBlockDetails($$10, this.level, $$3, $$7);
                            throw new ReportedException($$9);
                        }
                    }
                }
            }
        }
    }

    protected void onInsideBlock(BlockState $$0) {
    }

    public void gameEvent(GameEvent $$0, @Nullable Entity $$1) {
        this.level.gameEvent($$1, $$0, this.position);
    }

    public void gameEvent(GameEvent $$0) {
        this.gameEvent($$0, this);
    }

    protected void playStepSound(BlockPos $$0, BlockState $$1) {
        BlockState $$2 = this.level.getBlockState((BlockPos)$$0.above());
        boolean $$3 = $$2.is(BlockTags.INSIDE_STEP_SOUND_BLOCKS);
        if (!$$3 && $$1.getMaterial().isLiquid()) {
            return;
        }
        SoundType $$4 = $$3 ? $$2.getSoundType() : $$1.getSoundType();
        this.playSound($$4.getStepSound(), $$4.getVolume() * 0.15f, $$4.getPitch());
    }

    private void playAmethystStepSound(BlockState $$0) {
        if ($$0.is(BlockTags.CRYSTAL_SOUND_BLOCKS) && this.tickCount >= this.lastCrystalSoundPlayTick + 20) {
            this.crystalSoundIntensity *= (float)Math.pow((double)0.997, (double)(this.tickCount - this.lastCrystalSoundPlayTick));
            this.crystalSoundIntensity = Math.min((float)1.0f, (float)(this.crystalSoundIntensity + 0.07f));
            float $$1 = 0.5f + this.crystalSoundIntensity * this.random.nextFloat() * 1.2f;
            float $$2 = 0.1f + this.crystalSoundIntensity * 1.2f;
            this.playSound(SoundEvents.AMETHYST_BLOCK_CHIME, $$2, $$1);
            this.lastCrystalSoundPlayTick = this.tickCount;
        }
    }

    protected void playSwimSound(float $$0) {
        this.playSound(this.getSwimSound(), $$0, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.4f);
    }

    protected void onFlap() {
    }

    protected boolean isFlapping() {
        return false;
    }

    public void playSound(SoundEvent $$0, float $$1, float $$2) {
        if (!this.isSilent()) {
            this.level.playSound(null, this.getX(), this.getY(), this.getZ(), $$0, this.getSoundSource(), $$1, $$2);
        }
    }

    public void playSound(SoundEvent $$0) {
        if (!this.isSilent()) {
            this.playSound($$0, 1.0f, 1.0f);
        }
    }

    public boolean isSilent() {
        return this.entityData.get(DATA_SILENT);
    }

    public void setSilent(boolean $$0) {
        this.entityData.set(DATA_SILENT, $$0);
    }

    public boolean isNoGravity() {
        return this.entityData.get(DATA_NO_GRAVITY);
    }

    public void setNoGravity(boolean $$0) {
        this.entityData.set(DATA_NO_GRAVITY, $$0);
    }

    protected MovementEmission getMovementEmission() {
        return MovementEmission.ALL;
    }

    public boolean dampensVibrations() {
        return false;
    }

    protected void checkFallDamage(double $$0, boolean $$1, BlockState $$2, BlockPos $$3) {
        if ($$1) {
            if (this.fallDistance > 0.0f) {
                $$2.getBlock().fallOn(this.level, $$2, $$3, this, this.fallDistance);
                this.level.gameEvent(GameEvent.HIT_GROUND, this.position, GameEvent.Context.of(this, this.getBlockStateOn()));
            }
            this.resetFallDistance();
        } else if ($$0 < 0.0) {
            this.fallDistance -= (float)$$0;
        }
    }

    public boolean fireImmune() {
        return this.getType().fireImmune();
    }

    public boolean causeFallDamage(float $$0, float $$1, DamageSource $$2) {
        if (this.isVehicle()) {
            for (Entity $$3 : this.getPassengers()) {
                $$3.causeFallDamage($$0, $$1, $$2);
            }
        }
        return false;
    }

    public boolean isInWater() {
        return this.wasTouchingWater;
    }

    private boolean isInRain() {
        BlockPos $$0 = this.blockPosition();
        return this.level.isRainingAt($$0) || this.level.isRainingAt(new BlockPos((double)$$0.getX(), this.getBoundingBox().maxY, (double)$$0.getZ()));
    }

    private boolean isInBubbleColumn() {
        return this.level.getBlockState(this.blockPosition()).is(Blocks.BUBBLE_COLUMN);
    }

    public boolean isInWaterOrRain() {
        return this.isInWater() || this.isInRain();
    }

    public boolean isInWaterRainOrBubble() {
        return this.isInWater() || this.isInRain() || this.isInBubbleColumn();
    }

    public boolean isInWaterOrBubble() {
        return this.isInWater() || this.isInBubbleColumn();
    }

    public boolean isUnderWater() {
        return this.wasEyeInWater && this.isInWater();
    }

    public void updateSwimming() {
        if (this.isSwimming()) {
            this.setSwimming(this.isSprinting() && this.isInWater() && !this.isPassenger());
        } else {
            this.setSwimming(this.isSprinting() && this.isUnderWater() && !this.isPassenger() && this.level.getFluidState(this.blockPosition).is(FluidTags.WATER));
        }
    }

    protected boolean updateInWaterStateAndDoFluidPushing() {
        this.fluidHeight.clear();
        this.updateInWaterStateAndDoWaterCurrentPushing();
        double $$0 = this.level.dimensionType().ultraWarm() ? 0.007 : 0.0023333333333333335;
        boolean $$1 = this.updateFluidHeightAndDoFluidPushing(FluidTags.LAVA, $$0);
        return this.isInWater() || $$1;
    }

    void updateInWaterStateAndDoWaterCurrentPushing() {
        Boat $$0;
        Entity entity = this.getVehicle();
        if (entity instanceof Boat && !($$0 = (Boat)entity).isUnderWater()) {
            this.wasTouchingWater = false;
        } else if (this.updateFluidHeightAndDoFluidPushing(FluidTags.WATER, 0.014)) {
            if (!this.wasTouchingWater && !this.firstTick) {
                this.doWaterSplashEffect();
            }
            this.resetFallDistance();
            this.wasTouchingWater = true;
            this.clearFire();
        } else {
            this.wasTouchingWater = false;
        }
    }

    private void updateFluidOnEyes() {
        Boat $$2;
        this.wasEyeInWater = this.isEyeInFluid(FluidTags.WATER);
        this.fluidOnEyes.clear();
        double $$0 = this.getEyeY() - 0.1111111119389534;
        Entity $$1 = this.getVehicle();
        if ($$1 instanceof Boat && !($$2 = (Boat)$$1).isUnderWater() && $$2.getBoundingBox().maxY >= $$0 && $$2.getBoundingBox().minY <= $$0) {
            return;
        }
        BlockPos $$3 = new BlockPos(this.getX(), $$0, this.getZ());
        FluidState $$4 = this.level.getFluidState($$3);
        double $$5 = (float)$$3.getY() + $$4.getHeight(this.level, $$3);
        if ($$5 > $$0) {
            $$4.getTags().forEach(arg_0 -> this.fluidOnEyes.add(arg_0));
        }
    }

    protected void doWaterSplashEffect() {
        Entity $$0 = this.isVehicle() && this.getControllingPassenger() != null ? this.getControllingPassenger() : this;
        float $$1 = $$0 == this ? 0.2f : 0.9f;
        Vec3 $$2 = $$0.getDeltaMovement();
        float $$3 = Math.min((float)1.0f, (float)((float)Math.sqrt((double)($$2.x * $$2.x * (double)0.2f + $$2.y * $$2.y + $$2.z * $$2.z * (double)0.2f)) * $$1));
        if ($$3 < 0.25f) {
            this.playSound(this.getSwimSplashSound(), $$3, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.4f);
        } else {
            this.playSound(this.getSwimHighSpeedSplashSound(), $$3, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.4f);
        }
        float $$4 = Mth.floor(this.getY());
        int $$5 = 0;
        while ((float)$$5 < 1.0f + this.dimensions.width * 20.0f) {
            double $$6 = (this.random.nextDouble() * 2.0 - 1.0) * (double)this.dimensions.width;
            double $$7 = (this.random.nextDouble() * 2.0 - 1.0) * (double)this.dimensions.width;
            this.level.addParticle(ParticleTypes.BUBBLE, this.getX() + $$6, $$4 + 1.0f, this.getZ() + $$7, $$2.x, $$2.y - this.random.nextDouble() * (double)0.2f, $$2.z);
            ++$$5;
        }
        int $$8 = 0;
        while ((float)$$8 < 1.0f + this.dimensions.width * 20.0f) {
            double $$9 = (this.random.nextDouble() * 2.0 - 1.0) * (double)this.dimensions.width;
            double $$10 = (this.random.nextDouble() * 2.0 - 1.0) * (double)this.dimensions.width;
            this.level.addParticle(ParticleTypes.SPLASH, this.getX() + $$9, $$4 + 1.0f, this.getZ() + $$10, $$2.x, $$2.y, $$2.z);
            ++$$8;
        }
        this.gameEvent(GameEvent.SPLASH);
    }

    @Deprecated
    protected BlockState getBlockStateOnLegacy() {
        return this.level.getBlockState(this.getOnPosLegacy());
    }

    public BlockState getBlockStateOn() {
        return this.level.getBlockState(this.getOnPos());
    }

    public boolean canSpawnSprintParticle() {
        return this.isSprinting() && !this.isInWater() && !this.isSpectator() && !this.isCrouching() && !this.isInLava() && this.isAlive();
    }

    protected void spawnSprintParticle() {
        int $$2;
        int $$1;
        int $$0 = Mth.floor(this.getX());
        BlockPos $$3 = new BlockPos($$0, $$1 = Mth.floor(this.getY() - (double)0.2f), $$2 = Mth.floor(this.getZ()));
        BlockState $$4 = this.level.getBlockState($$3);
        if ($$4.getRenderShape() != RenderShape.INVISIBLE) {
            Vec3 $$5 = this.getDeltaMovement();
            this.level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, $$4), this.getX() + (this.random.nextDouble() - 0.5) * (double)this.dimensions.width, this.getY() + 0.1, this.getZ() + (this.random.nextDouble() - 0.5) * (double)this.dimensions.width, $$5.x * -4.0, 1.5, $$5.z * -4.0);
        }
    }

    public boolean isEyeInFluid(TagKey<Fluid> $$0) {
        return this.fluidOnEyes.contains($$0);
    }

    public boolean isInLava() {
        return !this.firstTick && this.fluidHeight.getDouble(FluidTags.LAVA) > 0.0;
    }

    public void moveRelative(float $$0, Vec3 $$1) {
        Vec3 $$2 = Entity.getInputVector($$1, $$0, this.getYRot());
        this.setDeltaMovement(this.getDeltaMovement().add($$2));
    }

    private static Vec3 getInputVector(Vec3 $$0, float $$1, float $$2) {
        double $$3 = $$0.lengthSqr();
        if ($$3 < 1.0E-7) {
            return Vec3.ZERO;
        }
        Vec3 $$4 = ($$3 > 1.0 ? $$0.normalize() : $$0).scale($$1);
        float $$5 = Mth.sin($$2 * ((float)Math.PI / 180));
        float $$6 = Mth.cos($$2 * ((float)Math.PI / 180));
        return new Vec3($$4.x * (double)$$6 - $$4.z * (double)$$5, $$4.y, $$4.z * (double)$$6 + $$4.x * (double)$$5);
    }

    @Deprecated
    public float getLightLevelDependentMagicValue() {
        if (this.level.hasChunkAt(this.getBlockX(), this.getBlockZ())) {
            return this.level.getLightLevelDependentMagicValue(new BlockPos(this.getX(), this.getEyeY(), this.getZ()));
        }
        return 0.0f;
    }

    public void absMoveTo(double $$0, double $$1, double $$2, float $$3, float $$4) {
        this.absMoveTo($$0, $$1, $$2);
        this.setYRot($$3 % 360.0f);
        this.setXRot(Mth.clamp($$4, -90.0f, 90.0f) % 360.0f);
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    public void absMoveTo(double $$0, double $$1, double $$2) {
        double $$3 = Mth.clamp($$0, -3.0E7, 3.0E7);
        double $$4 = Mth.clamp($$2, -3.0E7, 3.0E7);
        this.xo = $$3;
        this.yo = $$1;
        this.zo = $$4;
        this.setPos($$3, $$1, $$4);
    }

    public void moveTo(Vec3 $$0) {
        this.moveTo($$0.x, $$0.y, $$0.z);
    }

    public void moveTo(double $$0, double $$1, double $$2) {
        this.moveTo($$0, $$1, $$2, this.getYRot(), this.getXRot());
    }

    public void moveTo(BlockPos $$0, float $$1, float $$2) {
        this.moveTo((double)$$0.getX() + 0.5, $$0.getY(), (double)$$0.getZ() + 0.5, $$1, $$2);
    }

    public void moveTo(double $$0, double $$1, double $$2, float $$3, float $$4) {
        this.setPosRaw($$0, $$1, $$2);
        this.setYRot($$3);
        this.setXRot($$4);
        this.setOldPosAndRot();
        this.reapplyPosition();
    }

    public final void setOldPosAndRot() {
        double $$0 = this.getX();
        double $$1 = this.getY();
        double $$2 = this.getZ();
        this.xo = $$0;
        this.yo = $$1;
        this.zo = $$2;
        this.xOld = $$0;
        this.yOld = $$1;
        this.zOld = $$2;
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    public float distanceTo(Entity $$0) {
        float $$1 = (float)(this.getX() - $$0.getX());
        float $$2 = (float)(this.getY() - $$0.getY());
        float $$3 = (float)(this.getZ() - $$0.getZ());
        return Mth.sqrt($$1 * $$1 + $$2 * $$2 + $$3 * $$3);
    }

    public double distanceToSqr(double $$0, double $$1, double $$2) {
        double $$3 = this.getX() - $$0;
        double $$4 = this.getY() - $$1;
        double $$5 = this.getZ() - $$2;
        return $$3 * $$3 + $$4 * $$4 + $$5 * $$5;
    }

    public double distanceToSqr(Entity $$0) {
        return this.distanceToSqr($$0.position());
    }

    public double distanceToSqr(Vec3 $$0) {
        double $$1 = this.getX() - $$0.x;
        double $$2 = this.getY() - $$0.y;
        double $$3 = this.getZ() - $$0.z;
        return $$1 * $$1 + $$2 * $$2 + $$3 * $$3;
    }

    public void playerTouch(Player $$0) {
    }

    public void push(Entity $$0) {
        double $$2;
        if (this.isPassengerOfSameVehicle($$0)) {
            return;
        }
        if ($$0.noPhysics || this.noPhysics) {
            return;
        }
        double $$1 = $$0.getX() - this.getX();
        double $$3 = Mth.absMax($$1, $$2 = $$0.getZ() - this.getZ());
        if ($$3 >= (double)0.01f) {
            $$3 = Math.sqrt((double)$$3);
            $$1 /= $$3;
            $$2 /= $$3;
            double $$4 = 1.0 / $$3;
            if ($$4 > 1.0) {
                $$4 = 1.0;
            }
            $$1 *= $$4;
            $$2 *= $$4;
            $$1 *= (double)0.05f;
            $$2 *= (double)0.05f;
            if (!this.isVehicle() && this.isPushable()) {
                this.push(-$$1, 0.0, -$$2);
            }
            if (!$$0.isVehicle() && $$0.isPushable()) {
                $$0.push($$1, 0.0, $$2);
            }
        }
    }

    public void push(double $$0, double $$1, double $$2) {
        this.setDeltaMovement(this.getDeltaMovement().add($$0, $$1, $$2));
        this.hasImpulse = true;
    }

    protected void markHurt() {
        this.hurtMarked = true;
    }

    public boolean hurt(DamageSource $$0, float $$1) {
        if (this.isInvulnerableTo($$0)) {
            return false;
        }
        this.markHurt();
        return false;
    }

    public final Vec3 getViewVector(float $$0) {
        return this.calculateViewVector(this.getViewXRot($$0), this.getViewYRot($$0));
    }

    public float getViewXRot(float $$0) {
        if ($$0 == 1.0f) {
            return this.getXRot();
        }
        return Mth.lerp($$0, this.xRotO, this.getXRot());
    }

    public float getViewYRot(float $$0) {
        if ($$0 == 1.0f) {
            return this.getYRot();
        }
        return Mth.lerp($$0, this.yRotO, this.getYRot());
    }

    protected final Vec3 calculateViewVector(float $$0, float $$1) {
        float $$2 = $$0 * ((float)Math.PI / 180);
        float $$3 = -$$1 * ((float)Math.PI / 180);
        float $$4 = Mth.cos($$3);
        float $$5 = Mth.sin($$3);
        float $$6 = Mth.cos($$2);
        float $$7 = Mth.sin($$2);
        return new Vec3($$5 * $$6, -$$7, $$4 * $$6);
    }

    public final Vec3 getUpVector(float $$0) {
        return this.calculateUpVector(this.getViewXRot($$0), this.getViewYRot($$0));
    }

    protected final Vec3 calculateUpVector(float $$0, float $$1) {
        return this.calculateViewVector($$0 - 90.0f, $$1);
    }

    public final Vec3 getEyePosition() {
        return new Vec3(this.getX(), this.getEyeY(), this.getZ());
    }

    public final Vec3 getEyePosition(float $$0) {
        double $$1 = Mth.lerp((double)$$0, this.xo, this.getX());
        double $$2 = Mth.lerp((double)$$0, this.yo, this.getY()) + (double)this.getEyeHeight();
        double $$3 = Mth.lerp((double)$$0, this.zo, this.getZ());
        return new Vec3($$1, $$2, $$3);
    }

    public Vec3 getLightProbePosition(float $$0) {
        return this.getEyePosition($$0);
    }

    public final Vec3 getPosition(float $$0) {
        double $$1 = Mth.lerp((double)$$0, this.xo, this.getX());
        double $$2 = Mth.lerp((double)$$0, this.yo, this.getY());
        double $$3 = Mth.lerp((double)$$0, this.zo, this.getZ());
        return new Vec3($$1, $$2, $$3);
    }

    public HitResult pick(double $$0, float $$1, boolean $$2) {
        Vec3 $$3 = this.getEyePosition($$1);
        Vec3 $$4 = this.getViewVector($$1);
        Vec3 $$5 = $$3.add($$4.x * $$0, $$4.y * $$0, $$4.z * $$0);
        return this.level.clip(new ClipContext($$3, $$5, ClipContext.Block.OUTLINE, $$2 ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE, this));
    }

    public boolean isPickable() {
        return false;
    }

    public boolean isPushable() {
        return false;
    }

    public void awardKillScore(Entity $$0, int $$1, DamageSource $$2) {
        if ($$0 instanceof ServerPlayer) {
            CriteriaTriggers.ENTITY_KILLED_PLAYER.trigger((ServerPlayer)$$0, this, $$2);
        }
    }

    public boolean shouldRender(double $$0, double $$1, double $$2) {
        double $$3 = this.getX() - $$0;
        double $$4 = this.getY() - $$1;
        double $$5 = this.getZ() - $$2;
        double $$6 = $$3 * $$3 + $$4 * $$4 + $$5 * $$5;
        return this.shouldRenderAtSqrDistance($$6);
    }

    public boolean shouldRenderAtSqrDistance(double $$0) {
        double $$1 = this.getBoundingBox().getSize();
        if (Double.isNaN((double)$$1)) {
            $$1 = 1.0;
        }
        return $$0 < ($$1 *= 64.0 * viewScale) * $$1;
    }

    public boolean saveAsPassenger(CompoundTag $$0) {
        if (this.removalReason != null && !this.removalReason.shouldSave()) {
            return false;
        }
        String $$1 = this.getEncodeId();
        if ($$1 == null) {
            return false;
        }
        $$0.putString(ID_TAG, $$1);
        this.saveWithoutId($$0);
        return true;
    }

    public boolean save(CompoundTag $$0) {
        if (this.isPassenger()) {
            return false;
        }
        return this.saveAsPassenger($$0);
    }

    public CompoundTag saveWithoutId(CompoundTag $$0) {
        try {
            int $$3;
            if (this.vehicle != null) {
                $$0.put("Pos", this.newDoubleList(this.vehicle.getX(), this.getY(), this.vehicle.getZ()));
            } else {
                $$0.put("Pos", this.newDoubleList(this.getX(), this.getY(), this.getZ()));
            }
            Vec3 $$1 = this.getDeltaMovement();
            $$0.put("Motion", this.newDoubleList($$1.x, $$1.y, $$1.z));
            $$0.put("Rotation", this.newFloatList(this.getYRot(), this.getXRot()));
            $$0.putFloat("FallDistance", this.fallDistance);
            $$0.putShort("Fire", (short)this.remainingFireTicks);
            $$0.putShort("Air", (short)this.getAirSupply());
            $$0.putBoolean("OnGround", this.onGround);
            $$0.putBoolean("Invulnerable", this.invulnerable);
            $$0.putInt("PortalCooldown", this.portalCooldown);
            $$0.putUUID(UUID_TAG, this.getUUID());
            Component $$2 = this.getCustomName();
            if ($$2 != null) {
                $$0.putString("CustomName", Component.Serializer.toJson($$2));
            }
            if (this.isCustomNameVisible()) {
                $$0.putBoolean("CustomNameVisible", this.isCustomNameVisible());
            }
            if (this.isSilent()) {
                $$0.putBoolean("Silent", this.isSilent());
            }
            if (this.isNoGravity()) {
                $$0.putBoolean("NoGravity", this.isNoGravity());
            }
            if (this.hasGlowingTag) {
                $$0.putBoolean("Glowing", true);
            }
            if (($$3 = this.getTicksFrozen()) > 0) {
                $$0.putInt("TicksFrozen", this.getTicksFrozen());
            }
            if (this.hasVisualFire) {
                $$0.putBoolean("HasVisualFire", this.hasVisualFire);
            }
            if (!this.tags.isEmpty()) {
                ListTag $$4 = new ListTag();
                for (String $$5 : this.tags) {
                    $$4.add(StringTag.valueOf($$5));
                }
                $$0.put("Tags", $$4);
            }
            this.addAdditionalSaveData($$0);
            if (this.isVehicle()) {
                ListTag $$6 = new ListTag();
                for (Entity $$7 : this.getPassengers()) {
                    CompoundTag $$8;
                    if (!$$7.saveAsPassenger($$8 = new CompoundTag())) continue;
                    $$6.add($$8);
                }
                if (!$$6.isEmpty()) {
                    $$0.put(PASSENGERS_TAG, $$6);
                }
            }
        }
        catch (Throwable $$9) {
            CrashReport $$10 = CrashReport.forThrowable($$9, "Saving entity NBT");
            CrashReportCategory $$11 = $$10.addCategory("Entity being saved");
            this.fillCrashReportCategory($$11);
            throw new ReportedException($$10);
        }
        return $$0;
    }

    public void load(CompoundTag $$0) {
        try {
            ListTag $$1 = $$0.getList("Pos", 6);
            ListTag $$2 = $$0.getList("Motion", 6);
            ListTag $$3 = $$0.getList("Rotation", 5);
            double $$4 = $$2.getDouble(0);
            double $$5 = $$2.getDouble(1);
            double $$6 = $$2.getDouble(2);
            this.setDeltaMovement(Math.abs((double)$$4) > 10.0 ? 0.0 : $$4, Math.abs((double)$$5) > 10.0 ? 0.0 : $$5, Math.abs((double)$$6) > 10.0 ? 0.0 : $$6);
            double $$7 = 3.0000512E7;
            this.setPosRaw(Mth.clamp($$1.getDouble(0), -3.0000512E7, 3.0000512E7), Mth.clamp($$1.getDouble(1), -2.0E7, 2.0E7), Mth.clamp($$1.getDouble(2), -3.0000512E7, 3.0000512E7));
            this.setYRot($$3.getFloat(0));
            this.setXRot($$3.getFloat(1));
            this.setOldPosAndRot();
            this.setYHeadRot(this.getYRot());
            this.setYBodyRot(this.getYRot());
            this.fallDistance = $$0.getFloat("FallDistance");
            this.remainingFireTicks = $$0.getShort("Fire");
            if ($$0.contains("Air")) {
                this.setAirSupply($$0.getShort("Air"));
            }
            this.onGround = $$0.getBoolean("OnGround");
            this.invulnerable = $$0.getBoolean("Invulnerable");
            this.portalCooldown = $$0.getInt("PortalCooldown");
            if ($$0.hasUUID(UUID_TAG)) {
                this.uuid = $$0.getUUID(UUID_TAG);
                this.stringUUID = this.uuid.toString();
            }
            if (!(Double.isFinite((double)this.getX()) && Double.isFinite((double)this.getY()) && Double.isFinite((double)this.getZ()))) {
                throw new IllegalStateException("Entity has invalid position");
            }
            if (!Double.isFinite((double)this.getYRot()) || !Double.isFinite((double)this.getXRot())) {
                throw new IllegalStateException("Entity has invalid rotation");
            }
            this.reapplyPosition();
            this.setRot(this.getYRot(), this.getXRot());
            if ($$0.contains("CustomName", 8)) {
                String $$8 = $$0.getString("CustomName");
                try {
                    this.setCustomName(Component.Serializer.fromJson($$8));
                }
                catch (Exception $$9) {
                    LOGGER.warn("Failed to parse entity custom name {}", (Object)$$8, (Object)$$9);
                }
            }
            this.setCustomNameVisible($$0.getBoolean("CustomNameVisible"));
            this.setSilent($$0.getBoolean("Silent"));
            this.setNoGravity($$0.getBoolean("NoGravity"));
            this.setGlowingTag($$0.getBoolean("Glowing"));
            this.setTicksFrozen($$0.getInt("TicksFrozen"));
            this.hasVisualFire = $$0.getBoolean("HasVisualFire");
            if ($$0.contains("Tags", 9)) {
                this.tags.clear();
                ListTag $$10 = $$0.getList("Tags", 8);
                int $$11 = Math.min((int)$$10.size(), (int)1024);
                for (int $$12 = 0; $$12 < $$11; ++$$12) {
                    this.tags.add((Object)$$10.getString($$12));
                }
            }
            this.readAdditionalSaveData($$0);
            if (this.repositionEntityAfterLoad()) {
                this.reapplyPosition();
            }
        }
        catch (Throwable $$13) {
            CrashReport $$14 = CrashReport.forThrowable($$13, "Loading entity NBT");
            CrashReportCategory $$15 = $$14.addCategory("Entity being loaded");
            this.fillCrashReportCategory($$15);
            throw new ReportedException($$14);
        }
    }

    protected boolean repositionEntityAfterLoad() {
        return true;
    }

    @Nullable
    protected final String getEncodeId() {
        EntityType<?> $$0 = this.getType();
        ResourceLocation $$1 = EntityType.getKey($$0);
        return !$$0.canSerialize() || $$1 == null ? null : $$1.toString();
    }

    protected abstract void readAdditionalSaveData(CompoundTag var1);

    protected abstract void addAdditionalSaveData(CompoundTag var1);

    protected ListTag newDoubleList(double ... $$0) {
        ListTag $$1 = new ListTag();
        for (double $$2 : $$0) {
            $$1.add(DoubleTag.valueOf($$2));
        }
        return $$1;
    }

    protected ListTag newFloatList(float ... $$0) {
        ListTag $$1 = new ListTag();
        for (float $$2 : $$0) {
            $$1.add(FloatTag.valueOf($$2));
        }
        return $$1;
    }

    @Nullable
    public ItemEntity spawnAtLocation(ItemLike $$0) {
        return this.spawnAtLocation($$0, 0);
    }

    @Nullable
    public ItemEntity spawnAtLocation(ItemLike $$0, int $$1) {
        return this.spawnAtLocation(new ItemStack($$0), (float)$$1);
    }

    @Nullable
    public ItemEntity spawnAtLocation(ItemStack $$0) {
        return this.spawnAtLocation($$0, 0.0f);
    }

    @Nullable
    public ItemEntity spawnAtLocation(ItemStack $$0, float $$1) {
        if ($$0.isEmpty()) {
            return null;
        }
        if (this.level.isClientSide) {
            return null;
        }
        ItemEntity $$2 = new ItemEntity(this.level, this.getX(), this.getY() + (double)$$1, this.getZ(), $$0);
        $$2.setDefaultPickUpDelay();
        this.level.addFreshEntity($$2);
        return $$2;
    }

    public boolean isAlive() {
        return !this.isRemoved();
    }

    public boolean isInWall() {
        if (this.noPhysics) {
            return false;
        }
        float $$0 = this.dimensions.width * 0.8f;
        AABB $$12 = AABB.ofSize(this.getEyePosition(), $$0, 1.0E-6, $$0);
        return BlockPos.betweenClosedStream($$12).anyMatch($$1 -> {
            BlockState $$2 = this.level.getBlockState((BlockPos)$$1);
            return !$$2.isAir() && $$2.isSuffocating(this.level, (BlockPos)$$1) && Shapes.joinIsNotEmpty($$2.getCollisionShape(this.level, (BlockPos)$$1).move($$1.getX(), $$1.getY(), $$1.getZ()), Shapes.create($$12), BooleanOp.AND);
        });
    }

    public InteractionResult interact(Player $$0, InteractionHand $$1) {
        return InteractionResult.PASS;
    }

    public boolean canCollideWith(Entity $$0) {
        return $$0.canBeCollidedWith() && !this.isPassengerOfSameVehicle($$0);
    }

    public boolean canBeCollidedWith() {
        return false;
    }

    public void rideTick() {
        this.setDeltaMovement(Vec3.ZERO);
        this.tick();
        if (!this.isPassenger()) {
            return;
        }
        this.getVehicle().positionRider(this);
    }

    public void positionRider(Entity $$0) {
        this.positionRider($$0, Entity::setPos);
    }

    private void positionRider(Entity $$0, MoveFunction $$1) {
        if (!this.hasPassenger($$0)) {
            return;
        }
        double $$2 = this.getY() + this.getPassengersRidingOffset() + $$0.getMyRidingOffset();
        $$1.accept($$0, this.getX(), $$2, this.getZ());
    }

    public void onPassengerTurned(Entity $$0) {
    }

    public double getMyRidingOffset() {
        return 0.0;
    }

    public double getPassengersRidingOffset() {
        return (double)this.dimensions.height * 0.75;
    }

    public boolean startRiding(Entity $$0) {
        return this.startRiding($$0, false);
    }

    public boolean showVehicleHealth() {
        return this instanceof LivingEntity;
    }

    public boolean startRiding(Entity $$02, boolean $$1) {
        if ($$02 == this.vehicle) {
            return false;
        }
        Entity $$2 = $$02;
        while ($$2.vehicle != null) {
            if ($$2.vehicle == this) {
                return false;
            }
            $$2 = $$2.vehicle;
        }
        if (!($$1 || this.canRide($$02) && $$02.canAddPassenger(this))) {
            return false;
        }
        if (this.isPassenger()) {
            this.stopRiding();
        }
        this.setPose(Pose.STANDING);
        this.vehicle = $$02;
        if (!this.vehicle.addPassenger(this)) {
            this.vehicle = null;
            return false;
        }
        $$02.getIndirectPassengersStream().filter($$0 -> $$0 instanceof ServerPlayer).forEach($$0 -> CriteriaTriggers.START_RIDING_TRIGGER.trigger((ServerPlayer)$$0));
        return true;
    }

    protected boolean canRide(Entity $$0) {
        return !this.isShiftKeyDown() && this.boardingCooldown <= 0;
    }

    protected boolean canEnterPose(Pose $$0) {
        return this.level.noCollision(this, this.getBoundingBoxForPose($$0).deflate(1.0E-7));
    }

    public void ejectPassengers() {
        for (int $$0 = this.passengers.size() - 1; $$0 >= 0; --$$0) {
            ((Entity)this.passengers.get($$0)).stopRiding();
        }
    }

    public void removeVehicle() {
        if (this.vehicle != null) {
            Entity $$0 = this.vehicle;
            this.vehicle = null;
            $$0.removePassenger(this);
        }
    }

    public void stopRiding() {
        this.removeVehicle();
    }

    protected boolean addPassenger(Entity $$0) {
        if ($$0.getVehicle() != this) {
            throw new IllegalStateException("Use x.startRiding(y), not y.addPassenger(x)");
        }
        if (this.passengers.isEmpty()) {
            this.passengers = ImmutableList.of((Object)$$0);
        } else {
            ArrayList $$1 = Lists.newArrayList(this.passengers);
            if (!this.level.isClientSide && $$0 instanceof Player && !(this.getFirstPassenger() instanceof Player)) {
                $$1.add(0, (Object)$$0);
            } else {
                $$1.add((Object)$$0);
            }
            this.passengers = ImmutableList.copyOf((Collection)$$1);
        }
        return true;
    }

    protected void removePassenger(Entity $$0) {
        if ($$0.getVehicle() == this) {
            throw new IllegalStateException("Use x.stopRiding(y), not y.removePassenger(x)");
        }
        this.passengers = this.passengers.size() == 1 && this.passengers.get(0) == $$0 ? ImmutableList.of() : (ImmutableList)this.passengers.stream().filter($$1 -> $$1 != $$0).collect(ImmutableList.toImmutableList());
        $$0.boardingCooldown = 60;
    }

    protected boolean canAddPassenger(Entity $$0) {
        return this.passengers.isEmpty();
    }

    public void lerpTo(double $$0, double $$1, double $$2, float $$3, float $$4, int $$5, boolean $$6) {
        this.setPos($$0, $$1, $$2);
        this.setRot($$3, $$4);
    }

    public void lerpHeadTo(float $$0, int $$1) {
        this.setYHeadRot($$0);
    }

    public float getPickRadius() {
        return 0.0f;
    }

    public Vec3 getLookAngle() {
        return this.calculateViewVector(this.getXRot(), this.getYRot());
    }

    public Vec3 getHandHoldingItemAngle(Item $$0) {
        Entity entity = this;
        if (entity instanceof Player) {
            Player $$1 = (Player)entity;
            boolean $$2 = $$1.getOffhandItem().is($$0) && !$$1.getMainHandItem().is($$0);
            HumanoidArm $$3 = $$2 ? $$1.getMainArm().getOpposite() : $$1.getMainArm();
            return this.calculateViewVector(0.0f, this.getYRot() + (float)($$3 == HumanoidArm.RIGHT ? 80 : -80)).scale(0.5);
        }
        return Vec3.ZERO;
    }

    public Vec2 getRotationVector() {
        return new Vec2(this.getXRot(), this.getYRot());
    }

    public Vec3 getForward() {
        return Vec3.directionFromRotation(this.getRotationVector());
    }

    public void handleInsidePortal(BlockPos $$0) {
        if (this.isOnPortalCooldown()) {
            this.setPortalCooldown();
            return;
        }
        if (!this.level.isClientSide && !$$0.equals(this.portalEntrancePos)) {
            this.portalEntrancePos = $$0.immutable();
        }
        this.isInsidePortal = true;
    }

    protected void handleNetherPortal() {
        if (!(this.level instanceof ServerLevel)) {
            return;
        }
        int $$0 = this.getPortalWaitTime();
        ServerLevel $$1 = (ServerLevel)this.level;
        if (this.isInsidePortal) {
            ResourceKey<Level> $$3;
            MinecraftServer $$2 = $$1.getServer();
            ServerLevel $$4 = $$2.getLevel($$3 = this.level.dimension() == Level.NETHER ? Level.OVERWORLD : Level.NETHER);
            if ($$4 != null && $$2.isNetherEnabled() && !this.isPassenger() && this.portalTime++ >= $$0) {
                this.level.getProfiler().push("portal");
                this.portalTime = $$0;
                this.setPortalCooldown();
                this.changeDimension($$4);
                this.level.getProfiler().pop();
            }
            this.isInsidePortal = false;
        } else {
            if (this.portalTime > 0) {
                this.portalTime -= 4;
            }
            if (this.portalTime < 0) {
                this.portalTime = 0;
            }
        }
        this.processPortalCooldown();
    }

    public int getDimensionChangingDelay() {
        return 300;
    }

    public void lerpMotion(double $$0, double $$1, double $$2) {
        this.setDeltaMovement($$0, $$1, $$2);
    }

    public void handleEntityEvent(byte $$0) {
        switch ($$0) {
            case 53: {
                HoneyBlock.showSlideParticles(this);
            }
        }
    }

    public void animateHurt(float $$0) {
    }

    public Iterable<ItemStack> getHandSlots() {
        return EMPTY_LIST;
    }

    public Iterable<ItemStack> getArmorSlots() {
        return EMPTY_LIST;
    }

    public Iterable<ItemStack> getAllSlots() {
        return Iterables.concat(this.getHandSlots(), this.getArmorSlots());
    }

    public void setItemSlot(EquipmentSlot $$0, ItemStack $$1) {
    }

    public boolean isOnFire() {
        boolean $$0 = this.level != null && this.level.isClientSide;
        return !this.fireImmune() && (this.remainingFireTicks > 0 || $$0 && this.getSharedFlag(0));
    }

    public boolean isPassenger() {
        return this.getVehicle() != null;
    }

    public boolean isVehicle() {
        return !this.passengers.isEmpty();
    }

    public boolean rideableUnderWater() {
        return true;
    }

    public void setShiftKeyDown(boolean $$0) {
        this.setSharedFlag(1, $$0);
    }

    public boolean isShiftKeyDown() {
        return this.getSharedFlag(1);
    }

    public boolean isSteppingCarefully() {
        return this.isShiftKeyDown();
    }

    public boolean isSuppressingBounce() {
        return this.isShiftKeyDown();
    }

    public boolean isDiscrete() {
        return this.isShiftKeyDown();
    }

    public boolean isDescending() {
        return this.isShiftKeyDown();
    }

    public boolean isCrouching() {
        return this.hasPose(Pose.CROUCHING);
    }

    public boolean isSprinting() {
        return this.getSharedFlag(3);
    }

    public void setSprinting(boolean $$0) {
        this.setSharedFlag(3, $$0);
    }

    public boolean isSwimming() {
        return this.getSharedFlag(4);
    }

    public boolean isVisuallySwimming() {
        return this.hasPose(Pose.SWIMMING);
    }

    public boolean isVisuallyCrawling() {
        return this.isVisuallySwimming() && !this.isInWater();
    }

    public void setSwimming(boolean $$0) {
        this.setSharedFlag(4, $$0);
    }

    public final boolean hasGlowingTag() {
        return this.hasGlowingTag;
    }

    public final void setGlowingTag(boolean $$0) {
        this.hasGlowingTag = $$0;
        this.setSharedFlag(6, this.isCurrentlyGlowing());
    }

    public boolean isCurrentlyGlowing() {
        if (this.level.isClientSide()) {
            return this.getSharedFlag(6);
        }
        return this.hasGlowingTag;
    }

    public boolean isInvisible() {
        return this.getSharedFlag(5);
    }

    public boolean isInvisibleTo(Player $$0) {
        if ($$0.isSpectator()) {
            return false;
        }
        Team $$1 = this.getTeam();
        if ($$1 != null && $$0 != null && $$0.getTeam() == $$1 && $$1.canSeeFriendlyInvisibles()) {
            return false;
        }
        return this.isInvisible();
    }

    public void updateDynamicGameEventListener(BiConsumer<DynamicGameEventListener<?>, ServerLevel> $$0) {
    }

    @Nullable
    public Team getTeam() {
        return this.level.getScoreboard().getPlayersTeam(this.getScoreboardName());
    }

    public boolean isAlliedTo(Entity $$0) {
        return this.isAlliedTo($$0.getTeam());
    }

    public boolean isAlliedTo(Team $$0) {
        if (this.getTeam() != null) {
            return this.getTeam().isAlliedTo($$0);
        }
        return false;
    }

    public void setInvisible(boolean $$0) {
        this.setSharedFlag(5, $$0);
    }

    protected boolean getSharedFlag(int $$0) {
        return (this.entityData.get(DATA_SHARED_FLAGS_ID) & 1 << $$0) != 0;
    }

    protected void setSharedFlag(int $$0, boolean $$1) {
        byte $$2 = this.entityData.get(DATA_SHARED_FLAGS_ID);
        if ($$1) {
            this.entityData.set(DATA_SHARED_FLAGS_ID, (byte)($$2 | 1 << $$0));
        } else {
            this.entityData.set(DATA_SHARED_FLAGS_ID, (byte)($$2 & ~(1 << $$0)));
        }
    }

    public int getMaxAirSupply() {
        return 300;
    }

    public int getAirSupply() {
        return this.entityData.get(DATA_AIR_SUPPLY_ID);
    }

    public void setAirSupply(int $$0) {
        this.entityData.set(DATA_AIR_SUPPLY_ID, $$0);
    }

    public int getTicksFrozen() {
        return this.entityData.get(DATA_TICKS_FROZEN);
    }

    public void setTicksFrozen(int $$0) {
        this.entityData.set(DATA_TICKS_FROZEN, $$0);
    }

    public float getPercentFrozen() {
        int $$0 = this.getTicksRequiredToFreeze();
        return (float)Math.min((int)this.getTicksFrozen(), (int)$$0) / (float)$$0;
    }

    public boolean isFullyFrozen() {
        return this.getTicksFrozen() >= this.getTicksRequiredToFreeze();
    }

    public int getTicksRequiredToFreeze() {
        return 140;
    }

    public void thunderHit(ServerLevel $$0, LightningBolt $$1) {
        this.setRemainingFireTicks(this.remainingFireTicks + 1);
        if (this.remainingFireTicks == 0) {
            this.setSecondsOnFire(8);
        }
        this.hurt(DamageSource.LIGHTNING_BOLT, 5.0f);
    }

    public void onAboveBubbleCol(boolean $$0) {
        double $$3;
        Vec3 $$1 = this.getDeltaMovement();
        if ($$0) {
            double $$2 = Math.max((double)-0.9, (double)($$1.y - 0.03));
        } else {
            $$3 = Math.min((double)1.8, (double)($$1.y + 0.1));
        }
        this.setDeltaMovement($$1.x, $$3, $$1.z);
    }

    public void onInsideBubbleColumn(boolean $$0) {
        double $$3;
        Vec3 $$1 = this.getDeltaMovement();
        if ($$0) {
            double $$2 = Math.max((double)-0.3, (double)($$1.y - 0.03));
        } else {
            $$3 = Math.min((double)0.7, (double)($$1.y + 0.06));
        }
        this.setDeltaMovement($$1.x, $$3, $$1.z);
        this.resetFallDistance();
    }

    public boolean wasKilled(ServerLevel $$0, LivingEntity $$1) {
        return true;
    }

    public void checkSlowFallDistance() {
        if (this.deltaMovement.y() > -0.5 && this.fallDistance > 1.0f) {
            this.fallDistance = 1.0f;
        }
    }

    public void resetFallDistance() {
        this.fallDistance = 0.0f;
    }

    protected void moveTowardsClosestSpace(double $$0, double $$1, double $$2) {
        BlockPos $$3 = new BlockPos($$0, $$1, $$2);
        Vec3 $$4 = new Vec3($$0 - (double)$$3.getX(), $$1 - (double)$$3.getY(), $$2 - (double)$$3.getZ());
        BlockPos.MutableBlockPos $$5 = new BlockPos.MutableBlockPos();
        Direction $$6 = Direction.UP;
        double $$7 = Double.MAX_VALUE;
        for (Direction $$8 : new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, Direction.UP}) {
            double $$10;
            $$5.setWithOffset((Vec3i)$$3, $$8);
            if (this.level.getBlockState($$5).isCollisionShapeFullBlock(this.level, $$5)) continue;
            double $$9 = $$4.get($$8.getAxis());
            double d = $$10 = $$8.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1.0 - $$9 : $$9;
            if (!($$10 < $$7)) continue;
            $$7 = $$10;
            $$6 = $$8;
        }
        float $$11 = this.random.nextFloat() * 0.2f + 0.1f;
        float $$12 = $$6.getAxisDirection().getStep();
        Vec3 $$13 = this.getDeltaMovement().scale(0.75);
        if ($$6.getAxis() == Direction.Axis.X) {
            this.setDeltaMovement($$12 * $$11, $$13.y, $$13.z);
        } else if ($$6.getAxis() == Direction.Axis.Y) {
            this.setDeltaMovement($$13.x, $$12 * $$11, $$13.z);
        } else if ($$6.getAxis() == Direction.Axis.Z) {
            this.setDeltaMovement($$13.x, $$13.y, $$12 * $$11);
        }
    }

    public void makeStuckInBlock(BlockState $$0, Vec3 $$1) {
        this.resetFallDistance();
        this.stuckSpeedMultiplier = $$1;
    }

    private static Component removeAction(Component $$0) {
        MutableComponent $$1 = $$0.plainCopy().setStyle($$0.getStyle().withClickEvent(null));
        for (Component $$2 : $$0.getSiblings()) {
            $$1.append(Entity.removeAction($$2));
        }
        return $$1;
    }

    @Override
    public Component getName() {
        Component $$0 = this.getCustomName();
        if ($$0 != null) {
            return Entity.removeAction($$0);
        }
        return this.getTypeName();
    }

    protected Component getTypeName() {
        return this.type.getDescription();
    }

    public boolean is(Entity $$0) {
        return this == $$0;
    }

    public float getYHeadRot() {
        return 0.0f;
    }

    public void setYHeadRot(float $$0) {
    }

    public void setYBodyRot(float $$0) {
    }

    public boolean isAttackable() {
        return true;
    }

    public boolean skipAttackInteraction(Entity $$0) {
        return false;
    }

    public String toString() {
        String $$0;
        String string = $$0 = this.level == null ? "~NULL~" : this.level.toString();
        if (this.removalReason != null) {
            return String.format((Locale)Locale.ROOT, (String)"%s['%s'/%d, l='%s', x=%.2f, y=%.2f, z=%.2f, removed=%s]", (Object[])new Object[]{this.getClass().getSimpleName(), this.getName().getString(), this.id, $$0, this.getX(), this.getY(), this.getZ(), this.removalReason});
        }
        return String.format((Locale)Locale.ROOT, (String)"%s['%s'/%d, l='%s', x=%.2f, y=%.2f, z=%.2f]", (Object[])new Object[]{this.getClass().getSimpleName(), this.getName().getString(), this.id, $$0, this.getX(), this.getY(), this.getZ()});
    }

    public boolean isInvulnerableTo(DamageSource $$0) {
        return this.isRemoved() || this.invulnerable && $$0 != DamageSource.OUT_OF_WORLD && !$$0.isCreativePlayer() || $$0.isFire() && this.fireImmune();
    }

    public boolean isInvulnerable() {
        return this.invulnerable;
    }

    public void setInvulnerable(boolean $$0) {
        this.invulnerable = $$0;
    }

    public void copyPosition(Entity $$0) {
        this.moveTo($$0.getX(), $$0.getY(), $$0.getZ(), $$0.getYRot(), $$0.getXRot());
    }

    public void restoreFrom(Entity $$0) {
        CompoundTag $$1 = $$0.saveWithoutId(new CompoundTag());
        $$1.remove("Dimension");
        this.load($$1);
        this.portalCooldown = $$0.portalCooldown;
        this.portalEntrancePos = $$0.portalEntrancePos;
    }

    @Nullable
    public Entity changeDimension(ServerLevel $$0) {
        if (!(this.level instanceof ServerLevel) || this.isRemoved()) {
            return null;
        }
        this.level.getProfiler().push("changeDimension");
        this.unRide();
        this.level.getProfiler().push("reposition");
        PortalInfo $$1 = this.findDimensionEntryPoint($$0);
        if ($$1 == null) {
            return null;
        }
        this.level.getProfiler().popPush("reloading");
        Object $$2 = this.getType().create($$0);
        if ($$2 != null) {
            ((Entity)$$2).restoreFrom(this);
            ((Entity)$$2).moveTo($$1.pos.x, $$1.pos.y, $$1.pos.z, $$1.yRot, ((Entity)$$2).getXRot());
            ((Entity)$$2).setDeltaMovement($$1.speed);
            $$0.addDuringTeleport((Entity)$$2);
            if ($$0.dimension() == Level.END) {
                ServerLevel.makeObsidianPlatform($$0);
            }
        }
        this.removeAfterChangingDimensions();
        this.level.getProfiler().pop();
        ((ServerLevel)this.level).resetEmptyTime();
        $$0.resetEmptyTime();
        this.level.getProfiler().pop();
        return $$2;
    }

    protected void removeAfterChangingDimensions() {
        this.setRemoved(RemovalReason.CHANGED_DIMENSION);
    }

    @Nullable
    protected PortalInfo findDimensionEntryPoint(ServerLevel $$0) {
        boolean $$5;
        boolean $$2;
        boolean $$1 = this.level.dimension() == Level.END && $$0.dimension() == Level.OVERWORLD;
        boolean bl = $$2 = $$0.dimension() == Level.END;
        if ($$1 || $$2) {
            BlockPos $$4;
            if ($$2) {
                BlockPos $$3 = ServerLevel.END_SPAWN_POINT;
            } else {
                $$4 = $$0.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, $$0.getSharedSpawnPos());
            }
            return new PortalInfo(new Vec3((double)$$4.getX() + 0.5, $$4.getY(), (double)$$4.getZ() + 0.5), this.getDeltaMovement(), this.getYRot(), this.getXRot());
        }
        boolean bl2 = $$5 = $$0.dimension() == Level.NETHER;
        if (this.level.dimension() != Level.NETHER && !$$5) {
            return null;
        }
        WorldBorder $$6 = $$0.getWorldBorder();
        double $$7 = DimensionType.getTeleportationScale(this.level.dimensionType(), $$0.dimensionType());
        BlockPos $$8 = $$6.clampToBounds(this.getX() * $$7, this.getY(), this.getZ() * $$7);
        return (PortalInfo)this.getExitPortal($$0, $$8, $$5, $$6).map($$12 -> {
            Vec3 $$7;
            Direction.Axis $$6;
            BlockState $$2 = this.level.getBlockState(this.portalEntrancePos);
            if ($$2.hasProperty(BlockStateProperties.HORIZONTAL_AXIS)) {
                Direction.Axis $$3 = $$2.getValue(BlockStateProperties.HORIZONTAL_AXIS);
                BlockUtil.FoundRectangle $$4 = BlockUtil.getLargestRectangleAround(this.portalEntrancePos, $$3, 21, Direction.Axis.Y, 21, (Predicate<BlockPos>)((Predicate)$$1 -> this.level.getBlockState((BlockPos)$$1) == $$2));
                Vec3 $$5 = this.getRelativePortalPosition($$3, $$4);
            } else {
                $$6 = Direction.Axis.X;
                $$7 = new Vec3(0.5, 0.0, 0.0);
            }
            return PortalShape.createPortalInfo($$0, $$12, $$6, $$7, this, this.getDeltaMovement(), this.getYRot(), this.getXRot());
        }).orElse(null);
    }

    protected Vec3 getRelativePortalPosition(Direction.Axis $$0, BlockUtil.FoundRectangle $$1) {
        return PortalShape.getRelativePosition($$1, $$0, this.position(), this.getDimensions(this.getPose()));
    }

    protected Optional<BlockUtil.FoundRectangle> getExitPortal(ServerLevel $$0, BlockPos $$1, boolean $$2, WorldBorder $$3) {
        return $$0.getPortalForcer().findPortalAround($$1, $$2, $$3);
    }

    public boolean canChangeDimensions() {
        return !this.isPassenger() && !this.isVehicle();
    }

    public float getBlockExplosionResistance(Explosion $$0, BlockGetter $$1, BlockPos $$2, BlockState $$3, FluidState $$4, float $$5) {
        return $$5;
    }

    public boolean shouldBlockExplode(Explosion $$0, BlockGetter $$1, BlockPos $$2, BlockState $$3, float $$4) {
        return true;
    }

    public int getMaxFallDistance() {
        return 3;
    }

    public boolean isIgnoringBlockTriggers() {
        return false;
    }

    public void fillCrashReportCategory(CrashReportCategory $$0) {
        $$0.setDetail("Entity Type", () -> EntityType.getKey(this.getType()) + " (" + this.getClass().getCanonicalName() + ")");
        $$0.setDetail("Entity ID", this.id);
        $$0.setDetail("Entity Name", () -> this.getName().getString());
        $$0.setDetail("Entity's Exact location", String.format((Locale)Locale.ROOT, (String)"%.2f, %.2f, %.2f", (Object[])new Object[]{this.getX(), this.getY(), this.getZ()}));
        $$0.setDetail("Entity's Block location", CrashReportCategory.formatLocation((LevelHeightAccessor)this.level, Mth.floor(this.getX()), Mth.floor(this.getY()), Mth.floor(this.getZ())));
        Vec3 $$1 = this.getDeltaMovement();
        $$0.setDetail("Entity's Momentum", String.format((Locale)Locale.ROOT, (String)"%.2f, %.2f, %.2f", (Object[])new Object[]{$$1.x, $$1.y, $$1.z}));
        $$0.setDetail("Entity's Passengers", () -> this.getPassengers().toString());
        $$0.setDetail("Entity's Vehicle", () -> String.valueOf((Object)this.getVehicle()));
    }

    public boolean displayFireAnimation() {
        return this.isOnFire() && !this.isSpectator();
    }

    public void setUUID(UUID $$0) {
        this.uuid = $$0;
        this.stringUUID = this.uuid.toString();
    }

    @Override
    public UUID getUUID() {
        return this.uuid;
    }

    public String getStringUUID() {
        return this.stringUUID;
    }

    public String getScoreboardName() {
        return this.stringUUID;
    }

    public boolean isPushedByFluid() {
        return true;
    }

    public static double getViewScale() {
        return viewScale;
    }

    public static void setViewScale(double $$0) {
        viewScale = $$0;
    }

    @Override
    public Component getDisplayName() {
        return PlayerTeam.formatNameForTeam(this.getTeam(), this.getName()).withStyle((UnaryOperator<Style>)((UnaryOperator)$$0 -> $$0.withHoverEvent(this.createHoverEvent()).withInsertion(this.getStringUUID())));
    }

    public void setCustomName(@Nullable Component $$0) {
        this.entityData.set(DATA_CUSTOM_NAME, Optional.ofNullable((Object)$$0));
    }

    @Override
    @Nullable
    public Component getCustomName() {
        return (Component)this.entityData.get(DATA_CUSTOM_NAME).orElse(null);
    }

    @Override
    public boolean hasCustomName() {
        return this.entityData.get(DATA_CUSTOM_NAME).isPresent();
    }

    public void setCustomNameVisible(boolean $$0) {
        this.entityData.set(DATA_CUSTOM_NAME_VISIBLE, $$0);
    }

    public boolean isCustomNameVisible() {
        return this.entityData.get(DATA_CUSTOM_NAME_VISIBLE);
    }

    public final void teleportToWithTicket(double $$0, double $$1, double $$2) {
        if (!(this.level instanceof ServerLevel)) {
            return;
        }
        ChunkPos $$3 = new ChunkPos(new BlockPos($$0, $$1, $$2));
        ((ServerLevel)this.level).getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, $$3, 0, this.getId());
        this.level.getChunk($$3.x, $$3.z);
        this.teleportTo($$0, $$1, $$2);
    }

    public boolean teleportTo(ServerLevel $$0, double $$1, double $$2, double $$3, Set<RelativeMovement> $$4, float $$5, float $$6) {
        float $$7 = Mth.clamp($$6, -90.0f, 90.0f);
        if ($$0 == this.level) {
            this.moveTo($$1, $$2, $$3, $$5, $$7);
            this.setYHeadRot($$5);
        } else {
            this.unRide();
            Object $$8 = this.getType().create($$0);
            if ($$8 != null) {
                ((Entity)$$8).restoreFrom(this);
                ((Entity)$$8).moveTo($$1, $$2, $$3, $$5, $$7);
                ((Entity)$$8).setYHeadRot($$5);
                this.setRemoved(RemovalReason.CHANGED_DIMENSION);
                $$0.addDuringTeleport((Entity)$$8);
            } else {
                return false;
            }
        }
        return true;
    }

    public void dismountTo(double $$0, double $$1, double $$2) {
        this.teleportTo($$0, $$1, $$2);
    }

    public void teleportTo(double $$02, double $$1, double $$2) {
        if (!(this.level instanceof ServerLevel)) {
            return;
        }
        this.moveTo($$02, $$1, $$2, this.getYRot(), this.getXRot());
        this.getSelfAndPassengers().forEach($$0 -> {
            for (Entity $$1 : $$0.passengers) {
                $$0.positionRider($$1, Entity::moveTo);
            }
        });
    }

    public void teleportRelative(double $$0, double $$1, double $$2) {
        this.teleportTo(this.getX() + $$0, this.getY() + $$1, this.getZ() + $$2);
    }

    public boolean shouldShowName() {
        return this.isCustomNameVisible();
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> $$0) {
        if (DATA_POSE.equals($$0)) {
            this.refreshDimensions();
        }
    }

    @Deprecated
    protected void fixupDimensions() {
        EntityDimensions $$1;
        Pose $$0 = this.getPose();
        this.dimensions = $$1 = this.getDimensions($$0);
        this.eyeHeight = this.getEyeHeight($$0, $$1);
    }

    public void refreshDimensions() {
        boolean $$3;
        EntityDimensions $$2;
        EntityDimensions $$0 = this.dimensions;
        Pose $$12 = this.getPose();
        this.dimensions = $$2 = this.getDimensions($$12);
        this.eyeHeight = this.getEyeHeight($$12, $$2);
        this.reapplyPosition();
        boolean bl = $$3 = (double)$$2.width <= 4.0 && (double)$$2.height <= 4.0;
        if (!(this.level.isClientSide || this.firstTick || this.noPhysics || !$$3 || !($$2.width > $$0.width) && !($$2.height > $$0.height) || this instanceof Player)) {
            Vec3 $$4 = this.position().add(0.0, (double)$$0.height / 2.0, 0.0);
            double $$5 = (double)Math.max((float)0.0f, (float)($$2.width - $$0.width)) + 1.0E-6;
            double $$6 = (double)Math.max((float)0.0f, (float)($$2.height - $$0.height)) + 1.0E-6;
            VoxelShape $$7 = Shapes.create(AABB.ofSize($$4, $$5, $$6, $$5));
            this.level.findFreePosition(this, $$7, $$4, $$2.width, $$2.height, $$2.width).ifPresent($$1 -> this.setPos($$1.add(0.0, (double)(-$$0.height) / 2.0, 0.0)));
        }
    }

    public Direction getDirection() {
        return Direction.fromYRot(this.getYRot());
    }

    public Direction getMotionDirection() {
        return this.getDirection();
    }

    protected HoverEvent createHoverEvent() {
        return new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new HoverEvent.EntityTooltipInfo(this.getType(), this.getUUID(), this.getName()));
    }

    public boolean broadcastToPlayer(ServerPlayer $$0) {
        return true;
    }

    @Override
    public final AABB getBoundingBox() {
        return this.bb;
    }

    public AABB getBoundingBoxForCulling() {
        return this.getBoundingBox();
    }

    protected AABB getBoundingBoxForPose(Pose $$0) {
        EntityDimensions $$1 = this.getDimensions($$0);
        float $$2 = $$1.width / 2.0f;
        Vec3 $$3 = new Vec3(this.getX() - (double)$$2, this.getY(), this.getZ() - (double)$$2);
        Vec3 $$4 = new Vec3(this.getX() + (double)$$2, this.getY() + (double)$$1.height, this.getZ() + (double)$$2);
        return new AABB($$3, $$4);
    }

    public final void setBoundingBox(AABB $$0) {
        this.bb = $$0;
    }

    protected float getEyeHeight(Pose $$0, EntityDimensions $$1) {
        return $$1.height * 0.85f;
    }

    public float getEyeHeight(Pose $$0) {
        return this.getEyeHeight($$0, this.getDimensions($$0));
    }

    public final float getEyeHeight() {
        return this.eyeHeight;
    }

    public Vec3 getLeashOffset(float $$0) {
        return this.getLeashOffset();
    }

    protected Vec3 getLeashOffset() {
        return new Vec3(0.0, this.getEyeHeight(), this.getBbWidth() * 0.4f);
    }

    public SlotAccess getSlot(int $$0) {
        return SlotAccess.NULL;
    }

    @Override
    public void sendSystemMessage(Component $$0) {
    }

    public Level getCommandSenderWorld() {
        return this.level;
    }

    @Nullable
    public MinecraftServer getServer() {
        return this.level.getServer();
    }

    public InteractionResult interactAt(Player $$0, Vec3 $$1, InteractionHand $$2) {
        return InteractionResult.PASS;
    }

    public boolean ignoreExplosion() {
        return false;
    }

    public void doEnchantDamageEffects(LivingEntity $$0, Entity $$1) {
        if ($$1 instanceof LivingEntity) {
            EnchantmentHelper.doPostHurtEffects((LivingEntity)$$1, $$0);
        }
        EnchantmentHelper.doPostDamageEffects($$0, $$1);
    }

    public void startSeenByPlayer(ServerPlayer $$0) {
    }

    public void stopSeenByPlayer(ServerPlayer $$0) {
    }

    public float rotate(Rotation $$0) {
        float $$1 = Mth.wrapDegrees(this.getYRot());
        switch ($$0) {
            case CLOCKWISE_180: {
                return $$1 + 180.0f;
            }
            case COUNTERCLOCKWISE_90: {
                return $$1 + 270.0f;
            }
            case CLOCKWISE_90: {
                return $$1 + 90.0f;
            }
        }
        return $$1;
    }

    public float mirror(Mirror $$0) {
        float $$1 = Mth.wrapDegrees(this.getYRot());
        switch ($$0) {
            case FRONT_BACK: {
                return -$$1;
            }
            case LEFT_RIGHT: {
                return 180.0f - $$1;
            }
        }
        return $$1;
    }

    public boolean onlyOpCanSetNbt() {
        return false;
    }

    @Nullable
    public Entity getControllingPassenger() {
        return null;
    }

    public final boolean hasControllingPassenger() {
        return this.getControllingPassenger() != null;
    }

    public final List<Entity> getPassengers() {
        return this.passengers;
    }

    @Nullable
    public Entity getFirstPassenger() {
        return this.passengers.isEmpty() ? null : (Entity)this.passengers.get(0);
    }

    public boolean hasPassenger(Entity $$0) {
        return this.passengers.contains((Object)$$0);
    }

    public boolean hasPassenger(Predicate<Entity> $$0) {
        for (Entity $$1 : this.passengers) {
            if (!$$0.test((Object)$$1)) continue;
            return true;
        }
        return false;
    }

    private Stream<Entity> getIndirectPassengersStream() {
        return this.passengers.stream().flatMap(Entity::getSelfAndPassengers);
    }

    public Stream<Entity> getSelfAndPassengers() {
        return Stream.concat((Stream)Stream.of((Object)this), this.getIndirectPassengersStream());
    }

    public Stream<Entity> getPassengersAndSelf() {
        return Stream.concat((Stream)this.passengers.stream().flatMap(Entity::getPassengersAndSelf), (Stream)Stream.of((Object)this));
    }

    public Iterable<Entity> getIndirectPassengers() {
        return () -> this.getIndirectPassengersStream().iterator();
    }

    public boolean hasExactlyOnePlayerPassenger() {
        return this.getIndirectPassengersStream().filter($$0 -> $$0 instanceof Player).count() == 1L;
    }

    public Entity getRootVehicle() {
        Entity $$0 = this;
        while ($$0.isPassenger()) {
            $$0 = $$0.getVehicle();
        }
        return $$0;
    }

    public boolean isPassengerOfSameVehicle(Entity $$0) {
        return this.getRootVehicle() == $$0.getRootVehicle();
    }

    public boolean hasIndirectPassenger(Entity $$0) {
        return this.getIndirectPassengersStream().anyMatch($$1 -> $$1 == $$0);
    }

    public boolean isControlledByLocalInstance() {
        Entity $$0 = this.getControllingPassenger();
        if ($$0 instanceof Player) {
            return ((Player)$$0).isLocalPlayer();
        }
        return !this.level.isClientSide;
    }

    protected static Vec3 getCollisionHorizontalEscapeVector(double $$0, double $$1, float $$2) {
        double $$3 = ($$0 + $$1 + (double)1.0E-5f) / 2.0;
        float $$4 = -Mth.sin($$2 * ((float)Math.PI / 180));
        float $$5 = Mth.cos($$2 * ((float)Math.PI / 180));
        float $$6 = Math.max((float)Math.abs((float)$$4), (float)Math.abs((float)$$5));
        return new Vec3((double)$$4 * $$3 / (double)$$6, 0.0, (double)$$5 * $$3 / (double)$$6);
    }

    public Vec3 getDismountLocationForPassenger(LivingEntity $$0) {
        return new Vec3(this.getX(), this.getBoundingBox().maxY, this.getZ());
    }

    @Nullable
    public Entity getVehicle() {
        return this.vehicle;
    }

    public PushReaction getPistonPushReaction() {
        return PushReaction.NORMAL;
    }

    public SoundSource getSoundSource() {
        return SoundSource.NEUTRAL;
    }

    protected int getFireImmuneTicks() {
        return 1;
    }

    public CommandSourceStack createCommandSourceStack() {
        return new CommandSourceStack(this, this.position(), this.getRotationVector(), this.level instanceof ServerLevel ? (ServerLevel)this.level : null, this.getPermissionLevel(), this.getName().getString(), this.getDisplayName(), this.level.getServer(), this);
    }

    protected int getPermissionLevel() {
        return 0;
    }

    public boolean hasPermissions(int $$0) {
        return this.getPermissionLevel() >= $$0;
    }

    @Override
    public boolean acceptsSuccess() {
        return this.level.getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK);
    }

    @Override
    public boolean acceptsFailure() {
        return true;
    }

    @Override
    public boolean shouldInformAdmins() {
        return true;
    }

    public void lookAt(EntityAnchorArgument.Anchor $$0, Vec3 $$1) {
        Vec3 $$2 = $$0.apply(this);
        double $$3 = $$1.x - $$2.x;
        double $$4 = $$1.y - $$2.y;
        double $$5 = $$1.z - $$2.z;
        double $$6 = Math.sqrt((double)($$3 * $$3 + $$5 * $$5));
        this.setXRot(Mth.wrapDegrees((float)(-(Mth.atan2($$4, $$6) * 57.2957763671875))));
        this.setYRot(Mth.wrapDegrees((float)(Mth.atan2($$5, $$3) * 57.2957763671875) - 90.0f));
        this.setYHeadRot(this.getYRot());
        this.xRotO = this.getXRot();
        this.yRotO = this.getYRot();
    }

    public boolean updateFluidHeightAndDoFluidPushing(TagKey<Fluid> $$0, double $$1) {
        if (this.touchingUnloadedChunk()) {
            return false;
        }
        AABB $$2 = this.getBoundingBox().deflate(0.001);
        int $$3 = Mth.floor($$2.minX);
        int $$4 = Mth.ceil($$2.maxX);
        int $$5 = Mth.floor($$2.minY);
        int $$6 = Mth.ceil($$2.maxY);
        int $$7 = Mth.floor($$2.minZ);
        int $$8 = Mth.ceil($$2.maxZ);
        double $$9 = 0.0;
        boolean $$10 = this.isPushedByFluid();
        boolean $$11 = false;
        Vec3 $$12 = Vec3.ZERO;
        int $$13 = 0;
        BlockPos.MutableBlockPos $$14 = new BlockPos.MutableBlockPos();
        for (int $$15 = $$3; $$15 < $$4; ++$$15) {
            for (int $$16 = $$5; $$16 < $$6; ++$$16) {
                for (int $$17 = $$7; $$17 < $$8; ++$$17) {
                    double $$19;
                    $$14.set($$15, $$16, $$17);
                    FluidState $$18 = this.level.getFluidState($$14);
                    if (!$$18.is($$0) || !(($$19 = (double)((float)$$16 + $$18.getHeight(this.level, $$14))) >= $$2.minY)) continue;
                    $$11 = true;
                    $$9 = Math.max((double)($$19 - $$2.minY), (double)$$9);
                    if (!$$10) continue;
                    Vec3 $$20 = $$18.getFlow(this.level, $$14);
                    if ($$9 < 0.4) {
                        $$20 = $$20.scale($$9);
                    }
                    $$12 = $$12.add($$20);
                    ++$$13;
                }
            }
        }
        if ($$12.length() > 0.0) {
            if ($$13 > 0) {
                $$12 = $$12.scale(1.0 / (double)$$13);
            }
            if (!(this instanceof Player)) {
                $$12 = $$12.normalize();
            }
            Vec3 $$21 = this.getDeltaMovement();
            $$12 = $$12.scale($$1 * 1.0);
            double $$22 = 0.003;
            if (Math.abs((double)$$21.x) < 0.003 && Math.abs((double)$$21.z) < 0.003 && $$12.length() < 0.0045000000000000005) {
                $$12 = $$12.normalize().scale(0.0045000000000000005);
            }
            this.setDeltaMovement(this.getDeltaMovement().add($$12));
        }
        this.fluidHeight.put($$0, $$9);
        return $$11;
    }

    public boolean touchingUnloadedChunk() {
        int $$4;
        AABB $$0 = this.getBoundingBox().inflate(1.0);
        int $$1 = Mth.floor($$0.minX);
        int $$2 = Mth.ceil($$0.maxX);
        int $$3 = Mth.floor($$0.minZ);
        return !this.level.hasChunksAt($$1, $$3, $$2, $$4 = Mth.ceil($$0.maxZ));
    }

    public double getFluidHeight(TagKey<Fluid> $$0) {
        return this.fluidHeight.getDouble($$0);
    }

    public double getFluidJumpThreshold() {
        return (double)this.getEyeHeight() < 0.4 ? 0.0 : 0.4;
    }

    public final float getBbWidth() {
        return this.dimensions.width;
    }

    public final float getBbHeight() {
        return this.dimensions.height;
    }

    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    public EntityDimensions getDimensions(Pose $$0) {
        return this.type.getDimensions();
    }

    public Vec3 position() {
        return this.position;
    }

    public Vec3 trackingPosition() {
        return this.position();
    }

    @Override
    public BlockPos blockPosition() {
        return this.blockPosition;
    }

    public BlockState getFeetBlockState() {
        if (this.feetBlockState == null) {
            this.feetBlockState = this.level.getBlockState(this.blockPosition());
        }
        return this.feetBlockState;
    }

    public ChunkPos chunkPosition() {
        return this.chunkPosition;
    }

    public Vec3 getDeltaMovement() {
        return this.deltaMovement;
    }

    public void setDeltaMovement(Vec3 $$0) {
        this.deltaMovement = $$0;
    }

    public void addDeltaMovement(Vec3 $$0) {
        this.deltaMovement = this.deltaMovement.add($$0);
    }

    public void setDeltaMovement(double $$0, double $$1, double $$2) {
        this.setDeltaMovement(new Vec3($$0, $$1, $$2));
    }

    public final int getBlockX() {
        return this.blockPosition.getX();
    }

    public final double getX() {
        return this.position.x;
    }

    public double getX(double $$0) {
        return this.position.x + (double)this.getBbWidth() * $$0;
    }

    public double getRandomX(double $$0) {
        return this.getX((2.0 * this.random.nextDouble() - 1.0) * $$0);
    }

    public final int getBlockY() {
        return this.blockPosition.getY();
    }

    public final double getY() {
        return this.position.y;
    }

    public double getY(double $$0) {
        return this.position.y + (double)this.getBbHeight() * $$0;
    }

    public double getRandomY() {
        return this.getY(this.random.nextDouble());
    }

    public double getEyeY() {
        return this.position.y + (double)this.eyeHeight;
    }

    public final int getBlockZ() {
        return this.blockPosition.getZ();
    }

    public final double getZ() {
        return this.position.z;
    }

    public double getZ(double $$0) {
        return this.position.z + (double)this.getBbWidth() * $$0;
    }

    public double getRandomZ(double $$0) {
        return this.getZ((2.0 * this.random.nextDouble() - 1.0) * $$0);
    }

    public final void setPosRaw(double $$0, double $$1, double $$2) {
        if (this.position.x != $$0 || this.position.y != $$1 || this.position.z != $$2) {
            this.position = new Vec3($$0, $$1, $$2);
            int $$3 = Mth.floor($$0);
            int $$4 = Mth.floor($$1);
            int $$5 = Mth.floor($$2);
            if ($$3 != this.blockPosition.getX() || $$4 != this.blockPosition.getY() || $$5 != this.blockPosition.getZ()) {
                this.blockPosition = new BlockPos($$3, $$4, $$5);
                this.feetBlockState = null;
                if (SectionPos.blockToSectionCoord($$3) != this.chunkPosition.x || SectionPos.blockToSectionCoord($$5) != this.chunkPosition.z) {
                    this.chunkPosition = new ChunkPos(this.blockPosition);
                }
            }
            this.levelCallback.onMove();
        }
    }

    public void checkDespawn() {
    }

    public Vec3 getRopeHoldPosition(float $$0) {
        return this.getPosition($$0).add(0.0, (double)this.eyeHeight * 0.7, 0.0);
    }

    public void recreateFromPacket(ClientboundAddEntityPacket $$0) {
        int $$1 = $$0.getId();
        double $$2 = $$0.getX();
        double $$3 = $$0.getY();
        double $$4 = $$0.getZ();
        this.syncPacketPositionCodec($$2, $$3, $$4);
        this.moveTo($$2, $$3, $$4);
        this.setXRot($$0.getXRot());
        this.setYRot($$0.getYRot());
        this.setId($$1);
        this.setUUID($$0.getUUID());
    }

    @Nullable
    public ItemStack getPickResult() {
        return null;
    }

    public void setIsInPowderSnow(boolean $$0) {
        this.isInPowderSnow = $$0;
    }

    public boolean canFreeze() {
        return !this.getType().is(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES);
    }

    public boolean isFreezing() {
        return (this.isInPowderSnow || this.wasInPowderSnow) && this.canFreeze();
    }

    public float getYRot() {
        return this.yRot;
    }

    public float getVisualRotationYInDegrees() {
        return this.getYRot();
    }

    public void setYRot(float $$0) {
        if (!Float.isFinite((float)$$0)) {
            Util.logAndPauseIfInIde("Invalid entity rotation: " + $$0 + ", discarding.");
            return;
        }
        this.yRot = $$0;
    }

    public float getXRot() {
        return this.xRot;
    }

    public void setXRot(float $$0) {
        if (!Float.isFinite((float)$$0)) {
            Util.logAndPauseIfInIde("Invalid entity rotation: " + $$0 + ", discarding.");
            return;
        }
        this.xRot = $$0;
    }

    public boolean canSprint() {
        return false;
    }

    public final boolean isRemoved() {
        return this.removalReason != null;
    }

    @Nullable
    public RemovalReason getRemovalReason() {
        return this.removalReason;
    }

    @Override
    public final void setRemoved(RemovalReason $$0) {
        if (this.removalReason == null) {
            this.removalReason = $$0;
        }
        if (this.removalReason.shouldDestroy()) {
            this.stopRiding();
        }
        this.getPassengers().forEach(Entity::stopRiding);
        this.levelCallback.onRemove($$0);
    }

    protected void unsetRemoved() {
        this.removalReason = null;
    }

    @Override
    public void setLevelCallback(EntityInLevelCallback $$0) {
        this.levelCallback = $$0;
    }

    @Override
    public boolean shouldBeSaved() {
        if (this.removalReason != null && !this.removalReason.shouldSave()) {
            return false;
        }
        if (this.isPassenger()) {
            return false;
        }
        return !this.isVehicle() || !this.hasExactlyOnePlayerPassenger();
    }

    @Override
    public boolean isAlwaysTicking() {
        return false;
    }

    public boolean mayInteract(Level $$0, BlockPos $$1) {
        return true;
    }

    public Level getLevel() {
        return this.level;
    }

    public static enum RemovalReason {
        KILLED(true, false),
        DISCARDED(true, false),
        UNLOADED_TO_CHUNK(false, true),
        UNLOADED_WITH_PLAYER(false, false),
        CHANGED_DIMENSION(false, false);

        private final boolean destroy;
        private final boolean save;

        private RemovalReason(boolean $$0, boolean $$1) {
            this.destroy = $$0;
            this.save = $$1;
        }

        public boolean shouldDestroy() {
            return this.destroy;
        }

        public boolean shouldSave() {
            return this.save;
        }
    }

    public static enum MovementEmission {
        NONE(false, false),
        SOUNDS(true, false),
        EVENTS(false, true),
        ALL(true, true);

        final boolean sounds;
        final boolean events;

        private MovementEmission(boolean $$0, boolean $$1) {
            this.sounds = $$0;
            this.events = $$1;
        }

        public boolean emitsAnything() {
            return this.events || this.sounds;
        }

        public boolean emitsEvents() {
            return this.events;
        }

        public boolean emitsSounds() {
            return this.sounds;
        }
    }

    @FunctionalInterface
    public static interface MoveFunction {
        public void accept(Entity var1, double var2, double var4, double var6);
    }
}