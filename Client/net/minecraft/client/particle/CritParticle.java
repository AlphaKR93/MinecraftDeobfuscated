/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
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

public class CritParticle
extends TextureSheetParticle {
    CritParticle(ClientLevel $$0, double $$1, double $$2, double $$3, double $$4, double $$5, double $$6) {
        super($$0, $$1, $$2, $$3, 0.0, 0.0, 0.0);
        float $$7;
        this.friction = 0.7f;
        this.gravity = 0.5f;
        this.xd *= (double)0.1f;
        this.yd *= (double)0.1f;
        this.zd *= (double)0.1f;
        this.xd += $$4 * 0.4;
        this.yd += $$5 * 0.4;
        this.zd += $$6 * 0.4;
        this.rCol = $$7 = (float)(Math.random() * (double)0.3f + (double)0.6f);
        this.gCol = $$7;
        this.bCol = $$7;
        this.quadSize *= 0.75f;
        this.lifetime = Math.max((int)((int)(6.0 / (Math.random() * 0.8 + 0.6))), (int)1);
        this.hasPhysics = false;
        this.tick();
    }

    @Override
    public float getQuadSize(float $$0) {
        return this.quadSize * Mth.clamp(((float)this.age + $$0) / (float)this.lifetime * 32.0f, 0.0f, 1.0f);
    }

    @Override
    public void tick() {
        super.tick();
        this.gCol *= 0.96f;
        this.bCol *= 0.9f;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public static class DamageIndicatorProvider
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public DamageIndicatorProvider(SpriteSet $$0) {
            this.sprite = $$0;
        }

        @Override
        public Particle createParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            CritParticle $$8 = new CritParticle($$1, $$2, $$3, $$4, $$5, $$6 + 1.0, $$7);
            $$8.setLifetime(20);
            $$8.pickSprite(this.sprite);
            return $$8;
        }
    }

    public static class MagicProvider
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public MagicProvider(SpriteSet $$0) {
            this.sprite = $$0;
        }

        @Override
        public Particle createParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            CritParticle $$8 = new CritParticle($$1, $$2, $$3, $$4, $$5, $$6, $$7);
            $$8.rCol *= 0.3f;
            $$8.gCol *= 0.8f;
            $$8.pickSprite(this.sprite);
            return $$8;
        }
    }

    public static class Provider
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet $$0) {
            this.sprite = $$0;
        }

        @Override
        public Particle createParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            CritParticle $$8 = new CritParticle($$1, $$2, $$3, $$4, $$5, $$6, $$7);
            $$8.pickSprite(this.sprite);
            return $$8;
        }
    }
}