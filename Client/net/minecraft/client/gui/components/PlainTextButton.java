/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.gui.components;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;

public class PlainTextButton
extends Button {
    private final Font font;
    private final Component message;
    private final Component underlinedMessage;

    public PlainTextButton(int $$0, int $$1, int $$2, int $$3, Component $$4, Button.OnPress $$5, Font $$6) {
        super($$0, $$1, $$2, $$3, $$4, $$5, DEFAULT_NARRATION);
        this.font = $$6;
        this.message = $$4;
        this.underlinedMessage = ComponentUtils.mergeStyles($$4.copy(), Style.EMPTY.withUnderlined(true));
    }

    @Override
    public void renderWidget(PoseStack $$0, int $$1, int $$2, float $$3) {
        Component $$4 = this.isHoveredOrFocused() ? this.underlinedMessage : this.message;
        PlainTextButton.drawString($$0, this.font, $$4, this.getX(), this.getY(), 0xFFFFFF | Mth.ceil(this.alpha * 255.0f) << 24);
    }
}