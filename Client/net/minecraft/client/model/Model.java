/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.util.function.Function
 */
package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.function.Function;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public abstract class Model {
    protected final Function<ResourceLocation, RenderType> renderType;

    public Model(Function<ResourceLocation, RenderType> $$0) {
        this.renderType = $$0;
    }

    public final RenderType renderType(ResourceLocation $$0) {
        return (RenderType)this.renderType.apply((Object)$$0);
    }

    public abstract void renderToBuffer(PoseStack var1, VertexConsumer var2, int var3, int var4, float var5, float var6, float var7, float var8);
}