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
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;

public class LavaParticle
extends TextureSheetParticle {
    LavaParticle(ClientLevel $$0, double $$1, double $$2, double $$3) {
        super($$0, $$1, $$2, $$3, 0.0, 0.0, 0.0);
        this.gravity = 0.75f;
        this.friction = 0.999f;
        this.xd *= (double)0.8f;
        this.yd *= (double)0.8f;
        this.zd *= (double)0.8f;
        this.yd = this.random.nextFloat() * 0.4f + 0.05f;
        this.quadSize *= this.random.nextFloat() * 2.0f + 0.2f;
        this.lifetime = (int)(16.0 / (Math.random() * 0.8 + 0.2));
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public int getLightColor(float $$0) {
        int $$1 = super.getLightColor($$0);
        int $$2 = 240;
        int $$3 = $$1 >> 16 & 0xFF;
        return 0xF0 | $$3 << 16;
    }

    @Override
    public float getQuadSize(float $$0) {
        float $$1 = ((float)this.age + $$0) / (float)this.lifetime;
        return this.quadSize * (1.0f - $$1 * $$1);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.removed) {
            float $$0 = (float)this.age / (float)this.lifetime;
            if (this.random.nextFloat() > $$0) {
                this.level.addParticle(ParticleTypes.SMOKE, this.x, this.y, this.z, this.xd, this.yd, this.zd);
            }
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
            LavaParticle $$8 = new LavaParticle($$1, $$2, $$3, $$4);
            $$8.pickSprite(this.sprite);
            return $$8;
        }
    }
}