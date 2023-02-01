/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Objects
 */
package net.minecraft.client.gui.components;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Objects;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class StringWidget
extends AbstractWidget {
    private int color;
    private final Font font;
    private float alignX;

    public StringWidget(Component $$0, Font $$1) {
        int n = $$1.width($$0.getVisualOrderText());
        Objects.requireNonNull((Object)$$1);
        this(0, 0, n, 9, $$0, $$1);
    }

    public StringWidget(int $$0, int $$1, Component $$2, Font $$3) {
        this(0, 0, $$0, $$1, $$2, $$3);
    }

    public StringWidget(int $$0, int $$1, int $$2, int $$3, Component $$4, Font $$5) {
        super($$0, $$1, $$2, $$3, $$4);
        this.color = 0xFFFFFF;
        this.alignX = 0.5f;
        this.font = $$5;
        this.active = false;
    }

    public StringWidget color(int $$0) {
        this.color = $$0;
        return this;
    }

    private StringWidget horizontalAlignment(float $$0) {
        this.alignX = $$0;
        return this;
    }

    public StringWidget alignLeft() {
        return this.horizontalAlignment(0.0f);
    }

    public StringWidget alignCenter() {
        return this.horizontalAlignment(0.5f);
    }

    public StringWidget alignRight() {
        return this.horizontalAlignment(1.0f);
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput $$0) {
    }

    @Override
    public void renderWidget(PoseStack $$0, int $$1, int $$2, float $$3) {
        Component $$4 = this.getMessage();
        int $$5 = this.getX() + Math.round((float)(this.alignX * (float)(this.getWidth() - this.font.width($$4))));
        int n = this.getY();
        int n2 = this.getHeight();
        Objects.requireNonNull((Object)this.font);
        int $$6 = n + (n2 - 9) / 2;
        StringWidget.drawString($$0, this.font, $$4, $$5, $$6, this.color);
    }
}