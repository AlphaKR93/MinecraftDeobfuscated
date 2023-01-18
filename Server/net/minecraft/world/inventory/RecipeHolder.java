/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.util.Collection
 *  java.util.Collections
 *  javax.annotation.Nullable
 */
package net.minecraft.world.inventory;

import java.util.Collection;
import java.util.Collections;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

public interface RecipeHolder {
    public void setRecipeUsed(@Nullable Recipe<?> var1);

    @Nullable
    public Recipe<?> getRecipeUsed();

    default public void awardUsedRecipes(Player $$0) {
        Recipe<?> $$1 = this.getRecipeUsed();
        if ($$1 != null && !$$1.isSpecial()) {
            $$0.awardRecipes((Collection<Recipe<?>>)Collections.singleton($$1));
            this.setRecipeUsed(null);
        }
    }

    default public boolean setRecipeUsed(Level $$0, ServerPlayer $$1, Recipe<?> $$2) {
        if ($$2.isSpecial() || !$$0.getGameRules().getBoolean(GameRules.RULE_LIMITED_CRAFTING) || $$1.getRecipeBook().contains($$2)) {
            this.setRecipeUsed($$2);
            return true;
        }
        return false;
    }
}