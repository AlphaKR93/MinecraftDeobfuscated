/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.List
 *  java.util.function.Function
 */
package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import java.util.List;
import java.util.function.Function;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.WallBannerBlock;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RotationSegment;

public class BannerRenderer
implements BlockEntityRenderer<BannerBlockEntity> {
    private static final int BANNER_WIDTH = 20;
    private static final int BANNER_HEIGHT = 40;
    private static final int MAX_PATTERNS = 16;
    public static final String FLAG = "flag";
    private static final String POLE = "pole";
    private static final String BAR = "bar";
    private final ModelPart flag;
    private final ModelPart pole;
    private final ModelPart bar;

    public BannerRenderer(BlockEntityRendererProvider.Context $$0) {
        ModelPart $$1 = $$0.bakeLayer(ModelLayers.BANNER);
        this.flag = $$1.getChild(FLAG);
        this.pole = $$1.getChild(POLE);
        this.bar = $$1.getChild(BAR);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        $$1.addOrReplaceChild(FLAG, CubeListBuilder.create().texOffs(0, 0).addBox(-10.0f, 0.0f, -2.0f, 20.0f, 40.0f, 1.0f), PartPose.ZERO);
        $$1.addOrReplaceChild(POLE, CubeListBuilder.create().texOffs(44, 0).addBox(-1.0f, -30.0f, -1.0f, 2.0f, 42.0f, 2.0f), PartPose.ZERO);
        $$1.addOrReplaceChild(BAR, CubeListBuilder.create().texOffs(0, 42).addBox(-10.0f, -32.0f, -1.0f, 20.0f, 2.0f, 2.0f), PartPose.ZERO);
        return LayerDefinition.create($$0, 64, 64);
    }

    @Override
    public void render(BannerBlockEntity $$0, float $$1, PoseStack $$2, MultiBufferSource $$3, int $$4, int $$5) {
        long $$10;
        List<Pair<Holder<BannerPattern>, DyeColor>> $$6 = $$0.getPatterns();
        float $$7 = 0.6666667f;
        boolean $$8 = $$0.getLevel() == null;
        $$2.pushPose();
        if ($$8) {
            long $$9 = 0L;
            $$2.translate(0.5f, 0.5f, 0.5f);
            this.pole.visible = true;
        } else {
            $$10 = $$0.getLevel().getGameTime();
            BlockState $$11 = $$0.getBlockState();
            if ($$11.getBlock() instanceof BannerBlock) {
                $$2.translate(0.5f, 0.5f, 0.5f);
                float $$12 = -RotationSegment.convertToDegrees($$11.getValue(BannerBlock.ROTATION));
                $$2.mulPose(Axis.YP.rotationDegrees($$12));
                this.pole.visible = true;
            } else {
                $$2.translate(0.5f, -0.16666667f, 0.5f);
                float $$13 = -$$11.getValue(WallBannerBlock.FACING).toYRot();
                $$2.mulPose(Axis.YP.rotationDegrees($$13));
                $$2.translate(0.0f, -0.3125f, -0.4375f);
                this.pole.visible = false;
            }
        }
        $$2.pushPose();
        $$2.scale(0.6666667f, -0.6666667f, -0.6666667f);
        VertexConsumer $$14 = ModelBakery.BANNER_BASE.buffer($$3, (Function<ResourceLocation, RenderType>)((Function)RenderType::entitySolid));
        this.pole.render($$2, $$14, $$4, $$5);
        this.bar.render($$2, $$14, $$4, $$5);
        BlockPos $$15 = $$0.getBlockPos();
        float $$16 = ((float)Math.floorMod((long)((long)($$15.getX() * 7 + $$15.getY() * 9 + $$15.getZ() * 13) + $$10), (long)100L) + $$1) / 100.0f;
        this.flag.xRot = (-0.0125f + 0.01f * Mth.cos((float)Math.PI * 2 * $$16)) * (float)Math.PI;
        this.flag.y = -32.0f;
        BannerRenderer.renderPatterns($$2, $$3, $$4, $$5, this.flag, ModelBakery.BANNER_BASE, true, $$6);
        $$2.popPose();
        $$2.popPose();
    }

    public static void renderPatterns(PoseStack $$0, MultiBufferSource $$1, int $$2, int $$3, ModelPart $$4, Material $$5, boolean $$6, List<Pair<Holder<BannerPattern>, DyeColor>> $$7) {
        BannerRenderer.renderPatterns($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, false);
    }

    public static void renderPatterns(PoseStack $$0, MultiBufferSource $$12, int $$2, int $$3, ModelPart $$4, Material $$5, boolean $$62, List<Pair<Holder<BannerPattern>, DyeColor>> $$7, boolean $$8) {
        $$4.render($$0, $$5.buffer($$12, (Function<ResourceLocation, RenderType>)((Function)RenderType::entitySolid), $$8), $$2, $$3);
        for (int $$9 = 0; $$9 < 17 && $$9 < $$7.size(); ++$$9) {
            Pair $$10 = (Pair)$$7.get($$9);
            float[] $$11 = ((DyeColor)$$10.getSecond()).getTextureDiffuseColors();
            ((Holder)$$10.getFirst()).unwrapKey().map($$1 -> $$62 ? Sheets.getBannerMaterial($$1) : Sheets.getShieldMaterial($$1)).ifPresent($$6 -> $$4.render($$0, $$6.buffer($$12, (Function<ResourceLocation, RenderType>)((Function)RenderType::entityNoOutline)), $$2, $$3, $$11[0], $$11[1], $$11[2], 1.0f));
        }
    }
}