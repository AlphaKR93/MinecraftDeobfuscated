/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.lang.Deprecated
 *  java.lang.IncompatibleClassChangeError
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.AbstractList
 *  java.util.Collection
 *  java.util.List
 *  java.util.Objects
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.components;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public abstract class AbstractSelectionList<E extends Entry<E>>
extends AbstractContainerEventHandler
implements Renderable,
NarratableEntry {
    protected final Minecraft minecraft;
    protected final int itemHeight;
    private final List<E> children = new TrackedList();
    protected int width;
    protected int height;
    protected int y0;
    protected int y1;
    protected int x1;
    protected int x0;
    protected boolean centerListVertically = true;
    private double scrollAmount;
    private boolean renderSelection = true;
    private boolean renderHeader;
    protected int headerHeight;
    private boolean scrolling;
    @Nullable
    private E selected;
    private boolean renderBackground = true;
    private boolean renderTopAndBottom = true;
    @Nullable
    private E hovered;

    public AbstractSelectionList(Minecraft $$0, int $$1, int $$2, int $$3, int $$4, int $$5) {
        this.minecraft = $$0;
        this.width = $$1;
        this.height = $$2;
        this.y0 = $$3;
        this.y1 = $$4;
        this.itemHeight = $$5;
        this.x0 = 0;
        this.x1 = $$1;
    }

    public void setRenderSelection(boolean $$0) {
        this.renderSelection = $$0;
    }

    protected void setRenderHeader(boolean $$0, int $$1) {
        this.renderHeader = $$0;
        this.headerHeight = $$1;
        if (!$$0) {
            this.headerHeight = 0;
        }
    }

    public int getRowWidth() {
        return 220;
    }

    @Nullable
    public E getSelected() {
        return this.selected;
    }

    public void setSelected(@Nullable E $$0) {
        this.selected = $$0;
    }

    public E getFirstElement() {
        return (E)((Entry)this.children.get(0));
    }

    public void setRenderBackground(boolean $$0) {
        this.renderBackground = $$0;
    }

    public void setRenderTopAndBottom(boolean $$0) {
        this.renderTopAndBottom = $$0;
    }

    @Nullable
    public E getFocused() {
        return (E)((Entry)super.getFocused());
    }

    public final List<E> children() {
        return this.children;
    }

    protected final void clearEntries() {
        this.children.clear();
        this.selected = null;
    }

    protected void replaceEntries(Collection<E> $$0) {
        this.clearEntries();
        this.children.addAll($$0);
    }

    protected E getEntry(int $$0) {
        return (E)((Entry)this.children().get($$0));
    }

    protected int addEntry(E $$0) {
        this.children.add($$0);
        return this.children.size() - 1;
    }

    protected void addEntryToTop(E $$0) {
        double $$1 = (double)this.getMaxScroll() - this.getScrollAmount();
        this.children.add(0, $$0);
        this.setScrollAmount((double)this.getMaxScroll() - $$1);
    }

    protected boolean removeEntryFromTop(E $$0) {
        double $$1 = (double)this.getMaxScroll() - this.getScrollAmount();
        boolean $$2 = this.removeEntry($$0);
        this.setScrollAmount((double)this.getMaxScroll() - $$1);
        return $$2;
    }

    protected int getItemCount() {
        return this.children().size();
    }

    protected boolean isSelectedItem(int $$0) {
        return Objects.equals(this.getSelected(), (Object)this.children().get($$0));
    }

    @Nullable
    protected final E getEntryAtPosition(double $$0, double $$1) {
        int $$2 = this.getRowWidth() / 2;
        int $$3 = this.x0 + this.width / 2;
        int $$4 = $$3 - $$2;
        int $$5 = $$3 + $$2;
        int $$6 = Mth.floor($$1 - (double)this.y0) - this.headerHeight + (int)this.getScrollAmount() - 4;
        int $$7 = $$6 / this.itemHeight;
        if ($$0 < (double)this.getScrollbarPosition() && $$0 >= (double)$$4 && $$0 <= (double)$$5 && $$7 >= 0 && $$6 >= 0 && $$7 < this.getItemCount()) {
            return (E)((Entry)this.children().get($$7));
        }
        return null;
    }

    public void updateSize(int $$0, int $$1, int $$2, int $$3) {
        this.width = $$0;
        this.height = $$1;
        this.y0 = $$2;
        this.y1 = $$3;
        this.x0 = 0;
        this.x1 = $$0;
    }

    public void setLeftPos(int $$0) {
        this.x0 = $$0;
        this.x1 = $$0 + this.width;
    }

    protected int getMaxPosition() {
        return this.getItemCount() * this.itemHeight + this.headerHeight;
    }

    protected void clickedHeader(int $$0, int $$1) {
    }

    protected void renderHeader(PoseStack $$0, int $$1, int $$2) {
    }

    protected void renderBackground(PoseStack $$0) {
    }

    protected void renderDecorations(PoseStack $$0, int $$1, int $$2) {
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        int $$12;
        this.renderBackground($$0);
        int $$4 = this.getScrollbarPosition();
        int $$5 = $$4 + 6;
        this.hovered = this.isMouseOver($$1, $$2) ? this.getEntryAtPosition($$1, $$2) : null;
        Object v0 = this.hovered;
        if (this.renderBackground) {
            RenderSystem.setShaderTexture(0, GuiComponent.BACKGROUND_LOCATION);
            RenderSystem.setShaderColor(0.125f, 0.125f, 0.125f, 1.0f);
            int $$6 = 32;
            AbstractSelectionList.blit($$0, this.x0, this.y0, this.x1, this.y1 + (int)this.getScrollAmount(), this.x1 - this.x0, this.y1 - this.y0, 32, 32);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
        int $$7 = this.getRowLeft();
        int $$8 = this.y0 + 4 - (int)this.getScrollAmount();
        if (this.renderHeader) {
            this.renderHeader($$0, $$7, $$8);
        }
        this.renderList($$0, $$1, $$2, $$3);
        if (this.renderTopAndBottom) {
            RenderSystem.setShaderTexture(0, GuiComponent.BACKGROUND_LOCATION);
            RenderSystem.enableDepthTest();
            RenderSystem.depthFunc(519);
            int $$9 = 32;
            int $$10 = -100;
            RenderSystem.setShaderColor(0.25f, 0.25f, 0.25f, 1.0f);
            AbstractSelectionList.blit($$0, this.x0, 0, -100, 0.0f, 0.0f, this.width, this.y0, 32, 32);
            AbstractSelectionList.blit($$0, this.x0, this.y1, -100, 0.0f, this.y1, this.width, this.height - this.y1, 32, 32);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.depthFunc(515);
            RenderSystem.disableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
            int $$11 = 4;
            this.fillGradient($$0, this.x0, this.y0, this.x1, this.y0 + 4, -16777216, 0);
            this.fillGradient($$0, this.x0, this.y1 - 4, this.x1, this.y1, 0, -16777216);
        }
        if (($$12 = this.getMaxScroll()) > 0) {
            int $$13 = (int)((float)((this.y1 - this.y0) * (this.y1 - this.y0)) / (float)this.getMaxPosition());
            $$13 = Mth.clamp($$13, 32, this.y1 - this.y0 - 8);
            int $$14 = (int)this.getScrollAmount() * (this.y1 - this.y0 - $$13) / $$12 + this.y0;
            if ($$14 < this.y0) {
                $$14 = this.y0;
            }
            AbstractSelectionList.fill($$0, $$4, this.y0, $$5, this.y1, -16777216);
            AbstractSelectionList.fill($$0, $$4, $$14, $$5, $$14 + $$13, -8355712);
            AbstractSelectionList.fill($$0, $$4, $$14, $$5 - 1, $$14 + $$13 - 1, -4144960);
        }
        this.renderDecorations($$0, $$1, $$2);
        RenderSystem.disableBlend();
    }

    protected void centerScrollOn(E $$0) {
        this.setScrollAmount(this.children().indexOf($$0) * this.itemHeight + this.itemHeight / 2 - (this.y1 - this.y0) / 2);
    }

    protected void ensureVisible(E $$0) {
        int $$3;
        int $$1 = this.getRowTop(this.children().indexOf($$0));
        int $$2 = $$1 - this.y0 - 4 - this.itemHeight;
        if ($$2 < 0) {
            this.scroll($$2);
        }
        if (($$3 = this.y1 - $$1 - this.itemHeight - this.itemHeight) < 0) {
            this.scroll(-$$3);
        }
    }

    private void scroll(int $$0) {
        this.setScrollAmount(this.getScrollAmount() + (double)$$0);
    }

    public double getScrollAmount() {
        return this.scrollAmount;
    }

    public void setScrollAmount(double $$0) {
        this.scrollAmount = Mth.clamp($$0, 0.0, (double)this.getMaxScroll());
    }

    public int getMaxScroll() {
        return Math.max((int)0, (int)(this.getMaxPosition() - (this.y1 - this.y0 - 4)));
    }

    public int getScrollBottom() {
        return (int)this.getScrollAmount() - this.height - this.headerHeight;
    }

    protected void updateScrollingState(double $$0, double $$1, int $$2) {
        this.scrolling = $$2 == 0 && $$0 >= (double)this.getScrollbarPosition() && $$0 < (double)(this.getScrollbarPosition() + 6);
    }

    protected int getScrollbarPosition() {
        return this.width / 2 + 124;
    }

    @Override
    public boolean mouseClicked(double $$0, double $$1, int $$2) {
        this.updateScrollingState($$0, $$1, $$2);
        if (!this.isMouseOver($$0, $$1)) {
            return false;
        }
        E $$3 = this.getEntryAtPosition($$0, $$1);
        if ($$3 != null) {
            if ($$3.mouseClicked($$0, $$1, $$2)) {
                GuiEventListener $$4 = this.getFocused();
                if ($$4 != $$3 && $$4 instanceof ContainerEventHandler) {
                    ContainerEventHandler $$5 = (ContainerEventHandler)$$4;
                    $$5.setFocused(null);
                }
                this.setFocused((GuiEventListener)$$3);
                this.setDragging(true);
                return true;
            }
        } else if ($$2 == 0) {
            this.clickedHeader((int)($$0 - (double)(this.x0 + this.width / 2 - this.getRowWidth() / 2)), (int)($$1 - (double)this.y0) + (int)this.getScrollAmount() - 4);
            return true;
        }
        return this.scrolling;
    }

    @Override
    public boolean mouseReleased(double $$0, double $$1, int $$2) {
        if (this.getFocused() != null) {
            this.getFocused().mouseReleased($$0, $$1, $$2);
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double $$0, double $$1, int $$2, double $$3, double $$4) {
        if (super.mouseDragged($$0, $$1, $$2, $$3, $$4)) {
            return true;
        }
        if ($$2 != 0 || !this.scrolling) {
            return false;
        }
        if ($$1 < (double)this.y0) {
            this.setScrollAmount(0.0);
        } else if ($$1 > (double)this.y1) {
            this.setScrollAmount(this.getMaxScroll());
        } else {
            double $$5 = Math.max((int)1, (int)this.getMaxScroll());
            int $$6 = this.y1 - this.y0;
            int $$7 = Mth.clamp((int)((float)($$6 * $$6) / (float)this.getMaxPosition()), 32, $$6 - 8);
            double $$8 = Math.max((double)1.0, (double)($$5 / (double)($$6 - $$7)));
            this.setScrollAmount(this.getScrollAmount() + $$4 * $$8);
        }
        return true;
    }

    @Override
    public boolean mouseScrolled(double $$0, double $$1, double $$2) {
        this.setScrollAmount(this.getScrollAmount() - $$2 * (double)this.itemHeight / 2.0);
        return true;
    }

    @Override
    public void setFocused(@Nullable GuiEventListener $$0) {
        super.setFocused($$0);
        int $$1 = this.children.indexOf((Object)$$0);
        if ($$1 >= 0) {
            Entry $$2 = (Entry)this.children.get($$1);
            this.setSelected($$2);
            if (this.minecraft.getLastInputType().isKeyboard()) {
                this.ensureVisible($$2);
            }
        }
    }

    @Nullable
    protected E nextEntry(ScreenDirection $$02) {
        return this.nextEntry($$02, $$0 -> true);
    }

    @Nullable
    protected E nextEntry(ScreenDirection $$0, Predicate<E> $$1) {
        return this.nextEntry($$0, $$1, this.getSelected());
    }

    @Nullable
    protected E nextEntry(ScreenDirection $$0, Predicate<E> $$1, @Nullable E $$2) {
        int $$3;
        switch ($$0) {
            default: {
                throw new IncompatibleClassChangeError();
            }
            case RIGHT: 
            case LEFT: {
                int n = 0;
                break;
            }
            case UP: {
                int n = -1;
                break;
            }
            case DOWN: {
                int n = $$3 = 1;
            }
        }
        if (!this.children().isEmpty() && $$3 != 0) {
            if ($$2 == null) {
                int $$4 = $$3 > 0 ? 0 : this.children().size() - 1;
            } else {
                int $$5 = this.children().indexOf($$2) + $$3;
            }
            for (void $$6 = $$5; $$6 >= 0 && $$6 < this.children.size(); $$6 += $$3) {
                Entry $$7 = (Entry)this.children().get((int)$$6);
                if (!$$1.test((Object)$$7)) continue;
                return (E)$$7;
            }
        }
        return null;
    }

    @Override
    public boolean isMouseOver(double $$0, double $$1) {
        return $$1 >= (double)this.y0 && $$1 <= (double)this.y1 && $$0 >= (double)this.x0 && $$0 <= (double)this.x1;
    }

    protected void renderList(PoseStack $$0, int $$1, int $$2, float $$3) {
        int $$4 = this.getRowLeft();
        int $$5 = this.getRowWidth();
        int $$6 = this.itemHeight - 4;
        int $$7 = this.getItemCount();
        for (int $$8 = 0; $$8 < $$7; ++$$8) {
            int $$9 = this.getRowTop($$8);
            int $$10 = this.getRowBottom($$8);
            if ($$10 < this.y0 || $$9 > this.y1) continue;
            this.renderItem($$0, $$1, $$2, $$3, $$8, $$4, $$9, $$5, $$6);
        }
    }

    protected void renderItem(PoseStack $$0, int $$1, int $$2, float $$3, int $$4, int $$5, int $$6, int $$7, int $$8) {
        E $$9 = this.getEntry($$4);
        if (this.renderSelection && this.isSelectedItem($$4)) {
            int $$10 = this.isFocused() ? -1 : -8355712;
            this.renderSelection($$0, $$6, $$7, $$8, $$10, -16777216);
        }
        ((Entry)$$9).render($$0, $$4, $$6, $$5, $$7, $$8, $$1, $$2, Objects.equals(this.hovered, $$9), $$3);
    }

    protected void renderSelection(PoseStack $$0, int $$1, int $$2, int $$3, int $$4, int $$5) {
        int $$6 = this.x0 + (this.width - $$2) / 2;
        int $$7 = this.x0 + (this.width + $$2) / 2;
        AbstractSelectionList.fill($$0, $$6, $$1 - 2, $$7, $$1 + $$3 + 2, $$4);
        AbstractSelectionList.fill($$0, $$6 + 1, $$1 - 1, $$7 - 1, $$1 + $$3 + 1, $$5);
    }

    public int getRowLeft() {
        return this.x0 + this.width / 2 - this.getRowWidth() / 2 + 2;
    }

    public int getRowRight() {
        return this.getRowLeft() + this.getRowWidth();
    }

    protected int getRowTop(int $$0) {
        return this.y0 + 4 - (int)this.getScrollAmount() + $$0 * this.itemHeight + this.headerHeight;
    }

    protected int getRowBottom(int $$0) {
        return this.getRowTop($$0) + this.itemHeight;
    }

    @Override
    public NarratableEntry.NarrationPriority narrationPriority() {
        if (this.isFocused()) {
            return NarratableEntry.NarrationPriority.FOCUSED;
        }
        if (this.hovered != null) {
            return NarratableEntry.NarrationPriority.HOVERED;
        }
        return NarratableEntry.NarrationPriority.NONE;
    }

    @Nullable
    protected E remove(int $$0) {
        Entry $$1 = (Entry)this.children.get($$0);
        if (this.removeEntry((Entry)this.children.get($$0))) {
            return (E)$$1;
        }
        return null;
    }

    protected boolean removeEntry(E $$0) {
        boolean $$1 = this.children.remove($$0);
        if ($$1 && $$0 == this.getSelected()) {
            this.setSelected(null);
        }
        return $$1;
    }

    @Nullable
    protected E getHovered() {
        return this.hovered;
    }

    void bindEntryToSelf(Entry<E> $$0) {
        $$0.list = this;
    }

    protected void narrateListElementPosition(NarrationElementOutput $$0, E $$1) {
        int $$3;
        List<E> $$2 = this.children();
        if ($$2.size() > 1 && ($$3 = $$2.indexOf($$1)) != -1) {
            $$0.add(NarratedElementType.POSITION, (Component)Component.translatable("narrator.position.list", $$3 + 1, $$2.size()));
        }
    }

    @Override
    public ScreenRectangle getRectangle() {
        return new ScreenRectangle(this.x0, this.y0, this.x1 - this.x0, this.y1 - this.y0);
    }

    class TrackedList
    extends AbstractList<E> {
        private final List<E> delegate = Lists.newArrayList();

        TrackedList() {
        }

        public E get(int $$0) {
            return (Entry)this.delegate.get($$0);
        }

        public int size() {
            return this.delegate.size();
        }

        public E set(int $$0, E $$1) {
            Entry $$2 = (Entry)this.delegate.set($$0, $$1);
            AbstractSelectionList.this.bindEntryToSelf($$1);
            return $$2;
        }

        public void add(int $$0, E $$1) {
            this.delegate.add($$0, $$1);
            AbstractSelectionList.this.bindEntryToSelf($$1);
        }

        public E remove(int $$0) {
            return (Entry)this.delegate.remove($$0);
        }
    }

    protected static abstract class Entry<E extends Entry<E>>
    implements GuiEventListener {
        @Deprecated
        AbstractSelectionList<E> list;

        protected Entry() {
        }

        @Override
        public void setFocused(boolean $$0) {
        }

        @Override
        public boolean isFocused() {
            return this.list.getFocused() == this;
        }

        public abstract void render(PoseStack var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10);

        @Override
        public boolean isMouseOver(double $$0, double $$1) {
            return Objects.equals(this.list.getEntryAtPosition($$0, $$1), (Object)this);
        }
    }
}