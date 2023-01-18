/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2IntFunction
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.BiPredicate
 *  java.util.function.Function
 */
package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import java.util.function.BiPredicate;
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
import net.minecraft.client.renderer.blockentity.BrightnessCombiner;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;

public class BedRenderer
implements BlockEntityRenderer<BedBlockEntity> {
    private final ModelPart headRoot;
    private final ModelPart footRoot;

    public BedRenderer(BlockEntityRendererProvider.Context $$0) {
        this.headRoot = $$0.bakeLayer(ModelLayers.BED_HEAD);
        this.footRoot = $$0.bakeLayer(ModelLayers.BED_FOOT);
    }

    public static LayerDefinition createHeadLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        $$1.addOrReplaceChild("main", CubeListBuilder.create().texOffs(0, 0).addBox(0.0f, 0.0f, 0.0f, 16.0f, 16.0f, 6.0f), PartPose.ZERO);
        $$1.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(50, 6).addBox(0.0f, 6.0f, 0.0f, 3.0f, 3.0f, 3.0f), PartPose.rotation(1.5707964f, 0.0f, 1.5707964f));
        $$1.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(50, 18).addBox(-16.0f, 6.0f, 0.0f, 3.0f, 3.0f, 3.0f), PartPose.rotation(1.5707964f, 0.0f, (float)Math.PI));
        return LayerDefinition.create($$0, 64, 64);
    }

    public static LayerDefinition createFootLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        $$1.addOrReplaceChild("main", CubeListBuilder.create().texOffs(0, 22).addBox(0.0f, 0.0f, 0.0f, 16.0f, 16.0f, 6.0f), PartPose.ZERO);
        $$1.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(50, 0).addBox(0.0f, 6.0f, -16.0f, 3.0f, 3.0f, 3.0f), PartPose.rotation(1.5707964f, 0.0f, 0.0f));
        $$1.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(50, 12).addBox(-16.0f, 6.0f, -16.0f, 3.0f, 3.0f, 3.0f), PartPose.rotation(1.5707964f, 0.0f, 4.712389f));
        return LayerDefinition.create($$0, 64, 64);
    }

    @Override
    public void render(BedBlockEntity $$02, float $$12, PoseStack $$2, MultiBufferSource $$3, int $$4, int $$5) {
        Material $$6 = Sheets.BED_TEXTURES[$$02.getColor().getId()];
        Level $$7 = $$02.getLevel();
        if ($$7 != null) {
            BlockState $$8 = $$02.getBlockState();
            DoubleBlockCombiner.NeighborCombineResult<BedBlockEntity> $$9 = DoubleBlockCombiner.combineWithNeigbour(BlockEntityType.BED, (Function<BlockState, DoubleBlockCombiner.BlockType>)((Function)BedBlock::getBlockType), (Function<BlockState, Direction>)((Function)BedBlock::getConnectedDirection), ChestBlock.FACING, $$8, $$7, $$02.getBlockPos(), (BiPredicate<LevelAccessor, BlockPos>)((BiPredicate)($$0, $$1) -> false));
            int $$10 = ((Int2IntFunction)$$9.apply(new BrightnessCombiner())).get($$4);
            this.renderPiece($$2, $$3, $$8.getValue(BedBlock.PART) == BedPart.HEAD ? this.headRoot : this.footRoot, $$8.getValue(BedBlock.FACING), $$6, $$10, $$5, false);
        } else {
            this.renderPiece($$2, $$3, this.headRoot, Direction.SOUTH, $$6, $$4, $$5, false);
            this.renderPiece($$2, $$3, this.footRoot, Direction.SOUTH, $$6, $$4, $$5, true);
        }
    }

    private void renderPiece(PoseStack $$0, MultiBufferSource $$1, ModelPart $$2, Direction $$3, Material $$4, int $$5, int $$6, boolean $$7) {
        $$0.pushPose();
        $$0.translate(0.0f, 0.5625f, $$7 ? -1.0f : 0.0f);
        $$0.mulPose(Axis.XP.rotationDegrees(90.0f));
        $$0.translate(0.5f, 0.5f, 0.5f);
        $$0.mulPose(Axis.ZP.rotationDegrees(180.0f + $$3.toYRot()));
        $$0.translate(-0.5f, -0.5f, -0.5f);
        VertexConsumer $$8 = $$4.buffer($$1, (Function<ResourceLocation, RenderType>)((Function)RenderType::entitySolid));
        $$2.render($$0, $$8, $$5, $$6);
        $$0.popPose();
    }
}