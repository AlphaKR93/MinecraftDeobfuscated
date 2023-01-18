/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Objects
 */
package net.minecraft.client.gui.components;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Objects;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;

public class MultiLineTextWidget
extends AbstractWidget {
    private final MultiLineLabel multiLineLabel;
    private final int lineHeight;
    private final boolean centered;

    protected MultiLineTextWidget(MultiLineLabel $$0, Font $$1, Component $$2, boolean $$3) {
        int n = $$0.getWidth();
        int n2 = $$0.getLineCount();
        Objects.requireNonNull((Object)$$1);
        super(0, 0, n, n2 * 9, $$2);
        this.multiLineLabel = $$0;
        Objects.requireNonNull((Object)$$1);
        this.lineHeight = 9;
        this.centered = $$3;
        this.active = false;
    }

    public static MultiLineTextWidget createCentered(int $$0, Font $$1, Component $$2) {
        MultiLineLabel $$3 = MultiLineLabel.create($$1, (FormattedText)$$2, $$0);
        return new MultiLineTextWidget($$3, $$1, $$2, true);
    }

    public static MultiLineTextWidget create(int $$0, Font $$1, Component $$2) {
        MultiLineLabel $$3 = MultiLineLabel.create($$1, (FormattedText)$$2, $$0);
        return new MultiLineTextWidget($$3, $$1, $$2, false);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput $$0) {
    }

    @Override
    public void renderButton(PoseStack $$0, int $$1, int $$2, float $$3) {
        if (this.centered) {
            this.multiLineLabel.renderCentered($$0, this.getX() + this.getWidth() / 2, this.getY(), this.lineHeight, 0xFFFFFF);
        } else {
            this.multiLineLabel.renderLeftAligned($$0, this.getX(), this.getY(), this.lineHeight, 0xFFFFFF);
        }
    }
}