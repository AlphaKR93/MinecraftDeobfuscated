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
import net.minecraft.client.model.FoxModel;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.item.ItemStack;

public class FoxHeldItemLayer
extends RenderLayer<Fox, FoxModel<Fox>> {
    private final ItemInHandRenderer itemInHandRenderer;

    public FoxHeldItemLayer(RenderLayerParent<Fox, FoxModel<Fox>> $$0, ItemInHandRenderer $$1) {
        super($$0);
        this.itemInHandRenderer = $$1;
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, Fox $$3, float $$4, float $$5, float $$6, float $$7, float $$8, float $$9) {
        boolean $$10 = $$3.isSleeping();
        boolean $$11 = $$3.isBaby();
        $$0.pushPose();
        if ($$11) {
            float $$12 = 0.75f;
            $$0.scale(0.75f, 0.75f, 0.75f);
            $$0.translate(0.0f, 0.5f, 0.209375f);
        }
        $$0.translate(((FoxModel)this.getParentModel()).head.x / 16.0f, ((FoxModel)this.getParentModel()).head.y / 16.0f, ((FoxModel)this.getParentModel()).head.z / 16.0f);
        float $$13 = $$3.getHeadRollAngle($$6);
        $$0.mulPose(Axis.ZP.rotation($$13));
        $$0.mulPose(Axis.YP.rotationDegrees($$8));
        $$0.mulPose(Axis.XP.rotationDegrees($$9));
        if ($$3.isBaby()) {
            if ($$10) {
                $$0.translate(0.4f, 0.26f, 0.15f);
            } else {
                $$0.translate(0.06f, 0.26f, -0.5f);
            }
        } else if ($$10) {
            $$0.translate(0.46f, 0.26f, 0.22f);
        } else {
            $$0.translate(0.06f, 0.27f, -0.5f);
        }
        $$0.mulPose(Axis.XP.rotationDegrees(90.0f));
        if ($$10) {
            $$0.mulPose(Axis.ZP.rotationDegrees(90.0f));
        }
        ItemStack $$14 = $$3.getItemBySlot(EquipmentSlot.MAINHAND);
        this.itemInHandRenderer.renderItem($$3, $$14, ItemTransforms.TransformType.GROUND, false, $$0, $$1, $$2);
        $$0.popPose();
    }
}