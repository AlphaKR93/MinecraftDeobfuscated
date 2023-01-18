/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Map
 *  java.util.function.Function
 */
package net.minecraft.client.renderer.blockentity;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.client.model.Model;
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
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.Vec3;

public class HangingSignRenderer
extends SignRenderer {
    private static final String PLANK = "plank";
    private static final String V_CHAINS = "vChains";
    public static final String NORMAL_CHAINS = "normalChains";
    public static final String CHAIN_L_1 = "chainL1";
    public static final String CHAIN_L_2 = "chainL2";
    public static final String CHAIN_R_1 = "chainR1";
    public static final String CHAIN_R_2 = "chainR2";
    public static final String BOARD = "board";
    private final Map<WoodType, HangingSignModel> hangingSignModels = (Map)WoodType.values().collect(ImmutableMap.toImmutableMap($$0 -> $$0, $$1 -> new HangingSignModel($$02.bakeLayer(ModelLayers.createHangingSignModelName($$1)))));

    public HangingSignRenderer(BlockEntityRendererProvider.Context $$02) {
        super($$02);
    }

    @Override
    public void render(SignBlockEntity $$0, float $$1, PoseStack $$2, MultiBufferSource $$3, int $$4, int $$5) {
        BlockState $$6 = $$0.getBlockState();
        $$2.pushPose();
        WoodType $$7 = SignBlock.getWoodType($$6.getBlock());
        HangingSignModel $$8 = (HangingSignModel)this.hangingSignModels.get((Object)$$7);
        boolean $$9 = !($$6.getBlock() instanceof CeilingHangingSignBlock);
        boolean $$10 = $$6.hasProperty(BlockStateProperties.ATTACHED) && $$6.getValue(BlockStateProperties.ATTACHED) != false;
        $$2.translate(0.5, 0.9375, 0.5);
        if ($$10) {
            float $$11 = -RotationSegment.convertToDegrees($$6.getValue(CeilingHangingSignBlock.ROTATION));
            $$2.mulPose(Axis.YP.rotationDegrees($$11));
        } else {
            $$2.mulPose(Axis.YP.rotationDegrees(this.getSignAngle($$6, $$9)));
        }
        $$2.translate(0.0f, -0.3125f, 0.0f);
        $$8.evaluateVisibleParts($$6);
        float $$12 = 1.0f;
        this.renderSign($$2, $$3, $$4, $$5, 1.0f, $$7, $$8);
        this.renderSignText($$0, $$2, $$3, $$4, 1.0f);
    }

    private float getSignAngle(BlockState $$0, boolean $$1) {
        return $$1 ? -$$0.getValue(WallSignBlock.FACING).toYRot() : -((float)($$0.getValue(CeilingHangingSignBlock.ROTATION) * 360) / 16.0f);
    }

    @Override
    Material getSignMaterial(WoodType $$0) {
        return Sheets.getHangingSignMaterial($$0);
    }

    @Override
    void renderSignModel(PoseStack $$0, int $$1, int $$2, Model $$3, VertexConsumer $$4) {
        HangingSignModel $$5 = (HangingSignModel)$$3;
        $$5.root.render($$0, $$4, $$1, $$2);
    }

    @Override
    Vec3 getTextOffset(float $$0) {
        return new Vec3(0.0, -0.32f * $$0, 0.063f * $$0);
    }

    public static LayerDefinition createHangingSignLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        $$1.addOrReplaceChild(BOARD, CubeListBuilder.create().texOffs(0, 12).addBox(-7.0f, 0.0f, -1.0f, 14.0f, 10.0f, 2.0f), PartPose.ZERO);
        $$1.addOrReplaceChild(PLANK, CubeListBuilder.create().texOffs(0, 0).addBox(-8.0f, -6.0f, -2.0f, 16.0f, 2.0f, 4.0f), PartPose.ZERO);
        PartDefinition $$2 = $$1.addOrReplaceChild(NORMAL_CHAINS, CubeListBuilder.create(), PartPose.ZERO);
        $$2.addOrReplaceChild(CHAIN_L_1, CubeListBuilder.create().texOffs(0, 6).addBox(-1.5f, 0.0f, 0.0f, 3.0f, 6.0f, 0.0f), PartPose.offsetAndRotation(-5.0f, -6.0f, 0.0f, 0.0f, -0.7853982f, 0.0f));
        $$2.addOrReplaceChild(CHAIN_L_2, CubeListBuilder.create().texOffs(6, 6).addBox(-1.5f, 0.0f, 0.0f, 3.0f, 6.0f, 0.0f), PartPose.offsetAndRotation(-5.0f, -6.0f, 0.0f, 0.0f, 0.7853982f, 0.0f));
        $$2.addOrReplaceChild(CHAIN_R_1, CubeListBuilder.create().texOffs(0, 6).addBox(-1.5f, 0.0f, 0.0f, 3.0f, 6.0f, 0.0f), PartPose.offsetAndRotation(5.0f, -6.0f, 0.0f, 0.0f, -0.7853982f, 0.0f));
        $$2.addOrReplaceChild(CHAIN_R_2, CubeListBuilder.create().texOffs(6, 6).addBox(-1.5f, 0.0f, 0.0f, 3.0f, 6.0f, 0.0f), PartPose.offsetAndRotation(5.0f, -6.0f, 0.0f, 0.0f, 0.7853982f, 0.0f));
        $$1.addOrReplaceChild(V_CHAINS, CubeListBuilder.create().texOffs(14, 6).addBox(-6.0f, -6.0f, 0.0f, 12.0f, 6.0f, 0.0f), PartPose.ZERO);
        return LayerDefinition.create($$0, 64, 32);
    }

    public static final class HangingSignModel
    extends Model {
        public final ModelPart root;
        public final ModelPart plank;
        public final ModelPart vChains;
        public final ModelPart normalChains;

        public HangingSignModel(ModelPart $$0) {
            super((Function<ResourceLocation, RenderType>)((Function)RenderType::entityCutoutNoCull));
            this.root = $$0;
            this.plank = $$0.getChild(HangingSignRenderer.PLANK);
            this.normalChains = $$0.getChild(HangingSignRenderer.NORMAL_CHAINS);
            this.vChains = $$0.getChild(HangingSignRenderer.V_CHAINS);
        }

        public void evaluateVisibleParts(BlockState $$0) {
            boolean $$1;
            this.plank.visible = $$1 = !($$0.getBlock() instanceof CeilingHangingSignBlock);
            this.vChains.visible = false;
            this.normalChains.visible = true;
            if (!$$1) {
                boolean $$2 = $$0.getValue(BlockStateProperties.ATTACHED);
                this.normalChains.visible = !$$2;
                this.vChains.visible = $$2;
            }
        }

        @Override
        public void renderToBuffer(PoseStack $$0, VertexConsumer $$1, int $$2, int $$3, float $$4, float $$5, float $$6, float $$7) {
            this.root.render($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
        }
    }
}