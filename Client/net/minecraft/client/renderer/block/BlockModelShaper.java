/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Comparable
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.util.Map
 *  java.util.Map$Entry
 */
package net.minecraft.client.renderer.block;

import java.util.Map;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class BlockModelShaper {
    private Map<BlockState, BakedModel> modelByStateCache = Map.of();
    private final ModelManager modelManager;

    public BlockModelShaper(ModelManager $$0) {
        this.modelManager = $$0;
    }

    public TextureAtlasSprite getParticleIcon(BlockState $$0) {
        return this.getBlockModel($$0).getParticleIcon();
    }

    public BakedModel getBlockModel(BlockState $$0) {
        BakedModel $$1 = (BakedModel)this.modelByStateCache.get((Object)$$0);
        if ($$1 == null) {
            $$1 = this.modelManager.getMissingModel();
        }
        return $$1;
    }

    public ModelManager getModelManager() {
        return this.modelManager;
    }

    public void replaceCache(Map<BlockState, BakedModel> $$0) {
        this.modelByStateCache = $$0;
    }

    public static ModelResourceLocation stateToModelLocation(BlockState $$0) {
        return BlockModelShaper.stateToModelLocation(BuiltInRegistries.BLOCK.getKey($$0.getBlock()), $$0);
    }

    public static ModelResourceLocation stateToModelLocation(ResourceLocation $$0, BlockState $$1) {
        return new ModelResourceLocation($$0, BlockModelShaper.statePropertiesToString($$1.getValues()));
    }

    public static String statePropertiesToString(Map<Property<?>, Comparable<?>> $$0) {
        StringBuilder $$1 = new StringBuilder();
        for (Map.Entry $$2 : $$0.entrySet()) {
            if ($$1.length() != 0) {
                $$1.append(',');
            }
            Property $$3 = (Property)$$2.getKey();
            $$1.append($$3.getName());
            $$1.append('=');
            $$1.append(BlockModelShaper.getValue($$3, (Comparable)$$2.getValue()));
        }
        return $$1.toString();
    }

    private static <T extends Comparable<T>> String getValue(Property<T> $$0, Comparable<?> $$1) {
        return $$0.getName($$1);
    }
}