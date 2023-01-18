/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.network.protocol.game;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class ClientboundLevelParticlesPacket
implements Packet<ClientGamePacketListener> {
    private final double x;
    private final double y;
    private final double z;
    private final float xDist;
    private final float yDist;
    private final float zDist;
    private final float maxSpeed;
    private final int count;
    private final boolean overrideLimiter;
    private final ParticleOptions particle;

    public <T extends ParticleOptions> ClientboundLevelParticlesPacket(T $$0, boolean $$1, double $$2, double $$3, double $$4, float $$5, float $$6, float $$7, float $$8, int $$9) {
        this.particle = $$0;
        this.overrideLimiter = $$1;
        this.x = $$2;
        this.y = $$3;
        this.z = $$4;
        this.xDist = $$5;
        this.yDist = $$6;
        this.zDist = $$7;
        this.maxSpeed = $$8;
        this.count = $$9;
    }

    public ClientboundLevelParticlesPacket(FriendlyByteBuf $$0) {
        ParticleType<?> $$1 = $$0.readById(BuiltInRegistries.PARTICLE_TYPE);
        this.overrideLimiter = $$0.readBoolean();
        this.x = $$0.readDouble();
        this.y = $$0.readDouble();
        this.z = $$0.readDouble();
        this.xDist = $$0.readFloat();
        this.yDist = $$0.readFloat();
        this.zDist = $$0.readFloat();
        this.maxSpeed = $$0.readFloat();
        this.count = $$0.readInt();
        this.particle = this.readParticle($$0, $$1);
    }

    private <T extends ParticleOptions> T readParticle(FriendlyByteBuf $$0, ParticleType<T> $$1) {
        return $$1.getDeserializer().fromNetwork($$1, $$0);
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeId(BuiltInRegistries.PARTICLE_TYPE, this.particle.getType());
        $$0.writeBoolean(this.overrideLimiter);
        $$0.writeDouble(this.x);
        $$0.writeDouble(this.y);
        $$0.writeDouble(this.z);
        $$0.writeFloat(this.xDist);
        $$0.writeFloat(this.yDist);
        $$0.writeFloat(this.zDist);
        $$0.writeFloat(this.maxSpeed);
        $$0.writeInt(this.count);
        this.particle.writeToNetwork($$0);
    }

    public boolean isOverrideLimiter() {
        return this.overrideLimiter;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public float getXDist() {
        return this.xDist;
    }

    public float getYDist() {
        return this.yDist;
    }

    public float getZDist() {
        return this.zDist;
    }

    public float getMaxSpeed() {
        return this.maxSpeed;
    }

    public int getCount() {
        return this.count;
    }

    public ParticleOptions getParticle() {
        return this.particle;
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleParticleEvent(this);
    }
}