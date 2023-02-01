/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.logging.LogUtils
 *  java.lang.Boolean
 *  java.lang.Float
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.Map
 *  java.util.UUID
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import org.slf4j.Logger;

public class AreaEffectCloud
extends Entity
implements TraceableEntity {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int TIME_BETWEEN_APPLICATIONS = 5;
    private static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.defineId(AreaEffectCloud.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_COLOR = SynchedEntityData.defineId(AreaEffectCloud.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_WAITING = SynchedEntityData.defineId(AreaEffectCloud.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<ParticleOptions> DATA_PARTICLE = SynchedEntityData.defineId(AreaEffectCloud.class, EntityDataSerializers.PARTICLE);
    private static final float MAX_RADIUS = 32.0f;
    private static final float MINIMAL_RADIUS = 0.5f;
    private static final float DEFAULT_RADIUS = 3.0f;
    public static final float DEFAULT_WIDTH = 6.0f;
    public static final float HEIGHT = 0.5f;
    private Potion potion = Potions.EMPTY;
    private final List<MobEffectInstance> effects = Lists.newArrayList();
    private final Map<Entity, Integer> victims = Maps.newHashMap();
    private int duration = 600;
    private int waitTime = 20;
    private int reapplicationDelay = 20;
    private boolean fixedColor;
    private int durationOnUse;
    private float radiusOnUse;
    private float radiusPerTick;
    @Nullable
    private LivingEntity owner;
    @Nullable
    private UUID ownerUUID;

    public AreaEffectCloud(EntityType<? extends AreaEffectCloud> $$0, Level $$1) {
        super($$0, $$1);
        this.noPhysics = true;
    }

    public AreaEffectCloud(Level $$0, double $$1, double $$2, double $$3) {
        this((EntityType<? extends AreaEffectCloud>)EntityType.AREA_EFFECT_CLOUD, $$0);
        this.setPos($$1, $$2, $$3);
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(DATA_COLOR, 0);
        this.getEntityData().define(DATA_RADIUS, Float.valueOf((float)3.0f));
        this.getEntityData().define(DATA_WAITING, false);
        this.getEntityData().define(DATA_PARTICLE, ParticleTypes.ENTITY_EFFECT);
    }

    public void setRadius(float $$0) {
        if (!this.level.isClientSide) {
            this.getEntityData().set(DATA_RADIUS, Float.valueOf((float)Mth.clamp($$0, 0.0f, 32.0f)));
        }
    }

    @Override
    public void refreshDimensions() {
        double $$0 = this.getX();
        double $$1 = this.getY();
        double $$2 = this.getZ();
        super.refreshDimensions();
        this.setPos($$0, $$1, $$2);
    }

    public float getRadius() {
        return this.getEntityData().get(DATA_RADIUS).floatValue();
    }

    public void setPotion(Potion $$0) {
        this.potion = $$0;
        if (!this.fixedColor) {
            this.updateColor();
        }
    }

    private void updateColor() {
        if (this.potion == Potions.EMPTY && this.effects.isEmpty()) {
            this.getEntityData().set(DATA_COLOR, 0);
        } else {
            this.getEntityData().set(DATA_COLOR, PotionUtils.getColor(PotionUtils.getAllEffects(this.potion, this.effects)));
        }
    }

    public void addEffect(MobEffectInstance $$0) {
        this.effects.add((Object)$$0);
        if (!this.fixedColor) {
            this.updateColor();
        }
    }

    public int getColor() {
        return this.getEntityData().get(DATA_COLOR);
    }

    public void setFixedColor(int $$0) {
        this.fixedColor = true;
        this.getEntityData().set(DATA_COLOR, $$0);
    }

    public ParticleOptions getParticle() {
        return this.getEntityData().get(DATA_PARTICLE);
    }

    public void setParticle(ParticleOptions $$0) {
        this.getEntityData().set(DATA_PARTICLE, $$0);
    }

    protected void setWaiting(boolean $$0) {
        this.getEntityData().set(DATA_WAITING, $$0);
    }

    public boolean isWaiting() {
        return this.getEntityData().get(DATA_WAITING);
    }

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int $$0) {
        this.duration = $$0;
    }

    @Override
    public void tick() {
        block20: {
            ArrayList $$24;
            float $$1;
            block21: {
                boolean $$23;
                boolean $$02;
                block19: {
                    float $$6;
                    int $$5;
                    super.tick();
                    $$02 = this.isWaiting();
                    $$1 = this.getRadius();
                    if (!this.level.isClientSide) break block19;
                    if ($$02 && this.random.nextBoolean()) {
                        return;
                    }
                    ParticleOptions $$2 = this.getParticle();
                    if ($$02) {
                        int $$3 = 2;
                        float $$4 = 0.2f;
                    } else {
                        $$5 = Mth.ceil((float)Math.PI * $$1 * $$1);
                        $$6 = $$1;
                    }
                    for (int $$7 = 0; $$7 < $$5; ++$$7) {
                        double $$22;
                        double $$21;
                        double $$20;
                        float $$8 = this.random.nextFloat() * ((float)Math.PI * 2);
                        float $$9 = Mth.sqrt(this.random.nextFloat()) * $$6;
                        double $$10 = this.getX() + (double)(Mth.cos($$8) * $$9);
                        double $$11 = this.getY();
                        double $$12 = this.getZ() + (double)(Mth.sin($$8) * $$9);
                        if ($$2.getType() == ParticleTypes.ENTITY_EFFECT) {
                            int $$13 = $$02 && this.random.nextBoolean() ? 0xFFFFFF : this.getColor();
                            double $$14 = (float)($$13 >> 16 & 0xFF) / 255.0f;
                            double $$15 = (float)($$13 >> 8 & 0xFF) / 255.0f;
                            double $$16 = (float)($$13 & 0xFF) / 255.0f;
                        } else if ($$02) {
                            double $$17 = 0.0;
                            double $$18 = 0.0;
                            double $$19 = 0.0;
                        } else {
                            $$20 = (0.5 - this.random.nextDouble()) * 0.15;
                            $$21 = 0.01f;
                            $$22 = (0.5 - this.random.nextDouble()) * 0.15;
                        }
                        this.level.addAlwaysVisibleParticle($$2, $$10, $$11, $$12, $$20, $$21, $$22);
                    }
                    break block20;
                }
                if (this.tickCount >= this.waitTime + this.duration) {
                    this.discard();
                    return;
                }
                boolean bl = $$23 = this.tickCount < this.waitTime;
                if ($$02 != $$23) {
                    this.setWaiting($$23);
                }
                if ($$23) {
                    return;
                }
                if (this.radiusPerTick != 0.0f) {
                    if (($$1 += this.radiusPerTick) < 0.5f) {
                        this.discard();
                        return;
                    }
                    this.setRadius($$1);
                }
                if (this.tickCount % 5 != 0) break block20;
                this.victims.entrySet().removeIf($$0 -> this.tickCount >= (Integer)$$0.getValue());
                $$24 = Lists.newArrayList();
                for (MobEffectInstance $$25 : this.potion.getEffects()) {
                    $$24.add((Object)new MobEffectInstance($$25.getEffect(), $$25.mapDuration($$0 -> $$0 / 4), $$25.getAmplifier(), $$25.isAmbient(), $$25.isVisible()));
                }
                $$24.addAll(this.effects);
                if (!$$24.isEmpty()) break block21;
                this.victims.clear();
                break block20;
            }
            List $$26 = this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox());
            if ($$26.isEmpty()) break block20;
            for (LivingEntity $$27 : $$26) {
                double $$29;
                double $$28;
                double $$30;
                if (this.victims.containsKey((Object)$$27) || !$$27.isAffectedByPotions() || !(($$30 = ($$28 = $$27.getX() - this.getX()) * $$28 + ($$29 = $$27.getZ() - this.getZ()) * $$29) <= (double)($$1 * $$1))) continue;
                this.victims.put((Object)$$27, (Object)(this.tickCount + this.reapplicationDelay));
                for (MobEffectInstance $$31 : $$24) {
                    if ($$31.getEffect().isInstantenous()) {
                        $$31.getEffect().applyInstantenousEffect(this, this.getOwner(), $$27, $$31.getAmplifier(), 0.5);
                        continue;
                    }
                    $$27.addEffect(new MobEffectInstance($$31), this);
                }
                if (this.radiusOnUse != 0.0f) {
                    if (($$1 += this.radiusOnUse) < 0.5f) {
                        this.discard();
                        return;
                    }
                    this.setRadius($$1);
                }
                if (this.durationOnUse == 0) continue;
                this.duration += this.durationOnUse;
                if (this.duration > 0) continue;
                this.discard();
                return;
            }
        }
    }

    public float getRadiusOnUse() {
        return this.radiusOnUse;
    }

    public void setRadiusOnUse(float $$0) {
        this.radiusOnUse = $$0;
    }

    public float getRadiusPerTick() {
        return this.radiusPerTick;
    }

    public void setRadiusPerTick(float $$0) {
        this.radiusPerTick = $$0;
    }

    public int getDurationOnUse() {
        return this.durationOnUse;
    }

    public void setDurationOnUse(int $$0) {
        this.durationOnUse = $$0;
    }

    public int getWaitTime() {
        return this.waitTime;
    }

    public void setWaitTime(int $$0) {
        this.waitTime = $$0;
    }

    public void setOwner(@Nullable LivingEntity $$0) {
        this.owner = $$0;
        this.ownerUUID = $$0 == null ? null : $$0.getUUID();
    }

    @Override
    @Nullable
    public LivingEntity getOwner() {
        Entity $$0;
        if (this.owner == null && this.ownerUUID != null && this.level instanceof ServerLevel && ($$0 = ((ServerLevel)this.level).getEntity(this.ownerUUID)) instanceof LivingEntity) {
            this.owner = (LivingEntity)$$0;
        }
        return this.owner;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag $$0) {
        this.tickCount = $$0.getInt("Age");
        this.duration = $$0.getInt("Duration");
        this.waitTime = $$0.getInt("WaitTime");
        this.reapplicationDelay = $$0.getInt("ReapplicationDelay");
        this.durationOnUse = $$0.getInt("DurationOnUse");
        this.radiusOnUse = $$0.getFloat("RadiusOnUse");
        this.radiusPerTick = $$0.getFloat("RadiusPerTick");
        this.setRadius($$0.getFloat("Radius"));
        if ($$0.hasUUID("Owner")) {
            this.ownerUUID = $$0.getUUID("Owner");
        }
        if ($$0.contains("Particle", 8)) {
            try {
                this.setParticle(ParticleArgument.readParticle(new StringReader($$0.getString("Particle")), BuiltInRegistries.PARTICLE_TYPE.asLookup()));
            }
            catch (CommandSyntaxException $$1) {
                LOGGER.warn("Couldn't load custom particle {}", (Object)$$0.getString("Particle"), (Object)$$1);
            }
        }
        if ($$0.contains("Color", 99)) {
            this.setFixedColor($$0.getInt("Color"));
        }
        if ($$0.contains("Potion", 8)) {
            this.setPotion(PotionUtils.getPotion($$0));
        }
        if ($$0.contains("Effects", 9)) {
            ListTag $$2 = $$0.getList("Effects", 10);
            this.effects.clear();
            for (int $$3 = 0; $$3 < $$2.size(); ++$$3) {
                MobEffectInstance $$4 = MobEffectInstance.load($$2.getCompound($$3));
                if ($$4 == null) continue;
                this.addEffect($$4);
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag $$0) {
        $$0.putInt("Age", this.tickCount);
        $$0.putInt("Duration", this.duration);
        $$0.putInt("WaitTime", this.waitTime);
        $$0.putInt("ReapplicationDelay", this.reapplicationDelay);
        $$0.putInt("DurationOnUse", this.durationOnUse);
        $$0.putFloat("RadiusOnUse", this.radiusOnUse);
        $$0.putFloat("RadiusPerTick", this.radiusPerTick);
        $$0.putFloat("Radius", this.getRadius());
        $$0.putString("Particle", this.getParticle().writeToString());
        if (this.ownerUUID != null) {
            $$0.putUUID("Owner", this.ownerUUID);
        }
        if (this.fixedColor) {
            $$0.putInt("Color", this.getColor());
        }
        if (this.potion != Potions.EMPTY) {
            $$0.putString("Potion", BuiltInRegistries.POTION.getKey(this.potion).toString());
        }
        if (!this.effects.isEmpty()) {
            ListTag $$1 = new ListTag();
            for (MobEffectInstance $$2 : this.effects) {
                $$1.add($$2.save(new CompoundTag()));
            }
            $$0.put("Effects", $$1);
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> $$0) {
        if (DATA_RADIUS.equals($$0)) {
            this.refreshDimensions();
        }
        super.onSyncedDataUpdated($$0);
    }

    public Potion getPotion() {
        return this.potion;
    }

    @Override
    public PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    @Override
    public EntityDimensions getDimensions(Pose $$0) {
        return EntityDimensions.scalable(this.getRadius() * 2.0f, 0.5f);
    }
}