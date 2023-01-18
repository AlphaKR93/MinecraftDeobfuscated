/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.util.function.Consumer
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.gameevent;

import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.GameEventListenerRegistry;

public class DynamicGameEventListener<T extends GameEventListener> {
    private T listener;
    @Nullable
    private SectionPos lastSection;

    public DynamicGameEventListener(T $$0) {
        this.listener = $$0;
    }

    public void add(ServerLevel $$0) {
        this.move($$0);
    }

    public void updateListener(T $$0, @Nullable Level $$12) {
        Object $$2 = this.listener;
        if ($$2 == $$0) {
            return;
        }
        if ($$12 instanceof ServerLevel) {
            ServerLevel $$3 = (ServerLevel)$$12;
            DynamicGameEventListener.ifChunkExists($$3, this.lastSection, (Consumer<GameEventListenerRegistry>)((Consumer)$$1 -> $$1.unregister((GameEventListener)$$2)));
            DynamicGameEventListener.ifChunkExists($$3, this.lastSection, (Consumer<GameEventListenerRegistry>)((Consumer)$$1 -> $$1.register((GameEventListener)$$0)));
        }
        this.listener = $$0;
    }

    public T getListener() {
        return this.listener;
    }

    public void remove(ServerLevel $$02) {
        DynamicGameEventListener.ifChunkExists($$02, this.lastSection, (Consumer<GameEventListenerRegistry>)((Consumer)$$0 -> $$0.unregister((GameEventListener)this.listener)));
    }

    public void move(ServerLevel $$0) {
        this.listener.getListenerSource().getPosition($$0).map(SectionPos::of).ifPresent($$1 -> {
            if (this.lastSection == null || !this.lastSection.equals($$1)) {
                DynamicGameEventListener.ifChunkExists($$0, this.lastSection, (Consumer<GameEventListenerRegistry>)((Consumer)$$0 -> $$0.unregister((GameEventListener)this.listener)));
                this.lastSection = $$1;
                DynamicGameEventListener.ifChunkExists($$0, this.lastSection, (Consumer<GameEventListenerRegistry>)((Consumer)$$0 -> $$0.register((GameEventListener)this.listener)));
            }
        });
    }

    private static void ifChunkExists(LevelReader $$0, @Nullable SectionPos $$1, Consumer<GameEventListenerRegistry> $$2) {
        if ($$1 == null) {
            return;
        }
        ChunkAccess $$3 = $$0.getChunk($$1.x(), $$1.z(), ChunkStatus.FULL, false);
        if ($$3 != null) {
            $$2.accept((Object)$$3.getListenerRegistry($$1.y()));
        }
    }
}