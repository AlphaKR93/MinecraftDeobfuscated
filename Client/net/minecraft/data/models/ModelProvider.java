/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonElement
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.nio.file.Path
 *  java.util.HashMap
 *  java.util.HashSet
 *  java.util.List
 *  java.util.Map
 *  java.util.Set
 *  java.util.concurrent.CompletableFuture
 *  java.util.function.BiConsumer
 *  java.util.function.Consumer
 *  java.util.function.Function
 *  java.util.function.Supplier
 */
package net.minecraft.data.models;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.BlockStateGenerator;
import net.minecraft.data.models.model.DelegatedModel;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModelProvider
implements DataProvider {
    private final PackOutput.PathProvider blockStatePathProvider;
    private final PackOutput.PathProvider modelPathProvider;

    public ModelProvider(PackOutput $$0) {
        this.blockStatePathProvider = $$0.createPathProvider(PackOutput.Target.RESOURCE_PACK, "blockstates");
        this.modelPathProvider = $$0.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput $$02) {
        HashMap $$1 = Maps.newHashMap();
        Consumer $$2 = arg_0 -> ModelProvider.lambda$run$0((Map)$$1, arg_0);
        HashMap $$3 = Maps.newHashMap();
        HashSet $$4 = Sets.newHashSet();
        BiConsumer $$5 = (arg_0, arg_1) -> ModelProvider.lambda$run$1((Map)$$3, arg_0, arg_1);
        Consumer $$6 = arg_0 -> ((Set)$$4).add(arg_0);
        new BlockModelGenerators((Consumer<BlockStateGenerator>)$$2, (BiConsumer<ResourceLocation, Supplier<JsonElement>>)$$5, (Consumer<Item>)$$6).run();
        new ItemModelGenerators((BiConsumer<ResourceLocation, Supplier<JsonElement>>)$$5).run();
        List $$7 = BuiltInRegistries.BLOCK.stream().filter(arg_0 -> ModelProvider.lambda$run$2((Map)$$1, arg_0)).toList();
        if (!$$7.isEmpty()) {
            throw new IllegalStateException("Missing blockstate definitions for: " + $$7);
        }
        BuiltInRegistries.BLOCK.forEach(arg_0 -> ModelProvider.lambda$run$3((Set)$$4, (Map)$$3, arg_0));
        CompletableFuture[] completableFutureArray = new CompletableFuture[2];
        completableFutureArray[0] = this.saveCollection($$02, (Map)$$1, (Function)$$0 -> this.blockStatePathProvider.json($$0.builtInRegistryHolder().key().location()));
        completableFutureArray[1] = this.saveCollection($$02, (Map)$$3, (Function)this.modelPathProvider::json);
        return CompletableFuture.allOf((CompletableFuture[])completableFutureArray);
    }

    private <T> CompletableFuture<?> saveCollection(CachedOutput $$0, Map<T, ? extends Supplier<JsonElement>> $$1, Function<T, Path> $$22) {
        return CompletableFuture.allOf((CompletableFuture[])((CompletableFuture[])$$1.entrySet().stream().map($$2 -> {
            Path $$3 = (Path)$$22.apply($$2.getKey());
            JsonElement $$4 = (JsonElement)((Supplier)$$2.getValue()).get();
            return DataProvider.saveStable($$0, $$4, $$3);
        }).toArray(CompletableFuture[]::new)));
    }

    @Override
    public final String getName() {
        return "Model Definitions";
    }

    private static /* synthetic */ void lambda$run$3(Set $$0, Map $$1, Block $$2) {
        Item $$3 = (Item)Item.BY_BLOCK.get((Object)$$2);
        if ($$3 != null) {
            if ($$0.contains((Object)$$3)) {
                return;
            }
            ResourceLocation $$4 = ModelLocationUtils.getModelLocation($$3);
            if (!$$1.containsKey((Object)$$4)) {
                $$1.put((Object)$$4, (Object)new DelegatedModel(ModelLocationUtils.getModelLocation($$2)));
            }
        }
    }

    private static /* synthetic */ boolean lambda$run$2(Map $$0, Block $$1) {
        return !$$0.containsKey((Object)$$1);
    }

    private static /* synthetic */ void lambda$run$1(Map $$0, ResourceLocation $$1, Supplier $$2) {
        Supplier $$3 = (Supplier)$$0.put((Object)$$1, (Object)$$2);
        if ($$3 != null) {
            throw new IllegalStateException("Duplicate model definition for " + $$1);
        }
    }

    private static /* synthetic */ void lambda$run$0(Map $$0, BlockStateGenerator $$1) {
        Block $$2 = $$1.getBlock();
        BlockStateGenerator $$3 = (BlockStateGenerator)$$0.put((Object)$$2, (Object)$$1);
        if ($$3 != null) {
            throw new IllegalStateException("Duplicate blockstate definition for " + $$2);
        }
    }
}