/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;

public class HugeExplosionSeedParticle
extends NoRenderParticle {
    private int life;
    private final int lifeTime;

    HugeExplosionSeedParticle(ClientLevel $$0, double $$1, double $$2, double $$3) {
        super($$0, $$1, $$2, $$3, 0.0, 0.0, 0.0);
        this.lifeTime = 8;
    }

    @Override
    public void tick() {
        for (int $$0 = 0; $$0 < 6; ++$$0) {
            double $$1 = this.x + (this.random.nextDouble() - this.random.nextDouble()) * 4.0;
            double $$2 = this.y + (this.random.nextDouble() - this.random.nextDouble()) * 4.0;
            double $$3 = this.z + (this.random.nextDouble() - this.random.nextDouble()) * 4.0;
            this.level.addParticle(ParticleTypes.EXPLOSION, $$1, $$2, $$3, (float)this.life / (float)this.lifeTime, 0.0, 0.0);
        }
        ++this.life;
        if (this.life == this.lifeTime) {
            this.remove();
        }
    }

    public static class Provider
    implements ParticleProvider<SimpleParticleType> {
        @Override
        public Particle createParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            return new HugeExplosionSeedParticle($$1, $$2, $$3, $$4);
        }
    }
}