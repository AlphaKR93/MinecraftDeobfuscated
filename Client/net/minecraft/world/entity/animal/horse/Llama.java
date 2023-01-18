/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  java.lang.Class
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.function.IntFunction
 *  java.util.function.Predicate
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.animal.horse;

import com.mojang.serialization.Codec;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Container;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LlamaFollowCaravanGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.RunAroundLikeCrazyGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LlamaSpit;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WoolCarpetBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class Llama
extends AbstractChestedHorse
implements VariantHolder<Variant>,
RangedAttackMob {
    private static final int MAX_STRENGTH = 5;
    private static final Ingredient FOOD_ITEMS = Ingredient.of(Items.WHEAT, Blocks.HAY_BLOCK.asItem());
    private static final EntityDataAccessor<Integer> DATA_STRENGTH_ID = SynchedEntityData.defineId(Llama.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_SWAG_ID = SynchedEntityData.defineId(Llama.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_VARIANT_ID = SynchedEntityData.defineId(Llama.class, EntityDataSerializers.INT);
    boolean didSpit;
    @Nullable
    private Llama caravanHead;
    @Nullable
    private Llama caravanTail;

    public Llama(EntityType<? extends Llama> $$0, Level $$1) {
        super((EntityType<? extends AbstractChestedHorse>)$$0, $$1);
    }

    public boolean isTraderLlama() {
        return false;
    }

    private void setStrength(int $$0) {
        this.entityData.set(DATA_STRENGTH_ID, Math.max((int)1, (int)Math.min((int)5, (int)$$0)));
    }

    private void setRandomStrength(RandomSource $$0) {
        int $$1 = $$0.nextFloat() < 0.04f ? 5 : 3;
        this.setStrength(1 + $$0.nextInt($$1));
    }

    public int getStrength() {
        return this.entityData.get(DATA_STRENGTH_ID);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        super.addAdditionalSaveData($$0);
        $$0.putInt("Variant", this.getVariant().id);
        $$0.putInt("Strength", this.getStrength());
        if (!this.inventory.getItem(1).isEmpty()) {
            $$0.put("DecorItem", this.inventory.getItem(1).save(new CompoundTag()));
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$0) {
        this.setStrength($$0.getInt("Strength"));
        super.readAdditionalSaveData($$0);
        this.setVariant(Variant.byId($$0.getInt("Variant")));
        if ($$0.contains("DecorItem", 10)) {
            this.inventory.setItem(1, ItemStack.of($$0.getCompound("DecorItem")));
        }
        this.updateContainerEquipment();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new RunAroundLikeCrazyGoal(this, 1.2));
        this.goalSelector.addGoal(2, new LlamaFollowCaravanGoal(this, 2.1f));
        this.goalSelector.addGoal(3, new RangedAttackGoal(this, 1.25, 40, 20.0f));
        this.goalSelector.addGoal(3, new PanicGoal(this, 1.2));
        this.goalSelector.addGoal(4, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(5, new TemptGoal(this, 1.25, Ingredient.of(Items.HAY_BLOCK), false));
        this.goalSelector.addGoal(6, new FollowParentGoal(this, 1.0));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.7));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 6.0f));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new LlamaHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new LlamaAttackWolfGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Llama.createBaseChestedHorseAttributes().add(Attributes.FOLLOW_RANGE, 40.0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_STRENGTH_ID, 0);
        this.entityData.define(DATA_SWAG_ID, -1);
        this.entityData.define(DATA_VARIANT_ID, 0);
    }

    @Override
    public Variant getVariant() {
        return Variant.byId(this.entityData.get(DATA_VARIANT_ID));
    }

    @Override
    public void setVariant(Variant $$0) {
        this.entityData.set(DATA_VARIANT_ID, $$0.id);
    }

    @Override
    protected int getInventorySize() {
        if (this.hasChest()) {
            return 2 + 3 * this.getInventoryColumns();
        }
        return super.getInventorySize();
    }

    @Override
    public void positionRider(Entity $$0) {
        if (!this.hasPassenger($$0)) {
            return;
        }
        float $$1 = Mth.cos(this.yBodyRot * ((float)Math.PI / 180));
        float $$2 = Mth.sin(this.yBodyRot * ((float)Math.PI / 180));
        float $$3 = 0.3f;
        $$0.setPos(this.getX() + (double)(0.3f * $$2), this.getY() + this.getPassengersRidingOffset() + $$0.getMyRidingOffset(), this.getZ() - (double)(0.3f * $$1));
    }

    @Override
    public double getPassengersRidingOffset() {
        return (double)this.getBbHeight() * 0.6;
    }

    @Override
    @Nullable
    public LivingEntity getControllingPassenger() {
        return null;
    }

    @Override
    public boolean isFood(ItemStack $$0) {
        return FOOD_ITEMS.test($$0);
    }

    @Override
    protected boolean handleEating(Player $$0, ItemStack $$1) {
        SoundEvent $$6;
        int $$2 = 0;
        int $$3 = 0;
        float $$4 = 0.0f;
        boolean $$5 = false;
        if ($$1.is(Items.WHEAT)) {
            $$2 = 10;
            $$3 = 3;
            $$4 = 2.0f;
        } else if ($$1.is(Blocks.HAY_BLOCK.asItem())) {
            $$2 = 90;
            $$3 = 6;
            $$4 = 10.0f;
            if (this.isTamed() && this.getAge() == 0 && this.canFallInLove()) {
                $$5 = true;
                this.setInLove($$0);
            }
        }
        if (this.getHealth() < this.getMaxHealth() && $$4 > 0.0f) {
            this.heal($$4);
            $$5 = true;
        }
        if (this.isBaby() && $$2 > 0) {
            this.level.addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), 0.0, 0.0, 0.0);
            if (!this.level.isClientSide) {
                this.ageUp($$2);
            }
            $$5 = true;
        }
        if ($$3 > 0 && ($$5 || !this.isTamed()) && this.getTemper() < this.getMaxTemper()) {
            $$5 = true;
            if (!this.level.isClientSide) {
                this.modifyTemper($$3);
            }
        }
        if ($$5 && !this.isSilent() && ($$6 = this.getEatingSound()) != null) {
            this.level.playSound(null, this.getX(), this.getY(), this.getZ(), this.getEatingSound(), this.getSoundSource(), 1.0f, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.2f);
        }
        return $$5;
    }

    @Override
    public boolean isImmobile() {
        return this.isDeadOrDying() || this.isEating();
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor $$0, DifficultyInstance $$1, MobSpawnType $$2, @Nullable SpawnGroupData $$3, @Nullable CompoundTag $$4) {
        Variant $$7;
        RandomSource $$5 = $$0.getRandom();
        this.setRandomStrength($$5);
        if ($$3 instanceof LlamaGroupData) {
            Variant $$6 = ((LlamaGroupData)$$3).variant;
        } else {
            $$7 = Util.getRandom(Variant.values(), $$5);
            $$3 = new LlamaGroupData($$7);
        }
        this.setVariant($$7);
        return super.finalizeSpawn($$0, $$1, $$2, $$3, $$4);
    }

    @Override
    protected boolean canPerformRearing() {
        return false;
    }

    @Override
    protected SoundEvent getAngrySound() {
        return SoundEvents.LLAMA_ANGRY;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.LLAMA_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource $$0) {
        return SoundEvents.LLAMA_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.LLAMA_DEATH;
    }

    @Override
    @Nullable
    protected SoundEvent getEatingSound() {
        return SoundEvents.LLAMA_EAT;
    }

    @Override
    protected void playStepSound(BlockPos $$0, BlockState $$1) {
        this.playSound(SoundEvents.LLAMA_STEP, 0.15f, 1.0f);
    }

    @Override
    protected void playChestEquipsSound() {
        this.playSound(SoundEvents.LLAMA_CHEST, 1.0f, (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
    }

    @Override
    public int getInventoryColumns() {
        return this.getStrength();
    }

    @Override
    public boolean canWearArmor() {
        return true;
    }

    @Override
    public boolean isWearingArmor() {
        return !this.inventory.getItem(1).isEmpty();
    }

    @Override
    public boolean isArmor(ItemStack $$0) {
        return $$0.is(ItemTags.WOOL_CARPETS);
    }

    @Override
    public boolean isSaddleable() {
        return false;
    }

    @Override
    public void containerChanged(Container $$0) {
        DyeColor $$1 = this.getSwag();
        super.containerChanged($$0);
        DyeColor $$2 = this.getSwag();
        if (this.tickCount > 20 && $$2 != null && $$2 != $$1) {
            this.playSound(SoundEvents.LLAMA_SWAG, 0.5f, 1.0f);
        }
    }

    @Override
    protected void updateContainerEquipment() {
        if (this.level.isClientSide) {
            return;
        }
        super.updateContainerEquipment();
        this.setSwag(Llama.getDyeColor(this.inventory.getItem(1)));
    }

    private void setSwag(@Nullable DyeColor $$0) {
        this.entityData.set(DATA_SWAG_ID, $$0 == null ? -1 : $$0.getId());
    }

    @Nullable
    private static DyeColor getDyeColor(ItemStack $$0) {
        Block $$1 = Block.byItem($$0.getItem());
        if ($$1 instanceof WoolCarpetBlock) {
            return ((WoolCarpetBlock)$$1).getColor();
        }
        return null;
    }

    @Nullable
    public DyeColor getSwag() {
        int $$0 = this.entityData.get(DATA_SWAG_ID);
        return $$0 == -1 ? null : DyeColor.byId($$0);
    }

    @Override
    public int getMaxTemper() {
        return 30;
    }

    @Override
    public boolean canMate(Animal $$0) {
        return $$0 != this && $$0 instanceof Llama && this.canParent() && ((Llama)$$0).canParent();
    }

    @Override
    @Nullable
    public Llama getBreedOffspring(ServerLevel $$0, AgeableMob $$1) {
        Llama $$2 = this.makeNewLlama();
        if ($$2 != null) {
            this.setOffspringAttributes($$1, $$2);
            Llama $$3 = (Llama)$$1;
            int $$4 = this.random.nextInt(Math.max((int)this.getStrength(), (int)$$3.getStrength())) + 1;
            if (this.random.nextFloat() < 0.03f) {
                ++$$4;
            }
            $$2.setStrength($$4);
            $$2.setVariant(this.random.nextBoolean() ? this.getVariant() : $$3.getVariant());
        }
        return $$2;
    }

    @Nullable
    protected Llama makeNewLlama() {
        return EntityType.LLAMA.create(this.level);
    }

    private void spit(LivingEntity $$0) {
        LlamaSpit $$1 = new LlamaSpit(this.level, this);
        double $$2 = $$0.getX() - this.getX();
        double $$3 = $$0.getY(0.3333333333333333) - $$1.getY();
        double $$4 = $$0.getZ() - this.getZ();
        double $$5 = Math.sqrt((double)($$2 * $$2 + $$4 * $$4)) * (double)0.2f;
        $$1.shoot($$2, $$3 + $$5, $$4, 1.5f, 10.0f);
        if (!this.isSilent()) {
            this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.LLAMA_SPIT, this.getSoundSource(), 1.0f, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.2f);
        }
        this.level.addFreshEntity($$1);
        this.didSpit = true;
    }

    void setDidSpit(boolean $$0) {
        this.didSpit = $$0;
    }

    @Override
    public boolean causeFallDamage(float $$0, float $$1, DamageSource $$2) {
        int $$3 = this.calculateFallDamage($$0, $$1);
        if ($$3 <= 0) {
            return false;
        }
        if ($$0 >= 6.0f) {
            this.hurt($$2, $$3);
            if (this.isVehicle()) {
                for (Entity $$4 : this.getIndirectPassengers()) {
                    $$4.hurt($$2, $$3);
                }
            }
        }
        this.playBlockFallSound();
        return true;
    }

    public void leaveCaravan() {
        if (this.caravanHead != null) {
            this.caravanHead.caravanTail = null;
        }
        this.caravanHead = null;
    }

    public void joinCaravan(Llama $$0) {
        this.caravanHead = $$0;
        this.caravanHead.caravanTail = this;
    }

    public boolean hasCaravanTail() {
        return this.caravanTail != null;
    }

    public boolean inCaravan() {
        return this.caravanHead != null;
    }

    @Nullable
    public Llama getCaravanHead() {
        return this.caravanHead;
    }

    @Override
    protected double followLeashSpeed() {
        return 2.0;
    }

    @Override
    protected void followMommy() {
        if (!this.inCaravan() && this.isBaby()) {
            super.followMommy();
        }
    }

    @Override
    public boolean canEatGrass() {
        return false;
    }

    @Override
    public void performRangedAttack(LivingEntity $$0, float $$1) {
        this.spit($$0);
    }

    @Override
    public Vec3 getLeashOffset() {
        return new Vec3(0.0, 0.75 * (double)this.getEyeHeight(), (double)this.getBbWidth() * 0.5);
    }

    public static enum Variant implements StringRepresentable
    {
        CREAMY(0, "creamy"),
        WHITE(1, "white"),
        BROWN(2, "brown"),
        GRAY(3, "gray");

        public static final Codec<Variant> CODEC;
        private static final IntFunction<Variant> BY_ID;
        final int id;
        private final String name;

        private Variant(int $$0, String $$1) {
            this.id = $$0;
            this.name = $$1;
        }

        public int getId() {
            return this.id;
        }

        public static Variant byId(int $$0) {
            return (Variant)BY_ID.apply($$0);
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        static {
            CODEC = StringRepresentable.fromEnum((Supplier<E[]>)((Supplier)Variant::values));
            BY_ID = ByIdMap.continuous(Variant::getId, Variant.values(), ByIdMap.OutOfBoundsStrategy.CLAMP);
        }
    }

    static class LlamaHurtByTargetGoal
    extends HurtByTargetGoal {
        public LlamaHurtByTargetGoal(Llama $$0) {
            super($$0, new Class[0]);
        }

        @Override
        public boolean canContinueToUse() {
            if (this.mob instanceof Llama) {
                Llama $$0 = (Llama)this.mob;
                if ($$0.didSpit) {
                    $$0.setDidSpit(false);
                    return false;
                }
            }
            return super.canContinueToUse();
        }
    }

    static class LlamaAttackWolfGoal
    extends NearestAttackableTargetGoal<Wolf> {
        public LlamaAttackWolfGoal(Llama $$02) {
            super($$02, Wolf.class, 16, false, true, (Predicate<LivingEntity>)((Predicate)$$0 -> !((Wolf)$$0).isTame()));
        }

        @Override
        protected double getFollowDistance() {
            return super.getFollowDistance() * 0.25;
        }
    }

    static class LlamaGroupData
    extends AgeableMob.AgeableMobGroupData {
        public final Variant variant;

        LlamaGroupData(Variant $$0) {
            super(true);
            this.variant = $$0;
        }
    }
}