/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.gui.components;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class SpacerWidget
extends AbstractWidget {
    public SpacerWidget(int $$0, int $$1) {
        this(0, 0, $$0, $$1);
    }

    public SpacerWidget(int $$0, int $$1, int $$2, int $$3) {
        super($$0, $$1, $$2, $$3, Component.empty());
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput $$0) {
    }

    @Override
    public boolean changeFocus(boolean $$0) {
        return false;
    }

    public static AbstractWidget width(int $$0) {
        return new SpacerWidget($$0, 0);
    }

    public static AbstractWidget height(int $$0) {
        return new SpacerWidget(0, $$0);
    }
}