/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  java.lang.Float
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.Throwable
 *  java.util.Map
 *  javax.annotation.Nullable
 *  org.joml.Matrix3f
 *  org.joml.Matrix4f
 *  org.joml.Quaternionf
 */
package net.minecraft.client.renderer.entity;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class EntityRenderDispatcher
implements ResourceManagerReloadListener {
    private static final RenderType SHADOW_RENDER_TYPE = RenderType.entityShadow(new ResourceLocation("textures/misc/shadow.png"));
    private Map<EntityType<?>, EntityRenderer<?>> renderers = ImmutableMap.of();
    private Map<String, EntityRenderer<? extends Player>> playerRenderers = ImmutableMap.of();
    public final TextureManager textureManager;
    private Level level;
    public Camera camera;
    private Quaternionf cameraOrientation;
    public Entity crosshairPickEntity;
    private final ItemRenderer itemRenderer;
    private final BlockRenderDispatcher blockRenderDispatcher;
    private final ItemInHandRenderer itemInHandRenderer;
    private final Font font;
    public final Options options;
    private final EntityModelSet entityModels;
    private boolean shouldRenderShadow = true;
    private boolean renderHitBoxes;

    public <E extends Entity> int getPackedLightCoords(E $$0, float $$1) {
        return this.getRenderer($$0).getPackedLightCoords($$0, $$1);
    }

    public EntityRenderDispatcher(Minecraft $$0, TextureManager $$1, ItemRenderer $$2, BlockRenderDispatcher $$3, Font $$4, Options $$5, EntityModelSet $$6) {
        this.textureManager = $$1;
        this.itemRenderer = $$2;
        this.itemInHandRenderer = new ItemInHandRenderer($$0, this, $$2);
        this.blockRenderDispatcher = $$3;
        this.font = $$4;
        this.options = $$5;
        this.entityModels = $$6;
    }

    public <T extends Entity> EntityRenderer<? super T> getRenderer(T $$0) {
        if ($$0 instanceof AbstractClientPlayer) {
            String $$1 = ((AbstractClientPlayer)$$0).getModelName();
            EntityRenderer $$2 = (EntityRenderer)this.playerRenderers.get((Object)$$1);
            if ($$2 != null) {
                return $$2;
            }
            return (EntityRenderer)this.playerRenderers.get((Object)"default");
        }
        return (EntityRenderer)this.renderers.get($$0.getType());
    }

    public void prepare(Level $$0, Camera $$1, Entity $$2) {
        this.level = $$0;
        this.camera = $$1;
        this.cameraOrientation = $$1.rotation();
        this.crosshairPickEntity = $$2;
    }

    public void overrideCameraOrientation(Quaternionf $$0) {
        this.cameraOrientation = $$0;
    }

    public void setRenderShadow(boolean $$0) {
        this.shouldRenderShadow = $$0;
    }

    public void setRenderHitBoxes(boolean $$0) {
        this.renderHitBoxes = $$0;
    }

    public boolean shouldRenderHitBoxes() {
        return this.renderHitBoxes;
    }

    public <E extends Entity> boolean shouldRender(E $$0, Frustum $$1, double $$2, double $$3, double $$4) {
        EntityRenderer<E> $$5 = this.getRenderer($$0);
        return $$5.shouldRender($$0, $$1, $$2, $$3, $$4);
    }

    public <E extends Entity> void render(E $$0, double $$1, double $$2, double $$3, float $$4, float $$5, PoseStack $$6, MultiBufferSource $$7, int $$8) {
        EntityRenderer<E> $$9 = this.getRenderer($$0);
        try {
            double $$14;
            float $$15;
            Vec3 $$10 = $$9.getRenderOffset($$0, $$5);
            double $$11 = $$1 + $$10.x();
            double $$12 = $$2 + $$10.y();
            double $$13 = $$3 + $$10.z();
            $$6.pushPose();
            $$6.translate($$11, $$12, $$13);
            $$9.render($$0, $$4, $$5, $$6, $$7, $$8);
            if ($$0.displayFireAnimation()) {
                this.renderFlame($$6, $$7, $$0);
            }
            $$6.translate(-$$10.x(), -$$10.y(), -$$10.z());
            if (this.options.entityShadows().get().booleanValue() && this.shouldRenderShadow && $$9.shadowRadius > 0.0f && !$$0.isInvisible() && ($$15 = (float)((1.0 - ($$14 = this.distanceToSqr($$0.getX(), $$0.getY(), $$0.getZ())) / 256.0) * (double)$$9.shadowStrength)) > 0.0f) {
                EntityRenderDispatcher.renderShadow($$6, $$7, $$0, $$15, $$5, this.level, $$9.shadowRadius);
            }
            if (this.renderHitBoxes && !$$0.isInvisible() && !Minecraft.getInstance().showOnlyReducedInfo()) {
                EntityRenderDispatcher.renderHitbox($$6, $$7.getBuffer(RenderType.lines()), $$0, $$5);
            }
            $$6.popPose();
        }
        catch (Throwable $$16) {
            CrashReport $$17 = CrashReport.forThrowable($$16, "Rendering entity in world");
            CrashReportCategory $$18 = $$17.addCategory("Entity being rendered");
            $$0.fillCrashReportCategory($$18);
            CrashReportCategory $$19 = $$17.addCategory("Renderer details");
            $$19.setDetail("Assigned renderer", $$9);
            $$19.setDetail("Location", CrashReportCategory.formatLocation((LevelHeightAccessor)this.level, $$1, $$2, $$3));
            $$19.setDetail("Rotation", Float.valueOf((float)$$4));
            $$19.setDetail("Delta", Float.valueOf((float)$$5));
            throw new ReportedException($$17);
        }
    }

    private static void renderHitbox(PoseStack $$0, VertexConsumer $$1, Entity $$2, float $$3) {
        AABB $$4 = $$2.getBoundingBox().move(-$$2.getX(), -$$2.getY(), -$$2.getZ());
        LevelRenderer.renderLineBox($$0, $$1, $$4, 1.0f, 1.0f, 1.0f, 1.0f);
        if ($$2 instanceof EnderDragon) {
            double $$5 = -Mth.lerp((double)$$3, $$2.xOld, $$2.getX());
            double $$6 = -Mth.lerp((double)$$3, $$2.yOld, $$2.getY());
            double $$7 = -Mth.lerp((double)$$3, $$2.zOld, $$2.getZ());
            for (EnderDragonPart $$8 : ((EnderDragon)$$2).getSubEntities()) {
                $$0.pushPose();
                double $$9 = $$5 + Mth.lerp((double)$$3, $$8.xOld, $$8.getX());
                double $$10 = $$6 + Mth.lerp((double)$$3, $$8.yOld, $$8.getY());
                double $$11 = $$7 + Mth.lerp((double)$$3, $$8.zOld, $$8.getZ());
                $$0.translate($$9, $$10, $$11);
                LevelRenderer.renderLineBox($$0, $$1, $$8.getBoundingBox().move(-$$8.getX(), -$$8.getY(), -$$8.getZ()), 0.25f, 1.0f, 0.0f, 1.0f);
                $$0.popPose();
            }
        }
        if ($$2 instanceof LivingEntity) {
            float $$12 = 0.01f;
            LevelRenderer.renderLineBox($$0, $$1, $$4.minX, $$2.getEyeHeight() - 0.01f, $$4.minZ, $$4.maxX, $$2.getEyeHeight() + 0.01f, $$4.maxZ, 1.0f, 0.0f, 0.0f, 1.0f);
        }
        Vec3 $$13 = $$2.getViewVector($$3);
        Matrix4f $$14 = $$0.last().pose();
        Matrix3f $$15 = $$0.last().normal();
        $$1.vertex($$14, 0.0f, $$2.getEyeHeight(), 0.0f).color(0, 0, 255, 255).normal($$15, (float)$$13.x, (float)$$13.y, (float)$$13.z).endVertex();
        $$1.vertex($$14, (float)($$13.x * 2.0), (float)((double)$$2.getEyeHeight() + $$13.y * 2.0), (float)($$13.z * 2.0)).color(0, 0, 255, 255).normal($$15, (float)$$13.x, (float)$$13.y, (float)$$13.z).endVertex();
    }

    private void renderFlame(PoseStack $$0, MultiBufferSource $$1, Entity $$2) {
        TextureAtlasSprite $$3 = ModelBakery.FIRE_0.sprite();
        TextureAtlasSprite $$4 = ModelBakery.FIRE_1.sprite();
        $$0.pushPose();
        float $$5 = $$2.getBbWidth() * 1.4f;
        $$0.scale($$5, $$5, $$5);
        float $$6 = 0.5f;
        float $$7 = 0.0f;
        float $$8 = $$2.getBbHeight() / $$5;
        float $$9 = 0.0f;
        $$0.mulPose(Axis.YP.rotationDegrees(-this.camera.getYRot()));
        $$0.translate(0.0f, 0.0f, -0.3f + (float)((int)$$8) * 0.02f);
        float $$10 = 0.0f;
        int $$11 = 0;
        VertexConsumer $$12 = $$1.getBuffer(Sheets.cutoutBlockSheet());
        PoseStack.Pose $$13 = $$0.last();
        while ($$8 > 0.0f) {
            TextureAtlasSprite $$14 = $$11 % 2 == 0 ? $$3 : $$4;
            float $$15 = $$14.getU0();
            float $$16 = $$14.getV0();
            float $$17 = $$14.getU1();
            float $$18 = $$14.getV1();
            if ($$11 / 2 % 2 == 0) {
                float $$19 = $$17;
                $$17 = $$15;
                $$15 = $$19;
            }
            EntityRenderDispatcher.fireVertex($$13, $$12, $$6 - 0.0f, 0.0f - $$9, $$10, $$17, $$18);
            EntityRenderDispatcher.fireVertex($$13, $$12, -$$6 - 0.0f, 0.0f - $$9, $$10, $$15, $$18);
            EntityRenderDispatcher.fireVertex($$13, $$12, -$$6 - 0.0f, 1.4f - $$9, $$10, $$15, $$16);
            EntityRenderDispatcher.fireVertex($$13, $$12, $$6 - 0.0f, 1.4f - $$9, $$10, $$17, $$16);
            $$8 -= 0.45f;
            $$9 -= 0.45f;
            $$6 *= 0.9f;
            $$10 += 0.03f;
            ++$$11;
        }
        $$0.popPose();
    }

    private static void fireVertex(PoseStack.Pose $$0, VertexConsumer $$1, float $$2, float $$3, float $$4, float $$5, float $$6) {
        $$1.vertex($$0.pose(), $$2, $$3, $$4).color(255, 255, 255, 255).uv($$5, $$6).overlayCoords(0, 10).uv2(240).normal($$0.normal(), 0.0f, 1.0f, 0.0f).endVertex();
    }

    private static void renderShadow(PoseStack $$0, MultiBufferSource $$1, Entity $$2, float $$3, float $$4, LevelReader $$5, float $$6) {
        Mob $$8;
        float $$7 = $$6;
        if ($$2 instanceof Mob && ($$8 = (Mob)$$2).isBaby()) {
            $$7 *= 0.5f;
        }
        double $$9 = Mth.lerp((double)$$4, $$2.xOld, $$2.getX());
        double $$10 = Mth.lerp((double)$$4, $$2.yOld, $$2.getY());
        double $$11 = Mth.lerp((double)$$4, $$2.zOld, $$2.getZ());
        int $$12 = Mth.floor($$9 - (double)$$7);
        int $$13 = Mth.floor($$9 + (double)$$7);
        int $$14 = Mth.floor($$10 - (double)$$7);
        int $$15 = Mth.floor($$10);
        int $$16 = Mth.floor($$11 - (double)$$7);
        int $$17 = Mth.floor($$11 + (double)$$7);
        PoseStack.Pose $$18 = $$0.last();
        VertexConsumer $$19 = $$1.getBuffer(SHADOW_RENDER_TYPE);
        for (BlockPos $$20 : BlockPos.betweenClosed(new BlockPos($$12, $$14, $$16), new BlockPos($$13, $$15, $$17))) {
            EntityRenderDispatcher.renderBlockShadow($$18, $$19, $$5, $$20, $$9, $$10, $$11, $$7, $$3);
        }
    }

    private static void renderBlockShadow(PoseStack.Pose $$0, VertexConsumer $$1, LevelReader $$2, BlockPos $$3, double $$4, double $$5, double $$6, float $$7, float $$8) {
        Vec3i $$9 = $$3.below();
        BlockState $$10 = $$2.getBlockState((BlockPos)$$9);
        if ($$10.getRenderShape() == RenderShape.INVISIBLE || $$2.getMaxLocalRawBrightness($$3) <= 3) {
            return;
        }
        if (!$$10.isCollisionShapeFullBlock($$2, (BlockPos)$$9)) {
            return;
        }
        VoxelShape $$11 = $$10.getShape($$2, (BlockPos)$$3.below());
        if ($$11.isEmpty()) {
            return;
        }
        float $$12 = LightTexture.getBrightness($$2.dimensionType(), $$2.getMaxLocalRawBrightness($$3));
        float $$13 = (float)(((double)$$8 - ($$5 - (double)$$3.getY()) / 2.0) * 0.5 * (double)$$12);
        if ($$13 >= 0.0f) {
            if ($$13 > 1.0f) {
                $$13 = 1.0f;
            }
            AABB $$14 = $$11.bounds();
            double $$15 = (double)$$3.getX() + $$14.minX;
            double $$16 = (double)$$3.getX() + $$14.maxX;
            double $$17 = (double)$$3.getY() + $$14.minY;
            double $$18 = (double)$$3.getZ() + $$14.minZ;
            double $$19 = (double)$$3.getZ() + $$14.maxZ;
            float $$20 = (float)($$15 - $$4);
            float $$21 = (float)($$16 - $$4);
            float $$22 = (float)($$17 - $$5);
            float $$23 = (float)($$18 - $$6);
            float $$24 = (float)($$19 - $$6);
            float $$25 = -$$20 / 2.0f / $$7 + 0.5f;
            float $$26 = -$$21 / 2.0f / $$7 + 0.5f;
            float $$27 = -$$23 / 2.0f / $$7 + 0.5f;
            float $$28 = -$$24 / 2.0f / $$7 + 0.5f;
            EntityRenderDispatcher.shadowVertex($$0, $$1, $$13, $$20, $$22, $$23, $$25, $$27);
            EntityRenderDispatcher.shadowVertex($$0, $$1, $$13, $$20, $$22, $$24, $$25, $$28);
            EntityRenderDispatcher.shadowVertex($$0, $$1, $$13, $$21, $$22, $$24, $$26, $$28);
            EntityRenderDispatcher.shadowVertex($$0, $$1, $$13, $$21, $$22, $$23, $$26, $$27);
        }
    }

    private static void shadowVertex(PoseStack.Pose $$0, VertexConsumer $$1, float $$2, float $$3, float $$4, float $$5, float $$6, float $$7) {
        $$1.vertex($$0.pose(), $$3, $$4, $$5).color(1.0f, 1.0f, 1.0f, $$2).uv($$6, $$7).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal($$0.normal(), 0.0f, 1.0f, 0.0f).endVertex();
    }

    public void setLevel(@Nullable Level $$0) {
        this.level = $$0;
        if ($$0 == null) {
            this.camera = null;
        }
    }

    public double distanceToSqr(Entity $$0) {
        return this.camera.getPosition().distanceToSqr($$0.position());
    }

    public double distanceToSqr(double $$0, double $$1, double $$2) {
        return this.camera.getPosition().distanceToSqr($$0, $$1, $$2);
    }

    public Quaternionf cameraOrientation() {
        return this.cameraOrientation;
    }

    public ItemInHandRenderer getItemInHandRenderer() {
        return this.itemInHandRenderer;
    }

    @Override
    public void onResourceManagerReload(ResourceManager $$0) {
        EntityRendererProvider.Context $$1 = new EntityRendererProvider.Context(this, this.itemRenderer, this.blockRenderDispatcher, this.itemInHandRenderer, $$0, this.entityModels, this.font);
        this.renderers = EntityRenderers.createEntityRenderers($$1);
        this.playerRenderers = EntityRenderers.createPlayerRenderers($$1);
    }
}