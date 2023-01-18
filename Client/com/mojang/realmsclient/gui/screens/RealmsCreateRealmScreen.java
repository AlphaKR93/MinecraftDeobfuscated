/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsResetWorldScreen;
import com.mojang.realmsclient.util.task.WorldCreationTask;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;

public class RealmsCreateRealmScreen
extends RealmsScreen {
    private static final Component NAME_LABEL = Component.translatable("mco.configure.world.name");
    private static final Component DESCRIPTION_LABEL = Component.translatable("mco.configure.world.description");
    private final RealmsServer server;
    private final RealmsMainScreen lastScreen;
    private EditBox nameBox;
    private EditBox descriptionBox;
    private Button createButton;

    public RealmsCreateRealmScreen(RealmsServer $$0, RealmsMainScreen $$1) {
        super(Component.translatable("mco.selectServer.create"));
        this.server = $$0;
        this.lastScreen = $$1;
    }

    @Override
    public void tick() {
        if (this.nameBox != null) {
            this.nameBox.tick();
        }
        if (this.descriptionBox != null) {
            this.descriptionBox.tick();
        }
    }

    @Override
    public void init() {
        this.createButton = this.addRenderableWidget(Button.builder(Component.translatable("mco.create.world"), $$0 -> this.createWorld()).bounds(this.width / 2 - 100, this.height / 4 + 120 + 17, 97, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, $$0 -> this.minecraft.setScreen(this.lastScreen)).bounds(this.width / 2 + 5, this.height / 4 + 120 + 17, 95, 20).build());
        this.createButton.active = false;
        this.nameBox = new EditBox(this.minecraft.font, this.width / 2 - 100, 65, 200, 20, null, Component.translatable("mco.configure.world.name"));
        this.addWidget(this.nameBox);
        this.setInitialFocus(this.nameBox);
        this.descriptionBox = new EditBox(this.minecraft.font, this.width / 2 - 100, 115, 200, 20, null, Component.translatable("mco.configure.world.description"));
        this.addWidget(this.descriptionBox);
    }

    @Override
    public boolean charTyped(char $$0, int $$1) {
        boolean $$2 = super.charTyped($$0, $$1);
        this.createButton.active = this.valid();
        return $$2;
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if ($$0 == 256) {
            this.minecraft.setScreen(this.lastScreen);
            return true;
        }
        boolean $$3 = super.keyPressed($$0, $$1, $$2);
        this.createButton.active = this.valid();
        return $$3;
    }

    private void createWorld() {
        if (this.valid()) {
            RealmsResetWorldScreen $$0 = new RealmsResetWorldScreen(this.lastScreen, this.server, Component.translatable("mco.selectServer.create"), Component.translatable("mco.create.world.subtitle"), 0xA0A0A0, Component.translatable("mco.create.world.skip"), () -> this.minecraft.execute(() -> this.minecraft.setScreen(this.lastScreen.newScreen())), () -> this.minecraft.setScreen(this.lastScreen.newScreen()));
            $$0.setResetTitle(Component.translatable("mco.create.world.reset.title"));
            this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, new WorldCreationTask(this.server.id, this.nameBox.getValue(), this.descriptionBox.getValue(), $$0)));
        }
    }

    private boolean valid() {
        return !this.nameBox.getValue().trim().isEmpty();
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderBackground($$0);
        RealmsCreateRealmScreen.drawCenteredString($$0, this.font, this.title, this.width / 2, 11, 0xFFFFFF);
        this.font.draw($$0, NAME_LABEL, (float)(this.width / 2 - 100), 52.0f, 0xA0A0A0);
        this.font.draw($$0, DESCRIPTION_LABEL, (float)(this.width / 2 - 100), 102.0f, 0xA0A0A0);
        if (this.nameBox != null) {
            this.nameBox.render($$0, $$1, $$2, $$3);
        }
        if (this.descriptionBox != null) {
            this.descriptionBox.render($$0, $$1, $$2, $$3);
        }
        super.render($$0, $$1, $$2, $$3);
    }
}