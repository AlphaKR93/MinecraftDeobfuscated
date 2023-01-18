/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Byte
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Optional
 *  java.util.UUID
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.animal.horse;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HasCustomInventoryScreen;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStandGoal;
import net.minecraft.world.entity.ai.goal.RunAroundLikeCrazyGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractHorse
extends Animal
implements ContainerListener,
HasCustomInventoryScreen,
PlayerRideableJumping,
Saddleable {
    public static final int EQUIPMENT_SLOT_OFFSET = 400;
    public static final int CHEST_SLOT_OFFSET = 499;
    public static final int INVENTORY_SLOT_OFFSET = 500;
    private static final Predicate<LivingEntity> PARENT_HORSE_SELECTOR = $$0 -> $$0 instanceof AbstractHorse && ((AbstractHorse)$$0).isBred();
    private static final TargetingConditions MOMMY_TARGETING = TargetingConditions.forNonCombat().range(16.0).ignoreLineOfSight().selector(PARENT_HORSE_SELECTOR);
    private static final Ingredient FOOD_ITEMS = Ingredient.of(Items.WHEAT, Items.SUGAR, Blocks.HAY_BLOCK.asItem(), Items.APPLE, Items.GOLDEN_CARROT, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE);
    private static final EntityDataAccessor<Byte> DATA_ID_FLAGS = SynchedEntityData.defineId(AbstractHorse.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Optional<UUID>> DATA_ID_OWNER_UUID = SynchedEntityData.defineId(AbstractHorse.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final int FLAG_TAME = 2;
    private static final int FLAG_SADDLE = 4;
    private static final int FLAG_BRED = 8;
    private static final int FLAG_EATING = 16;
    private static final int FLAG_STANDING = 32;
    private static final int FLAG_OPEN_MOUTH = 64;
    public static final int INV_SLOT_SADDLE = 0;
    public static final int INV_SLOT_ARMOR = 1;
    public static final int INV_BASE_COUNT = 2;
    private int eatingCounter;
    private int mouthCounter;
    private int standCounter;
    public int tailCounter;
    public int sprintCounter;
    protected boolean isJumping;
    protected SimpleContainer inventory;
    protected int temper;
    protected float playerJumpPendingScale;
    protected boolean allowStandSliding;
    private float eatAnim;
    private float eatAnimO;
    private float standAnim;
    private float standAnimO;
    private float mouthAnim;
    private float mouthAnimO;
    protected boolean canGallop = true;
    protected int gallopSoundCounter;

    protected AbstractHorse(EntityType<? extends AbstractHorse> $$0, Level $$1) {
        super((EntityType<? extends Animal>)$$0, $$1);
        this.maxUpStep = 1.0f;
        this.createInventory();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.2));
        this.goalSelector.addGoal(1, new RunAroundLikeCrazyGoal(this, 1.2));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0, AbstractHorse.class));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.0));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.7));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0f));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        if (this.canPerformRearing()) {
            this.goalSelector.addGoal(9, new RandomStandGoal(this));
        }
        this.addBehaviourGoals();
    }

    protected void addBehaviourGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.25, Ingredient.of(Items.GOLDEN_CARROT, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE), false));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ID_FLAGS, (byte)0);
        this.entityData.define(DATA_ID_OWNER_UUID, Optional.empty());
    }

    protected boolean getFlag(int $$0) {
        return (this.entityData.get(DATA_ID_FLAGS) & $$0) != 0;
    }

    protected void setFlag(int $$0, boolean $$1) {
        byte $$2 = this.entityData.get(DATA_ID_FLAGS);
        if ($$1) {
            this.entityData.set(DATA_ID_FLAGS, (byte)($$2 | $$0));
        } else {
            this.entityData.set(DATA_ID_FLAGS, (byte)($$2 & ~$$0));
        }
    }

    public boolean isTamed() {
        return this.getFlag(2);
    }

    @Nullable
    public UUID getOwnerUUID() {
        return (UUID)this.entityData.get(DATA_ID_OWNER_UUID).orElse(null);
    }

    public void setOwnerUUID(@Nullable UUID $$0) {
        this.entityData.set(DATA_ID_OWNER_UUID, Optional.ofNullable((Object)$$0));
    }

    public boolean isJumping() {
        return this.isJumping;
    }

    public void setTamed(boolean $$0) {
        this.setFlag(2, $$0);
    }

    public void setIsJumping(boolean $$0) {
        this.isJumping = $$0;
    }

    @Override
    protected void onLeashDistance(float $$0) {
        if ($$0 > 6.0f && this.isEating()) {
            this.setEating(false);
        }
    }

    public boolean isEating() {
        return this.getFlag(16);
    }

    public boolean isStanding() {
        return this.getFlag(32);
    }

    public boolean isBred() {
        return this.getFlag(8);
    }

    public void setBred(boolean $$0) {
        this.setFlag(8, $$0);
    }

    @Override
    public boolean isSaddleable() {
        return this.isAlive() && !this.isBaby() && this.isTamed();
    }

    @Override
    public void equipSaddle(@Nullable SoundSource $$0) {
        this.inventory.setItem(0, new ItemStack(Items.SADDLE));
        if ($$0 != null) {
            this.level.playSound(null, this, this.getSaddleSoundEvent(), $$0, 0.5f, 1.0f);
        }
    }

    public void equipArmor(Player $$0, ItemStack $$1) {
        if (this.isArmor($$1)) {
            this.inventory.setItem(1, new ItemStack($$1.getItem()));
            if (!$$0.getAbilities().instabuild) {
                $$1.shrink(1);
            }
        }
    }

    @Override
    public boolean isSaddled() {
        return this.getFlag(4);
    }

    public int getTemper() {
        return this.temper;
    }

    public void setTemper(int $$0) {
        this.temper = $$0;
    }

    public int modifyTemper(int $$0) {
        int $$1 = Mth.clamp(this.getTemper() + $$0, 0, this.getMaxTemper());
        this.setTemper($$1);
        return $$1;
    }

    @Override
    public boolean isPushable() {
        return !this.isVehicle();
    }

    private void eating() {
        SoundEvent $$0;
        this.openMouth();
        if (!this.isSilent() && ($$0 = this.getEatingSound()) != null) {
            this.level.playSound(null, this.getX(), this.getY(), this.getZ(), $$0, this.getSoundSource(), 1.0f, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.2f);
        }
    }

    @Override
    public boolean causeFallDamage(float $$0, float $$1, DamageSource $$2) {
        int $$3;
        if ($$0 > 1.0f) {
            this.playSound(SoundEvents.HORSE_LAND, 0.4f, 1.0f);
        }
        if (($$3 = this.calculateFallDamage($$0, $$1)) <= 0) {
            return false;
        }
        this.hurt($$2, $$3);
        if (this.isVehicle()) {
            for (Entity $$4 : this.getIndirectPassengers()) {
                $$4.hurt($$2, $$3);
            }
        }
        this.playBlockFallSound();
        return true;
    }

    @Override
    protected int calculateFallDamage(float $$0, float $$1) {
        return Mth.ceil(($$0 * 0.5f - 3.0f) * $$1);
    }

    protected int getInventorySize() {
        return 2;
    }

    protected void createInventory() {
        SimpleContainer $$0 = this.inventory;
        this.inventory = new SimpleContainer(this.getInventorySize());
        if ($$0 != null) {
            $$0.removeListener(this);
            int $$1 = Math.min((int)$$0.getContainerSize(), (int)this.inventory.getContainerSize());
            for (int $$2 = 0; $$2 < $$1; ++$$2) {
                ItemStack $$3 = $$0.getItem($$2);
                if ($$3.isEmpty()) continue;
                this.inventory.setItem($$2, $$3.copy());
            }
        }
        this.inventory.addListener(this);
        this.updateContainerEquipment();
    }

    protected void updateContainerEquipment() {
        if (this.level.isClientSide) {
            return;
        }
        this.setFlag(4, !this.inventory.getItem(0).isEmpty());
    }

    @Override
    public void containerChanged(Container $$0) {
        boolean $$1 = this.isSaddled();
        this.updateContainerEquipment();
        if (this.tickCount > 20 && !$$1 && this.isSaddled()) {
            this.playSound(SoundEvents.HORSE_SADDLE, 0.5f, 1.0f);
        }
    }

    public double getCustomJump() {
        return this.getAttributeValue(Attributes.JUMP_STRENGTH);
    }

    @Override
    public boolean hurt(DamageSource $$0, float $$1) {
        boolean $$2 = super.hurt($$0, $$1);
        if ($$2 && this.random.nextInt(3) == 0) {
            this.standIfPossible();
        }
        return $$2;
    }

    protected boolean canPerformRearing() {
        return true;
    }

    @Nullable
    protected SoundEvent getEatingSound() {
        return null;
    }

    @Nullable
    protected SoundEvent getAngrySound() {
        return null;
    }

    @Override
    protected void playStepSound(BlockPos $$0, BlockState $$1) {
        if ($$1.getMaterial().isLiquid()) {
            return;
        }
        BlockState $$2 = this.level.getBlockState((BlockPos)$$0.above());
        SoundType $$3 = $$1.getSoundType();
        if ($$2.is(Blocks.SNOW)) {
            $$3 = $$2.getSoundType();
        }
        if (this.isVehicle() && this.canGallop) {
            ++this.gallopSoundCounter;
            if (this.gallopSoundCounter > 5 && this.gallopSoundCounter % 3 == 0) {
                this.playGallopSound($$3);
            } else if (this.gallopSoundCounter <= 5) {
                this.playSound(SoundEvents.HORSE_STEP_WOOD, $$3.getVolume() * 0.15f, $$3.getPitch());
            }
        } else if ($$3 == SoundType.WOOD) {
            this.playSound(SoundEvents.HORSE_STEP_WOOD, $$3.getVolume() * 0.15f, $$3.getPitch());
        } else {
            this.playSound(SoundEvents.HORSE_STEP, $$3.getVolume() * 0.15f, $$3.getPitch());
        }
    }

    protected void playGallopSound(SoundType $$0) {
        this.playSound(SoundEvents.HORSE_GALLOP, $$0.getVolume() * 0.15f, $$0.getPitch());
    }

    public static AttributeSupplier.Builder createBaseHorseAttributes() {
        return Mob.createMobAttributes().add(Attributes.JUMP_STRENGTH).add(Attributes.MAX_HEALTH, 53.0).add(Attributes.MOVEMENT_SPEED, 0.225f);
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return 6;
    }

    public int getMaxTemper() {
        return 100;
    }

    @Override
    protected float getSoundVolume() {
        return 0.8f;
    }

    @Override
    public int getAmbientSoundInterval() {
        return 400;
    }

    @Override
    public void openCustomInventoryScreen(Player $$0) {
        if (!this.level.isClientSide && (!this.isVehicle() || this.hasPassenger($$0)) && this.isTamed()) {
            $$0.openHorseInventory(this, this.inventory);
        }
    }

    public InteractionResult fedFood(Player $$0, ItemStack $$1) {
        boolean $$2 = this.handleEating($$0, $$1);
        if (!$$0.getAbilities().instabuild) {
            $$1.shrink(1);
        }
        if (this.level.isClientSide) {
            return InteractionResult.CONSUME;
        }
        return $$2 ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }

    protected boolean handleEating(Player $$0, ItemStack $$1) {
        boolean $$2 = false;
        float $$3 = 0.0f;
        int $$4 = 0;
        int $$5 = 0;
        if ($$1.is(Items.WHEAT)) {
            $$3 = 2.0f;
            $$4 = 20;
            $$5 = 3;
        } else if ($$1.is(Items.SUGAR)) {
            $$3 = 1.0f;
            $$4 = 30;
            $$5 = 3;
        } else if ($$1.is(Blocks.HAY_BLOCK.asItem())) {
            $$3 = 20.0f;
            $$4 = 180;
        } else if ($$1.is(Items.APPLE)) {
            $$3 = 3.0f;
            $$4 = 60;
            $$5 = 3;
        } else if ($$1.is(Items.GOLDEN_CARROT)) {
            $$3 = 4.0f;
            $$4 = 60;
            $$5 = 5;
            if (!this.level.isClientSide && this.isTamed() && this.getAge() == 0 && !this.isInLove()) {
                $$2 = true;
                this.setInLove($$0);
            }
        } else if ($$1.is(Items.GOLDEN_APPLE) || $$1.is(Items.ENCHANTED_GOLDEN_APPLE)) {
            $$3 = 10.0f;
            $$4 = 240;
            $$5 = 10;
            if (!this.level.isClientSide && this.isTamed() && this.getAge() == 0 && !this.isInLove()) {
                $$2 = true;
                this.setInLove($$0);
            }
        }
        if (this.getHealth() < this.getMaxHealth() && $$3 > 0.0f) {
            this.heal($$3);
            $$2 = true;
        }
        if (this.isBaby() && $$4 > 0) {
            this.level.addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), 0.0, 0.0, 0.0);
            if (!this.level.isClientSide) {
                this.ageUp($$4);
            }
            $$2 = true;
        }
        if ($$5 > 0 && ($$2 || !this.isTamed()) && this.getTemper() < this.getMaxTemper()) {
            $$2 = true;
            if (!this.level.isClientSide) {
                this.modifyTemper($$5);
            }
        }
        if ($$2) {
            this.eating();
            this.gameEvent(GameEvent.EAT);
        }
        return $$2;
    }

    protected void doPlayerRide(Player $$0) {
        this.setEating(false);
        this.setStanding(false);
        if (!this.level.isClientSide) {
            $$0.setYRot(this.getYRot());
            $$0.setXRot(this.getXRot());
            $$0.startRiding(this);
        }
    }

    @Override
    public boolean isImmobile() {
        return super.isImmobile() && this.isVehicle() && this.isSaddled() || this.isEating() || this.isStanding();
    }

    @Override
    public boolean isFood(ItemStack $$0) {
        return FOOD_ITEMS.test($$0);
    }

    private void moveTail() {
        this.tailCounter = 1;
    }

    @Override
    protected void dropEquipment() {
        super.dropEquipment();
        if (this.inventory == null) {
            return;
        }
        for (int $$0 = 0; $$0 < this.inventory.getContainerSize(); ++$$0) {
            ItemStack $$1 = this.inventory.getItem($$0);
            if ($$1.isEmpty() || EnchantmentHelper.hasVanishingCurse($$1)) continue;
            this.spawnAtLocation($$1);
        }
    }

    @Override
    public void aiStep() {
        if (this.random.nextInt(200) == 0) {
            this.moveTail();
        }
        super.aiStep();
        if (this.level.isClientSide || !this.isAlive()) {
            return;
        }
        if (this.random.nextInt(900) == 0 && this.deathTime == 0) {
            this.heal(1.0f);
        }
        if (this.canEatGrass()) {
            if (!this.isEating() && !this.isVehicle() && this.random.nextInt(300) == 0 && this.level.getBlockState((BlockPos)this.blockPosition().below()).is(Blocks.GRASS_BLOCK)) {
                this.setEating(true);
            }
            if (this.isEating() && ++this.eatingCounter > 50) {
                this.eatingCounter = 0;
                this.setEating(false);
            }
        }
        this.followMommy();
    }

    protected void followMommy() {
        LivingEntity $$0;
        if (this.isBred() && this.isBaby() && !this.isEating() && ($$0 = this.level.getNearestEntity(AbstractHorse.class, MOMMY_TARGETING, this, this.getX(), this.getY(), this.getZ(), this.getBoundingBox().inflate(16.0))) != null && this.distanceToSqr($$0) > 4.0) {
            this.navigation.createPath($$0, 0);
        }
    }

    public boolean canEatGrass() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.mouthCounter > 0 && ++this.mouthCounter > 30) {
            this.mouthCounter = 0;
            this.setFlag(64, false);
        }
        if ((this.isControlledByLocalInstance() || this.isEffectiveAi()) && this.standCounter > 0 && ++this.standCounter > 20) {
            this.standCounter = 0;
            this.setStanding(false);
        }
        if (this.tailCounter > 0 && ++this.tailCounter > 8) {
            this.tailCounter = 0;
        }
        if (this.sprintCounter > 0) {
            ++this.sprintCounter;
            if (this.sprintCounter > 300) {
                this.sprintCounter = 0;
            }
        }
        this.eatAnimO = this.eatAnim;
        if (this.isEating()) {
            this.eatAnim += (1.0f - this.eatAnim) * 0.4f + 0.05f;
            if (this.eatAnim > 1.0f) {
                this.eatAnim = 1.0f;
            }
        } else {
            this.eatAnim += (0.0f - this.eatAnim) * 0.4f - 0.05f;
            if (this.eatAnim < 0.0f) {
                this.eatAnim = 0.0f;
            }
        }
        this.standAnimO = this.standAnim;
        if (this.isStanding()) {
            this.eatAnimO = this.eatAnim = 0.0f;
            this.standAnim += (1.0f - this.standAnim) * 0.4f + 0.05f;
            if (this.standAnim > 1.0f) {
                this.standAnim = 1.0f;
            }
        } else {
            this.allowStandSliding = false;
            this.standAnim += (0.8f * this.standAnim * this.standAnim * this.standAnim - this.standAnim) * 0.6f - 0.05f;
            if (this.standAnim < 0.0f) {
                this.standAnim = 0.0f;
            }
        }
        this.mouthAnimO = this.mouthAnim;
        if (this.getFlag(64)) {
            this.mouthAnim += (1.0f - this.mouthAnim) * 0.7f + 0.05f;
            if (this.mouthAnim > 1.0f) {
                this.mouthAnim = 1.0f;
            }
        } else {
            this.mouthAnim += (0.0f - this.mouthAnim) * 0.7f - 0.05f;
            if (this.mouthAnim < 0.0f) {
                this.mouthAnim = 0.0f;
            }
        }
    }

    @Override
    public InteractionResult mobInteract(Player $$0, InteractionHand $$1) {
        if (this.isVehicle() || this.isBaby()) {
            return super.mobInteract($$0, $$1);
        }
        if (this.isTamed() && $$0.isSecondaryUseActive()) {
            this.openCustomInventoryScreen($$0);
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        }
        ItemStack $$2 = $$0.getItemInHand($$1);
        if (!$$2.isEmpty()) {
            InteractionResult $$3 = $$2.interactLivingEntity($$0, this, $$1);
            if ($$3.consumesAction()) {
                return $$3;
            }
            if (this.canWearArmor() && this.isArmor($$2) && !this.isWearingArmor()) {
                this.equipArmor($$0, $$2);
                return InteractionResult.sidedSuccess(this.level.isClientSide);
            }
        }
        this.doPlayerRide($$0);
        return InteractionResult.sidedSuccess(this.level.isClientSide);
    }

    private void openMouth() {
        if (!this.level.isClientSide) {
            this.mouthCounter = 1;
            this.setFlag(64, true);
        }
    }

    public void setEating(boolean $$0) {
        this.setFlag(16, $$0);
    }

    public void setStanding(boolean $$0) {
        if ($$0) {
            this.setEating(false);
        }
        this.setFlag(32, $$0);
    }

    @Nullable
    public SoundEvent getAmbientStandSound() {
        return this.getAmbientSound();
    }

    public void standIfPossible() {
        if (this.canPerformRearing() && this.isControlledByLocalInstance() || this.isEffectiveAi()) {
            this.standCounter = 1;
            this.setStanding(true);
        }
    }

    public void makeMad() {
        if (!this.isStanding()) {
            this.standIfPossible();
            SoundEvent $$0 = this.getAngrySound();
            if ($$0 != null) {
                this.playSound($$0, this.getSoundVolume(), this.getVoicePitch());
            }
        }
    }

    public boolean tameWithName(Player $$0) {
        this.setOwnerUUID($$0.getUUID());
        this.setTamed(true);
        if ($$0 instanceof ServerPlayer) {
            CriteriaTriggers.TAME_ANIMAL.trigger((ServerPlayer)$$0, this);
        }
        this.level.broadcastEntityEvent(this, (byte)7);
        return true;
    }

    @Override
    public void travel(Vec3 $$0) {
        if (!this.isAlive()) {
            return;
        }
        LivingEntity $$1 = this.getControllingPassenger();
        if (!this.isVehicle() || $$1 == null || this.mountIgnoresControllerInput($$1)) {
            this.flyingSpeed = 0.02f;
            super.travel($$0);
            return;
        }
        this.setRot($$1.getYRot(), $$1.getXRot() * 0.5f);
        this.yBodyRot = this.yHeadRot = this.getYRot();
        this.yRotO = this.yHeadRot;
        float $$2 = $$1.xxa * 0.5f;
        float $$3 = $$1.zza;
        if ($$3 <= 0.0f) {
            $$3 *= 0.25f;
            this.gallopSoundCounter = 0;
        }
        if (this.onGround && this.playerJumpPendingScale == 0.0f && this.isStanding() && !this.allowStandSliding) {
            $$2 = 0.0f;
            $$3 = 0.0f;
        }
        if (this.playerJumpPendingScale > 0.0f && !this.isJumping() && this.onGround) {
            this.executeRidersJump(this.playerJumpPendingScale, $$2, $$3);
            this.playerJumpPendingScale = 0.0f;
        }
        this.flyingSpeed = this.getSpeed() * 0.1f;
        if (this.isControlledByLocalInstance()) {
            this.setSpeed(this.getDrivenMovementSpeed($$1));
            super.travel(new Vec3($$2, $$0.y, $$3));
        } else if ($$1 instanceof Player) {
            this.setDeltaMovement(this.getX() - this.xOld, this.getY() - this.yOld, this.getZ() - this.zOld);
        }
        if (this.onGround) {
            this.playerJumpPendingScale = 0.0f;
            this.setIsJumping(false);
        }
        this.calculateEntityAnimation(this, false);
        this.tryCheckInsideBlocks();
    }

    protected float getDrivenMovementSpeed(LivingEntity $$0) {
        return (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED);
    }

    protected boolean mountIgnoresControllerInput(LivingEntity $$0) {
        return false;
    }

    protected void executeRidersJump(float $$0, float $$1, float $$2) {
        double $$3 = this.getCustomJump() * (double)$$0 * (double)this.getBlockJumpFactor();
        double $$4 = $$3 + this.getJumpBoostPower();
        Vec3 $$5 = this.getDeltaMovement();
        this.setDeltaMovement($$5.x, $$4, $$5.z);
        this.setIsJumping(true);
        this.hasImpulse = true;
        if ($$2 > 0.0f) {
            float $$6 = Mth.sin(this.getYRot() * ((float)Math.PI / 180));
            float $$7 = Mth.cos(this.getYRot() * ((float)Math.PI / 180));
            this.setDeltaMovement(this.getDeltaMovement().add(-0.4f * $$6 * $$0, 0.0, 0.4f * $$7 * $$0));
        }
    }

    protected void playJumpSound() {
        this.playSound(SoundEvents.HORSE_JUMP, 0.4f, 1.0f);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        super.addAdditionalSaveData($$0);
        $$0.putBoolean("EatingHaystack", this.isEating());
        $$0.putBoolean("Bred", this.isBred());
        $$0.putInt("Temper", this.getTemper());
        $$0.putBoolean("Tame", this.isTamed());
        if (this.getOwnerUUID() != null) {
            $$0.putUUID("Owner", this.getOwnerUUID());
        }
        if (!this.inventory.getItem(0).isEmpty()) {
            $$0.put("SaddleItem", this.inventory.getItem(0).save(new CompoundTag()));
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$0) {
        ItemStack $$4;
        UUID $$3;
        super.readAdditionalSaveData($$0);
        this.setEating($$0.getBoolean("EatingHaystack"));
        this.setBred($$0.getBoolean("Bred"));
        this.setTemper($$0.getInt("Temper"));
        this.setTamed($$0.getBoolean("Tame"));
        if ($$0.hasUUID("Owner")) {
            UUID $$1 = $$0.getUUID("Owner");
        } else {
            String $$2 = $$0.getString("Owner");
            $$3 = OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), $$2);
        }
        if ($$3 != null) {
            this.setOwnerUUID($$3);
        }
        if ($$0.contains("SaddleItem", 10) && ($$4 = ItemStack.of($$0.getCompound("SaddleItem"))).is(Items.SADDLE)) {
            this.inventory.setItem(0, $$4);
        }
        this.updateContainerEquipment();
    }

    @Override
    public boolean canMate(Animal $$0) {
        return false;
    }

    protected boolean canParent() {
        return !this.isVehicle() && !this.isPassenger() && this.isTamed() && !this.isBaby() && this.getHealth() >= this.getMaxHealth() && this.isInLove();
    }

    @Override
    @Nullable
    public AgeableMob getBreedOffspring(ServerLevel $$0, AgeableMob $$1) {
        return null;
    }

    protected void setOffspringAttributes(AgeableMob $$0, AbstractHorse $$1) {
        double $$2 = this.getAttributeBaseValue(Attributes.MAX_HEALTH) + $$0.getAttributeBaseValue(Attributes.MAX_HEALTH) + (double)this.generateRandomMaxHealth(this.random);
        $$1.getAttribute(Attributes.MAX_HEALTH).setBaseValue($$2 / 3.0);
        double $$3 = this.getAttributeBaseValue(Attributes.JUMP_STRENGTH) + $$0.getAttributeBaseValue(Attributes.JUMP_STRENGTH) + this.generateRandomJumpStrength(this.random);
        $$1.getAttribute(Attributes.JUMP_STRENGTH).setBaseValue($$3 / 3.0);
        double $$4 = this.getAttributeBaseValue(Attributes.MOVEMENT_SPEED) + $$0.getAttributeBaseValue(Attributes.MOVEMENT_SPEED) + this.generateRandomSpeed(this.random);
        $$1.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue($$4 / 3.0);
    }

    public float getEatAnim(float $$0) {
        return Mth.lerp($$0, this.eatAnimO, this.eatAnim);
    }

    public float getStandAnim(float $$0) {
        return Mth.lerp($$0, this.standAnimO, this.standAnim);
    }

    public float getMouthAnim(float $$0) {
        return Mth.lerp($$0, this.mouthAnimO, this.mouthAnim);
    }

    @Override
    public void onPlayerJump(int $$0) {
        if (!this.isSaddled()) {
            return;
        }
        if ($$0 < 0) {
            $$0 = 0;
        } else {
            this.allowStandSliding = true;
            this.standIfPossible();
        }
        this.playerJumpPendingScale = $$0 >= 90 ? 1.0f : 0.4f + 0.4f * (float)$$0 / 90.0f;
    }

    @Override
    public boolean canJump(Player $$0) {
        return this.isSaddled();
    }

    @Override
    public void handleStartJump(int $$0) {
        this.allowStandSliding = true;
        this.standIfPossible();
        this.playJumpSound();
    }

    @Override
    public void handleStopJump() {
    }

    protected void spawnTamingParticles(boolean $$0) {
        SimpleParticleType $$1 = $$0 ? ParticleTypes.HEART : ParticleTypes.SMOKE;
        for (int $$2 = 0; $$2 < 7; ++$$2) {
            double $$3 = this.random.nextGaussian() * 0.02;
            double $$4 = this.random.nextGaussian() * 0.02;
            double $$5 = this.random.nextGaussian() * 0.02;
            this.level.addParticle($$1, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), $$3, $$4, $$5);
        }
    }

    @Override
    public void handleEntityEvent(byte $$0) {
        if ($$0 == 7) {
            this.spawnTamingParticles(true);
        } else if ($$0 == 6) {
            this.spawnTamingParticles(false);
        } else {
            super.handleEntityEvent($$0);
        }
    }

    @Override
    public void positionRider(Entity $$0) {
        super.positionRider($$0);
        if ($$0 instanceof Mob) {
            Mob $$1 = (Mob)$$0;
            this.yBodyRot = $$1.yBodyRot;
        }
        if (this.standAnimO > 0.0f) {
            float $$2 = Mth.sin(this.yBodyRot * ((float)Math.PI / 180));
            float $$3 = Mth.cos(this.yBodyRot * ((float)Math.PI / 180));
            float $$4 = 0.7f * this.standAnimO;
            float $$5 = 0.15f * this.standAnimO;
            $$0.setPos(this.getX() + (double)($$4 * $$2), this.getY() + this.getPassengersRidingOffset() + $$0.getMyRidingOffset() + (double)$$5, this.getZ() - (double)($$4 * $$3));
            if ($$0 instanceof LivingEntity) {
                ((LivingEntity)$$0).yBodyRot = this.yBodyRot;
            }
        }
    }

    protected float generateRandomMaxHealth(RandomSource $$0) {
        return 15.0f + (float)$$0.nextInt(8) + (float)$$0.nextInt(9);
    }

    protected double generateRandomJumpStrength(RandomSource $$0) {
        return (double)0.4f + $$0.nextDouble() * 0.2 + $$0.nextDouble() * 0.2 + $$0.nextDouble() * 0.2;
    }

    protected double generateRandomSpeed(RandomSource $$0) {
        return ((double)0.45f + $$0.nextDouble() * 0.3 + $$0.nextDouble() * 0.3 + $$0.nextDouble() * 0.3) * 0.25;
    }

    @Override
    public boolean onClimbable() {
        return false;
    }

    @Override
    protected float getStandingEyeHeight(Pose $$0, EntityDimensions $$1) {
        return $$1.height * 0.95f;
    }

    public boolean canWearArmor() {
        return false;
    }

    public boolean isWearingArmor() {
        return !this.getItemBySlot(EquipmentSlot.CHEST).isEmpty();
    }

    public boolean isArmor(ItemStack $$0) {
        return false;
    }

    private SlotAccess createEquipmentSlotAccess(final int $$0, final Predicate<ItemStack> $$1) {
        return new SlotAccess(){

            @Override
            public ItemStack get() {
                return AbstractHorse.this.inventory.getItem($$0);
            }

            @Override
            public boolean set(ItemStack $$02) {
                if (!$$1.test((Object)$$02)) {
                    return false;
                }
                AbstractHorse.this.inventory.setItem($$0, $$02);
                AbstractHorse.this.updateContainerEquipment();
                return true;
            }
        };
    }

    @Override
    public SlotAccess getSlot(int $$02) {
        int $$2;
        int $$1 = $$02 - 400;
        if ($$1 >= 0 && $$1 < 2 && $$1 < this.inventory.getContainerSize()) {
            if ($$1 == 0) {
                return this.createEquipmentSlotAccess($$1, (Predicate<ItemStack>)((Predicate)$$0 -> $$0.isEmpty() || $$0.is(Items.SADDLE)));
            }
            if ($$1 == 1) {
                if (!this.canWearArmor()) {
                    return SlotAccess.NULL;
                }
                return this.createEquipmentSlotAccess($$1, (Predicate<ItemStack>)((Predicate)$$0 -> $$0.isEmpty() || this.isArmor((ItemStack)$$0)));
            }
        }
        if (($$2 = $$02 - 500 + 2) >= 2 && $$2 < this.inventory.getContainerSize()) {
            return SlotAccess.forContainer(this.inventory, $$2);
        }
        return super.getSlot($$02);
    }

    @Override
    @Nullable
    public LivingEntity getControllingPassenger() {
        Entity entity;
        if (this.isSaddled() && (entity = this.getFirstPassenger()) instanceof LivingEntity) {
            LivingEntity $$0 = (LivingEntity)entity;
            return $$0;
        }
        return null;
    }

    @Nullable
    private Vec3 getDismountLocationInDirection(Vec3 $$0, LivingEntity $$1) {
        double $$2 = this.getX() + $$0.x;
        double $$3 = this.getBoundingBox().minY;
        double $$4 = this.getZ() + $$0.z;
        BlockPos.MutableBlockPos $$5 = new BlockPos.MutableBlockPos();
        block0: for (Pose $$6 : $$1.getDismountPoses()) {
            $$5.set($$2, $$3, $$4);
            double $$7 = this.getBoundingBox().maxY + 0.75;
            do {
                Vec3 $$10;
                AABB $$9;
                double $$8 = this.level.getBlockFloorHeight($$5);
                if ((double)$$5.getY() + $$8 > $$7) continue block0;
                if (DismountHelper.isBlockFloorValid($$8) && DismountHelper.canDismountTo(this.level, $$1, ($$9 = $$1.getLocalBoundsForPose($$6)).move($$10 = new Vec3($$2, (double)$$5.getY() + $$8, $$4)))) {
                    $$1.setPose($$6);
                    return $$10;
                }
                $$5.move(Direction.UP);
            } while ((double)$$5.getY() < $$7);
        }
        return null;
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity $$0) {
        Vec3 $$1 = AbstractHorse.getCollisionHorizontalEscapeVector(this.getBbWidth(), $$0.getBbWidth(), this.getYRot() + ($$0.getMainArm() == HumanoidArm.RIGHT ? 90.0f : -90.0f));
        Vec3 $$2 = this.getDismountLocationInDirection($$1, $$0);
        if ($$2 != null) {
            return $$2;
        }
        Vec3 $$3 = AbstractHorse.getCollisionHorizontalEscapeVector(this.getBbWidth(), $$0.getBbWidth(), this.getYRot() + ($$0.getMainArm() == HumanoidArm.LEFT ? 90.0f : -90.0f));
        Vec3 $$4 = this.getDismountLocationInDirection($$3, $$0);
        if ($$4 != null) {
            return $$4;
        }
        return this.position();
    }

    protected void randomizeAttributes(RandomSource $$0) {
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor $$0, DifficultyInstance $$1, MobSpawnType $$2, @Nullable SpawnGroupData $$3, @Nullable CompoundTag $$4) {
        if ($$3 == null) {
            $$3 = new AgeableMob.AgeableMobGroupData(0.2f);
        }
        this.randomizeAttributes($$0.getRandom());
        return super.finalizeSpawn($$0, $$1, $$2, $$3, $$4);
    }

    public boolean hasInventoryChanged(Container $$0) {
        return this.inventory != $$0;
    }

    public int getAmbientStandInterval() {
        return this.getAmbientSoundInterval();
    }
}