/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;

public class RealmsClientOutdatedScreen
extends RealmsScreen {
    private static final Component INCOMPATIBLE_TITLE = Component.translatable("mco.client.incompatible.title");
    private static final Component[] INCOMPATIBLE_MESSAGES_SNAPSHOT = new Component[]{Component.translatable("mco.client.incompatible.msg.line1"), Component.translatable("mco.client.incompatible.msg.line2"), Component.translatable("mco.client.incompatible.msg.line3")};
    private static final Component[] INCOMPATIBLE_MESSAGES = new Component[]{Component.translatable("mco.client.incompatible.msg.line1"), Component.translatable("mco.client.incompatible.msg.line2")};
    private final Screen lastScreen;

    public RealmsClientOutdatedScreen(Screen $$0) {
        super(INCOMPATIBLE_TITLE);
        this.lastScreen = $$0;
    }

    @Override
    public void init() {
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, $$0 -> this.minecraft.setScreen(this.lastScreen)).bounds(this.width / 2 - 100, RealmsClientOutdatedScreen.row(12), 200, 20).build());
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderBackground($$0);
        RealmsClientOutdatedScreen.drawCenteredString($$0, this.font, this.title, this.width / 2, RealmsClientOutdatedScreen.row(3), 0xFF0000);
        Component[] $$4 = this.getMessages();
        for (int $$5 = 0; $$5 < $$4.length; ++$$5) {
            RealmsClientOutdatedScreen.drawCenteredString($$0, this.font, $$4[$$5], this.width / 2, RealmsClientOutdatedScreen.row(5) + $$5 * 12, 0xFFFFFF);
        }
        super.render($$0, $$1, $$2, $$3);
    }

    private Component[] getMessages() {
        if (this.minecraft.getGame().getVersion().isStable()) {
            return INCOMPATIBLE_MESSAGES;
        }
        return INCOMPATIBLE_MESSAGES_SNAPSHOT;
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if ($$0 == 257 || $$0 == 335 || $$0 == 256) {
            this.minecraft.setScreen(this.lastScreen);
            return true;
        }
        return super.keyPressed($$0, $$1, $$2);
    }
}