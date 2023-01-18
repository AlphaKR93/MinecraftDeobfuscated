/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSyntaxException
 *  java.lang.Integer
 *  java.lang.Object
 *  java.util.List
 *  java.util.Map
 *  java.util.Set
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 */
package net.minecraft.advancements.critereon;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ItemLike;

public class ItemPredicate {
    public static final ItemPredicate ANY = new ItemPredicate();
    @Nullable
    private final TagKey<Item> tag;
    @Nullable
    private final Set<Item> items;
    private final MinMaxBounds.Ints count;
    private final MinMaxBounds.Ints durability;
    private final EnchantmentPredicate[] enchantments;
    private final EnchantmentPredicate[] storedEnchantments;
    @Nullable
    private final Potion potion;
    private final NbtPredicate nbt;

    public ItemPredicate() {
        this.tag = null;
        this.items = null;
        this.potion = null;
        this.count = MinMaxBounds.Ints.ANY;
        this.durability = MinMaxBounds.Ints.ANY;
        this.enchantments = EnchantmentPredicate.NONE;
        this.storedEnchantments = EnchantmentPredicate.NONE;
        this.nbt = NbtPredicate.ANY;
    }

    public ItemPredicate(@Nullable TagKey<Item> $$0, @Nullable Set<Item> $$1, MinMaxBounds.Ints $$2, MinMaxBounds.Ints $$3, EnchantmentPredicate[] $$4, EnchantmentPredicate[] $$5, @Nullable Potion $$6, NbtPredicate $$7) {
        this.tag = $$0;
        this.items = $$1;
        this.count = $$2;
        this.durability = $$3;
        this.enchantments = $$4;
        this.storedEnchantments = $$5;
        this.potion = $$6;
        this.nbt = $$7;
    }

    public boolean matches(ItemStack $$0) {
        if (this == ANY) {
            return true;
        }
        if (this.tag != null && !$$0.is(this.tag)) {
            return false;
        }
        if (this.items != null && !this.items.contains((Object)$$0.getItem())) {
            return false;
        }
        if (!this.count.matches($$0.getCount())) {
            return false;
        }
        if (!this.durability.isAny() && !$$0.isDamageableItem()) {
            return false;
        }
        if (!this.durability.matches($$0.getMaxDamage() - $$0.getDamageValue())) {
            return false;
        }
        if (!this.nbt.matches($$0)) {
            return false;
        }
        if (this.enchantments.length > 0) {
            Map<Enchantment, Integer> $$1 = EnchantmentHelper.deserializeEnchantments($$0.getEnchantmentTags());
            for (EnchantmentPredicate $$2 : this.enchantments) {
                if ($$2.containedIn($$1)) continue;
                return false;
            }
        }
        if (this.storedEnchantments.length > 0) {
            Map<Enchantment, Integer> $$3 = EnchantmentHelper.deserializeEnchantments(EnchantedBookItem.getEnchantments($$0));
            for (EnchantmentPredicate $$4 : this.storedEnchantments) {
                if ($$4.containedIn($$3)) continue;
                return false;
            }
        }
        Potion $$5 = PotionUtils.getPotion($$0);
        return this.potion == null || this.potion == $$5;
    }

    public static ItemPredicate fromJson(@Nullable JsonElement $$0) {
        if ($$0 == null || $$0.isJsonNull()) {
            return ANY;
        }
        JsonObject $$1 = GsonHelper.convertToJsonObject($$0, "item");
        MinMaxBounds.Ints $$2 = MinMaxBounds.Ints.fromJson($$1.get("count"));
        MinMaxBounds.Ints $$3 = MinMaxBounds.Ints.fromJson($$1.get("durability"));
        if ($$1.has("data")) {
            throw new JsonParseException("Disallowed data tag found");
        }
        NbtPredicate $$4 = NbtPredicate.fromJson($$1.get("nbt"));
        ImmutableSet $$5 = null;
        JsonArray $$6 = GsonHelper.getAsJsonArray($$1, "items", null);
        if ($$6 != null) {
            ImmutableSet.Builder $$7 = ImmutableSet.builder();
            for (JsonElement $$8 : $$6) {
                ResourceLocation $$9 = new ResourceLocation(GsonHelper.convertToString($$8, "item"));
                $$7.add((Object)((Item)BuiltInRegistries.ITEM.getOptional($$9).orElseThrow(() -> new JsonSyntaxException("Unknown item id '" + $$9 + "'"))));
            }
            $$5 = $$7.build();
        }
        TagKey<Item> $$10 = null;
        if ($$1.has("tag")) {
            ResourceLocation $$11 = new ResourceLocation(GsonHelper.getAsString($$1, "tag"));
            $$10 = TagKey.create(Registries.ITEM, $$11);
        }
        Potion $$12 = null;
        if ($$1.has("potion")) {
            ResourceLocation $$13 = new ResourceLocation(GsonHelper.getAsString($$1, "potion"));
            $$12 = (Potion)BuiltInRegistries.POTION.getOptional($$13).orElseThrow(() -> new JsonSyntaxException("Unknown potion '" + $$13 + "'"));
        }
        EnchantmentPredicate[] $$14 = EnchantmentPredicate.fromJsonArray($$1.get("enchantments"));
        EnchantmentPredicate[] $$15 = EnchantmentPredicate.fromJsonArray($$1.get("stored_enchantments"));
        return new ItemPredicate($$10, (Set<Item>)$$5, $$2, $$3, $$14, $$15, $$12, $$4);
    }

