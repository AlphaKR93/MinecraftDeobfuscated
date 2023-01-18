/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.gui.components;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class Checkbox
extends AbstractButton {
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/checkbox.png");
    private static final int TEXT_COLOR = 0xE0E0E0;
    private boolean selected;
    private final boolean showLabel;

    public Checkbox(int $$0, int $$1, int $$2, int $$3, Component $$4, boolean $$5) {
        this($$0, $$1, $$2, $$3, $$4, $$5, true);
    }

    public Checkbox(int $$0, int $$1, int $$2, int $$3, Component $$4, boolean $$5, boolean $$6) {
        super($$0, $$1, $$2, $$3, $$4);
        this.selected = $$5;
        this.showLabel = $$6;
    }

    @Override
    public void onPress() {
        this.selected = !this.selected;
    }

    public boolean selected() {
        return this.selected;
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput $$0) {
        $$0.add(NarratedElementType.TITLE, (Component)this.createNarrationMessage());
        if (this.active) {
            if (this.isFocused()) {
                $$0.add(NarratedElementType.USAGE, (Component)Component.translatable("narration.checkbox.usage.focused"));
            } else {
                $$0.add(NarratedElementType.USAGE, (Component)Component.translatable("narration.checkbox.usage.hovered"));
            }
        }
    }

    @Override
    public void renderButton(PoseStack $$0, int $$1, int $$2, float $$3) {
        Minecraft $$4 = Minecraft.getInstance();
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.enableDepthTest();
        Font $$5 = $$4.font;
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        Checkbox.blit($$0, this.getX(), this.getY(), this.isFocused() ? 20.0f : 0.0f, this.selected ? 20.0f : 0.0f, 20, this.height, 64, 64);
        this.renderBg($$0, $$4, $$1, $$2);
        if (this.showLabel) {
            Checkbox.drawString($$0, $$5, this.getMessage(), this.getX() + 24, this.getY() + (this.height - 8) / 2, 0xE0E0E0 | Mth.ceil(this.alpha * 255.0f) << 24);
        }
    }
}