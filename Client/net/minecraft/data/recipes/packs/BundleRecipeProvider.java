/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Character
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Consumer
 */
package net.minecraft.data.recipes.packs;

import java.util.function.Consumer;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;

public class BundleRecipeProvider
extends RecipeProvider {
    public BundleRecipeProvider(PackOutput $$0) {
        super($$0);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> $$0) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Items.BUNDLE).define(Character.valueOf((char)'#'), Items.RABBIT_HIDE).define(Character.valueOf((char)'-'), Items.STRING).pattern("-#-").pattern("# #").pattern("###").unlockedBy("has_string", BundleRecipeProvider.has(Items.STRING)).save($$0);
    }
}