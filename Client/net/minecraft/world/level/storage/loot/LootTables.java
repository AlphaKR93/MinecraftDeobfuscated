/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.gson.Gson
 *  com.google.gson.JsonElement
 *  com.mojang.logging.LogUtils
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Map
 *  java.util.Set
 *  java.util.function.Function
 *  org.slf4j.Logger
 */
package net.minecraft.world.level.storage.loot;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.Deserializers;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.PredicateManager;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.slf4j.Logger;

public class LootTables
extends SimpleJsonResourceReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = Deserializers.createLootTableSerializer().create();
    private Map<ResourceLocation, LootTable> tables = ImmutableMap.of();
    private final PredicateManager predicateManager;

    public LootTables(PredicateManager $$0) {
        super(GSON, "loot_tables");
        this.predicateManager = $$0;
    }

    public LootTable get(ResourceLocation $$0) {
        return (LootTable)this.tables.getOrDefault((Object)$$0, (Object)LootTable.EMPTY);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> $$02, ResourceManager $$12, ProfilerFiller $$22) {
        ImmutableMap.Builder $$3 = ImmutableMap.builder();
        JsonElement $$4 = (JsonElement)$$02.remove((Object)BuiltInLootTables.EMPTY);
        if ($$4 != null) {
            LOGGER.warn("Datapack tried to redefine {} loot table, ignoring", (Object)BuiltInLootTables.EMPTY);
        }
        $$02.forEach(($$1, $$2) -> {
            try {
                LootTable $$3 = (LootTable)GSON.fromJson($$2, LootTable.class);
                $$3.put($$1, (Object)$$3);
            }
            catch (Exception $$4) {
                LOGGER.error("Couldn't parse loot table {}", $$1, (Object)$$4);
            }
        });
        $$3.put((Object)BuiltInLootTables.EMPTY, (Object)LootTable.EMPTY);
        ImmutableMap $$5 = $$3.build();
        ValidationContext $$6 = new ValidationContext(LootContextParamSets.ALL_PARAMS, (Function<ResourceLocation, LootItemCondition>)((Function)this.predicateManager::get), (Function<ResourceLocation, LootTable>)((Function)arg_0 -> ((ImmutableMap)$$5).get(arg_0)));
        $$5.forEach(($$1, $$2) -> LootTables.validate($$6, $$1, $$2));
        $$6.getProblems().forEach(($$0, $$1) -> LOGGER.warn("Found validation problem in {}: {}", $$0, $$1));
        this.tables = $$5;
    }

    public static void validate(ValidationContext $$0, ResourceLocation $$1, LootTable $$2) {
        $$2.validate($$0.setParams($$2.getParamSet()).enterTable("{" + $$1 + "}", $$1));
    }

    public static JsonElement serialize(LootTable $$0) {
        return GSON.toJsonTree((Object)$$0);
    }

    public Set<ResourceLocation> getIds() {
        return this.tables.keySet();
    }
}