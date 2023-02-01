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

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import org.joml.Vector2i;
import org.joml.Vector2ic;

public class DefaultTooltipPositioner
implements ClientTooltipPositioner {
    public static final ClientTooltipPositioner INSTANCE = new DefaultTooltipPositioner();

    private DefaultTooltipPositioner() {
    }

    @Override
    public Vector2ic positionTooltip(Screen $$0, int $$1, int $$2, int $$3, int $$4) {
        Vector2i $$5 = new Vector2i($$1, $$2).add(12, -12);
        this.positionTooltip($$0, $$5, $$3, $$4);
        return $$5;
    }

    private void positionTooltip(Screen $$0, Vector2i $$1, int $$2, int $$3) {
        int $$4;
        if ($$1.x + $$2 > $$0.width) {
            $$1.x = Math.max((int)($$1.x - 24 - $$2), (int)4);
        }
        if ($$1.y + ($$4 = $$3 + 3) > $$0.height) {
            $$1.y = $$0.height - $$4;
        }
    }
}