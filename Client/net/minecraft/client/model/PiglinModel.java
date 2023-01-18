/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.model;

import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinArmPose;

public class PiglinModel<T extends Mob>
extends PlayerModel<T> {
    public final ModelPart rightEar;
    private final ModelPart leftEar;
    private final PartPose bodyDefault;
    private final PartPose headDefault;
    private final PartPose leftArmDefault;
    private final PartPose rightArmDefault;

    public PiglinModel(ModelPart $$0) {
        super($$0, false);
        this.rightEar = this.head.getChild("right_ear");
        this.leftEar = this.head.getChild("left_ear");
        this.bodyDefault = this.body.storePose();
        this.headDefault = this.head.storePose();
        this.leftArmDefault = this.leftArm.storePose();
        this.rightArmDefault = this.rightArm.storePose();
    }

    public static MeshDefinition createMesh(CubeDeformation $$0) {
        MeshDefinition $$1 = PlayerModel.createMesh($$0, false);
        PartDefinition $$2 = $$1.getRoot();
        $$2.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, $$0), PartPose.ZERO);
        PiglinModel.addHead($$0, $$1);
        $$2.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        return $$1;
    }

    public static void addHead(CubeDeformation $$0, MeshDefinition $$1) {
        PartDefinition $$2 = $$1.getRoot();
        PartDefinition $$3 = $$2.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0f, -8.0f, -4.0f, 10.0f, 8.0f, 8.0f, $$0).texOffs(31, 1).addBox(-2.0f, -4.0f, -5.0f, 4.0f, 4.0f, 1.0f, $$0).texOffs(2, 4).addBox(2.0f, -2.0f, -5.0f, 1.0f, 2.0f, 1.0f, $$0).texOffs(2, 0).addBox(-3.0f, -2.0f, -5.0f, 1.0f, 2.0f, 1.0f, $$0), PartPose.ZERO);
        $$3.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(51, 6).addBox(0.0f, 0.0f, -2.0f, 1.0f, 5.0f, 4.0f, $$0), PartPose.offsetAndRotation(4.5f, -6.0f, 0.0f, 0.0f, 0.0f, -0.5235988f));
        $$3.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(39, 6).addBox(-1.0f, 0.0f, -2.0f, 1.0f, 5.0f, 4.0f, $$0), PartPose.offsetAndRotation(-4.5f, -6.0f, 0.0f, 0.0f, 0.0f, 0.5235988f));
    }

    @Override
    public void setupAnim(T $$0, float $$1, float $$2, float $$3, float $$4, float $$5) {
        this.body.loadPose(this.bodyDefault);
        this.head.loadPose(this.headDefault);
        this.leftArm.loadPose(this.leftArmDefault);
        this.rightArm.loadPose(this.rightArmDefault);
        super.setupAnim($$0, $$1, $$2, $$3, $$4, $$5);
        float $$6 = 0.5235988f;
        float $$7 = $$3 * 0.1f + $$1 * 0.5f;
        float $$8 = 0.08f + $$2 * 0.4f;
        this.leftEar.zRot = -0.5235988f - Mth.cos($$7 * 1.2f) * $$8;
        this.rightEar.zRot = 0.5235988f + Mth.cos($$7) * $$8;
        if ($$0 instanceof AbstractPiglin) {
            AbstractPiglin $$9 = (AbstractPiglin)$$0;
            PiglinArmPose $$10 = $$9.getArmPose();
            if ($$10 == PiglinArmPose.DANCING) {
                float $$11 = $$3 / 60.0f;
                this.rightEar.zRot = 0.5235988f + (float)Math.PI / 180 * Mth.sin($$11 * 30.0f) * 10.0f;
                this.leftEar.zRot = -0.5235988f - (float)Math.PI / 180 * Mth.cos($$11 * 30.0f) * 10.0f;
                this.head.x = Mth.sin($$11 * 10.0f);
                this.head.y = Mth.sin($$11 * 40.0f) + 0.4f;
                this.rightArm.zRot = (float)Math.PI / 180 * (70.0f + Mth.cos($$11 * 40.0f) * 10.0f);
                this.leftArm.zRot = this.rightArm.zRot * -1.0f;
                this.rightArm.y = Mth.sin($$11 * 40.0f) * 0.5f + 1.5f;
                this.leftArm.y = Mth.sin($$11 * 40.0f) * 0.5f + 1.5f;
                this.body.y = Mth.sin($$11 * 40.0f) * 0.35f;
            } else if ($$10 == PiglinArmPose.ATTACKING_WITH_MELEE_WEAPON && this.attackTime == 0.0f) {
                this.holdWeaponHigh($$0);
            } else if ($$10 == PiglinArmPose.CROSSBOW_HOLD) {
                AnimationUtils.animateCrossbowHold(this.rightArm, this.leftArm, this.head, !((Mob)$$0).isLeftHanded());
            } else if ($$10 == PiglinArmPose.CROSSBOW_CHARGE) {
                AnimationUtils.animateCrossbowCharge(this.rightArm, this.leftArm, $$0, !((Mob)$$0).isLeftHanded());
            } else if ($$10 == PiglinArmPose.ADMIRING_ITEM) {
                this.head.xRot = 0.5f;
                this.head.yRot = 0.0f;
                if (((Mob)$$0).isLeftHanded()) {
                    this.rightArm.yRot = -0.5f;
                    this.rightArm.xRot = -0.9f;
                } else {
                    this.leftArm.yRot = 0.5f;
                    this.leftArm.xRot = -0.9f;
                }
            }
        } else if (((Entity)$$0).getType() == EntityType.ZOMBIFIED_PIGLIN) {
            AnimationUtils.animateZombieArms(this.leftArm, this.rightArm, ((Mob)$$0).isAggressive(), this.attackTime, $$3);
        }
        this.leftPants.copyFrom(this.leftLeg);
        this.rightPants.copyFrom(this.rightLeg);
        this.leftSleeve.copyFrom(this.leftArm);
        this.rightSleeve.copyFrom(this.rightArm);
        this.jacket.copyFrom(this.body);
        this.hat.copyFrom(this.head);
    }

    @Override
    protected void setupAttackAnimation(T $$0, float $$1) {
        if (this.attackTime > 0.0f && $$0 instanceof Piglin && ((Piglin)$$0).getArmPose() == PiglinArmPose.ATTACKING_WITH_MELEE_WEAPON) {
            AnimationUtils.swingWeaponDown(this.rightArm, this.leftArm, $$0, this.attackTime, $$1);
            return;
        }
        super.setupAttackAnimation($$0, $$1);
    }

    private void holdWeaponHigh(T $$0) {
        if (((Mob)$$0).isLeftHanded()) {
            this.leftArm.xRot = -1.8f;
        } else {
            this.rightArm.xRot = -1.8f;
        }
    }
}