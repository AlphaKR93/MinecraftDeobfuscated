/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  java.lang.Boolean
 *  java.lang.Float
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Optional
 *  java.util.UUID
 *  java.util.function.BiConsumer
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world.entity.animal.allay;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.GameEventTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.allay.AllayAi;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import net.minecraft.world.level.gameevent.EntityPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.vibrations.VibrationListener;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class Allay
extends PathfinderMob
implements InventoryCarrier {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int VIBRATION_EVENT_LISTENER_RANGE = 16;
    private static final Vec3i ITEM_PICKUP_REACH = new Vec3i(1, 1, 1);
    private static final int LIFTING_ITEM_ANIMATION_DURATION = 5;
    private static final float DANCING_LOOP_DURATION = 55.0f;
    private static final float SPINNING_ANIMATION_DURATION = 15.0f;
    private static final Ingredient DUPLICATION_ITEM = Ingredient.of(Items.AMETHYST_SHARD);
    private static final int DUPLICATION_COOLDOWN_TICKS = 6000;
    private static final int NUM_OF_DUPLICATION_HEARTS = 3;
    private static final double RIDING_OFFSET = 0.4;
    private static final EntityDataAccessor<Boolean> DATA_DANCING = SynchedEntityData.defineId(Allay.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_CAN_DUPLICATE = SynchedEntityData.defineId(Allay.class, EntityDataSerializers.BOOLEAN);
    protected static final ImmutableList<SensorType<? extends Sensor<? super Allay>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.HURT_BY, SensorType.NEAREST_ITEMS);
    protected static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.PATH, MemoryModuleType.LOOK_TARGET, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.HURT_BY, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryModuleType.LIKED_PLAYER, MemoryModuleType.LIKED_NOTEBLOCK_POSITION, MemoryModuleType.LIKED_NOTEBLOCK_COOLDOWN_TICKS, MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS, MemoryModuleType.IS_PANICKING, (Object[])new MemoryModuleType[0]);
    public static final ImmutableList<Float> THROW_SOUND_PITCHES = ImmutableList.of((Object)Float.valueOf((float)0.5625f), (Object)Float.valueOf((float)0.625f), (Object)Float.valueOf((float)0.75f), (Object)Float.valueOf((float)0.9375f), (Object)Float.valueOf((float)1.0f), (Object)Float.valueOf((float)1.0f), (Object)Float.valueOf((float)1.125f), (Object)Float.valueOf((float)1.25f), (Object)Float.valueOf((float)1.5f), (Object)Float.valueOf((float)1.875f), (Object)Float.valueOf((float)2.0f), (Object)Float.valueOf((float)2.25f), (Object[])new Float[]{Float.valueOf((float)2.5f), Float.valueOf((float)3.0f), Float.valueOf((float)3.75f), Float.valueOf((float)4.0f)});
    private final DynamicGameEventListener<VibrationListener> dynamicVibrationListener;
    private final VibrationListener.VibrationListenerConfig vibrationListenerConfig;
    private final DynamicGameEventListener<JukeboxListener> dynamicJukeboxListener;
    private final SimpleContainer inventory = new SimpleContainer(1);
    @Nullable
    @Nullable
    private BlockPos jukeboxPos;
    private long duplicationCooldown;
    private float holdingItemAnimationTicks;
    private float holdingItemAnimationTicks0;
    private float dancingAnimationTicks;
    private float spinningAnimationTicks;
    private float spinningAnimationTicks0;

    public Allay(EntityType<? extends Allay> $$0, Level $$1) {
        super((EntityType<? extends PathfinderMob>)$$0, $$1);
        this.moveControl = new FlyingMoveControl(this, 20, true);
        this.setCanPickUpLoot(this.canPickUpLoot());
        EntityPositionSource $$2 = new EntityPositionSource(this, this.getEyeHeight());
        this.vibrationListenerConfig = new AllayVibrationListenerConfig();
        this.dynamicVibrationListener = new DynamicGameEventListener<VibrationListener>(new VibrationListener($$2, 16, this.vibrationListenerConfig));
        this.dynamicJukeboxListener = new DynamicGameEventListener<JukeboxListener>(new JukeboxListener($$2, GameEvent.JUKEBOX_PLAY.getNotificationRadius()));
    }

    protected Brain.Provider<Allay> brainProvider() {
        return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> $$0) {
        return AllayAi.makeBrain(this.brainProvider().makeBrain($$0));
    }

    public Brain<Allay> getBrain() {
        return super.getBrain();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 20.0).add(Attributes.FLYING_SPEED, 0.1f).add(Attributes.MOVEMENT_SPEED, 0.1f).add(Attributes.ATTACK_DAMAGE, 2.0).add(Attributes.FOLLOW_RANGE, 48.0);
    }

    @Override
    protected PathNavigation createNavigation(Level $$0) {
        FlyingPathNavigation $$1 = new FlyingPathNavigation(this, $$0);
        $$1.setCanOpenDoors(false);
        $$1.setCanFloat(true);
        $$1.setCanPassDoors(true);
        return $$1;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_DANCING, false);
        this.entityData.define(DATA_CAN_DUPLICATE, true);
    }

    @Override
    public void travel(Vec3 $$0) {
        if (this.isEffectiveAi() || this.isControlledByLocalInstance()) {
            if (this.isInWater()) {
                this.moveRelative(0.02f, $$0);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.8f));
            } else if (this.isInLava()) {
                this.moveRelative(0.02f, $$0);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.5));
            } else {
                this.moveRelative(this.getSpeed(), $$0);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.91f));
            }
        }
        this.calculateEntityAnimation(this, false);
    }

    @Override
    protected float getStandingEyeHeight(Pose $$0, EntityDimensions $$1) {
        return $$1.height * 0.6f;
    }

    @Override
    public boolean causeFallDamage(float $$0, float $$1, DamageSource $$2) {
        return false;
    }

    @Override
    public boolean hurt(DamageSource $$0, float $$1) {
        Entity entity = $$0.getEntity();
        if (entity instanceof Player) {
            Player $$2 = (Player)entity;
            Optional<UUID> $$3 = this.getBrain().getMemory(MemoryModuleType.LIKED_PLAYER);
            if ($$3.isPresent() && $$2.getUUID().equals($$3.get())) {
                return false;
            }
        }
        return super.hurt($$0, $$1);
    }

    @Override
    protected void playStepSound(BlockPos $$0, BlockState $$1) {
    }

    @Override
    protected void checkFallDamage(double $$0, boolean $$1, BlockState $$2, BlockPos $$3) {
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.hasItemInSlot(EquipmentSlot.MAINHAND) ? SoundEvents.ALLAY_AMBIENT_WITH_ITEM : SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource $$0) {
        return SoundEvents.ALLAY_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ALLAY_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4f;
    }

    @Override
    protected void customServerAiStep() {
        this.level.getProfiler().push("allayBrain");
        this.getBrain().tick((ServerLevel)this.level, this);
        this.level.getProfiler().pop();
        this.level.getProfiler().push("allayActivityUpdate");
        AllayAi.updateActivity(this);
        this.level.getProfiler().pop();
        super.customServerAiStep();
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level.isClientSide && this.isAlive() && this.tickCount % 10 == 0) {
            this.heal(1.0f);
        }
        if (this.isDancing() && this.shouldStopDancing() && this.tickCount % 20 == 0) {
            this.setDancing(false);
            this.jukeboxPos = null;
        }
        this.updateDuplicationCooldown();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide) {
            this.holdingItemAnimationTicks0 = this.holdingItemAnimationTicks;
            this.holdingItemAnimationTicks = this.hasItemInHand() ? Mth.clamp(this.holdingItemAnimationTicks + 1.0f, 0.0f, 5.0f) : Mth.clamp(this.holdingItemAnimationTicks - 1.0f, 0.0f, 5.0f);
            if (this.isDancing()) {
                this.dancingAnimationTicks += 1.0f;
                this.spinningAnimationTicks0 = this.spinningAnimationTicks;
                this.spinningAnimationTicks = this.isSpinning() ? (this.spinningAnimationTicks += 1.0f) : (this.spinningAnimationTicks -= 1.0f);
                this.spinningAnimationTicks = Mth.clamp(this.spinningAnimationTicks, 0.0f, 15.0f);
            } else {
                this.dancingAnimationTicks = 0.0f;
                this.spinningAnimationTicks = 0.0f;
                this.spinningAnimationTicks0 = 0.0f;
            }
        } else {
            this.dynamicVibrationListener.getListener().tick(this.level);
            if (this.isPanicking()) {
                this.setDancing(false);
            }
        }
    }

    @Override
    public boolean canPickUpLoot() {
        return !this.isOnPickupCooldown() && this.hasItemInHand();
    }

    public boolean hasItemInHand() {
        return !this.getItemInHand(InteractionHand.MAIN_HAND).isEmpty();
    }

    @Override
    public boolean canTakeItem(ItemStack $$0) {
        return false;
    }

    private boolean isOnPickupCooldown() {
        return this.getBrain().checkMemory(MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS, MemoryStatus.VALUE_PRESENT);
    }

    @Override
    protected InteractionResult mobInteract(Player $$0, InteractionHand $$1) {
        ItemStack $$2 = $$0.getItemInHand($$1);
        ItemStack $$3 = this.getItemInHand(InteractionHand.MAIN_HAND);
        if (this.isDancing() && this.isDuplicationItem($$2) && this.canDuplicate()) {
            this.duplicateAllay();
            this.level.broadcastEntityEvent(this, (byte)18);
            this.level.playSound($$0, this, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.NEUTRAL, 2.0f, 1.0f);
            this.removeInteractionItem($$0, $$2);
            return InteractionResult.SUCCESS;
        }
        if ($$3.isEmpty() && !$$2.isEmpty()) {
            ItemStack $$4 = $$2.copy();
            $$4.setCount(1);
            this.setItemInHand(InteractionHand.MAIN_HAND, $$4);
            this.removeInteractionItem($$0, $$2);
            this.level.playSound($$0, this, SoundEvents.ALLAY_ITEM_GIVEN, SoundSource.NEUTRAL, 2.0f, 1.0f);
            this.getBrain().setMemory(MemoryModuleType.LIKED_PLAYER, $$0.getUUID());
            return InteractionResult.SUCCESS;
        }
        if (!$$3.isEmpty() && $$1 == InteractionHand.MAIN_HAND && $$2.isEmpty()) {
            this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            this.level.playSound($$0, this, SoundEvents.ALLAY_ITEM_TAKEN, SoundSource.NEUTRAL, 2.0f, 1.0f);
            this.swing(InteractionHand.MAIN_HAND);
            for (ItemStack $$5 : this.getInventory().removeAllItems()) {
                BehaviorUtils.throwItem(this, $$5, this.position());
            }
            this.getBrain().eraseMemory(MemoryModuleType.LIKED_PLAYER);
            $$0.addItem($$3);
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract($$0, $$1);
    }

    public void setJukeboxPlaying(BlockPos $$0, boolean $$1) {
        if ($$1) {
            if (!this.isDancing()) {
                this.jukeboxPos = $$0;
                this.setDancing(true);
            }
        } else if ($$0.equals(this.jukeboxPos) || this.jukeboxPos == null) {
            this.jukeboxPos = null;
            this.setDancing(false);
        }
    }

    @Override
    public SimpleContainer getInventory() {
        return this.inventory;
    }

    @Override
    protected Vec3i getPickupReach() {
        return ITEM_PICKUP_REACH;
    }

    @Override
    public boolean wantsToPickUp(ItemStack $$0) {
        ItemStack $$1 = this.getItemInHand(InteractionHand.MAIN_HAND);
        return !$$1.isEmpty() && this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) && this.inventory.canAddItem($$0) && this.allayConsidersItemEqual($$1, $$0);
    }

    private boolean allayConsidersItemEqual(ItemStack $$0, ItemStack $$1) {
        return $$0.sameItem($$1) && !this.hasNonMatchingPotion($$0, $$1);
    }

    private boolean hasNonMatchingPotion(ItemStack $$0, ItemStack $$1) {
        boolean $$5;
        boolean $$3;
        CompoundTag $$2 = $$0.getTag();
        boolean bl = $$3 = $$2 != null && $$2.contains("Potion");
        if (!$$3) {
            return false;
        }
        CompoundTag $$4 = $$1.getTag();
        boolean bl2 = $$5 = $$4 != null && $$4.contains("Potion");
        if (!$$5) {
            return true;
        }
        Tag $$6 = $$2.get("Potion");
        Tag $$7 = $$4.get("Potion");
        return $$6 != null && $$7 != null && !$$6.equals($$7);
    }

    @Override
    protected void pickUpItem(ItemEntity $$0) {
        InventoryCarrier.pickUpItem(this, this, $$0);
    }

    @Override
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        DebugPackets.sendEntityBrain(this);
    }

    @Override
    public boolean isFlapping() {
        return !this.isOnGround();
    }

    @Override
    public void updateDynamicGameEventListener(BiConsumer<DynamicGameEventListener<?>, ServerLevel> $$0) {
        Level level = this.level;
        if (level instanceof ServerLevel) {
            ServerLevel $$1 = (ServerLevel)level;
            $$0.accept(this.dynamicVibrationListener, (Object)$$1);
            $$0.accept(this.dynamicJukeboxListener, (Object)$$1);
        }
    }

    public boolean isDancing() {
        return this.entityData.get(DATA_DANCING);
    }

    public boolean isPanicking() {
        return this.brain.getMemory(MemoryModuleType.IS_PANICKING).isPresent();
    }

    public void setDancing(boolean $$0) {
        if (this.level.isClientSide || !this.isEffectiveAi() || $$0 && this.isPanicking()) {
            return;
        }
        this.entityData.set(DATA_DANCING, $$0);
    }

    private boolean shouldStopDancing() {
        return this.jukeboxPos == null || !this.jukeboxPos.closerToCenterThan(this.position(), GameEvent.JUKEBOX_PLAY.getNotificationRadius()) || !this.level.getBlockState(this.jukeboxPos).is(Blocks.JUKEBOX);
    }

    public float getHoldingItemAnimationProgress(float $$0) {
        return Mth.lerp($$0, this.holdingItemAnimationTicks0, this.holdingItemAnimationTicks) / 5.0f;
    }

    public boolean isSpinning() {
        float $$0 = this.dancingAnimationTicks % 55.0f;
        return $$0 < 15.0f;
    }

    public float getSpinningProgress(float $$0) {
        return Mth.lerp($$0, this.spinningAnimationTicks0, this.spinningAnimationTicks) / 15.0f;
    }

    @Override
    public boolean equipmentHasChanged(ItemStack $$0, ItemStack $$1) {
        return !this.allayConsidersItemEqual($$0, $$1);
    }

    @Override
    protected void dropEquipment() {
        super.dropEquipment();
        this.inventory.removeAllItems().forEach(this::spawnAtLocation);
        ItemStack $$0 = this.getItemBySlot(EquipmentSlot.MAINHAND);
        if (!$$0.isEmpty() && !EnchantmentHelper.hasVanishingCurse($$0)) {
            this.spawnAtLocation($$0);
            this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        }
    }

    @Override
    public boolean removeWhenFarAway(double $$0) {
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        super.addAdditionalSaveData($$0);
        this.writeInventoryToTag($$0);
        VibrationListener.codec(this.vibrationListenerConfig).encodeStart((DynamicOps)NbtOps.INSTANCE, (Object)this.dynamicVibrationListener.getListener()).resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).ifPresent($$1 -> $$0.put("listener", (Tag)$$1));
        $$0.putLong("DuplicationCooldown", this.duplicationCooldown);
        $$0.putBoolean("CanDuplicate", this.canDuplicate());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$02) {
        super.readAdditionalSaveData($$02);
        this.readInventoryFromTag($$02);
        if ($$02.contains("listener", 10)) {
            VibrationListener.codec(this.vibrationListenerConfig).parse(new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)$$02.getCompound("listener"))).resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).ifPresent($$0 -> this.dynamicVibrationListener.updateListener((VibrationListener)$$0, this.level));
        }
        this.duplicationCooldown = $$02.getInt("DuplicationCooldown");
        this.entityData.set(DATA_CAN_DUPLICATE, $$02.getBoolean("CanDuplicate"));
    }

    @Override
    protected boolean shouldStayCloseToLeashHolder() {
        return false;
    }

    private void updateDuplicationCooldown() {
        if (this.duplicationCooldown > 0L) {
            --this.duplicationCooldown;
        }
        if (!this.level.isClientSide() && this.duplicationCooldown == 0L && !this.canDuplicate()) {
            this.entityData.set(DATA_CAN_DUPLICATE, true);
        }
    }

    private boolean isDuplicationItem(ItemStack $$0) {
        return DUPLICATION_ITEM.test($$0);
    }

    private void duplicateAllay() {
        Allay $$0 = EntityType.ALLAY.create(this.level);
        if ($$0 != null) {
            $$0.moveTo(this.position());
            $$0.setPersistenceRequired();
            $$0.resetDuplicationCooldown();
            this.resetDuplicationCooldown();
            this.level.addFreshEntity($$0);
        }
    }

    private void resetDuplicationCooldown() {
        this.duplicationCooldown = 6000L;
        this.entityData.set(DATA_CAN_DUPLICATE, false);
    }

    private boolean canDuplicate() {
        return this.entityData.get(DATA_CAN_DUPLICATE);
    }

    private void removeInteractionItem(Player $$0, ItemStack $$1) {
        if (!$$0.getAbilities().instabuild) {
            $$1.shrink(1);
        }
    }

    @Override
    public Vec3 getLeashOffset() {
        return new Vec3(0.0, (double)this.getEyeHeight() * 0.6, (double)this.getBbWidth() * 0.1);
    }

    @Override
    public double getMyRidingOffset() {
        return 0.4;
    }

    @Override
    public void handleEntityEvent(byte $$0) {
        if ($$0 == 18) {
            for (int $$1 = 0; $$1 < 3; ++$$1) {
                this.spawnHeartParticle();
            }
        } else {
            super.handleEntityEvent($$0);
        }
    }

    private void spawnHeartParticle() {
        double $$0 = this.random.nextGaussian() * 0.02;
        double $$1 = this.random.nextGaussian() * 0.02;
        double $$2 = this.random.nextGaussian() * 0.02;
        this.level.addParticle(ParticleTypes.HEART, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), $$0, $$1, $$2);
    }

    class AllayVibrationListenerConfig
    implements VibrationListener.VibrationListenerConfig {
        AllayVibrationListenerConfig() {
        }

        @Override
        public boolean shouldListen(ServerLevel $$0, GameEventListener $$1, BlockPos $$2, GameEvent $$3, GameEvent.Context $$4) {
            if (Allay.this.isNoAi()) {
                return false;
            }
            Optional<GlobalPos> $$5 = Allay.this.getBrain().getMemory(MemoryModuleType.LIKED_NOTEBLOCK_POSITION);
            if ($$5.isEmpty()) {
                return true;
            }
            GlobalPos $$6 = (GlobalPos)$$5.get();
            return $$6.dimension().equals($$0.dimension()) && $$6.pos().equals($$2);
        }

        @Override
        public void onSignalReceive(ServerLevel $$0, GameEventListener $$1, BlockPos $$2, GameEvent $$3, @Nullable Entity $$4, @Nullable Entity $$5, float $$6) {
            if ($$3 == GameEvent.NOTE_BLOCK_PLAY) {
                AllayAi.hearNoteblock(Allay.this, new BlockPos($$2));
            }
        }

        @Override
        public TagKey<GameEvent> getListenableEvents() {
            return GameEventTags.ALLAY_CAN_LISTEN;
        }
    }

    class JukeboxListener
    implements GameEventListener {
        private final PositionSource listenerSource;
        private final int listenerRadius;

        public JukeboxListener(PositionSource $$0, int $$1) {
            this.listenerSource = $$0;
            this.listenerRadius = $$1;
        }

        @Override
        public PositionSource getListenerSource() {
            return this.listenerSource;
        }

        @Override
        public int getListenerRadius() {
            return this.listenerRadius;
        }

        @Override
        public boolean handleGameEvent(ServerLevel $$0, GameEvent $$1, GameEvent.Context $$2, Vec3 $$3) {
            if ($$1 == GameEvent.JUKEBOX_PLAY) {
                Allay.this.setJukeboxPlaying(new BlockPos($$3), true);
                return true;
            }
            if ($$1 == GameEvent.JUKEBOX_STOP_PLAY) {
                Allay.this.setJukeboxPlaying(new BlockPos($$3), false);
                return true;
            }
            return false;
        }
    }
}