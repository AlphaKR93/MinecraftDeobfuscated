/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  java.lang.Object
 *  java.util.function.Supplier
 */
package net.minecraft.data.models.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;

public class DelegatedModel
implements Supplier<JsonElement> {
    private final ResourceLocation parent;

    public DelegatedModel(ResourceLocation $$0) {
        this.parent = $$0;
    }

    public JsonElement get() {
        JsonObject $$0 = new JsonObject();
        $$0.addProperty("parent", this.parent.toString());
        return $$0;
    }
}