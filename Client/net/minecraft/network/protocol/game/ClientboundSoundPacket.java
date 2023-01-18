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

public class ClientboundSoundPacket
implements Packet<ClientGamePacketListener> {
    public static final float LOCATION_ACCURACY = 8.0f;
    private final Holder<SoundEvent> sound;
    private final SoundSource source;
    private final int x;
    private final int y;
    private final int z;
    private final float volume;
    private final float pitch;
    private final long seed;

    public ClientboundSoundPacket(Holder<SoundEvent> $$0, SoundSource $$1, double $$2, double $$3, double $$4, float $$5, float $$6, long $$7) {
        this.sound = $$0;
        this.source = $$1;
        this.x = (int)($$2 * 8.0);
        this.y = (int)($$3 * 8.0);
        this.z = (int)($$4 * 8.0);
        this.volume = $$5;
        this.pitch = $$6;
        this.seed = $$7;
    }

    public ClientboundSoundPacket(FriendlyByteBuf $$0) {
        this.sound = $$0.readById(BuiltInRegistries.SOUND_EVENT.asHolderIdMap(), SoundEvent::readFromNetwork);
        this.source = $$0.readEnum(SoundSource.class);
        this.x = $$0.readInt();
        this.y = $$0.readInt();
        this.z = $$0.readInt();
        this.volume = $$0.readFloat();
        this.pitch = $$0.readFloat();
        this.seed = $$0.readLong();
    }

    @Override
    public void write(FriendlyByteBuf $$02) {
        $$02.writeId(BuiltInRegistries.SOUND_EVENT.asHolderIdMap(), this.sound, ($$0, $$1) -> $$1.writeToNetwork((FriendlyByteBuf)((Object)$$0)));
        $$02.writeEnum(this.source);
        $$02.writeInt(this.x);
        $$02.writeInt(this.y);
        $$02.writeInt(this.z);
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

    public double getX() {
        return (float)this.x / 8.0f;
    }

    public double getY() {
        return (float)this.y / 8.0f;
    }

    public double getZ() {
        return (float)this.z / 8.0f;
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
        $$0.handleSoundEvent(this);
    }
}