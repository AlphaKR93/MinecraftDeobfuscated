/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Collection
 *  java.util.Collections
 *  java.util.Set
 *  java.util.function.Function
 */
package net.minecraft.server.level;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;

public class ServerBossEvent
extends BossEvent {
    private final Set<ServerPlayer> players = Sets.newHashSet();
    private final Set<ServerPlayer> unmodifiablePlayers = Collections.unmodifiableSet(this.players);
    private boolean visible = true;

    public ServerBossEvent(Component $$0, BossEvent.BossBarColor $$1, BossEvent.BossBarOverlay $$2) {
        super(Mth.createInsecureUUID(), $$0, $$1, $$2);
    }

    @Override
    public void setProgress(float $$0) {
        if ($$0 != this.progress) {
            super.setProgress($$0);
            this.broadcast((Function<BossEvent, ClientboundBossEventPacket>)((Function)ClientboundBossEventPacket::createUpdateProgressPacket));
        }
    }

    @Override
    public void setColor(BossEvent.BossBarColor $$0) {
        if ($$0 != this.color) {
            super.setColor($$0);
            this.broadcast((Function<BossEvent, ClientboundBossEventPacket>)((Function)ClientboundBossEventPacket::createUpdateStylePacket));
        }
    }

    @Override
    public void setOverlay(BossEvent.BossBarOverlay $$0) {
        if ($$0 != this.overlay) {
            super.setOverlay($$0);
            this.broadcast((Function<BossEvent, ClientboundBossEventPacket>)((Function)ClientboundBossEventPacket::createUpdateStylePacket));
        }
    }

    @Override
    public BossEvent setDarkenScreen(boolean $$0) {
        if ($$0 != this.darkenScreen) {
            super.setDarkenScreen($$0);
            this.broadcast((Function<BossEvent, ClientboundBossEventPacket>)((Function)ClientboundBossEventPacket::createUpdatePropertiesPacket));
        }
        return this;
    }

    @Override
    public BossEvent setPlayBossMusic(boolean $$0) {
        if ($$0 != this.playBossMusic) {
            super.setPlayBossMusic($$0);
            this.broadcast((Function<BossEvent, ClientboundBossEventPacket>)((Function)ClientboundBossEventPacket::createUpdatePropertiesPacket));
        }
        return this;
    }

    @Override
    public BossEvent setCreateWorldFog(boolean $$0) {
        if ($$0 != this.createWorldFog) {
            super.setCreateWorldFog($$0);
            this.broadcast((Function<BossEvent, ClientboundBossEventPacket>)((Function)ClientboundBossEventPacket::createUpdatePropertiesPacket));
        }
        return this;
    }

    @Override
    public void setName(Component $$0) {
        if (!Objects.equal((Object)$$0, (Object)this.name)) {
            super.setName($$0);
            this.broadcast((Function<BossEvent, ClientboundBossEventPacket>)((Function)ClientboundBossEventPacket::createUpdateNamePacket));
        }
    }

    private void broadcast(Function<BossEvent, ClientboundBossEventPacket> $$0) {
        if (this.visible) {
            ClientboundBossEventPacket $$1 = (ClientboundBossEventPacket)$$0.apply((Object)this);
            for (ServerPlayer $$2 : this.players) {
                $$2.connection.send($$1);
            }
        }
    }

    public void addPlayer(ServerPlayer $$0) {
        if (this.players.add((Object)$$0) && this.visible) {
            $$0.connection.send(ClientboundBossEventPacket.createAddPacket(this));
        }
    }

    public void removePlayer(ServerPlayer $$0) {
        if (this.players.remove((Object)$$0) && this.visible) {
            $$0.connection.send(ClientboundBossEventPacket.createRemovePacket(this.getId()));
        }
    }

    public void removeAllPlayers() {
        if (!this.players.isEmpty()) {
            for (ServerPlayer $$0 : Lists.newArrayList(this.players)) {
                this.removePlayer($$0);
            }
        }
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean $$0) {
        if ($$0 != this.visible) {
            this.visible = $$0;
            for (ServerPlayer $$1 : this.players) {
                $$1.connection.send($$0 ? ClientboundBossEventPacket.createAddPacket(this) : ClientboundBossEventPacket.createRemovePacket(this.getId()));
            }
        }
    }

    public Collection<ServerPlayer> getPlayers() {
        return this.unmodifiablePlayers;
    }
}