/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Consumer
 */
package net.minecraft.client.gui.layouts;

import java.util.function.Consumer;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.layouts.LayoutElement;

public class SpacerElement
implements LayoutElement {
    private int x;
    private int y;
    private final int width;
    private final int height;

    public SpacerElement(int $$0, int $$1) {
        this(0, 0, $$0, $$1);
    }

    public SpacerElement(int $$0, int $$1, int $$2, int $$3) {
        this.x = $$0;
        this.y = $$1;
        this.width = $$2;
        this.height = $$3;
    }

    public static SpacerElement width(int $$0) {
        return new SpacerElement($$0, 0);
    }

    public static SpacerElement height(int $$0) {
        return new SpacerElement(0, $$0);
    }

    @Override
    public void setX(int $$0) {
        this.x = $$0;
    }

    @Override
    public void setY(int $$0) {
        this.y = $$0;
    }

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public int getY() {
        return this.y;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public void visitWidgets(Consumer<AbstractWidget> $$0) {
    }
}