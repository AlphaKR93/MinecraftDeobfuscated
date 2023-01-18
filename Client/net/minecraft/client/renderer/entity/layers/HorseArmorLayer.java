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
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.item.DyeableHorseArmorItem;
import net.minecraft.world.item.HorseArmorItem;
import net.minecraft.world.item.ItemStack;

public class HorseArmorLayer
extends RenderLayer<Horse, HorseModel<Horse>> {
    private final HorseModel<Horse> model;

    public HorseArmorLayer(RenderLayerParent<Horse, HorseModel<Horse>> $$0, EntityModelSet $$1) {
        super($$0);
        this.model = new HorseModel($$1.bakeLayer(ModelLayers.HORSE_ARMOR));
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, Horse $$3, float $$4, float $$5, float $$6, float $$7, float $$8, float $$9) {
        float $$18;
        float $$17;
        float $$16;
        ItemStack $$10 = $$3.getArmor();
        if (!($$10.getItem() instanceof HorseArmorItem)) {
            return;
        }
        HorseArmorItem $$11 = (HorseArmorItem)$$10.getItem();
        ((HorseModel)this.getParentModel()).copyPropertiesTo(this.model);
        this.model.prepareMobModel($$3, $$4, $$5, $$6);
        this.model.setupAnim($$3, $$4, $$5, $$7, $$8, $$9);
        if ($$11 instanceof DyeableHorseArmorItem) {
            int $$12 = ((DyeableHorseArmorItem)$$11).getColor($$10);
            float $$13 = (float)($$12 >> 16 & 0xFF) / 255.0f;
            float $$14 = (float)($$12 >> 8 & 0xFF) / 255.0f;
            float $$15 = (float)($$12 & 0xFF) / 255.0f;
        } else {
            $$16 = 1.0f;
            $$17 = 1.0f;
            $$18 = 1.0f;
        }
        VertexConsumer $$19 = $$1.getBuffer(RenderType.entityCutoutNoCull($$11.getTexture()));
        this.model.renderToBuffer($$0, $$19, $$2, OverlayTexture.NO_OVERLAY, $$16, $$17, $$18, 1.0f);
    }
}