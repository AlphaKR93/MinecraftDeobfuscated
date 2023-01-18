/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.CatModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Cat;

public class CatCollarLayer
extends RenderLayer<Cat, CatModel<Cat>> {
    private static final ResourceLocation CAT_COLLAR_LOCATION = new ResourceLocation("textures/entity/cat/cat_collar.png");
    private final CatModel<Cat> catModel;

    public CatCollarLayer(RenderLayerParent<Cat, CatModel<Cat>> $$0, EntityModelSet $$1) {
        super($$0);
        this.catModel = new CatModel($$1.bakeLayer(ModelLayers.CAT_COLLAR));
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, Cat $$3, float $$4, float $$5, float $$6, float $$7, float $$8, float $$9) {
        if (!$$3.isTame()) {
            return;
        }
        float[] $$10 = $$3.getCollarColor().getTextureDiffuseColors();
        CatCollarLayer.coloredCutoutModelCopyLayerRender(this.getParentModel(), this.catModel, CAT_COLLAR_LOCATION, $$0, $$1, $$2, $$3, $$4, $$5, $$7, $$8, $$9, $$6, $$10[0], $$10[1], $$10[2]);
    }
}