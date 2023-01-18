/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.ListIterator
 *  java.util.Optional
 *  java.util.function.BooleanSupplier
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.components.events;

import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.gui.components.events.GuiEventListener;

public interface ContainerEventHandler
extends GuiEventListener {
    public List<? extends GuiEventListener> children();

    default public Optional<GuiEventListener> getChildAt(double $$0, double $$1) {
        for (GuiEventListener $$2 : this.children()) {
            if (!$$2.isMouseOver($$0, $$1)) continue;
            return Optional.of((Object)$$2);
        }
        return Optional.empty();
    }

    @Override
    default public boolean mouseClicked(double $$0, double $$1, int $$2) {
        GuiEventListener $$3 = null;
        List $$4 = List.copyOf(this.children());
        for (GuiEventListener $$5 : $$4) {
            if (!$$5.mouseClicked($$0, $$1, $$2)) continue;
            $$3 = $$5;
        }
        if ($$3 != null) {
            this.setFocused($$3);
            if ($$2 == 0) {
                this.setDragging(true);
            }
            return true;
        }
        return false;
    }

    @Override
    default public boolean mouseReleased(double $$0, double $$1, int $$2) {
        this.setDragging(false);
        return this.getChildAt($$0, $$1).filter($$3 -> $$3.mouseReleased($$0, $$1, $$2)).isPresent();
    }

    @Override
    default public boolean mouseDragged(double $$0, double $$1, int $$2, double $$3, double $$4) {
        if (this.getFocused() != null && this.isDragging() && $$2 == 0) {
            return this.getFocused().mouseDragged($$0, $$1, $$2, $$3, $$4);
        }
        return false;
    }

    public boolean isDragging();

    public void setDragging(boolean var1);

    @Override
    default public boolean mouseScrolled(double $$0, double $$1, double $$2) {
        return this.getChildAt($$0, $$1).filter($$3 -> $$3.mouseScrolled($$0, $$1, $$2)).isPresent();
    }

    @Override
    default public boolean keyPressed(int $$0, int $$1, int $$2) {
        return this.getFocused() != null && this.getFocused().keyPressed($$0, $$1, $$2);
    }

    @Override
    default public boolean keyReleased(int $$0, int $$1, int $$2) {
        return this.getFocused() != null && this.getFocused().keyReleased($$0, $$1, $$2);
    }

    @Override
    default public boolean charTyped(char $$0, int $$1) {
        return this.getFocused() != null && this.getFocused().charTyped($$0, $$1);
    }

    @Nullable
    public GuiEventListener getFocused();

    public void setFocused(@Nullable GuiEventListener var1);

    default public void setInitialFocus(@Nullable GuiEventListener $$0) {
        this.setFocused($$0);
        $$0.changeFocus(true);
    }

    default public void magicalSpecialHackyFocus(@Nullable GuiEventListener $$0) {
        this.setFocused($$0);
    }

    @Override
    default public boolean changeFocus(boolean $$0) {
        Supplier $$10;
        BooleanSupplier $$9;
        int $$7;
        boolean $$2;
        GuiEventListener $$1 = this.getFocused();
        boolean bl = $$2 = $$1 != null;
        if ($$2 && $$1.changeFocus($$0)) {
            return true;
        }
        List<? extends GuiEventListener> $$3 = this.children();
        int $$4 = $$3.indexOf((Object)$$1);
        if ($$2 && $$4 >= 0) {
            int $$5 = $$4 + ($$0 ? 1 : 0);
        } else if ($$0) {
            boolean $$6 = false;
        } else {
            $$7 = $$3.size();
        }
        ListIterator $$8 = $$3.listIterator($$7);
        BooleanSupplier booleanSupplier = $$0 ? () -> ((ListIterator)$$8).hasNext() : ($$9 = () -> ((ListIterator)$$8).hasPrevious());
        Supplier supplier = $$0 ? () -> ((ListIterator)$$8).next() : ($$10 = () -> ((ListIterator)$$8).previous());
        while ($$9.getAsBoolean()) {
            GuiEventListener $$11 = (GuiEventListener)$$10.get();
            if (!$$11.changeFocus($$0)) continue;
            this.setFocused($$11);
            return true;
        }
        this.setFocused(null);
        return false;
    }
}