/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.Objects
 */
package net.minecraft.client.gui.screens;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.FormattedText;

public class PopupScreen
extends Screen {
    private static final int BUTTON_PADDING = 20;
    private static final int BUTTON_MARGIN = 5;
    private static final int BUTTON_HEIGHT = 20;
    private final Component narrationMessage;
    private final FormattedText message;
    private final ImmutableList<ButtonOption> buttonOptions;
    private MultiLineLabel messageLines = MultiLineLabel.EMPTY;
    private int contentTop;
    private int buttonWidth;

    protected PopupScreen(Component $$0, List<Component> $$1, ImmutableList<ButtonOption> $$2) {
        super($$0);
        this.message = FormattedText.composite($$1);
        this.narrationMessage = CommonComponents.joinForNarration($$0, ComponentUtils.formatList($$1, CommonComponents.EMPTY));
        this.buttonOptions = $$2;
    }

    @Override
    public Component getNarrationMessage() {
        return this.narrationMessage;
    }

    @Override
    public void init() {
        for (ButtonOption $$0 : this.buttonOptions) {
            this.buttonWidth = Math.max((int)this.buttonWidth, (int)(20 + this.font.width($$0.message) + 20));
        }
        int $$1 = 5 + this.buttonWidth + 5;
        int $$2 = $$1 * this.buttonOptions.size();
        this.messageLines = MultiLineLabel.create(this.font, this.message, $$2);
        int n = this.messageLines.getLineCount();
        Objects.requireNonNull((Object)this.font);
        int $$3 = n * 9;
        this.contentTop = (int)((double)this.height / 2.0 - (double)$$3 / 2.0);
        Objects.requireNonNull((Object)this.font);
        int $$4 = this.contentTop + $$3 + 9 * 2;
        int $$5 = (int)((double)this.width / 2.0 - (double)$$2 / 2.0);
        for (ButtonOption $$6 : this.buttonOptions) {
            this.addRenderableWidget(Button.builder($$6.message, $$6.onPress).bounds($$5, $$4, this.buttonWidth, 20).build());
            $$5 += $$1;
        }
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderDirtBackground($$0);
        int n = this.width / 2;
        Objects.requireNonNull((Object)this.font);
        PopupScreen.drawCenteredString($$0, this.font, this.title, n, this.contentTop - 9 * 2, -1);
        this.messageLines.renderCentered($$0, this.width / 2, this.contentTop);
        super.render($$0, $$1, $$2, $$3);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    public static final class ButtonOption {
        final Component message;
        final Button.OnPress onPress;

        public ButtonOption(Component $$0, Button.OnPress $$1) {
            this.message = $$0;
            this.onPress = $$1;
        }
    }
}