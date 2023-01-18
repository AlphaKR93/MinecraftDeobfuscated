/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  org.joml.Matrix3f
 *  org.joml.Matrix4f
 */
package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.List;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class BeaconRenderer
implements BlockEntityRenderer<BeaconBlockEntity> {
    public static final ResourceLocation BEAM_LOCATION = new ResourceLocation("textures/entity/beacon_beam.png");
    public static final int MAX_RENDER_Y = 1024;

    public BeaconRenderer(BlockEntityRendererProvider.Context $$0) {
    }

    @Override
    public void render(BeaconBlockEntity $$0, float $$1, PoseStack $$2, MultiBufferSource $$3, int $$4, int $$5) {
        long $$6 = $$0.getLevel().getGameTime();
        List<BeaconBlockEntity.BeaconBeamSection> $$7 = $$0.getBeamSections();
        int $$8 = 0;
        for (int $$9 = 0; $$9 < $$7.size(); ++$$9) {
            BeaconBlockEntity.BeaconBeamSection $$10 = (BeaconBlockEntity.BeaconBeamSection)$$7.get($$9);
            BeaconRenderer.renderBeaconBeam($$2, $$3, $$1, $$6, $$8, $$9 == $$7.size() - 1 ? 1024 : $$10.getHeight(), $$10.getColor());
            $$8 += $$10.getHeight();
        }
    }

    private static void renderBeaconBeam(PoseStack $$0, MultiBufferSource $$1, float $$2, long $$3, int $$4, int $$5, float[] $$6) {
        BeaconRenderer.renderBeaconBeam($$0, $$1, BEAM_LOCATION, $$2, 1.0f, $$3, $$4, $$5, $$6, 0.2f, 0.25f);
    }

    public static void renderBeaconBeam(PoseStack $$0, MultiBufferSource $$1, ResourceLocation $$2, float $$3, float $$4, long $$5, int $$6, int $$7, float[] $$8, float $$9, float $$10) {
        int $$11 = $$6 + $$7;
        $$0.pushPose();
        $$0.translate(0.5, 0.0, 0.5);
        float $$12 = (float)Math.floorMod((long)$$5, (int)40) + $$3;
        float $$13 = $$7 < 0 ? $$12 : -$$12;
        float $$14 = Mth.frac($$13 * 0.2f - (float)Mth.floor($$13 * 0.1f));
        float $$15 = $$8[0];
        float $$16 = $$8[1];
        float $$17 = $$8[2];
        $$0.pushPose();
        $$0.mulPose(Axis.YP.rotationDegrees($$12 * 2.25f - 45.0f));
        float $$18 = 0.0f;
        float $$19 = $$9;
        float $$20 = $$9;
        float $$21 = 0.0f;
        float $$22 = -$$9;
        float $$23 = 0.0f;
        float $$24 = 0.0f;
        float $$25 = -$$9;
        float $$26 = 0.0f;
        float $$27 = 1.0f;
        float $$28 = -1.0f + $$14;
        float $$29 = (float)$$7 * $$4 * (0.5f / $$9) + $$28;
        BeaconRenderer.renderPart($$0, $$1.getBuffer(RenderType.beaconBeam($$2, false)), $$15, $$16, $$17, 1.0f, $$6, $$11, 0.0f, $$19, $$20, 0.0f, $$22, 0.0f, 0.0f, $$25, 0.0f, 1.0f, $$29, $$28);
        $$0.popPose();
        float $$30 = -$$10;
        float $$31 = -$$10;
        float $$32 = $$10;
        float $$33 = -$$10;
        float $$34 = -$$10;
        float $$35 = $$10;
        float $$36 = $$10;
        float $$37 = $$10;
        float $$38 = 0.0f;
        float $$39 = 1.0f;
        float $$40 = -1.0f + $$14;
        float $$41 = (float)$$7 * $$4 + $$40;
        BeaconRenderer.renderPart($$0, $$1.getBuffer(RenderType.beaconBeam($$2, true)), $$15, $$16, $$17, 0.125f, $$6, $$11, $$30, $$31, $$32, $$33, $$34, $$35, $$36, $$37, 0.0f, 1.0f, $$41, $$40);
        $$0.popPose();
    }

    private static void renderPart(PoseStack $$0, VertexConsumer $$1, float $$2, float $$3, float $$4, float $$5, int $$6, int $$7, float $$8, float $$9, float $$10, float $$11, float $$12, float $$13, float $$14, float $$15, float $$16, float $$17, float $$18, float $$19) {
        PoseStack.Pose $$20 = $$0.last();
        Matrix4f $$21 = $$20.pose();
        Matrix3f $$22 = $$20.normal();
        BeaconRenderer.renderQuad($$21, $$22, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$8, $$9, $$10, $$11, $$16, $$17, $$18, $$19);
        BeaconRenderer.renderQuad($$21, $$22, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$14, $$15, $$12, $$13, $$16, $$17, $$18, $$19);
        BeaconRenderer.renderQuad($$21, $$22, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$10, $$11, $$14, $$15, $$16, $$17, $$18, $$19);
        BeaconRenderer.renderQuad($$21, $$22, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$12, $$13, $$8, $$9, $$16, $$17, $$18, $$19);
    }

    private static void renderQuad(Matrix4f $$0, Matrix3f $$1, VertexConsumer $$2, float $$3, float $$4, float $$5, float $$6, int $$7, int $$8, float $$9, float $$10, float $$11, float $$12, float $$13, float $$14, float $$15, float $$16) {
        BeaconRenderer.addVertex($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$8, $$9, $$10, $$14, $$15);
        BeaconRenderer.addVertex($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$9, $$10, $$14, $$16);
        BeaconRenderer.addVertex($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$11, $$12, $$13, $$16);
        BeaconRenderer.addVertex($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$8, $$11, $$12, $$13, $$15);
    }

    private static void addVertex(Matrix4f $$0, Matrix3f $$1, VertexConsumer $$2, float $$3, float $$4, float $$5, float $$6, int $$7, float $$8, float $$9, float $$10, float $$11) {
        $$2.vertex($$0, $$8, $$7, $$9).color($$3, $$4, $$5, $$6).uv($$10, $$11).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal($$1, 0.0f, 1.0f, 0.0f).endVertex();
    }

    @Override
    public boolean shouldRenderOffScreen(BeaconBlockEntity $$0) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

    @Override
    public boolean shouldRender(BeaconBlockEntity $$0, Vec3 $$1) {
        return Vec3.atCenterOf($$0.getBlockPos()).multiply(1.0, 0.0, 1.0).closerThan($$1.multiply(1.0, 0.0, 1.0), this.getViewDistance());
    }
}