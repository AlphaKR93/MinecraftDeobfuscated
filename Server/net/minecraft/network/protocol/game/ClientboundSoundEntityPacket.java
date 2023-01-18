/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.network.protocol.game;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

public class ClientboundSoundEntityPacket
implements Packet<ClientGamePacketListener> {
    private final Holder<SoundEvent> sound;
    private final SoundSource source;
    private final int id;
    private final float volume;
    private final float pitch;
    private final long seed;

    public ClientboundSoundEntityPacket(Holder<SoundEvent> $$0, SoundSource $$1, Entity $$2, float $$3, float $$4, long $$5) {
        this.sound = $$0;
        this.source = $$1;
        this.id = $$2.getId();
        this.volume = $$3;
        this.pitch = $$4;
        this.seed = $$5;
    }

    public ClientboundSoundEntityPacket(FriendlyByteBuf $$0) {
        this.sound = $$0.readById(BuiltInRegistries.SOUND_EVENT.asHolderIdMap(), SoundEvent::readFromNetwork);
        this.source = $$0.readEnum(SoundSource.class);
        this.id = $$0.readVarInt();
        this.volume = $$0.readFloat();
        this.pitch = $$0.readFloat();
        this.seed = $$0.readLong();
    }

    @Override
    public void write(FriendlyByteBuf $$02) {
        $$02.writeId(BuiltInRegistries.SOUND_EVENT.asHolderIdMap(), this.sound, ($$0, $$1) -> $$1.writeToNetwork((FriendlyByteBuf)((Object)$$0)));
        $$02.writeEnum(this.source);
        $$02.writeVarInt(this.id);
        $$02.writeFloat(this.volume);
        $$02.writeFloat(this.pitch);
        $$02.writeLong(this.seed);
    }

    public Holder<SoundEvent> getSound() {
        return this.sound;
    }

    public SoundSource getSource() {
        return this.source;
    }

    public int getId() {
        return this.id;
    }

    public float getVolume() {
        return this.volume;
    }

    public float getPitch() {
        return this.pitch;
    }

    public long getSeed() {
        return this.seed;
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleSoundEntityEvent(this);
    }
}