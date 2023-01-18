/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  org.joml.Vector2ic
 */
package net.minecraft.client.gui.screens.inventory.tooltip;

import net.minecraft.client.gui.screens.Screen;
import org.joml.Vector2ic;

public interface ClientTooltipPositioner {
    public Vector2ic positionTooltip(Screen var1, int var2, int var3, int var4, int var5);
}