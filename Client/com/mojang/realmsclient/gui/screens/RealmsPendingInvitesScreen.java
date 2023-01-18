/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Thread
 *  java.util.Arrays
 *  java.util.List
 *  java.util.stream.Collectors
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.PendingInvite;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RowButton;
import com.mojang.realmsclient.util.RealmsTextureManager;
import com.mojang.realmsclient.util.RealmsUtil;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class RealmsPendingInvitesScreen
extends RealmsScreen {
    static final Logger LOGGER = LogUtils.getLogger();
    static final ResourceLocation ACCEPT_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/accept_icon.png");
    static final ResourceLocation REJECT_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/reject_icon.png");
    private static final Component NO_PENDING_INVITES_TEXT = Component.translatable("mco.invites.nopending");
    static final Component ACCEPT_INVITE_TOOLTIP = Component.translatable("mco.invites.button.accept");
    static final Component REJECT_INVITE_TOOLTIP = Component.translatable("mco.invites.button.reject");
    private final Screen lastScreen;
    @Nullable
    Component toolTip;
    boolean loaded;
    PendingInvitationSelectionList pendingInvitationSelectionList;
    int selectedInvite = -1;
    private Button acceptButton;
    private Button rejectButton;

    public RealmsPendingInvitesScreen(Screen $$0) {
        super(Component.translatable("mco.invites.title"));
        this.lastScreen = $$0;
    }

    @Override
    public void init() {
        this.pendingInvitationSelectionList = new PendingInvitationSelectionList();
        new Thread("Realms-pending-invitations-fetcher"){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void run() {
                RealmsClient $$02 = RealmsClient.create();
                try {
                    List<PendingInvite> $$1 = $$02.pendingInvites().pendingInvites;
                    List $$2 = (List)$$1.stream().map($$0 -> new Entry((PendingInvite)$$0)).collect(Collectors.toList());
                    RealmsPendingInvitesScreen.this.minecraft.execute(() -> RealmsPendingInvitesScreen.this.pendingInvitationSelectionList.replaceEntries($$2));
                }
                catch (RealmsServiceException $$3) {
                    LOGGER.error("Couldn't list invites");
                }
                finally {
                    RealmsPendingInvitesScreen.this.loaded = true;
                }
            }
        }.start();
        this.addWidget(this.pendingInvitationSelectionList);
        this.acceptButton = this.addRenderableWidget(Button.builder(Component.translatable("mco.invites.button.accept"), $$0 -> {
            this.accept(this.selectedInvite);
            this.selectedInvite = -1;
            this.updateButtonStates();
        }).bounds(this.width / 2 - 174, this.height - 32, 100, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, $$0 -> this.minecraft.setScreen(new RealmsMainScreen(this.lastScreen))).bounds(this.width / 2 - 50, this.height - 32, 100, 20).build());
        this.rejectButton = this.addRenderableWidget(Button.builder(Component.translatable("mco.invites.button.reject"), $$0 -> {
            this.reject(this.selectedInvite);
            this.selectedInvite = -1;
            this.updateButtonStates();
        }).bounds(this.width / 2 + 74, this.height - 32, 100, 20).build());
        this.updateButtonStates();
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if ($$0 == 256) {
            this.minecraft.setScreen(new RealmsMainScreen(this.lastScreen));
            return true;
        }
        return super.keyPressed($$0, $$1, $$2);
    }

    void updateList(int $$0) {
        this.pendingInvitationSelectionList.removeAtIndex($$0);
    }

    void reject(final int $$0) {
        if ($$0 < this.pendingInvitationSelectionList.getItemCount()) {
            new Thread("Realms-reject-invitation"){

                public void run() {
                    try {
                        RealmsClient $$02 = RealmsClient.create();
                        $$02.rejectInvitation(((Entry)RealmsPendingInvitesScreen.this.pendingInvitationSelectionList.children().get((int)$$0)).pendingInvite.invitationId);
                        RealmsPendingInvitesScreen.this.minecraft.execute(() -> RealmsPendingInvitesScreen.this.updateList($$0));
                    }
                    catch (RealmsServiceException $$1) {
                        LOGGER.error("Couldn't reject invite");
                    }
                }
            }.start();
        }
    }

    void accept(final int $$0) {
        if ($$0 < this.pendingInvitationSelectionList.getItemCount()) {
            new Thread("Realms-accept-invitation"){

                public void run() {
                    try {
                        RealmsClient $$02 = RealmsClient.create();
                        $$02.acceptInvitation(((Entry)RealmsPendingInvitesScreen.this.pendingInvitationSelectionList.children().get((int)$$0)).pendingInvite.invitationId);
                        RealmsPendingInvitesScreen.this.minecraft.execute(() -> RealmsPendingInvitesScreen.this.updateList($$0));
                    }
                    catch (RealmsServiceException $$1) {
                        LOGGER.error("Couldn't accept invite");
                    }
                }
            }.start();
        }
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.toolTip = null;
        this.renderBackground($$0);
        this.pendingInvitationSelectionList.render($$0, $$1, $$2, $$3);
        RealmsPendingInvitesScreen.drawCenteredString($$0, this.font, this.title, this.width / 2, 12, 0xFFFFFF);
        if (this.toolTip != null) {
            this.renderMousehoverTooltip($$0, this.toolTip, $$1, $$2);
        }
        if (this.pendingInvitationSelectionList.getItemCount() == 0 && this.loaded) {
            RealmsPendingInvitesScreen.drawCenteredString($$0, this.font, NO_PENDING_INVITES_TEXT, this.width / 2, this.height / 2 - 20, 0xFFFFFF);
        }
        super.render($$0, $$1, $$2, $$3);
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

    void updateButtonStates() {
        this.acceptButton.visible = this.shouldAcceptAndRejectButtonBeVisible(this.selectedInvite);
        this.rejectButton.visible = this.shouldAcceptAndRejectButtonBeVisible(this.selectedInvite);
    }

    private boolean shouldAcceptAndRejectButtonBeVisible(int $$0) {
        return $$0 != -1;
    }

    class PendingInvitationSelectionList
    extends RealmsObjectSelectionList<Entry> {
        public PendingInvitationSelectionList() {
            super(RealmsPendingInvitesScreen.this.width, RealmsPendingInvitesScreen.this.height, 32, RealmsPendingInvitesScreen.this.height - 40, 36);
        }

        public void removeAtIndex(int $$0) {
            this.remove($$0);
        }

        @Override
        public int getMaxPosition() {
            return this.getItemCount() * 36;
        }

        @Override
        public int getRowWidth() {
            return 260;
        }

        @Override
        public void renderBackground(PoseStack $$0) {
            RealmsPendingInvitesScreen.this.renderBackground($$0);
        }

        @Override
        public void selectItem(int $$0) {
            super.selectItem($$0);
            this.selectInviteListItem($$0);
        }

        public void selectInviteListItem(int $$0) {
            RealmsPendingInvitesScreen.this.selectedInvite = $$0;
            RealmsPendingInvitesScreen.this.updateButtonStates();
        }

        @Override
        public void setSelected(@Nullable Entry $$0) {
            super.setSelected($$0);
            RealmsPendingInvitesScreen.this.selectedInvite = this.children().indexOf((Object)$$0);
            RealmsPendingInvitesScreen.this.updateButtonStates();
        }
    }

    class Entry
    extends ObjectSelectionList.Entry<Entry> {
        private static final int TEXT_LEFT = 38;
        final PendingInvite pendingInvite;
        private final List<RowButton> rowButtons;

        Entry(PendingInvite $$0) {
            this.pendingInvite = $$0;
            this.rowButtons = Arrays.asList((Object[])new RowButton[]{new AcceptRowButton(), new RejectRowButton()});
        }

        @Override
        public void render(PoseStack $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
            this.renderPendingInvitationItem($$0, this.pendingInvite, $$3, $$2, $$6, $$7);
        }

        @Override
        public boolean mouseClicked(double $$0, double $$1, int $$2) {
            RowButton.rowButtonMouseClicked(RealmsPendingInvitesScreen.this.pendingInvitationSelectionList, this, this.rowButtons, $$2, $$0, $$1);
            return true;
        }

        private void renderPendingInvitationItem(PoseStack $$0, PendingInvite $$1, int $$2, int $$3, int $$4, int $$5) {
            RealmsPendingInvitesScreen.this.font.draw($$0, $$1.worldName, (float)($$2 + 38), (float)($$3 + 1), 0xFFFFFF);
            RealmsPendingInvitesScreen.this.font.draw($$0, $$1.worldOwnerName, (float)($$2 + 38), (float)($$3 + 12), 0x6C6C6C);
            RealmsPendingInvitesScreen.this.font.draw($$0, RealmsUtil.convertToAgePresentationFromInstant($$1.date), (float)($$2 + 38), (float)($$3 + 24), 0x6C6C6C);
            RowButton.drawButtonsInRow($$0, this.rowButtons, RealmsPendingInvitesScreen.this.pendingInvitationSelectionList, $$2, $$3, $$4, $$5);
            RealmsTextureManager.withBoundFace($$1.worldOwnerUuid, () -> PlayerFaceRenderer.draw($$0, $$2, $$3, 32));
        }

        @Override
        public Component getNarration() {
            Component $$0 = CommonComponents.joinLines(Component.literal(this.pendingInvite.worldName), Component.literal(this.pendingInvite.worldOwnerName), Component.literal(RealmsUtil.convertToAgePresentationFromInstant(this.pendingInvite.date)));
            return Component.translatable("narrator.select", $$0);
        }

        class AcceptRowButton
        extends RowButton {
            AcceptRowButton() {
                super(15, 15, 215, 5);
            }

            @Override
            protected void draw(PoseStack $$0, int $$1, int $$2, boolean $$3) {
                RenderSystem.setShaderTexture(0, ACCEPT_ICON_LOCATION);
                float $$4 = $$3 ? 19.0f : 0.0f;
                GuiComponent.blit($$0, $$1, $$2, $$4, 0.0f, 18, 18, 37, 18);
                if ($$3) {
                    RealmsPendingInvitesScreen.this.toolTip = ACCEPT_INVITE_TOOLTIP;
                }
            }

            @Override
            public void onClick(int $$0) {
                RealmsPendingInvitesScreen.this.accept($$0);
            }
        }

        class RejectRowButton
        extends RowButton {
            RejectRowButton() {
                super(15, 15, 235, 5);
            }

            @Override
            protected void draw(PoseStack $$0, int $$1, int $$2, boolean $$3) {
                RenderSystem.setShaderTexture(0, REJECT_ICON_LOCATION);
                float $$4 = $$3 ? 19.0f : 0.0f;
                GuiComponent.blit($$0, $$1, $$2, $$4, 0.0f, 18, 18, 37, 18);
                if ($$3) {
                    RealmsPendingInvitesScreen.this.toolTip = REJECT_INVITE_TOOLTIP;
                }
            }

            @Override
            public void onClick(int $$0) {
                RealmsPendingInvitesScreen.this.reject($$0);
            }
        }
    }
}