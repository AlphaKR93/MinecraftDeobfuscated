/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.screens.controls;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.controls.KeyBindsList;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class KeyBindsScreen
extends OptionsSubScreen {
    @Nullable
    public KeyMapping selectedKey;
    public long lastKeySelection;
    private KeyBindsList keyBindsList;
    private Button resetButton;

    public KeyBindsScreen(Screen $$0, Options $$1) {
        super($$0, $$1, Component.translatable("controls.keybinds.title"));
    }

    @Override
    protected void init() {
        this.keyBindsList = new KeyBindsList(this, this.minecraft);
        this.addWidget(this.keyBindsList);
        this.resetButton = this.addRenderableWidget(Button.builder(Component.translatable("controls.resetAll"), $$0 -> {
            for (KeyMapping $$1 : this.options.keyMappings) {
                $$1.setKey($$1.getDefaultKey());
            }
            this.resetMappingAndUpdateButtons();
        }).bounds(this.width / 2 - 155, this.height - 29, 150, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, $$0 -> this.minecraft.setScreen(this.lastScreen)).bounds(this.width / 2 - 155 + 160, this.height - 29, 150, 20).build());
    }

    private void resetMappingAndUpdateButtons() {
        KeyMapping.resetMapping();
        this.keyBindsList.children().forEach(KeyBindsList.Entry::onMappingChanged);
    }

    @Override
    public boolean mouseClicked(double $$0, double $$1, int $$2) {
        if (this.selectedKey != null) {
            this.options.setKey(this.selectedKey, InputConstants.Type.MOUSE.getOrCreate($$2));
            this.selectedKey = null;
            this.resetMappingAndUpdateButtons();
            return true;
        }
        return super.mouseClicked($$0, $$1, $$2);
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if (this.selectedKey != null) {
            if ($$0 == 256) {
                this.options.setKey(this.selectedKey, InputConstants.UNKNOWN);
            } else {
                this.options.setKey(this.selectedKey, InputConstants.getKey($$0, $$1));
            }
            this.selectedKey = null;
            this.lastKeySelection = Util.getMillis();
            this.resetMappingAndUpdateButtons();
            return true;
        }
        return super.keyPressed($$0, $$1, $$2);
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderBackground($$0);
        this.keyBindsList.render($$0, $$1, $$2, $$3);
        KeyBindsScreen.drawCenteredString($$0, this.font, this.title, this.width / 2, 8, 0xFFFFFF);
        boolean $$4 = false;
        for (KeyMapping $$5 : this.options.keyMappings) {
            if ($$5.isDefault()) continue;
            $$4 = true;
            break;
        }
        this.resetButton.active = $$4;
        super.render($$0, $$1, $$2, $$3);
    }
}