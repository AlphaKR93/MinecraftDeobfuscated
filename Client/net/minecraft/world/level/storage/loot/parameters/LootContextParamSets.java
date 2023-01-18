/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.BiMap
 *  com.google.common.collect.HashBiMap
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.String
 *  java.util.function.Consumer
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.storage.loot.parameters;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class LootContextParamSets {
    private static final BiMap<ResourceLocation, LootContextParamSet> REGISTRY = HashBiMap.create();
    public static final LootContextParamSet EMPTY = LootContextParamSets.register("empty", (Consumer<LootContextParamSet.Builder>)((Consumer)$$0 -> {}));
    public static final LootContextParamSet CHEST = LootContextParamSets.register("chest", (Consumer<LootContextParamSet.Builder>)((Consumer)$$0 -> $$0.required(LootContextParams.ORIGIN).optional(LootContextParams.THIS_ENTITY)));
    public static final LootContextParamSet COMMAND = LootContextParamSets.register("command", (Consumer<LootContextParamSet.Builder>)((Consumer)$$0 -> $$0.required(LootContextParams.ORIGIN).optional(LootContextParams.THIS_ENTITY)));
    public static final LootContextParamSet SELECTOR = LootContextParamSets.register("selector", (Consumer<LootContextParamSet.Builder>)((Consumer)$$0 -> $$0.required(LootContextParams.ORIGIN).required(LootContextParams.THIS_ENTITY)));
    public static final LootContextParamSet FISHING = LootContextParamSets.register("fishing", (Consumer<LootContextParamSet.Builder>)((Consumer)$$0 -> $$0.required(LootContextParams.ORIGIN).required(LootContextParams.TOOL).optional(LootContextParams.THIS_ENTITY)));
    public static final LootContextParamSet ENTITY = LootContextParamSets.register("entity", (Consumer<LootContextParamSet.Builder>)((Consumer)$$0 -> $$0.required(LootContextParams.THIS_ENTITY).required(LootContextParams.ORIGIN).required(LootContextParams.DAMAGE_SOURCE).optional(LootContextParams.KILLER_ENTITY).optional(LootContextParams.DIRECT_KILLER_ENTITY).optional(LootContextParams.LAST_DAMAGE_PLAYER)));
    public static final LootContextParamSet GIFT = LootContextParamSets.register("gift", (Consumer<LootContextParamSet.Builder>)((Consumer)$$0 -> $$0.required(LootContextParams.ORIGIN).required(LootContextParams.THIS_ENTITY)));
    public static final LootContextParamSet PIGLIN_BARTER = LootContextParamSets.register("barter", (Consumer<LootContextParamSet.Builder>)((Consumer)$$0 -> $$0.required(LootContextParams.THIS_ENTITY)));
    public static final LootContextParamSet ADVANCEMENT_REWARD = LootContextParamSets.register("advancement_reward", (Consumer<LootContextParamSet.Builder>)((Consumer)$$0 -> $$0.required(LootContextParams.THIS_ENTITY).required(LootContextParams.ORIGIN)));
    public static final LootContextParamSet ADVANCEMENT_ENTITY = LootContextParamSets.register("advancement_entity", (Consumer<LootContextParamSet.Builder>)((Consumer)$$0 -> $$0.required(LootContextParams.THIS_ENTITY).required(LootContextParams.ORIGIN)));
    public static final LootContextParamSet ALL_PARAMS = LootContextParamSets.register("generic", (Consumer<LootContextParamSet.Builder>)((Consumer)$$0 -> $$0.required(LootContextParams.THIS_ENTITY).required(LootContextParams.LAST_DAMAGE_PLAYER).required(LootContextParams.DAMAGE_SOURCE).required(LootContextParams.KILLER_ENTITY).required(LootContextParams.DIRECT_KILLER_ENTITY).required(LootContextParams.ORIGIN).required(LootContextParams.BLOCK_STATE).required(LootContextParams.BLOCK_ENTITY).required(LootContextParams.TOOL).required(LootContextParams.EXPLOSION_RADIUS)));
    public static final LootContextParamSet BLOCK = LootContextParamSets.register("block", (Consumer<LootContextParamSet.Builder>)((Consumer)$$0 -> $$0.required(LootContextParams.BLOCK_STATE).required(LootContextParams.ORIGIN).required(LootContextParams.TOOL).optional(LootContextParams.THIS_ENTITY).optional(LootContextParams.BLOCK_ENTITY).optional(LootContextParams.EXPLOSION_RADIUS)));

    private static LootContextParamSet register(String $$0, Consumer<LootContextParamSet.Builder> $$1) {
        LootContextParamSet.Builder $$2 = new LootContextParamSet.Builder();
        $$1.accept((Object)$$2);
        LootContextParamSet $$3 = $$2.build();
        ResourceLocation $$4 = new ResourceLocation($$0);
        LootContextParamSet $$5 = (LootContextParamSet)REGISTRY.put((Object)$$4, (Object)$$3);
        if ($$5 != null) {
            throw new IllegalStateException("Loot table parameter set " + $$4 + " is already registered");
        }
        return $$3;
    }

    @Nullable
    public static LootContextParamSet get(ResourceLocation $$0) {
        return (LootContextParamSet)REGISTRY.get((Object)$$0);
    }

    @Nullable
    public static ResourceLocation getKey(LootContextParamSet $$0) {
        return (ResourceLocation)REGISTRY.inverse().get((Object)$$0);
    }
}