/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 *  org.joml.Matrix3f
 *  org.joml.Matrix4f
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import javax.annotation.Nullable;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EndCrystalRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class EnderDragonRenderer
extends EntityRenderer<EnderDragon> {
    public static final ResourceLocation CRYSTAL_BEAM_LOCATION = new ResourceLocation("textures/entity/end_crystal/end_crystal_beam.png");
    private static final ResourceLocation DRAGON_EXPLODING_LOCATION = new ResourceLocation("textures/entity/enderdragon/dragon_exploding.png");
    private static final ResourceLocation DRAGON_LOCATION = new ResourceLocation("textures/entity/enderdragon/dragon.png");
    private static final ResourceLocation DRAGON_EYES_LOCATION = new ResourceLocation("textures/entity/enderdragon/dragon_eyes.png");
    private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(DRAGON_LOCATION);
    private static final RenderType DECAL = RenderType.entityDecal(DRAGON_LOCATION);
    private static final RenderType EYES = RenderType.eyes(DRAGON_EYES_LOCATION);
    private static final RenderType BEAM = RenderType.entitySmoothCutout(CRYSTAL_BEAM_LOCATION);
    private static final float HALF_SQRT_3 = (float)(Math.sqrt((double)3.0) / 2.0);
    private final DragonModel model;

    public EnderDragonRenderer(EntityRendererProvider.Context $$0) {
        super($$0);
        this.shadowRadius = 0.5f;
        this.model = new DragonModel($$0.bakeLayer(ModelLayers.ENDER_DRAGON));
    }

    @Override
    public void render(EnderDragon $$0, float $$1, float $$2, PoseStack $$3, MultiBufferSource $$4, int $$5) {
        $$3.pushPose();
        float $$6 = (float)$$0.getLatencyPos(7, $$2)[0];
        float $$7 = (float)($$0.getLatencyPos(5, $$2)[1] - $$0.getLatencyPos(10, $$2)[1]);
        $$3.mulPose(Axis.YP.rotationDegrees(-$$6));
        $$3.mulPose(Axis.XP.rotationDegrees($$7 * 10.0f));
        $$3.translate(0.0f, 0.0f, 1.0f);
        $$3.scale(-1.0f, -1.0f, 1.0f);
        $$3.translate(0.0f, -1.501f, 0.0f);
        boolean $$8 = $$0.hurtTime > 0;
        this.model.prepareMobModel($$0, 0.0f, 0.0f, $$2);
        if ($$0.dragonDeathTime > 0) {
            float $$9 = (float)$$0.dragonDeathTime / 200.0f;
            VertexConsumer $$10 = $$4.getBuffer(RenderType.dragonExplosionAlpha(DRAGON_EXPLODING_LOCATION));
            this.model.renderToBuffer($$3, $$10, $$5, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, $$9);
            VertexConsumer $$11 = $$4.getBuffer(DECAL);
            this.model.renderToBuffer($$3, $$11, $$5, OverlayTexture.pack(0.0f, $$8), 1.0f, 1.0f, 1.0f, 1.0f);
        } else {
            VertexConsumer $$12 = $$4.getBuffer(RENDER_TYPE);
            this.model.renderToBuffer($$3, $$12, $$5, OverlayTexture.pack(0.0f, $$8), 1.0f, 1.0f, 1.0f, 1.0f);
        }
        VertexConsumer $$13 = $$4.getBuffer(EYES);
        this.model.renderToBuffer($$3, $$13, $$5, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
        if ($$0.dragonDeathTime > 0) {
            float $$14 = ((float)$$0.dragonDeathTime + $$2) / 200.0f;
            float $$15 = Math.min((float)($$14 > 0.8f ? ($$14 - 0.8f) / 0.2f : 0.0f), (float)1.0f);
            RandomSource $$16 = RandomSource.create(432L);
            VertexConsumer $$17 = $$4.getBuffer(RenderType.lightning());
            $$3.pushPose();
            $$3.translate(0.0f, -1.0f, -2.0f);
            int $$18 = 0;
            while ((float)$$18 < ($$14 + $$14 * $$14) / 2.0f * 60.0f) {
                $$3.mulPose(Axis.XP.rotationDegrees($$16.nextFloat() * 360.0f));
                $$3.mulPose(Axis.YP.rotationDegrees($$16.nextFloat() * 360.0f));
                $$3.mulPose(Axis.ZP.rotationDegrees($$16.nextFloat() * 360.0f));
                $$3.mulPose(Axis.XP.rotationDegrees($$16.nextFloat() * 360.0f));
                $$3.mulPose(Axis.YP.rotationDegrees($$16.nextFloat() * 360.0f));
                $$3.mulPose(Axis.ZP.rotationDegrees($$16.nextFloat() * 360.0f + $$14 * 90.0f));
                float $$19 = $$16.nextFloat() * 20.0f + 5.0f + $$15 * 10.0f;
                float $$20 = $$16.nextFloat() * 2.0f + 1.0f + $$15 * 2.0f;
                Matrix4f $$21 = $$3.last().pose();
                int $$22 = (int)(255.0f * (1.0f - $$15));
                EnderDragonRenderer.vertex01($$17, $$21, $$22);
                EnderDragonRenderer.vertex2($$17, $$21, $$19, $$20);
                EnderDragonRenderer.vertex3($$17, $$21, $$19, $$20);
                EnderDragonRenderer.vertex01($$17, $$21, $$22);
                EnderDragonRenderer.vertex3($$17, $$21, $$19, $$20);
                EnderDragonRenderer.vertex4($$17, $$21, $$19, $$20);
                EnderDragonRenderer.vertex01($$17, $$21, $$22);
                EnderDragonRenderer.vertex4($$17, $$21, $$19, $$20);
                EnderDragonRenderer.vertex2($$17, $$21, $$19, $$20);
                ++$$18;
            }
            $$3.popPose();
        }
        $$3.popPose();
        if ($$0.nearestCrystal != null) {
            $$3.pushPose();
            float $$23 = (float)($$0.nearestCrystal.getX() - Mth.lerp((double)$$2, $$0.xo, $$0.getX()));
            float $$24 = (float)($$0.nearestCrystal.getY() - Mth.lerp((double)$$2, $$0.yo, $$0.getY()));
            float $$25 = (float)($$0.nearestCrystal.getZ() - Mth.lerp((double)$$2, $$0.zo, $$0.getZ()));
            EnderDragonRenderer.renderCrystalBeams($$23, $$24 + EndCrystalRenderer.getY($$0.nearestCrystal, $$2), $$25, $$2, $$0.tickCount, $$3, $$4, $$5);
            $$3.popPose();
        }
        super.render($$0, $$1, $$2, $$3, $$4, $$5);
    }

    private static void vertex01(VertexConsumer $$0, Matrix4f $$1, int $$2) {
        $$0.vertex($$1, 0.0f, 0.0f, 0.0f).color(255, 255, 255, $$2).endVertex();
    }

    private static void vertex2(VertexConsumer $$0, Matrix4f $$1, float $$2, float $$3) {
        $$0.vertex($$1, -HALF_SQRT_3 * $$3, $$2, -0.5f * $$3).color(255, 0, 255, 0).endVertex();
    }

    private static void vertex3(VertexConsumer $$0, Matrix4f $$1, float $$2, float $$3) {
        $$0.vertex($$1, HALF_SQRT_3 * $$3, $$2, -0.5f * $$3).color(255, 0, 255, 0).endVertex();
    }

    private static void vertex4(VertexConsumer $$0, Matrix4f $$1, float $$2, float $$3) {
        $$0.vertex($$1, 0.0f, $$2, 1.0f * $$3).color(255, 0, 255, 0).endVertex();
    }

    public static void renderCrystalBeams(float $$0, float $$1, float $$2, float $$3, int $$4, PoseStack $$5, MultiBufferSource $$6, int $$7) {
        float $$8 = Mth.sqrt($$0 * $$0 + $$2 * $$2);
        float $$9 = Mth.sqrt($$0 * $$0 + $$1 * $$1 + $$2 * $$2);
        $$5.pushPose();
        $$5.translate(0.0f, 2.0f, 0.0f);
        $$5.mulPose(Axis.YP.rotation((float)(-Math.atan2((double)$$2, (double)$$0)) - 1.5707964f));
        $$5.mulPose(Axis.XP.rotation((float)(-Math.atan2((double)$$8, (double)$$1)) - 1.5707964f));
        VertexConsumer $$10 = $$6.getBuffer(BEAM);
        float $$11 = 0.0f - ((float)$$4 + $$3) * 0.01f;
        float $$12 = Mth.sqrt($$0 * $$0 + $$1 * $$1 + $$2 * $$2) / 32.0f - ((float)$$4 + $$3) * 0.01f;
        int $$13 = 8;
        float $$14 = 0.0f;
        float $$15 = 0.75f;
        float $$16 = 0.0f;
        PoseStack.Pose $$17 = $$5.last();
        Matrix4f $$18 = $$17.pose();
        Matrix3f $$19 = $$17.normal();
        for (int $$20 = 1; $$20 <= 8; ++$$20) {
            float $$21 = Mth.sin((float)$$20 * ((float)Math.PI * 2) / 8.0f) * 0.75f;
            float $$22 = Mth.cos((float)$$20 * ((float)Math.PI * 2) / 8.0f) * 0.75f;
            float $$23 = (float)$$20 / 8.0f;
            $$10.vertex($$18, $$14 * 0.2f, $$15 * 0.2f, 0.0f).color(0, 0, 0, 255).uv($$16, $$11).overlayCoords(OverlayTexture.NO_OVERLAY).uv2($$7).normal($$19, 0.0f, -1.0f, 0.0f).endVertex();
            $$10.vertex($$18, $$14, $$15, $$9).color(255, 255, 255, 255).uv($$16, $$12).overlayCoords(OverlayTexture.NO_OVERLAY).uv2($$7).normal($$19, 0.0f, -1.0f, 0.0f).endVertex();
            $$10.vertex($$18, $$21, $$22, $$9).color(255, 255, 255, 255).uv($$23, $$12).overlayCoords(OverlayTexture.NO_OVERLAY).uv2($$7).normal($$19, 0.0f, -1.0f, 0.0f).endVertex();
            $$10.vertex($$18, $$21 * 0.2f, $$22 * 0.2f, 0.0f).color(0, 0, 0, 255).uv($$23, $$11).overlayCoords(OverlayTexture.NO_OVERLAY).uv2($$7).normal($$19, 0.0f, -1.0f, 0.0f).endVertex();
            $$14 = $$21;
            $$15 = $$22;
            $$16 = $$23;
        }
        $$5.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(EnderDragon $$0) {
        return DRAGON_LOCATION;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        float $$2 = -16.0f;
        PartDefinition $$3 = $$1.addOrReplaceChild("head", CubeListBuilder.create().addBox("upperlip", -6.0f, -1.0f, -24.0f, 12, 5, 16, 176, 44).addBox("upperhead", -8.0f, -8.0f, -10.0f, 16, 16, 16, 112, 30).mirror().addBox("scale", -5.0f, -12.0f, -4.0f, 2, 4, 6, 0, 0).addBox("nostril", -5.0f, -3.0f, -22.0f, 2, 2, 4, 112, 0).mirror().addBox("scale", 3.0f, -12.0f, -4.0f, 2, 4, 6, 0, 0).addBox("nostril", 3.0f, -3.0f, -22.0f, 2, 2, 4, 112, 0), PartPose.ZERO);
        $$3.addOrReplaceChild("jaw", CubeListBuilder.create().addBox("jaw", -6.0f, 0.0f, -16.0f, 12, 4, 16, 176, 65), PartPose.offset(0.0f, 4.0f, -8.0f));
        $$1.addOrReplaceChild("neck", CubeListBuilder.create().addBox("box", -5.0f, -5.0f, -5.0f, 10, 10, 10, 192, 104).addBox("scale", -1.0f, -9.0f, -3.0f, 2, 4, 6, 48, 0), PartPose.ZERO);
        $$1.addOrReplaceChild("body", CubeListBuilder.create().addBox("body", -12.0f, 0.0f, -16.0f, 24, 24, 64, 0, 0).addBox("scale", -1.0f, -6.0f, -10.0f, 2, 6, 12, 220, 53).addBox("scale", -1.0f, -6.0f, 10.0f, 2, 6, 12, 220, 53).addBox("scale", -1.0f, -6.0f, 30.0f, 2, 6, 12, 220, 53), PartPose.offset(0.0f, 4.0f, 8.0f));
        PartDefinition $$4 = $$1.addOrReplaceChild("left_wing", CubeListBuilder.create().mirror().addBox("bone", 0.0f, -4.0f, -4.0f, 56, 8, 8, 112, 88).addBox("skin", 0.0f, 0.0f, 2.0f, 56, 0, 56, -56, 88), PartPose.offset(12.0f, 5.0f, 2.0f));
        $$4.addOrReplaceChild("left_wing_tip", CubeListBuilder.create().mirror().addBox("bone", 0.0f, -2.0f, -2.0f, 56, 4, 4, 112, 136).addBox("skin", 0.0f, 0.0f, 2.0f, 56, 0, 56, -56, 144), PartPose.offset(56.0f, 0.0f, 0.0f));
        PartDefinition $$5 = $$1.addOrReplaceChild("left_front_leg", CubeListBuilder.create().addBox("main", -4.0f, -4.0f, -4.0f, 8, 24, 8, 112, 104), PartPose.offset(12.0f, 20.0f, 2.0f));
        PartDefinition $$6 = $$5.addOrReplaceChild("left_front_leg_tip", CubeListBuilder.create().addBox("main", -3.0f, -1.0f, -3.0f, 6, 24, 6, 226, 138), PartPose.offset(0.0f, 20.0f, -1.0f));
        $$6.addOrReplaceChild("left_front_foot", CubeListBuilder.create().addBox("main", -4.0f, 0.0f, -12.0f, 8, 4, 16, 144, 104), PartPose.offset(0.0f, 23.0f, 0.0f));
        PartDefinition $$7 = $$1.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().addBox("main", -8.0f, -4.0f, -8.0f, 16, 32, 16, 0, 0), PartPose.offset(16.0f, 16.0f, 42.0f));
        PartDefinition $$8 = $$7.addOrReplaceChild("left_hind_leg_tip", CubeListBuilder.create().addBox("main", -6.0f, -2.0f, 0.0f, 12, 32, 12, 196, 0), PartPose.offset(0.0f, 32.0f, -4.0f));
        $$8.addOrReplaceChild("left_hind_foot", CubeListBuilder.create().addBox("main", -9.0f, 0.0f, -20.0f, 18, 6, 24, 112, 0), PartPose.offset(0.0f, 31.0f, 4.0f));
        PartDefinition $$9 = $$1.addOrReplaceChild("right_wing", CubeListBuilder.create().addBox("bone", -56.0f, -4.0f, -4.0f, 56, 8, 8, 112, 88).addBox("skin", -56.0f, 0.0f, 2.0f, 56, 0, 56, -56, 88), PartPose.offset(-12.0f, 5.0f, 2.0f));
        $$9.addOrReplaceChild("right_wing_tip", CubeListBuilder.create().addBox("bone", -56.0f, -2.0f, -2.0f, 56, 4, 4, 112, 136).addBox("skin", -56.0f, 0.0f, 2.0f, 56, 0, 56, -56, 144), PartPose.offset(-56.0f, 0.0f, 0.0f));
        PartDefinition $$10 = $$1.addOrReplaceChild("right_front_leg", CubeListBuilder.create().addBox("main", -4.0f, -4.0f, -4.0f, 8, 24, 8, 112, 104), PartPose.offset(-12.0f, 20.0f, 2.0f));
        PartDefinition $$11 = $$10.addOrReplaceChild("right_front_leg_tip", CubeListBuilder.create().addBox("main", -3.0f, -1.0f, -3.0f, 6, 24, 6, 226, 138), PartPose.offset(0.0f, 20.0f, -1.0f));
        $$11.addOrReplaceChild("right_front_foot", CubeListBuilder.create().addBox("main", -4.0f, 0.0f, -12.0f, 8, 4, 16, 144, 104), PartPose.offset(0.0f, 23.0f, 0.0f));
        PartDefinition $$12 = $$1.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().addBox("main", -8.0f, -4.0f, -8.0f, 16, 32, 16, 0, 0), PartPose.offset(-16.0f, 16.0f, 42.0f));
        PartDefinition $$13 = $$12.addOrReplaceChild("right_hind_leg_tip", CubeListBuilder.create().addBox("main", -6.0f, -2.0f, 0.0f, 12, 32, 12, 196, 0), PartPose.offset(0.0f, 32.0f, -4.0f));
        $$13.addOrReplaceChild("right_hind_foot", CubeListBuilder.create().addBox("main", -9.0f, 0.0f, -20.0f, 18, 6, 24, 112, 0), PartPose.offset(0.0f, 31.0f, 4.0f));
        return LayerDefinition.create($$0, 256, 256);
    }

    public static class DragonModel
    extends EntityModel<EnderDragon> {
        private final ModelPart head;
        private final ModelPart neck;
        private final ModelPart jaw;
        private final ModelPart body;
        private final ModelPart leftWing;
        private final ModelPart leftWingTip;
        private final ModelPart leftFrontLeg;
        private final ModelPart leftFrontLegTip;
        private final ModelPart leftFrontFoot;
        private final ModelPart leftRearLeg;
        private final ModelPart leftRearLegTip;
        private final ModelPart leftRearFoot;
        private final ModelPart rightWing;
        private final ModelPart rightWingTip;
        private final ModelPart rightFrontLeg;
        private final ModelPart rightFrontLegTip;
        private final ModelPart rightFrontFoot;
        private final ModelPart rightRearLeg;
        private final ModelPart rightRearLegTip;
        private final ModelPart rightRearFoot;
        @Nullable
        private EnderDragon entity;
        private float a;

        public DragonModel(ModelPart $$0) {
            this.head = $$0.getChild("head");
            this.jaw = this.head.getChild("jaw");
            this.neck = $$0.getChild("neck");
            this.body = $$0.getChild("body");
            this.leftWing = $$0.getChild("left_wing");
            this.leftWingTip = this.leftWing.getChild("left_wing_tip");
            this.leftFrontLeg = $$0.getChild("left_front_leg");
            this.leftFrontLegTip = this.leftFrontLeg.getChild("left_front_leg_tip");
            this.leftFrontFoot = this.leftFrontLegTip.getChild("left_front_foot");
            this.leftRearLeg = $$0.getChild("left_hind_leg");
            this.leftRearLegTip = this.leftRearLeg.getChild("left_hind_leg_tip");
            this.leftRearFoot = this.leftRearLegTip.getChild("left_hind_foot");
            this.rightWing = $$0.getChild("right_wing");
            this.rightWingTip = this.rightWing.getChild("right_wing_tip");
            this.rightFrontLeg = $$0.getChild("right_front_leg");
            this.rightFrontLegTip = this.rightFrontLeg.getChild("right_front_leg_tip");
            this.rightFrontFoot = this.rightFrontLegTip.getChild("right_front_foot");
            this.rightRearLeg = $$0.getChild("right_hind_leg");
            this.rightRearLegTip = this.rightRearLeg.getChild("right_hind_leg_tip");
            this.rightRearFoot = this.rightRearLegTip.getChild("right_hind_foot");
        }

        @Override
        public void prepareMobModel(EnderDragon $$0, float $$1, float $$2, float $$3) {
            this.entity = $$0;
            this.a = $$3;
        }

        @Override
        public void setupAnim(EnderDragon $$0, float $$1, float $$2, float $$3, float $$4, float $$5) {
        }

        @Override
        public void renderToBuffer(PoseStack $$0, VertexConsumer $$1, int $$2, int $$3, float $$4, float $$5, float $$6, float $$7) {
            $$0.pushPose();
            float $$8 = Mth.lerp(this.a, this.entity.oFlapTime, this.entity.flapTime);
            this.jaw.xRot = (float)(Math.sin((double)($$8 * ((float)Math.PI * 2))) + 1.0) * 0.2f;
            float $$9 = (float)(Math.sin((double)($$8 * ((float)Math.PI * 2) - 1.0f)) + 1.0);
            $$9 = ($$9 * $$9 + $$9 * 2.0f) * 0.05f;
            $$0.translate(0.0f, $$9 - 2.0f, -3.0f);
            $$0.mulPose(Axis.XP.rotationDegrees($$9 * 2.0f));
            float $$10 = 0.0f;
            float $$11 = 20.0f;
            float $$12 = -12.0f;
            float $$13 = 1.5f;
            double[] $$14 = this.entity.getLatencyPos(6, this.a);
            float $$15 = Mth.wrapDegrees((float)(this.entity.getLatencyPos(5, this.a)[0] - this.entity.getLatencyPos(10, this.a)[0]));
            float $$16 = Mth.wrapDegrees((float)(this.entity.getLatencyPos(5, this.a)[0] + (double)($$15 / 2.0f)));
            float $$17 = $$8 * ((float)Math.PI * 2);
            for (int $$18 = 0; $$18 < 5; ++$$18) {
                double[] $$19 = this.entity.getLatencyPos(5 - $$18, this.a);
                float $$20 = (float)Math.cos((double)((float)$$18 * 0.45f + $$17)) * 0.15f;
                this.neck.yRot = Mth.wrapDegrees((float)($$19[0] - $$14[0])) * ((float)Math.PI / 180) * 1.5f;
                this.neck.xRot = $$20 + this.entity.getHeadPartYOffset($$18, $$14, $$19) * ((float)Math.PI / 180) * 1.5f * 5.0f;
                this.neck.zRot = -Mth.wrapDegrees((float)($$19[0] - (double)$$16)) * ((float)Math.PI / 180) * 1.5f;
                this.neck.y = $$11;
                this.neck.z = $$12;
                this.neck.x = $$10;
                $$11 += Mth.sin(this.neck.xRot) * 10.0f;
                $$12 -= Mth.cos(this.neck.yRot) * Mth.cos(this.neck.xRot) * 10.0f;
                $$10 -= Mth.sin(this.neck.yRot) * Mth.cos(this.neck.xRot) * 10.0f;
                this.neck.render($$0, $$1, $$2, $$3, 1.0f, 1.0f, 1.0f, $$7);
            }
            this.head.y = $$11;
            this.head.z = $$12;
            this.head.x = $$10;
            double[] $$21 = this.entity.getLatencyPos(0, this.a);
            this.head.yRot = Mth.wrapDegrees((float)($$21[0] - $$14[0])) * ((float)Math.PI / 180);
            this.head.xRot = Mth.wrapDegrees(this.entity.getHeadPartYOffset(6, $$14, $$21)) * ((float)Math.PI / 180) * 1.5f * 5.0f;
            this.head.zRot = -Mth.wrapDegrees((float)($$21[0] - (double)$$16)) * ((float)Math.PI / 180);
            this.head.render($$0, $$1, $$2, $$3, 1.0f, 1.0f, 1.0f, $$7);
            $$0.pushPose();
            $$0.translate(0.0f, 1.0f, 0.0f);
            $$0.mulPose(Axis.ZP.rotationDegrees(-$$15 * 1.5f));
            $$0.translate(0.0f, -1.0f, 0.0f);
            this.body.zRot = 0.0f;
            this.body.render($$0, $$1, $$2, $$3, 1.0f, 1.0f, 1.0f, $$7);
            float $$22 = $$8 * ((float)Math.PI * 2);
            this.leftWing.xRot = 0.125f - (float)Math.cos((double)$$22) * 0.2f;
            this.leftWing.yRot = -0.25f;
            this.leftWing.zRot = -((float)(Math.sin((double)$$22) + 0.125)) * 0.8f;
            this.leftWingTip.zRot = (float)(Math.sin((double)($$22 + 2.0f)) + 0.5) * 0.75f;
            this.rightWing.xRot = this.leftWing.xRot;
            this.rightWing.yRot = -this.leftWing.yRot;
            this.rightWing.zRot = -this.leftWing.zRot;
            this.rightWingTip.zRot = -this.leftWingTip.zRot;
            this.renderSide($$0, $$1, $$2, $$3, $$9, this.leftWing, this.leftFrontLeg, this.leftFrontLegTip, this.leftFrontFoot, this.leftRearLeg, this.leftRearLegTip, this.leftRearFoot, $$7);
            this.renderSide($$0, $$1, $$2, $$3, $$9, this.rightWing, this.rightFrontLeg, this.rightFrontLegTip, this.rightFrontFoot, this.rightRearLeg, this.rightRearLegTip, this.rightRearFoot, $$7);
            $$0.popPose();
            float $$23 = -Mth.sin($$8 * ((float)Math.PI * 2)) * 0.0f;
            $$17 = $$8 * ((float)Math.PI * 2);
            $$11 = 10.0f;
            $$12 = 60.0f;
            $$10 = 0.0f;
            $$14 = this.entity.getLatencyPos(11, this.a);
            for (int $$24 = 0; $$24 < 12; ++$$24) {
                $$21 = this.entity.getLatencyPos(12 + $$24, this.a);
                this.neck.yRot = (Mth.wrapDegrees((float)($$21[0] - $$14[0])) * 1.5f + 180.0f) * ((float)Math.PI / 180);
                this.neck.xRot = ($$23 += Mth.sin((float)$$24 * 0.45f + $$17) * 0.05f) + (float)($$21[1] - $$14[1]) * ((float)Math.PI / 180) * 1.5f * 5.0f;
                this.neck.zRot = Mth.wrapDegrees((float)($$21[0] - (double)$$16)) * ((float)Math.PI / 180) * 1.5f;
                this.neck.y = $$11;
                this.neck.z = $$12;
                this.neck.x = $$10;
                $$11 += Mth.sin(this.neck.xRot) * 10.0f;
                $$12 -= Mth.cos(this.neck.yRot) * Mth.cos(this.neck.xRot) * 10.0f;
                $$10 -= Mth.sin(this.neck.yRot) * Mth.cos(this.neck.xRot) * 10.0f;
                this.neck.render($$0, $$1, $$2, $$3, 1.0f, 1.0f, 1.0f, $$7);
            }
            $$0.popPose();
        }

        private void renderSide(PoseStack $$0, VertexConsumer $$1, int $$2, int $$3, float $$4, ModelPart $$5, ModelPart $$6, ModelPart $$7, ModelPart $$8, ModelPart $$9, ModelPart $$10, ModelPart $$11, float $$12) {
            $$9.xRot = 1.0f + $$4 * 0.1f;
            $$10.xRot = 0.5f + $$4 * 0.1f;
            $$11.xRot = 0.75f + $$4 * 0.1f;
            $$6.xRot = 1.3f + $$4 * 0.1f;
            $$7.xRot = -0.5f - $$4 * 0.1f;
            $$8.xRot = 0.75f + $$4 * 0.1f;
            $$5.render($$0, $$1, $$2, $$3, 1.0f, 1.0f, 1.0f, $$12);
            $$6.render($$0, $$1, $$2, $$3, 1.0f, 1.0f, 1.0f, $$12);
            $$9.render($$0, $$1, $$2, $$3, 1.0f, 1.0f, 1.0f, $$12);
        }
    }
}