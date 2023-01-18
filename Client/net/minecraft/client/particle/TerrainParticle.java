/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class TerrainParticle
extends TextureSheetParticle {
    private final BlockPos pos;
    private final float uo;
    private final float vo;

    public TerrainParticle(ClientLevel $$0, double $$1, double $$2, double $$3, double $$4, double $$5, double $$6, BlockState $$7) {
        this($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, new BlockPos($$1, $$2, $$3));
    }

    public TerrainParticle(ClientLevel $$0, double $$1, double $$2, double $$3, double $$4, double $$5, double $$6, BlockState $$7, BlockPos $$8) {
        super($$0, $$1, $$2, $$3, $$4, $$5, $$6);
        this.pos = $$8;
        this.setSprite(Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getParticleIcon($$7));
        this.gravity = 1.0f;
        this.rCol = 0.6f;
        this.gCol = 0.6f;
        this.bCol = 0.6f;
        if (!$$7.is(Blocks.GRASS_BLOCK)) {
            int $$9 = Minecraft.getInstance().getBlockColors().getColor($$7, $$0, $$8, 0);
            this.rCol *= (float)($$9 >> 16 & 0xFF) / 255.0f;
            this.gCol *= (float)($$9 >> 8 & 0xFF) / 255.0f;
            this.bCol *= (float)($$9 & 0xFF) / 255.0f;
        }
        this.quadSize /= 2.0f;
        this.uo = this.random.nextFloat() * 3.0f;
        this.vo = this.random.nextFloat() * 3.0f;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.TERRAIN_SHEET;
    }

    @Override
    protected float getU0() {
        return this.sprite.getU((this.uo + 1.0f) / 4.0f * 16.0f);
    }

    @Override
    protected float getU1() {
        return this.sprite.getU(this.uo / 4.0f * 16.0f);
    }

    @Override
    protected float getV0() {
        return this.sprite.getV(this.vo / 4.0f * 16.0f);
    }

    @Override
    protected float getV1() {
        return this.sprite.getV((this.vo + 1.0f) / 4.0f * 16.0f);
    }

    @Override
    public int getLightColor(float $$0) {
        int $$1 = super.getLightColor($$0);
        if ($$1 == 0 && this.level.hasChunkAt(this.pos)) {
            return LevelRenderer.getLightColor(this.level, this.pos);
        }
        return $$1;
    }

    public static class Provider
    implements ParticleProvider<BlockParticleOption> {
        @Override
        public Particle createParticle(BlockParticleOption $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            BlockState $$8 = $$0.getState();
            if ($$8.isAir() || $$8.is(Blocks.MOVING_PISTON)) {
                return null;
            }
            return new TerrainParticle($$1, $$2, $$3, $$4, $$5, $$6, $$7, $$8);
        }
    }
}