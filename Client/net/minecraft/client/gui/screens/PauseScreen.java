/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Objects
 *  java.util.function.Consumer
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.realmsclient.RealmsMainScreen;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CenteredStringWidget;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.ShareToLanScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.achievement.StatsScreen;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.social.SocialInteractionsScreen;
import net.minecraft.network.chat.Component;

public class PauseScreen
extends Screen {
    private static final String URL_FEEDBACK_SNAPSHOT = "https://aka.ms/snapshotfeedback?ref=game";
    private static final String URL_FEEDBACK_RELEASE = "https://aka.ms/javafeedback?ref=game";
    private static final String URL_BUGS = "https://aka.ms/snapshotbugs?ref=game";
    private static final int COLUMNS = 2;
    private static final int MENU_PADDING_TOP = 50;
    private static final int BUTTON_PADDING = 4;
    private static final int BUTTON_WIDTH_FULL = 204;
    private static final int BUTTON_WIDTH_HALF = 98;
    private static final Component RETURN_TO_GAME = Component.translatable("menu.returnToGame");
    private static final Component ADVANCEMENTS = Component.translatable("gui.advancements");
    private static final Component STATS = Component.translatable("gui.stats");
    private static final Component SEND_FEEDBACK = Component.translatable("menu.sendFeedback");
    private static final Component REPORT_BUGS = Component.translatable("menu.reportBugs");
    private static final Component OPTIONS = Component.translatable("menu.options");
    private static final Component SHARE_TO_LAN = Component.translatable("menu.shareToLan");
    private static final Component PLAYER_REPORTING = Component.translatable("menu.playerReporting");
    private static final Component RETURN_TO_MENU = Component.translatable("menu.returnToMenu");
    private static final Component DISCONNECT = Component.translatable("menu.disconnect");
    private static final Component SAVING_LEVEL = Component.translatable("menu.savingLevel");
    private static final Component GAME = Component.translatable("menu.game");
    private static final Component PAUSED = Component.translatable("menu.paused");
    private final boolean showPauseMenu;
    @Nullable
    private Button disconnectButton;

    public PauseScreen(boolean $$0) {
        super($$0 ? GAME : PAUSED);
        this.showPauseMenu = $$0;
    }

    @Override
    protected void init() {
        if (this.showPauseMenu) {
            this.createPauseMenu();
        }
        int n = this.showPauseMenu ? 40 : 10;
        Objects.requireNonNull((Object)this.font);
        this.addRenderableWidget(new CenteredStringWidget(0, n, this.width, 9, this.title, this.font));
    }

    private void createPauseMenu() {
        GridLayout $$02 = new GridLayout();
        $$02.defaultCellSetting().padding(4, 4, 4, 0);
        GridLayout.RowHelper $$1 = $$02.createRowHelper(2);
        $$1.addChild(Button.builder(RETURN_TO_GAME, $$0 -> {
            this.minecraft.setScreen(null);
            this.minecraft.mouseHandler.grabMouse();
        }).width(204).build(), 2, $$02.newCellSettings().paddingTop(50));
        $$1.addChild(this.openScreenButton(ADVANCEMENTS, (Supplier<Screen>)((Supplier)() -> new AdvancementsScreen(this.minecraft.player.connection.getAdvancements()))));
        $$1.addChild(this.openScreenButton(STATS, (Supplier<Screen>)((Supplier)() -> new StatsScreen(this, this.minecraft.player.getStats()))));
        $$1.addChild(this.openLinkButton(SEND_FEEDBACK, SharedConstants.getCurrentVersion().isStable() ? URL_FEEDBACK_RELEASE : URL_FEEDBACK_SNAPSHOT));
        $$1.addChild(this.openLinkButton((Component)PauseScreen.REPORT_BUGS, (String)URL_BUGS)).active = !SharedConstants.getCurrentVersion().getDataVersion().isSideSeries();
        $$1.addChild(this.openScreenButton(OPTIONS, (Supplier<Screen>)((Supplier)() -> new OptionsScreen(this, this.minecraft.options))));
        if (this.minecraft.hasSingleplayerServer() && !this.minecraft.getSingleplayerServer().isPublished()) {
            $$1.addChild(this.openScreenButton(SHARE_TO_LAN, (Supplier<Screen>)((Supplier)() -> new ShareToLanScreen(this))));
        } else {
            $$1.addChild(this.openScreenButton(PLAYER_REPORTING, (Supplier<Screen>)((Supplier)SocialInteractionsScreen::new)));
        }
        Component $$2 = this.minecraft.isLocalServer() ? RETURN_TO_MENU : DISCONNECT;
        this.disconnectButton = $$1.addChild(Button.builder($$2, $$0 -> {
            $$0.active = false;
            this.minecraft.getReportingContext().draftReportHandled(this.minecraft, this, this::onDisconnect, true);
        }).width(204).build(), 2);
        $$02.arrangeElements();
        FrameLayout.alignInRectangle($$02, 0, 0, this.width, this.height, 0.5f, 0.25f);
        $$02.visitWidgets((Consumer<AbstractWidget>)((Consumer)this::addRenderableWidget));
    }

    private void onDisconnect() {
        boolean $$0 = this.minecraft.isLocalServer();
        boolean $$1 = this.minecraft.isConnectedToRealms();
        this.minecraft.level.disconnect();
        if ($$0) {
            this.minecraft.clearLevel(new GenericDirtMessageScreen(SAVING_LEVEL));
        } else {
            this.minecraft.clearLevel();
        }
        TitleScreen $$2 = new TitleScreen();
        if ($$0) {
            this.minecraft.setScreen($$2);
        } else if ($$1) {
            this.minecraft.setScreen(new RealmsMainScreen($$2));
        } else {
            this.minecraft.setScreen(new JoinMultiplayerScreen($$2));
        }
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        if (this.showPauseMenu) {
            this.renderBackground($$0);
        }
        super.render($$0, $$1, $$2, $$3);
        if (this.showPauseMenu && this.minecraft != null && this.minecraft.getReportingContext().hasDraftReport() && this.disconnectButton != null) {
            RenderSystem.setShaderTexture(0, AbstractWidget.WIDGETS_LOCATION);
            this.blit($$0, this.disconnectButton.getX() + this.disconnectButton.getWidth() - 17, this.disconnectButton.getY() + 3, 182, 24, 15, 15);
        }
    }

    private Button openScreenButton(Component $$0, Supplier<Screen> $$12) {
        return Button.builder($$0, $$1 -> this.minecraft.setScreen((Screen)$$12.get())).width(98).build();
    }

    private Button openLinkButton(Component $$0, String $$1) {
        return this.openScreenButton($$0, (Supplier<Screen>)((Supplier)() -> new ConfirmLinkScreen($$1 -> {
            if ($$1) {
                Util.getPlatform().openUri($$1);
            }
            this.minecraft.setScreen(this);
        }, $$1, true)));
    }
}