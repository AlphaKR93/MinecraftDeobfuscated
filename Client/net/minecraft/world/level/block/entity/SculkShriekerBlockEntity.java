/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.OptionalInt
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world.level.block.entity;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.OptionalInt;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.GameEventTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.SpawnUtil;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.monster.warden.WardenSpawnTracker;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.SculkShriekerBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.vibrations.VibrationListener;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class SculkShriekerBlockEntity
extends BlockEntity
implements VibrationListener.VibrationListenerConfig {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int LISTENER_RADIUS = 8;
    private static final int WARNING_SOUND_RADIUS = 10;
    private static final int WARDEN_SPAWN_ATTEMPTS = 20;
    private static final int WARDEN_SPAWN_RANGE_XZ = 5;
    private static final int WARDEN_SPAWN_RANGE_Y = 6;
    private static final int DARKNESS_RADIUS = 40;
    private static final Int2ObjectMap<SoundEvent> SOUND_BY_LEVEL = (Int2ObjectMap)Util.make(new Int2ObjectOpenHashMap(), $$0 -> {
        $$0.put(1, (Object)SoundEvents.WARDEN_NEARBY_CLOSE);
        $$0.put(2, (Object)SoundEvents.WARDEN_NEARBY_CLOSER);
        $$0.put(3, (Object)SoundEvents.WARDEN_NEARBY_CLOSEST);
        $$0.put(4, (Object)SoundEvents.WARDEN_LISTENING_ANGRY);
    });
    private static final int SHRIEKING_TICKS = 90;
    private int warningLevel;
    private VibrationListener listener;

    public SculkShriekerBlockEntity(BlockPos $$0, BlockState $$1) {
        super(BlockEntityType.SCULK_SHRIEKER, $$0, $$1);
        this.listener = new VibrationListener(new BlockPositionSource(this.worldPosition), 8, this);
    }

    public VibrationListener getListener() {
        return this.listener;
    }

    @Override
    public void load(CompoundTag $$02) {
        super.load($$02);
        if ($$02.contains("warning_level", 99)) {
            this.warningLevel = $$02.getInt("warning_level");
        }
        if ($$02.contains("listener", 10)) {
            VibrationListener.codec(this).parse(new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)$$02.getCompound("listener"))).resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).ifPresent($$0 -> {
                this.listener = $$0;
            });
        }
    }

    @Override
    protected void saveAdditional(CompoundTag $$0) {
        super.saveAdditional($$0);
        $$0.putInt("warning_level", this.warningLevel);
        VibrationListener.codec(this).encodeStart((DynamicOps)NbtOps.INSTANCE, (Object)this.listener).resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).ifPresent($$1 -> $$0.put("listener", (Tag)$$1));
    }

    @Override
    public TagKey<GameEvent> getListenableEvents() {
        return GameEventTags.SHRIEKER_CAN_LISTEN;
    }

    @Override
    public boolean shouldListen(ServerLevel $$0, GameEventListener $$1, BlockPos $$2, GameEvent $$3, GameEvent.Context $$4) {
        return this.getBlockState().getValue(SculkShriekerBlock.SHRIEKING) == false && SculkShriekerBlockEntity.tryGetPlayer($$4.sourceEntity()) != null;
    }

    @Nullable
    public static ServerPlayer tryGetPlayer(@Nullable Entity $$0) {
        ItemEntity $$5;
        Projectile $$3;
        Entity entity;
        Entity entity2;
        if ($$0 instanceof ServerPlayer) {
            ServerPlayer $$1 = (ServerPlayer)$$0;
            return $$1;
        }
        if ($$0 != null && (entity2 = $$0.getControllingPassenger()) instanceof ServerPlayer) {
            ServerPlayer $$2 = (ServerPlayer)entity2;
            return $$2;
        }
        if ($$0 instanceof Projectile && (entity = ($$3 = (Projectile)$$0).getOwner()) instanceof ServerPlayer) {
            ServerPlayer $$4 = (ServerPlayer)entity;
            return $$4;
        }
        if ($$0 instanceof ItemEntity && (entity = ($$5 = (ItemEntity)$$0).getOwner()) instanceof ServerPlayer) {
            ServerPlayer $$6 = (ServerPlayer)entity;
            return $$6;
        }
        return null;
    }

    @Override
    public void onSignalReceive(ServerLevel $$0, GameEventListener $$1, BlockPos $$2, GameEvent $$3, @Nullable Entity $$4, @Nullable Entity $$5, float $$6) {
        this.tryShriek($$0, SculkShriekerBlockEntity.tryGetPlayer($$5 != null ? $$5 : $$4));
    }

    public void tryShriek(ServerLevel $$0, @Nullable ServerPlayer $$1) {
        if ($$1 == null) {
            return;
        }
        BlockState $$2 = this.getBlockState();
        if ($$2.getValue(SculkShriekerBlock.SHRIEKING).booleanValue()) {
            return;
        }
        this.warningLevel = 0;
        if (this.canRespond($$0) && !this.tryToWarn($$0, $$1)) {
            return;
        }
        this.shriek($$0, $$1);
    }

    private boolean tryToWarn(ServerLevel $$02, ServerPlayer $$1) {
        OptionalInt $$2 = WardenSpawnTracker.tryWarn($$02, this.getBlockPos(), $$1);
        $$2.ifPresent($$0 -> {
            this.warningLevel = $$0;
        });
        return $$2.isPresent();
    }

    private void shriek(ServerLevel $$0, @Nullable Entity $$1) {
        BlockPos $$2 = this.getBlockPos();
        BlockState $$3 = this.getBlockState();
        $$0.setBlock($$2, (BlockState)$$3.setValue(SculkShriekerBlock.SHRIEKING, true), 2);
        $$0.scheduleTick($$2, $$3.getBlock(), 90);
        $$0.levelEvent(3007, $$2, 0);
        $$0.gameEvent(GameEvent.SHRIEK, $$2, GameEvent.Context.of($$1));
    }

    private boolean canRespond(ServerLevel $$0) {
        return this.getBlockState().getValue(SculkShriekerBlock.CAN_SUMMON) != false && $$0.getDifficulty() != Difficulty.PEACEFUL && $$0.getGameRules().getBoolean(GameRules.RULE_DO_WARDEN_SPAWNING);
    }

    public void tryRespond(ServerLevel $$0) {
        if (this.canRespond($$0) && this.warningLevel > 0) {
            if (!this.trySummonWarden($$0)) {
                this.playWardenReplySound();
            }
            Warden.applyDarknessAround($$0, Vec3.atCenterOf(this.getBlockPos()), null, 40);
        }
    }

    private void playWardenReplySound() {
        SoundEvent $$0 = (SoundEvent)SOUND_BY_LEVEL.get(this.warningLevel);
        if ($$0 != null) {
            BlockPos $$1 = this.getBlockPos();
            int $$2 = $$1.getX() + Mth.randomBetweenInclusive(this.level.random, -10, 10);
            int $$3 = $$1.getY() + Mth.randomBetweenInclusive(this.level.random, -10, 10);
            int $$4 = $$1.getZ() + Mth.randomBetweenInclusive(this.level.random, -10, 10);
            this.level.playSound(null, $$2, $$3, $$4, $$0, SoundSource.HOSTILE, 5.0f, 1.0f);
        }
    }

    private boolean trySummonWarden(ServerLevel $$0) {
        if (this.warningLevel < 4) {
            return false;
        }
        return SpawnUtil.trySpawnMob(EntityType.WARDEN, MobSpawnType.TRIGGERED, $$0, this.getBlockPos(), 20, 5, 6, SpawnUtil.Strategy.ON_TOP_OF_COLLIDER).isPresent();
    }

    @Override
    public void onSignalSchedule() {
        this.setChanged();
    }
}