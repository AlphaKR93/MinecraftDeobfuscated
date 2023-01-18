/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 */
package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;

public class RealmsLongConfirmationScreen
extends RealmsScreen {
    private final Type type;
    private final Component line2;
    private final Component line3;
    protected final BooleanConsumer callback;
    private final boolean yesNoQuestion;

    public RealmsLongConfirmationScreen(BooleanConsumer $$0, Type $$1, Component $$2, Component $$3, boolean $$4) {
        super(GameNarrator.NO_TITLE);
        this.callback = $$0;
        this.type = $$1;
        this.line2 = $$2;
        this.line3 = $$3;
        this.yesNoQuestion = $$4;
    }

    @Override
    public void init() {
        if (this.yesNoQuestion) {
            this.addRenderableWidget(Button.builder(CommonComponents.GUI_YES, $$0 -> this.callback.accept(true)).bounds(this.width / 2 - 105, RealmsLongConfirmationScreen.row(8), 100, 20).build());
            this.addRenderableWidget(Button.builder(CommonComponents.GUI_NO, $$0 -> this.callback.accept(false)).bounds(this.width / 2 + 5, RealmsLongConfirmationScreen.row(8), 100, 20).build());
        } else {
            this.addRenderableWidget(Button.builder(Component.translatable("mco.gui.ok"), $$0 -> this.callback.accept(true)).bounds(this.width / 2 - 50, RealmsLongConfirmationScreen.row(8), 100, 20).build());
        }
    }

    @Override
    public Component getNarrationMessage() {
        return CommonComponents.joinLines(this.type.text, this.line2, this.line3);
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if ($$0 == 256) {
            this.callback.accept(false);
            return true;
        }
        return super.keyPressed($$0, $$1, $$2);
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderBackground($$0);
        RealmsLongConfirmationScreen.drawCenteredString($$0, this.font, this.type.text, this.width / 2, RealmsLongConfirmationScreen.row(2), this.type.colorCode);
        RealmsLongConfirmationScreen.drawCenteredString($$0, this.font, this.line2, this.width / 2, RealmsLongConfirmationScreen.row(4), 0xFFFFFF);
        RealmsLongConfirmationScreen.drawCenteredString($$0, this.font, this.line3, this.width / 2, RealmsLongConfirmationScreen.row(6), 0xFFFFFF);
        super.render($$0, $$1, $$2, $$3);
    }

    public static enum Type {
        Warning("Warning!", 0xFF0000),
        Info("Info!", 8226750);

        public final int colorCode;
        public final Component text;

        private Type(String $$0, int $$1) {
            this.text = Component.literal($$0);
            this.colorCode = $$1;
        }
    }
}