/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.function.Supplier
 */
package net.minecraft.client.gui.screens.recipebook;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.RecipeBook;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

public class RecipeButton
extends AbstractWidget {
    private static final ResourceLocation RECIPE_BOOK_LOCATION = new ResourceLocation("textures/gui/recipe_book.png");
    private static final float ANIMATION_TIME = 15.0f;
    private static final int BACKGROUND_SIZE = 25;
    public static final int TICKS_TO_SWAP = 30;
    private static final Component MORE_RECIPES_TOOLTIP = Component.translatable("gui.recipebook.moreRecipes");
    private RecipeBookMenu<?> menu;
    private RecipeBook book;
    private RecipeCollection collection;
    private float time;
    private float animationTime;
    private int currentIndex;

    public RecipeButton() {
        super(0, 0, 25, 25, CommonComponents.EMPTY);
    }

    public void init(RecipeCollection $$0, RecipeBookPage $$1) {
        this.collection = $$0;
        this.menu = (RecipeBookMenu)$$1.getMinecraft().player.containerMenu;
        this.book = $$1.getRecipeBook();
        List<Recipe<?>> $$2 = $$0.getRecipes(this.book.isFiltering(this.menu));
        for (Recipe $$3 : $$2) {
            if (!this.book.willHighlight($$3)) continue;
            $$1.recipesShown($$2);
            this.animationTime = 15.0f;
            break;
        }
    }

    public RecipeCollection getCollection() {
        return this.collection;
    }

    @Override
    public void renderWidget(PoseStack $$0, int $$1, int $$2, float $$3) {
        if (!Screen.hasControlDown()) {
            this.time += $$3;
        }
        Minecraft $$4 = Minecraft.getInstance();
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
        RenderSystem.setShaderTexture(0, RECIPE_BOOK_LOCATION);
        int $$5 = 29;
        if (!this.collection.hasCraftable()) {
            $$5 += 25;
        }
        int $$6 = 206;
        if (this.collection.getRecipes(this.book.isFiltering(this.menu)).size() > 1) {
            $$6 += 25;
        }
        boolean $$7 = this.animationTime > 0.0f;
        PoseStack $$8 = RenderSystem.getModelViewStack();
        if ($$7) {
            float $$9 = 1.0f + 0.1f * (float)Math.sin((double)(this.animationTime / 15.0f * (float)Math.PI));
            $$8.pushPose();
            $$8.translate(this.getX() + 8, this.getY() + 12, 0.0f);
            $$8.scale($$9, $$9, 1.0f);
            $$8.translate(-(this.getX() + 8), -(this.getY() + 12), 0.0f);
            RenderSystem.applyModelViewMatrix();
            this.animationTime -= $$3;
        }
        this.blit($$0, this.getX(), this.getY(), $$5, $$6, this.width, this.height);
        List<Recipe<?>> $$10 = this.getOrderedRecipes();
        this.currentIndex = Mth.floor(this.time / 30.0f) % $$10.size();
        ItemStack $$11 = ((Recipe)$$10.get(this.currentIndex)).getResultItem(this.collection.registryAccess());
        int $$12 = 4;
        if (this.collection.hasSingleResultItem() && this.getOrderedRecipes().size() > 1) {
            $$4.getItemRenderer().renderAndDecorateItem($$11, this.getX() + $$12 + 1, this.getY() + $$12 + 1, 0, 10);
            --$$12;
        }
        $$4.getItemRenderer().renderAndDecorateFakeItem($$11, this.getX() + $$12, this.getY() + $$12);
        if ($$7) {
            $$8.popPose();
            RenderSystem.applyModelViewMatrix();
        }
    }

    private List<Recipe<?>> getOrderedRecipes() {
        List<Recipe<?>> $$0 = this.collection.getDisplayRecipes(true);
        if (!this.book.isFiltering(this.menu)) {
            $$0.addAll(this.collection.getDisplayRecipes(false));
        }
        return $$0;
    }

    public boolean isOnlyOption() {
        return this.getOrderedRecipes().size() == 1;
    }

    public Recipe<?> getRecipe() {
        List<Recipe<?>> $$0 = this.getOrderedRecipes();
        return (Recipe)$$0.get(this.currentIndex);
    }

    public List<Component> getTooltipText(Screen $$0) {
        ItemStack $$1 = ((Recipe)this.getOrderedRecipes().get(this.currentIndex)).getResultItem(this.collection.registryAccess());
        ArrayList $$2 = Lists.newArrayList($$0.getTooltipFromItem($$1));
        if (this.collection.getRecipes(this.book.isFiltering(this.menu)).size() > 1) {
            $$2.add((Object)MORE_RECIPES_TOOLTIP);
        }
        return $$2;
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput $$0) {
        ItemStack $$1 = ((Recipe)this.getOrderedRecipes().get(this.currentIndex)).getResultItem(this.collection.registryAccess());
        $$0.add(NarratedElementType.TITLE, (Component)Component.translatable("narration.recipe", $$1.getHoverName()));
        if (this.collection.getRecipes(this.book.isFiltering(this.menu)).size() > 1) {
            $$0.add(NarratedElementType.USAGE, Component.translatable("narration.button.usage.hovered"), Component.translatable("narration.recipe.usage.more"));
        } else {
            $$0.add(NarratedElementType.USAGE, (Component)Component.translatable("narration.button.usage.hovered"));
        }
    }

    @Override
    public int getWidth() {
        return 25;
    }

    @Override
    protected boolean isValidClickButton(int $$0) {
        return $$0 == 0 || $$0 == 1;
    }
}