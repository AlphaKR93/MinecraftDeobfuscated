/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.network.protocol.login;

import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.login.ServerLoginPacketListener;

public class ServerboundCustomQueryPacket
implements Packet<ServerLoginPacketListener> {
    private static final int MAX_PAYLOAD_SIZE = 0x100000;
    private final int transactionId;
    @Nullable
    private final FriendlyByteBuf data;

    public ServerboundCustomQueryPacket(int $$0, @Nullable FriendlyByteBuf $$1) {
        this.transactionId = $$0;
        this.data = $$1;
    }

    public ServerboundCustomQueryPacket(FriendlyByteBuf $$02) {
        this.transactionId = $$02.readVarInt();
        this.data = (FriendlyByteBuf)((Object)$$02.readNullable($$0 -> {
            int $$1 = $$0.readableBytes();
            if ($$1 < 0 || $$1 > 0x100000) {
                throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
            }
            return new FriendlyByteBuf($$0.readBytes($$1));
        }));
    }

    @Override
    public void write(FriendlyByteBuf $$02) {
        $$02.writeVarInt(this.transactionId);
        $$02.writeNullable(this.data, ($$0, $$1) -> $$0.writeBytes($$1.slice()));
    }

    @Override
    public void handle(ServerLoginPacketListener $$0) {
        $$0.handleCustomQueryPacket(this);
    }

    public int getTransactionId() {
        return this.transactionId;
    }

    @Nullable
    public FriendlyByteBuf getData() {
        return this.data;
    }
}