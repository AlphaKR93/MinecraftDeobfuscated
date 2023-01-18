/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;

public class SimpleAnimatedParticle
extends TextureSheetParticle {
    protected final SpriteSet sprites;
    private float fadeR;
    private float fadeG;
    private float fadeB;
    private boolean hasFade;

    protected SimpleAnimatedParticle(ClientLevel $$0, double $$1, double $$2, double $$3, SpriteSet $$4, float $$5) {
        super($$0, $$1, $$2, $$3);
        this.friction = 0.91f;
        this.gravity = $$5;
        this.sprites = $$4;
    }

    public void setColor(int $$0) {
        float $$1 = (float)(($$0 & 0xFF0000) >> 16) / 255.0f;
        float $$2 = (float)(($$0 & 0xFF00) >> 8) / 255.0f;
        float $$3 = (float)(($$0 & 0xFF) >> 0) / 255.0f;
        float $$4 = 1.0f;
        this.setColor($$1 * 1.0f, $$2 * 1.0f, $$3 * 1.0f);
    }

    public void setFadeColor(int $$0) {
        this.fadeR = (float)(($$0 & 0xFF0000) >> 16) / 255.0f;
        this.fadeG = (float)(($$0 & 0xFF00) >> 8) / 255.0f;
        this.fadeB = (float)(($$0 & 0xFF) >> 0) / 255.0f;
        this.hasFade = true;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);
        if (this.age > this.lifetime / 2) {
            this.setAlpha(1.0f - ((float)this.age - (float)(this.lifetime / 2)) / (float)this.lifetime);
            if (this.hasFade) {
                this.rCol += (this.fadeR - this.rCol) * 0.2f;
                this.gCol += (this.fadeG - this.gCol) * 0.2f;
                this.bCol += (this.fadeB - this.bCol) * 0.2f;
            }
        }
    }

    @Override
    public int getLightColor(float $$0) {
        return 0xF000F0;
    }
}