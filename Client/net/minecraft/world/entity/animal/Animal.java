/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.UUID
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.animal;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

public abstract class Animal
extends AgeableMob {
    protected static final int PARENT_AGE_AFTER_BREEDING = 6000;
    private int inLove;
    @Nullable
    private UUID loveCause;

    protected Animal(EntityType<? extends Animal> $$0, Level $$1) {
        super((EntityType<? extends AgeableMob>)$$0, $$1);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 16.0f);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, -1.0f);
    }

    @Override
    protected void customServerAiStep() {
        if (this.getAge() != 0) {
            this.inLove = 0;
        }
        super.customServerAiStep();
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.getAge() != 0) {
            this.inLove = 0;
        }
        if (this.inLove > 0) {
            --this.inLove;
            if (this.inLove % 10 == 0) {
                double $$0 = this.random.nextGaussian() * 0.02;
                double $$1 = this.random.nextGaussian() * 0.02;
                double $$2 = this.random.nextGaussian() * 0.02;
                this.level.addParticle(ParticleTypes.HEART, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), $$0, $$1, $$2);
            }
        }
    }

    @Override
    public boolean hurt(DamageSource $$0, float $$1) {
        if (this.isInvulnerableTo($$0)) {
            return false;
        }
        this.inLove = 0;
        return super.hurt($$0, $$1);
    }

    @Override
    public float getWalkTargetValue(BlockPos $$0, LevelReader $$1) {
        if ($$1.getBlockState((BlockPos)$$0.below()).is(Blocks.GRASS_BLOCK)) {
            return 10.0f;
        }
        return $$1.getPathfindingCostFromLightLevels($$0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        super.addAdditionalSaveData($$0);
        $$0.putInt("InLove", this.inLove);
        if (this.loveCause != null) {
            $$0.putUUID("LoveCause", this.loveCause);
        }
    }

    @Override
    public double getMyRidingOffset() {
        return 0.14;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$0) {
        super.readAdditionalSaveData($$0);
        this.inLove = $$0.getInt("InLove");
        this.loveCause = $$0.hasUUID("LoveCause") ? $$0.getUUID("LoveCause") : null;
    }

    public static boolean checkAnimalSpawnRules(EntityType<? extends Animal> $$0, LevelAccessor $$1, MobSpawnType $$2, BlockPos $$3, RandomSource $$4) {
        return $$1.getBlockState((BlockPos)$$3.below()).is(BlockTags.ANIMALS_SPAWNABLE_ON) && Animal.isBrightEnoughToSpawn($$1, $$3);
    }

    protected static boolean isBrightEnoughToSpawn(BlockAndTintGetter $$0, BlockPos $$1) {
        return $$0.getRawBrightness($$1, 0) > 8;
    }

    @Override
    public int getAmbientSoundInterval() {
        return 120;
    }

    @Override
    public boolean removeWhenFarAway(double $$0) {
        return false;
    }

    @Override
    public int getExperienceReward() {
        return 1 + this.level.random.nextInt(3);
    }

    public boolean isFood(ItemStack $$0) {
        return $$0.is(Items.WHEAT);
    }

    @Override
    public InteractionResult mobInteract(Player $$0, InteractionHand $$1) {
        ItemStack $$2 = $$0.getItemInHand($$1);
        if (this.isFood($$2)) {
            int $$3 = this.getAge();
            if (!this.level.isClientSide && $$3 == 0 && this.canFallInLove()) {
                this.usePlayerItem($$0, $$1, $$2);
                this.setInLove($$0);
                return InteractionResult.SUCCESS;
            }
            if (this.isBaby()) {
                this.usePlayerItem($$0, $$1, $$2);
                this.ageUp(Animal.getSpeedUpSecondsWhenFeeding(-$$3), true);
                return InteractionResult.sidedSuccess(this.level.isClientSide);
            }
            if (this.level.isClientSide) {
                return InteractionResult.CONSUME;
            }
        }
        return super.mobInteract($$0, $$1);
    }

    protected void usePlayerItem(Player $$0, InteractionHand $$1, ItemStack $$2) {
        if (!$$0.getAbilities().instabuild) {
            $$2.shrink(1);
        }
    }

    public boolean canFallInLove() {
        return this.inLove <= 0;
    }

    public void setInLove(@Nullable Player $$0) {
        this.inLove = 600;
        if ($$0 != null) {
            this.loveCause = $$0.getUUID();
        }
        this.level.broadcastEntityEvent(this, (byte)18);
    }

    public void setInLoveTime(int $$0) {
        this.inLove = $$0;
    }

    public int getInLoveTime() {
        return this.inLove;
    }

    @Nullable
    public ServerPlayer getLoveCause() {
        if (this.loveCause == null) {
            return null;
        }
        Player $$0 = this.level.getPlayerByUUID(this.loveCause);
        if ($$0 instanceof ServerPlayer) {
            return (ServerPlayer)$$0;
        }
        return null;
    }

    public boolean isInLove() {
        return this.inLove > 0;
    }

    public void resetLove() {
        this.inLove = 0;
    }

    public boolean canMate(Animal $$0) {
        if ($$0 == this) {
            return false;
        }
        if ($$0.getClass() != this.getClass()) {
            return false;
        }
        return this.isInLove() && $$0.isInLove();
    }

    public void spawnChildFromBreeding(ServerLevel $$0, Animal $$1) {
        AgeableMob $$2 = this.getBreedOffspring($$0, $$1);
        if ($$2 == null) {
            return;
        }
        ServerPlayer $$3 = this.getLoveCause();
        if ($$3 == null && $$1.getLoveCause() != null) {
            $$3 = $$1.getLoveCause();
        }
        if ($$3 != null) {
            $$3.awardStat(Stats.ANIMALS_BRED);
            CriteriaTriggers.BRED_ANIMALS.trigger($$3, this, $$1, $$2);
        }
        this.setAge(6000);
        $$1.setAge(6000);
        this.resetLove();
        $$1.resetLove();
        $$2.setBaby(true);
        $$2.moveTo(this.getX(), this.getY(), this.getZ(), 0.0f, 0.0f);
        $$0.addFreshEntityWithPassengers($$2);
        $$0.broadcastEntityEvent(this, (byte)18);
        if ($$0.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            $$0.addFreshEntity(new ExperienceOrb($$0, this.getX(), this.getY(), this.getZ(), this.getRandom().nextInt(7) + 1));
        }
    }

    @Override
    public void handleEntityEvent(byte $$0) {
        if ($$0 == 18) {
            for (int $$1 = 0; $$1 < 7; ++$$1) {
                double $$2 = this.random.nextGaussian() * 0.02;
                double $$3 = this.random.nextGaussian() * 0.02;
                double $$4 = this.random.nextGaussian() * 0.02;
                this.level.addParticle(ParticleTypes.HEART, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), $$2, $$3, $$4);
            }
        } else {
            super.handleEntityEvent($$0);
        }
    }
}