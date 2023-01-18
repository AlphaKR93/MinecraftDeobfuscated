/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Integer
 *  java.lang.Object
 *  java.util.function.BiConsumer
 */
package net.minecraft.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.BiConsumer;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

public class LogoRenderer
extends GuiComponent {
    public static final ResourceLocation MINECRAFT_LOGO = new ResourceLocation("textures/gui/title/minecraft.png");
    public static final ResourceLocation MINECRAFT_EDITION = new ResourceLocation("textures/gui/title/edition.png");
    public static final int LOGO_WIDTH = 274;
    public static final int LOGO_HEIGHT = 44;
    public static final int DEFAULT_HEIGHT_OFFSET = 30;
    private final boolean showEasterEgg = (double)RandomSource.create().nextFloat() < 1.0E-4;
    private final boolean keepLogoThroughFade;

    public LogoRenderer(boolean $$0) {
        this.keepLogoThroughFade = $$0;
    }

    public void renderLogo(PoseStack $$0, int $$1, float $$2) {
        this.renderLogo($$0, $$1, $$2, 30);
    }

    public void renderLogo(PoseStack $$0, int $$12, float $$22, int $$3) {
        RenderSystem.setShaderTexture(0, MINECRAFT_LOGO);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, this.keepLogoThroughFade ? 1.0f : $$22);
        int $$4 = $$12 / 2 - 137;
        if (this.showEasterEgg) {
            this.blitOutlineBlack($$4, $$3, (BiConsumer<Integer, Integer>)((BiConsumer)($$1, $$2) -> {
                this.blit($$0, (int)$$1, (int)$$2, 0, 0, 99, 44);
                this.blit($$0, $$1 + 99, (int)$$2, 129, 0, 27, 44);
                this.blit($$0, $$1 + 99 + 26, (int)$$2, 126, 0, 3, 44);
                this.blit($$0, $$1 + 99 + 26 + 3, (int)$$2, 99, 0, 26, 44);
                this.blit($$0, $$1 + 155, (int)$$2, 0, 45, 155, 44);
            }));
        } else {
            this.blitOutlineBlack($$4, $$3, (BiConsumer<Integer, Integer>)((BiConsumer)($$1, $$2) -> {
                this.blit($$0, (int)$$1, (int)$$2, 0, 0, 155, 44);
                this.blit($$0, $$1 + 155, (int)$$2, 0, 45, 155, 44);
            }));
        }
        RenderSystem.setShaderTexture(0, MINECRAFT_EDITION);
        LogoRenderer.blit($$0, $$4 + 88, $$3 + 37, 0.0f, 0.0f, 98, 14, 128, 16);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }
}