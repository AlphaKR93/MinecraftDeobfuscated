/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Short
 */
package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.resources.ResourceLocation;

public class ServerboundCustomPayloadPacket
implements Packet<ServerGamePacketListener> {
    private static final int MAX_PAYLOAD_SIZE = Short.MAX_VALUE;
    public static final ResourceLocation BRAND = new ResourceLocation("brand");
    private final ResourceLocation identifier;
    private final FriendlyByteBuf data;

    public ServerboundCustomPayloadPacket(ResourceLocation $$0, FriendlyByteBuf $$1) {
        this.identifier = $$0;
        this.data = $$1;
    }

    public ServerboundCustomPayloadPacket(FriendlyByteBuf $$0) {
        this.identifier = $$0.readResourceLocation();
        int $$1 = $$0.readableBytes();
        if ($$1 < 0 || $$1 > Short.MAX_VALUE) {
            throw new IllegalArgumentException("Payload may not be larger than 32767 bytes");
        }
        this.data = new FriendlyByteBuf($$0.readBytes($$1));
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeResourceLocation(this.identifier);
        $$0.writeBytes(this.data);
    }

    @Override
    public void handle(ServerGamePacketListener $$0) {
        $$0.handleCustomPayload(this);
        this.data.release();
    }

    public ResourceLocation getIdentifier() {
        return this.identifier;
    }

    public FriendlyByteBuf getData() {
        return this.data;
    }
}