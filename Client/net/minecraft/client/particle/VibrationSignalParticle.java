/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Optional
 *  java.util.function.Consumer
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 */
package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.VibrationParticleOption;
import net.minecraft.util.Mth;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public class VibrationSignalParticle
extends TextureSheetParticle {
    private final PositionSource target;
    private float rot;
    private float rotO;
    private float pitch;
    private float pitchO;

    VibrationSignalParticle(ClientLevel $$0, double $$1, double $$2, double $$3, PositionSource $$4, int $$5) {
        super($$0, $$1, $$2, $$3, 0.0, 0.0, 0.0);
        this.quadSize = 0.3f;
        this.target = $$4;
        this.lifetime = $$5;
        Optional<Vec3> $$6 = $$4.getPosition($$0);
        if ($$6.isPresent()) {
            Vec3 $$7 = (Vec3)$$6.get();
            double $$8 = $$1 - $$7.x();
            double $$9 = $$2 - $$7.y();
            double $$10 = $$3 - $$7.z();
            this.rotO = this.rot = (float)Mth.atan2($$8, $$10);
            this.pitchO = this.pitch = (float)Mth.atan2($$9, Math.sqrt((double)($$8 * $$8 + $$10 * $$10)));
        }
    }

    @Override
    public void render(VertexConsumer $$0, Camera $$1, float $$2) {
        float $$32 = Mth.sin(((float)this.age + $$2 - (float)Math.PI * 2) * 0.05f) * 2.0f;
        float $$4 = Mth.lerp($$2, this.rotO, this.rot);
        float $$5 = Mth.lerp($$2, this.pitchO, this.pitch) + 1.5707964f;
        this.renderSignal($$0, $$1, $$2, (Consumer<Quaternionf>)((Consumer)$$3 -> $$3.rotateY($$4).rotateX(-$$5).rotateY($$32)));
        this.renderSignal($$0, $$1, $$2, (Consumer<Quaternionf>)((Consumer)$$3 -> $$3.rotateY((float)(-Math.PI) + $$4).rotateX($$5).rotateY($$32)));
    }

    private void renderSignal(VertexConsumer $$0, Camera $$1, float $$2, Consumer<Quaternionf> $$3) {
        Vec3 $$4 = $$1.getPosition();
        float $$5 = (float)(Mth.lerp((double)$$2, this.xo, this.x) - $$4.x());
        float $$6 = (float)(Mth.lerp((double)$$2, this.yo, this.y) - $$4.y());
        float $$7 = (float)(Mth.lerp((double)$$2, this.zo, this.z) - $$4.z());
        Vector3f $$8 = new Vector3f(0.5f, 0.5f, 0.5f).normalize();
        Quaternionf $$9 = new Quaternionf().setAngleAxis(0.0f, $$8.x(), $$8.y(), $$8.z());
        $$3.accept((Object)$$9);
        Vector3f[] $$10 = new Vector3f[]{new Vector3f(-1.0f, -1.0f, 0.0f), new Vector3f(-1.0f, 1.0f, 0.0f), new Vector3f(1.0f, 1.0f, 0.0f), new Vector3f(1.0f, -1.0f, 0.0f)};
        float $$11 = this.getQuadSize($$2);
        for (int $$12 = 0; $$12 < 4; ++$$12) {
            Vector3f $$13 = $$10[$$12];
            $$13.rotate((Quaternionfc)$$9);
            $$13.mul($$11);
            $$13.add($$5, $$6, $$7);
        }
        float $$14 = this.getU0();
        float $$15 = this.getU1();
        float $$16 = this.getV0();
        float $$17 = this.getV1();
        int $$18 = this.getLightColor($$2);
        $$0.vertex($$10[0].x(), $$10[0].y(), $$10[0].z()).uv($$15, $$17).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2($$18).endVertex();
        $$0.vertex($$10[1].x(), $$10[1].y(), $$10[1].z()).uv($$15, $$16).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2($$18).endVertex();
        $$0.vertex($$10[2].x(), $$10[2].y(), $$10[2].z()).uv($$14, $$16).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2($$18).endVertex();
        $$0.vertex($$10[3].x(), $$10[3].y(), $$10[3].z()).uv($$14, $$17).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2($$18).endVertex();
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
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }
        Optional<Vec3> $$0 = this.target.getPosition(this.level);
        if ($$0.isEmpty()) {
            this.remove();
            return;
        }
        int $$1 = this.lifetime - this.age;
        double $$2 = 1.0 / (double)$$1;
        Vec3 $$3 = (Vec3)$$0.get();
        this.x = Mth.lerp($$2, this.x, $$3.x());
        this.y = Mth.lerp($$2, this.y, $$3.y());
        this.z = Mth.lerp($$2, this.z, $$3.z());
        double $$4 = this.x - $$3.x();
        double $$5 = this.y - $$3.y();
        double $$6 = this.z - $$3.z();
        this.rotO = this.rot;
        this.rot = (float)Mth.atan2($$4, $$6);
        this.pitchO = this.pitch;
        this.pitch = (float)Mth.atan2($$5, Math.sqrt((double)($$4 * $$4 + $$6 * $$6)));
    }

    public static class Provider
    implements ParticleProvider<VibrationParticleOption> {
        private final SpriteSet sprite;

        public Provider(SpriteSet $$0) {
            this.sprite = $$0;
        }

        @Override
        public Particle createParticle(VibrationParticleOption $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            VibrationSignalParticle $$8 = new VibrationSignalParticle($$1, $$2, $$3, $$4, $$0.getDestination(), $$0.getArrivalInTicks());
            $$8.pickSprite(this.sprite);
            $$8.setAlpha(1.0f);
            return $$8;
        }
    }
}