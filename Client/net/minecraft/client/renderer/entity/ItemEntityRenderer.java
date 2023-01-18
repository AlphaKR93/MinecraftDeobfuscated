/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemEntityRenderer
extends EntityRenderer<ItemEntity> {
    private static final float ITEM_BUNDLE_OFFSET_SCALE = 0.15f;
    private static final int ITEM_COUNT_FOR_5_BUNDLE = 48;
    private static final int ITEM_COUNT_FOR_4_BUNDLE = 32;
    private static final int ITEM_COUNT_FOR_3_BUNDLE = 16;
    private static final int ITEM_COUNT_FOR_2_BUNDLE = 1;
    private static final float FLAT_ITEM_BUNDLE_OFFSET_X = 0.0f;
    private static final float FLAT_ITEM_BUNDLE_OFFSET_Y = 0.0f;
    private static final float FLAT_ITEM_BUNDLE_OFFSET_Z = 0.09375f;
    private final ItemRenderer itemRenderer;
    private final RandomSource random = RandomSource.create();

    public ItemEntityRenderer(EntityRendererProvider.Context $$0) {
        super($$0);
        this.itemRenderer = $$0.getItemRenderer();
        this.shadowRadius = 0.15f;
        this.shadowStrength = 0.75f;
    }

    private int getRenderAmount(ItemStack $$0) {
        int $$1 = 1;
        if ($$0.getCount() > 48) {
            $$1 = 5;
        } else if ($$0.getCount() > 32) {
            $$1 = 4;
        } else if ($$0.getCount() > 16) {
            $$1 = 3;
        } else if ($$0.getCount() > 1) {
            $$1 = 2;
        }
        return $$1;
    }

    @Override
    public void render(ItemEntity $$0, float $$1, float $$2, PoseStack $$3, MultiBufferSource $$4, int $$5) {
        $$3.pushPose();
        ItemStack $$6 = $$0.getItem();
        int $$7 = $$6.isEmpty() ? 187 : Item.getId($$6.getItem()) + $$6.getDamageValue();
        this.random.setSeed($$7);
        BakedModel $$8 = this.itemRenderer.getModel($$6, $$0.level, null, $$0.getId());
        boolean $$9 = $$8.isGui3d();
        int $$10 = this.getRenderAmount($$6);
        float $$11 = 0.25f;
        float $$12 = Mth.sin(((float)$$0.getAge() + $$2) / 10.0f + $$0.bobOffs) * 0.1f + 0.1f;
        float $$13 = $$8.getTransforms().getTransform((ItemTransforms.TransformType)ItemTransforms.TransformType.GROUND).scale.y();
        $$3.translate(0.0f, $$12 + 0.25f * $$13, 0.0f);
        float $$14 = $$0.getSpin($$2);
        $$3.mulPose(Axis.YP.rotation($$14));
        float $$15 = $$8.getTransforms().ground.scale.x();
        float $$16 = $$8.getTransforms().ground.scale.y();
        float $$17 = $$8.getTransforms().ground.scale.z();
        if (!$$9) {
            float $$18 = -0.0f * (float)($$10 - 1) * 0.5f * $$15;
            float $$19 = -0.0f * (float)($$10 - 1) * 0.5f * $$16;
            float $$20 = -0.09375f * (float)($$10 - 1) * 0.5f * $$17;
            $$3.translate($$18, $$19, $$20);
        }
        for (int $$21 = 0; $$21 < $$10; ++$$21) {
            $$3.pushPose();
            if ($$21 > 0) {
                if ($$9) {
                    float $$22 = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f;
                    float $$23 = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f;
                    float $$24 = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f;
                    $$3.translate($$22, $$23, $$24);
                } else {
                    float $$25 = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f * 0.5f;
                    float $$26 = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f * 0.5f;
                    $$3.translate($$25, $$26, 0.0f);
                }
            }
            this.itemRenderer.render($$6, ItemTransforms.TransformType.GROUND, false, $$3, $$4, $$5, OverlayTexture.NO_OVERLAY, $$8);
            $$3.popPose();
            if ($$9) continue;
            $$3.translate(0.0f * $$15, 0.0f * $$16, 0.09375f * $$17);
        }
        $$3.popPose();
        super.render($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    public ResourceLocation getTextureLocation(ItemEntity $$0) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}