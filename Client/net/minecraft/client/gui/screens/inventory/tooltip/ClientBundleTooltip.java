/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.gui.screens.inventory.tooltip;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.item.ItemStack;

public class ClientBundleTooltip
implements ClientTooltipComponent {
    public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/gui/container/bundle.png");
    private static final int MARGIN_Y = 4;
    private static final int BORDER_WIDTH = 1;
    private static final int TEX_SIZE = 128;
    private static final int SLOT_SIZE_X = 18;
    private static final int SLOT_SIZE_Y = 20;
    private final NonNullList<ItemStack> items;
    private final int weight;

    public ClientBundleTooltip(BundleTooltip $$0) {
        this.items = $$0.getItems();
        this.weight = $$0.getWeight();
    }

    @Override
    public int getHeight() {
        return this.gridSizeY() * 20 + 2 + 4;
    }

    @Override
    public int getWidth(Font $$0) {
        return this.gridSizeX() * 18 + 2;
    }

    @Override
    public void renderImage(Font $$0, int $$1, int $$2, PoseStack $$3, ItemRenderer $$4, int $$5) {
        int $$6 = this.gridSizeX();
        int $$7 = this.gridSizeY();
        boolean $$8 = this.weight >= 64;
        int $$9 = 0;
        for (int $$10 = 0; $$10 < $$7; ++$$10) {
            for (int $$11 = 0; $$11 < $$6; ++$$11) {
                int $$12 = $$1 + $$11 * 18 + 1;
                int $$13 = $$2 + $$10 * 20 + 1;
                this.renderSlot($$12, $$13, $$9++, $$8, $$0, $$3, $$4, $$5);
            }
        }
        this.drawBorder($$1, $$2, $$6, $$7, $$3, $$5);
    }

    private void renderSlot(int $$0, int $$1, int $$2, boolean $$3, Font $$4, PoseStack $$5, ItemRenderer $$6, int $$7) {
        if ($$2 >= this.items.size()) {
            this.blit($$5, $$0, $$1, $$7, $$3 ? Texture.BLOCKED_SLOT : Texture.SLOT);
            return;
        }
        ItemStack $$8 = this.items.get($$2);
        this.blit($$5, $$0, $$1, $$7, Texture.SLOT);
        $$6.renderAndDecorateItem($$8, $$0 + 1, $$1 + 1, $$2);
        $$6.renderGuiItemDecorations($$4, $$8, $$0 + 1, $$1 + 1);
        if ($$2 == 0) {
            AbstractContainerScreen.renderSlotHighlight($$5, $$0 + 1, $$1 + 1, $$7);
        }
    }

    private void drawBorder(int $$0, int $$1, int $$2, int $$3, PoseStack $$4, int $$5) {
        this.blit($$4, $$0, $$1, $$5, Texture.BORDER_CORNER_TOP);
        this.blit($$4, $$0 + $$2 * 18 + 1, $$1, $$5, Texture.BORDER_CORNER_TOP);
        for (int $$6 = 0; $$6 < $$2; ++$$6) {
            this.blit($$4, $$0 + 1 + $$6 * 18, $$1, $$5, Texture.BORDER_HORIZONTAL_TOP);
            this.blit($$4, $$0 + 1 + $$6 * 18, $$1 + $$3 * 20, $$5, Texture.BORDER_HORIZONTAL_BOTTOM);
        }
        for (int $$7 = 0; $$7 < $$3; ++$$7) {
            this.blit($$4, $$0, $$1 + $$7 * 20 + 1, $$5, Texture.BORDER_VERTICAL);
            this.blit($$4, $$0 + $$2 * 18 + 1, $$1 + $$7 * 20 + 1, $$5, Texture.BORDER_VERTICAL);
        }
        this.blit($$4, $$0, $$1 + $$3 * 20, $$5, Texture.BORDER_CORNER_BOTTOM);
        this.blit($$4, $$0 + $$2 * 18 + 1, $$1 + $$3 * 20, $$5, Texture.BORDER_CORNER_BOTTOM);
    }

    private void blit(PoseStack $$0, int $$1, int $$2, int $$3, Texture $$4) {
        RenderSystem.setShaderTexture(0, TEXTURE_LOCATION);
        GuiComponent.blit($$0, $$1, $$2, $$3, $$4.x, $$4.y, $$4.w, $$4.h, 128, 128);
    }

    private int gridSizeX() {
        return Math.max((int)2, (int)((int)Math.ceil((double)Math.sqrt((double)((double)this.items.size() + 1.0)))));
    }

    private int gridSizeY() {
        return (int)Math.ceil((double)(((double)this.items.size() + 1.0) / (double)this.gridSizeX()));
    }

    static enum Texture {
        SLOT(0, 0, 18, 20),
        BLOCKED_SLOT(0, 40, 18, 20),
        BORDER_VERTICAL(0, 18, 1, 20),
        BORDER_HORIZONTAL_TOP(0, 20, 18, 1),
        BORDER_HORIZONTAL_BOTTOM(0, 60, 18, 1),
        BORDER_CORNER_TOP(0, 20, 1, 1),
        BORDER_CORNER_BOTTOM(0, 60, 1, 1);

        public final int x;
        public final int y;
        public final int w;
        public final int h;

        private Texture(int $$0, int $$1, int $$2, int $$3) {
            this.x = $$0;
            this.y = $$1;
            this.w = $$2;
            this.h = $$3;
        }
    }
}