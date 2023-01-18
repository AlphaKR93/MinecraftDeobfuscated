/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.Sets
 *  com.google.common.collect.Sets$SetView
 *  com.mojang.logging.LogUtils
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.nio.file.Path
 *  java.util.HashMap
 *  java.util.List
 *  java.util.Map
 *  java.util.Set
 *  java.util.concurrent.CompletableFuture
 *  java.util.function.BiConsumer
 *  java.util.function.Function
 *  java.util.function.Supplier
 *  org.slf4j.Logger
 */
package net.minecraft.data.loot;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.slf4j.Logger;

public class LootTableProvider
implements DataProvider {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final PackOutput.PathProvider pathProvider;
    private final Set<ResourceLocation> requiredTables;
    private final List<SubProviderEntry> subProviders;

    public LootTableProvider(PackOutput $$0, Set<ResourceLocation> $$1, List<SubProviderEntry> $$2) {
        this.pathProvider = $$0.createPathProvider(PackOutput.Target.DATA_PACK, "loot_tables");
        this.subProviders = $$2;
        this.requiredTables = $$1;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput $$02) {
        HashMap $$12 = Maps.newHashMap();
        this.subProviders.forEach(arg_0 -> LootTableProvider.lambda$run$1((Map)$$12, arg_0));
        ValidationContext $$22 = new ValidationContext(LootContextParamSets.ALL_PARAMS, (Function<ResourceLocation, LootItemCondition>)((Function)$$0 -> null), (Function<ResourceLocation, LootTable>)((Function)arg_0 -> ((Map)$$12).get(arg_0)));
        Sets.SetView $$3 = Sets.difference(this.requiredTables, (Set)$$12.keySet());
        for (ResourceLocation $$4 : $$3) {
            $$22.reportProblem("Missing built-in table: " + $$4);
        }
        $$12.forEach(($$1, $$2) -> LootTables.validate($$22, $$1, $$2));
        Multimap<String, String> $$5 = $$22.getProblems();
        if (!$$5.isEmpty()) {
            $$5.forEach(($$0, $$1) -> LOGGER.warn("Found validation problem in {}: {}", $$0, $$1));
            throw new IllegalStateException("Failed to validate loot tables, see logs");
        }
        return CompletableFuture.allOf((CompletableFuture[])((CompletableFuture[])$$12.entrySet().stream().map($$1 -> {
            ResourceLocation $$2 = (ResourceLocation)$$1.getKey();
            LootTable $$3 = (LootTable)$$1.getValue();
            Path $$4 = this.pathProvider.json($$2);
            return DataProvider.saveStable($$02, LootTables.serialize($$3), $$4);
        }).toArray(CompletableFuture[]::new)));
    }

    @Override
    public final String getName() {
        return "Loot Tables";
    }

    private static /* synthetic */ void lambda$run$1(Map $$0, SubProviderEntry $$1) {
        ((LootTableSubProvider)$$1.provider().get()).generate((BiConsumer<ResourceLocation, LootTable.Builder>)((BiConsumer)($$2, $$3) -> {
            if ($$0.put($$2, (Object)$$3.setParamSet($$1.paramSet).build()) != null) {
                throw new IllegalStateException("Duplicate loot table " + $$2);
            }
        }));
    }

    public record SubProviderEntry(Supplier<LootTableSubProvider> provider, LootContextParamSet paramSet) {
    }
}