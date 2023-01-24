/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  net.minecraft.world.entity.LivingEntity
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class ItemInHandLayer<T extends LivingEntity, M extends EntityModel<T>>
extends RenderLayer<T, M> {
    private final ItemInHandRenderer itemInHandRenderer;

    public ItemInHandLayer(RenderLayerParent<T, M> $$0, ItemInHandRenderer $$1) {
        super($$0);
        this.itemInHandRenderer = $$1;
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, T $$3, float $$4, float $$5, float $$6, float $$7, float $$8, float $$9) {
        ItemStack $$12;
        boolean $$10 = $$3.getMainArm() == HumanoidArm.RIGHT;
        ItemStack $$11 = $$10 ? $$3.getOffhandItem() : $$3.getMainHandItem();
        ItemStack itemStack = $$12 = $$10 ? $$3.getMainHandItem() : $$3.getOffhandItem();
        if ($$11.isEmpty() && $$12.isEmpty()) {
            return;
        }
        $$0.pushPose();
        if (((EntityModel)this.getParentModel()).young) {
            float $$13 = 0.5f;
            $$0.translate(0.0f, 0.75f, 0.0f);
            $$0.scale(0.5f, 0.5f, 0.5f);
        }
        this.renderArmWithItem((LivingEntity)$$3, $$12, ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, HumanoidArm.RIGHT, $$0, $$1, $$2);
        this.renderArmWithItem((LivingEntity)$$3, $$11, ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND, HumanoidArm.LEFT, $$0, $$1, $$2);
        $$0.popPose();
    }

    protected void renderArmWithItem(LivingEntity $$0, ItemStack $$1, ItemTransforms.TransformType $$2, HumanoidArm $$3, PoseStack $$4, MultiBufferSource $$5, int $$6) {
        if ($$1.isEmpty()) {
            return;
        }
        $$4.pushPose();
        ((ArmedModel)this.getParentModel()).translateToHand($$3, $$4);
        $$4.mulPose(Axis.XP.rotationDegrees(-90.0f));
        $$4.mulPose(Axis.YP.rotationDegrees(180.0f));
        boolean $$7 = $$3 == HumanoidArm.LEFT;
        $$4.translate((float)($$7 ? -1 : 1) / 16.0f, 0.125f, -0.625f);
        this.itemInHandRenderer.renderItem($$0, $$1, $$2, $$7, $$4, $$5, $$6);
        $$4.popPose();
    }
}