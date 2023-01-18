/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Optional
 *  java.util.function.Predicate
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 *  org.joml.Matrix4f
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Transformation;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.debug.BeeDebugRenderer;
import net.minecraft.client.renderer.debug.BrainDebugRenderer;
import net.minecraft.client.renderer.debug.ChunkBorderRenderer;
import net.minecraft.client.renderer.debug.ChunkDebugRenderer;
import net.minecraft.client.renderer.debug.CollisionBoxRenderer;
import net.minecraft.client.renderer.debug.GameEventListenerRenderer;
import net.minecraft.client.renderer.debug.GameTestDebugRenderer;
import net.minecraft.client.renderer.debug.GoalSelectorDebugRenderer;
import net.minecraft.client.renderer.debug.HeightMapRenderer;
import net.minecraft.client.renderer.debug.LightDebugRenderer;
import net.minecraft.client.renderer.debug.NeighborsUpdateRenderer;
import net.minecraft.client.renderer.debug.PathfindingRenderer;
import net.minecraft.client.renderer.debug.RaidDebugRenderer;
import net.minecraft.client.renderer.debug.SolidFaceRenderer;
import net.minecraft.client.renderer.debug.StructureRenderer;
import net.minecraft.client.renderer.debug.VillageSectionsDebugRenderer;
import net.minecraft.client.renderer.debug.WaterDebugRenderer;
import net.minecraft.client.renderer.debug.WorldGenAttemptRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionfc;

public class DebugRenderer {
    public final PathfindingRenderer pathfindingRenderer = new PathfindingRenderer();
    public final SimpleDebugRenderer waterDebugRenderer;
    public final SimpleDebugRenderer chunkBorderRenderer;
    public final SimpleDebugRenderer heightMapRenderer;
    public final SimpleDebugRenderer collisionBoxRenderer;
    public final SimpleDebugRenderer neighborsUpdateRenderer;
    public final StructureRenderer structureRenderer;
    public final SimpleDebugRenderer lightDebugRenderer;
    public final SimpleDebugRenderer worldGenAttemptRenderer;
    public final SimpleDebugRenderer solidFaceRenderer;
    public final SimpleDebugRenderer chunkRenderer;
    public final BrainDebugRenderer brainDebugRenderer;
    public final VillageSectionsDebugRenderer villageSectionsDebugRenderer;
    public final BeeDebugRenderer beeDebugRenderer;
    public final RaidDebugRenderer raidDebugRenderer;
    public final GoalSelectorDebugRenderer goalSelectorRenderer;
    public final GameTestDebugRenderer gameTestDebugRenderer;
    public final GameEventListenerRenderer gameEventListenerRenderer;
    private boolean renderChunkborder;

    public DebugRenderer(Minecraft $$0) {
        this.waterDebugRenderer = new WaterDebugRenderer($$0);
        this.chunkBorderRenderer = new ChunkBorderRenderer($$0);
        this.heightMapRenderer = new HeightMapRenderer($$0);
        this.collisionBoxRenderer = new CollisionBoxRenderer($$0);
        this.neighborsUpdateRenderer = new NeighborsUpdateRenderer($$0);
        this.structureRenderer = new StructureRenderer($$0);
        this.lightDebugRenderer = new LightDebugRenderer($$0);
        this.worldGenAttemptRenderer = new WorldGenAttemptRenderer();
        this.solidFaceRenderer = new SolidFaceRenderer($$0);
        this.chunkRenderer = new ChunkDebugRenderer($$0);
        this.brainDebugRenderer = new BrainDebugRenderer($$0);
        this.villageSectionsDebugRenderer = new VillageSectionsDebugRenderer();
        this.beeDebugRenderer = new BeeDebugRenderer($$0);
        this.raidDebugRenderer = new RaidDebugRenderer($$0);
        this.goalSelectorRenderer = new GoalSelectorDebugRenderer($$0);
        this.gameTestDebugRenderer = new GameTestDebugRenderer();
        this.gameEventListenerRenderer = new GameEventListenerRenderer($$0);
    }

    public void clear() {
        this.pathfindingRenderer.clear();
        this.waterDebugRenderer.clear();
        this.chunkBorderRenderer.clear();
        this.heightMapRenderer.clear();
        this.collisionBoxRenderer.clear();
        this.neighborsUpdateRenderer.clear();
        this.structureRenderer.clear();
        this.lightDebugRenderer.clear();
        this.worldGenAttemptRenderer.clear();
        this.solidFaceRenderer.clear();
        this.chunkRenderer.clear();
        this.brainDebugRenderer.clear();
        this.villageSectionsDebugRenderer.clear();
        this.beeDebugRenderer.clear();
        this.raidDebugRenderer.clear();
        this.goalSelectorRenderer.clear();
        this.gameTestDebugRenderer.clear();
        this.gameEventListenerRenderer.clear();
    }

    public boolean switchRenderChunkborder() {
        this.renderChunkborder = !this.renderChunkborder;
        return this.renderChunkborder;
    }

    public void render(PoseStack $$0, MultiBufferSource.BufferSource $$1, double $$2, double $$3, double $$4) {
        if (this.renderChunkborder && !Minecraft.getInstance().showOnlyReducedInfo()) {
            this.chunkBorderRenderer.render($$0, $$1, $$2, $$3, $$4);
        }
        this.gameTestDebugRenderer.render($$0, $$1, $$2, $$3, $$4);
    }

