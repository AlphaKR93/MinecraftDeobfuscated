/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.client.particle;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

public class FallingDustParticle
extends TextureSheetParticle {
    private final float rotSpeed;
    private final SpriteSet sprites;

    FallingDustParticle(ClientLevel $$0, double $$1, double $$2, double $$3, float $$4, float $$5, float $$6, SpriteSet $$7) {
        super($$0, $$1, $$2, $$3);
        this.sprites = $$7;
        this.rCol = $$4;
        this.gCol = $$5;
        this.bCol = $$6;
        float $$8 = 0.9f;
        this.quadSize *= 0.67499995f;
        int $$9 = (int)(32.0 / (Math.random() * 0.8 + 0.2));
        this.lifetime = (int)Math.max((float)((float)$$9 * 0.9f), (float)1.0f);
        this.setSpriteFromAge($$7);
        this.rotSpeed = ((float)Math.random() - 0.5f) * 0.1f;
        this.roll = (float)Math.random() * ((float)Math.PI * 2);
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
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }
        this.setSpriteFromAge(this.sprites);
        this.oRoll = this.roll;
        this.roll += (float)Math.PI * this.rotSpeed * 2.0f;
        if (this.onGround) {
            this.roll = 0.0f;
            this.oRoll = 0.0f;
        }
        this.move(this.xd, this.yd, this.zd);
        this.yd -= (double)0.003f;
        this.yd = Math.max((double)this.yd, (double)-0.14f);
    }

    public static class Provider
    implements ParticleProvider<BlockParticleOption> {
        private final SpriteSet sprite;

        public Provider(SpriteSet $$0) {
            this.sprite = $$0;
        }

        @Override
        @Nullable
        public Particle createParticle(BlockParticleOption $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            BlockState $$8 = $$0.getState();
            if (!$$8.isAir() && $$8.getRenderShape() == RenderShape.INVISIBLE) {
                return null;
            }
            BlockPos $$9 = new BlockPos($$2, $$3, $$4);
            int $$10 = Minecraft.getInstance().getBlockColors().getColor($$8, $$1, $$9);
            if ($$8.getBlock() instanceof FallingBlock) {
                $$10 = ((FallingBlock)$$8.getBlock()).getDustColor($$8, $$1, $$9);
            }
            float $$11 = (float)($$10 >> 16 & 0xFF) / 255.0f;
            float $$12 = (float)($$10 >> 8 & 0xFF) / 255.0f;
            float $$13 = (float)($$10 & 0xFF) / 255.0f;
            return new FallingDustParticle($$1, $$2, $$3, $$4, $$11, $$12, $$13, this.sprite);
        }
    }
}