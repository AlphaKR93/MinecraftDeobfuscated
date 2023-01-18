/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PandaModel;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.item.ItemStack;

public class PandaHoldsItemLayer
extends RenderLayer<Panda, PandaModel<Panda>> {
    private final ItemInHandRenderer itemInHandRenderer;

    public PandaHoldsItemLayer(RenderLayerParent<Panda, PandaModel<Panda>> $$0, ItemInHandRenderer $$1) {
        super($$0);
        this.itemInHandRenderer = $$1;
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, Panda $$3, float $$4, float $$5, float $$6, float $$7, float $$8, float $$9) {
        ItemStack $$10 = $$3.getItemBySlot(EquipmentSlot.MAINHAND);
        if (!$$3.isSitting() || $$3.isScared()) {
            return;
        }
        float $$11 = -0.6f;
        float $$12 = 1.4f;
        if ($$3.isEating()) {
            $$11 -= 0.2f * Mth.sin($$7 * 0.6f) + 0.2f;
            $$12 -= 0.09f * Mth.sin($$7 * 0.6f);
        }
        $$0.pushPose();
        $$0.translate(0.1f, $$12, $$11);
        this.itemInHandRenderer.renderItem($$3, $$10, ItemTransforms.TransformType.GROUND, false, $$0, $$1, $$2);
        $$0.popPose();
    }
}