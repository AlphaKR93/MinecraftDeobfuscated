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
import net.minecraft.client.model.ElytraModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ElytraLayer<T extends LivingEntity, M extends EntityModel<T>>
extends RenderLayer<T, M> {
    private static final ResourceLocation WINGS_LOCATION = new ResourceLocation("textures/entity/elytra.png");
    private final ElytraModel<T> elytraModel;

    public ElytraLayer(RenderLayerParent<T, M> $$0, EntityModelSet $$1) {
        super($$0);
        this.elytraModel = new ElytraModel($$1.bakeLayer(ModelLayers.ELYTRA));
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, T $$3, float $$4, float $$5, float $$6, float $$7, float $$8, float $$9) {
        ResourceLocation $$15;
        ItemStack $$10 = ((LivingEntity)$$3).getItemBySlot(EquipmentSlot.CHEST);
        if (!$$10.is(Items.ELYTRA)) {
            return;
        }
        if ($$3 instanceof AbstractClientPlayer) {
            AbstractClientPlayer $$11 = (AbstractClientPlayer)$$3;
            if ($$11.isElytraLoaded() && $$11.getElytraTextureLocation() != null) {
                ResourceLocation $$12 = $$11.getElytraTextureLocation();
            } else if ($$11.isCapeLoaded() && $$11.getCloakTextureLocation() != null && $$11.isModelPartShown(PlayerModelPart.CAPE)) {
                ResourceLocation $$13 = $$11.getCloakTextureLocation();
            } else {
                ResourceLocation $$14 = WINGS_LOCATION;
            }
        } else {
            $$15 = WINGS_LOCATION;
        }
        $$0.pushPose();
        $$0.translate(0.0f, 0.0f, 0.125f);
        ((EntityModel)this.getParentModel()).copyPropertiesTo(this.elytraModel);
        this.elytraModel.setupAnim($$3, $$4, $$5, $$7, $$8, $$9);
        VertexConsumer $$16 = ItemRenderer.getArmorFoilBuffer($$1, RenderType.armorCutoutNoCull($$15), false, $$10.hasFoil());
        this.elytraModel.renderToBuffer($$0, $$16, $$2, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
        $$0.popPose();
    }
}