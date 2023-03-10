/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Boolean
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.List
 *  java.util.UUID
 *  java.util.function.Predicate
 */
package net.minecraft.world.entity.monster;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableWitchTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestHealableRaiderTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class Witch
extends Raider
implements RangedAttackMob {
    private static final UUID SPEED_MODIFIER_DRINKING_UUID = UUID.fromString((String)"5CD17E52-A79A-43D3-A529-90FDE04B181E");
    private static final AttributeModifier SPEED_MODIFIER_DRINKING = new AttributeModifier(SPEED_MODIFIER_DRINKING_UUID, "Drinking speed penalty", -0.25, AttributeModifier.Operation.ADDITION);
    private static final EntityDataAccessor<Boolean> DATA_USING_ITEM = SynchedEntityData.defineId(Witch.class, EntityDataSerializers.BOOLEAN);
    private int usingTime;
    private NearestHealableRaiderTargetGoal<Raider> healRaidersGoal;
    private NearestAttackableWitchTargetGoal<Player> attackPlayersGoal;

    public Witch(EntityType<? extends Witch> $$0, Level $$1) {
        super((EntityType<? extends Raider>)$$0, $$1);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.healRaidersGoal = new NearestHealableRaiderTargetGoal<Raider>(this, Raider.class, true, (Predicate<LivingEntity>)((Predicate)$$0 -> $$0 != null && this.hasActiveRaid() && $$0.getType() != EntityType.WITCH));
        this.attackPlayersGoal = new NearestAttackableWitchTargetGoal<Player>(this, Player.class, 10, true, false, null);
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.0, 60, 10.0f));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, Raider.class));
        this.targetSelector.addGoal(2, this.healRaidersGoal);
        this.targetSelector.addGoal(3, this.attackPlayersGoal);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(DATA_USING_ITEM, false);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.WITCH_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource $$0) {
        return SoundEvents.WITCH_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.WITCH_DEATH;
    }

    public void setUsingItem(boolean $$0) {
        this.getEntityData().set(DATA_USING_ITEM, $$0);
    }

    public boolean isDrinkingPotion() {
        return this.getEntityData().get(DATA_USING_ITEM);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 26.0).add(Attributes.MOVEMENT_SPEED, 0.25);
    }

    @Override
    public void aiStep() {
        if (!this.level.isClientSide && this.isAlive()) {
            this.healRaidersGoal.decrementCooldown();
            if (this.healRaidersGoal.getCooldown() <= 0) {
                this.attackPlayersGoal.setCanAttack(true);
            } else {
                this.attackPlayersGoal.setCanAttack(false);
            }
            if (this.isDrinkingPotion()) {
                if (this.usingTime-- <= 0) {
                    List<MobEffectInstance> $$1;
                    this.setUsingItem(false);
                    ItemStack $$0 = this.getMainHandItem();
                    this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                    if ($$0.is(Items.POTION) && ($$1 = PotionUtils.getMobEffects($$0)) != null) {
                        for (MobEffectInstance $$2 : $$1) {
                            this.addEffect(new MobEffectInstance($$2));
                        }
                    }
                    this.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(SPEED_MODIFIER_DRINKING);
                }
            } else {
                Potion $$3 = null;
                if (this.random.nextFloat() < 0.15f && this.isEyeInFluid(FluidTags.WATER) && !this.hasEffect(MobEffects.WATER_BREATHING)) {
                    $$3 = Potions.WATER_BREATHING;
                } else if (this.random.nextFloat() < 0.15f && (this.isOnFire() || this.getLastDamageSource() != null && this.getLastDamageSource().isFire()) && !this.hasEffect(MobEffects.FIRE_RESISTANCE)) {
                    $$3 = Potions.FIRE_RESISTANCE;
                } else if (this.random.nextFloat() < 0.05f && this.getHealth() < this.getMaxHealth()) {
                    $$3 = Potions.HEALING;
                } else if (this.random.nextFloat() < 0.5f && this.getTarget() != null && !this.hasEffect(MobEffects.MOVEMENT_SPEED) && this.getTarget().distanceToSqr(this) > 121.0) {
                    $$3 = Potions.SWIFTNESS;
                }
                if ($$3 != null) {
                    this.setItemSlot(EquipmentSlot.MAINHAND, PotionUtils.setPotion(new ItemStack(Items.POTION), $$3));
                    this.usingTime = this.getMainHandItem().getUseDuration();
                    this.setUsingItem(true);
                    if (!this.isSilent()) {
                        this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.WITCH_DRINK, this.getSoundSource(), 1.0f, 0.8f + this.random.nextFloat() * 0.4f);
                    }
                    AttributeInstance $$4 = this.getAttribute(Attributes.MOVEMENT_SPEED);
                    $$4.removeModifier(SPEED_MODIFIER_DRINKING);
                    $$4.addTransientModifier(SPEED_MODIFIER_DRINKING);
                }
            }
            if (this.random.nextFloat() < 7.5E-4f) {
                this.level.broadcastEntityEvent(this, (byte)15);
            }
        }
        super.aiStep();
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return SoundEvents.WITCH_CELEBRATE;
    }

    @Override
    public void handleEntityEvent(byte $$0) {
        if ($$0 == 15) {
            for (int $$1 = 0; $$1 < this.random.nextInt(35) + 10; ++$$1) {
                this.level.addParticle(ParticleTypes.WITCH, this.getX() + this.random.nextGaussian() * (double)0.13f, this.getBoundingBox().maxY + 0.5 + this.random.nextGaussian() * (double)0.13f, this.getZ() + this.random.nextGaussian() * (double)0.13f, 0.0, 0.0, 0.0);
            }
        } else {
            super.handleEntityEvent($$0);
        }
    }

    @Override
    protected float getDamageAfterMagicAbsorb(DamageSource $$0, float $$1) {
        $$1 = super.getDamageAfterMagicAbsorb($$0, $$1);
        if ($$0.getEntity() == this) {
            $$1 = 0.0f;
        }
        if ($$0.isMagic()) {
            $$1 *= 0.15f;
        }
        return $$1;
    }

    @Override
    public void performRangedAttack(LivingEntity $$0, float $$1) {
        if (this.isDrinkingPotion()) {
            return;
        }
        Vec3 $$2 = $$0.getDeltaMovement();
        double $$3 = $$0.getX() + $$2.x - this.getX();
        double $$4 = $$0.getEyeY() - (double)1.1f - this.getY();
        double $$5 = $$0.getZ() + $$2.z - this.getZ();
        double $$6 = Math.sqrt((double)($$3 * $$3 + $$5 * $$5));
        Potion $$7 = Potions.HARMING;
        if ($$0 instanceof Raider) {
            $$7 = $$0.getHealth() <= 4.0f ? Potions.HEALING : Potions.REGENERATION;
            this.setTarget(null);
        } else if ($$6 >= 8.0 && !$$0.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) {
            $$7 = Potions.SLOWNESS;
        } else if ($$0.getHealth() >= 8.0f && !$$0.hasEffect(MobEffects.POISON)) {
            $$7 = Potions.POISON;
        } else if ($$6 <= 3.0 && !$$0.hasEffect(MobEffects.WEAKNESS) && this.random.nextFloat() < 0.25f) {
            $$7 = Potions.WEAKNESS;
        }
        ThrownPotion $$8 = new ThrownPotion(this.level, this);
        $$8.setItem(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), $$7));
        $$8.setXRot($$8.getXRot() - -20.0f);
        $$8.shoot($$3, $$4 + $$6 * 0.2, $$5, 0.75f, 8.0f);
        if (!this.isSilent()) {
            this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.WITCH_THROW, this.getSoundSource(), 1.0f, 0.8f + this.random.nextFloat() * 0.4f);
        }
        this.level.addFreshEntity($$8);
    }

    @Override
    protected float getStandingEyeHeight(Pose $$0, EntityDimensions $$1) {
        return 1.62f;
    }

    @Override
    public void applyRaidBuffs(int $$0, boolean $$1) {
    }

    @Override
    public boolean canBeLeader() {
        return false;
    }
}