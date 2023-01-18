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
import net.minecraft.world.inventory.GrindstoneMenu;

public class GrindstoneScreen
extends AbstractContainerScreen<GrindstoneMenu> {
    private static final ResourceLocation GRINDSTONE_LOCATION = new ResourceLocation("textures/gui/container/grindstone.png");

    public GrindstoneScreen(GrindstoneMenu $$0, Inventory $$1, Component $$2) {
        super($$0, $$1, $$2);
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderBackground($$0);
        this.renderBg($$0, $$3, $$1, $$2);
        super.render($$0, $$1, $$2, $$3);
        this.renderTooltip($$0, $$1, $$2);
    }

    @Override
    protected void renderBg(PoseStack $$0, float $$1, int $$2, int $$3) {
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
        RenderSystem.setShaderTexture(0, GRINDSTONE_LOCATION);
        int $$4 = (this.width - this.imageWidth) / 2;
        int $$5 = (this.height - this.imageHeight) / 2;
        this.blit($$0, $$4, $$5, 0, 0, this.imageWidth, this.imageHeight);
        if ((((GrindstoneMenu)this.menu).getSlot(0).hasItem() || ((GrindstoneMenu)this.menu).getSlot(1).hasItem()) && !((GrindstoneMenu)this.menu).getSlot(2).hasItem()) {
            this.blit($$0, $$4 + 92, $$5 + 31, this.imageWidth, 0, 28, 21);
        }
    }
}