/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  java.lang.Iterable
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Function
 */
package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Function;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

public class HumanoidModel<T extends LivingEntity>
extends AgeableListModel<T>
implements ArmedModel,
HeadedModel {
    public static final float OVERLAY_SCALE = 0.25f;
    public static final float HAT_OVERLAY_SCALE = 0.5f;
    private static final float SPYGLASS_ARM_ROT_Y = 0.2617994f;
    private static final float SPYGLASS_ARM_ROT_X = 1.9198622f;
    private static final float SPYGLASS_ARM_CROUCH_ROT_X = 0.2617994f;
    public static final float TOOT_HORN_XROT_BASE = 1.4835298f;
    public static final float TOOT_HORN_YROT_BASE = 0.5235988f;
    public final ModelPart head;
    public final ModelPart hat;
    public final ModelPart body;
    public final ModelPart rightArm;
    public final ModelPart leftArm;
    public final ModelPart rightLeg;
    public final ModelPart leftLeg;
    public ArmPose leftArmPose = ArmPose.EMPTY;
    public ArmPose rightArmPose = ArmPose.EMPTY;
    public boolean crouching;
    public float swimAmount;

    public HumanoidModel(ModelPart $$0) {
        this($$0, (Function<ResourceLocation, RenderType>)((Function)RenderType::entityCutoutNoCull));
    }

    public HumanoidModel(ModelPart $$0, Function<ResourceLocation, RenderType> $$1) {
        super($$1, true, 16.0f, 0.0f, 2.0f, 2.0f, 24.0f);
        this.head = $$0.getChild("head");
        this.hat = $$0.getChild("hat");
        this.body = $$0.getChild("body");
        this.rightArm = $$0.getChild("right_arm");
        this.leftArm = $$0.getChild("left_arm");
        this.rightLeg = $$0.getChild("right_leg");
        this.leftLeg = $$0.getChild("left_leg");
    }

    public static MeshDefinition createMesh(CubeDeformation $$0, float $$1) {
        MeshDefinition $$2 = new MeshDefinition();
        PartDefinition $$3 = $$2.getRoot();
        $$3.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, $$0), PartPose.offset(0.0f, 0.0f + $$1, 0.0f));
        $$3.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, $$0.extend(0.5f)), PartPose.offset(0.0f, 0.0f + $$1, 0.0f));
        $$3.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, $$0), PartPose.offset(0.0f, 0.0f + $$1, 0.0f));
        $$3.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-3.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, $$0), PartPose.offset(-5.0f, 2.0f + $$1, 0.0f));
        $$3.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, $$0), PartPose.offset(5.0f, 2.0f + $$1, 0.0f));
        $$3.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, $$0), PartPose.offset(-1.9f, 12.0f + $$1, 0.0f));
        $$3.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, $$0), PartPose.offset(1.9f, 12.0f + $$1, 0.0f));
        return $$2;
    }

    @Override
    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of((Object)this.head);
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of((Object)this.body, (Object)this.rightArm, (Object)this.leftArm, (Object)this.rightLeg, (Object)this.leftLeg, (Object)this.hat);
    }

    @Override
    public void prepareMobModel(T $$0, float $$1, float $$2, float $$3) {
        this.swimAmount = ((LivingEntity)$$0).getSwimAmount($$3);
        super.prepareMobModel($$0, $$1, $$2, $$3);
    }

    @Override
    public void setupAnim(T $$0, float $$1, float $$2, float $$3, float $$4, float $$5) {
        boolean $$9;
        boolean $$6 = ((LivingEntity)$$0).getFallFlyingTicks() > 4;
        boolean $$7 = ((LivingEntity)$$0).isVisuallySwimming();
        this.head.yRot = $$4 * ((float)Math.PI / 180);
        this.head.xRot = $$6 ? -0.7853982f : (this.swimAmount > 0.0f ? ($$7 ? this.rotlerpRad(this.swimAmount, this.head.xRot, -0.7853982f) : this.rotlerpRad(this.swimAmount, this.head.xRot, $$5 * ((float)Math.PI / 180))) : $$5 * ((float)Math.PI / 180));
        this.body.yRot = 0.0f;
        this.rightArm.z = 0.0f;
        this.rightArm.x = -5.0f;
        this.leftArm.z = 0.0f;
        this.leftArm.x = 5.0f;
        float $$8 = 1.0f;
        if ($$6) {
            $$8 = (float)((Entity)$$0).getDeltaMovement().lengthSqr();
            $$8 /= 0.2f;
            $$8 *= $$8 * $$8;
        }
        if ($$8 < 1.0f) {
            $$8 = 1.0f;
        }
        this.rightArm.xRot = Mth.cos($$1 * 0.6662f + (float)Math.PI) * 2.0f * $$2 * 0.5f / $$8;
        this.leftArm.xRot = Mth.cos($$1 * 0.6662f) * 2.0f * $$2 * 0.5f / $$8;
        this.rightArm.zRot = 0.0f;
        this.leftArm.zRot = 0.0f;
        this.rightLeg.xRot = Mth.cos($$1 * 0.6662f) * 1.4f * $$2 / $$8;
        this.leftLeg.xRot = Mth.cos($$1 * 0.6662f + (float)Math.PI) * 1.4f * $$2 / $$8;
        this.rightLeg.yRot = 0.0f;
        this.leftLeg.yRot = 0.0f;
        this.rightLeg.zRot = 0.0f;
        this.leftLeg.zRot = 0.0f;
        if (this.riding) {
            this.rightArm.xRot += -0.62831855f;
            this.leftArm.xRot += -0.62831855f;
            this.rightLeg.xRot = -1.4137167f;
            this.rightLeg.yRot = 0.31415927f;
            this.rightLeg.zRot = 0.07853982f;
            this.leftLeg.xRot = -1.4137167f;
            this.leftLeg.yRot = -0.31415927f;
            this.leftLeg.zRot = -0.07853982f;
        }
        this.rightArm.yRot = 0.0f;
        this.leftArm.yRot = 0.0f;
        boolean bl = $$9 = ((LivingEntity)$$0).getMainArm() == HumanoidArm.RIGHT;
        if (((LivingEntity)$$0).isUsingItem()) {
            boolean $$10;
            boolean bl2 = $$10 = ((LivingEntity)$$0).getUsedItemHand() == InteractionHand.MAIN_HAND;
            if ($$10 == $$9) {
                this.poseRightArm($$0);
            } else {
                this.poseLeftArm($$0);
            }
        } else {
            boolean $$11;
            boolean bl3 = $$11 = $$9 ? this.leftArmPose.isTwoHanded() : this.rightArmPose.isTwoHanded();
            if ($$9 != $$11) {
                this.poseLeftArm($$0);
                this.poseRightArm($$0);
            } else {
                this.poseRightArm($$0);
                this.poseLeftArm($$0);
            }
        }
        this.setupAttackAnimation($$0, $$3);
        if (this.crouching) {
            this.body.xRot = 0.5f;
            this.rightArm.xRot += 0.4f;
            this.leftArm.xRot += 0.4f;
            this.rightLeg.z = 4.0f;
            this.leftLeg.z = 4.0f;
            this.rightLeg.y = 12.2f;
            this.leftLeg.y = 12.2f;
            this.head.y = 4.2f;
            this.body.y = 3.2f;
            this.leftArm.y = 5.2f;
            this.rightArm.y = 5.2f;
        } else {
            this.body.xRot = 0.0f;
            this.rightLeg.z = 0.1f;
            this.leftLeg.z = 0.1f;
            this.rightLeg.y = 12.0f;
            this.leftLeg.y = 12.0f;
            this.head.y = 0.0f;
            this.body.y = 0.0f;
            this.leftArm.y = 2.0f;
            this.rightArm.y = 2.0f;
        }
        if (this.rightArmPose != ArmPose.SPYGLASS) {
            AnimationUtils.bobModelPart(this.rightArm, $$3, 1.0f);
        }
        if (this.leftArmPose != ArmPose.SPYGLASS) {
            AnimationUtils.bobModelPart(this.leftArm, $$3, -1.0f);
        }
        if (this.swimAmount > 0.0f) {
            float $$15;
            float $$12 = $$1 % 26.0f;
            HumanoidArm $$13 = this.getAttackArm($$0);
            float $$14 = $$13 == HumanoidArm.RIGHT && this.attackTime > 0.0f ? 0.0f : this.swimAmount;
            float f = $$15 = $$13 == HumanoidArm.LEFT && this.attackTime > 0.0f ? 0.0f : this.swimAmount;
            if (!((LivingEntity)$$0).isUsingItem()) {
                if ($$12 < 14.0f) {
                    this.leftArm.xRot = this.rotlerpRad($$15, this.leftArm.xRot, 0.0f);
                    this.rightArm.xRot = Mth.lerp($$14, this.rightArm.xRot, 0.0f);
                    this.leftArm.yRot = this.rotlerpRad($$15, this.leftArm.yRot, (float)Math.PI);
                    this.rightArm.yRot = Mth.lerp($$14, this.rightArm.yRot, (float)Math.PI);
                    this.leftArm.zRot = this.rotlerpRad($$15, this.leftArm.zRot, (float)Math.PI + 1.8707964f * this.quadraticArmUpdate($$12) / this.quadraticArmUpdate(14.0f));
                    this.rightArm.zRot = Mth.lerp($$14, this.rightArm.zRot, (float)Math.PI - 1.8707964f * this.quadraticArmUpdate($$12) / this.quadraticArmUpdate(14.0f));
                } else if ($$12 >= 14.0f && $$12 < 22.0f) {
                    float $$16 = ($$12 - 14.0f) / 8.0f;
                    this.leftArm.xRot = this.rotlerpRad($$15, this.leftArm.xRot, 1.5707964f * $$16);
                    this.rightArm.xRot = Mth.lerp($$14, this.rightArm.xRot, 1.5707964f * $$16);
                    this.leftArm.yRot = this.rotlerpRad($$15, this.leftArm.yRot, (float)Math.PI);
                    this.rightArm.yRot = Mth.lerp($$14, this.rightArm.yRot, (float)Math.PI);
                    this.leftArm.zRot = this.rotlerpRad($$15, this.leftArm.zRot, 5.012389f - 1.8707964f * $$16);
                    this.rightArm.zRot = Mth.lerp($$14, this.rightArm.zRot, 1.2707963f + 1.8707964f * $$16);
                } else if ($$12 >= 22.0f && $$12 < 26.0f) {
                    float $$17 = ($$12 - 22.0f) / 4.0f;
                    this.leftArm.xRot = this.rotlerpRad($$15, this.leftArm.xRot, 1.5707964f - 1.5707964f * $$17);
                    this.rightArm.xRot = Mth.lerp($$14, this.rightArm.xRot, 1.5707964f - 1.5707964f * $$17);
                    this.leftArm.yRot = this.rotlerpRad($$15, this.leftArm.yRot, (float)Math.PI);
                    this.rightArm.yRot = Mth.lerp($$14, this.rightArm.yRot, (float)Math.PI);
                    this.leftArm.zRot = this.rotlerpRad($$15, this.leftArm.zRot, (float)Math.PI);
                    this.rightArm.zRot = Mth.lerp($$14, this.rightArm.zRot, (float)Math.PI);
                }
            }
            float $$18 = 0.3f;
            float $$19 = 0.33333334f;
            this.leftLeg.xRot = Mth.lerp(this.swimAmount, this.leftLeg.xRot, 0.3f * Mth.cos($$1 * 0.33333334f + (float)Math.PI));
            this.rightLeg.xRot = Mth.lerp(this.swimAmount, this.rightLeg.xRot, 0.3f * Mth.cos($$1 * 0.33333334f));
        }
        this.hat.copyFrom(this.head);
    }

    private void poseRightArm(T $$0) {
        switch (this.rightArmPose) {
            case EMPTY: {
                this.rightArm.yRot = 0.0f;
                break;
            }
            case BLOCK: {
                this.rightArm.xRot = this.rightArm.xRot * 0.5f - 0.9424779f;
                this.rightArm.yRot = -0.5235988f;
                break;
            }
            case ITEM: {
                this.rightArm.xRot = this.rightArm.xRot * 0.5f - 0.31415927f;
                this.rightArm.yRot = 0.0f;
                break;
            }
            case THROW_SPEAR: {
                this.rightArm.xRot = this.rightArm.xRot * 0.5f - (float)Math.PI;
                this.rightArm.yRot = 0.0f;
                break;
            }
            case BOW_AND_ARROW: {
                this.rightArm.yRot = -0.1f + this.head.yRot;
                this.leftArm.yRot = 0.1f + this.head.yRot + 0.4f;
                this.rightArm.xRot = -1.5707964f + this.head.xRot;
                this.leftArm.xRot = -1.5707964f + this.head.xRot;
                break;
            }
            case CROSSBOW_CHARGE: {
                AnimationUtils.animateCrossbowCharge(this.rightArm, this.leftArm, $$0, true);
                break;
            }
            case CROSSBOW_HOLD: {
                AnimationUtils.animateCrossbowHold(this.rightArm, this.leftArm, this.head, true);
                break;
            }
            case SPYGLASS: {
                this.rightArm.xRot = Mth.clamp(this.head.xRot - 1.9198622f - (((Entity)$$0).isCrouching() ? 0.2617994f : 0.0f), -2.4f, 3.3f);
                this.rightArm.yRot = this.head.yRot - 0.2617994f;
                break;
            }
            case TOOT_HORN: {
                this.rightArm.xRot = Mth.clamp(this.head.xRot, -1.2f, 1.2f) - 1.4835298f;
                this.rightArm.yRot = this.head.yRot - 0.5235988f;
            }
        }
    }

    private void poseLeftArm(T $$0) {
        switch (this.leftArmPose) {
            case EMPTY: {
                this.leftArm.yRot = 0.0f;
                break;
            }
            case BLOCK: {
                this.leftArm.xRot = this.leftArm.xRot * 0.5f - 0.9424779f;
                this.leftArm.yRot = 0.5235988f;
                break;
            }
            case ITEM: {
                this.leftArm.xRot = this.leftArm.xRot * 0.5f - 0.31415927f;
                this.leftArm.yRot = 0.0f;
                break;
            }
            case THROW_SPEAR: {
                this.leftArm.xRot = this.leftArm.xRot * 0.5f - (float)Math.PI;
                this.leftArm.yRot = 0.0f;
                break;
            }
            case BOW_AND_ARROW: {
                this.rightArm.yRot = -0.1f + this.head.yRot - 0.4f;
                this.leftArm.yRot = 0.1f + this.head.yRot;
                this.rightArm.xRot = -1.5707964f + this.head.xRot;
                this.leftArm.xRot = -1.5707964f + this.head.xRot;
                break;
            }
            case CROSSBOW_CHARGE: {
                AnimationUtils.animateCrossbowCharge(this.rightArm, this.leftArm, $$0, false);
                break;
            }
            case CROSSBOW_HOLD: {
                AnimationUtils.animateCrossbowHold(this.rightArm, this.leftArm, this.head, false);
                break;
            }
            case SPYGLASS: {
                this.leftArm.xRot = Mth.clamp(this.head.xRot - 1.9198622f - (((Entity)$$0).isCrouching() ? 0.2617994f : 0.0f), -2.4f, 3.3f);
                this.leftArm.yRot = this.head.yRot + 0.2617994f;
                break;
            }
            case TOOT_HORN: {
                this.leftArm.xRot = Mth.clamp(this.head.xRot, -1.2f, 1.2f) - 1.4835298f;
                this.leftArm.yRot = this.head.yRot + 0.5235988f;
            }
        }
    }

    protected void setupAttackAnimation(T $$0, float $$1) {
        if (this.attackTime <= 0.0f) {
            return;
        }
        HumanoidArm $$2 = this.getAttackArm($$0);
        ModelPart $$3 = this.getArm($$2);
        float $$4 = this.attackTime;
        this.body.yRot = Mth.sin(Mth.sqrt($$4) * ((float)Math.PI * 2)) * 0.2f;
        if ($$2 == HumanoidArm.LEFT) {
            this.body.yRot *= -1.0f;
        }
        this.rightArm.z = Mth.sin(this.body.yRot) * 5.0f;
        this.rightArm.x = -Mth.cos(this.body.yRot) * 5.0f;
        this.leftArm.z = -Mth.sin(this.body.yRot) * 5.0f;
        this.leftArm.x = Mth.cos(this.body.yRot) * 5.0f;
        this.rightArm.yRot += this.body.yRot;
        this.leftArm.yRot += this.body.yRot;
        this.leftArm.xRot += this.body.yRot;
        $$4 = 1.0f - this.attackTime;
        $$4 *= $$4;
        $$4 *= $$4;
        $$4 = 1.0f - $$4;
        float $$5 = Mth.sin($$4 * (float)Math.PI);
        float $$6 = Mth.sin(this.attackTime * (float)Math.PI) * -(this.head.xRot - 0.7f) * 0.75f;
        $$3.xRot -= $$5 * 1.2f + $$6;
        $$3.yRot += this.body.yRot * 2.0f;
        $$3.zRot += Mth.sin(this.attackTime * (float)Math.PI) * -0.4f;
    }

    protected float rotlerpRad(float $$0, float $$1, float $$2) {
        float $$3 = ($$2 - $$1) % ((float)Math.PI * 2);
        if ($$3 < (float)(-Math.PI)) {
            $$3 += (float)Math.PI * 2;
        }
        if ($$3 >= (float)Math.PI) {
            $$3 -= (float)Math.PI * 2;
        }
        return $$1 + $$0 * $$3;
    }

    private float quadraticArmUpdate(float $$0) {
        return -65.0f * $$0 + $$0 * $$0;
    }

    @Override
    public void copyPropertiesTo(HumanoidModel<T> $$0) {
        super.copyPropertiesTo($$0);
        $$0.leftArmPose = this.leftArmPose;
        $$0.rightArmPose = this.rightArmPose;
        $$0.crouching = this.crouching;
        $$0.head.copyFrom(this.head);
        $$0.hat.copyFrom(this.hat);
        $$0.body.copyFrom(this.body);
        $$0.rightArm.copyFrom(this.rightArm);
        $$0.leftArm.copyFrom(this.leftArm);
        $$0.rightLeg.copyFrom(this.rightLeg);
        $$0.leftLeg.copyFrom(this.leftLeg);
    }

    public void setAllVisible(boolean $$0) {
        this.head.visible = $$0;
        this.hat.visible = $$0;
        this.body.visible = $$0;
        this.rightArm.visible = $$0;
        this.leftArm.visible = $$0;
        this.rightLeg.visible = $$0;
        this.leftLeg.visible = $$0;
    }

    @Override
    public void translateToHand(HumanoidArm $$0, PoseStack $$1) {
        this.getArm($$0).translateAndRotate($$1);
    }

    protected ModelPart getArm(HumanoidArm $$0) {
        if ($$0 == HumanoidArm.LEFT) {
            return this.leftArm;
        }
        return this.rightArm;
    }

    @Override
    public ModelPart getHead() {
        return this.head;
    }

    private HumanoidArm getAttackArm(T $$0) {
        HumanoidArm $$1 = ((LivingEntity)$$0).getMainArm();
        return ((LivingEntity)$$0).swingingArm == InteractionHand.MAIN_HAND ? $$1 : $$1.getOpposite();
    }

    public static enum ArmPose {
        EMPTY(false),
        ITEM(false),
        BLOCK(false),
        BOW_AND_ARROW(true),
        THROW_SPEAR(false),
        CROSSBOW_CHARGE(true),
        CROSSBOW_HOLD(true),
        SPYGLASS(false),
        TOOT_HORN(false);

        private final boolean twoHanded;

        private ArmPose(boolean $$0) {
            this.twoHanded = $$0;
        }

        public boolean isTwoHanded() {
            return this.twoHanded;
        }
    }
}