/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.world.entity.Entity;

public abstract class ColorableHierarchicalModel<E extends Entity>
extends HierarchicalModel<E> {
    private float r = 1.0f;
    private float g = 1.0f;
    private float b = 1.0f;

    public void setColor(float $$0, float $$1, float $$2) {
        this.r = $$0;
        this.g = $$1;
        this.b = $$2;
    }

    @Override
    public void renderToBuffer(PoseStack $$0, VertexConsumer $$1, int $$2, int $$3, float $$4, float $$5, float $$6, float $$7) {
        super.renderToBuffer($$0, $$1, $$2, $$3, this.r * $$4, this.g * $$5, this.b * $$6, $$7);
    }
}