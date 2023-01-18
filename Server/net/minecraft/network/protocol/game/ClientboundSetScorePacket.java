/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Objects
 *  javax.annotation.Nullable
 */
package net.minecraft.network.protocol.game;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.ServerScoreboard;

public class ClientboundSetScorePacket
implements Packet<ClientGamePacketListener> {
    private final String owner;
    @Nullable
    private final String objectiveName;
    private final int score;
    private final ServerScoreboard.Method method;

    public ClientboundSetScorePacket(ServerScoreboard.Method $$0, @Nullable String $$1, String $$2, int $$3) {
        if ($$0 != ServerScoreboard.Method.REMOVE && $$1 == null) {
            throw new IllegalArgumentException("Need an objective name");
        }
        this.owner = $$2;
        this.objectiveName = $$1;
        this.score = $$3;
        this.method = $$0;
    }

    public ClientboundSetScorePacket(FriendlyByteBuf $$0) {
        this.owner = $$0.readUtf();
        this.method = $$0.readEnum(ServerScoreboard.Method.class);
        String $$1 = $$0.readUtf();
        this.objectiveName = Objects.equals((Object)$$1, (Object)"") ? null : $$1;
        this.score = this.method != ServerScoreboard.Method.REMOVE ? $$0.readVarInt() : 0;
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeUtf(this.owner);
        $$0.writeEnum(this.method);
        $$0.writeUtf(this.objectiveName == null ? "" : this.objectiveName);
        if (this.method != ServerScoreboard.Method.REMOVE) {
            $$0.writeVarInt(this.score);
        }
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleSetScore(this);
    }

    public String getOwner() {
        return this.owner;
    }

    @Nullable
    public String getObjectiveName() {
        return this.objectiveName;
    }

    public int getScore() {
        return this.score;
    }

    public ServerScoreboard.Method getMethod() {
        return this.method;
    }
}