    public JsonElement serializeToJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        JsonObject $$0 = new JsonObject();
        if (this.items != null) {
            JsonArray $$1 = new JsonArray();
            for (Item $$2 : this.items) {
                $$1.add(BuiltInRegistries.ITEM.getKey($$2).toString());
            }
            $$0.add("items", (JsonElement)$$1);
        }
        if (this.tag != null) {
            $$0.addProperty("tag", this.tag.location().toString());
        }
        $$0.add("count", this.count.serializeToJson());
        $$0.add("durability", this.durability.serializeToJson());
        $$0.add("nbt", this.nbt.serializeToJson());
        if (this.enchantments.length > 0) {
            JsonArray $$3 = new JsonArray();
            for (EnchantmentPredicate $$4 : this.enchantments) {
                $$3.add($$4.serializeToJson());
            }
            $$0.add("enchantments", (JsonElement)$$3);
        }
        if (this.storedEnchantments.length > 0) {
            JsonArray $$5 = new JsonArray();
            for (EnchantmentPredicate $$6 : this.storedEnchantments) {
                $$5.add($$6.serializeToJson());
            }
            $$0.add("stored_enchantments", (JsonElement)$$5);
        }
        if (this.potion != null) {
            $$0.addProperty("potion", BuiltInRegistries.POTION.getKey(this.potion).toString());
        }
        return $$0;
    }

    public static ItemPredicate[] fromJsonArray(@Nullable JsonElement $$0) {
        if ($$0 == null || $$0.isJsonNull()) {
            return new ItemPredicate[0];
        }
        JsonArray $$1 = GsonHelper.convertToJsonArray($$0, "items");
        ItemPredicate[] $$2 = new ItemPredicate[$$1.size()];
        for (int $$3 = 0; $$3 < $$2.length; ++$$3) {
            $$2[$$3] = ItemPredicate.fromJson($$1.get($$3));
        }
        return $$2;
    }

    public static class Builder {
        private final List<EnchantmentPredicate> enchantments = Lists.newArrayList();
        private final List<EnchantmentPredicate> storedEnchantments = Lists.newArrayList();
        @Nullable
        private Set<Item> items;
        @Nullable
        private TagKey<Item> tag;
        private MinMaxBounds.Ints count = MinMaxBounds.Ints.ANY;
        private MinMaxBounds.Ints durability = MinMaxBounds.Ints.ANY;
        @Nullable
        private Potion potion;
        private NbtPredicate nbt = NbtPredicate.ANY;

        private Builder() {
        }

        public static Builder item() {
            return new Builder();
        }

        public Builder of(ItemLike ... $$0) {
            this.items = (Set)Stream.of((Object[])$$0).map(ItemLike::asItem).collect(ImmutableSet.toImmutableSet());
            return this;
        }

        public Builder of(TagKey<Item> $$0) {
            this.tag = $$0;
            return this;
        }

        public Builder withCount(MinMaxBounds.Ints $$0) {
            this.count = $$0;
            return this;
        }

        public Builder hasDurability(MinMaxBounds.Ints $$0) {
            this.durability = $$0;
            return this;
        }

        public Builder isPotion(Potion $$0) {
            this.potion = $$0;
            return this;
        }

        public Builder hasNbt(CompoundTag $$0) {
            this.nbt = new NbtPredicate($$0);
            return this;
        }

        public Builder hasEnchantment(EnchantmentPredicate $$0) {
            this.enchantments.add((Object)$$0);
            return this;
        }

        public Builder hasStoredEnchantment(EnchantmentPredicate $$0) {
            this.storedEnchantments.add((Object)$$0);
            return this;
        }

        public ItemPredicate build() {
            return new ItemPredicate(this.tag, this.items, this.count, this.durability, (EnchantmentPredicate[])this.enchantments.toArray((Object[])EnchantmentPredicate.NONE), (EnchantmentPredicate[])this.storedEnchantments.toArray((Object[])EnchantmentPredicate.NONE), this.potion, this.nbt);
        }
    }
}