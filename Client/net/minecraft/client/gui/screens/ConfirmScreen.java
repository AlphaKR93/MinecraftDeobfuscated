/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.Objects
 */
package net.minecraft.client.gui.screens;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.Mth;

public class ConfirmScreen
extends Screen {
    private static final int MARGIN = 20;
    private final Component message;
    private MultiLineLabel multilineMessage = MultiLineLabel.EMPTY;
    protected Component yesButton;
    protected Component noButton;
    private int delayTicker;
    protected final BooleanConsumer callback;
    private final List<Button> exitButtons = Lists.newArrayList();

    public ConfirmScreen(BooleanConsumer $$0, Component $$1, Component $$2) {
        this($$0, $$1, $$2, CommonComponents.GUI_YES, CommonComponents.GUI_NO);
    }

    public ConfirmScreen(BooleanConsumer $$0, Component $$1, Component $$2, Component $$3, Component $$4) {
        super($$1);
        this.callback = $$0;
        this.message = $$2;
        this.yesButton = $$3;
        this.noButton = $$4;
    }

    @Override
    public Component getNarrationMessage() {
        return CommonComponents.joinForNarration(super.getNarrationMessage(), this.message);
    }

    @Override
    protected void init() {
        super.init();
        this.multilineMessage = MultiLineLabel.create(this.font, (FormattedText)this.message, this.width - 50);
        int $$0 = Mth.clamp(this.messageTop() + this.messageHeight() + 20, this.height / 6 + 96, this.height - 24);
        this.exitButtons.clear();
        this.addButtons($$0);
    }

    protected void addButtons(int $$02) {
        this.addExitButton(Button.builder(this.yesButton, $$0 -> this.callback.accept(true)).bounds(this.width / 2 - 155, $$02, 150, 20).build());
        this.addExitButton(Button.builder(this.noButton, $$0 -> this.callback.accept(false)).bounds(this.width / 2 - 155 + 160, $$02, 150, 20).build());
    }

    protected void addExitButton(Button $$0) {
        this.exitButtons.add((Object)this.addRenderableWidget($$0));
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderBackground($$0);
        ConfirmScreen.drawCenteredString($$0, this.font, this.title, this.width / 2, this.titleTop(), 0xFFFFFF);
        this.multilineMessage.renderCentered($$0, this.width / 2, this.messageTop());
        super.render($$0, $$1, $$2, $$3);
    }

    private int titleTop() {
        int $$0 = (this.height - this.messageHeight()) / 2;
        Objects.requireNonNull((Object)this.font);
        return Mth.clamp($$0 - 20 - 9, 10, 80);
    }

    private int messageTop() {
        return this.titleTop() + 20;
    }

    private int messageHeight() {
        int n = this.multilineMessage.getLineCount();
        Objects.requireNonNull((Object)this.font);
        return n * 9;
    }

    public void setDelay(int $$0) {
        this.delayTicker = $$0;
        for (Button $$1 : this.exitButtons) {
            $$1.active = false;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (--this.delayTicker == 0) {
            for (Button $$0 : this.exitButtons) {
                $$0.active = true;
            }
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if ($$0 == 256) {
            this.callback.accept(false);
            return true;
        }
        return super.keyPressed($$0, $$1, $$2);
    }
}