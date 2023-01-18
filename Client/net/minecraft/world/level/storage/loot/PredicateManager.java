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
 *  java.util.function.Function
 *  java.util.function.Predicate
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
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.storage.loot.Deserializers;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import org.slf4j.Logger;

public class PredicateManager
extends SimpleJsonResourceReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = Deserializers.createConditionSerializer().create();
    private Map<ResourceLocation, LootItemCondition> conditions = ImmutableMap.of();

    public PredicateManager() {
        super(GSON, "predicates");
    }

    @Nullable
    public LootItemCondition get(ResourceLocation $$0) {
        return (LootItemCondition)this.conditions.get((Object)$$0);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> $$02, ResourceManager $$12, ProfilerFiller $$22) {
        ImmutableMap.Builder $$3 = ImmutableMap.builder();
        $$02.forEach(($$1, $$2) -> {
            try {
                if ($$2.isJsonArray()) {
                    LootItemCondition[] $$3 = (LootItemCondition[])GSON.fromJson($$2, LootItemCondition[].class);
                    $$3.put($$1, (Object)new CompositePredicate($$3));
                } else {
                    LootItemCondition $$4 = (LootItemCondition)GSON.fromJson($$2, LootItemCondition.class);
                    $$3.put($$1, (Object)$$4);
                }
            }
            catch (Exception $$5) {
                LOGGER.error("Couldn't parse loot table {}", $$1, (Object)$$5);
            }
        });
        ImmutableMap $$4 = $$3.build();
        ValidationContext $$5 = new ValidationContext(LootContextParamSets.ALL_PARAMS, (Function<ResourceLocation, LootItemCondition>)((Function)arg_0 -> ((Map)$$4).get(arg_0)), (Function<ResourceLocation, LootTable>)((Function)$$0 -> null));
        $$4.forEach(($$1, $$2) -> $$2.validate($$5.enterCondition("{" + $$1 + "}", (ResourceLocation)$$1)));
        $$5.getProblems().forEach(($$0, $$1) -> LOGGER.warn("Found validation problem in {}: {}", $$0, $$1));
        this.conditions = $$4;
    }

    public Set<ResourceLocation> getKeys() {
        return Collections.unmodifiableSet((Set)this.conditions.keySet());
    }

    static class CompositePredicate
    implements LootItemCondition {
        private final LootItemCondition[] terms;
        private final Predicate<LootContext> composedPredicate;

        CompositePredicate(LootItemCondition[] $$0) {
            this.terms = $$0;
            this.composedPredicate = LootItemConditions.andConditions($$0);
        }

        public final boolean test(LootContext $$0) {
            return this.composedPredicate.test((Object)$$0);
        }

        @Override
        public void validate(ValidationContext $$0) {
            LootItemCondition.super.validate($$0);
            for (int $$1 = 0; $$1 < this.terms.length; ++$$1) {
                this.terms[$$1].validate($$0.forChild(".term[" + $$1 + "]"));
            }
        }

        @Override
        public LootItemConditionType getType() {
            throw new UnsupportedOperationException();
        }
    }
}