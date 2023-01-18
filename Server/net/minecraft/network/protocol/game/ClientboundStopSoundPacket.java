/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;

public class ClientboundStopSoundPacket
implements Packet<ClientGamePacketListener> {
    private static final int HAS_SOURCE = 1;
    private static final int HAS_SOUND = 2;
    @Nullable
    private final ResourceLocation name;
    @Nullable
    private final SoundSource source;

    public ClientboundStopSoundPacket(@Nullable ResourceLocation $$0, @Nullable SoundSource $$1) {
        this.name = $$0;
        this.source = $$1;
    }

    public ClientboundStopSoundPacket(FriendlyByteBuf $$0) {
        byte $$1 = $$0.readByte();
        this.source = ($$1 & 1) > 0 ? $$0.readEnum(SoundSource.class) : null;
        this.name = ($$1 & 2) > 0 ? $$0.readResourceLocation() : null;
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        if (this.source != null) {
            if (this.name != null) {
                $$0.writeByte(3);
                $$0.writeEnum(this.source);
                $$0.writeResourceLocation(this.name);
            } else {
                $$0.writeByte(1);
                $$0.writeEnum(this.source);
            }
        } else if (this.name != null) {
            $$0.writeByte(2);
            $$0.writeResourceLocation(this.name);
        } else {
            $$0.writeByte(0);
        }
    }

    @Nullable
    public ResourceLocation getName() {
        return this.name;
    }

    @Nullable
    public SoundSource getSource() {
        return this.source;
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleStopSoundEvent(this);
    }
}