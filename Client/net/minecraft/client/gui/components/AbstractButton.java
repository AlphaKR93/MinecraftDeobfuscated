/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;

public abstract class AbstractButton
extends AbstractWidget {
    public AbstractButton(int $$0, int $$1, int $$2, int $$3, Component $$4) {
        super($$0, $$1, $$2, $$3, $$4);
    }

    public abstract void onPress();

    @Override
    public void onClick(double $$0, double $$1) {
        this.onPress();
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if (!this.active || !this.visible) {
            return false;
        }
        if ($$0 == 257 || $$0 == 32 || $$0 == 335) {
            this.playDownSound(Minecraft.getInstance().getSoundManager());
            this.onPress();
            return true;
        }
        return false;
    }
}