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
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;

public class OutOfMemoryScreen
extends Screen {
    private MultiLineLabel message = MultiLineLabel.EMPTY;

    public OutOfMemoryScreen() {
        super(Component.translatable("outOfMemory.error"));
    }

    @Override
    protected void init() {
        this.addRenderableWidget(Button.builder(Component.translatable("gui.toTitle"), $$0 -> this.minecraft.setScreen(new TitleScreen())).bounds(this.width / 2 - 155, this.height / 4 + 120 + 12, 150, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("menu.quit"), $$0 -> this.minecraft.stop()).bounds(this.width / 2 - 155 + 160, this.height / 4 + 120 + 12, 150, 20).build());
        this.message = MultiLineLabel.create(this.font, (FormattedText)Component.translatable("outOfMemory.message"), 295);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderBackground($$0);
        OutOfMemoryScreen.drawCenteredString($$0, this.font, this.title, this.width / 2, this.height / 4 - 60 + 20, 0xFFFFFF);
        this.message.renderLeftAligned($$0, this.width / 2 - 145, this.height / 4, 9, 0xA0A0A0);
        super.render($$0, $$1, $$2, $$3);
    }
}