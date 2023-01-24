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
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.level.Level;

public class SmithingTransformRecipe
implements SmithingRecipe {
    private final ResourceLocation id;
    final Ingredient template;
    final Ingredient base;
    final Ingredient addition;
    final ItemStack result;

    public SmithingTransformRecipe(ResourceLocation $$0, Ingredient $$1, Ingredient $$2, Ingredient $$3, ItemStack $$4) {
        this.id = $$0;
        this.template = $$1;
        this.base = $$2;
        this.addition = $$3;
        this.result = $$4;
    }

    @Override
    public boolean matches(Container $$0, Level $$1) {
        return this.template.test($$0.getItem(0)) && this.base.test($$0.getItem(1)) && this.addition.test($$0.getItem(2));
    }

    @Override
    public ItemStack assemble(Container $$0, RegistryAccess $$1) {
        ItemStack $$2 = this.result.copy();
        CompoundTag $$3 = $$0.getItem(1).getTag();
        if ($$3 != null) {
            $$2.setTag($$3.copy());
        }
        return $$2;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess $$0) {
        return this.result;
    }

    @Override
    public boolean isTemplateIngredient(ItemStack $$0) {
        return this.template.test($$0);
    }

    @Override
    public boolean isBaseIngredient(ItemStack $$0) {
        return this.base.test($$0);
    }

    @Override
    public boolean isAdditionIngredient(ItemStack $$0) {
        return this.addition.test($$0);
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.SMITHING_TRANSFORM;
    }

    @Override
    public boolean isIncomplete() {
        return Stream.of((Object[])new Ingredient[]{this.template, this.base, this.addition}).anyMatch(Ingredient::isEmpty);
    }

    public static class Serializer
    implements RecipeSerializer<SmithingTransformRecipe> {
        @Override
        public SmithingTransformRecipe fromJson(ResourceLocation $$0, JsonObject $$1) {
            Ingredient $$2 = Ingredient.fromJson((JsonElement)GsonHelper.getAsJsonObject($$1, "template"));
            Ingredient $$3 = Ingredient.fromJson((JsonElement)GsonHelper.getAsJsonObject($$1, "base"));
            Ingredient $$4 = Ingredient.fromJson((JsonElement)GsonHelper.getAsJsonObject($$1, "addition"));
            ItemStack $$5 = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject($$1, "result"));
            return new SmithingTransformRecipe($$0, $$2, $$3, $$4, $$5);
        }

        @Override
        public SmithingTransformRecipe fromNetwork(ResourceLocation $$0, FriendlyByteBuf $$1) {
            Ingredient $$2 = Ingredient.fromNetwork($$1);
            Ingredient $$3 = Ingredient.fromNetwork($$1);
            Ingredient $$4 = Ingredient.fromNetwork($$1);
            ItemStack $$5 = $$1.readItem();
            return new SmithingTransformRecipe($$0, $$2, $$3, $$4, $$5);
        }

        @Override
        public void toNetwork(FriendlyByteBuf $$0, SmithingTransformRecipe $$1) {
            $$1.template.toNetwork($$0);
            $$1.base.toNetwork($$0);
            $$1.addition.toNetwork($$0);
            $$0.writeItem($$1.result);
        }
    }
}