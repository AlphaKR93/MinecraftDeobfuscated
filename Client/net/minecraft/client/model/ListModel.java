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

public abstract class ListModel<E extends Entity>
extends EntityModel<E> {
    public ListModel() {
        this((Function<ResourceLocation, RenderType>)((Function)RenderType::entityCutoutNoCull));
    }

    public ListModel(Function<ResourceLocation, RenderType> $$0) {
        super($$0);
    }

    @Override
    public void renderToBuffer(PoseStack $$0, VertexConsumer $$1, int $$2, int $$3, float $$4, float $$5, float $$6, float $$7) {
        this.parts().forEach($$8 -> $$8.render($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7));
    }

    public abstract Iterable<ModelPart> parts();
}