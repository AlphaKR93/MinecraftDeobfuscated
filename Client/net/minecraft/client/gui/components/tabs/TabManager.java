/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.util.Objects
 *  java.util.function.Consumer
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.components.tabs;

import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.navigation.ScreenRectangle;

public class TabManager {
    private final Consumer<AbstractWidget> addWidget;
    private final Consumer<AbstractWidget> removeWidget;
    @Nullable
    private Tab currentTab;
    @Nullable
    private ScreenRectangle tabArea;

    public TabManager(Consumer<AbstractWidget> $$0, Consumer<AbstractWidget> $$1) {
        this.addWidget = $$0;
        this.removeWidget = $$1;
    }

    public void setTabArea(ScreenRectangle $$0) {
        this.tabArea = $$0;
        Tab $$1 = this.getCurrentTab();
        if ($$1 != null) {
            $$1.doLayout($$0);
        }
    }

    public void setCurrentTab(Tab $$0) {
        if (!Objects.equals((Object)this.currentTab, (Object)$$0)) {
            if (this.currentTab != null) {
                this.currentTab.visitChildren(this.removeWidget);
            }
            this.currentTab = $$0;
            $$0.visitChildren(this.addWidget);
            if (this.tabArea != null) {
                $$0.doLayout(this.tabArea);
            }
        }
    }

    @Nullable
    public Tab getCurrentTab() {
        return this.currentTab;
    }

    public void tickCurrent() {
        Tab $$0 = this.getCurrentTab();
        if ($$0 != null) {
            $$0.tick();
        }
    }
}