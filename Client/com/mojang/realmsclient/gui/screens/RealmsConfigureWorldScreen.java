/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.logging.LogUtils
 *  java.lang.IllegalStateException
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.Thread
 *  java.util.List
 *  java.util.function.Consumer
 *  java.util.function.Supplier
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
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsWorldSlotButton;
import com.mojang.realmsclient.gui.screens.RealmsBackupScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsPlayerScreen;
import com.mojang.realmsclient.gui.screens.RealmsResetWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsSelectWorldTemplateScreen;
import com.mojang.realmsclient.gui.screens.RealmsSettingsScreen;
import com.mojang.realmsclient.gui.screens.RealmsSlotOptionsScreen;
import com.mojang.realmsclient.gui.screens.RealmsSubscriptionInfoScreen;
import com.mojang.realmsclient.util.task.CloseServerTask;
import com.mojang.realmsclient.util.task.OpenServerTask;
import com.mojang.realmsclient.util.task.SwitchMinigameTask;
import com.mojang.realmsclient.util.task.SwitchSlotTask;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class RealmsConfigureWorldScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceLocation ON_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/on_icon.png");
    private static final ResourceLocation OFF_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/off_icon.png");
    private static final ResourceLocation EXPIRED_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/expired_icon.png");
    private static final ResourceLocation EXPIRES_SOON_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/expires_soon_icon.png");
    private static final Component WORLD_LIST_TITLE = Component.translatable("mco.configure.worlds.title");
    private static final Component TITLE = Component.translatable("mco.configure.world.title");
    private static final Component MINIGAME_PREFIX = Component.translatable("mco.configure.current.minigame").append(": ");
    private static final Component SERVER_EXPIRED_TOOLTIP = Component.translatable("mco.selectServer.expired");
    private static final Component SERVER_EXPIRING_SOON_TOOLTIP = Component.translatable("mco.selectServer.expires.soon");
    private static final Component SERVER_EXPIRING_IN_DAY_TOOLTIP = Component.translatable("mco.selectServer.expires.day");
    private static final Component SERVER_OPEN_TOOLTIP = Component.translatable("mco.selectServer.open");
    private static final Component SERVER_CLOSED_TOOLTIP = Component.translatable("mco.selectServer.closed");
    private static final int DEFAULT_BUTTON_WIDTH = 80;
    private static final int DEFAULT_BUTTON_OFFSET = 5;
    @Nullable
    private Component toolTip;
    private final RealmsMainScreen lastScreen;
    @Nullable
    private RealmsServer serverData;
    private final long serverId;
    private int leftX;
    private int rightX;
    private Button playersButton;
    private Button settingsButton;
    private Button subscriptionButton;
    private Button optionsButton;
    private Button backupButton;
    private Button resetWorldButton;
    private Button switchMinigameButton;
    private boolean stateChanged;
    private int animTick;
    private int clicks;
    private final List<RealmsWorldSlotButton> slotButtonList = Lists.newArrayList();

    public RealmsConfigureWorldScreen(RealmsMainScreen $$0, long $$1) {
        super(TITLE);
        this.lastScreen = $$0;
        this.serverId = $$1;
    }

    @Override
    public void init() {
        if (this.serverData == null) {
            this.fetchServerData(this.serverId);
        }
        this.leftX = this.width / 2 - 187;
        this.rightX = this.width / 2 + 190;
        this.playersButton = this.addRenderableWidget(Button.builder(Component.translatable("mco.configure.world.buttons.players"), $$0 -> this.minecraft.setScreen(new RealmsPlayerScreen(this, this.serverData))).bounds(this.centerButton(0, 3), RealmsConfigureWorldScreen.row(0), 100, 20).build());
        this.settingsButton = this.addRenderableWidget(Button.builder(Component.translatable("mco.configure.world.buttons.settings"), $$0 -> this.minecraft.setScreen(new RealmsSettingsScreen(this, this.serverData.clone()))).bounds(this.centerButton(1, 3), RealmsConfigureWorldScreen.row(0), 100, 20).build());
        this.subscriptionButton = this.addRenderableWidget(Button.builder(Component.translatable("mco.configure.world.buttons.subscription"), $$0 -> this.minecraft.setScreen(new RealmsSubscriptionInfoScreen(this, this.serverData.clone(), this.lastScreen))).bounds(this.centerButton(2, 3), RealmsConfigureWorldScreen.row(0), 100, 20).build());
        this.slotButtonList.clear();
        for (int $$02 = 1; $$02 < 5; ++$$02) {
            this.slotButtonList.add((Object)this.addSlotButton($$02));
        }
        this.switchMinigameButton = this.addRenderableWidget(Button.builder(Component.translatable("mco.configure.world.buttons.switchminigame"), $$0 -> this.minecraft.setScreen(new RealmsSelectWorldTemplateScreen(Component.translatable("mco.template.title.minigame"), (Consumer<WorldTemplate>)((Consumer)this::templateSelectionCallback), RealmsServer.WorldType.MINIGAME))).bounds(this.leftButton(0), RealmsConfigureWorldScreen.row(13) - 5, 100, 20).build());
        this.optionsButton = this.addRenderableWidget(Button.builder(Component.translatable("mco.configure.world.buttons.options"), $$0 -> this.minecraft.setScreen(new RealmsSlotOptionsScreen(this, ((RealmsWorldOptions)this.serverData.slots.get((Object)this.serverData.activeSlot)).clone(), this.serverData.worldType, this.serverData.activeSlot))).bounds(this.leftButton(0), RealmsConfigureWorldScreen.row(13) - 5, 90, 20).build());
        this.backupButton = this.addRenderableWidget(Button.builder(Component.translatable("mco.configure.world.backup"), $$0 -> this.minecraft.setScreen(new RealmsBackupScreen(this, this.serverData.clone(), this.serverData.activeSlot))).bounds(this.leftButton(1), RealmsConfigureWorldScreen.row(13) - 5, 90, 20).build());
        this.resetWorldButton = this.addRenderableWidget(Button.builder(Component.translatable("mco.configure.world.buttons.resetworld"), $$0 -> this.minecraft.setScreen(new RealmsResetWorldScreen(this, this.serverData.clone(), () -> this.minecraft.execute(() -> this.minecraft.setScreen(this.getNewScreen())), () -> this.minecraft.setScreen(this.getNewScreen())))).bounds(this.leftButton(2), RealmsConfigureWorldScreen.row(13) - 5, 90, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, $$0 -> this.backButtonClicked()).bounds(this.rightX - 80 + 8, RealmsConfigureWorldScreen.row(13) - 5, 70, 20).build());
        this.backupButton.active = true;
        if (this.serverData == null) {
            this.hideMinigameButtons();
            this.hideRegularButtons();
            this.playersButton.active = false;
            this.settingsButton.active = false;
            this.subscriptionButton.active = false;
        } else {
            this.disableButtons();
            if (this.isMinigame()) {
                this.hideRegularButtons();
            } else {
                this.hideMinigameButtons();
            }
        }
    }

    private RealmsWorldSlotButton addSlotButton(int $$02) {
        int $$12 = this.frame($$02);
        int $$2 = RealmsConfigureWorldScreen.row(5) + 5;
        RealmsWorldSlotButton $$3 = new RealmsWorldSlotButton($$12, $$2, 80, 80, (Supplier<RealmsServer>)((Supplier)() -> this.serverData), (Consumer<Component>)((Consumer)$$0 -> {
            this.toolTip = $$0;
        }), $$02, $$1 -> {
            RealmsWorldSlotButton.State $$2 = ((RealmsWorldSlotButton)$$1).getState();
            if ($$2 != null) {
                switch ($$2.action) {
                    case NOTHING: {
                        break;
                    }
                    case JOIN: {
                        this.joinRealm(this.serverData);
                        break;
                    }
                    case SWITCH_SLOT: {
                        if ($$2.minigame) {
                            this.switchToMinigame();
                            break;
                        }
                        if ($$2.empty) {
                            this.switchToEmptySlot($$02, this.serverData);
                            break;
                        }
                        this.switchToFullSlot($$02, this.serverData);
                        break;
                    }
                    default: {
                        throw new IllegalStateException("Unknown action " + $$2.action);
                    }
                }
            }
        });
        return this.addRenderableWidget($$3);
    }

    private int leftButton(int $$0) {
        return this.leftX + $$0 * 95;
    }

    private int centerButton(int $$0, int $$1) {
        return this.width / 2 - ($$1 * 105 - 5) / 2 + $$0 * 105;
    }

    @Override
    public void tick() {
        super.tick();
        ++this.animTick;
        --this.clicks;
        if (this.clicks < 0) {
            this.clicks = 0;
        }
        this.slotButtonList.forEach(RealmsWorldSlotButton::tick);
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.toolTip = null;
        this.renderBackground($$0);
        RealmsConfigureWorldScreen.drawCenteredString($$0, this.font, WORLD_LIST_TITLE, this.width / 2, RealmsConfigureWorldScreen.row(4), 0xFFFFFF);
        super.render($$0, $$1, $$2, $$3);
        if (this.serverData == null) {
            RealmsConfigureWorldScreen.drawCenteredString($$0, this.font, this.title, this.width / 2, 17, 0xFFFFFF);
            return;
        }
        String $$4 = this.serverData.getName();
        int $$5 = this.font.width($$4);
        int $$6 = this.serverData.state == RealmsServer.State.CLOSED ? 0xA0A0A0 : 0x7FFF7F;
        int $$7 = this.font.width(this.title);
        RealmsConfigureWorldScreen.drawCenteredString($$0, this.font, this.title, this.width / 2, 12, 0xFFFFFF);
        RealmsConfigureWorldScreen.drawCenteredString($$0, this.font, $$4, this.width / 2, 24, $$6);
        int $$8 = Math.min((int)(this.centerButton(2, 3) + 80 - 11), (int)(this.width / 2 + $$5 / 2 + $$7 / 2 + 10));
        this.drawServerStatus($$0, $$8, 7, $$1, $$2);
        if (this.isMinigame()) {
            this.font.draw($$0, MINIGAME_PREFIX.copy().append(this.serverData.getMinigameName()), (float)(this.leftX + 80 + 20 + 10), (float)RealmsConfigureWorldScreen.row(13), 0xFFFFFF);
        }
        if (this.toolTip != null) {
            this.renderMousehoverTooltip($$0, this.toolTip, $$1, $$2);
        }
    }

    private int frame(int $$0) {
        return this.leftX + ($$0 - 1) * 98;
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
        if (this.stateChanged) {
            this.lastScreen.resetScreen();
        }
        this.minecraft.setScreen(this.lastScreen);
    }

    private void fetchServerData(long $$0) {
        new Thread(() -> {
            RealmsClient $$1 = RealmsClient.create();
            try {
                RealmsServer $$2 = $$1.getOwnWorld($$0);
                this.minecraft.execute(() -> {
                    this.serverData = $$2;
                    this.disableButtons();
                    if (this.isMinigame()) {
                        this.show(this.switchMinigameButton);
                    } else {
                        this.show(this.optionsButton);
                        this.show(this.backupButton);
                        this.show(this.resetWorldButton);
                    }
                });
            }
            catch (RealmsServiceException $$3) {
                LOGGER.error("Couldn't get own world");
                this.minecraft.execute(() -> this.minecraft.setScreen(new RealmsGenericErrorScreen(Component.nullToEmpty($$3.getMessage()), (Screen)this.lastScreen)));
            }
        }).start();
    }

    private void disableButtons() {
        this.playersButton.active = !this.serverData.expired;
        this.settingsButton.active = !this.serverData.expired;
        this.subscriptionButton.active = true;
        this.switchMinigameButton.active = !this.serverData.expired;
        this.optionsButton.active = !this.serverData.expired;
        this.resetWorldButton.active = !this.serverData.expired;
    }

    private void joinRealm(RealmsServer $$0) {
        if (this.serverData.state == RealmsServer.State.OPEN) {
            this.lastScreen.play($$0, new RealmsConfigureWorldScreen(this.lastScreen.newScreen(), this.serverId));
        } else {
            this.openTheWorld(true, new RealmsConfigureWorldScreen(this.lastScreen.newScreen(), this.serverId));
        }
    }

    private void switchToMinigame() {
        RealmsSelectWorldTemplateScreen $$0 = new RealmsSelectWorldTemplateScreen(Component.translatable("mco.template.title.minigame"), (Consumer<WorldTemplate>)((Consumer)this::templateSelectionCallback), RealmsServer.WorldType.MINIGAME);
        $$0.setWarning(Component.translatable("mco.minigame.world.info.line1"), Component.translatable("mco.minigame.world.info.line2"));
        this.minecraft.setScreen($$0);
    }

    private void switchToFullSlot(int $$0, RealmsServer $$1) {
        MutableComponent $$22 = Component.translatable("mco.configure.world.slot.switch.question.line1");
        MutableComponent $$3 = Component.translatable("mco.configure.world.slot.switch.question.line2");
        this.minecraft.setScreen(new RealmsLongConfirmationScreen($$2 -> {
            if ($$2) {
                this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, new SwitchSlotTask($$0.id, $$0, () -> this.minecraft.execute(() -> this.minecraft.setScreen(this.getNewScreen())))));
            } else {
                this.minecraft.setScreen(this);
            }
        }, RealmsLongConfirmationScreen.Type.Info, $$22, $$3, true));
    }

    private void switchToEmptySlot(int $$0, RealmsServer $$1) {
        MutableComponent $$22 = Component.translatable("mco.configure.world.slot.switch.question.line1");
        MutableComponent $$3 = Component.translatable("mco.configure.world.slot.switch.question.line2");
        this.minecraft.setScreen(new RealmsLongConfirmationScreen($$2 -> {
            if ($$2) {
                RealmsResetWorldScreen $$3 = new RealmsResetWorldScreen(this, $$1, Component.translatable("mco.configure.world.switch.slot"), Component.translatable("mco.configure.world.switch.slot.subtitle"), 0xA0A0A0, CommonComponents.GUI_CANCEL, () -> this.minecraft.execute(() -> this.minecraft.setScreen(this.getNewScreen())), () -> this.minecraft.setScreen(this.getNewScreen()));
                $$3.setSlot($$0);
                $$3.setResetTitle(Component.translatable("mco.create.world.reset.title"));
                this.minecraft.setScreen($$3);
            } else {
                this.minecraft.setScreen(this);
            }
        }, RealmsLongConfirmationScreen.Type.Info, $$22, $$3, true));
    }

    protected void renderMousehoverTooltip(PoseStack $$0, @Nullable Component $$1, int $$2, int $$3) {
        int $$4 = $$2 + 12;
        int $$5 = $$3 - 12;
        int $$6 = this.font.width($$1);
        if ($$4 + $$6 + 3 > this.rightX) {
            $$4 = $$4 - $$6 - 20;
        }
        this.fillGradient($$0, $$4 - 3, $$5 - 3, $$4 + $$6 + 3, $$5 + 8 + 3, -1073741824, -1073741824);
        this.font.drawShadow($$0, $$1, (float)$$4, (float)$$5, 0xFFFFFF);
    }

    private void drawServerStatus(PoseStack $$0, int $$1, int $$2, int $$3, int $$4) {
        if (this.serverData.expired) {
            this.drawExpired($$0, $$1, $$2, $$3, $$4);
        } else if (this.serverData.state == RealmsServer.State.CLOSED) {
            this.drawClose($$0, $$1, $$2, $$3, $$4);
        } else if (this.serverData.state == RealmsServer.State.OPEN) {
            if (this.serverData.daysLeft < 7) {
                this.drawExpiring($$0, $$1, $$2, $$3, $$4, this.serverData.daysLeft);
            } else {
                this.drawOpen($$0, $$1, $$2, $$3, $$4);
            }
        }
    }

    private void drawExpired(PoseStack $$0, int $$1, int $$2, int $$3, int $$4) {
        RenderSystem.setShaderTexture(0, EXPIRED_ICON_LOCATION);
        GuiComponent.blit($$0, $$1, $$2, 0.0f, 0.0f, 10, 28, 10, 28);
        if ($$3 >= $$1 && $$3 <= $$1 + 9 && $$4 >= $$2 && $$4 <= $$2 + 27) {
            this.toolTip = SERVER_EXPIRED_TOOLTIP;
        }
    }

    private void drawExpiring(PoseStack $$0, int $$1, int $$2, int $$3, int $$4, int $$5) {
        RenderSystem.setShaderTexture(0, EXPIRES_SOON_ICON_LOCATION);
        if (this.animTick % 20 < 10) {
            GuiComponent.blit($$0, $$1, $$2, 0.0f, 0.0f, 10, 28, 20, 28);
        } else {
            GuiComponent.blit($$0, $$1, $$2, 10.0f, 0.0f, 10, 28, 20, 28);
        }
        if ($$3 >= $$1 && $$3 <= $$1 + 9 && $$4 >= $$2 && $$4 <= $$2 + 27) {
            this.toolTip = $$5 <= 0 ? SERVER_EXPIRING_SOON_TOOLTIP : ($$5 == 1 ? SERVER_EXPIRING_IN_DAY_TOOLTIP : Component.translatable("mco.selectServer.expires.days", $$5));
        }
    }

    private void drawOpen(PoseStack $$0, int $$1, int $$2, int $$3, int $$4) {
        RenderSystem.setShaderTexture(0, ON_ICON_LOCATION);
        GuiComponent.blit($$0, $$1, $$2, 0.0f, 0.0f, 10, 28, 10, 28);
        if ($$3 >= $$1 && $$3 <= $$1 + 9 && $$4 >= $$2 && $$4 <= $$2 + 27) {
            this.toolTip = SERVER_OPEN_TOOLTIP;
        }
    }

    private void drawClose(PoseStack $$0, int $$1, int $$2, int $$3, int $$4) {
        RenderSystem.setShaderTexture(0, OFF_ICON_LOCATION);
        GuiComponent.blit($$0, $$1, $$2, 0.0f, 0.0f, 10, 28, 10, 28);
        if ($$3 >= $$1 && $$3 <= $$1 + 9 && $$4 >= $$2 && $$4 <= $$2 + 27) {
            this.toolTip = SERVER_CLOSED_TOOLTIP;
        }
    }

    private boolean isMinigame() {
        return this.serverData != null && this.serverData.worldType == RealmsServer.WorldType.MINIGAME;
    }

    private void hideRegularButtons() {
        this.hide(this.optionsButton);
        this.hide(this.backupButton);
        this.hide(this.resetWorldButton);
    }

    private void hide(Button $$0) {
        $$0.visible = false;
        this.removeWidget($$0);
    }

    private void show(Button $$0) {
        $$0.visible = true;
        this.addRenderableWidget($$0);
    }

    private void hideMinigameButtons() {
        this.hide(this.switchMinigameButton);
    }

    public void saveSlotSettings(RealmsWorldOptions $$0) {
        RealmsWorldOptions $$1 = (RealmsWorldOptions)this.serverData.slots.get((Object)this.serverData.activeSlot);
        $$0.templateId = $$1.templateId;
        $$0.templateImage = $$1.templateImage;
        RealmsClient $$2 = RealmsClient.create();
        try {
            $$2.updateSlot(this.serverData.id, this.serverData.activeSlot, $$0);
            this.serverData.slots.put((Object)this.serverData.activeSlot, (Object)$$0);
        }
        catch (RealmsServiceException $$3) {
            LOGGER.error("Couldn't save slot settings");
            this.minecraft.setScreen(new RealmsGenericErrorScreen($$3, (Screen)this));
            return;
        }
        this.minecraft.setScreen(this);
    }

    public void saveSettings(String $$0, String $$1) {
        String $$2 = $$1.trim().isEmpty() ? null : $$1;
        RealmsClient $$3 = RealmsClient.create();
        try {
            $$3.update(this.serverData.id, $$0, $$2);
            this.serverData.setName($$0);
            this.serverData.setDescription($$2);
        }
        catch (RealmsServiceException $$4) {
            LOGGER.error("Couldn't save settings");
            this.minecraft.setScreen(new RealmsGenericErrorScreen($$4, (Screen)this));
            return;
        }
        this.minecraft.setScreen(this);
    }

    public void openTheWorld(boolean $$0, Screen $$1) {
        this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen($$1, new OpenServerTask(this.serverData, this, this.lastScreen, $$0, this.minecraft)));
    }

    public void closeTheWorld(Screen $$0) {
        this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen($$0, new CloseServerTask(this.serverData, this)));
    }

    public void stateChanged() {
        this.stateChanged = true;
    }

    private void templateSelectionCallback(@Nullable WorldTemplate $$0) {
        if ($$0 != null && WorldTemplate.WorldTemplateType.MINIGAME == $$0.type) {
            this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, new SwitchMinigameTask(this.serverData.id, $$0, this.getNewScreen())));
        } else {
            this.minecraft.setScreen(this);
        }
    }

    public RealmsConfigureWorldScreen getNewScreen() {
        return new RealmsConfigureWorldScreen(this.lastScreen, this.serverId);
    }
}