/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ErrorScreen
extends Screen {
    private final Component message;

    public ErrorScreen(Component $$0, Component $$1) {
        super($$0);
        this.message = $$1;
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, $$0 -> this.minecraft.setScreen(null)).bounds(this.width / 2 - 100, 140, 200, 20).build());
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.fillGradient($$0, 0, 0, this.width, this.height, -12574688, -11530224);
        ErrorScreen.drawCenteredString($$0, this.font, this.title, this.width / 2, 90, 0xFFFFFF);
        ErrorScreen.drawCenteredString($$0, this.font, this.message, this.width / 2, 110, 0xFFFFFF);
        super.render($$0, $$1, $$2, $$3);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}