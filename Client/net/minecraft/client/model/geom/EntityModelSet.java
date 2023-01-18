/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Map
 */
package net.minecraft.client.model.geom;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

public class EntityModelSet
implements ResourceManagerReloadListener {
    private Map<ModelLayerLocation, LayerDefinition> roots = ImmutableMap.of();

    public ModelPart bakeLayer(ModelLayerLocation $$0) {
        LayerDefinition $$1 = (LayerDefinition)this.roots.get((Object)$$0);
        if ($$1 == null) {
            throw new IllegalArgumentException("No model for layer " + $$0);
        }
        return $$1.bakeRoot();
    }

    @Override
    public void onResourceManagerReload(ResourceManager $$0) {
        this.roots = ImmutableMap.copyOf(LayerDefinitions.createRoots());
    }
}