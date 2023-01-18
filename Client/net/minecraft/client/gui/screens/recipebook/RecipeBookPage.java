/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.String
 *  java.util.List
 *  java.util.function.Consumer
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.screens.recipebook;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.gui.screens.recipebook.OverlayRecipeComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeButton;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.gui.screens.recipebook.RecipeShownListener;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.item.crafting.Recipe;

public class RecipeBookPage {
    public static final int ITEMS_PER_PAGE = 20;
    private final List<RecipeButton> buttons = Lists.newArrayListWithCapacity((int)20);
    @Nullable
    private RecipeButton hoveredButton;
    private final OverlayRecipeComponent overlay = new OverlayRecipeComponent();
    private Minecraft minecraft;
    private final List<RecipeShownListener> showListeners = Lists.newArrayList();
    private List<RecipeCollection> recipeCollections = ImmutableList.of();
    private StateSwitchingButton forwardButton;
    private StateSwitchingButton backButton;
    private int totalPages;
    private int currentPage;
    private RecipeBook recipeBook;
    @Nullable
    private Recipe<?> lastClickedRecipe;
    @Nullable
    private RecipeCollection lastClickedRecipeCollection;

    public RecipeBookPage() {
        for (int $$0 = 0; $$0 < 20; ++$$0) {
            this.buttons.add((Object)new RecipeButton());
        }
    }

    public void init(Minecraft $$0, int $$1, int $$2) {
        this.minecraft = $$0;
        this.recipeBook = $$0.player.getRecipeBook();
        for (int $$3 = 0; $$3 < this.buttons.size(); ++$$3) {
            ((RecipeButton)this.buttons.get($$3)).setPosition($$1 + 11 + 25 * ($$3 % 5), $$2 + 31 + 25 * ($$3 / 5));
        }
        this.forwardButton = new StateSwitchingButton($$1 + 93, $$2 + 137, 12, 17, false);
        this.forwardButton.initTextureValues(1, 208, 13, 18, RecipeBookComponent.RECIPE_BOOK_LOCATION);
        this.backButton = new StateSwitchingButton($$1 + 38, $$2 + 137, 12, 17, true);
        this.backButton.initTextureValues(1, 208, 13, 18, RecipeBookComponent.RECIPE_BOOK_LOCATION);
    }

    public void addListener(RecipeBookComponent $$0) {
        this.showListeners.remove((Object)$$0);
        this.showListeners.add((Object)$$0);
    }

    public void updateCollections(List<RecipeCollection> $$0, boolean $$1) {
        this.recipeCollections = $$0;
        this.totalPages = (int)Math.ceil((double)((double)$$0.size() / 20.0));
        if (this.totalPages <= this.currentPage || $$1) {
            this.currentPage = 0;
        }
        this.updateButtonsForPage();
    }

    private void updateButtonsForPage() {
        int $$0 = 20 * this.currentPage;
        for (int $$1 = 0; $$1 < this.buttons.size(); ++$$1) {
            RecipeButton $$2 = (RecipeButton)this.buttons.get($$1);
            if ($$0 + $$1 < this.recipeCollections.size()) {
                RecipeCollection $$3 = (RecipeCollection)this.recipeCollections.get($$0 + $$1);
                $$2.init($$3, this);
                $$2.visible = true;
                continue;
            }
            $$2.visible = false;
        }
        this.updateArrowButtons();
    }

    private void updateArrowButtons() {
        this.forwardButton.visible = this.totalPages > 1 && this.currentPage < this.totalPages - 1;
        this.backButton.visible = this.totalPages > 1 && this.currentPage > 0;
    }

    public void render(PoseStack $$0, int $$1, int $$2, int $$3, int $$4, float $$5) {
        if (this.totalPages > 1) {
            String $$6 = this.currentPage + 1 + "/" + this.totalPages;
            int $$7 = this.minecraft.font.width($$6);
            this.minecraft.font.draw($$0, $$6, (float)($$1 - $$7 / 2 + 73), (float)($$2 + 141), -1);
        }
        this.hoveredButton = null;
        for (RecipeButton $$8 : this.buttons) {
            $$8.render($$0, $$3, $$4, $$5);
            if (!$$8.visible || !$$8.isHoveredOrFocused()) continue;
            this.hoveredButton = $$8;
        }
        this.backButton.render($$0, $$3, $$4, $$5);
        this.forwardButton.render($$0, $$3, $$4, $$5);
        this.overlay.render($$0, $$3, $$4, $$5);
    }

    public void renderTooltip(PoseStack $$0, int $$1, int $$2) {
        if (this.minecraft.screen != null && this.hoveredButton != null && !this.overlay.isVisible()) {
            this.minecraft.screen.renderComponentTooltip($$0, this.hoveredButton.getTooltipText(this.minecraft.screen), $$1, $$2);
        }
    }

    @Nullable
    public Recipe<?> getLastClickedRecipe() {
        return this.lastClickedRecipe;
    }

    @Nullable
    public RecipeCollection getLastClickedRecipeCollection() {
        return this.lastClickedRecipeCollection;
    }

    public void setInvisible() {
        this.overlay.setVisible(false);
    }

    public boolean mouseClicked(double $$0, double $$1, int $$2, int $$3, int $$4, int $$5, int $$6) {
        this.lastClickedRecipe = null;
        this.lastClickedRecipeCollection = null;
        if (this.overlay.isVisible()) {
            if (this.overlay.mouseClicked($$0, $$1, $$2)) {
                this.lastClickedRecipe = this.overlay.getLastRecipeClicked();
                this.lastClickedRecipeCollection = this.overlay.getRecipeCollection();
            } else {
                this.overlay.setVisible(false);
            }
            return true;
        }
        if (this.forwardButton.mouseClicked($$0, $$1, $$2)) {
            ++this.currentPage;
            this.updateButtonsForPage();
            return true;
        }
        if (this.backButton.mouseClicked($$0, $$1, $$2)) {
            --this.currentPage;
            this.updateButtonsForPage();
            return true;
        }
        for (RecipeButton $$7 : this.buttons) {
            if (!$$7.mouseClicked($$0, $$1, $$2)) continue;
            if ($$2 == 0) {
                this.lastClickedRecipe = $$7.getRecipe();
                this.lastClickedRecipeCollection = $$7.getCollection();
            } else if ($$2 == 1 && !this.overlay.isVisible() && !$$7.isOnlyOption()) {
                this.overlay.init(this.minecraft, $$7.getCollection(), $$7.getX(), $$7.getY(), $$3 + $$5 / 2, $$4 + 13 + $$6 / 2, $$7.getWidth());
            }
            return true;
        }
        return false;
    }

    public void recipesShown(List<Recipe<?>> $$0) {
        for (RecipeShownListener $$1 : this.showListeners) {
            $$1.recipesShown($$0);
        }
    }

    public Minecraft getMinecraft() {
        return this.minecraft;
    }

    public RecipeBook getRecipeBook() {
        return this.recipeBook;
    }

    protected void listButtons(Consumer<AbstractWidget> $$0) {
        $$0.accept((Object)this.forwardButton);
        $$0.accept((Object)this.backButton);
        this.buttons.forEach($$0);
    }
}