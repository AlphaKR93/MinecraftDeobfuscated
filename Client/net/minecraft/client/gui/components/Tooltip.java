/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.components;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarrationSupplier;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

public class Tooltip
implements NarrationSupplier {
    private static final int MAX_WIDTH = 170;
    private final Component message;
    @Nullable
    private List<FormattedCharSequence> cachedTooltip;
    @Nullable
    private final Component narration;

    private Tooltip(Component $$0, @Nullable Component $$1) {
        this.message = $$0;
        this.narration = $$1;
    }

    public static Tooltip create(Component $$0, @Nullable Component $$1) {
        return new Tooltip($$0, $$1);
    }

    public static Tooltip create(Component $$0) {
        return new Tooltip($$0, $$0);
    }

    @Override
    public void updateNarration(NarrationElementOutput $$0) {
        if (this.narration != null) {
            $$0.add(NarratedElementType.HINT, this.narration);
        }
    }

    public List<FormattedCharSequence> toCharSequence(Minecraft $$0) {
        if (this.cachedTooltip == null) {
            this.cachedTooltip = Tooltip.splitTooltip($$0, this.message);
        }
        return this.cachedTooltip;
    }

    public static List<FormattedCharSequence> splitTooltip(Minecraft $$0, Component $$1) {
        return $$0.font.split($$1, 170);
    }
}