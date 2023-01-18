/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Function
 */
package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.function.Function;
import net.minecraft.client.model.ShulkerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ShulkerBoxRenderer
implements BlockEntityRenderer<ShulkerBoxBlockEntity> {
    private final ShulkerModel<?> model;

    public ShulkerBoxRenderer(BlockEntityRendererProvider.Context $$0) {
        this.model = new ShulkerModel($$0.bakeLayer(ModelLayers.SHULKER));
    }

    @Override
    public void render(ShulkerBoxBlockEntity $$0, float $$1, PoseStack $$2, MultiBufferSource $$3, int $$4, int $$5) {
        Material $$10;
        DyeColor $$8;
        BlockState $$7;
        Direction $$6 = Direction.UP;
        if ($$0.hasLevel() && ($$7 = $$0.getLevel().getBlockState($$0.getBlockPos())).getBlock() instanceof ShulkerBoxBlock) {
            $$6 = $$7.getValue(ShulkerBoxBlock.FACING);
        }
        if (($$8 = $$0.getColor()) == null) {
            Material $$9 = Sheets.DEFAULT_SHULKER_TEXTURE_LOCATION;
        } else {
            $$10 = (Material)Sheets.SHULKER_TEXTURE_LOCATION.get($$8.getId());
        }
        $$2.pushPose();
        $$2.translate(0.5f, 0.5f, 0.5f);
        float $$11 = 0.9995f;
        $$2.scale(0.9995f, 0.9995f, 0.9995f);
        $$2.mulPose($$6.getRotation());
        $$2.scale(1.0f, -1.0f, -1.0f);
        $$2.translate(0.0f, -1.0f, 0.0f);
        ModelPart $$12 = this.model.getLid();
        $$12.setPos(0.0f, 24.0f - $$0.getProgress($$1) * 0.5f * 16.0f, 0.0f);
        $$12.yRot = 270.0f * $$0.getProgress($$1) * ((float)Math.PI / 180);
        VertexConsumer $$13 = $$10.buffer($$3, (Function<ResourceLocation, RenderType>)((Function)RenderType::entityCutoutNoCull));
        this.model.renderToBuffer($$2, $$13, $$4, $$5, 1.0f, 1.0f, 1.0f, 1.0f);
        $$2.popPose();
    }
}