/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Thread
 *  javax.annotation.Nullable
 */
package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsNews;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsDataFetcher;
import com.mojang.realmsclient.gui.task.DataFetcher;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;

public class RealmsNotificationsScreen
extends RealmsScreen {
    private static final ResourceLocation INVITE_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/invite_icon.png");
    private static final ResourceLocation TRIAL_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/trial_icon.png");
    private static final ResourceLocation NEWS_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/news_notification_mainscreen.png");
    @Nullable
    private DataFetcher.Subscription realmsDataSubscription;
    private volatile int numberOfPendingInvites;
    static boolean checkedMcoAvailability;
    private static boolean trialAvailable;
    static boolean validClient;
    private static boolean hasUnreadNews;

    public RealmsNotificationsScreen() {
        super(GameNarrator.NO_TITLE);
    }

    @Override
    public void init() {
        this.checkIfMcoEnabled();
        if (this.realmsDataSubscription != null) {
            this.realmsDataSubscription.forceUpdate();
        }
    }

    @Override
    public void tick() {
        boolean $$0;
        boolean bl = $$0 = this.getRealmsNotificationsEnabled() && this.inTitleScreen() && validClient;
        if (this.realmsDataSubscription == null && $$0) {
            this.realmsDataSubscription = this.initDataFetcher(this.minecraft.realmsDataFetcher());
        } else if (this.realmsDataSubscription != null && !$$0) {
            this.realmsDataSubscription = null;
        }
        if (this.realmsDataSubscription != null) {
            this.realmsDataSubscription.tick();
        }
    }

    private DataFetcher.Subscription initDataFetcher(RealmsDataFetcher $$02) {
        DataFetcher.Subscription $$12 = $$02.dataFetcher.createSubscription();
        $$12.subscribe($$02.pendingInvitesTask, $$0 -> {
            this.numberOfPendingInvites = $$0;
        });
        $$12.subscribe($$02.trialAvailabilityTask, $$0 -> {
            trialAvailable = $$0;
        });
        $$12.subscribe($$02.newsTask, $$1 -> {
            $$0.newsManager.updateUnreadNews((RealmsNews)$$1);
            hasUnreadNews = $$0.newsManager.hasUnreadNews();
        });
        return $$12;
    }

    private boolean getRealmsNotificationsEnabled() {
        return this.minecraft.options.realmsNotifications().get();
    }

    private boolean inTitleScreen() {
        return this.minecraft.screen instanceof TitleScreen;
    }

    private void checkIfMcoEnabled() {
        if (!checkedMcoAvailability) {
            checkedMcoAvailability = true;
            new Thread("Realms Notification Availability checker #1"){

                public void run() {
                    RealmsClient $$0 = RealmsClient.create();
                    try {
                        RealmsClient.CompatibleVersionResponse $$1 = $$0.clientCompatible();
                        if ($$1 != RealmsClient.CompatibleVersionResponse.COMPATIBLE) {
                            return;
                        }
                    }
                    catch (RealmsServiceException $$2) {
                        if ($$2.httpResultCode != 401) {
                            checkedMcoAvailability = false;
                        }
                        return;
                    }
                    validClient = true;
                }
            }.start();
        }
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        if (validClient) {
            this.drawIcons($$0, $$1, $$2);
        }
        super.render($$0, $$1, $$2, $$3);
    }

    private void drawIcons(PoseStack $$0, int $$1, int $$2) {
        int $$3 = this.numberOfPendingInvites;
        int $$4 = 24;
        int $$5 = this.height / 4 + 48;
        int $$6 = this.width / 2 + 80;
        int $$7 = $$5 + 48 + 2;
        int $$8 = 0;
        if (hasUnreadNews) {
            RenderSystem.setShaderTexture(0, NEWS_ICON_LOCATION);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            $$0.pushPose();
            $$0.scale(0.4f, 0.4f, 0.4f);
            GuiComponent.blit($$0, (int)((double)($$6 + 2 - $$8) * 2.5), (int)((double)$$7 * 2.5), 0.0f, 0.0f, 40, 40, 40, 40);
            $$0.popPose();
            $$8 += 14;
        }
        if ($$3 != 0) {
            RenderSystem.setShaderTexture(0, INVITE_ICON_LOCATION);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            GuiComponent.blit($$0, $$6 - $$8, $$7 - 6, 0.0f, 0.0f, 15, 25, 31, 25);
            $$8 += 16;
        }
        if (trialAvailable) {
            RenderSystem.setShaderTexture(0, TRIAL_ICON_LOCATION);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            int $$9 = 0;
            if ((Util.getMillis() / 800L & 1L) == 1L) {
                $$9 = 8;
            }
            GuiComponent.blit($$0, $$6 + 4 - $$8, $$7 + 4, 0.0f, $$9, 8, 8, 8, 16);
        }
    }
}