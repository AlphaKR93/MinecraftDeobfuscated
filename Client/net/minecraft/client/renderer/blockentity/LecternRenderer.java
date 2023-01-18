/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Function
 */
package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.function.Function;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.EnchantTableRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class LecternRenderer
implements BlockEntityRenderer<LecternBlockEntity> {
    private final BookModel bookModel;

    public LecternRenderer(BlockEntityRendererProvider.Context $$0) {
        this.bookModel = new BookModel($$0.bakeLayer(ModelLayers.BOOK));
    }

    @Override
    public void render(LecternBlockEntity $$0, float $$1, PoseStack $$2, MultiBufferSource $$3, int $$4, int $$5) {
        BlockState $$6 = $$0.getBlockState();
        if (!$$6.getValue(LecternBlock.HAS_BOOK).booleanValue()) {
            return;
        }
        $$2.pushPose();
        $$2.translate(0.5f, 1.0625f, 0.5f);
        float $$7 = $$6.getValue(LecternBlock.FACING).getClockWise().toYRot();
        $$2.mulPose(Axis.YP.rotationDegrees(-$$7));
        $$2.mulPose(Axis.ZP.rotationDegrees(67.5f));
        $$2.translate(0.0f, -0.125f, 0.0f);
        this.bookModel.setupAnim(0.0f, 0.1f, 0.9f, 1.2f);
        VertexConsumer $$8 = EnchantTableRenderer.BOOK_LOCATION.buffer($$3, (Function<ResourceLocation, RenderType>)((Function)RenderType::entitySolid));
        this.bookModel.render($$2, $$8, $$4, $$5, 1.0f, 1.0f, 1.0f, 1.0f);
        $$2.popPose();
    }
}