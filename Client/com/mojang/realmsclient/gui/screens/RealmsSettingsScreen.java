/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 */
package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.realms.RealmsScreen;

public class RealmsSettingsScreen
extends RealmsScreen {
    private static final int COMPONENT_WIDTH = 212;
    private static final Component NAME_LABEL = Component.translatable("mco.configure.world.name");
    private static final Component DESCRIPTION_LABEL = Component.translatable("mco.configure.world.description");
    private final RealmsConfigureWorldScreen configureWorldScreen;
    private final RealmsServer serverData;
    private Button doneButton;
    private EditBox descEdit;
    private EditBox nameEdit;

    public RealmsSettingsScreen(RealmsConfigureWorldScreen $$0, RealmsServer $$1) {
        super(Component.translatable("mco.configure.world.settings.title"));
        this.configureWorldScreen = $$0;
        this.serverData = $$1;
    }

    @Override
    public void tick() {
        this.nameEdit.tick();
        this.descEdit.tick();
        this.doneButton.active = !this.nameEdit.getValue().trim().isEmpty();
    }

    @Override
    public void init() {
        int $$03 = this.width / 2 - 106;
        this.doneButton = this.addRenderableWidget(Button.builder(Component.translatable("mco.configure.world.buttons.done"), $$0 -> this.save()).bounds($$03 - 2, RealmsSettingsScreen.row(12), 106, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, $$0 -> this.minecraft.setScreen(this.configureWorldScreen)).bounds(this.width / 2 + 2, RealmsSettingsScreen.row(12), 106, 20).build());
        String $$1 = this.serverData.state == RealmsServer.State.OPEN ? "mco.configure.world.buttons.close" : "mco.configure.world.buttons.open";
        Button $$2 = Button.builder(Component.translatable($$1), $$02 -> {
            if (this.serverData.state == RealmsServer.State.OPEN) {
                MutableComponent $$1 = Component.translatable("mco.configure.world.close.question.line1");
                MutableComponent $$2 = Component.translatable("mco.configure.world.close.question.line2");
                this.minecraft.setScreen(new RealmsLongConfirmationScreen($$0 -> {
                    if ($$0) {
                        this.configureWorldScreen.closeTheWorld(this);
                    } else {
                        this.minecraft.setScreen(this);
                    }
                }, RealmsLongConfirmationScreen.Type.Info, $$1, $$2, true));
            } else {
                this.configureWorldScreen.openTheWorld(false, this);
            }
        }).bounds(this.width / 2 - 53, RealmsSettingsScreen.row(0), 106, 20).build();
        this.addRenderableWidget($$2);
        this.nameEdit = new EditBox(this.minecraft.font, $$03, RealmsSettingsScreen.row(4), 212, 20, null, Component.translatable("mco.configure.world.name"));
        this.nameEdit.setMaxLength(32);
        this.nameEdit.setValue(this.serverData.getName());
        this.addWidget(this.nameEdit);
        this.magicalSpecialHackyFocus(this.nameEdit);
        this.descEdit = new EditBox(this.minecraft.font, $$03, RealmsSettingsScreen.row(8), 212, 20, null, Component.translatable("mco.configure.world.description"));
        this.descEdit.setMaxLength(32);
        this.descEdit.setValue(this.serverData.getDescription());
        this.addWidget(this.descEdit);
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if ($$0 == 256) {
            this.minecraft.setScreen(this.configureWorldScreen);
            return true;
        }
        return super.keyPressed($$0, $$1, $$2);
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderBackground($$0);
        RealmsSettingsScreen.drawCenteredString($$0, this.font, this.title, this.width / 2, 17, 0xFFFFFF);
        this.font.draw($$0, NAME_LABEL, (float)(this.width / 2 - 106), (float)RealmsSettingsScreen.row(3), 0xA0A0A0);
        this.font.draw($$0, DESCRIPTION_LABEL, (float)(this.width / 2 - 106), (float)RealmsSettingsScreen.row(7), 0xA0A0A0);
        this.nameEdit.render($$0, $$1, $$2, $$3);
        this.descEdit.render($$0, $$1, $$2, $$3);
        super.render($$0, $$1, $$2, $$3);
    }

    public void save() {
        this.configureWorldScreen.saveSettings(this.nameEdit.getValue(), this.descEdit.getValue());
    }
}