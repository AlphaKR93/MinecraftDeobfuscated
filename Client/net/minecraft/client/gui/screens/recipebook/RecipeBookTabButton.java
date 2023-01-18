/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.function.Supplier
 */
package net.minecraft.client.gui.screens.recipebook;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

public class RecipeBookTabButton
extends StateSwitchingButton {
    private final RecipeBookCategories category;
    private static final float ANIMATION_TIME = 15.0f;
    private float animationTime;

    public RecipeBookTabButton(RecipeBookCategories $$0) {
        super(0, 0, 35, 27, false);
        this.category = $$0;
        this.initTextureValues(153, 2, 35, 0, RecipeBookComponent.RECIPE_BOOK_LOCATION);
    }

    public void startAnimation(Minecraft $$0) {
        ClientRecipeBook $$1 = $$0.player.getRecipeBook();
        List<RecipeCollection> $$2 = $$1.getCollection(this.category);
        if (!($$0.player.containerMenu instanceof RecipeBookMenu)) {
            return;
        }
        for (RecipeCollection $$3 : $$2) {
            for (Recipe $$4 : $$3.getRecipes($$1.isFiltering((RecipeBookMenu)$$0.player.containerMenu))) {
                if (!$$1.willHighlight($$4)) continue;
                this.animationTime = 15.0f;
                return;
            }
        }
    }

    @Override
    public void renderButton(PoseStack $$0, int $$1, int $$2, float $$3) {
        if (this.animationTime > 0.0f) {
            float $$4 = 1.0f + 0.1f * (float)Math.sin((double)(this.animationTime / 15.0f * (float)Math.PI));
            $$0.pushPose();
            $$0.translate(this.getX() + 8, this.getY() + 12, 0.0f);
            $$0.scale(1.0f, $$4, 1.0f);
            $$0.translate(-(this.getX() + 8), -(this.getY() + 12), 0.0f);
        }
        Minecraft $$5 = Minecraft.getInstance();
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
        RenderSystem.setShaderTexture(0, this.resourceLocation);
        RenderSystem.disableDepthTest();
        int $$6 = this.xTexStart;
        int $$7 = this.yTexStart;
        if (this.isStateTriggered) {
            $$6 += this.xDiffTex;
        }
        if (this.isHoveredOrFocused()) {
            $$7 += this.yDiffTex;
        }
        int $$8 = this.getX();
        if (this.isStateTriggered) {
            $$8 -= 2;
        }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        this.blit($$0, $$8, this.getY(), $$6, $$7, this.width, this.height);
        RenderSystem.enableDepthTest();
        this.renderIcon($$5.getItemRenderer());
        if (this.animationTime > 0.0f) {
            $$0.popPose();
            this.animationTime -= $$3;
        }
    }

    private void renderIcon(ItemRenderer $$0) {
        int $$2;
        List<ItemStack> $$1 = this.category.getIconItems();
        int n = $$2 = this.isStateTriggered ? -2 : 0;
        if ($$1.size() == 1) {
            $$0.renderAndDecorateFakeItem((ItemStack)$$1.get(0), this.getX() + 9 + $$2, this.getY() + 5);
        } else if ($$1.size() == 2) {
            $$0.renderAndDecorateFakeItem((ItemStack)$$1.get(0), this.getX() + 3 + $$2, this.getY() + 5);
            $$0.renderAndDecorateFakeItem((ItemStack)$$1.get(1), this.getX() + 14 + $$2, this.getY() + 5);
        }
    }

    public RecipeBookCategories getCategory() {
        return this.category;
    }

    public boolean updateVisibility(ClientRecipeBook $$0) {
        List<RecipeCollection> $$1 = $$0.getCollection(this.category);
        this.visible = false;
        if ($$1 != null) {
            for (RecipeCollection $$2 : $$1) {
                if (!$$2.hasKnownRecipes() || !$$2.hasFitting()) continue;
                this.visible = true;
                break;
            }
        }
        return this.visible;
    }
}