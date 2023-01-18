/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.io.IOException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.Throwable
 *  java.util.function.Consumer
 *  java.util.function.Function
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.screens.worldselection;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.FileUtil;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import org.slf4j.Logger;

public class SelectWorldScreen
extends Screen {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final WorldOptions TEST_OPTIONS = new WorldOptions("test1".hashCode(), true, false);
    protected final Screen lastScreen;
    private Button deleteButton;
    private Button selectButton;
    private Button renameButton;
    private Button copyButton;
    protected EditBox searchBox;
    private WorldSelectionList list;

    public SelectWorldScreen(Screen $$0) {
        super(Component.translatable("selectWorld.title"));
        this.lastScreen = $$0;
    }

    @Override
    public boolean mouseScrolled(double $$0, double $$1, double $$2) {
        return super.mouseScrolled($$0, $$1, $$2);
    }

    @Override
    public void tick() {
        this.searchBox.tick();
    }

    @Override
    protected void init() {
        this.searchBox = new EditBox(this.font, this.width / 2 - 100, 22, 200, 20, this.searchBox, Component.translatable("selectWorld.search"));
        this.searchBox.setResponder((Consumer<String>)((Consumer)$$0 -> this.list.updateFilter((String)$$0)));
        this.list = new WorldSelectionList(this, this.minecraft, this.width, this.height, 48, this.height - 64, 36, this.searchBox.getValue(), this.list);
        this.addWidget(this.searchBox);
        this.addWidget(this.list);
        this.selectButton = this.addRenderableWidget(Button.builder(Component.translatable("selectWorld.select"), $$0 -> this.list.getSelectedOpt().ifPresent(WorldSelectionList.WorldListEntry::joinWorld)).bounds(this.width / 2 - 154, this.height - 52, 150, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("selectWorld.create"), $$0 -> CreateWorldScreen.openFresh(this.minecraft, this)).bounds(this.width / 2 + 4, this.height - 52, 150, 20).build());
        this.renameButton = this.addRenderableWidget(Button.builder(Component.translatable("selectWorld.edit"), $$0 -> this.list.getSelectedOpt().ifPresent(WorldSelectionList.WorldListEntry::editWorld)).bounds(this.width / 2 - 154, this.height - 28, 72, 20).build());
        this.deleteButton = this.addRenderableWidget(Button.builder(Component.translatable("selectWorld.delete"), $$0 -> this.list.getSelectedOpt().ifPresent(WorldSelectionList.WorldListEntry::deleteWorld)).bounds(this.width / 2 - 76, this.height - 28, 72, 20).build());
        this.copyButton = this.addRenderableWidget(Button.builder(Component.translatable("selectWorld.recreate"), $$0 -> this.list.getSelectedOpt().ifPresent(WorldSelectionList.WorldListEntry::recreateWorld)).bounds(this.width / 2 + 4, this.height - 28, 72, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, $$0 -> this.minecraft.setScreen(this.lastScreen)).bounds(this.width / 2 + 82, this.height - 28, 72, 20).build());
        this.updateButtonStatus(false);
        this.setInitialFocus(this.searchBox);
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if (super.keyPressed($$0, $$1, $$2)) {
            return true;
        }
        return this.searchBox.keyPressed($$0, $$1, $$2);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }

    @Override
    public boolean charTyped(char $$0, int $$1) {
        return this.searchBox.charTyped($$0, $$1);
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.list.render($$0, $$1, $$2, $$3);
        this.searchBox.render($$0, $$1, $$2, $$3);
        SelectWorldScreen.drawCenteredString($$0, this.font, this.title, this.width / 2, 8, 0xFFFFFF);
        super.render($$0, $$1, $$2, $$3);
    }

    public void updateButtonStatus(boolean $$0) {
        this.selectButton.active = $$0;
        this.deleteButton.active = $$0;
        this.renameButton.active = $$0;
        this.copyButton.active = $$0;
    }

    @Override
    public void removed() {
        if (this.list != null) {
            this.list.children().forEach(WorldSelectionList.Entry::close);
        }
    }

    private /* synthetic */ void lambda$init$7(Button $$0) {
        try {
            WorldSelectionList.WorldListEntry $$3;
            WorldSelectionList.Entry $$2;
            String $$1 = "DEBUG world";
            if (!this.list.children().isEmpty() && ($$2 = (WorldSelectionList.Entry)this.list.children().get(0)) instanceof WorldSelectionList.WorldListEntry && ($$3 = (WorldSelectionList.WorldListEntry)$$2).getLevelName().equals((Object)"DEBUG world")) {
                $$3.doDeleteWorld();
            }
            LevelSettings $$4 = new LevelSettings("DEBUG world", GameType.SPECTATOR, false, Difficulty.NORMAL, true, new GameRules(), WorldDataConfiguration.DEFAULT);
            String $$5 = FileUtil.findAvailableName(this.minecraft.getLevelSource().getBaseDir(), "DEBUG world", "");
            this.minecraft.createWorldOpenFlows().createFreshLevel($$5, $$4, TEST_OPTIONS, (Function<RegistryAccess, WorldDimensions>)((Function)WorldPresets::createNormalWorldDimensions));
        }
        catch (IOException $$6) {
            LOGGER.error("Failed to recreate the debug world", (Throwable)$$6);
        }
    }
}