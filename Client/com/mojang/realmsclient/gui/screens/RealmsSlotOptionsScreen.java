/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  java.lang.Boolean
 *  java.lang.IndexOutOfBoundsException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Collection
 *  java.util.List
 *  java.util.function.Consumer
 */
package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameType;

public class RealmsSlotOptionsScreen
extends RealmsScreen {
    private static final int DEFAULT_DIFFICULTY = 2;
    public static final List<Difficulty> DIFFICULTIES = ImmutableList.of((Object)Difficulty.PEACEFUL, (Object)Difficulty.EASY, (Object)Difficulty.NORMAL, (Object)Difficulty.HARD);
    private static final int DEFAULT_GAME_MODE = 0;
    public static final List<GameType> GAME_MODES = ImmutableList.of((Object)GameType.SURVIVAL, (Object)GameType.CREATIVE, (Object)GameType.ADVENTURE);
    private static final Component NAME_LABEL = Component.translatable("mco.configure.world.edit.slot.name");
    static final Component SPAWN_PROTECTION_TEXT = Component.translatable("mco.configure.world.spawnProtection");
    private static final Component SPAWN_WARNING_TITLE = Component.translatable("mco.configure.world.spawn_toggle.title").withStyle(ChatFormatting.RED, ChatFormatting.BOLD);
    private EditBox nameEdit;
    protected final RealmsConfigureWorldScreen parent;
    private int column1X;
    private int columnWidth;
    private final RealmsWorldOptions options;
    private final RealmsServer.WorldType worldType;
    private Difficulty difficulty;
    private GameType gameMode;
    private final String defaultSlotName;
    private String worldName;
    private boolean pvp;
    private boolean spawnNPCs;
    private boolean spawnAnimals;
    private boolean spawnMonsters;
    int spawnProtection;
    private boolean commandBlocks;
    private boolean forceGameMode;
    SettingsSlider spawnProtectionButton;

    public RealmsSlotOptionsScreen(RealmsConfigureWorldScreen $$0, RealmsWorldOptions $$1, RealmsServer.WorldType $$2, int $$3) {
        super(Component.translatable("mco.configure.world.buttons.options"));
        this.parent = $$0;
        this.options = $$1;
        this.worldType = $$2;
        this.difficulty = RealmsSlotOptionsScreen.findByIndex(DIFFICULTIES, $$1.difficulty, 2);
        this.gameMode = RealmsSlotOptionsScreen.findByIndex(GAME_MODES, $$1.gameMode, 0);
        this.defaultSlotName = $$1.getDefaultSlotName($$3);
        this.setWorldName($$1.getSlotName($$3));
        if ($$2 == RealmsServer.WorldType.NORMAL) {
            this.pvp = $$1.pvp;
            this.spawnProtection = $$1.spawnProtection;
            this.forceGameMode = $$1.forceGameMode;
            this.spawnAnimals = $$1.spawnAnimals;
            this.spawnMonsters = $$1.spawnMonsters;
            this.spawnNPCs = $$1.spawnNPCs;
            this.commandBlocks = $$1.commandBlocks;
        } else {
            this.pvp = true;
            this.spawnProtection = 0;
            this.forceGameMode = false;
            this.spawnAnimals = true;
            this.spawnMonsters = true;
            this.spawnNPCs = true;
            this.commandBlocks = true;
        }
    }

    @Override
    public void tick() {
        this.nameEdit.tick();
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if ($$0 == 256) {
            this.minecraft.setScreen(this.parent);
            return true;
        }
        return super.keyPressed($$0, $$1, $$2);
    }

    private static <T> T findByIndex(List<T> $$0, int $$1, int $$2) {
        try {
            return (T)$$0.get($$1);
        }
        catch (IndexOutOfBoundsException $$3) {
            return (T)$$0.get($$2);
        }
    }

    private static <T> int findIndex(List<T> $$0, T $$1, int $$2) {
        int $$3 = $$0.indexOf($$1);
        return $$3 == -1 ? $$2 : $$3;
    }

    @Override
    public void init() {
        this.columnWidth = 170;
        this.column1X = this.width / 2 - this.columnWidth;
        int $$02 = this.width / 2 + 10;
        if (this.worldType != RealmsServer.WorldType.NORMAL) {
            MutableComponent $$3;
            if (this.worldType == RealmsServer.WorldType.ADVENTUREMAP) {
                MutableComponent $$12 = Component.translatable("mco.configure.world.edit.subscreen.adventuremap");
            } else if (this.worldType == RealmsServer.WorldType.INSPIRATION) {
                MutableComponent $$22 = Component.translatable("mco.configure.world.edit.subscreen.inspiration");
            } else {
                $$3 = Component.translatable("mco.configure.world.edit.subscreen.experience");
            }
            this.addLabel(new RealmsLabel($$3, this.width / 2, 26, 0xFF0000));
        }
        this.nameEdit = new EditBox(this.minecraft.font, this.column1X + 2, RealmsSlotOptionsScreen.row(1), this.columnWidth - 4, 20, null, Component.translatable("mco.configure.world.edit.slot.name"));
        this.nameEdit.setMaxLength(10);
        this.nameEdit.setValue(this.worldName);
        this.nameEdit.setResponder((Consumer<String>)((Consumer)this::setWorldName));
        this.magicalSpecialHackyFocus(this.nameEdit);
        CycleButton<Boolean> $$4 = this.addRenderableWidget(CycleButton.onOffBuilder(this.pvp).create($$02, RealmsSlotOptionsScreen.row(1), this.columnWidth, 20, Component.translatable("mco.configure.world.pvp"), ($$0, $$1) -> {
            this.pvp = $$1;
        }));
        this.addRenderableWidget(CycleButton.builder(GameType::getShortDisplayName).withValues((Collection<GameType>)GAME_MODES).withInitialValue(this.gameMode).create(this.column1X, RealmsSlotOptionsScreen.row(3), this.columnWidth, 20, Component.translatable("selectWorld.gameMode"), ($$0, $$1) -> {
            this.gameMode = $$1;
        }));
        MutableComponent $$5 = Component.translatable("mco.configure.world.spawn_toggle.message");
        CycleButton<Boolean> $$6 = this.addRenderableWidget(CycleButton.onOffBuilder(this.spawnAnimals).create($$02, RealmsSlotOptionsScreen.row(3), this.columnWidth, 20, Component.translatable("mco.configure.world.spawnAnimals"), this.confirmDangerousOption($$5, (Consumer<Boolean>)((Consumer)$$0 -> {
            this.spawnAnimals = $$0;
        }))));
        CycleButton<Boolean> $$7 = CycleButton.onOffBuilder(this.difficulty != Difficulty.PEACEFUL && this.spawnMonsters).create($$02, RealmsSlotOptionsScreen.row(5), this.columnWidth, 20, Component.translatable("mco.configure.world.spawnMonsters"), this.confirmDangerousOption($$5, (Consumer<Boolean>)((Consumer)$$0 -> {
            this.spawnMonsters = $$0;
        })));
        this.addRenderableWidget(CycleButton.builder(Difficulty::getDisplayName).withValues((Collection<Difficulty>)DIFFICULTIES).withInitialValue(this.difficulty).create(this.column1X, RealmsSlotOptionsScreen.row(5), this.columnWidth, 20, Component.translatable("options.difficulty"), ($$1, $$2) -> {
            this.difficulty = $$2;
            if (this.worldType == RealmsServer.WorldType.NORMAL) {
                boolean $$3;
                $$0.active = $$3 = this.difficulty != Difficulty.PEACEFUL;
                $$7.setValue($$3 && this.spawnMonsters);
            }
        }));
        this.addRenderableWidget($$7);
        this.spawnProtectionButton = this.addRenderableWidget(new SettingsSlider(this.column1X, RealmsSlotOptionsScreen.row(7), this.columnWidth, this.spawnProtection, 0.0f, 16.0f));
        CycleButton<Boolean> $$8 = this.addRenderableWidget(CycleButton.onOffBuilder(this.spawnNPCs).create($$02, RealmsSlotOptionsScreen.row(7), this.columnWidth, 20, Component.translatable("mco.configure.world.spawnNPCs"), this.confirmDangerousOption(Component.translatable("mco.configure.world.spawn_toggle.message.npc"), (Consumer<Boolean>)((Consumer)$$0 -> {
            this.spawnNPCs = $$0;
        }))));
        CycleButton<Boolean> $$9 = this.addRenderableWidget(CycleButton.onOffBuilder(this.forceGameMode).create(this.column1X, RealmsSlotOptionsScreen.row(9), this.columnWidth, 20, Component.translatable("mco.configure.world.forceGameMode"), ($$0, $$1) -> {
            this.forceGameMode = $$1;
        }));
        CycleButton<Boolean> $$10 = this.addRenderableWidget(CycleButton.onOffBuilder(this.commandBlocks).create($$02, RealmsSlotOptionsScreen.row(9), this.columnWidth, 20, Component.translatable("mco.configure.world.commandBlocks"), ($$0, $$1) -> {
            this.commandBlocks = $$1;
        }));
        if (this.worldType != RealmsServer.WorldType.NORMAL) {
            $$4.active = false;
            $$6.active = false;
            $$8.active = false;
            $$7.active = false;
            this.spawnProtectionButton.active = false;
            $$10.active = false;
            $$9.active = false;
        }
        if (this.difficulty == Difficulty.PEACEFUL) {
            $$7.active = false;
        }
        this.addRenderableWidget(Button.builder(Component.translatable("mco.configure.world.buttons.done"), $$0 -> this.saveSettings()).bounds(this.column1X, RealmsSlotOptionsScreen.row(13), this.columnWidth, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, $$0 -> this.minecraft.setScreen(this.parent)).bounds($$02, RealmsSlotOptionsScreen.row(13), this.columnWidth, 20).build());
        this.addWidget(this.nameEdit);
    }

    private CycleButton.OnValueChange<Boolean> confirmDangerousOption(Component $$0, Consumer<Boolean> $$1) {
        return ($$2, $$3) -> {
            if ($$3.booleanValue()) {
                $$1.accept((Object)true);
            } else {
                this.minecraft.setScreen(new ConfirmScreen($$1 -> {
                    if ($$1) {
                        $$1.accept((Object)false);
                    }
                    this.minecraft.setScreen(this);
                }, SPAWN_WARNING_TITLE, $$0, CommonComponents.GUI_PROCEED, CommonComponents.GUI_CANCEL));
            }
        };
    }

    @Override
    public Component getNarrationMessage() {
        return CommonComponents.joinForNarration(this.getTitle(), this.createLabelNarration());
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderBackground($$0);
        RealmsSlotOptionsScreen.drawCenteredString($$0, this.font, this.title, this.width / 2, 17, 0xFFFFFF);
        this.font.draw($$0, NAME_LABEL, (float)(this.column1X + this.columnWidth / 2 - this.font.width(NAME_LABEL) / 2), (float)(RealmsSlotOptionsScreen.row(0) - 5), 0xFFFFFF);
        this.nameEdit.render($$0, $$1, $$2, $$3);
        super.render($$0, $$1, $$2, $$3);
    }

    private void setWorldName(String $$0) {
        this.worldName = $$0.equals((Object)this.defaultSlotName) ? "" : $$0;
    }

    private void saveSettings() {
        int $$0 = RealmsSlotOptionsScreen.findIndex(DIFFICULTIES, this.difficulty, 2);
        int $$1 = RealmsSlotOptionsScreen.findIndex(GAME_MODES, this.gameMode, 0);
        if (this.worldType == RealmsServer.WorldType.ADVENTUREMAP || this.worldType == RealmsServer.WorldType.EXPERIENCE || this.worldType == RealmsServer.WorldType.INSPIRATION) {
            this.parent.saveSlotSettings(new RealmsWorldOptions(this.options.pvp, this.options.spawnAnimals, this.options.spawnMonsters, this.options.spawnNPCs, this.options.spawnProtection, this.options.commandBlocks, $$0, $$1, this.options.forceGameMode, this.worldName));
        } else {
            boolean $$2 = this.worldType == RealmsServer.WorldType.NORMAL && this.difficulty != Difficulty.PEACEFUL && this.spawnMonsters;
            this.parent.saveSlotSettings(new RealmsWorldOptions(this.pvp, this.spawnAnimals, $$2, this.spawnNPCs, this.spawnProtection, this.commandBlocks, $$0, $$1, this.forceGameMode, this.worldName));
        }
    }

    class SettingsSlider
    extends AbstractSliderButton {
        private final double minValue;
        private final double maxValue;

        public SettingsSlider(int $$0, int $$1, int $$2, int $$3, float $$4, float $$5) {
            super($$0, $$1, $$2, 20, CommonComponents.EMPTY, 0.0);
            this.minValue = $$4;
            this.maxValue = $$5;
            this.value = (Mth.clamp((float)$$3, $$4, $$5) - $$4) / ($$5 - $$4);
            this.updateMessage();
        }

        @Override
        public void applyValue() {
            if (!RealmsSlotOptionsScreen.this.spawnProtectionButton.active) {
                return;
            }
            RealmsSlotOptionsScreen.this.spawnProtection = (int)Mth.lerp(Mth.clamp(this.value, 0.0, 1.0), this.minValue, this.maxValue);
        }

        @Override
        protected void updateMessage() {
            this.setMessage(CommonComponents.optionNameValue(SPAWN_PROTECTION_TEXT, RealmsSlotOptionsScreen.this.spawnProtection == 0 ? CommonComponents.OPTION_OFF : Component.literal(String.valueOf((int)RealmsSlotOptionsScreen.this.spawnProtection))));
        }

        @Override
        public void onClick(double $$0, double $$1) {
        }

        @Override
        public void onRelease(double $$0, double $$1) {
        }
    }
}