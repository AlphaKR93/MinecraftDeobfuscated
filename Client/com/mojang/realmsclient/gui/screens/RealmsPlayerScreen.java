/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.Ops;
import com.mojang.realmsclient.dto.PlayerInfo;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsConfirmScreen;
import com.mojang.realmsclient.gui.screens.RealmsInviteScreen;
import com.mojang.realmsclient.util.RealmsTextureManager;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class RealmsPlayerScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceLocation OP_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/op_icon.png");
    private static final ResourceLocation USER_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/user_icon.png");
    private static final ResourceLocation CROSS_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/cross_player_icon.png");
    private static final ResourceLocation OPTIONS_BACKGROUND = new ResourceLocation("minecraft", "textures/gui/options_background.png");
    private static final Component NORMAL_USER_TOOLTIP = Component.translatable("mco.configure.world.invites.normal.tooltip");
    private static final Component OP_TOOLTIP = Component.translatable("mco.configure.world.invites.ops.tooltip");
    private static final Component REMOVE_ENTRY_TOOLTIP = Component.translatable("mco.configure.world.invites.remove.tooltip");
    private static final Component INVITED_LABEL = Component.translatable("mco.configure.world.invited");
    @Nullable
    private Component toolTip;
    private final RealmsConfigureWorldScreen lastScreen;
    final RealmsServer serverData;
    private InvitedObjectSelectionList invitedObjectSelectionList;
    int column1X;
    int columnWidth;
    private int column2X;
    private Button removeButton;
    private Button opdeopButton;
    private int selectedInvitedIndex = -1;
    private String selectedInvited;
    int player = -1;
    private boolean stateChanged;
    UserAction hoveredUserAction = UserAction.NONE;

    public RealmsPlayerScreen(RealmsConfigureWorldScreen $$0, RealmsServer $$1) {
        super(Component.translatable("mco.configure.world.players.title"));
        this.lastScreen = $$0;
        this.serverData = $$1;
    }

    @Override
    public void init() {
        this.column1X = this.width / 2 - 160;
        this.columnWidth = 150;
        this.column2X = this.width / 2 + 12;
        this.invitedObjectSelectionList = new InvitedObjectSelectionList();
        this.invitedObjectSelectionList.setLeftPos(this.column1X);
        this.addWidget(this.invitedObjectSelectionList);
        for (PlayerInfo $$02 : this.serverData.players) {
            this.invitedObjectSelectionList.addEntry($$02);
        }
        this.addRenderableWidget(Button.builder(Component.translatable("mco.configure.world.buttons.invite"), $$0 -> this.minecraft.setScreen(new RealmsInviteScreen(this.lastScreen, this, this.serverData))).bounds(this.column2X, RealmsPlayerScreen.row(1), this.columnWidth + 10, 20).build());
        this.removeButton = this.addRenderableWidget(Button.builder(Component.translatable("mco.configure.world.invites.remove.tooltip"), $$0 -> this.uninvite(this.player)).bounds(this.column2X, RealmsPlayerScreen.row(7), this.columnWidth + 10, 20).build());
        this.opdeopButton = this.addRenderableWidget(Button.builder(Component.translatable("mco.configure.world.invites.ops.tooltip"), $$0 -> {
            if (((PlayerInfo)this.serverData.players.get(this.player)).isOperator()) {
                this.deop(this.player);
            } else {
                this.op(this.player);
            }
        }).bounds(this.column2X, RealmsPlayerScreen.row(9), this.columnWidth + 10, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, $$0 -> this.backButtonClicked()).bounds(this.column2X + this.columnWidth / 2 + 2, RealmsPlayerScreen.row(12), this.columnWidth / 2 + 10 - 2, 20).build());
        this.updateButtonStates();
    }

    void updateButtonStates() {
        this.removeButton.visible = this.shouldRemoveAndOpdeopButtonBeVisible(this.player);
        this.opdeopButton.visible = this.shouldRemoveAndOpdeopButtonBeVisible(this.player);
    }

    private boolean shouldRemoveAndOpdeopButtonBeVisible(int $$0) {
        return $$0 != -1;
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
            this.minecraft.setScreen(this.lastScreen.getNewScreen());
        } else {
            this.minecraft.setScreen(this.lastScreen);
        }
    }

    void op(int $$0) {
        this.updateButtonStates();
        RealmsClient $$1 = RealmsClient.create();
        String $$2 = ((PlayerInfo)this.serverData.players.get($$0)).getUuid();
        try {
            this.updateOps($$1.op(this.serverData.id, $$2));
        }
        catch (RealmsServiceException $$3) {
            LOGGER.error("Couldn't op the user");
        }
    }

    void deop(int $$0) {
        this.updateButtonStates();
        RealmsClient $$1 = RealmsClient.create();
        String $$2 = ((PlayerInfo)this.serverData.players.get($$0)).getUuid();
        try {
            this.updateOps($$1.deop(this.serverData.id, $$2));
        }
        catch (RealmsServiceException $$3) {
            LOGGER.error("Couldn't deop the user");
        }
    }

    private void updateOps(Ops $$0) {
        for (PlayerInfo $$1 : this.serverData.players) {
            $$1.setOperator($$0.ops.contains((Object)$$1.getName()));
        }
    }

    void uninvite(int $$02) {
        this.updateButtonStates();
        if ($$02 >= 0 && $$02 < this.serverData.players.size()) {
            PlayerInfo $$1 = (PlayerInfo)this.serverData.players.get($$02);
            this.selectedInvited = $$1.getUuid();
            this.selectedInvitedIndex = $$02;
            RealmsConfirmScreen $$2 = new RealmsConfirmScreen($$0 -> {
                if ($$0) {
                    RealmsClient $$1 = RealmsClient.create();
                    try {
                        $$1.uninvite(this.serverData.id, this.selectedInvited);
                    }
                    catch (RealmsServiceException $$2) {
                        LOGGER.error("Couldn't uninvite user");
                    }
                    this.deleteFromInvitedList(this.selectedInvitedIndex);
                    this.player = -1;
                    this.updateButtonStates();
                }
                this.stateChanged = true;
                this.minecraft.setScreen(this);
            }, Component.literal("Question"), Component.translatable("mco.configure.world.uninvite.question").append(" '").append($$1.getName()).append("' ?"));
            this.minecraft.setScreen($$2);
        }
    }

    private void deleteFromInvitedList(int $$0) {
        this.serverData.players.remove($$0);
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.toolTip = null;
        this.hoveredUserAction = UserAction.NONE;
        this.renderBackground($$0);
        if (this.invitedObjectSelectionList != null) {
            this.invitedObjectSelectionList.render($$0, $$1, $$2, $$3);
        }
        RealmsPlayerScreen.drawCenteredString($$0, this.font, this.title, this.width / 2, 17, 0xFFFFFF);
        int $$4 = RealmsPlayerScreen.row(12) + 20;
        RenderSystem.setShaderTexture(0, OPTIONS_BACKGROUND);
        RenderSystem.setShaderColor(0.25f, 0.25f, 0.25f, 1.0f);
        RealmsPlayerScreen.blit($$0, 0, $$4, 0.0f, 0.0f, this.width, this.height - $$4, 32, 32);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        if (this.serverData != null && this.serverData.players != null) {
            this.font.draw($$0, Component.empty().append(INVITED_LABEL).append(" (").append(Integer.toString((int)this.serverData.players.size())).append(")"), (float)this.column1X, (float)RealmsPlayerScreen.row(0), 0xA0A0A0);
        } else {
            this.font.draw($$0, INVITED_LABEL, (float)this.column1X, (float)RealmsPlayerScreen.row(0), 0xA0A0A0);
        }
        super.render($$0, $$1, $$2, $$3);
        if (this.serverData == null) {
            return;
        }
        this.renderMousehoverTooltip($$0, this.toolTip, $$1, $$2);
    }

    protected void renderMousehoverTooltip(PoseStack $$0, @Nullable Component $$1, int $$2, int $$3) {
        if ($$1 == null) {
            return;
        }
        int $$4 = $$2 + 12;
        int $$5 = $$3 - 12;
        int $$6 = this.font.width($$1);
        this.fillGradient($$0, $$4 - 3, $$5 - 3, $$4 + $$6 + 3, $$5 + 8 + 3, -1073741824, -1073741824);
        this.font.drawShadow($$0, $$1, (float)$$4, (float)$$5, 0xFFFFFF);
    }

    void drawRemoveIcon(PoseStack $$0, int $$1, int $$2, int $$3, int $$4) {
        boolean $$5 = $$3 >= $$1 && $$3 <= $$1 + 9 && $$4 >= $$2 && $$4 <= $$2 + 9 && $$4 < RealmsPlayerScreen.row(12) + 20 && $$4 > RealmsPlayerScreen.row(1);
        RenderSystem.setShaderTexture(0, CROSS_ICON_LOCATION);
        float $$6 = $$5 ? 7.0f : 0.0f;
        GuiComponent.blit($$0, $$1, $$2, 0.0f, $$6, 8, 7, 8, 14);
        if ($$5) {
            this.toolTip = REMOVE_ENTRY_TOOLTIP;
            this.hoveredUserAction = UserAction.REMOVE;
        }
    }

    void drawOpped(PoseStack $$0, int $$1, int $$2, int $$3, int $$4) {
        boolean $$5 = $$3 >= $$1 && $$3 <= $$1 + 9 && $$4 >= $$2 && $$4 <= $$2 + 9 && $$4 < RealmsPlayerScreen.row(12) + 20 && $$4 > RealmsPlayerScreen.row(1);
        RenderSystem.setShaderTexture(0, OP_ICON_LOCATION);
        float $$6 = $$5 ? 8.0f : 0.0f;
        GuiComponent.blit($$0, $$1, $$2, 0.0f, $$6, 8, 8, 8, 16);
        if ($$5) {
            this.toolTip = OP_TOOLTIP;
            this.hoveredUserAction = UserAction.TOGGLE_OP;
        }
    }

    void drawNormal(PoseStack $$0, int $$1, int $$2, int $$3, int $$4) {
        boolean $$5 = $$3 >= $$1 && $$3 <= $$1 + 9 && $$4 >= $$2 && $$4 <= $$2 + 9 && $$4 < RealmsPlayerScreen.row(12) + 20 && $$4 > RealmsPlayerScreen.row(1);
        RenderSystem.setShaderTexture(0, USER_ICON_LOCATION);
        float $$6 = $$5 ? 8.0f : 0.0f;
        GuiComponent.blit($$0, $$1, $$2, 0.0f, $$6, 8, 8, 8, 16);
        if ($$5) {
            this.toolTip = NORMAL_USER_TOOLTIP;
            this.hoveredUserAction = UserAction.TOGGLE_OP;
        }
    }

    static enum UserAction {
        TOGGLE_OP,
        REMOVE,
        NONE;

    }

    class InvitedObjectSelectionList
    extends RealmsObjectSelectionList<Entry> {
        public InvitedObjectSelectionList() {
            super(RealmsPlayerScreen.this.columnWidth + 10, RealmsPlayerScreen.row(12) + 20, RealmsPlayerScreen.row(1), RealmsPlayerScreen.row(12) + 20, 13);
        }

        public void addEntry(PlayerInfo $$0) {
            this.addEntry(new Entry($$0));
        }

        @Override
        public int getRowWidth() {
            return (int)((double)this.width * 1.0);
        }

        @Override
        public boolean mouseClicked(double $$0, double $$1, int $$2) {
            if ($$2 == 0 && $$0 < (double)this.getScrollbarPosition() && $$1 >= (double)this.y0 && $$1 <= (double)this.y1) {
                int $$3 = RealmsPlayerScreen.this.column1X;
                int $$4 = RealmsPlayerScreen.this.column1X + RealmsPlayerScreen.this.columnWidth;
                int $$5 = (int)Math.floor((double)($$1 - (double)this.y0)) - this.headerHeight + (int)this.getScrollAmount() - 4;
                int $$6 = $$5 / this.itemHeight;
                if ($$0 >= (double)$$3 && $$0 <= (double)$$4 && $$6 >= 0 && $$5 >= 0 && $$6 < this.getItemCount()) {
                    this.selectItem($$6);
                    this.itemClicked($$5, $$6, $$0, $$1, this.width);
                }
                return true;
            }
            return super.mouseClicked($$0, $$1, $$2);
        }

        @Override
        public void itemClicked(int $$0, int $$1, double $$2, double $$3, int $$4) {
            if ($$1 < 0 || $$1 > RealmsPlayerScreen.this.serverData.players.size() || RealmsPlayerScreen.this.hoveredUserAction == UserAction.NONE) {
                return;
            }
            if (RealmsPlayerScreen.this.hoveredUserAction == UserAction.TOGGLE_OP) {
                if (((PlayerInfo)RealmsPlayerScreen.this.serverData.players.get($$1)).isOperator()) {
                    RealmsPlayerScreen.this.deop($$1);
                } else {
                    RealmsPlayerScreen.this.op($$1);
                }
            } else if (RealmsPlayerScreen.this.hoveredUserAction == UserAction.REMOVE) {
                RealmsPlayerScreen.this.uninvite($$1);
            }
        }

        @Override
        public void selectItem(int $$0) {
            super.selectItem($$0);
            this.selectInviteListItem($$0);
        }

        public void selectInviteListItem(int $$0) {
            RealmsPlayerScreen.this.player = $$0;
            RealmsPlayerScreen.this.updateButtonStates();
        }

        @Override
        public void setSelected(@Nullable Entry $$0) {
            super.setSelected($$0);
            RealmsPlayerScreen.this.player = this.children().indexOf((Object)$$0);
            RealmsPlayerScreen.this.updateButtonStates();
        }

        @Override
        public void renderBackground(PoseStack $$0) {
            RealmsPlayerScreen.this.renderBackground($$0);
        }

        @Override
        public int getScrollbarPosition() {
            return RealmsPlayerScreen.this.column1X + this.width - 5;
        }

        @Override
        public int getMaxPosition() {
            return this.getItemCount() * 13;
        }
    }

    class Entry
    extends ObjectSelectionList.Entry<Entry> {
        private final PlayerInfo playerInfo;

        public Entry(PlayerInfo $$0) {
            this.playerInfo = $$0;
        }

        @Override
        public void render(PoseStack $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
            this.renderInvitedItem($$0, this.playerInfo, $$3, $$2, $$6, $$7);
        }

        private void renderInvitedItem(PoseStack $$0, PlayerInfo $$1, int $$2, int $$3, int $$4, int $$5) {
            int $$8;
            if (!$$1.getAccepted()) {
                int $$6 = 0xA0A0A0;
            } else if ($$1.getOnline()) {
                int $$7 = 0x7FFF7F;
            } else {
                $$8 = 0xFFFFFF;
            }
            RealmsPlayerScreen.this.font.draw($$0, $$1.getName(), (float)(RealmsPlayerScreen.this.column1X + 3 + 12), (float)($$3 + 1), $$8);
            if ($$1.isOperator()) {
                RealmsPlayerScreen.this.drawOpped($$0, RealmsPlayerScreen.this.column1X + RealmsPlayerScreen.this.columnWidth - 10, $$3 + 1, $$4, $$5);
            } else {
                RealmsPlayerScreen.this.drawNormal($$0, RealmsPlayerScreen.this.column1X + RealmsPlayerScreen.this.columnWidth - 10, $$3 + 1, $$4, $$5);
            }
            RealmsPlayerScreen.this.drawRemoveIcon($$0, RealmsPlayerScreen.this.column1X + RealmsPlayerScreen.this.columnWidth - 22, $$3 + 2, $$4, $$5);
            RealmsTextureManager.withBoundFace($$1.getUuid(), () -> PlayerFaceRenderer.draw($$0, RealmsPlayerScreen.this.column1X + 2 + 2, $$3 + 1, 8));
        }

        @Override
        public Component getNarration() {
            return Component.translatable("narrator.select", this.playerInfo.getName());
        }
    }
}