/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.JsonOps
 *  com.mojang.serialization.Lifecycle
 *  java.io.IOException
 *  java.io.UncheckedIOException
 *  java.lang.Boolean
 *  java.lang.Exception
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.RuntimeException
 *  java.lang.String
 *  java.lang.Throwable
 *  java.nio.file.FileVisitOption
 *  java.nio.file.Files
 *  java.nio.file.Path
 *  java.nio.file.attribute.FileAttribute
 *  java.util.Collection
 *  java.util.Comparator
 *  java.util.List
 *  java.util.Optional
 *  java.util.OptionalLong
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.Executor
 *  java.util.function.Consumer
 *  java.util.function.Function
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.mutable.MutableObject
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.screens.worldselection;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.FileUtil;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.tabs.GridLayoutTab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.client.gui.screens.worldselection.ConfirmExperimentalFeaturesScreen;
import net.minecraft.client.gui.screens.worldselection.EditGameRulesScreen;
import net.minecraft.client.gui.screens.worldselection.PresetEditor;
import net.minecraft.client.gui.screens.worldselection.SwitchGrid;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.client.gui.screens.worldselection.WorldOpenFlows;
import net.minecraft.commands.Commands;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PrimaryLevelData;
import org.apache.commons.lang3.mutable.MutableObject;
import org.slf4j.Logger;

