/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 */
package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public abstract class SingleQuadParticle
extends Particle {
    protected float quadSize;

    protected SingleQuadParticle(ClientLevel $$0, double $$1, double $$2, double $$3) {
        super($$0, $$1, $$2, $$3);
        this.quadSize = 0.1f * (this.random.nextFloat() * 0.5f + 0.5f) * 2.0f;
    }

    protected SingleQuadParticle(ClientLevel $$0, double $$1, double $$2, double $$3, double $$4, double $$5, double $$6) {
        super($$0, $$1, $$2, $$3, $$4, $$5, $$6);
        this.quadSize = 0.1f * (this.random.nextFloat() * 0.5f + 0.5f) * 2.0f;
    }

    @Override
    public void render(VertexConsumer $$0, Camera $$1, float $$2) {
        Quaternionf $$8;
        Vec3 $$3 = $$1.getPosition();
        float $$4 = (float)(Mth.lerp((double)$$2, this.xo, this.x) - $$3.x());
        float $$5 = (float)(Mth.lerp((double)$$2, this.yo, this.y) - $$3.y());
        float $$6 = (float)(Mth.lerp((double)$$2, this.zo, this.z) - $$3.z());
        if (this.roll == 0.0f) {
            Quaternionf $$7 = $$1.rotation();
        } else {
            $$8 = new Quaternionf((Quaternionfc)$$1.rotation());
            $$8.rotateZ(Mth.lerp($$2, this.oRoll, this.roll));
        }
        Vector3f[] $$9 = new Vector3f[]{new Vector3f(-1.0f, -1.0f, 0.0f), new Vector3f(-1.0f, 1.0f, 0.0f), new Vector3f(1.0f, 1.0f, 0.0f), new Vector3f(1.0f, -1.0f, 0.0f)};
        float $$10 = this.getQuadSize($$2);
        for (int $$11 = 0; $$11 < 4; ++$$11) {
            Vector3f $$12 = $$9[$$11];
            $$12.rotate((Quaternionfc)$$8);
            $$12.mul($$10);
            $$12.add($$4, $$5, $$6);
        }
        float $$13 = this.getU0();
        float $$14 = this.getU1();
        float $$15 = this.getV0();
        float $$16 = this.getV1();
        int $$17 = this.getLightColor($$2);
        $$0.vertex($$9[0].x(), $$9[0].y(), $$9[0].z()).uv($$14, $$16).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2($$17).endVertex();
        $$0.vertex($$9[1].x(), $$9[1].y(), $$9[1].z()).uv($$14, $$15).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2($$17).endVertex();
        $$0.vertex($$9[2].x(), $$9[2].y(), $$9[2].z()).uv($$13, $$15).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2($$17).endVertex();
        $$0.vertex($$9[3].x(), $$9[3].y(), $$9[3].z()).uv($$13, $$16).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2($$17).endVertex();
    }

    public float getQuadSize(float $$0) {
        return this.quadSize;
    }

    @Override
    public Particle scale(float $$0) {
        this.quadSize *= $$0;
        return super.scale($$0);
    }

    protected abstract float getU0();

    protected abstract float getU1();

    protected abstract float getV0();

    protected abstract float getV1();
}