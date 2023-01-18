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
import net.minecraft.FileUtil;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.client.gui.screens.worldselection.ConfirmExperimentalFeaturesScreen;
import net.minecraft.client.gui.screens.worldselection.EditGameRulesScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.client.gui.screens.worldselection.WorldGenSettingsComponent;
import net.minecraft.client.gui.screens.worldselection.WorldOpenFlows;
import net.minecraft.client.resources.language.I18n;
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
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String TEMP_WORLD_PREFIX = "mcworld-";
    private static final Component GAME_MODEL_LABEL = Component.translatable("selectWorld.gameMode");
    private static final Component SEED_LABEL = Component.translatable("selectWorld.enterSeed");
    private static final Component SEED_INFO = Component.translatable("selectWorld.seedInfo");
    private static final Component NAME_LABEL = Component.translatable("selectWorld.enterName");
    private static final Component OUTPUT_DIR_INFO = Component.translatable("selectWorld.resultFolder");
    private static final Component COMMANDS_INFO = Component.translatable("selectWorld.allowCommands.info");
    private static final Component PREPARING_WORLD_DATA = Component.translatable("createWorld.preparing");
    @Nullable
    private final Screen lastScreen;
    private EditBox nameEdit;
    String resultFolder;
    private SelectedGameMode gameMode = SelectedGameMode.SURVIVAL;
    @Nullable
    private SelectedGameMode oldGameMode;
    private Difficulty difficulty = Difficulty.NORMAL;
    private boolean commands;
    private boolean commandsChanged;
    public boolean hardCore;
    protected WorldDataConfiguration dataConfiguration;
    @Nullable
    private Path tempDataPackDir;
    @Nullable
    private PackRepository tempDataPackRepository;
    private boolean worldGenSettingsVisible;
    private Button createButton;
    private CycleButton<SelectedGameMode> modeButton;
    private CycleButton<Difficulty> difficultyButton;
    private Button moreOptionsButton;
    private Button gameRulesButton;
    private Button dataPacksButton;
    private CycleButton<Boolean> commandsButton;
    private Component gameModeHelp1;
    private Component gameModeHelp2;
    private String initName;
    private GameRules gameRules = new GameRules();
    public final WorldGenSettingsComponent worldGenSettingsComponent;

    public static void openFresh(Minecraft $$02, @Nullable Screen $$12) {
        CreateWorldScreen.queueLoadScreen($$02, PREPARING_WORLD_DATA);
        PackRepository $$22 = new PackRepository(new ServerPacksSource());
        WorldLoader.InitConfig $$32 = CreateWorldScreen.createDefaultLoadConfig($$22, WorldDataConfiguration.DEFAULT);
        CompletableFuture<WorldCreationContext> $$4 = WorldLoader.load($$32, $$0 -> new WorldLoader.DataLoadOutput<DataPackReloadCookie>(new DataPackReloadCookie(new WorldGenSettings(WorldOptions.defaultWithRandomSeed(), WorldPresets.createNormalWorldDimensions($$0.datapackWorldgen())), $$0.dataConfiguration()), $$0.datapackDimensions()), ($$0, $$1, $$2, $$3) -> {
            $$0.close();
            return new WorldCreationContext($$3.worldGenSettings(), $$2, $$1, $$3.dataConfiguration());
        }, (Executor)Util.backgroundExecutor(), $$02);
        $$02.managedBlock(() -> $$4.isDone());
        $$02.setScreen(new CreateWorldScreen($$12, WorldDataConfiguration.DEFAULT, new WorldGenSettingsComponent((WorldCreationContext)((Object)$$4.join()), (Optional<ResourceKey<WorldPreset>>)Optional.of(WorldPresets.NORMAL), OptionalLong.empty())));
    }

    public static CreateWorldScreen createFromExisting(@Nullable Screen $$0, LevelSettings $$1, WorldCreationContext $$2, @Nullable Path $$3) {
        CreateWorldScreen $$4 = new CreateWorldScreen($$0, $$2.dataConfiguration(), new WorldGenSettingsComponent($$2, WorldPresets.fromSettings($$2.selectedDimensions().dimensions()), OptionalLong.of((long)$$2.options().seed())));
        $$4.initName = $$1.levelName();
        $$4.commands = $$1.allowCommands();
        $$4.commandsChanged = true;
        $$4.difficulty = $$1.difficulty();
        $$4.gameRules.assignFrom($$1.gameRules(), null);
        if ($$1.hardcore()) {
            $$4.gameMode = SelectedGameMode.HARDCORE;
        } else if ($$1.gameType().isSurvival()) {
            $$4.gameMode = SelectedGameMode.SURVIVAL;
        } else if ($$1.gameType().isCreative()) {
            $$4.gameMode = SelectedGameMode.CREATIVE;
        }
        $$4.tempDataPackDir = $$3;
        return $$4;
    }

    private CreateWorldScreen(@Nullable Screen $$0, WorldDataConfiguration $$1, WorldGenSettingsComponent $$2) {
        super(Component.translatable("selectWorld.create"));
        this.lastScreen = $$0;
        this.initName = I18n.get("selectWorld.newWorld", new Object[0]);
        this.dataConfiguration = $$1;
        this.worldGenSettingsComponent = $$2;
    }

    @Override
    public void tick() {
        this.nameEdit.tick();
        this.worldGenSettingsComponent.tick();
    }

    @Override
    protected void init() {
        this.nameEdit = new EditBox(this.font, this.width / 2 - 100, 60, 200, 20, (Component)Component.translatable("selectWorld.enterName")){

            @Override
            protected MutableComponent createNarrationMessage() {
                return CommonComponents.joinForNarration(super.createNarrationMessage(), Component.translatable("selectWorld.resultFolder")).append(" ").append(CreateWorldScreen.this.resultFolder);
            }
        };
        this.nameEdit.setValue(this.initName);
        this.nameEdit.setResponder((Consumer<String>)((Consumer)$$0 -> {
            this.initName = $$0;
            this.createButton.active = !this.nameEdit.getValue().isEmpty();
            this.updateResultFolder();
        }));
        this.addWidget(this.nameEdit);
        int $$02 = this.width / 2 - 155;
        int $$12 = this.width / 2 + 5;
        this.modeButton = this.addRenderableWidget((GuiEventListener & Renderable)CycleButton.builder(SelectedGameMode::getDisplayName).withValues(SelectedGameMode.SURVIVAL, SelectedGameMode.HARDCORE, SelectedGameMode.CREATIVE).withInitialValue(this.gameMode).withCustomNarration((Function<CycleButton<SelectedGameMode>, MutableComponent>)((Function)$$0 -> AbstractWidget.wrapDefaultNarrationMessage($$0.getMessage()).append(CommonComponents.NARRATION_SEPARATOR).append(this.gameModeHelp1).append(" ").append(this.gameModeHelp2))).create($$02, 100, 150, 20, GAME_MODEL_LABEL, ($$0, $$1) -> this.setGameMode((SelectedGameMode)((Object)$$1))));
        this.difficultyButton = this.addRenderableWidget((GuiEventListener & Renderable)CycleButton.builder(Difficulty::getDisplayName).withValues(Difficulty.values()).withInitialValue(this.getEffectiveDifficulty()).create($$12, 100, 150, 20, Component.translatable("options.difficulty"), ($$0, $$1) -> {
            this.difficulty = $$1;
        }));
        this.commandsButton = this.addRenderableWidget((GuiEventListener & Renderable)CycleButton.onOffBuilder(this.commands && !this.hardCore).withCustomNarration((Function<CycleButton<Boolean>, MutableComponent>)((Function)$$0 -> CommonComponents.joinForNarration($$0.createDefaultNarrationMessage(), Component.translatable("selectWorld.allowCommands.info")))).create($$02, 151, 150, 20, Component.translatable("selectWorld.allowCommands"), ($$0, $$1) -> {
            this.commandsChanged = true;
            this.commands = $$1;
        }));
        this.dataPacksButton = this.addRenderableWidget(Button.builder(Component.translatable("selectWorld.dataPacks"), $$0 -> this.openDataPackSelectionScreen()).bounds($$12, 151, 150, 20).build());
        this.gameRulesButton = this.addRenderableWidget(Button.builder(Component.translatable("selectWorld.gameRules"), $$0 -> this.minecraft.setScreen(new EditGameRulesScreen(this.gameRules.copy(), (Consumer<Optional<GameRules>>)((Consumer)$$02 -> {
            this.minecraft.setScreen(this);
            $$02.ifPresent($$0 -> {
                this.gameRules = $$0;
            });
        })))).bounds($$02, 185, 150, 20).build());
        this.worldGenSettingsComponent.init(this, this.minecraft, this.font);
        this.moreOptionsButton = this.addRenderableWidget(Button.builder(Component.translatable("selectWorld.moreWorldOptions"), $$0 -> this.toggleWorldGenSettingsVisibility()).bounds($$12, 185, 150, 20).build());
        this.createButton = this.addRenderableWidget(Button.builder(Component.translatable("selectWorld.create"), $$0 -> this.onCreate()).bounds($$02, this.height - 28, 150, 20).build());
        this.createButton.active = !this.initName.isEmpty();
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, $$0 -> this.popScreen()).bounds($$12, this.height - 28, 150, 20).build());
        this.refreshWorldGenSettingsVisibility();
        this.setInitialFocus(this.nameEdit);
        this.setGameMode(this.gameMode);
        this.updateResultFolder();
    }

    private Difficulty getEffectiveDifficulty() {
        return this.gameMode == SelectedGameMode.HARDCORE ? Difficulty.HARD : this.difficulty;
    }

    private void updateGameModeHelp() {
        this.gameModeHelp1 = Component.translatable("selectWorld.gameMode." + this.gameMode.name + ".line1");
        this.gameModeHelp2 = Component.translatable("selectWorld.gameMode." + this.gameMode.name + ".line2");
    }

    private void updateResultFolder() {
        this.resultFolder = this.nameEdit.getValue().trim();
        if (this.resultFolder.isEmpty()) {
            this.resultFolder = "World";
        }
        try {
            this.resultFolder = FileUtil.findAvailableName(this.minecraft.getLevelSource().getBaseDir(), this.resultFolder, "");
        }
        catch (Exception $$0) {
            this.resultFolder = "World";
            try {
                this.resultFolder = FileUtil.findAvailableName(this.minecraft.getLevelSource().getBaseDir(), this.resultFolder, "");
            }
            catch (Exception $$1) {
                throw new RuntimeException("Could not create save folder", (Throwable)$$1);
            }
        }
    }

    private static void queueLoadScreen(Minecraft $$0, Component $$1) {
        $$0.forceSetScreen(new GenericDirtMessageScreen($$1));
    }

    private void onCreate() {
        WorldCreationContext $$0 = this.worldGenSettingsComponent.settings();
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
        WorldCreationContext $$5 = this.worldGenSettingsComponent.settings();
        WorldOptions $$6 = this.worldGenSettingsComponent.createFinalOptions($$4, this.hardCore);
        LevelSettings $$7 = this.createLevelSettings($$4);
        PrimaryLevelData $$8 = new PrimaryLevelData($$7, $$6, $$0, $$2);
        this.minecraft.createWorldOpenFlows().createLevelFromExistingSettings((LevelStorageSource.LevelStorageAccess)$$3.get(), $$5.dataPackResources(), $$1, $$8);
    }

    private LevelSettings createLevelSettings(boolean $$0) {
        String $$1 = this.nameEdit.getValue().trim();
        if ($$0) {
            GameRules $$2 = new GameRules();
            $$2.getRule(GameRules.RULE_DAYLIGHT).set(false, null);
            return new LevelSettings($$1, GameType.SPECTATOR, false, Difficulty.PEACEFUL, true, $$2, WorldDataConfiguration.DEFAULT);
        }
        return new LevelSettings($$1, this.gameMode.gameType, this.hardCore, this.getEffectiveDifficulty(), this.commands && !this.hardCore, this.gameRules, this.dataConfiguration);
    }

    private void toggleWorldGenSettingsVisibility() {
        this.setWorldGenSettingsVisible(!this.worldGenSettingsVisible);
    }

    private void setGameMode(SelectedGameMode $$0) {
        if (!this.commandsChanged) {
            this.commands = $$0 == SelectedGameMode.CREATIVE;
            this.commandsButton.setValue(this.commands);
        }
        if ($$0 == SelectedGameMode.HARDCORE) {
            this.hardCore = true;
            this.commandsButton.active = false;
            this.commandsButton.setValue(false);
            this.worldGenSettingsComponent.switchToHardcore();
            this.difficultyButton.setValue(Difficulty.HARD);
            this.difficultyButton.active = false;
        } else {
            this.hardCore = false;
            this.commandsButton.active = true;
            this.commandsButton.setValue(this.commands);
            this.worldGenSettingsComponent.switchOutOfHardcode();
            this.difficultyButton.setValue(this.difficulty);
            this.difficultyButton.active = true;
        }
        this.gameMode = $$0;
        this.updateGameModeHelp();
    }

    public void refreshWorldGenSettingsVisibility() {
        this.setWorldGenSettingsVisible(this.worldGenSettingsVisible);
    }

    private void setWorldGenSettingsVisible(boolean $$0) {
        this.worldGenSettingsVisible = $$0;
        this.modeButton.visible = !$$0;
        boolean bl = this.difficultyButton.visible = !$$0;
        if (this.worldGenSettingsComponent.isDebug()) {
            this.dataPacksButton.visible = false;
            this.modeButton.active = false;
            if (this.oldGameMode == null) {
                this.oldGameMode = this.gameMode;
            }
            this.setGameMode(SelectedGameMode.DEBUG);
            this.commandsButton.visible = false;
        } else {
            this.modeButton.active = true;
            if (this.oldGameMode != null) {
                this.setGameMode(this.oldGameMode);
            }
            this.commandsButton.visible = !$$0;
            this.dataPacksButton.visible = !$$0;
        }
        this.worldGenSettingsComponent.setVisibility($$0);
        this.nameEdit.setVisible(!$$0);
        if ($$0) {
            this.moreOptionsButton.setMessage(CommonComponents.GUI_DONE);
        } else {
            this.moreOptionsButton.setMessage(Component.translatable("selectWorld.moreWorldOptions"));
        }
        this.gameRulesButton.visible = !$$0;
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
        if (this.worldGenSettingsVisible) {
            this.setWorldGenSettingsVisible(false);
        } else {
            this.popScreen();
        }
    }

    public void popScreen() {
        this.minecraft.setScreen(this.lastScreen);
        this.removeTempDataPackDir();
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderBackground($$0);
        CreateWorldScreen.drawCenteredString($$0, this.font, this.title, this.width / 2, 20, -1);
        if (this.worldGenSettingsVisible) {
            CreateWorldScreen.drawString($$0, this.font, SEED_LABEL, this.width / 2 - 100, 47, -6250336);
            CreateWorldScreen.drawString($$0, this.font, SEED_INFO, this.width / 2 - 100, 85, -6250336);
            this.worldGenSettingsComponent.render($$0, $$1, $$2, $$3);
        } else {
            CreateWorldScreen.drawString($$0, this.font, NAME_LABEL, this.width / 2 - 100, 47, -6250336);
            CreateWorldScreen.drawString($$0, this.font, Component.empty().append(OUTPUT_DIR_INFO).append(" ").append(this.resultFolder), this.width / 2 - 100, 85, -6250336);
            this.nameEdit.render($$0, $$1, $$2, $$3);
            CreateWorldScreen.drawString($$0, this.font, this.gameModeHelp1, this.width / 2 - 150, 122, -6250336);
            CreateWorldScreen.drawString($$0, this.font, this.gameModeHelp2, this.width / 2 - 150, 134, -6250336);
            if (this.commandsButton.visible) {
                CreateWorldScreen.drawString($$0, this.font, COMMANDS_INFO, this.width / 2 - 150, 172, -6250336);
            }
        }
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

    private void openDataPackSelectionScreen() {
        Pair<Path, PackRepository> $$0 = this.getDataPackSelectionSettings();
        if ($$0 != null) {
            this.minecraft.setScreen(new PackSelectionScreen(this, (PackRepository)$$0.getSecond(), (Consumer<PackRepository>)((Consumer)this::tryApplyNewDataPacks), (Path)$$0.getFirst(), Component.translatable("dataPack.title")));
        }
    }

    private void tryApplyNewDataPacks(PackRepository $$0) {
        ImmutableList $$1 = ImmutableList.copyOf($$0.getSelectedIds());
        List $$2 = (List)$$0.getAvailableIds().stream().filter(arg_0 -> CreateWorldScreen.lambda$tryApplyNewDataPacks$16((List)$$1, arg_0)).collect(ImmutableList.toImmutableList());
        WorldDataConfiguration $$3 = new WorldDataConfiguration(new DataPackConfig((List<String>)$$1, (List<String>)$$2), this.dataConfiguration.enabledFeatures());
        if ($$1.equals(this.dataConfiguration.dataPacks().getEnabled())) {
            this.dataConfiguration = $$3;
            return;
        }
        FeatureFlagSet $$4 = $$0.getRequestedFeatureFlags();
        if (FeatureFlags.isExperimental($$4)) {
            this.minecraft.tell(() -> this.minecraft.setScreen(new ConfirmExperimentalFeaturesScreen($$0.getSelectedPacks(), $$2 -> {
                if ($$2) {
                    this.applyNewPackConfig($$0, $$3);
                } else {
                    this.openDataPackSelectionScreen();
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
            WorldCreationContext $$12 = this.worldGenSettingsComponent.settings();
            RegistryOps $$2 = RegistryOps.create(JsonOps.INSTANCE, $$12.worldgenLoadContext());
            DataResult $$3 = WorldGenSettings.encode($$2, $$12.options(), $$12.selectedDimensions()).setLifecycle(Lifecycle.stable());
            RegistryOps $$4 = RegistryOps.create(JsonOps.INSTANCE, $$0.datapackWorldgen());
            WorldGenSettings $$5 = (WorldGenSettings)((Object)((Object)$$3.flatMap($$1 -> WorldGenSettings.CODEC.parse($$4, $$1)).getOrThrow(false, Util.prefix("Error parsing worldgen settings after loading data packs: ", (Consumer<String>)((Consumer)arg_0 -> ((Logger)LOGGER).error(arg_0))))));
            return new WorldLoader.DataLoadOutput<DataPackReloadCookie>(new DataPackReloadCookie($$5, $$0.dataConfiguration()), $$0.datapackDimensions());
        }, ($$0, $$1, $$2, $$3) -> {
            $$0.close();
            return new WorldCreationContext($$3.worldGenSettings(), $$2, $$1, $$3.dataConfiguration());
        }, (Executor)Util.backgroundExecutor(), this.minecraft).thenAcceptAsync($$0 -> {
            this.dataConfiguration = $$0.dataConfiguration();
            this.worldGenSettingsComponent.updateSettings((WorldCreationContext)((Object)$$0));
            this.rebuildWidgets();
        }, (Executor)this.minecraft).handle(($$0, $$1) -> {
            if ($$1 != null) {
                LOGGER.warn("Failed to validate datapack", $$1);
                this.minecraft.tell(() -> this.minecraft.setScreen(new ConfirmScreen($$0 -> {
                    if ($$0) {
                        this.openDataPackSelectionScreen();
                    } else {
                        this.dataConfiguration = WorldDataConfiguration.DEFAULT;
                        this.minecraft.setScreen(this);
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
    private Pair<Path, PackRepository> getDataPackSelectionSettings() {
        Path $$0 = this.getTempDataPackDir();
        if ($$0 != null) {
            if (this.tempDataPackRepository == null) {
                this.tempDataPackRepository = ServerPacksSource.createPackRepository($$0);
                this.tempDataPackRepository.reload();
            }
            this.tempDataPackRepository.setSelected((Collection<String>)this.dataConfiguration.dataPacks().getEnabled());
            return Pair.of((Object)$$0, (Object)this.tempDataPackRepository);
        }
        return null;
    }

    private static /* synthetic */ boolean lambda$tryApplyNewDataPacks$16(List $$0, String $$1) {
        return !$$0.contains((Object)$$1);
    }

    static enum SelectedGameMode {
        SURVIVAL("survival", GameType.SURVIVAL),
        HARDCORE("hardcore", GameType.SURVIVAL),
        CREATIVE("creative", GameType.CREATIVE),
        DEBUG("spectator", GameType.SPECTATOR);

        final String name;
        final GameType gameType;
        private final Component displayName;

        private SelectedGameMode(String $$0, GameType $$1) {
            this.name = $$0;
            this.gameType = $$1;
            this.displayName = Component.translatable("selectWorld.gameMode." + $$0);
        }

        public Component getDisplayName() {
            return this.displayName;
        }
    }

    record DataPackReloadCookie(WorldGenSettings worldGenSettings, WorldDataConfiguration dataConfiguration) {
    }
}