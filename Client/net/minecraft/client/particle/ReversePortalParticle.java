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
import net.minecraft.client.particle.PortalParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;

public class ReversePortalParticle
extends PortalParticle {
    ReversePortalParticle(ClientLevel $$0, double $$1, double $$2, double $$3, double $$4, double $$5, double $$6) {
        super($$0, $$1, $$2, $$3, $$4, $$5, $$6);
        this.quadSize *= 1.5f;
        this.lifetime = (int)(Math.random() * 2.0) + 60;
    }

    @Override
    public float getQuadSize(float $$0) {
        float $$1 = 1.0f - ((float)this.age + $$0) / ((float)this.lifetime * 1.5f);
        return this.quadSize * $$1;
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
        float $$0 = (float)this.age / (float)this.lifetime;
        this.x += this.xd * (double)$$0;
        this.y += this.yd * (double)$$0;
        this.z += this.zd * (double)$$0;
    }

    public static class ReversePortalProvider
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public ReversePortalProvider(SpriteSet $$0) {
            this.sprite = $$0;
        }

        @Override
        public Particle createParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            ReversePortalParticle $$8 = new ReversePortalParticle($$1, $$2, $$3, $$4, $$5, $$6, $$7);
            $$8.pickSprite(this.sprite);
            return $$8;
        }
    }
}