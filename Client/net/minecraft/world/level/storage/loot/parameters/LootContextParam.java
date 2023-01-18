/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 */
package net.minecraft.world.level.storage.loot.parameters;

import net.minecraft.resources.ResourceLocation;

public class LootContextParam<T> {
    private final ResourceLocation name;

    public LootContextParam(ResourceLocation $$0) {
        this.name = $$0;
    }

    public ResourceLocation getName() {
        return this.name;
    }

    public String toString() {
        return "<parameter " + this.name + ">";
    }
}