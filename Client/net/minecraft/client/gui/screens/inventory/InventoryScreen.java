/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.Slot;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

public class InventoryScreen
extends EffectRenderingInventoryScreen<InventoryMenu>
implements RecipeUpdateListener {
    private static final ResourceLocation RECIPE_BUTTON_LOCATION = new ResourceLocation("textures/gui/recipe_button.png");
    private float xMouse;
    private float yMouse;
    private final RecipeBookComponent recipeBookComponent = new RecipeBookComponent();
    private boolean recipeBookComponentInitialized;
    private boolean widthTooNarrow;
    private boolean buttonClicked;

    public InventoryScreen(Player $$0) {
        super($$0.inventoryMenu, $$0.getInventory(), Component.translatable("container.crafting"));
        this.passEvents = true;
        this.titleLabelX = 97;
    }

    @Override
    public void containerTick() {
        if (this.minecraft.gameMode.hasInfiniteItems()) {
            this.minecraft.setScreen(new CreativeModeInventoryScreen(this.minecraft.player, this.minecraft.player.connection.enabledFeatures(), this.minecraft.options.operatorItemsTab().get()));
            return;
        }
        this.recipeBookComponent.tick();
    }

    @Override
    protected void init() {
        if (this.minecraft.gameMode.hasInfiniteItems()) {
            this.minecraft.setScreen(new CreativeModeInventoryScreen(this.minecraft.player, this.minecraft.player.connection.enabledFeatures(), this.minecraft.options.operatorItemsTab().get()));
            return;
        }
        super.init();
        this.widthTooNarrow = this.width < 379;
        this.recipeBookComponent.init(this.width, this.height, this.minecraft, this.widthTooNarrow, (RecipeBookMenu)this.menu);
        this.recipeBookComponentInitialized = true;
        this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
        this.addRenderableWidget(new ImageButton(this.leftPos + 104, this.height / 2 - 22, 20, 18, 0, 0, 19, RECIPE_BUTTON_LOCATION, $$0 -> {
            this.recipeBookComponent.toggleVisibility();
            this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
            $$0.setPosition(this.leftPos + 104, this.height / 2 - 22);
            this.buttonClicked = true;
        }));
        this.addWidget(this.recipeBookComponent);
        this.setInitialFocus(this.recipeBookComponent);
    }

    @Override
    protected void renderLabels(PoseStack $$0, int $$1, int $$2) {
        this.font.draw($$0, this.title, (float)this.titleLabelX, (float)this.titleLabelY, 0x404040);
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
            this.recipeBookComponent.renderGhostRecipe($$0, this.leftPos, this.topPos, false, $$3);
        }
        this.renderTooltip($$0, $$1, $$2);
        this.recipeBookComponent.renderTooltip($$0, this.leftPos, this.topPos, $$1, $$2);
        this.xMouse = $$1;
        this.yMouse = $$2;
    }

    @Override
    protected void renderBg(PoseStack $$0, float $$1, int $$2, int $$3) {
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
        RenderSystem.setShaderTexture(0, INVENTORY_LOCATION);
        int $$4 = this.leftPos;
        int $$5 = this.topPos;
        this.blit($$0, $$4, $$5, 0, 0, this.imageWidth, this.imageHeight);
        InventoryScreen.renderEntityInInventoryFollowsMouse($$4 + 51, $$5 + 75, 30, (float)($$4 + 51) - this.xMouse, (float)($$5 + 75 - 50) - this.yMouse, this.minecraft.player);
    }

    public static void renderEntityInInventoryFollowsMouse(int $$0, int $$1, int $$2, float $$3, float $$4, LivingEntity $$5) {
        float $$6 = (float)Math.atan((double)($$3 / 40.0f));
        float $$7 = (float)Math.atan((double)($$4 / 40.0f));
        Quaternionf $$8 = new Quaternionf().rotateZ((float)Math.PI);
        Quaternionf $$9 = new Quaternionf().rotateX($$7 * 20.0f * ((float)Math.PI / 180));
        $$8.mul((Quaternionfc)$$9);
        float $$10 = $$5.yBodyRot;
        float $$11 = $$5.getYRot();
        float $$12 = $$5.getXRot();
        float $$13 = $$5.yHeadRotO;
        float $$14 = $$5.yHeadRot;
        $$5.yBodyRot = 180.0f + $$6 * 20.0f;
        $$5.setYRot(180.0f + $$6 * 40.0f);
        $$5.setXRot(-$$7 * 20.0f);
        $$5.yHeadRot = $$5.getYRot();
        $$5.yHeadRotO = $$5.getYRot();
        InventoryScreen.renderEntityInInventory($$0, $$1, $$2, $$8, $$9, $$5);
        $$5.yBodyRot = $$10;
        $$5.setYRot($$11);
        $$5.setXRot($$12);
        $$5.yHeadRotO = $$13;
        $$5.yHeadRot = $$14;
    }

    public static void renderEntityInInventory(int $$0, int $$1, int $$2, Quaternionf $$3, @Nullable Quaternionf $$4, LivingEntity $$5) {
        PoseStack $$6 = RenderSystem.getModelViewStack();
        $$6.pushPose();
        $$6.translate($$0, $$1, 1050.0f);
        $$6.scale(1.0f, 1.0f, -1.0f);
        RenderSystem.applyModelViewMatrix();
        PoseStack $$7 = new PoseStack();
        $$7.translate(0.0f, 0.0f, 1000.0f);
        $$7.scale($$2, $$2, $$2);
        $$7.mulPose($$3);
        Lighting.setupForEntityInInventory();
        EntityRenderDispatcher $$8 = Minecraft.getInstance().getEntityRenderDispatcher();
        if ($$4 != null) {
            $$4.conjugate();
            $$8.overrideCameraOrientation($$4);
        }
        $$8.setRenderShadow(false);
        MultiBufferSource.BufferSource $$9 = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.runAsFancy(() -> $$8.render($$5, 0.0, 0.0, 0.0, 0.0f, 1.0f, $$7, $$9, 0xF000F0));
        $$9.endBatch();
        $$8.setRenderShadow(true);
        $$6.popPose();
        RenderSystem.applyModelViewMatrix();
        Lighting.setupFor3DItems();
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
            return false;
        }
        return super.mouseClicked($$0, $$1, $$2);
    }

    @Override
    public boolean mouseReleased(double $$0, double $$1, int $$2) {
        if (this.buttonClicked) {
            this.buttonClicked = false;
            return true;
        }
        return super.mouseReleased($$0, $$1, $$2);
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