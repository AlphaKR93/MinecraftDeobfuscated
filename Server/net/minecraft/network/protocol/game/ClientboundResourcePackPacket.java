/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  javax.annotation.Nullable
 */
package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class ClientboundResourcePackPacket
implements Packet<ClientGamePacketListener> {
    public static final int MAX_HASH_LENGTH = 40;
    private final String url;
    private final String hash;
    private final boolean required;
    @Nullable
    private final Component prompt;

    public ClientboundResourcePackPacket(String $$0, String $$1, boolean $$2, @Nullable Component $$3) {
        if ($$1.length() > 40) {
            throw new IllegalArgumentException("Hash is too long (max 40, was " + $$1.length() + ")");
        }
        this.url = $$0;
        this.hash = $$1;
        this.required = $$2;
        this.prompt = $$3;
    }

    public ClientboundResourcePackPacket(FriendlyByteBuf $$0) {
        this.url = $$0.readUtf();
        this.hash = $$0.readUtf(40);
        this.required = $$0.readBoolean();
        this.prompt = (Component)$$0.readNullable(FriendlyByteBuf::readComponent);
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeUtf(this.url);
        $$0.writeUtf(this.hash);
        $$0.writeBoolean(this.required);
        $$0.writeNullable(this.prompt, FriendlyByteBuf::writeComponent);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleResourcePack(this);
    }

    public String getUrl() {
        return this.url;
    }

    public String getHash() {
        return this.hash;
    }

    public boolean isRequired() {
        return this.required;
    }

    @Nullable
    public Component getPrompt() {
        return this.prompt;
    }
}