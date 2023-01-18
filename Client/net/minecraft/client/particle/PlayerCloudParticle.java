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
import net.minecraft.world.entity.player.Player;

public class PlayerCloudParticle
extends TextureSheetParticle {
    private final SpriteSet sprites;

    PlayerCloudParticle(ClientLevel $$0, double $$1, double $$2, double $$3, double $$4, double $$5, double $$6, SpriteSet $$7) {
        super($$0, $$1, $$2, $$3, 0.0, 0.0, 0.0);
        float $$9;
        this.friction = 0.96f;
        this.sprites = $$7;
        float $$8 = 2.5f;
        this.xd *= (double)0.1f;
        this.yd *= (double)0.1f;
        this.zd *= (double)0.1f;
        this.xd += $$4;
        this.yd += $$5;
        this.zd += $$6;
        this.rCol = $$9 = 1.0f - (float)(Math.random() * (double)0.3f);
        this.gCol = $$9;
        this.bCol = $$9;
        this.quadSize *= 1.875f;
        int $$10 = (int)(8.0 / (Math.random() * 0.8 + 0.3));
        this.lifetime = (int)Math.max((float)((float)$$10 * 2.5f), (float)1.0f);
        this.hasPhysics = false;
        this.setSpriteFromAge($$7);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public float getQuadSize(float $$0) {
        return this.quadSize * Mth.clamp(((float)this.age + $$0) / (float)this.lifetime * 32.0f, 0.0f, 1.0f);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.removed) {
            double $$1;
            this.setSpriteFromAge(this.sprites);
            Player $$0 = this.level.getNearestPlayer(this.x, this.y, this.z, 2.0, false);
            if ($$0 != null && this.y > ($$1 = $$0.getY())) {
                this.y += ($$1 - this.y) * 0.2;
                this.yd += ($$0.getDeltaMovement().y - this.yd) * 0.2;
                this.setPos(this.x, this.y, this.z);
            }
        }
    }

    public static class SneezeProvider
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public SneezeProvider(SpriteSet $$0) {
            this.sprites = $$0;
        }

        @Override
        public Particle createParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            PlayerCloudParticle $$8 = new PlayerCloudParticle($$1, $$2, $$3, $$4, $$5, $$6, $$7, this.sprites);
            $$8.setColor(200.0f, 50.0f, 120.0f);
            $$8.setAlpha(0.4f);
            return $$8;
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
            return new PlayerCloudParticle($$1, $$2, $$3, $$4, $$5, $$6, $$7, this.sprites);
        }
    }
}