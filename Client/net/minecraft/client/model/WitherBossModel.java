/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 */
package net.minecraft.client.model;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.boss.wither.WitherBoss;

public class WitherBossModel<T extends WitherBoss>
extends HierarchicalModel<T> {
    private static final String RIBCAGE = "ribcage";
    private static final String CENTER_HEAD = "center_head";
    private static final String RIGHT_HEAD = "right_head";
    private static final String LEFT_HEAD = "left_head";
    private static final float RIBCAGE_X_ROT_OFFSET = 0.065f;
    private static final float TAIL_X_ROT_OFFSET = 0.265f;
    private final ModelPart root;
    private final ModelPart centerHead;
    private final ModelPart rightHead;
    private final ModelPart leftHead;
    private final ModelPart ribcage;
    private final ModelPart tail;

    public WitherBossModel(ModelPart $$0) {
        this.root = $$0;
        this.ribcage = $$0.getChild(RIBCAGE);
        this.tail = $$0.getChild("tail");
        this.centerHead = $$0.getChild(CENTER_HEAD);
        this.rightHead = $$0.getChild(RIGHT_HEAD);
        this.leftHead = $$0.getChild(LEFT_HEAD);
    }

    public static LayerDefinition createBodyLayer(CubeDeformation $$0) {
        MeshDefinition $$1 = new MeshDefinition();
        PartDefinition $$2 = $$1.getRoot();
        $$2.addOrReplaceChild("shoulders", CubeListBuilder.create().texOffs(0, 16).addBox(-10.0f, 3.9f, -0.5f, 20.0f, 3.0f, 3.0f, $$0), PartPose.ZERO);
        float $$3 = 0.20420352f;
        $$2.addOrReplaceChild(RIBCAGE, CubeListBuilder.create().texOffs(0, 22).addBox(0.0f, 0.0f, 0.0f, 3.0f, 10.0f, 3.0f, $$0).texOffs(24, 22).addBox(-4.0f, 1.5f, 0.5f, 11.0f, 2.0f, 2.0f, $$0).texOffs(24, 22).addBox(-4.0f, 4.0f, 0.5f, 11.0f, 2.0f, 2.0f, $$0).texOffs(24, 22).addBox(-4.0f, 6.5f, 0.5f, 11.0f, 2.0f, 2.0f, $$0), PartPose.offsetAndRotation(-2.0f, 6.9f, -0.5f, 0.20420352f, 0.0f, 0.0f));
        $$2.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(12, 22).addBox(0.0f, 0.0f, 0.0f, 3.0f, 6.0f, 3.0f, $$0), PartPose.offsetAndRotation(-2.0f, 6.9f + Mth.cos(0.20420352f) * 10.0f, -0.5f + Mth.sin(0.20420352f) * 10.0f, 0.83252203f, 0.0f, 0.0f));
        $$2.addOrReplaceChild(CENTER_HEAD, CubeListBuilder.create().texOffs(0, 0).addBox(-4.0f, -4.0f, -4.0f, 8.0f, 8.0f, 8.0f, $$0), PartPose.ZERO);
        CubeListBuilder $$4 = CubeListBuilder.create().texOffs(32, 0).addBox(-4.0f, -4.0f, -4.0f, 6.0f, 6.0f, 6.0f, $$0);
        $$2.addOrReplaceChild(RIGHT_HEAD, $$4, PartPose.offset(-8.0f, 4.0f, 0.0f));
        $$2.addOrReplaceChild(LEFT_HEAD, $$4, PartPose.offset(10.0f, 4.0f, 0.0f));
        return LayerDefinition.create($$1, 64, 64);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T $$0, float $$1, float $$2, float $$3, float $$4, float $$5) {
        float $$6 = Mth.cos($$3 * 0.1f);
        this.ribcage.xRot = (0.065f + 0.05f * $$6) * (float)Math.PI;
        this.tail.setPos(-2.0f, 6.9f + Mth.cos(this.ribcage.xRot) * 10.0f, -0.5f + Mth.sin(this.ribcage.xRot) * 10.0f);
        this.tail.xRot = (0.265f + 0.1f * $$6) * (float)Math.PI;
        this.centerHead.yRot = $$4 * ((float)Math.PI / 180);
        this.centerHead.xRot = $$5 * ((float)Math.PI / 180);
    }

    @Override
    public void prepareMobModel(T $$0, float $$1, float $$2, float $$3) {
        WitherBossModel.setupHeadRotation($$0, this.rightHead, 0);
        WitherBossModel.setupHeadRotation($$0, this.leftHead, 1);
    }

    private static <T extends WitherBoss> void setupHeadRotation(T $$0, ModelPart $$1, int $$2) {
        $$1.yRot = ($$0.getHeadYRot($$2) - $$0.yBodyRot) * ((float)Math.PI / 180);
        $$1.xRot = $$0.getHeadXRot($$2) * ((float)Math.PI / 180);
    }
}