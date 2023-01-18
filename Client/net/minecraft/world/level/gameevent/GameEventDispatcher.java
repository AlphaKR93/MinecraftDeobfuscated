/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.util.ArrayList
 *  java.util.Collections
 *  java.util.List
 */
package net.minecraft.world.level.gameevent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.GameEventListenerRegistry;
import net.minecraft.world.phys.Vec3;

public class GameEventDispatcher {
    private final ServerLevel level;

    public GameEventDispatcher(ServerLevel $$0) {
        this.level = $$0;
    }

    public void post(GameEvent $$0, Vec3 $$1, GameEvent.Context $$2) {
        int $$3 = $$0.getNotificationRadius();
        BlockPos $$4 = new BlockPos($$1);
        int $$5 = SectionPos.blockToSectionCoord($$4.getX() - $$3);
        int $$6 = SectionPos.blockToSectionCoord($$4.getY() - $$3);
        int $$7 = SectionPos.blockToSectionCoord($$4.getZ() - $$3);
        int $$8 = SectionPos.blockToSectionCoord($$4.getX() + $$3);
        int $$9 = SectionPos.blockToSectionCoord($$4.getY() + $$3);
        int $$10 = SectionPos.blockToSectionCoord($$4.getZ() + $$3);
        ArrayList $$11 = new ArrayList();
        GameEventListenerRegistry.ListenerVisitor $$12 = (arg_0, arg_1) -> this.lambda$post$0((List)$$11, $$0, $$1, $$2, arg_0, arg_1);
        boolean $$13 = false;
        for (int $$14 = $$5; $$14 <= $$8; ++$$14) {
            for (int $$15 = $$7; $$15 <= $$10; ++$$15) {
                LevelChunk $$16 = this.level.getChunkSource().getChunkNow($$14, $$15);
                if ($$16 == null) continue;
                for (int $$17 = $$6; $$17 <= $$9; ++$$17) {
                    $$13 |= ((ChunkAccess)$$16).getListenerRegistry($$17).visitInRangeListeners($$0, $$1, $$2, $$12);
                }
            }
        }
        if (!$$11.isEmpty()) {
            this.handleGameEventMessagesInQueue((List<GameEvent.ListenerInfo>)$$11);
        }
        if ($$13) {
            DebugPackets.sendGameEventInfo(this.level, $$0, $$1);
        }
    }

    private void handleGameEventMessagesInQueue(List<GameEvent.ListenerInfo> $$0) {
        Collections.sort($$0);
        for (GameEvent.ListenerInfo $$1 : $$0) {
            GameEventListener $$2 = $$1.recipient();
            $$2.handleGameEvent(this.level, $$1.gameEvent(), $$1.context(), $$1.source());
        }
    }

    private /* synthetic */ void lambda$post$0(List $$0, GameEvent $$1, Vec3 $$2, GameEvent.Context $$3, GameEventListener $$4, Vec3 $$5) {
        if ($$4.getDeliveryMode() == GameEventListener.DeliveryMode.BY_DISTANCE) {
            $$0.add((Object)new GameEvent.ListenerInfo($$1, $$2, $$3, $$4, $$5));
        } else {
            $$4.handleGameEvent(this.level, $$1, $$3, $$2);
        }
    }
}