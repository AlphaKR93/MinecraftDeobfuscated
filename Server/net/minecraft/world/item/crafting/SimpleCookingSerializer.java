/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 */
package net.minecraft.world.item.crafting;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;

public class SimpleCookingSerializer<T extends AbstractCookingRecipe>
implements RecipeSerializer<T> {
    private final int defaultCookingTime;
    private final CookieBaker<T> factory;

    public SimpleCookingSerializer(CookieBaker<T> $$0, int $$1) {
        this.defaultCookingTime = $$1;
        this.factory = $$0;
    }

    @Override
    public T fromJson(ResourceLocation $$0, JsonObject $$1) {
        String $$2 = GsonHelper.getAsString($$1, "group", "");
        CookingBookCategory $$3 = CookingBookCategory.CODEC.byName(GsonHelper.getAsString($$1, "category", null), CookingBookCategory.MISC);
        JsonArray $$4 = GsonHelper.isArrayNode($$1, "ingredient") ? GsonHelper.getAsJsonArray($$1, "ingredient") : GsonHelper.getAsJsonObject($$1, "ingredient");
        Ingredient $$5 = Ingredient.fromJson((JsonElement)$$4);
        String $$6 = GsonHelper.getAsString($$1, "result");
        ResourceLocation $$7 = new ResourceLocation($$6);
        ItemStack $$8 = new ItemStack((ItemLike)BuiltInRegistries.ITEM.getOptional($$7).orElseThrow(() -> new IllegalStateException("Item: " + $$6 + " does not exist")));
        float $$9 = GsonHelper.getAsFloat($$1, "experience", 0.0f);
        int $$10 = GsonHelper.getAsInt($$1, "cookingtime", this.defaultCookingTime);
        return this.factory.create($$0, $$2, $$3, $$5, $$8, $$9, $$10);
    }

    @Override
    public T fromNetwork(ResourceLocation $$0, FriendlyByteBuf $$1) {
        String $$2 = $$1.readUtf();
        CookingBookCategory $$3 = $$1.readEnum(CookingBookCategory.class);
        Ingredient $$4 = Ingredient.fromNetwork($$1);
        ItemStack $$5 = $$1.readItem();
        float $$6 = $$1.readFloat();
        int $$7 = $$1.readVarInt();
        return this.factory.create($$0, $$2, $$3, $$4, $$5, $$6, $$7);
    }

    @Override
    public void toNetwork(FriendlyByteBuf $$0, T $$1) {
        $$0.writeUtf(((AbstractCookingRecipe)$$1).group);
        $$0.writeEnum(((AbstractCookingRecipe)$$1).category());
        ((AbstractCookingRecipe)$$1).ingredient.toNetwork($$0);
        $$0.writeItem(((AbstractCookingRecipe)$$1).result);
        $$0.writeFloat(((AbstractCookingRecipe)$$1).experience);
        $$0.writeVarInt(((AbstractCookingRecipe)$$1).cookingTime);
    }

    static interface CookieBaker<T extends AbstractCookingRecipe> {
        public T create(ResourceLocation var1, String var2, CookingBookCategory var3, Ingredient var4, ItemStack var5, float var6, int var7);
    }
}