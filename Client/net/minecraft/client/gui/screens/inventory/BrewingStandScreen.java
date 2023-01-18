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
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.BrewingStandMenu;

public class BrewingStandScreen
extends AbstractContainerScreen<BrewingStandMenu> {
    private static final ResourceLocation BREWING_STAND_LOCATION = new ResourceLocation("textures/gui/container/brewing_stand.png");
    private static final int[] BUBBLELENGTHS = new int[]{29, 24, 20, 16, 11, 6, 0};

    public BrewingStandScreen(BrewingStandMenu $$0, Inventory $$1, Component $$2) {
        super($$0, $$1, $$2);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderBackground($$0);
        super.render($$0, $$1, $$2, $$3);
        this.renderTooltip($$0, $$1, $$2);
    }

    @Override
    protected void renderBg(PoseStack $$0, float $$1, int $$2, int $$3) {
        int $$8;
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, BREWING_STAND_LOCATION);
        int $$4 = (this.width - this.imageWidth) / 2;
        int $$5 = (this.height - this.imageHeight) / 2;
        this.blit($$0, $$4, $$5, 0, 0, this.imageWidth, this.imageHeight);
        int $$6 = ((BrewingStandMenu)this.menu).getFuel();
        int $$7 = Mth.clamp((18 * $$6 + 20 - 1) / 20, 0, 18);
        if ($$7 > 0) {
            this.blit($$0, $$4 + 60, $$5 + 44, 176, 29, $$7, 4);
        }
        if (($$8 = ((BrewingStandMenu)this.menu).getBrewingTicks()) > 0) {
            int $$9 = (int)(28.0f * (1.0f - (float)$$8 / 400.0f));
            if ($$9 > 0) {
                this.blit($$0, $$4 + 97, $$5 + 16, 176, 0, 9, $$9);
            }
            if (($$9 = BUBBLELENGTHS[$$8 / 2 % 7]) > 0) {
                this.blit($$0, $$4 + 63, $$5 + 14 + 29 - $$9, 185, 29 - $$9, 12, $$9);
            }
        }
    }
}