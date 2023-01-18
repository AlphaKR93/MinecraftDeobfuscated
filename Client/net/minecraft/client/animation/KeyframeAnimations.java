/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.String
 *  java.util.List
 *  java.util.Map$Entry
 *  java.util.Optional
 *  org.joml.Vector3f
 */
package net.minecraft.client.animation;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

public class KeyframeAnimations {
    public static void animate(HierarchicalModel<?> $$0, AnimationDefinition $$1, long $$2, float $$3, Vector3f $$4) {
        float $$5 = KeyframeAnimations.getElapsedSeconds($$1, $$2);
        for (Map.Entry $$6 : $$1.boneAnimations().entrySet()) {
            Optional<ModelPart> $$7 = $$0.getAnyDescendantWithName((String)$$6.getKey());
            List $$8 = (List)$$6.getValue();
            $$7.ifPresent($$42 -> $$8.forEach($$4 -> {
                Keyframe[] $$5 = $$4.keyframes();
                int $$6 = Math.max((int)0, (int)(Mth.binarySearch(0, $$5.length, $$2 -> $$5 <= $$5[$$2].timestamp()) - 1));
                int $$7 = Math.min((int)($$5.length - 1), (int)($$6 + 1));
                Keyframe $$8 = $$5[$$6];
                Keyframe $$9 = $$5[$$7];
                float $$10 = $$5 - $$8.timestamp();
                float $$11 = Mth.clamp($$10 / ($$9.timestamp() - $$8.timestamp()), 0.0f, 1.0f);
                $$9.interpolation().apply($$4, $$11, $$5, $$6, $$7, $$3);
                $$4.target().apply((ModelPart)$$42, $$4);
            }));
        }
    }

    private static float getElapsedSeconds(AnimationDefinition $$0, long $$1) {
        float $$2 = (float)$$1 / 1000.0f;
        return $$0.looping() ? $$2 % $$0.lengthInSeconds() : $$2;
    }

    public static Vector3f posVec(float $$0, float $$1, float $$2) {
        return new Vector3f($$0, -$$1, $$2);
    }

    public static Vector3f degreeVec(float $$0, float $$1, float $$2) {
        return new Vector3f($$0 * ((float)Math.PI / 180), $$1 * ((float)Math.PI / 180), $$2 * ((float)Math.PI / 180));
    }

    public static Vector3f scaleVec(double $$0, double $$1, double $$2) {
        return new Vector3f((float)($$0 - 1.0), (float)($$1 - 1.0), (float)($$2 - 1.0));
    }
}