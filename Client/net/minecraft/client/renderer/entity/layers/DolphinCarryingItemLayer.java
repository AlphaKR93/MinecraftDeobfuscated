/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.DolphinModel;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.item.ItemStack;

public class DolphinCarryingItemLayer
extends RenderLayer<Dolphin, DolphinModel<Dolphin>> {
    private final ItemInHandRenderer itemInHandRenderer;

    public DolphinCarryingItemLayer(RenderLayerParent<Dolphin, DolphinModel<Dolphin>> $$0, ItemInHandRenderer $$1) {
        super($$0);
        this.itemInHandRenderer = $$1;
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, Dolphin $$3, float $$4, float $$5, float $$6, float $$7, float $$8, float $$9) {
        boolean $$10 = $$3.getMainArm() == HumanoidArm.RIGHT;
        $$0.pushPose();
        float $$11 = 1.0f;
        float $$12 = -1.0f;
        float $$13 = Mth.abs($$3.getXRot()) / 60.0f;
        if ($$3.getXRot() < 0.0f) {
            $$0.translate(0.0f, 1.0f - $$13 * 0.5f, -1.0f + $$13 * 0.5f);
        } else {
            $$0.translate(0.0f, 1.0f + $$13 * 0.8f, -1.0f + $$13 * 0.2f);
        }
        ItemStack $$14 = $$10 ? $$3.getMainHandItem() : $$3.getOffhandItem();
        this.itemInHandRenderer.renderItem($$3, $$14, ItemTransforms.TransformType.GROUND, false, $$0, $$1, $$2);
        $$0.popPose();
    }
}