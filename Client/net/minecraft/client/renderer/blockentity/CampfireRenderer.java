/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;

public class CampfireRenderer
implements BlockEntityRenderer<CampfireBlockEntity> {
    private static final float SIZE = 0.375f;
    private final ItemRenderer itemRenderer;

    public CampfireRenderer(BlockEntityRendererProvider.Context $$0) {
        this.itemRenderer = $$0.getItemRenderer();
    }

    @Override
    public void render(CampfireBlockEntity $$0, float $$1, PoseStack $$2, MultiBufferSource $$3, int $$4, int $$5) {
        Direction $$6 = $$0.getBlockState().getValue(CampfireBlock.FACING);
        NonNullList<ItemStack> $$7 = $$0.getItems();
        int $$8 = (int)$$0.getBlockPos().asLong();
        for (int $$9 = 0; $$9 < $$7.size(); ++$$9) {
            ItemStack $$10 = $$7.get($$9);
            if ($$10 == ItemStack.EMPTY) continue;
            $$2.pushPose();
            $$2.translate(0.5f, 0.44921875f, 0.5f);
            Direction $$11 = Direction.from2DDataValue(($$9 + $$6.get2DDataValue()) % 4);
            float $$12 = -$$11.toYRot();
            $$2.mulPose(Axis.YP.rotationDegrees($$12));
            $$2.mulPose(Axis.XP.rotationDegrees(90.0f));
            $$2.translate(-0.3125f, -0.3125f, 0.0f);
            $$2.scale(0.375f, 0.375f, 0.375f);
            this.itemRenderer.renderStatic($$10, ItemTransforms.TransformType.FIXED, $$4, $$5, $$2, $$3, $$8 + $$9);
            $$2.popPose();
        }
    }
}