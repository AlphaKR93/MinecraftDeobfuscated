/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Iterator
 */
package net.minecraft.world.item.crafting;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.Iterator;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;

public class ShapelessRecipe
implements CraftingRecipe {
    private final ResourceLocation id;
    final String group;
    final CraftingBookCategory category;
    final ItemStack result;
    final NonNullList<Ingredient> ingredients;

    public ShapelessRecipe(ResourceLocation $$0, String $$1, CraftingBookCategory $$2, ItemStack $$3, NonNullList<Ingredient> $$4) {
        this.id = $$0;
        this.group = $$1;
        this.category = $$2;
        this.result = $$3;
        this.ingredients = $$4;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.SHAPELESS_RECIPE;
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
        return this.ingredients;
    }

    @Override
    public boolean matches(CraftingContainer $$0, Level $$1) {
        StackedContents $$2 = new StackedContents();
        int $$3 = 0;
        for (int $$4 = 0; $$4 < $$0.getContainerSize(); ++$$4) {
            ItemStack $$5 = $$0.getItem($$4);
            if ($$5.isEmpty()) continue;
            ++$$3;
            $$2.accountStack($$5, 1);
        }
        return $$3 == this.ingredients.size() && $$2.canCraft(this, null);
    }

    @Override
    public ItemStack assemble(CraftingContainer $$0, RegistryAccess $$1) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int $$0, int $$1) {
        return $$0 * $$1 >= this.ingredients.size();
    }

    public static class Serializer
    implements RecipeSerializer<ShapelessRecipe> {
        @Override
        public ShapelessRecipe fromJson(ResourceLocation $$0, JsonObject $$1) {
            String $$2 = GsonHelper.getAsString($$1, "group", "");
            CraftingBookCategory $$3 = CraftingBookCategory.CODEC.byName(GsonHelper.getAsString($$1, "category", null), CraftingBookCategory.MISC);
            NonNullList<Ingredient> $$4 = Serializer.itemsFromJson(GsonHelper.getAsJsonArray($$1, "ingredients"));
            if ($$4.isEmpty()) {
                throw new JsonParseException("No ingredients for shapeless recipe");
            }
            if ($$4.size() > 9) {
                throw new JsonParseException("Too many ingredients for shapeless recipe");
            }
            ItemStack $$5 = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject($$1, "result"));
            return new ShapelessRecipe($$0, $$2, $$3, $$5, $$4);
        }

        private static NonNullList<Ingredient> itemsFromJson(JsonArray $$0) {
            NonNullList<Ingredient> $$1 = NonNullList.create();
            for (int $$2 = 0; $$2 < $$0.size(); ++$$2) {
                Ingredient $$3 = Ingredient.fromJson($$0.get($$2));
                if ($$3.isEmpty()) continue;
                $$1.add($$3);
            }
            return $$1;
        }

        @Override
        public ShapelessRecipe fromNetwork(ResourceLocation $$0, FriendlyByteBuf $$1) {
            String $$2 = $$1.readUtf();
            CraftingBookCategory $$3 = $$1.readEnum(CraftingBookCategory.class);
            int $$4 = $$1.readVarInt();
            NonNullList<Ingredient> $$5 = NonNullList.withSize($$4, Ingredient.EMPTY);
            for (int $$6 = 0; $$6 < $$5.size(); ++$$6) {
                $$5.set($$6, Ingredient.fromNetwork($$1));
            }
            ItemStack $$7 = $$1.readItem();
            return new ShapelessRecipe($$0, $$2, $$3, $$7, $$5);
        }

        @Override
        public void toNetwork(FriendlyByteBuf $$0, ShapelessRecipe $$1) {
            $$0.writeUtf($$1.group);
            $$0.writeEnum($$1.category);
            $$0.writeVarInt($$1.ingredients.size());
            Iterator iterator = $$1.ingredients.iterator();
            while (iterator.hasNext()) {
                Ingredient $$2 = (Ingredient)iterator.next();
                $$2.toNetwork($$0);
            }
            $$0.writeItem($$1.result);
        }
    }
}