/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.FunctionalInterface
 *  java.lang.Object
 *  org.joml.Matrix4f
 */
package net.minecraft.client.gui.screens.inventory.tooltip;

import com.mojang.blaze3d.vertex.BufferBuilder;
import org.joml.Matrix4f;

public class TooltipRenderUtil {
    public static final int MOUSE_OFFSET = 12;
    private static final int PADDING = 3;
    public static final int PADDING_LEFT = 3;
    public static final int PADDING_RIGHT = 3;
    public static final int PADDING_TOP = 3;
    public static final int PADDING_BOTTOM = 3;
    private static final int BACKGROUND_COLOR = -267386864;
    private static final int BORDER_COLOR_TOP = 0x505000FF;
    private static final int BORDER_COLOR_BOTTOM = 1344798847;

    public static void renderTooltipBackground(BlitPainter $$0, Matrix4f $$1, BufferBuilder $$2, int $$3, int $$4, int $$5, int $$6, int $$7) {
        int $$8 = $$3 - 3;
        int $$9 = $$4 - 3;
        int $$10 = $$5 + 3 + 3;
        int $$11 = $$6 + 3 + 3;
        TooltipRenderUtil.renderHorizontalLine($$0, $$1, $$2, $$8, $$9 - 1, $$10, $$7, -267386864);
        TooltipRenderUtil.renderHorizontalLine($$0, $$1, $$2, $$8, $$9 + $$11, $$10, $$7, -267386864);
        TooltipRenderUtil.renderRectangle($$0, $$1, $$2, $$8, $$9, $$10, $$11, $$7, -267386864);
        TooltipRenderUtil.renderVerticalLine($$0, $$1, $$2, $$8 - 1, $$9, $$11, $$7, -267386864);
        TooltipRenderUtil.renderVerticalLine($$0, $$1, $$2, $$8 + $$10, $$9, $$11, $$7, -267386864);
        TooltipRenderUtil.renderFrameGradient($$0, $$1, $$2, $$8, $$9 + 1, $$10, $$11, $$7, 0x505000FF, 1344798847);
    }

    private static void renderFrameGradient(BlitPainter $$0, Matrix4f $$1, BufferBuilder $$2, int $$3, int $$4, int $$5, int $$6, int $$7, int $$8, int $$9) {
        TooltipRenderUtil.renderVerticalLineGradient($$0, $$1, $$2, $$3, $$4, $$6 - 2, $$7, $$8, $$9);
        TooltipRenderUtil.renderVerticalLineGradient($$0, $$1, $$2, $$3 + $$5 - 1, $$4, $$6 - 2, $$7, $$8, $$9);
        TooltipRenderUtil.renderHorizontalLine($$0, $$1, $$2, $$3, $$4 - 1, $$5, $$7, $$8);
        TooltipRenderUtil.renderHorizontalLine($$0, $$1, $$2, $$3, $$4 - 1 + $$6 - 1, $$5, $$7, $$9);
    }

    private static void renderVerticalLine(BlitPainter $$0, Matrix4f $$1, BufferBuilder $$2, int $$3, int $$4, int $$5, int $$6, int $$7) {
        $$0.blit($$1, $$2, $$3, $$4, $$3 + 1, $$4 + $$5, $$6, $$7, $$7);
    }

    private static void renderVerticalLineGradient(BlitPainter $$0, Matrix4f $$1, BufferBuilder $$2, int $$3, int $$4, int $$5, int $$6, int $$7, int $$8) {
        $$0.blit($$1, $$2, $$3, $$4, $$3 + 1, $$4 + $$5, $$6, $$7, $$8);
    }

    private static void renderHorizontalLine(BlitPainter $$0, Matrix4f $$1, BufferBuilder $$2, int $$3, int $$4, int $$5, int $$6, int $$7) {
        $$0.blit($$1, $$2, $$3, $$4, $$3 + $$5, $$4 + 1, $$6, $$7, $$7);
    }

    private static void renderRectangle(BlitPainter $$0, Matrix4f $$1, BufferBuilder $$2, int $$3, int $$4, int $$5, int $$6, int $$7, int $$8) {
        $$0.blit($$1, $$2, $$3, $$4, $$3 + $$5, $$4 + $$6, $$7, $$8, $$8);
    }

    @FunctionalInterface
    public static interface BlitPainter {
        public void blit(Matrix4f var1, BufferBuilder var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9);
    }
}