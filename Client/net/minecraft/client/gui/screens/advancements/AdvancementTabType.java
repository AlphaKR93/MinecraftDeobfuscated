/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.UnsupportedOperationException
 */
package net.minecraft.client.gui.screens.advancements;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;

enum AdvancementTabType {
    ABOVE(0, 0, 28, 32, 8),
    BELOW(84, 0, 28, 32, 8),
    LEFT(0, 64, 32, 28, 5),
    RIGHT(96, 64, 32, 28, 5);

    private final int textureX;
    private final int textureY;
    private final int width;
    private final int height;
    private final int max;

    private AdvancementTabType(int $$0, int $$1, int $$2, int $$3, int $$4) {
        this.textureX = $$0;
        this.textureY = $$1;
        this.width = $$2;
        this.height = $$3;
        this.max = $$4;
    }

    public int getMax() {
        return this.max;
    }

    public void draw(PoseStack $$0, GuiComponent $$1, int $$2, int $$3, boolean $$4, int $$5) {
        int $$6 = this.textureX;
        if ($$5 > 0) {
            $$6 += this.width;
        }
        if ($$5 == this.max - 1) {
            $$6 += this.width;
        }
        int $$7 = $$4 ? this.textureY + this.height : this.textureY;
        $$1.blit($$0, $$2 + this.getX($$5), $$3 + this.getY($$5), $$6, $$7, this.width, this.height);
    }

    public void drawIcon(int $$0, int $$1, int $$2, ItemRenderer $$3, ItemStack $$4) {
        int $$5 = $$0 + this.getX($$2);
        int $$6 = $$1 + this.getY($$2);
        switch (this) {
            case ABOVE: {
                $$5 += 6;
                $$6 += 9;
                break;
            }
            case BELOW: {
                $$5 += 6;
                $$6 += 6;
                break;
            }
            case LEFT: {
                $$5 += 10;
                $$6 += 5;
                break;
            }
            case RIGHT: {
                $$5 += 6;
                $$6 += 5;
            }
        }
        $$3.renderAndDecorateFakeItem($$4, $$5, $$6);
    }

    public int getX(int $$0) {
        switch (this) {
            case ABOVE: {
                return (this.width + 4) * $$0;
            }
            case BELOW: {
                return (this.width + 4) * $$0;
            }
            case LEFT: {
                return -this.width + 4;
            }
            case RIGHT: {
                return 248;
            }
        }
        throw new UnsupportedOperationException("Don't know what this tab type is!" + this);
    }

    public int getY(int $$0) {
        switch (this) {
            case ABOVE: {
                return -this.height + 4;
            }
            case BELOW: {
                return 136;
            }
            case LEFT: {
                return this.height * $$0;
            }
            case RIGHT: {
                return this.height * $$0;
            }
        }
        throw new UnsupportedOperationException("Don't know what this tab type is!" + this);
    }

    public boolean isMouseOver(int $$0, int $$1, int $$2, double $$3, double $$4) {
        int $$5 = $$0 + this.getX($$2);
        int $$6 = $$1 + this.getY($$2);
        return $$3 > (double)$$5 && $$3 < (double)($$5 + this.width) && $$4 > (double)$$6 && $$4 < (double)($$6 + this.height);
    }
}