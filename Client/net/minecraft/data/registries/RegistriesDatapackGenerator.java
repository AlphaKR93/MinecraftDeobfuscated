/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.Encoder
 *  com.mojang.serialization.JsonOps
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.nio.file.Path
 *  java.util.Optional
 *  java.util.concurrent.CompletableFuture
 *  org.slf4j.Logger
 */
package net.minecraft.data.registries;

import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import org.slf4j.Logger;

public class RegistriesDatapackGenerator
implements DataProvider {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final PackOutput output;
    private final CompletableFuture<HolderLookup.Provider> registries;

    public RegistriesDatapackGenerator(PackOutput $$0, CompletableFuture<HolderLookup.Provider> $$1) {
        this.registries = $$1;
        this.output = $$0;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput $$0) {
        return this.registries.thenCompose($$1 -> {
            RegistryOps $$2 = RegistryOps.create(JsonOps.INSTANCE, $$1);
            return CompletableFuture.allOf((CompletableFuture[])((CompletableFuture[])RegistryDataLoader.WORLDGEN_REGISTRIES.stream().flatMap($$3 -> this.dumpRegistryCap($$0, (HolderLookup.Provider)$$1, $$2, (RegistryDataLoader.RegistryData)((Object)((Object)$$3))).stream()).toArray(CompletableFuture[]::new)));
        });
    }

    private <T> Optional<CompletableFuture<?>> dumpRegistryCap(CachedOutput $$0, HolderLookup.Provider $$1, DynamicOps<JsonElement> $$2, RegistryDataLoader.RegistryData<T> $$3) {
        ResourceKey $$4 = $$3.key();
        return $$1.lookup($$4).map($$42 -> {
            PackOutput.PathProvider $$5 = this.output.createPathProvider(PackOutput.Target.DATA_PACK, $$4.location().getPath());
            return CompletableFuture.allOf((CompletableFuture[])((CompletableFuture[])$$42.listElements().map($$4 -> RegistriesDatapackGenerator.dumpValue($$5.json($$4.key().location()), $$0, $$2, $$3.elementCodec(), $$4.value())).toArray(CompletableFuture[]::new)));
        });
    }

    private static <E> CompletableFuture<?> dumpValue(Path $$0, CachedOutput $$12, DynamicOps<JsonElement> $$2, Encoder<E> $$3, E $$4) {
        Optional $$5 = $$3.encodeStart($$2, $$4).resultOrPartial($$1 -> LOGGER.error("Couldn't serialize element {}: {}", (Object)$$0, $$1));
        if ($$5.isPresent()) {
            return DataProvider.saveStable($$12, (JsonElement)$$5.get(), $$0);
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public final String getName() {
        return "Registries";
    }
}