/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 */
package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.realms.RealmsScreen;

public class RealmsParentalConsentScreen
extends RealmsScreen {
    private static final Component MESSAGE = Component.translatable("mco.account.privacyinfo");
    private final Screen nextScreen;
    private MultiLineLabel messageLines = MultiLineLabel.EMPTY;

    public RealmsParentalConsentScreen(Screen $$0) {
        super(GameNarrator.NO_TITLE);
        this.nextScreen = $$0;
    }

    @Override
    public void init() {
        MutableComponent $$02 = Component.translatable("mco.account.update");
        Component $$1 = CommonComponents.GUI_BACK;
        int $$2 = Math.max((int)this.font.width($$02), (int)this.font.width($$1)) + 30;
        MutableComponent $$3 = Component.translatable("mco.account.privacy.info");
        int $$4 = (int)((double)this.font.width($$3) * 1.2);
        this.addRenderableWidget(Button.builder($$3, $$0 -> Util.getPlatform().openUri("https://aka.ms/MinecraftGDPR")).bounds(this.width / 2 - $$4 / 2, RealmsParentalConsentScreen.row(11), $$4, 20).build());
        this.addRenderableWidget(Button.builder($$02, $$0 -> Util.getPlatform().openUri("https://aka.ms/UpdateMojangAccount")).bounds(this.width / 2 - ($$2 + 5), RealmsParentalConsentScreen.row(13), $$2, 20).build());
        this.addRenderableWidget(Button.builder($$1, $$0 -> this.minecraft.setScreen(this.nextScreen)).bounds(this.width / 2 + 5, RealmsParentalConsentScreen.row(13), $$2, 20).build());
        this.messageLines = MultiLineLabel.create(this.font, (FormattedText)MESSAGE, (int)Math.round((double)((double)this.width * 0.9)));
    }

    @Override
    public Component getNarrationMessage() {
        return MESSAGE;
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderBackground($$0);
        this.messageLines.renderCentered($$0, this.width / 2, 15, 15, 0xFFFFFF);
        super.render($$0, $$1, $$2, $$3);
    }
}