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
import net.minecraft.core.particles.SculkChargeParticleOptions;

public class SculkChargeParticle
extends TextureSheetParticle {
    private final SpriteSet sprites;

    SculkChargeParticle(ClientLevel $$0, double $$1, double $$2, double $$3, double $$4, double $$5, double $$6, SpriteSet $$7) {
        super($$0, $$1, $$2, $$3, $$4, $$5, $$6);
        this.friction = 0.96f;
        this.sprites = $$7;
        this.scale(1.5f);
        this.hasPhysics = false;
        this.setSpriteFromAge($$7);
    }

    @Override
    public int getLightColor(float $$0) {
        return 240;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);
    }

    public record Provider(SpriteSet sprite) implements ParticleProvider<SculkChargeParticleOptions>
    {
        @Override
        public Particle createParticle(SculkChargeParticleOptions $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            SculkChargeParticle $$8 = new SculkChargeParticle($$1, $$2, $$3, $$4, $$5, $$6, $$7, this.sprite);
            $$8.setAlpha(1.0f);
            $$8.setParticleSpeed($$5, $$6, $$7);
            $$8.oRoll = $$0.roll();
            $$8.roll = $$0.roll();
            $$8.setLifetime($$1.random.nextInt(12) + 8);
            return $$8;
        }
    }
}