/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.components;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public abstract class ContainerObjectSelectionList<E extends Entry<E>>
extends AbstractSelectionList<E> {
    private boolean hasFocus;

    public ContainerObjectSelectionList(Minecraft $$0, int $$1, int $$2, int $$3, int $$4, int $$5) {
        super($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    public boolean changeFocus(boolean $$0) {
        this.hasFocus = super.changeFocus($$0);
        if (this.hasFocus) {
            this.ensureVisible((Entry)this.getFocused());
        }
        return this.hasFocus;
    }

    @Override
    public NarratableEntry.NarrationPriority narrationPriority() {
        if (this.hasFocus) {
            return NarratableEntry.NarrationPriority.FOCUSED;
        }
        return super.narrationPriority();
    }

    @Override
    protected boolean isSelectedItem(int $$0) {
        return false;
    }

    @Override
    public void updateNarration(NarrationElementOutput $$0) {
        Entry $$1 = (Entry)this.getHovered();
        if ($$1 != null) {
            $$1.updateNarration($$0.nest());
            this.narrateListElementPosition($$0, $$1);
        } else {
            Entry $$2 = (Entry)this.getFocused();
            if ($$2 != null) {
                $$2.updateNarration($$0.nest());
                this.narrateListElementPosition($$0, $$2);
            }
        }
        $$0.add(NarratedElementType.USAGE, (Component)Component.translatable("narration.component_list.usage"));
    }

    public static abstract class Entry<E extends Entry<E>>
    extends AbstractSelectionList.Entry<E>
    implements ContainerEventHandler {
        @Nullable
        private GuiEventListener focused;
        @Nullable
        private NarratableEntry lastNarratable;
        private boolean dragging;

        @Override
        public boolean isDragging() {
            return this.dragging;
        }

        @Override
        public void setDragging(boolean $$0) {
            this.dragging = $$0;
        }

        @Override
        public void setFocused(@Nullable GuiEventListener $$0) {
            this.focused = $$0;
        }

        @Override
        @Nullable
        public GuiEventListener getFocused() {
            return this.focused;
        }

        public abstract List<? extends NarratableEntry> narratables();

        void updateNarration(NarrationElementOutput $$0) {
            List<NarratableEntry> $$1 = this.narratables();
            Screen.NarratableSearchResult $$2 = Screen.findNarratableWidget($$1, this.lastNarratable);
            if ($$2 != null) {
                if ($$2.priority.isTerminal()) {
                    this.lastNarratable = $$2.entry;
                }
                if ($$1.size() > 1) {
                    $$0.add(NarratedElementType.POSITION, (Component)Component.translatable("narrator.position.object_list", $$2.index + 1, $$1.size()));
                    if ($$2.priority == NarratableEntry.NarrationPriority.FOCUSED) {
                        $$0.add(NarratedElementType.USAGE, (Component)Component.translatable("narration.component_list.usage"));
                    }
                }
                $$2.entry.updateNarration($$0.nest());
            }
        }
    }
}