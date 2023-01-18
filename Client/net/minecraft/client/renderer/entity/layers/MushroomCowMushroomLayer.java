/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.CowModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.level.block.state.BlockState;

public class MushroomCowMushroomLayer<T extends MushroomCow>
extends RenderLayer<T, CowModel<T>> {
    private final BlockRenderDispatcher blockRenderer;

    public MushroomCowMushroomLayer(RenderLayerParent<T, CowModel<T>> $$0, BlockRenderDispatcher $$1) {
        super($$0);
        this.blockRenderer = $$1;
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, T $$3, float $$4, float $$5, float $$6, float $$7, float $$8, float $$9) {
        boolean $$11;
        if (((AgeableMob)$$3).isBaby()) {
            return;
        }
        Minecraft $$10 = Minecraft.getInstance();
        boolean bl = $$11 = $$10.shouldEntityAppearGlowing((Entity)$$3) && ((Entity)$$3).isInvisible();
        if (((Entity)$$3).isInvisible() && !$$11) {
            return;
        }
        BlockState $$12 = ((MushroomCow)$$3).getVariant().getBlockState();
        int $$13 = LivingEntityRenderer.getOverlayCoords($$3, 0.0f);
        BakedModel $$14 = this.blockRenderer.getBlockModel($$12);
        $$0.pushPose();
        $$0.translate(0.2f, -0.35f, 0.5f);
        $$0.mulPose(Axis.YP.rotationDegrees(-48.0f));
        $$0.scale(-1.0f, -1.0f, 1.0f);
        $$0.translate(-0.5f, -0.5f, -0.5f);
        this.renderMushroomBlock($$0, $$1, $$2, $$11, $$12, $$13, $$14);
        $$0.popPose();
        $$0.pushPose();
        $$0.translate(0.2f, -0.35f, 0.5f);
        $$0.mulPose(Axis.YP.rotationDegrees(42.0f));
        $$0.translate(0.1f, 0.0f, -0.6f);
        $$0.mulPose(Axis.YP.rotationDegrees(-48.0f));
        $$0.scale(-1.0f, -1.0f, 1.0f);
        $$0.translate(-0.5f, -0.5f, -0.5f);
        this.renderMushroomBlock($$0, $$1, $$2, $$11, $$12, $$13, $$14);
        $$0.popPose();
        $$0.pushPose();
        ((CowModel)this.getParentModel()).getHead().translateAndRotate($$0);
        $$0.translate(0.0f, -0.7f, -0.2f);
        $$0.mulPose(Axis.YP.rotationDegrees(-78.0f));
        $$0.scale(-1.0f, -1.0f, 1.0f);
        $$0.translate(-0.5f, -0.5f, -0.5f);
        this.renderMushroomBlock($$0, $$1, $$2, $$11, $$12, $$13, $$14);
        $$0.popPose();
    }

    private void renderMushroomBlock(PoseStack $$0, MultiBufferSource $$1, int $$2, boolean $$3, BlockState $$4, int $$5, BakedModel $$6) {
        if ($$3) {
            this.blockRenderer.getModelRenderer().renderModel($$0.last(), $$1.getBuffer(RenderType.outline(TextureAtlas.LOCATION_BLOCKS)), $$4, $$6, 0.0f, 0.0f, 0.0f, $$2, $$5);
        } else {
            this.blockRenderer.renderSingleBlock($$4, $$0, $$1, $$2, $$5);
        }
    }
}