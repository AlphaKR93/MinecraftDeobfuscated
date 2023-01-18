/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.stream.Stream
 */
package net.minecraft.world.item.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class UpgradeRecipe
implements Recipe<Container> {
    final Ingredient base;
    final Ingredient addition;
    final ItemStack result;
    private final ResourceLocation id;

    public UpgradeRecipe(ResourceLocation $$0, Ingredient $$1, Ingredient $$2, ItemStack $$3) {
        this.id = $$0;
        this.base = $$1;
        this.addition = $$2;
        this.result = $$3;
    }

    @Override
    public boolean matches(Container $$0, Level $$1) {
        return this.base.test($$0.getItem(0)) && this.addition.test($$0.getItem(1));
    }

    @Override
    public ItemStack assemble(Container $$0) {
        ItemStack $$1 = this.result.copy();
        CompoundTag $$2 = $$0.getItem(0).getTag();
        if ($$2 != null) {
            $$1.setTag($$2.copy());
        }
        return $$1;
    }

    @Override
    public boolean canCraftInDimensions(int $$0, int $$1) {
        return $$0 * $$1 >= 2;
    }

    @Override
    public ItemStack getResultItem() {
        return this.result;
    }

    public boolean isAdditionIngredient(ItemStack $$0) {
        return this.addition.test($$0);
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(Blocks.SMITHING_TABLE);
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.SMITHING;
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeType.SMITHING;
    }

    @Override
    public boolean isIncomplete() {
        return Stream.of((Object[])new Ingredient[]{this.base, this.addition}).anyMatch($$0 -> $$0.getItems().length == 0);
    }

    public static class Serializer
    implements RecipeSerializer<UpgradeRecipe> {
        @Override
        public UpgradeRecipe fromJson(ResourceLocation $$0, JsonObject $$1) {
            Ingredient $$2 = Ingredient.fromJson((JsonElement)GsonHelper.getAsJsonObject($$1, "base"));
            Ingredient $$3 = Ingredient.fromJson((JsonElement)GsonHelper.getAsJsonObject($$1, "addition"));
            ItemStack $$4 = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject($$1, "result"));
            return new UpgradeRecipe($$0, $$2, $$3, $$4);
        }

        @Override
        public UpgradeRecipe fromNetwork(ResourceLocation $$0, FriendlyByteBuf $$1) {
            Ingredient $$2 = Ingredient.fromNetwork($$1);
            Ingredient $$3 = Ingredient.fromNetwork($$1);
            ItemStack $$4 = $$1.readItem();
            return new UpgradeRecipe($$0, $$2, $$3, $$4);
        }

        @Override
        public void toNetwork(FriendlyByteBuf $$0, UpgradeRecipe $$1) {
            $$1.base.toNetwork($$0);
            $$1.addition.toNetwork($$0);
            $$0.writeItem($$1.result);
        }
    }
}