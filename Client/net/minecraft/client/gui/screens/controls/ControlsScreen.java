/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.gui.screens.controls;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.MouseSettingsScreen;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.controls.KeyBindsScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ControlsScreen
extends OptionsSubScreen {
    private static final int ROW_SPACING = 24;

    public ControlsScreen(Screen $$0, Options $$1) {
        super($$0, $$1, Component.translatable("controls.title"));
    }

    @Override
    protected void init() {
        super.init();
        int $$02 = this.width / 2 - 155;
        int $$1 = $$02 + 160;
        int $$2 = this.height / 6 - 12;
        this.addRenderableWidget(Button.builder(Component.translatable("options.mouse_settings"), $$0 -> this.minecraft.setScreen(new MouseSettingsScreen(this, this.options))).bounds($$02, $$2, 150, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("controls.keybinds"), $$0 -> this.minecraft.setScreen(new KeyBindsScreen(this, this.options))).bounds($$1, $$2, 150, 20).build());
        this.addRenderableWidget(this.options.toggleCrouch().createButton(this.options, $$02, $$2 += 24, 150));
        this.addRenderableWidget(this.options.toggleSprint().createButton(this.options, $$1, $$2, 150));
        this.addRenderableWidget(this.options.autoJump().createButton(this.options, $$02, $$2 += 24, 150));
        this.addRenderableWidget(this.options.operatorItemsTab().createButton(this.options, $$1, $$2, 150));
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, $$0 -> this.minecraft.setScreen(this.lastScreen)).bounds(this.width / 2 - 100, $$2 += 24, 200, 20).build());
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderBackground($$0);
        ControlsScreen.drawCenteredString($$0, this.font, this.title, this.width / 2, 15, 0xFFFFFF);
        super.render($$0, $$1, $$2, $$3);
    }
}