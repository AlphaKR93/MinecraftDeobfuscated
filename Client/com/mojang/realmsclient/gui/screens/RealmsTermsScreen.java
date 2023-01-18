/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Objects
 *  java.util.concurrent.locks.ReentrantLock
 *  org.slf4j.Logger
 */
package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.util.task.GetServerDetailsTask;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.Util;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.realms.RealmsScreen;
import org.slf4j.Logger;

public class RealmsTermsScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Component TITLE = Component.translatable("mco.terms.title");
    private static final Component TERMS_STATIC_TEXT = Component.translatable("mco.terms.sentence.1");
    private static final Component TERMS_LINK_TEXT = Component.literal(" ").append(Component.translatable("mco.terms.sentence.2").withStyle(Style.EMPTY.withUnderlined(true)));
    private final Screen lastScreen;
    private final RealmsMainScreen mainScreen;
    private final RealmsServer realmsServer;
    private boolean onLink;
    private final String realmsToSUrl = "https://aka.ms/MinecraftRealmsTerms";

    public RealmsTermsScreen(Screen $$0, RealmsMainScreen $$1, RealmsServer $$2) {
        super(TITLE);
        this.lastScreen = $$0;
        this.mainScreen = $$1;
        this.realmsServer = $$2;
    }

    @Override
    public void init() {
        int $$02 = this.width / 4 - 2;
        this.addRenderableWidget(Button.builder(Component.translatable("mco.terms.buttons.agree"), $$0 -> this.agreedToTos()).bounds(this.width / 4, RealmsTermsScreen.row(12), $$02, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("mco.terms.buttons.disagree"), $$0 -> this.minecraft.setScreen(this.lastScreen)).bounds(this.width / 2 + 4, RealmsTermsScreen.row(12), $$02, 20).build());
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if ($$0 == 256) {
            this.minecraft.setScreen(this.lastScreen);
            return true;
        }
        return super.keyPressed($$0, $$1, $$2);
    }

    private void agreedToTos() {
        RealmsClient $$0 = RealmsClient.create();
        try {
            $$0.agreeToTos();
            this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, new GetServerDetailsTask(this.mainScreen, this.lastScreen, this.realmsServer, new ReentrantLock())));
        }
        catch (RealmsServiceException $$1) {
            LOGGER.error("Couldn't agree to TOS");
        }
    }

    @Override
    public boolean mouseClicked(double $$0, double $$1, int $$2) {
        if (this.onLink) {
            this.minecraft.keyboardHandler.setClipboard("https://aka.ms/MinecraftRealmsTerms");
            Util.getPlatform().openUri("https://aka.ms/MinecraftRealmsTerms");
            return true;
        }
        return super.mouseClicked($$0, $$1, $$2);
    }

    @Override
    public Component getNarrationMessage() {
        return CommonComponents.joinForNarration(super.getNarrationMessage(), TERMS_STATIC_TEXT).append(" ").append(TERMS_LINK_TEXT);
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderBackground($$0);
        RealmsTermsScreen.drawCenteredString($$0, this.font, this.title, this.width / 2, 17, 0xFFFFFF);
        this.font.draw($$0, TERMS_STATIC_TEXT, (float)(this.width / 2 - 120), (float)RealmsTermsScreen.row(5), 0xFFFFFF);
        int $$4 = this.font.width(TERMS_STATIC_TEXT);
        int $$5 = this.width / 2 - 121 + $$4;
        int $$6 = RealmsTermsScreen.row(5);
        int $$7 = $$5 + this.font.width(TERMS_LINK_TEXT) + 1;
        Objects.requireNonNull((Object)this.font);
        int $$8 = $$6 + 1 + 9;
        this.onLink = $$5 <= $$1 && $$1 <= $$7 && $$6 <= $$2 && $$2 <= $$8;
        this.font.draw($$0, TERMS_LINK_TEXT, (float)(this.width / 2 - 120 + $$4), (float)RealmsTermsScreen.row(5), this.onLink ? 7107012 : 0x3366BB);
        super.render($$0, $$1, $$2, $$3);
    }
}