    public static Optional<Entity> getTargetedEntity(@Nullable Entity $$02, int $$1) {
        int $$6;
        Predicate $$7;
        AABB $$5;
        Vec3 $$3;
        Vec3 $$4;
        if ($$02 == null) {
            return Optional.empty();
        }
        Vec3 $$2 = $$02.getEyePosition();
        EntityHitResult $$8 = ProjectileUtil.getEntityHitResult($$02, $$2, $$4 = $$2.add($$3 = $$02.getViewVector(1.0f).scale($$1)), $$5 = $$02.getBoundingBox().expandTowards($$3).inflate(1.0), (Predicate<Entity>)($$7 = $$0 -> !$$0.isSpectator() && $$0.isPickable()), $$6 = $$1 * $$1);
        if ($$8 == null) {
            return Optional.empty();
        }
        if ($$2.distanceToSqr($$8.getLocation()) > (double)$$6) {
            return Optional.empty();
        }
        return Optional.of((Object)$$8.getEntity());
    }

    public static void renderFilledBox(BlockPos $$0, BlockPos $$1, float $$2, float $$3, float $$4, float $$5) {
        Camera $$6 = Minecraft.getInstance().gameRenderer.getMainCamera();
        if (!$$6.isInitialized()) {
            return;
        }
        Vec3 $$7 = $$6.getPosition().reverse();
        AABB $$8 = new AABB($$0, $$1).move($$7);
        DebugRenderer.renderFilledBox($$8, $$2, $$3, $$4, $$5);
    }

    public static void renderFilledBox(BlockPos $$0, float $$1, float $$2, float $$3, float $$4, float $$5) {
        Camera $$6 = Minecraft.getInstance().gameRenderer.getMainCamera();
        if (!$$6.isInitialized()) {
            return;
        }
        Vec3 $$7 = $$6.getPosition().reverse();
        AABB $$8 = new AABB($$0).move($$7).inflate($$1);
        DebugRenderer.renderFilledBox($$8, $$2, $$3, $$4, $$5);
    }

    public static void renderFilledBox(AABB $$0, float $$1, float $$2, float $$3, float $$4) {
        DebugRenderer.renderFilledBox($$0.minX, $$0.minY, $$0.minZ, $$0.maxX, $$0.maxY, $$0.maxZ, $$1, $$2, $$3, $$4);
    }

    public static void renderFilledBox(double $$0, double $$1, double $$2, double $$3, double $$4, double $$5, float $$6, float $$7, float $$8, float $$9) {
        Tesselator $$10 = Tesselator.getInstance();
        BufferBuilder $$11 = $$10.getBuilder();
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionColorShader));
        $$11.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
        LevelRenderer.addChainedFilledBoxVertices($$11, $$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$8, $$9);
        $$10.end();
    }

    public static void renderFloatingText(String $$0, int $$1, int $$2, int $$3, int $$4) {
        DebugRenderer.renderFloatingText($$0, (double)$$1 + 0.5, (double)$$2 + 0.5, (double)$$3 + 0.5, $$4);
    }

    public static void renderFloatingText(String $$0, double $$1, double $$2, double $$3, int $$4) {
        DebugRenderer.renderFloatingText($$0, $$1, $$2, $$3, $$4, 0.02f);
    }

    public static void renderFloatingText(String $$0, double $$1, double $$2, double $$3, int $$4, float $$5) {
        DebugRenderer.renderFloatingText($$0, $$1, $$2, $$3, $$4, $$5, true, 0.0f, false);
    }

    public static void renderFloatingText(String $$0, double $$1, double $$2, double $$3, int $$4, float $$5, boolean $$6, float $$7, boolean $$8) {
        Minecraft $$9 = Minecraft.getInstance();
        Camera $$10 = $$9.gameRenderer.getMainCamera();
        if (!$$10.isInitialized() || $$9.getEntityRenderDispatcher().options == null) {
            return;
        }
        Font $$11 = $$9.font;
        double $$12 = $$10.getPosition().x;
        double $$13 = $$10.getPosition().y;
        double $$14 = $$10.getPosition().z;
        PoseStack $$15 = RenderSystem.getModelViewStack();
        $$15.pushPose();
        $$15.translate((float)($$1 - $$12), (float)($$2 - $$13) + 0.07f, (float)($$3 - $$14));
        $$15.mulPoseMatrix(new Matrix4f().rotation((Quaternionfc)$$10.rotation()));
        $$15.scale($$5, -$$5, $$5);
        if ($$8) {
            RenderSystem.disableDepthTest();
        } else {
            RenderSystem.enableDepthTest();
        }
        RenderSystem.depthMask(true);
        $$15.scale(-1.0f, 1.0f, 1.0f);
        RenderSystem.applyModelViewMatrix();
        float $$16 = $$6 ? (float)(-$$11.width($$0)) / 2.0f : 0.0f;
        MultiBufferSource.BufferSource $$17 = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
        $$11.drawInBatch($$0, $$16 -= $$7 / $$5, 0.0f, $$4, false, Transformation.identity().getMatrix(), (MultiBufferSource)$$17, $$8, 0, 0xF000F0);
        $$17.endBatch();
        RenderSystem.enableDepthTest();
        $$15.popPose();
        RenderSystem.applyModelViewMatrix();
    }

    public static interface SimpleDebugRenderer {
        public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7);

        default public void clear() {
        }
    }
}