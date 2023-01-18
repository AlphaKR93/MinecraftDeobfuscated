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
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class CenteredStringWidget
extends AbstractWidget {
    private int color;
    private final Font font;

    public CenteredStringWidget(Component $$0, Font $$1) {
        int n = $$1.width($$0.getVisualOrderText());
        Objects.requireNonNull((Object)$$1);
        this(0, 0, n, 9, $$0, $$1);
    }

    public CenteredStringWidget(int $$0, int $$1, Component $$2, Font $$3) {
        this(0, 0, $$0, $$1, $$2, $$3);
    }

    public CenteredStringWidget(int $$0, int $$1, int $$2, int $$3, Component $$4, Font $$5) {
        super($$0, $$1, $$2, $$3, $$4);
        this.color = 0xFFFFFF;
        this.font = $$5;
        this.active = false;
    }

    public CenteredStringWidget color(int $$0) {
        this.color = $$0;
        return this;
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput $$0) {
    }

    @Override
    public void renderButton(PoseStack $$0, int $$1, int $$2, float $$3) {
        Component component = this.getMessage();
        int n = this.getX() + this.getWidth() / 2;
        int n2 = this.getY();
        int n3 = this.getHeight();
        Objects.requireNonNull((Object)this.font);
        CenteredStringWidget.drawCenteredString($$0, this.font, component, n, n2 + (n3 - 9) / 2, this.color);
    }
}