/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.InputType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public abstract class AbstractSliderButton
extends AbstractWidget {
    private static final ResourceLocation SLIDER_LOCATION = new ResourceLocation("textures/gui/slider.png");
    private static final int HEIGHT = 20;
    private static final int HANDLE_HALF_WIDTH = 4;
    private static final int HANDLE_WIDTH = 8;
    private static final int TEXTURE_WIDTH = 200;
    private static final int BACKGROUND = 0;
    private static final int BACKGROUND_FOCUSED = 1;
    private static final int HANDLE = 2;
    private static final int HANDLE_FOCUSED = 3;
    protected double value;
    private boolean canChangeValue;

    public AbstractSliderButton(int $$0, int $$1, int $$2, int $$3, Component $$4, double $$5) {
        super($$0, $$1, $$2, $$3, $$4);
        this.value = $$5;
    }

    @Override
    protected ResourceLocation getTextureLocation() {
        return SLIDER_LOCATION;
    }

    @Override
    protected int getTextureY() {
        int $$0 = this.isFocused() && !this.canChangeValue ? 1 : 0;
        return $$0 * 20;
    }

    private int getHandleTextureY() {
        int $$0 = this.isHovered || this.canChangeValue ? 3 : 2;
        return $$0 * 20;
    }

    @Override
    protected MutableComponent createNarrationMessage() {
        return Component.translatable("gui.narrate.slider", this.getMessage());
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput $$0) {
        $$0.add(NarratedElementType.TITLE, (Component)this.createNarrationMessage());
        if (this.active) {
            if (this.isFocused()) {
                $$0.add(NarratedElementType.USAGE, (Component)Component.translatable("narration.slider.usage.focused"));
            } else {
                $$0.add(NarratedElementType.USAGE, (Component)Component.translatable("narration.slider.usage.hovered"));
            }
        }
    }

    @Override
    protected void renderBg(PoseStack $$0, Minecraft $$1, int $$2, int $$3) {
        RenderSystem.setShaderTexture(0, this.getTextureLocation());
        int $$4 = this.getHandleTextureY();
        this.blit($$0, this.getX() + (int)(this.value * (double)(this.width - 8)), this.getY(), 0, $$4, 4, 20);
        this.blit($$0, this.getX() + (int)(this.value * (double)(this.width - 8)) + 4, this.getY(), 196, $$4, 4, 20);
    }

    @Override
    public void onClick(double $$0, double $$1) {
        this.setValueFromMouse($$0);
    }

    @Override
    public void setFocused(boolean $$0) {
        super.setFocused($$0);
        if (!$$0) {
            this.canChangeValue = false;
            return;
        }
        InputType $$1 = Minecraft.getInstance().getLastInputType();
        if ($$1 == InputType.MOUSE || $$1 == InputType.KEYBOARD_TAB) {
            this.canChangeValue = true;
        }
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if ($$0 == 32 || $$0 == 257 || $$0 == 335) {
            this.canChangeValue = !this.canChangeValue;
            return true;
        }
        if (this.canChangeValue) {
            boolean $$3;
            boolean bl = $$3 = $$0 == 263;
            if ($$3 || $$0 == 262) {
                float $$4 = $$3 ? -1.0f : 1.0f;
                this.setValue(this.value + (double)($$4 / (float)(this.width - 8)));
                return true;
            }
        }
        return false;
    }

    private void setValueFromMouse(double $$0) {
        this.setValue(($$0 - (double)(this.getX() + 4)) / (double)(this.width - 8));
    }

    private void setValue(double $$0) {
        double $$1 = this.value;
        this.value = Mth.clamp($$0, 0.0, 1.0);
        if ($$1 != this.value) {
            this.applyValue();
        }
        this.updateMessage();
    }

    @Override
    protected void onDrag(double $$0, double $$1, double $$2, double $$3) {
        this.setValueFromMouse($$0);
        super.onDrag($$0, $$1, $$2, $$3);
    }

    @Override
    public void playDownSound(SoundManager $$0) {
    }

    @Override
    public void onRelease(double $$0, double $$1) {
        super.playDownSound(Minecraft.getInstance().getSoundManager());
    }

    protected abstract void updateMessage();

    protected abstract void applyValue();
}