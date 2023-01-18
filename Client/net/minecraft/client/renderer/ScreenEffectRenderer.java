/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 *  org.joml.Matrix4f
 */
package net.minecraft.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;

public class ScreenEffectRenderer {
    private static final ResourceLocation UNDERWATER_LOCATION = new ResourceLocation("textures/misc/underwater.png");

    public static void renderScreenEffect(Minecraft $$0, PoseStack $$1) {
        BlockState $$3;
        LocalPlayer $$2 = $$0.player;
        if (!$$2.noPhysics && ($$3 = ScreenEffectRenderer.getViewBlockingState($$2)) != null) {
            ScreenEffectRenderer.renderTex($$0.getBlockRenderer().getBlockModelShaper().getParticleIcon($$3), $$1);
        }
        if (!$$0.player.isSpectator()) {
            if ($$0.player.isEyeInFluid(FluidTags.WATER)) {
                ScreenEffectRenderer.renderWater($$0, $$1);
            }
            if ($$0.player.isOnFire()) {
                ScreenEffectRenderer.renderFire($$0, $$1);
            }
        }
    }

    @Nullable
    private static BlockState getViewBlockingState(Player $$0) {
        BlockPos.MutableBlockPos $$1 = new BlockPos.MutableBlockPos();
        for (int $$2 = 0; $$2 < 8; ++$$2) {
            double $$3 = $$0.getX() + (double)(((float)(($$2 >> 0) % 2) - 0.5f) * $$0.getBbWidth() * 0.8f);
            double $$4 = $$0.getEyeY() + (double)(((float)(($$2 >> 1) % 2) - 0.5f) * 0.1f);
            double $$5 = $$0.getZ() + (double)(((float)(($$2 >> 2) % 2) - 0.5f) * $$0.getBbWidth() * 0.8f);
            $$1.set($$3, $$4, $$5);
            BlockState $$6 = $$0.level.getBlockState($$1);
            if ($$6.getRenderShape() == RenderShape.INVISIBLE || !$$6.isViewBlocking($$0.level, $$1)) continue;
            return $$6;
        }
        return null;
    }

    private static void renderTex(TextureAtlasSprite $$0, PoseStack $$1) {
        RenderSystem.setShaderTexture(0, $$0.atlasLocation());
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionColorTexShader));
        BufferBuilder $$2 = Tesselator.getInstance().getBuilder();
        float $$3 = 0.1f;
        float $$4 = -1.0f;
        float $$5 = 1.0f;
        float $$6 = -1.0f;
        float $$7 = 1.0f;
        float $$8 = -0.5f;
        float $$9 = $$0.getU0();
        float $$10 = $$0.getU1();
        float $$11 = $$0.getV0();
        float $$12 = $$0.getV1();
        Matrix4f $$13 = $$1.last().pose();
        $$2.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
        $$2.vertex($$13, -1.0f, -1.0f, -0.5f).color(0.1f, 0.1f, 0.1f, 1.0f).uv($$10, $$12).endVertex();
        $$2.vertex($$13, 1.0f, -1.0f, -0.5f).color(0.1f, 0.1f, 0.1f, 1.0f).uv($$9, $$12).endVertex();
        $$2.vertex($$13, 1.0f, 1.0f, -0.5f).color(0.1f, 0.1f, 0.1f, 1.0f).uv($$9, $$11).endVertex();
        $$2.vertex($$13, -1.0f, 1.0f, -0.5f).color(0.1f, 0.1f, 0.1f, 1.0f).uv($$10, $$11).endVertex();
        BufferUploader.drawWithShader($$2.end());
    }

    private static void renderWater(Minecraft $$0, PoseStack $$1) {
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
        RenderSystem.setShaderTexture(0, UNDERWATER_LOCATION);
        BufferBuilder $$2 = Tesselator.getInstance().getBuilder();
        BlockPos $$3 = new BlockPos($$0.player.getX(), $$0.player.getEyeY(), $$0.player.getZ());
        float $$4 = LightTexture.getBrightness($$0.player.level.dimensionType(), $$0.player.level.getMaxLocalRawBrightness($$3));
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor($$4, $$4, $$4, 0.1f);
        float $$5 = 4.0f;
        float $$6 = -1.0f;
        float $$7 = 1.0f;
        float $$8 = -1.0f;
        float $$9 = 1.0f;
        float $$10 = -0.5f;
        float $$11 = -$$0.player.getYRot() / 64.0f;
        float $$12 = $$0.player.getXRot() / 64.0f;
        Matrix4f $$13 = $$1.last().pose();
        $$2.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        $$2.vertex($$13, -1.0f, -1.0f, -0.5f).uv(4.0f + $$11, 4.0f + $$12).endVertex();
        $$2.vertex($$13, 1.0f, -1.0f, -0.5f).uv(0.0f + $$11, 4.0f + $$12).endVertex();
        $$2.vertex($$13, 1.0f, 1.0f, -0.5f).uv(0.0f + $$11, 0.0f + $$12).endVertex();
        $$2.vertex($$13, -1.0f, 1.0f, -0.5f).uv(4.0f + $$11, 0.0f + $$12).endVertex();
        BufferUploader.drawWithShader($$2.end());
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
    }

    private static void renderFire(Minecraft $$0, PoseStack $$1) {
        BufferBuilder $$2 = Tesselator.getInstance().getBuilder();
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionColorTexShader));
        RenderSystem.depthFunc(519);
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        TextureAtlasSprite $$3 = ModelBakery.FIRE_1.sprite();
        RenderSystem.setShaderTexture(0, $$3.atlasLocation());
        float $$4 = $$3.getU0();
        float $$5 = $$3.getU1();
        float $$6 = ($$4 + $$5) / 2.0f;
        float $$7 = $$3.getV0();
        float $$8 = $$3.getV1();
        float $$9 = ($$7 + $$8) / 2.0f;
        float $$10 = $$3.uvShrinkRatio();
        float $$11 = Mth.lerp($$10, $$4, $$6);
        float $$12 = Mth.lerp($$10, $$5, $$6);
        float $$13 = Mth.lerp($$10, $$7, $$9);
        float $$14 = Mth.lerp($$10, $$8, $$9);
        float $$15 = 1.0f;
        for (int $$16 = 0; $$16 < 2; ++$$16) {
            $$1.pushPose();
            float $$17 = -0.5f;
            float $$18 = 0.5f;
            float $$19 = -0.5f;
            float $$20 = 0.5f;
            float $$21 = -0.5f;
            $$1.translate((float)(-($$16 * 2 - 1)) * 0.24f, -0.3f, 0.0f);
            $$1.mulPose(Axis.YP.rotationDegrees((float)($$16 * 2 - 1) * 10.0f));
            Matrix4f $$22 = $$1.last().pose();
            $$2.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
            $$2.vertex($$22, -0.5f, -0.5f, -0.5f).color(1.0f, 1.0f, 1.0f, 0.9f).uv($$12, $$14).endVertex();
            $$2.vertex($$22, 0.5f, -0.5f, -0.5f).color(1.0f, 1.0f, 1.0f, 0.9f).uv($$11, $$14).endVertex();
            $$2.vertex($$22, 0.5f, 0.5f, -0.5f).color(1.0f, 1.0f, 1.0f, 0.9f).uv($$11, $$13).endVertex();
            $$2.vertex($$22, -0.5f, 0.5f, -0.5f).color(1.0f, 1.0f, 1.0f, 0.9f).uv($$12, $$13).endVertex();
            BufferUploader.drawWithShader($$2.end());
            $$1.popPose();
        }
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.depthFunc(515);
    }
}