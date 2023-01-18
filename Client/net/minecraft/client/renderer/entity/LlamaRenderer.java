/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.IncompatibleClassChangeError
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.LlamaModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.LlamaDecorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.Llama;

public class LlamaRenderer
extends MobRenderer<Llama, LlamaModel<Llama>> {
    private static final ResourceLocation CREAMY = new ResourceLocation("textures/entity/llama/creamy.png");
    private static final ResourceLocation WHITE = new ResourceLocation("textures/entity/llama/white.png");
    private static final ResourceLocation BROWN = new ResourceLocation("textures/entity/llama/brown.png");
    private static final ResourceLocation GRAY = new ResourceLocation("textures/entity/llama/gray.png");

    public LlamaRenderer(EntityRendererProvider.Context $$0, ModelLayerLocation $$1) {
        super($$0, new LlamaModel($$0.bakeLayer($$1)), 0.7f);
        this.addLayer(new LlamaDecorLayer(this, $$0.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(Llama $$0) {
        return switch ($$0.getVariant()) {
            default -> throw new IncompatibleClassChangeError();
            case Llama.Variant.CREAMY -> CREAMY;
            case Llama.Variant.WHITE -> WHITE;
            case Llama.Variant.BROWN -> BROWN;
            case Llama.Variant.GRAY -> GRAY;
        };
    }
}