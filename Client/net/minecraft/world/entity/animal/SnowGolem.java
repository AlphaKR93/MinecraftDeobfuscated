/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Byte
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.animal;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class SnowGolem
extends AbstractGolem
implements Shearable,
RangedAttackMob {
    private static final EntityDataAccessor<Byte> DATA_PUMPKIN_ID = SynchedEntityData.defineId(SnowGolem.class, EntityDataSerializers.BYTE);
    private static final byte PUMPKIN_FLAG = 16;
    private static final float EYE_HEIGHT = 1.7f;

    public SnowGolem(EntityType<? extends SnowGolem> $$0, Level $$1) {
        super((EntityType<? extends AbstractGolem>)$$0, $$1);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new RangedAttackGoal(this, 1.25, 20, 10.0f));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal((PathfinderMob)this, 1.0, 1.0000001E-5f));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 6.0f));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<Mob>(this, Mob.class, 10, true, false, (Predicate<LivingEntity>)((Predicate)$$0 -> $$0 instanceof Enemy)));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 4.0).add(Attributes.MOVEMENT_SPEED, 0.2f);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_PUMPKIN_ID, (byte)16);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        super.addAdditionalSaveData($$0);
        $$0.putBoolean("Pumpkin", this.hasPumpkin());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$0) {
        super.readAdditionalSaveData($$0);
        if ($$0.contains("Pumpkin")) {
            this.setPumpkin($$0.getBoolean("Pumpkin"));
        }
    }

    @Override
    public boolean isSensitiveToWater() {
        return true;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level.isClientSide) {
            int $$2;
            int $$1;
            int $$0 = Mth.floor(this.getX());
            BlockPos $$3 = new BlockPos($$0, $$1 = Mth.floor(this.getY()), $$2 = Mth.floor(this.getZ()));
            Biome $$4 = (Biome)this.level.getBiome($$3).value();
            if ($$4.shouldSnowGolemBurn($$3)) {
                this.hurt(DamageSource.ON_FIRE, 1.0f);
            }
            if (!this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                return;
            }
            BlockState $$5 = Blocks.SNOW.defaultBlockState();
            for (int $$6 = 0; $$6 < 4; ++$$6) {
                $$0 = Mth.floor(this.getX() + (double)((float)($$6 % 2 * 2 - 1) * 0.25f));
                BlockPos $$7 = new BlockPos($$0, $$1 = Mth.floor(this.getY()), $$2 = Mth.floor(this.getZ() + (double)((float)($$6 / 2 % 2 * 2 - 1) * 0.25f)));
                if (!this.level.getBlockState($$7).isAir() || !$$5.canSurvive(this.level, $$7)) continue;
                this.level.setBlockAndUpdate($$7, $$5);
                this.level.gameEvent(GameEvent.BLOCK_PLACE, $$7, GameEvent.Context.of(this, $$5));
            }
        }
    }

    @Override
    public void performRangedAttack(LivingEntity $$0, float $$1) {
        Snowball $$2 = new Snowball(this.level, this);
        double $$3 = $$0.getEyeY() - (double)1.1f;
        double $$4 = $$0.getX() - this.getX();
        double $$5 = $$3 - $$2.getY();
        double $$6 = $$0.getZ() - this.getZ();
        double $$7 = Math.sqrt((double)($$4 * $$4 + $$6 * $$6)) * (double)0.2f;
        $$2.shoot($$4, $$5 + $$7, $$6, 1.6f, 12.0f);
        this.playSound(SoundEvents.SNOW_GOLEM_SHOOT, 1.0f, 0.4f / (this.getRandom().nextFloat() * 0.4f + 0.8f));
        this.level.addFreshEntity($$2);
    }

    @Override
    protected float getStandingEyeHeight(Pose $$0, EntityDimensions $$1) {
        return 1.7f;
    }

    @Override
    protected InteractionResult mobInteract(Player $$0, InteractionHand $$12) {
        ItemStack $$2 = $$0.getItemInHand($$12);
        if ($$2.is(Items.SHEARS) && this.readyForShearing()) {
            this.shear(SoundSource.PLAYERS);
            this.gameEvent(GameEvent.SHEAR, $$0);
            if (!this.level.isClientSide) {
                $$2.hurtAndBreak(1, $$0, $$1 -> $$1.broadcastBreakEvent($$12));
            }
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    public void shear(SoundSource $$0) {
        this.level.playSound(null, this, SoundEvents.SNOW_GOLEM_SHEAR, $$0, 1.0f, 1.0f);
        if (!this.level.isClientSide()) {
            this.setPumpkin(false);
            this.spawnAtLocation(new ItemStack(Items.CARVED_PUMPKIN), 1.7f);
        }
    }

    @Override
    public boolean readyForShearing() {
        return this.isAlive() && this.hasPumpkin();
    }

    public boolean hasPumpkin() {
        return (this.entityData.get(DATA_PUMPKIN_ID) & 0x10) != 0;
    }

    public void setPumpkin(boolean $$0) {
        byte $$1 = this.entityData.get(DATA_PUMPKIN_ID);
        if ($$0) {
            this.entityData.set(DATA_PUMPKIN_ID, (byte)($$1 | 0x10));
        } else {
            this.entityData.set(DATA_PUMPKIN_ID, (byte)($$1 & 0xFFFFFFEF));
        }
    }

    @Override
    @Nullable
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SNOW_GOLEM_AMBIENT;
    }

    @Override
    @Nullable
    protected SoundEvent getHurtSound(DamageSource $$0) {
        return SoundEvents.SNOW_GOLEM_HURT;
    }

    @Override
    @Nullable
    protected SoundEvent getDeathSound() {
        return SoundEvents.SNOW_GOLEM_DEATH;
    }

    @Override
    public Vec3 getLeashOffset() {
        return new Vec3(0.0, 0.75f * this.getEyeHeight(), this.getBbWidth() * 0.4f);
    }
}