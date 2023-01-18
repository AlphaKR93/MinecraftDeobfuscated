/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMaps
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Optional
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.gameevent.vibrations;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.VibrationParticleOption;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.GameEventTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipBlockStateContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.vibrations.VibrationInfo;
import net.minecraft.world.level.gameevent.vibrations.VibrationSelector;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class VibrationListener
implements GameEventListener {
    @VisibleForTesting
    public static final Object2IntMap<GameEvent> VIBRATION_FREQUENCY_FOR_EVENT = Object2IntMaps.unmodifiable((Object2IntMap)((Object2IntMap)Util.make(new Object2IntOpenHashMap(), $$0 -> {
        $$0.put((Object)GameEvent.STEP, 1);
        $$0.put((Object)GameEvent.FLAP, 2);
        $$0.put((Object)GameEvent.SWIM, 3);
        $$0.put((Object)GameEvent.ELYTRA_GLIDE, 4);
        $$0.put((Object)GameEvent.HIT_GROUND, 5);
        $$0.put((Object)GameEvent.TELEPORT, 5);
        $$0.put((Object)GameEvent.SPLASH, 6);
        $$0.put((Object)GameEvent.ENTITY_SHAKE, 6);
        $$0.put((Object)GameEvent.BLOCK_CHANGE, 6);
        $$0.put((Object)GameEvent.NOTE_BLOCK_PLAY, 6);
        $$0.put((Object)GameEvent.PROJECTILE_SHOOT, 7);
        $$0.put((Object)GameEvent.DRINK, 7);
        $$0.put((Object)GameEvent.PRIME_FUSE, 7);
        $$0.put((Object)GameEvent.PROJECTILE_LAND, 8);
        $$0.put((Object)GameEvent.EAT, 8);
        $$0.put((Object)GameEvent.ENTITY_INTERACT, 8);
        $$0.put((Object)GameEvent.ENTITY_DAMAGE, 8);
        $$0.put((Object)GameEvent.EQUIP, 9);
        $$0.put((Object)GameEvent.SHEAR, 9);
        $$0.put((Object)GameEvent.ENTITY_ROAR, 9);
        $$0.put((Object)GameEvent.BLOCK_CLOSE, 10);
        $$0.put((Object)GameEvent.BLOCK_DEACTIVATE, 10);
        $$0.put((Object)GameEvent.BLOCK_DETACH, 10);
        $$0.put((Object)GameEvent.DISPENSE_FAIL, 10);
        $$0.put((Object)GameEvent.BLOCK_OPEN, 11);
        $$0.put((Object)GameEvent.BLOCK_ACTIVATE, 11);
        $$0.put((Object)GameEvent.BLOCK_ATTACH, 11);
        $$0.put((Object)GameEvent.ENTITY_PLACE, 12);
        $$0.put((Object)GameEvent.BLOCK_PLACE, 12);
        $$0.put((Object)GameEvent.FLUID_PLACE, 12);
        $$0.put((Object)GameEvent.ENTITY_DIE, 13);
        $$0.put((Object)GameEvent.BLOCK_DESTROY, 13);
        $$0.put((Object)GameEvent.FLUID_PICKUP, 13);
        $$0.put((Object)GameEvent.ITEM_INTERACT_FINISH, 14);
        $$0.put((Object)GameEvent.CONTAINER_CLOSE, 14);
        $$0.put((Object)GameEvent.PISTON_CONTRACT, 14);
        $$0.put((Object)GameEvent.PISTON_EXTEND, 15);
        $$0.put((Object)GameEvent.CONTAINER_OPEN, 15);
        $$0.put((Object)GameEvent.EXPLODE, 15);
        $$0.put((Object)GameEvent.LIGHTNING_STRIKE, 15);
        $$0.put((Object)GameEvent.INSTRUMENT_PLAY, 15);
    })));
    protected final PositionSource listenerSource;
    protected final int listenerRange;
    protected final VibrationListenerConfig config;
    @Nullable
    protected VibrationInfo currentVibration;
    protected int travelTimeInTicks;
    private final VibrationSelector selectionStrategy;

    public static Codec<VibrationListener> codec(VibrationListenerConfig $$0) {
        return RecordCodecBuilder.create($$12 -> $$12.group((App)PositionSource.CODEC.fieldOf("source").forGetter($$0 -> $$0.listenerSource), (App)ExtraCodecs.NON_NEGATIVE_INT.fieldOf("range").forGetter($$0 -> $$0.listenerRange), (App)VibrationInfo.CODEC.optionalFieldOf("event").forGetter($$0 -> Optional.ofNullable((Object)((Object)$$0.currentVibration))), (App)VibrationSelector.CODEC.fieldOf("selector").forGetter($$0 -> $$0.selectionStrategy), (App)ExtraCodecs.NON_NEGATIVE_INT.fieldOf("event_delay").orElse((Object)0).forGetter($$0 -> $$0.travelTimeInTicks)).apply((Applicative)$$12, ($$1, $$2, $$3, $$4, $$5) -> new VibrationListener((PositionSource)$$1, (int)$$2, $$0, (VibrationInfo)((Object)((Object)((Object)$$3.orElse(null)))), (VibrationSelector)$$4, (int)$$5)));
    }

    private VibrationListener(PositionSource $$0, int $$1, VibrationListenerConfig $$2, @Nullable VibrationInfo $$3, VibrationSelector $$4, int $$5) {
        this.listenerSource = $$0;
        this.listenerRange = $$1;
        this.config = $$2;
        this.currentVibration = $$3;
        this.travelTimeInTicks = $$5;
        this.selectionStrategy = $$4;
    }

    public VibrationListener(PositionSource $$0, int $$1, VibrationListenerConfig $$2) {
        this($$0, $$1, $$2, null, new VibrationSelector(), 0);
    }

    public static int getGameEventFrequency(GameEvent $$0) {
        return VIBRATION_FREQUENCY_FOR_EVENT.getOrDefault((Object)$$0, 0);
    }

    public void tick(Level $$0) {
        if ($$0 instanceof ServerLevel) {
            ServerLevel $$12 = (ServerLevel)$$0;
            if (this.currentVibration == null) {
                this.selectionStrategy.chosenCandidate($$12.getGameTime()).ifPresent($$1 -> {
                    this.currentVibration = $$1;
                    Vec3 $$2 = this.currentVibration.pos();
                    this.travelTimeInTicks = Mth.floor(this.currentVibration.distance());
                    $$12.sendParticles(new VibrationParticleOption(this.listenerSource, this.travelTimeInTicks), $$2.x, $$2.y, $$2.z, 1, 0.0, 0.0, 0.0, 0.0);
                    this.config.onSignalSchedule();
                    this.selectionStrategy.startOver();
                });
            }
            if (this.currentVibration != null) {
                --this.travelTimeInTicks;
                if (this.travelTimeInTicks <= 0) {
                    this.travelTimeInTicks = 0;
                    this.config.onSignalReceive($$12, this, new BlockPos(this.currentVibration.pos()), this.currentVibration.gameEvent(), (Entity)this.currentVibration.getEntity($$12).orElse(null), (Entity)this.currentVibration.getProjectileOwner($$12).orElse(null), this.currentVibration.distance());
                    this.currentVibration = null;
                }
            }
        }
    }

    @Override
    public PositionSource getListenerSource() {
        return this.listenerSource;
    }

    @Override
    public int getListenerRadius() {
        return this.listenerRange;
    }

    @Override
    public boolean handleGameEvent(ServerLevel $$0, GameEvent $$1, GameEvent.Context $$2, Vec3 $$3) {
        if (this.currentVibration != null) {
            return false;
        }
        if (!this.config.isValidVibration($$1, $$2)) {
            return false;
        }
        Optional<Vec3> $$4 = this.listenerSource.getPosition($$0);
        if ($$4.isEmpty()) {
            return false;
        }
        Vec3 $$5 = (Vec3)$$4.get();
        if (!this.config.shouldListen($$0, this, new BlockPos($$3), $$1, $$2)) {
            return false;
        }
        if (VibrationListener.isOccluded($$0, $$3, $$5)) {
            return false;
        }
        this.scheduleVibration($$0, $$1, $$2, $$3, $$5);
        return true;
    }

    public void forceGameEvent(ServerLevel $$0, GameEvent $$1, GameEvent.Context $$2, Vec3 $$3) {
        this.listenerSource.getPosition($$0).ifPresent($$4 -> this.scheduleVibration($$0, $$1, $$2, $$3, (Vec3)$$4));
    }

    public void scheduleVibration(ServerLevel $$0, GameEvent $$1, GameEvent.Context $$2, Vec3 $$3, Vec3 $$4) {
        this.selectionStrategy.addCandidate(new VibrationInfo($$1, (float)$$3.distanceTo($$4), $$3, $$2.sourceEntity()), $$0.getGameTime());
    }

    private static boolean isOccluded(Level $$02, Vec3 $$1, Vec3 $$2) {
        Vec3 $$3 = new Vec3((double)Mth.floor($$1.x) + 0.5, (double)Mth.floor($$1.y) + 0.5, (double)Mth.floor($$1.z) + 0.5);
        Vec3 $$4 = new Vec3((double)Mth.floor($$2.x) + 0.5, (double)Mth.floor($$2.y) + 0.5, (double)Mth.floor($$2.z) + 0.5);
        for (Direction $$5 : Direction.values()) {
            Vec3 $$6 = $$3.relative($$5, 1.0E-5f);
            if ($$02.isBlockInLine(new ClipBlockStateContext($$6, $$4, (Predicate<BlockState>)((Predicate)$$0 -> $$0.is(BlockTags.OCCLUDES_VIBRATION_SIGNALS)))).getType() == HitResult.Type.BLOCK) continue;
            return false;
        }
        return true;
    }

    public static interface VibrationListenerConfig {
        default public TagKey<GameEvent> getListenableEvents() {
            return GameEventTags.VIBRATIONS;
        }

        default public boolean canTriggerAvoidVibration() {
            return false;
        }

        default public boolean isValidVibration(GameEvent $$0, GameEvent.Context $$1) {
            if (!$$0.is(this.getListenableEvents())) {
                return false;
            }
            Entity $$2 = $$1.sourceEntity();
            if ($$2 != null) {
                if ($$2.isSpectator()) {
                    return false;
                }
                if ($$2.isSteppingCarefully() && $$0.is(GameEventTags.IGNORE_VIBRATIONS_SNEAKING)) {
                    if (this.canTriggerAvoidVibration() && $$2 instanceof ServerPlayer) {
                        ServerPlayer $$3 = (ServerPlayer)$$2;
                        CriteriaTriggers.AVOID_VIBRATION.trigger($$3);
                    }
                    return false;
                }
                if ($$2.dampensVibrations()) {
                    return false;
                }
            }
            if ($$1.affectedState() != null) {
                return !$$1.affectedState().is(BlockTags.DAMPENS_VIBRATIONS);
            }
            return true;
        }

        public boolean shouldListen(ServerLevel var1, GameEventListener var2, BlockPos var3, GameEvent var4, GameEvent.Context var5);

        public void onSignalReceive(ServerLevel var1, GameEventListener var2, BlockPos var3, GameEvent var4, @Nullable Entity var5, @Nullable Entity var6, float var7);

        default public void onSignalSchedule() {
        }
    }
}