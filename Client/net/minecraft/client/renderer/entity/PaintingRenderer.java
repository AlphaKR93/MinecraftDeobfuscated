/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  org.joml.Matrix3f
 *  org.joml.Matrix4f
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.PaintingTextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class PaintingRenderer
extends EntityRenderer<Painting> {
    public PaintingRenderer(EntityRendererProvider.Context $$0) {
        super($$0);
    }

    @Override
    public void render(Painting $$0, float $$1, float $$2, PoseStack $$3, MultiBufferSource $$4, int $$5) {
        $$3.pushPose();
        $$3.mulPose(Axis.YP.rotationDegrees(180.0f - $$1));
        PaintingVariant $$6 = (PaintingVariant)$$0.getVariant().value();
        float $$7 = 0.0625f;
        $$3.scale(0.0625f, 0.0625f, 0.0625f);
        VertexConsumer $$8 = $$4.getBuffer(RenderType.entitySolid(this.getTextureLocation($$0)));
        PaintingTextureManager $$9 = Minecraft.getInstance().getPaintingTextures();
        this.renderPainting($$3, $$8, $$0, $$6.getWidth(), $$6.getHeight(), $$9.get($$6), $$9.getBackSprite());
        $$3.popPose();
        super.render($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    public ResourceLocation getTextureLocation(Painting $$0) {
        return Minecraft.getInstance().getPaintingTextures().getBackSprite().atlasLocation();
    }

    private void renderPainting(PoseStack $$0, VertexConsumer $$1, Painting $$2, int $$3, int $$4, TextureAtlasSprite $$5, TextureAtlasSprite $$6) {
        PoseStack.Pose $$7 = $$0.last();
        Matrix4f $$8 = $$7.pose();
        Matrix3f $$9 = $$7.normal();
        float $$10 = (float)(-$$3) / 2.0f;
        float $$11 = (float)(-$$4) / 2.0f;
        float $$12 = 0.5f;
        float $$13 = $$6.getU0();
        float $$14 = $$6.getU1();
        float $$15 = $$6.getV0();
        float $$16 = $$6.getV1();
        float $$17 = $$6.getU0();
        float $$18 = $$6.getU1();
        float $$19 = $$6.getV0();
        float $$20 = $$6.getV(1.0);
        float $$21 = $$6.getU0();
        float $$22 = $$6.getU(1.0);
        float $$23 = $$6.getV0();
        float $$24 = $$6.getV1();
        int $$25 = $$3 / 16;
        int $$26 = $$4 / 16;
        double $$27 = 16.0 / (double)$$25;
        double $$28 = 16.0 / (double)$$26;
        for (int $$29 = 0; $$29 < $$25; ++$$29) {
            for (int $$30 = 0; $$30 < $$26; ++$$30) {
                float $$31 = $$10 + (float)(($$29 + 1) * 16);
                float $$32 = $$10 + (float)($$29 * 16);
                float $$33 = $$11 + (float)(($$30 + 1) * 16);
                float $$34 = $$11 + (float)($$30 * 16);
                int $$35 = $$2.getBlockX();
                int $$36 = Mth.floor($$2.getY() + (double)(($$33 + $$34) / 2.0f / 16.0f));
                int $$37 = $$2.getBlockZ();
                Direction $$38 = $$2.getDirection();
                if ($$38 == Direction.NORTH) {
                    $$35 = Mth.floor($$2.getX() + (double)(($$31 + $$32) / 2.0f / 16.0f));
                }
                if ($$38 == Direction.WEST) {
                    $$37 = Mth.floor($$2.getZ() - (double)(($$31 + $$32) / 2.0f / 16.0f));
                }
                if ($$38 == Direction.SOUTH) {
                    $$35 = Mth.floor($$2.getX() - (double)(($$31 + $$32) / 2.0f / 16.0f));
                }
                if ($$38 == Direction.EAST) {
                    $$37 = Mth.floor($$2.getZ() + (double)(($$31 + $$32) / 2.0f / 16.0f));
                }
                int $$39 = LevelRenderer.getLightColor($$2.level, new BlockPos($$35, $$36, $$37));
                float $$40 = $$5.getU($$27 * (double)($$25 - $$29));
                float $$41 = $$5.getU($$27 * (double)($$25 - ($$29 + 1)));
                float $$42 = $$5.getV($$28 * (double)($$26 - $$30));
                float $$43 = $$5.getV($$28 * (double)($$26 - ($$30 + 1)));
                this.vertex($$8, $$9, $$1, $$31, $$34, $$41, $$42, -0.5f, 0, 0, -1, $$39);
                this.vertex($$8, $$9, $$1, $$32, $$34, $$40, $$42, -0.5f, 0, 0, -1, $$39);
                this.vertex($$8, $$9, $$1, $$32, $$33, $$40, $$43, -0.5f, 0, 0, -1, $$39);
                this.vertex($$8, $$9, $$1, $$31, $$33, $$41, $$43, -0.5f, 0, 0, -1, $$39);
                this.vertex($$8, $$9, $$1, $$31, $$33, $$14, $$15, 0.5f, 0, 0, 1, $$39);
                this.vertex($$8, $$9, $$1, $$32, $$33, $$13, $$15, 0.5f, 0, 0, 1, $$39);
                this.vertex($$8, $$9, $$1, $$32, $$34, $$13, $$16, 0.5f, 0, 0, 1, $$39);
                this.vertex($$8, $$9, $$1, $$31, $$34, $$14, $$16, 0.5f, 0, 0, 1, $$39);
                this.vertex($$8, $$9, $$1, $$31, $$33, $$17, $$19, -0.5f, 0, 1, 0, $$39);
                this.vertex($$8, $$9, $$1, $$32, $$33, $$18, $$19, -0.5f, 0, 1, 0, $$39);
                this.vertex($$8, $$9, $$1, $$32, $$33, $$18, $$20, 0.5f, 0, 1, 0, $$39);
                this.vertex($$8, $$9, $$1, $$31, $$33, $$17, $$20, 0.5f, 0, 1, 0, $$39);
                this.vertex($$8, $$9, $$1, $$31, $$34, $$17, $$19, 0.5f, 0, -1, 0, $$39);
                this.vertex($$8, $$9, $$1, $$32, $$34, $$18, $$19, 0.5f, 0, -1, 0, $$39);
                this.vertex($$8, $$9, $$1, $$32, $$34, $$18, $$20, -0.5f, 0, -1, 0, $$39);
                this.vertex($$8, $$9, $$1, $$31, $$34, $$17, $$20, -0.5f, 0, -1, 0, $$39);
                this.vertex($$8, $$9, $$1, $$31, $$33, $$22, $$23, 0.5f, -1, 0, 0, $$39);
                this.vertex($$8, $$9, $$1, $$31, $$34, $$22, $$24, 0.5f, -1, 0, 0, $$39);
                this.vertex($$8, $$9, $$1, $$31, $$34, $$21, $$24, -0.5f, -1, 0, 0, $$39);
                this.vertex($$8, $$9, $$1, $$31, $$33, $$21, $$23, -0.5f, -1, 0, 0, $$39);
                this.vertex($$8, $$9, $$1, $$32, $$33, $$22, $$23, -0.5f, 1, 0, 0, $$39);
                this.vertex($$8, $$9, $$1, $$32, $$34, $$22, $$24, -0.5f, 1, 0, 0, $$39);
                this.vertex($$8, $$9, $$1, $$32, $$34, $$21, $$24, 0.5f, 1, 0, 0, $$39);
                this.vertex($$8, $$9, $$1, $$32, $$33, $$21, $$23, 0.5f, 1, 0, 0, $$39);
            }
        }
    }

    private void vertex(Matrix4f $$0, Matrix3f $$1, VertexConsumer $$2, float $$3, float $$4, float $$5, float $$6, float $$7, int $$8, int $$9, int $$10, int $$11) {
        $$2.vertex($$0, $$3, $$4, $$7).color(255, 255, 255, 255).uv($$5, $$6).overlayCoords(OverlayTexture.NO_OVERLAY).uv2($$11).normal($$1, $$8, $$9, $$10).endVertex();
    }
}