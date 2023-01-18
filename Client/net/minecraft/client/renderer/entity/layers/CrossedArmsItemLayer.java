/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class CrossedArmsItemLayer<T extends LivingEntity, M extends EntityModel<T>>
extends RenderLayer<T, M> {
    private final ItemInHandRenderer itemInHandRenderer;

    public CrossedArmsItemLayer(RenderLayerParent<T, M> $$0, ItemInHandRenderer $$1) {
        super($$0);
        this.itemInHandRenderer = $$1;
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, T $$3, float $$4, float $$5, float $$6, float $$7, float $$8, float $$9) {
        $$0.pushPose();
        $$0.translate(0.0f, 0.4f, -0.4f);
        $$0.mulPose(Axis.XP.rotationDegrees(180.0f));
        ItemStack $$10 = ((LivingEntity)$$3).getItemBySlot(EquipmentSlot.MAINHAND);
        this.itemInHandRenderer.renderItem((LivingEntity)$$3, $$10, ItemTransforms.TransformType.GROUND, false, $$0, $$1, $$2);
        $$0.popPose();
    }
}