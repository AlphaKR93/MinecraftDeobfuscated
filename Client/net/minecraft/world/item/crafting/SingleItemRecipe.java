/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 */
package net.minecraft.world.item.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public abstract class SingleItemRecipe
implements Recipe<Container> {
    protected final Ingredient ingredient;
    protected final ItemStack result;
    private final RecipeType<?> type;
    private final RecipeSerializer<?> serializer;
    protected final ResourceLocation id;
    protected final String group;

    public SingleItemRecipe(RecipeType<?> $$0, RecipeSerializer<?> $$1, ResourceLocation $$2, String $$3, Ingredient $$4, ItemStack $$5) {
        this.type = $$0;
        this.serializer = $$1;
        this.id = $$2;
        this.group = $$3;
        this.ingredient = $$4;
        this.result = $$5;
    }

    @Override
    public RecipeType<?> getType() {
        return this.type;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return this.serializer;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public ItemStack getResultItem() {
        return this.result;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> $$0 = NonNullList.create();
        $$0.add(this.ingredient);
        return $$0;
    }

    @Override
    public boolean canCraftInDimensions(int $$0, int $$1) {
        return true;
    }

    @Override
    public ItemStack assemble(Container $$0) {
        return this.result.copy();
    }

    public static class Serializer<T extends SingleItemRecipe>
    implements RecipeSerializer<T> {
        final SingleItemMaker<T> factory;

        protected Serializer(SingleItemMaker<T> $$0) {
            this.factory = $$0;
        }

        @Override
        public T fromJson(ResourceLocation $$0, JsonObject $$1) {
            Ingredient $$4;
            String $$2 = GsonHelper.getAsString($$1, "group", "");
            if (GsonHelper.isArrayNode($$1, "ingredient")) {
                Ingredient $$3 = Ingredient.fromJson((JsonElement)GsonHelper.getAsJsonArray($$1, "ingredient"));
            } else {
                $$4 = Ingredient.fromJson((JsonElement)GsonHelper.getAsJsonObject($$1, "ingredient"));
            }
            String $$5 = GsonHelper.getAsString($$1, "result");
            int $$6 = GsonHelper.getAsInt($$1, "count");
            ItemStack $$7 = new ItemStack(BuiltInRegistries.ITEM.get(new ResourceLocation($$5)), $$6);
            return this.factory.create($$0, $$2, $$4, $$7);
        }

        @Override
        public T fromNetwork(ResourceLocation $$0, FriendlyByteBuf $$1) {
            String $$2 = $$1.readUtf();
            Ingredient $$3 = Ingredient.fromNetwork($$1);
            ItemStack $$4 = $$1.readItem();
            return this.factory.create($$0, $$2, $$3, $$4);
        }

        @Override
        public void toNetwork(FriendlyByteBuf $$0, T $$1) {
            $$0.writeUtf(((SingleItemRecipe)$$1).group);
            ((SingleItemRecipe)$$1).ingredient.toNetwork($$0);
            $$0.writeItem(((SingleItemRecipe)$$1).result);
        }

        static interface SingleItemMaker<T extends SingleItemRecipe> {
            public T create(ResourceLocation var1, String var2, Ingredient var3, ItemStack var4);
        }
    }
}