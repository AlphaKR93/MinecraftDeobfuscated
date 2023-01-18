/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.minecraft.client.animation;

import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public record AnimationChannel(Target target, Keyframe[] keyframes) {

    public static interface Target {
        public void apply(ModelPart var1, Vector3f var2);
    }

    public static class Interpolations {
        public static final Interpolation LINEAR = ($$0, $$1, $$2, $$3, $$4, $$5) -> {
            Vector3f $$6 = $$2[$$3].target();
            Vector3f $$7 = $$2[$$4].target();
            return $$6.lerp((Vector3fc)$$7, $$1, $$0).mul($$5);
        };
        public static final Interpolation CATMULLROM = ($$0, $$1, $$2, $$3, $$4, $$5) -> {
            Vector3f $$6 = $$2[Math.max((int)0, (int)($$3 - 1))].target();
            Vector3f $$7 = $$2[$$3].target();
            Vector3f $$8 = $$2[$$4].target();
            Vector3f $$9 = $$2[Math.min((int)($$2.length - 1), (int)($$4 + 1))].target();
            $$0.set(Mth.catmullrom($$1, $$6.x(), $$7.x(), $$8.x(), $$9.x()) * $$5, Mth.catmullrom($$1, $$6.y(), $$7.y(), $$8.y(), $$9.y()) * $$5, Mth.catmullrom($$1, $$6.z(), $$7.z(), $$8.z(), $$9.z()) * $$5);
            return $$0;
        };
    }

    public static class Targets {
        public static final Target POSITION = ModelPart::offsetPos;
        public static final Target ROTATION = ModelPart::offsetRotation;
        public static final Target SCALE = ModelPart::offsetScale;
    }

    public static interface Interpolation {
        public Vector3f apply(Vector3f var1, float var2, Keyframe[] var3, int var4, int var5, float var6);
    }
}