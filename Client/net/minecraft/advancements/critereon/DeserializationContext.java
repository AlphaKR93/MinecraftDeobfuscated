/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.mojang.logging.LogUtils
 *  java.lang.Object
 *  java.lang.String
 *  java.util.function.Function
 *  org.slf4j.Logger
 */
package net.minecraft.advancements.critereon;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.Deserializers;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.PredicateManager;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.slf4j.Logger;

public class DeserializationContext {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final ResourceLocation id;
    private final PredicateManager predicateManager;
    private final Gson predicateGson = Deserializers.createConditionSerializer().create();

    public DeserializationContext(ResourceLocation $$0, PredicateManager $$1) {
        this.id = $$0;
        this.predicateManager = $$1;
    }

    public final LootItemCondition[] deserializeConditions(JsonArray $$02, String $$12, LootContextParamSet $$22) {
        LootItemCondition[] $$3 = (LootItemCondition[])this.predicateGson.fromJson((JsonElement)$$02, LootItemCondition[].class);
        ValidationContext $$4 = new ValidationContext($$22, (Function<ResourceLocation, LootItemCondition>)((Function)this.predicateManager::get), (Function<ResourceLocation, LootTable>)((Function)$$0 -> null));
        for (LootItemCondition $$5 : $$3) {
            $$5.validate($$4);
            $$4.getProblems().forEach(($$1, $$2) -> LOGGER.warn("Found validation problem in advancement trigger {}/{}: {}", new Object[]{$$12, $$1, $$2}));
        }
        return $$3;
    }

    public ResourceLocation getAdvancementId() {
        return this.id;
    }
}