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
import net.minecraft.util.Mth;

public class BaseAshSmokeParticle
extends TextureSheetParticle {
    private final SpriteSet sprites;

    protected BaseAshSmokeParticle(ClientLevel $$0, double $$1, double $$2, double $$3, float $$4, float $$5, float $$6, double $$7, double $$8, double $$9, float $$10, SpriteSet $$11, float $$12, int $$13, float $$14, boolean $$15) {
        super($$0, $$1, $$2, $$3, 0.0, 0.0, 0.0);
        float $$16;
        this.friction = 0.96f;
        this.gravity = $$14;
        this.speedUpWhenYMotionIsBlocked = true;
        this.sprites = $$11;
        this.xd *= (double)$$4;
        this.yd *= (double)$$5;
        this.zd *= (double)$$6;
        this.xd += $$7;
        this.yd += $$8;
        this.zd += $$9;
        this.rCol = $$16 = $$0.random.nextFloat() * $$12;
        this.gCol = $$16;
        this.bCol = $$16;
        this.quadSize *= 0.75f * $$10;
        this.lifetime = (int)((double)$$13 / ((double)$$0.random.nextFloat() * 0.8 + 0.2) * (double)$$10);
        this.lifetime = Math.max((int)this.lifetime, (int)1);
        this.setSpriteFromAge($$11);
        this.hasPhysics = $$15;
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