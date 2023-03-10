/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  java.lang.Object
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.Set
 */
package net.minecraft.client.gui.screens.recipebook;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.core.RegistryAccess;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

public class RecipeCollection {
    private final RegistryAccess registryAccess;
    private final List<Recipe<?>> recipes;
    private final boolean singleResultItem;
    private final Set<Recipe<?>> craftable = Sets.newHashSet();
    private final Set<Recipe<?>> fitsDimensions = Sets.newHashSet();
    private final Set<Recipe<?>> known = Sets.newHashSet();

    public RecipeCollection(RegistryAccess $$0, List<Recipe<?>> $$1) {
        this.registryAccess = $$0;
        this.recipes = ImmutableList.copyOf($$1);
        this.singleResultItem = $$1.size() <= 1 ? true : RecipeCollection.allRecipesHaveSameResult($$0, $$1);
    }

    private static boolean allRecipesHaveSameResult(RegistryAccess $$0, List<Recipe<?>> $$1) {
        int $$2 = $$1.size();
        ItemStack $$3 = ((Recipe)$$1.get(0)).getResultItem($$0);
        for (int $$4 = 1; $$4 < $$2; ++$$4) {
            ItemStack $$5 = ((Recipe)$$1.get($$4)).getResultItem($$0);
            if (ItemStack.isSame($$3, $$5) && ItemStack.tagMatches($$3, $$5)) continue;
            return false;
        }
        return true;
    }

    public RegistryAccess registryAccess() {
        return this.registryAccess;
    }

    public boolean hasKnownRecipes() {
        return !this.known.isEmpty();
    }

    public void updateKnownRecipes(RecipeBook $$0) {
        for (Recipe $$1 : this.recipes) {
            if (!$$0.contains($$1)) continue;
            this.known.add((Object)$$1);
        }
    }

    public void canCraft(StackedContents $$0, int $$1, int $$2, RecipeBook $$3) {
        for (Recipe $$4 : this.recipes) {
            boolean $$5;
            boolean bl = $$5 = $$4.canCraftInDimensions($$1, $$2) && $$3.contains($$4);
            if ($$5) {
                this.fitsDimensions.add((Object)$$4);
            } else {
                this.fitsDimensions.remove((Object)$$4);
            }
            if ($$5 && $$0.canCraft($$4, null)) {
                this.craftable.add((Object)$$4);
                continue;
            }
            this.craftable.remove((Object)$$4);
        }
    }

    public boolean isCraftable(Recipe<?> $$0) {
        return this.craftable.contains($$0);
    }

    public boolean hasCraftable() {
        return !this.craftable.isEmpty();
    }

    public boolean hasFitting() {
        return !this.fitsDimensions.isEmpty();
    }

    public List<Recipe<?>> getRecipes() {
        return this.recipes;
    }

    public List<Recipe<?>> getRecipes(boolean $$0) {
        ArrayList $$1 = Lists.newArrayList();
        Set<Recipe<?>> $$2 = $$0 ? this.craftable : this.fitsDimensions;
        for (Recipe $$3 : this.recipes) {
            if (!$$2.contains((Object)$$3)) continue;
            $$1.add((Object)$$3);
        }
        return $$1;
    }

    public List<Recipe<?>> getDisplayRecipes(boolean $$0) {
        ArrayList $$1 = Lists.newArrayList();
        for (Recipe $$2 : this.recipes) {
            if (!this.fitsDimensions.contains((Object)$$2) || this.craftable.contains((Object)$$2) != $$0) continue;
            $$1.add((Object)$$2);
        }
        return $$1;
    }

    public boolean hasSingleResultItem() {
        return this.singleResultItem;
    }
}