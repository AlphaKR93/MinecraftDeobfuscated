/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class GlowParticle
extends TextureSheetParticle {
    static final RandomSource RANDOM = RandomSource.create();
    private final SpriteSet sprites;

    GlowParticle(ClientLevel $$0, double $$1, double $$2, double $$3, double $$4, double $$5, double $$6, SpriteSet $$7) {
        super($$0, $$1, $$2, $$3, $$4, $$5, $$6);
        this.friction = 0.96f;
        this.speedUpWhenYMotionIsBlocked = true;
        this.sprites = $$7;
        this.quadSize *= 0.75f;
        this.hasPhysics = false;
        this.setSpriteFromAge($$7);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public int getLightColor(float $$0) {
        float $$1 = ((float)this.age + $$0) / (float)this.lifetime;
        $$1 = Mth.clamp($$1, 0.0f, 1.0f);
        int $$2 = super.getLightColor($$0);
        int $$3 = $$2 & 0xFF;
        int $$4 = $$2 >> 16 & 0xFF;
        if (($$3 += (int)($$1 * 15.0f * 16.0f)) > 240) {
            $$3 = 240;
        }
        return $$3 | $$4 << 16;
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);
    }

    public static class ScrapeProvider
    implements ParticleProvider<SimpleParticleType> {
        private final double SPEED_FACTOR = 0.01;
        private final SpriteSet sprite;

        public ScrapeProvider(SpriteSet $$0) {
            this.sprite = $$0;
        }

        @Override
        public Particle createParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            GlowParticle $$8 = new GlowParticle($$1, $$2, $$3, $$4, 0.0, 0.0, 0.0, this.sprite);
            if ($$1.random.nextBoolean()) {
                $$8.setColor(0.29f, 0.58f, 0.51f);
            } else {
                $$8.setColor(0.43f, 0.77f, 0.62f);
            }
            $$8.setParticleSpeed($$5 * 0.01, $$6 * 0.01, $$7 * 0.01);
            int $$9 = 10;
            int $$10 = 40;
            $$8.setLifetime($$1.random.nextInt(30) + 10);
            return $$8;
        }
    }

    public static class ElectricSparkProvider
    implements ParticleProvider<SimpleParticleType> {
        private final double SPEED_FACTOR = 0.25;
        private final SpriteSet sprite;

        public ElectricSparkProvider(SpriteSet $$0) {
            this.sprite = $$0;
        }

        @Override
        public Particle createParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            GlowParticle $$8 = new GlowParticle($$1, $$2, $$3, $$4, 0.0, 0.0, 0.0, this.sprite);
            $$8.setColor(1.0f, 0.9f, 1.0f);
            $$8.setParticleSpeed($$5 * 0.25, $$6 * 0.25, $$7 * 0.25);
            int $$9 = 2;
            int $$10 = 4;
            $$8.setLifetime($$1.random.nextInt(2) + 2);
            return $$8;
        }
    }

    public static class WaxOffProvider
    implements ParticleProvider<SimpleParticleType> {
        private final double SPEED_FACTOR = 0.01;
        private final SpriteSet sprite;

        public WaxOffProvider(SpriteSet $$0) {
            this.sprite = $$0;
        }

        @Override
        public Particle createParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            GlowParticle $$8 = new GlowParticle($$1, $$2, $$3, $$4, 0.0, 0.0, 0.0, this.sprite);
            $$8.setColor(1.0f, 0.9f, 1.0f);
            $$8.setParticleSpeed($$5 * 0.01 / 2.0, $$6 * 0.01, $$7 * 0.01 / 2.0);
            int $$9 = 10;
            int $$10 = 40;
            $$8.setLifetime($$1.random.nextInt(30) + 10);
            return $$8;
        }
    }

    public static class WaxOnProvider
    implements ParticleProvider<SimpleParticleType> {
        private final double SPEED_FACTOR = 0.01;
        private final SpriteSet sprite;

        public WaxOnProvider(SpriteSet $$0) {
            this.sprite = $$0;
        }

        @Override
        public Particle createParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            GlowParticle $$8 = new GlowParticle($$1, $$2, $$3, $$4, 0.0, 0.0, 0.0, this.sprite);
            $$8.setColor(0.91f, 0.55f, 0.08f);
            $$8.setParticleSpeed($$5 * 0.01 / 2.0, $$6 * 0.01, $$7 * 0.01 / 2.0);
            int $$9 = 10;
            int $$10 = 40;
            $$8.setLifetime($$1.random.nextInt(30) + 10);
            return $$8;
        }
    }

    public static class GlowSquidProvider
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public GlowSquidProvider(SpriteSet $$0) {
            this.sprite = $$0;
        }

        @Override
        public Particle createParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            GlowParticle $$8 = new GlowParticle($$1, $$2, $$3, $$4, 0.5 - RANDOM.nextDouble(), $$6, 0.5 - RANDOM.nextDouble(), this.sprite);
            if ($$1.random.nextBoolean()) {
                $$8.setColor(0.6f, 1.0f, 0.8f);
            } else {
                $$8.setColor(0.08f, 0.4f, 0.4f);
            }
            $$8.yd *= (double)0.2f;
            if ($$5 == 0.0 && $$7 == 0.0) {
                $$8.xd *= (double)0.1f;
                $$8.zd *= (double)0.1f;
            }
            $$8.setLifetime((int)(8.0 / ($$1.random.nextDouble() * 0.8 + 0.2)));
            return $$8;
        }
    }
}