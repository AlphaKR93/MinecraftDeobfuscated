/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.network.protocol.login;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.login.ClientLoginPacketListener;
import net.minecraft.resources.ResourceLocation;

public class ClientboundCustomQueryPacket
implements Packet<ClientLoginPacketListener> {
    private static final int MAX_PAYLOAD_SIZE = 0x100000;
    private final int transactionId;
    private final ResourceLocation identifier;
    private final FriendlyByteBuf data;

    public ClientboundCustomQueryPacket(int $$0, ResourceLocation $$1, FriendlyByteBuf $$2) {
        this.transactionId = $$0;
        this.identifier = $$1;
        this.data = $$2;
    }

    public ClientboundCustomQueryPacket(FriendlyByteBuf $$0) {
        this.transactionId = $$0.readVarInt();
        this.identifier = $$0.readResourceLocation();
        int $$1 = $$0.readableBytes();
        if ($$1 < 0 || $$1 > 0x100000) {
            throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
        }
        this.data = new FriendlyByteBuf($$0.readBytes($$1));
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeVarInt(this.transactionId);
        $$0.writeResourceLocation(this.identifier);
        $$0.writeBytes(this.data.copy());
    }

    @Override
    public void handle(ClientLoginPacketListener $$0) {
        $$0.handleCustomQuery(this);
    }

    public int getTransactionId() {
        return this.transactionId;
    }

    public ResourceLocation getIdentifier() {
        return this.identifier;
    }

    public FriendlyByteBuf getData() {
        return this.data;
    }
}