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
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.item.ItemStack;

public abstract class ItemCombinerScreen<T extends ItemCombinerMenu>
extends AbstractContainerScreen<T>
implements ContainerListener {
    private final ResourceLocation menuResource;

    public ItemCombinerScreen(T $$0, Inventory $$1, Component $$2, ResourceLocation $$3) {
        super($$0, $$1, $$2);
        this.menuResource = $$3;
    }

    protected void subInit() {
    }

    @Override
    protected void init() {
        super.init();
        this.subInit();
        ((ItemCombinerMenu)this.menu).addSlotListener(this);
    }

    @Override
    public void removed() {
        super.removed();
        ((ItemCombinerMenu)this.menu).removeSlotListener(this);
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderBackground($$0);
        super.render($$0, $$1, $$2, $$3);
        RenderSystem.disableBlend();
        this.renderFg($$0, $$1, $$2, $$3);
        this.renderTooltip($$0, $$1, $$2);
    }

    protected void renderFg(PoseStack $$0, int $$1, int $$2, float $$3) {
    }

    @Override
    protected void renderBg(PoseStack $$0, float $$1, int $$2, int $$3) {
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
        RenderSystem.setShaderTexture(0, this.menuResource);
        this.blit($$0, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        this.renderErrorIcon($$0, this.leftPos, this.topPos);
    }

    protected abstract void renderErrorIcon(PoseStack var1, int var2, int var3);

    @Override
    public void dataChanged(AbstractContainerMenu $$0, int $$1, int $$2) {
    }

    @Override
    public void slotChanged(AbstractContainerMenu $$0, int $$1, ItemStack $$2) {
    }
}