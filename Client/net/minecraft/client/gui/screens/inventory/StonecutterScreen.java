/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.function.Supplier
 */
package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.crafting.StonecutterRecipe;

public class StonecutterScreen
extends AbstractContainerScreen<StonecutterMenu> {
    private static final ResourceLocation BG_LOCATION = new ResourceLocation("textures/gui/container/stonecutter.png");
    private static final int SCROLLER_WIDTH = 12;
    private static final int SCROLLER_HEIGHT = 15;
    private static final int RECIPES_COLUMNS = 4;
    private static final int RECIPES_ROWS = 3;
    private static final int RECIPES_IMAGE_SIZE_WIDTH = 16;
    private static final int RECIPES_IMAGE_SIZE_HEIGHT = 18;
    private static final int SCROLLER_FULL_HEIGHT = 54;
    private static final int RECIPES_X = 52;
    private static final int RECIPES_Y = 14;
    private float scrollOffs;
    private boolean scrolling;
    private int startIndex;
    private boolean displayRecipes;

    public StonecutterScreen(StonecutterMenu $$0, Inventory $$1, Component $$2) {
        super($$0, $$1, $$2);
        $$0.registerUpdateListener(this::containerChanged);
        --this.titleLabelY;
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        super.render($$0, $$1, $$2, $$3);
        this.renderTooltip($$0, $$1, $$2);
    }

    @Override
    protected void renderBg(PoseStack $$0, float $$1, int $$2, int $$3) {
        this.renderBackground($$0);
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
        RenderSystem.setShaderTexture(0, BG_LOCATION);
        int $$4 = this.leftPos;
        int $$5 = this.topPos;
        this.blit($$0, $$4, $$5, 0, 0, this.imageWidth, this.imageHeight);
        int $$6 = (int)(41.0f * this.scrollOffs);
        this.blit($$0, $$4 + 119, $$5 + 15 + $$6, 176 + (this.isScrollBarActive() ? 0 : 12), 0, 12, 15);
        int $$7 = this.leftPos + 52;
        int $$8 = this.topPos + 14;
        int $$9 = this.startIndex + 12;
        this.renderButtons($$0, $$2, $$3, $$7, $$8, $$9);
        this.renderRecipes($$7, $$8, $$9);
    }

    @Override
    protected void renderTooltip(PoseStack $$0, int $$1, int $$2) {
        super.renderTooltip($$0, $$1, $$2);
        if (this.displayRecipes) {
            int $$3 = this.leftPos + 52;
            int $$4 = this.topPos + 14;
            int $$5 = this.startIndex + 12;
            List<StonecutterRecipe> $$6 = ((StonecutterMenu)this.menu).getRecipes();
            for (int $$7 = this.startIndex; $$7 < $$5 && $$7 < ((StonecutterMenu)this.menu).getNumRecipes(); ++$$7) {
                int $$8 = $$7 - this.startIndex;
                int $$9 = $$3 + $$8 % 4 * 16;
                int $$10 = $$4 + $$8 / 4 * 18 + 2;
                if ($$1 < $$9 || $$1 >= $$9 + 16 || $$2 < $$10 || $$2 >= $$10 + 18) continue;
                this.renderTooltip($$0, ((StonecutterRecipe)$$6.get($$7)).getResultItem(), $$1, $$2);
            }
        }
    }

    private void renderButtons(PoseStack $$0, int $$1, int $$2, int $$3, int $$4, int $$5) {
        for (int $$6 = this.startIndex; $$6 < $$5 && $$6 < ((StonecutterMenu)this.menu).getNumRecipes(); ++$$6) {
            int $$7 = $$6 - this.startIndex;
            int $$8 = $$3 + $$7 % 4 * 16;
            int $$9 = $$7 / 4;
            int $$10 = $$4 + $$9 * 18 + 2;
            int $$11 = this.imageHeight;
            if ($$6 == ((StonecutterMenu)this.menu).getSelectedRecipeIndex()) {
                $$11 += 18;
            } else if ($$1 >= $$8 && $$2 >= $$10 && $$1 < $$8 + 16 && $$2 < $$10 + 18) {
                $$11 += 36;
            }
            this.blit($$0, $$8, $$10 - 1, 0, $$11, 16, 18);
        }
    }

    private void renderRecipes(int $$0, int $$1, int $$2) {
        List<StonecutterRecipe> $$3 = ((StonecutterMenu)this.menu).getRecipes();
        for (int $$4 = this.startIndex; $$4 < $$2 && $$4 < ((StonecutterMenu)this.menu).getNumRecipes(); ++$$4) {
            int $$5 = $$4 - this.startIndex;
            int $$6 = $$0 + $$5 % 4 * 16;
            int $$7 = $$5 / 4;
            int $$8 = $$1 + $$7 * 18 + 2;
            this.minecraft.getItemRenderer().renderAndDecorateItem(((StonecutterRecipe)$$3.get($$4)).getResultItem(), $$6, $$8);
        }
    }

    @Override
    public boolean mouseClicked(double $$0, double $$1, int $$2) {
        this.scrolling = false;
        if (this.displayRecipes) {
            int $$3 = this.leftPos + 52;
            int $$4 = this.topPos + 14;
            int $$5 = this.startIndex + 12;
            for (int $$6 = this.startIndex; $$6 < $$5; ++$$6) {
                int $$7 = $$6 - this.startIndex;
                double $$8 = $$0 - (double)($$3 + $$7 % 4 * 16);
                double $$9 = $$1 - (double)($$4 + $$7 / 4 * 18);
                if (!($$8 >= 0.0) || !($$9 >= 0.0) || !($$8 < 16.0) || !($$9 < 18.0) || !((StonecutterMenu)this.menu).clickMenuButton(this.minecraft.player, $$6)) continue;
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0f));
                this.minecraft.gameMode.handleInventoryButtonClick(((StonecutterMenu)this.menu).containerId, $$6);
                return true;
            }
            $$3 = this.leftPos + 119;
            $$4 = this.topPos + 9;
            if ($$0 >= (double)$$3 && $$0 < (double)($$3 + 12) && $$1 >= (double)$$4 && $$1 < (double)($$4 + 54)) {
                this.scrolling = true;
            }
        }
        return super.mouseClicked($$0, $$1, $$2);
    }

    @Override
    public boolean mouseDragged(double $$0, double $$1, int $$2, double $$3, double $$4) {
        if (this.scrolling && this.isScrollBarActive()) {
            int $$5 = this.topPos + 14;
            int $$6 = $$5 + 54;
            this.scrollOffs = ((float)$$1 - (float)$$5 - 7.5f) / ((float)($$6 - $$5) - 15.0f);
            this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0f, 1.0f);
            this.startIndex = (int)((double)(this.scrollOffs * (float)this.getOffscreenRows()) + 0.5) * 4;
            return true;
        }
        return super.mouseDragged($$0, $$1, $$2, $$3, $$4);
    }

    @Override
    public boolean mouseScrolled(double $$0, double $$1, double $$2) {
        if (this.isScrollBarActive()) {
            int $$3 = this.getOffscreenRows();
            float $$4 = (float)$$2 / (float)$$3;
            this.scrollOffs = Mth.clamp(this.scrollOffs - $$4, 0.0f, 1.0f);
            this.startIndex = (int)((double)(this.scrollOffs * (float)$$3) + 0.5) * 4;
        }
        return true;
    }

    private boolean isScrollBarActive() {
        return this.displayRecipes && ((StonecutterMenu)this.menu).getNumRecipes() > 12;
    }

    protected int getOffscreenRows() {
        return (((StonecutterMenu)this.menu).getNumRecipes() + 4 - 1) / 4 - 3;
    }

    private void containerChanged() {
        this.displayRecipes = ((StonecutterMenu)this.menu).hasInputItem();
        if (!this.displayRecipes) {
            this.scrollOffs = 0.0f;
            this.startIndex = 0;
        }
    }
}