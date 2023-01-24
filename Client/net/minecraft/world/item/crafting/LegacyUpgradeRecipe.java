/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  java.lang.Deprecated
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

@Deprecated(forRemoval=true)
public class LegacyUpgradeRecipe
implements SmithingRecipe {
    final Ingredient base;
    final Ingredient addition;
    final ItemStack result;
    private final ResourceLocation id;

    public LegacyUpgradeRecipe(ResourceLocation $$0, Ingredient $$1, Ingredient $$2, ItemStack $$3) {
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
    public ItemStack assemble(Container $$0, RegistryAccess $$1) {
        ItemStack $$2 = this.result.copy();
        CompoundTag $$3 = $$0.getItem(0).getTag();
        if ($$3 != null) {
            $$2.setTag($$3.copy());
        }
        return $$2;
    }

    @Override
    public boolean canCraftInDimensions(int $$0, int $$1) {
        return $$0 * $$1 >= 2;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess $$0) {
        return this.result;
    }

    @Override
    public boolean isTemplateIngredient(ItemStack $$0) {
        return false;
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
        return RecipeSerializer.SMITHING;
    }

    @Override
    public boolean isIncomplete() {
        return Stream.of((Object[])new Ingredient[]{this.base, this.addition}).anyMatch($$0 -> $$0.getItems().length == 0);
    }

    public static class Serializer
    implements RecipeSerializer<LegacyUpgradeRecipe> {
        @Override
        public LegacyUpgradeRecipe fromJson(ResourceLocation $$0, JsonObject $$1) {
            Ingredient $$2 = Ingredient.fromJson((JsonElement)GsonHelper.getAsJsonObject($$1, "base"));
            Ingredient $$3 = Ingredient.fromJson((JsonElement)GsonHelper.getAsJsonObject($$1, "addition"));
            ItemStack $$4 = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject($$1, "result"));
            return new LegacyUpgradeRecipe($$0, $$2, $$3, $$4);
        }

        @Override
        public LegacyUpgradeRecipe fromNetwork(ResourceLocation $$0, FriendlyByteBuf $$1) {
            Ingredient $$2 = Ingredient.fromNetwork($$1);
            Ingredient $$3 = Ingredient.fromNetwork($$1);
            ItemStack $$4 = $$1.readItem();
            return new LegacyUpgradeRecipe($$0, $$2, $$3, $$4);
        }

        @Override
        public void toNetwork(FriendlyByteBuf $$0, LegacyUpgradeRecipe $$1) {
            $$1.base.toNetwork($$0);
            $$1.addition.toNetwork($$0);
            $$0.writeItem($$1.result);
        }
    }
}