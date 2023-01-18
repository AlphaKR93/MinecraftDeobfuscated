/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Runnable
 *  java.util.Objects
 */
package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Objects;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.Mth;

public class AlertScreen
extends Screen {
    private static final int LABEL_Y = 90;
    private final Component messageText;
    private MultiLineLabel message = MultiLineLabel.EMPTY;
    private final Runnable callback;
    private final Component okButton;
    private final boolean shouldCloseOnEsc;

    public AlertScreen(Runnable $$0, Component $$1, Component $$2) {
        this($$0, $$1, $$2, CommonComponents.GUI_BACK, true);
    }

    public AlertScreen(Runnable $$0, Component $$1, Component $$2, Component $$3, boolean $$4) {
        super($$1);
        this.callback = $$0;
        this.messageText = $$2;
        this.okButton = $$3;
        this.shouldCloseOnEsc = $$4;
    }

    @Override
    public Component getNarrationMessage() {
        return CommonComponents.joinForNarration(super.getNarrationMessage(), this.messageText);
    }

    @Override
    protected void init() {
        super.init();
        this.message = MultiLineLabel.create(this.font, (FormattedText)this.messageText, this.width - 50);
        int n = this.message.getLineCount();
        Objects.requireNonNull((Object)this.font);
        int $$02 = n * 9;
        int $$1 = Mth.clamp(90 + $$02 + 12, this.height / 6 + 96, this.height - 24);
        int $$2 = 150;
        this.addRenderableWidget(Button.builder(this.okButton, $$0 -> this.callback.run()).bounds((this.width - 150) / 2, $$1, 150, 20).build());
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderBackground($$0);
        AlertScreen.drawCenteredString($$0, this.font, this.title, this.width / 2, 70, 0xFFFFFF);
        this.message.renderCentered($$0, this.width / 2, 90);
        super.render($$0, $$1, $$2, $$3);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return this.shouldCloseOnEsc;
    }
}