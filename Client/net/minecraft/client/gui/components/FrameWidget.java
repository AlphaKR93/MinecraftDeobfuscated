/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.util.ArrayList
 *  java.util.Collections
 *  java.util.List
 *  java.util.function.Consumer
 */
package net.minecraft.client.gui.components;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.LayoutSettings;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class FrameWidget
extends AbstractContainerWidget {
    private final List<ChildContainer> children = new ArrayList();
    private final List<AbstractWidget> containedChildrenView = Collections.unmodifiableList((List)Lists.transform(this.children, $$0 -> $$0.child));
    private int minWidth;
    private int minHeight;
    private final LayoutSettings defaultChildLayoutSettings = LayoutSettings.defaults().align(0.5f, 0.5f);

    public static FrameWidget withMinDimensions(int $$0, int $$1) {
        return new FrameWidget(0, 0, 0, 0).setMinDimensions($$0, $$1);
    }

    public FrameWidget() {
        this(0, 0, 0, 0);
    }

    public FrameWidget(int $$02, int $$1, int $$2, int $$3) {
        super($$02, $$1, $$2, $$3, Component.empty());
    }

    public FrameWidget setMinDimensions(int $$0, int $$1) {
        return this.setMinWidth($$0).setMinHeight($$1);
    }

    public FrameWidget setMinHeight(int $$0) {
        this.minHeight = $$0;
        return this;
    }

    public FrameWidget setMinWidth(int $$0) {
        this.minWidth = $$0;
        return this;
    }

    public LayoutSettings newChildLayoutSettings() {
        return this.defaultChildLayoutSettings.copy();
    }

    public LayoutSettings defaultChildLayoutSetting() {
        return this.defaultChildLayoutSettings;
    }

    public void pack() {
        int $$0 = this.minWidth;
        int $$1 = this.minHeight;
        for (ChildContainer $$2 : this.children) {
            $$0 = Math.max((int)$$0, (int)$$2.getWidth());
            $$1 = Math.max((int)$$1, (int)$$2.getHeight());
        }
        for (ChildContainer $$3 : this.children) {
            $$3.setX(this.getX(), $$0);
            $$3.setY(this.getY(), $$1);
        }
        this.width = $$0;
        this.height = $$1;
    }

    public <T extends AbstractWidget> T addChild(T $$0) {
        return this.addChild($$0, this.newChildLayoutSettings());
    }

    public <T extends AbstractWidget> T addChild(T $$0, LayoutSettings $$1) {
        this.children.add((Object)new ChildContainer($$0, $$1));
        return $$0;
    }

    protected List<AbstractWidget> getContainedChildren() {
        return this.containedChildrenView;
    }

    public static void centerInRectangle(AbstractWidget $$0, int $$1, int $$2, int $$3, int $$4) {
        FrameWidget.alignInRectangle($$0, $$1, $$2, $$3, $$4, 0.5f, 0.5f);
    }

    public static void alignInRectangle(AbstractWidget $$0, int $$1, int $$2, int $$3, int $$4, float $$5, float $$6) {
        FrameWidget.alignInDimension($$1, $$3, $$0.getWidth(), (Consumer<Integer>)((Consumer)$$0::setX), $$5);
        FrameWidget.alignInDimension($$2, $$4, $$0.getHeight(), (Consumer<Integer>)((Consumer)$$0::setY), $$6);
    }

    public static void alignInDimension(int $$0, int $$1, int $$2, Consumer<Integer> $$3, float $$4) {
        int $$5 = (int)Mth.lerp($$4, 0.0f, $$1 - $$2);
        $$3.accept((Object)($$0 + $$5));
    }

    static class ChildContainer
    extends AbstractContainerWidget.AbstractChildWrapper {
        protected ChildContainer(AbstractWidget $$0, LayoutSettings $$1) {
            super($$0, $$1);
        }
    }
}