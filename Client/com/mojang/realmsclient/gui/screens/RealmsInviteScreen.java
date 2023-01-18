/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsPlayerScreen;
import javax.annotation.Nullable;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;
import org.slf4j.Logger;

public class RealmsInviteScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Component NAME_LABEL = Component.translatable("mco.configure.world.invite.profile.name");
    private static final Component NO_SUCH_PLAYER_ERROR_TEXT = Component.translatable("mco.configure.world.players.error");
    private EditBox profileName;
    private final RealmsServer serverData;
    private final RealmsConfigureWorldScreen configureScreen;
    private final Screen lastScreen;
    @Nullable
    private Component errorMsg;

    public RealmsInviteScreen(RealmsConfigureWorldScreen $$0, Screen $$1, RealmsServer $$2) {
        super(GameNarrator.NO_TITLE);
        this.configureScreen = $$0;
        this.lastScreen = $$1;
        this.serverData = $$2;
    }

    @Override
    public void tick() {
        this.profileName.tick();
    }

    @Override
    public void init() {
        this.profileName = new EditBox(this.minecraft.font, this.width / 2 - 100, RealmsInviteScreen.row(2), 200, 20, null, Component.translatable("mco.configure.world.invite.profile.name"));
        this.addWidget(this.profileName);
        this.setInitialFocus(this.profileName);
        this.addRenderableWidget(Button.builder(Component.translatable("mco.configure.world.buttons.invite"), $$0 -> this.onInvite()).bounds(this.width / 2 - 100, RealmsInviteScreen.row(10), 200, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, $$0 -> this.minecraft.setScreen(this.lastScreen)).bounds(this.width / 2 - 100, RealmsInviteScreen.row(12), 200, 20).build());
    }

    private void onInvite() {
        RealmsClient $$0 = RealmsClient.create();
        if (this.profileName.getValue() == null || this.profileName.getValue().isEmpty()) {
            this.showError(NO_SUCH_PLAYER_ERROR_TEXT);
            return;
        }
        try {
            RealmsServer $$1 = $$0.invite(this.serverData.id, this.profileName.getValue().trim());
            if ($$1 != null) {
                this.serverData.players = $$1.players;
                this.minecraft.setScreen(new RealmsPlayerScreen(this.configureScreen, this.serverData));
            } else {
                this.showError(NO_SUCH_PLAYER_ERROR_TEXT);
            }
        }
        catch (Exception $$2) {
            LOGGER.error("Couldn't invite user");
            this.showError(NO_SUCH_PLAYER_ERROR_TEXT);
        }
    }

    private void showError(Component $$0) {
        this.errorMsg = $$0;
        this.minecraft.getNarrator().sayNow($$0);
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if ($$0 == 256) {
            this.minecraft.setScreen(this.lastScreen);
            return true;
        }
        return super.keyPressed($$0, $$1, $$2);
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderBackground($$0);
        this.font.draw($$0, NAME_LABEL, (float)(this.width / 2 - 100), (float)RealmsInviteScreen.row(1), 0xA0A0A0);
        if (this.errorMsg != null) {
            RealmsInviteScreen.drawCenteredString($$0, this.font, this.errorMsg, this.width / 2, RealmsInviteScreen.row(5), 0xFF0000);
        }
        this.profileName.render($$0, $$1, $$2, $$3);
        super.render($$0, $$1, $$2, $$3);
    }
}