/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;

public class LlamaModel<T extends AbstractChestedHorse>
extends EntityModel<T> {
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart rightChest;
    private final ModelPart leftChest;

    public LlamaModel(ModelPart $$0) {
        this.head = $$0.getChild("head");
        this.body = $$0.getChild("body");
        this.rightChest = $$0.getChild("right_chest");
        this.leftChest = $$0.getChild("left_chest");
        this.rightHindLeg = $$0.getChild("right_hind_leg");
        this.leftHindLeg = $$0.getChild("left_hind_leg");
        this.rightFrontLeg = $$0.getChild("right_front_leg");
        this.leftFrontLeg = $$0.getChild("left_front_leg");
    }

    public static LayerDefinition createBodyLayer(CubeDeformation $$0) {
        MeshDefinition $$1 = new MeshDefinition();
        PartDefinition $$2 = $$1.getRoot();
        $$2.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0f, -14.0f, -10.0f, 4.0f, 4.0f, 9.0f, $$0).texOffs(0, 14).addBox("neck", -4.0f, -16.0f, -6.0f, 8.0f, 18.0f, 6.0f, $$0).texOffs(17, 0).addBox("ear", -4.0f, -19.0f, -4.0f, 3.0f, 3.0f, 2.0f, $$0).texOffs(17, 0).addBox("ear", 1.0f, -19.0f, -4.0f, 3.0f, 3.0f, 2.0f, $$0), PartPose.offset(0.0f, 7.0f, -6.0f));
        $$2.addOrReplaceChild("body", CubeListBuilder.create().texOffs(29, 0).addBox(-6.0f, -10.0f, -7.0f, 12.0f, 18.0f, 10.0f, $$0), PartPose.offsetAndRotation(0.0f, 5.0f, 2.0f, 1.5707964f, 0.0f, 0.0f));
        $$2.addOrReplaceChild("right_chest", CubeListBuilder.create().texOffs(45, 28).addBox(-3.0f, 0.0f, 0.0f, 8.0f, 8.0f, 3.0f, $$0), PartPose.offsetAndRotation(-8.5f, 3.0f, 3.0f, 0.0f, 1.5707964f, 0.0f));
        $$2.addOrReplaceChild("left_chest", CubeListBuilder.create().texOffs(45, 41).addBox(-3.0f, 0.0f, 0.0f, 8.0f, 8.0f, 3.0f, $$0), PartPose.offsetAndRotation(5.5f, 3.0f, 3.0f, 0.0f, 1.5707964f, 0.0f));
        int $$3 = 4;
        int $$4 = 14;
        CubeListBuilder $$5 = CubeListBuilder.create().texOffs(29, 29).addBox(-2.0f, 0.0f, -2.0f, 4.0f, 14.0f, 4.0f, $$0);
        $$2.addOrReplaceChild("right_hind_leg", $$5, PartPose.offset(-3.5f, 10.0f, 6.0f));
        $$2.addOrReplaceChild("left_hind_leg", $$5, PartPose.offset(3.5f, 10.0f, 6.0f));
        $$2.addOrReplaceChild("right_front_leg", $$5, PartPose.offset(-3.5f, 10.0f, -5.0f));
        $$2.addOrReplaceChild("left_front_leg", $$5, PartPose.offset(3.5f, 10.0f, -5.0f));
        return LayerDefinition.create($$1, 128, 64);
    }

    @Override
    public void setupAnim(T $$0, float $$1, float $$2, float $$3, float $$4, float $$5) {
        boolean $$6;
        this.head.xRot = $$5 * ((float)Math.PI / 180);
        this.head.yRot = $$4 * ((float)Math.PI / 180);
        this.rightHindLeg.xRot = Mth.cos($$1 * 0.6662f) * 1.4f * $$2;
        this.leftHindLeg.xRot = Mth.cos($$1 * 0.6662f + (float)Math.PI) * 1.4f * $$2;
        this.rightFrontLeg.xRot = Mth.cos($$1 * 0.6662f + (float)Math.PI) * 1.4f * $$2;
        this.leftFrontLeg.xRot = Mth.cos($$1 * 0.6662f) * 1.4f * $$2;
        this.rightChest.visible = $$6 = !((AgeableMob)$$0).isBaby() && ((AbstractChestedHorse)$$0).hasChest();
        this.leftChest.visible = $$6;
    }

    @Override
    public void renderToBuffer(PoseStack $$0, VertexConsumer $$1, int $$2, int $$3, float $$4, float $$5, float $$6, float $$7) {
        if (this.young) {
            float $$82 = 2.0f;
            $$0.pushPose();
            float $$9 = 0.7f;
            $$0.scale(0.71428573f, 0.64935064f, 0.7936508f);
            $$0.translate(0.0f, 1.3125f, 0.22f);
            this.head.render($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
            $$0.popPose();
            $$0.pushPose();
            float $$10 = 1.1f;
            $$0.scale(0.625f, 0.45454544f, 0.45454544f);
            $$0.translate(0.0f, 2.0625f, 0.0f);
            this.body.render($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
            $$0.popPose();
            $$0.pushPose();
            $$0.scale(0.45454544f, 0.41322312f, 0.45454544f);
            $$0.translate(0.0f, 2.0625f, 0.0f);
            ImmutableList.of((Object)this.rightHindLeg, (Object)this.leftHindLeg, (Object)this.rightFrontLeg, (Object)this.leftFrontLeg, (Object)this.rightChest, (Object)this.leftChest).forEach($$8 -> $$8.render($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7));
            $$0.popPose();
        } else {
            ImmutableList.of((Object)this.head, (Object)this.body, (Object)this.rightHindLeg, (Object)this.leftHindLeg, (Object)this.rightFrontLeg, (Object)this.leftFrontLeg, (Object)this.rightChest, (Object)this.leftChest).forEach($$8 -> $$8.render($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7));
        }
    }
}