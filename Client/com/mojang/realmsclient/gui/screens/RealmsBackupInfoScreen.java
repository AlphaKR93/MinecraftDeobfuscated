/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.CharSequence
 *  java.lang.Exception
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Locale
 */
package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.realmsclient.dto.Backup;
import com.mojang.realmsclient.gui.screens.RealmsSlotOptionsScreen;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameType;

public class RealmsBackupInfoScreen
extends RealmsScreen {
    private static final Component TEXT_UNKNOWN = Component.literal("UNKNOWN");
    private final Screen lastScreen;
    final Backup backup;
    private BackupInfoList backupInfoList;

    public RealmsBackupInfoScreen(Screen $$0, Backup $$1) {
        super(Component.literal("Changes from last backup"));
        this.lastScreen = $$0;
        this.backup = $$1;
    }

    @Override
    public void tick() {
    }

    @Override
    public void init() {
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, $$0 -> this.minecraft.setScreen(this.lastScreen)).bounds(this.width / 2 - 100, this.height / 4 + 120 + 24, 200, 20).build());
        this.backupInfoList = new BackupInfoList(this.minecraft);
        this.addWidget(this.backupInfoList);
        this.magicalSpecialHackyFocus(this.backupInfoList);
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
        this.backupInfoList.render($$0, $$1, $$2, $$3);
        RealmsBackupInfoScreen.drawCenteredString($$0, this.font, this.title, this.width / 2, 10, 0xFFFFFF);
        super.render($$0, $$1, $$2, $$3);
    }

    Component checkForSpecificMetadata(String $$0, String $$1) {
        String $$2 = $$0.toLowerCase(Locale.ROOT);
        if ($$2.contains((CharSequence)"game") && $$2.contains((CharSequence)"mode")) {
            return this.gameModeMetadata($$1);
        }
        if ($$2.contains((CharSequence)"game") && $$2.contains((CharSequence)"difficulty")) {
            return this.gameDifficultyMetadata($$1);
        }
        return Component.literal($$1);
    }

    private Component gameDifficultyMetadata(String $$0) {
        try {
            return ((Difficulty)RealmsSlotOptionsScreen.DIFFICULTIES.get(Integer.parseInt((String)$$0))).getDisplayName();
        }
        catch (Exception $$1) {
            return TEXT_UNKNOWN;
        }
    }

    private Component gameModeMetadata(String $$0) {
        try {
            return ((GameType)RealmsSlotOptionsScreen.GAME_MODES.get(Integer.parseInt((String)$$0))).getShortDisplayName();
        }
        catch (Exception $$1) {
            return TEXT_UNKNOWN;
        }
    }

    class BackupInfoList
    extends ObjectSelectionList<BackupInfoListEntry> {
        public BackupInfoList(Minecraft $$02) {
            super($$02, RealmsBackupInfoScreen.this.width, RealmsBackupInfoScreen.this.height, 32, RealmsBackupInfoScreen.this.height - 64, 36);
            this.setRenderSelection(false);
            if (RealmsBackupInfoScreen.this.backup.changeList != null) {
                RealmsBackupInfoScreen.this.backup.changeList.forEach(($$0, $$1) -> this.addEntry(new BackupInfoListEntry((String)$$0, (String)$$1)));
            }
        }
    }

    class BackupInfoListEntry
    extends ObjectSelectionList.Entry<BackupInfoListEntry> {
        private final String key;
        private final String value;

        public BackupInfoListEntry(String $$0, String $$1) {
            this.key = $$0;
            this.value = $$1;
        }

        @Override
        public void render(PoseStack $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
            Font $$10 = ((RealmsBackupInfoScreen)RealmsBackupInfoScreen.this).minecraft.font;
            GuiComponent.drawString($$0, $$10, this.key, $$3, $$2, 0xA0A0A0);
            GuiComponent.drawString($$0, $$10, RealmsBackupInfoScreen.this.checkForSpecificMetadata(this.key, this.value), $$3, $$2 + 12, 0xFFFFFF);
        }

        @Override
        public Component getNarration() {
            return Component.translatable("narrator.select", this.key + " " + this.value);
        }
    }
}