/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.authlib.minecraft.report.AbuseReport
 *  com.mojang.authlib.minecraft.report.AbuseReportLimits
 *  com.mojang.authlib.minecraft.report.ReportChatMessage
 *  com.mojang.authlib.minecraft.report.ReportEvidence
 *  com.mojang.authlib.minecraft.report.ReportedEntity
 *  com.mojang.datafixers.util.Either
 *  it.unimi.dsi.fastutil.ints.IntCollection
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  java.lang.CharSequence
 *  java.lang.Object
 *  java.lang.String
 *  java.nio.ByteBuffer
 *  java.time.Instant
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.Objects
 *  java.util.UUID
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 */
package net.minecraft.client.multiplayer.chat.report;

import com.google.common.collect.Lists;
import com.mojang.authlib.minecraft.report.AbuseReport;
import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import com.mojang.authlib.minecraft.report.ReportChatMessage;
import com.mojang.authlib.minecraft.report.ReportEvidence;
import com.mojang.authlib.minecraft.report.ReportedEntity;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.multiplayer.chat.ChatLog;
import net.minecraft.client.multiplayer.chat.LoggedChatMessage;
import net.minecraft.client.multiplayer.chat.report.ChatReportContextBuilder;
import net.minecraft.client.multiplayer.chat.report.ReportReason;
import net.minecraft.client.multiplayer.chat.report.ReportingContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.SignedMessageBody;
import net.minecraft.network.chat.SignedMessageLink;
import org.apache.commons.lang3.StringUtils;

public class ChatReportBuilder {
    private final ChatReport report;
    private final AbuseReportLimits limits;

    public ChatReportBuilder(ChatReport $$0, AbuseReportLimits $$1) {
        this.report = $$0;
        this.limits = $$1;
    }

    public ChatReportBuilder(UUID $$0, AbuseReportLimits $$1) {
        this.report = new ChatReport(UUID.randomUUID(), Instant.now(), $$0);
        this.limits = $$1;
    }

    public ChatReport report() {
        return this.report;
    }

    public UUID reportedProfileId() {
        return this.report.reportedProfileId;
    }

    public IntSet reportedMessages() {
        return this.report.reportedMessages;
    }

    public String comments() {
        return this.report.comments;
    }

    public void setComments(String $$0) {
        this.report.comments = $$0;
    }

    @Nullable
    public ReportReason reason() {
        return this.report.reason;
    }

    public void setReason(ReportReason $$0) {
        this.report.reason = $$0;
    }

    public void toggleReported(int $$0) {
        this.report.toggleReported($$0, this.limits);
    }

    public boolean isReported(int $$0) {
        return this.report.reportedMessages.contains($$0);
    }

    public boolean hasContent() {
        return StringUtils.isNotEmpty((CharSequence)this.comments()) || !this.reportedMessages().isEmpty() || this.reason() != null;
    }

    @Nullable
    public CannotBuildReason checkBuildable() {
        if (this.report.reportedMessages.isEmpty()) {
            return CannotBuildReason.NO_REPORTED_MESSAGES;
        }
        if (this.report.reportedMessages.size() > this.limits.maxReportedMessageCount()) {
            return CannotBuildReason.TOO_MANY_MESSAGES;
        }
        if (this.report.reason == null) {
            return CannotBuildReason.NO_REASON;
        }
        if (this.report.comments.length() > this.limits.maxOpinionCommentsLength()) {
            return CannotBuildReason.COMMENTS_TOO_LONG;
        }
        return null;
    }

    public Either<Result, CannotBuildReason> build(ReportingContext $$0) {
        CannotBuildReason $$1 = this.checkBuildable();
        if ($$1 != null) {
            return Either.right((Object)((Object)$$1));
        }
        String $$2 = ((ReportReason)((Object)Objects.requireNonNull((Object)((Object)this.report.reason)))).backendName();
        ReportEvidence $$3 = this.buildEvidence($$0.chatLog());
        ReportedEntity $$4 = new ReportedEntity(this.report.reportedProfileId);
        AbuseReport $$5 = new AbuseReport(this.report.comments, $$2, $$3, $$4, this.report.createdAt);
        return Either.left((Object)((Object)new Result(this.report.reportId, $$5)));
    }

    private ReportEvidence buildEvidence(ChatLog $$0) {
        ArrayList $$1 = new ArrayList();
        ChatReportContextBuilder $$2 = new ChatReportContextBuilder(this.limits.leadingContextMessageCount());
        $$2.collectAllContext($$0, (IntCollection)this.report.reportedMessages, (arg_0, arg_1) -> this.lambda$buildEvidence$0((List)$$1, arg_0, arg_1));
        return new ReportEvidence(Lists.reverse((List)$$1));
    }

    private ReportChatMessage buildReportedChatMessage(LoggedChatMessage.Player $$0, boolean $$1) {
        SignedMessageLink $$2 = $$0.message().link();
        SignedMessageBody $$3 = $$0.message().signedBody();
        List $$4 = $$3.lastSeen().entries().stream().map(MessageSignature::asByteBuffer).toList();
        ByteBuffer $$5 = (ByteBuffer)Util.mapNullable($$0.message().signature(), MessageSignature::asByteBuffer);
        return new ReportChatMessage($$2.index(), $$2.sender(), $$2.sessionId(), $$3.timeStamp(), $$3.salt(), $$4, $$3.content(), $$5, $$1);
    }

    public ChatReportBuilder copy() {
        return new ChatReportBuilder(this.report.copy(), this.limits);
    }

    private /* synthetic */ void lambda$buildEvidence$0(List $$0, int $$1, LoggedChatMessage.Player $$2) {
        $$0.add((Object)this.buildReportedChatMessage($$2, this.isReported($$1)));
    }

    public class ChatReport {
        final UUID reportId;
        final Instant createdAt;
        final UUID reportedProfileId;
        final IntSet reportedMessages = new IntOpenHashSet();
        String comments = "";
        @Nullable
        ReportReason reason;

        ChatReport(UUID $$1, Instant $$2, UUID $$3) {
            this.reportId = $$1;
            this.createdAt = $$2;
            this.reportedProfileId = $$3;
        }

        public void toggleReported(int $$0, AbuseReportLimits $$1) {
            if (this.reportedMessages.contains($$0)) {
                this.reportedMessages.remove($$0);
            } else if (this.reportedMessages.size() < $$1.maxReportedMessageCount()) {
                this.reportedMessages.add($$0);
            }
        }

        public ChatReport copy() {
            ChatReport $$0 = new ChatReport(this.reportId, this.createdAt, this.reportedProfileId);
            $$0.reportedMessages.addAll((IntCollection)this.reportedMessages);
            $$0.comments = this.comments;
            $$0.reason = this.reason;
            return $$0;
        }

        public boolean isReportedPlayer(UUID $$0) {
            return $$0.equals((Object)this.reportedProfileId);
        }
    }

    public record CannotBuildReason(Component message) {
        public static final CannotBuildReason NO_REASON = new CannotBuildReason(Component.translatable("gui.chatReport.send.no_reason"));
        public static final CannotBuildReason NO_REPORTED_MESSAGES = new CannotBuildReason(Component.translatable("gui.chatReport.send.no_reported_messages"));
        public static final CannotBuildReason TOO_MANY_MESSAGES = new CannotBuildReason(Component.translatable("gui.chatReport.send.too_many_messages"));
        public static final CannotBuildReason COMMENTS_TOO_LONG = new CannotBuildReason(Component.translatable("gui.chatReport.send.comments_too_long"));
    }

    public record Result(UUID id, AbuseReport report) {
    }
}