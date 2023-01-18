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
import net.minecraft.client.model.WitchModel;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CrossedArmsItemLayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class WitchItemLayer<T extends LivingEntity>
extends CrossedArmsItemLayer<T, WitchModel<T>> {
    public WitchItemLayer(RenderLayerParent<T, WitchModel<T>> $$0, ItemInHandRenderer $$1) {
        super($$0, $$1);
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, T $$3, float $$4, float $$5, float $$6, float $$7, float $$8, float $$9) {
        ItemStack $$10 = ((LivingEntity)$$3).getMainHandItem();
        $$0.pushPose();
        if ($$10.is(Items.POTION)) {
            ((WitchModel)this.getParentModel()).getHead().translateAndRotate($$0);
            ((WitchModel)this.getParentModel()).getNose().translateAndRotate($$0);
            $$0.translate(0.0625f, 0.25f, 0.0f);
            $$0.mulPose(Axis.ZP.rotationDegrees(180.0f));
            $$0.mulPose(Axis.XP.rotationDegrees(140.0f));
            $$0.mulPose(Axis.ZP.rotationDegrees(10.0f));
            $$0.translate(0.0f, -0.4f, 0.4f);
        }
        super.render($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$8, $$9);
        $$0.popPose();
    }
}