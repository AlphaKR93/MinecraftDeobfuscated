/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.client.gui.components.events;

public interface GuiEventListener {
    public static final long DOUBLE_CLICK_THRESHOLD_MS = 250L;

    default public void mouseMoved(double $$0, double $$1) {
    }

    default public boolean mouseClicked(double $$0, double $$1, int $$2) {
        return false;
    }

    default public boolean mouseReleased(double $$0, double $$1, int $$2) {
        return false;
    }

    default public boolean mouseDragged(double $$0, double $$1, int $$2, double $$3, double $$4) {
        return false;
    }

    default public boolean mouseScrolled(double $$0, double $$1, double $$2) {
        return false;
    }

    default public boolean keyPressed(int $$0, int $$1, int $$2) {
        return false;
    }

    default public boolean keyReleased(int $$0, int $$1, int $$2) {
        return false;
    }

    default public boolean charTyped(char $$0, int $$1) {
        return false;
    }

    default public boolean changeFocus(boolean $$0) {
        return false;
    }

    default public boolean isMouseOver(double $$0, double $$1) {
        return false;
    }
}