public class CreateWorldScreen
extends Screen {
    private static final int GROUP_BOTTOM = 1;
    private static final int TAB_COLUMN_WIDTH = 210;
    private static final int FOOTER_HEIGHT = 36;
    private static final int TEXT_INDENT = 1;
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String TEMP_WORLD_PREFIX = "mcworld-";
    static final Component GAME_MODEL_LABEL = Component.translatable("selectWorld.gameMode");
    static final Component NAME_LABEL = Component.translatable("selectWorld.enterName");
    static final Component ALLOW_CHEATS_INFO = Component.translatable("selectWorld.allowCommands.info");
    private static final Component PREPARING_WORLD_DATA = Component.translatable("createWorld.preparing");
    private static final int HORIZONTAL_BUTTON_SPACING = 10;
    private static final int VERTICAL_BUTTON_SPACING = 8;
    final WorldCreationUiState uiState;
    private final TabManager tabManager = new TabManager((Consumer<AbstractWidget>)((Consumer)this::addRenderableWidget), (Consumer<AbstractWidget>)((Consumer)$$1 -> this.removeWidget((GuiEventListener)$$1)));
    @Nullable
    private final Screen lastScreen;
    @Nullable
    private String resultFolder;
    @Nullable
    private Path tempDataPackDir;
    @Nullable
    private PackRepository tempDataPackRepository;
    @Nullable
    private GridLayout bottomButtons;
    @Nullable
    private TabNavigationBar tabNavigationBar;

    public static void openFresh(Minecraft $$02, @Nullable Screen $$12) {
        CreateWorldScreen.queueLoadScreen($$02, PREPARING_WORLD_DATA);
        PackRepository $$22 = new PackRepository(new ServerPacksSource());
        WorldLoader.InitConfig $$32 = CreateWorldScreen.createDefaultLoadConfig($$22, WorldDataConfiguration.DEFAULT);
        CompletableFuture<WorldCreationContext> $$4 = WorldLoader.load($$32, $$0 -> new WorldLoader.DataLoadOutput<DataPackReloadCookie>(new DataPackReloadCookie(new WorldGenSettings(WorldOptions.defaultWithRandomSeed(), WorldPresets.createNormalWorldDimensions($$0.datapackWorldgen())), $$0.dataConfiguration()), $$0.datapackDimensions()), ($$0, $$1, $$2, $$3) -> {
            $$0.close();
            return new WorldCreationContext($$3.worldGenSettings(), $$2, $$1, $$3.dataConfiguration());
        }, (Executor)Util.backgroundExecutor(), $$02);
        $$02.managedBlock(() -> $$4.isDone());
        $$02.setScreen(new CreateWorldScreen($$12, (WorldCreationContext)((Object)$$4.join()), (Optional<ResourceKey<WorldPreset>>)Optional.of(WorldPresets.NORMAL), OptionalLong.empty()));
    }

    public static CreateWorldScreen createFromExisting(@Nullable Screen $$0, LevelSettings $$1, WorldCreationContext $$2, @Nullable Path $$3) {
        CreateWorldScreen $$4 = new CreateWorldScreen($$0, $$2, WorldPresets.fromSettings($$2.selectedDimensions().dimensions()), OptionalLong.of((long)$$2.options().seed()));
        $$4.uiState.setName($$1.levelName());
        $$4.uiState.setAllowCheats($$1.allowCommands());
        $$4.uiState.setDifficulty($$1.difficulty());
        $$4.uiState.getGameRules().assignFrom($$1.gameRules(), null);
        if ($$1.hardcore()) {
            $$4.uiState.setGameMode(WorldCreationUiState.SelectedGameMode.HARDCORE);
        } else if ($$1.gameType().isSurvival()) {
            $$4.uiState.setGameMode(WorldCreationUiState.SelectedGameMode.SURVIVAL);
        } else if ($$1.gameType().isCreative()) {
            $$4.uiState.setGameMode(WorldCreationUiState.SelectedGameMode.CREATIVE);
        }
        $$4.tempDataPackDir = $$3;
        return $$4;
    }

    private CreateWorldScreen(@Nullable Screen $$0, WorldCreationContext $$12, Optional<ResourceKey<WorldPreset>> $$2, OptionalLong $$3) {
        super(Component.translatable("selectWorld.create"));
        this.lastScreen = $$0;
        this.uiState = new WorldCreationUiState($$12, $$2, $$3);
    }

    public WorldCreationUiState getUiState() {
        return this.uiState;
    }

    @Override
    public void tick() {
        this.tabManager.tickCurrent();
    }

    @Override
    protected void init() {
        this.updateResultFolder(this.uiState.getName());
        this.tabNavigationBar = TabNavigationBar.builder(this.tabManager, this.width).addTabs(new GameTab(), new WorldTab(), new MoreTab()).build();
        this.tabNavigationBar.visitWidgets((Consumer<AbstractWidget>)((Consumer)this::addRenderableWidget));
        this.uiState.addListener((Consumer<WorldCreationUiState>)((Consumer)$$0 -> {
            if (!$$0.nameChanged()) {
                return;
            }
            this.updateResultFolder($$0.getName());
        }));
        this.bottomButtons = new GridLayout().columnSpacing(10);
        GridLayout.RowHelper $$02 = this.bottomButtons.createRowHelper(2);
        Button $$12 = $$02.addChild(Button.builder(Component.translatable("selectWorld.create"), $$0 -> this.onCreate()).build());
        this.uiState.addListener((Consumer<WorldCreationUiState>)((Consumer)$$1 -> {
            $$0.active = !this.uiState.getName().isEmpty();
        }));
        $$02.addChild(Button.builder(CommonComponents.GUI_CANCEL, $$0 -> this.popScreen()).build());
        this.bottomButtons.visitWidgets((Consumer<AbstractWidget>)((Consumer)$$0 -> {
            $$0.setTabOrderGroup(1);
            this.addRenderableWidget($$0);
        }));
        this.tabNavigationBar.setInitialTab(0);
        this.uiState.onChanged();
        this.repositionElements();
    }

    @Override
    public void repositionElements() {
        if (this.tabNavigationBar == null || this.bottomButtons == null) {
            return;
        }
        this.tabNavigationBar.setWidth(this.width);
        this.tabNavigationBar.arrangeElements();
        this.bottomButtons.arrangeElements();
        FrameLayout.centerInRectangle(this.bottomButtons, 0, this.height - 36, this.width, 36);
        int $$0 = this.tabNavigationBar.getY() + this.tabNavigationBar.getHeight();
        ScreenRectangle $$1 = new ScreenRectangle(0, $$0, this.width, this.bottomButtons.getY() - $$0);
        this.tabManager.setTabArea($$1);
    }

    private void updateResultFolder(String $$0) {
        this.resultFolder = $$0.trim();
        if (this.resultFolder.isEmpty()) {
            this.resultFolder = "World";
        }
        try {
            this.resultFolder = FileUtil.findAvailableName(this.minecraft.getLevelSource().getBaseDir(), this.resultFolder, "");
        }
        catch (Exception $$1) {
            this.resultFolder = "World";
            try {
                this.resultFolder = FileUtil.findAvailableName(this.minecraft.getLevelSource().getBaseDir(), this.resultFolder, "");
            }
            catch (Exception $$2) {
                throw new RuntimeException("Could not create save folder", (Throwable)$$2);
            }
        }
    }

    private static void queueLoadScreen(Minecraft $$0, Component $$1) {
        $$0.forceSetScreen(new GenericDirtMessageScreen($$1));
    }

    private void onCreate() {
        WorldCreationContext $$0 = this.uiState.getSettings();
        WorldDimensions.Complete $$1 = $$0.selectedDimensions().bake($$0.datapackDimensions());
        LayeredRegistryAccess<RegistryLayer> $$2 = $$0.worldgenRegistries().replaceFrom(RegistryLayer.DIMENSIONS, $$1.dimensionsRegistryAccess());
        Lifecycle $$3 = FeatureFlags.isExperimental($$0.dataConfiguration().enabledFeatures()) ? Lifecycle.experimental() : Lifecycle.stable();
        Lifecycle $$4 = $$2.compositeAccess().allRegistriesLifecycle();
        Lifecycle $$5 = $$4.add($$3);
        WorldOpenFlows.confirmWorldCreation(this.minecraft, this, $$5, () -> this.createNewWorld($$1.specialWorldProperty(), $$2, $$5));
    }

    private void createNewWorld(PrimaryLevelData.SpecialWorldProperty $$0, LayeredRegistryAccess<RegistryLayer> $$1, Lifecycle $$2) {
        CreateWorldScreen.queueLoadScreen(this.minecraft, PREPARING_WORLD_DATA);
        Optional<LevelStorageSource.LevelStorageAccess> $$3 = this.createNewWorldDirectory();
        if ($$3.isEmpty()) {
            return;
        }
        this.removeTempDataPackDir();
        boolean $$4 = $$0 == PrimaryLevelData.SpecialWorldProperty.DEBUG;
        WorldCreationContext $$5 = this.uiState.getSettings();
        LevelSettings $$6 = this.createLevelSettings($$4);
        PrimaryLevelData $$7 = new PrimaryLevelData($$6, $$5.options(), $$0, $$2);
        this.minecraft.createWorldOpenFlows().createLevelFromExistingSettings((LevelStorageSource.LevelStorageAccess)$$3.get(), $$5.dataPackResources(), $$1, $$7);
    }

    private LevelSettings createLevelSettings(boolean $$0) {
        String $$1 = this.uiState.getName().trim();
        if ($$0) {
            GameRules $$2 = new GameRules();
            $$2.getRule(GameRules.RULE_DAYLIGHT).set(false, null);
            return new LevelSettings($$1, GameType.SPECTATOR, false, Difficulty.PEACEFUL, true, $$2, WorldDataConfiguration.DEFAULT);
        }
        return new LevelSettings($$1, this.uiState.getGameMode().gameType, this.uiState.isHardcore(), this.uiState.getDifficulty(), this.uiState.isAllowCheats(), this.uiState.getGameRules(), this.uiState.getSettings().dataConfiguration());
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if (super.keyPressed($$0, $$1, $$2)) {
            return true;
        }
        if ($$0 == 257 || $$0 == 335) {
            this.onCreate();
            return true;
        }
        return false;
    }

    @Override
    public void onClose() {
        this.popScreen();
    }

    public void popScreen() {
        this.minecraft.setScreen(this.lastScreen);
        this.removeTempDataPackDir();
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderBackground($$0);
        super.render($$0, $$1, $$2, $$3);
    }

    @Override
    protected <T extends GuiEventListener & NarratableEntry> T addWidget(T $$0) {
        return super.addWidget($$0);
    }

    @Override
    protected <T extends GuiEventListener & Renderable> T addRenderableWidget(T $$0) {
        return super.addRenderableWidget($$0);
    }

    @Nullable
    private Path getTempDataPackDir() {
        if (this.tempDataPackDir == null) {
            try {
                this.tempDataPackDir = Files.createTempDirectory((String)TEMP_WORLD_PREFIX, (FileAttribute[])new FileAttribute[0]);
            }
            catch (IOException $$0) {
                LOGGER.warn("Failed to create temporary dir", (Throwable)$$0);
                SystemToast.onPackCopyFailure(this.minecraft, this.resultFolder);
                this.popScreen();
            }
        }
        return this.tempDataPackDir;
    }

    void openDataPackSelectionScreen(WorldDataConfiguration $$0) {
        Pair<Path, PackRepository> $$1 = this.getDataPackSelectionSettings($$0);
        if ($$1 != null) {
            this.minecraft.setScreen(new PackSelectionScreen(this, (PackRepository)$$1.getSecond(), (Consumer<PackRepository>)((Consumer)this::tryApplyNewDataPacks), (Path)$$1.getFirst(), Component.translatable("dataPack.title")));
        }
    }

    private void tryApplyNewDataPacks(PackRepository $$0) {
        List $$2;
        ImmutableList $$1 = ImmutableList.copyOf($$0.getSelectedIds());
        WorldDataConfiguration $$3 = new WorldDataConfiguration(new DataPackConfig((List<String>)$$1, (List<String>)($$2 = (List)$$0.getAvailableIds().stream().filter(arg_0 -> CreateWorldScreen.lambda$tryApplyNewDataPacks$9((List)$$1, arg_0)).collect(ImmutableList.toImmutableList()))), this.uiState.getSettings().dataConfiguration().enabledFeatures());
        if (this.uiState.tryUpdateDataConfiguration($$3)) {
            return;
        }
        FeatureFlagSet $$4 = $$0.getRequestedFeatureFlags();
        if (FeatureFlags.isExperimental($$4)) {
            this.minecraft.tell(() -> this.minecraft.setScreen(new ConfirmExperimentalFeaturesScreen($$0.getSelectedPacks(), $$2 -> {
                if ($$2) {
                    this.applyNewPackConfig($$0, $$3);
                } else {
                    this.openDataPackSelectionScreen(this.uiState.getSettings().dataConfiguration());
                }
            })));
        } else {
            this.applyNewPackConfig($$0, $$3);
        }
    }

    private void applyNewPackConfig(PackRepository $$02, WorldDataConfiguration $$12) {
        this.minecraft.tell(() -> this.minecraft.setScreen(new GenericDirtMessageScreen(Component.translatable("dataPack.validation.working"))));
        WorldLoader.InitConfig $$22 = CreateWorldScreen.createDefaultLoadConfig($$02, $$12);
        WorldLoader.load($$22, $$0 -> {
            if ($$0.datapackWorldgen().registryOrThrow(Registries.WORLD_PRESET).size() == 0) {
                throw new IllegalStateException("Needs at least one world preset to continue");
            }
            if ($$0.datapackWorldgen().registryOrThrow(Registries.BIOME).size() == 0) {
                throw new IllegalStateException("Needs at least one biome continue");
            }
            WorldCreationContext $$12 = this.uiState.getSettings();
            RegistryOps $$2 = RegistryOps.create(JsonOps.INSTANCE, $$12.worldgenLoadContext());
            DataResult $$3 = WorldGenSettings.encode($$2, $$12.options(), $$12.selectedDimensions()).setLifecycle(Lifecycle.stable());
            RegistryOps $$4 = RegistryOps.create(JsonOps.INSTANCE, $$0.datapackWorldgen());
            WorldGenSettings $$5 = (WorldGenSettings)((Object)((Object)$$3.flatMap($$1 -> WorldGenSettings.CODEC.parse($$4, $$1)).getOrThrow(false, Util.prefix("Error parsing worldgen settings after loading data packs: ", (Consumer<String>)((Consumer)arg_0 -> ((Logger)LOGGER).error(arg_0))))));
            return new WorldLoader.DataLoadOutput<DataPackReloadCookie>(new DataPackReloadCookie($$5, $$0.dataConfiguration()), $$0.datapackDimensions());
        }, ($$0, $$1, $$2, $$3) -> {
            $$0.close();
            return new WorldCreationContext($$3.worldGenSettings(), $$2, $$1, $$3.dataConfiguration());
        }, (Executor)Util.backgroundExecutor(), this.minecraft).thenAcceptAsync(this.uiState::setSettings, (Executor)this.minecraft).handle(($$0, $$1) -> {
            if ($$1 != null) {
                LOGGER.warn("Failed to validate datapack", $$1);
                this.minecraft.tell(() -> this.minecraft.setScreen(new ConfirmScreen($$0 -> {
                    if ($$0) {
                        this.openDataPackSelectionScreen(this.uiState.getSettings().dataConfiguration());
                    } else {
                        this.openDataPackSelectionScreen(WorldDataConfiguration.DEFAULT);
                    }
                }, Component.translatable("dataPack.validation.failed"), CommonComponents.EMPTY, Component.translatable("dataPack.validation.back"), Component.translatable("dataPack.validation.reset"))));
            } else {
                this.minecraft.tell(() -> this.minecraft.setScreen(this));
            }
            return null;
        });
    }

    private static WorldLoader.InitConfig createDefaultLoadConfig(PackRepository $$0, WorldDataConfiguration $$1) {
        WorldLoader.PackConfig $$2 = new WorldLoader.PackConfig($$0, $$1, false, true);
        return new WorldLoader.InitConfig($$2, Commands.CommandSelection.INTEGRATED, 2);
    }

    private void removeTempDataPackDir() {
        if (this.tempDataPackDir != null) {
            try (Stream $$02 = Files.walk((Path)this.tempDataPackDir, (FileVisitOption[])new FileVisitOption[0]);){
                $$02.sorted(Comparator.reverseOrder()).forEach($$0 -> {
                    try {
                        Files.delete((Path)$$0);
                    }
                    catch (IOException $$1) {
                        LOGGER.warn("Failed to remove temporary file {}", $$0, (Object)$$1);
                    }
                });
            }
            catch (IOException $$1) {
                LOGGER.warn("Failed to list temporary dir {}", (Object)this.tempDataPackDir);
            }
            this.tempDataPackDir = null;
        }
    }

    private static void copyBetweenDirs(Path $$0, Path $$1, Path $$2) {
        try {
            Util.copyBetweenDirs($$0, $$1, $$2);
        }
        catch (IOException $$3) {
            LOGGER.warn("Failed to copy datapack file from {} to {}", (Object)$$2, (Object)$$1);
            throw new UncheckedIOException($$3);
        }
    }

    private Optional<LevelStorageSource.LevelStorageAccess> createNewWorldDirectory() {
        Optional optional;
        block12: {
            LevelStorageSource.LevelStorageAccess $$02;
            block11: {
                $$02 = this.minecraft.getLevelSource().createAccess(this.resultFolder);
                if (this.tempDataPackDir != null) break block11;
                return Optional.of((Object)$$02);
            }
            Stream $$12 = Files.walk((Path)this.tempDataPackDir, (FileVisitOption[])new FileVisitOption[0]);
            try {
                Path $$2 = $$02.getLevelPath(LevelResource.DATAPACK_DIR);
                FileUtil.createDirectoriesSafe($$2);
                $$12.filter($$0 -> !$$0.equals((Object)this.tempDataPackDir)).forEach($$1 -> CreateWorldScreen.copyBetweenDirs(this.tempDataPackDir, $$2, $$1));
                optional = Optional.of((Object)$$02);
                if ($$12 == null) break block12;
            }
            catch (Throwable throwable) {
                try {
                    try {
                        if ($$12 != null) {
                            try {
                                $$12.close();
                            }
                            catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        }
                        throw throwable;
                    }
                    catch (IOException | UncheckedIOException $$3) {
                        LOGGER.warn("Failed to copy datapacks to world {}", (Object)this.resultFolder, (Object)$$3);
                        $$02.close();
                    }
                }
                catch (IOException | UncheckedIOException $$4) {
                    LOGGER.warn("Failed to create access for {}", (Object)this.resultFolder, (Object)$$4);
                }
            }
            $$12.close();
        }
        return optional;
        SystemToast.onPackCopyFailure(this.minecraft, this.resultFolder);
        this.popScreen();
        return Optional.empty();
    }

    @Nullable
    public static Path createTempDataPackDirFromExistingWorld(Path $$0, Minecraft $$12) {
        MutableObject $$22 = new MutableObject();
        try (Stream $$3 = Files.walk((Path)$$0, (FileVisitOption[])new FileVisitOption[0]);){
            $$3.filter($$1 -> !$$1.equals((Object)$$0)).forEach($$2 -> {
                Path $$3 = (Path)$$22.getValue();
                if ($$3 == null) {
                    try {
                        $$3 = Files.createTempDirectory((String)TEMP_WORLD_PREFIX, (FileAttribute[])new FileAttribute[0]);
                    }
                    catch (IOException $$4) {
                        LOGGER.warn("Failed to create temporary dir");
                        throw new UncheckedIOException($$4);
                    }
                    $$22.setValue((Object)$$3);
                }
                CreateWorldScreen.copyBetweenDirs($$0, $$3, $$2);
            });
        }
        catch (IOException | UncheckedIOException $$4) {
            LOGGER.warn("Failed to copy datapacks from world {}", (Object)$$0, (Object)$$4);
            SystemToast.onPackCopyFailure($$12, $$0.toString());
            return null;
        }
        return (Path)$$22.getValue();
    }

    @Nullable
    private Pair<Path, PackRepository> getDataPackSelectionSettings(WorldDataConfiguration $$0) {
        Path $$1 = this.getTempDataPackDir();
        if ($$1 != null) {
            if (this.tempDataPackRepository == null) {
                this.tempDataPackRepository = ServerPacksSource.createPackRepository($$1);
                this.tempDataPackRepository.reload();
            }
            this.tempDataPackRepository.setSelected((Collection<String>)$$0.dataPacks().getEnabled());
            return Pair.of((Object)$$1, (Object)this.tempDataPackRepository);
        }
        return null;
    }

    private static /* synthetic */ boolean lambda$tryApplyNewDataPacks$9(List $$0, String $$1) {
        return !$$0.contains((Object)$$1);
    }

    class GameTab
    extends GridLayoutTab {
        private static final Component TITLE = Component.translatable("createWorld.tab.game.title");
        private static final Component ALLOW_CHEATS = Component.translatable("selectWorld.allowCommands");
        private final EditBox nameEdit;

        GameTab() {
            super(TITLE);
            GridLayout.RowHelper $$02 = this.layout.rowSpacing(8).createRowHelper(1);
            LayoutSettings $$12 = $$02.newCellSettings();
            GridLayout.RowHelper $$2 = new GridLayout().rowSpacing(4).createRowHelper(1);
            $$2.addChild(new StringWidget(NAME_LABEL, ((CreateWorldScreen)CreateWorldScreen.this).minecraft.font), $$2.newCellSettings().paddingLeft(1));
            this.nameEdit = $$2.addChild(new EditBox(CreateWorldScreen.this.font, 0, 0, 208, 20, Component.translatable("selectWorld.enterName")), $$2.newCellSettings().padding(1));
            this.nameEdit.setValue(CreateWorldScreen.this.uiState.getName());
            this.nameEdit.setResponder((Consumer<String>)((Consumer)CreateWorldScreen.this.uiState::setName));
            CreateWorldScreen.this.setInitialFocus(this.nameEdit);
            $$02.addChild($$2.getGrid(), $$02.newCellSettings().alignHorizontallyCenter());
            CycleButton<WorldCreationUiState.SelectedGameMode> $$3 = $$02.addChild(CycleButton.builder($$0 -> $$0.displayName).withValues(WorldCreationUiState.SelectedGameMode.SURVIVAL, WorldCreationUiState.SelectedGameMode.HARDCORE, WorldCreationUiState.SelectedGameMode.CREATIVE).create(0, 0, 210, 20, GAME_MODEL_LABEL, ($$0, $$1) -> CreateWorldScreen.this.uiState.setGameMode((WorldCreationUiState.SelectedGameMode)((Object)$$1))), $$12);
            CreateWorldScreen.this.uiState.addListener((Consumer<WorldCreationUiState>)((Consumer)$$1 -> {
                $$3.setValue($$1.getGameMode());
                $$0.active = !$$1.isDebug();
                $$3.setTooltip(Tooltip.create($$1.getGameMode().getInfo()));
            }));
            CycleButton<Difficulty> $$4 = $$02.addChild(CycleButton.builder(Difficulty::getDisplayName).withValues(Difficulty.values()).create(0, 0, 210, 20, Component.translatable("options.difficulty"), ($$0, $$1) -> CreateWorldScreen.this.uiState.setDifficulty((Difficulty)$$1)), $$12);
            CreateWorldScreen.this.uiState.addListener((Consumer<WorldCreationUiState>)((Consumer)$$1 -> {
                $$4.setValue(CreateWorldScreen.this.uiState.getDifficulty());
                $$0.active = !CreateWorldScreen.this.uiState.isHardcore();
                $$4.setTooltip(Tooltip.create(CreateWorldScreen.this.uiState.getDifficulty().getInfo()));
            }));
            CycleButton<Boolean> $$5 = $$02.addChild(CycleButton.onOffBuilder().withTooltip($$0 -> Tooltip.create(ALLOW_CHEATS_INFO)).create(0, 0, 210, 20, ALLOW_CHEATS, ($$0, $$1) -> CreateWorldScreen.this.uiState.setAllowCheats((boolean)$$1)));
            CreateWorldScreen.this.uiState.addListener((Consumer<WorldCreationUiState>)((Consumer)$$1 -> {
                $$5.setValue(CreateWorldScreen.this.uiState.isAllowCheats());
                $$0.active = !CreateWorldScreen.this.uiState.isDebug() && !CreateWorldScreen.this.uiState.isHardcore();
            }));
        }

        @Override
        public void tick() {
            this.nameEdit.tick();
        }
    }

    class WorldTab
    extends GridLayoutTab {
        private static final Component TITLE = Component.translatable("createWorld.tab.world.title");
        private static final Component AMPLIFIED_HELP_TEXT = Component.translatable("generator.minecraft.amplified.info");
        private static final Component GENERATE_STRUCTURES = Component.translatable("selectWorld.mapFeatures");
        private static final Component GENERATE_STRUCTURES_INFO = Component.translatable("selectWorld.mapFeatures.info");
        private static final Component BONUS_CHEST = Component.translatable("selectWorld.bonusItems");
        private static final Component SEED_LABEL = Component.translatable("selectWorld.enterSeed");
        static final Component SEED_EMPTY_HINT = Component.translatable("selectWorld.seedInfo").withStyle(ChatFormatting.DARK_GRAY);
        private static final int WORLD_TAB_WIDTH = 310;
        private final EditBox seedEdit;
        private final Button customizeTypeButton;

        WorldTab() {
            super(TITLE);
            GridLayout.RowHelper $$02 = this.layout.columnSpacing(10).rowSpacing(8).createRowHelper(2);
            CycleButton<WorldCreationUiState.WorldTypeEntry> $$12 = $$02.addChild(CycleButton.builder(WorldCreationUiState.WorldTypeEntry::describePreset).withValues(this.createWorldTypeValueSupplier()).withCustomNarration((Function<CycleButton<WorldCreationUiState.WorldTypeEntry>, MutableComponent>)((Function)WorldTab::createTypeButtonNarration)).create(0, 0, 150, 20, Component.translatable("selectWorld.mapType"), ($$0, $$1) -> CreateWorldScreen.this.uiState.setWorldType((WorldCreationUiState.WorldTypeEntry)((Object)$$1))));
            $$12.setValue(CreateWorldScreen.this.uiState.getWorldType());
            CreateWorldScreen.this.uiState.addListener((Consumer<WorldCreationUiState>)((Consumer)$$1 -> {
                WorldCreationUiState.WorldTypeEntry $$2 = $$1.getWorldType();
                $$12.setValue($$2);
                if ($$2.isAmplified()) {
                    $$12.setTooltip(Tooltip.create(AMPLIFIED_HELP_TEXT));
                } else {
                    $$12.setTooltip(null);
                }
                $$0.active = CreateWorldScreen.this.uiState.getWorldType().preset() != null;
            }));
            this.customizeTypeButton = $$02.addChild(Button.builder(Component.translatable("selectWorld.customizeType"), $$0 -> this.openPresetEditor()).build());
            CreateWorldScreen.this.uiState.addListener((Consumer<WorldCreationUiState>)((Consumer)$$0 -> {
                this.customizeTypeButton.active = !$$0.isDebug() && $$0.getPresetEditor() != null;
            }));
            GridLayout.RowHelper $$2 = new GridLayout().rowSpacing(4).createRowHelper(1);
            $$2.addChild(new StringWidget(SEED_LABEL, CreateWorldScreen.this.font).alignLeft());
            this.seedEdit = $$2.addChild(new EditBox(CreateWorldScreen.this.font, 0, 0, 308, 20, Component.translatable("selectWorld.enterSeed")){

                @Override
                protected MutableComponent createNarrationMessage() {
                    return super.createNarrationMessage().append(CommonComponents.NARRATION_SEPARATOR).append(SEED_EMPTY_HINT);
                }
            }, $$02.newCellSettings().padding(1));
            this.seedEdit.setHint(SEED_EMPTY_HINT);
            this.seedEdit.setValue(CreateWorldScreen.this.uiState.getSeed());
            this.seedEdit.setResponder((Consumer<String>)((Consumer)$$0 -> CreateWorldScreen.this.uiState.setSeed(this.seedEdit.getValue())));
            $$02.addChild($$2.getGrid(), 2);
            SwitchGrid.Builder $$3 = SwitchGrid.builder(310).withPaddingLeft(1);
            $$3.addSwitch(GENERATE_STRUCTURES, CreateWorldScreen.this.uiState::isGenerateStructures, (Consumer<Boolean>)((Consumer)CreateWorldScreen.this.uiState::setGenerateStructures)).withIsActiveCondition(() -> !CreateWorldScreen.this.uiState.isDebug()).withInfo(GENERATE_STRUCTURES_INFO);
            $$3.addSwitch(BONUS_CHEST, CreateWorldScreen.this.uiState::isBonusChest, (Consumer<Boolean>)((Consumer)CreateWorldScreen.this.uiState::setBonusChest)).withIsActiveCondition(() -> !CreateWorldScreen.this.uiState.isHardcore() && !CreateWorldScreen.this.uiState.isDebug());
            SwitchGrid $$4 = $$3.build((Consumer<LayoutElement>)((Consumer)$$1 -> $$02.addChild($$1, 2)));
            CreateWorldScreen.this.uiState.addListener((Consumer<WorldCreationUiState>)((Consumer)$$1 -> $$4.refreshStates()));
        }

        private void openPresetEditor() {
            PresetEditor $$0 = CreateWorldScreen.this.uiState.getPresetEditor();
            if ($$0 != null) {
                CreateWorldScreen.this.minecraft.setScreen($$0.createEditScreen(CreateWorldScreen.this, CreateWorldScreen.this.uiState.getSettings()));
            }
        }

        private CycleButton.ValueListSupplier<WorldCreationUiState.WorldTypeEntry> createWorldTypeValueSupplier() {
            return new CycleButton.ValueListSupplier<WorldCreationUiState.WorldTypeEntry>(){

                @Override
                public List<WorldCreationUiState.WorldTypeEntry> getSelectedList() {
                    return CycleButton.DEFAULT_ALT_LIST_SELECTOR.getAsBoolean() ? CreateWorldScreen.this.uiState.getAltPresetList() : CreateWorldScreen.this.uiState.getNormalPresetList();
                }

                @Override
                public List<WorldCreationUiState.WorldTypeEntry> getDefaultList() {
                    return CreateWorldScreen.this.uiState.getNormalPresetList();
                }
            };
        }

        private static MutableComponent createTypeButtonNarration(CycleButton<WorldCreationUiState.WorldTypeEntry> $$0) {
            if ($$0.getValue().isAmplified()) {
                return CommonComponents.joinForNarration($$0.createDefaultNarrationMessage(), AMPLIFIED_HELP_TEXT);
            }
            return $$0.createDefaultNarrationMessage();
        }

        @Override
        public void tick() {
            this.seedEdit.tick();
        }
    }

    class MoreTab
    extends GridLayoutTab {
        private static final Component TITLE = Component.translatable("createWorld.tab.more.title");
        private static final Component GAME_RULES_LABEL = Component.translatable("selectWorld.gameRules");
        private static final Component DATA_PACKS_LABEL = Component.translatable("selectWorld.dataPacks");

        MoreTab() {
            super(TITLE);
            GridLayout.RowHelper $$02 = this.layout.rowSpacing(8).createRowHelper(1);
            $$02.addChild(Button.builder(GAME_RULES_LABEL, $$0 -> this.openGameRulesScreen()).width(210).build());
            $$02.addChild(Button.builder(DATA_PACKS_LABEL, $$0 -> CreateWorldScreen.this.openDataPackSelectionScreen(CreateWorldScreen.this.uiState.getSettings().dataConfiguration())).width(210).build());
        }

        private void openGameRulesScreen() {
            CreateWorldScreen.this.minecraft.setScreen(new EditGameRulesScreen(CreateWorldScreen.this.uiState.getGameRules().copy(), (Consumer<Optional<GameRules>>)((Consumer)$$0 -> {
                CreateWorldScreen.this.minecraft.setScreen(CreateWorldScreen.this);
                $$0.ifPresent(CreateWorldScreen.this.uiState::setGameRules);
            })));
        }
    }

    record DataPackReloadCookie(WorldGenSettings worldGenSettings, WorldDataConfiguration dataConfiguration) {
    }
}