/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Consumer
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 */
package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.function.Consumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.ShriekParticleOption;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public class ShriekParticle
extends TextureSheetParticle {
    private static final Vector3f ROTATION_VECTOR = new Vector3f(0.5f, 0.5f, 0.5f).normalize();
    private static final Vector3f TRANSFORM_VECTOR = new Vector3f(-1.0f, -1.0f, 0.0f);
    private static final float MAGICAL_X_ROT = 1.0472f;
    private int delay;

    ShriekParticle(ClientLevel $$0, double $$1, double $$2, double $$3, int $$4) {
        super($$0, $$1, $$2, $$3, 0.0, 0.0, 0.0);
        this.quadSize = 0.85f;
        this.delay = $$4;
        this.lifetime = 30;
        this.gravity = 0.0f;
        this.xd = 0.0;
        this.yd = 0.1;
        this.zd = 0.0;
    }

    @Override
    public float getQuadSize(float $$0) {
        return this.quadSize * Mth.clamp(((float)this.age + $$0) / (float)this.lifetime * 0.75f, 0.0f, 1.0f);
    }

    @Override
    public void render(VertexConsumer $$02, Camera $$1, float $$2) {
        if (this.delay > 0) {
            return;
        }
        this.alpha = 1.0f - Mth.clamp(((float)this.age + $$2) / (float)this.lifetime, 0.0f, 1.0f);
        this.renderRotatedParticle($$02, $$1, $$2, (Consumer<Quaternionf>)((Consumer)$$0 -> $$0.mul((Quaternionfc)new Quaternionf().rotationX(-1.0472f))));
        this.renderRotatedParticle($$02, $$1, $$2, (Consumer<Quaternionf>)((Consumer)$$0 -> $$0.mul((Quaternionfc)new Quaternionf().rotationYXZ((float)(-Math.PI), 1.0472f, 0.0f))));
    }

    private void renderRotatedParticle(VertexConsumer $$0, Camera $$1, float $$2, Consumer<Quaternionf> $$3) {
        Vec3 $$4 = $$1.getPosition();
        float $$5 = (float)(Mth.lerp((double)$$2, this.xo, this.x) - $$4.x());
        float $$6 = (float)(Mth.lerp((double)$$2, this.yo, this.y) - $$4.y());
        float $$7 = (float)(Mth.lerp((double)$$2, this.zo, this.z) - $$4.z());
        Quaternionf $$8 = new Quaternionf().setAngleAxis(0.0f, ROTATION_VECTOR.x(), ROTATION_VECTOR.y(), ROTATION_VECTOR.z());
        $$3.accept((Object)$$8);
        $$8.transform(TRANSFORM_VECTOR);
        Vector3f[] $$9 = new Vector3f[]{new Vector3f(-1.0f, -1.0f, 0.0f), new Vector3f(-1.0f, 1.0f, 0.0f), new Vector3f(1.0f, 1.0f, 0.0f), new Vector3f(1.0f, -1.0f, 0.0f)};
        float $$10 = this.getQuadSize($$2);
        for (int $$11 = 0; $$11 < 4; ++$$11) {
            Vector3f $$12 = $$9[$$11];
            $$12.rotate((Quaternionfc)$$8);
            $$12.mul($$10);
            $$12.add($$5, $$6, $$7);
        }
        int $$13 = this.getLightColor($$2);
        this.makeCornerVertex($$0, $$9[0], this.getU1(), this.getV1(), $$13);
        this.makeCornerVertex($$0, $$9[1], this.getU1(), this.getV0(), $$13);
        this.makeCornerVertex($$0, $$9[2], this.getU0(), this.getV0(), $$13);
        this.makeCornerVertex($$0, $$9[3], this.getU0(), this.getV1(), $$13);
    }

    private void makeCornerVertex(VertexConsumer $$0, Vector3f $$1, float $$2, float $$3, int $$4) {
        $$0.vertex($$1.x(), $$1.y(), $$1.z()).uv($$2, $$3).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2($$4).endVertex();
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
        if (this.delay > 0) {
            --this.delay;
            return;
        }
        super.tick();
    }

    public static class Provider
    implements ParticleProvider<ShriekParticleOption> {
        private final SpriteSet sprite;

        public Provider(SpriteSet $$0) {
            this.sprite = $$0;
        }

        @Override
        public Particle createParticle(ShriekParticleOption $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            ShriekParticle $$8 = new ShriekParticle($$1, $$2, $$3, $$4, $$0.getDelay());
            $$8.pickSprite(this.sprite);
            $$8.setAlpha(1.0f);
            return $$8;
        }
    }
}