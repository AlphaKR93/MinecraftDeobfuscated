/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableList
 *  com.mojang.serialization.Dynamic
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.animal.frog;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.frog.TadpoleAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class Tadpole
extends AbstractFish {
    @VisibleForTesting
    public static int ticksToBeFrog = Math.abs((int)-24000);
    public static float HITBOX_WIDTH = 0.4f;
    public static float HITBOX_HEIGHT = 0.3f;
    private int age;
    protected static final ImmutableList<SensorType<? extends Sensor<? super Tadpole>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.HURT_BY, SensorType.FROG_TEMPTATIONS);
    protected static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.LOOK_TARGET, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.NEAREST_VISIBLE_ADULT, MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, MemoryModuleType.IS_TEMPTED, MemoryModuleType.TEMPTING_PLAYER, MemoryModuleType.BREED_TARGET, MemoryModuleType.IS_PANICKING);

    public Tadpole(EntityType<? extends AbstractFish> $$0, Level $$1) {
        super($$0, $$1);
        this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.02f, 0.1f, true);
        this.lookControl = new SmoothSwimmingLookControl(this, 10);
    }

    @Override
    protected PathNavigation createNavigation(Level $$0) {
        return new WaterBoundPathNavigation(this, $$0);
    }

    protected Brain.Provider<Tadpole> brainProvider() {
        return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> $$0) {
        return TadpoleAi.makeBrain(this.brainProvider().makeBrain($$0));
    }

    public Brain<Tadpole> getBrain() {
        return super.getBrain();
    }

    @Override
    protected SoundEvent getFlopSound() {
        return SoundEvents.TADPOLE_FLOP;
    }

    @Override
    protected void customServerAiStep() {
        this.level.getProfiler().push("tadpoleBrain");
        this.getBrain().tick((ServerLevel)this.level, this);
        this.level.getProfiler().pop();
        this.level.getProfiler().push("tadpoleActivityUpdate");
        TadpoleAi.updateActivity(this);
        this.level.getProfiler().pop();
        super.customServerAiStep();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 1.0).add(Attributes.MAX_HEALTH, 6.0);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level.isClientSide) {
            this.setAge(this.age + 1);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        super.addAdditionalSaveData($$0);
        $$0.putInt("Age", this.age);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$0) {
        super.readAdditionalSaveData($$0);
        this.setAge($$0.getInt("Age"));
    }

    @Override
    @Nullable
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Override
    @Nullable
    protected SoundEvent getHurtSound(DamageSource $$0) {
        return SoundEvents.TADPOLE_HURT;
    }

    @Override
    @Nullable
    protected SoundEvent getDeathSound() {
        return SoundEvents.TADPOLE_DEATH;
    }

    @Override
    public InteractionResult mobInteract(Player $$0, InteractionHand $$1) {
        ItemStack $$2 = $$0.getItemInHand($$1);
        if (this.isFood($$2)) {
            this.feed($$0, $$2);
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        }
        return (InteractionResult)((Object)Bucketable.bucketMobPickup($$0, $$1, this).orElse((Object)super.mobInteract($$0, $$1)));
    }

    @Override
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        DebugPackets.sendEntityBrain(this);
    }

    @Override
    public boolean fromBucket() {
        return true;
    }

    @Override
    public void setFromBucket(boolean $$0) {
    }

    @Override
    public void saveToBucketTag(ItemStack $$0) {
        Bucketable.saveDefaultDataToBucketTag(this, $$0);
        CompoundTag $$1 = $$0.getOrCreateTag();
        $$1.putInt("Age", this.getAge());
    }

    @Override
    public void loadFromBucketTag(CompoundTag $$0) {
        Bucketable.loadDefaultDataFromBucketTag(this, $$0);
        if ($$0.contains("Age")) {
            this.setAge($$0.getInt("Age"));
        }
    }

    @Override
    public ItemStack getBucketItemStack() {
        return new ItemStack(Items.TADPOLE_BUCKET);
    }

    @Override
    public SoundEvent getPickupSound() {
        return SoundEvents.BUCKET_FILL_TADPOLE;
    }

    private boolean isFood(ItemStack $$0) {
        return Frog.TEMPTATION_ITEM.test($$0);
    }

    private void feed(Player $$0, ItemStack $$1) {
        this.usePlayerItem($$0, $$1);
        this.ageUp(AgeableMob.getSpeedUpSecondsWhenFeeding(this.getTicksLeftUntilAdult()));
        this.level.addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), 0.0, 0.0, 0.0);
    }

    private void usePlayerItem(Player $$0, ItemStack $$1) {
        if (!$$0.getAbilities().instabuild) {
            $$1.shrink(1);
        }
    }

    private int getAge() {
        return this.age;
    }

    private void ageUp(int $$0) {
        this.setAge(this.age + $$0 * 20);
    }

    private void setAge(int $$0) {
        this.age = $$0;
        if (this.age >= ticksToBeFrog) {
            this.ageUp();
        }
    }

    private void ageUp() {
        Level level = this.level;
        if (level instanceof ServerLevel) {
            ServerLevel $$0 = (ServerLevel)level;
            Frog $$1 = EntityType.FROG.create(this.level);
            if ($$1 != null) {
                $$1.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
                $$1.finalizeSpawn($$0, this.level.getCurrentDifficultyAt($$1.blockPosition()), MobSpawnType.CONVERSION, null, null);
                $$1.setNoAi(this.isNoAi());
                if (this.hasCustomName()) {
                    $$1.setCustomName(this.getCustomName());
                    $$1.setCustomNameVisible(this.isCustomNameVisible());
                }
                $$1.setPersistenceRequired();
                this.playSound(SoundEvents.TADPOLE_GROW_UP, 0.15f, 1.0f);
                $$0.addFreshEntityWithPassengers($$1);
                this.discard();
            }
        }
    }

    private int getTicksLeftUntilAdult() {
        return Math.max((int)0, (int)(ticksToBeFrog - this.age));
    }

    @Override
    public boolean shouldDropExperience() {
        return false;
    }
}