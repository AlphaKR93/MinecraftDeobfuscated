/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Optional
 *  java.util.stream.Stream
 */
package net.minecraft.world.item.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.item.armortrim.TrimPatterns;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.level.Level;

public class SmithingTrimRecipe
implements SmithingRecipe {
    private final ResourceLocation id;
    final Ingredient template;
    final Ingredient base;
    final Ingredient addition;

    public SmithingTrimRecipe(ResourceLocation $$0, Ingredient $$1, Ingredient $$2, Ingredient $$3) {
        this.id = $$0;
        this.template = $$1;
        this.base = $$2;
        this.addition = $$3;
    }

    @Override
    public boolean matches(Container $$0, Level $$1) {
        return this.template.test($$0.getItem(0)) && this.base.test($$0.getItem(1)) && this.addition.test($$0.getItem(2));
    }

    @Override
    public ItemStack assemble(Container $$0, RegistryAccess $$1) {
        ItemStack $$2 = $$0.getItem(1);
        if (this.base.test($$2)) {
            Optional<Holder.Reference<TrimMaterial>> $$3 = TrimMaterials.getFromIngredient($$1, $$0.getItem(2));
            Optional<Holder.Reference<TrimPattern>> $$4 = TrimPatterns.getFromTemplate($$1, $$0.getItem(0));
            if ($$3.isPresent() && $$4.isPresent()) {
                Optional<ArmorTrim> $$5 = ArmorTrim.getTrim($$1, $$2);
                if ($$5.isPresent() && ((ArmorTrim)$$5.get()).hasPatternAndMaterial((Holder)$$4.get(), (Holder)$$3.get())) {
                    return ItemStack.EMPTY;
                }
                if (this.isArmorMaterialIncompatible($$2, (TrimMaterial)((Object)((Holder.Reference)$$3.get()).value()))) {
                    return ItemStack.EMPTY;
                }
                ItemStack $$6 = $$2.copy();
                $$6.setCount(1);
                ArmorTrim $$7 = new ArmorTrim((Holder)$$3.get(), (Holder)$$4.get());
                if (ArmorTrim.setTrim($$1, $$6, $$7)) {
                    return $$6;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess $$0) {
        Optional<Holder.Reference<TrimMaterial>> $$3;
        ItemStack $$1 = new ItemStack(Items.IRON_CHESTPLATE);
        Optional $$2 = $$0.registryOrThrow(Registries.TRIM_PATTERN).holders().findFirst();
        if ($$2.isPresent() && ($$3 = $$0.registryOrThrow(Registries.TRIM_MATERIAL).getHolder(TrimMaterials.REDSTONE)).isPresent()) {
            ArmorTrim $$4 = new ArmorTrim((Holder)$$3.get(), (Holder)$$2.get());
            ArmorTrim.setTrim($$0, $$1, $$4);
        }
        return $$1;
    }

    private boolean isArmorMaterialIncompatible(ItemStack $$0, TrimMaterial $$1) {
        ArmorItem $$2;
        Item item = $$0.getItem();
        return item instanceof ArmorItem && ($$2 = (ArmorItem)item).getMaterial() == $$1.incompatibleArmorMaterial().orElse(null);
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
        return RecipeSerializer.SMITHING_TRIM;
    }

    @Override
    public boolean isIncomplete() {
        return Stream.of((Object[])new Ingredient[]{this.template, this.base, this.addition}).anyMatch(Ingredient::isEmpty);
    }

    public static class Serializer
    implements RecipeSerializer<SmithingTrimRecipe> {
        @Override
        public SmithingTrimRecipe fromJson(ResourceLocation $$0, JsonObject $$1) {
            Ingredient $$2 = Ingredient.fromJson((JsonElement)GsonHelper.getAsJsonObject($$1, "template"));
            Ingredient $$3 = Ingredient.fromJson((JsonElement)GsonHelper.getAsJsonObject($$1, "base"));
            Ingredient $$4 = Ingredient.fromJson((JsonElement)GsonHelper.getAsJsonObject($$1, "addition"));
            return new SmithingTrimRecipe($$0, $$2, $$3, $$4);
        }

        @Override
        public SmithingTrimRecipe fromNetwork(ResourceLocation $$0, FriendlyByteBuf $$1) {
            Ingredient $$2 = Ingredient.fromNetwork($$1);
            Ingredient $$3 = Ingredient.fromNetwork($$1);
            Ingredient $$4 = Ingredient.fromNetwork($$1);
            return new SmithingTrimRecipe($$0, $$2, $$3, $$4);
        }

        @Override
        public void toNetwork(FriendlyByteBuf $$0, SmithingTrimRecipe $$1) {
            $$1.template.toNetwork($$0);
            $$1.base.toNetwork($$0);
            $$1.addition.toNetwork($$0);
        }
    }
}