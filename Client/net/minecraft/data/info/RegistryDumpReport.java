/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  java.lang.Integer
 *  java.lang.Number
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.nio.file.Path
 *  java.util.concurrent.CompletableFuture
 */
package net.minecraft.data.info;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

public class RegistryDumpReport
implements DataProvider {
    private final PackOutput output;

    public RegistryDumpReport(PackOutput $$0) {
        this.output = $$0;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput $$0) {
        JsonObject $$12 = new JsonObject();
        BuiltInRegistries.REGISTRY.holders().forEach($$1 -> $$12.add($$1.key().location().toString(), RegistryDumpReport.dumpRegistry((Registry)$$1.value())));
        Path $$2 = this.output.getOutputFolder(PackOutput.Target.REPORTS).resolve("registries.json");
        return DataProvider.saveStable($$0, (JsonElement)$$12, $$2);
    }

    private static <T> JsonElement dumpRegistry(Registry<T> $$0) {
        JsonObject $$1 = new JsonObject();
        if ($$0 instanceof DefaultedRegistry) {
            ResourceLocation $$22 = ((DefaultedRegistry)$$0).getDefaultKey();
            $$1.addProperty("default", $$22.toString());
        }
        int $$3 = BuiltInRegistries.REGISTRY.getId($$0);
        $$1.addProperty("protocol_id", (Number)Integer.valueOf((int)$$3));
        JsonObject $$4 = new JsonObject();
        $$0.holders().forEach($$2 -> {
            Object $$3 = $$2.value();
            int $$4 = $$0.getId($$3);
            JsonObject $$5 = new JsonObject();
            $$5.addProperty("protocol_id", (Number)Integer.valueOf((int)$$4));
            $$4.add($$2.key().location().toString(), (JsonElement)$$5);
        });
        $$1.add("entries", (JsonElement)$$4);
        return $$1;
    }

    @Override
    public final String getName() {
        return "Registry Dump";
    }
}