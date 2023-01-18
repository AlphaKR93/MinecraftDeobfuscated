/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Iterator
 *  java.util.List
 *  java.util.Optional
 *  java.util.Set
 */
package net.minecraft.world.level.gameevent;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.GameEventListenerRegistry;
import net.minecraft.world.phys.Vec3;

public class EuclideanGameEventListenerRegistry
implements GameEventListenerRegistry {
    private final List<GameEventListener> listeners = Lists.newArrayList();
    private final Set<GameEventListener> listenersToRemove = Sets.newHashSet();
    private final List<GameEventListener> listenersToAdd = Lists.newArrayList();
    private boolean processing;
    private final ServerLevel level;

    public EuclideanGameEventListenerRegistry(ServerLevel $$0) {
        this.level = $$0;
    }

    @Override
    public boolean isEmpty() {
        return this.listeners.isEmpty();
    }

    @Override
    public void register(GameEventListener $$0) {
        if (this.processing) {
            this.listenersToAdd.add((Object)$$0);
        } else {
            this.listeners.add((Object)$$0);
        }
        DebugPackets.sendGameEventListenerInfo(this.level, $$0);
    }

    @Override
    public void unregister(GameEventListener $$0) {
        if (this.processing) {
            this.listenersToRemove.add((Object)$$0);
        } else {
            this.listeners.remove((Object)$$0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean visitInRangeListeners(GameEvent $$0, Vec3 $$1, GameEvent.Context $$2, GameEventListenerRegistry.ListenerVisitor $$3) {
        this.processing = true;
        boolean $$4 = false;
        try {
            Iterator $$5 = this.listeners.iterator();
            while ($$5.hasNext()) {
                GameEventListener $$6 = (GameEventListener)$$5.next();
                if (this.listenersToRemove.remove((Object)$$6)) {
                    $$5.remove();
                    continue;
                }
                Optional<Vec3> $$7 = EuclideanGameEventListenerRegistry.getPostableListenerPosition(this.level, $$1, $$6);
                if (!$$7.isPresent()) continue;
                $$3.visit($$6, (Vec3)$$7.get());
                $$4 = true;
            }
        }
        finally {
            this.processing = false;
        }
        if (!this.listenersToAdd.isEmpty()) {
            this.listeners.addAll(this.listenersToAdd);
            this.listenersToAdd.clear();
        }
        if (!this.listenersToRemove.isEmpty()) {
            this.listeners.removeAll(this.listenersToRemove);
            this.listenersToRemove.clear();
        }
        return $$4;
    }

    private static Optional<Vec3> getPostableListenerPosition(ServerLevel $$0, Vec3 $$1, GameEventListener $$2) {
        int $$5;
        Optional<Vec3> $$3 = $$2.getListenerSource().getPosition($$0);
        if ($$3.isEmpty()) {
            return Optional.empty();
        }
        double $$4 = ((Vec3)$$3.get()).distanceToSqr($$1);
        if ($$4 > (double)($$5 = $$2.getListenerRadius() * $$2.getListenerRadius())) {
            return Optional.empty();
        }
        return $$3;
    }
}