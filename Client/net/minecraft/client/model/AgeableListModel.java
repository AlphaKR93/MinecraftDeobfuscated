/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Function
 */
package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.function.Function;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public abstract class AgeableListModel<E extends Entity>
extends EntityModel<E> {
    private final boolean scaleHead;
    private final float babyYHeadOffset;
    private final float babyZHeadOffset;
    private final float babyHeadScale;
    private final float babyBodyScale;
    private final float bodyYOffset;

    protected AgeableListModel(boolean $$0, float $$1, float $$2) {
        this($$0, $$1, $$2, 2.0f, 2.0f, 24.0f);
    }

    protected AgeableListModel(boolean $$0, float $$1, float $$2, float $$3, float $$4, float $$5) {
        this((Function<ResourceLocation, RenderType>)((Function)RenderType::entityCutoutNoCull), $$0, $$1, $$2, $$3, $$4, $$5);
    }

    protected AgeableListModel(Function<ResourceLocation, RenderType> $$0, boolean $$1, float $$2, float $$3, float $$4, float $$5, float $$6) {
        super($$0);
        this.scaleHead = $$1;
        this.babyYHeadOffset = $$2;
        this.babyZHeadOffset = $$3;
        this.babyHeadScale = $$4;
        this.babyBodyScale = $$5;
        this.bodyYOffset = $$6;
    }

    protected AgeableListModel() {
        this(false, 5.0f, 2.0f);
    }

    @Override
    public void renderToBuffer(PoseStack $$0, VertexConsumer $$1, int $$2, int $$3, float $$4, float $$5, float $$6, float $$7) {
        if (this.young) {
            $$0.pushPose();
            if (this.scaleHead) {
                float $$82 = 1.5f / this.babyHeadScale;
                $$0.scale($$82, $$82, $$82);
            }
            $$0.translate(0.0f, this.babyYHeadOffset / 16.0f, this.babyZHeadOffset / 16.0f);
            this.headParts().forEach($$8 -> $$8.render($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7));
            $$0.popPose();
            $$0.pushPose();
            float $$9 = 1.0f / this.babyBodyScale;
            $$0.scale($$9, $$9, $$9);
            $$0.translate(0.0f, this.bodyYOffset / 16.0f, 0.0f);
            this.bodyParts().forEach($$8 -> $$8.render($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7));
            $$0.popPose();
        } else {
            this.headParts().forEach($$8 -> $$8.render($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7));
            this.bodyParts().forEach($$8 -> $$8.render($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7));
        }
    }

    protected abstract Iterable<ModelPart> headParts();

    protected abstract Iterable<ModelPart> bodyParts();
}