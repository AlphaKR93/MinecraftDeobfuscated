/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  java.lang.FunctionalInterface
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.item.crafting;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class SimpleCraftingRecipeSerializer<T extends CraftingRecipe>
implements RecipeSerializer<T> {
    private final Factory<T> constructor;

    public SimpleCraftingRecipeSerializer(Factory<T> $$0) {
        this.constructor = $$0;
    }

    @Override
    public T fromJson(ResourceLocation $$0, JsonObject $$1) {
        CraftingBookCategory $$2 = CraftingBookCategory.CODEC.byName(GsonHelper.getAsString($$1, "category", null), CraftingBookCategory.MISC);
        return this.constructor.create($$0, $$2);
    }

    @Override
    public T fromNetwork(ResourceLocation $$0, FriendlyByteBuf $$1) {
        CraftingBookCategory $$2 = $$1.readEnum(CraftingBookCategory.class);
        return this.constructor.create($$0, $$2);
    }

    @Override
    public void toNetwork(FriendlyByteBuf $$0, T $$1) {
        $$0.writeEnum($$1.category());
    }

    @FunctionalInterface
    public static interface Factory<T extends CraftingRecipe> {
        public T create(ResourceLocation var1, CraftingBookCategory var2);
    }
}