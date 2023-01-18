/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.logging.LogUtils
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.Thread
 *  java.util.Collection
 *  java.util.List
 *  java.util.Map$Entry
 *  java.util.stream.Collectors
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsWorldSlotButton;
import com.mojang.realmsclient.gui.screens.RealmsDownloadLatestWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsResetWorldScreen;
import com.mojang.realmsclient.util.RealmsTextureManager;
import com.mojang.realmsclient.util.task.OpenServerTask;
import com.mojang.realmsclient.util.task.SwitchSlotTask;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.Mth;
import org.slf4j.Logger;

public class RealmsBrokenWorldScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int DEFAULT_BUTTON_WIDTH = 80;
    private final Screen lastScreen;
    private final RealmsMainScreen mainScreen;
    @Nullable
    private RealmsServer serverData;
    private final long serverId;
    private final Component[] message = new Component[]{Component.translatable("mco.brokenworld.message.line1"), Component.translatable("mco.brokenworld.message.line2")};
    private int leftX;
    private int rightX;
    private final List<Integer> slotsThatHasBeenDownloaded = Lists.newArrayList();
    private int animTick;

    public RealmsBrokenWorldScreen(Screen $$0, RealmsMainScreen $$1, long $$2, boolean $$3) {
        super($$3 ? Component.translatable("mco.brokenworld.minigame.title") : Component.translatable("mco.brokenworld.title"));
        this.lastScreen = $$0;
        this.mainScreen = $$1;
        this.serverId = $$2;
    }

    @Override
    public void init() {
        this.leftX = this.width / 2 - 150;
        this.rightX = this.width / 2 + 190;
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, $$0 -> this.backButtonClicked()).bounds(this.rightX - 80 + 8, RealmsBrokenWorldScreen.row(13) - 5, 70, 20).build());
        if (this.serverData == null) {
            this.fetchServerData(this.serverId);
        } else {
            this.addButtons();
        }
    }

    @Override
    public Component getNarrationMessage() {
        return ComponentUtils.formatList((Collection<? extends Component>)((Collection)Stream.concat((Stream)Stream.of((Object)this.title), (Stream)Stream.of((Object[])this.message)).collect(Collectors.toList())), CommonComponents.SPACE);
    }

    private void addButtons() {
        for (Map.Entry $$0 : this.serverData.slots.entrySet()) {
            Button $$4;
            boolean $$2;
            int $$13 = (Integer)$$0.getKey();
            boolean bl = $$2 = $$13 != this.serverData.activeSlot || this.serverData.worldType == RealmsServer.WorldType.MINIGAME;
            if ($$2) {
                Button $$3 = Button.builder(Component.translatable("mco.brokenworld.play"), $$1 -> {
                    if (((RealmsWorldOptions)this.serverData.slots.get((Object)Integer.valueOf((int)$$0))).empty) {
                        RealmsResetWorldScreen $$2 = new RealmsResetWorldScreen(this, this.serverData, Component.translatable("mco.configure.world.switch.slot"), Component.translatable("mco.configure.world.switch.slot.subtitle"), 0xA0A0A0, CommonComponents.GUI_CANCEL, this::doSwitchOrReset, () -> {
                            this.minecraft.setScreen(this);
                            this.doSwitchOrReset();
                        });
                        $$2.setSlot($$13);
                        $$2.setResetTitle(Component.translatable("mco.create.world.reset.title"));
                        this.minecraft.setScreen($$2);
                    } else {
                        this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, new SwitchSlotTask(this.serverData.id, $$13, this::doSwitchOrReset)));
                    }
                }).bounds(this.getFramePositionX($$13), RealmsBrokenWorldScreen.row(8), 80, 20).build();
            } else {
                $$4 = Button.builder(Component.translatable("mco.brokenworld.download"), $$12 -> {
                    MutableComponent $$2 = Component.translatable("mco.configure.world.restore.download.question.line1");
                    MutableComponent $$3 = Component.translatable("mco.configure.world.restore.download.question.line2");
                    this.minecraft.setScreen(new RealmsLongConfirmationScreen($$1 -> {
                        if ($$1) {
                            this.downloadWorld($$13);
                        } else {
                            this.minecraft.setScreen(this);
                        }
                    }, RealmsLongConfirmationScreen.Type.Info, $$2, $$3, true));
                }).bounds(this.getFramePositionX($$13), RealmsBrokenWorldScreen.row(8), 80, 20).build();
            }
            if (this.slotsThatHasBeenDownloaded.contains((Object)$$13)) {
                $$4.active = false;
                $$4.setMessage(Component.translatable("mco.brokenworld.downloaded"));
            }
            this.addRenderableWidget($$4);
            this.addRenderableWidget(Button.builder(Component.translatable("mco.brokenworld.reset"), $$1 -> {
                RealmsResetWorldScreen $$2 = new RealmsResetWorldScreen(this, this.serverData, this::doSwitchOrReset, () -> {
                    this.minecraft.setScreen(this);
                    this.doSwitchOrReset();
                });
                if ($$13 != this.serverData.activeSlot || this.serverData.worldType == RealmsServer.WorldType.MINIGAME) {
                    $$2.setSlot($$13);
                }
                this.minecraft.setScreen($$2);
            }).bounds(this.getFramePositionX($$13), RealmsBrokenWorldScreen.row(10), 80, 20).build());
        }
    }

    @Override
    public void tick() {
        ++this.animTick;
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderBackground($$0);
        super.render($$0, $$1, $$2, $$3);
        RealmsBrokenWorldScreen.drawCenteredString($$0, this.font, this.title, this.width / 2, 17, 0xFFFFFF);
        for (int $$4 = 0; $$4 < this.message.length; ++$$4) {
            RealmsBrokenWorldScreen.drawCenteredString($$0, this.font, this.message[$$4], this.width / 2, RealmsBrokenWorldScreen.row(-1) + 3 + $$4 * 12, 0xA0A0A0);
        }
        if (this.serverData == null) {
            return;
        }
        for (Map.Entry $$5 : this.serverData.slots.entrySet()) {
            if (((RealmsWorldOptions)$$5.getValue()).templateImage != null && ((RealmsWorldOptions)$$5.getValue()).templateId != -1L) {
                this.drawSlotFrame($$0, this.getFramePositionX((Integer)$$5.getKey()), RealmsBrokenWorldScreen.row(1) + 5, $$1, $$2, this.serverData.activeSlot == (Integer)$$5.getKey() && !this.isMinigame(), ((RealmsWorldOptions)$$5.getValue()).getSlotName((Integer)$$5.getKey()), (Integer)$$5.getKey(), ((RealmsWorldOptions)$$5.getValue()).templateId, ((RealmsWorldOptions)$$5.getValue()).templateImage, ((RealmsWorldOptions)$$5.getValue()).empty);
                continue;
            }
            this.drawSlotFrame($$0, this.getFramePositionX((Integer)$$5.getKey()), RealmsBrokenWorldScreen.row(1) + 5, $$1, $$2, this.serverData.activeSlot == (Integer)$$5.getKey() && !this.isMinigame(), ((RealmsWorldOptions)$$5.getValue()).getSlotName((Integer)$$5.getKey()), (Integer)$$5.getKey(), -1L, null, ((RealmsWorldOptions)$$5.getValue()).empty);
        }
    }

    private int getFramePositionX(int $$0) {
        return this.leftX + ($$0 - 1) * 110;
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if ($$0 == 256) {
            this.backButtonClicked();
            return true;
        }
        return super.keyPressed($$0, $$1, $$2);
    }

    private void backButtonClicked() {
        this.minecraft.setScreen(this.lastScreen);
    }

    private void fetchServerData(long $$0) {
        new Thread(() -> {
            RealmsClient $$1 = RealmsClient.create();
            try {
                this.serverData = $$1.getOwnWorld($$0);
                this.addButtons();
            }
            catch (RealmsServiceException $$2) {
                LOGGER.error("Couldn't get own world");
                this.minecraft.setScreen(new RealmsGenericErrorScreen(Component.nullToEmpty($$2.getMessage()), this.lastScreen));
            }
        }).start();
    }

    public void doSwitchOrReset() {
        new Thread(() -> {
            RealmsClient $$0 = RealmsClient.create();
            if (this.serverData.state == RealmsServer.State.CLOSED) {
                this.minecraft.execute(() -> this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this, new OpenServerTask(this.serverData, this, this.mainScreen, true, this.minecraft))));
            } else {
                try {
                    RealmsServer $$1 = $$0.getOwnWorld(this.serverId);
                    this.minecraft.execute(() -> this.mainScreen.newScreen().play($$1, this));
                }
                catch (RealmsServiceException $$2) {
                    LOGGER.error("Couldn't get own world");
                    this.minecraft.execute(() -> this.minecraft.setScreen(this.lastScreen));
                }
            }
        }).start();
    }

    private void downloadWorld(int $$0) {
        RealmsClient $$12 = RealmsClient.create();
        try {
            WorldDownload $$2 = $$12.requestDownloadInfo(this.serverData.id, $$0);
            RealmsDownloadLatestWorldScreen $$3 = new RealmsDownloadLatestWorldScreen(this, $$2, this.serverData.getWorldName($$0), $$1 -> {
                if ($$1) {
                    this.slotsThatHasBeenDownloaded.add((Object)$$0);
                    this.clearWidgets();
                    this.addButtons();
                } else {
                    this.minecraft.setScreen(this);
                }
            });
            this.minecraft.setScreen($$3);
        }
        catch (RealmsServiceException $$4) {
            LOGGER.error("Couldn't download world data");
            this.minecraft.setScreen(new RealmsGenericErrorScreen($$4, (Screen)this));
        }
    }

    private boolean isMinigame() {
        return this.serverData != null && this.serverData.worldType == RealmsServer.WorldType.MINIGAME;
    }

    private void drawSlotFrame(PoseStack $$0, int $$1, int $$2, int $$3, int $$4, boolean $$5, String $$6, int $$7, long $$8, @Nullable String $$9, boolean $$10) {
        if ($$10) {
            RenderSystem.setShaderTexture(0, RealmsWorldSlotButton.EMPTY_SLOT_LOCATION);
        } else if ($$9 != null && $$8 != -1L) {
            RealmsTextureManager.bindWorldTemplate(String.valueOf((long)$$8), $$9);
        } else if ($$7 == 1) {
            RenderSystem.setShaderTexture(0, RealmsWorldSlotButton.DEFAULT_WORLD_SLOT_1);
        } else if ($$7 == 2) {
            RenderSystem.setShaderTexture(0, RealmsWorldSlotButton.DEFAULT_WORLD_SLOT_2);
        } else if ($$7 == 3) {
            RenderSystem.setShaderTexture(0, RealmsWorldSlotButton.DEFAULT_WORLD_SLOT_3);
        } else {
            RealmsTextureManager.bindWorldTemplate(String.valueOf((int)this.serverData.minigameId), this.serverData.minigameImage);
        }
        if (!$$5) {
            RenderSystem.setShaderColor(0.56f, 0.56f, 0.56f, 1.0f);
        } else if ($$5) {
            float $$11 = 0.9f + 0.1f * Mth.cos((float)this.animTick * 0.2f);
            RenderSystem.setShaderColor($$11, $$11, $$11, 1.0f);
        }
        GuiComponent.blit($$0, $$1 + 3, $$2 + 3, 0.0f, 0.0f, 74, 74, 74, 74);
        RenderSystem.setShaderTexture(0, RealmsWorldSlotButton.SLOT_FRAME_LOCATION);
        if ($$5) {
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        } else {
            RenderSystem.setShaderColor(0.56f, 0.56f, 0.56f, 1.0f);
        }
        GuiComponent.blit($$0, $$1, $$2, 0.0f, 0.0f, 80, 80, 80, 80);
        RealmsBrokenWorldScreen.drawCenteredString($$0, this.font, $$6, $$1 + 40, $$2 + 66, 0xFFFFFF);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }
}