/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Collection
 *  java.util.Locale
 *  java.util.Set
 *  java.util.UUID
 *  java.util.function.Consumer
 *  java.util.stream.Collectors
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.screens.social;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Collection;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.social.PlayerSocialManager;
import net.minecraft.client.gui.screens.social.SocialInteractionsPlayerList;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public class SocialInteractionsScreen
extends Screen {
    protected static final ResourceLocation SOCIAL_INTERACTIONS_LOCATION = new ResourceLocation("textures/gui/social_interactions.png");
    private static final Component TAB_ALL = Component.translatable("gui.socialInteractions.tab_all");
    private static final Component TAB_HIDDEN = Component.translatable("gui.socialInteractions.tab_hidden");
    private static final Component TAB_BLOCKED = Component.translatable("gui.socialInteractions.tab_blocked");
    private static final Component TAB_ALL_SELECTED = TAB_ALL.plainCopy().withStyle(ChatFormatting.UNDERLINE);
    private static final Component TAB_HIDDEN_SELECTED = TAB_HIDDEN.plainCopy().withStyle(ChatFormatting.UNDERLINE);
    private static final Component TAB_BLOCKED_SELECTED = TAB_BLOCKED.plainCopy().withStyle(ChatFormatting.UNDERLINE);
    private static final Component SEARCH_HINT = Component.translatable("gui.socialInteractions.search_hint").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY);
    static final Component EMPTY_SEARCH = Component.translatable("gui.socialInteractions.search_empty").withStyle(ChatFormatting.GRAY);
    private static final Component EMPTY_HIDDEN = Component.translatable("gui.socialInteractions.empty_hidden").withStyle(ChatFormatting.GRAY);
    private static final Component EMPTY_BLOCKED = Component.translatable("gui.socialInteractions.empty_blocked").withStyle(ChatFormatting.GRAY);
    private static final Component BLOCKING_HINT = Component.translatable("gui.socialInteractions.blocking_hint");
    private static final String BLOCK_LINK = "https://aka.ms/javablocking";
    private static final int BG_BORDER_SIZE = 8;
    private static final int BG_UNITS = 16;
    private static final int BG_WIDTH = 236;
    private static final int SEARCH_HEIGHT = 16;
    private static final int MARGIN_Y = 64;
    public static final int LIST_START = 88;
    public static final int SEARCH_START = 78;
    private static final int IMAGE_WIDTH = 238;
    private static final int BUTTON_HEIGHT = 20;
    private static final int ITEM_HEIGHT = 36;
    SocialInteractionsPlayerList socialInteractionsPlayerList;
    EditBox searchBox;
    private String lastSearch = "";
    private Page page = Page.ALL;
    private Button allButton;
    private Button hiddenButton;
    private Button blockedButton;
    private Button blockingHintButton;
    @Nullable
    private Component serverLabel;
    private int playerCount;
    private boolean initialized;

    public SocialInteractionsScreen() {
        super(Component.translatable("gui.socialInteractions.title"));
        this.updateServerLabel(Minecraft.getInstance());
    }

    private int windowHeight() {
        return Math.max((int)52, (int)(this.height - 128 - 16));
    }

    private int backgroundUnits() {
        return this.windowHeight() / 16;
    }

    private int listEnd() {
        return 80 + this.backgroundUnits() * 16 - 8;
    }

    private int marginX() {
        return (this.width - 238) / 2;
    }

    @Override
    public Component getNarrationMessage() {
        if (this.serverLabel != null) {
            return CommonComponents.joinForNarration(super.getNarrationMessage(), this.serverLabel);
        }
        return super.getNarrationMessage();
    }

    @Override
    public void tick() {
        super.tick();
        this.searchBox.tick();
    }

    @Override
    protected void init() {
        if (this.initialized) {
            this.socialInteractionsPlayerList.updateSize(this.width, this.height, 88, this.listEnd());
        } else {
            this.socialInteractionsPlayerList = new SocialInteractionsPlayerList(this, this.minecraft, this.width, this.height, 88, this.listEnd(), 36);
        }
        int $$03 = this.socialInteractionsPlayerList.getRowWidth() / 3;
        int $$1 = this.socialInteractionsPlayerList.getRowLeft();
        int $$2 = this.socialInteractionsPlayerList.getRowRight();
        int $$3 = this.font.width(BLOCKING_HINT) + 40;
        int $$4 = 64 + 16 * this.backgroundUnits();
        int $$5 = (this.width - $$3) / 2 + 3;
        this.allButton = this.addRenderableWidget(Button.builder(TAB_ALL, $$0 -> this.showPage(Page.ALL)).bounds($$1, 45, $$03, 20).build());
        this.hiddenButton = this.addRenderableWidget(Button.builder(TAB_HIDDEN, $$0 -> this.showPage(Page.HIDDEN)).bounds(($$1 + $$2 - $$03) / 2 + 1, 45, $$03, 20).build());
        this.blockedButton = this.addRenderableWidget(Button.builder(TAB_BLOCKED, $$0 -> this.showPage(Page.BLOCKED)).bounds($$2 - $$03 + 1, 45, $$03, 20).build());
        String $$6 = this.searchBox != null ? this.searchBox.getValue() : "";
        this.searchBox = new EditBox(this.font, this.marginX() + 28, 78, 196, 16, SEARCH_HINT){

            @Override
            protected MutableComponent createNarrationMessage() {
                if (!SocialInteractionsScreen.this.searchBox.getValue().isEmpty() && SocialInteractionsScreen.this.socialInteractionsPlayerList.isEmpty()) {
                    return super.createNarrationMessage().append(", ").append(EMPTY_SEARCH);
                }
                return super.createNarrationMessage();
            }
        };
        this.searchBox.setMaxLength(16);
        this.searchBox.setBordered(false);
        this.searchBox.setVisible(true);
        this.searchBox.setTextColor(0xFFFFFF);
        this.searchBox.setValue($$6);
        this.searchBox.setHint(SEARCH_HINT);
        this.searchBox.setResponder((Consumer<String>)((Consumer)this::checkSearchStringUpdate));
        this.addWidget(this.searchBox);
        this.addWidget(this.socialInteractionsPlayerList);
        this.blockingHintButton = this.addRenderableWidget(Button.builder(BLOCKING_HINT, $$02 -> this.minecraft.setScreen(new ConfirmLinkScreen($$0 -> {
            if ($$0) {
                Util.getPlatform().openUri(BLOCK_LINK);
            }
            this.minecraft.setScreen(this);
        }, BLOCK_LINK, true))).bounds($$5, $$4, $$3, 20).build());
        this.initialized = true;
        this.showPage(this.page);
    }

    private void showPage(Page $$0) {
        this.page = $$0;
        this.allButton.setMessage(TAB_ALL);
        this.hiddenButton.setMessage(TAB_HIDDEN);
        this.blockedButton.setMessage(TAB_BLOCKED);
        boolean $$1 = false;
        switch ($$0) {
            case ALL: {
                this.allButton.setMessage(TAB_ALL_SELECTED);
                Collection<UUID> $$2 = this.minecraft.player.connection.getOnlinePlayerIds();
                this.socialInteractionsPlayerList.updatePlayerList($$2, this.socialInteractionsPlayerList.getScrollAmount(), true);
                break;
            }
            case HIDDEN: {
                this.hiddenButton.setMessage(TAB_HIDDEN_SELECTED);
                Set<UUID> $$3 = this.minecraft.getPlayerSocialManager().getHiddenPlayers();
                $$1 = $$3.isEmpty();
                this.socialInteractionsPlayerList.updatePlayerList((Collection<UUID>)$$3, this.socialInteractionsPlayerList.getScrollAmount(), false);
                break;
            }
            case BLOCKED: {
                this.blockedButton.setMessage(TAB_BLOCKED_SELECTED);
                PlayerSocialManager $$4 = this.minecraft.getPlayerSocialManager();
                Set $$5 = (Set)this.minecraft.player.connection.getOnlinePlayerIds().stream().filter($$4::isBlocked).collect(Collectors.toSet());
                $$1 = $$5.isEmpty();
                this.socialInteractionsPlayerList.updatePlayerList((Collection<UUID>)$$5, this.socialInteractionsPlayerList.getScrollAmount(), false);
            }
        }
        GameNarrator $$6 = this.minecraft.getNarrator();
        if (!this.searchBox.getValue().isEmpty() && this.socialInteractionsPlayerList.isEmpty() && !this.searchBox.isFocused()) {
            $$6.sayNow(EMPTY_SEARCH);
        } else if ($$1) {
            if ($$0 == Page.HIDDEN) {
                $$6.sayNow(EMPTY_HIDDEN);
            } else if ($$0 == Page.BLOCKED) {
                $$6.sayNow(EMPTY_BLOCKED);
            }
        }
    }

    @Override
    public void renderBackground(PoseStack $$0) {
        int $$1 = this.marginX() + 3;
        super.renderBackground($$0);
        RenderSystem.setShaderTexture(0, SOCIAL_INTERACTIONS_LOCATION);
        this.blit($$0, $$1, 64, 1, 1, 236, 8);
        int $$2 = this.backgroundUnits();
        for (int $$3 = 0; $$3 < $$2; ++$$3) {
            this.blit($$0, $$1, 72 + 16 * $$3, 1, 10, 236, 16);
        }
        this.blit($$0, $$1, 72 + 16 * $$2, 1, 27, 236, 8);
        this.blit($$0, $$1 + 10, 76, 243, 1, 12, 12);
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.updateServerLabel(this.minecraft);
        this.renderBackground($$0);
        if (this.serverLabel != null) {
            SocialInteractionsScreen.drawString($$0, this.minecraft.font, this.serverLabel, this.marginX() + 8, 35, -1);
        }
        if (!this.socialInteractionsPlayerList.isEmpty()) {
            this.socialInteractionsPlayerList.render($$0, $$1, $$2, $$3);
        } else if (!this.searchBox.getValue().isEmpty()) {
            SocialInteractionsScreen.drawCenteredString($$0, this.minecraft.font, EMPTY_SEARCH, this.width / 2, (78 + this.listEnd()) / 2, -1);
        } else if (this.page == Page.HIDDEN) {
            SocialInteractionsScreen.drawCenteredString($$0, this.minecraft.font, EMPTY_HIDDEN, this.width / 2, (78 + this.listEnd()) / 2, -1);
        } else if (this.page == Page.BLOCKED) {
            SocialInteractionsScreen.drawCenteredString($$0, this.minecraft.font, EMPTY_BLOCKED, this.width / 2, (78 + this.listEnd()) / 2, -1);
        }
        this.searchBox.render($$0, $$1, $$2, $$3);
        this.blockingHintButton.visible = this.page == Page.BLOCKED;
        super.render($$0, $$1, $$2, $$3);
    }

    @Override
    public boolean mouseClicked(double $$0, double $$1, int $$2) {
        if (this.searchBox.isFocused()) {
            this.searchBox.mouseClicked($$0, $$1, $$2);
        }
        return super.mouseClicked($$0, $$1, $$2) || this.socialInteractionsPlayerList.mouseClicked($$0, $$1, $$2);
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if (!this.searchBox.isFocused() && this.minecraft.options.keySocialInteractions.matches($$0, $$1)) {
            this.minecraft.setScreen(null);
            return true;
        }
        return super.keyPressed($$0, $$1, $$2);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void checkSearchStringUpdate(String $$0) {
        if (!($$0 = $$0.toLowerCase(Locale.ROOT)).equals((Object)this.lastSearch)) {
            this.socialInteractionsPlayerList.setFilter($$0);
            this.lastSearch = $$0;
            this.showPage(this.page);
        }
    }

    private void updateServerLabel(Minecraft $$0) {
        int $$1 = $$0.getConnection().getOnlinePlayers().size();
        if (this.playerCount != $$1) {
            String $$2 = "";
            ServerData $$3 = $$0.getCurrentServer();
            if ($$0.isLocalServer()) {
                $$2 = $$0.getSingleplayerServer().getMotd();
            } else if ($$3 != null) {
                $$2 = $$3.name;
            }
            this.serverLabel = $$1 > 1 ? Component.translatable("gui.socialInteractions.server_label.multiple", $$2, $$1) : Component.translatable("gui.socialInteractions.server_label.single", $$2, $$1);
            this.playerCount = $$1;
        }
    }

    public void onAddPlayer(PlayerInfo $$0) {
        this.socialInteractionsPlayerList.addPlayer($$0, this.page);
    }

    public void onRemovePlayer(UUID $$0) {
        this.socialInteractionsPlayerList.removePlayer($$0);
    }

    public static enum Page {
        ALL,
        HIDDEN,
        BLOCKED;

    }
}