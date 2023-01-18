/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Runnable
 *  java.lang.Throwable
 *  java.util.Map
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 */
package net.minecraft.client.renderer.blockentity;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Camera;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.HitResult;

public class BlockEntityRenderDispatcher
implements ResourceManagerReloadListener {
    private Map<BlockEntityType<?>, BlockEntityRenderer<?>> renderers = ImmutableMap.of();
    private final Font font;
    private final EntityModelSet entityModelSet;
    public Level level;
    public Camera camera;
    public HitResult cameraHitResult;
    private final Supplier<BlockRenderDispatcher> blockRenderDispatcher;
    private final Supplier<ItemRenderer> itemRenderer;
    private final Supplier<EntityRenderDispatcher> entityRenderer;

    public BlockEntityRenderDispatcher(Font $$0, EntityModelSet $$1, Supplier<BlockRenderDispatcher> $$2, Supplier<ItemRenderer> $$3, Supplier<EntityRenderDispatcher> $$4) {
        this.itemRenderer = $$3;
        this.entityRenderer = $$4;
        this.font = $$0;
        this.entityModelSet = $$1;
        this.blockRenderDispatcher = $$2;
    }

    @Nullable
    public <E extends BlockEntity> BlockEntityRenderer<E> getRenderer(E $$0) {
        return (BlockEntityRenderer)this.renderers.get($$0.getType());
    }

    public void prepare(Level $$0, Camera $$1, HitResult $$2) {
        if (this.level != $$0) {
            this.setLevel($$0);
        }
        this.camera = $$1;
        this.cameraHitResult = $$2;
    }

    public <E extends BlockEntity> void render(E $$0, float $$1, PoseStack $$2, MultiBufferSource $$3) {
        BlockEntityRenderer $$4 = this.getRenderer($$0);
        if ($$4 == null) {
            return;
        }
        if (!$$0.hasLevel() || !$$0.getType().isValid($$0.getBlockState())) {
            return;
        }
        if (!$$4.shouldRender($$0, this.camera.getPosition())) {
            return;
        }
        BlockEntityRenderDispatcher.tryRender($$0, () -> BlockEntityRenderDispatcher.setupAndRender($$4, $$0, $$1, $$2, $$3));
    }

    private static <T extends BlockEntity> void setupAndRender(BlockEntityRenderer<T> $$0, T $$1, float $$2, PoseStack $$3, MultiBufferSource $$4) {
        int $$7;
        Level $$5 = $$1.getLevel();
        if ($$5 != null) {
            int $$6 = LevelRenderer.getLightColor($$5, $$1.getBlockPos());
        } else {
            $$7 = 0xF000F0;
        }
        $$0.render($$1, $$2, $$3, $$4, $$7, OverlayTexture.NO_OVERLAY);
    }

    public <E extends BlockEntity> boolean renderItem(E $$0, PoseStack $$1, MultiBufferSource $$2, int $$3, int $$4) {
        BlockEntityRenderer $$5 = this.getRenderer($$0);
        if ($$5 == null) {
            return true;
        }
        BlockEntityRenderDispatcher.tryRender($$0, () -> $$5.render($$0, 0.0f, $$1, $$2, $$3, $$4));
        return false;
    }

    private static void tryRender(BlockEntity $$0, Runnable $$1) {
        try {
            $$1.run();
        }
        catch (Throwable $$2) {
            CrashReport $$3 = CrashReport.forThrowable($$2, "Rendering Block Entity");
            CrashReportCategory $$4 = $$3.addCategory("Block Entity Details");
            $$0.fillCrashReportCategory($$4);
            throw new ReportedException($$3);
        }
    }

    public void setLevel(@Nullable Level $$0) {
        this.level = $$0;
        if ($$0 == null) {
            this.camera = null;
        }
    }

    @Override
    public void onResourceManagerReload(ResourceManager $$0) {
        BlockEntityRendererProvider.Context $$1 = new BlockEntityRendererProvider.Context(this, (BlockRenderDispatcher)this.blockRenderDispatcher.get(), (ItemRenderer)this.itemRenderer.get(), (EntityRenderDispatcher)this.entityRenderer.get(), this.entityModelSet, this.font);
        this.renderers = BlockEntityRenderers.createEntityRenderers($$1);
    }
}