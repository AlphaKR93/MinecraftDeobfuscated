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

public class HugeExplosionParticle
extends TextureSheetParticle {
    private final SpriteSet sprites;

    protected HugeExplosionParticle(ClientLevel $$0, double $$1, double $$2, double $$3, double $$4, SpriteSet $$5) {
        super($$0, $$1, $$2, $$3, 0.0, 0.0, 0.0);
        float $$6;
        this.lifetime = 6 + this.random.nextInt(4);
        this.rCol = $$6 = this.random.nextFloat() * 0.6f + 0.4f;
        this.gCol = $$6;
        this.bCol = $$6;
        this.quadSize = 2.0f * (1.0f - (float)$$4 * 0.5f);
        this.sprites = $$5;
        this.setSpriteFromAge($$5);
    }

    @Override
    public int getLightColor(float $$0) {
        return 0xF000F0;
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
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }

    public static class Provider
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet $$0) {
            this.sprites = $$0;
        }

        @Override
        public Particle createParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            return new HugeExplosionParticle($$1, $$2, $$3, $$4, $$5, this.sprites);
        }
    }
}