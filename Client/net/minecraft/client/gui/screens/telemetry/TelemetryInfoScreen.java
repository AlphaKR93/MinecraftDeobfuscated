/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Boolean
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.nio.file.Path
 *  java.util.function.Consumer
 */
package net.minecraft.client.gui.screens.telemetry;

import com.mojang.blaze3d.vertex.PoseStack;
import java.nio.file.Path;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CenteredStringWidget;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.telemetry.TelemetryEventWidget;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class TelemetryInfoScreen
extends Screen {
    private static final int PADDING = 8;
    private static final String FEEDBACK_URL = "https://aka.ms/javafeedback?ref=game";
    private static final Component TITLE = Component.translatable("telemetry_info.screen.title");
    private static final Component DESCRIPTION = Component.translatable("telemetry_info.screen.description").withStyle(ChatFormatting.GRAY);
    private static final Component BUTTON_GIVE_FEEDBACK = Component.translatable("telemetry_info.button.give_feedback");
    private static final Component BUTTON_SHOW_DATA = Component.translatable("telemetry_info.button.show_data");
    private final Screen lastScreen;
    private final Options options;
    private TelemetryEventWidget telemetryEventWidget;
    private double savedScroll;

    public TelemetryInfoScreen(Screen $$0, Options $$1) {
        super(TITLE);
        this.lastScreen = $$0;
        this.options = $$1;
    }

    @Override
    public Component getNarrationMessage() {
        return CommonComponents.joinForNarration(super.getNarrationMessage(), DESCRIPTION);
    }

    @Override
    protected void init() {
        FrameLayout $$02 = new FrameLayout(0, 0, this.width, this.height);
        $$02.defaultChildLayoutSetting().padding(8);
        $$02.setMinHeight(this.height);
        GridLayout $$12 = $$02.addChild(new GridLayout(), $$02.newChildLayoutSettings().align(0.5f, 0.0f));
        $$12.defaultCellSetting().alignHorizontallyCenter().paddingBottom(8);
        GridLayout.RowHelper $$2 = $$12.createRowHelper(1);
        $$2.addChild(new CenteredStringWidget(this.getTitle(), this.font));
        $$2.addChild(MultiLineTextWidget.createCentered(this.width - 16, this.font, DESCRIPTION));
        GridLayout $$3 = this.twoButtonContainer(Button.builder(BUTTON_GIVE_FEEDBACK, this::openFeedbackLink).build(), Button.builder(BUTTON_SHOW_DATA, this::openDataFolder).build());
        $$2.addChild($$3);
        GridLayout $$4 = this.twoButtonContainer(this.createTelemetryButton(), Button.builder(CommonComponents.GUI_DONE, this::openLastScreen).build());
        $$02.addChild($$4, $$02.newChildLayoutSettings().align(0.5f, 1.0f));
        $$02.arrangeElements();
        this.telemetryEventWidget = new TelemetryEventWidget(0, 0, this.width - 40, $$4.getY() - ($$3.getY() + $$3.getHeight()) - 16, this.minecraft.font);
        this.telemetryEventWidget.setScrollAmount(this.savedScroll);
        this.telemetryEventWidget.setOnScrolledListener($$0 -> {
            this.savedScroll = $$0;
        });
        this.setInitialFocus(this.telemetryEventWidget);
        $$2.addChild(this.telemetryEventWidget);
        $$02.arrangeElements();
        FrameLayout.alignInRectangle($$02, 0, 0, this.width, this.height, 0.5f, 0.0f);
        $$02.visitWidgets((Consumer<AbstractWidget>)((Consumer)$$1 -> {
            AbstractWidget cfr_ignored_0 = (AbstractWidget)this.addRenderableWidget($$1);
        }));
    }

    private AbstractWidget createTelemetryButton() {
        AbstractWidget $$02 = this.options.telemetryOptInExtra().createButton(this.options, 0, 0, 150, (Consumer<Boolean>)((Consumer)$$0 -> this.telemetryEventWidget.onOptInChanged((boolean)$$0)));
        $$02.active = this.minecraft.extraTelemetryAvailable();
        return $$02;
    }

    private void openLastScreen(Button $$0) {
        this.minecraft.setScreen(this.lastScreen);
    }

    private void openFeedbackLink(Button $$02) {
        this.minecraft.setScreen(new ConfirmLinkScreen($$0 -> {
            if ($$0) {
                Util.getPlatform().openUri(FEEDBACK_URL);
            }
            this.minecraft.setScreen(this);
        }, FEEDBACK_URL, true));
    }

    private void openDataFolder(Button $$0) {
        Path $$1 = this.minecraft.getTelemetryManager().getLogDirectory();
        Util.getPlatform().openUri($$1.toUri());
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderDirtBackground($$0);
        super.render($$0, $$1, $$2, $$3);
    }

    private GridLayout twoButtonContainer(AbstractWidget $$0, AbstractWidget $$1) {
        GridLayout $$2 = new GridLayout();
        $$2.defaultCellSetting().alignHorizontallyCenter().paddingHorizontal(4);
        $$2.addChild($$0, 0, 0);
        $$2.addChild($$1, 0, 1);
        return $$2;
    }
}