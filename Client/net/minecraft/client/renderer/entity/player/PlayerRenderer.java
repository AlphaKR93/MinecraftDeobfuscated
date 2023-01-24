/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Objects
 */
package net.minecraft.client.renderer.entity.player;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Objects;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.entity.layers.BeeStingerLayer;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.Deadmau5EarsLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ParrotOnShoulderLayer;
import net.minecraft.client.renderer.entity.layers.PlayerItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.SpinAttackEffectLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;

public class PlayerRenderer
extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    public PlayerRenderer(EntityRendererProvider.Context $$0, boolean $$1) {
        super($$0, new PlayerModel($$0.bakeLayer($$1 ? ModelLayers.PLAYER_SLIM : ModelLayers.PLAYER), $$1), 0.5f);
        this.addLayer(new HumanoidArmorLayer(this, new HumanoidModel($$0.bakeLayer($$1 ? ModelLayers.PLAYER_SLIM_INNER_ARMOR : ModelLayers.PLAYER_INNER_ARMOR)), new HumanoidModel($$0.bakeLayer($$1 ? ModelLayers.PLAYER_SLIM_OUTER_ARMOR : ModelLayers.PLAYER_OUTER_ARMOR)), $$0.getModelManager()));
        this.addLayer(new PlayerItemInHandLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>(this, $$0.getItemInHandRenderer()));
        this.addLayer(new ArrowLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>($$0, this));
        this.addLayer(new Deadmau5EarsLayer(this));
        this.addLayer(new CapeLayer(this));
        this.addLayer(new CustomHeadLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>(this, $$0.getModelSet(), $$0.getItemInHandRenderer()));
        this.addLayer(new ElytraLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>(this, $$0.getModelSet()));
        this.addLayer(new ParrotOnShoulderLayer<AbstractClientPlayer>(this, $$0.getModelSet()));
        this.addLayer(new SpinAttackEffectLayer<AbstractClientPlayer>(this, $$0.getModelSet()));
        this.addLayer(new BeeStingerLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>(this));
    }

    @Override
    public void render(AbstractClientPlayer $$0, float $$1, float $$2, PoseStack $$3, MultiBufferSource $$4, int $$5) {
        this.setModelProperties($$0);
        super.render($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    public Vec3 getRenderOffset(AbstractClientPlayer $$0, float $$1) {
        if ($$0.isCrouching()) {
            return new Vec3(0.0, -0.125, 0.0);
        }
        return super.getRenderOffset($$0, $$1);
    }

    private void setModelProperties(AbstractClientPlayer $$0) {
        PlayerModel $$1 = (PlayerModel)this.getModel();
        if ($$0.isSpectator()) {
            $$1.setAllVisible(false);
            $$1.head.visible = true;
            $$1.hat.visible = true;
        } else {
            $$1.setAllVisible(true);
            $$1.hat.visible = $$0.isModelPartShown(PlayerModelPart.HAT);
            $$1.jacket.visible = $$0.isModelPartShown(PlayerModelPart.JACKET);
            $$1.leftPants.visible = $$0.isModelPartShown(PlayerModelPart.LEFT_PANTS_LEG);
            $$1.rightPants.visible = $$0.isModelPartShown(PlayerModelPart.RIGHT_PANTS_LEG);
            $$1.leftSleeve.visible = $$0.isModelPartShown(PlayerModelPart.LEFT_SLEEVE);
            $$1.rightSleeve.visible = $$0.isModelPartShown(PlayerModelPart.RIGHT_SLEEVE);
            $$1.crouching = $$0.isCrouching();
            HumanoidModel.ArmPose $$2 = PlayerRenderer.getArmPose($$0, InteractionHand.MAIN_HAND);
            HumanoidModel.ArmPose $$3 = PlayerRenderer.getArmPose($$0, InteractionHand.OFF_HAND);
            if ($$2.isTwoHanded()) {
                HumanoidModel.ArmPose armPose = $$3 = $$0.getOffhandItem().isEmpty() ? HumanoidModel.ArmPose.EMPTY : HumanoidModel.ArmPose.ITEM;
            }
            if ($$0.getMainArm() == HumanoidArm.RIGHT) {
                $$1.rightArmPose = $$2;
                $$1.leftArmPose = $$3;
            } else {
                $$1.rightArmPose = $$3;
                $$1.leftArmPose = $$2;
            }
        }
    }

    private static HumanoidModel.ArmPose getArmPose(AbstractClientPlayer $$0, InteractionHand $$1) {
        ItemStack $$2 = $$0.getItemInHand($$1);
        if ($$2.isEmpty()) {
            return HumanoidModel.ArmPose.EMPTY;
        }
        if ($$0.getUsedItemHand() == $$1 && $$0.getUseItemRemainingTicks() > 0) {
            UseAnim $$3 = $$2.getUseAnimation();
            if ($$3 == UseAnim.BLOCK) {
                return HumanoidModel.ArmPose.BLOCK;
            }
            if ($$3 == UseAnim.BOW) {
                return HumanoidModel.ArmPose.BOW_AND_ARROW;
            }
            if ($$3 == UseAnim.SPEAR) {
                return HumanoidModel.ArmPose.THROW_SPEAR;
            }
            if ($$3 == UseAnim.CROSSBOW && $$1 == $$0.getUsedItemHand()) {
                return HumanoidModel.ArmPose.CROSSBOW_CHARGE;
            }
            if ($$3 == UseAnim.SPYGLASS) {
                return HumanoidModel.ArmPose.SPYGLASS;
            }
            if ($$3 == UseAnim.TOOT_HORN) {
                return HumanoidModel.ArmPose.TOOT_HORN;
            }
        } else if (!$$0.swinging && $$2.is(Items.CROSSBOW) && CrossbowItem.isCharged($$2)) {
            return HumanoidModel.ArmPose.CROSSBOW_HOLD;
        }
        return HumanoidModel.ArmPose.ITEM;
    }

    @Override
    public ResourceLocation getTextureLocation(AbstractClientPlayer $$0) {
        return $$0.getSkinTextureLocation();
    }

    @Override
    protected void scale(AbstractClientPlayer $$0, PoseStack $$1, float $$2) {
        float $$3 = 0.9375f;
        $$1.scale(0.9375f, 0.9375f, 0.9375f);
    }

    @Override
    protected void renderNameTag(AbstractClientPlayer $$0, Component $$1, PoseStack $$2, MultiBufferSource $$3, int $$4) {
        Scoreboard $$6;
        Objective $$7;
        double $$5 = this.entityRenderDispatcher.distanceToSqr($$0);
        $$2.pushPose();
        if ($$5 < 100.0 && ($$7 = ($$6 = $$0.getScoreboard()).getDisplayObjective(2)) != null) {
            Score $$8 = $$6.getOrCreatePlayerScore($$0.getScoreboardName(), $$7);
            super.renderNameTag($$0, Component.literal(Integer.toString((int)$$8.getScore())).append(CommonComponents.SPACE).append($$7.getDisplayName()), $$2, $$3, $$4);
            Objects.requireNonNull((Object)this.getFont());
            $$2.translate(0.0f, 9.0f * 1.15f * 0.025f, 0.0f);
        }
        super.renderNameTag($$0, $$1, $$2, $$3, $$4);
        $$2.popPose();
    }

    public void renderRightHand(PoseStack $$0, MultiBufferSource $$1, int $$2, AbstractClientPlayer $$3) {
        this.renderHand($$0, $$1, $$2, $$3, ((PlayerModel)this.model).rightArm, ((PlayerModel)this.model).rightSleeve);
    }

    public void renderLeftHand(PoseStack $$0, MultiBufferSource $$1, int $$2, AbstractClientPlayer $$3) {
        this.renderHand($$0, $$1, $$2, $$3, ((PlayerModel)this.model).leftArm, ((PlayerModel)this.model).leftSleeve);
    }

    private void renderHand(PoseStack $$0, MultiBufferSource $$1, int $$2, AbstractClientPlayer $$3, ModelPart $$4, ModelPart $$5) {
        PlayerModel $$6 = (PlayerModel)this.getModel();
        this.setModelProperties($$3);
        $$6.attackTime = 0.0f;
        $$6.crouching = false;
        $$6.swimAmount = 0.0f;
        $$6.setupAnim($$3, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        $$4.xRot = 0.0f;
        $$4.render($$0, $$1.getBuffer(RenderType.entitySolid($$3.getSkinTextureLocation())), $$2, OverlayTexture.NO_OVERLAY);
        $$5.xRot = 0.0f;
        $$5.render($$0, $$1.getBuffer(RenderType.entityTranslucent($$3.getSkinTextureLocation())), $$2, OverlayTexture.NO_OVERLAY);
    }

    @Override
    protected void setupRotations(AbstractClientPlayer $$0, PoseStack $$1, float $$2, float $$3, float $$4) {
        float $$5 = $$0.getSwimAmount($$4);
        if ($$0.isFallFlying()) {
            super.setupRotations($$0, $$1, $$2, $$3, $$4);
            float $$6 = (float)$$0.getFallFlyingTicks() + $$4;
            float $$7 = Mth.clamp($$6 * $$6 / 100.0f, 0.0f, 1.0f);
            if (!$$0.isAutoSpinAttack()) {
                $$1.mulPose(Axis.XP.rotationDegrees($$7 * (-90.0f - $$0.getXRot())));
            }
            Vec3 $$8 = $$0.getViewVector($$4);
            Vec3 $$9 = $$0.getDeltaMovement();
            double $$10 = $$9.horizontalDistanceSqr();
            double $$11 = $$8.horizontalDistanceSqr();
            if ($$10 > 0.0 && $$11 > 0.0) {
                double $$12 = ($$9.x * $$8.x + $$9.z * $$8.z) / Math.sqrt((double)($$10 * $$11));
                double $$13 = $$9.x * $$8.z - $$9.z * $$8.x;
                $$1.mulPose(Axis.YP.rotation((float)(Math.signum((double)$$13) * Math.acos((double)$$12))));
            }
        } else if ($$5 > 0.0f) {
            super.setupRotations($$0, $$1, $$2, $$3, $$4);
            float $$14 = $$0.isInWater() ? -90.0f - $$0.getXRot() : -90.0f;
            float $$15 = Mth.lerp($$5, 0.0f, $$14);
            $$1.mulPose(Axis.XP.rotationDegrees($$15));
            if ($$0.isVisuallySwimming()) {
                $$1.translate(0.0f, -1.0f, 0.3f);
            }
        } else {
            super.setupRotations($$0, $$1, $$2, $$3, $$4);
        }
    }
}