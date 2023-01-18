/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParser
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DataResult$PartialResult
 *  com.mojang.serialization.JsonOps
 *  com.mojang.serialization.Lifecycle
 *  java.io.BufferedReader
 *  java.io.Reader
 *  java.lang.Boolean
 *  java.lang.CharSequence
 *  java.lang.Exception
 *  java.lang.Long
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.nio.file.Files
 *  java.nio.file.Path
 *  java.nio.file.Paths
 *  java.util.List
 *  java.util.Objects
 *  java.util.Optional
 *  java.util.OptionalLong
 *  java.util.function.Consumer
 *  java.util.function.Function
 *  java.util.stream.Collectors
 *  org.lwjgl.util.tinyfd.TinyFileDialogs
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.screens.worldselection;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import java.io.BufferedReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.PresetEditor;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.client.gui.screens.worldselection.WorldOpenFlows;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.WorldPresetTags;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import org.lwjgl.util.tinyfd.TinyFileDialogs;
import org.slf4j.Logger;

public class WorldGenSettingsComponent
implements Renderable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Component CUSTOM_WORLD_DESCRIPTION = Component.translatable("generator.custom");
    private static final Component AMPLIFIED_HELP_TEXT = Component.translatable("generator.minecraft.amplified.info");
    private static final Component MAP_FEATURES_INFO = Component.translatable("selectWorld.mapFeatures.info");
    private static final Component SELECT_FILE_PROMPT = Component.translatable("selectWorld.import_worldgen_settings.select_file");
    private MultiLineLabel amplifiedWorldInfo = MultiLineLabel.EMPTY;
    private Font font;
    private int width;
    private EditBox seedEdit;
    private CycleButton<Boolean> featuresButton;
    private CycleButton<Boolean> bonusItemsButton;
    private CycleButton<Holder<WorldPreset>> typeButton;
    private Button customWorldDummyButton;
    private Button customizeTypeButton;
    private Button importSettingsButton;
    private WorldCreationContext settings;
    private Optional<Holder<WorldPreset>> preset;
    private OptionalLong seed;

    public WorldGenSettingsComponent(WorldCreationContext $$0, Optional<ResourceKey<WorldPreset>> $$1, OptionalLong $$2) {
        this.settings = $$0;
        this.preset = WorldGenSettingsComponent.findPreset($$0, $$1);
        this.seed = $$2;
    }

    private static Optional<Holder<WorldPreset>> findPreset(WorldCreationContext $$0, Optional<ResourceKey<WorldPreset>> $$12) {
        return $$12.flatMap($$1 -> $$0.worldgenLoadContext().registryOrThrow(Registries.WORLD_PRESET).getHolder($$1));
    }

    public void init(CreateWorldScreen $$02, Minecraft $$1, Font $$23) {
        this.font = $$23;
        this.width = $$02.width;
        this.seedEdit = new EditBox(this.font, this.width / 2 - 100, 60, 200, 20, Component.translatable("selectWorld.enterSeed"));
        this.seedEdit.setValue(WorldGenSettingsComponent.toString(this.seed));
        this.seedEdit.setResponder((Consumer<String>)((Consumer)$$0 -> {
            this.seed = WorldOptions.parseSeed(this.seedEdit.getValue());
        }));
        $$02.addWidget(this.seedEdit);
        int $$3 = this.width / 2 - 155;
        int $$4 = this.width / 2 + 5;
        this.featuresButton = $$02.addRenderableWidget(CycleButton.onOffBuilder(this.settings.options().generateStructures()).withCustomNarration((Function<CycleButton<Boolean>, MutableComponent>)((Function)$$0 -> CommonComponents.joinForNarration($$0.createDefaultNarrationMessage(), Component.translatable("selectWorld.mapFeatures.info")))).create($$3, 100, 150, 20, Component.translatable("selectWorld.mapFeatures"), ($$0, $$12) -> this.updateSettings($$1 -> $$1.withStructures((boolean)$$12))));
        this.featuresButton.visible = false;
        Registry $$5 = this.settings.worldgenLoadContext().registryOrThrow(Registries.WORLD_PRESET);
        List $$6 = (List)WorldGenSettingsComponent.getNonEmptyList($$5, WorldPresetTags.NORMAL).orElseGet(() -> (List)$$5.holders().collect(Collectors.toUnmodifiableList()));
        List $$7 = (List)WorldGenSettingsComponent.getNonEmptyList($$5, WorldPresetTags.EXTENDED).orElse((Object)$$6);
        this.typeButton = $$02.addRenderableWidget(CycleButton.builder(WorldGenSettingsComponent::describePreset).withValues($$6, $$7).withCustomNarration($$0 -> {
            if (WorldGenSettingsComponent.isAmplified((Holder)$$0.getValue())) {
                return CommonComponents.joinForNarration($$0.createDefaultNarrationMessage(), AMPLIFIED_HELP_TEXT);
            }
            return $$0.createDefaultNarrationMessage();
        }).create($$4, 100, 150, 20, Component.translatable("selectWorld.mapType"), ($$12, $$22) -> {
            this.preset = Optional.of((Object)$$22);
            this.updateSettings(($$1, $$2) -> ((WorldPreset)$$22.value()).createWorldDimensions());
            $$02.refreshWorldGenSettingsVisibility();
        }));
        this.preset.ifPresent(this.typeButton::setValue);
        this.typeButton.visible = false;
        this.customWorldDummyButton = $$02.addRenderableWidget(Button.builder(CommonComponents.optionNameValue(Component.translatable("selectWorld.mapType"), CUSTOM_WORLD_DESCRIPTION), $$0 -> {}).bounds($$4, 100, 150, 20).build());
        this.customWorldDummyButton.active = false;
        this.customWorldDummyButton.visible = false;
        this.customizeTypeButton = $$02.addRenderableWidget(Button.builder(Component.translatable("selectWorld.customizeType"), $$2 -> {
            PresetEditor $$3 = (PresetEditor)PresetEditor.EDITORS.get((Object)this.preset.flatMap(Holder::unwrapKey));
            if ($$3 != null) {
                $$1.setScreen($$3.createEditScreen($$02, this.settings));
            }
        }).bounds($$4, 120, 150, 20).build());
        this.customizeTypeButton.visible = false;
        this.bonusItemsButton = $$02.addRenderableWidget(CycleButton.onOffBuilder(this.settings.options().generateBonusChest() && !$$02.hardCore).create($$3, 151, 150, 20, Component.translatable("selectWorld.bonusItems"), ($$0, $$12) -> this.updateSettings($$1 -> $$1.withBonusChest((boolean)$$12))));
        this.bonusItemsButton.visible = false;
        this.importSettingsButton = $$02.addRenderableWidget(Button.builder(Component.translatable("selectWorld.import_worldgen_settings"), $$2 -> {
            DataResult $$10;
            String $$32 = TinyFileDialogs.tinyfd_openFileDialog((CharSequence)SELECT_FILE_PROMPT.getString(), null, null, null, (boolean)false);
            if ($$32 == null) {
                return;
            }
            RegistryOps $$4 = RegistryOps.create(JsonOps.INSTANCE, this.settings.worldgenLoadContext());
            try (BufferedReader $$5 = Files.newBufferedReader((Path)Paths.get((String)$$32, (String[])new String[0]));){
                JsonElement $$6 = JsonParser.parseReader((Reader)$$5);
                DataResult $$7 = WorldGenSettings.CODEC.parse($$4, (Object)$$6);
            }
            catch (Exception $$9) {
                $$10 = DataResult.error((String)("Failed to parse file: " + $$9.getMessage()));
            }
            if ($$10.error().isPresent()) {
                MutableComponent $$11 = Component.translatable("selectWorld.import_worldgen_settings.failure");
                String $$12 = ((DataResult.PartialResult)$$10.error().get()).message();
                LOGGER.error("Error parsing world settings: {}", (Object)$$12);
                MutableComponent $$13 = Component.literal($$12);
                $$1.getToasts().addToast(SystemToast.multiline($$1, SystemToast.SystemToastIds.WORLD_GEN_SETTINGS_TRANSFER, $$11, $$13));
                return;
            }
            Lifecycle $$14 = $$10.lifecycle();
            $$10.resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).ifPresent($$3 -> WorldOpenFlows.confirmWorldCreation($$1, $$02, $$14, () -> this.importSettings($$3.options(), $$3.dimensions())));
        }).bounds($$3, 185, 150, 20).build());
        this.importSettingsButton.visible = false;
        this.amplifiedWorldInfo = MultiLineLabel.create($$23, (FormattedText)AMPLIFIED_HELP_TEXT, this.typeButton.getWidth());
    }

    private static Optional<List<Holder<WorldPreset>>> getNonEmptyList(Registry<WorldPreset> $$02, TagKey<WorldPreset> $$1) {
        return $$02.getTag($$1).map($$0 -> $$0.stream().toList()).filter($$0 -> !$$0.isEmpty());
    }

    private static boolean isAmplified(Holder<WorldPreset> $$02) {
        return $$02.unwrapKey().filter($$0 -> $$0.equals(WorldPresets.AMPLIFIED)).isPresent();
    }

    private static Component describePreset(Holder<WorldPreset> $$02) {
        return (Component)$$02.unwrapKey().map($$0 -> Component.translatable($$0.location().toLanguageKey("generator"))).orElse((Object)CUSTOM_WORLD_DESCRIPTION);
    }

    private void importSettings(WorldOptions $$0, WorldDimensions $$1) {
        this.settings = this.settings.withSettings($$0, $$1);
        this.preset = WorldGenSettingsComponent.findPreset(this.settings, WorldPresets.fromSettings($$1.dimensions()));
        this.selectWorldTypeButton(true);
        this.seed = OptionalLong.of((long)$$0.seed());
        this.seedEdit.setValue(WorldGenSettingsComponent.toString(this.seed));
    }

    public void tick() {
        this.seedEdit.tick();
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        if (this.featuresButton.visible) {
            this.font.drawShadow($$0, MAP_FEATURES_INFO, (float)(this.width / 2 - 150), 122.0f, -6250336);
        }
        this.seedEdit.render($$0, $$1, $$2, $$3);
        if (this.preset.filter(WorldGenSettingsComponent::isAmplified).isPresent()) {
            int n = this.typeButton.getX() + 2;
            int n2 = this.typeButton.getY() + 22;
            Objects.requireNonNull((Object)this.font);
            this.amplifiedWorldInfo.renderLeftAligned($$0, n, n2, 9, 0xA0A0A0);
        }
    }

    void updateSettings(WorldCreationContext.DimensionsUpdater $$0) {
        this.settings = this.settings.withDimensions($$0);
    }

    private void updateSettings(WorldCreationContext.OptionsModifier $$0) {
        this.settings = this.settings.withOptions($$0);
    }

    void updateSettings(WorldCreationContext $$0) {
        this.settings = $$0;
    }

    private static String toString(OptionalLong $$0) {
        if ($$0.isPresent()) {
            return Long.toString((long)$$0.getAsLong());
        }
        return "";
    }

    public WorldOptions createFinalOptions(boolean $$0, boolean $$1) {
        OptionalLong $$2 = WorldOptions.parseSeed(this.seedEdit.getValue());
        WorldOptions $$3 = this.settings.options();
        if ($$0 || $$1) {
            $$3 = $$3.withBonusChest(false);
        }
        if ($$0) {
            $$3 = $$3.withStructures(false);
        }
        return $$3.withSeed($$2);
    }

    public boolean isDebug() {
        return this.settings.selectedDimensions().isDebug();
    }

    public void setVisibility(boolean $$0) {
        this.selectWorldTypeButton($$0);
        if (this.isDebug()) {
            this.featuresButton.visible = false;
            this.bonusItemsButton.visible = false;
            this.customizeTypeButton.visible = false;
            this.importSettingsButton.visible = false;
        } else {
            this.featuresButton.visible = $$0;
            this.bonusItemsButton.visible = $$0;
            this.customizeTypeButton.visible = $$0 && PresetEditor.EDITORS.containsKey((Object)this.preset.flatMap(Holder::unwrapKey));
            this.importSettingsButton.visible = $$0;
        }
        this.seedEdit.setVisible($$0);
    }

    private void selectWorldTypeButton(boolean $$0) {
        if (this.preset.isPresent()) {
            this.typeButton.visible = $$0;
            this.customWorldDummyButton.visible = false;
        } else {
            this.typeButton.visible = false;
            this.customWorldDummyButton.visible = $$0;
        }
    }

    public WorldCreationContext settings() {
        return this.settings;
    }

    public RegistryAccess registryHolder() {
        return this.settings.worldgenLoadContext();
    }

    public void switchToHardcore() {
        this.bonusItemsButton.active = false;
        this.bonusItemsButton.setValue(false);
    }

    public void switchOutOfHardcode() {
        this.bonusItemsButton.active = true;
        this.bonusItemsButton.setValue(this.settings.options().generateBonusChest());
    }
}