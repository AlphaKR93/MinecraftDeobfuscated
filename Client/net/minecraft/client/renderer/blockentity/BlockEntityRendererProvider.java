/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.FunctionalInterface
 *  java.lang.Object
 */
package net.minecraft.client.renderer.blockentity;

import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;

@FunctionalInterface
public interface BlockEntityRendererProvider<T extends BlockEntity> {
    public BlockEntityRenderer<T> create(Context var1);

    public static class Context {
        private final BlockEntityRenderDispatcher blockEntityRenderDispatcher;
        private final BlockRenderDispatcher blockRenderDispatcher;
        private final ItemRenderer itemRenderer;
        private final EntityRenderDispatcher entityRenderer;
        private final EntityModelSet modelSet;
        private final Font font;

        public Context(BlockEntityRenderDispatcher $$0, BlockRenderDispatcher $$1, ItemRenderer $$2, EntityRenderDispatcher $$3, EntityModelSet $$4, Font $$5) {
            this.blockEntityRenderDispatcher = $$0;
            this.blockRenderDispatcher = $$1;
            this.itemRenderer = $$2;
            this.entityRenderer = $$3;
            this.modelSet = $$4;
            this.font = $$5;
        }

        public BlockEntityRenderDispatcher getBlockEntityRenderDispatcher() {
            return this.blockEntityRenderDispatcher;
        }

        public BlockRenderDispatcher getBlockRenderDispatcher() {
            return this.blockRenderDispatcher;
        }

        public EntityRenderDispatcher getEntityRenderer() {
            return this.entityRenderer;
        }

        public ItemRenderer getItemRenderer() {
            return this.itemRenderer;
        }

        public EntityModelSet getModelSet() {
            return this.modelSet;
        }

        public ModelPart bakeLayer(ModelLayerLocation $$0) {
            return this.modelSet.bakeLayer($$0);
        }

        public Font getFont() {
            return this.font;
        }
    }
}