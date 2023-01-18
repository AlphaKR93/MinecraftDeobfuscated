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

public class NoteParticle
extends TextureSheetParticle {
    NoteParticle(ClientLevel $$0, double $$1, double $$2, double $$3, double $$4) {
        super($$0, $$1, $$2, $$3, 0.0, 0.0, 0.0);
        this.friction = 0.66f;
        this.speedUpWhenYMotionIsBlocked = true;
        this.xd *= (double)0.01f;
        this.yd *= (double)0.01f;
        this.zd *= (double)0.01f;
        this.yd += 0.2;
        this.rCol = Math.max((float)0.0f, (float)(Mth.sin(((float)$$4 + 0.0f) * ((float)Math.PI * 2)) * 0.65f + 0.35f));
        this.gCol = Math.max((float)0.0f, (float)(Mth.sin(((float)$$4 + 0.33333334f) * ((float)Math.PI * 2)) * 0.65f + 0.35f));
        this.bCol = Math.max((float)0.0f, (float)(Mth.sin(((float)$$4 + 0.6666667f) * ((float)Math.PI * 2)) * 0.65f + 0.35f));
        this.quadSize *= 1.5f;
        this.lifetime = 6;
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
        private final SpriteSet sprite;

        public Provider(SpriteSet $$0) {
            this.sprite = $$0;
        }

        @Override
        public Particle createParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            NoteParticle $$8 = new NoteParticle($$1, $$2, $$3, $$4, $$5);
            $$8.pickSprite(this.sprite);
            return $$8;
        }
    }
}