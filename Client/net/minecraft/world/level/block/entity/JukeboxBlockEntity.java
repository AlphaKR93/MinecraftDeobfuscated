/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Clearable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class JukeboxBlockEntity
extends BlockEntity
implements Clearable {
    private ItemStack record = ItemStack.EMPTY;
    private int ticksSinceLastEvent;
    private long tickCount;
    private long recordStartedTick;
    private boolean isPlaying;

    public JukeboxBlockEntity(BlockPos $$0, BlockState $$1) {
        super(BlockEntityType.JUKEBOX, $$0, $$1);
    }

    @Override
    public void load(CompoundTag $$0) {
        super.load($$0);
        if ($$0.contains("RecordItem", 10)) {
            this.setRecord(ItemStack.of($$0.getCompound("RecordItem")));
        }
        this.isPlaying = $$0.getBoolean("IsPlaying");
        this.recordStartedTick = $$0.getLong("RecordStartTick");
        this.tickCount = $$0.getLong("TickCount");
    }

    @Override
    protected void saveAdditional(CompoundTag $$0) {
        super.saveAdditional($$0);
        if (!this.getRecord().isEmpty()) {
            $$0.put("RecordItem", this.getRecord().save(new CompoundTag()));
        }
        $$0.putBoolean("IsPlaying", this.isPlaying);
        $$0.putLong("RecordStartTick", this.recordStartedTick);
        $$0.putLong("TickCount", this.tickCount);
    }

    public ItemStack getRecord() {
        return this.record;
    }

    public void setRecord(ItemStack $$0) {
        this.record = $$0;
        this.setChanged();
    }

    public void playRecord() {
        this.recordStartedTick = this.tickCount;
        this.isPlaying = true;
    }

    @Override
    public void clearContent() {
        this.setRecord(ItemStack.EMPTY);
        this.isPlaying = false;
    }

    public static void playRecordTick(Level $$0, BlockPos $$1, BlockState $$2, JukeboxBlockEntity $$3) {
        Item item;
        ++$$3.ticksSinceLastEvent;
        if (JukeboxBlockEntity.recordIsPlaying($$2, $$3) && (item = $$3.getRecord().getItem()) instanceof RecordItem) {
            RecordItem $$4 = (RecordItem)item;
            if (JukeboxBlockEntity.recordShouldStopPlaying($$3, $$4)) {
                $$0.gameEvent(GameEvent.JUKEBOX_STOP_PLAY, $$1, GameEvent.Context.of($$2));
                $$3.isPlaying = false;
            } else if (JukeboxBlockEntity.shouldSendJukeboxPlayingEvent($$3)) {
                $$3.ticksSinceLastEvent = 0;
                $$0.gameEvent(GameEvent.JUKEBOX_PLAY, $$1, GameEvent.Context.of($$2));
            }
        }
        ++$$3.tickCount;
    }

    private static boolean recordIsPlaying(BlockState $$0, JukeboxBlockEntity $$1) {
        return $$0.getValue(JukeboxBlock.HAS_RECORD) != false && $$1.isPlaying;
    }

    private static boolean recordShouldStopPlaying(JukeboxBlockEntity $$0, RecordItem $$1) {
        return $$0.tickCount >= $$0.recordStartedTick + (long)$$1.getLengthInTicks();
    }

    private static boolean shouldSendJukeboxPlayingEvent(JukeboxBlockEntity $$0) {
        return $$0.ticksSinceLastEvent >= 20;
    }
}