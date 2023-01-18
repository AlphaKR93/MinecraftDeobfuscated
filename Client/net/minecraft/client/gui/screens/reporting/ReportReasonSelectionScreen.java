/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Objects
 *  java.util.function.Consumer
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.screens.reporting;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.chat.report.ReportReason;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ReportReasonSelectionScreen
extends Screen {
    private static final String ADDITIONAL_INFO_LINK = "https://aka.ms/aboutjavareporting";
    private static final Component REASON_TITLE = Component.translatable("gui.abuseReport.reason.title");
    private static final Component REASON_DESCRIPTION = Component.translatable("gui.abuseReport.reason.description");
    private static final Component READ_INFO_LABEL = Component.translatable("gui.chatReport.read_info");
    private static final int FOOTER_HEIGHT = 95;
    private static final int BUTTON_WIDTH = 150;
    private static final int BUTTON_HEIGHT = 20;
    private static final int CONTENT_WIDTH = 320;
    private static final int PADDING = 4;
    @Nullable
    private final Screen lastScreen;
    @Nullable
    private ReasonSelectionList reasonSelectionList;
    @Nullable
    ReportReason currentlySelectedReason;
    private final Consumer<ReportReason> onSelectedReason;

    public ReportReasonSelectionScreen(@Nullable Screen $$0, @Nullable ReportReason $$1, Consumer<ReportReason> $$2) {
        super(REASON_TITLE);
        this.lastScreen = $$0;
        this.currentlySelectedReason = $$1;
        this.onSelectedReason = $$2;
    }

    @Override
    protected void init() {
        this.reasonSelectionList = new ReasonSelectionList(this.minecraft);
        this.reasonSelectionList.setRenderBackground(false);
        this.addWidget(this.reasonSelectionList);
        ReasonSelectionList.Entry $$03 = (ReasonSelectionList.Entry)Util.mapNullable(this.currentlySelectedReason, this.reasonSelectionList::findEntry);
        this.reasonSelectionList.setSelected($$03);
        int $$1 = this.width / 2 - 150 - 5;
        this.addRenderableWidget(Button.builder(READ_INFO_LABEL, $$02 -> this.minecraft.setScreen(new ConfirmLinkScreen($$0 -> {
            if ($$0) {
                Util.getPlatform().openUri(ADDITIONAL_INFO_LINK);
            }
            this.minecraft.setScreen(this);
        }, ADDITIONAL_INFO_LINK, true))).bounds($$1, this.buttonTop(), 150, 20).build());
        int $$2 = this.width / 2 + 5;
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, $$0 -> {
            ReasonSelectionList.Entry $$1 = (ReasonSelectionList.Entry)this.reasonSelectionList.getSelected();
            if ($$1 != null) {
                this.onSelectedReason.accept((Object)$$1.getReason());
            }
            this.minecraft.setScreen(this.lastScreen);
        }).bounds($$2, this.buttonTop(), 150, 20).build());
        super.init();
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderBackground($$0);
        this.reasonSelectionList.render($$0, $$1, $$2, $$3);
        ReportReasonSelectionScreen.drawCenteredString($$0, this.font, this.title, this.width / 2, 16, 0xFFFFFF);
        super.render($$0, $$1, $$2, $$3);
        ReportReasonSelectionScreen.fill($$0, this.contentLeft(), this.descriptionTop(), this.contentRight(), this.descriptionBottom(), 0x7F000000);
        ReportReasonSelectionScreen.drawString($$0, this.font, REASON_DESCRIPTION, this.contentLeft() + 4, this.descriptionTop() + 4, -8421505);
        ReasonSelectionList.Entry $$4 = (ReasonSelectionList.Entry)this.reasonSelectionList.getSelected();
        if ($$4 != null) {
            int $$5 = this.contentLeft() + 4 + 16;
            int $$6 = this.contentRight() - 4;
            int n = this.descriptionTop() + 4;
            Objects.requireNonNull((Object)this.font);
            int $$7 = n + 9 + 2;
            int $$8 = this.descriptionBottom() - 4;
            int $$9 = $$6 - $$5;
            int $$10 = $$8 - $$7;
            int $$11 = this.font.wordWrapHeight($$4.reason.description(), $$9);
            this.font.drawWordWrap($$4.reason.description(), $$5, $$7 + ($$10 - $$11) / 2, $$9, -1);
        }
    }

    private int buttonTop() {
        return this.height - 20 - 4;
    }

    private int contentLeft() {
        return (this.width - 320) / 2;
    }

    private int contentRight() {
        return (this.width + 320) / 2;
    }

    private int descriptionTop() {
        return this.height - 95 + 4;
    }

    private int descriptionBottom() {
        return this.buttonTop() - 4;
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }

    public class ReasonSelectionList
    extends ObjectSelectionList<Entry> {
        public ReasonSelectionList(Minecraft $$1) {
            super($$1, ReportReasonSelectionScreen.this.width, ReportReasonSelectionScreen.this.height, 40, ReportReasonSelectionScreen.this.height - 95, 18);
            for (ReportReason $$2 : ReportReason.values()) {
                if (!$$2.reportable()) continue;
                this.addEntry(new Entry($$2));
            }
        }

        @Nullable
        public Entry findEntry(ReportReason $$0) {
            return (Entry)this.children().stream().filter($$1 -> $$1.reason == $$0).findFirst().orElse(null);
        }

        @Override
        public int getRowWidth() {
            return 320;
        }

        @Override
        protected int getScrollbarPosition() {
            return this.getRowRight() - 2;
        }

        @Override
        public void setSelected(@Nullable Entry $$0) {
            super.setSelected($$0);
            ReportReasonSelectionScreen.this.currentlySelectedReason = $$0 != null ? $$0.getReason() : null;
        }

        public class Entry
        extends ObjectSelectionList.Entry<Entry> {
            final ReportReason reason;

            public Entry(ReportReason $$1) {
                this.reason = $$1;
            }

            @Override
            public void render(PoseStack $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
                int $$10 = $$3 + 1;
                Objects.requireNonNull((Object)ReportReasonSelectionScreen.this.font);
                int $$11 = $$2 + ($$5 - 9) / 2 + 1;
                GuiComponent.drawString($$0, ReportReasonSelectionScreen.this.font, this.reason.title(), $$10, $$11, -1);
            }

            @Override
            public Component getNarration() {
                return Component.translatable("gui.abuseReport.reason.narration", this.reason.title(), this.reason.description());
            }

            @Override
            public boolean mouseClicked(double $$0, double $$1, int $$2) {
                if ($$2 == 0) {
                    ReasonSelectionList.this.setSelected(this);
                    return true;
                }
                return false;
            }

            public ReportReason getReason() {
                return this.reason;
            }
        }
    }
}