/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Boolean
 *  java.lang.Class
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Collection
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.monster;

import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PowerableMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SwellGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class Creeper
extends Monster
implements PowerableMob {
    private static final EntityDataAccessor<Integer> DATA_SWELL_DIR = SynchedEntityData.defineId(Creeper.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_IS_POWERED = SynchedEntityData.defineId(Creeper.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_IS_IGNITED = SynchedEntityData.defineId(Creeper.class, EntityDataSerializers.BOOLEAN);
    private int oldSwell;
    private int swell;
    private int maxSwell = 30;
    private int explosionRadius = 3;
    private int droppedSkulls;

    public Creeper(EntityType<? extends Creeper> $$0, Level $$1) {
        super((EntityType<? extends Monster>)$$0, $$1);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SwellGoal(this));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<Ocelot>(this, Ocelot.class, 6.0f, 1.0, 1.2));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<Cat>(this, Cat.class, 6.0f, 1.0, 1.2));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<Player>((Mob)this, Player.class, true));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this, new Class[0]));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.25);
    }

    @Override
    public int getMaxFallDistance() {
        if (this.getTarget() == null) {
            return 3;
        }
        return 3 + (int)(this.getHealth() - 1.0f);
    }

    @Override
    public boolean causeFallDamage(float $$0, float $$1, DamageSource $$2) {
        boolean $$3 = super.causeFallDamage($$0, $$1, $$2);
        this.swell += (int)($$0 * 1.5f);
        if (this.swell > this.maxSwell - 5) {
            this.swell = this.maxSwell - 5;
        }
        return $$3;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_SWELL_DIR, -1);
        this.entityData.define(DATA_IS_POWERED, false);
        this.entityData.define(DATA_IS_IGNITED, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        super.addAdditionalSaveData($$0);
        if (this.entityData.get(DATA_IS_POWERED).booleanValue()) {
            $$0.putBoolean("powered", true);
        }
        $$0.putShort("Fuse", (short)this.maxSwell);
        $$0.putByte("ExplosionRadius", (byte)this.explosionRadius);
        $$0.putBoolean("ignited", this.isIgnited());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$0) {
        super.readAdditionalSaveData($$0);
        this.entityData.set(DATA_IS_POWERED, $$0.getBoolean("powered"));
        if ($$0.contains("Fuse", 99)) {
            this.maxSwell = $$0.getShort("Fuse");
        }
        if ($$0.contains("ExplosionRadius", 99)) {
            this.explosionRadius = $$0.getByte("ExplosionRadius");
        }
        if ($$0.getBoolean("ignited")) {
            this.ignite();
        }
    }

    @Override
    public void tick() {
        if (this.isAlive()) {
            int $$0;
            this.oldSwell = this.swell;
            if (this.isIgnited()) {
                this.setSwellDir(1);
            }
            if (($$0 = this.getSwellDir()) > 0 && this.swell == 0) {
                this.playSound(SoundEvents.CREEPER_PRIMED, 1.0f, 0.5f);
                this.gameEvent(GameEvent.PRIME_FUSE);
            }
            this.swell += $$0;
            if (this.swell < 0) {
                this.swell = 0;
            }
            if (this.swell >= this.maxSwell) {
                this.swell = this.maxSwell;
                this.explodeCreeper();
            }
        }
        super.tick();
    }

    @Override
    public void setTarget(@Nullable LivingEntity $$0) {
        if ($$0 instanceof Goat) {
            return;
        }
        super.setTarget($$0);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource $$0) {
        return SoundEvents.CREEPER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.CREEPER_DEATH;
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource $$0, int $$1, boolean $$2) {
        Creeper $$4;
        super.dropCustomDeathLoot($$0, $$1, $$2);
        Entity $$3 = $$0.getEntity();
        if ($$3 != this && $$3 instanceof Creeper && ($$4 = (Creeper)$$3).canDropMobsSkull()) {
            $$4.increaseDroppedSkulls();
            this.spawnAtLocation(Items.CREEPER_HEAD);
        }
    }

    @Override
    public boolean doHurtTarget(Entity $$0) {
        return true;
    }

    @Override
    public boolean isPowered() {
        return this.entityData.get(DATA_IS_POWERED);
    }

    public float getSwelling(float $$0) {
        return Mth.lerp($$0, this.oldSwell, this.swell) / (float)(this.maxSwell - 2);
    }

    public int getSwellDir() {
        return this.entityData.get(DATA_SWELL_DIR);
    }

    public void setSwellDir(int $$0) {
        this.entityData.set(DATA_SWELL_DIR, $$0);
    }

    @Override
    public void thunderHit(ServerLevel $$0, LightningBolt $$1) {
        super.thunderHit($$0, $$1);
        this.entityData.set(DATA_IS_POWERED, true);
    }

    @Override
    protected InteractionResult mobInteract(Player $$0, InteractionHand $$12) {
        ItemStack $$2 = $$0.getItemInHand($$12);
        if ($$2.is(ItemTags.CREEPER_IGNITERS)) {
            SoundEvent $$3 = $$2.is(Items.FIRE_CHARGE) ? SoundEvents.FIRECHARGE_USE : SoundEvents.FLINTANDSTEEL_USE;
            this.level.playSound($$0, this.getX(), this.getY(), this.getZ(), $$3, this.getSoundSource(), 1.0f, this.random.nextFloat() * 0.4f + 0.8f);
            if (!this.level.isClientSide) {
                this.ignite();
                if (!$$2.isDamageableItem()) {
                    $$2.shrink(1);
                } else {
                    $$2.hurtAndBreak(1, $$0, $$1 -> $$1.broadcastBreakEvent($$12));
                }
            }
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        }
        return super.mobInteract($$0, $$12);
    }

    private void explodeCreeper() {
        if (!this.level.isClientSide) {
            float $$0 = this.isPowered() ? 2.0f : 1.0f;
            this.dead = true;
            this.level.explode(this, this.getX(), this.getY(), this.getZ(), (float)this.explosionRadius * $$0, Level.ExplosionInteraction.MOB);
            this.discard();
            this.spawnLingeringCloud();
        }
    }

    private void spawnLingeringCloud() {
        Collection<MobEffectInstance> $$0 = this.getActiveEffects();
        if (!$$0.isEmpty()) {
            AreaEffectCloud $$1 = new AreaEffectCloud(this.level, this.getX(), this.getY(), this.getZ());
            $$1.setRadius(2.5f);
            $$1.setRadiusOnUse(-0.5f);
            $$1.setWaitTime(10);
            $$1.setDuration($$1.getDuration() / 2);
            $$1.setRadiusPerTick(-$$1.getRadius() / (float)$$1.getDuration());
            for (MobEffectInstance $$2 : $$0) {
                $$1.addEffect(new MobEffectInstance($$2));
            }
            this.level.addFreshEntity($$1);
        }
    }

    public boolean isIgnited() {
        return this.entityData.get(DATA_IS_IGNITED);
    }

    public void ignite() {
        this.entityData.set(DATA_IS_IGNITED, true);
    }

    public boolean canDropMobsSkull() {
        return this.isPowered() && this.droppedSkulls < 1;
    }

    public void increaseDroppedSkulls() {
        ++this.droppedSkulls;
    }
}