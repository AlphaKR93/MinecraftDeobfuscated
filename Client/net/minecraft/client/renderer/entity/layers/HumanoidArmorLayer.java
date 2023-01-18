/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Map
 *  javax.annotation.Nullable
 */
package net.minecraft.client.renderer.entity.layers;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.DyeableArmorItem;
import net.minecraft.world.item.ItemStack;

public class HumanoidArmorLayer<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>>
extends RenderLayer<T, M> {
    private static final Map<String, ResourceLocation> ARMOR_LOCATION_CACHE = Maps.newHashMap();
    private final A innerModel;
    private final A outerModel;

    public HumanoidArmorLayer(RenderLayerParent<T, M> $$0, A $$1, A $$2) {
        super($$0);
        this.innerModel = $$1;
        this.outerModel = $$2;
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, T $$3, float $$4, float $$5, float $$6, float $$7, float $$8, float $$9) {
        this.renderArmorPiece($$0, $$1, $$3, EquipmentSlot.CHEST, $$2, this.getArmorModel(EquipmentSlot.CHEST));
        this.renderArmorPiece($$0, $$1, $$3, EquipmentSlot.LEGS, $$2, this.getArmorModel(EquipmentSlot.LEGS));
        this.renderArmorPiece($$0, $$1, $$3, EquipmentSlot.FEET, $$2, this.getArmorModel(EquipmentSlot.FEET));
        this.renderArmorPiece($$0, $$1, $$3, EquipmentSlot.HEAD, $$2, this.getArmorModel(EquipmentSlot.HEAD));
    }

    private void renderArmorPiece(PoseStack $$0, MultiBufferSource $$1, T $$2, EquipmentSlot $$3, int $$4, A $$5) {
        ItemStack $$6 = ((LivingEntity)$$2).getItemBySlot($$3);
        if (!($$6.getItem() instanceof ArmorItem)) {
            return;
        }
        ArmorItem $$7 = (ArmorItem)$$6.getItem();
        if ($$7.getSlot() != $$3) {
            return;
        }
        ((HumanoidModel)this.getParentModel()).copyPropertiesTo($$5);
        this.setPartVisibility($$5, $$3);
        boolean $$8 = this.usesInnerModel($$3);
        boolean $$9 = $$6.hasFoil();
        if ($$7 instanceof DyeableArmorItem) {
            int $$10 = ((DyeableArmorItem)$$7).getColor($$6);
            float $$11 = (float)($$10 >> 16 & 0xFF) / 255.0f;
            float $$12 = (float)($$10 >> 8 & 0xFF) / 255.0f;
            float $$13 = (float)($$10 & 0xFF) / 255.0f;
            this.renderModel($$0, $$1, $$4, $$7, $$9, $$5, $$8, $$11, $$12, $$13, null);
            this.renderModel($$0, $$1, $$4, $$7, $$9, $$5, $$8, 1.0f, 1.0f, 1.0f, "overlay");
        } else {
            this.renderModel($$0, $$1, $$4, $$7, $$9, $$5, $$8, 1.0f, 1.0f, 1.0f, null);
        }
    }

    protected void setPartVisibility(A $$0, EquipmentSlot $$1) {
        ((HumanoidModel)$$0).setAllVisible(false);
        switch ($$1) {
            case HEAD: {
                ((HumanoidModel)$$0).head.visible = true;
                ((HumanoidModel)$$0).hat.visible = true;
                break;
            }
            case CHEST: {
                ((HumanoidModel)$$0).body.visible = true;
                ((HumanoidModel)$$0).rightArm.visible = true;
                ((HumanoidModel)$$0).leftArm.visible = true;
                break;
            }
            case LEGS: {
                ((HumanoidModel)$$0).body.visible = true;
                ((HumanoidModel)$$0).rightLeg.visible = true;
                ((HumanoidModel)$$0).leftLeg.visible = true;
                break;
            }
            case FEET: {
                ((HumanoidModel)$$0).rightLeg.visible = true;
                ((HumanoidModel)$$0).leftLeg.visible = true;
            }
        }
    }

    private void renderModel(PoseStack $$0, MultiBufferSource $$1, int $$2, ArmorItem $$3, boolean $$4, A $$5, boolean $$6, float $$7, float $$8, float $$9, @Nullable String $$10) {
        VertexConsumer $$11 = ItemRenderer.getArmorFoilBuffer($$1, RenderType.armorCutoutNoCull(this.getArmorLocation($$3, $$6, $$10)), false, $$4);
        ((AgeableListModel)$$5).renderToBuffer($$0, $$11, $$2, OverlayTexture.NO_OVERLAY, $$7, $$8, $$9, 1.0f);
    }

    private A getArmorModel(EquipmentSlot $$0) {
        return this.usesInnerModel($$0) ? this.innerModel : this.outerModel;
    }

    private boolean usesInnerModel(EquipmentSlot $$0) {
        return $$0 == EquipmentSlot.LEGS;
    }

    private ResourceLocation getArmorLocation(ArmorItem $$0, boolean $$1, @Nullable String $$2) {
        String $$3 = "textures/models/armor/" + $$0.getMaterial().getName() + "_layer_" + ($$1 ? 2 : 1) + ($$2 == null ? "" : "_" + $$2) + ".png";
        return (ResourceLocation)ARMOR_LOCATION_CACHE.computeIfAbsent((Object)$$3, ResourceLocation::new);
    }
}