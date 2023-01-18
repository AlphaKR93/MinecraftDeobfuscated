/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Byte
 *  java.lang.Class
 *  java.lang.Enum
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.EnumSet
 *  java.util.List
 *  java.util.function.IntFunction
 *  java.util.function.Predicate
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.animal;

import java.util.EnumSet;
import java.util.List;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class Panda
extends Animal {
    private static final EntityDataAccessor<Integer> UNHAPPY_COUNTER = SynchedEntityData.defineId(Panda.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> SNEEZE_COUNTER = SynchedEntityData.defineId(Panda.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> EAT_COUNTER = SynchedEntityData.defineId(Panda.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Byte> MAIN_GENE_ID = SynchedEntityData.defineId(Panda.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Byte> HIDDEN_GENE_ID = SynchedEntityData.defineId(Panda.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Byte> DATA_ID_FLAGS = SynchedEntityData.defineId(Panda.class, EntityDataSerializers.BYTE);
    static final TargetingConditions BREED_TARGETING = TargetingConditions.forNonCombat().range(8.0);
    private static final int FLAG_SNEEZE = 2;
    private static final int FLAG_ROLL = 4;
    private static final int FLAG_SIT = 8;
    private static final int FLAG_ON_BACK = 16;
    private static final int EAT_TICK_INTERVAL = 5;
    public static final int TOTAL_ROLL_STEPS = 32;
    private static final int TOTAL_UNHAPPY_TIME = 32;
    boolean gotBamboo;
    boolean didBite;
    public int rollCounter;
    private Vec3 rollDelta;
    private float sitAmount;
    private float sitAmountO;
    private float onBackAmount;
    private float onBackAmountO;
    private float rollAmount;
    private float rollAmountO;
    PandaLookAtPlayerGoal lookAtPlayerGoal;
    static final Predicate<ItemEntity> PANDA_ITEMS = $$0 -> {
        ItemStack $$1 = $$0.getItem();
        return ($$1.is(Blocks.BAMBOO.asItem()) || $$1.is(Blocks.CAKE.asItem())) && $$0.isAlive() && !$$0.hasPickUpDelay();
    };

    public Panda(EntityType<? extends Panda> $$0, Level $$1) {
        super((EntityType<? extends Animal>)$$0, $$1);
        this.moveControl = new PandaMoveControl(this);
        if (!this.isBaby()) {
            this.setCanPickUpLoot(true);
        }
    }

    @Override
    public boolean canTakeItem(ItemStack $$0) {
        EquipmentSlot $$1 = Mob.getEquipmentSlotForItem($$0);
        if (!this.getItemBySlot($$1).isEmpty()) {
            return false;
        }
        return $$1 == EquipmentSlot.MAINHAND && super.canTakeItem($$0);
    }

    public int getUnhappyCounter() {
        return this.entityData.get(UNHAPPY_COUNTER);
    }

    public void setUnhappyCounter(int $$0) {
        this.entityData.set(UNHAPPY_COUNTER, $$0);
    }

    public boolean isSneezing() {
        return this.getFlag(2);
    }

    public boolean isSitting() {
        return this.getFlag(8);
    }

    public void sit(boolean $$0) {
        this.setFlag(8, $$0);
    }

    public boolean isOnBack() {
        return this.getFlag(16);
    }

    public void setOnBack(boolean $$0) {
        this.setFlag(16, $$0);
    }

    public boolean isEating() {
        return this.entityData.get(EAT_COUNTER) > 0;
    }

    public void eat(boolean $$0) {
        this.entityData.set(EAT_COUNTER, $$0 ? 1 : 0);
    }

    private int getEatCounter() {
        return this.entityData.get(EAT_COUNTER);
    }

    private void setEatCounter(int $$0) {
        this.entityData.set(EAT_COUNTER, $$0);
    }

    public void sneeze(boolean $$0) {
        this.setFlag(2, $$0);
        if (!$$0) {
            this.setSneezeCounter(0);
        }
    }

    public int getSneezeCounter() {
        return this.entityData.get(SNEEZE_COUNTER);
    }

    public void setSneezeCounter(int $$0) {
        this.entityData.set(SNEEZE_COUNTER, $$0);
    }

    public Gene getMainGene() {
        return Gene.byId(this.entityData.get(MAIN_GENE_ID).byteValue());
    }

    public void setMainGene(Gene $$0) {
        if ($$0.getId() > 6) {
            $$0 = Gene.getRandom(this.random);
        }
        this.entityData.set(MAIN_GENE_ID, (byte)$$0.getId());
    }

    public Gene getHiddenGene() {
        return Gene.byId(this.entityData.get(HIDDEN_GENE_ID).byteValue());
    }

    public void setHiddenGene(Gene $$0) {
        if ($$0.getId() > 6) {
            $$0 = Gene.getRandom(this.random);
        }
        this.entityData.set(HIDDEN_GENE_ID, (byte)$$0.getId());
    }

    public boolean isRolling() {
        return this.getFlag(4);
    }

    public void roll(boolean $$0) {
        this.setFlag(4, $$0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(UNHAPPY_COUNTER, 0);
        this.entityData.define(SNEEZE_COUNTER, 0);
        this.entityData.define(MAIN_GENE_ID, (byte)0);
        this.entityData.define(HIDDEN_GENE_ID, (byte)0);
        this.entityData.define(DATA_ID_FLAGS, (byte)0);
        this.entityData.define(EAT_COUNTER, 0);
    }

    private boolean getFlag(int $$0) {
        return (this.entityData.get(DATA_ID_FLAGS) & $$0) != 0;
    }

    private void setFlag(int $$0, boolean $$1) {
        byte $$2 = this.entityData.get(DATA_ID_FLAGS);
        if ($$1) {
            this.entityData.set(DATA_ID_FLAGS, (byte)($$2 | $$0));
        } else {
            this.entityData.set(DATA_ID_FLAGS, (byte)($$2 & ~$$0));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        super.addAdditionalSaveData($$0);
        $$0.putString("MainGene", this.getMainGene().getSerializedName());
        $$0.putString("HiddenGene", this.getHiddenGene().getSerializedName());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$0) {
        super.readAdditionalSaveData($$0);
        this.setMainGene(Gene.byName($$0.getString("MainGene")));
        this.setHiddenGene(Gene.byName($$0.getString("HiddenGene")));
    }

    @Override
    @Nullable
    public AgeableMob getBreedOffspring(ServerLevel $$0, AgeableMob $$1) {
        Panda $$2 = EntityType.PANDA.create($$0);
        if ($$2 != null) {
            if ($$1 instanceof Panda) {
                Panda $$3 = (Panda)$$1;
                $$2.setGeneFromParents(this, $$3);
            }
            $$2.setAttributes();
        }
        return $$2;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new PandaPanicGoal(this, 2.0));
        this.goalSelector.addGoal(2, new PandaBreedGoal(this, 1.0));
        this.goalSelector.addGoal(3, new PandaAttackGoal(this, (double)1.2f, true));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.0, Ingredient.of(Blocks.BAMBOO.asItem()), false));
        this.goalSelector.addGoal(6, new PandaAvoidGoal<Player>(this, Player.class, 8.0f, 2.0, 2.0));
        this.goalSelector.addGoal(6, new PandaAvoidGoal<Monster>(this, Monster.class, 4.0f, 2.0, 2.0));
        this.goalSelector.addGoal(7, new PandaSitGoal());
        this.goalSelector.addGoal(8, new PandaLieOnBackGoal(this));
        this.goalSelector.addGoal(8, new PandaSneezeGoal(this));
        this.lookAtPlayerGoal = new PandaLookAtPlayerGoal(this, Player.class, 6.0f);
        this.goalSelector.addGoal(9, this.lookAtPlayerGoal);
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(12, new PandaRollGoal(this));
        this.goalSelector.addGoal(13, new FollowParentGoal(this, 1.25));
        this.goalSelector.addGoal(14, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.targetSelector.addGoal(1, new PandaHurtByTargetGoal(this, new Class[0]).setAlertOthers(new Class[0]));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.15f).add(Attributes.ATTACK_DAMAGE, 6.0);
    }

    public Gene getVariant() {
        return Gene.getVariantFromGenes(this.getMainGene(), this.getHiddenGene());
    }

    public boolean isLazy() {
        return this.getVariant() == Gene.LAZY;
    }

    public boolean isWorried() {
        return this.getVariant() == Gene.WORRIED;
    }

    public boolean isPlayful() {
        return this.getVariant() == Gene.PLAYFUL;
    }

    public boolean isBrown() {
        return this.getVariant() == Gene.BROWN;
    }

    public boolean isWeak() {
        return this.getVariant() == Gene.WEAK;
    }

    @Override
    public boolean isAggressive() {
        return this.getVariant() == Gene.AGGRESSIVE;
    }

    @Override
    public boolean canBeLeashed(Player $$0) {
        return false;
    }

    @Override
    public boolean doHurtTarget(Entity $$0) {
        this.playSound(SoundEvents.PANDA_BITE, 1.0f, 1.0f);
        if (!this.isAggressive()) {
            this.didBite = true;
        }
        return super.doHurtTarget($$0);
    }

    @Override
    public void tick() {
        LivingEntity $$0;
        super.tick();
        if (this.isWorried()) {
            if (this.level.isThundering() && !this.isInWater()) {
                this.sit(true);
                this.eat(false);
            } else if (!this.isEating()) {
                this.sit(false);
            }
        }
        if (($$0 = this.getTarget()) == null) {
            this.gotBamboo = false;
            this.didBite = false;
        }
        if (this.getUnhappyCounter() > 0) {
            if ($$0 != null) {
                this.lookAt($$0, 90.0f, 90.0f);
            }
            if (this.getUnhappyCounter() == 29 || this.getUnhappyCounter() == 14) {
                this.playSound(SoundEvents.PANDA_CANT_BREED, 1.0f, 1.0f);
            }
            this.setUnhappyCounter(this.getUnhappyCounter() - 1);
        }
        if (this.isSneezing()) {
            this.setSneezeCounter(this.getSneezeCounter() + 1);
            if (this.getSneezeCounter() > 20) {
                this.sneeze(false);
                this.afterSneeze();
            } else if (this.getSneezeCounter() == 1) {
                this.playSound(SoundEvents.PANDA_PRE_SNEEZE, 1.0f, 1.0f);
            }
        }
        if (this.isRolling()) {
            this.handleRoll();
        } else {
            this.rollCounter = 0;
        }
        if (this.isSitting()) {
            this.setXRot(0.0f);
        }
        this.updateSitAmount();
        this.handleEating();
        this.updateOnBackAnimation();
        this.updateRollAmount();
    }

    public boolean isScared() {
        return this.isWorried() && this.level.isThundering();
    }

    private void handleEating() {
        if (!this.isEating() && this.isSitting() && !this.isScared() && !this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty() && this.random.nextInt(80) == 1) {
            this.eat(true);
        } else if (this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty() || !this.isSitting()) {
            this.eat(false);
        }
        if (this.isEating()) {
            this.addEatingParticles();
            if (!this.level.isClientSide && this.getEatCounter() > 80 && this.random.nextInt(20) == 1) {
                if (this.getEatCounter() > 100 && this.isFoodOrCake(this.getItemBySlot(EquipmentSlot.MAINHAND))) {
                    if (!this.level.isClientSide) {
                        this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                        this.gameEvent(GameEvent.EAT);
                    }
                    this.sit(false);
                }
                this.eat(false);
                return;
            }
            this.setEatCounter(this.getEatCounter() + 1);
        }
    }

    private void addEatingParticles() {
        if (this.getEatCounter() % 5 == 0) {
            this.playSound(SoundEvents.PANDA_EAT, 0.5f + 0.5f * (float)this.random.nextInt(2), (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
            for (int $$0 = 0; $$0 < 6; ++$$0) {
                Vec3 $$1 = new Vec3(((double)this.random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, ((double)this.random.nextFloat() - 0.5) * 0.1);
                $$1 = $$1.xRot(-this.getXRot() * ((float)Math.PI / 180));
                $$1 = $$1.yRot(-this.getYRot() * ((float)Math.PI / 180));
                double $$2 = (double)(-this.random.nextFloat()) * 0.6 - 0.3;
                Vec3 $$3 = new Vec3(((double)this.random.nextFloat() - 0.5) * 0.8, $$2, 1.0 + ((double)this.random.nextFloat() - 0.5) * 0.4);
                $$3 = $$3.yRot(-this.yBodyRot * ((float)Math.PI / 180));
                $$3 = $$3.add(this.getX(), this.getEyeY() + 1.0, this.getZ());
                this.level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, this.getItemBySlot(EquipmentSlot.MAINHAND)), $$3.x, $$3.y, $$3.z, $$1.x, $$1.y + 0.05, $$1.z);
            }
        }
    }

    private void updateSitAmount() {
        this.sitAmountO = this.sitAmount;
        this.sitAmount = this.isSitting() ? Math.min((float)1.0f, (float)(this.sitAmount + 0.15f)) : Math.max((float)0.0f, (float)(this.sitAmount - 0.19f));
    }

    private void updateOnBackAnimation() {
        this.onBackAmountO = this.onBackAmount;
        this.onBackAmount = this.isOnBack() ? Math.min((float)1.0f, (float)(this.onBackAmount + 0.15f)) : Math.max((float)0.0f, (float)(this.onBackAmount - 0.19f));
    }

    private void updateRollAmount() {
        this.rollAmountO = this.rollAmount;
        this.rollAmount = this.isRolling() ? Math.min((float)1.0f, (float)(this.rollAmount + 0.15f)) : Math.max((float)0.0f, (float)(this.rollAmount - 0.19f));
    }

    public float getSitAmount(float $$0) {
        return Mth.lerp($$0, this.sitAmountO, this.sitAmount);
    }

    public float getLieOnBackAmount(float $$0) {
        return Mth.lerp($$0, this.onBackAmountO, this.onBackAmount);
    }

    public float getRollAmount(float $$0) {
        return Mth.lerp($$0, this.rollAmountO, this.rollAmount);
    }

    private void handleRoll() {
        ++this.rollCounter;
        if (this.rollCounter > 32) {
            this.roll(false);
            return;
        }
        if (!this.level.isClientSide) {
            Vec3 $$0 = this.getDeltaMovement();
            if (this.rollCounter == 1) {
                float $$1 = this.getYRot() * ((float)Math.PI / 180);
                float $$2 = this.isBaby() ? 0.1f : 0.2f;
                this.rollDelta = new Vec3($$0.x + (double)(-Mth.sin($$1) * $$2), 0.0, $$0.z + (double)(Mth.cos($$1) * $$2));
                this.setDeltaMovement(this.rollDelta.add(0.0, 0.27, 0.0));
            } else if ((float)this.rollCounter == 7.0f || (float)this.rollCounter == 15.0f || (float)this.rollCounter == 23.0f) {
                this.setDeltaMovement(0.0, this.onGround ? 0.27 : $$0.y, 0.0);
            } else {
                this.setDeltaMovement(this.rollDelta.x, $$0.y, this.rollDelta.z);
            }
        }
    }

    private void afterSneeze() {
        Vec3 $$0 = this.getDeltaMovement();
        this.level.addParticle(ParticleTypes.SNEEZE, this.getX() - (double)(this.getBbWidth() + 1.0f) * 0.5 * (double)Mth.sin(this.yBodyRot * ((float)Math.PI / 180)), this.getEyeY() - (double)0.1f, this.getZ() + (double)(this.getBbWidth() + 1.0f) * 0.5 * (double)Mth.cos(this.yBodyRot * ((float)Math.PI / 180)), $$0.x, 0.0, $$0.z);
        this.playSound(SoundEvents.PANDA_SNEEZE, 1.0f, 1.0f);
        List $$1 = this.level.getEntitiesOfClass(Panda.class, this.getBoundingBox().inflate(10.0));
        for (Panda $$2 : $$1) {
            if ($$2.isBaby() || !$$2.onGround || $$2.isInWater() || !$$2.canPerformAction()) continue;
            $$2.jumpFromGround();
        }
        if (!this.level.isClientSide() && this.random.nextInt(700) == 0 && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            this.spawnAtLocation(Items.SLIME_BALL);
        }
    }

    @Override
    protected void pickUpItem(ItemEntity $$0) {
        if (this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty() && PANDA_ITEMS.test((Object)$$0)) {
            this.onItemPickup($$0);
            ItemStack $$1 = $$0.getItem();
            this.setItemSlot(EquipmentSlot.MAINHAND, $$1);
            this.setGuaranteedDrop(EquipmentSlot.MAINHAND);
            this.take($$0, $$1.getCount());
            $$0.discard();
        }
    }

    @Override
    public boolean hurt(DamageSource $$0, float $$1) {
        if (!this.level.isClientSide) {
            this.sit(false);
        }
        return super.hurt($$0, $$1);
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor $$0, DifficultyInstance $$1, MobSpawnType $$2, @Nullable SpawnGroupData $$3, @Nullable CompoundTag $$4) {
        RandomSource $$5 = $$0.getRandom();
        this.setMainGene(Gene.getRandom($$5));
        this.setHiddenGene(Gene.getRandom($$5));
        this.setAttributes();
        if ($$3 == null) {
            $$3 = new AgeableMob.AgeableMobGroupData(0.2f);
        }
        return super.finalizeSpawn($$0, $$1, $$2, $$3, $$4);
    }

    public void setGeneFromParents(Panda $$0, @Nullable Panda $$1) {
        if ($$1 == null) {
            if (this.random.nextBoolean()) {
                this.setMainGene($$0.getOneOfGenesRandomly());
                this.setHiddenGene(Gene.getRandom(this.random));
            } else {
                this.setMainGene(Gene.getRandom(this.random));
                this.setHiddenGene($$0.getOneOfGenesRandomly());
            }
        } else if (this.random.nextBoolean()) {
            this.setMainGene($$0.getOneOfGenesRandomly());
            this.setHiddenGene($$1.getOneOfGenesRandomly());
        } else {
            this.setMainGene($$1.getOneOfGenesRandomly());
            this.setHiddenGene($$0.getOneOfGenesRandomly());
        }
        if (this.random.nextInt(32) == 0) {
            this.setMainGene(Gene.getRandom(this.random));
        }
        if (this.random.nextInt(32) == 0) {
            this.setHiddenGene(Gene.getRandom(this.random));
        }
    }

    private Gene getOneOfGenesRandomly() {
        if (this.random.nextBoolean()) {
            return this.getMainGene();
        }
        return this.getHiddenGene();
    }

    public void setAttributes() {
        if (this.isWeak()) {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(10.0);
        }
        if (this.isLazy()) {
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.07f);
        }
    }

    void tryToSit() {
        if (!this.isInWater()) {
            this.setZza(0.0f);
            this.getNavigation().stop();
            this.sit(true);
        }
    }

    @Override
    public InteractionResult mobInteract(Player $$0, InteractionHand $$1) {
        ItemStack $$2 = $$0.getItemInHand($$1);
        if (this.isScared()) {
            return InteractionResult.PASS;
        }
        if (this.isOnBack()) {
            this.setOnBack(false);
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        }
        if (this.isFood($$2)) {
            if (this.getTarget() != null) {
                this.gotBamboo = true;
            }
            if (this.isBaby()) {
                this.usePlayerItem($$0, $$1, $$2);
                this.ageUp((int)((float)(-this.getAge() / 20) * 0.1f), true);
            } else if (!this.level.isClientSide && this.getAge() == 0 && this.canFallInLove()) {
                this.usePlayerItem($$0, $$1, $$2);
                this.setInLove($$0);
            } else if (!(this.level.isClientSide || this.isSitting() || this.isInWater())) {
                this.tryToSit();
                this.eat(true);
                ItemStack $$3 = this.getItemBySlot(EquipmentSlot.MAINHAND);
                if (!$$3.isEmpty() && !$$0.getAbilities().instabuild) {
                    this.spawnAtLocation($$3);
                }
                this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack($$2.getItem(), 1));
                this.usePlayerItem($$0, $$1, $$2);
            } else {
                return InteractionResult.PASS;
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    @Nullable
    protected SoundEvent getAmbientSound() {
        if (this.isAggressive()) {
            return SoundEvents.PANDA_AGGRESSIVE_AMBIENT;
        }
        if (this.isWorried()) {
            return SoundEvents.PANDA_WORRIED_AMBIENT;
        }
        return SoundEvents.PANDA_AMBIENT;
    }

    @Override
    protected void playStepSound(BlockPos $$0, BlockState $$1) {
        this.playSound(SoundEvents.PANDA_STEP, 0.15f, 1.0f);
    }

    @Override
    public boolean isFood(ItemStack $$0) {
        return $$0.is(Blocks.BAMBOO.asItem());
    }

    private boolean isFoodOrCake(ItemStack $$0) {
        return this.isFood($$0) || $$0.is(Blocks.CAKE.asItem());
    }

    @Override
    @Nullable
    protected SoundEvent getDeathSound() {
        return SoundEvents.PANDA_DEATH;
    }

    @Override
    @Nullable
    protected SoundEvent getHurtSound(DamageSource $$0) {
        return SoundEvents.PANDA_HURT;
    }

    public boolean canPerformAction() {
        return !this.isOnBack() && !this.isScared() && !this.isEating() && !this.isRolling() && !this.isSitting();
    }

    static class PandaMoveControl
    extends MoveControl {
        private final Panda panda;

        public PandaMoveControl(Panda $$0) {
            super($$0);
            this.panda = $$0;
        }

        @Override
        public void tick() {
            if (!this.panda.canPerformAction()) {
                return;
            }
            super.tick();
        }
    }

    public static enum Gene implements StringRepresentable
    {
        NORMAL(0, "normal", false),
        LAZY(1, "lazy", false),
        WORRIED(2, "worried", false),
        PLAYFUL(3, "playful", false),
        BROWN(4, "brown", true),
        WEAK(5, "weak", true),
        AGGRESSIVE(6, "aggressive", false);

        public static final StringRepresentable.EnumCodec<Gene> CODEC;
        private static final IntFunction<Gene> BY_ID;
        private static final int MAX_GENE = 6;
        private final int id;
        private final String name;
        private final boolean isRecessive;

        private Gene(int $$0, String $$1, boolean $$2) {
            this.id = $$0;
            this.name = $$1;
            this.isRecessive = $$2;
        }

        public int getId() {
            return this.id;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        public boolean isRecessive() {
            return this.isRecessive;
        }

        static Gene getVariantFromGenes(Gene $$0, Gene $$1) {
            if ($$0.isRecessive()) {
                if ($$0 == $$1) {
                    return $$0;
                }
                return NORMAL;
            }
            return $$0;
        }

        public static Gene byId(int $$0) {
            return (Gene)BY_ID.apply($$0);
        }

        public static Gene byName(String $$0) {
            return CODEC.byName($$0, NORMAL);
        }

        public static Gene getRandom(RandomSource $$0) {
            int $$1 = $$0.nextInt(16);
            if ($$1 == 0) {
                return LAZY;
            }
            if ($$1 == 1) {
                return WORRIED;
            }
            if ($$1 == 2) {
                return PLAYFUL;
            }
            if ($$1 == 4) {
                return AGGRESSIVE;
            }
            if ($$1 < 9) {
                return WEAK;
            }
            if ($$1 < 11) {
                return BROWN;
            }
            return NORMAL;
        }

        static {
            CODEC = StringRepresentable.fromEnum((Supplier<E[]>)((Supplier)Gene::values));
            BY_ID = ByIdMap.continuous(Gene::getId, Gene.values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        }
    }

    static class PandaPanicGoal
    extends PanicGoal {
        private final Panda panda;

        public PandaPanicGoal(Panda $$0, double $$1) {
            super($$0, $$1);
            this.panda = $$0;
        }

        @Override
        protected boolean shouldPanic() {
            return this.mob.isFreezing() || this.mob.isOnFire();
        }

        @Override
        public boolean canContinueToUse() {
            if (this.panda.isSitting()) {
                this.panda.getNavigation().stop();
                return false;
            }
            return super.canContinueToUse();
        }
    }

    static class PandaBreedGoal
    extends BreedGoal {
        private final Panda panda;
        private int unhappyCooldown;

        public PandaBreedGoal(Panda $$0, double $$1) {
            super($$0, $$1);
            this.panda = $$0;
        }

        @Override
        public boolean canUse() {
            if (super.canUse() && this.panda.getUnhappyCounter() == 0) {
                if (!this.canFindBamboo()) {
                    if (this.unhappyCooldown <= this.panda.tickCount) {
                        this.panda.setUnhappyCounter(32);
                        this.unhappyCooldown = this.panda.tickCount + 600;
                        if (this.panda.isEffectiveAi()) {
                            Player $$0 = this.level.getNearestPlayer(BREED_TARGETING, this.panda);
                            this.panda.lookAtPlayerGoal.setTarget($$0);
                        }
                    }
                    return false;
                }
                return true;
            }
            return false;
        }

        private boolean canFindBamboo() {
            BlockPos $$0 = this.panda.blockPosition();
            BlockPos.MutableBlockPos $$1 = new BlockPos.MutableBlockPos();
            for (int $$2 = 0; $$2 < 3; ++$$2) {
                for (int $$3 = 0; $$3 < 8; ++$$3) {
                    int $$4 = 0;
                    while ($$4 <= $$3) {
                        int $$5;
                        int n = $$5 = $$4 < $$3 && $$4 > -$$3 ? $$3 : 0;
                        while ($$5 <= $$3) {
                            $$1.setWithOffset($$0, $$4, $$2, $$5);
                            if (this.level.getBlockState($$1).is(Blocks.BAMBOO)) {
                                return true;
                            }
                            $$5 = $$5 > 0 ? -$$5 : 1 - $$5;
                        }
                        $$4 = $$4 > 0 ? -$$4 : 1 - $$4;
                    }
                }
            }
            return false;
        }
    }

    static class PandaAttackGoal
    extends MeleeAttackGoal {
        private final Panda panda;

        public PandaAttackGoal(Panda $$0, double $$1, boolean $$2) {
            super($$0, $$1, $$2);
            this.panda = $$0;
        }

        @Override
        public boolean canUse() {
            return this.panda.canPerformAction() && super.canUse();
        }
    }

    static class PandaAvoidGoal<T extends LivingEntity>
    extends AvoidEntityGoal<T> {
        private final Panda panda;

        public PandaAvoidGoal(Panda $$0, Class<T> $$1, float $$2, double $$3, double $$4) {
            super($$0, $$1, $$2, $$3, $$4, (Predicate<LivingEntity>)((Predicate)arg_0 -> EntitySelector.NO_SPECTATORS.test(arg_0)));
            this.panda = $$0;
        }

        @Override
        public boolean canUse() {
            return this.panda.isWorried() && this.panda.canPerformAction() && super.canUse();
        }
    }

    class PandaSitGoal
    extends Goal {
        private int cooldown;

        public PandaSitGoal() {
            this.setFlags((EnumSet<Goal.Flag>)EnumSet.of((Enum)Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (this.cooldown > Panda.this.tickCount || Panda.this.isBaby() || Panda.this.isInWater() || !Panda.this.canPerformAction() || Panda.this.getUnhappyCounter() > 0) {
                return false;
            }
            List $$0 = Panda.this.level.getEntitiesOfClass(ItemEntity.class, Panda.this.getBoundingBox().inflate(6.0, 6.0, 6.0), PANDA_ITEMS);
            return !$$0.isEmpty() || !Panda.this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty();
        }

        @Override
        public boolean canContinueToUse() {
            if (Panda.this.isInWater() || !Panda.this.isLazy() && Panda.this.random.nextInt(PandaSitGoal.reducedTickDelay(600)) == 1) {
                return false;
            }
            return Panda.this.random.nextInt(PandaSitGoal.reducedTickDelay(2000)) != 1;
        }

        @Override
        public void tick() {
            if (!Panda.this.isSitting() && !Panda.this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()) {
                Panda.this.tryToSit();
            }
        }

        @Override
        public void start() {
            List $$0 = Panda.this.level.getEntitiesOfClass(ItemEntity.class, Panda.this.getBoundingBox().inflate(8.0, 8.0, 8.0), PANDA_ITEMS);
            if (!$$0.isEmpty() && Panda.this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()) {
                Panda.this.getNavigation().moveTo((Entity)$$0.get(0), (double)1.2f);
            } else if (!Panda.this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()) {
                Panda.this.tryToSit();
            }
            this.cooldown = 0;
        }

        @Override
        public void stop() {
            ItemStack $$0 = Panda.this.getItemBySlot(EquipmentSlot.MAINHAND);
            if (!$$0.isEmpty()) {
                Panda.this.spawnAtLocation($$0);
                Panda.this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                int $$1 = Panda.this.isLazy() ? Panda.this.random.nextInt(50) + 10 : Panda.this.random.nextInt(150) + 10;
                this.cooldown = Panda.this.tickCount + $$1 * 20;
            }
            Panda.this.sit(false);
        }
    }

    static class PandaLieOnBackGoal
    extends Goal {
        private final Panda panda;
        private int cooldown;

        public PandaLieOnBackGoal(Panda $$0) {
            this.panda = $$0;
        }

        @Override
        public boolean canUse() {
            return this.cooldown < this.panda.tickCount && this.panda.isLazy() && this.panda.canPerformAction() && this.panda.random.nextInt(PandaLieOnBackGoal.reducedTickDelay(400)) == 1;
        }

        @Override
        public boolean canContinueToUse() {
            if (this.panda.isInWater() || !this.panda.isLazy() && this.panda.random.nextInt(PandaLieOnBackGoal.reducedTickDelay(600)) == 1) {
                return false;
            }
            return this.panda.random.nextInt(PandaLieOnBackGoal.reducedTickDelay(2000)) != 1;
        }

        @Override
        public void start() {
            this.panda.setOnBack(true);
            this.cooldown = 0;
        }

        @Override
        public void stop() {
            this.panda.setOnBack(false);
            this.cooldown = this.panda.tickCount + 200;
        }
    }

    static class PandaSneezeGoal
    extends Goal {
        private final Panda panda;

        public PandaSneezeGoal(Panda $$0) {
            this.panda = $$0;
        }

        @Override
        public boolean canUse() {
            if (!this.panda.isBaby() || !this.panda.canPerformAction()) {
                return false;
            }
            if (this.panda.isWeak() && this.panda.random.nextInt(PandaSneezeGoal.reducedTickDelay(500)) == 1) {
                return true;
            }
            return this.panda.random.nextInt(PandaSneezeGoal.reducedTickDelay(6000)) == 1;
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }

        @Override
        public void start() {
            this.panda.sneeze(true);
        }
    }

    static class PandaLookAtPlayerGoal
    extends LookAtPlayerGoal {
        private final Panda panda;

        public PandaLookAtPlayerGoal(Panda $$0, Class<? extends LivingEntity> $$1, float $$2) {
            super($$0, $$1, $$2);
            this.panda = $$0;
        }

        public void setTarget(LivingEntity $$0) {
            this.lookAt = $$0;
        }

        @Override
        public boolean canContinueToUse() {
            return this.lookAt != null && super.canContinueToUse();
        }

        @Override
        public boolean canUse() {
            if (this.mob.getRandom().nextFloat() >= this.probability) {
                return false;
            }
            if (this.lookAt == null) {
                this.lookAt = this.lookAtType == Player.class ? this.mob.level.getNearestPlayer(this.lookAtContext, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ()) : this.mob.level.getNearestEntity(this.mob.level.getEntitiesOfClass(this.lookAtType, this.mob.getBoundingBox().inflate(this.lookDistance, 3.0, this.lookDistance), $$0 -> true), this.lookAtContext, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
            }
            return this.panda.canPerformAction() && this.lookAt != null;
        }

        @Override
        public void tick() {
            if (this.lookAt != null) {
                super.tick();
            }
        }
    }

    static class PandaRollGoal
    extends Goal {
        private final Panda panda;

        public PandaRollGoal(Panda $$0) {
            this.panda = $$0;
            this.setFlags((EnumSet<Goal.Flag>)EnumSet.of((Enum)Goal.Flag.MOVE, (Enum)Goal.Flag.LOOK, (Enum)Goal.Flag.JUMP));
        }

        @Override
        public boolean canUse() {
            int $$4;
            if (!this.panda.isBaby() && !this.panda.isPlayful() || !this.panda.onGround) {
                return false;
            }
            if (!this.panda.canPerformAction()) {
                return false;
            }
            float $$0 = this.panda.getYRot() * ((float)Math.PI / 180);
            float $$1 = -Mth.sin($$0);
            float $$2 = Mth.cos($$0);
            int $$3 = (double)Math.abs((float)$$1) > 0.5 ? Mth.sign($$1) : 0;
            int n = $$4 = (double)Math.abs((float)$$2) > 0.5 ? Mth.sign($$2) : 0;
            if (this.panda.level.getBlockState(this.panda.blockPosition().offset($$3, -1, $$4)).isAir()) {
                return true;
            }
            if (this.panda.isPlayful() && this.panda.random.nextInt(PandaRollGoal.reducedTickDelay(60)) == 1) {
                return true;
            }
            return this.panda.random.nextInt(PandaRollGoal.reducedTickDelay(500)) == 1;
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }

        @Override
        public void start() {
            this.panda.roll(true);
        }

        @Override
        public boolean isInterruptable() {
            return false;
        }
    }

    static class PandaHurtByTargetGoal
    extends HurtByTargetGoal {
        private final Panda panda;

        public PandaHurtByTargetGoal(Panda $$0, Class<?> ... $$1) {
            super($$0, $$1);
            this.panda = $$0;
        }

        @Override
        public boolean canContinueToUse() {
            if (this.panda.gotBamboo || this.panda.didBite) {
                this.panda.setTarget(null);
                return false;
            }
            return super.canContinueToUse();
        }

        @Override
        protected void alertOther(Mob $$0, LivingEntity $$1) {
            if ($$0 instanceof Panda && ((Panda)$$0).isAggressive()) {
                $$0.setTarget($$1);
            }
        }
    }
}