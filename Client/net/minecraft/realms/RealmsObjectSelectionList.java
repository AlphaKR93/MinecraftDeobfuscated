/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Collection
 */
package net.minecraft.realms;

import java.util.Collection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ObjectSelectionList;

public abstract class RealmsObjectSelectionList<E extends ObjectSelectionList.Entry<E>>
extends ObjectSelectionList<E> {
    protected RealmsObjectSelectionList(int $$0, int $$1, int $$2, int $$3, int $$4) {
        super(Minecraft.getInstance(), $$0, $$1, $$2, $$3, $$4);
    }

    public void setSelectedItem(int $$0) {
        if ($$0 == -1) {
            this.setSelected(null);
        } else if (super.getItemCount() != 0) {
            this.setSelected((ObjectSelectionList.Entry)this.getEntry($$0));
        }
    }

    public void selectItem(int $$0) {
        this.setSelectedItem($$0);
    }

    public void itemClicked(int $$0, int $$1, double $$2, double $$3, int $$4) {
    }

    @Override
    public int getMaxPosition() {
        return 0;
    }

    @Override
    public int getScrollbarPosition() {
        return this.getRowLeft() + this.getRowWidth();
    }

    @Override
    public int getRowWidth() {
        return (int)((double)this.width * 0.6);
    }

    @Override
    public void replaceEntries(Collection<E> $$0) {
        super.replaceEntries($$0);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public int getRowTop(int $$0) {
        return super.getRowTop($$0);
    }

    @Override
    public int getRowLeft() {
        return super.getRowLeft();
    }

    @Override
    public int addEntry(E $$0) {
        return super.addEntry($$0);
    }

    public void clear() {
        this.clearEntries();
    }
}