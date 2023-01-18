/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Collection
 *  java.util.List
 *  java.util.Map
 *  java.util.Optional
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.components;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;

public class OptionsList
extends ContainerObjectSelectionList<Entry> {
    public OptionsList(Minecraft $$0, int $$1, int $$2, int $$3, int $$4, int $$5) {
        super($$0, $$1, $$2, $$3, $$4, $$5);
        this.centerListVertically = false;
    }

    public int addBig(OptionInstance<?> $$0) {
        return this.addEntry(Entry.big(this.minecraft.options, this.width, $$0));
    }

    public void addSmall(OptionInstance<?> $$0, @Nullable OptionInstance<?> $$1) {
        this.addEntry(Entry.small(this.minecraft.options, this.width, $$0, $$1));
    }

    public void addSmall(OptionInstance<?>[] $$0) {
        for (int $$1 = 0; $$1 < $$0.length; $$1 += 2) {
            this.addSmall($$0[$$1], $$1 < $$0.length - 1 ? $$0[$$1 + 1] : null);
        }
    }

    @Override
    public int getRowWidth() {
        return 400;
    }

    @Override
    protected int getScrollbarPosition() {
        return super.getScrollbarPosition() + 32;
    }

    @Nullable
    public AbstractWidget findOption(OptionInstance<?> $$0) {
        for (Entry $$1 : this.children()) {
            AbstractWidget $$2 = (AbstractWidget)$$1.options.get($$0);
            if ($$2 == null) continue;
            return $$2;
        }
        return null;
    }

    public Optional<AbstractWidget> getMouseOver(double $$0, double $$1) {
        for (Entry $$2 : this.children()) {
            for (AbstractWidget $$3 : $$2.children) {
                if (!$$3.isMouseOver($$0, $$1)) continue;
                return Optional.of((Object)$$3);
            }
        }
        return Optional.empty();
    }

    protected static class Entry
    extends ContainerObjectSelectionList.Entry<Entry> {
        final Map<OptionInstance<?>, AbstractWidget> options;
        final List<AbstractWidget> children;

        private Entry(Map<OptionInstance<?>, AbstractWidget> $$0) {
            this.options = $$0;
            this.children = ImmutableList.copyOf((Collection)$$0.values());
        }

        public static Entry big(Options $$0, int $$1, OptionInstance<?> $$2) {
            return new Entry((Map<OptionInstance<?>, AbstractWidget>)ImmutableMap.of($$2, (Object)$$2.createButton($$0, $$1 / 2 - 155, 0, 310)));
        }

        public static Entry small(Options $$0, int $$1, OptionInstance<?> $$2, @Nullable OptionInstance<?> $$3) {
            AbstractWidget $$4 = $$2.createButton($$0, $$1 / 2 - 155, 0, 150);
            if ($$3 == null) {
                return new Entry((Map<OptionInstance<?>, AbstractWidget>)ImmutableMap.of($$2, (Object)$$4));
            }
            return new Entry((Map<OptionInstance<?>, AbstractWidget>)ImmutableMap.of($$2, (Object)$$4, $$3, (Object)$$3.createButton($$0, $$1 / 2 - 155 + 160, 0, 150)));
        }

        @Override
        public void render(PoseStack $$0, int $$1, int $$2, int $$3, int $$4, int $$52, int $$6, int $$7, boolean $$8, float $$9) {
            this.children.forEach($$5 -> {
                $$5.setY($$2);
                $$5.render($$0, $$6, $$7, $$9);
            });
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return this.children;
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return this.children;
        }
    }
}