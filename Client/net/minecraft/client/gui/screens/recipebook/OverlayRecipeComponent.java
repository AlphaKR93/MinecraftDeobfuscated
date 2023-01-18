/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Collections
 *  java.util.Iterator
 *  java.util.List
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.screens.recipebook;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.recipebook.PlaceRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

public class OverlayRecipeComponent
extends GuiComponent
implements Renderable,
GuiEventListener {
    static final ResourceLocation RECIPE_BOOK_LOCATION = new ResourceLocation("textures/gui/recipe_book.png");
    private static final int MAX_ROW = 4;
    private static final int MAX_ROW_LARGE = 5;
    private static final float ITEM_RENDER_SCALE = 0.375f;
    private final List<OverlayRecipeButton> recipeButtons = Lists.newArrayList();
    private boolean isVisible;
    private int x;
    private int y;
    Minecraft minecraft;
    private RecipeCollection collection;
    @Nullable
    private Recipe<?> lastRecipeClicked;
    float time;
    boolean isFurnaceMenu;

    public void init(Minecraft $$0, RecipeCollection $$1, int $$2, int $$3, int $$4, int $$5, float $$6) {
        float $$20;
        float $$19;
        float $$18;
        float $$17;
        float $$16;
        this.minecraft = $$0;
        this.collection = $$1;
        if ($$0.player.containerMenu instanceof AbstractFurnaceMenu) {
            this.isFurnaceMenu = true;
        }
        boolean $$7 = $$0.player.getRecipeBook().isFiltering((RecipeBookMenu)$$0.player.containerMenu);
        List<Recipe<?>> $$8 = $$1.getDisplayRecipes(true);
        List<Recipe<?>> $$9 = $$7 ? Collections.emptyList() : $$1.getDisplayRecipes(false);
        int $$10 = $$8.size();
        int $$11 = $$10 + $$9.size();
        int $$12 = $$11 <= 16 ? 4 : 5;
        int $$13 = (int)Math.ceil((double)((float)$$11 / (float)$$12));
        this.x = $$2;
        this.y = $$3;
        int $$14 = 25;
        float $$15 = this.x + Math.min((int)$$11, (int)$$12) * 25;
        if ($$15 > ($$16 = (float)($$4 + 50))) {
            this.x = (int)((float)this.x - $$6 * (float)((int)(($$15 - $$16) / $$6)));
        }
        if (($$17 = (float)(this.y + $$13 * 25)) > ($$18 = (float)($$5 + 50))) {
            this.y = (int)((float)this.y - $$6 * (float)Mth.ceil(($$17 - $$18) / $$6));
        }
        if (($$19 = (float)this.y) < ($$20 = (float)($$5 - 100))) {
            this.y = (int)((float)this.y - $$6 * (float)Mth.ceil(($$19 - $$20) / $$6));
        }
        this.isVisible = true;
        this.recipeButtons.clear();
        for (int $$21 = 0; $$21 < $$11; ++$$21) {
            boolean $$22 = $$21 < $$10;
            Recipe $$23 = $$22 ? (Recipe)$$8.get($$21) : (Recipe)$$9.get($$21 - $$10);
            int $$24 = this.x + 4 + 25 * ($$21 % $$12);
            int $$25 = this.y + 5 + 25 * ($$21 / $$12);
            if (this.isFurnaceMenu) {
                this.recipeButtons.add((Object)new OverlaySmeltingRecipeButton($$24, $$25, $$23, $$22));
                continue;
            }
            this.recipeButtons.add((Object)new OverlayRecipeButton($$24, $$25, $$23, $$22));
        }
        this.lastRecipeClicked = null;
    }

    @Override
    public boolean changeFocus(boolean $$0) {
        return false;
    }

    public RecipeCollection getRecipeCollection() {
        return this.collection;
    }

    @Nullable
    public Recipe<?> getLastRecipeClicked() {
        return this.lastRecipeClicked;
    }

    @Override
    public boolean mouseClicked(double $$0, double $$1, int $$2) {
        if ($$2 != 0) {
            return false;
        }
        for (OverlayRecipeButton $$3 : this.recipeButtons) {
            if (!$$3.mouseClicked($$0, $$1, $$2)) continue;
            this.lastRecipeClicked = $$3.recipe;
            return true;
        }
        return false;
    }

    @Override
    public boolean isMouseOver(double $$0, double $$1) {
        return false;
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        if (!this.isVisible) {
            return;
        }
        this.time += $$3;
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, RECIPE_BOOK_LOCATION);
        $$0.pushPose();
        $$0.translate(0.0f, 0.0f, 170.0f);
        int $$4 = this.recipeButtons.size() <= 16 ? 4 : 5;
        int $$5 = Math.min((int)this.recipeButtons.size(), (int)$$4);
        int $$6 = Mth.ceil((float)this.recipeButtons.size() / (float)$$4);
        int $$7 = 24;
        int $$8 = 4;
        int $$9 = 82;
        int $$10 = 208;
        this.nineInchSprite($$0, $$5, $$6, 24, 4, 82, 208);
        RenderSystem.disableBlend();
        for (OverlayRecipeButton $$11 : this.recipeButtons) {
            $$11.render($$0, $$1, $$2, $$3);
        }
        $$0.popPose();
    }

    private void nineInchSprite(PoseStack $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6) {
        this.blit($$0, this.x, this.y, $$5, $$6, $$4, $$4);
        this.blit($$0, this.x + $$4 * 2 + $$1 * $$3, this.y, $$5 + $$3 + $$4, $$6, $$4, $$4);
        this.blit($$0, this.x, this.y + $$4 * 2 + $$2 * $$3, $$5, $$6 + $$3 + $$4, $$4, $$4);
        this.blit($$0, this.x + $$4 * 2 + $$1 * $$3, this.y + $$4 * 2 + $$2 * $$3, $$5 + $$3 + $$4, $$6 + $$3 + $$4, $$4, $$4);
        for (int $$7 = 0; $$7 < $$1; ++$$7) {
            this.blit($$0, this.x + $$4 + $$7 * $$3, this.y, $$5 + $$4, $$6, $$3, $$4);
            this.blit($$0, this.x + $$4 + ($$7 + 1) * $$3, this.y, $$5 + $$4, $$6, $$4, $$4);
            for (int $$8 = 0; $$8 < $$2; ++$$8) {
                if ($$7 == 0) {
                    this.blit($$0, this.x, this.y + $$4 + $$8 * $$3, $$5, $$6 + $$4, $$4, $$3);
                    this.blit($$0, this.x, this.y + $$4 + ($$8 + 1) * $$3, $$5, $$6 + $$4, $$4, $$4);
                }
                this.blit($$0, this.x + $$4 + $$7 * $$3, this.y + $$4 + $$8 * $$3, $$5 + $$4, $$6 + $$4, $$3, $$3);
                this.blit($$0, this.x + $$4 + ($$7 + 1) * $$3, this.y + $$4 + $$8 * $$3, $$5 + $$4, $$6 + $$4, $$4, $$3);
                this.blit($$0, this.x + $$4 + $$7 * $$3, this.y + $$4 + ($$8 + 1) * $$3, $$5 + $$4, $$6 + $$4, $$3, $$4);
                this.blit($$0, this.x + $$4 + ($$7 + 1) * $$3 - 1, this.y + $$4 + ($$8 + 1) * $$3 - 1, $$5 + $$4, $$6 + $$4, $$4 + 1, $$4 + 1);
                if ($$7 != $$1 - 1) continue;
                this.blit($$0, this.x + $$4 * 2 + $$1 * $$3, this.y + $$4 + $$8 * $$3, $$5 + $$3 + $$4, $$6 + $$4, $$4, $$3);
                this.blit($$0, this.x + $$4 * 2 + $$1 * $$3, this.y + $$4 + ($$8 + 1) * $$3, $$5 + $$3 + $$4, $$6 + $$4, $$4, $$4);
            }
            this.blit($$0, this.x + $$4 + $$7 * $$3, this.y + $$4 * 2 + $$2 * $$3, $$5 + $$4, $$6 + $$3 + $$4, $$3, $$4);
            this.blit($$0, this.x + $$4 + ($$7 + 1) * $$3, this.y + $$4 * 2 + $$2 * $$3, $$5 + $$4, $$6 + $$3 + $$4, $$4, $$4);
        }
    }

    public void setVisible(boolean $$0) {
        this.isVisible = $$0;
    }

    public boolean isVisible() {
        return this.isVisible;
    }

    class OverlaySmeltingRecipeButton
    extends OverlayRecipeButton {
        public OverlaySmeltingRecipeButton(int $$0, int $$1, Recipe<?> $$2, boolean $$3) {
            super($$0, $$1, $$2, $$3);
        }

        @Override
        protected void calculateIngredientsPositions(Recipe<?> $$0) {
            ItemStack[] $$1 = $$0.getIngredients().get(0).getItems();
            this.ingredientPos.add((Object)new OverlayRecipeButton.Pos(10, 10, $$1));
        }
    }

    class OverlayRecipeButton
    extends AbstractWidget
    implements PlaceRecipe<Ingredient> {
        final Recipe<?> recipe;
        private final boolean isCraftable;
        protected final List<Pos> ingredientPos;

        public OverlayRecipeButton(int $$0, int $$1, Recipe<?> $$2, boolean $$3) {
            super($$0, $$1, 200, 20, CommonComponents.EMPTY);
            this.ingredientPos = Lists.newArrayList();
            this.width = 24;
            this.height = 24;
            this.recipe = $$2;
            this.isCraftable = $$3;
            this.calculateIngredientsPositions($$2);
        }

        protected void calculateIngredientsPositions(Recipe<?> $$0) {
            this.placeRecipe(3, 3, -1, $$0, $$0.getIngredients().iterator(), 0);
        }

        @Override
        public void updateWidgetNarration(NarrationElementOutput $$0) {
            this.defaultButtonNarrationText($$0);
        }

        @Override
        public void addItemToSlot(Iterator<Ingredient> $$0, int $$1, int $$2, int $$3, int $$4) {
            ItemStack[] $$5 = ((Ingredient)$$0.next()).getItems();
            if ($$5.length != 0) {
                this.ingredientPos.add((Object)new Pos(3 + $$4 * 7, 3 + $$3 * 7, $$5));
            }
        }

        @Override
        public void renderButton(PoseStack $$0, int $$1, int $$2, float $$3) {
            int $$5;
            RenderSystem.setShaderTexture(0, RECIPE_BOOK_LOCATION);
            int $$4 = 152;
            if (!this.isCraftable) {
                $$4 += 26;
            }
            int n = $$5 = OverlayRecipeComponent.this.isFurnaceMenu ? 130 : 78;
            if (this.isHoveredOrFocused()) {
                $$5 += 26;
            }
            this.blit($$0, this.getX(), this.getY(), $$4, $$5, this.width, this.height);
            PoseStack $$6 = RenderSystem.getModelViewStack();
            $$6.pushPose();
            $$6.translate((double)(this.getX() + 2), (double)(this.getY() + 2), 150.0);
            for (Pos $$7 : this.ingredientPos) {
                $$6.pushPose();
                $$6.translate((double)$$7.x, (double)$$7.y, 0.0);
                $$6.scale(0.375f, 0.375f, 1.0f);
                $$6.translate(-8.0, -8.0, 0.0);
                RenderSystem.applyModelViewMatrix();
                OverlayRecipeComponent.this.minecraft.getItemRenderer().renderAndDecorateItem($$7.ingredients[Mth.floor(OverlayRecipeComponent.this.time / 30.0f) % $$7.ingredients.length], 0, 0);
                $$6.popPose();
            }
            $$6.popPose();
            RenderSystem.applyModelViewMatrix();
        }

        protected class Pos {
            public final ItemStack[] ingredients;
            public final int x;
            public final int y;

            public Pos(int $$1, int $$2, ItemStack[] $$3) {
                this.x = $$1;
                this.y = $$2;
                this.ingredients = $$3;
            }
        }
    }
}