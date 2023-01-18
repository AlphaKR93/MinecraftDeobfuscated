/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Supplier
 */
package net.minecraft.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.function.Supplier;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public abstract class AbstractScrollWidget
extends AbstractWidget
implements Renderable,
GuiEventListener {
    private static final int BORDER_COLOR_FOCUSED = -1;
    private static final int BORDER_COLOR = -6250336;
    private static final int BACKGROUND_COLOR = -16777216;
    private static final int INNER_PADDING = 4;
    private double scrollAmount;
    private boolean scrolling;

    public AbstractScrollWidget(int $$0, int $$1, int $$2, int $$3, Component $$4) {
        super($$0, $$1, $$2, $$3, $$4);
    }

    @Override
    public boolean mouseClicked(double $$0, double $$1, int $$2) {
        if (!this.visible) {
            return false;
        }
        boolean $$3 = this.withinContentAreaPoint($$0, $$1);
        boolean $$4 = this.scrollbarVisible() && $$0 >= (double)(this.getX() + this.width) && $$0 <= (double)(this.getX() + this.width + 8) && $$1 >= (double)this.getY() && $$1 < (double)(this.getY() + this.height);
        this.setFocused($$3 || $$4);
        if ($$4 && $$2 == 0) {
            this.scrolling = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double $$0, double $$1, int $$2) {
        if ($$2 == 0) {
            this.scrolling = false;
        }
        return super.mouseReleased($$0, $$1, $$2);
    }

    @Override
    public boolean mouseDragged(double $$0, double $$1, int $$2, double $$3, double $$4) {
        if (!(this.visible && this.isFocused() && this.scrolling)) {
            return false;
        }
        if ($$1 < (double)this.getY()) {
            this.setScrollAmount(0.0);
        } else if ($$1 > (double)(this.getY() + this.height)) {
            this.setScrollAmount(this.getMaxScrollAmount());
        } else {
            int $$5 = this.getScrollBarHeight();
            double $$6 = Math.max((int)1, (int)(this.getMaxScrollAmount() / (this.height - $$5)));
            this.setScrollAmount(this.scrollAmount + $$4 * $$6);
        }
        return true;
    }

    @Override
    public boolean mouseScrolled(double $$0, double $$1, double $$2) {
        if (!this.visible) {
            return false;
        }
        this.setScrollAmount(this.scrollAmount - $$2 * this.scrollRate());
        return true;
    }

    @Override
    public void renderButton(PoseStack $$0, int $$1, int $$2, float $$3) {
        if (!this.visible) {
            return;
        }
        this.renderBackground($$0);
        AbstractScrollWidget.enableScissor(this.getX() + 1, this.getY() + 1, this.getX() + this.width - 1, this.getY() + this.height - 1);
        $$0.pushPose();
        $$0.translate(0.0, -this.scrollAmount, 0.0);
        this.renderContents($$0, $$1, $$2, $$3);
        $$0.popPose();
        AbstractScrollWidget.disableScissor();
        this.renderDecorations($$0);
    }

    private int getScrollBarHeight() {
        return Mth.clamp((int)((float)(this.height * this.height) / (float)this.getContentHeight()), 32, this.height);
    }

    protected void renderDecorations(PoseStack $$0) {
        if (this.scrollbarVisible()) {
            this.renderScrollBar();
        }
    }

    protected int innerPadding() {
        return 4;
    }

    protected int totalInnerPadding() {
        return this.innerPadding() * 2;
    }

    protected double scrollAmount() {
        return this.scrollAmount;
    }

    protected void setScrollAmount(double $$0) {
        this.scrollAmount = Mth.clamp($$0, 0.0, (double)this.getMaxScrollAmount());
    }

    protected int getMaxScrollAmount() {
        return Math.max((int)0, (int)(this.getContentHeight() - (this.height - 4)));
    }

    private int getContentHeight() {
        return this.getInnerHeight() + 4;
    }

    private void renderBackground(PoseStack $$0) {
        int $$1 = this.isFocused() ? -1 : -6250336;
        AbstractScrollWidget.fill($$0, this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, $$1);
        AbstractScrollWidget.fill($$0, this.getX() + 1, this.getY() + 1, this.getX() + this.width - 1, this.getY() + this.height - 1, -16777216);
    }

    private void renderScrollBar() {
        int $$0 = this.getScrollBarHeight();
        int $$1 = this.getX() + this.width;
        int $$2 = this.getX() + this.width + 8;
        int $$3 = Math.max((int)this.getY(), (int)((int)this.scrollAmount * (this.height - $$0) / this.getMaxScrollAmount() + this.getY()));
        int $$4 = $$3 + $$0;
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionColorShader));
        Tesselator $$5 = Tesselator.getInstance();
        BufferBuilder $$6 = $$5.getBuilder();
        $$6.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        $$6.vertex($$1, $$4, 0.0).color(128, 128, 128, 255).endVertex();
        $$6.vertex($$2, $$4, 0.0).color(128, 128, 128, 255).endVertex();
        $$6.vertex($$2, $$3, 0.0).color(128, 128, 128, 255).endVertex();
        $$6.vertex($$1, $$3, 0.0).color(128, 128, 128, 255).endVertex();
        $$6.vertex($$1, $$4 - 1, 0.0).color(192, 192, 192, 255).endVertex();
        $$6.vertex($$2 - 1, $$4 - 1, 0.0).color(192, 192, 192, 255).endVertex();
        $$6.vertex($$2 - 1, $$3, 0.0).color(192, 192, 192, 255).endVertex();
        $$6.vertex($$1, $$3, 0.0).color(192, 192, 192, 255).endVertex();
        $$5.end();
    }

    protected boolean withinContentAreaTopBottom(int $$0, int $$1) {
        return (double)$$1 - this.scrollAmount >= (double)this.getY() && (double)$$0 - this.scrollAmount <= (double)(this.getY() + this.height);
    }

    protected boolean withinContentAreaPoint(double $$0, double $$1) {
        return $$0 >= (double)this.getX() && $$0 < (double)(this.getX() + this.width) && $$1 >= (double)this.getY() && $$1 < (double)(this.getY() + this.height);
    }

    protected abstract int getInnerHeight();

    protected abstract boolean scrollbarVisible();

    protected abstract double scrollRate();

    protected abstract void renderContents(PoseStack var1, int var2, int var3, float var4);
}