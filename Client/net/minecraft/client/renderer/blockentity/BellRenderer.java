/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.function.Function
 */
package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
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
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BellBlockEntity;

public class BellRenderer
implements BlockEntityRenderer<BellBlockEntity> {
    public static final Material BELL_RESOURCE_LOCATION = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("entity/bell/bell_body"));
    private static final String BELL_BODY = "bell_body";
    private final ModelPart bellBody;

    public BellRenderer(BlockEntityRendererProvider.Context $$0) {
        ModelPart $$1 = $$0.bakeLayer(ModelLayers.BELL);
        this.bellBody = $$1.getChild(BELL_BODY);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        PartDefinition $$2 = $$1.addOrReplaceChild(BELL_BODY, CubeListBuilder.create().texOffs(0, 0).addBox(-3.0f, -6.0f, -3.0f, 6.0f, 7.0f, 6.0f), PartPose.offset(8.0f, 12.0f, 8.0f));
        $$2.addOrReplaceChild("bell_base", CubeListBuilder.create().texOffs(0, 13).addBox(4.0f, 4.0f, 4.0f, 8.0f, 2.0f, 8.0f), PartPose.offset(-8.0f, -12.0f, -8.0f));
        return LayerDefinition.create($$0, 32, 32);
    }

    @Override
    public void render(BellBlockEntity $$0, float $$1, PoseStack $$2, MultiBufferSource $$3, int $$4, int $$5) {
        float $$6 = (float)$$0.ticks + $$1;
        float $$7 = 0.0f;
        float $$8 = 0.0f;
        if ($$0.shaking) {
            float $$9 = Mth.sin($$6 / (float)Math.PI) / (4.0f + $$6 / 3.0f);
            if ($$0.clickDirection == Direction.NORTH) {
                $$7 = -$$9;
            } else if ($$0.clickDirection == Direction.SOUTH) {
                $$7 = $$9;
            } else if ($$0.clickDirection == Direction.EAST) {
                $$8 = -$$9;
            } else if ($$0.clickDirection == Direction.WEST) {
                $$8 = $$9;
            }
        }
        this.bellBody.xRot = $$7;
        this.bellBody.zRot = $$8;
        VertexConsumer $$10 = BELL_RESOURCE_LOCATION.buffer($$3, (Function<ResourceLocation, RenderType>)((Function)RenderType::entitySolid));
        this.bellBody.render($$2, $$10, $$4, $$5);
    }
}