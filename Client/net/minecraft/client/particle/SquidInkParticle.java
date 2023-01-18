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
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.FastColor;

public class SquidInkParticle
extends SimpleAnimatedParticle {
    SquidInkParticle(ClientLevel $$0, double $$1, double $$2, double $$3, double $$4, double $$5, double $$6, int $$7, SpriteSet $$8) {
        super($$0, $$1, $$2, $$3, $$8, 0.0f);
        this.friction = 0.92f;
        this.quadSize = 0.5f;
        this.setAlpha(1.0f);
        this.setColor(FastColor.ARGB32.red($$7), FastColor.ARGB32.green($$7), FastColor.ARGB32.blue($$7));
        this.lifetime = (int)((double)(this.quadSize * 12.0f) / (Math.random() * (double)0.8f + (double)0.2f));
        this.setSpriteFromAge($$8);
        this.hasPhysics = false;
        this.xd = $$4;
        this.yd = $$5;
        this.zd = $$6;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.removed) {
            this.setSpriteFromAge(this.sprites);
            if (this.age > this.lifetime / 2) {
                this.setAlpha(1.0f - ((float)this.age - (float)(this.lifetime / 2)) / (float)this.lifetime);
            }
            if (this.level.getBlockState(new BlockPos(this.x, this.y, this.z)).isAir()) {
                this.yd -= (double)0.0074f;
            }
        }
    }

    public static class GlowInkProvider
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public GlowInkProvider(SpriteSet $$0) {
            this.sprites = $$0;
        }

        @Override
        public Particle createParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            return new SquidInkParticle($$1, $$2, $$3, $$4, $$5, $$6, $$7, FastColor.ARGB32.color(255, 204, 31, 102), this.sprites);
        }
    }

    public static class Provider
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet $$0) {
            this.sprites = $$0;
        }

        @Override
        public Particle createParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            return new SquidInkParticle($$1, $$2, $$3, $$4, $$5, $$6, $$7, FastColor.ARGB32.color(255, 255, 255, 255), this.sprites);
        }
    }
}