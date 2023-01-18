/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSyntaxException
 *  com.mojang.datafixers.util.Pair
 *  java.lang.Boolean
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Collection
 *  java.util.List
 *  java.util.Optional
 */
package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetBannerPatternFunction
extends LootItemConditionalFunction {
    final List<Pair<Holder<BannerPattern>, DyeColor>> patterns;
    final boolean append;

    SetBannerPatternFunction(LootItemCondition[] $$0, List<Pair<Holder<BannerPattern>, DyeColor>> $$1, boolean $$2) {
        super($$0);
        this.patterns = $$1;
        this.append = $$2;
    }

    @Override
    protected ItemStack run(ItemStack $$0, LootContext $$1) {
        ListTag $$6;
        CompoundTag $$2 = BlockItem.getBlockEntityData($$0);
        if ($$2 == null) {
            $$2 = new CompoundTag();
        }
        BannerPattern.Builder $$3 = new BannerPattern.Builder();
        this.patterns.forEach($$3::addPattern);
        ListTag $$4 = $$3.toListTag();
        if (this.append) {
            ListTag $$5 = $$2.getList("Patterns", 10).copy();
            $$5.addAll((Collection)$$4);
        } else {
            $$6 = $$4;
        }
        $$2.put("Patterns", $$6);
        BlockItem.setBlockEntityData($$0, BlockEntityType.BANNER, $$2);
        return $$0;
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.SET_BANNER_PATTERN;
    }

    public static Builder setBannerPattern(boolean $$0) {
        return new Builder($$0);
    }

    public static class Builder
    extends LootItemConditionalFunction.Builder<Builder> {
        private final ImmutableList.Builder<Pair<Holder<BannerPattern>, DyeColor>> patterns = ImmutableList.builder();
        private final boolean append;

        Builder(boolean $$0) {
            this.append = $$0;
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new SetBannerPatternFunction(this.getConditions(), (List<Pair<Holder<BannerPattern>, DyeColor>>)this.patterns.build(), this.append);
        }

        public Builder addPattern(ResourceKey<BannerPattern> $$0, DyeColor $$1) {
            return this.addPattern(BuiltInRegistries.BANNER_PATTERN.getHolderOrThrow($$0), $$1);
        }

        public Builder addPattern(Holder<BannerPattern> $$0, DyeColor $$1) {
            this.patterns.add((Object)Pair.of($$0, (Object)$$1));
            return this;
        }
    }

    public static class Serializer
    extends LootItemConditionalFunction.Serializer<SetBannerPatternFunction> {
        @Override
        public void serialize(JsonObject $$0, SetBannerPatternFunction $$12, JsonSerializationContext $$2) {
            super.serialize($$0, $$12, $$2);
            JsonArray $$3 = new JsonArray();
            $$12.patterns.forEach($$1 -> {
                JsonObject $$2 = new JsonObject();
                $$2.addProperty("pattern", ((ResourceKey)((Holder)$$1.getFirst()).unwrapKey().orElseThrow(() -> new JsonSyntaxException("Unknown pattern: " + $$1.getFirst()))).location().toString());
                $$2.addProperty("color", ((DyeColor)$$1.getSecond()).getName());
                $$3.add((JsonElement)$$2);
            });
            $$0.add("patterns", (JsonElement)$$3);
            $$0.addProperty("append", Boolean.valueOf((boolean)$$12.append));
        }

        @Override
        public SetBannerPatternFunction deserialize(JsonObject $$0, JsonDeserializationContext $$1, LootItemCondition[] $$2) {
            ImmutableList.Builder $$3 = ImmutableList.builder();
            JsonArray $$4 = GsonHelper.getAsJsonArray($$0, "patterns");
            for (int $$5 = 0; $$5 < $$4.size(); ++$$5) {
                JsonObject $$6 = GsonHelper.convertToJsonObject($$4.get($$5), "pattern[" + $$5 + "]");
                String $$7 = GsonHelper.getAsString($$6, "pattern");
                Optional<Holder.Reference<BannerPattern>> $$8 = BuiltInRegistries.BANNER_PATTERN.getHolder(ResourceKey.create(Registries.BANNER_PATTERN, new ResourceLocation($$7)));
                if ($$8.isEmpty()) {
                    throw new JsonSyntaxException("Unknown pattern: " + $$7);
                }
                String $$9 = GsonHelper.getAsString($$6, "color");
                DyeColor $$10 = DyeColor.byName($$9, null);
                if ($$10 == null) {
                    throw new JsonSyntaxException("Unknown color: " + $$9);
                }
                $$3.add((Object)Pair.of((Object)((Holder)$$8.get()), (Object)$$10));
            }
            boolean $$11 = GsonHelper.getAsBoolean($$0, "append");
            return new SetBannerPatternFunction($$2, (List<Pair<Holder<BannerPattern>, DyeColor>>)$$3.build(), $$11);
        }
    }
}