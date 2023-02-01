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
import net.minecraft.util.Mth;
import org.joml.Vector2i;
import org.joml.Vector2ic;

public class MenuTooltipPositioner
implements ClientTooltipPositioner {
    private static final int MARGIN = 5;
    private static final int MOUSE_OFFSET_X = 12;
    public static final int MAX_OVERLAP_WITH_WIDGET = 3;
    public static final int MAX_DISTANCE_TO_WIDGET = 5;
    private final AbstractWidget widget;

    public MenuTooltipPositioner(AbstractWidget $$0) {
        this.widget = $$0;
    }

    @Override
    public Vector2ic positionTooltip(Screen $$0, int $$1, int $$2, int $$3, int $$4) {
        int $$8;
        Vector2i $$5 = new Vector2i($$1 + 12, $$2);
        if ($$5.x + $$3 > $$0.width - 5) {
            $$5.x = Math.max((int)($$1 - 12 - $$3), (int)9);
        }
        $$5.y += 3;
        int $$6 = $$4 + 3 + 3;
        int $$7 = this.widget.getY() + this.widget.getHeight() + 3 + MenuTooltipPositioner.getOffset(0, 0, this.widget.getHeight());
        $$5.y = $$7 + $$6 <= ($$8 = $$0.height - 5) ? ($$5.y += MenuTooltipPositioner.getOffset($$5.y, this.widget.getY(), this.widget.getHeight())) : ($$5.y -= $$6 + MenuTooltipPositioner.getOffset($$5.y, this.widget.getY() + this.widget.getHeight(), this.widget.getHeight()));
        return $$5;
    }

    private static int getOffset(int $$0, int $$1, int $$2) {
        int $$3 = Math.min((int)Math.abs((int)($$0 - $$1)), (int)$$2);
        return Math.round((float)Mth.lerp((float)$$3 / (float)$$2, $$2 - 3, 5.0f));
    }
}