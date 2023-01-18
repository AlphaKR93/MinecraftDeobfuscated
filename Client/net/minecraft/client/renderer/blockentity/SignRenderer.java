/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.List
 *  java.util.Map
 *  java.util.function.Function
 */
package net.minecraft.client.renderer.blockentity;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.Material;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.Vec3;

public class SignRenderer
implements BlockEntityRenderer<SignBlockEntity> {
    private static final String STICK = "stick";
    private static final int BLACK_TEXT_OUTLINE_COLOR = -988212;
    private static final int OUTLINE_RENDER_DISTANCE = Mth.square(16);
    private final Map<WoodType, SignModel> signModels = (Map)WoodType.values().collect(ImmutableMap.toImmutableMap($$0 -> $$0, $$1 -> new SignModel($$02.bakeLayer(ModelLayers.createSignModelName($$1)))));
    private final Font font;

    public SignRenderer(BlockEntityRendererProvider.Context $$02) {
        this.font = $$02.getFont();
    }

    @Override
    public void render(SignBlockEntity $$0, float $$1, PoseStack $$2, MultiBufferSource $$3, int $$4, int $$5) {
        BlockState $$6 = $$0.getBlockState();
        $$2.pushPose();
        float $$7 = 0.6666667f;
        WoodType $$8 = SignBlock.getWoodType($$6.getBlock());
        SignModel $$9 = (SignModel)this.signModels.get((Object)$$8);
        if ($$6.getBlock() instanceof StandingSignBlock) {
            $$2.translate(0.5f, 0.5f, 0.5f);
            float $$10 = -RotationSegment.convertToDegrees($$6.getValue(StandingSignBlock.ROTATION));
            $$2.mulPose(Axis.YP.rotationDegrees($$10));
            $$9.stick.visible = true;
        } else {
            $$2.translate(0.5f, 0.5f, 0.5f);
            float $$11 = -$$6.getValue(WallSignBlock.FACING).toYRot();
            $$2.mulPose(Axis.YP.rotationDegrees($$11));
            $$2.translate(0.0f, -0.3125f, -0.4375f);
            $$9.stick.visible = false;
        }
        this.renderSign($$2, $$3, $$4, $$5, 0.6666667f, $$8, $$9);
        this.renderSignText($$0, $$2, $$3, $$4, 0.6666667f);
    }

    void renderSign(PoseStack $$0, MultiBufferSource $$1, int $$2, int $$3, float $$4, WoodType $$5, Model $$6) {
        $$0.pushPose();
        $$0.scale($$4, -$$4, -$$4);
        Material $$7 = this.getSignMaterial($$5);
        VertexConsumer $$8 = $$7.buffer($$1, (Function<ResourceLocation, RenderType>)((Function)$$6::renderType));
        this.renderSignModel($$0, $$2, $$3, $$6, $$8);
        $$0.popPose();
    }

    void renderSignModel(PoseStack $$0, int $$1, int $$2, Model $$3, VertexConsumer $$4) {
        SignModel $$5 = (SignModel)$$3;
        $$5.root.render($$0, $$4, $$1, $$2);
    }

    Material getSignMaterial(WoodType $$0) {
        return Sheets.getSignMaterial($$0);
    }

    void renderSignText(SignBlockEntity $$0, PoseStack $$12, MultiBufferSource $$2, int $$3, float $$4) {
        int $$15;
        boolean $$14;
        int $$13;
        float $$5 = 0.015625f * $$4;
        Vec3 $$6 = this.getTextOffset($$4);
        $$12.translate($$6.x, $$6.y, $$6.z);
        $$12.scale($$5, -$$5, $$5);
        int $$7 = SignRenderer.getDarkColor($$0);
        int $$8 = 4 * $$0.getTextLineHeight() / 2;
        FormattedCharSequence[] $$9 = $$0.getRenderMessages(Minecraft.getInstance().isTextFilteringEnabled(), (Function<Component, FormattedCharSequence>)((Function)$$1 -> {
            List<FormattedCharSequence> $$2 = this.font.split((FormattedText)$$1, $$0.getMaxTextLineWidth());
            return $$2.isEmpty() ? FormattedCharSequence.EMPTY : (FormattedCharSequence)$$2.get(0);
        }));
        if ($$0.hasGlowingText()) {
            int $$10 = $$0.getColor().getTextColor();
            boolean $$11 = SignRenderer.isOutlineVisible($$0, $$10);
            int $$122 = 0xF000F0;
        } else {
            $$13 = $$7;
            $$14 = false;
            $$15 = $$3;
        }
        for (int $$16 = 0; $$16 < 4; ++$$16) {
            FormattedCharSequence $$17 = $$9[$$16];
            float $$18 = -this.font.width($$17) / 2;
            if ($$14) {
                this.font.drawInBatch8xOutline($$17, $$18, $$16 * $$0.getTextLineHeight() - $$8, $$13, $$7, $$12.last().pose(), $$2, $$15);
                continue;
            }
            this.font.drawInBatch($$17, $$18, (float)($$16 * $$0.getTextLineHeight() - $$8), $$13, false, $$12.last().pose(), $$2, false, 0, $$15);
        }
        $$12.popPose();
    }

    Vec3 getTextOffset(float $$0) {
        return new Vec3(0.0, 0.5f * $$0, 0.07f * $$0);
    }

    static boolean isOutlineVisible(SignBlockEntity $$0, int $$1) {
        if ($$1 == DyeColor.BLACK.getTextColor()) {
            return true;
        }
        Minecraft $$2 = Minecraft.getInstance();
        LocalPlayer $$3 = $$2.player;
        if ($$3 != null && $$2.options.getCameraType().isFirstPerson() && $$3.isScoping()) {
            return true;
        }
        Entity $$4 = $$2.getCameraEntity();
        return $$4 != null && $$4.distanceToSqr(Vec3.atCenterOf($$0.getBlockPos())) < (double)OUTLINE_RENDER_DISTANCE;
    }

    static int getDarkColor(SignBlockEntity $$0) {
        int $$1 = $$0.getColor().getTextColor();
        double $$2 = 0.4;
        int $$3 = (int)((double)NativeImage.getR($$1) * 0.4);
        int $$4 = (int)((double)NativeImage.getG($$1) * 0.4);
        int $$5 = (int)((double)NativeImage.getB($$1) * 0.4);
        if ($$1 == DyeColor.BLACK.getTextColor() && $$0.hasGlowingText()) {
            return -988212;
        }
        return NativeImage.combine(0, $$5, $$4, $$3);
    }

    public static SignModel createSignModel(EntityModelSet $$0, WoodType $$1) {
        return new SignModel($$0.bakeLayer(ModelLayers.createSignModelName($$1)));
    }

    public static LayerDefinition createSignLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        $$1.addOrReplaceChild("sign", CubeListBuilder.create().texOffs(0, 0).addBox(-12.0f, -14.0f, -1.0f, 24.0f, 12.0f, 2.0f), PartPose.ZERO);
        $$1.addOrReplaceChild(STICK, CubeListBuilder.create().texOffs(0, 14).addBox(-1.0f, -2.0f, -1.0f, 2.0f, 14.0f, 2.0f), PartPose.ZERO);
        return LayerDefinition.create($$0, 64, 32);
    }

    public static final class SignModel
    extends Model {
        public final ModelPart root;
        public final ModelPart stick;

        public SignModel(ModelPart $$0) {
            super((Function<ResourceLocation, RenderType>)((Function)RenderType::entityCutoutNoCull));
            this.root = $$0;
            this.stick = $$0.getChild(SignRenderer.STICK);
        }

        @Override
        public void renderToBuffer(PoseStack $$0, VertexConsumer $$1, int $$2, int $$3, float $$4, float $$5, float $$6, float $$7) {
            this.root.render($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
        }
    }
}