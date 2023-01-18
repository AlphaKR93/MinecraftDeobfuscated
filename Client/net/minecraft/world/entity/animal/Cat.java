/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  java.lang.Boolean
 *  java.lang.Class
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.animal;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.CatVariantTags;
import net.minecraft.tags.StructureTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.CatLieOnBedGoal;
import net.minecraft.world.entity.ai.goal.CatSitOnBlockGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.OcelotAttackGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.CatVariant;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;

public class Cat
extends TamableAnimal
implements VariantHolder<CatVariant> {
    public static final double TEMPT_SPEED_MOD = 0.6;
    public static final double WALK_SPEED_MOD = 0.8;
    public static final double SPRINT_SPEED_MOD = 1.33;
    private static final Ingredient TEMPT_INGREDIENT = Ingredient.of(Items.COD, Items.SALMON);
    private static final EntityDataAccessor<CatVariant> DATA_VARIANT_ID = SynchedEntityData.defineId(Cat.class, EntityDataSerializers.CAT_VARIANT);
    private static final EntityDataAccessor<Boolean> IS_LYING = SynchedEntityData.defineId(Cat.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> RELAX_STATE_ONE = SynchedEntityData.defineId(Cat.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_COLLAR_COLOR = SynchedEntityData.defineId(Cat.class, EntityDataSerializers.INT);
    private CatAvoidEntityGoal<Player> avoidPlayersGoal;
    @Nullable
    private TemptGoal temptGoal;
    private float lieDownAmount;
    private float lieDownAmountO;
    private float lieDownAmountTail;
    private float lieDownAmountOTail;
    private float relaxStateOneAmount;
    private float relaxStateOneAmountO;

    public Cat(EntityType<? extends Cat> $$0, Level $$1) {
        super((EntityType<? extends TamableAnimal>)$$0, $$1);
    }

    public ResourceLocation getResourceLocation() {
        return this.getVariant().texture();
    }

    @Override
    protected void registerGoals() {
        this.temptGoal = new CatTemptGoal(this, 0.6, TEMPT_INGREDIENT, true);
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new CatRelaxOnOwnerGoal(this));
        this.goalSelector.addGoal(3, this.temptGoal);
        this.goalSelector.addGoal(5, new CatLieOnBedGoal(this, 1.1, 8));
        this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0, 10.0f, 5.0f, false));
        this.goalSelector.addGoal(7, new CatSitOnBlockGoal(this, 0.8));
        this.goalSelector.addGoal(8, new LeapAtTargetGoal(this, 0.3f));
        this.goalSelector.addGoal(9, new OcelotAttackGoal(this));
        this.goalSelector.addGoal(10, new BreedGoal(this, 0.8));
        this.goalSelector.addGoal(11, new WaterAvoidingRandomStrollGoal((PathfinderMob)this, 0.8, 1.0000001E-5f));
        this.goalSelector.addGoal(12, new LookAtPlayerGoal(this, Player.class, 10.0f));
        this.targetSelector.addGoal(1, new NonTameRandomTargetGoal<Rabbit>(this, Rabbit.class, false, null));
        this.targetSelector.addGoal(1, new NonTameRandomTargetGoal<Turtle>(this, Turtle.class, false, Turtle.BABY_ON_LAND_SELECTOR));
    }

    @Override
    public CatVariant getVariant() {
        return this.entityData.get(DATA_VARIANT_ID);
    }

    @Override
    public void setVariant(CatVariant $$0) {
        this.entityData.set(DATA_VARIANT_ID, $$0);
    }

    public void setLying(boolean $$0) {
        this.entityData.set(IS_LYING, $$0);
    }

    public boolean isLying() {
        return this.entityData.get(IS_LYING);
    }

    public void setRelaxStateOne(boolean $$0) {
        this.entityData.set(RELAX_STATE_ONE, $$0);
    }

    public boolean isRelaxStateOne() {
        return this.entityData.get(RELAX_STATE_ONE);
    }

    public DyeColor getCollarColor() {
        return DyeColor.byId(this.entityData.get(DATA_COLLAR_COLOR));
    }

    public void setCollarColor(DyeColor $$0) {
        this.entityData.set(DATA_COLLAR_COLOR, $$0.getId());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_VARIANT_ID, BuiltInRegistries.CAT_VARIANT.getOrThrow(CatVariant.BLACK));
        this.entityData.define(IS_LYING, false);
        this.entityData.define(RELAX_STATE_ONE, false);
        this.entityData.define(DATA_COLLAR_COLOR, DyeColor.RED.getId());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        super.addAdditionalSaveData($$0);
        $$0.putString("variant", BuiltInRegistries.CAT_VARIANT.getKey(this.getVariant()).toString());
        $$0.putByte("CollarColor", (byte)this.getCollarColor().getId());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$0) {
        super.readAdditionalSaveData($$0);
        CatVariant $$1 = BuiltInRegistries.CAT_VARIANT.get(ResourceLocation.tryParse($$0.getString("variant")));
        if ($$1 != null) {
            this.setVariant($$1);
        }
        if ($$0.contains("CollarColor", 99)) {
            this.setCollarColor(DyeColor.byId($$0.getInt("CollarColor")));
        }
    }

    @Override
    public void customServerAiStep() {
        if (this.getMoveControl().hasWanted()) {
            double $$0 = this.getMoveControl().getSpeedModifier();
            if ($$0 == 0.6) {
                this.setPose(Pose.CROUCHING);
                this.setSprinting(false);
            } else if ($$0 == 1.33) {
                this.setPose(Pose.STANDING);
                this.setSprinting(true);
            } else {
                this.setPose(Pose.STANDING);
                this.setSprinting(false);
            }
        } else {
            this.setPose(Pose.STANDING);
            this.setSprinting(false);
        }
    }

    @Override
    @Nullable
    protected SoundEvent getAmbientSound() {
        if (this.isTame()) {
            if (this.isInLove()) {
                return SoundEvents.CAT_PURR;
            }
            if (this.random.nextInt(4) == 0) {
                return SoundEvents.CAT_PURREOW;
            }
            return SoundEvents.CAT_AMBIENT;
        }
        return SoundEvents.CAT_STRAY_AMBIENT;
    }

    @Override
    public int getAmbientSoundInterval() {
        return 120;
    }

    public void hiss() {
        this.playSound(SoundEvents.CAT_HISS, this.getSoundVolume(), this.getVoicePitch());
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource $$0) {
        return SoundEvents.CAT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.CAT_DEATH;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0).add(Attributes.MOVEMENT_SPEED, 0.3f).add(Attributes.ATTACK_DAMAGE, 3.0);
    }

    @Override
    public boolean causeFallDamage(float $$0, float $$1, DamageSource $$2) {
        return false;
    }

    @Override
    protected void usePlayerItem(Player $$0, InteractionHand $$1, ItemStack $$2) {
        if (this.isFood($$2)) {
            this.playSound(SoundEvents.CAT_EAT, 1.0f, 1.0f);
        }
        super.usePlayerItem($$0, $$1, $$2);
    }

    private float getAttackDamage() {
        return (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
    }

    @Override
    public boolean doHurtTarget(Entity $$0) {
        return $$0.hurt(DamageSource.mobAttack(this), this.getAttackDamage());
    }

    @Override
    public void tick() {
        super.tick();
        if (this.temptGoal != null && this.temptGoal.isRunning() && !this.isTame() && this.tickCount % 100 == 0) {
            this.playSound(SoundEvents.CAT_BEG_FOR_FOOD, 1.0f, 1.0f);
        }
        this.handleLieDown();
    }

    private void handleLieDown() {
        if ((this.isLying() || this.isRelaxStateOne()) && this.tickCount % 5 == 0) {
            this.playSound(SoundEvents.CAT_PURR, 0.6f + 0.4f * (this.random.nextFloat() - this.random.nextFloat()), 1.0f);
        }
        this.updateLieDownAmount();
        this.updateRelaxStateOneAmount();
    }

    private void updateLieDownAmount() {
        this.lieDownAmountO = this.lieDownAmount;
        this.lieDownAmountOTail = this.lieDownAmountTail;
        if (this.isLying()) {
            this.lieDownAmount = Math.min((float)1.0f, (float)(this.lieDownAmount + 0.15f));
            this.lieDownAmountTail = Math.min((float)1.0f, (float)(this.lieDownAmountTail + 0.08f));
        } else {
            this.lieDownAmount = Math.max((float)0.0f, (float)(this.lieDownAmount - 0.22f));
            this.lieDownAmountTail = Math.max((float)0.0f, (float)(this.lieDownAmountTail - 0.13f));
        }
    }

    private void updateRelaxStateOneAmount() {
        this.relaxStateOneAmountO = this.relaxStateOneAmount;
        this.relaxStateOneAmount = this.isRelaxStateOne() ? Math.min((float)1.0f, (float)(this.relaxStateOneAmount + 0.1f)) : Math.max((float)0.0f, (float)(this.relaxStateOneAmount - 0.13f));
    }

    public float getLieDownAmount(float $$0) {
        return Mth.lerp($$0, this.lieDownAmountO, this.lieDownAmount);
    }

    public float getLieDownAmountTail(float $$0) {
        return Mth.lerp($$0, this.lieDownAmountOTail, this.lieDownAmountTail);
    }

    public float getRelaxStateOneAmount(float $$0) {
        return Mth.lerp($$0, this.relaxStateOneAmountO, this.relaxStateOneAmount);
    }

    @Override
    @Nullable
    public Cat getBreedOffspring(ServerLevel $$0, AgeableMob $$1) {
        Cat $$2 = EntityType.CAT.create($$0);
        if ($$2 != null && $$1 instanceof Cat) {
            Cat $$3 = (Cat)$$1;
            if (this.random.nextBoolean()) {
                $$2.setVariant(this.getVariant());
            } else {
                $$2.setVariant($$3.getVariant());
            }
            if (this.isTame()) {
                $$2.setOwnerUUID(this.getOwnerUUID());
                $$2.setTame(true);
                if (this.random.nextBoolean()) {
                    $$2.setCollarColor(this.getCollarColor());
                } else {
                    $$2.setCollarColor($$3.getCollarColor());
                }
            }
        }
        return $$2;
    }

    @Override
    public boolean canMate(Animal $$0) {
        if (!this.isTame()) {
            return false;
        }
        if (!($$0 instanceof Cat)) {
            return false;
        }
        Cat $$1 = (Cat)$$0;
        return $$1.isTame() && super.canMate($$0);
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor $$02, DifficultyInstance $$12, MobSpawnType $$2, @Nullable SpawnGroupData $$3, @Nullable CompoundTag $$4) {
        $$3 = super.finalizeSpawn($$02, $$12, $$2, $$3, $$4);
        boolean $$5 = $$02.getMoonBrightness() > 0.9f;
        TagKey<CatVariant> $$6 = $$5 ? CatVariantTags.FULL_MOON_SPAWNS : CatVariantTags.DEFAULT_SPAWNS;
        BuiltInRegistries.CAT_VARIANT.getTag($$6).flatMap($$1 -> $$1.getRandomElement($$02.getRandom())).ifPresent($$0 -> this.setVariant((CatVariant)((Object)((Object)$$0.value()))));
        ServerLevel $$7 = $$02.getLevel();
        if ($$7.structureManager().getStructureWithPieceAt(this.blockPosition(), StructureTags.CATS_SPAWN_AS_BLACK).isValid()) {
            this.setVariant(BuiltInRegistries.CAT_VARIANT.getOrThrow(CatVariant.ALL_BLACK));
            this.setPersistenceRequired();
        }
        return $$3;
    }

    /*
     * Enabled aggressive block sorting
     */
    @Override
    public InteractionResult mobInteract(Player $$0, InteractionHand $$1) {
        InteractionResult $$6;
        ItemStack $$2 = $$0.getItemInHand($$1);
        Item $$3 = $$2.getItem();
        if (this.level.isClientSide) {
            if (this.isTame() && this.isOwnedBy($$0)) {
                return InteractionResult.SUCCESS;
            }
            if (this.isFood($$2) && (this.getHealth() < this.getMaxHealth() || !this.isTame())) {
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }
        if (this.isTame()) {
            if (this.isOwnedBy($$0)) {
                if ($$3 instanceof DyeItem) {
                    DyeColor $$4 = ((DyeItem)$$3).getDyeColor();
                    if ($$4 != this.getCollarColor()) {
                        this.setCollarColor($$4);
                        if (!$$0.getAbilities().instabuild) {
                            $$2.shrink(1);
                        }
                        this.setPersistenceRequired();
                        return InteractionResult.CONSUME;
                    }
                } else {
                    if ($$3.isEdible() && this.isFood($$2) && this.getHealth() < this.getMaxHealth()) {
                        this.usePlayerItem($$0, $$1, $$2);
                        this.heal($$3.getFoodProperties().getNutrition());
                        return InteractionResult.CONSUME;
                    }
                    InteractionResult $$5 = super.mobInteract($$0, $$1);
                    if (!$$5.consumesAction() || this.isBaby()) {
                        this.setOrderedToSit(!this.isOrderedToSit());
                    }
                    return $$5;
                }
            }
        } else if (this.isFood($$2)) {
            this.usePlayerItem($$0, $$1, $$2);
            if (this.random.nextInt(3) == 0) {
                this.tame($$0);
                this.setOrderedToSit(true);
                this.level.broadcastEntityEvent(this, (byte)7);
            } else {
                this.level.broadcastEntityEvent(this, (byte)6);
            }
            this.setPersistenceRequired();
            return InteractionResult.CONSUME;
        }
        if (($$6 = super.mobInteract($$0, $$1)).consumesAction()) {
            this.setPersistenceRequired();
        }
        return $$6;
    }

    @Override
    public boolean isFood(ItemStack $$0) {
        return TEMPT_INGREDIENT.test($$0);
    }

    @Override
    protected float getStandingEyeHeight(Pose $$0, EntityDimensions $$1) {
        return $$1.height * 0.5f;
    }

    @Override
    public boolean removeWhenFarAway(double $$0) {
        return !this.isTame() && this.tickCount > 2400;
    }

    @Override
    protected void reassessTameGoals() {
        if (this.avoidPlayersGoal == null) {
            this.avoidPlayersGoal = new CatAvoidEntityGoal<Player>(this, Player.class, 16.0f, 0.8, 1.33);
        }
        this.goalSelector.removeGoal(this.avoidPlayersGoal);
        if (!this.isTame()) {
            this.goalSelector.addGoal(4, this.avoidPlayersGoal);
        }
    }

    @Override
    public boolean isSteppingCarefully() {
        return this.isCrouching() || super.isSteppingCarefully();
    }

    static class CatTemptGoal
    extends TemptGoal {
        @Nullable
        private Player selectedPlayer;
        private final Cat cat;

        public CatTemptGoal(Cat $$0, double $$1, Ingredient $$2, boolean $$3) {
            super($$0, $$1, $$2, $$3);
            this.cat = $$0;
        }

        @Override
        public void tick() {
            super.tick();
            if (this.selectedPlayer == null && this.mob.getRandom().nextInt(this.adjustedTickDelay(600)) == 0) {
                this.selectedPlayer = this.player;
            } else if (this.mob.getRandom().nextInt(this.adjustedTickDelay(500)) == 0) {
                this.selectedPlayer = null;
            }
        }

        @Override
        protected boolean canScare() {
            if (this.selectedPlayer != null && this.selectedPlayer.equals(this.player)) {
                return false;
            }
            return super.canScare();
        }

        @Override
        public boolean canUse() {
            return super.canUse() && !this.cat.isTame();
        }
    }

    static class CatRelaxOnOwnerGoal
    extends Goal {
        private final Cat cat;
        @Nullable
        private Player ownerPlayer;
        @Nullable
        private BlockPos goalPos;
        private int onBedTicks;

        public CatRelaxOnOwnerGoal(Cat $$0) {
            this.cat = $$0;
        }

        @Override
        public boolean canUse() {
            if (!this.cat.isTame()) {
                return false;
            }
            if (this.cat.isOrderedToSit()) {
                return false;
            }
            LivingEntity $$0 = this.cat.getOwner();
            if ($$0 instanceof Player) {
                this.ownerPlayer = (Player)$$0;
                if (!$$0.isSleeping()) {
                    return false;
                }
                if (this.cat.distanceToSqr(this.ownerPlayer) > 100.0) {
                    return false;
                }
                BlockPos $$12 = this.ownerPlayer.blockPosition();
                BlockState $$2 = this.cat.level.getBlockState($$12);
                if ($$2.is(BlockTags.BEDS)) {
                    this.goalPos = (BlockPos)$$2.getOptionalValue(BedBlock.FACING).map($$1 -> $$12.relative($$1.getOpposite())).orElseGet(() -> new BlockPos($$12));
                    return !this.spaceIsOccupied();
                }
            }
            return false;
        }

        private boolean spaceIsOccupied() {
            List $$0 = this.cat.level.getEntitiesOfClass(Cat.class, new AABB(this.goalPos).inflate(2.0));
            for (Cat $$1 : $$0) {
                if ($$1 == this.cat || !$$1.isLying() && !$$1.isRelaxStateOne()) continue;
                return true;
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return this.cat.isTame() && !this.cat.isOrderedToSit() && this.ownerPlayer != null && this.ownerPlayer.isSleeping() && this.goalPos != null && !this.spaceIsOccupied();
        }

        @Override
        public void start() {
            if (this.goalPos != null) {
                this.cat.setInSittingPose(false);
                this.cat.getNavigation().moveTo(this.goalPos.getX(), this.goalPos.getY(), this.goalPos.getZ(), 1.1f);
            }
        }

        @Override
        public void stop() {
            this.cat.setLying(false);
            float $$0 = this.cat.level.getTimeOfDay(1.0f);
            if (this.ownerPlayer.getSleepTimer() >= 100 && (double)$$0 > 0.77 && (double)$$0 < 0.8 && (double)this.cat.level.getRandom().nextFloat() < 0.7) {
                this.giveMorningGift();
            }
            this.onBedTicks = 0;
            this.cat.setRelaxStateOne(false);
            this.cat.getNavigation().stop();
        }

        private void giveMorningGift() {
            RandomSource $$0 = this.cat.getRandom();
            BlockPos.MutableBlockPos $$1 = new BlockPos.MutableBlockPos();
            $$1.set(this.cat.isLeashed() ? this.cat.getLeashHolder().blockPosition() : this.cat.blockPosition());
            this.cat.randomTeleport($$1.getX() + $$0.nextInt(11) - 5, $$1.getY() + $$0.nextInt(5) - 2, $$1.getZ() + $$0.nextInt(11) - 5, false);
            $$1.set(this.cat.blockPosition());
            LootTable $$2 = this.cat.level.getServer().getLootTables().get(BuiltInLootTables.CAT_MORNING_GIFT);
            LootContext.Builder $$3 = new LootContext.Builder((ServerLevel)this.cat.level).withParameter(LootContextParams.ORIGIN, this.cat.position()).withParameter(LootContextParams.THIS_ENTITY, this.cat).withRandom($$0);
            ObjectArrayList<ItemStack> $$4 = $$2.getRandomItems($$3.create(LootContextParamSets.GIFT));
            for (ItemStack $$5 : $$4) {
                this.cat.level.addFreshEntity(new ItemEntity(this.cat.level, (double)$$1.getX() - (double)Mth.sin(this.cat.yBodyRot * ((float)Math.PI / 180)), $$1.getY(), (double)$$1.getZ() + (double)Mth.cos(this.cat.yBodyRot * ((float)Math.PI / 180)), $$5));
            }
        }

        @Override
        public void tick() {
            if (this.ownerPlayer != null && this.goalPos != null) {
                this.cat.setInSittingPose(false);
                this.cat.getNavigation().moveTo(this.goalPos.getX(), this.goalPos.getY(), this.goalPos.getZ(), 1.1f);
                if (this.cat.distanceToSqr(this.ownerPlayer) < 2.5) {
                    ++this.onBedTicks;
                    if (this.onBedTicks > this.adjustedTickDelay(16)) {
                        this.cat.setLying(true);
                        this.cat.setRelaxStateOne(false);
                    } else {
                        this.cat.lookAt(this.ownerPlayer, 45.0f, 45.0f);
                        this.cat.setRelaxStateOne(true);
                    }
                } else {
                    this.cat.setLying(false);
                }
            }
        }
    }

    static class CatAvoidEntityGoal<T extends LivingEntity>
    extends AvoidEntityGoal<T> {
        private final Cat cat;

        public CatAvoidEntityGoal(Cat $$0, Class<T> $$1, float $$2, double $$3, double $$4) {
            super($$0, $$1, $$2, $$3, $$4, (Predicate<LivingEntity>)((Predicate)arg_0 -> EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(arg_0)));
            this.cat = $$0;
        }

        @Override
        public boolean canUse() {
            return !this.cat.isTame() && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return !this.cat.isTame() && super.canContinueToUse();
        }
    }
}