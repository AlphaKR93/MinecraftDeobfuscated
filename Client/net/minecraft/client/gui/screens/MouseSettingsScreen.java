/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Arrays
 *  java.util.stream.Stream
 */
package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Arrays;
import java.util.stream.Stream;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class MouseSettingsScreen
extends OptionsSubScreen {
    private OptionsList list;

    private static OptionInstance<?>[] options(Options $$0) {
        return new OptionInstance[]{$$0.sensitivity(), $$0.invertYMouse(), $$0.mouseWheelSensitivity(), $$0.discreteMouseScroll(), $$0.touchscreen()};
    }

    public MouseSettingsScreen(Screen $$0, Options $$1) {
        super($$0, $$1, Component.translatable("options.mouse_settings.title"));
    }

    @Override
    protected void init() {
        this.list = new OptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
        if (InputConstants.isRawMouseInputSupported()) {
            this.list.addSmall((OptionInstance[])Stream.concat((Stream)Arrays.stream((Object[])MouseSettingsScreen.options(this.options)), (Stream)Stream.of(this.options.rawMouseInput())).toArray(OptionInstance[]::new));
        } else {
            this.list.addSmall(MouseSettingsScreen.options(this.options));
        }
        this.addWidget(this.list);
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, $$0 -> {
            this.options.save();
            this.minecraft.setScreen(this.lastScreen);
        }).bounds(this.width / 2 - 100, this.height - 27, 200, 20).build());
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderBackground($$0);
        this.list.render($$0, $$1, $$2, $$3);
        MouseSettingsScreen.drawCenteredString($$0, this.font, this.title, this.width / 2, 5, 0xFFFFFF);
        super.render($$0, $$1, $$2, $$3);
    }
}