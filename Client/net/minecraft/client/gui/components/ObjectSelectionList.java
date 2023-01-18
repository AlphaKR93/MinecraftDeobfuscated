/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarrationSupplier;
import net.minecraft.network.chat.Component;

public abstract class ObjectSelectionList<E extends Entry<E>>
extends AbstractSelectionList<E> {
    private static final Component USAGE_NARRATION = Component.translatable("narration.selection.usage");
    private boolean inFocus;

    public ObjectSelectionList(Minecraft $$0, int $$1, int $$2, int $$3, int $$4, int $$5) {
        super($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    public boolean changeFocus(boolean $$0) {
        if (!this.inFocus && this.getItemCount() == 0) {
            return false;
        }
        boolean bl = this.inFocus = !this.inFocus;
        if (this.inFocus && this.getSelected() == null && this.getItemCount() > 0) {
            this.moveSelection(AbstractSelectionList.SelectionDirection.DOWN);
        } else if (this.inFocus && this.getSelected() != null) {
            this.refreshSelection();
        }
        return this.inFocus;
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
        @Override
        public boolean changeFocus(boolean $$0) {
            return false;
        }

        public abstract Component getNarration();

        @Override
        public void updateNarration(NarrationElementOutput $$0) {
            $$0.add(NarratedElementType.TITLE, this.getNarration());
        }
    }
}