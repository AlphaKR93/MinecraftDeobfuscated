/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.client.gui.screens.recipebook;

import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;

public interface RecipeUpdateListener {
    public void recipesUpdated();

    public RecipeBookComponent getRecipeBookComponent();
}