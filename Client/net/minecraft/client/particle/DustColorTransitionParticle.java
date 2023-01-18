/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.DustParticleBase;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.DustColorTransitionOptions;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class DustColorTransitionParticle
extends DustParticleBase<DustColorTransitionOptions> {
    private final Vector3f fromColor;
    private final Vector3f toColor;

    protected DustColorTransitionParticle(ClientLevel $$0, double $$1, double $$2, double $$3, double $$4, double $$5, double $$6, DustColorTransitionOptions $$7, SpriteSet $$8) {
        super($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$8);
        float $$9 = this.random.nextFloat() * 0.4f + 0.6f;
        this.fromColor = this.randomizeColor($$7.getFromColor(), $$9);
        this.toColor = this.randomizeColor($$7.getToColor(), $$9);
    }

    private Vector3f randomizeColor(Vector3f $$0, float $$1) {
        return new Vector3f(this.randomizeColor($$0.x(), $$1), this.randomizeColor($$0.y(), $$1), this.randomizeColor($$0.z(), $$1));
    }

    private void lerpColors(float $$0) {
        float $$1 = ((float)this.age + $$0) / ((float)this.lifetime + 1.0f);
        Vector3f $$2 = new Vector3f((Vector3fc)this.fromColor).lerp((Vector3fc)this.toColor, $$1);
        this.rCol = $$2.x();
        this.gCol = $$2.y();
        this.bCol = $$2.z();
    }

    @Override
    public void render(VertexConsumer $$0, Camera $$1, float $$2) {
        this.lerpColors($$2);
        super.render($$0, $$1, $$2);
    }

    public static class Provider
    implements ParticleProvider<DustColorTransitionOptions> {
        private final SpriteSet sprites;

        public Provider(SpriteSet $$0) {
            this.sprites = $$0;
        }

        @Override
        public Particle createParticle(DustColorTransitionOptions $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            return new DustColorTransitionParticle($$1, $$2, $$3, $$4, $$5, $$6, $$7, $$0, this.sprites);
        }
    }
}