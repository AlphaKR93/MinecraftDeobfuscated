/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Byte
 *  java.lang.Class
 *  java.lang.Enum
 *  java.lang.Float
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.EnumSet
 *  java.util.List
 *  java.util.Optional
 *  java.util.UUID
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 *  net.minecraft.world.level.Level
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.minecraft.world.entity.monster;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Shulker
extends AbstractGolem
implements VariantHolder<Optional<DyeColor>>,
Enemy {
    private static final UUID COVERED_ARMOR_MODIFIER_UUID = UUID.fromString((String)"7E0292F2-9434-48D5-A29F-9583AF7DF27F");
    private static final AttributeModifier COVERED_ARMOR_MODIFIER = new AttributeModifier(COVERED_ARMOR_MODIFIER_UUID, "Covered armor bonus", 20.0, AttributeModifier.Operation.ADDITION);
    protected static final EntityDataAccessor<Direction> DATA_ATTACH_FACE_ID = SynchedEntityData.defineId(Shulker.class, EntityDataSerializers.DIRECTION);
    protected static final EntityDataAccessor<Byte> DATA_PEEK_ID = SynchedEntityData.defineId(Shulker.class, EntityDataSerializers.BYTE);
    protected static final EntityDataAccessor<Byte> DATA_COLOR_ID = SynchedEntityData.defineId(Shulker.class, EntityDataSerializers.BYTE);
    private static final int TELEPORT_STEPS = 6;
    private static final byte NO_COLOR = 16;
    private static final byte DEFAULT_COLOR = 16;
    private static final int MAX_TELEPORT_DISTANCE = 8;
    private static final int OTHER_SHULKER_SCAN_RADIUS = 8;
    private static final int OTHER_SHULKER_LIMIT = 5;
    private static final float PEEK_PER_TICK = 0.05f;
    static final Vector3f FORWARD = (Vector3f)Util.make(() -> {
        Vec3i $$0 = Direction.SOUTH.getNormal();
        return new Vector3f((float)$$0.getX(), (float)$$0.getY(), (float)$$0.getZ());
    });
    private float currentPeekAmountO;
    private float currentPeekAmount;
    @Nullable
    private BlockPos clientOldAttachPosition;
    private int clientSideTeleportInterpolation;
    private static final float MAX_LID_OPEN = 1.0f;

    public Shulker(EntityType<? extends Shulker> $$0, Level $$1) {
        super((EntityType<? extends AbstractGolem>)$$0, $$1);
        this.xpReward = 5;
        this.lookControl = new ShulkerLookControl(this);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 8.0f, 0.02f, true));
        this.goalSelector.addGoal(4, new ShulkerAttackGoal());
        this.goalSelector.addGoal(7, new ShulkerPeekGoal());
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, this.getClass()).setAlertOthers(new Class[0]));
        this.targetSelector.addGoal(2, new ShulkerNearestAttackGoal(this));
        this.targetSelector.addGoal(3, new ShulkerDefenseAttackGoal(this));
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SHULKER_AMBIENT;
    }

    @Override
    public void playAmbientSound() {
        if (!this.isClosed()) {
            super.playAmbientSound();
        }
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SHULKER_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource $$0) {
        if (this.isClosed()) {
            return SoundEvents.SHULKER_HURT_CLOSED;
        }
        return SoundEvents.SHULKER_HURT;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ATTACH_FACE_ID, Direction.DOWN);
        this.entityData.define(DATA_PEEK_ID, (byte)0);
        this.entityData.define(DATA_COLOR_ID, (byte)16);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 30.0);
    }

    @Override
    protected BodyRotationControl createBodyControl() {
        return new ShulkerBodyRotationControl(this);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$0) {
        super.readAdditionalSaveData($$0);
        this.setAttachFace(Direction.from3DDataValue($$0.getByte("AttachFace")));
        this.entityData.set(DATA_PEEK_ID, $$0.getByte("Peek"));
        if ($$0.contains("Color", 99)) {
            this.entityData.set(DATA_COLOR_ID, $$0.getByte("Color"));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        super.addAdditionalSaveData($$0);
        $$0.putByte("AttachFace", (byte)this.getAttachFace().get3DDataValue());
        $$0.putByte("Peek", this.entityData.get(DATA_PEEK_ID));
        $$0.putByte("Color", this.entityData.get(DATA_COLOR_ID));
    }

    @Override
    public void tick() {
        super.tick();
        if (!(this.level.isClientSide || this.isPassenger() || this.canStayAt(this.blockPosition(), this.getAttachFace()))) {
            this.findNewAttachment();
        }
        if (this.updatePeekAmount()) {
            this.onPeekAmountChange();
        }
        if (this.level.isClientSide) {
            if (this.clientSideTeleportInterpolation > 0) {
                --this.clientSideTeleportInterpolation;
            } else {
                this.clientOldAttachPosition = null;
            }
        }
    }

    private void findNewAttachment() {
        Direction $$0 = this.findAttachableSurface(this.blockPosition());
        if ($$0 != null) {
            this.setAttachFace($$0);
        } else {
            this.teleportSomewhere();
        }
    }

    @Override
    protected AABB makeBoundingBox() {
        float $$0 = Shulker.getPhysicalPeek(this.currentPeekAmount);
        Direction $$1 = this.getAttachFace().getOpposite();
        float $$2 = this.getType().getWidth() / 2.0f;
        return Shulker.getProgressAabb($$1, $$0).move(this.getX() - (double)$$2, this.getY(), this.getZ() - (double)$$2);
    }

    private static float getPhysicalPeek(float $$0) {
        return 0.5f - Mth.sin((0.5f + $$0) * (float)Math.PI) * 0.5f;
    }

    private boolean updatePeekAmount() {
        this.currentPeekAmountO = this.currentPeekAmount;
        float $$0 = (float)this.getRawPeekAmount() * 0.01f;
        if (this.currentPeekAmount == $$0) {
            return false;
        }
        this.currentPeekAmount = this.currentPeekAmount > $$0 ? Mth.clamp(this.currentPeekAmount - 0.05f, $$0, 1.0f) : Mth.clamp(this.currentPeekAmount + 0.05f, 0.0f, $$0);
        return true;
    }

    private void onPeekAmountChange() {
        this.reapplyPosition();
        float $$02 = Shulker.getPhysicalPeek(this.currentPeekAmount);
        float $$1 = Shulker.getPhysicalPeek(this.currentPeekAmountO);
        Direction $$2 = this.getAttachFace().getOpposite();
        float $$3 = $$02 - $$1;
        if ($$3 <= 0.0f) {
            return;
        }
        List $$4 = this.level.getEntities((Entity)this, Shulker.getProgressDeltaAabb($$2, $$1, $$02).move(this.getX() - 0.5, this.getY(), this.getZ() - 0.5), EntitySelector.NO_SPECTATORS.and($$0 -> !$$0.isPassengerOfSameVehicle(this)));
        for (Entity $$5 : $$4) {
            if ($$5 instanceof Shulker || $$5.noPhysics) continue;
            $$5.move(MoverType.SHULKER, new Vec3($$3 * (float)$$2.getStepX(), $$3 * (float)$$2.getStepY(), $$3 * (float)$$2.getStepZ()));
        }
    }

    public static AABB getProgressAabb(Direction $$0, float $$1) {
        return Shulker.getProgressDeltaAabb($$0, -1.0f, $$1);
    }

    public static AABB getProgressDeltaAabb(Direction $$0, float $$1, float $$2) {
        double $$3 = Math.max((float)$$1, (float)$$2);
        double $$4 = Math.min((float)$$1, (float)$$2);
        return new AABB(BlockPos.ZERO).expandTowards((double)$$0.getStepX() * $$3, (double)$$0.getStepY() * $$3, (double)$$0.getStepZ() * $$3).contract((double)(-$$0.getStepX()) * (1.0 + $$4), (double)(-$$0.getStepY()) * (1.0 + $$4), (double)(-$$0.getStepZ()) * (1.0 + $$4));
    }

    @Override
    public double getMyRidingOffset() {
        EntityType<?> $$0 = this.getVehicle().getType();
        if (this.getVehicle() instanceof Boat || $$0 == EntityType.MINECART) {
            return 0.1875 - this.getVehicle().getPassengersRidingOffset();
        }
        return super.getMyRidingOffset();
    }

    @Override
    public boolean startRiding(Entity $$0, boolean $$1) {
        if (this.level.isClientSide()) {
            this.clientOldAttachPosition = null;
            this.clientSideTeleportInterpolation = 0;
        }
        this.setAttachFace(Direction.DOWN);
        return super.startRiding($$0, $$1);
    }

    @Override
    public void stopRiding() {
        super.stopRiding();
        if (this.level.isClientSide) {
            this.clientOldAttachPosition = this.blockPosition();
        }
        this.yBodyRotO = 0.0f;
        this.yBodyRot = 0.0f;
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor $$0, DifficultyInstance $$1, MobSpawnType $$2, @Nullable SpawnGroupData $$3, @Nullable CompoundTag $$4) {
        this.setYRot(0.0f);
        this.yHeadRot = this.getYRot();
        this.setOldPosAndRot();
        return super.finalizeSpawn($$0, $$1, $$2, $$3, $$4);
    }

    @Override
    public void move(MoverType $$0, Vec3 $$1) {
        if ($$0 == MoverType.SHULKER_BOX) {
            this.teleportSomewhere();
        } else {
            super.move($$0, $$1);
        }
    }

    @Override
    public Vec3 getDeltaMovement() {
        return Vec3.ZERO;
    }

    @Override
    public void setDeltaMovement(Vec3 $$0) {
    }

    @Override
    public void setPos(double $$0, double $$1, double $$2) {
        BlockPos $$3 = this.blockPosition();
        if (this.isPassenger()) {
            super.setPos($$0, $$1, $$2);
        } else {
            super.setPos((double)Mth.floor($$0) + 0.5, Mth.floor($$1 + 0.5), (double)Mth.floor($$2) + 0.5);
        }
        if (this.tickCount == 0) {
            return;
        }
        BlockPos $$4 = this.blockPosition();
        if (!$$4.equals($$3)) {
            this.entityData.set(DATA_PEEK_ID, (byte)0);
            this.hasImpulse = true;
            if (this.level.isClientSide && !this.isPassenger() && !$$4.equals(this.clientOldAttachPosition)) {
                this.clientOldAttachPosition = $$3;
                this.clientSideTeleportInterpolation = 6;
                this.xOld = this.getX();
                this.yOld = this.getY();
                this.zOld = this.getZ();
            }
        }
    }

    @Nullable
    protected Direction findAttachableSurface(BlockPos $$0) {
        for (Direction $$1 : Direction.values()) {
            if (!this.canStayAt($$0, $$1)) continue;
            return $$1;
        }
        return null;
    }

    boolean canStayAt(BlockPos $$0, Direction $$1) {
        if (this.isPositionBlocked($$0)) {
            return false;
        }
        Direction $$2 = $$1.getOpposite();
        if (!this.level.loadedAndEntityCanStandOnFace((BlockPos)$$0.relative($$1), (Entity)this, $$2)) {
            return false;
        }
        AABB $$3 = Shulker.getProgressAabb($$2, 1.0f).move($$0).deflate(1.0E-6);
        return this.level.noCollision((Entity)this, $$3);
    }

    private boolean isPositionBlocked(BlockPos $$0) {
        BlockState $$1 = this.level.getBlockState($$0);
        if ($$1.isAir()) {
            return false;
        }
        boolean $$2 = $$1.is(Blocks.MOVING_PISTON) && $$0.equals(this.blockPosition());
        return !$$2;
    }

    protected boolean teleportSomewhere() {
        if (this.isNoAi() || !this.isAlive()) {
            return false;
        }
        BlockPos $$0 = this.blockPosition();
        for (int $$1 = 0; $$1 < 5; ++$$1) {
            Direction $$3;
            BlockPos $$2 = $$0.offset(Mth.randomBetweenInclusive(this.random, -8, 8), Mth.randomBetweenInclusive(this.random, -8, 8), Mth.randomBetweenInclusive(this.random, -8, 8));
            if ($$2.getY() <= this.level.getMinBuildHeight() || !this.level.isEmptyBlock($$2) || !this.level.getWorldBorder().isWithinBounds($$2) || !this.level.noCollision((Entity)this, new AABB($$2).deflate(1.0E-6)) || ($$3 = this.findAttachableSurface($$2)) == null) continue;
            this.unRide();
            this.setAttachFace($$3);
            this.playSound(SoundEvents.SHULKER_TELEPORT, 1.0f, 1.0f);
            this.setPos((double)$$2.getX() + 0.5, $$2.getY(), (double)$$2.getZ() + 0.5);
            this.level.gameEvent(GameEvent.TELEPORT, $$0, GameEvent.Context.of(this));
            this.entityData.set(DATA_PEEK_ID, (byte)0);
            this.setTarget(null);
            return true;
        }
        return false;
    }

    @Override
    public void lerpTo(double $$0, double $$1, double $$2, float $$3, float $$4, int $$5, boolean $$6) {
        this.lerpSteps = 0;
        this.setPos($$0, $$1, $$2);
        this.setRot($$3, $$4);
    }

    @Override
    public boolean hurt(DamageSource $$0, float $$1) {
        Entity $$2;
        if (this.isClosed() && ($$2 = $$0.getDirectEntity()) instanceof AbstractArrow) {
            return false;
        }
        if (super.hurt($$0, $$1)) {
            Entity $$3;
            if ((double)this.getHealth() < (double)this.getMaxHealth() * 0.5 && this.random.nextInt(4) == 0) {
                this.teleportSomewhere();
            } else if ($$0.isProjectile() && ($$3 = $$0.getDirectEntity()) != null && $$3.getType() == EntityType.SHULKER_BULLET) {
                this.hitByShulkerBullet();
            }
            return true;
        }
        return false;
    }

    private boolean isClosed() {
        return this.getRawPeekAmount() == 0;
    }

    private void hitByShulkerBullet() {
        Vec3 $$0 = this.position();
        AABB $$1 = this.getBoundingBox();
        if (this.isClosed() || !this.teleportSomewhere()) {
            return;
        }
        int $$2 = this.level.getEntities(EntityType.SHULKER, $$1.inflate(8.0), Entity::isAlive).size();
        float $$3 = (float)($$2 - 1) / 5.0f;
        if (this.level.random.nextFloat() < $$3) {
            return;
        }
        Shulker $$4 = EntityType.SHULKER.create(this.level);
        if ($$4 != null) {
            $$4.setVariant(this.getVariant());
            $$4.moveTo($$0);
            this.level.addFreshEntity((Entity)$$4);
        }
    }

    @Override
    public boolean canBeCollidedWith() {
        return this.isAlive();
    }

    public Direction getAttachFace() {
        return this.entityData.get(DATA_ATTACH_FACE_ID);
    }

    private void setAttachFace(Direction $$0) {
        this.entityData.set(DATA_ATTACH_FACE_ID, $$0);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> $$0) {
        if (DATA_ATTACH_FACE_ID.equals($$0)) {
            this.setBoundingBox(this.makeBoundingBox());
        }
        super.onSyncedDataUpdated($$0);
    }

    private int getRawPeekAmount() {
        return this.entityData.get(DATA_PEEK_ID).byteValue();
    }

    void setRawPeekAmount(int $$0) {
        if (!this.level.isClientSide) {
            this.getAttribute(Attributes.ARMOR).removeModifier(COVERED_ARMOR_MODIFIER);
            if ($$0 == 0) {
                this.getAttribute(Attributes.ARMOR).addPermanentModifier(COVERED_ARMOR_MODIFIER);
                this.playSound(SoundEvents.SHULKER_CLOSE, 1.0f, 1.0f);
                this.gameEvent(GameEvent.CONTAINER_CLOSE);
            } else {
                this.playSound(SoundEvents.SHULKER_OPEN, 1.0f, 1.0f);
                this.gameEvent(GameEvent.CONTAINER_OPEN);
            }
        }
        this.entityData.set(DATA_PEEK_ID, (byte)$$0);
    }

    public float getClientPeekAmount(float $$0) {
        return Mth.lerp($$0, this.currentPeekAmountO, this.currentPeekAmount);
    }

    @Override
    protected float getStandingEyeHeight(Pose $$0, EntityDimensions $$1) {
        return 0.5f;
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket $$0) {
        super.recreateFromPacket($$0);
        this.yBodyRot = 0.0f;
        this.yBodyRotO = 0.0f;
    }

    @Override
    public int getMaxHeadXRot() {
        return 180;
    }

    @Override
    public int getMaxHeadYRot() {
        return 180;
    }

    @Override
    public void push(Entity $$0) {
    }

    @Override
    public float getPickRadius() {
        return 0.0f;
    }

    public Optional<Vec3> getRenderPosition(float $$0) {
        if (this.clientOldAttachPosition == null || this.clientSideTeleportInterpolation <= 0) {
            return Optional.empty();
        }
        double $$1 = (double)((float)this.clientSideTeleportInterpolation - $$0) / 6.0;
        $$1 *= $$1;
        BlockPos $$2 = this.blockPosition();
        double $$3 = (double)($$2.getX() - this.clientOldAttachPosition.getX()) * $$1;
        double $$4 = (double)($$2.getY() - this.clientOldAttachPosition.getY()) * $$1;
        double $$5 = (double)($$2.getZ() - this.clientOldAttachPosition.getZ()) * $$1;
        return Optional.of((Object)new Vec3(-$$3, -$$4, -$$5));
    }

    @Override
    public void setVariant(Optional<DyeColor> $$02) {
        this.entityData.set(DATA_COLOR_ID, (Byte)$$02.map($$0 -> (byte)$$0.getId()).orElse((Object)16));
    }

    @Override
    public Optional<DyeColor> getVariant() {
        return Optional.ofNullable((Object)this.getColor());
    }

    @Nullable
    public DyeColor getColor() {
        byte $$0 = this.entityData.get(DATA_COLOR_ID);
        if ($$0 == 16 || $$0 > 15) {
            return null;
        }
        return DyeColor.byId($$0);
    }

    class ShulkerLookControl
    extends LookControl {
        public ShulkerLookControl(Mob $$0) {
            super($$0);
        }

        @Override
        protected void clampHeadRotationToBody() {
        }

        @Override
        protected Optional<Float> getYRotD() {
            Direction $$0 = Shulker.this.getAttachFace().getOpposite();
            Vector3f $$1 = $$0.getRotation().transform(new Vector3f((Vector3fc)FORWARD));
            Vec3i $$2 = $$0.getNormal();
            Vector3f $$3 = new Vector3f((float)$$2.getX(), (float)$$2.getY(), (float)$$2.getZ());
            $$3.cross((Vector3fc)$$1);
            double $$4 = this.wantedX - this.mob.getX();
            double $$5 = this.wantedY - this.mob.getEyeY();
            double $$6 = this.wantedZ - this.mob.getZ();
            Vector3f $$7 = new Vector3f((float)$$4, (float)$$5, (float)$$6);
            float $$8 = $$3.dot((Vector3fc)$$7);
            float $$9 = $$1.dot((Vector3fc)$$7);
            return Math.abs((float)$$8) > 1.0E-5f || Math.abs((float)$$9) > 1.0E-5f ? Optional.of((Object)Float.valueOf((float)((float)(Mth.atan2(-$$8, $$9) * 57.2957763671875)))) : Optional.empty();
        }

        @Override
        protected Optional<Float> getXRotD() {
            return Optional.of((Object)Float.valueOf((float)0.0f));
        }
    }

    class ShulkerAttackGoal
    extends Goal {
        private int attackTime;

        public ShulkerAttackGoal() {
            this.setFlags((EnumSet<Goal.Flag>)EnumSet.of((Enum)Goal.Flag.MOVE, (Enum)Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity $$0 = Shulker.this.getTarget();
            if ($$0 == null || !$$0.isAlive()) {
                return false;
            }
            return Shulker.this.level.getDifficulty() != Difficulty.PEACEFUL;
        }

        @Override
        public void start() {
            this.attackTime = 20;
            Shulker.this.setRawPeekAmount(100);
        }

        @Override
        public void stop() {
            Shulker.this.setRawPeekAmount(0);
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            if (Shulker.this.level.getDifficulty() == Difficulty.PEACEFUL) {
                return;
            }
            --this.attackTime;
            LivingEntity $$0 = Shulker.this.getTarget();
            if ($$0 == null) {
                return;
            }
            Shulker.this.getLookControl().setLookAt($$0, 180.0f, 180.0f);
            double $$1 = Shulker.this.distanceToSqr($$0);
            if ($$1 < 400.0) {
                if (this.attackTime <= 0) {
                    this.attackTime = 20 + Shulker.this.random.nextInt(10) * 20 / 2;
                    Shulker.this.level.addFreshEntity((Entity)new ShulkerBullet(Shulker.this.level, Shulker.this, $$0, Shulker.this.getAttachFace().getAxis()));
                    Shulker.this.playSound(SoundEvents.SHULKER_SHOOT, 2.0f, (Shulker.this.random.nextFloat() - Shulker.this.random.nextFloat()) * 0.2f + 1.0f);
                }
            } else {
                Shulker.this.setTarget(null);
            }
            super.tick();
        }
    }

    class ShulkerPeekGoal
    extends Goal {
        private int peekTime;

        ShulkerPeekGoal() {
        }

        @Override
        public boolean canUse() {
            return Shulker.this.getTarget() == null && Shulker.this.random.nextInt(ShulkerPeekGoal.reducedTickDelay(40)) == 0 && Shulker.this.canStayAt(Shulker.this.blockPosition(), Shulker.this.getAttachFace());
        }

        @Override
        public boolean canContinueToUse() {
            return Shulker.this.getTarget() == null && this.peekTime > 0;
        }

        @Override
        public void start() {
            this.peekTime = this.adjustedTickDelay(20 * (1 + Shulker.this.random.nextInt(3)));
            Shulker.this.setRawPeekAmount(30);
        }

        @Override
        public void stop() {
            if (Shulker.this.getTarget() == null) {
                Shulker.this.setRawPeekAmount(0);
            }
        }

        @Override
        public void tick() {
            --this.peekTime;
        }
    }

    class ShulkerNearestAttackGoal
    extends NearestAttackableTargetGoal<Player> {
        public ShulkerNearestAttackGoal(Shulker $$0) {
            super((Mob)$$0, Player.class, true);
        }

        @Override
        public boolean canUse() {
            if (Shulker.this.level.getDifficulty() == Difficulty.PEACEFUL) {
                return false;
            }
            return super.canUse();
        }

        @Override
        protected AABB getTargetSearchArea(double $$0) {
            Direction $$1 = ((Shulker)this.mob).getAttachFace();
            if ($$1.getAxis() == Direction.Axis.X) {
                return this.mob.getBoundingBox().inflate(4.0, $$0, $$0);
            }
            if ($$1.getAxis() == Direction.Axis.Z) {
                return this.mob.getBoundingBox().inflate($$0, $$0, 4.0);
            }
            return this.mob.getBoundingBox().inflate($$0, 4.0, $$0);
        }
    }

    static class ShulkerDefenseAttackGoal
    extends NearestAttackableTargetGoal<LivingEntity> {
        public ShulkerDefenseAttackGoal(Shulker $$02) {
            super($$02, LivingEntity.class, 10, true, false, (Predicate<LivingEntity>)((Predicate)$$0 -> $$0 instanceof Enemy));
        }

        @Override
        public boolean canUse() {
            if (this.mob.getTeam() == null) {
                return false;
            }
            return super.canUse();
        }

        @Override
        protected AABB getTargetSearchArea(double $$0) {
            Direction $$1 = ((Shulker)this.mob).getAttachFace();
            if ($$1.getAxis() == Direction.Axis.X) {
                return this.mob.getBoundingBox().inflate(4.0, $$0, $$0);
            }
            if ($$1.getAxis() == Direction.Axis.Z) {
                return this.mob.getBoundingBox().inflate($$0, $$0, 4.0);
            }
            return this.mob.getBoundingBox().inflate($$0, 4.0, $$0);
        }
    }

    static class ShulkerBodyRotationControl
    extends BodyRotationControl {
        public ShulkerBodyRotationControl(Mob $$0) {
            super($$0);
        }

        @Override
        public void clientTick() {
        }
    }
}