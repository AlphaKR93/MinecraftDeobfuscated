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

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.phys.Vec3;

public class GuardianModel
extends HierarchicalModel<Guardian> {
    private static final float[] SPIKE_X_ROT = new float[]{1.75f, 0.25f, 0.0f, 0.0f, 0.5f, 0.5f, 0.5f, 0.5f, 1.25f, 0.75f, 0.0f, 0.0f};
    private static final float[] SPIKE_Y_ROT = new float[]{0.0f, 0.0f, 0.0f, 0.0f, 0.25f, 1.75f, 1.25f, 0.75f, 0.0f, 0.0f, 0.0f, 0.0f};
    private static final float[] SPIKE_Z_ROT = new float[]{0.0f, 0.0f, 0.25f, 1.75f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.75f, 1.25f};
    private static final float[] SPIKE_X = new float[]{0.0f, 0.0f, 8.0f, -8.0f, -8.0f, 8.0f, 8.0f, -8.0f, 0.0f, 0.0f, 8.0f, -8.0f};
    private static final float[] SPIKE_Y = new float[]{-8.0f, -8.0f, -8.0f, -8.0f, 0.0f, 0.0f, 0.0f, 0.0f, 8.0f, 8.0f, 8.0f, 8.0f};
    private static final float[] SPIKE_Z = new float[]{8.0f, -8.0f, 0.0f, 0.0f, -8.0f, -8.0f, 8.0f, 8.0f, 8.0f, -8.0f, 0.0f, 0.0f};
    private static final String EYE = "eye";
    private static final String TAIL_0 = "tail0";
    private static final String TAIL_1 = "tail1";
    private static final String TAIL_2 = "tail2";
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart eye;
    private final ModelPart[] spikeParts;
    private final ModelPart[] tailParts;

    public GuardianModel(ModelPart $$0) {
        this.root = $$0;
        this.spikeParts = new ModelPart[12];
        this.head = $$0.getChild("head");
        for (int $$1 = 0; $$1 < this.spikeParts.length; ++$$1) {
            this.spikeParts[$$1] = this.head.getChild(GuardianModel.createSpikeName($$1));
        }
        this.eye = this.head.getChild(EYE);
        this.tailParts = new ModelPart[3];
        this.tailParts[0] = this.head.getChild(TAIL_0);
        this.tailParts[1] = this.tailParts[0].getChild(TAIL_1);
        this.tailParts[2] = this.tailParts[1].getChild(TAIL_2);
    }

    private static String createSpikeName(int $$0) {
        return "spike" + $$0;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        PartDefinition $$2 = $$1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0f, 10.0f, -8.0f, 12.0f, 12.0f, 16.0f).texOffs(0, 28).addBox(-8.0f, 10.0f, -6.0f, 2.0f, 12.0f, 12.0f).texOffs(0, 28).addBox(6.0f, 10.0f, -6.0f, 2.0f, 12.0f, 12.0f, true).texOffs(16, 40).addBox(-6.0f, 8.0f, -6.0f, 12.0f, 2.0f, 12.0f).texOffs(16, 40).addBox(-6.0f, 22.0f, -6.0f, 12.0f, 2.0f, 12.0f), PartPose.ZERO);
        CubeListBuilder $$3 = CubeListBuilder.create().texOffs(0, 0).addBox(-1.0f, -4.5f, -1.0f, 2.0f, 9.0f, 2.0f);
        for (int $$4 = 0; $$4 < 12; ++$$4) {
            float $$5 = GuardianModel.getSpikeX($$4, 0.0f, 0.0f);
            float $$6 = GuardianModel.getSpikeY($$4, 0.0f, 0.0f);
            float $$7 = GuardianModel.getSpikeZ($$4, 0.0f, 0.0f);
            float $$8 = (float)Math.PI * SPIKE_X_ROT[$$4];
            float $$9 = (float)Math.PI * SPIKE_Y_ROT[$$4];
            float $$10 = (float)Math.PI * SPIKE_Z_ROT[$$4];
            $$2.addOrReplaceChild(GuardianModel.createSpikeName($$4), $$3, PartPose.offsetAndRotation($$5, $$6, $$7, $$8, $$9, $$10));
        }
        $$2.addOrReplaceChild(EYE, CubeListBuilder.create().texOffs(8, 0).addBox(-1.0f, 15.0f, 0.0f, 2.0f, 2.0f, 1.0f), PartPose.offset(0.0f, 0.0f, -8.25f));
        PartDefinition $$11 = $$2.addOrReplaceChild(TAIL_0, CubeListBuilder.create().texOffs(40, 0).addBox(-2.0f, 14.0f, 7.0f, 4.0f, 4.0f, 8.0f), PartPose.ZERO);
        PartDefinition $$12 = $$11.addOrReplaceChild(TAIL_1, CubeListBuilder.create().texOffs(0, 54).addBox(0.0f, 14.0f, 0.0f, 3.0f, 3.0f, 7.0f), PartPose.offset(-1.5f, 0.5f, 14.0f));
        $$12.addOrReplaceChild(TAIL_2, CubeListBuilder.create().texOffs(41, 32).addBox(0.0f, 14.0f, 0.0f, 2.0f, 2.0f, 6.0f).texOffs(25, 19).addBox(1.0f, 10.5f, 3.0f, 1.0f, 9.0f, 9.0f), PartPose.offset(0.5f, 0.5f, 6.0f));
        return LayerDefinition.create($$0, 64, 64);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(Guardian $$0, float $$1, float $$2, float $$3, float $$4, float $$5) {
        float $$6 = $$3 - (float)$$0.tickCount;
        this.head.yRot = $$4 * ((float)Math.PI / 180);
        this.head.xRot = $$5 * ((float)Math.PI / 180);
        float $$7 = (1.0f - $$0.getSpikesAnimation($$6)) * 0.55f;
        this.setupSpikes($$3, $$7);
        Entity $$8 = Minecraft.getInstance().getCameraEntity();
        if ($$0.hasActiveAttackTarget()) {
            $$8 = $$0.getActiveAttackTarget();
        }
        if ($$8 != null) {
            Vec3 $$9 = $$8.getEyePosition(0.0f);
            Vec3 $$10 = $$0.getEyePosition(0.0f);
            double $$11 = $$9.y - $$10.y;
            this.eye.y = $$11 > 0.0 ? 0.0f : 1.0f;
            Vec3 $$12 = $$0.getViewVector(0.0f);
            $$12 = new Vec3($$12.x, 0.0, $$12.z);
            Vec3 $$13 = new Vec3($$10.x - $$9.x, 0.0, $$10.z - $$9.z).normalize().yRot(1.5707964f);
            double $$14 = $$12.dot($$13);
            this.eye.x = Mth.sqrt((float)Math.abs((double)$$14)) * 2.0f * (float)Math.signum((double)$$14);
        }
        this.eye.visible = true;
        float $$15 = $$0.getTailAnimation($$6);
        this.tailParts[0].yRot = Mth.sin($$15) * (float)Math.PI * 0.05f;
        this.tailParts[1].yRot = Mth.sin($$15) * (float)Math.PI * 0.1f;
        this.tailParts[2].yRot = Mth.sin($$15) * (float)Math.PI * 0.15f;
    }

    private void setupSpikes(float $$0, float $$1) {
        for (int $$2 = 0; $$2 < 12; ++$$2) {
            this.spikeParts[$$2].x = GuardianModel.getSpikeX($$2, $$0, $$1);
            this.spikeParts[$$2].y = GuardianModel.getSpikeY($$2, $$0, $$1);
            this.spikeParts[$$2].z = GuardianModel.getSpikeZ($$2, $$0, $$1);
        }
    }

    private static float getSpikeOffset(int $$0, float $$1, float $$2) {
        return 1.0f + Mth.cos($$1 * 1.5f + (float)$$0) * 0.01f - $$2;
    }

    private static float getSpikeX(int $$0, float $$1, float $$2) {
        return SPIKE_X[$$0] * GuardianModel.getSpikeOffset($$0, $$1, $$2);
    }

    private static float getSpikeY(int $$0, float $$1, float $$2) {
        return 16.0f + SPIKE_Y[$$0] * GuardianModel.getSpikeOffset($$0, $$1, $$2);
    }

    private static float getSpikeZ(int $$0, float $$1, float $$2) {
        return SPIKE_Z[$$0] * GuardianModel.getSpikeOffset($$0, $$1, $$2);
    }
}