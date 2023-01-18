/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.google.common.collect.UnmodifiableIterator
 *  com.mojang.datafixers.util.Pair
 *  java.lang.Boolean
 *  java.lang.Enum
 *  java.lang.Float
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.Map
 *  java.util.function.Function
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.vehicle;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.BlockUtil;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.entity.vehicle.MinecartChest;
import net.minecraft.world.entity.vehicle.MinecartCommandBlock;
import net.minecraft.world.entity.vehicle.MinecartFurnace;
import net.minecraft.world.entity.vehicle.MinecartHopper;
import net.minecraft.world.entity.vehicle.MinecartSpawner;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class AbstractMinecart
extends Entity {
    private static final EntityDataAccessor<Integer> DATA_ID_HURT = SynchedEntityData.defineId(AbstractMinecart.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_ID_HURTDIR = SynchedEntityData.defineId(AbstractMinecart.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_ID_DAMAGE = SynchedEntityData.defineId(AbstractMinecart.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_ID_DISPLAY_BLOCK = SynchedEntityData.defineId(AbstractMinecart.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_ID_DISPLAY_OFFSET = SynchedEntityData.defineId(AbstractMinecart.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_ID_CUSTOM_DISPLAY = SynchedEntityData.defineId(AbstractMinecart.class, EntityDataSerializers.BOOLEAN);
    private static final ImmutableMap<Pose, ImmutableList<Integer>> POSE_DISMOUNT_HEIGHTS = ImmutableMap.of((Object)((Object)Pose.STANDING), (Object)ImmutableList.of((Object)0, (Object)1, (Object)-1), (Object)((Object)Pose.CROUCHING), (Object)ImmutableList.of((Object)0, (Object)1, (Object)-1), (Object)((Object)Pose.SWIMMING), (Object)ImmutableList.of((Object)0, (Object)1));
    protected static final float WATER_SLOWDOWN_FACTOR = 0.95f;
    private boolean flipped;
    private static final Map<RailShape, Pair<Vec3i, Vec3i>> EXITS = (Map)Util.make(Maps.newEnumMap(RailShape.class), $$0 -> {
        Vec3i $$1 = Direction.WEST.getNormal();
        Vec3i $$2 = Direction.EAST.getNormal();
        Vec3i $$3 = Direction.NORTH.getNormal();
        Vec3i $$4 = Direction.SOUTH.getNormal();
        Vec3i $$5 = $$1.below();
        Vec3i $$6 = $$2.below();
        Vec3i $$7 = $$3.below();
        Vec3i $$8 = $$4.below();
        $$0.put((Enum)RailShape.NORTH_SOUTH, (Object)Pair.of((Object)$$3, (Object)$$4));
        $$0.put((Enum)RailShape.EAST_WEST, (Object)Pair.of((Object)$$1, (Object)$$2));
        $$0.put((Enum)RailShape.ASCENDING_EAST, (Object)Pair.of((Object)$$5, (Object)$$2));
        $$0.put((Enum)RailShape.ASCENDING_WEST, (Object)Pair.of((Object)$$1, (Object)$$6));
        $$0.put((Enum)RailShape.ASCENDING_NORTH, (Object)Pair.of((Object)$$3, (Object)$$8));
        $$0.put((Enum)RailShape.ASCENDING_SOUTH, (Object)Pair.of((Object)$$7, (Object)$$4));
        $$0.put((Enum)RailShape.SOUTH_EAST, (Object)Pair.of((Object)$$4, (Object)$$2));
        $$0.put((Enum)RailShape.SOUTH_WEST, (Object)Pair.of((Object)$$4, (Object)$$1));
        $$0.put((Enum)RailShape.NORTH_WEST, (Object)Pair.of((Object)$$3, (Object)$$1));
        $$0.put((Enum)RailShape.NORTH_EAST, (Object)Pair.of((Object)$$3, (Object)$$2));
    });
    private int lSteps;
    private double lx;
    private double ly;
    private double lz;
    private double lyr;
    private double lxr;
    private double lxd;
    private double lyd;
    private double lzd;

    protected AbstractMinecart(EntityType<?> $$0, Level $$1) {
        super($$0, $$1);
        this.blocksBuilding = true;
    }

    protected AbstractMinecart(EntityType<?> $$0, Level $$1, double $$2, double $$3, double $$4) {
        this($$0, $$1);
        this.setPos($$2, $$3, $$4);
        this.xo = $$2;
        this.yo = $$3;
        this.zo = $$4;
    }

    public static AbstractMinecart createMinecart(Level $$0, double $$1, double $$2, double $$3, Type $$4) {
        if ($$4 == Type.CHEST) {
            return new MinecartChest($$0, $$1, $$2, $$3);
        }
        if ($$4 == Type.FURNACE) {
            return new MinecartFurnace($$0, $$1, $$2, $$3);
        }
        if ($$4 == Type.TNT) {
            return new MinecartTNT($$0, $$1, $$2, $$3);
        }
        if ($$4 == Type.SPAWNER) {
            return new MinecartSpawner($$0, $$1, $$2, $$3);
        }
        if ($$4 == Type.HOPPER) {
            return new MinecartHopper($$0, $$1, $$2, $$3);
        }
        if ($$4 == Type.COMMAND_BLOCK) {
            return new MinecartCommandBlock($$0, $$1, $$2, $$3);
        }
        return new Minecart($$0, $$1, $$2, $$3);
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
        this.entityData.define(DATA_ID_DISPLAY_BLOCK, Block.getId(Blocks.AIR.defaultBlockState()));
        this.entityData.define(DATA_ID_DISPLAY_OFFSET, 6);
        this.entityData.define(DATA_ID_CUSTOM_DISPLAY, false);
    }

    @Override
    public boolean canCollideWith(Entity $$0) {
        return Boat.canVehicleCollide(this, $$0);
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
        return 0.0;
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity $$02) {
        Direction $$1 = this.getMotionDirection();
        if ($$1.getAxis() == Direction.Axis.Y) {
            return super.getDismountLocationForPassenger($$02);
        }
        int[][] $$2 = DismountHelper.offsetsForDirection($$1);
        BlockPos $$3 = this.blockPosition();
        BlockPos.MutableBlockPos $$4 = new BlockPos.MutableBlockPos();
        ImmutableList<Pose> $$5 = $$02.getDismountPoses();
        for (Pose $$6 : $$5) {
            EntityDimensions $$7 = $$02.getDimensions($$6);
            float $$8 = Math.min((float)$$7.width, (float)1.0f) / 2.0f;
            UnmodifiableIterator unmodifiableIterator = ((ImmutableList)POSE_DISMOUNT_HEIGHTS.get((Object)$$6)).iterator();
            while (unmodifiableIterator.hasNext()) {
                int $$9 = (Integer)unmodifiableIterator.next();
                for (int[] $$10 : $$2) {
                    Vec3 $$13;
                    AABB $$12;
                    $$4.set($$3.getX() + $$10[0], $$3.getY() + $$9, $$3.getZ() + $$10[1]);
                    double $$11 = this.level.getBlockFloorHeight(DismountHelper.nonClimbableShape(this.level, $$4), () -> DismountHelper.nonClimbableShape(this.level, (BlockPos)$$4.below()));
                    if (!DismountHelper.isBlockFloorValid($$11) || !DismountHelper.canDismountTo(this.level, $$02, ($$12 = new AABB(-$$8, 0.0, -$$8, $$8, $$7.height, $$8)).move($$13 = Vec3.upFromBottomCenterOf($$4, $$11)))) continue;
                    $$02.setPose($$6);
                    return $$13;
                }
            }
        }
        double $$14 = this.getBoundingBox().maxY;
        $$4.set((double)$$3.getX(), $$14, (double)$$3.getZ());
        for (Pose $$15 : $$5) {
            double $$16 = $$02.getDimensions((Pose)$$15).height;
            int $$17 = Mth.ceil($$14 - (double)$$4.getY() + $$16);
            double $$18 = DismountHelper.findCeilingFrom($$4, $$17, (Function<BlockPos, VoxelShape>)((Function)$$0 -> this.level.getBlockState((BlockPos)$$0).getCollisionShape(this.level, (BlockPos)$$0)));
            if (!($$14 + $$16 <= $$18)) continue;
            $$02.setPose($$15);
            break;
        }
        return super.getDismountLocationForPassenger($$02);
    }

    @Override
    public boolean hurt(DamageSource $$0, float $$1) {
        boolean $$2;
        if (this.level.isClientSide || this.isRemoved()) {
            return true;
        }
        if (this.isInvulnerableTo($$0)) {
            return false;
        }
        this.setHurtDir(-this.getHurtDir());
        this.setHurtTime(10);
        this.markHurt();
        this.setDamage(this.getDamage() + $$1 * 10.0f);
        this.gameEvent(GameEvent.ENTITY_DAMAGE, $$0.getEntity());
        boolean bl = $$2 = $$0.getEntity() instanceof Player && ((Player)$$0.getEntity()).getAbilities().instabuild;
        if ($$2 || this.getDamage() > 40.0f) {
            this.ejectPassengers();
            if (!$$2 || this.hasCustomName()) {
                this.destroy($$0);
            } else {
                this.discard();
            }
        }
        return true;
    }

    @Override
    protected float getBlockSpeedFactor() {
        BlockState $$0 = this.level.getBlockState(this.blockPosition());
        if ($$0.is(BlockTags.RAILS)) {
            return 1.0f;
        }
        return super.getBlockSpeedFactor();
    }

    public void destroy(DamageSource $$0) {
        this.kill();
        if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            ItemStack $$1 = new ItemStack(this.getDropItem());
            if (this.hasCustomName()) {
                $$1.setHoverName(this.getCustomName());
            }
            this.spawnAtLocation($$1);
        }
    }

    abstract Item getDropItem();

    @Override
    public void animateHurt() {
        this.setHurtDir(-this.getHurtDir());
        this.setHurtTime(10);
        this.setDamage(this.getDamage() + this.getDamage() * 10.0f);
    }

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    private static Pair<Vec3i, Vec3i> exits(RailShape $$0) {
        return (Pair)EXITS.get((Object)$$0);
    }

    @Override
    public Direction getMotionDirection() {
        return this.flipped ? this.getDirection().getOpposite().getClockWise() : this.getDirection().getClockWise();
    }

    @Override
    public void tick() {
        double $$12;
        BlockPos $$8;
        BlockState $$9;
        int $$7;
        int $$6;
        int $$5;
        if (this.getHurtTime() > 0) {
            this.setHurtTime(this.getHurtTime() - 1);
        }
        if (this.getDamage() > 0.0f) {
            this.setDamage(this.getDamage() - 1.0f);
        }
        this.checkOutOfWorld();
        this.handleNetherPortal();
        if (this.level.isClientSide) {
            if (this.lSteps > 0) {
                double $$0 = this.getX() + (this.lx - this.getX()) / (double)this.lSteps;
                double $$1 = this.getY() + (this.ly - this.getY()) / (double)this.lSteps;
                double $$2 = this.getZ() + (this.lz - this.getZ()) / (double)this.lSteps;
                double $$3 = Mth.wrapDegrees(this.lyr - (double)this.getYRot());
                this.setYRot(this.getYRot() + (float)$$3 / (float)this.lSteps);
                this.setXRot(this.getXRot() + (float)(this.lxr - (double)this.getXRot()) / (float)this.lSteps);
                --this.lSteps;
                this.setPos($$0, $$1, $$2);
                this.setRot(this.getYRot(), this.getXRot());
            } else {
                this.reapplyPosition();
                this.setRot(this.getYRot(), this.getXRot());
            }
            return;
        }
        if (!this.isNoGravity()) {
            double $$4 = this.isInWater() ? -0.005 : -0.04;
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, $$4, 0.0));
        }
        if (this.level.getBlockState(new BlockPos($$5 = Mth.floor(this.getX()), ($$6 = Mth.floor(this.getY())) - 1, $$7 = Mth.floor(this.getZ()))).is(BlockTags.RAILS)) {
            --$$6;
        }
        if (BaseRailBlock.isRail($$9 = this.level.getBlockState($$8 = new BlockPos($$5, $$6, $$7)))) {
            this.moveAlongTrack($$8, $$9);
            if ($$9.is(Blocks.ACTIVATOR_RAIL)) {
                this.activateMinecart($$5, $$6, $$7, $$9.getValue(PoweredRailBlock.POWERED));
            }
        } else {
            this.comeOffTrack();
        }
        this.checkInsideBlocks();
        this.setXRot(0.0f);
        double $$10 = this.xo - this.getX();
        double $$11 = this.zo - this.getZ();
        if ($$10 * $$10 + $$11 * $$11 > 0.001) {
            this.setYRot((float)(Mth.atan2($$11, $$10) * 180.0 / Math.PI));
            if (this.flipped) {
                this.setYRot(this.getYRot() + 180.0f);
            }
        }
        if (($$12 = (double)Mth.wrapDegrees(this.getYRot() - this.yRotO)) < -170.0 || $$12 >= 170.0) {
            this.setYRot(this.getYRot() + 180.0f);
            this.flipped = !this.flipped;
        }
        this.setRot(this.getYRot(), this.getXRot());
        if (this.getMinecartType() == Type.RIDEABLE && this.getDeltaMovement().horizontalDistanceSqr() > 0.01) {
            List<Entity> $$13 = this.level.getEntities(this, this.getBoundingBox().inflate(0.2f, 0.0, 0.2f), EntitySelector.pushableBy(this));
            if (!$$13.isEmpty()) {
                for (int $$14 = 0; $$14 < $$13.size(); ++$$14) {
                    Entity $$15 = (Entity)$$13.get($$14);
                    if ($$15 instanceof Player || $$15 instanceof IronGolem || $$15 instanceof AbstractMinecart || this.isVehicle() || $$15.isPassenger()) {
                        $$15.push(this);
                        continue;
                    }
                    $$15.startRiding(this);
                }
            }
        } else {
            for (Entity $$16 : this.level.getEntities(this, this.getBoundingBox().inflate(0.2f, 0.0, 0.2f))) {
                if (this.hasPassenger($$16) || !$$16.isPushable() || !($$16 instanceof AbstractMinecart)) continue;
                $$16.push(this);
            }
        }
        this.updateInWaterStateAndDoFluidPushing();
        if (this.isInLava()) {
            this.lavaHurt();
            this.fallDistance *= 0.5f;
        }
        this.firstTick = false;
    }

    protected double getMaxSpeed() {
        return (this.isInWater() ? 4.0 : 8.0) / 20.0;
    }

    public void activateMinecart(int $$0, int $$1, int $$2, boolean $$3) {
    }

    protected void comeOffTrack() {
        double $$0 = this.getMaxSpeed();
        Vec3 $$1 = this.getDeltaMovement();
        this.setDeltaMovement(Mth.clamp($$1.x, -$$0, $$0), $$1.y, Mth.clamp($$1.z, -$$0, $$0));
        if (this.onGround) {
            this.setDeltaMovement(this.getDeltaMovement().scale(0.5));
        }
        this.move(MoverType.SELF, this.getDeltaMovement());
        if (!this.onGround) {
            this.setDeltaMovement(this.getDeltaMovement().scale(0.95));
        }
    }

    protected void moveAlongTrack(BlockPos $$0, BlockState $$1) {
        double $$32;
        this.resetFallDistance();
        double $$2 = this.getX();
        double $$3 = this.getY();
        double $$4 = this.getZ();
        Vec3 $$5 = this.getPos($$2, $$3, $$4);
        $$3 = $$0.getY();
        boolean $$6 = false;
        boolean $$7 = false;
        if ($$1.is(Blocks.POWERED_RAIL)) {
            $$6 = $$1.getValue(PoweredRailBlock.POWERED);
            $$7 = !$$6;
        }
        double $$8 = 0.0078125;
        if (this.isInWater()) {
            $$8 *= 0.2;
        }
        Vec3 $$9 = this.getDeltaMovement();
        RailShape $$10 = $$1.getValue(((BaseRailBlock)$$1.getBlock()).getShapeProperty());
        switch ($$10) {
            case ASCENDING_EAST: {
                this.setDeltaMovement($$9.add(-$$8, 0.0, 0.0));
                $$3 += 1.0;
                break;
            }
            case ASCENDING_WEST: {
                this.setDeltaMovement($$9.add($$8, 0.0, 0.0));
                $$3 += 1.0;
                break;
            }
            case ASCENDING_NORTH: {
                this.setDeltaMovement($$9.add(0.0, 0.0, $$8));
                $$3 += 1.0;
                break;
            }
            case ASCENDING_SOUTH: {
                this.setDeltaMovement($$9.add(0.0, 0.0, -$$8));
                $$3 += 1.0;
            }
        }
        $$9 = this.getDeltaMovement();
        Pair<Vec3i, Vec3i> $$11 = AbstractMinecart.exits($$10);
        Vec3i $$12 = (Vec3i)$$11.getFirst();
        Vec3i $$13 = (Vec3i)$$11.getSecond();
        double $$14 = $$13.getX() - $$12.getX();
        double $$15 = $$13.getZ() - $$12.getZ();
        double $$16 = Math.sqrt((double)($$14 * $$14 + $$15 * $$15));
        double $$17 = $$9.x * $$14 + $$9.z * $$15;
        if ($$17 < 0.0) {
            $$14 = -$$14;
            $$15 = -$$15;
        }
        double $$18 = Math.min((double)2.0, (double)$$9.horizontalDistance());
        $$9 = new Vec3($$18 * $$14 / $$16, $$9.y, $$18 * $$15 / $$16);
        this.setDeltaMovement($$9);
        Entity $$19 = this.getFirstPassenger();
        if ($$19 instanceof Player) {
            Vec3 $$20 = $$19.getDeltaMovement();
            double $$21 = $$20.horizontalDistanceSqr();
            double $$22 = this.getDeltaMovement().horizontalDistanceSqr();
            if ($$21 > 1.0E-4 && $$22 < 0.01) {
                this.setDeltaMovement(this.getDeltaMovement().add($$20.x * 0.1, 0.0, $$20.z * 0.1));
                $$7 = false;
            }
        }
        if ($$7) {
            double $$23 = this.getDeltaMovement().horizontalDistance();
            if ($$23 < 0.03) {
                this.setDeltaMovement(Vec3.ZERO);
            } else {
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.5, 0.0, 0.5));
            }
        }
        double $$24 = (double)$$0.getX() + 0.5 + (double)$$12.getX() * 0.5;
        double $$25 = (double)$$0.getZ() + 0.5 + (double)$$12.getZ() * 0.5;
        double $$26 = (double)$$0.getX() + 0.5 + (double)$$13.getX() * 0.5;
        double $$27 = (double)$$0.getZ() + 0.5 + (double)$$13.getZ() * 0.5;
        $$14 = $$26 - $$24;
        $$15 = $$27 - $$25;
        if ($$14 == 0.0) {
            double $$28 = $$4 - (double)$$0.getZ();
        } else if ($$15 == 0.0) {
            double $$29 = $$2 - (double)$$0.getX();
        } else {
            double $$30 = $$2 - $$24;
            double $$31 = $$4 - $$25;
            $$32 = ($$30 * $$14 + $$31 * $$15) * 2.0;
        }
        $$2 = $$24 + $$14 * $$32;
        $$4 = $$25 + $$15 * $$32;
        this.setPos($$2, $$3, $$4);
        double $$33 = this.isVehicle() ? 0.75 : 1.0;
        double $$34 = this.getMaxSpeed();
        $$9 = this.getDeltaMovement();
        this.move(MoverType.SELF, new Vec3(Mth.clamp($$33 * $$9.x, -$$34, $$34), 0.0, Mth.clamp($$33 * $$9.z, -$$34, $$34)));
        if ($$12.getY() != 0 && Mth.floor(this.getX()) - $$0.getX() == $$12.getX() && Mth.floor(this.getZ()) - $$0.getZ() == $$12.getZ()) {
            this.setPos(this.getX(), this.getY() + (double)$$12.getY(), this.getZ());
        } else if ($$13.getY() != 0 && Mth.floor(this.getX()) - $$0.getX() == $$13.getX() && Mth.floor(this.getZ()) - $$0.getZ() == $$13.getZ()) {
            this.setPos(this.getX(), this.getY() + (double)$$13.getY(), this.getZ());
        }
        this.applyNaturalSlowdown();
        Vec3 $$35 = this.getPos(this.getX(), this.getY(), this.getZ());
        if ($$35 != null && $$5 != null) {
            double $$36 = ($$5.y - $$35.y) * 0.05;
            Vec3 $$37 = this.getDeltaMovement();
            double $$38 = $$37.horizontalDistance();
            if ($$38 > 0.0) {
                this.setDeltaMovement($$37.multiply(($$38 + $$36) / $$38, 1.0, ($$38 + $$36) / $$38));
            }
            this.setPos(this.getX(), $$35.y, this.getZ());
        }
        int $$39 = Mth.floor(this.getX());
        int $$40 = Mth.floor(this.getZ());
        if ($$39 != $$0.getX() || $$40 != $$0.getZ()) {
            Vec3 $$41 = this.getDeltaMovement();
            double $$42 = $$41.horizontalDistance();
            this.setDeltaMovement($$42 * (double)($$39 - $$0.getX()), $$41.y, $$42 * (double)($$40 - $$0.getZ()));
        }
        if ($$6) {
            Vec3 $$43 = this.getDeltaMovement();
            double $$44 = $$43.horizontalDistance();
            if ($$44 > 0.01) {
                double $$45 = 0.06;
                this.setDeltaMovement($$43.add($$43.x / $$44 * 0.06, 0.0, $$43.z / $$44 * 0.06));
            } else {
                Vec3 $$46 = this.getDeltaMovement();
                double $$47 = $$46.x;
                double $$48 = $$46.z;
                if ($$10 == RailShape.EAST_WEST) {
                    if (this.isRedstoneConductor((BlockPos)$$0.west())) {
                        $$47 = 0.02;
                    } else if (this.isRedstoneConductor((BlockPos)$$0.east())) {
                        $$47 = -0.02;
                    }
                } else if ($$10 == RailShape.NORTH_SOUTH) {
                    if (this.isRedstoneConductor((BlockPos)$$0.north())) {
                        $$48 = 0.02;
                    } else if (this.isRedstoneConductor((BlockPos)$$0.south())) {
                        $$48 = -0.02;
                    }
                } else {
                    return;
                }
                this.setDeltaMovement($$47, $$46.y, $$48);
            }
        }
    }

    private boolean isRedstoneConductor(BlockPos $$0) {
        return this.level.getBlockState($$0).isRedstoneConductor(this.level, $$0);
    }

    protected void applyNaturalSlowdown() {
        double $$0 = this.isVehicle() ? 0.997 : 0.96;
        Vec3 $$1 = this.getDeltaMovement();
        $$1 = $$1.multiply($$0, 0.0, $$0);
        if (this.isInWater()) {
            $$1 = $$1.scale(0.95f);
        }
        this.setDeltaMovement($$1);
    }

    @Nullable
    public Vec3 getPosOffs(double $$0, double $$1, double $$2, double $$3) {
        BlockState $$7;
        int $$6;
        int $$5;
        int $$4 = Mth.floor($$0);
        if (this.level.getBlockState(new BlockPos($$4, ($$5 = Mth.floor($$1)) - 1, $$6 = Mth.floor($$2))).is(BlockTags.RAILS)) {
            --$$5;
        }
        if (BaseRailBlock.isRail($$7 = this.level.getBlockState(new BlockPos($$4, $$5, $$6)))) {
            RailShape $$8 = $$7.getValue(((BaseRailBlock)$$7.getBlock()).getShapeProperty());
            $$1 = $$5;
            if ($$8.isAscending()) {
                $$1 = $$5 + 1;
            }
            Pair<Vec3i, Vec3i> $$9 = AbstractMinecart.exits($$8);
            Vec3i $$10 = (Vec3i)$$9.getFirst();
            Vec3i $$11 = (Vec3i)$$9.getSecond();
            double $$12 = $$11.getX() - $$10.getX();
            double $$13 = $$11.getZ() - $$10.getZ();
            double $$14 = Math.sqrt((double)($$12 * $$12 + $$13 * $$13));
            if ($$10.getY() != 0 && Mth.floor($$0 += ($$12 /= $$14) * $$3) - $$4 == $$10.getX() && Mth.floor($$2 += ($$13 /= $$14) * $$3) - $$6 == $$10.getZ()) {
                $$1 += (double)$$10.getY();
            } else if ($$11.getY() != 0 && Mth.floor($$0) - $$4 == $$11.getX() && Mth.floor($$2) - $$6 == $$11.getZ()) {
                $$1 += (double)$$11.getY();
            }
            return this.getPos($$0, $$1, $$2);
        }
        return null;
    }

    @Nullable
    public Vec3 getPos(double $$0, double $$1, double $$2) {
        BlockState $$6;
        int $$5;
        int $$4;
        int $$3 = Mth.floor($$0);
        if (this.level.getBlockState(new BlockPos($$3, ($$4 = Mth.floor($$1)) - 1, $$5 = Mth.floor($$2))).is(BlockTags.RAILS)) {
            --$$4;
        }
        if (BaseRailBlock.isRail($$6 = this.level.getBlockState(new BlockPos($$3, $$4, $$5)))) {
            double $$24;
            RailShape $$7 = $$6.getValue(((BaseRailBlock)$$6.getBlock()).getShapeProperty());
            Pair<Vec3i, Vec3i> $$8 = AbstractMinecart.exits($$7);
            Vec3i $$9 = (Vec3i)$$8.getFirst();
            Vec3i $$10 = (Vec3i)$$8.getSecond();
            double $$11 = (double)$$3 + 0.5 + (double)$$9.getX() * 0.5;
            double $$12 = (double)$$4 + 0.0625 + (double)$$9.getY() * 0.5;
            double $$13 = (double)$$5 + 0.5 + (double)$$9.getZ() * 0.5;
            double $$14 = (double)$$3 + 0.5 + (double)$$10.getX() * 0.5;
            double $$15 = (double)$$4 + 0.0625 + (double)$$10.getY() * 0.5;
            double $$16 = (double)$$5 + 0.5 + (double)$$10.getZ() * 0.5;
            double $$17 = $$14 - $$11;
            double $$18 = ($$15 - $$12) * 2.0;
            double $$19 = $$16 - $$13;
            if ($$17 == 0.0) {
                double $$20 = $$2 - (double)$$5;
            } else if ($$19 == 0.0) {
                double $$21 = $$0 - (double)$$3;
            } else {
                double $$22 = $$0 - $$11;
                double $$23 = $$2 - $$13;
                $$24 = ($$22 * $$17 + $$23 * $$19) * 2.0;
            }
            $$0 = $$11 + $$17 * $$24;
            $$1 = $$12 + $$18 * $$24;
            $$2 = $$13 + $$19 * $$24;
            if ($$18 < 0.0) {
                $$1 += 1.0;
            } else if ($$18 > 0.0) {
                $$1 += 0.5;
            }
            return new Vec3($$0, $$1, $$2);
        }
        return null;
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        AABB $$0 = this.getBoundingBox();
        if (this.hasCustomDisplay()) {
            return $$0.inflate((double)Math.abs((int)this.getDisplayOffset()) / 16.0);
        }
        return $$0;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag $$0) {
        if ($$0.getBoolean("CustomDisplayTile")) {
            this.setDisplayBlockState(NbtUtils.readBlockState(this.level.holderLookup(Registries.BLOCK), $$0.getCompound("DisplayState")));
            this.setDisplayOffset($$0.getInt("DisplayOffset"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag $$0) {
        if (this.hasCustomDisplay()) {
            $$0.putBoolean("CustomDisplayTile", true);
            $$0.put("DisplayState", NbtUtils.writeBlockState(this.getDisplayBlockState()));
            $$0.putInt("DisplayOffset", this.getDisplayOffset());
        }
    }

    @Override
    public void push(Entity $$0) {
        double $$2;
        if (this.level.isClientSide) {
            return;
        }
        if ($$0.noPhysics || this.noPhysics) {
            return;
        }
        if (this.hasPassenger($$0)) {
            return;
        }
        double $$1 = $$0.getX() - this.getX();
        double $$3 = $$1 * $$1 + ($$2 = $$0.getZ() - this.getZ()) * $$2;
        if ($$3 >= (double)1.0E-4f) {
            $$3 = Math.sqrt((double)$$3);
            $$1 /= $$3;
            $$2 /= $$3;
            double $$4 = 1.0 / $$3;
            if ($$4 > 1.0) {
                $$4 = 1.0;
            }
            $$1 *= $$4;
            $$2 *= $$4;
            $$1 *= (double)0.1f;
            $$2 *= (double)0.1f;
            $$1 *= 0.5;
            $$2 *= 0.5;
            if ($$0 instanceof AbstractMinecart) {
                Vec3 $$8;
                double $$6;
                double $$5 = $$0.getX() - this.getX();
                Vec3 $$7 = new Vec3($$5, 0.0, $$6 = $$0.getZ() - this.getZ()).normalize();
                double $$9 = Math.abs((double)$$7.dot($$8 = new Vec3(Mth.cos(this.getYRot() * ((float)Math.PI / 180)), 0.0, Mth.sin(this.getYRot() * ((float)Math.PI / 180))).normalize()));
                if ($$9 < (double)0.8f) {
                    return;
                }
                Vec3 $$10 = this.getDeltaMovement();
                Vec3 $$11 = $$0.getDeltaMovement();
                if (((AbstractMinecart)$$0).getMinecartType() == Type.FURNACE && this.getMinecartType() != Type.FURNACE) {
                    this.setDeltaMovement($$10.multiply(0.2, 1.0, 0.2));
                    this.push($$11.x - $$1, 0.0, $$11.z - $$2);
                    $$0.setDeltaMovement($$11.multiply(0.95, 1.0, 0.95));
                } else if (((AbstractMinecart)$$0).getMinecartType() != Type.FURNACE && this.getMinecartType() == Type.FURNACE) {
                    $$0.setDeltaMovement($$11.multiply(0.2, 1.0, 0.2));
                    $$0.push($$10.x + $$1, 0.0, $$10.z + $$2);
                    this.setDeltaMovement($$10.multiply(0.95, 1.0, 0.95));
                } else {
                    double $$12 = ($$11.x + $$10.x) / 2.0;
                    double $$13 = ($$11.z + $$10.z) / 2.0;
                    this.setDeltaMovement($$10.multiply(0.2, 1.0, 0.2));
                    this.push($$12 - $$1, 0.0, $$13 - $$2);
                    $$0.setDeltaMovement($$11.multiply(0.2, 1.0, 0.2));
                    $$0.push($$12 + $$1, 0.0, $$13 + $$2);
                }
            } else {
                this.push(-$$1, 0.0, -$$2);
                $$0.push($$1 / 4.0, 0.0, $$2 / 4.0);
            }
        }
    }

    @Override
    public void lerpTo(double $$0, double $$1, double $$2, float $$3, float $$4, int $$5, boolean $$6) {
        this.lx = $$0;
        this.ly = $$1;
        this.lz = $$2;
        this.lyr = $$3;
        this.lxr = $$4;
        this.lSteps = $$5 + 2;
        this.setDeltaMovement(this.lxd, this.lyd, this.lzd);
    }

    @Override
    public void lerpMotion(double $$0, double $$1, double $$2) {
        this.lxd = $$0;
        this.lyd = $$1;
        this.lzd = $$2;
        this.setDeltaMovement(this.lxd, this.lyd, this.lzd);
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

    public void setHurtDir(int $$0) {
        this.entityData.set(DATA_ID_HURTDIR, $$0);
    }

    public int getHurtDir() {
        return this.entityData.get(DATA_ID_HURTDIR);
    }

    public abstract Type getMinecartType();

    public BlockState getDisplayBlockState() {
        if (!this.hasCustomDisplay()) {
            return this.getDefaultDisplayBlockState();
        }
        return Block.stateById(this.getEntityData().get(DATA_ID_DISPLAY_BLOCK));
    }

    public BlockState getDefaultDisplayBlockState() {
        return Blocks.AIR.defaultBlockState();
    }

    public int getDisplayOffset() {
        if (!this.hasCustomDisplay()) {
            return this.getDefaultDisplayOffset();
        }
        return this.getEntityData().get(DATA_ID_DISPLAY_OFFSET);
    }

    public int getDefaultDisplayOffset() {
        return 6;
    }

    public void setDisplayBlockState(BlockState $$0) {
        this.getEntityData().set(DATA_ID_DISPLAY_BLOCK, Block.getId($$0));
        this.setCustomDisplay(true);
    }

    public void setDisplayOffset(int $$0) {
        this.getEntityData().set(DATA_ID_DISPLAY_OFFSET, $$0);
        this.setCustomDisplay(true);
    }

    public boolean hasCustomDisplay() {
        return this.getEntityData().get(DATA_ID_CUSTOM_DISPLAY);
    }

    public void setCustomDisplay(boolean $$0) {
        this.getEntityData().set(DATA_ID_CUSTOM_DISPLAY, $$0);
    }

    @Override
    public ItemStack getPickResult() {
        Item $$5;
        switch (this.getMinecartType()) {
            case FURNACE: {
                Item $$0 = Items.FURNACE_MINECART;
                break;
            }
            case CHEST: {
                Item $$1 = Items.CHEST_MINECART;
                break;
            }
            case TNT: {
                Item $$2 = Items.TNT_MINECART;
                break;
            }
            case HOPPER: {
                Item $$3 = Items.HOPPER_MINECART;
                break;
            }
            case COMMAND_BLOCK: {
                Item $$4 = Items.COMMAND_BLOCK_MINECART;
                break;
            }
            default: {
                $$5 = Items.MINECART;
            }
        }
        return new ItemStack($$5);
    }

    public static enum Type {
        RIDEABLE,
        CHEST,
        FURNACE,
        TNT,
        SPAWNER,
        HOPPER,
        COMMAND_BLOCK;

    }
}