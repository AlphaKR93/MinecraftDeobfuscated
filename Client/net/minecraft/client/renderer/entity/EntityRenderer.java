/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  org.joml.Matrix4f
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public abstract class EntityRenderer<T extends Entity> {
    protected static final float NAMETAG_SCALE = 0.025f;
    protected final EntityRenderDispatcher entityRenderDispatcher;
    private final Font font;
    protected float shadowRadius;
    protected float shadowStrength = 1.0f;

    protected EntityRenderer(EntityRendererProvider.Context $$0) {
        this.entityRenderDispatcher = $$0.getEntityRenderDispatcher();
        this.font = $$0.getFont();
    }

    public final int getPackedLightCoords(T $$0, float $$1) {
        BlockPos $$2 = new BlockPos(((Entity)$$0).getLightProbePosition($$1));
        return LightTexture.pack(this.getBlockLightLevel($$0, $$2), this.getSkyLightLevel($$0, $$2));
    }

    protected int getSkyLightLevel(T $$0, BlockPos $$1) {
        return ((Entity)$$0).level.getBrightness(LightLayer.SKY, $$1);
    }

    protected int getBlockLightLevel(T $$0, BlockPos $$1) {
        if (((Entity)$$0).isOnFire()) {
            return 15;
        }
        return ((Entity)$$0).level.getBrightness(LightLayer.BLOCK, $$1);
    }

    public boolean shouldRender(T $$0, Frustum $$1, double $$2, double $$3, double $$4) {
        if (!((Entity)$$0).shouldRender($$2, $$3, $$4)) {
            return false;
        }
        if (((Entity)$$0).noCulling) {
            return true;
        }
        AABB $$5 = ((Entity)$$0).getBoundingBoxForCulling().inflate(0.5);
        if ($$5.hasNaN() || $$5.getSize() == 0.0) {
            $$5 = new AABB(((Entity)$$0).getX() - 2.0, ((Entity)$$0).getY() - 2.0, ((Entity)$$0).getZ() - 2.0, ((Entity)$$0).getX() + 2.0, ((Entity)$$0).getY() + 2.0, ((Entity)$$0).getZ() + 2.0);
        }
        return $$1.isVisible($$5);
    }

    public Vec3 getRenderOffset(T $$0, float $$1) {
        return Vec3.ZERO;
    }

    public void render(T $$0, float $$1, float $$2, PoseStack $$3, MultiBufferSource $$4, int $$5) {
        if (!this.shouldShowName($$0)) {
            return;
        }
        this.renderNameTag($$0, ((Entity)$$0).getDisplayName(), $$3, $$4, $$5);
    }

    protected boolean shouldShowName(T $$0) {
        return ((Entity)$$0).shouldShowName() && ((Entity)$$0).hasCustomName();
    }

    public abstract ResourceLocation getTextureLocation(T var1);

    public Font getFont() {
        return this.font;
    }

    protected void renderNameTag(T $$0, Component $$1, PoseStack $$2, MultiBufferSource $$3, int $$4) {
        double $$5 = this.entityRenderDispatcher.distanceToSqr((Entity)$$0);
        if ($$5 > 4096.0) {
            return;
        }
        boolean $$6 = !((Entity)$$0).isDiscrete();
        float $$7 = ((Entity)$$0).getBbHeight() + 0.5f;
        int $$8 = "deadmau5".equals((Object)$$1.getString()) ? -10 : 0;
        $$2.pushPose();
        $$2.translate(0.0f, $$7, 0.0f);
        $$2.mulPose(this.entityRenderDispatcher.cameraOrientation());
        $$2.scale(-0.025f, -0.025f, 0.025f);
        Matrix4f $$9 = $$2.last().pose();
        float $$10 = Minecraft.getInstance().options.getBackgroundOpacity(0.25f);
        int $$11 = (int)($$10 * 255.0f) << 24;
        Font $$12 = this.getFont();
        float $$13 = -$$12.width($$1) / 2;
        $$12.drawInBatch($$1, $$13, (float)$$8, 0x20FFFFFF, false, $$9, $$3, $$6, $$11, $$4);
        if ($$6) {
            $$12.drawInBatch($$1, $$13, (float)$$8, -1, false, $$9, $$3, false, 0, $$4);
        }
        $$2.popPose();
    }
}