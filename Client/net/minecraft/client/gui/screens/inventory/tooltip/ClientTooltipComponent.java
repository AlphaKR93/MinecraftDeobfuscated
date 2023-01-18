/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  org.joml.Matrix4f
 */
package net.minecraft.client.gui.screens.inventory.tooltip;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientBundleTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.joml.Matrix4f;

public interface ClientTooltipComponent {
    public static ClientTooltipComponent create(FormattedCharSequence $$0) {
        return new ClientTextTooltip($$0);
    }

    public static ClientTooltipComponent create(TooltipComponent $$0) {
        if ($$0 instanceof BundleTooltip) {
            return new ClientBundleTooltip((BundleTooltip)$$0);
        }
        throw new IllegalArgumentException("Unknown TooltipComponent");
    }

    public int getHeight();

    public int getWidth(Font var1);

    default public void renderText(Font $$0, int $$1, int $$2, Matrix4f $$3, MultiBufferSource.BufferSource $$4) {
    }

    default public void renderImage(Font $$0, int $$1, int $$2, PoseStack $$3, ItemRenderer $$4, int $$5) {
    }
}