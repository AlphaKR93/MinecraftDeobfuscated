/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class CapeLayer
extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    public CapeLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> $$0) {
        super($$0);
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, AbstractClientPlayer $$3, float $$4, float $$5, float $$6, float $$7, float $$8, float $$9) {
        if (!$$3.isCapeLoaded() || $$3.isInvisible() || !$$3.isModelPartShown(PlayerModelPart.CAPE) || $$3.getCloakTextureLocation() == null) {
            return;
        }
        ItemStack $$10 = $$3.getItemBySlot(EquipmentSlot.CHEST);
        if ($$10.is(Items.ELYTRA)) {
            return;
        }
        $$0.pushPose();
        $$0.translate(0.0f, 0.0f, 0.125f);
        double $$11 = Mth.lerp((double)$$6, $$3.xCloakO, $$3.xCloak) - Mth.lerp((double)$$6, $$3.xo, $$3.getX());
        double $$12 = Mth.lerp((double)$$6, $$3.yCloakO, $$3.yCloak) - Mth.lerp((double)$$6, $$3.yo, $$3.getY());
        double $$13 = Mth.lerp((double)$$6, $$3.zCloakO, $$3.zCloak) - Mth.lerp((double)$$6, $$3.zo, $$3.getZ());
        float $$14 = $$3.yBodyRotO + ($$3.yBodyRot - $$3.yBodyRotO);
        double $$15 = Mth.sin($$14 * ((float)Math.PI / 180));
        double $$16 = -Mth.cos($$14 * ((float)Math.PI / 180));
        float $$17 = (float)$$12 * 10.0f;
        $$17 = Mth.clamp($$17, -6.0f, 32.0f);
        float $$18 = (float)($$11 * $$15 + $$13 * $$16) * 100.0f;
        $$18 = Mth.clamp($$18, 0.0f, 150.0f);
        float $$19 = (float)($$11 * $$16 - $$13 * $$15) * 100.0f;
        $$19 = Mth.clamp($$19, -20.0f, 20.0f);
        if ($$18 < 0.0f) {
            $$18 = 0.0f;
        }
        float $$20 = Mth.lerp($$6, $$3.oBob, $$3.bob);
        $$17 += Mth.sin(Mth.lerp($$6, $$3.walkDistO, $$3.walkDist) * 6.0f) * 32.0f * $$20;
        if ($$3.isCrouching()) {
            $$17 += 25.0f;
        }
        $$0.mulPose(Axis.XP.rotationDegrees(6.0f + $$18 / 2.0f + $$17));
        $$0.mulPose(Axis.ZP.rotationDegrees($$19 / 2.0f));
        $$0.mulPose(Axis.YP.rotationDegrees(180.0f - $$19 / 2.0f));
        VertexConsumer $$21 = $$1.getBuffer(RenderType.entitySolid($$3.getCloakTextureLocation()));
        ((PlayerModel)this.getParentModel()).renderCloak($$0, $$21, $$2, OverlayTexture.NO_OVERLAY);
        $$0.popPose();
    }
}