/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Objects
 */
package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Objects;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;

public class DisconnectedScreen
extends Screen {
    private final Component reason;
    private MultiLineLabel message = MultiLineLabel.EMPTY;
    private final Screen parent;
    private int textHeight;

    public DisconnectedScreen(Screen $$0, Component $$1, Component $$2) {
        super($$1);
        this.parent = $$0;
        this.reason = $$2;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    protected void init() {
        this.message = MultiLineLabel.create(this.font, (FormattedText)this.reason, this.width - 50);
        int n = this.message.getLineCount();
        Objects.requireNonNull((Object)this.font);
        this.textHeight = n * 9;
        Button.Builder builder = Button.builder(Component.translatable("gui.toMenu"), $$0 -> this.minecraft.setScreen(this.parent));
        int n2 = this.width / 2 - 100;
        int n3 = this.height / 2 + this.textHeight / 2;
        Objects.requireNonNull((Object)this.font);
        this.addRenderableWidget(builder.bounds(n2, Math.min((int)(n3 + 9), (int)(this.height - 30)), 200, 20).build());
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderBackground($$0);
        int n = this.width / 2;
        int n2 = this.height / 2 - this.textHeight / 2;
        Objects.requireNonNull((Object)this.font);
        DisconnectedScreen.drawCenteredString($$0, this.font, this.title, n, n2 - 9 * 2, 0xAAAAAA);
        this.message.renderCentered($$0, this.width / 2, this.height / 2 - this.textHeight / 2);
        super.render($$0, $$1, $$2, $$3);
    }
}