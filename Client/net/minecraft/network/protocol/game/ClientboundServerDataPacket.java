/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Optional
 *  javax.annotation.Nullable
 */
package net.minecraft.network.protocol.game;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class ClientboundServerDataPacket
implements Packet<ClientGamePacketListener> {
    private final Optional<Component> motd;
    private final Optional<String> iconBase64;
    private final boolean enforcesSecureChat;

    public ClientboundServerDataPacket(@Nullable Component $$0, @Nullable String $$1, boolean $$2) {
        this.motd = Optional.ofNullable((Object)$$0);
        this.iconBase64 = Optional.ofNullable((Object)$$1);
        this.enforcesSecureChat = $$2;
    }

    public ClientboundServerDataPacket(FriendlyByteBuf $$0) {
        this.motd = $$0.readOptional(FriendlyByteBuf::readComponent);
        this.iconBase64 = $$0.readOptional(FriendlyByteBuf::readUtf);
        this.enforcesSecureChat = $$0.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeOptional(this.motd, FriendlyByteBuf::writeComponent);
        $$0.writeOptional(this.iconBase64, FriendlyByteBuf::writeUtf);
        $$0.writeBoolean(this.enforcesSecureChat);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleServerData(this);
    }

    public Optional<Component> getMotd() {
        return this.motd;
    }

    public Optional<String> getIconBase64() {
        return this.iconBase64;
    }

    public boolean enforcesSecureChat() {
        return this.enforcesSecureChat;
    }
}