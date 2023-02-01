/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Consumer
 */
package net.minecraft.client.gui.components.tabs;

import java.util.function.Consumer;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;

public class GridLayoutTab
implements Tab {
    private final Component title;
    protected final GridLayout layout = new GridLayout();

    public GridLayoutTab(Component $$0) {
        this.title = $$0;
    }

    @Override
    public Component getTabTitle() {
        return this.title;
    }

    @Override
    public void visitChildren(Consumer<AbstractWidget> $$0) {
        this.layout.visitWidgets($$0);
    }

    @Override
    public void doLayout(ScreenRectangle $$0) {
        this.layout.arrangeElements();
        FrameLayout.centerInRectangle(this.layout, $$0);
    }
}