/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Supplier
 */
package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Supplier;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.Slot;

public class CraftingScreen
extends AbstractContainerScreen<CraftingMenu>
implements RecipeUpdateListener {
    private static final ResourceLocation CRAFTING_TABLE_LOCATION = new ResourceLocation("textures/gui/container/crafting_table.png");
    private static final ResourceLocation RECIPE_BUTTON_LOCATION = new ResourceLocation("textures/gui/recipe_button.png");
    private final RecipeBookComponent recipeBookComponent = new RecipeBookComponent();
    private boolean widthTooNarrow;

    public CraftingScreen(CraftingMenu $$0, Inventory $$1, Component $$2) {
        super($$0, $$1, $$2);
    }

    @Override
    protected void init() {
        super.init();
        this.widthTooNarrow = this.width < 379;
        this.recipeBookComponent.init(this.width, this.height, this.minecraft, this.widthTooNarrow, (RecipeBookMenu)this.menu);
        this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
        this.addRenderableWidget(new ImageButton(this.leftPos + 5, this.height / 2 - 49, 20, 18, 0, 0, 19, RECIPE_BUTTON_LOCATION, $$0 -> {
            this.recipeBookComponent.toggleVisibility();
            this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
            ((ImageButton)$$0).setPosition(this.leftPos + 5, this.height / 2 - 49);
        }));
        this.addWidget(this.recipeBookComponent);
        this.setInitialFocus(this.recipeBookComponent);
        this.titleLabelX = 29;
    }

    @Override
    public void containerTick() {
        super.containerTick();
        this.recipeBookComponent.tick();
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderBackground($$0);
        if (this.recipeBookComponent.isVisible() && this.widthTooNarrow) {
            this.renderBg($$0, $$3, $$1, $$2);
            this.recipeBookComponent.render($$0, $$1, $$2, $$3);
        } else {
            this.recipeBookComponent.render($$0, $$1, $$2, $$3);
            super.render($$0, $$1, $$2, $$3);
            this.recipeBookComponent.renderGhostRecipe($$0, this.leftPos, this.topPos, true, $$3);
        }
        this.renderTooltip($$0, $$1, $$2);
        this.recipeBookComponent.renderTooltip($$0, this.leftPos, this.topPos, $$1, $$2);
    }

    @Override
    protected void renderBg(PoseStack $$0, float $$1, int $$2, int $$3) {
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
        RenderSystem.setShaderTexture(0, CRAFTING_TABLE_LOCATION);
        int $$4 = this.leftPos;
        int $$5 = (this.height - this.imageHeight) / 2;
        this.blit($$0, $$4, $$5, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected boolean isHovering(int $$0, int $$1, int $$2, int $$3, double $$4, double $$5) {
        return (!this.widthTooNarrow || !this.recipeBookComponent.isVisible()) && super.isHovering($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    public boolean mouseClicked(double $$0, double $$1, int $$2) {
        if (this.recipeBookComponent.mouseClicked($$0, $$1, $$2)) {
            this.setFocused(this.recipeBookComponent);
            return true;
        }
        if (this.widthTooNarrow && this.recipeBookComponent.isVisible()) {
            return true;
        }
        return super.mouseClicked($$0, $$1, $$2);
    }

    @Override
    protected boolean hasClickedOutside(double $$0, double $$1, int $$2, int $$3, int $$4) {
        boolean $$5 = $$0 < (double)$$2 || $$1 < (double)$$3 || $$0 >= (double)($$2 + this.imageWidth) || $$1 >= (double)($$3 + this.imageHeight);
        return this.recipeBookComponent.hasClickedOutside($$0, $$1, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, $$4) && $$5;
    }

    @Override
    protected void slotClicked(Slot $$0, int $$1, int $$2, ClickType $$3) {
        super.slotClicked($$0, $$1, $$2, $$3);
        this.recipeBookComponent.slotClicked($$0);
    }

    @Override
    public void recipesUpdated() {
        this.recipeBookComponent.recipesUpdated();
    }

    @Override
    public RecipeBookComponent getRecipeBookComponent() {
        return this.recipeBookComponent;
    }
}