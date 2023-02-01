/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Optional
 *  java.util.function.Consumer
 *  java.util.function.Function
 *  org.joml.Vector3f
 */
package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import org.joml.Vector3f;

public abstract class HierarchicalModel<E extends Entity>
extends EntityModel<E> {
    private static final Vector3f ANIMATION_VECTOR_CACHE = new Vector3f();

    public HierarchicalModel() {
        this((Function<ResourceLocation, RenderType>)((Function)RenderType::entityCutoutNoCull));
    }

    public HierarchicalModel(Function<ResourceLocation, RenderType> $$0) {
        super($$0);
    }

    @Override
    public void renderToBuffer(PoseStack $$0, VertexConsumer $$1, int $$2, int $$3, float $$4, float $$5, float $$6, float $$7) {
        this.root().render($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
    }

    public abstract ModelPart root();

    public Optional<ModelPart> getAnyDescendantWithName(String $$0) {
        if ($$0.equals((Object)"root")) {
            return Optional.of((Object)this.root());
        }
        return this.root().getAllParts().filter($$1 -> $$1.hasChild($$0)).findFirst().map($$1 -> $$1.getChild($$0));
    }

    protected void animate(AnimationState $$0, AnimationDefinition $$1, float $$2) {
        this.animate($$0, $$1, $$2, 1.0f);
    }

    protected void animateWalk(AnimationDefinition $$0, float $$1, float $$2, float $$3, float $$4) {
        long $$5 = (long)($$1 * 50.0f * $$3);
        float $$6 = Math.min((float)($$2 * $$4), (float)1.0f);
        KeyframeAnimations.animate(this, $$0, $$5, $$6, ANIMATION_VECTOR_CACHE);
    }

    protected void animate(AnimationState $$0, AnimationDefinition $$12, float $$2, float $$3) {
        $$0.updateTime($$2, $$3);
        $$0.ifStarted((Consumer<AnimationState>)((Consumer)$$1 -> KeyframeAnimations.animate(this, $$12, $$1.getAccumulatedTime(), 1.0f, ANIMATION_VECTOR_CACHE)));
    }
}