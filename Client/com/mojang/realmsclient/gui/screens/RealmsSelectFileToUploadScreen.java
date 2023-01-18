/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.logging.LogUtils
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Runnable
 *  java.lang.String
 *  java.lang.Throwable
 *  java.text.DateFormat
 *  java.text.SimpleDateFormat
 *  java.util.Date
 *  java.util.List
 *  java.util.function.UnaryOperator
 *  java.util.stream.Collectors
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsResetWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsUploadScreen;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import org.slf4j.Logger;

public class RealmsSelectFileToUploadScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogUtils.getLogger();
    static final Component WORLD_TEXT = Component.translatable("selectWorld.world");
    static final Component HARDCORE_TEXT = Component.translatable("mco.upload.hardcore").withStyle((UnaryOperator<Style>)((UnaryOperator)$$0 -> $$0.withColor(-65536)));
    static final Component CHEATS_TEXT = Component.translatable("selectWorld.cheats");
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat();
    private final RealmsResetWorldScreen lastScreen;
    private final long worldId;
    private final int slotId;
    Button uploadButton;
    List<LevelSummary> levelList = Lists.newArrayList();
    int selectedWorld = -1;
    WorldSelectionList worldSelectionList;
    private final Runnable callback;

    public RealmsSelectFileToUploadScreen(long $$0, int $$1, RealmsResetWorldScreen $$2, Runnable $$3) {
        super(Component.translatable("mco.upload.select.world.title"));
        this.lastScreen = $$2;
        this.worldId = $$0;
        this.slotId = $$1;
        this.callback = $$3;
    }

    private void loadLevelList() throws Exception {
        LevelStorageSource.LevelCandidates $$02 = this.minecraft.getLevelSource().findLevelCandidates();
        this.levelList = (List)((List)this.minecraft.getLevelSource().loadLevelSummaries($$02).join()).stream().filter($$0 -> !$$0.requiresManualConversion() && !$$0.isLocked()).collect(Collectors.toList());
        for (LevelSummary $$1 : this.levelList) {
            this.worldSelectionList.addEntry($$1);
        }
    }

    @Override
    public void init() {
        this.worldSelectionList = new WorldSelectionList();
        try {
            this.loadLevelList();
        }
        catch (Exception $$02) {
            LOGGER.error("Couldn't load level list", (Throwable)$$02);
            this.minecraft.setScreen(new RealmsGenericErrorScreen(Component.literal("Unable to load worlds"), Component.nullToEmpty($$02.getMessage()), this.lastScreen));
            return;
        }
        this.addWidget(this.worldSelectionList);
        this.uploadButton = this.addRenderableWidget(Button.builder(Component.translatable("mco.upload.button.name"), $$0 -> this.upload()).bounds(this.width / 2 - 154, this.height - 32, 153, 20).build());
        this.uploadButton.active = this.selectedWorld >= 0 && this.selectedWorld < this.levelList.size();
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, $$0 -> this.minecraft.setScreen(this.lastScreen)).bounds(this.width / 2 + 6, this.height - 32, 153, 20).build());
        this.addLabel(new RealmsLabel(Component.translatable("mco.upload.select.world.subtitle"), this.width / 2, RealmsSelectFileToUploadScreen.row(-1), 0xA0A0A0));
        if (this.levelList.isEmpty()) {
            this.addLabel(new RealmsLabel(Component.translatable("mco.upload.select.world.none"), this.width / 2, this.height / 2 - 20, 0xFFFFFF));
        }
    }

    @Override
    public Component getNarrationMessage() {
        return CommonComponents.joinForNarration(this.getTitle(), this.createLabelNarration());
    }

    private void upload() {
        if (this.selectedWorld != -1 && !((LevelSummary)this.levelList.get(this.selectedWorld)).isHardcore()) {
            LevelSummary $$0 = (LevelSummary)this.levelList.get(this.selectedWorld);
            this.minecraft.setScreen(new RealmsUploadScreen(this.worldId, this.slotId, this.lastScreen, $$0, this.callback));
        }
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderBackground($$0);
        this.worldSelectionList.render($$0, $$1, $$2, $$3);
        RealmsSelectFileToUploadScreen.drawCenteredString($$0, this.font, this.title, this.width / 2, 13, 0xFFFFFF);
        super.render($$0, $$1, $$2, $$3);
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if ($$0 == 256) {
            this.minecraft.setScreen(this.lastScreen);
            return true;
        }
        return super.keyPressed($$0, $$1, $$2);
    }

    static Component gameModeName(LevelSummary $$0) {
        return $$0.getGameMode().getLongDisplayName();
    }

    static String formatLastPlayed(LevelSummary $$0) {
        return DATE_FORMAT.format(new Date($$0.getLastPlayed()));
    }

    class WorldSelectionList
    extends RealmsObjectSelectionList<Entry> {
        public WorldSelectionList() {
            super(RealmsSelectFileToUploadScreen.this.width, RealmsSelectFileToUploadScreen.this.height, RealmsSelectFileToUploadScreen.row(0), RealmsSelectFileToUploadScreen.this.height - 40, 36);
        }

        public void addEntry(LevelSummary $$0) {
            this.addEntry(new Entry($$0));
        }

        @Override
        public int getMaxPosition() {
            return RealmsSelectFileToUploadScreen.this.levelList.size() * 36;
        }

        @Override
        public void renderBackground(PoseStack $$0) {
            RealmsSelectFileToUploadScreen.this.renderBackground($$0);
        }

        @Override
        public void setSelected(@Nullable Entry $$0) {
            super.setSelected($$0);
            RealmsSelectFileToUploadScreen.this.selectedWorld = this.children().indexOf((Object)$$0);
            RealmsSelectFileToUploadScreen.this.uploadButton.active = RealmsSelectFileToUploadScreen.this.selectedWorld >= 0 && RealmsSelectFileToUploadScreen.this.selectedWorld < this.getItemCount() && !((LevelSummary)RealmsSelectFileToUploadScreen.this.levelList.get(RealmsSelectFileToUploadScreen.this.selectedWorld)).isHardcore();
        }
    }

    class Entry
    extends ObjectSelectionList.Entry<Entry> {
        private final LevelSummary levelSummary;
        private final String name;
        private final String id;
        private final Component info;

        public Entry(LevelSummary $$0) {
            Component $$2;
            this.levelSummary = $$0;
            this.name = $$0.getLevelName();
            this.id = $$0.getLevelId() + " (" + RealmsSelectFileToUploadScreen.formatLastPlayed($$0) + ")";
            if ($$0.isHardcore()) {
                Component $$1 = HARDCORE_TEXT;
            } else {
                $$2 = RealmsSelectFileToUploadScreen.gameModeName($$0);
            }
            if ($$0.hasCheats()) {
                $$2 = $$2.copy().append(", ").append(CHEATS_TEXT);
            }
            this.info = $$2;
        }

        @Override
        public void render(PoseStack $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
            this.renderItem($$0, $$1, $$3, $$2);
        }

        @Override
        public boolean mouseClicked(double $$0, double $$1, int $$2) {
            RealmsSelectFileToUploadScreen.this.worldSelectionList.selectItem(RealmsSelectFileToUploadScreen.this.levelList.indexOf((Object)this.levelSummary));
            return true;
        }

        protected void renderItem(PoseStack $$0, int $$1, int $$2, int $$3) {
            String $$5;
            if (this.name.isEmpty()) {
                String $$4 = WORLD_TEXT + " " + ($$1 + 1);
            } else {
                $$5 = this.name;
            }
            RealmsSelectFileToUploadScreen.this.font.draw($$0, $$5, (float)($$2 + 2), (float)($$3 + 1), 0xFFFFFF);
            RealmsSelectFileToUploadScreen.this.font.draw($$0, this.id, (float)($$2 + 2), (float)($$3 + 12), 0x808080);
            RealmsSelectFileToUploadScreen.this.font.draw($$0, this.info, (float)($$2 + 2), (float)($$3 + 12 + 10), 0x808080);
        }

        @Override
        public Component getNarration() {
            Component $$0 = CommonComponents.joinLines(Component.literal(this.levelSummary.getLevelName()), Component.literal(RealmsSelectFileToUploadScreen.formatLastPlayed(this.levelSummary)), RealmsSelectFileToUploadScreen.gameModeName(this.levelSummary));
            return Component.translatable("narrator.select", $$0);
        }
    }
}