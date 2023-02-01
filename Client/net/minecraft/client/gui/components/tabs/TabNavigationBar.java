/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.ArrayList
 *  java.util.Collections
 *  java.util.List
 *  java.util.Optional
 */
package net.minecraft.client.gui.components.tabs;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.math.Divisor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.network.chat.Component;

public class TabNavigationBar
extends GridLayout {
    private int width;
    private final TabManager tabManager;
    private final ImmutableList<Tab> tabs;
    private final ImmutableMap<Tab, Button> tabButtons;

    public void setWidth(int $$0) {
        this.width = $$0;
    }

    public static Builder builder(TabManager $$0, int $$1) {
        return new Builder($$0, $$1);
    }

    TabNavigationBar(int $$0, int $$12, int $$2, TabManager $$3, Iterable<Tab> $$4) {
        super($$0, $$12);
        this.width = $$2;
        this.tabManager = $$3;
        this.tabs = ImmutableList.copyOf($$4);
        ImmutableMap.Builder $$5 = ImmutableMap.builder();
        int $$6 = 0;
        for (Tab $$7 : $$4) {
            Button $$8 = Button.builder($$7.getTabTitle(), $$1 -> this.selectTab((Optional<Button>)Optional.of((Object)$$1), $$7)).createNarration($$1 -> Component.translatable("gui.narrate.tab", $$7.getTabTitle())).build();
            $$5.put((Object)$$7, (Object)this.addChild($$8, 0, $$6++));
        }
        this.tabButtons = $$5.build();
        this.arrangeElements();
    }

    @Override
    public void arrangeElements() {
        Divisor $$0 = new Divisor(this.width, this.tabs.size());
        for (Button $$1 : this.tabButtons.values()) {
            $$1.setWidth($$0.nextInt());
        }
        super.arrangeElements();
    }

    private void selectTab(Optional<Button> $$02, Tab $$1) {
        this.tabButtons.values().forEach($$0 -> {
            $$0.active = true;
        });
        $$02.ifPresent($$0 -> {
            $$0.active = false;
        });
        this.tabManager.setCurrentTab($$1);
    }

    public void setInitialTab(Tab $$0) {
        this.selectTab((Optional<Button>)Optional.ofNullable((Object)((Button)this.tabButtons.get((Object)$$0))), $$0);
    }

    public void setInitialTab(int $$0) {
        this.setInitialTab((Tab)this.tabs.get($$0));
    }

    public static class Builder {
        private int x = 0;
        private int y = 0;
        private int width;
        private final TabManager tabManager;
        private final List<Tab> tabs = new ArrayList();

        Builder(TabManager $$0, int $$1) {
            this.tabManager = $$0;
            this.width = $$1;
        }

        public Builder addTab(Tab $$0) {
            this.tabs.add((Object)$$0);
            return this;
        }

        public Builder addTabs(Tab ... $$0) {
            Collections.addAll(this.tabs, (Object[])$$0);
            return this;
        }

        public Builder setX(int $$0) {
            this.x = $$0;
            return this;
        }

        public Builder setY(int $$0) {
            this.y = $$0;
            return this;
        }

        public Builder setPosition(int $$0, int $$1) {
            return this.setX($$0).setY($$1);
        }

        public Builder setWidth(int $$0) {
            this.width = $$0;
            return this;
        }

        public TabNavigationBar build() {
            return new TabNavigationBar(this.x, this.y, this.width, this.tabManager, (Iterable<Tab>)this.tabs);
        }
    }
}