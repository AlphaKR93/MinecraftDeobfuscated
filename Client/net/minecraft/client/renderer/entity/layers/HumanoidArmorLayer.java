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
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.DyeableArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;

public class HumanoidArmorLayer<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>>
extends RenderLayer<T, M> {
    private static final Map<String, ResourceLocation> ARMOR_LOCATION_CACHE = Maps.newHashMap();
    private final A innerModel;
    private final A outerModel;
    private final TextureAtlas armorTrimAtlas;

    public HumanoidArmorLayer(RenderLayerParent<T, M> $$0, A $$1, A $$2, ModelManager $$3) {
        super($$0);
        this.innerModel = $$1;
        this.outerModel = $$2;
        this.armorTrimAtlas = $$3.getAtlas(Sheets.ARMOR_TRIMS_SHEET);
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, T $$3, float $$4, float $$5, float $$6, float $$7, float $$8, float $$9) {
        this.renderArmorPiece($$0, $$1, $$3, EquipmentSlot.CHEST, $$2, this.getArmorModel(EquipmentSlot.CHEST));
        this.renderArmorPiece($$0, $$1, $$3, EquipmentSlot.LEGS, $$2, this.getArmorModel(EquipmentSlot.LEGS));
        this.renderArmorPiece($$0, $$1, $$3, EquipmentSlot.FEET, $$2, this.getArmorModel(EquipmentSlot.FEET));
        this.renderArmorPiece($$0, $$1, $$3, EquipmentSlot.HEAD, $$2, this.getArmorModel(EquipmentSlot.HEAD));
    }

    /*
     * WARNING - void declaration
     */
    private void renderArmorPiece(PoseStack $$0, MultiBufferSource $$1, T $$2, EquipmentSlot $$3, int $$4, A $$5) {
        void $$8;
        ItemStack $$6 = ((LivingEntity)$$2).getItemBySlot($$3);
        Item item = $$6.getItem();
        if (!(item instanceof ArmorItem)) {
            return;
        }
        ArmorItem $$7 = (ArmorItem)item;
        if ($$8.getSlot() != $$3) {
            return;
        }
        ((HumanoidModel)this.getParentModel()).copyPropertiesTo($$5);
        this.setPartVisibility($$5, $$3);
        boolean $$9 = this.usesInnerModel($$3);
        boolean $$10 = $$6.hasFoil();
        if ($$8 instanceof DyeableArmorItem) {
            int $$11 = ((DyeableArmorItem)$$8).getColor($$6);
            float $$12 = (float)($$11 >> 16 & 0xFF) / 255.0f;
            float $$13 = (float)($$11 >> 8 & 0xFF) / 255.0f;
            float $$14 = (float)($$11 & 0xFF) / 255.0f;
            this.renderModel($$0, $$1, $$4, (ArmorItem)$$8, $$10, $$5, $$9, $$12, $$13, $$14, null);
            this.renderModel($$0, $$1, $$4, (ArmorItem)$$8, $$10, $$5, $$9, 1.0f, 1.0f, 1.0f, "overlay");
        } else {
            this.renderModel($$0, $$1, $$4, (ArmorItem)$$8, $$10, $$5, $$9, 1.0f, 1.0f, 1.0f, null);
        }
        if (((LivingEntity)$$2).level.enabledFeatures().contains(FeatureFlags.UPDATE_1_20)) {
            ArmorTrim.getTrim(((LivingEntity)$$2).level.registryAccess(), $$6).ifPresent(arg_0 -> this.lambda$renderArmorPiece$0((ArmorItem)$$8, $$0, $$1, $$4, $$10, $$5, $$9, arg_0));
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

    private void renderTrim(ArmorMaterial $$0, PoseStack $$1, MultiBufferSource $$2, int $$3, ArmorTrim $$4, boolean $$5, A $$6, boolean $$7, float $$8, float $$9, float $$10) {
        TextureAtlasSprite $$11 = this.armorTrimAtlas.getSprite($$7 ? $$4.innerTexture($$0) : $$4.outerTexture($$0));
        VertexConsumer $$12 = $$11.wrap(ItemRenderer.getFoilBufferDirect($$2, Sheets.armorTrimsSheet(), true, $$5));
        ((AgeableListModel)$$6).renderToBuffer($$1, $$12, $$3, OverlayTexture.NO_OVERLAY, $$8, $$9, $$10, 1.0f);
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

    private /* synthetic */ void lambda$renderArmorPiece$0(ArmorItem $$0, PoseStack $$1, MultiBufferSource $$2, int $$3, boolean $$4, HumanoidModel $$5, boolean $$6, ArmorTrim $$7) {
        this.renderTrim($$0.getMaterial(), $$1, $$2, $$3, $$7, $$4, $$5, $$6, 1.0f, 1.0f, 1.0f);
    }
}