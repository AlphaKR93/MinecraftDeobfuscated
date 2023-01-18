/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  java.lang.AutoCloseable
 *  java.lang.Object
 */
package net.minecraft.client.multiplayer.prediction;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class BlockStatePredictionHandler
implements AutoCloseable {
    private final Long2ObjectOpenHashMap<ServerVerifiedState> serverVerifiedStates = new Long2ObjectOpenHashMap();
    private int currentSequenceNr;
    private boolean isPredicting;

    public void retainKnownServerState(BlockPos $$0, BlockState $$1, LocalPlayer $$22) {
        this.serverVerifiedStates.compute($$0.asLong(), ($$2, $$3) -> {
            if ($$3 != null) {
                return $$3.setSequence(this.currentSequenceNr);
            }
            return new ServerVerifiedState(this.currentSequenceNr, $$1, $$22.position());
        });
    }

    public boolean updateKnownServerState(BlockPos $$0, BlockState $$1) {
        ServerVerifiedState $$2 = (ServerVerifiedState)this.serverVerifiedStates.get($$0.asLong());
        if ($$2 == null) {
            return false;
        }
        $$2.setBlockState($$1);
        return true;
    }

    public void endPredictionsUpTo(int $$0, ClientLevel $$1) {
        ObjectIterator $$2 = this.serverVerifiedStates.long2ObjectEntrySet().iterator();
        while ($$2.hasNext()) {
            Long2ObjectMap.Entry $$3 = (Long2ObjectMap.Entry)$$2.next();
            ServerVerifiedState $$4 = (ServerVerifiedState)$$3.getValue();
            if ($$4.sequence > $$0) continue;
            BlockPos $$5 = BlockPos.of($$3.getLongKey());
            $$2.remove();
            $$1.syncBlockState($$5, $$4.blockState, $$4.playerPos);
        }
    }

    public BlockStatePredictionHandler startPredicting() {
        ++this.currentSequenceNr;
        this.isPredicting = true;
        return this;
    }

    public void close() {
        this.isPredicting = false;
    }

    public int currentSequence() {
        return this.currentSequenceNr;
    }

    public boolean isPredicting() {
        return this.isPredicting;
    }

    static class ServerVerifiedState {
        final Vec3 playerPos;
        int sequence;
        BlockState blockState;

        ServerVerifiedState(int $$0, BlockState $$1, Vec3 $$2) {
            this.sequence = $$0;
            this.blockState = $$1;
            this.playerPos = $$2;
        }

        ServerVerifiedState setSequence(int $$0) {
            this.sequence = $$0;
            return this;
        }

        void setBlockState(BlockState $$0) {
            this.blockState = $$0;
        }
    }
}