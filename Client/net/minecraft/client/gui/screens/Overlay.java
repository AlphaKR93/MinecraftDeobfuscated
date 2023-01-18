/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.client.gui.screens;

import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Renderable;

public abstract class Overlay
extends GuiComponent
implements Renderable {
    public boolean isPauseScreen() {
        return true;
    }
}