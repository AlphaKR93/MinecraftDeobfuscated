/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.util.Pair
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Map
 *  java.util.stream.Stream
 *  org.joml.Quaternionf
 */
package net.minecraft.client.renderer.entity;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.ChestRaftModel;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.RaftModel;
import net.minecraft.client.model.WaterPatchModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.Boat;
import org.joml.Quaternionf;

public class BoatRenderer
extends EntityRenderer<Boat> {
    private final Map<Boat.Type, Pair<ResourceLocation, ListModel<Boat>>> boatResources;

    public BoatRenderer(EntityRendererProvider.Context $$02, boolean $$1) {
        super($$02);
        this.shadowRadius = 0.8f;
        this.boatResources = (Map)Stream.of((Object[])Boat.Type.values()).collect(ImmutableMap.toImmutableMap($$0 -> $$0, $$2 -> Pair.of((Object)new ResourceLocation(BoatRenderer.getTextureLocation($$2, $$1)), this.createBoatModel($$02, (Boat.Type)$$2, $$1))));
    }

    private ListModel<Boat> createBoatModel(EntityRendererProvider.Context $$0, Boat.Type $$1, boolean $$2) {
        ModelLayerLocation $$3 = $$2 ? ModelLayers.createChestBoatModelName($$1) : ModelLayers.createBoatModelName($$1);
        ModelPart $$4 = $$0.bakeLayer($$3);
        if ($$1 == Boat.Type.BAMBOO) {
            return $$2 ? new ChestRaftModel($$4) : new RaftModel($$4);
        }
        return $$2 ? new ChestBoatModel($$4) : new BoatModel($$4);
    }

    private static String getTextureLocation(Boat.Type $$0, boolean $$1) {
        if ($$1) {
            return "textures/entity/chest_boat/" + $$0.getName() + ".png";
        }
        return "textures/entity/boat/" + $$0.getName() + ".png";
    }

    @Override
    public void render(Boat $$0, float $$1, float $$2, PoseStack $$3, MultiBufferSource $$4, int $$5) {
        float $$8;
        $$3.pushPose();
        $$3.translate(0.0f, 0.375f, 0.0f);
        $$3.mulPose(Axis.YP.rotationDegrees(180.0f - $$1));
        float $$6 = (float)$$0.getHurtTime() - $$2;
        float $$7 = $$0.getDamage() - $$2;
        if ($$7 < 0.0f) {
            $$7 = 0.0f;
        }
        if ($$6 > 0.0f) {
            $$3.mulPose(Axis.XP.rotationDegrees(Mth.sin($$6) * $$6 * $$7 / 10.0f * (float)$$0.getHurtDir()));
        }
        if (!Mth.equal($$8 = $$0.getBubbleAngle($$2), 0.0f)) {
            $$3.mulPose(new Quaternionf().setAngleAxis($$0.getBubbleAngle($$2) * ((float)Math.PI / 180), 1.0f, 0.0f, 1.0f));
        }
        Pair $$9 = (Pair)this.boatResources.get((Object)$$0.getVariant());
        ResourceLocation $$10 = (ResourceLocation)$$9.getFirst();
        ListModel $$11 = (ListModel)$$9.getSecond();
        $$3.scale(-1.0f, -1.0f, 1.0f);
        $$3.mulPose(Axis.YP.rotationDegrees(90.0f));
        $$11.setupAnim($$0, $$2, 0.0f, -0.1f, 0.0f, 0.0f);
        VertexConsumer $$12 = $$4.getBuffer($$11.renderType($$10));
        $$11.renderToBuffer($$3, $$12, $$5, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
        if (!$$0.isUnderWater()) {
            VertexConsumer $$13 = $$4.getBuffer(RenderType.waterMask());
            if ($$11 instanceof WaterPatchModel) {
                WaterPatchModel $$14 = (WaterPatchModel)((Object)$$11);
                $$14.waterPatch().render($$3, $$13, $$5, OverlayTexture.NO_OVERLAY);
            }
        }
        $$3.popPose();
        super.render($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    public ResourceLocation getTextureLocation(Boat $$0) {
        return (ResourceLocation)((Pair)this.boatResources.get((Object)$$0.getVariant())).getFirst();
    }
}