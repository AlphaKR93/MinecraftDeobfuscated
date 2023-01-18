/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.Optional
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.components;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.LayoutSettings;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarrationSupplier;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public abstract class AbstractContainerWidget
extends AbstractWidget
implements ContainerEventHandler {
    @Nullable
    private GuiEventListener focused;
    private boolean dragging;

    public AbstractContainerWidget(int $$0, int $$1, int $$2, int $$3, Component $$4) {
        super($$0, $$1, $$2, $$3, $$4);
    }

    @Override
    public void renderButton(PoseStack $$0, int $$1, int $$2, float $$3) {
        for (AbstractWidget $$4 : this.getContainedChildren()) {
            $$4.render($$0, $$1, $$2, $$3);
        }
    }

    @Override
    public boolean isMouseOver(double $$0, double $$1) {
        for (AbstractWidget $$2 : this.getContainedChildren()) {
            if (!$$2.isMouseOver($$0, $$1)) continue;
            return true;
        }
        return false;
    }

    @Override
    public void mouseMoved(double $$0, double $$1) {
        this.getContainedChildren().forEach($$2 -> $$2.mouseMoved($$0, $$1));
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return this.getContainedChildren();
    }

    protected abstract List<? extends AbstractWidget> getContainedChildren();

    @Override
    public boolean isDragging() {
        return this.dragging;
    }

    @Override
    public void setDragging(boolean $$0) {
        this.dragging = $$0;
    }

    @Override
    public boolean mouseScrolled(double $$0, double $$1, double $$2) {
        boolean $$3 = false;
        for (AbstractWidget $$4 : this.getContainedChildren()) {
            if (!$$4.isMouseOver($$0, $$1) || !$$4.mouseScrolled($$0, $$1, $$2)) continue;
            $$3 = true;
        }
        return $$3 || super.mouseScrolled($$0, $$1, $$2);
    }

    @Override
    public boolean changeFocus(boolean $$0) {
        return ContainerEventHandler.super.changeFocus($$0);
    }

    @Nullable
    protected GuiEventListener getHovered() {
        for (AbstractWidget $$0 : this.getContainedChildren()) {
            if (!$$0.isHovered) continue;
            return $$0;
        }
        return null;
    }

    @Override
    @Nullable
    public GuiEventListener getFocused() {
        return this.focused;
    }

    @Override
    public void setFocused(@Nullable GuiEventListener $$0) {
        this.focused = $$0;
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput $$0) {
        GuiEventListener $$1 = this.getHovered();
        if ($$1 != null) {
            if ($$1 instanceof NarrationSupplier) {
                NarrationSupplier $$2 = (NarrationSupplier)((Object)$$1);
                $$2.updateNarration($$0.nest());
            }
        } else {
            GuiEventListener $$3 = this.getFocused();
            if ($$3 != null && $$3 instanceof NarrationSupplier) {
                NarrationSupplier $$4 = (NarrationSupplier)((Object)$$3);
                $$4.updateNarration($$0.nest());
            }
        }
    }

    @Override
    public NarratableEntry.NarrationPriority narrationPriority() {
        if (this.isHovered || this.getHovered() != null) {
            return NarratableEntry.NarrationPriority.HOVERED;
        }
        if (this.focused != null) {
            return NarratableEntry.NarrationPriority.FOCUSED;
        }
        return super.narrationPriority();
    }

    @Override
    public void setX(int $$0) {
        for (AbstractWidget $$1 : this.getContainedChildren()) {
            int $$2 = $$1.getX() + ($$0 - this.getX());
            $$1.setX($$2);
        }
        super.setX($$0);
    }

    @Override
    public void setY(int $$0) {
        for (AbstractWidget $$1 : this.getContainedChildren()) {
            int $$2 = $$1.getY() + ($$0 - this.getY());
            $$1.setY($$2);
        }
        super.setY($$0);
    }

    @Override
    public Optional<GuiEventListener> getChildAt(double $$0, double $$1) {
        return ContainerEventHandler.super.getChildAt($$0, $$1);
    }

    @Override
    public boolean mouseClicked(double $$0, double $$1, int $$2) {
        return ContainerEventHandler.super.mouseClicked($$0, $$1, $$2);
    }

    @Override
    public boolean mouseReleased(double $$0, double $$1, int $$2) {
        return ContainerEventHandler.super.mouseReleased($$0, $$1, $$2);
    }

    @Override
    public boolean mouseDragged(double $$0, double $$1, int $$2, double $$3, double $$4) {
        return ContainerEventHandler.super.mouseDragged($$0, $$1, $$2, $$3, $$4);
    }

    protected static abstract class AbstractChildWrapper {
        public final AbstractWidget child;
        public final LayoutSettings.LayoutSettingsImpl layoutSettings;

        protected AbstractChildWrapper(AbstractWidget $$0, LayoutSettings $$1) {
            this.child = $$0;
            this.layoutSettings = $$1.getExposed();
        }

        public int getHeight() {
            return this.child.getHeight() + this.layoutSettings.paddingTop + this.layoutSettings.paddingBottom;
        }

        public int getWidth() {
            return this.child.getWidth() + this.layoutSettings.paddingLeft + this.layoutSettings.paddingRight;
        }

        public void setX(int $$0, int $$1) {
            float $$2 = this.layoutSettings.paddingLeft;
            float $$3 = $$1 - this.child.getWidth() - this.layoutSettings.paddingRight;
            int $$4 = (int)Mth.lerp(this.layoutSettings.xAlignment, $$2, $$3);
            this.child.setX($$4 + $$0);
        }

        public void setY(int $$0, int $$1) {
            float $$2 = this.layoutSettings.paddingTop;
            float $$3 = $$1 - this.child.getHeight() - this.layoutSettings.paddingBottom;
            int $$4 = (int)Mth.lerp(this.layoutSettings.yAlignment, $$2, $$3);
            this.child.setY($$4 + $$0);
        }
    }
}