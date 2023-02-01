/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.logging.LogUtils
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.List
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.renderer.entity;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.logging.LogUtils;
import com.mojang.math.Axis;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.scores.Team;
import org.slf4j.Logger;

public abstract class LivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>>
extends EntityRenderer<T>
implements RenderLayerParent<T, M> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final float EYE_BED_OFFSET = 0.1f;
    protected M model;
    protected final List<RenderLayer<T, M>> layers = Lists.newArrayList();

    public LivingEntityRenderer(EntityRendererProvider.Context $$0, M $$1, float $$2) {
        super($$0);
        this.model = $$1;
        this.shadowRadius = $$2;
    }

    protected final boolean addLayer(RenderLayer<T, M> $$0) {
        return this.layers.add($$0);
    }

    @Override
    public M getModel() {
        return this.model;
    }

    @Override
    public void render(T $$0, float $$1, float $$2, PoseStack $$3, MultiBufferSource $$4, int $$5) {
        Direction $$12;
        $$3.pushPose();
        ((EntityModel)this.model).attackTime = this.getAttackAnim($$0, $$2);
        ((EntityModel)this.model).riding = ((Entity)$$0).isPassenger();
        ((EntityModel)this.model).young = ((LivingEntity)$$0).isBaby();
        float $$6 = Mth.rotLerp($$2, ((LivingEntity)$$0).yBodyRotO, ((LivingEntity)$$0).yBodyRot);
        float $$7 = Mth.rotLerp($$2, ((LivingEntity)$$0).yHeadRotO, ((LivingEntity)$$0).yHeadRot);
        float $$8 = $$7 - $$6;
        if (((Entity)$$0).isPassenger() && ((Entity)$$0).getVehicle() instanceof LivingEntity) {
            LivingEntity $$9 = (LivingEntity)((Entity)$$0).getVehicle();
            $$6 = Mth.rotLerp($$2, $$9.yBodyRotO, $$9.yBodyRot);
            $$8 = $$7 - $$6;
            float $$10 = Mth.wrapDegrees($$8);
            if ($$10 < -85.0f) {
                $$10 = -85.0f;
            }
            if ($$10 >= 85.0f) {
                $$10 = 85.0f;
            }
            $$6 = $$7 - $$10;
            if ($$10 * $$10 > 2500.0f) {
                $$6 += $$10 * 0.2f;
            }
            $$8 = $$7 - $$6;
        }
        float $$11 = Mth.lerp($$2, ((LivingEntity)$$0).xRotO, ((Entity)$$0).getXRot());
        if (LivingEntityRenderer.isEntityUpsideDown($$0)) {
            $$11 *= -1.0f;
            $$8 *= -1.0f;
        }
        if (((Entity)$$0).hasPose(Pose.SLEEPING) && ($$12 = ((LivingEntity)$$0).getBedOrientation()) != null) {
            float $$13 = ((Entity)$$0).getEyeHeight(Pose.STANDING) - 0.1f;
            $$3.translate((float)(-$$12.getStepX()) * $$13, 0.0f, (float)(-$$12.getStepZ()) * $$13);
        }
        float $$14 = this.getBob($$0, $$2);
        this.setupRotations($$0, $$3, $$14, $$6, $$2);
        $$3.scale(-1.0f, -1.0f, 1.0f);
        this.scale($$0, $$3, $$2);
        $$3.translate(0.0f, -1.501f, 0.0f);
        float $$15 = 0.0f;
        float $$16 = 0.0f;
        if (!((Entity)$$0).isPassenger() && ((LivingEntity)$$0).isAlive()) {
            $$15 = ((LivingEntity)$$0).walkAnimation.speed($$2);
            $$16 = ((LivingEntity)$$0).walkAnimation.position($$2);
            if (((LivingEntity)$$0).isBaby()) {
                $$16 *= 3.0f;
            }
            if ($$15 > 1.0f) {
                $$15 = 1.0f;
            }
        }
        ((EntityModel)this.model).prepareMobModel($$0, $$16, $$15, $$2);
        ((EntityModel)this.model).setupAnim($$0, $$16, $$15, $$14, $$8, $$11);
        Minecraft $$17 = Minecraft.getInstance();
        boolean $$18 = this.isBodyVisible($$0);
        boolean $$19 = !$$18 && !((Entity)$$0).isInvisibleTo($$17.player);
        boolean $$20 = $$17.shouldEntityAppearGlowing((Entity)$$0);
        RenderType $$21 = this.getRenderType($$0, $$18, $$19, $$20);
        if ($$21 != null) {
            VertexConsumer $$22 = $$4.getBuffer($$21);
            int $$23 = LivingEntityRenderer.getOverlayCoords($$0, this.getWhiteOverlayProgress($$0, $$2));
            ((Model)this.model).renderToBuffer($$3, $$22, $$5, $$23, 1.0f, 1.0f, 1.0f, $$19 ? 0.15f : 1.0f);
        }
        if (!((Entity)$$0).isSpectator()) {
            for (RenderLayer $$24 : this.layers) {
                $$24.render($$3, $$4, $$5, $$0, $$16, $$15, $$2, $$14, $$8, $$11);
            }
        }
        $$3.popPose();
        super.render($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Nullable
    protected RenderType getRenderType(T $$0, boolean $$1, boolean $$2, boolean $$3) {
        ResourceLocation $$4 = this.getTextureLocation($$0);
        if ($$2) {
            return RenderType.itemEntityTranslucentCull($$4);
        }
        if ($$1) {
            return ((Model)this.model).renderType($$4);
        }
        if ($$3) {
            return RenderType.outline($$4);
        }
        return null;
    }

    public static int getOverlayCoords(LivingEntity $$0, float $$1) {
        return OverlayTexture.pack(OverlayTexture.u($$1), OverlayTexture.v($$0.hurtTime > 0 || $$0.deathTime > 0));
    }

    protected boolean isBodyVisible(T $$0) {
        return !((Entity)$$0).isInvisible();
    }

    private static float sleepDirectionToRotation(Direction $$0) {
        switch ($$0) {
            case SOUTH: {
                return 90.0f;
            }
            case WEST: {
                return 0.0f;
            }
            case NORTH: {
                return 270.0f;
            }
            case EAST: {
                return 180.0f;
            }
        }
        return 0.0f;
    }

    protected boolean isShaking(T $$0) {
        return ((Entity)$$0).isFullyFrozen();
    }

    protected void setupRotations(T $$0, PoseStack $$1, float $$2, float $$3, float $$4) {
        if (this.isShaking($$0)) {
            $$3 += (float)(Math.cos((double)((double)((LivingEntity)$$0).tickCount * 3.25)) * Math.PI * (double)0.4f);
        }
        if (!((Entity)$$0).hasPose(Pose.SLEEPING)) {
            $$1.mulPose(Axis.YP.rotationDegrees(180.0f - $$3));
        }
        if (((LivingEntity)$$0).deathTime > 0) {
            float $$5 = ((float)((LivingEntity)$$0).deathTime + $$4 - 1.0f) / 20.0f * 1.6f;
            if (($$5 = Mth.sqrt($$5)) > 1.0f) {
                $$5 = 1.0f;
            }
            $$1.mulPose(Axis.ZP.rotationDegrees($$5 * this.getFlipDegrees($$0)));
        } else if (((LivingEntity)$$0).isAutoSpinAttack()) {
            $$1.mulPose(Axis.XP.rotationDegrees(-90.0f - ((Entity)$$0).getXRot()));
            $$1.mulPose(Axis.YP.rotationDegrees(((float)((LivingEntity)$$0).tickCount + $$4) * -75.0f));
        } else if (((Entity)$$0).hasPose(Pose.SLEEPING)) {
            Direction $$6 = ((LivingEntity)$$0).getBedOrientation();
            float $$7 = $$6 != null ? LivingEntityRenderer.sleepDirectionToRotation($$6) : $$3;
            $$1.mulPose(Axis.YP.rotationDegrees($$7));
            $$1.mulPose(Axis.ZP.rotationDegrees(this.getFlipDegrees($$0)));
            $$1.mulPose(Axis.YP.rotationDegrees(270.0f));
        } else if (LivingEntityRenderer.isEntityUpsideDown($$0)) {
            $$1.translate(0.0f, ((Entity)$$0).getBbHeight() + 0.1f, 0.0f);
            $$1.mulPose(Axis.ZP.rotationDegrees(180.0f));
        }
    }

    protected float getAttackAnim(T $$0, float $$1) {
        return ((LivingEntity)$$0).getAttackAnim($$1);
    }

    protected float getBob(T $$0, float $$1) {
        return (float)((LivingEntity)$$0).tickCount + $$1;
    }

    protected float getFlipDegrees(T $$0) {
        return 90.0f;
    }

    protected float getWhiteOverlayProgress(T $$0, float $$1) {
        return 0.0f;
    }

    protected void scale(T $$0, PoseStack $$1, float $$2) {
    }

    @Override
    protected boolean shouldShowName(T $$0) {
        boolean $$5;
        float $$2;
        double $$1 = this.entityRenderDispatcher.distanceToSqr((Entity)$$0);
        float f = $$2 = ((Entity)$$0).isDiscrete() ? 32.0f : 64.0f;
        if ($$1 >= (double)($$2 * $$2)) {
            return false;
        }
        Minecraft $$3 = Minecraft.getInstance();
        LocalPlayer $$4 = $$3.player;
        boolean bl = $$5 = !((Entity)$$0).isInvisibleTo($$4);
        if ($$0 != $$4) {
            Team $$6 = ((Entity)$$0).getTeam();
            Team $$7 = $$4.getTeam();
            if ($$6 != null) {
                Team.Visibility $$8 = $$6.getNameTagVisibility();
                switch ($$8) {
                    case ALWAYS: {
                        return $$5;
                    }
                    case NEVER: {
                        return false;
                    }
                    case HIDE_FOR_OTHER_TEAMS: {
                        return $$7 == null ? $$5 : $$6.isAlliedTo($$7) && ($$6.canSeeFriendlyInvisibles() || $$5);
                    }
                    case HIDE_FOR_OWN_TEAM: {
                        return $$7 == null ? $$5 : !$$6.isAlliedTo($$7) && $$5;
                    }
                }
                return true;
            }
        }
        return Minecraft.renderNames() && $$0 != $$3.getCameraEntity() && $$5 && !((Entity)$$0).isVehicle();
    }

    public static boolean isEntityUpsideDown(LivingEntity $$0) {
        String $$1;
        if (($$0 instanceof Player || $$0.hasCustomName()) && ("Dinnerbone".equals((Object)($$1 = ChatFormatting.stripFormatting($$0.getName().getString()))) || "Grumm".equals((Object)$$1))) {
            return !($$0 instanceof Player) || ((Player)$$0).isModelPartShown(PlayerModelPart.CAPE);
        }
        return false;
    }
}