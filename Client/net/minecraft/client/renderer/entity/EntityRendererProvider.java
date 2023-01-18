/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.FunctionalInterface
 *  java.lang.Object
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.Entity;

@FunctionalInterface
public interface EntityRendererProvider<T extends Entity> {
    public EntityRenderer<T> create(Context var1);

    public static class Context {
        private final EntityRenderDispatcher entityRenderDispatcher;
        private final ItemRenderer itemRenderer;
        private final BlockRenderDispatcher blockRenderDispatcher;
        private final ItemInHandRenderer itemInHandRenderer;
        private final ResourceManager resourceManager;
        private final EntityModelSet modelSet;
        private final Font font;

        public Context(EntityRenderDispatcher $$0, ItemRenderer $$1, BlockRenderDispatcher $$2, ItemInHandRenderer $$3, ResourceManager $$4, EntityModelSet $$5, Font $$6) {
            this.entityRenderDispatcher = $$0;
            this.itemRenderer = $$1;
            this.blockRenderDispatcher = $$2;
            this.itemInHandRenderer = $$3;
            this.resourceManager = $$4;
            this.modelSet = $$5;
            this.font = $$6;
        }

        public EntityRenderDispatcher getEntityRenderDispatcher() {
            return this.entityRenderDispatcher;
        }

        public ItemRenderer getItemRenderer() {
            return this.itemRenderer;
        }

        public BlockRenderDispatcher getBlockRenderDispatcher() {
            return this.blockRenderDispatcher;
        }

        public ItemInHandRenderer getItemInHandRenderer() {
            return this.itemInHandRenderer;
        }

        public ResourceManager getResourceManager() {
            return this.resourceManager;
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