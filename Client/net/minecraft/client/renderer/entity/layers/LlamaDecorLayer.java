/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.LlamaModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.item.DyeColor;

public class LlamaDecorLayer
extends RenderLayer<Llama, LlamaModel<Llama>> {
    private static final ResourceLocation[] TEXTURE_LOCATION = new ResourceLocation[]{new ResourceLocation("textures/entity/llama/decor/white.png"), new ResourceLocation("textures/entity/llama/decor/orange.png"), new ResourceLocation("textures/entity/llama/decor/magenta.png"), new ResourceLocation("textures/entity/llama/decor/light_blue.png"), new ResourceLocation("textures/entity/llama/decor/yellow.png"), new ResourceLocation("textures/entity/llama/decor/lime.png"), new ResourceLocation("textures/entity/llama/decor/pink.png"), new ResourceLocation("textures/entity/llama/decor/gray.png"), new ResourceLocation("textures/entity/llama/decor/light_gray.png"), new ResourceLocation("textures/entity/llama/decor/cyan.png"), new ResourceLocation("textures/entity/llama/decor/purple.png"), new ResourceLocation("textures/entity/llama/decor/blue.png"), new ResourceLocation("textures/entity/llama/decor/brown.png"), new ResourceLocation("textures/entity/llama/decor/green.png"), new ResourceLocation("textures/entity/llama/decor/red.png"), new ResourceLocation("textures/entity/llama/decor/black.png")};
    private static final ResourceLocation TRADER_LLAMA = new ResourceLocation("textures/entity/llama/decor/trader_llama.png");
    private final LlamaModel<Llama> model;

    public LlamaDecorLayer(RenderLayerParent<Llama, LlamaModel<Llama>> $$0, EntityModelSet $$1) {
        super($$0);
        this.model = new LlamaModel($$1.bakeLayer(ModelLayers.LLAMA_DECOR));
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, Llama $$3, float $$4, float $$5, float $$6, float $$7, float $$8, float $$9) {
        void $$13;
        DyeColor $$10 = $$3.getSwag();
        if ($$10 != null) {
            ResourceLocation $$11 = TEXTURE_LOCATION[$$10.getId()];
        } else if ($$3.isTraderLlama()) {
            ResourceLocation $$12 = TRADER_LLAMA;
        } else {
            return;
        }
        ((LlamaModel)this.getParentModel()).copyPropertiesTo(this.model);
        this.model.setupAnim($$3, $$4, $$5, $$7, $$8, $$9);
        VertexConsumer $$14 = $$1.getBuffer(RenderType.entityCutoutNoCull((ResourceLocation)$$13));
        this.model.renderToBuffer($$0, $$14, $$2, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
    }
}