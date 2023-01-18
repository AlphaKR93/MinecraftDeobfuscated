/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.minecraft.UserApiService
 *  java.lang.Object
 *  java.lang.Runnable
 *  java.util.Objects
 *  java.util.UUID
 *  javax.annotation.Nullable
 */
package net.minecraft.client.multiplayer.chat.report;

import com.mojang.authlib.minecraft.UserApiService;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.reporting.ChatReportScreen;
import net.minecraft.client.multiplayer.chat.ChatLog;
import net.minecraft.client.multiplayer.chat.report.AbuseReportSender;
import net.minecraft.client.multiplayer.chat.report.ChatReportBuilder;
import net.minecraft.client.multiplayer.chat.report.ReportEnvironment;
import net.minecraft.network.chat.Component;

public final class ReportingContext {
    private static final int LOG_CAPACITY = 1024;
    private final AbuseReportSender sender;
    private final ReportEnvironment environment;
    private final ChatLog chatLog;
    @Nullable
    private ChatReportBuilder.ChatReport chatReportDraft;

    public ReportingContext(AbuseReportSender $$0, ReportEnvironment $$1, ChatLog $$2) {
        this.sender = $$0;
        this.environment = $$1;
        this.chatLog = $$2;
    }

    public static ReportingContext create(ReportEnvironment $$0, UserApiService $$1) {
        ChatLog $$2 = new ChatLog(1024);
        AbuseReportSender $$3 = AbuseReportSender.create($$0, $$1);
        return new ReportingContext($$3, $$0, $$2);
    }

    public void draftReportHandled(Minecraft $$0, @Nullable Screen $$1, Runnable $$2, boolean $$3) {
        if (this.chatReportDraft != null) {
            ChatReportBuilder.ChatReport $$42 = this.chatReportDraft.copy();
            $$0.setScreen(new ConfirmScreen($$4 -> {
                this.setChatReportDraft(null);
                if ($$4) {
                    $$0.setScreen(new ChatReportScreen($$1, this, $$42));
                } else {
                    $$2.run();
                }
            }, Component.translatable($$3 ? "gui.chatReport.draft.quittotitle.title" : "gui.chatReport.draft.title"), Component.translatable($$3 ? "gui.chatReport.draft.quittotitle.content" : "gui.chatReport.draft.content"), Component.translatable("gui.chatReport.draft.edit"), Component.translatable("gui.chatReport.draft.discard")));
        } else {
            $$2.run();
        }
    }

    public AbuseReportSender sender() {
        return this.sender;
    }

    public ChatLog chatLog() {
        return this.chatLog;
    }

    public boolean matches(ReportEnvironment $$0) {
        return Objects.equals((Object)((Object)this.environment), (Object)((Object)$$0));
    }

    public void setChatReportDraft(@Nullable ChatReportBuilder.ChatReport $$0) {
        this.chatReportDraft = $$0;
    }

    public boolean hasDraftReport() {
        return this.chatReportDraft != null;
    }

    public boolean hasDraftReportFor(UUID $$0) {
        return this.hasDraftReport() && this.chatReportDraft.isReportedPlayer($$0);
    }
}