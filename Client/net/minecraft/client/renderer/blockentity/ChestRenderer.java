/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2IntFunction
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Calendar
 *  java.util.function.Function
 */
package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import java.util.Calendar;
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
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractChestBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;

public class ChestRenderer<T extends BlockEntity>
implements BlockEntityRenderer<T> {
    private static final String BOTTOM = "bottom";
    private static final String LID = "lid";
    private static final String LOCK = "lock";
    private final ModelPart lid;
    private final ModelPart bottom;
    private final ModelPart lock;
    private final ModelPart doubleLeftLid;
    private final ModelPart doubleLeftBottom;
    private final ModelPart doubleLeftLock;
    private final ModelPart doubleRightLid;
    private final ModelPart doubleRightBottom;
    private final ModelPart doubleRightLock;
    private boolean xmasTextures;

    public ChestRenderer(BlockEntityRendererProvider.Context $$0) {
        Calendar $$1 = Calendar.getInstance();
        if ($$1.get(2) + 1 == 12 && $$1.get(5) >= 24 && $$1.get(5) <= 26) {
            this.xmasTextures = true;
        }
        ModelPart $$2 = $$0.bakeLayer(ModelLayers.CHEST);
        this.bottom = $$2.getChild(BOTTOM);
        this.lid = $$2.getChild(LID);
        this.lock = $$2.getChild(LOCK);
        ModelPart $$3 = $$0.bakeLayer(ModelLayers.DOUBLE_CHEST_LEFT);
        this.doubleLeftBottom = $$3.getChild(BOTTOM);
        this.doubleLeftLid = $$3.getChild(LID);
        this.doubleLeftLock = $$3.getChild(LOCK);
        ModelPart $$4 = $$0.bakeLayer(ModelLayers.DOUBLE_CHEST_RIGHT);
        this.doubleRightBottom = $$4.getChild(BOTTOM);
        this.doubleRightLid = $$4.getChild(LID);
        this.doubleRightLock = $$4.getChild(LOCK);
    }

    public static LayerDefinition createSingleBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        $$1.addOrReplaceChild(BOTTOM, CubeListBuilder.create().texOffs(0, 19).addBox(1.0f, 0.0f, 1.0f, 14.0f, 10.0f, 14.0f), PartPose.ZERO);
        $$1.addOrReplaceChild(LID, CubeListBuilder.create().texOffs(0, 0).addBox(1.0f, 0.0f, 0.0f, 14.0f, 5.0f, 14.0f), PartPose.offset(0.0f, 9.0f, 1.0f));
        $$1.addOrReplaceChild(LOCK, CubeListBuilder.create().texOffs(0, 0).addBox(7.0f, -2.0f, 14.0f, 2.0f, 4.0f, 1.0f), PartPose.offset(0.0f, 9.0f, 1.0f));
        return LayerDefinition.create($$0, 64, 64);
    }

    public static LayerDefinition createDoubleBodyRightLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        $$1.addOrReplaceChild(BOTTOM, CubeListBuilder.create().texOffs(0, 19).addBox(1.0f, 0.0f, 1.0f, 15.0f, 10.0f, 14.0f), PartPose.ZERO);
        $$1.addOrReplaceChild(LID, CubeListBuilder.create().texOffs(0, 0).addBox(1.0f, 0.0f, 0.0f, 15.0f, 5.0f, 14.0f), PartPose.offset(0.0f, 9.0f, 1.0f));
        $$1.addOrReplaceChild(LOCK, CubeListBuilder.create().texOffs(0, 0).addBox(15.0f, -2.0f, 14.0f, 1.0f, 4.0f, 1.0f), PartPose.offset(0.0f, 9.0f, 1.0f));
        return LayerDefinition.create($$0, 64, 64);
    }

    public static LayerDefinition createDoubleBodyLeftLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        $$1.addOrReplaceChild(BOTTOM, CubeListBuilder.create().texOffs(0, 19).addBox(0.0f, 0.0f, 1.0f, 15.0f, 10.0f, 14.0f), PartPose.ZERO);
        $$1.addOrReplaceChild(LID, CubeListBuilder.create().texOffs(0, 0).addBox(0.0f, 0.0f, 0.0f, 15.0f, 5.0f, 14.0f), PartPose.offset(0.0f, 9.0f, 1.0f));
        $$1.addOrReplaceChild(LOCK, CubeListBuilder.create().texOffs(0, 0).addBox(0.0f, -2.0f, 14.0f, 1.0f, 4.0f, 1.0f), PartPose.offset(0.0f, 9.0f, 1.0f));
        return LayerDefinition.create($$0, 64, 64);
    }

    @Override
    public void render(T $$0, float $$1, PoseStack $$2, MultiBufferSource $$3, int $$4, int $$5) {
        DoubleBlockCombiner.NeighborCombineResult<ChestBlockEntity> $$15;
        Level $$6 = ((BlockEntity)$$0).getLevel();
        boolean $$7 = $$6 != null;
        BlockState $$8 = $$7 ? ((BlockEntity)$$0).getBlockState() : (BlockState)Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.SOUTH);
        ChestType $$9 = $$8.hasProperty(ChestBlock.TYPE) ? $$8.getValue(ChestBlock.TYPE) : ChestType.SINGLE;
        Block $$10 = $$8.getBlock();
        if (!($$10 instanceof AbstractChestBlock)) {
            return;
        }
        AbstractChestBlock $$11 = (AbstractChestBlock)$$10;
        boolean $$12 = $$9 != ChestType.SINGLE;
        $$2.pushPose();
        float $$13 = $$8.getValue(ChestBlock.FACING).toYRot();
        $$2.translate(0.5f, 0.5f, 0.5f);
        $$2.mulPose(Axis.YP.rotationDegrees(-$$13));
        $$2.translate(-0.5f, -0.5f, -0.5f);
        if ($$7) {
            DoubleBlockCombiner.NeighborCombineResult<ChestBlockEntity> $$14 = $$11.combine($$8, $$6, ((BlockEntity)$$0).getBlockPos(), true);
        } else {
            $$15 = DoubleBlockCombiner.Combiner::acceptNone;
        }
        float $$16 = $$15.apply(ChestBlock.opennessCombiner((LidBlockEntity)$$0)).get($$1);
        $$16 = 1.0f - $$16;
        $$16 = 1.0f - $$16 * $$16 * $$16;
        int $$17 = ((Int2IntFunction)$$15.apply(new BrightnessCombiner())).applyAsInt($$4);
        Material $$18 = Sheets.chooseMaterial($$0, $$9, this.xmasTextures);
        VertexConsumer $$19 = $$18.buffer($$3, (Function<ResourceLocation, RenderType>)((Function)RenderType::entityCutout));
        if ($$12) {
            if ($$9 == ChestType.LEFT) {
                this.render($$2, $$19, this.doubleLeftLid, this.doubleLeftLock, this.doubleLeftBottom, $$16, $$17, $$5);
            } else {
                this.render($$2, $$19, this.doubleRightLid, this.doubleRightLock, this.doubleRightBottom, $$16, $$17, $$5);
            }
        } else {
            this.render($$2, $$19, this.lid, this.lock, this.bottom, $$16, $$17, $$5);
        }
        $$2.popPose();
    }

    private void render(PoseStack $$0, VertexConsumer $$1, ModelPart $$2, ModelPart $$3, ModelPart $$4, float $$5, int $$6, int $$7) {
        $$3.xRot = $$2.xRot = -($$5 * 1.5707964f);
        $$2.render($$0, $$1, $$6, $$7);
        $$3.render($$0, $$1, $$6, $$7);
        $$4.render($$0, $$1, $$6, $$7);
    }
}