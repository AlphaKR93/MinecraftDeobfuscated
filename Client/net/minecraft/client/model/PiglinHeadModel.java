/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.PiglinModel;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.MeshDefinition;

public class PiglinHeadModel
extends SkullModelBase {
    private final ModelPart head;
    private final ModelPart leftEar;
    private final ModelPart rightEar;

    public PiglinHeadModel(ModelPart $$0) {
        this.head = $$0.getChild("head");
        this.leftEar = this.head.getChild("left_ear");
        this.rightEar = this.head.getChild("right_ear");
    }

    public static MeshDefinition createHeadModel() {
        MeshDefinition $$0 = new MeshDefinition();
        PiglinModel.addHead(CubeDeformation.NONE, $$0);
        return $$0;
    }

    @Override
    public void setupAnim(float $$0, float $$1, float $$2) {
        this.head.yRot = $$1 * ((float)Math.PI / 180);
        this.head.xRot = $$2 * ((float)Math.PI / 180);
        float $$3 = 1.2f;
        this.leftEar.zRot = (float)(-(Math.cos((double)($$0 * (float)Math.PI * 0.2f * 1.2f)) + 2.5)) * 0.2f;
        this.rightEar.zRot = (float)(Math.cos((double)($$0 * (float)Math.PI * 0.2f)) + 2.5) * 0.2f;
    }

    @Override
    public void renderToBuffer(PoseStack $$0, VertexConsumer $$1, int $$2, int $$3, float $$4, float $$5, float $$6, float $$7) {
        this.head.render($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
    }
}