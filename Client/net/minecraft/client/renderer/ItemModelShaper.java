/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  java.lang.Integer
 *  java.lang.Object
 *  java.util.Map$Entry
 *  javax.annotation.Nullable
 */
package net.minecraft.client.renderer;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemModelShaper {
    public final Int2ObjectMap<ModelResourceLocation> shapes = new Int2ObjectOpenHashMap(256);
    private final Int2ObjectMap<BakedModel> shapesCache = new Int2ObjectOpenHashMap(256);
    private final ModelManager modelManager;

    public ItemModelShaper(ModelManager $$0) {
        this.modelManager = $$0;
    }

    public BakedModel getItemModel(ItemStack $$0) {
        BakedModel $$1 = this.getItemModel($$0.getItem());
        return $$1 == null ? this.modelManager.getMissingModel() : $$1;
    }

    @Nullable
    public BakedModel getItemModel(Item $$0) {
        return (BakedModel)this.shapesCache.get(ItemModelShaper.getIndex($$0));
    }

    private static int getIndex(Item $$0) {
        return Item.getId($$0);
    }

    public void register(Item $$0, ModelResourceLocation $$1) {
        this.shapes.put(ItemModelShaper.getIndex($$0), (Object)$$1);
    }

    public ModelManager getModelManager() {
        return this.modelManager;
    }

    public void rebuildCache() {
        this.shapesCache.clear();
        for (Map.Entry $$0 : this.shapes.entrySet()) {
            this.shapesCache.put((Integer)$$0.getKey(), (Object)this.modelManager.getModel((ModelResourceLocation)$$0.getValue()));
        }
    }
}