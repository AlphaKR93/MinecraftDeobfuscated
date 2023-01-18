/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  org.joml.Quaternionf
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import org.joml.Quaternionf;

public class EndCrystalRenderer
extends EntityRenderer<EndCrystal> {
    private static final ResourceLocation END_CRYSTAL_LOCATION = new ResourceLocation("textures/entity/end_crystal/end_crystal.png");
    private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(END_CRYSTAL_LOCATION);
    private static final float SIN_45 = (float)Math.sin((double)0.7853981633974483);
    private static final String GLASS = "glass";
    private static final String BASE = "base";
    private final ModelPart cube;
    private final ModelPart glass;
    private final ModelPart base;

    public EndCrystalRenderer(EntityRendererProvider.Context $$0) {
        super($$0);
        this.shadowRadius = 0.5f;
        ModelPart $$1 = $$0.bakeLayer(ModelLayers.END_CRYSTAL);
        this.glass = $$1.getChild(GLASS);
        this.cube = $$1.getChild("cube");
        this.base = $$1.getChild(BASE);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        $$1.addOrReplaceChild(GLASS, CubeListBuilder.create().texOffs(0, 0).addBox(-4.0f, -4.0f, -4.0f, 8.0f, 8.0f, 8.0f), PartPose.ZERO);
        $$1.addOrReplaceChild("cube", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0f, -4.0f, -4.0f, 8.0f, 8.0f, 8.0f), PartPose.ZERO);
        $$1.addOrReplaceChild(BASE, CubeListBuilder.create().texOffs(0, 16).addBox(-6.0f, 0.0f, -6.0f, 12.0f, 4.0f, 12.0f), PartPose.ZERO);
        return LayerDefinition.create($$0, 64, 32);
    }

    @Override
    public void render(EndCrystal $$0, float $$1, float $$2, PoseStack $$3, MultiBufferSource $$4, int $$5) {
        $$3.pushPose();
        float $$6 = EndCrystalRenderer.getY($$0, $$2);
        float $$7 = ((float)$$0.time + $$2) * 3.0f;
        VertexConsumer $$8 = $$4.getBuffer(RENDER_TYPE);
        $$3.pushPose();
        $$3.scale(2.0f, 2.0f, 2.0f);
        $$3.translate(0.0f, -0.5f, 0.0f);
        int $$9 = OverlayTexture.NO_OVERLAY;
        if ($$0.showsBottom()) {
            this.base.render($$3, $$8, $$5, $$9);
        }
        $$3.mulPose(Axis.YP.rotationDegrees($$7));
        $$3.translate(0.0f, 1.5f + $$6 / 2.0f, 0.0f);
        $$3.mulPose(new Quaternionf().setAngleAxis(1.0471976f, SIN_45, 0.0f, SIN_45));
        this.glass.render($$3, $$8, $$5, $$9);
        float $$10 = 0.875f;
        $$3.scale(0.875f, 0.875f, 0.875f);
        $$3.mulPose(new Quaternionf().setAngleAxis(1.0471976f, SIN_45, 0.0f, SIN_45));
        $$3.mulPose(Axis.YP.rotationDegrees($$7));
        this.glass.render($$3, $$8, $$5, $$9);
        $$3.scale(0.875f, 0.875f, 0.875f);
        $$3.mulPose(new Quaternionf().setAngleAxis(1.0471976f, SIN_45, 0.0f, SIN_45));
        $$3.mulPose(Axis.YP.rotationDegrees($$7));
        this.cube.render($$3, $$8, $$5, $$9);
        $$3.popPose();
        $$3.popPose();
        BlockPos $$11 = $$0.getBeamTarget();
        if ($$11 != null) {
            float $$12 = (float)$$11.getX() + 0.5f;
            float $$13 = (float)$$11.getY() + 0.5f;
            float $$14 = (float)$$11.getZ() + 0.5f;
            float $$15 = (float)((double)$$12 - $$0.getX());
            float $$16 = (float)((double)$$13 - $$0.getY());
            float $$17 = (float)((double)$$14 - $$0.getZ());
            $$3.translate($$15, $$16, $$17);
            EnderDragonRenderer.renderCrystalBeams(-$$15, -$$16 + $$6, -$$17, $$2, $$0.time, $$3, $$4, $$5);
        }
        super.render($$0, $$1, $$2, $$3, $$4, $$5);
    }

    public static float getY(EndCrystal $$0, float $$1) {
        float $$2 = (float)$$0.time + $$1;
        float $$3 = Mth.sin($$2 * 0.2f) / 2.0f + 0.5f;
        $$3 = ($$3 * $$3 + $$3) * 0.4f;
        return $$3 - 1.4f;
    }

    @Override
    public ResourceLocation getTextureLocation(EndCrystal $$0) {
        return END_CRYSTAL_LOCATION;
    }

    @Override
    public boolean shouldRender(EndCrystal $$0, Frustum $$1, double $$2, double $$3, double $$4) {
        return super.shouldRender($$0, $$1, $$2, $$3, $$4) || $$0.getBeamTarget() != null;
    }
}