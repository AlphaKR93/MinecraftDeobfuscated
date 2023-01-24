/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSyntaxException
 *  java.lang.Integer
 *  java.lang.Iterable
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.HashMap
 *  java.util.HashSet
 *  java.util.Iterator
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.Set
 */
package net.minecraft.world.item.crafting;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class ShapedRecipe
implements CraftingRecipe {
    final int width;
    final int height;
    final NonNullList<Ingredient> recipeItems;
    final ItemStack result;
    private final ResourceLocation id;
    final String group;
    final CraftingBookCategory category;

    public ShapedRecipe(ResourceLocation $$0, String $$1, CraftingBookCategory $$2, int $$3, int $$4, NonNullList<Ingredient> $$5, ItemStack $$6) {
        this.id = $$0;
        this.group = $$1;
        this.category = $$2;
        this.width = $$3;
        this.height = $$4;
        this.recipeItems = $$5;
        this.result = $$6;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.SHAPED_RECIPE;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public CraftingBookCategory category() {
        return this.category;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess $$0) {
        return this.result;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.recipeItems;
    }

    @Override
    public boolean canCraftInDimensions(int $$0, int $$1) {
        return $$0 >= this.width && $$1 >= this.height;
    }

    @Override
    public boolean matches(CraftingContainer $$0, Level $$1) {
        for (int $$2 = 0; $$2 <= $$0.getWidth() - this.width; ++$$2) {
            for (int $$3 = 0; $$3 <= $$0.getHeight() - this.height; ++$$3) {
                if (this.matches($$0, $$2, $$3, true)) {
                    return true;
                }
                if (!this.matches($$0, $$2, $$3, false)) continue;
                return true;
            }
        }
        return false;
    }

    private boolean matches(CraftingContainer $$0, int $$1, int $$2, boolean $$3) {
        for (int $$4 = 0; $$4 < $$0.getWidth(); ++$$4) {
            for (int $$5 = 0; $$5 < $$0.getHeight(); ++$$5) {
                int $$6 = $$4 - $$1;
                int $$7 = $$5 - $$2;
                Ingredient $$8 = Ingredient.EMPTY;
                if ($$6 >= 0 && $$7 >= 0 && $$6 < this.width && $$7 < this.height) {
                    $$8 = $$3 ? this.recipeItems.get(this.width - $$6 - 1 + $$7 * this.width) : this.recipeItems.get($$6 + $$7 * this.width);
                }
                if ($$8.test($$0.getItem($$4 + $$5 * $$0.getWidth()))) continue;
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack assemble(CraftingContainer $$0, RegistryAccess $$1) {
        return this.getResultItem($$1).copy();
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    static NonNullList<Ingredient> dissolvePattern(String[] $$0, Map<String, Ingredient> $$1, int $$2, int $$3) {
        NonNullList<Ingredient> $$4 = NonNullList.withSize($$2 * $$3, Ingredient.EMPTY);
        HashSet $$5 = Sets.newHashSet((Iterable)$$1.keySet());
        $$5.remove((Object)" ");
        for (int $$6 = 0; $$6 < $$0.length; ++$$6) {
            for (int $$7 = 0; $$7 < $$0[$$6].length(); ++$$7) {
                String $$8 = $$0[$$6].substring($$7, $$7 + 1);
                Ingredient $$9 = (Ingredient)$$1.get((Object)$$8);
                if ($$9 == null) {
                    throw new JsonSyntaxException("Pattern references symbol '" + $$8 + "' but it's not defined in the key");
                }
                $$5.remove((Object)$$8);
                $$4.set($$7 + $$2 * $$6, $$9);
            }
        }
        if (!$$5.isEmpty()) {
            throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + (Set)$$5);
        }
        return $$4;
    }

    @VisibleForTesting
    static String[] shrink(String ... $$0) {
        int $$1 = Integer.MAX_VALUE;
        int $$2 = 0;
        int $$3 = 0;
        int $$4 = 0;
        for (int $$5 = 0; $$5 < $$0.length; ++$$5) {
            String $$6 = $$0[$$5];
            $$1 = Math.min((int)$$1, (int)ShapedRecipe.firstNonSpace($$6));
            int $$7 = ShapedRecipe.lastNonSpace($$6);
            $$2 = Math.max((int)$$2, (int)$$7);
            if ($$7 < 0) {
                if ($$3 == $$5) {
                    ++$$3;
                }
                ++$$4;
                continue;
            }
            $$4 = 0;
        }
        if ($$0.length == $$4) {
            return new String[0];
        }
        String[] $$8 = new String[$$0.length - $$4 - $$3];
        for (int $$9 = 0; $$9 < $$8.length; ++$$9) {
            $$8[$$9] = $$0[$$9 + $$3].substring($$1, $$2 + 1);
        }
        return $$8;
    }

    @Override
    public boolean isIncomplete() {
        NonNullList<Ingredient> $$02 = this.getIngredients();
        return $$02.isEmpty() || $$02.stream().filter($$0 -> !$$0.isEmpty()).anyMatch($$0 -> $$0.getItems().length == 0);
    }

    private static int firstNonSpace(String $$0) {
        int $$1;
        for ($$1 = 0; $$1 < $$0.length() && $$0.charAt($$1) == ' '; ++$$1) {
        }
        return $$1;
    }

    private static int lastNonSpace(String $$0) {
        int $$1;
        for ($$1 = $$0.length() - 1; $$1 >= 0 && $$0.charAt($$1) == ' '; --$$1) {
        }
        return $$1;
    }

    static String[] patternFromJson(JsonArray $$0) {
        String[] $$1 = new String[$$0.size()];
        if ($$1.length > 3) {
            throw new JsonSyntaxException("Invalid pattern: too many rows, 3 is maximum");
        }
        if ($$1.length == 0) {
            throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
        }
        for (int $$2 = 0; $$2 < $$1.length; ++$$2) {
            String $$3 = GsonHelper.convertToString($$0.get($$2), "pattern[" + $$2 + "]");
            if ($$3.length() > 3) {
                throw new JsonSyntaxException("Invalid pattern: too many columns, 3 is maximum");
            }
            if ($$2 > 0 && $$1[0].length() != $$3.length()) {
                throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
            }
            $$1[$$2] = $$3;
        }
        return $$1;
    }

    static Map<String, Ingredient> keyFromJson(JsonObject $$0) {
        HashMap $$1 = Maps.newHashMap();
        for (Map.Entry $$2 : $$0.entrySet()) {
            if (((String)$$2.getKey()).length() != 1) {
                throw new JsonSyntaxException("Invalid key entry: '" + (String)$$2.getKey() + "' is an invalid symbol (must be 1 character only).");
            }
            if (" ".equals($$2.getKey())) {
                throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
            }
            $$1.put((Object)((String)$$2.getKey()), (Object)Ingredient.fromJson((JsonElement)$$2.getValue()));
        }
        $$1.put((Object)" ", (Object)Ingredient.EMPTY);
        return $$1;
    }

    public static ItemStack itemStackFromJson(JsonObject $$0) {
        Item $$1 = ShapedRecipe.itemFromJson($$0);
        if ($$0.has("data")) {
            throw new JsonParseException("Disallowed data tag found");
        }
        int $$2 = GsonHelper.getAsInt($$0, "count", 1);
        if ($$2 < 1) {
            throw new JsonSyntaxException("Invalid output count: " + $$2);
        }
        return new ItemStack($$1, $$2);
    }

    public static Item itemFromJson(JsonObject $$0) {
        String $$1 = GsonHelper.getAsString($$0, "item");
        Item $$2 = (Item)BuiltInRegistries.ITEM.getOptional(new ResourceLocation($$1)).orElseThrow(() -> new JsonSyntaxException("Unknown item '" + $$1 + "'"));
        if ($$2 == Items.AIR) {
            throw new JsonSyntaxException("Invalid item: " + $$1);
        }
        return $$2;
    }

    public static class Serializer
    implements RecipeSerializer<ShapedRecipe> {
        @Override
        public ShapedRecipe fromJson(ResourceLocation $$0, JsonObject $$1) {
            String $$2 = GsonHelper.getAsString($$1, "group", "");
            CraftingBookCategory $$3 = CraftingBookCategory.CODEC.byName(GsonHelper.getAsString($$1, "category", null), CraftingBookCategory.MISC);
            Map<String, Ingredient> $$4 = ShapedRecipe.keyFromJson(GsonHelper.getAsJsonObject($$1, "key"));
            String[] $$5 = ShapedRecipe.shrink(ShapedRecipe.patternFromJson(GsonHelper.getAsJsonArray($$1, "pattern")));
            int $$6 = $$5[0].length();
            int $$7 = $$5.length;
            NonNullList<Ingredient> $$8 = ShapedRecipe.dissolvePattern($$5, $$4, $$6, $$7);
            ItemStack $$9 = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject($$1, "result"));
            return new ShapedRecipe($$0, $$2, $$3, $$6, $$7, $$8, $$9);
        }

        @Override
        public ShapedRecipe fromNetwork(ResourceLocation $$0, FriendlyByteBuf $$1) {
            int $$2 = $$1.readVarInt();
            int $$3 = $$1.readVarInt();
            String $$4 = $$1.readUtf();
            CraftingBookCategory $$5 = $$1.readEnum(CraftingBookCategory.class);
            NonNullList<Ingredient> $$6 = NonNullList.withSize($$2 * $$3, Ingredient.EMPTY);
            for (int $$7 = 0; $$7 < $$6.size(); ++$$7) {
                $$6.set($$7, Ingredient.fromNetwork($$1));
            }
            ItemStack $$8 = $$1.readItem();
            return new ShapedRecipe($$0, $$4, $$5, $$2, $$3, $$6, $$8);
        }

        @Override
        public void toNetwork(FriendlyByteBuf $$0, ShapedRecipe $$1) {
            $$0.writeVarInt($$1.width);
            $$0.writeVarInt($$1.height);
            $$0.writeUtf($$1.group);
            $$0.writeEnum($$1.category);
            Iterator iterator = $$1.recipeItems.iterator();
            while (iterator.hasNext()) {
                Ingredient $$2 = (Ingredient)iterator.next();
                $$2.toNetwork($$0);
            }
            $$0.writeItem($$1.result);
        }
    }
}