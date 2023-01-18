/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSyntaxException
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntComparators
 *  it.unimi.dsi.fastutil.ints.IntList
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.ArrayList
 *  java.util.Arrays
 *  java.util.Collection
 *  java.util.Collections
 *  java.util.Spliterator
 *  java.util.function.Predicate
 *  java.util.stream.Stream
 *  java.util.stream.StreamSupport
 *  javax.annotation.Nullable
 */
package net.minecraft.world.item.crafting;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Spliterator;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.ItemLike;

public final class Ingredient
implements Predicate<ItemStack> {
    public static final Ingredient EMPTY = new Ingredient((Stream<? extends Value>)Stream.empty());
    private final Value[] values;
    @Nullable
    private ItemStack[] itemStacks;
    @Nullable
    private IntList stackingIds;

    private Ingredient(Stream<? extends Value> $$0) {
        this.values = (Value[])$$0.toArray(Value[]::new);
    }

    public ItemStack[] getItems() {
        if (this.itemStacks == null) {
            this.itemStacks = (ItemStack[])Arrays.stream((Object[])this.values).flatMap($$0 -> $$0.getItems().stream()).distinct().toArray(ItemStack[]::new);
        }
        return this.itemStacks;
    }

    public boolean test(@Nullable ItemStack $$0) {
        if ($$0 == null) {
            return false;
        }
        if (this.isEmpty()) {
            return $$0.isEmpty();
        }
        for (ItemStack $$1 : this.getItems()) {
            if (!$$1.is($$0.getItem())) continue;
            return true;
        }
        return false;
    }

    public IntList getStackingIds() {
        if (this.stackingIds == null) {
            ItemStack[] $$0 = this.getItems();
            this.stackingIds = new IntArrayList($$0.length);
            for (ItemStack $$1 : $$0) {
                this.stackingIds.add(StackedContents.getStackingIndex($$1));
            }
            this.stackingIds.sort(IntComparators.NATURAL_COMPARATOR);
        }
        return this.stackingIds;
    }

    public void toNetwork(FriendlyByteBuf $$0) {
        $$0.writeCollection(Arrays.asList((Object[])this.getItems()), FriendlyByteBuf::writeItem);
    }

    public JsonElement toJson() {
        if (this.values.length == 1) {
            return this.values[0].serialize();
        }
        JsonArray $$0 = new JsonArray();
        for (Value $$1 : this.values) {
            $$0.add((JsonElement)$$1.serialize());
        }
        return $$0;
    }

    public boolean isEmpty() {
        return this.values.length == 0;
    }

    private static Ingredient fromValues(Stream<? extends Value> $$0) {
        Ingredient $$1 = new Ingredient($$0);
        return $$1.isEmpty() ? EMPTY : $$1;
    }

    public static Ingredient of() {
        return EMPTY;
    }

    public static Ingredient of(ItemLike ... $$0) {
        return Ingredient.of((Stream<ItemStack>)Arrays.stream((Object[])$$0).map(ItemStack::new));
    }

    public static Ingredient of(ItemStack ... $$0) {
        return Ingredient.of((Stream<ItemStack>)Arrays.stream((Object[])$$0));
    }

    public static Ingredient of(Stream<ItemStack> $$02) {
        return Ingredient.fromValues((Stream<? extends Value>)$$02.filter($$0 -> !$$0.isEmpty()).map(ItemValue::new));
    }

    public static Ingredient of(TagKey<Item> $$0) {
        return Ingredient.fromValues((Stream<? extends Value>)Stream.of((Object)new TagValue($$0)));
    }

    public static Ingredient fromNetwork(FriendlyByteBuf $$0) {
        return Ingredient.fromValues((Stream<? extends Value>)$$0.readList(FriendlyByteBuf::readItem).stream().map(ItemValue::new));
    }

    public static Ingredient fromJson(@Nullable JsonElement $$02) {
        if ($$02 == null || $$02.isJsonNull()) {
            throw new JsonSyntaxException("Item cannot be null");
        }
        if ($$02.isJsonObject()) {
            return Ingredient.fromValues((Stream<? extends Value>)Stream.of((Object)Ingredient.valueFromJson($$02.getAsJsonObject())));
        }
        if ($$02.isJsonArray()) {
            JsonArray $$1 = $$02.getAsJsonArray();
            if ($$1.size() == 0) {
                throw new JsonSyntaxException("Item array cannot be empty, at least one item must be defined");
            }
            return Ingredient.fromValues((Stream<? extends Value>)StreamSupport.stream((Spliterator)$$1.spliterator(), (boolean)false).map($$0 -> Ingredient.valueFromJson(GsonHelper.convertToJsonObject($$0, "item"))));
        }
        throw new JsonSyntaxException("Expected item to be object or array of objects");
    }

    private static Value valueFromJson(JsonObject $$0) {
        if ($$0.has("item") && $$0.has("tag")) {
            throw new JsonParseException("An ingredient entry is either a tag or an item, not both");
        }
        if ($$0.has("item")) {
            Item $$1 = ShapedRecipe.itemFromJson($$0);
            return new ItemValue(new ItemStack($$1));
        }
        if ($$0.has("tag")) {
            ResourceLocation $$2 = new ResourceLocation(GsonHelper.getAsString($$0, "tag"));
            TagKey<Item> $$3 = TagKey.create(Registries.ITEM, $$2);
            return new TagValue($$3);
        }
        throw new JsonParseException("An ingredient entry needs either a tag or an item");
    }

    static interface Value {
        public Collection<ItemStack> getItems();

        public JsonObject serialize();
    }

    static class TagValue
    implements Value {
        private final TagKey<Item> tag;

        TagValue(TagKey<Item> $$0) {
            this.tag = $$0;
        }

        @Override
        public Collection<ItemStack> getItems() {
            ArrayList $$0 = Lists.newArrayList();
            for (Holder $$1 : BuiltInRegistries.ITEM.getTagOrEmpty(this.tag)) {
                $$0.add((Object)new ItemStack($$1));
            }
            return $$0;
        }

        @Override
        public JsonObject serialize() {
            JsonObject $$0 = new JsonObject();
            $$0.addProperty("tag", this.tag.location().toString());
            return $$0;
        }
    }

    static class ItemValue
    implements Value {
        private final ItemStack item;

        ItemValue(ItemStack $$0) {
            this.item = $$0;
        }

        @Override
        public Collection<ItemStack> getItems() {
            return Collections.singleton((Object)this.item);
        }

        @Override
        public JsonObject serialize() {
            JsonObject $$0 = new JsonObject();
            $$0.addProperty("item", BuiltInRegistries.ITEM.getKey(this.item.getItem()).toString());
            return $$0;
        }
    }
}