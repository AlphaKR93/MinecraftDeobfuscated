/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Byte
 *  java.lang.Double
 *  java.lang.Integer
 *  java.lang.Iterable
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Iterator
 *  java.util.List
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.decoration;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Rotations;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ArmorStand
extends LivingEntity {
    public static final int WOBBLE_TIME = 5;
    private static final boolean ENABLE_ARMS = true;
    private static final Rotations DEFAULT_HEAD_POSE = new Rotations(0.0f, 0.0f, 0.0f);
    private static final Rotations DEFAULT_BODY_POSE = new Rotations(0.0f, 0.0f, 0.0f);
    private static final Rotations DEFAULT_LEFT_ARM_POSE = new Rotations(-10.0f, 0.0f, -10.0f);
    private static final Rotations DEFAULT_RIGHT_ARM_POSE = new Rotations(-15.0f, 0.0f, 10.0f);
    private static final Rotations DEFAULT_LEFT_LEG_POSE = new Rotations(-1.0f, 0.0f, -1.0f);
    private static final Rotations DEFAULT_RIGHT_LEG_POSE = new Rotations(1.0f, 0.0f, 1.0f);
    private static final EntityDimensions MARKER_DIMENSIONS = new EntityDimensions(0.0f, 0.0f, true);
    private static final EntityDimensions BABY_DIMENSIONS = EntityType.ARMOR_STAND.getDimensions().scale(0.5f);
    private static final double FEET_OFFSET = 0.1;
    private static final double CHEST_OFFSET = 0.9;
    private static final double LEGS_OFFSET = 0.4;
    private static final double HEAD_OFFSET = 1.6;
    public static final int DISABLE_TAKING_OFFSET = 8;
    public static final int DISABLE_PUTTING_OFFSET = 16;
    public static final int CLIENT_FLAG_SMALL = 1;
    public static final int CLIENT_FLAG_SHOW_ARMS = 4;
    public static final int CLIENT_FLAG_NO_BASEPLATE = 8;
    public static final int CLIENT_FLAG_MARKER = 16;
    public static final EntityDataAccessor<Byte> DATA_CLIENT_FLAGS = SynchedEntityData.defineId(ArmorStand.class, EntityDataSerializers.BYTE);
    public static final EntityDataAccessor<Rotations> DATA_HEAD_POSE = SynchedEntityData.defineId(ArmorStand.class, EntityDataSerializers.ROTATIONS);
    public static final EntityDataAccessor<Rotations> DATA_BODY_POSE = SynchedEntityData.defineId(ArmorStand.class, EntityDataSerializers.ROTATIONS);
    public static final EntityDataAccessor<Rotations> DATA_LEFT_ARM_POSE = SynchedEntityData.defineId(ArmorStand.class, EntityDataSerializers.ROTATIONS);
    public static final EntityDataAccessor<Rotations> DATA_RIGHT_ARM_POSE = SynchedEntityData.defineId(ArmorStand.class, EntityDataSerializers.ROTATIONS);
    public static final EntityDataAccessor<Rotations> DATA_LEFT_LEG_POSE = SynchedEntityData.defineId(ArmorStand.class, EntityDataSerializers.ROTATIONS);
    public static final EntityDataAccessor<Rotations> DATA_RIGHT_LEG_POSE = SynchedEntityData.defineId(ArmorStand.class, EntityDataSerializers.ROTATIONS);
    private static final Predicate<Entity> RIDABLE_MINECARTS = $$0 -> $$0 instanceof AbstractMinecart && ((AbstractMinecart)$$0).getMinecartType() == AbstractMinecart.Type.RIDEABLE;
    private final NonNullList<ItemStack> handItems = NonNullList.withSize(2, ItemStack.EMPTY);
    private final NonNullList<ItemStack> armorItems = NonNullList.withSize(4, ItemStack.EMPTY);
    private boolean invisible;
    public long lastHit;
    private int disabledSlots;
    private Rotations headPose = DEFAULT_HEAD_POSE;
    private Rotations bodyPose = DEFAULT_BODY_POSE;
    private Rotations leftArmPose = DEFAULT_LEFT_ARM_POSE;
    private Rotations rightArmPose = DEFAULT_RIGHT_ARM_POSE;
    private Rotations leftLegPose = DEFAULT_LEFT_LEG_POSE;
    private Rotations rightLegPose = DEFAULT_RIGHT_LEG_POSE;

    public ArmorStand(EntityType<? extends ArmorStand> $$0, Level $$1) {
        super((EntityType<? extends LivingEntity>)$$0, $$1);
        this.maxUpStep = 0.0f;
    }

    public ArmorStand(Level $$0, double $$1, double $$2, double $$3) {
        this((EntityType<? extends ArmorStand>)EntityType.ARMOR_STAND, $$0);
        this.setPos($$1, $$2, $$3);
    }

    @Override
    public void refreshDimensions() {
        double $$0 = this.getX();
        double $$1 = this.getY();
        double $$2 = this.getZ();
        super.refreshDimensions();
        this.setPos($$0, $$1, $$2);
    }

    private boolean hasPhysics() {
        return !this.isMarker() && !this.isNoGravity();
    }

    @Override
    public boolean isEffectiveAi() {
        return super.isEffectiveAi() && this.hasPhysics();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_CLIENT_FLAGS, (byte)0);
        this.entityData.define(DATA_HEAD_POSE, DEFAULT_HEAD_POSE);
        this.entityData.define(DATA_BODY_POSE, DEFAULT_BODY_POSE);
        this.entityData.define(DATA_LEFT_ARM_POSE, DEFAULT_LEFT_ARM_POSE);
        this.entityData.define(DATA_RIGHT_ARM_POSE, DEFAULT_RIGHT_ARM_POSE);
        this.entityData.define(DATA_LEFT_LEG_POSE, DEFAULT_LEFT_LEG_POSE);
        this.entityData.define(DATA_RIGHT_LEG_POSE, DEFAULT_RIGHT_LEG_POSE);
    }

    @Override
    public Iterable<ItemStack> getHandSlots() {
        return this.handItems;
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return this.armorItems;
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot $$0) {
        switch ($$0.getType()) {
            case HAND: {
                return this.handItems.get($$0.getIndex());
            }
            case ARMOR: {
                return this.armorItems.get($$0.getIndex());
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlot $$0, ItemStack $$1) {
        this.verifyEquippedItem($$1);
        switch ($$0.getType()) {
            case HAND: {
                this.onEquipItem($$0, this.handItems.set($$0.getIndex(), $$1), $$1);
                break;
            }
            case ARMOR: {
                this.onEquipItem($$0, this.armorItems.set($$0.getIndex(), $$1), $$1);
            }
        }
    }

    @Override
    public boolean canTakeItem(ItemStack $$0) {
        EquipmentSlot $$1 = Mob.getEquipmentSlotForItem($$0);
        return this.getItemBySlot($$1).isEmpty() && !this.isDisabled($$1);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        super.addAdditionalSaveData($$0);
        ListTag $$1 = new ListTag();
        Iterator iterator = this.armorItems.iterator();
        while (iterator.hasNext()) {
            ItemStack $$2 = (ItemStack)iterator.next();
            CompoundTag $$3 = new CompoundTag();
            if (!$$2.isEmpty()) {
                $$2.save($$3);
            }
            $$1.add($$3);
        }
        $$0.put("ArmorItems", $$1);
        ListTag $$4 = new ListTag();
        Iterator iterator2 = this.handItems.iterator();
        while (iterator2.hasNext()) {
            ItemStack $$5 = (ItemStack)iterator2.next();
            CompoundTag $$6 = new CompoundTag();
            if (!$$5.isEmpty()) {
                $$5.save($$6);
            }
            $$4.add($$6);
        }
        $$0.put("HandItems", $$4);
        $$0.putBoolean("Invisible", this.isInvisible());
        $$0.putBoolean("Small", this.isSmall());
        $$0.putBoolean("ShowArms", this.isShowArms());
        $$0.putInt("DisabledSlots", this.disabledSlots);
        $$0.putBoolean("NoBasePlate", this.isNoBasePlate());
        if (this.isMarker()) {
            $$0.putBoolean("Marker", this.isMarker());
        }
        $$0.put("Pose", this.writePose());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$0) {
        super.readAdditionalSaveData($$0);
        if ($$0.contains("ArmorItems", 9)) {
            ListTag $$1 = $$0.getList("ArmorItems", 10);
            for (int $$2 = 0; $$2 < this.armorItems.size(); ++$$2) {
                this.armorItems.set($$2, ItemStack.of($$1.getCompound($$2)));
            }
        }
        if ($$0.contains("HandItems", 9)) {
            ListTag $$3 = $$0.getList("HandItems", 10);
            for (int $$4 = 0; $$4 < this.handItems.size(); ++$$4) {
                this.handItems.set($$4, ItemStack.of($$3.getCompound($$4)));
            }
        }
        this.setInvisible($$0.getBoolean("Invisible"));
        this.setSmall($$0.getBoolean("Small"));
        this.setShowArms($$0.getBoolean("ShowArms"));
        this.disabledSlots = $$0.getInt("DisabledSlots");
        this.setNoBasePlate($$0.getBoolean("NoBasePlate"));
        this.setMarker($$0.getBoolean("Marker"));
        this.noPhysics = !this.hasPhysics();
        CompoundTag $$5 = $$0.getCompound("Pose");
        this.readPose($$5);
    }

    private void readPose(CompoundTag $$0) {
        ListTag $$1 = $$0.getList("Head", 5);
        this.setHeadPose($$1.isEmpty() ? DEFAULT_HEAD_POSE : new Rotations($$1));
        ListTag $$2 = $$0.getList("Body", 5);
        this.setBodyPose($$2.isEmpty() ? DEFAULT_BODY_POSE : new Rotations($$2));
        ListTag $$3 = $$0.getList("LeftArm", 5);
        this.setLeftArmPose($$3.isEmpty() ? DEFAULT_LEFT_ARM_POSE : new Rotations($$3));
        ListTag $$4 = $$0.getList("RightArm", 5);
        this.setRightArmPose($$4.isEmpty() ? DEFAULT_RIGHT_ARM_POSE : new Rotations($$4));
        ListTag $$5 = $$0.getList("LeftLeg", 5);
        this.setLeftLegPose($$5.isEmpty() ? DEFAULT_LEFT_LEG_POSE : new Rotations($$5));
        ListTag $$6 = $$0.getList("RightLeg", 5);
        this.setRightLegPose($$6.isEmpty() ? DEFAULT_RIGHT_LEG_POSE : new Rotations($$6));
    }

    private CompoundTag writePose() {
        CompoundTag $$0 = new CompoundTag();
        if (!DEFAULT_HEAD_POSE.equals(this.headPose)) {
            $$0.put("Head", this.headPose.save());
        }
        if (!DEFAULT_BODY_POSE.equals(this.bodyPose)) {
            $$0.put("Body", this.bodyPose.save());
        }
        if (!DEFAULT_LEFT_ARM_POSE.equals(this.leftArmPose)) {
            $$0.put("LeftArm", this.leftArmPose.save());
        }
        if (!DEFAULT_RIGHT_ARM_POSE.equals(this.rightArmPose)) {
            $$0.put("RightArm", this.rightArmPose.save());
        }
        if (!DEFAULT_LEFT_LEG_POSE.equals(this.leftLegPose)) {
            $$0.put("LeftLeg", this.leftLegPose.save());
        }
        if (!DEFAULT_RIGHT_LEG_POSE.equals(this.rightLegPose)) {
            $$0.put("RightLeg", this.rightLegPose.save());
        }
        return $$0;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void doPush(Entity $$0) {
    }

    @Override
    protected void pushEntities() {
        List<Entity> $$0 = this.level.getEntities(this, this.getBoundingBox(), RIDABLE_MINECARTS);
        for (int $$1 = 0; $$1 < $$0.size(); ++$$1) {
            Entity $$2 = (Entity)$$0.get($$1);
            if (!(this.distanceToSqr($$2) <= 0.2)) continue;
            $$2.push(this);
        }
    }

    @Override
    public InteractionResult interactAt(Player $$0, Vec3 $$1, InteractionHand $$2) {
        ItemStack $$3 = $$0.getItemInHand($$2);
        if (this.isMarker() || $$3.is(Items.NAME_TAG)) {
            return InteractionResult.PASS;
        }
        if ($$0.isSpectator()) {
            return InteractionResult.SUCCESS;
        }
        if ($$0.level.isClientSide) {
            return InteractionResult.CONSUME;
        }
        EquipmentSlot $$4 = Mob.getEquipmentSlotForItem($$3);
        if ($$3.isEmpty()) {
            EquipmentSlot $$6;
            EquipmentSlot $$5 = this.getClickedSlot($$1);
            EquipmentSlot equipmentSlot = $$6 = this.isDisabled($$5) ? $$4 : $$5;
            if (this.hasItemInSlot($$6) && this.swapItem($$0, $$6, $$3, $$2)) {
                return InteractionResult.SUCCESS;
            }
        } else {
            if (this.isDisabled($$4)) {
                return InteractionResult.FAIL;
            }
            if ($$4.getType() == EquipmentSlot.Type.HAND && !this.isShowArms()) {
                return InteractionResult.FAIL;
            }
            if (this.swapItem($$0, $$4, $$3, $$2)) {
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private EquipmentSlot getClickedSlot(Vec3 $$0) {
        EquipmentSlot $$1 = EquipmentSlot.MAINHAND;
        boolean $$2 = this.isSmall();
        double $$3 = $$2 ? $$0.y * 2.0 : $$0.y;
        EquipmentSlot $$4 = EquipmentSlot.FEET;
        if ($$3 >= 0.1) {
            double d = $$2 ? 0.8 : 0.45;
            if ($$3 < 0.1 + d && this.hasItemInSlot($$4)) {
                return EquipmentSlot.FEET;
            }
        }
        double d = $$2 ? 0.3 : 0.0;
        if ($$3 >= 0.9 + d) {
            double d2 = $$2 ? 1.0 : 0.7;
            if ($$3 < 0.9 + d2 && this.hasItemInSlot(EquipmentSlot.CHEST)) {
                return EquipmentSlot.CHEST;
            }
        }
        if ($$3 >= 0.4) {
            double d3 = $$2 ? 1.0 : 0.8;
            if ($$3 < 0.4 + d3 && this.hasItemInSlot(EquipmentSlot.LEGS)) {
                return EquipmentSlot.LEGS;
            }
        }
        if ($$3 >= 1.6 && this.hasItemInSlot(EquipmentSlot.HEAD)) {
            return EquipmentSlot.HEAD;
        }
        if (this.hasItemInSlot(EquipmentSlot.MAINHAND)) return $$1;
        if (!this.hasItemInSlot(EquipmentSlot.OFFHAND)) return $$1;
        return EquipmentSlot.OFFHAND;
    }

    private boolean isDisabled(EquipmentSlot $$0) {
        return (this.disabledSlots & 1 << $$0.getFilterFlag()) != 0 || $$0.getType() == EquipmentSlot.Type.HAND && !this.isShowArms();
    }

    private boolean swapItem(Player $$0, EquipmentSlot $$1, ItemStack $$2, InteractionHand $$3) {
        ItemStack $$4 = this.getItemBySlot($$1);
        if (!$$4.isEmpty() && (this.disabledSlots & 1 << $$1.getFilterFlag() + 8) != 0) {
            return false;
        }
        if ($$4.isEmpty() && (this.disabledSlots & 1 << $$1.getFilterFlag() + 16) != 0) {
            return false;
        }
        if ($$0.getAbilities().instabuild && $$4.isEmpty() && !$$2.isEmpty()) {
            ItemStack $$5 = $$2.copy();
            $$5.setCount(1);
            this.setItemSlot($$1, $$5);
            return true;
        }
        if (!$$2.isEmpty() && $$2.getCount() > 1) {
            if (!$$4.isEmpty()) {
                return false;
            }
            ItemStack $$6 = $$2.copy();
            $$6.setCount(1);
            this.setItemSlot($$1, $$6);
            $$2.shrink(1);
            return true;
        }
        this.setItemSlot($$1, $$2);
        $$0.setItemInHand($$3, $$4);
        return true;
    }

    @Override
    public boolean hurt(DamageSource $$0, float $$1) {
        if (this.level.isClientSide || this.isRemoved()) {
            return false;
        }
        if (DamageSource.OUT_OF_WORLD.equals($$0)) {
            this.kill();
            return false;
        }
        if (this.isInvulnerableTo($$0) || this.invisible || this.isMarker()) {
            return false;
        }
        if ($$0.isExplosion()) {
            this.brokenByAnything($$0);
            this.kill();
            return false;
        }
        if (DamageSource.IN_FIRE.equals($$0)) {
            if (this.isOnFire()) {
                this.causeDamage($$0, 0.15f);
            } else {
                this.setSecondsOnFire(5);
            }
            return false;
        }
        if (DamageSource.ON_FIRE.equals($$0) && this.getHealth() > 0.5f) {
            this.causeDamage($$0, 4.0f);
            return false;
        }
        boolean $$2 = $$0.getDirectEntity() instanceof AbstractArrow;
        boolean $$3 = $$2 && ((AbstractArrow)$$0.getDirectEntity()).getPierceLevel() > 0;
        boolean $$4 = "player".equals((Object)$$0.getMsgId());
        if (!$$4 && !$$2) {
            return false;
        }
        if ($$0.getEntity() instanceof Player && !((Player)$$0.getEntity()).getAbilities().mayBuild) {
            return false;
        }
        if ($$0.isCreativePlayer()) {
            this.playBrokenSound();
            this.showBreakingParticles();
            this.kill();
            return $$3;
        }
        long $$5 = this.level.getGameTime();
        if ($$5 - this.lastHit <= 5L || $$2) {
            this.brokenByPlayer($$0);
            this.showBreakingParticles();
            this.kill();
        } else {
            this.level.broadcastEntityEvent(this, (byte)32);
            this.gameEvent(GameEvent.ENTITY_DAMAGE, $$0.getEntity());
            this.lastHit = $$5;
        }
        return true;
    }

    @Override
    public void handleEntityEvent(byte $$0) {
        if ($$0 == 32) {
            if (this.level.isClientSide) {
                this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ARMOR_STAND_HIT, this.getSoundSource(), 0.3f, 1.0f, false);
                this.lastHit = this.level.getGameTime();
            }
        } else {
            super.handleEntityEvent($$0);
        }
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double $$0) {
        double $$1 = this.getBoundingBox().getSize() * 4.0;
        if (Double.isNaN((double)$$1) || $$1 == 0.0) {
            $$1 = 4.0;
        }
        return $$0 < ($$1 *= 64.0) * $$1;
    }

    private void showBreakingParticles() {
        if (this.level instanceof ServerLevel) {
            ((ServerLevel)this.level).sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.OAK_PLANKS.defaultBlockState()), this.getX(), this.getY(0.6666666666666666), this.getZ(), 10, this.getBbWidth() / 4.0f, this.getBbHeight() / 4.0f, this.getBbWidth() / 4.0f, 0.05);
        }
    }

    private void causeDamage(DamageSource $$0, float $$1) {
        float $$2 = this.getHealth();
        if (($$2 -= $$1) <= 0.5f) {
            this.brokenByAnything($$0);
            this.kill();
        } else {
            this.setHealth($$2);
            this.gameEvent(GameEvent.ENTITY_DAMAGE, $$0.getEntity());
        }
    }

    private void brokenByPlayer(DamageSource $$0) {
        Block.popResource(this.level, this.blockPosition(), new ItemStack(Items.ARMOR_STAND));
        this.brokenByAnything($$0);
    }

    private void brokenByAnything(DamageSource $$0) {
        this.playBrokenSound();
        this.dropAllDeathLoot($$0);
        for (int $$1 = 0; $$1 < this.handItems.size(); ++$$1) {
            ItemStack $$2 = this.handItems.get($$1);
            if ($$2.isEmpty()) continue;
            Block.popResource(this.level, (BlockPos)this.blockPosition().above(), $$2);
            this.handItems.set($$1, ItemStack.EMPTY);
        }
        for (int $$3 = 0; $$3 < this.armorItems.size(); ++$$3) {
            ItemStack $$4 = this.armorItems.get($$3);
            if ($$4.isEmpty()) continue;
            Block.popResource(this.level, (BlockPos)this.blockPosition().above(), $$4);
            this.armorItems.set($$3, ItemStack.EMPTY);
        }
    }

    private void playBrokenSound() {
        this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ARMOR_STAND_BREAK, this.getSoundSource(), 1.0f, 1.0f);
    }

    @Override
    protected float tickHeadTurn(float $$0, float $$1) {
        this.yBodyRotO = this.yRotO;
        this.yBodyRot = this.getYRot();
        return 0.0f;
    }

    @Override
    protected float getStandingEyeHeight(Pose $$0, EntityDimensions $$1) {
        return $$1.height * (this.isBaby() ? 0.5f : 0.9f);
    }

    @Override
    public double getMyRidingOffset() {
        return this.isMarker() ? 0.0 : (double)0.1f;
    }

    @Override
    public void travel(Vec3 $$0) {
        if (!this.hasPhysics()) {
            return;
        }
        super.travel($$0);
    }

    @Override
    public void setYBodyRot(float $$0) {
        this.yBodyRotO = this.yRotO = $$0;
        this.yHeadRotO = this.yHeadRot = $$0;
    }

    @Override
    public void setYHeadRot(float $$0) {
        this.yBodyRotO = this.yRotO = $$0;
        this.yHeadRotO = this.yHeadRot = $$0;
    }

    @Override
    public void tick() {
        Rotations $$5;
        Rotations $$4;
        Rotations $$3;
        Rotations $$2;
        Rotations $$1;
        super.tick();
        Rotations $$0 = this.entityData.get(DATA_HEAD_POSE);
        if (!this.headPose.equals($$0)) {
            this.setHeadPose($$0);
        }
        if (!this.bodyPose.equals($$1 = this.entityData.get(DATA_BODY_POSE))) {
            this.setBodyPose($$1);
        }
        if (!this.leftArmPose.equals($$2 = this.entityData.get(DATA_LEFT_ARM_POSE))) {
            this.setLeftArmPose($$2);
        }
        if (!this.rightArmPose.equals($$3 = this.entityData.get(DATA_RIGHT_ARM_POSE))) {
            this.setRightArmPose($$3);
        }
        if (!this.leftLegPose.equals($$4 = this.entityData.get(DATA_LEFT_LEG_POSE))) {
            this.setLeftLegPose($$4);
        }
        if (!this.rightLegPose.equals($$5 = this.entityData.get(DATA_RIGHT_LEG_POSE))) {
            this.setRightLegPose($$5);
        }
    }

    @Override
    protected void updateInvisibilityStatus() {
        this.setInvisible(this.invisible);
    }

    @Override
    public void setInvisible(boolean $$0) {
        this.invisible = $$0;
        super.setInvisible($$0);
    }

    @Override
    public boolean isBaby() {
        return this.isSmall();
    }

    @Override
    public void kill() {
        this.remove(Entity.RemovalReason.KILLED);
        this.gameEvent(GameEvent.ENTITY_DIE);
    }

    @Override
    public boolean ignoreExplosion() {
        return this.isInvisible();
    }

    @Override
    public PushReaction getPistonPushReaction() {
        if (this.isMarker()) {
            return PushReaction.IGNORE;
        }
        return super.getPistonPushReaction();
    }

    private void setSmall(boolean $$0) {
        this.entityData.set(DATA_CLIENT_FLAGS, this.setBit(this.entityData.get(DATA_CLIENT_FLAGS), 1, $$0));
    }

    public boolean isSmall() {
        return (this.entityData.get(DATA_CLIENT_FLAGS) & 1) != 0;
    }

    private void setShowArms(boolean $$0) {
        this.entityData.set(DATA_CLIENT_FLAGS, this.setBit(this.entityData.get(DATA_CLIENT_FLAGS), 4, $$0));
    }

    public boolean isShowArms() {
        return (this.entityData.get(DATA_CLIENT_FLAGS) & 4) != 0;
    }

    private void setNoBasePlate(boolean $$0) {
        this.entityData.set(DATA_CLIENT_FLAGS, this.setBit(this.entityData.get(DATA_CLIENT_FLAGS), 8, $$0));
    }

    public boolean isNoBasePlate() {
        return (this.entityData.get(DATA_CLIENT_FLAGS) & 8) != 0;
    }

    private void setMarker(boolean $$0) {
        this.entityData.set(DATA_CLIENT_FLAGS, this.setBit(this.entityData.get(DATA_CLIENT_FLAGS), 16, $$0));
    }

    public boolean isMarker() {
        return (this.entityData.get(DATA_CLIENT_FLAGS) & 0x10) != 0;
    }

    private byte setBit(byte $$0, int $$1, boolean $$2) {
        $$0 = $$2 ? (byte)($$0 | $$1) : (byte)($$0 & ~$$1);
        return $$0;
    }

    public void setHeadPose(Rotations $$0) {
        this.headPose = $$0;
        this.entityData.set(DATA_HEAD_POSE, $$0);
    }

    public void setBodyPose(Rotations $$0) {
        this.bodyPose = $$0;
        this.entityData.set(DATA_BODY_POSE, $$0);
    }

    public void setLeftArmPose(Rotations $$0) {
        this.leftArmPose = $$0;
        this.entityData.set(DATA_LEFT_ARM_POSE, $$0);
    }

    public void setRightArmPose(Rotations $$0) {
        this.rightArmPose = $$0;
        this.entityData.set(DATA_RIGHT_ARM_POSE, $$0);
    }

    public void setLeftLegPose(Rotations $$0) {
        this.leftLegPose = $$0;
        this.entityData.set(DATA_LEFT_LEG_POSE, $$0);
    }

    public void setRightLegPose(Rotations $$0) {
        this.rightLegPose = $$0;
        this.entityData.set(DATA_RIGHT_LEG_POSE, $$0);
    }

    public Rotations getHeadPose() {
        return this.headPose;
    }

    public Rotations getBodyPose() {
        return this.bodyPose;
    }

    public Rotations getLeftArmPose() {
        return this.leftArmPose;
    }

    public Rotations getRightArmPose() {
        return this.rightArmPose;
    }

    public Rotations getLeftLegPose() {
        return this.leftLegPose;
    }

    public Rotations getRightLegPose() {
        return this.rightLegPose;
    }

    @Override
    public boolean isPickable() {
        return super.isPickable() && !this.isMarker();
    }

    @Override
    public boolean skipAttackInteraction(Entity $$0) {
        return $$0 instanceof Player && !this.level.mayInteract((Player)$$0, this.blockPosition());
    }

    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    @Override
    public LivingEntity.Fallsounds getFallSounds() {
        return new LivingEntity.Fallsounds(SoundEvents.ARMOR_STAND_FALL, SoundEvents.ARMOR_STAND_FALL);
    }

    @Override
    @Nullable
    protected SoundEvent getHurtSound(DamageSource $$0) {
        return SoundEvents.ARMOR_STAND_HIT;
    }

    @Override
    @Nullable
    protected SoundEvent getDeathSound() {
        return SoundEvents.ARMOR_STAND_BREAK;
    }

    @Override
    public void thunderHit(ServerLevel $$0, LightningBolt $$1) {
    }

    @Override
    public boolean isAffectedByPotions() {
        return false;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> $$0) {
        if (DATA_CLIENT_FLAGS.equals($$0)) {
            this.refreshDimensions();
            this.blocksBuilding = !this.isMarker();
        }
        super.onSyncedDataUpdated($$0);
    }

    @Override
    public boolean attackable() {
        return false;
    }

    @Override
    public EntityDimensions getDimensions(Pose $$0) {
        return this.getDimensionsMarker(this.isMarker());
    }

    private EntityDimensions getDimensionsMarker(boolean $$0) {
        if ($$0) {
            return MARKER_DIMENSIONS;
        }
        return this.isBaby() ? BABY_DIMENSIONS : this.getType().getDimensions();
    }

    @Override
    public Vec3 getLightProbePosition(float $$0) {
        if (this.isMarker()) {
            AABB $$1 = this.getDimensionsMarker(false).makeBoundingBox(this.position());
            BlockPos $$2 = this.blockPosition();
            int $$3 = Integer.MIN_VALUE;
            for (BlockPos $$4 : BlockPos.betweenClosed(new BlockPos($$1.minX, $$1.minY, $$1.minZ), new BlockPos($$1.maxX, $$1.maxY, $$1.maxZ))) {
                int $$5 = Math.max((int)this.level.getBrightness(LightLayer.BLOCK, $$4), (int)this.level.getBrightness(LightLayer.SKY, $$4));
                if ($$5 == 15) {
                    return Vec3.atCenterOf($$4);
                }
                if ($$5 <= $$3) continue;
                $$3 = $$5;
                $$2 = $$4.immutable();
            }
            return Vec3.atCenterOf($$2);
        }
        return super.getLightProbePosition($$0);
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(Items.ARMOR_STAND);
    }

    @Override
    public boolean canBeSeenByAnyone() {
        return !this.isInvisible() && !this.isMarker();
    }
}