/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.function.Consumer
 *  java.util.function.Supplier
 */
package net.minecraft.client.gui.screens;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.LockIconButton;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.screens.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screens.ChatOptionsScreen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.LanguageSelectScreen;
import net.minecraft.client.gui.screens.OnlineOptionsScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.SkinCustomizationScreen;
import net.minecraft.client.gui.screens.SoundOptionsScreen;
import net.minecraft.client.gui.screens.VideoSettingsScreen;
import net.minecraft.client.gui.screens.controls.ControlsScreen;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.client.gui.screens.telemetry.TelemetryInfoScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ServerboundLockDifficultyPacket;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.Difficulty;

public class OptionsScreen
extends Screen {
    private static final Component SKIN_CUSTOMIZATION = Component.translatable("options.skinCustomisation");
    private static final Component SOUNDS = Component.translatable("options.sounds");
    private static final Component VIDEO = Component.translatable("options.video");
    private static final Component CONTROLS = Component.translatable("options.controls");
    private static final Component LANGUAGE = Component.translatable("options.language");
    private static final Component CHAT = Component.translatable("options.chat.title");
    private static final Component RESOURCEPACK = Component.translatable("options.resourcepack");
    private static final Component ACCESSIBILITY = Component.translatable("options.accessibility.title");
    private static final Component TELEMETRY = Component.translatable("options.telemetry");
    private static final int COLUMNS = 2;
    private final Screen lastScreen;
    private final Options options;
    private CycleButton<Difficulty> difficultyButton;
    private LockIconButton lockButton;

    public OptionsScreen(Screen $$0, Options $$1) {
        super(Component.translatable("options.title"));
        this.lastScreen = $$0;
        this.options = $$1;
    }

    @Override
    protected void init() {
        GridLayout $$02 = new GridLayout();
        $$02.defaultCellSetting().paddingHorizontal(5).paddingBottom(4).alignHorizontallyCenter();
        GridLayout.RowHelper $$1 = $$02.createRowHelper(2);
        $$1.addChild(this.options.fov().createButton(this.minecraft.options, 0, 0, 150));
        $$1.addChild(this.createOnlineButton());
        $$1.addChild(SpacerElement.height(26), 2);
        $$1.addChild(this.openScreenButton(SKIN_CUSTOMIZATION, (Supplier<Screen>)((Supplier)() -> new SkinCustomizationScreen(this, this.options))));
        $$1.addChild(this.openScreenButton(SOUNDS, (Supplier<Screen>)((Supplier)() -> new SoundOptionsScreen(this, this.options))));
        $$1.addChild(this.openScreenButton(VIDEO, (Supplier<Screen>)((Supplier)() -> new VideoSettingsScreen(this, this.options))));
        $$1.addChild(this.openScreenButton(CONTROLS, (Supplier<Screen>)((Supplier)() -> new ControlsScreen(this, this.options))));
        $$1.addChild(this.openScreenButton(LANGUAGE, (Supplier<Screen>)((Supplier)() -> new LanguageSelectScreen((Screen)this, this.options, this.minecraft.getLanguageManager()))));
        $$1.addChild(this.openScreenButton(CHAT, (Supplier<Screen>)((Supplier)() -> new ChatOptionsScreen(this, this.options))));
        $$1.addChild(this.openScreenButton(RESOURCEPACK, (Supplier<Screen>)((Supplier)() -> new PackSelectionScreen(this, this.minecraft.getResourcePackRepository(), (Consumer<PackRepository>)((Consumer)this::updatePackList), this.minecraft.getResourcePackDirectory(), Component.translatable("resourcePack.title")))));
        $$1.addChild(this.openScreenButton(ACCESSIBILITY, (Supplier<Screen>)((Supplier)() -> new AccessibilityOptionsScreen(this, this.options))));
        $$1.addChild(this.openScreenButton(TELEMETRY, (Supplier<Screen>)((Supplier)() -> new TelemetryInfoScreen(this, this.options))));
        $$1.addChild(Button.builder(CommonComponents.GUI_DONE, $$0 -> this.minecraft.setScreen(this.lastScreen)).width(200).build(), 2, $$1.newCellSettings().paddingTop(6));
        $$02.arrangeElements();
        FrameLayout.alignInRectangle($$02, 0, this.height / 6 - 12, this.width, this.height, 0.5f, 0.0f);
        $$02.visitWidgets((Consumer<AbstractWidget>)((Consumer)this::addRenderableWidget));
    }

    private LayoutElement createOnlineButton() {
        if (this.minecraft.level != null && this.minecraft.hasSingleplayerServer()) {
            this.difficultyButton = OptionsScreen.createDifficultyButton(0, 0, "options.difficulty", this.minecraft);
            if (!this.minecraft.level.getLevelData().isHardcore()) {
                this.lockButton = new LockIconButton(0, 0, $$0 -> this.minecraft.setScreen(new ConfirmScreen(this::lockCallback, Component.translatable("difficulty.lock.title"), Component.translatable("difficulty.lock.question", this.minecraft.level.getLevelData().getDifficulty().getDisplayName()))));
                this.difficultyButton.setWidth(this.difficultyButton.getWidth() - this.lockButton.getWidth());
                this.lockButton.setLocked(this.minecraft.level.getLevelData().isDifficultyLocked());
                this.lockButton.active = !this.lockButton.isLocked();
                this.difficultyButton.active = !this.lockButton.isLocked();
                LinearLayout $$02 = new LinearLayout(150, 0, LinearLayout.Orientation.HORIZONTAL);
                $$02.addChild(this.difficultyButton);
                $$02.addChild(this.lockButton);
                return $$02;
            }
            this.difficultyButton.active = false;
            return this.difficultyButton;
        }
        return Button.builder(Component.translatable("options.online"), $$0 -> this.minecraft.setScreen(OnlineOptionsScreen.createOnlineOptionsScreen(this.minecraft, this, this.options))).bounds(this.width / 2 + 5, this.height / 6 - 12 + 24, 150, 20).build();
    }

    public static CycleButton<Difficulty> createDifficultyButton(int $$0, int $$12, String $$22, Minecraft $$3) {
        return CycleButton.builder(Difficulty::getDisplayName).withValues(Difficulty.values()).withInitialValue($$3.level.getDifficulty()).create($$0, $$12, 150, 20, Component.translatable($$22), ($$1, $$2) -> $$3.getConnection().send(new ServerboundChangeDifficultyPacket((Difficulty)$$2)));
    }

    private void updatePackList(PackRepository $$0) {
        ImmutableList $$1 = ImmutableList.copyOf(this.options.resourcePacks);
        this.options.resourcePacks.clear();
        this.options.incompatibleResourcePacks.clear();
        for (Pack $$2 : $$0.getSelectedPacks()) {
            if ($$2.isFixedPosition()) continue;
            this.options.resourcePacks.add((Object)$$2.getId());
            if ($$2.getCompatibility().isCompatible()) continue;
            this.options.incompatibleResourcePacks.add((Object)$$2.getId());
        }
        this.options.save();
        ImmutableList $$3 = ImmutableList.copyOf(this.options.resourcePacks);
        if (!$$3.equals((Object)$$1)) {
            this.minecraft.reloadResourcePacks();
        }
    }

    private void lockCallback(boolean $$0) {
        this.minecraft.setScreen(this);
        if ($$0 && this.minecraft.level != null) {
            this.minecraft.getConnection().send(new ServerboundLockDifficultyPacket(true));
            this.lockButton.setLocked(true);
            this.lockButton.active = false;
            this.difficultyButton.active = false;
        }
    }

    @Override
    public void removed() {
        this.options.save();
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderBackground($$0);
        OptionsScreen.drawCenteredString($$0, this.font, this.title, this.width / 2, 15, 0xFFFFFF);
        super.render($$0, $$1, $$2, $$3);
    }

    private Button openScreenButton(Component $$0, Supplier<Screen> $$12) {
        return Button.builder($$0, $$1 -> this.minecraft.setScreen((Screen)$$12.get())).build();
    }
}