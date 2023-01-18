/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 */
package net.minecraft.client.model.geom;

import net.minecraft.resources.ResourceLocation;

public final class ModelLayerLocation {
    private final ResourceLocation model;
    private final String layer;

    public ModelLayerLocation(ResourceLocation $$0, String $$1) {
        this.model = $$0;
        this.layer = $$1;
    }

    public ResourceLocation getModel() {
        return this.model;
    }

    public String getLayer() {
        return this.layer;
    }

    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        if ($$0 instanceof ModelLayerLocation) {
            ModelLayerLocation $$1 = (ModelLayerLocation)$$0;
            return this.model.equals($$1.model) && this.layer.equals((Object)$$1.layer);
        }
        return false;
    }

    public int hashCode() {
        int $$0 = this.model.hashCode();
        $$0 = 31 * $$0 + this.layer.hashCode();
        return $$0;
    }

    public String toString() {
        return this.model + "#" + this.layer;
    }
}