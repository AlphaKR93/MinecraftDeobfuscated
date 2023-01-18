/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 */
package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;

public class ServerboundSignUpdatePacket
implements Packet<ServerGamePacketListener> {
    private static final int MAX_STRING_LENGTH = 384;
    private final BlockPos pos;
    private final String[] lines;

    public ServerboundSignUpdatePacket(BlockPos $$0, String $$1, String $$2, String $$3, String $$4) {
        this.pos = $$0;
        this.lines = new String[]{$$1, $$2, $$3, $$4};
    }

    public ServerboundSignUpdatePacket(FriendlyByteBuf $$0) {
        this.pos = $$0.readBlockPos();
        this.lines = new String[4];
        for (int $$1 = 0; $$1 < 4; ++$$1) {
            this.lines[$$1] = $$0.readUtf(384);
        }
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeBlockPos(this.pos);
        for (int $$1 = 0; $$1 < 4; ++$$1) {
            $$0.writeUtf(this.lines[$$1]);
        }
    }

    @Override
    public void handle(ServerGamePacketListener $$0) {
        $$0.handleSignUpdate(this);
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public String[] getLines() {
        return this.lines;
    }
}