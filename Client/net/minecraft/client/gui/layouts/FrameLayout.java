/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.function.Consumer
 */
package net.minecraft.client.gui.layouts;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.gui.layouts.AbstractLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.util.Mth;

public class FrameLayout
extends AbstractLayout {
    private final List<ChildContainer> children = new ArrayList();
    private int minWidth;
    private int minHeight;
    private final LayoutSettings defaultChildLayoutSettings = LayoutSettings.defaults().align(0.5f, 0.5f);

    public static FrameLayout withMinDimensions(int $$0, int $$1) {
        return new FrameLayout(0, 0, 0, 0).setMinDimensions($$0, $$1);
    }

    public FrameLayout() {
        this(0, 0, 0, 0);
    }

    public FrameLayout(int $$0, int $$1, int $$2, int $$3) {
        super($$0, $$1, $$2, $$3);
    }

    public FrameLayout setMinDimensions(int $$0, int $$1) {
        return this.setMinWidth($$0).setMinHeight($$1);
    }

    public FrameLayout setMinHeight(int $$0) {
        this.minHeight = $$0;
        return this;
    }

    public FrameLayout setMinWidth(int $$0) {
        this.minWidth = $$0;
        return this;
    }

    public LayoutSettings newChildLayoutSettings() {
        return this.defaultChildLayoutSettings.copy();
    }

    public LayoutSettings defaultChildLayoutSetting() {
        return this.defaultChildLayoutSettings;
    }

    @Override
    public void arrangeElements() {
        super.arrangeElements();
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

    public <T extends LayoutElement> T addChild(T $$0) {
        return this.addChild($$0, this.newChildLayoutSettings());
    }

    public <T extends LayoutElement> T addChild(T $$0, LayoutSettings $$1) {
        this.children.add((Object)new ChildContainer($$0, $$1));
        return $$0;
    }

    @Override
    protected void visitChildren(Consumer<LayoutElement> $$0) {
        this.children.forEach($$1 -> $$0.accept((Object)$$1.child));
    }

    public static void centerInRectangle(LayoutElement $$0, int $$1, int $$2, int $$3, int $$4) {
        FrameLayout.alignInRectangle($$0, $$1, $$2, $$3, $$4, 0.5f, 0.5f);
    }

    public static void centerInRectangle(LayoutElement $$0, ScreenRectangle $$1) {
        FrameLayout.centerInRectangle($$0, $$1.position().x(), $$1.position().y(), $$1.width(), $$1.height());
    }

    public static void alignInRectangle(LayoutElement $$0, int $$1, int $$2, int $$3, int $$4, float $$5, float $$6) {
        FrameLayout.alignInDimension($$1, $$3, $$0.getWidth(), (Consumer<Integer>)((Consumer)$$0::setX), $$5);
        FrameLayout.alignInDimension($$2, $$4, $$0.getHeight(), (Consumer<Integer>)((Consumer)$$0::setY), $$6);
    }

    public static void alignInDimension(int $$0, int $$1, int $$2, Consumer<Integer> $$3, float $$4) {
        int $$5 = (int)Mth.lerp($$4, 0.0f, $$1 - $$2);
        $$3.accept((Object)($$0 + $$5));
    }

    static class ChildContainer
    extends AbstractLayout.AbstractChildWrapper {
        protected ChildContainer(LayoutElement $$0, LayoutSettings $$1) {
            super($$0, $$1);
        }
    }
}