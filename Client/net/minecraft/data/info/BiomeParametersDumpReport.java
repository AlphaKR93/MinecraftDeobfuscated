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
package net.minecraft.data.info;

import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import org.slf4j.Logger;

public class BiomeParametersDumpReport
implements DataProvider {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Path topPath;
    private final CompletableFuture<HolderLookup.Provider> registries;

    public BiomeParametersDumpReport(PackOutput $$0, CompletableFuture<HolderLookup.Provider> $$1) {
        this.topPath = $$0.getOutputFolder(PackOutput.Target.REPORTS).resolve("biome_parameters");
        this.registries = $$1;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput $$0) {
        return this.registries.thenCompose($$1 -> {
            RegistryOps $$2 = RegistryOps.create(JsonOps.INSTANCE, $$1);
            HolderLookup.RegistryLookup<Biome> $$32 = $$1.lookupOrThrow(Registries.BIOME);
            return CompletableFuture.allOf((CompletableFuture[])((CompletableFuture[])MultiNoiseBiomeSource.Preset.getPresets().map($$3 -> {
                MultiNoiseBiomeSource $$4 = ((MultiNoiseBiomeSource.Preset)$$3.getSecond()).biomeSource($$32, false);
                return BiomeParametersDumpReport.dumpValue(this.createPath((ResourceLocation)$$3.getFirst()), $$0, $$2, MultiNoiseBiomeSource.CODEC, $$4);
            }).toArray(CompletableFuture[]::new)));
        });
    }

    private static <E> CompletableFuture<?> dumpValue(Path $$0, CachedOutput $$12, DynamicOps<JsonElement> $$2, Encoder<E> $$3, E $$4) {
        Optional $$5 = $$3.encodeStart($$2, $$4).resultOrPartial($$1 -> LOGGER.error("Couldn't serialize element {}: {}", (Object)$$0, $$1));
        if ($$5.isPresent()) {
            return DataProvider.saveStable($$12, (JsonElement)$$5.get(), $$0);
        }
        return CompletableFuture.completedFuture(null);
    }

    private Path createPath(ResourceLocation $$0) {
        return this.topPath.resolve($$0.getNamespace()).resolve($$0.getPath() + ".json");
    }

    @Override
    public final String getName() {
        return "Biome Parameters";
    }
}