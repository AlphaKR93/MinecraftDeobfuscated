/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  org.joml.Vector2i
 *  org.joml.Vector2ic
 */
package net.minecraft.client.gui.screens.inventory.tooltip;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import org.joml.Vector2i;
import org.joml.Vector2ic;

public class BelowOrAboveWidgetTooltipPositioner
implements ClientTooltipPositioner {
    private final AbstractWidget widget;

    public BelowOrAboveWidgetTooltipPositioner(AbstractWidget $$0) {
        this.widget = $$0;
    }

    @Override
    public Vector2ic positionTooltip(Screen $$0, int $$1, int $$2, int $$3, int $$4) {
        Vector2i $$5 = new Vector2i();
        $$5.x = this.widget.getX() + 3;
        $$5.y = this.widget.getY() + this.widget.getHeight() + 3 + 1;
        if ($$5.y + $$4 + 3 > $$0.height) {
            $$5.y = this.widget.getY() - $$4 - 3 - 1;
        }
        if ($$5.x + $$3 > $$0.width) {
            $$5.x = Math.max((int)(this.widget.getX() + this.widget.getWidth() - $$3 - 3), (int)4);
        }
        return $$5;
    }
}