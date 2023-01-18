/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  java.lang.Class
 *  java.lang.Enum
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.EnumSet
 *  java.util.HashMap
 *  java.util.Map
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.monster;

import com.google.common.collect.Maps;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreakDoorGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public class Vindicator
extends AbstractIllager {
    private static final String TAG_JOHNNY = "Johnny";
    static final Predicate<Difficulty> DOOR_BREAKING_PREDICATE = $$0 -> $$0 == Difficulty.NORMAL || $$0 == Difficulty.HARD;
    boolean isJohnny;

    public Vindicator(EntityType<? extends Vindicator> $$0, Level $$1) {
        super((EntityType<? extends AbstractIllager>)$$0, $$1);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new VindicatorBreakDoorGoal(this));
        this.goalSelector.addGoal(2, new AbstractIllager.RaiderOpenDoorGoal(this, this));
        this.goalSelector.addGoal(3, new Raider.HoldGroundAttackGoal(this, this, 10.0f));
        this.goalSelector.addGoal(4, new VindicatorMeleeAttackGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, Raider.class).setAlertOthers(new Class[0]));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<Player>((Mob)this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<AbstractVillager>((Mob)this, AbstractVillager.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<IronGolem>((Mob)this, IronGolem.class, true));
        this.targetSelector.addGoal(4, new VindicatorJohnnyAttackGoal(this));
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0f, 1.0f));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0f));
    }

    @Override
    protected void customServerAiStep() {
        if (!this.isNoAi() && GoalUtils.hasGroundPathNavigation(this)) {
            boolean $$0 = ((ServerLevel)this.level).isRaided(this.blockPosition());
            ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors($$0);
        }
        super.customServerAiStep();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.35f).add(Attributes.FOLLOW_RANGE, 12.0).add(Attributes.MAX_HEALTH, 24.0).add(Attributes.ATTACK_DAMAGE, 5.0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        super.addAdditionalSaveData($$0);
        if (this.isJohnny) {
            $$0.putBoolean(TAG_JOHNNY, true);
        }
    }

    @Override
    public AbstractIllager.IllagerArmPose getArmPose() {
        if (this.isAggressive()) {
            return AbstractIllager.IllagerArmPose.ATTACKING;
        }
        if (this.isCelebrating()) {
            return AbstractIllager.IllagerArmPose.CELEBRATING;
        }
        return AbstractIllager.IllagerArmPose.CROSSED;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$0) {
        super.readAdditionalSaveData($$0);
        if ($$0.contains(TAG_JOHNNY, 99)) {
            this.isJohnny = $$0.getBoolean(TAG_JOHNNY);
        }
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return SoundEvents.VINDICATOR_CELEBRATE;
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor $$0, DifficultyInstance $$1, MobSpawnType $$2, @Nullable SpawnGroupData $$3, @Nullable CompoundTag $$4) {
        SpawnGroupData $$5 = super.finalizeSpawn($$0, $$1, $$2, $$3, $$4);
        ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);
        RandomSource $$6 = $$0.getRandom();
        this.populateDefaultEquipmentSlots($$6, $$1);
        this.populateDefaultEquipmentEnchantments($$6, $$1);
        return $$5;
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource $$0, DifficultyInstance $$1) {
        if (this.getCurrentRaid() == null) {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_AXE));
        }
    }

    @Override
    public boolean isAlliedTo(Entity $$0) {
        if (super.isAlliedTo($$0)) {
            return true;
        }
        if ($$0 instanceof LivingEntity && ((LivingEntity)$$0).getMobType() == MobType.ILLAGER) {
            return this.getTeam() == null && $$0.getTeam() == null;
        }
        return false;
    }

    @Override
    public void setCustomName(@Nullable Component $$0) {
        super.setCustomName($$0);
        if (!this.isJohnny && $$0 != null && $$0.getString().equals((Object)TAG_JOHNNY)) {
            this.isJohnny = true;
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.VINDICATOR_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.VINDICATOR_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource $$0) {
        return SoundEvents.VINDICATOR_HURT;
    }

    @Override
    public void applyRaidBuffs(int $$0, boolean $$1) {
        boolean $$5;
        ItemStack $$2 = new ItemStack(Items.IRON_AXE);
        Raid $$3 = this.getCurrentRaid();
        int $$4 = 1;
        if ($$0 > $$3.getNumGroups(Difficulty.NORMAL)) {
            $$4 = 2;
        }
        boolean bl = $$5 = this.random.nextFloat() <= $$3.getEnchantOdds();
        if ($$5) {
            HashMap $$6 = Maps.newHashMap();
            $$6.put((Object)Enchantments.SHARPNESS, (Object)$$4);
            EnchantmentHelper.setEnchantments((Map<Enchantment, Integer>)$$6, $$2);
        }
        this.setItemSlot(EquipmentSlot.MAINHAND, $$2);
    }

    static class VindicatorBreakDoorGoal
    extends BreakDoorGoal {
        public VindicatorBreakDoorGoal(Mob $$0) {
            super($$0, 6, DOOR_BREAKING_PREDICATE);
            this.setFlags((EnumSet<Goal.Flag>)EnumSet.of((Enum)Goal.Flag.MOVE));
        }

        @Override
        public boolean canContinueToUse() {
            Vindicator $$0 = (Vindicator)this.mob;
            return $$0.hasActiveRaid() && super.canContinueToUse();
        }

        @Override
        public boolean canUse() {
            Vindicator $$0 = (Vindicator)this.mob;
            return $$0.hasActiveRaid() && $$0.random.nextInt(VindicatorBreakDoorGoal.reducedTickDelay(10)) == 0 && super.canUse();
        }

        @Override
        public void start() {
            super.start();
            this.mob.setNoActionTime(0);
        }
    }

    class VindicatorMeleeAttackGoal
    extends MeleeAttackGoal {
        public VindicatorMeleeAttackGoal(Vindicator $$0) {
            super($$0, 1.0, false);
        }

        @Override
        protected double getAttackReachSqr(LivingEntity $$0) {
            if (this.mob.getVehicle() instanceof Ravager) {
                float $$1 = this.mob.getVehicle().getBbWidth() - 0.1f;
                return $$1 * 2.0f * ($$1 * 2.0f) + $$0.getBbWidth();
            }
            return super.getAttackReachSqr($$0);
        }
    }

    static class VindicatorJohnnyAttackGoal
    extends NearestAttackableTargetGoal<LivingEntity> {
        public VindicatorJohnnyAttackGoal(Vindicator $$0) {
            super($$0, LivingEntity.class, 0, true, true, (Predicate<LivingEntity>)((Predicate)LivingEntity::attackable));
        }

        @Override
        public boolean canUse() {
            return ((Vindicator)this.mob).isJohnny && super.canUse();
        }

        @Override
        public void start() {
            super.start();
            this.mob.setNoActionTime(0);
        }
    }
}