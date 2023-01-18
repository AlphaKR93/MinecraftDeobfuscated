/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;

public class PlayerFaceRenderer {
    public static final int SKIN_HEAD_U = 8;
    public static final int SKIN_HEAD_V = 8;
    public static final int SKIN_HEAD_WIDTH = 8;
    public static final int SKIN_HEAD_HEIGHT = 8;
    public static final int SKIN_HAT_U = 40;
    public static final int SKIN_HAT_V = 8;
    public static final int SKIN_HAT_WIDTH = 8;
    public static final int SKIN_HAT_HEIGHT = 8;
    public static final int SKIN_TEX_WIDTH = 64;
    public static final int SKIN_TEX_HEIGHT = 64;

    public static void draw(PoseStack $$0, int $$1, int $$2, int $$3) {
        PlayerFaceRenderer.draw($$0, $$1, $$2, $$3, true, false);
    }

    public static void draw(PoseStack $$0, int $$1, int $$2, int $$3, boolean $$4, boolean $$5) {
        int $$6 = 8 + ($$5 ? 8 : 0);
        int $$7 = 8 * ($$5 ? -1 : 1);
        GuiComponent.blit($$0, $$1, $$2, $$3, $$3, 8.0f, $$6, 8, $$7, 64, 64);
        if ($$4) {
            PlayerFaceRenderer.drawHat($$0, $$1, $$2, $$3, $$5);
        }
    }

    private static void drawHat(PoseStack $$0, int $$1, int $$2, int $$3, boolean $$4) {
        int $$5 = 8 + ($$4 ? 8 : 0);
        int $$6 = 8 * ($$4 ? -1 : 1);
        RenderSystem.enableBlend();
        GuiComponent.blit($$0, $$1, $$2, $$3, $$3, 40.0f, $$5, 8, $$6, 64, 64);
        RenderSystem.disableBlend();
    }
}