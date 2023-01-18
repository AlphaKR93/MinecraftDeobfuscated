/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.lang.Boolean
 *  java.lang.Enum
 *  java.lang.Float
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.function.IntFunction
 *  java.util.function.Predicate
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.vehicle;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ServerboundPaddleBoatPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WaterlilyBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class Boat
extends Entity
implements VariantHolder<Type> {
    private static final EntityDataAccessor<Integer> DATA_ID_HURT = SynchedEntityData.defineId(Boat.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_ID_HURTDIR = SynchedEntityData.defineId(Boat.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_ID_DAMAGE = SynchedEntityData.defineId(Boat.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_ID_TYPE = SynchedEntityData.defineId(Boat.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_ID_PADDLE_LEFT = SynchedEntityData.defineId(Boat.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_ID_PADDLE_RIGHT = SynchedEntityData.defineId(Boat.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_ID_BUBBLE_TIME = SynchedEntityData.defineId(Boat.class, EntityDataSerializers.INT);
    public static final int PADDLE_LEFT = 0;
    public static final int PADDLE_RIGHT = 1;
    private static final int TIME_TO_EJECT = 60;
    private static final float PADDLE_SPEED = 0.3926991f;
    public static final double PADDLE_SOUND_TIME = 0.7853981852531433;
    public static final int BUBBLE_TIME = 60;
    private final float[] paddlePositions = new float[2];
    private float invFriction;
    private float outOfControlTicks;
    private float deltaRotation;
    private int lerpSteps;
    private double lerpX;
    private double lerpY;
    private double lerpZ;
    private double lerpYRot;
    private double lerpXRot;
    private boolean inputLeft;
    private boolean inputRight;
    private boolean inputUp;
    private boolean inputDown;
    private double waterLevel;
    private float landFriction;
    private Status status;
    private Status oldStatus;
    private double lastYd;
    private boolean isAboveBubbleColumn;
    private boolean bubbleColumnDirectionIsDown;
    private float bubbleMultiplier;
    private float bubbleAngle;
    private float bubbleAngleO;

    public Boat(EntityType<? extends Boat> $$0, Level $$1) {
        super($$0, $$1);
        this.blocksBuilding = true;
    }

    public Boat(Level $$0, double $$1, double $$2, double $$3) {
        this((EntityType<? extends Boat>)EntityType.BOAT, $$0);
        this.setPos($$1, $$2, $$3);
        this.xo = $$1;
        this.yo = $$2;
        this.zo = $$3;
    }

    @Override
    protected float getEyeHeight(Pose $$0, EntityDimensions $$1) {
        return $$1.height;
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.EVENTS;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_ID_HURT, 0);
        this.entityData.define(DATA_ID_HURTDIR, 1);
        this.entityData.define(DATA_ID_DAMAGE, Float.valueOf((float)0.0f));
        this.entityData.define(DATA_ID_TYPE, Type.OAK.ordinal());
        this.entityData.define(DATA_ID_PADDLE_LEFT, false);
        this.entityData.define(DATA_ID_PADDLE_RIGHT, false);
        this.entityData.define(DATA_ID_BUBBLE_TIME, 0);
    }

    @Override
    public boolean canCollideWith(Entity $$0) {
        return Boat.canVehicleCollide(this, $$0);
    }

    public static boolean canVehicleCollide(Entity $$0, Entity $$1) {
        return ($$1.canBeCollidedWith() || $$1.isPushable()) && !$$0.isPassengerOfSameVehicle($$1);
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    protected Vec3 getRelativePortalPosition(Direction.Axis $$0, BlockUtil.FoundRectangle $$1) {
        return LivingEntity.resetForwardDirectionOfRelativePortalPosition(super.getRelativePortalPosition($$0, $$1));
    }

    @Override
    public double getPassengersRidingOffset() {
        return this.getVariant() == Type.BAMBOO ? 0.3 : -0.1;
    }

    @Override
    public boolean hurt(DamageSource $$0, float $$1) {
        boolean $$2;
        if (this.isInvulnerableTo($$0)) {
            return false;
        }
        if (this.level.isClientSide || this.isRemoved()) {
            return true;
        }
        this.setHurtDir(-this.getHurtDir());
        this.setHurtTime(10);
        this.setDamage(this.getDamage() + $$1 * 10.0f);
        this.markHurt();
        this.gameEvent(GameEvent.ENTITY_DAMAGE, $$0.getEntity());
        boolean bl = $$2 = $$0.getEntity() instanceof Player && ((Player)$$0.getEntity()).getAbilities().instabuild;
        if ($$2 || this.getDamage() > 40.0f) {
            if (!$$2 && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                this.destroy($$0);
            }
            this.discard();
        }
        return true;
    }

    protected void destroy(DamageSource $$0) {
        this.spawnAtLocation(this.getDropItem());
    }

    @Override
    public void onAboveBubbleCol(boolean $$0) {
        if (!this.level.isClientSide) {
            this.isAboveBubbleColumn = true;
            this.bubbleColumnDirectionIsDown = $$0;
            if (this.getBubbleTime() == 0) {
                this.setBubbleTime(60);
            }
        }
        this.level.addParticle(ParticleTypes.SPLASH, this.getX() + (double)this.random.nextFloat(), this.getY() + 0.7, this.getZ() + (double)this.random.nextFloat(), 0.0, 0.0, 0.0);
        if (this.random.nextInt(20) == 0) {
            this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), this.getSwimSplashSound(), this.getSoundSource(), 1.0f, 0.8f + 0.4f * this.random.nextFloat(), false);
            this.gameEvent(GameEvent.SPLASH, this.getControllingPassenger());
        }
    }

    @Override
    public void push(Entity $$0) {
        if ($$0 instanceof Boat) {
            if ($$0.getBoundingBox().minY < this.getBoundingBox().maxY) {
                super.push($$0);
            }
        } else if ($$0.getBoundingBox().minY <= this.getBoundingBox().minY) {
            super.push($$0);
        }
    }

    public Item getDropItem() {
        return switch (this.getVariant()) {
            case Type.SPRUCE -> Items.SPRUCE_BOAT;
            case Type.BIRCH -> Items.BIRCH_BOAT;
            case Type.JUNGLE -> Items.JUNGLE_BOAT;
            case Type.ACACIA -> Items.ACACIA_BOAT;
            case Type.DARK_OAK -> Items.DARK_OAK_BOAT;
            case Type.MANGROVE -> Items.MANGROVE_BOAT;
            case Type.BAMBOO -> Items.BAMBOO_RAFT;
            default -> Items.OAK_BOAT;
        };
    }

    @Override
    public void animateHurt(float $$0) {
        this.setHurtDir(-this.getHurtDir());
        this.setHurtTime(10);
        this.setDamage(this.getDamage() * 11.0f);
    }

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    @Override
    public void lerpTo(double $$0, double $$1, double $$2, float $$3, float $$4, int $$5, boolean $$6) {
        this.lerpX = $$0;
        this.lerpY = $$1;
        this.lerpZ = $$2;
        this.lerpYRot = $$3;
        this.lerpXRot = $$4;
        this.lerpSteps = 10;
    }

    @Override
    public Direction getMotionDirection() {
        return this.getDirection().getClockWise();
    }

    @Override
    public void tick() {
        this.oldStatus = this.status;
        this.status = this.getStatus();
        this.outOfControlTicks = this.status == Status.UNDER_WATER || this.status == Status.UNDER_FLOWING_WATER ? (this.outOfControlTicks += 1.0f) : 0.0f;
        if (!this.level.isClientSide && this.outOfControlTicks >= 60.0f) {
            this.ejectPassengers();
        }
        if (this.getHurtTime() > 0) {
            this.setHurtTime(this.getHurtTime() - 1);
        }
        if (this.getDamage() > 0.0f) {
            this.setDamage(this.getDamage() - 1.0f);
        }
        super.tick();
        this.tickLerp();
        if (this.isControlledByLocalInstance()) {
            if (!(this.getFirstPassenger() instanceof Player)) {
                this.setPaddleState(false, false);
            }
            this.floatBoat();
            if (this.level.isClientSide) {
                this.controlBoat();
                this.level.sendPacketToServer(new ServerboundPaddleBoatPacket(this.getPaddleState(0), this.getPaddleState(1)));
            }
            this.move(MoverType.SELF, this.getDeltaMovement());
        } else {
            this.setDeltaMovement(Vec3.ZERO);
        }
        this.tickBubbleColumn();
        for (int $$0 = 0; $$0 <= 1; ++$$0) {
            if (this.getPaddleState($$0)) {
                SoundEvent $$1;
                if (!this.isSilent() && (double)(this.paddlePositions[$$0] % ((float)Math.PI * 2)) <= 0.7853981852531433 && (double)((this.paddlePositions[$$0] + 0.3926991f) % ((float)Math.PI * 2)) >= 0.7853981852531433 && ($$1 = this.getPaddleSound()) != null) {
                    Vec3 $$2 = this.getViewVector(1.0f);
                    double $$3 = $$0 == 1 ? -$$2.z : $$2.z;
                    double $$4 = $$0 == 1 ? $$2.x : -$$2.x;
                    this.level.playSound(null, this.getX() + $$3, this.getY(), this.getZ() + $$4, $$1, this.getSoundSource(), 1.0f, 0.8f + 0.4f * this.random.nextFloat());
                }
                int n = $$0;
                this.paddlePositions[n] = this.paddlePositions[n] + 0.3926991f;
                continue;
            }
            this.paddlePositions[$$0] = 0.0f;
        }
        this.checkInsideBlocks();
        List<Entity> $$5 = this.level.getEntities(this, this.getBoundingBox().inflate(0.2f, -0.01f, 0.2f), EntitySelector.pushableBy(this));
        if (!$$5.isEmpty()) {
            boolean $$6 = !this.level.isClientSide && !(this.getControllingPassenger() instanceof Player);
            for (int $$7 = 0; $$7 < $$5.size(); ++$$7) {
                Entity $$8 = (Entity)$$5.get($$7);
                if ($$8.hasPassenger(this)) continue;
                if ($$6 && this.getPassengers().size() < this.getMaxPassengers() && !$$8.isPassenger() && $$8.getBbWidth() < this.getBbWidth() && $$8 instanceof LivingEntity && !($$8 instanceof WaterAnimal) && !($$8 instanceof Player)) {
                    $$8.startRiding(this);
                    continue;
                }
                this.push($$8);
            }
        }
    }

    private void tickBubbleColumn() {
        if (this.level.isClientSide) {
            int $$02 = this.getBubbleTime();
            this.bubbleMultiplier = $$02 > 0 ? (this.bubbleMultiplier += 0.05f) : (this.bubbleMultiplier -= 0.1f);
            this.bubbleMultiplier = Mth.clamp(this.bubbleMultiplier, 0.0f, 1.0f);
            this.bubbleAngleO = this.bubbleAngle;
            this.bubbleAngle = 10.0f * (float)Math.sin((double)(0.5f * (float)this.level.getGameTime())) * this.bubbleMultiplier;
        } else {
            int $$1;
            if (!this.isAboveBubbleColumn) {
                this.setBubbleTime(0);
            }
            if (($$1 = this.getBubbleTime()) > 0) {
                this.setBubbleTime(--$$1);
                int $$2 = 60 - $$1 - 1;
                if ($$2 > 0 && $$1 == 0) {
                    this.setBubbleTime(0);
                    Vec3 $$3 = this.getDeltaMovement();
                    if (this.bubbleColumnDirectionIsDown) {
                        this.setDeltaMovement($$3.add(0.0, -0.7, 0.0));
                        this.ejectPassengers();
                    } else {
                        this.setDeltaMovement($$3.x, this.hasPassenger((Predicate<Entity>)((Predicate)$$0 -> $$0 instanceof Player)) ? 2.7 : 0.6, $$3.z);
                    }
                }
                this.isAboveBubbleColumn = false;
            }
        }
    }

    @Nullable
    protected SoundEvent getPaddleSound() {
        switch (this.getStatus()) {
            case IN_WATER: 
            case UNDER_WATER: 
            case UNDER_FLOWING_WATER: {
                return SoundEvents.BOAT_PADDLE_WATER;
            }
            case ON_LAND: {
                return SoundEvents.BOAT_PADDLE_LAND;
            }
        }
        return null;
    }

    private void tickLerp() {
        if (this.isControlledByLocalInstance()) {
            this.lerpSteps = 0;
            this.syncPacketPositionCodec(this.getX(), this.getY(), this.getZ());
        }
        if (this.lerpSteps <= 0) {
            return;
        }
        double $$0 = this.getX() + (this.lerpX - this.getX()) / (double)this.lerpSteps;
        double $$1 = this.getY() + (this.lerpY - this.getY()) / (double)this.lerpSteps;
        double $$2 = this.getZ() + (this.lerpZ - this.getZ()) / (double)this.lerpSteps;
        double $$3 = Mth.wrapDegrees(this.lerpYRot - (double)this.getYRot());
        this.setYRot(this.getYRot() + (float)$$3 / (float)this.lerpSteps);
        this.setXRot(this.getXRot() + (float)(this.lerpXRot - (double)this.getXRot()) / (float)this.lerpSteps);
        --this.lerpSteps;
        this.setPos($$0, $$1, $$2);
        this.setRot(this.getYRot(), this.getXRot());
    }

    public void setPaddleState(boolean $$0, boolean $$1) {
        this.entityData.set(DATA_ID_PADDLE_LEFT, $$0);
        this.entityData.set(DATA_ID_PADDLE_RIGHT, $$1);
    }

    public float getRowingTime(int $$0, float $$1) {
        if (this.getPaddleState($$0)) {
            return Mth.clampedLerp(this.paddlePositions[$$0] - 0.3926991f, this.paddlePositions[$$0], $$1);
        }
        return 0.0f;
    }

    private Status getStatus() {
        Status $$0 = this.isUnderwater();
        if ($$0 != null) {
            this.waterLevel = this.getBoundingBox().maxY;
            return $$0;
        }
        if (this.checkInWater()) {
            return Status.IN_WATER;
        }
        float $$1 = this.getGroundFriction();
        if ($$1 > 0.0f) {
            this.landFriction = $$1;
            return Status.ON_LAND;
        }
        return Status.IN_AIR;
    }

    public float getWaterLevelAbove() {
        AABB $$0 = this.getBoundingBox();
        int $$1 = Mth.floor($$0.minX);
        int $$2 = Mth.ceil($$0.maxX);
        int $$3 = Mth.floor($$0.maxY);
        int $$4 = Mth.ceil($$0.maxY - this.lastYd);
        int $$5 = Mth.floor($$0.minZ);
        int $$6 = Mth.ceil($$0.maxZ);
        BlockPos.MutableBlockPos $$7 = new BlockPos.MutableBlockPos();
        block0: for (int $$8 = $$3; $$8 < $$4; ++$$8) {
            float $$9 = 0.0f;
            for (int $$10 = $$1; $$10 < $$2; ++$$10) {
                for (int $$11 = $$5; $$11 < $$6; ++$$11) {
                    $$7.set($$10, $$8, $$11);
                    FluidState $$12 = this.level.getFluidState($$7);
                    if ($$12.is(FluidTags.WATER)) {
                        $$9 = Math.max((float)$$9, (float)$$12.getHeight(this.level, $$7));
                    }
                    if ($$9 >= 1.0f) continue block0;
                }
            }
            if (!($$9 < 1.0f)) continue;
            return (float)$$7.getY() + $$9;
        }
        return $$4 + 1;
    }

    public float getGroundFriction() {
        AABB $$0 = this.getBoundingBox();
        AABB $$1 = new AABB($$0.minX, $$0.minY - 0.001, $$0.minZ, $$0.maxX, $$0.minY, $$0.maxZ);
        int $$2 = Mth.floor($$1.minX) - 1;
        int $$3 = Mth.ceil($$1.maxX) + 1;
        int $$4 = Mth.floor($$1.minY) - 1;
        int $$5 = Mth.ceil($$1.maxY) + 1;
        int $$6 = Mth.floor($$1.minZ) - 1;
        int $$7 = Mth.ceil($$1.maxZ) + 1;
        VoxelShape $$8 = Shapes.create($$1);
        float $$9 = 0.0f;
        int $$10 = 0;
        BlockPos.MutableBlockPos $$11 = new BlockPos.MutableBlockPos();
        for (int $$12 = $$2; $$12 < $$3; ++$$12) {
            for (int $$13 = $$6; $$13 < $$7; ++$$13) {
                int $$14 = ($$12 == $$2 || $$12 == $$3 - 1 ? 1 : 0) + ($$13 == $$6 || $$13 == $$7 - 1 ? 1 : 0);
                if ($$14 == 2) continue;
                for (int $$15 = $$4; $$15 < $$5; ++$$15) {
                    if ($$14 > 0 && ($$15 == $$4 || $$15 == $$5 - 1)) continue;
                    $$11.set($$12, $$15, $$13);
                    BlockState $$16 = this.level.getBlockState($$11);
                    if ($$16.getBlock() instanceof WaterlilyBlock || !Shapes.joinIsNotEmpty($$16.getCollisionShape(this.level, $$11).move($$12, $$15, $$13), $$8, BooleanOp.AND)) continue;
                    $$9 += $$16.getBlock().getFriction();
                    ++$$10;
                }
            }
        }
        return $$9 / (float)$$10;
    }

    private boolean checkInWater() {
        AABB $$0 = this.getBoundingBox();
        int $$1 = Mth.floor($$0.minX);
        int $$2 = Mth.ceil($$0.maxX);
        int $$3 = Mth.floor($$0.minY);
        int $$4 = Mth.ceil($$0.minY + 0.001);
        int $$5 = Mth.floor($$0.minZ);
        int $$6 = Mth.ceil($$0.maxZ);
        boolean $$7 = false;
        this.waterLevel = -1.7976931348623157E308;
        BlockPos.MutableBlockPos $$8 = new BlockPos.MutableBlockPos();
        for (int $$9 = $$1; $$9 < $$2; ++$$9) {
            for (int $$10 = $$3; $$10 < $$4; ++$$10) {
                for (int $$11 = $$5; $$11 < $$6; ++$$11) {
                    $$8.set($$9, $$10, $$11);
                    FluidState $$12 = this.level.getFluidState($$8);
                    if (!$$12.is(FluidTags.WATER)) continue;
                    float $$13 = (float)$$10 + $$12.getHeight(this.level, $$8);
                    this.waterLevel = Math.max((double)$$13, (double)this.waterLevel);
                    $$7 |= $$0.minY < (double)$$13;
                }
            }
        }
        return $$7;
    }

    @Nullable
    private Status isUnderwater() {
        AABB $$0 = this.getBoundingBox();
        double $$1 = $$0.maxY + 0.001;
        int $$2 = Mth.floor($$0.minX);
        int $$3 = Mth.ceil($$0.maxX);
        int $$4 = Mth.floor($$0.maxY);
        int $$5 = Mth.ceil($$1);
        int $$6 = Mth.floor($$0.minZ);
        int $$7 = Mth.ceil($$0.maxZ);
        boolean $$8 = false;
        BlockPos.MutableBlockPos $$9 = new BlockPos.MutableBlockPos();
        for (int $$10 = $$2; $$10 < $$3; ++$$10) {
            for (int $$11 = $$4; $$11 < $$5; ++$$11) {
                for (int $$12 = $$6; $$12 < $$7; ++$$12) {
                    $$9.set($$10, $$11, $$12);
                    FluidState $$13 = this.level.getFluidState($$9);
                    if (!$$13.is(FluidTags.WATER) || !($$1 < (double)((float)$$9.getY() + $$13.getHeight(this.level, $$9)))) continue;
                    if ($$13.isSource()) {
                        $$8 = true;
                        continue;
                    }
                    return Status.UNDER_FLOWING_WATER;
                }
            }
        }
        return $$8 ? Status.UNDER_WATER : null;
    }

    private void floatBoat() {
        double $$0 = -0.04f;
        double $$1 = this.isNoGravity() ? 0.0 : (double)-0.04f;
        double $$2 = 0.0;
        this.invFriction = 0.05f;
        if (this.oldStatus == Status.IN_AIR && this.status != Status.IN_AIR && this.status != Status.ON_LAND) {
            this.waterLevel = this.getY(1.0);
            this.setPos(this.getX(), (double)(this.getWaterLevelAbove() - this.getBbHeight()) + 0.101, this.getZ());
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.0, 1.0));
            this.lastYd = 0.0;
            this.status = Status.IN_WATER;
        } else {
            if (this.status == Status.IN_WATER) {
                $$2 = (this.waterLevel - this.getY()) / (double)this.getBbHeight();
                this.invFriction = 0.9f;
            } else if (this.status == Status.UNDER_FLOWING_WATER) {
                $$1 = -7.0E-4;
                this.invFriction = 0.9f;
            } else if (this.status == Status.UNDER_WATER) {
                $$2 = 0.01f;
                this.invFriction = 0.45f;
            } else if (this.status == Status.IN_AIR) {
                this.invFriction = 0.9f;
            } else if (this.status == Status.ON_LAND) {
                this.invFriction = this.landFriction;
                if (this.getControllingPassenger() instanceof Player) {
                    this.landFriction /= 2.0f;
                }
            }
            Vec3 $$3 = this.getDeltaMovement();
            this.setDeltaMovement($$3.x * (double)this.invFriction, $$3.y + $$1, $$3.z * (double)this.invFriction);
            this.deltaRotation *= this.invFriction;
            if ($$2 > 0.0) {
                Vec3 $$4 = this.getDeltaMovement();
                this.setDeltaMovement($$4.x, ($$4.y + $$2 * 0.06153846016296973) * 0.75, $$4.z);
            }
        }
    }

    private void controlBoat() {
        if (!this.isVehicle()) {
            return;
        }
        float $$0 = 0.0f;
        if (this.inputLeft) {
            this.deltaRotation -= 1.0f;
        }
        if (this.inputRight) {
            this.deltaRotation += 1.0f;
        }
        if (this.inputRight != this.inputLeft && !this.inputUp && !this.inputDown) {
            $$0 += 0.005f;
        }
        this.setYRot(this.getYRot() + this.deltaRotation);
        if (this.inputUp) {
            $$0 += 0.04f;
        }
        if (this.inputDown) {
            $$0 -= 0.005f;
        }
        this.setDeltaMovement(this.getDeltaMovement().add(Mth.sin(-this.getYRot() * ((float)Math.PI / 180)) * $$0, 0.0, Mth.cos(this.getYRot() * ((float)Math.PI / 180)) * $$0));
        this.setPaddleState(this.inputRight && !this.inputLeft || this.inputUp, this.inputLeft && !this.inputRight || this.inputUp);
    }

    protected float getSinglePassengerXOffset() {
        return 0.0f;
    }

    @Override
    public void positionRider(Entity $$0) {
        if (!this.hasPassenger($$0)) {
            return;
        }
        float $$1 = this.getSinglePassengerXOffset();
        float $$2 = (float)((this.isRemoved() ? (double)0.01f : this.getPassengersRidingOffset()) + $$0.getMyRidingOffset());
        if (this.getPassengers().size() > 1) {
            int $$3 = this.getPassengers().indexOf((Object)$$0);
            $$1 = $$3 == 0 ? 0.2f : -0.6f;
            if ($$0 instanceof Animal) {
                $$1 += 0.2f;
            }
        }
        Vec3 $$4 = new Vec3($$1, 0.0, 0.0).yRot(-this.getYRot() * ((float)Math.PI / 180) - 1.5707964f);
        $$0.setPos(this.getX() + $$4.x, this.getY() + (double)$$2, this.getZ() + $$4.z);
        $$0.setYRot($$0.getYRot() + this.deltaRotation);
        $$0.setYHeadRot($$0.getYHeadRot() + this.deltaRotation);
        this.clampRotation($$0);
        if ($$0 instanceof Animal && this.getPassengers().size() == this.getMaxPassengers()) {
            int $$5 = $$0.getId() % 2 == 0 ? 90 : 270;
            $$0.setYBodyRot(((Animal)$$0).yBodyRot + (float)$$5);
            $$0.setYHeadRot($$0.getYHeadRot() + (float)$$5);
        }
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity $$0) {
        double $$3;
        Vec3 $$1 = Boat.getCollisionHorizontalEscapeVector(this.getBbWidth() * Mth.SQRT_OF_TWO, $$0.getBbWidth(), $$0.getYRot());
        double $$2 = this.getX() + $$1.x;
        BlockPos $$4 = new BlockPos($$2, this.getBoundingBox().maxY, $$3 = this.getZ() + $$1.z);
        Vec3i $$5 = $$4.below();
        if (!this.level.isWaterAt((BlockPos)$$5)) {
            double $$8;
            ArrayList $$6 = Lists.newArrayList();
            double $$7 = this.level.getBlockFloorHeight($$4);
            if (DismountHelper.isBlockFloorValid($$7)) {
                $$6.add((Object)new Vec3($$2, (double)$$4.getY() + $$7, $$3));
            }
            if (DismountHelper.isBlockFloorValid($$8 = this.level.getBlockFloorHeight((BlockPos)$$5))) {
                $$6.add((Object)new Vec3($$2, (double)$$5.getY() + $$8, $$3));
            }
            for (Pose $$9 : $$0.getDismountPoses()) {
                for (Vec3 $$10 : $$6) {
                    if (!DismountHelper.canDismountTo(this.level, $$10, $$0, $$9)) continue;
                    $$0.setPose($$9);
                    return $$10;
                }
            }
        }
        return super.getDismountLocationForPassenger($$0);
    }

    protected void clampRotation(Entity $$0) {
        $$0.setYBodyRot(this.getYRot());
        float $$1 = Mth.wrapDegrees($$0.getYRot() - this.getYRot());
        float $$2 = Mth.clamp($$1, -105.0f, 105.0f);
        $$0.yRotO += $$2 - $$1;
        $$0.setYRot($$0.getYRot() + $$2 - $$1);
        $$0.setYHeadRot($$0.getYRot());
    }

    @Override
    public void onPassengerTurned(Entity $$0) {
        this.clampRotation($$0);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag $$0) {
        $$0.putString("Type", this.getVariant().getSerializedName());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag $$0) {
        if ($$0.contains("Type", 8)) {
            this.setVariant(Type.byName($$0.getString("Type")));
        }
    }

    @Override
    public InteractionResult interact(Player $$0, InteractionHand $$1) {
        if ($$0.isSecondaryUseActive()) {
            return InteractionResult.PASS;
        }
        if (this.outOfControlTicks < 60.0f) {
            if (!this.level.isClientSide) {
                return $$0.startRiding(this) ? InteractionResult.CONSUME : InteractionResult.PASS;
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void checkFallDamage(double $$0, boolean $$1, BlockState $$2, BlockPos $$3) {
        this.lastYd = this.getDeltaMovement().y;
        if (this.isPassenger()) {
            return;
        }
        if ($$1) {
            if (this.fallDistance > 3.0f) {
                if (this.status != Status.ON_LAND) {
                    this.resetFallDistance();
                    return;
                }
                this.causeFallDamage(this.fallDistance, 1.0f, DamageSource.FALL);
                if (!this.level.isClientSide && !this.isRemoved()) {
                    this.kill();
                    if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                        for (int $$4 = 0; $$4 < 3; ++$$4) {
                            this.spawnAtLocation(this.getVariant().getPlanks());
                        }
                        for (int $$5 = 0; $$5 < 2; ++$$5) {
                            this.spawnAtLocation(Items.STICK);
                        }
                    }
                }
            }
            this.resetFallDistance();
        } else if (!this.level.getFluidState((BlockPos)this.blockPosition().below()).is(FluidTags.WATER) && $$0 < 0.0) {
            this.fallDistance -= (float)$$0;
        }
    }

    public boolean getPaddleState(int $$0) {
        return this.entityData.get($$0 == 0 ? DATA_ID_PADDLE_LEFT : DATA_ID_PADDLE_RIGHT) != false && this.getControllingPassenger() != null;
    }

    public void setDamage(float $$0) {
        this.entityData.set(DATA_ID_DAMAGE, Float.valueOf((float)$$0));
    }

    public float getDamage() {
        return this.entityData.get(DATA_ID_DAMAGE).floatValue();
    }

    public void setHurtTime(int $$0) {
        this.entityData.set(DATA_ID_HURT, $$0);
    }

    public int getHurtTime() {
        return this.entityData.get(DATA_ID_HURT);
    }

    private void setBubbleTime(int $$0) {
        this.entityData.set(DATA_ID_BUBBLE_TIME, $$0);
    }

    private int getBubbleTime() {
        return this.entityData.get(DATA_ID_BUBBLE_TIME);
    }

    public float getBubbleAngle(float $$0) {
        return Mth.lerp($$0, this.bubbleAngleO, this.bubbleAngle);
    }

    public void setHurtDir(int $$0) {
        this.entityData.set(DATA_ID_HURTDIR, $$0);
    }

    public int getHurtDir() {
        return this.entityData.get(DATA_ID_HURTDIR);
    }

    @Override
    public void setVariant(Type $$0) {
        this.entityData.set(DATA_ID_TYPE, $$0.ordinal());
    }

    @Override
    public Type getVariant() {
        return Type.byId(this.entityData.get(DATA_ID_TYPE));
    }

    @Override
    protected boolean canAddPassenger(Entity $$0) {
        return this.getPassengers().size() < this.getMaxPassengers() && !this.isEyeInFluid(FluidTags.WATER);
    }

    protected int getMaxPassengers() {
        return 2;
    }

    @Override
    @Nullable
    public Entity getControllingPassenger() {
        return this.getFirstPassenger();
    }

    public void setInput(boolean $$0, boolean $$1, boolean $$2, boolean $$3) {
        this.inputLeft = $$0;
        this.inputRight = $$1;
        this.inputUp = $$2;
        this.inputDown = $$3;
    }

    @Override
    public boolean isUnderWater() {
        return this.status == Status.UNDER_WATER || this.status == Status.UNDER_FLOWING_WATER;
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(this.getDropItem());
    }

    public static enum Type implements StringRepresentable
    {
        OAK(Blocks.OAK_PLANKS, "oak"),
        SPRUCE(Blocks.SPRUCE_PLANKS, "spruce"),
        BIRCH(Blocks.BIRCH_PLANKS, "birch"),
        JUNGLE(Blocks.JUNGLE_PLANKS, "jungle"),
        ACACIA(Blocks.ACACIA_PLANKS, "acacia"),
        DARK_OAK(Blocks.DARK_OAK_PLANKS, "dark_oak"),
        MANGROVE(Blocks.MANGROVE_PLANKS, "mangrove"),
        BAMBOO(Blocks.BAMBOO_PLANKS, "bamboo");

        private final String name;
        private final Block planks;
        public static final StringRepresentable.EnumCodec<Type> CODEC;
        private static final IntFunction<Type> BY_ID;

        private Type(Block $$0, String $$1) {
            this.name = $$1;
            this.planks = $$0;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        public String getName() {
            return this.name;
        }

        public Block getPlanks() {
            return this.planks;
        }

        public String toString() {
            return this.name;
        }

        public static Type byId(int $$0) {
            return (Type)BY_ID.apply($$0);
        }

        public static Type byName(String $$0) {
            return CODEC.byName($$0, OAK);
        }

        static {
            CODEC = StringRepresentable.fromEnum((Supplier<E[]>)((Supplier)Type::values));
            BY_ID = ByIdMap.continuous(Enum::ordinal, Type.values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        }
    }

    public static enum Status {
        IN_WATER,
        UNDER_WATER,
        UNDER_FLOWING_WATER,
        ON_LAND,
        IN_AIR;

    }
}