/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.lang.Object
 *  java.util.List
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.screens.recipebook;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

public class GhostRecipe {
    @Nullable
    private Recipe<?> recipe;
    private final List<GhostIngredient> ingredients = Lists.newArrayList();
    float time;

    public void clear() {
        this.recipe = null;
        this.ingredients.clear();
        this.time = 0.0f;
    }

    public void addIngredient(Ingredient $$0, int $$1, int $$2) {
        this.ingredients.add((Object)new GhostIngredient($$0, $$1, $$2));
    }

    public GhostIngredient get(int $$0) {
        return (GhostIngredient)this.ingredients.get($$0);
    }

    public int size() {
        return this.ingredients.size();
    }

    @Nullable
    public Recipe<?> getRecipe() {
        return this.recipe;
    }

    public void setRecipe(Recipe<?> $$0) {
        this.recipe = $$0;
    }

    public void render(PoseStack $$0, Minecraft $$1, int $$2, int $$3, boolean $$4, float $$5) {
        if (!Screen.hasControlDown()) {
            this.time += $$5;
        }
        for (int $$6 = 0; $$6 < this.ingredients.size(); ++$$6) {
            GhostIngredient $$7 = (GhostIngredient)this.ingredients.get($$6);
            int $$8 = $$7.getX() + $$2;
            int $$9 = $$7.getY() + $$3;
            if ($$6 == 0 && $$4) {
                GuiComponent.fill($$0, $$8 - 4, $$9 - 4, $$8 + 20, $$9 + 20, 0x30FF0000);
            } else {
                GuiComponent.fill($$0, $$8, $$9, $$8 + 16, $$9 + 16, 0x30FF0000);
            }
            ItemStack $$10 = $$7.getItem();
            ItemRenderer $$11 = $$1.getItemRenderer();
            $$11.renderAndDecorateFakeItem($$10, $$8, $$9);
            RenderSystem.depthFunc(516);
            GuiComponent.fill($$0, $$8, $$9, $$8 + 16, $$9 + 16, 0x30FFFFFF);
            RenderSystem.depthFunc(515);
            if ($$6 != 0) continue;
            $$11.renderGuiItemDecorations($$1.font, $$10, $$8, $$9);
        }
    }

    public class GhostIngredient {
        private final Ingredient ingredient;
        private final int x;
        private final int y;

        public GhostIngredient(Ingredient $$1, int $$2, int $$3) {
            this.ingredient = $$1;
            this.x = $$2;
            this.y = $$3;
        }

        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }

        public ItemStack getItem() {
            ItemStack[] $$0 = this.ingredient.getItems();
            if ($$0.length == 0) {
                return ItemStack.EMPTY;
            }
            return $$0[Mth.floor(GhostRecipe.this.time / 30.0f) % $$0.length];
        }
    }
}