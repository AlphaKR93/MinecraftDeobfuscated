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
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class DripParticle
extends TextureSheetParticle {
    private final Fluid type;
    protected boolean isGlowing;

    DripParticle(ClientLevel $$0, double $$1, double $$2, double $$3, Fluid $$4) {
        super($$0, $$1, $$2, $$3);
        this.setSize(0.01f, 0.01f);
        this.gravity = 0.06f;
        this.type = $$4;
    }

    protected Fluid getType() {
        return this.type;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public int getLightColor(float $$0) {
        if (this.isGlowing) {
            return 240;
        }
        return super.getLightColor($$0);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.preMoveUpdate();
        if (this.removed) {
            return;
        }
        this.yd -= (double)this.gravity;
        this.move(this.xd, this.yd, this.zd);
        this.postMoveUpdate();
        if (this.removed) {
            return;
        }
        this.xd *= (double)0.98f;
        this.yd *= (double)0.98f;
        this.zd *= (double)0.98f;
        BlockPos $$0 = new BlockPos(this.x, this.y, this.z);
        FluidState $$1 = this.level.getFluidState($$0);
        if ($$1.getType() == this.type && this.y < (double)((float)$$0.getY() + $$1.getHeight(this.level, $$0))) {
            this.remove();
        }
    }

    protected void preMoveUpdate() {
        if (this.lifetime-- <= 0) {
            this.remove();
        }
    }

    protected void postMoveUpdate() {
    }

    public static class ObsidianTearLandProvider
    implements ParticleProvider<SimpleParticleType> {
        protected final SpriteSet sprite;

        public ObsidianTearLandProvider(SpriteSet $$0) {
            this.sprite = $$0;
        }

        @Override
        public Particle createParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            DripLandParticle $$8 = new DripLandParticle($$1, $$2, $$3, $$4, Fluids.EMPTY);
            $$8.isGlowing = true;
            $$8.lifetime = (int)(28.0 / (Math.random() * 0.8 + 0.2));
            $$8.setColor(0.51171875f, 0.03125f, 0.890625f);
            $$8.pickSprite(this.sprite);
            return $$8;
        }
    }

    public static class ObsidianTearFallProvider
    implements ParticleProvider<SimpleParticleType> {
        protected final SpriteSet sprite;

        public ObsidianTearFallProvider(SpriteSet $$0) {
            this.sprite = $$0;
        }

        @Override
        public Particle createParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            FallAndLandParticle $$8 = new FallAndLandParticle($$1, $$2, $$3, $$4, Fluids.EMPTY, ParticleTypes.LANDING_OBSIDIAN_TEAR);
            $$8.isGlowing = true;
            $$8.gravity = 0.01f;
            $$8.setColor(0.51171875f, 0.03125f, 0.890625f);
            $$8.pickSprite(this.sprite);
            return $$8;
        }
    }

    public static class ObsidianTearHangProvider
    implements ParticleProvider<SimpleParticleType> {
        protected final SpriteSet sprite;

        public ObsidianTearHangProvider(SpriteSet $$0) {
            this.sprite = $$0;
        }

        @Override
        public Particle createParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            DripHangParticle $$8 = new DripHangParticle($$1, $$2, $$3, $$4, Fluids.EMPTY, ParticleTypes.FALLING_OBSIDIAN_TEAR);
            $$8.isGlowing = true;
            $$8.gravity *= 0.01f;
            $$8.lifetime = 100;
            $$8.setColor(0.51171875f, 0.03125f, 0.890625f);
            $$8.pickSprite(this.sprite);
            return $$8;
        }
    }

    public static class SporeBlossomFallProvider
    implements ParticleProvider<SimpleParticleType> {
        protected final SpriteSet sprite;
        private final RandomSource random;

        public SporeBlossomFallProvider(SpriteSet $$0) {
            this.sprite = $$0;
            this.random = RandomSource.create();
        }

        @Override
        public Particle createParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            int $$8 = (int)(64.0f / Mth.randomBetween(this.random, 0.1f, 0.9f));
            FallingParticle $$9 = new FallingParticle($$1, $$2, $$3, $$4, Fluids.EMPTY, $$8);
            $$9.gravity = 0.005f;
            $$9.setColor(0.32f, 0.5f, 0.22f);
            $$9.pickSprite(this.sprite);
            return $$9;
        }
    }

    public static class NectarFallProvider
    implements ParticleProvider<SimpleParticleType> {
        protected final SpriteSet sprite;

        public NectarFallProvider(SpriteSet $$0) {
            this.sprite = $$0;
        }

        @Override
        public Particle createParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            FallingParticle $$8 = new FallingParticle($$1, $$2, $$3, $$4, Fluids.EMPTY);
            $$8.lifetime = (int)(16.0 / (Math.random() * 0.8 + 0.2));
            $$8.gravity = 0.007f;
            $$8.setColor(0.92f, 0.782f, 0.72f);
            $$8.pickSprite(this.sprite);
            return $$8;
        }
    }

    public static class DripstoneLavaFallProvider
    implements ParticleProvider<SimpleParticleType> {
        protected final SpriteSet sprite;

        public DripstoneLavaFallProvider(SpriteSet $$0) {
            this.sprite = $$0;
        }

        @Override
        public Particle createParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            DripstoneFallAndLandParticle $$8 = new DripstoneFallAndLandParticle($$1, $$2, $$3, $$4, (Fluid)Fluids.LAVA, ParticleTypes.LANDING_LAVA);
            $$8.setColor(1.0f, 0.2857143f, 0.083333336f);
            $$8.pickSprite(this.sprite);
            return $$8;
        }
    }

    public static class DripstoneLavaHangProvider
    implements ParticleProvider<SimpleParticleType> {
        protected final SpriteSet sprite;

        public DripstoneLavaHangProvider(SpriteSet $$0) {
            this.sprite = $$0;
        }

        @Override
        public Particle createParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            CoolingDripHangParticle $$8 = new CoolingDripHangParticle($$1, $$2, $$3, $$4, Fluids.LAVA, ParticleTypes.FALLING_DRIPSTONE_LAVA);
            $$8.pickSprite(this.sprite);
            return $$8;
        }
    }

    public static class DripstoneWaterFallProvider
    implements ParticleProvider<SimpleParticleType> {
        protected final SpriteSet sprite;

        public DripstoneWaterFallProvider(SpriteSet $$0) {
            this.sprite = $$0;
        }

        @Override
        public Particle createParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            DripstoneFallAndLandParticle $$8 = new DripstoneFallAndLandParticle($$1, $$2, $$3, $$4, (Fluid)Fluids.WATER, ParticleTypes.SPLASH);
            $$8.setColor(0.2f, 0.3f, 1.0f);
            $$8.pickSprite(this.sprite);
            return $$8;
        }
    }

    public static class DripstoneWaterHangProvider
    implements ParticleProvider<SimpleParticleType> {
        protected final SpriteSet sprite;

        public DripstoneWaterHangProvider(SpriteSet $$0) {
            this.sprite = $$0;
        }

        @Override
        public Particle createParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            DripHangParticle $$8 = new DripHangParticle($$1, $$2, $$3, $$4, Fluids.WATER, ParticleTypes.FALLING_DRIPSTONE_WATER);
            $$8.setColor(0.2f, 0.3f, 1.0f);
            $$8.pickSprite(this.sprite);
            return $$8;
        }
    }

    public static class HoneyLandProvider
    implements ParticleProvider<SimpleParticleType> {
        protected final SpriteSet sprite;

        public HoneyLandProvider(SpriteSet $$0) {
            this.sprite = $$0;
        }

        @Override
        public Particle createParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            DripLandParticle $$8 = new DripLandParticle($$1, $$2, $$3, $$4, Fluids.EMPTY);
            $$8.lifetime = (int)(128.0 / (Math.random() * 0.8 + 0.2));
            $$8.setColor(0.522f, 0.408f, 0.082f);
            $$8.pickSprite(this.sprite);
            return $$8;
        }
    }

    public static class HoneyFallProvider
    implements ParticleProvider<SimpleParticleType> {
        protected final SpriteSet sprite;

        public HoneyFallProvider(SpriteSet $$0) {
            this.sprite = $$0;
        }

        @Override
        public Particle createParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            HoneyFallAndLandParticle $$8 = new HoneyFallAndLandParticle($$1, $$2, $$3, $$4, Fluids.EMPTY, ParticleTypes.LANDING_HONEY);
            $$8.gravity = 0.01f;
            $$8.setColor(0.582f, 0.448f, 0.082f);
            $$8.pickSprite(this.sprite);
            return $$8;
        }
    }

    public static class HoneyHangProvider
    implements ParticleProvider<SimpleParticleType> {
        protected final SpriteSet sprite;

        public HoneyHangProvider(SpriteSet $$0) {
            this.sprite = $$0;
        }

        @Override
        public Particle createParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            DripHangParticle $$8 = new DripHangParticle($$1, $$2, $$3, $$4, Fluids.EMPTY, ParticleTypes.FALLING_HONEY);
            $$8.gravity *= 0.01f;
            $$8.lifetime = 100;
            $$8.setColor(0.622f, 0.508f, 0.082f);
            $$8.pickSprite(this.sprite);
            return $$8;
        }
    }

    public static class LavaLandProvider
    implements ParticleProvider<SimpleParticleType> {
        protected final SpriteSet sprite;

        public LavaLandProvider(SpriteSet $$0) {
            this.sprite = $$0;
        }

        @Override
        public Particle createParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            DripLandParticle $$8 = new DripLandParticle($$1, $$2, $$3, $$4, Fluids.LAVA);
            $$8.setColor(1.0f, 0.2857143f, 0.083333336f);
            $$8.pickSprite(this.sprite);
            return $$8;
        }
    }

    public static class LavaFallProvider
    implements ParticleProvider<SimpleParticleType> {
        protected final SpriteSet sprite;

        public LavaFallProvider(SpriteSet $$0) {
            this.sprite = $$0;
        }

        @Override
        public Particle createParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            FallAndLandParticle $$8 = new FallAndLandParticle($$1, $$2, $$3, $$4, (Fluid)Fluids.LAVA, ParticleTypes.LANDING_LAVA);
            $$8.setColor(1.0f, 0.2857143f, 0.083333336f);
            $$8.pickSprite(this.sprite);
            return $$8;
        }
    }

    public static class LavaHangProvider
    implements ParticleProvider<SimpleParticleType> {
        protected final SpriteSet sprite;

        public LavaHangProvider(SpriteSet $$0) {
            this.sprite = $$0;
        }

        @Override
        public Particle createParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            CoolingDripHangParticle $$8 = new CoolingDripHangParticle($$1, $$2, $$3, $$4, Fluids.LAVA, ParticleTypes.FALLING_LAVA);
            $$8.pickSprite(this.sprite);
            return $$8;
        }
    }

    public static class WaterFallProvider
    implements ParticleProvider<SimpleParticleType> {
        protected final SpriteSet sprite;

        public WaterFallProvider(SpriteSet $$0) {
            this.sprite = $$0;
        }

        @Override
        public Particle createParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            FallAndLandParticle $$8 = new FallAndLandParticle($$1, $$2, $$3, $$4, (Fluid)Fluids.WATER, ParticleTypes.SPLASH);
            $$8.setColor(0.2f, 0.3f, 1.0f);
            $$8.pickSprite(this.sprite);
            return $$8;
        }
    }

    public static class WaterHangProvider
    implements ParticleProvider<SimpleParticleType> {
        protected final SpriteSet sprite;

        public WaterHangProvider(SpriteSet $$0) {
            this.sprite = $$0;
        }

        @Override
        public Particle createParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            DripHangParticle $$8 = new DripHangParticle($$1, $$2, $$3, $$4, Fluids.WATER, ParticleTypes.FALLING_WATER);
            $$8.setColor(0.2f, 0.3f, 1.0f);
            $$8.pickSprite(this.sprite);
            return $$8;
        }
    }

    static class DripLandParticle
    extends DripParticle {
        DripLandParticle(ClientLevel $$0, double $$1, double $$2, double $$3, Fluid $$4) {
            super($$0, $$1, $$2, $$3, $$4);
            this.lifetime = (int)(16.0 / (Math.random() * 0.8 + 0.2));
        }
    }

    static class FallingParticle
    extends DripParticle {
        FallingParticle(ClientLevel $$0, double $$1, double $$2, double $$3, Fluid $$4) {
            this($$0, $$1, $$2, $$3, $$4, (int)(64.0 / (Math.random() * 0.8 + 0.2)));
        }

        FallingParticle(ClientLevel $$0, double $$1, double $$2, double $$3, Fluid $$4, int $$5) {
            super($$0, $$1, $$2, $$3, $$4);
            this.lifetime = $$5;
        }

        @Override
        protected void postMoveUpdate() {
            if (this.onGround) {
                this.remove();
            }
        }
    }

    static class DripstoneFallAndLandParticle
    extends FallAndLandParticle {
        DripstoneFallAndLandParticle(ClientLevel $$0, double $$1, double $$2, double $$3, Fluid $$4, ParticleOptions $$5) {
            super($$0, $$1, $$2, $$3, $$4, $$5);
        }

        @Override
        protected void postMoveUpdate() {
            if (this.onGround) {
                this.remove();
                this.level.addParticle(this.landParticle, this.x, this.y, this.z, 0.0, 0.0, 0.0);
                SoundEvent $$0 = this.getType() == Fluids.LAVA ? SoundEvents.POINTED_DRIPSTONE_DRIP_LAVA : SoundEvents.POINTED_DRIPSTONE_DRIP_WATER;
                float $$1 = Mth.randomBetween(this.random, 0.3f, 1.0f);
                this.level.playLocalSound(this.x, this.y, this.z, $$0, SoundSource.BLOCKS, $$1, 1.0f, false);
            }
        }
    }

    static class HoneyFallAndLandParticle
    extends FallAndLandParticle {
        HoneyFallAndLandParticle(ClientLevel $$0, double $$1, double $$2, double $$3, Fluid $$4, ParticleOptions $$5) {
            super($$0, $$1, $$2, $$3, $$4, $$5);
        }

        @Override
        protected void postMoveUpdate() {
            if (this.onGround) {
                this.remove();
                this.level.addParticle(this.landParticle, this.x, this.y, this.z, 0.0, 0.0, 0.0);
                float $$0 = Mth.randomBetween(this.random, 0.3f, 1.0f);
                this.level.playLocalSound(this.x, this.y, this.z, SoundEvents.BEEHIVE_DRIP, SoundSource.BLOCKS, $$0, 1.0f, false);
            }
        }
    }

    static class FallAndLandParticle
    extends FallingParticle {
        protected final ParticleOptions landParticle;

        FallAndLandParticle(ClientLevel $$0, double $$1, double $$2, double $$3, Fluid $$4, ParticleOptions $$5) {
            super($$0, $$1, $$2, $$3, $$4);
            this.landParticle = $$5;
        }

        @Override
        protected void postMoveUpdate() {
            if (this.onGround) {
                this.remove();
                this.level.addParticle(this.landParticle, this.x, this.y, this.z, 0.0, 0.0, 0.0);
            }
        }
    }

    static class CoolingDripHangParticle
    extends DripHangParticle {
        CoolingDripHangParticle(ClientLevel $$0, double $$1, double $$2, double $$3, Fluid $$4, ParticleOptions $$5) {
            super($$0, $$1, $$2, $$3, $$4, $$5);
        }

        @Override
        protected void preMoveUpdate() {
            this.rCol = 1.0f;
            this.gCol = 16.0f / (float)(40 - this.lifetime + 16);
            this.bCol = 4.0f / (float)(40 - this.lifetime + 8);
            super.preMoveUpdate();
        }
    }

    static class DripHangParticle
    extends DripParticle {
        private final ParticleOptions fallingParticle;

        DripHangParticle(ClientLevel $$0, double $$1, double $$2, double $$3, Fluid $$4, ParticleOptions $$5) {
            super($$0, $$1, $$2, $$3, $$4);
            this.fallingParticle = $$5;
            this.gravity *= 0.02f;
            this.lifetime = 40;
        }

        @Override
        protected void preMoveUpdate() {
            if (this.lifetime-- <= 0) {
                this.remove();
                this.level.addParticle(this.fallingParticle, this.x, this.y, this.z, this.xd, this.yd, this.zd);
            }
        }

        @Override
        protected void postMoveUpdate() {
            this.xd *= 0.02;
            this.yd *= 0.02;
            this.zd *= 0.02;
        }
    }
}