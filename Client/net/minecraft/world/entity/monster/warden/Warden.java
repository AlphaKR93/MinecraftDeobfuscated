/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Collections
 *  java.util.List
 *  java.util.Optional
 *  java.util.UUID
 *  java.util.function.BiConsumer
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 *  org.jetbrains.annotations.Contract
 *  org.slf4j.Logger
 */
package net.minecraft.world.entity.monster.warden;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.GameEventTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.warden.SonicBoom;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.warden.AngerLevel;
import net.minecraft.world.entity.monster.warden.AngerManagement;
import net.minecraft.world.entity.monster.warden.WardenAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import net.minecraft.world.level.gameevent.EntityPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.vibrations.VibrationListener;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;

public class Warden
extends Monster
implements VibrationListener.VibrationListenerConfig {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int GAME_EVENT_LISTENER_RANGE = 16;
    private static final int VIBRATION_COOLDOWN_TICKS = 40;
    private static final int TIME_TO_USE_MELEE_UNTIL_SONIC_BOOM = 200;
    private static final int MAX_HEALTH = 500;
    private static final float MOVEMENT_SPEED_WHEN_FIGHTING = 0.3f;
    private static final float KNOCKBACK_RESISTANCE = 1.0f;
    private static final float ATTACK_KNOCKBACK = 1.5f;
    private static final int ATTACK_DAMAGE = 30;
    private static final EntityDataAccessor<Integer> CLIENT_ANGER_LEVEL = SynchedEntityData.defineId(Warden.class, EntityDataSerializers.INT);
    private static final int DARKNESS_DISPLAY_LIMIT = 200;
    private static final int DARKNESS_DURATION = 260;
    private static final int DARKNESS_RADIUS = 20;
    private static final int DARKNESS_INTERVAL = 120;
    private static final int ANGERMANAGEMENT_TICK_DELAY = 20;
    private static final int DEFAULT_ANGER = 35;
    private static final int PROJECTILE_ANGER = 10;
    private static final int ON_HURT_ANGER_BOOST = 20;
    private static final int RECENT_PROJECTILE_TICK_THRESHOLD = 100;
    private static final int TOUCH_COOLDOWN_TICKS = 20;
    private static final int DIGGING_PARTICLES_AMOUNT = 30;
    private static final float DIGGING_PARTICLES_DURATION = 4.5f;
    private static final float DIGGING_PARTICLES_OFFSET = 0.7f;
    private static final int PROJECTILE_ANGER_DISTANCE = 30;
    private int tendrilAnimation;
    private int tendrilAnimationO;
    private int heartAnimation;
    private int heartAnimationO;
    public AnimationState roarAnimationState = new AnimationState();
    public AnimationState sniffAnimationState = new AnimationState();
    public AnimationState emergeAnimationState = new AnimationState();
    public AnimationState diggingAnimationState = new AnimationState();
    public AnimationState attackAnimationState = new AnimationState();
    public AnimationState sonicBoomAnimationState = new AnimationState();
    private final DynamicGameEventListener<VibrationListener> dynamicGameEventListener;
    private AngerManagement angerManagement = new AngerManagement((Predicate<Entity>)((Predicate)this::canTargetEntity), (List<Pair<UUID, Integer>>)Collections.emptyList());

    public Warden(EntityType<? extends Monster> $$0, Level $$1) {
        super($$0, $$1);
        this.dynamicGameEventListener = new DynamicGameEventListener<VibrationListener>(new VibrationListener(new EntityPositionSource(this, this.getEyeHeight()), 16, this));
        this.xpReward = 5;
        this.getNavigation().setCanFloat(true);
        this.setPathfindingMalus(BlockPathTypes.UNPASSABLE_RAIL, 0.0f);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_OTHER, 8.0f);
        this.setPathfindingMalus(BlockPathTypes.POWDER_SNOW, 8.0f);
        this.setPathfindingMalus(BlockPathTypes.LAVA, 8.0f);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 0.0f);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0.0f);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this, this.hasPose(Pose.EMERGING) ? 1 : 0);
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket $$0) {
        super.recreateFromPacket($$0);
        if ($$0.getData() == 1) {
            this.setPose(Pose.EMERGING);
        }
    }

    @Override
    public boolean checkSpawnObstruction(LevelReader $$0) {
        return super.checkSpawnObstruction($$0) && $$0.noCollision(this, this.getType().getDimensions().makeBoundingBox(this.position()));
    }

    @Override
    public float getWalkTargetValue(BlockPos $$0, LevelReader $$1) {
        return 0.0f;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource $$0) {
        if (this.isDiggingOrEmerging() && !$$0.isBypassInvul()) {
            return true;
        }
        return super.isInvulnerableTo($$0);
    }

    private boolean isDiggingOrEmerging() {
        return this.hasPose(Pose.DIGGING) || this.hasPose(Pose.EMERGING);
    }

    @Override
    protected boolean canRide(Entity $$0) {
        return false;
    }

    @Override
    public boolean canDisableShield() {
        return true;
    }

    @Override
    protected float nextStep() {
        return this.moveDist + 0.55f;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 500.0).add(Attributes.MOVEMENT_SPEED, 0.3f).add(Attributes.KNOCKBACK_RESISTANCE, 1.0).add(Attributes.ATTACK_KNOCKBACK, 1.5).add(Attributes.ATTACK_DAMAGE, 30.0);
    }

    @Override
    public boolean dampensVibrations() {
        return true;
    }

    @Override
    protected float getSoundVolume() {
        return 4.0f;
    }

    @Override
    @Nullable
    protected SoundEvent getAmbientSound() {
        if (this.hasPose(Pose.ROARING) || this.isDiggingOrEmerging()) {
            return null;
        }
        return this.getAngerLevel().getAmbientSound();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource $$0) {
        return SoundEvents.WARDEN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.WARDEN_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos $$0, BlockState $$1) {
        this.playSound(SoundEvents.WARDEN_STEP, 10.0f, 1.0f);
    }

    @Override
    public boolean doHurtTarget(Entity $$0) {
        this.level.broadcastEntityEvent(this, (byte)4);
        this.playSound(SoundEvents.WARDEN_ATTACK_IMPACT, 10.0f, this.getVoicePitch());
        SonicBoom.setCooldown(this, 40);
        return super.doHurtTarget($$0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CLIENT_ANGER_LEVEL, 0);
    }

    public int getClientAngerLevel() {
        return this.entityData.get(CLIENT_ANGER_LEVEL);
    }

    private void syncClientAngerLevel() {
        this.entityData.set(CLIENT_ANGER_LEVEL, this.getActiveAnger());
    }

    @Override
    public void tick() {
        Level level = this.level;
        if (level instanceof ServerLevel) {
            ServerLevel $$0 = (ServerLevel)level;
            this.dynamicGameEventListener.getListener().tick($$0);
            if (this.isPersistenceRequired() || this.requiresCustomPersistence()) {
                WardenAi.setDigCooldown(this);
            }
        }
        super.tick();
        if (this.level.isClientSide()) {
            if (this.tickCount % this.getHeartBeatDelay() == 0) {
                this.heartAnimation = 10;
                if (!this.isSilent()) {
                    this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.WARDEN_HEARTBEAT, this.getSoundSource(), 5.0f, this.getVoicePitch(), false);
                }
            }
            this.tendrilAnimationO = this.tendrilAnimation;
            if (this.tendrilAnimation > 0) {
                --this.tendrilAnimation;
            }
            this.heartAnimationO = this.heartAnimation;
            if (this.heartAnimation > 0) {
                --this.heartAnimation;
            }
            switch (this.getPose()) {
                case EMERGING: {
                    this.clientDiggingParticles(this.emergeAnimationState);
                    break;
                }
                case DIGGING: {
                    this.clientDiggingParticles(this.diggingAnimationState);
                }
            }
        }
    }

    @Override
    protected void customServerAiStep() {
        ServerLevel $$0 = (ServerLevel)this.level;
        $$0.getProfiler().push("wardenBrain");
        this.getBrain().tick($$0, this);
        this.level.getProfiler().pop();
        super.customServerAiStep();
        if ((this.tickCount + this.getId()) % 120 == 0) {
            Warden.applyDarknessAround($$0, this.position(), this, 20);
        }
        if (this.tickCount % 20 == 0) {
            this.angerManagement.tick($$0, (Predicate<Entity>)((Predicate)this::canTargetEntity));
            this.syncClientAngerLevel();
        }
        WardenAi.updateActivity(this);
    }

    @Override
    public void handleEntityEvent(byte $$0) {
        if ($$0 == 4) {
            this.roarAnimationState.stop();
            this.attackAnimationState.start(this.tickCount);
        } else if ($$0 == 61) {
            this.tendrilAnimation = 10;
        } else if ($$0 == 62) {
            this.sonicBoomAnimationState.start(this.tickCount);
        } else {
            super.handleEntityEvent($$0);
        }
    }

    private int getHeartBeatDelay() {
        float $$0 = (float)this.getClientAngerLevel() / (float)AngerLevel.ANGRY.getMinimumAnger();
        return 40 - Mth.floor(Mth.clamp($$0, 0.0f, 1.0f) * 30.0f);
    }

    public float getTendrilAnimation(float $$0) {
        return Mth.lerp($$0, this.tendrilAnimationO, this.tendrilAnimation) / 10.0f;
    }

    public float getHeartAnimation(float $$0) {
        return Mth.lerp($$0, this.heartAnimationO, this.heartAnimation) / 10.0f;
    }

    private void clientDiggingParticles(AnimationState $$0) {
        if ((float)$$0.getAccumulatedTime() < 4500.0f) {
            RandomSource $$1 = this.getRandom();
            BlockState $$2 = this.getBlockStateOn();
            if ($$2.getRenderShape() != RenderShape.INVISIBLE) {
                for (int $$3 = 0; $$3 < 30; ++$$3) {
                    double $$4 = this.getX() + (double)Mth.randomBetween($$1, -0.7f, 0.7f);
                    double $$5 = this.getY();
                    double $$6 = this.getZ() + (double)Mth.randomBetween($$1, -0.7f, 0.7f);
                    this.level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, $$2), $$4, $$5, $$6, 0.0, 0.0, 0.0);
                }
            }
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> $$0) {
        if (DATA_POSE.equals($$0)) {
            switch (this.getPose()) {
                case ROARING: {
                    this.roarAnimationState.start(this.tickCount);
                    break;
                }
                case SNIFFING: {
                    this.sniffAnimationState.start(this.tickCount);
                    break;
                }
                case EMERGING: {
                    this.emergeAnimationState.start(this.tickCount);
                    break;
                }
                case DIGGING: {
                    this.diggingAnimationState.start(this.tickCount);
                }
            }
        }
        super.onSyncedDataUpdated($$0);
    }

    @Override
    public boolean ignoreExplosion() {
        return this.isDiggingOrEmerging();
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> $$0) {
        return WardenAi.makeBrain(this, $$0);
    }

    public Brain<Warden> getBrain() {
        return super.getBrain();
    }

    @Override
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        DebugPackets.sendEntityBrain(this);
    }

    @Override
    public void updateDynamicGameEventListener(BiConsumer<DynamicGameEventListener<?>, ServerLevel> $$0) {
        Level level = this.level;
        if (level instanceof ServerLevel) {
            ServerLevel $$1 = (ServerLevel)level;
            $$0.accept(this.dynamicGameEventListener, (Object)$$1);
        }
    }

    @Override
    public TagKey<GameEvent> getListenableEvents() {
        return GameEventTags.WARDEN_CAN_LISTEN;
    }

    @Override
    public boolean canTriggerAvoidVibration() {
        return true;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Contract(value="null->false")
    public boolean canTargetEntity(@Nullable Entity $$0) {
        if (!($$0 instanceof LivingEntity)) return false;
        LivingEntity $$1 = (LivingEntity)$$0;
        if (this.level != $$0.level) return false;
        if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test((Object)$$0)) return false;
        if (this.isAlliedTo($$0)) return false;
        if ($$1.getType() == EntityType.ARMOR_STAND) return false;
        if ($$1.getType() == EntityType.WARDEN) return false;
        if ($$1.isInvulnerable()) return false;
        if ($$1.isDeadOrDying()) return false;
        if (!this.level.getWorldBorder().isWithinBounds($$1.getBoundingBox())) return false;
        return true;
    }

    public static void applyDarknessAround(ServerLevel $$0, Vec3 $$1, @Nullable Entity $$2, int $$3) {
        MobEffectInstance $$4 = new MobEffectInstance(MobEffects.DARKNESS, 260, 0, false, false);
        MobEffectUtil.addEffectToPlayersAround($$0, $$2, $$1, $$3, $$4, 200);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        super.addAdditionalSaveData($$0);
        AngerManagement.codec((Predicate<Entity>)((Predicate)this::canTargetEntity)).encodeStart((DynamicOps)NbtOps.INSTANCE, (Object)this.angerManagement).resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).ifPresent($$1 -> $$0.put("anger", (Tag)$$1));
        VibrationListener.codec(this).encodeStart((DynamicOps)NbtOps.INSTANCE, (Object)this.dynamicGameEventListener.getListener()).resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).ifPresent($$1 -> $$0.put("listener", (Tag)$$1));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$02) {
        super.readAdditionalSaveData($$02);
        if ($$02.contains("anger")) {
            AngerManagement.codec((Predicate<Entity>)((Predicate)this::canTargetEntity)).parse(new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)$$02.get("anger"))).resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).ifPresent($$0 -> {
                this.angerManagement = $$0;
            });
            this.syncClientAngerLevel();
        }
        if ($$02.contains("listener", 10)) {
            VibrationListener.codec(this).parse(new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)$$02.getCompound("listener"))).resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).ifPresent($$0 -> this.dynamicGameEventListener.updateListener((VibrationListener)$$0, this.level));
        }
    }

    private void playListeningSound() {
        if (!this.hasPose(Pose.ROARING)) {
            this.playSound(this.getAngerLevel().getListeningSound(), 10.0f, this.getVoicePitch());
        }
    }

    public AngerLevel getAngerLevel() {
        return AngerLevel.byAnger(this.getActiveAnger());
    }

    private int getActiveAnger() {
        return this.angerManagement.getActiveAnger(this.getTarget());
    }

    public void clearAnger(Entity $$0) {
        this.angerManagement.clearAnger($$0);
    }

    public void increaseAngerAt(@Nullable Entity $$0) {
        this.increaseAngerAt($$0, 35, true);
    }

    @VisibleForTesting
    public void increaseAngerAt(@Nullable Entity $$0, int $$1, boolean $$2) {
        if (!this.isNoAi() && this.canTargetEntity($$0)) {
            WardenAi.setDigCooldown(this);
            boolean $$3 = !(this.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null) instanceof Player);
            int $$4 = this.angerManagement.increaseAnger($$0, $$1);
            if ($$0 instanceof Player && $$3 && AngerLevel.byAnger($$4).isAngry()) {
                this.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
            }
            if ($$2) {
                this.playListeningSound();
            }
        }
    }

    public Optional<LivingEntity> getEntityAngryAt() {
        if (this.getAngerLevel().isAngry()) {
            return this.angerManagement.getActiveEntity();
        }
        return Optional.empty();
    }

    @Override
    @Nullable
    public LivingEntity getTarget() {
        return (LivingEntity)this.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
    }

    @Override
    public boolean removeWhenFarAway(double $$0) {
        return false;
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor $$0, DifficultyInstance $$1, MobSpawnType $$2, @Nullable SpawnGroupData $$3, @Nullable CompoundTag $$4) {
        this.getBrain().setMemoryWithExpiry(MemoryModuleType.DIG_COOLDOWN, Unit.INSTANCE, 1200L);
        if ($$2 == MobSpawnType.TRIGGERED) {
            this.setPose(Pose.EMERGING);
            this.getBrain().setMemoryWithExpiry(MemoryModuleType.IS_EMERGING, Unit.INSTANCE, WardenAi.EMERGE_DURATION);
            this.playSound(SoundEvents.WARDEN_AGITATED, 5.0f, 1.0f);
        }
        return super.finalizeSpawn($$0, $$1, $$2, $$3, $$4);
    }

    @Override
    public boolean hurt(DamageSource $$0, float $$1) {
        boolean $$2 = super.hurt($$0, $$1);
        if (!(this.level.isClientSide || this.isNoAi() || this.isDiggingOrEmerging())) {
            Entity $$3 = $$0.getEntity();
            this.increaseAngerAt($$3, AngerLevel.ANGRY.getMinimumAnger() + 20, false);
            if (this.brain.getMemory(MemoryModuleType.ATTACK_TARGET).isEmpty() && $$3 instanceof LivingEntity) {
                LivingEntity $$4 = (LivingEntity)$$3;
                if (!($$0 instanceof IndirectEntityDamageSource) || this.closerThan($$4, 5.0)) {
                    this.setAttackTarget($$4);
                }
            }
        }
        return $$2;
    }

    public void setAttackTarget(LivingEntity $$0) {
        this.getBrain().eraseMemory(MemoryModuleType.ROAR_TARGET);
        this.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, $$0);
        this.getBrain().eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        SonicBoom.setCooldown(this, 200);
    }

    @Override
    public EntityDimensions getDimensions(Pose $$0) {
        EntityDimensions $$1 = super.getDimensions($$0);
        if (this.isDiggingOrEmerging()) {
            return EntityDimensions.fixed($$1.width, 1.0f);
        }
        return $$1;
    }

    @Override
    public boolean isPushable() {
        return !this.isDiggingOrEmerging() && super.isPushable();
    }

    @Override
    protected void doPush(Entity $$0) {
        if (!this.isNoAi() && !this.getBrain().hasMemoryValue(MemoryModuleType.TOUCH_COOLDOWN)) {
            this.getBrain().setMemoryWithExpiry(MemoryModuleType.TOUCH_COOLDOWN, Unit.INSTANCE, 20L);
            this.increaseAngerAt($$0);
            WardenAi.setDisturbanceLocation(this, $$0.blockPosition());
        }
        super.doPush($$0);
    }

    @Override
    public boolean shouldListen(ServerLevel $$0, GameEventListener $$1, BlockPos $$2, GameEvent $$3, GameEvent.Context $$4) {
        LivingEntity $$5;
        if (this.isNoAi() || this.isDeadOrDying() || this.getBrain().hasMemoryValue(MemoryModuleType.VIBRATION_COOLDOWN) || this.isDiggingOrEmerging() || !$$0.getWorldBorder().isWithinBounds($$2)) {
            return false;
        }
        Entity entity = $$4.sourceEntity();
        return !(entity instanceof LivingEntity) || this.canTargetEntity($$5 = (LivingEntity)entity);
    }

    @Override
    public void onSignalReceive(ServerLevel $$0, GameEventListener $$1, BlockPos $$2, GameEvent $$3, @Nullable Entity $$4, @Nullable Entity $$5, float $$6) {
        if (this.isDeadOrDying()) {
            return;
        }
        this.brain.setMemoryWithExpiry(MemoryModuleType.VIBRATION_COOLDOWN, Unit.INSTANCE, 40L);
        $$0.broadcastEntityEvent(this, (byte)61);
        this.playSound(SoundEvents.WARDEN_TENDRIL_CLICKS, 5.0f, this.getVoicePitch());
        BlockPos $$7 = $$2;
        if ($$5 != null) {
            if (this.closerThan($$5, 30.0)) {
                if (this.getBrain().hasMemoryValue(MemoryModuleType.RECENT_PROJECTILE)) {
                    if (this.canTargetEntity($$5)) {
                        $$7 = $$5.blockPosition();
                    }
                    this.increaseAngerAt($$5);
                } else {
                    this.increaseAngerAt($$5, 10, true);
                }
            }
            this.getBrain().setMemoryWithExpiry(MemoryModuleType.RECENT_PROJECTILE, Unit.INSTANCE, 100L);
        } else {
            this.increaseAngerAt($$4);
        }
        if (!this.getAngerLevel().isAngry()) {
            Optional<LivingEntity> $$8 = this.angerManagement.getActiveEntity();
            if ($$5 != null || $$8.isEmpty() || $$8.get() == $$4) {
                WardenAi.setDisturbanceLocation(this, $$7);
            }
        }
    }

    @VisibleForTesting
    public AngerManagement getAngerManagement() {
        return this.angerManagement;
    }

    @Override
    protected PathNavigation createNavigation(Level $$0) {
        return new GroundPathNavigation(this, $$0){

            @Override
            protected PathFinder createPathFinder(int $$0) {
                this.nodeEvaluator = new WalkNodeEvaluator();
                this.nodeEvaluator.setCanPassDoors(true);
                return new PathFinder(this.nodeEvaluator, $$0){

                    @Override
                    protected float distance(Node $$0, Node $$1) {
                        return $$0.distanceToXZ($$1);
                    }
                };
            }
        };
    }
}