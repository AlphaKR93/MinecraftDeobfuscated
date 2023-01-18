/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.gui.screens.multiplayer;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.WarningScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class SafetyScreen
extends WarningScreen {
    private static final Component TITLE = Component.translatable("multiplayerWarning.header").withStyle(ChatFormatting.BOLD);
    private static final Component CONTENT = Component.translatable("multiplayerWarning.message");
    private static final Component CHECK = Component.translatable("multiplayerWarning.check");
    private static final Component NARRATION = TITLE.copy().append("\n").append(CONTENT);
    private final Screen previous;

    public SafetyScreen(Screen $$0) {
        super(TITLE, CONTENT, CHECK, NARRATION);
        this.previous = $$0;
    }

    @Override
    protected void initButtons(int $$02) {
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_PROCEED, $$0 -> {
            if (this.stopShowing.selected()) {
                this.minecraft.options.skipMultiplayerWarning = true;
                this.minecraft.options.save();
            }
            this.minecraft.setScreen(new JoinMultiplayerScreen(this.previous));
        }).bounds(this.width / 2 - 155, 100 + $$02, 150, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, $$0 -> this.minecraft.setScreen(this.previous)).bounds(this.width / 2 - 155 + 160, 100 + $$02, 150, 20).build());
    }
}