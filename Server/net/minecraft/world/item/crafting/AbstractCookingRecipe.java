/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 */
package net.minecraft.world.item.crafting;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public abstract class AbstractCookingRecipe
implements Recipe<Container> {
    protected final RecipeType<?> type;
    protected final ResourceLocation id;
    private final CookingBookCategory category;
    protected final String group;
    protected final Ingredient ingredient;
    protected final ItemStack result;
    protected final float experience;
    protected final int cookingTime;

    public AbstractCookingRecipe(RecipeType<?> $$0, ResourceLocation $$1, String $$2, CookingBookCategory $$3, Ingredient $$4, ItemStack $$5, float $$6, int $$7) {
        this.type = $$0;
        this.category = $$3;
        this.id = $$1;
        this.group = $$2;
        this.ingredient = $$4;
        this.result = $$5;
        this.experience = $$6;
        this.cookingTime = $$7;
    }

    @Override
    public boolean matches(Container $$0, Level $$1) {
        return this.ingredient.test($$0.getItem(0));
    }

    @Override
    public ItemStack assemble(Container $$0, RegistryAccess $$1) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int $$0, int $$1) {
        return true;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> $$0 = NonNullList.create();
        $$0.add(this.ingredient);
        return $$0;
    }

    public float getExperience() {
        return this.experience;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess $$0) {
        return this.result;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    public int getCookingTime() {
        return this.cookingTime;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeType<?> getType() {
        return this.type;
    }

    public CookingBookCategory category() {
        return this.category;
    }
}