/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Optional
 */
package net.minecraft.client.particle;

import java.util.Optional;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.ParticleGroup;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class SuspendedParticle
extends TextureSheetParticle {
    SuspendedParticle(ClientLevel $$0, SpriteSet $$1, double $$2, double $$3, double $$4) {
        super($$0, $$2, $$3 - 0.125, $$4);
        this.setSize(0.01f, 0.01f);
        this.pickSprite($$1);
        this.quadSize *= this.random.nextFloat() * 0.6f + 0.2f;
        this.lifetime = (int)(16.0 / (Math.random() * 0.8 + 0.2));
        this.hasPhysics = false;
        this.friction = 1.0f;
        this.gravity = 0.0f;
    }

    SuspendedParticle(ClientLevel $$0, SpriteSet $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
        super($$0, $$2, $$3 - 0.125, $$4, $$5, $$6, $$7);
        this.setSize(0.01f, 0.01f);
        this.pickSprite($$1);
        this.quadSize *= this.random.nextFloat() * 0.6f + 0.6f;
        this.lifetime = (int)(16.0 / (Math.random() * 0.8 + 0.2));
        this.hasPhysics = false;
        this.friction = 1.0f;
        this.gravity = 0.0f;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public static class WarpedSporeProvider
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public WarpedSporeProvider(SpriteSet $$0) {
            this.sprite = $$0;
        }

        @Override
        public Particle createParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            double $$8 = (double)$$1.random.nextFloat() * -1.9 * (double)$$1.random.nextFloat() * 0.1;
            SuspendedParticle $$9 = new SuspendedParticle($$1, this.sprite, $$2, $$3, $$4, 0.0, $$8, 0.0);
            $$9.setColor(0.1f, 0.1f, 0.3f);
            $$9.setSize(0.001f, 0.001f);
            return $$9;
        }
    }

    public static class CrimsonSporeProvider
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public CrimsonSporeProvider(SpriteSet $$0) {
            this.sprite = $$0;
        }

        @Override
        public Particle createParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            RandomSource $$8 = $$1.random;
            double $$9 = $$8.nextGaussian() * (double)1.0E-6f;
            double $$10 = $$8.nextGaussian() * (double)1.0E-4f;
            double $$11 = $$8.nextGaussian() * (double)1.0E-6f;
            SuspendedParticle $$12 = new SuspendedParticle($$1, this.sprite, $$2, $$3, $$4, $$9, $$10, $$11);
            $$12.setColor(0.9f, 0.4f, 0.5f);
            return $$12;
        }
    }

    public static class SporeBlossomAirProvider
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public SporeBlossomAirProvider(SpriteSet $$0) {
            this.sprite = $$0;
        }

        @Override
        public Particle createParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            SuspendedParticle $$8 = new SuspendedParticle($$1, this.sprite, $$2, $$3, $$4, 0.0, -0.8f, 0.0){

                @Override
                public Optional<ParticleGroup> getParticleGroup() {
                    return Optional.of((Object)ParticleGroup.SPORE_BLOSSOM);
                }
            };
            $$8.lifetime = Mth.randomBetweenInclusive($$1.random, 500, 1000);
            $$8.gravity = 0.01f;
            $$8.setColor(0.32f, 0.5f, 0.22f);
            return $$8;
        }
    }

    public static class UnderwaterProvider
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public UnderwaterProvider(SpriteSet $$0) {
            this.sprite = $$0;
        }

        @Override
        public Particle createParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            SuspendedParticle $$8 = new SuspendedParticle($$1, this.sprite, $$2, $$3, $$4);
            $$8.setColor(0.4f, 0.4f, 0.7f);
            return $$8;
        }
    }
}