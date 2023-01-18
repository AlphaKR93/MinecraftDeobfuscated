/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world.level.block.entity;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.SculkSensorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.vibrations.VibrationListener;
import org.slf4j.Logger;

public class SculkSensorBlockEntity
extends BlockEntity
implements VibrationListener.VibrationListenerConfig {
    private static final Logger LOGGER = LogUtils.getLogger();
    private VibrationListener listener;
    private int lastVibrationFrequency;

    public SculkSensorBlockEntity(BlockPos $$0, BlockState $$1) {
        super(BlockEntityType.SCULK_SENSOR, $$0, $$1);
        this.listener = new VibrationListener(new BlockPositionSource(this.worldPosition), ((SculkSensorBlock)$$1.getBlock()).getListenerRange(), this);
    }

    @Override
    public void load(CompoundTag $$02) {
        super.load($$02);
        this.lastVibrationFrequency = $$02.getInt("last_vibration_frequency");
        if ($$02.contains("listener", 10)) {
            VibrationListener.codec(this).parse(new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)$$02.getCompound("listener"))).resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).ifPresent($$0 -> {
                this.listener = $$0;
            });
        }
    }

    @Override
    protected void saveAdditional(CompoundTag $$0) {
        super.saveAdditional($$0);
        $$0.putInt("last_vibration_frequency", this.lastVibrationFrequency);
        VibrationListener.codec(this).encodeStart((DynamicOps)NbtOps.INSTANCE, (Object)this.listener).resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).ifPresent($$1 -> $$0.put("listener", (Tag)$$1));
    }

    public VibrationListener getListener() {
        return this.listener;
    }

    public int getLastVibrationFrequency() {
        return this.lastVibrationFrequency;
    }

    @Override
    public boolean canTriggerAvoidVibration() {
        return true;
    }

    @Override
    public boolean shouldListen(ServerLevel $$0, GameEventListener $$1, BlockPos $$2, GameEvent $$3, @Nullable GameEvent.Context $$4) {
        if ($$2.equals(this.getBlockPos()) && ($$3 == GameEvent.BLOCK_DESTROY || $$3 == GameEvent.BLOCK_PLACE)) {
            return false;
        }
        return SculkSensorBlock.canActivate(this.getBlockState());
    }

    @Override
    public void onSignalReceive(ServerLevel $$0, GameEventListener $$1, BlockPos $$2, GameEvent $$3, @Nullable Entity $$4, @Nullable Entity $$5, float $$6) {
        BlockState $$7 = this.getBlockState();
        if (SculkSensorBlock.canActivate($$7)) {
            this.lastVibrationFrequency = VibrationListener.getGameEventFrequency($$3);
            SculkSensorBlock.activate($$4, $$0, this.worldPosition, $$7, SculkSensorBlockEntity.getRedstoneStrengthForDistance($$6, $$1.getListenerRadius()));
        }
    }

    @Override
    public void onSignalSchedule() {
        this.setChanged();
    }

    public static int getRedstoneStrengthForDistance(float $$0, int $$1) {
        double $$2 = (double)$$0 / (double)$$1;
        return Math.max((int)1, (int)(15 - Mth.floor($$2 * 15.0)));
    }

    public void setLastVibrationFrequency(int $$0) {
        this.lastVibrationFrequency = $$0;
    }
}