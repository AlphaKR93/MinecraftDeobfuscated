/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.minecraft.report.AbuseReportLimits
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.Objects
 *  java.util.UUID
 *  java.util.function.Consumer
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.screens.reporting;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.reporting.ChatSelectionLogFiller;
import net.minecraft.client.multiplayer.chat.ChatTrustLevel;
import net.minecraft.client.multiplayer.chat.LoggedChatMessage;
import net.minecraft.client.multiplayer.chat.report.ChatReportBuilder;
import net.minecraft.client.multiplayer.chat.report.ReportingContext;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

public class ChatSelectionScreen
extends Screen {
    private static final Component TITLE = Component.translatable("gui.chatSelection.title");
    private static final Component CONTEXT_INFO = Component.translatable("gui.chatSelection.context").withStyle(ChatFormatting.GRAY);
    @Nullable
    private final Screen lastScreen;
    private final ReportingContext reportingContext;
    private Button confirmSelectedButton;
    private MultiLineLabel contextInfoLabel;
    @Nullable
    private ChatSelectionList chatSelectionList;
    final ChatReportBuilder report;
    private final Consumer<ChatReportBuilder> onSelected;
    private ChatSelectionLogFiller chatLogFiller;

    public ChatSelectionScreen(@Nullable Screen $$0, ReportingContext $$1, ChatReportBuilder $$2, Consumer<ChatReportBuilder> $$3) {
        super(TITLE);
        this.lastScreen = $$0;
        this.reportingContext = $$1;
        this.report = $$2.copy();
        this.onSelected = $$3;
    }

    @Override
    protected void init() {
        this.chatLogFiller = new ChatSelectionLogFiller(this.reportingContext, (Predicate<LoggedChatMessage.Player>)((Predicate)this::canReport));
        this.contextInfoLabel = MultiLineLabel.create(this.font, (FormattedText)CONTEXT_INFO, this.width - 16);
        int n = this.contextInfoLabel.getLineCount() + 1;
        Objects.requireNonNull((Object)this.font);
        this.chatSelectionList = new ChatSelectionList(this.minecraft, n * 9);
        this.chatSelectionList.setRenderBackground(false);
        this.addWidget(this.chatSelectionList);
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, $$0 -> this.onClose()).bounds(this.width / 2 - 155, this.height - 32, 150, 20).build());
        this.confirmSelectedButton = this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, $$0 -> {
            this.onSelected.accept((Object)this.report);
            this.onClose();
        }).bounds(this.width / 2 - 155 + 160, this.height - 32, 150, 20).build());
        this.updateConfirmSelectedButton();
        this.extendLog();
        this.chatSelectionList.setScrollAmount(this.chatSelectionList.getMaxScroll());
    }

    private boolean canReport(LoggedChatMessage $$0) {
        return $$0.canReport(this.report.reportedProfileId());
    }

    private void extendLog() {
        int $$0 = this.chatSelectionList.getMaxVisibleEntries();
        this.chatLogFiller.fillNextPage($$0, this.chatSelectionList);
    }

    void onReachedScrollTop() {
        this.extendLog();
    }

    void updateConfirmSelectedButton() {
        this.confirmSelectedButton.active = !this.report.reportedMessages().isEmpty();
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderBackground($$0);
        this.chatSelectionList.render($$0, $$1, $$2, $$3);
        ChatSelectionScreen.drawCenteredString($$0, this.font, this.title, this.width / 2, 16, 0xFFFFFF);
        AbuseReportLimits $$4 = this.reportingContext.sender().reportLimits();
        int $$5 = this.report.reportedMessages().size();
        int $$6 = $$4.maxReportedMessageCount();
        MutableComponent $$7 = Component.translatable("gui.chatSelection.selected", $$5, $$6);
        int n = this.width / 2;
        Objects.requireNonNull((Object)this.font);
        ChatSelectionScreen.drawCenteredString($$0, this.font, $$7, n, 16 + 9 * 3 / 2, 0xA0A0A0);
        this.contextInfoLabel.renderCentered($$0, this.width / 2, this.chatSelectionList.getFooterTop());
        super.render($$0, $$1, $$2, $$3);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }

    @Override
    public Component getNarrationMessage() {
        return CommonComponents.joinForNarration(super.getNarrationMessage(), CONTEXT_INFO);
    }

    public class ChatSelectionList
    extends ObjectSelectionList<Entry>
    implements ChatSelectionLogFiller.Output {
        @Nullable
        private Heading previousHeading;

        public ChatSelectionList(Minecraft $$1, int $$2) {
            super($$1, ChatSelectionScreen.this.width, ChatSelectionScreen.this.height, 40, ChatSelectionScreen.this.height - 40 - $$2, 16);
        }

        @Override
        public void setScrollAmount(double $$0) {
            double $$1 = this.getScrollAmount();
            super.setScrollAmount($$0);
            if ((float)this.getMaxScroll() > 1.0E-5f && $$0 <= (double)1.0E-5f && !Mth.equal($$0, $$1)) {
                ChatSelectionScreen.this.onReachedScrollTop();
            }
        }

        @Override
        public void acceptMessage(int $$0, LoggedChatMessage.Player $$1) {
            boolean $$2 = $$1.canReport(ChatSelectionScreen.this.report.reportedProfileId());
            ChatTrustLevel $$3 = $$1.trustLevel();
            GuiMessageTag $$4 = $$3.createTag($$1.message());
            MessageEntry $$5 = new MessageEntry($$0, $$1.toContentComponent(), $$1.toNarrationComponent(), $$4, $$2, true);
            this.addEntryToTop($$5);
            this.updateHeading($$1, $$2);
        }

        private void updateHeading(LoggedChatMessage.Player $$0, boolean $$1) {
            MessageHeadingEntry $$2 = new MessageHeadingEntry($$0.profile(), $$0.toHeadingComponent(), $$1);
            this.addEntryToTop($$2);
            Heading $$3 = new Heading($$0.profileId(), $$2);
            if (this.previousHeading != null && this.previousHeading.canCombine($$3)) {
                this.removeEntryFromTop(this.previousHeading.entry());
            }
            this.previousHeading = $$3;
        }

        @Override
        public void acceptDivider(Component $$0) {
            this.addEntryToTop(new PaddingEntry());
            this.addEntryToTop(new DividerEntry($$0));
            this.addEntryToTop(new PaddingEntry());
            this.previousHeading = null;
        }

        @Override
        protected int getScrollbarPosition() {
            return (this.width + this.getRowWidth()) / 2;
        }

        @Override
        public int getRowWidth() {
            return Math.min((int)350, (int)(this.width - 50));
        }

        public int getMaxVisibleEntries() {
            return Mth.positiveCeilDiv(this.y1 - this.y0, this.itemHeight);
        }

        @Override
        protected void renderItem(PoseStack $$0, int $$1, int $$2, float $$3, int $$4, int $$5, int $$6, int $$7, int $$8) {
            Entry $$9 = (Entry)this.getEntry($$4);
            if (this.shouldHighlightEntry($$9)) {
                boolean $$10 = this.getSelected() == $$9;
                int $$11 = this.isFocused() && $$10 ? -1 : -8355712;
                this.renderSelection($$0, $$6, $$7, $$8, $$11, -16777216);
            }
            $$9.render($$0, $$4, $$6, $$5, $$7, $$8, $$1, $$2, this.getHovered() == $$9, $$3);
        }

        private boolean shouldHighlightEntry(Entry $$0) {
            if ($$0.canSelect()) {
                boolean $$1 = this.getSelected() == $$0;
                boolean $$2 = this.getSelected() == null;
                boolean $$3 = this.getHovered() == $$0;
                return $$1 || $$2 && $$3 && $$0.canReport();
            }
            return false;
        }

        @Override
        protected void moveSelection(AbstractSelectionList.SelectionDirection $$0) {
            if (!this.moveSelectableSelection($$0) && $$0 == AbstractSelectionList.SelectionDirection.UP) {
                ChatSelectionScreen.this.onReachedScrollTop();
                this.moveSelectableSelection($$0);
            }
        }

        private boolean moveSelectableSelection(AbstractSelectionList.SelectionDirection $$0) {
            return this.moveSelection($$0, Entry::canSelect);
        }

        @Override
        public boolean keyPressed(int $$0, int $$1, int $$2) {
            Entry $$3 = (Entry)this.getSelected();
            if ($$3 != null && $$3.keyPressed($$0, $$1, $$2)) {
                return true;
            }
            this.setFocused(null);
            return super.keyPressed($$0, $$1, $$2);
        }

        public int getFooterTop() {
            Objects.requireNonNull((Object)ChatSelectionScreen.this.font);
            return this.y1 + 9;
        }

        @Override
        protected boolean isFocused() {
            return ChatSelectionScreen.this.getFocused() == this;
        }

        public class MessageEntry
        extends Entry {
            private static final ResourceLocation CHECKMARK_TEXTURE = new ResourceLocation("minecraft", "textures/gui/checkmark.png");
            private static final int CHECKMARK_WIDTH = 9;
            private static final int CHECKMARK_HEIGHT = 8;
            private static final int INDENT_AMOUNT = 11;
            private static final int TAG_MARGIN_LEFT = 4;
            private final int chatId;
            private final FormattedText text;
            private final Component narration;
            @Nullable
            private final List<FormattedCharSequence> hoverText;
            @Nullable
            private final GuiMessageTag.Icon tagIcon;
            @Nullable
            private final List<FormattedCharSequence> tagHoverText;
            private final boolean canReport;
            private final boolean playerMessage;

            public MessageEntry(int $$1, Component $$2, @Nullable Component $$3, GuiMessageTag $$4, boolean $$5, boolean $$6) {
                this.chatId = $$1;
                this.tagIcon = (GuiMessageTag.Icon)((Object)Util.mapNullable($$4, GuiMessageTag::icon));
                this.tagHoverText = $$4 != null && $$4.text() != null ? ChatSelectionScreen.this.font.split($$4.text(), ChatSelectionList.this.getRowWidth()) : null;
                this.canReport = $$5;
                this.playerMessage = $$6;
                FormattedText $$7 = ChatSelectionScreen.this.font.substrByWidth($$2, this.getMaximumTextWidth() - ChatSelectionScreen.this.font.width(CommonComponents.ELLIPSIS));
                if ($$2 != $$7) {
                    this.text = FormattedText.composite($$7, CommonComponents.ELLIPSIS);
                    this.hoverText = ChatSelectionScreen.this.font.split($$2, ChatSelectionList.this.getRowWidth());
                } else {
                    this.text = $$2;
                    this.hoverText = null;
                }
                this.narration = $$3;
            }

            @Override
            public void render(PoseStack $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
                if (this.isSelected() && this.canReport) {
                    this.renderSelectedCheckmark($$0, $$2, $$3, $$5);
                }
                int $$10 = $$3 + this.getTextIndent();
                Objects.requireNonNull((Object)ChatSelectionScreen.this.font);
                int $$11 = $$2 + 1 + ($$5 - 9) / 2;
                GuiComponent.drawString($$0, ChatSelectionScreen.this.font, Language.getInstance().getVisualOrder(this.text), $$10, $$11, this.canReport ? -1 : -1593835521);
                if (this.hoverText != null && $$8) {
                    ChatSelectionScreen.this.setTooltipForNextRenderPass(this.hoverText);
                }
                int $$12 = ChatSelectionScreen.this.font.width(this.text);
                this.renderTag($$0, $$10 + $$12 + 4, $$2, $$5, $$6, $$7);
            }

            private void renderTag(PoseStack $$0, int $$1, int $$2, int $$3, int $$4, int $$5) {
                if (this.tagIcon != null) {
                    int $$6 = $$2 + ($$3 - this.tagIcon.height) / 2;
                    this.tagIcon.draw($$0, $$1, $$6);
                    if (this.tagHoverText != null && $$4 >= $$1 && $$4 <= $$1 + this.tagIcon.width && $$5 >= $$6 && $$5 <= $$6 + this.tagIcon.height) {
                        ChatSelectionScreen.this.setTooltipForNextRenderPass(this.tagHoverText);
                    }
                }
            }

            private void renderSelectedCheckmark(PoseStack $$0, int $$1, int $$2, int $$3) {
                int $$4 = $$2;
                int $$5 = $$1 + ($$3 - 8) / 2;
                RenderSystem.setShaderTexture(0, CHECKMARK_TEXTURE);
                RenderSystem.enableBlend();
                GuiComponent.blit($$0, $$4, $$5, 0.0f, 0.0f, 9, 8, 9, 8);
                RenderSystem.disableBlend();
            }

            private int getMaximumTextWidth() {
                int $$0 = this.tagIcon != null ? this.tagIcon.width + 4 : 0;
                return ChatSelectionList.this.getRowWidth() - this.getTextIndent() - 4 - $$0;
            }

            private int getTextIndent() {
                return this.playerMessage ? 11 : 0;
            }

            @Override
            public Component getNarration() {
                return this.isSelected() ? Component.translatable("narrator.select", this.narration) : this.narration;
            }

            @Override
            public boolean mouseClicked(double $$0, double $$1, int $$2) {
                if ($$2 == 0) {
                    ChatSelectionList.this.setSelected(null);
                    return this.toggleReport();
                }
                return false;
            }

            @Override
            public boolean keyPressed(int $$0, int $$1, int $$2) {
                if ($$0 == 257 || $$0 == 32 || $$0 == 335) {
                    return this.toggleReport();
                }
                return false;
            }

            @Override
            public boolean isSelected() {
                return ChatSelectionScreen.this.report.isReported(this.chatId);
            }

            @Override
            public boolean canSelect() {
                return true;
            }

            @Override
            public boolean canReport() {
                return this.canReport;
            }

            private boolean toggleReport() {
                if (this.canReport) {
                    ChatSelectionScreen.this.report.toggleReported(this.chatId);
                    ChatSelectionScreen.this.updateConfirmSelectedButton();
                    return true;
                }
                return false;
            }
        }

        public class MessageHeadingEntry
        extends Entry {
            private static final int FACE_SIZE = 12;
            private final Component heading;
            private final ResourceLocation skin;
            private final boolean canReport;

            public MessageHeadingEntry(GameProfile $$1, Component $$2, boolean $$3) {
                this.heading = $$2;
                this.canReport = $$3;
                this.skin = ChatSelectionList.this.minecraft.getSkinManager().getInsecureSkinLocation($$1);
            }

            @Override
            public void render(PoseStack $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
                int $$10 = $$3 - 12 - 4;
                int $$11 = $$2 + ($$5 - 12) / 2;
                this.renderFace($$0, $$10, $$11, this.skin);
                Objects.requireNonNull((Object)ChatSelectionScreen.this.font);
                int $$12 = $$2 + 1 + ($$5 - 9) / 2;
                GuiComponent.drawString($$0, ChatSelectionScreen.this.font, this.heading, $$3, $$12, this.canReport ? -1 : -1593835521);
            }

            private void renderFace(PoseStack $$0, int $$1, int $$2, ResourceLocation $$3) {
                RenderSystem.setShaderTexture(0, $$3);
                PlayerFaceRenderer.draw($$0, $$1, $$2, 12);
            }
        }

        record Heading(UUID sender, Entry entry) {
            public boolean canCombine(Heading $$0) {
                return $$0.sender.equals((Object)this.sender);
            }
        }

        public abstract class Entry
        extends ObjectSelectionList.Entry<Entry> {
            @Override
            public Component getNarration() {
                return CommonComponents.EMPTY;
            }

            public boolean isSelected() {
                return false;
            }

            public boolean canSelect() {
                return false;
            }

            public boolean canReport() {
                return this.canSelect();
            }
        }

        public class PaddingEntry
        extends Entry {
            @Override
            public void render(PoseStack $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
            }
        }

        public class DividerEntry
        extends Entry {
            private static final int COLOR = -6250336;
            private final Component text;

            public DividerEntry(Component $$1) {
                this.text = $$1;
            }

            @Override
            public void render(PoseStack $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
                int $$10 = $$2 + $$5 / 2;
                int $$11 = $$3 + $$4 - 8;
                int $$12 = ChatSelectionScreen.this.font.width(this.text);
                int $$13 = ($$3 + $$11 - $$12) / 2;
                Objects.requireNonNull((Object)ChatSelectionScreen.this.font);
                int $$14 = $$10 - 9 / 2;
                GuiComponent.drawString($$0, ChatSelectionScreen.this.font, this.text, $$13, $$14, -6250336);
            }

            @Override
            public Component getNarration() {
                return this.text;
            }
        }
    }
}