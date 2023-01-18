/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  org.joml.Matrix4f
 */
package net.minecraft.client.gui.screens.inventory.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix4f;

public class ClientTextTooltip
implements ClientTooltipComponent {
    private final FormattedCharSequence text;

    public ClientTextTooltip(FormattedCharSequence $$0) {
        this.text = $$0;
    }

    @Override
    public int getWidth(Font $$0) {
        return $$0.width(this.text);
    }

    @Override
    public int getHeight() {
        return 10;
    }

    @Override
    public void renderText(Font $$0, int $$1, int $$2, Matrix4f $$3, MultiBufferSource.BufferSource $$4) {
        $$0.drawInBatch(this.text, (float)$$1, (float)$$2, -1, true, $$3, (MultiBufferSource)$$4, false, 0, 0xF000F0);
    }
}