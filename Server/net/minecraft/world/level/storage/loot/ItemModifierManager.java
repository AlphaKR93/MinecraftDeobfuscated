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
 *  java.lang.UnsupportedOperationException
 *  java.util.Collections
 *  java.util.Map
 *  java.util.Set
 *  java.util.function.BiFunction
 *  java.util.function.Function
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world.level.storage.loot;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.Deserializers;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.PredicateManager;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.slf4j.Logger;

public class ItemModifierManager
extends SimpleJsonResourceReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = Deserializers.createFunctionSerializer().create();
    private final PredicateManager predicateManager;
    private final LootTables lootTables;
    private Map<ResourceLocation, LootItemFunction> functions = ImmutableMap.of();

    public ItemModifierManager(PredicateManager $$0, LootTables $$1) {
        super(GSON, "item_modifiers");
        this.predicateManager = $$0;
        this.lootTables = $$1;
    }

    @Nullable
    public LootItemFunction get(ResourceLocation $$0) {
        return (LootItemFunction)this.functions.get((Object)$$0);
    }

    public LootItemFunction get(ResourceLocation $$0, LootItemFunction $$1) {
        return (LootItemFunction)this.functions.getOrDefault((Object)$$0, (Object)$$1);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> $$02, ResourceManager $$12, ProfilerFiller $$22) {
        ImmutableMap.Builder $$3 = ImmutableMap.builder();
        $$02.forEach(($$1, $$2) -> {
            try {
                if ($$2.isJsonArray()) {
                    LootItemFunction[] $$3 = (LootItemFunction[])GSON.fromJson($$2, LootItemFunction[].class);
                    $$3.put($$1, (Object)new FunctionSequence($$3));
                } else {
                    LootItemFunction $$4 = (LootItemFunction)GSON.fromJson($$2, LootItemFunction.class);
                    $$3.put($$1, (Object)$$4);
                }
            }
            catch (Exception $$5) {
                LOGGER.error("Couldn't parse item modifier {}", $$1, (Object)$$5);
            }
        });
        ImmutableMap $$4 = $$3.build();
        ValidationContext $$5 = new ValidationContext(LootContextParamSets.ALL_PARAMS, (Function<ResourceLocation, LootItemCondition>)((Function)this.predicateManager::get), (Function<ResourceLocation, LootTable>)((Function)this.lootTables::get));
        $$4.forEach(($$1, $$2) -> $$2.validate($$5));
        $$5.getProblems().forEach(($$0, $$1) -> LOGGER.warn("Found item modifier validation problem in {}: {}", $$0, $$1));
        this.functions = $$4;
    }

    public Set<ResourceLocation> getKeys() {
        return Collections.unmodifiableSet((Set)this.functions.keySet());
    }

    static class FunctionSequence
    implements LootItemFunction {
        protected final LootItemFunction[] functions;
        private final BiFunction<ItemStack, LootContext, ItemStack> compositeFunction;

        public FunctionSequence(LootItemFunction[] $$0) {
            this.functions = $$0;
            this.compositeFunction = LootItemFunctions.compose($$0);
        }

        public ItemStack apply(ItemStack $$0, LootContext $$1) {
            return (ItemStack)this.compositeFunction.apply((Object)$$0, (Object)$$1);
        }

        @Override
        public LootItemFunctionType getType() {
            throw new UnsupportedOperationException();
        }
    }
}