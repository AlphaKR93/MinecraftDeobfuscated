/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Objects
 */
package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.realmsclient.exception.RealmsServiceException;
import java.util.Objects;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.realms.RealmsScreen;

public class RealmsGenericErrorScreen
extends RealmsScreen {
    private final Screen nextScreen;
    private final Pair<Component, Component> lines;
    private MultiLineLabel line2Split = MultiLineLabel.EMPTY;

    public RealmsGenericErrorScreen(RealmsServiceException $$0, Screen $$1) {
        super(GameNarrator.NO_TITLE);
        this.nextScreen = $$1;
        this.lines = RealmsGenericErrorScreen.errorMessage($$0);
    }

    public RealmsGenericErrorScreen(Component $$0, Screen $$1) {
        super(GameNarrator.NO_TITLE);
        this.nextScreen = $$1;
        this.lines = RealmsGenericErrorScreen.errorMessage($$0);
    }

    public RealmsGenericErrorScreen(Component $$0, Component $$1, Screen $$2) {
        super(GameNarrator.NO_TITLE);
        this.nextScreen = $$2;
        this.lines = RealmsGenericErrorScreen.errorMessage($$0, $$1);
    }

    private static Pair<Component, Component> errorMessage(RealmsServiceException $$0) {
        if ($$0.realmsError == null) {
            return Pair.of((Object)Component.literal("An error occurred (" + $$0.httpResultCode + "):"), (Object)Component.literal($$0.rawResponse));
        }
        String $$1 = "mco.errorMessage." + $$0.realmsError.getErrorCode();
        return Pair.of((Object)Component.literal("Realms (" + $$0.realmsError + "):"), (Object)(I18n.exists($$1) ? Component.translatable($$1) : Component.nullToEmpty($$0.realmsError.getErrorMessage())));
    }

    private static Pair<Component, Component> errorMessage(Component $$0) {
        return Pair.of((Object)Component.literal("An error occurred: "), (Object)$$0);
    }

    private static Pair<Component, Component> errorMessage(Component $$0, Component $$1) {
        return Pair.of((Object)$$0, (Object)$$1);
    }

    @Override
    public void init() {
        this.addRenderableWidget(Button.builder(Component.literal("Ok"), $$0 -> this.minecraft.setScreen(this.nextScreen)).bounds(this.width / 2 - 100, this.height - 52, 200, 20).build());
        this.line2Split = MultiLineLabel.create(this.font, (FormattedText)this.lines.getSecond(), this.width * 3 / 4);
    }

    @Override
    public Component getNarrationMessage() {
        return Component.empty().append((Component)this.lines.getFirst()).append(": ").append((Component)this.lines.getSecond());
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderBackground($$0);
        RealmsGenericErrorScreen.drawCenteredString($$0, this.font, (Component)this.lines.getFirst(), this.width / 2, 80, 0xFFFFFF);
        int n = this.width / 2;
        Objects.requireNonNull((Object)this.minecraft.font);
        this.line2Split.renderCentered($$0, n, 100, 9, 0xFF0000);
        super.render($$0, $$1, $$2, $$3);
    }
}