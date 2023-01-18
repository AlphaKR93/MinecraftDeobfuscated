/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.BelowOrAboveWidgetTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;

public abstract class AbstractWidget
extends GuiComponent
implements Renderable,
GuiEventListener,
NarratableEntry {
    public static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");
    protected int width;
    protected int height;
    private int x;
    private int y;
    private Component message;
    protected boolean isHovered;
    public boolean active = true;
    public boolean visible = true;
    protected float alpha = 1.0f;
    private boolean focused;
    @Nullable
    private Tooltip tooltip;
    private int tooltipMsDelay;
    private long hoverOrFocusedStartTime;
    private boolean wasHoveredOrFocused;

    public AbstractWidget(int $$0, int $$1, int $$2, int $$3, Component $$4) {
        this.x = $$0;
        this.y = $$1;
        this.width = $$2;
        this.height = $$3;
        this.message = $$4;
    }

    public int getHeight() {
        return this.height;
    }

    protected int getYImage(boolean $$0) {
        int $$1 = 1;
        if (!this.active) {
            $$1 = 0;
        } else if ($$0) {
            $$1 = 2;
        }
        return $$1;
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        if (!this.visible) {
            return;
        }
        this.isHovered = $$1 >= this.getX() && $$2 >= this.getY() && $$1 < this.getX() + this.width && $$2 < this.getY() + this.height;
        this.renderButton($$0, $$1, $$2, $$3);
        this.updateTooltip();
    }

    private void updateTooltip() {
        Screen $$1;
        if (this.tooltip == null) {
            return;
        }
        boolean $$0 = this.isHoveredOrFocused();
        if ($$0 != this.wasHoveredOrFocused) {
            if ($$0) {
                this.hoverOrFocusedStartTime = Util.getMillis();
            }
            this.wasHoveredOrFocused = $$0;
        }
        if ($$0 && Util.getMillis() - this.hoverOrFocusedStartTime > (long)this.tooltipMsDelay && ($$1 = Minecraft.getInstance().screen) != null) {
            $$1.setTooltipForNextRenderPass(this.tooltip, this.createTooltipPositioner(), this.isFocused());
        }
    }

    protected ClientTooltipPositioner createTooltipPositioner() {
        if (this.isFocused()) {
            return new BelowOrAboveWidgetTooltipPositioner(this);
        }
        return DefaultTooltipPositioner.INSTANCE;
    }

    public void setTooltip(@Nullable Tooltip $$0) {
        this.tooltip = $$0;
    }

    public void setTooltipDelay(int $$0) {
        this.tooltipMsDelay = $$0;
    }

    protected MutableComponent createNarrationMessage() {
        return AbstractWidget.wrapDefaultNarrationMessage(this.getMessage());
    }

    public static MutableComponent wrapDefaultNarrationMessage(Component $$0) {
        return Component.translatable("gui.narrate.button", $$0);
    }

    public void renderButton(PoseStack $$0, int $$1, int $$2, float $$3) {
        Minecraft $$4 = Minecraft.getInstance();
        Font $$5 = $$4.font;
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
        RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, this.alpha);
        int $$6 = this.getYImage(this.isHoveredOrFocused());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        this.blit($$0, this.getX(), this.getY(), 0, 46 + $$6 * 20, this.width / 2, this.height);
        this.blit($$0, this.getX() + this.width / 2, this.getY(), 200 - this.width / 2, 46 + $$6 * 20, this.width / 2, this.height);
        this.renderBg($$0, $$4, $$1, $$2);
        int $$7 = this.active ? 0xFFFFFF : 0xA0A0A0;
        AbstractWidget.drawCenteredString($$0, $$5, this.getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, $$7 | Mth.ceil(this.alpha * 255.0f) << 24);
    }

    protected void renderBg(PoseStack $$0, Minecraft $$1, int $$2, int $$3) {
    }

    public void onClick(double $$0, double $$1) {
    }

    public void onRelease(double $$0, double $$1) {
    }

    protected void onDrag(double $$0, double $$1, double $$2, double $$3) {
    }

    @Override
    public boolean mouseClicked(double $$0, double $$1, int $$2) {
        boolean $$3;
        if (!this.active || !this.visible) {
            return false;
        }
        if (this.isValidClickButton($$2) && ($$3 = this.clicked($$0, $$1))) {
            this.playDownSound(Minecraft.getInstance().getSoundManager());
            this.onClick($$0, $$1);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double $$0, double $$1, int $$2) {
        if (this.isValidClickButton($$2)) {
            this.onRelease($$0, $$1);
            return true;
        }
        return false;
    }

    protected boolean isValidClickButton(int $$0) {
        return $$0 == 0;
    }

    @Override
    public boolean mouseDragged(double $$0, double $$1, int $$2, double $$3, double $$4) {
        if (this.isValidClickButton($$2)) {
            this.onDrag($$0, $$1, $$3, $$4);
            return true;
        }
        return false;
    }

    protected boolean clicked(double $$0, double $$1) {
        return this.active && this.visible && $$0 >= (double)this.getX() && $$1 >= (double)this.getY() && $$0 < (double)(this.getX() + this.width) && $$1 < (double)(this.getY() + this.height);
    }

    public boolean isHoveredOrFocused() {
        return this.isHovered || this.focused;
    }

    @Override
    public boolean changeFocus(boolean $$0) {
        if (!this.active || !this.visible) {
            return false;
        }
        this.focused = !this.focused;
        this.onFocusedChanged(this.focused);
        return this.focused;
    }

    protected void onFocusedChanged(boolean $$0) {
    }

    @Override
    public boolean isMouseOver(double $$0, double $$1) {
        return this.active && this.visible && $$0 >= (double)this.getX() && $$1 >= (double)this.getY() && $$0 < (double)(this.getX() + this.width) && $$1 < (double)(this.getY() + this.height);
    }

    public void playDownSound(SoundManager $$0) {
        $$0.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int $$0) {
        this.width = $$0;
    }

    public void setAlpha(float $$0) {
        this.alpha = $$0;
    }

    public void setMessage(Component $$0) {
        this.message = $$0;
    }

    public Component getMessage() {
        return this.message;
    }

    public boolean isFocused() {
        return this.focused;
    }

    @Override
    public boolean isActive() {
        return this.visible && this.active;
    }

    protected void setFocused(boolean $$0) {
        this.focused = $$0;
    }

    @Override
    public NarratableEntry.NarrationPriority narrationPriority() {
        if (this.focused) {
            return NarratableEntry.NarrationPriority.FOCUSED;
        }
        if (this.isHovered) {
            return NarratableEntry.NarrationPriority.HOVERED;
        }
        return NarratableEntry.NarrationPriority.NONE;
    }

    @Override
    public final void updateNarration(NarrationElementOutput $$0) {
        this.updateWidgetNarration($$0);
        if (this.tooltip != null) {
            this.tooltip.updateNarration($$0);
        }
    }

    protected abstract void updateWidgetNarration(NarrationElementOutput var1);

    protected void defaultButtonNarrationText(NarrationElementOutput $$0) {
        $$0.add(NarratedElementType.TITLE, (Component)this.createNarrationMessage());
        if (this.active) {
            if (this.isFocused()) {
                $$0.add(NarratedElementType.USAGE, (Component)Component.translatable("narration.button.usage.focused"));
            } else {
                $$0.add(NarratedElementType.USAGE, (Component)Component.translatable("narration.button.usage.hovered"));
            }
        }
    }

    public int getX() {
        return this.x;
    }

    public void setX(int $$0) {
        this.x = $$0;
    }

    public void setPosition(int $$0, int $$1) {
        this.setX($$0);
        this.setY($$1);
    }

    public int getY() {
        return this.y;
    }

    public void setY(int $$0) {
        this.y = $$0;
    }
}