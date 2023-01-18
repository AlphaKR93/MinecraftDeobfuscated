/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.minecraft.BanDetails
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  java.lang.CharSequence
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.String
 *  java.time.Duration
 *  java.time.Instant
 *  java.time.temporal.Temporal
 *  org.apache.commons.lang3.StringUtils
 */
package net.minecraft.client.gui.screens;

import com.mojang.authlib.minecraft.BanDetails;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.Temporal;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.multiplayer.chat.report.ReportReason;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;
import org.apache.commons.lang3.StringUtils;

public class BanNoticeScreen {
    public static final String URL_MODERATION = "https://aka.ms/mcjavamoderation";
    private static final Component TEMPORARY_BAN_TITLE = Component.translatable("gui.banned.title.temporary").withStyle(ChatFormatting.BOLD);
    private static final Component PERMANENT_BAN_TITLE = Component.translatable("gui.banned.title.permanent").withStyle(ChatFormatting.BOLD);

    public static ConfirmLinkScreen create(BooleanConsumer $$0, BanDetails $$1) {
        return new ConfirmLinkScreen($$0, BanNoticeScreen.getBannedTitle($$1), BanNoticeScreen.getBannedScreenText($$1), URL_MODERATION, CommonComponents.GUI_ACKNOWLEDGE, true);
    }

    private static Component getBannedTitle(BanDetails $$0) {
        return BanNoticeScreen.isTemporaryBan($$0) ? TEMPORARY_BAN_TITLE : PERMANENT_BAN_TITLE;
    }

    private static Component getBannedScreenText(BanDetails $$0) {
        return Component.translatable("gui.banned.description", BanNoticeScreen.getBanReasonText($$0), BanNoticeScreen.getBanStatusText($$0), Component.literal(URL_MODERATION));
    }

    private static Component getBanReasonText(BanDetails $$0) {
        String $$1 = $$0.reason();
        String $$2 = $$0.reasonMessage();
        if (StringUtils.isNumeric((CharSequence)$$1)) {
            int $$3 = Integer.parseInt((String)$$1);
            Component $$4 = ReportReason.getTranslationById($$3);
            $$4 = $$4 != null ? ComponentUtils.mergeStyles($$4.copy(), Style.EMPTY.withBold(true)) : ($$2 != null ? Component.translatable("gui.banned.description.reason_id_message", $$3, $$2).withStyle(ChatFormatting.BOLD) : Component.translatable("gui.banned.description.reason_id", $$3).withStyle(ChatFormatting.BOLD));
            return Component.translatable("gui.banned.description.reason", $$4);
        }
        return Component.translatable("gui.banned.description.unknownreason");
    }

    private static Component getBanStatusText(BanDetails $$0) {
        if (BanNoticeScreen.isTemporaryBan($$0)) {
            Component $$1 = BanNoticeScreen.getBanDurationText($$0);
            return Component.translatable("gui.banned.description.temporary", Component.translatable("gui.banned.description.temporary.duration", $$1).withStyle(ChatFormatting.BOLD));
        }
        return Component.translatable("gui.banned.description.permanent").withStyle(ChatFormatting.BOLD);
    }

    private static Component getBanDurationText(BanDetails $$0) {
        Duration $$1 = Duration.between((Temporal)Instant.now(), (Temporal)$$0.expires());
        long $$2 = $$1.toHours();
        if ($$2 > 72L) {
            return CommonComponents.days($$1.toDays());
        }
        if ($$2 < 1L) {
            return CommonComponents.minutes($$1.toMinutes());
        }
        return CommonComponents.hours($$1.toHours());
    }

    private static boolean isTemporaryBan(BanDetails $$0) {
        return $$0.expires() != null;
    }
}