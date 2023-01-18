/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.Thread
 *  java.lang.Throwable
 *  java.text.DateFormat
 *  java.util.GregorianCalendar
 *  java.util.TimeZone
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.Subscription;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen;
import java.text.DateFormat;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.realms.RealmsScreen;
import org.slf4j.Logger;

public class RealmsSubscriptionInfoScreen
extends RealmsScreen {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final Component SUBSCRIPTION_TITLE = Component.translatable("mco.configure.world.subscription.title");
    private static final Component SUBSCRIPTION_START_LABEL = Component.translatable("mco.configure.world.subscription.start");
    private static final Component TIME_LEFT_LABEL = Component.translatable("mco.configure.world.subscription.timeleft");
    private static final Component DAYS_LEFT_LABEL = Component.translatable("mco.configure.world.subscription.recurring.daysleft");
    private static final Component SUBSCRIPTION_EXPIRED_TEXT = Component.translatable("mco.configure.world.subscription.expired");
    private static final Component SUBSCRIPTION_LESS_THAN_A_DAY_TEXT = Component.translatable("mco.configure.world.subscription.less_than_a_day");
    private static final Component MONTH_SUFFIX = Component.translatable("mco.configure.world.subscription.month");
    private static final Component MONTHS_SUFFIX = Component.translatable("mco.configure.world.subscription.months");
    private static final Component DAY_SUFFIX = Component.translatable("mco.configure.world.subscription.day");
    private static final Component DAYS_SUFFIX = Component.translatable("mco.configure.world.subscription.days");
    private static final Component UNKNOWN = Component.translatable("mco.configure.world.subscription.unknown");
    private final Screen lastScreen;
    final RealmsServer serverData;
    final Screen mainScreen;
    private Component daysLeft = UNKNOWN;
    private Component startDate = UNKNOWN;
    @Nullable
    private Subscription.SubscriptionType type;
    private static final String PURCHASE_LINK = "https://aka.ms/ExtendJavaRealms";

    public RealmsSubscriptionInfoScreen(Screen $$0, RealmsServer $$1, Screen $$2) {
        super(GameNarrator.NO_TITLE);
        this.lastScreen = $$0;
        this.serverData = $$1;
        this.mainScreen = $$2;
    }

    @Override
    public void init() {
        this.getSubscription(this.serverData.id);
        this.addRenderableWidget(Button.builder(Component.translatable("mco.configure.world.subscription.extend"), $$0 -> {
            String $$1 = "https://aka.ms/ExtendJavaRealms?subscriptionId=" + this.serverData.remoteSubscriptionId + "&profileId=" + this.minecraft.getUser().getUuid();
            this.minecraft.keyboardHandler.setClipboard($$1);
            Util.getPlatform().openUri($$1);
        }).bounds(this.width / 2 - 100, RealmsSubscriptionInfoScreen.row(6), 200, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, $$0 -> this.minecraft.setScreen(this.lastScreen)).bounds(this.width / 2 - 100, RealmsSubscriptionInfoScreen.row(12), 200, 20).build());
        if (this.serverData.expired) {
            this.addRenderableWidget(Button.builder(Component.translatable("mco.configure.world.delete.button"), $$0 -> {
                MutableComponent $$1 = Component.translatable("mco.configure.world.delete.question.line1");
                MutableComponent $$2 = Component.translatable("mco.configure.world.delete.question.line2");
                this.minecraft.setScreen(new RealmsLongConfirmationScreen(this::deleteRealm, RealmsLongConfirmationScreen.Type.Warning, $$1, $$2, true));
            }).bounds(this.width / 2 - 100, RealmsSubscriptionInfoScreen.row(10), 200, 20).build());
        }
    }

    @Override
    public Component getNarrationMessage() {
        return CommonComponents.joinLines(SUBSCRIPTION_TITLE, SUBSCRIPTION_START_LABEL, this.startDate, TIME_LEFT_LABEL, this.daysLeft);
    }

    private void deleteRealm(boolean $$0) {
        if ($$0) {
            new Thread("Realms-delete-realm"){

                public void run() {
                    try {
                        RealmsClient $$0 = RealmsClient.create();
                        $$0.deleteWorld(RealmsSubscriptionInfoScreen.this.serverData.id);
                    }
                    catch (RealmsServiceException $$1) {
                        LOGGER.error("Couldn't delete world", (Throwable)$$1);
                    }
                    RealmsSubscriptionInfoScreen.this.minecraft.execute(() -> RealmsSubscriptionInfoScreen.this.minecraft.setScreen(RealmsSubscriptionInfoScreen.this.mainScreen));
                }
            }.start();
        }
        this.minecraft.setScreen(this);
    }

    private void getSubscription(long $$0) {
        RealmsClient $$1 = RealmsClient.create();
        try {
            Subscription $$2 = $$1.subscriptionFor($$0);
            this.daysLeft = this.daysLeftPresentation($$2.daysLeft);
            this.startDate = RealmsSubscriptionInfoScreen.localPresentation($$2.startDate);
            this.type = $$2.type;
        }
        catch (RealmsServiceException $$3) {
            LOGGER.error("Couldn't get subscription");
            this.minecraft.setScreen(new RealmsGenericErrorScreen($$3, this.lastScreen));
        }
    }

    private static Component localPresentation(long $$0) {
        GregorianCalendar $$1 = new GregorianCalendar(TimeZone.getDefault());
        $$1.setTimeInMillis($$0);
        return Component.literal(DateFormat.getDateTimeInstance().format($$1.getTime()));
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if ($$0 == 256) {
            this.minecraft.setScreen(this.lastScreen);
            return true;
        }
        return super.keyPressed($$0, $$1, $$2);
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderBackground($$0);
        int $$4 = this.width / 2 - 100;
        RealmsSubscriptionInfoScreen.drawCenteredString($$0, this.font, SUBSCRIPTION_TITLE, this.width / 2, 17, 0xFFFFFF);
        this.font.draw($$0, SUBSCRIPTION_START_LABEL, (float)$$4, (float)RealmsSubscriptionInfoScreen.row(0), 0xA0A0A0);
        this.font.draw($$0, this.startDate, (float)$$4, (float)RealmsSubscriptionInfoScreen.row(1), 0xFFFFFF);
        if (this.type == Subscription.SubscriptionType.NORMAL) {
            this.font.draw($$0, TIME_LEFT_LABEL, (float)$$4, (float)RealmsSubscriptionInfoScreen.row(3), 0xA0A0A0);
        } else if (this.type == Subscription.SubscriptionType.RECURRING) {
            this.font.draw($$0, DAYS_LEFT_LABEL, (float)$$4, (float)RealmsSubscriptionInfoScreen.row(3), 0xA0A0A0);
        }
        this.font.draw($$0, this.daysLeft, (float)$$4, (float)RealmsSubscriptionInfoScreen.row(4), 0xFFFFFF);
        super.render($$0, $$1, $$2, $$3);
    }

    private Component daysLeftPresentation(int $$0) {
        if ($$0 < 0 && this.serverData.expired) {
            return SUBSCRIPTION_EXPIRED_TEXT;
        }
        if ($$0 <= 1) {
            return SUBSCRIPTION_LESS_THAN_A_DAY_TEXT;
        }
        int $$1 = $$0 / 30;
        int $$2 = $$0 % 30;
        MutableComponent $$3 = Component.empty();
        if ($$1 > 0) {
            $$3.append(Integer.toString((int)$$1)).append(CommonComponents.SPACE);
            if ($$1 == 1) {
                $$3.append(MONTH_SUFFIX);
            } else {
                $$3.append(MONTHS_SUFFIX);
            }
        }
        if ($$2 > 0) {
            if ($$1 > 0) {
                $$3.append(", ");
            }
            $$3.append(Integer.toString((int)$$2)).append(CommonComponents.SPACE);
            if ($$2 == 1) {
                $$3.append(DAY_SUFFIX);
            } else {
                $$3.append(DAYS_SUFFIX);
            }
        }
        return $$3;
    }
}