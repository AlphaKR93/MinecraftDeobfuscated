/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.minecraft.report.AbuseReportLimits
 *  com.mojang.datafixers.util.Unit
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.Throwable
 *  java.util.Objects
 *  java.util.UUID
 *  java.util.concurrent.CancellationException
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.Executor
 *  java.util.function.Consumer
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.screens.reporting;

import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Unit;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.GenericWaitingScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.WarningScreen;
import net.minecraft.client.gui.screens.reporting.ChatSelectionScreen;
import net.minecraft.client.gui.screens.reporting.ReportReasonSelectionScreen;
import net.minecraft.client.multiplayer.chat.report.ChatReportBuilder;
import net.minecraft.client.multiplayer.chat.report.ReportReason;
import net.minecraft.client.multiplayer.chat.report.ReportingContext;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.ThrowingComponent;
import org.slf4j.Logger;

public class ChatReportScreen
extends Screen {
    private static final int BUTTON_WIDTH = 120;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_MARGIN = 20;
    private static final int BUTTON_MARGIN_HALF = 10;
    private static final int LABEL_HEIGHT = 25;
    private static final int SCREEN_WIDTH = 280;
    private static final int SCREEN_HEIGHT = 300;
    private static final Component OBSERVED_WHAT_LABEL = Component.translatable("gui.chatReport.observed_what");
    private static final Component SELECT_REASON = Component.translatable("gui.chatReport.select_reason");
    private static final Component MORE_COMMENTS_LABEL = Component.translatable("gui.chatReport.more_comments");
    private static final Component DESCRIBE_PLACEHOLDER = Component.translatable("gui.chatReport.describe");
    private static final Component REPORT_SENT_MESSAGE = Component.translatable("gui.chatReport.report_sent_msg");
    private static final Component SELECT_CHAT_MESSAGE = Component.translatable("gui.chatReport.select_chat");
    private static final Component REPORT_SENDING_TITLE = Component.translatable("gui.abuseReport.sending.title").withStyle(ChatFormatting.BOLD);
    private static final Component REPORT_SENT_TITLE = Component.translatable("gui.abuseReport.sent.title").withStyle(ChatFormatting.BOLD);
    private static final Component REPORT_ERROR_TITLE = Component.translatable("gui.abuseReport.error.title").withStyle(ChatFormatting.BOLD);
    private static final Component REPORT_SEND_GENERIC_ERROR = Component.translatable("gui.abuseReport.send.generic_error");
    private static final Logger LOGGER = LogUtils.getLogger();
    @Nullable
    final Screen lastScreen;
    private final ReportingContext reportingContext;
    @Nullable
    private MultiLineLabel reasonDescriptionLabel;
    @Nullable
    private MultiLineEditBox commentBox;
    private Button sendButton;
    private ChatReportBuilder reportBuilder;
    @Nullable
    private ChatReportBuilder.CannotBuildReason cannotBuildReason;

    private ChatReportScreen(@Nullable Screen $$0, ReportingContext $$1, ChatReportBuilder $$2) {
        super(Component.translatable("gui.chatReport.title"));
        this.lastScreen = $$0;
        this.reportingContext = $$1;
        this.reportBuilder = $$2;
    }

    public ChatReportScreen(@Nullable Screen $$0, ReportingContext $$1, UUID $$2) {
        this($$0, $$1, new ChatReportBuilder($$2, $$1.sender().reportLimits()));
    }

    public ChatReportScreen(@Nullable Screen $$0, ReportingContext $$1, ChatReportBuilder.ChatReport $$2) {
        this($$0, $$1, new ChatReportBuilder($$2, $$1.sender().reportLimits()));
    }

    @Override
    protected void init() {
        MutableComponent $$5;
        AbuseReportLimits $$03 = this.reportingContext.sender().reportLimits();
        int $$1 = this.width / 2;
        ReportReason $$2 = this.reportBuilder.reason();
        this.reasonDescriptionLabel = $$2 != null ? MultiLineLabel.create(this.font, (FormattedText)$$2.description(), 280) : null;
        IntSet $$3 = this.reportBuilder.reportedMessages();
        if ($$3.isEmpty()) {
            Component $$4 = SELECT_CHAT_MESSAGE;
        } else {
            $$5 = Component.translatable("gui.chatReport.selected_chat", $$3.size());
        }
        this.addRenderableWidget(Button.builder($$5, $$02 -> this.minecraft.setScreen(new ChatSelectionScreen(this, this.reportingContext, this.reportBuilder, (Consumer<ChatReportBuilder>)((Consumer)$$0 -> {
            this.reportBuilder = $$0;
            this.onReportChanged();
        })))).bounds(this.contentLeft(), this.selectChatTop(), 280, 20).build());
        Component $$6 = Util.mapNullable($$2, ReportReason::title, SELECT_REASON);
        this.addRenderableWidget(Button.builder($$6, $$02 -> this.minecraft.setScreen(new ReportReasonSelectionScreen(this, this.reportBuilder.reason(), (Consumer<ReportReason>)((Consumer)$$0 -> {
            this.reportBuilder.setReason((ReportReason)((Object)((Object)$$0)));
            this.onReportChanged();
        })))).bounds(this.contentLeft(), this.selectInfoTop(), 280, 20).build());
        this.commentBox = this.addRenderableWidget(new MultiLineEditBox(this.minecraft.font, this.contentLeft(), this.commentBoxTop(), 280, this.commentBoxBottom() - this.commentBoxTop(), DESCRIBE_PLACEHOLDER, Component.translatable("gui.chatReport.comments")));
        this.commentBox.setValue(this.reportBuilder.comments());
        this.commentBox.setCharacterLimit($$03.maxOpinionCommentsLength());
        this.commentBox.setValueListener((Consumer<String>)((Consumer)$$0 -> {
            this.reportBuilder.setComments((String)$$0);
            this.onReportChanged();
        }));
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, $$0 -> this.onClose()).bounds($$1 - 120, this.completeButtonTop(), 120, 20).build());
        this.sendButton = this.addRenderableWidget(Button.builder(Component.translatable("gui.chatReport.send"), $$0 -> this.sendReport()).bounds($$1 + 10, this.completeButtonTop(), 120, 20).build());
        this.onReportChanged();
    }

    private void onReportChanged() {
        this.cannotBuildReason = this.reportBuilder.checkBuildable();
        this.sendButton.active = this.cannotBuildReason == null;
        this.sendButton.setTooltip((Tooltip)Util.mapNullable(this.cannotBuildReason, $$0 -> Tooltip.create($$0.message())));
    }

    private void sendReport() {
        this.reportBuilder.build(this.reportingContext).ifLeft($$02 -> {
            CompletableFuture<Unit> $$12 = this.reportingContext.sender().send($$02.id(), $$02.report());
            this.minecraft.setScreen(GenericWaitingScreen.createWaiting(REPORT_SENDING_TITLE, CommonComponents.GUI_CANCEL, () -> {
                this.minecraft.setScreen(this);
                $$12.cancel(true);
            }));
            $$12.handleAsync(($$0, $$1) -> {
                if ($$1 == null) {
                    this.onReportSendSuccess();
                } else {
                    if ($$1 instanceof CancellationException) {
                        return null;
                    }
                    this.onReportSendError((Throwable)$$1);
                }
                return null;
            }, (Executor)this.minecraft);
        }).ifRight($$0 -> this.displayReportSendError($$0.message()));
    }

    private void onReportSendSuccess() {
        this.clearDraft();
        this.minecraft.setScreen(GenericWaitingScreen.createCompleted(REPORT_SENT_TITLE, REPORT_SENT_MESSAGE, CommonComponents.GUI_DONE, () -> this.minecraft.setScreen(null)));
    }

    private void onReportSendError(Throwable $$0) {
        Component $$3;
        LOGGER.error("Encountered error while sending abuse report", $$0);
        Throwable throwable = $$0.getCause();
        if (throwable instanceof ThrowingComponent) {
            ThrowingComponent $$1 = (ThrowingComponent)throwable;
            Component $$2 = $$1.getComponent();
        } else {
            $$3 = REPORT_SEND_GENERIC_ERROR;
        }
        this.displayReportSendError($$3);
    }

    private void displayReportSendError(Component $$0) {
        MutableComponent $$1 = $$0.copy().withStyle(ChatFormatting.RED);
        this.minecraft.setScreen(GenericWaitingScreen.createCompleted(REPORT_ERROR_TITLE, $$1, CommonComponents.GUI_BACK, () -> this.minecraft.setScreen(this)));
    }

    void saveDraft() {
        if (this.reportBuilder.hasContent()) {
            this.reportingContext.setChatReportDraft(this.reportBuilder.report().copy());
        }
    }

    void clearDraft() {
        this.reportingContext.setChatReportDraft(null);
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        int $$4 = this.width / 2;
        RenderSystem.disableDepthTest();
        this.renderBackground($$0);
        ChatReportScreen.drawCenteredString($$0, this.font, this.title, $$4, 10, 0xFFFFFF);
        int n = this.selectChatTop();
        Objects.requireNonNull((Object)this.font);
        ChatReportScreen.drawCenteredString($$0, this.font, OBSERVED_WHAT_LABEL, $$4, n - 9 - 6, 0xFFFFFF);
        if (this.reasonDescriptionLabel != null) {
            int n2 = this.contentLeft();
            int n3 = this.selectInfoTop() + 20 + 5;
            Objects.requireNonNull((Object)this.font);
            this.reasonDescriptionLabel.renderLeftAligned($$0, n2, n3, 9, 0xFFFFFF);
        }
        int n4 = this.contentLeft();
        int n5 = this.commentBoxTop();
        Objects.requireNonNull((Object)this.font);
        ChatReportScreen.drawString($$0, this.font, MORE_COMMENTS_LABEL, n4, n5 - 9 - 6, 0xFFFFFF);
        super.render($$0, $$1, $$2, $$3);
        RenderSystem.enableDepthTest();
    }

    @Override
    public void tick() {
        this.commentBox.tick();
        super.tick();
    }

    @Override
    public void onClose() {
        if (this.reportBuilder.hasContent()) {
            this.minecraft.setScreen(new DiscardReportWarningScreen());
        } else {
            this.minecraft.setScreen(this.lastScreen);
        }
    }

    @Override
    public void removed() {
        this.saveDraft();
        super.removed();
    }

    @Override
    public boolean mouseReleased(double $$0, double $$1, int $$2) {
        if (super.mouseReleased($$0, $$1, $$2)) {
            return true;
        }
        return this.commentBox.mouseReleased($$0, $$1, $$2);
    }

    private int contentLeft() {
        return this.width / 2 - 140;
    }

    private int contentRight() {
        return this.width / 2 + 140;
    }

    private int contentTop() {
        return Math.max((int)((this.height - 300) / 2), (int)0);
    }

    private int contentBottom() {
        return Math.min((int)((this.height + 300) / 2), (int)this.height);
    }

    private int selectChatTop() {
        return this.contentTop() + 40;
    }

    private int selectInfoTop() {
        return this.selectChatTop() + 10 + 20;
    }

    private int commentBoxTop() {
        int $$0 = this.selectInfoTop() + 20 + 25;
        if (this.reasonDescriptionLabel != null) {
            int n = this.reasonDescriptionLabel.getLineCount() + 1;
            Objects.requireNonNull((Object)this.font);
            $$0 += n * 9;
        }
        return $$0;
    }

    private int commentBoxBottom() {
        return this.completeButtonTop() - 20;
    }

    private int completeButtonTop() {
        return this.contentBottom() - 20 - 10;
    }

    class DiscardReportWarningScreen
    extends WarningScreen {
        private static final Component TITLE = Component.translatable("gui.chatReport.discard.title").withStyle(ChatFormatting.BOLD);
        private static final Component MESSAGE = Component.translatable("gui.chatReport.discard.content");
        private static final Component RETURN = Component.translatable("gui.chatReport.discard.return");
        private static final Component DRAFT = Component.translatable("gui.chatReport.discard.draft");
        private static final Component DISCARD = Component.translatable("gui.chatReport.discard.discard");

        protected DiscardReportWarningScreen() {
            super(TITLE, MESSAGE, MESSAGE);
        }

        @Override
        protected void initButtons(int $$02) {
            int $$1 = 150;
            this.addRenderableWidget(Button.builder(RETURN, $$0 -> this.onClose()).bounds(this.width / 2 - 155, 100 + $$02, 150, 20).build());
            this.addRenderableWidget(Button.builder(DRAFT, $$0 -> {
                ChatReportScreen.this.saveDraft();
                this.minecraft.setScreen(ChatReportScreen.this.lastScreen);
            }).bounds(this.width / 2 + 5, 100 + $$02, 150, 20).build());
            this.addRenderableWidget(Button.builder(DISCARD, $$0 -> {
                ChatReportScreen.this.clearDraft();
                this.minecraft.setScreen(ChatReportScreen.this.lastScreen);
            }).bounds(this.width / 2 - 75, 130 + $$02, 150, 20).build());
        }

        @Override
        public void onClose() {
            this.minecraft.setScreen(ChatReportScreen.this);
        }

        @Override
        public boolean shouldCloseOnEsc() {
            return false;
        }

        @Override
        protected void renderTitle(PoseStack $$0) {
            DiscardReportWarningScreen.drawString($$0, this.font, this.title, this.width / 2 - 155, 30, 0xFFFFFF);
        }
    }
}