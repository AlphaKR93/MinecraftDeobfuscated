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
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.DustParticleOptionsBase;
import net.minecraft.util.Mth;

public class DustParticleBase<T extends DustParticleOptionsBase>
extends TextureSheetParticle {
    private final SpriteSet sprites;

    protected DustParticleBase(ClientLevel $$0, double $$1, double $$2, double $$3, double $$4, double $$5, double $$6, T $$7, SpriteSet $$8) {
        super($$0, $$1, $$2, $$3, $$4, $$5, $$6);
        this.friction = 0.96f;
        this.speedUpWhenYMotionIsBlocked = true;
        this.sprites = $$8;
        this.xd *= (double)0.1f;
        this.yd *= (double)0.1f;
        this.zd *= (double)0.1f;
        float $$9 = this.random.nextFloat() * 0.4f + 0.6f;
        this.rCol = this.randomizeColor(((DustParticleOptionsBase)$$7).getColor().x(), $$9);
        this.gCol = this.randomizeColor(((DustParticleOptionsBase)$$7).getColor().y(), $$9);
        this.bCol = this.randomizeColor(((DustParticleOptionsBase)$$7).getColor().z(), $$9);
        this.quadSize *= 0.75f * ((DustParticleOptionsBase)$$7).getScale();
        int $$10 = (int)(8.0 / (this.random.nextDouble() * 0.8 + 0.2));
        this.lifetime = (int)Math.max((float)((float)$$10 * ((DustParticleOptionsBase)$$7).getScale()), (float)1.0f);
        this.setSpriteFromAge($$8);
    }

    protected float randomizeColor(float $$0, float $$1) {
        return (this.random.nextFloat() * 0.2f + 0.8f) * $$0 * $$1;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public float getQuadSize(float $$0) {
        return this.quadSize * Mth.clamp(((float)this.age + $$0) / (float)this.lifetime * 32.0f, 0.0f, 1.0f);
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);
    }
}