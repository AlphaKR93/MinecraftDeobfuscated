/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.util.function.Consumer
 */
package net.minecraft.client.gui.layouts;

import java.util.function.Consumer;
import net.minecraft.client.gui.components.AbstractWidget;

public interface LayoutElement {
    public void setX(int var1);

    public void setY(int var1);

    public int getX();

    public int getY();

    public int getWidth();

    public int getHeight();

    default public void setPosition(int $$0, int $$1) {
        this.setX($$0);
        this.setY($$1);
    }

    public void visitWidgets(Consumer<AbstractWidget> var1);
}