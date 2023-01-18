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
import com.mojang.math.Axis;
import java.util.function.Function;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.EnchantmentTableBlockEntity;

public class EnchantTableRenderer
implements BlockEntityRenderer<EnchantmentTableBlockEntity> {
    public static final Material BOOK_LOCATION = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("entity/enchanting_table_book"));
    private final BookModel bookModel;

    public EnchantTableRenderer(BlockEntityRendererProvider.Context $$0) {
        this.bookModel = new BookModel($$0.bakeLayer(ModelLayers.BOOK));
    }

    @Override
    public void render(EnchantmentTableBlockEntity $$0, float $$1, PoseStack $$2, MultiBufferSource $$3, int $$4, int $$5) {
        float $$7;
        $$2.pushPose();
        $$2.translate(0.5f, 0.75f, 0.5f);
        float $$6 = (float)$$0.time + $$1;
        $$2.translate(0.0f, 0.1f + Mth.sin($$6 * 0.1f) * 0.01f, 0.0f);
        for ($$7 = $$0.rot - $$0.oRot; $$7 >= (float)Math.PI; $$7 -= (float)Math.PI * 2) {
        }
        while ($$7 < (float)(-Math.PI)) {
            $$7 += (float)Math.PI * 2;
        }
        float $$8 = $$0.oRot + $$7 * $$1;
        $$2.mulPose(Axis.YP.rotation(-$$8));
        $$2.mulPose(Axis.ZP.rotationDegrees(80.0f));
        float $$9 = Mth.lerp($$1, $$0.oFlip, $$0.flip);
        float $$10 = Mth.frac($$9 + 0.25f) * 1.6f - 0.3f;
        float $$11 = Mth.frac($$9 + 0.75f) * 1.6f - 0.3f;
        float $$12 = Mth.lerp($$1, $$0.oOpen, $$0.open);
        this.bookModel.setupAnim($$6, Mth.clamp($$10, 0.0f, 1.0f), Mth.clamp($$11, 0.0f, 1.0f), $$12);
        VertexConsumer $$13 = BOOK_LOCATION.buffer($$3, (Function<ResourceLocation, RenderType>)((Function)RenderType::entitySolid));
        this.bookModel.render($$2, $$13, $$4, $$5, 1.0f, 1.0f, 1.0f, 1.0f);
        $$2.popPose();
    }
}