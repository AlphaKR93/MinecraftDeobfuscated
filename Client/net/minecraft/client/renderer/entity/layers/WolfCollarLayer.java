/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Wolf;

public class WolfCollarLayer
extends RenderLayer<Wolf, WolfModel<Wolf>> {
    private static final ResourceLocation WOLF_COLLAR_LOCATION = new ResourceLocation("textures/entity/wolf/wolf_collar.png");

    public WolfCollarLayer(RenderLayerParent<Wolf, WolfModel<Wolf>> $$0) {
        super($$0);
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, Wolf $$3, float $$4, float $$5, float $$6, float $$7, float $$8, float $$9) {
        if (!$$3.isTame() || $$3.isInvisible()) {
            return;
        }
        float[] $$10 = $$3.getCollarColor().getTextureDiffuseColors();
        WolfCollarLayer.renderColoredCutoutModel(this.getParentModel(), WOLF_COLLAR_LOCATION, $$0, $$1, $$2, $$3, $$10[0], $$10[1], $$10[2]);
    }
}