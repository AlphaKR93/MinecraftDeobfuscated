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

public class DragonBreathParticle
extends TextureSheetParticle {
    private static final int COLOR_MIN = 11993298;
    private static final int COLOR_MAX = 14614777;
    private static final float COLOR_MIN_RED = 0.7176471f;
    private static final float COLOR_MIN_GREEN = 0.0f;
    private static final float COLOR_MIN_BLUE = 0.8235294f;
    private static final float COLOR_MAX_RED = 0.8745098f;
    private static final float COLOR_MAX_GREEN = 0.0f;
    private static final float COLOR_MAX_BLUE = 0.9764706f;
    private boolean hasHitGround;
    private final SpriteSet sprites;

    DragonBreathParticle(ClientLevel $$0, double $$1, double $$2, double $$3, double $$4, double $$5, double $$6, SpriteSet $$7) {
        super($$0, $$1, $$2, $$3);
        this.friction = 0.96f;
        this.xd = $$4;
        this.yd = $$5;
        this.zd = $$6;
        this.rCol = Mth.nextFloat(this.random, 0.7176471f, 0.8745098f);
        this.gCol = Mth.nextFloat(this.random, 0.0f, 0.0f);
        this.bCol = Mth.nextFloat(this.random, 0.8235294f, 0.9764706f);
        this.quadSize *= 0.75f;
        this.lifetime = (int)(20.0 / ((double)this.random.nextFloat() * 0.8 + 0.2));
        this.hasHitGround = false;
        this.hasPhysics = false;
        this.sprites = $$7;
        this.setSpriteFromAge($$7);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }
        this.setSpriteFromAge(this.sprites);
        if (this.onGround) {
            this.yd = 0.0;
            this.hasHitGround = true;
        }
        if (this.hasHitGround) {
            this.yd += 0.002;
        }
        this.move(this.xd, this.yd, this.zd);
        if (this.y == this.yo) {
            this.xd *= 1.1;
            this.zd *= 1.1;
        }
        this.xd *= (double)this.friction;
        this.zd *= (double)this.friction;
        if (this.hasHitGround) {
            this.yd *= (double)this.friction;
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public float getQuadSize(float $$0) {
        return this.quadSize * Mth.clamp(((float)this.age + $$0) / (float)this.lifetime * 32.0f, 0.0f, 1.0f);
    }

    public static class Provider
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet $$0) {
            this.sprites = $$0;
        }

        @Override
        public Particle createParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            return new DragonBreathParticle($$1, $$2, $$3, $$4, $$5, $$6, $$7, this.sprites);
        }
    }
}