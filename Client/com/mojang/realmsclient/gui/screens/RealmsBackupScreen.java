/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.lang.Boolean
 *  java.lang.CharSequence
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.Thread
 *  java.lang.Throwable
 *  java.text.DateFormat
 *  java.util.Collections
 *  java.util.Date
 *  java.util.List
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.Backup;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.screens.RealmsBackupInfoScreen;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.util.RealmsUtil;
import com.mojang.realmsclient.util.task.DownloadTask;
import com.mojang.realmsclient.util.task.RestoreTask;
import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class RealmsBackupScreen
extends RealmsScreen {
    static final Logger LOGGER = LogUtils.getLogger();
    static final ResourceLocation PLUS_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/plus_icon.png");
    static final ResourceLocation RESTORE_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/restore_icon.png");
    static final Component RESTORE_TOOLTIP = Component.translatable("mco.backup.button.restore");
    static final Component HAS_CHANGES_TOOLTIP = Component.translatable("mco.backup.changes.tooltip");
    private static final Component TITLE = Component.translatable("mco.configure.world.backup");
    private static final Component NO_BACKUPS_LABEL = Component.translatable("mco.backup.nobackups");
    static int lastScrollPosition = -1;
    private final RealmsConfigureWorldScreen lastScreen;
    List<Backup> backups = Collections.emptyList();
    @Nullable
    Component toolTip;
    BackupObjectSelectionList backupObjectSelectionList;
    int selectedBackup = -1;
    private final int slotId;
    private Button downloadButton;
    private Button restoreButton;
    private Button changesButton;
    Boolean noBackups = false;
    final RealmsServer serverData;
    private static final String UPLOADED_KEY = "Uploaded";

    public RealmsBackupScreen(RealmsConfigureWorldScreen $$0, RealmsServer $$1, int $$2) {
        super(Component.translatable("mco.configure.world.backup"));
        this.lastScreen = $$0;
        this.serverData = $$1;
        this.slotId = $$2;
    }

    @Override
    public void init() {
        this.backupObjectSelectionList = new BackupObjectSelectionList();
        if (lastScrollPosition != -1) {
            this.backupObjectSelectionList.setScrollAmount(lastScrollPosition);
        }
        new Thread("Realms-fetch-backups"){

            public void run() {
                RealmsClient $$0 = RealmsClient.create();
                try {
                    List<Backup> $$1 = $$0.backupsFor((long)RealmsBackupScreen.this.serverData.id).backups;
                    RealmsBackupScreen.this.minecraft.execute(() -> {
                        RealmsBackupScreen.this.backups = $$1;
                        RealmsBackupScreen.this.noBackups = RealmsBackupScreen.this.backups.isEmpty();
                        RealmsBackupScreen.this.backupObjectSelectionList.clear();
                        for (Backup $$1 : RealmsBackupScreen.this.backups) {
                            RealmsBackupScreen.this.backupObjectSelectionList.addEntry($$1);
                        }
                        RealmsBackupScreen.this.generateChangeList();
                    });
                }
                catch (RealmsServiceException $$2) {
                    LOGGER.error("Couldn't request backups", (Throwable)$$2);
                }
            }
        }.start();
        this.downloadButton = this.addRenderableWidget(Button.builder(Component.translatable("mco.backup.button.download"), $$0 -> this.downloadClicked()).bounds(this.width - 135, RealmsBackupScreen.row(1), 120, 20).build());
        this.restoreButton = this.addRenderableWidget(Button.builder(Component.translatable("mco.backup.button.restore"), $$0 -> this.restoreClicked(this.selectedBackup)).bounds(this.width - 135, RealmsBackupScreen.row(3), 120, 20).build());
        this.changesButton = this.addRenderableWidget(Button.builder(Component.translatable("mco.backup.changes.tooltip"), $$0 -> {
            this.minecraft.setScreen(new RealmsBackupInfoScreen(this, (Backup)this.backups.get(this.selectedBackup)));
            this.selectedBackup = -1;
        }).bounds(this.width - 135, RealmsBackupScreen.row(5), 120, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, $$0 -> this.minecraft.setScreen(this.lastScreen)).bounds(this.width - 100, this.height - 35, 85, 20).build());
        this.addWidget(this.backupObjectSelectionList);
        this.magicalSpecialHackyFocus(this.backupObjectSelectionList);
        this.updateButtonStates();
    }

    void generateChangeList() {
        if (this.backups.size() <= 1) {
            return;
        }
        for (int $$0 = 0; $$0 < this.backups.size() - 1; ++$$0) {
            Backup $$1 = (Backup)this.backups.get($$0);
            Backup $$2 = (Backup)this.backups.get($$0 + 1);
            if ($$1.metadata.isEmpty() || $$2.metadata.isEmpty()) continue;
            for (String $$3 : $$1.metadata.keySet()) {
                if (!$$3.contains((CharSequence)UPLOADED_KEY) && $$2.metadata.containsKey((Object)$$3)) {
                    if (((String)$$1.metadata.get((Object)$$3)).equals($$2.metadata.get((Object)$$3))) continue;
                    this.addToChangeList($$1, $$3);
                    continue;
                }
                this.addToChangeList($$1, $$3);
            }
        }
    }

    private void addToChangeList(Backup $$0, String $$1) {
        if ($$1.contains((CharSequence)UPLOADED_KEY)) {
            String $$2 = DateFormat.getDateTimeInstance((int)3, (int)3).format($$0.lastModifiedDate);
            $$0.changeList.put((Object)$$1, (Object)$$2);
            $$0.setUploadedVersion(true);
        } else {
            $$0.changeList.put((Object)$$1, (Object)((String)$$0.metadata.get((Object)$$1)));
        }
    }

    void updateButtonStates() {
        this.restoreButton.visible = this.shouldRestoreButtonBeVisible();
        this.changesButton.visible = this.shouldChangesButtonBeVisible();
    }

    private boolean shouldChangesButtonBeVisible() {
        if (this.selectedBackup == -1) {
            return false;
        }
        return !((Backup)this.backups.get((int)this.selectedBackup)).changeList.isEmpty();
    }

    private boolean shouldRestoreButtonBeVisible() {
        if (this.selectedBackup == -1) {
            return false;
        }
        return !this.serverData.expired;
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if ($$0 == 256) {
            this.minecraft.setScreen(this.lastScreen);
            return true;
        }
        return super.keyPressed($$0, $$1, $$2);
    }

    void restoreClicked(int $$02) {
        if ($$02 >= 0 && $$02 < this.backups.size() && !this.serverData.expired) {
            this.selectedBackup = $$02;
            Date $$1 = ((Backup)this.backups.get((int)$$02)).lastModifiedDate;
            String $$2 = DateFormat.getDateTimeInstance((int)3, (int)3).format($$1);
            String $$3 = RealmsUtil.convertToAgePresentationFromInstant($$1);
            MutableComponent $$4 = Component.translatable("mco.configure.world.restore.question.line1", $$2, $$3);
            MutableComponent $$5 = Component.translatable("mco.configure.world.restore.question.line2");
            this.minecraft.setScreen(new RealmsLongConfirmationScreen($$0 -> {
                if ($$0) {
                    this.restore();
                } else {
                    this.selectedBackup = -1;
                    this.minecraft.setScreen(this);
                }
            }, RealmsLongConfirmationScreen.Type.Warning, $$4, $$5, true));
        }
    }

    private void downloadClicked() {
        MutableComponent $$02 = Component.translatable("mco.configure.world.restore.download.question.line1");
        MutableComponent $$1 = Component.translatable("mco.configure.world.restore.download.question.line2");
        this.minecraft.setScreen(new RealmsLongConfirmationScreen($$0 -> {
            if ($$0) {
                this.downloadWorldData();
            } else {
                this.minecraft.setScreen(this);
            }
        }, RealmsLongConfirmationScreen.Type.Info, $$02, $$1, true));
    }

    private void downloadWorldData() {
        this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen.getNewScreen(), new DownloadTask(this.serverData.id, this.slotId, this.serverData.name + " (" + ((RealmsWorldOptions)this.serverData.slots.get((Object)this.serverData.activeSlot)).getSlotName(this.serverData.activeSlot) + ")", this)));
    }

    private void restore() {
        Backup $$0 = (Backup)this.backups.get(this.selectedBackup);
        this.selectedBackup = -1;
        this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen.getNewScreen(), new RestoreTask($$0, this.serverData.id, this.lastScreen)));
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.toolTip = null;
        this.renderBackground($$0);
        this.backupObjectSelectionList.render($$0, $$1, $$2, $$3);
        RealmsBackupScreen.drawCenteredString($$0, this.font, this.title, this.width / 2, 12, 0xFFFFFF);
        this.font.draw($$0, TITLE, (float)((this.width - 150) / 2 - 90), 20.0f, 0xA0A0A0);
        if (this.noBackups.booleanValue()) {
            this.font.draw($$0, NO_BACKUPS_LABEL, 20.0f, (float)(this.height / 2 - 10), 0xFFFFFF);
        }
        this.downloadButton.active = this.noBackups == false;
        super.render($$0, $$1, $$2, $$3);
        if (this.toolTip != null) {
            this.renderMousehoverTooltip($$0, this.toolTip, $$1, $$2);
        }
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

    class BackupObjectSelectionList
    extends RealmsObjectSelectionList<Entry> {
        public BackupObjectSelectionList() {
            super(RealmsBackupScreen.this.width - 150, RealmsBackupScreen.this.height, 32, RealmsBackupScreen.this.height - 15, 36);
        }

        public void addEntry(Backup $$0) {
            this.addEntry(new Entry($$0));
        }

        @Override
        public int getRowWidth() {
            return (int)((double)this.width * 0.93);
        }

        @Override
        public boolean isFocused() {
            return RealmsBackupScreen.this.getFocused() == this;
        }

        @Override
        public int getMaxPosition() {
            return this.getItemCount() * 36;
        }

        @Override
        public void renderBackground(PoseStack $$0) {
            RealmsBackupScreen.this.renderBackground($$0);
        }

        @Override
        public boolean mouseClicked(double $$0, double $$1, int $$2) {
            if ($$2 != 0) {
                return false;
            }
            if ($$0 < (double)this.getScrollbarPosition() && $$1 >= (double)this.y0 && $$1 <= (double)this.y1) {
                int $$3 = this.width / 2 - 92;
                int $$4 = this.width;
                int $$5 = (int)Math.floor((double)($$1 - (double)this.y0)) - this.headerHeight + (int)this.getScrollAmount();
                int $$6 = $$5 / this.itemHeight;
                if ($$0 >= (double)$$3 && $$0 <= (double)$$4 && $$6 >= 0 && $$5 >= 0 && $$6 < this.getItemCount()) {
                    this.selectItem($$6);
                    this.itemClicked($$5, $$6, $$0, $$1, this.width);
                }
                return true;
            }
            return false;
        }

        @Override
        public int getScrollbarPosition() {
            return this.width - 5;
        }

        @Override
        public void itemClicked(int $$0, int $$1, double $$2, double $$3, int $$4) {
            int $$5 = this.width - 35;
            int $$6 = $$1 * this.itemHeight + 36 - (int)this.getScrollAmount();
            int $$7 = $$5 + 10;
            int $$8 = $$6 - 3;
            if ($$2 >= (double)$$5 && $$2 <= (double)($$5 + 9) && $$3 >= (double)$$6 && $$3 <= (double)($$6 + 9)) {
                if (!((Backup)RealmsBackupScreen.this.backups.get((int)$$1)).changeList.isEmpty()) {
                    RealmsBackupScreen.this.selectedBackup = -1;
                    lastScrollPosition = (int)this.getScrollAmount();
                    this.minecraft.setScreen(new RealmsBackupInfoScreen(RealmsBackupScreen.this, (Backup)RealmsBackupScreen.this.backups.get($$1)));
                }
            } else if ($$2 >= (double)$$7 && $$2 < (double)($$7 + 13) && $$3 >= (double)$$8 && $$3 < (double)($$8 + 15)) {
                lastScrollPosition = (int)this.getScrollAmount();
                RealmsBackupScreen.this.restoreClicked($$1);
            }
        }

        @Override
        public void selectItem(int $$0) {
            super.selectItem($$0);
            this.selectInviteListItem($$0);
        }

        public void selectInviteListItem(int $$0) {
            RealmsBackupScreen.this.selectedBackup = $$0;
            RealmsBackupScreen.this.updateButtonStates();
        }

        @Override
        public void setSelected(@Nullable Entry $$0) {
            super.setSelected($$0);
            RealmsBackupScreen.this.selectedBackup = this.children().indexOf((Object)$$0);
            RealmsBackupScreen.this.updateButtonStates();
        }
    }

    class Entry
    extends ObjectSelectionList.Entry<Entry> {
        private final Backup backup;

        public Entry(Backup $$0) {
            this.backup = $$0;
        }

        @Override
        public void render(PoseStack $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
            this.renderBackupItem($$0, this.backup, $$3 - 40, $$2, $$6, $$7);
        }

        private void renderBackupItem(PoseStack $$0, Backup $$1, int $$2, int $$3, int $$4, int $$5) {
            int $$6 = $$1.isUploadedVersion() ? -8388737 : 0xFFFFFF;
            RealmsBackupScreen.this.font.draw($$0, "Backup (" + RealmsUtil.convertToAgePresentationFromInstant($$1.lastModifiedDate) + ")", (float)($$2 + 40), (float)($$3 + 1), $$6);
            RealmsBackupScreen.this.font.draw($$0, this.getMediumDatePresentation($$1.lastModifiedDate), (float)($$2 + 40), (float)($$3 + 12), 0x4C4C4C);
            int $$7 = RealmsBackupScreen.this.width - 175;
            int $$8 = -3;
            int $$9 = $$7 - 10;
            boolean $$10 = false;
            if (!RealmsBackupScreen.this.serverData.expired) {
                this.drawRestore($$0, $$7, $$3 + -3, $$4, $$5);
            }
            if (!$$1.changeList.isEmpty()) {
                this.drawInfo($$0, $$9, $$3 + 0, $$4, $$5);
            }
        }

        private String getMediumDatePresentation(Date $$0) {
            return DateFormat.getDateTimeInstance((int)3, (int)3).format($$0);
        }

        private void drawRestore(PoseStack $$0, int $$1, int $$2, int $$3, int $$4) {
            boolean $$5 = $$3 >= $$1 && $$3 <= $$1 + 12 && $$4 >= $$2 && $$4 <= $$2 + 14 && $$4 < RealmsBackupScreen.this.height - 15 && $$4 > 32;
            RenderSystem.setShaderTexture(0, RESTORE_ICON_LOCATION);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            $$0.pushPose();
            $$0.scale(0.5f, 0.5f, 0.5f);
            float $$6 = $$5 ? 28.0f : 0.0f;
            GuiComponent.blit($$0, $$1 * 2, $$2 * 2, 0.0f, $$6, 23, 28, 23, 56);
            $$0.popPose();
            if ($$5) {
                RealmsBackupScreen.this.toolTip = RESTORE_TOOLTIP;
            }
        }

        private void drawInfo(PoseStack $$0, int $$1, int $$2, int $$3, int $$4) {
            boolean $$5 = $$3 >= $$1 && $$3 <= $$1 + 8 && $$4 >= $$2 && $$4 <= $$2 + 8 && $$4 < RealmsBackupScreen.this.height - 15 && $$4 > 32;
            RenderSystem.setShaderTexture(0, PLUS_ICON_LOCATION);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            $$0.pushPose();
            $$0.scale(0.5f, 0.5f, 0.5f);
            float $$6 = $$5 ? 15.0f : 0.0f;
            GuiComponent.blit($$0, $$1 * 2, $$2 * 2, 0.0f, $$6, 15, 15, 15, 30);
            $$0.popPose();
            if ($$5) {
                RealmsBackupScreen.this.toolTip = HAS_CHANGES_TOOLTIP;
            }
        }

        @Override
        public Component getNarration() {
            return Component.translatable("narrator.select", this.backup.lastModifiedDate.toString());
        }
    }
}