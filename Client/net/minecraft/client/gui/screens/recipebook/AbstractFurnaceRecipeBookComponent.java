/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Iterator
 *  java.util.List
 *  java.util.Set
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.screens.recipebook;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

public abstract class AbstractFurnaceRecipeBookComponent
extends RecipeBookComponent {
    @Nullable
    private Ingredient fuels;

    @Override
    protected void initFilterButtonTextures() {
        this.filterButton.initTextureValues(152, 182, 28, 18, RECIPE_BOOK_LOCATION);
    }

    @Override
    public void slotClicked(@Nullable Slot $$0) {
        super.slotClicked($$0);
        if ($$0 != null && $$0.index < this.menu.getSize()) {
            this.ghostRecipe.clear();
        }
    }

    @Override
    public void setupGhostRecipe(Recipe<?> $$0, List<Slot> $$1) {
        ItemStack $$2 = $$0.getResultItem();
        this.ghostRecipe.setRecipe($$0);
        this.ghostRecipe.addIngredient(Ingredient.of($$2), ((Slot)$$1.get((int)2)).x, ((Slot)$$1.get((int)2)).y);
        NonNullList<Ingredient> $$3 = $$0.getIngredients();
        Slot $$4 = (Slot)$$1.get(1);
        if ($$4.getItem().isEmpty()) {
            if (this.fuels == null) {
                this.fuels = Ingredient.of((Stream<ItemStack>)this.getFuelItems().stream().map(ItemStack::new));
            }
            this.ghostRecipe.addIngredient(this.fuels, $$4.x, $$4.y);
        }
        Iterator $$5 = $$3.iterator();
        for (int $$6 = 0; $$6 < 2; ++$$6) {
            if (!$$5.hasNext()) {
                return;
            }
            Ingredient $$7 = (Ingredient)$$5.next();
            if ($$7.isEmpty()) continue;
            Slot $$8 = (Slot)$$1.get($$6);
            this.ghostRecipe.addIngredient($$7, $$8.x, $$8.y);
        }
    }

    protected abstract Set<Item> getFuelItems();
}