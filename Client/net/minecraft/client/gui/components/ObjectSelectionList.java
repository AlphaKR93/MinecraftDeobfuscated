/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.components;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarrationSupplier;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.network.chat.Component;

public abstract class ObjectSelectionList<E extends Entry<E>>
extends AbstractSelectionList<E> {
    private static final Component USAGE_NARRATION = Component.translatable("narration.selection.usage");

    public ObjectSelectionList(Minecraft $$0, int $$1, int $$2, int $$3, int $$4, int $$5) {
        super($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    @Nullable
    public ComponentPath nextFocusPath(FocusNavigationEvent $$0) {
        if (this.getItemCount() == 0) {
            return null;
        }
        if (this.isFocused() && $$0 instanceof FocusNavigationEvent.ArrowNavigation) {
            FocusNavigationEvent.ArrowNavigation $$1 = (FocusNavigationEvent.ArrowNavigation)$$0;
            Entry $$2 = (Entry)this.nextEntry($$1.direction());
            if ($$2 != null) {
                return ComponentPath.path(this, ComponentPath.leaf($$2));
            }
            return null;
        }
        if (!this.isFocused()) {
            Entry $$3 = (Entry)this.getSelected();
            if ($$3 == null) {
                $$3 = (Entry)this.nextEntry($$0.getVerticalDirectionForInitialFocus());
            }
            if ($$3 == null) {
                return null;
            }
            return ComponentPath.path(this, ComponentPath.leaf($$3));
        }
        return null;
    }

    @Override
    public void updateNarration(NarrationElementOutput $$0) {
        Entry $$1 = (Entry)this.getHovered();
        if ($$1 != null) {
            this.narrateListElementPosition($$0.nest(), $$1);
            $$1.updateNarration($$0);
        } else {
            Entry $$2 = (Entry)this.getSelected();
            if ($$2 != null) {
                this.narrateListElementPosition($$0.nest(), $$2);
                $$2.updateNarration($$0);
            }
        }
        if (this.isFocused()) {
            $$0.add(NarratedElementType.USAGE, USAGE_NARRATION);
        }
    }

    public static abstract class Entry<E extends Entry<E>>
    extends AbstractSelectionList.Entry<E>
    implements NarrationSupplier {
        public abstract Component getNarration();

        @Override
        public void updateNarration(NarrationElementOutput $$0) {
            $$0.add(NarratedElementType.TITLE, this.getNarration());
        }
    }
}