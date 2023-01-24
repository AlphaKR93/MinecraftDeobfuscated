/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.OptionalInt
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.OptionalInt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.Vec3;

public class ItemFrameRenderer<T extends ItemFrame>
extends EntityRenderer<T> {
    private static final ModelResourceLocation FRAME_LOCATION = ModelResourceLocation.vanilla("item_frame", "map=false");
    private static final ModelResourceLocation MAP_FRAME_LOCATION = ModelResourceLocation.vanilla("item_frame", "map=true");
    private static final ModelResourceLocation GLOW_FRAME_LOCATION = ModelResourceLocation.vanilla("glow_item_frame", "map=false");
    private static final ModelResourceLocation GLOW_MAP_FRAME_LOCATION = ModelResourceLocation.vanilla("glow_item_frame", "map=true");
    public static final int GLOW_FRAME_BRIGHTNESS = 5;
    public static final int BRIGHT_MAP_LIGHT_ADJUSTMENT = 30;
    private final ItemRenderer itemRenderer;
    private final BlockRenderDispatcher blockRenderer;

    public ItemFrameRenderer(EntityRendererProvider.Context $$0) {
        super($$0);
        this.itemRenderer = $$0.getItemRenderer();
        this.blockRenderer = $$0.getBlockRenderDispatcher();
    }

    @Override
    protected int getBlockLightLevel(T $$0, BlockPos $$1) {
        if (((Entity)$$0).getType() == EntityType.GLOW_ITEM_FRAME) {
            return Math.max((int)5, (int)super.getBlockLightLevel($$0, $$1));
        }
        return super.getBlockLightLevel($$0, $$1);
    }

    @Override
    public void render(T $$0, float $$1, float $$2, PoseStack $$3, MultiBufferSource $$4, int $$5) {
        super.render($$0, $$1, $$2, $$3, $$4, $$5);
        $$3.pushPose();
        Direction $$6 = ((HangingEntity)$$0).getDirection();
        Vec3 $$7 = this.getRenderOffset($$0, $$2);
        $$3.translate(-$$7.x(), -$$7.y(), -$$7.z());
        double $$8 = 0.46875;
        $$3.translate((double)$$6.getStepX() * 0.46875, (double)$$6.getStepY() * 0.46875, (double)$$6.getStepZ() * 0.46875);
        $$3.mulPose(Axis.XP.rotationDegrees(((Entity)$$0).getXRot()));
        $$3.mulPose(Axis.YP.rotationDegrees(180.0f - ((Entity)$$0).getYRot()));
        boolean $$9 = ((Entity)$$0).isInvisible();
        ItemStack $$10 = ((ItemFrame)$$0).getItem();
        if (!$$9) {
            ModelManager $$11 = this.blockRenderer.getBlockModelShaper().getModelManager();
            ModelResourceLocation $$12 = this.getFrameModelResourceLoc($$0, $$10);
            $$3.pushPose();
            $$3.translate(-0.5f, -0.5f, -0.5f);
            this.blockRenderer.getModelRenderer().renderModel($$3.last(), $$4.getBuffer(Sheets.solidBlockSheet()), null, $$11.getModel($$12), 1.0f, 1.0f, 1.0f, $$5, OverlayTexture.NO_OVERLAY);
            $$3.popPose();
        }
        if (!$$10.isEmpty()) {
            OptionalInt $$13 = ((ItemFrame)$$0).getFramedMapId();
            if ($$9) {
                $$3.translate(0.0f, 0.0f, 0.5f);
            } else {
                $$3.translate(0.0f, 0.0f, 0.4375f);
            }
            int $$14 = $$13.isPresent() ? ((ItemFrame)$$0).getRotation() % 4 * 2 : ((ItemFrame)$$0).getRotation();
            $$3.mulPose(Axis.ZP.rotationDegrees((float)$$14 * 360.0f / 8.0f));
            if ($$13.isPresent()) {
                $$3.mulPose(Axis.ZP.rotationDegrees(180.0f));
                float $$15 = 0.0078125f;
                $$3.scale(0.0078125f, 0.0078125f, 0.0078125f);
                $$3.translate(-64.0f, -64.0f, 0.0f);
                MapItemSavedData $$16 = MapItem.getSavedData($$13.getAsInt(), ((ItemFrame)$$0).level);
                $$3.translate(0.0f, 0.0f, -1.0f);
                if ($$16 != null) {
                    int $$17 = this.getLightVal($$0, 15728850, $$5);
                    Minecraft.getInstance().gameRenderer.getMapRenderer().render($$3, $$4, $$13.getAsInt(), $$16, true, $$17);
                }
            } else {
                int $$18 = this.getLightVal($$0, 0xF000F0, $$5);
                $$3.scale(0.5f, 0.5f, 0.5f);
                this.itemRenderer.renderStatic($$10, ItemTransforms.TransformType.FIXED, $$18, OverlayTexture.NO_OVERLAY, $$3, $$4, ((ItemFrame)$$0).level, ((Entity)$$0).getId());
            }
        }
        $$3.popPose();
    }

    private int getLightVal(T $$0, int $$1, int $$2) {
        return ((Entity)$$0).getType() == EntityType.GLOW_ITEM_FRAME ? $$1 : $$2;
    }

    private ModelResourceLocation getFrameModelResourceLoc(T $$0, ItemStack $$1) {
        boolean $$2;
        boolean bl = $$2 = ((Entity)$$0).getType() == EntityType.GLOW_ITEM_FRAME;
        if ($$1.is(Items.FILLED_MAP)) {
            return $$2 ? GLOW_MAP_FRAME_LOCATION : MAP_FRAME_LOCATION;
        }
        return $$2 ? GLOW_FRAME_LOCATION : FRAME_LOCATION;
    }

    @Override
    public Vec3 getRenderOffset(T $$0, float $$1) {
        return new Vec3((float)((HangingEntity)$$0).getDirection().getStepX() * 0.3f, -0.25, (float)((HangingEntity)$$0).getDirection().getStepZ() * 0.3f);
    }

    @Override
    public ResourceLocation getTextureLocation(T $$0) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

    @Override
    protected boolean shouldShowName(T $$0) {
        if (!Minecraft.renderNames() || ((ItemFrame)$$0).getItem().isEmpty() || !((ItemFrame)$$0).getItem().hasCustomHoverName() || this.entityRenderDispatcher.crosshairPickEntity != $$0) {
            return false;
        }
        double $$1 = this.entityRenderDispatcher.distanceToSqr((Entity)$$0);
        float $$2 = ((Entity)$$0).isDiscrete() ? 32.0f : 64.0f;
        return $$1 < (double)($$2 * $$2);
    }

    @Override
    protected void renderNameTag(T $$0, Component $$1, PoseStack $$2, MultiBufferSource $$3, int $$4) {
        super.renderNameTag($$0, ((ItemFrame)$$0).getItem().getHoverName(), $$2, $$3, $$4);
    }
}