/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  java.lang.Object
 *  java.util.Set
 *  javax.annotation.Nullable
 */
package net.minecraft.stats;

import com.google.common.collect.Sets;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.RecipeBookSettings;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.crafting.Recipe;

public class RecipeBook {
    protected final Set<ResourceLocation> known = Sets.newHashSet();
    protected final Set<ResourceLocation> highlight = Sets.newHashSet();
    private final RecipeBookSettings bookSettings = new RecipeBookSettings();

    public void copyOverData(RecipeBook $$0) {
        this.known.clear();
        this.highlight.clear();
        this.bookSettings.replaceFrom($$0.bookSettings);
        this.known.addAll($$0.known);
        this.highlight.addAll($$0.highlight);
    }

    public void add(Recipe<?> $$0) {
        if (!$$0.isSpecial()) {
            this.add($$0.getId());
        }
    }

    protected void add(ResourceLocation $$0) {
        this.known.add((Object)$$0);
    }

    public boolean contains(@Nullable Recipe<?> $$0) {
        if ($$0 == null) {
            return false;
        }
        return this.known.contains((Object)$$0.getId());
    }

    public boolean contains(ResourceLocation $$0) {
        return this.known.contains((Object)$$0);
    }

    public void remove(Recipe<?> $$0) {
        this.remove($$0.getId());
    }

    protected void remove(ResourceLocation $$0) {
        this.known.remove((Object)$$0);
        this.highlight.remove((Object)$$0);
    }

    public boolean willHighlight(Recipe<?> $$0) {
        return this.highlight.contains((Object)$$0.getId());
    }

    public void removeHighlight(Recipe<?> $$0) {
        this.highlight.remove((Object)$$0.getId());
    }

    public void addHighlight(Recipe<?> $$0) {
        this.addHighlight($$0.getId());
    }

    protected void addHighlight(ResourceLocation $$0) {
        this.highlight.add((Object)$$0);
    }

    public boolean isOpen(RecipeBookType $$0) {
        return this.bookSettings.isOpen($$0);
    }

    public void setOpen(RecipeBookType $$0, boolean $$1) {
        this.bookSettings.setOpen($$0, $$1);
    }

    public boolean isFiltering(RecipeBookMenu<?> $$0) {
        return this.isFiltering($$0.getRecipeBookType());
    }

    public boolean isFiltering(RecipeBookType $$0) {
        return this.bookSettings.isFiltering($$0);
    }

    public void setFiltering(RecipeBookType $$0, boolean $$1) {
        this.bookSettings.setFiltering($$0, $$1);
    }

    public void setBookSettings(RecipeBookSettings $$0) {
        this.bookSettings.replaceFrom($$0);
    }

    public RecipeBookSettings getBookSettings() {
        return this.bookSettings.copy();
    }

    public void setBookSetting(RecipeBookType $$0, boolean $$1, boolean $$2) {
        this.bookSettings.setOpen($$0, $$1);
        this.bookSettings.setFiltering($$0, $$2);
    }
}