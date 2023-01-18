/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.BaseAshSmokeParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

public class WhiteAshParticle
extends BaseAshSmokeParticle {
    private static final int COLOR_RGB24 = 12235202;

    protected WhiteAshParticle(ClientLevel $$0, double $$1, double $$2, double $$3, double $$4, double $$5, double $$6, float $$7, SpriteSet $$8) {
        super($$0, $$1, $$2, $$3, 0.1f, -0.1f, 0.1f, $$4, $$5, $$6, $$7, $$8, 0.0f, 20, 0.0125f, false);
        this.rCol = 0.7294118f;
        this.gCol = 0.69411767f;
        this.bCol = 0.7607843f;
    }

    public static class Provider
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet $$0) {
            this.sprites = $$0;
        }

        @Override
        public Particle createParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            RandomSource $$8 = $$1.random;
            double $$9 = (double)$$8.nextFloat() * -1.9 * (double)$$8.nextFloat() * 0.1;
            double $$10 = (double)$$8.nextFloat() * -0.5 * (double)$$8.nextFloat() * 0.1 * 5.0;
            double $$11 = (double)$$8.nextFloat() * -1.9 * (double)$$8.nextFloat() * 0.1;
            return new WhiteAshParticle($$1, $$2, $$3, $$4, $$9, $$10, $$11, 1.0f, this.sprites);
        }
    }
}