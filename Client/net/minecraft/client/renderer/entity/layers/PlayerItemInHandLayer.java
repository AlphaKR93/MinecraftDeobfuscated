/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class PlayerItemInHandLayer<T extends Player, M extends EntityModel<T> & HeadedModel>
extends ItemInHandLayer<T, M> {
    private final ItemInHandRenderer itemInHandRenderer;
    private static final float X_ROT_MIN = -0.5235988f;
    private static final float X_ROT_MAX = 1.5707964f;

    public PlayerItemInHandLayer(RenderLayerParent<T, M> $$0, ItemInHandRenderer $$1) {
        super($$0, $$1);
        this.itemInHandRenderer = $$1;
    }

    @Override
    protected void renderArmWithItem(LivingEntity $$0, ItemStack $$1, ItemTransforms.TransformType $$2, HumanoidArm $$3, PoseStack $$4, MultiBufferSource $$5, int $$6) {
        if ($$1.is(Items.SPYGLASS) && $$0.getUseItem() == $$1 && $$0.swingTime == 0) {
            this.renderArmWithSpyglass($$0, $$1, $$3, $$4, $$5, $$6);
        } else {
            super.renderArmWithItem($$0, $$1, $$2, $$3, $$4, $$5, $$6);
        }
    }

    private void renderArmWithSpyglass(LivingEntity $$0, ItemStack $$1, HumanoidArm $$2, PoseStack $$3, MultiBufferSource $$4, int $$5) {
        $$3.pushPose();
        ModelPart $$6 = ((HeadedModel)this.getParentModel()).getHead();
        float $$7 = $$6.xRot;
        $$6.xRot = Mth.clamp($$6.xRot, -0.5235988f, 1.5707964f);
        $$6.translateAndRotate($$3);
        $$6.xRot = $$7;
        CustomHeadLayer.translateToHead($$3, false);
        boolean $$8 = $$2 == HumanoidArm.LEFT;
        $$3.translate(($$8 ? -2.5f : 2.5f) / 16.0f, -0.0625f, 0.0f);
        this.itemInHandRenderer.renderItem($$0, $$1, ItemTransforms.TransformType.HEAD, false, $$3, $$4, $$5);
        $$3.popPose();
    }
}