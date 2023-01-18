/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Boolean
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.animal;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ItemBasedSteering;
import net.minecraft.world.entity.ItemSteerable;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class Pig
extends Animal
implements ItemSteerable,
Saddleable {
    private static final EntityDataAccessor<Boolean> DATA_SADDLE_ID = SynchedEntityData.defineId(Pig.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_BOOST_TIME = SynchedEntityData.defineId(Pig.class, EntityDataSerializers.INT);
    private static final Ingredient FOOD_ITEMS = Ingredient.of(Items.CARROT, Items.POTATO, Items.BEETROOT);
    private final ItemBasedSteering steering;

    public Pig(EntityType<? extends Pig> $$0, Level $$1) {
        super((EntityType<? extends Animal>)$$0, $$1);
        this.steering = new ItemBasedSteering(this.entityData, DATA_BOOST_TIME, DATA_SADDLE_ID);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.25));
        this.goalSelector.addGoal(3, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.2, Ingredient.of(Items.CARROT_ON_A_STICK), false));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.2, FOOD_ITEMS, false));
        this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.1));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0f));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0).add(Attributes.MOVEMENT_SPEED, 0.25);
    }

    @Override
    @Nullable
    public Entity getControllingPassenger() {
        Entity $$0 = this.getFirstPassenger();
        return $$0 != null && this.canBeControlledBy($$0) ? $$0 : null;
    }

    private boolean canBeControlledBy(Entity $$0) {
        if (this.isSaddled() && $$0 instanceof Player) {
            Player $$1 = (Player)$$0;
            return $$1.getMainHandItem().is(Items.CARROT_ON_A_STICK) || $$1.getOffhandItem().is(Items.CARROT_ON_A_STICK);
        }
        return false;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> $$0) {
        if (DATA_BOOST_TIME.equals($$0) && this.level.isClientSide) {
            this.steering.onSynced();
        }
        super.onSyncedDataUpdated($$0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_SADDLE_ID, false);
        this.entityData.define(DATA_BOOST_TIME, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        super.addAdditionalSaveData($$0);
        this.steering.addAdditionalSaveData($$0);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$0) {
        super.readAdditionalSaveData($$0);
        this.steering.readAdditionalSaveData($$0);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.PIG_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource $$0) {
        return SoundEvents.PIG_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PIG_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos $$0, BlockState $$1) {
        this.playSound(SoundEvents.PIG_STEP, 0.15f, 1.0f);
    }

    @Override
    public InteractionResult mobInteract(Player $$0, InteractionHand $$1) {
        boolean $$2 = this.isFood($$0.getItemInHand($$1));
        if (!$$2 && this.isSaddled() && !this.isVehicle() && !$$0.isSecondaryUseActive()) {
            if (!this.level.isClientSide) {
                $$0.startRiding(this);
            }
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        }
        InteractionResult $$3 = super.mobInteract($$0, $$1);
        if (!$$3.consumesAction()) {
            ItemStack $$4 = $$0.getItemInHand($$1);
            if ($$4.is(Items.SADDLE)) {
                return $$4.interactLivingEntity($$0, this, $$1);
            }
            return InteractionResult.PASS;
        }
        return $$3;
    }

    @Override
    public boolean isSaddleable() {
        return this.isAlive() && !this.isBaby();
    }

    @Override
    protected void dropEquipment() {
        super.dropEquipment();
        if (this.isSaddled()) {
            this.spawnAtLocation(Items.SADDLE);
        }
    }

    @Override
    public boolean isSaddled() {
        return this.steering.hasSaddle();
    }

    @Override
    public void equipSaddle(@Nullable SoundSource $$0) {
        this.steering.setSaddle(true);
        if ($$0 != null) {
            this.level.playSound(null, this, SoundEvents.PIG_SADDLE, $$0, 0.5f, 1.0f);
        }
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity $$0) {
        Direction $$1 = this.getMotionDirection();
        if ($$1.getAxis() == Direction.Axis.Y) {
            return super.getDismountLocationForPassenger($$0);
        }
        int[][] $$2 = DismountHelper.offsetsForDirection($$1);
        BlockPos $$3 = this.blockPosition();
        BlockPos.MutableBlockPos $$4 = new BlockPos.MutableBlockPos();
        for (Pose $$5 : $$0.getDismountPoses()) {
            AABB $$6 = $$0.getLocalBoundsForPose($$5);
            for (int[] $$7 : $$2) {
                Vec3 $$9;
                $$4.set($$3.getX() + $$7[0], $$3.getY(), $$3.getZ() + $$7[1]);
                double $$8 = this.level.getBlockFloorHeight($$4);
                if (!DismountHelper.isBlockFloorValid($$8) || !DismountHelper.canDismountTo(this.level, $$0, $$6.move($$9 = Vec3.upFromBottomCenterOf($$4, $$8)))) continue;
                $$0.setPose($$5);
                return $$9;
            }
        }
        return super.getDismountLocationForPassenger($$0);
    }

    @Override
    public void thunderHit(ServerLevel $$0, LightningBolt $$1) {
        if ($$0.getDifficulty() != Difficulty.PEACEFUL) {
            ZombifiedPiglin $$2 = EntityType.ZOMBIFIED_PIGLIN.create($$0);
            if ($$2 != null) {
                $$2.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_SWORD));
                $$2.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
                $$2.setNoAi(this.isNoAi());
                $$2.setBaby(this.isBaby());
                if (this.hasCustomName()) {
                    $$2.setCustomName(this.getCustomName());
                    $$2.setCustomNameVisible(this.isCustomNameVisible());
                }
                $$2.setPersistenceRequired();
                $$0.addFreshEntity($$2);
                this.discard();
            } else {
                super.thunderHit($$0, $$1);
            }
        } else {
            super.thunderHit($$0, $$1);
        }
    }

    @Override
    public void travel(Vec3 $$0) {
        this.travel(this, this.steering, $$0);
    }

    @Override
    public float getSteeringSpeed() {
        return (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 0.225f;
    }

    @Override
    public void travelWithInput(Vec3 $$0) {
        super.travel($$0);
    }

    @Override
    public boolean boost() {
        return this.steering.boost(this.getRandom());
    }

    @Override
    @Nullable
    public Pig getBreedOffspring(ServerLevel $$0, AgeableMob $$1) {
        return EntityType.PIG.create($$0);
    }

    @Override
    public boolean isFood(ItemStack $$0) {
        return FOOD_ITEMS.test($$0);
    }

    @Override
    public Vec3 getLeashOffset() {
        return new Vec3(0.0, 0.6f * this.getEyeHeight(), this.getBbWidth() * 0.4f);
    }
}