/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Boolean
 *  java.lang.Long
 *  java.lang.Object
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.Collection
 *  java.util.List
 *  java.util.Optional
 *  java.util.OptionalLong
 *  java.util.function.Consumer
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.screens.worldselection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.gui.screens.worldselection.PresetEditor;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.WorldPresetTags;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.presets.WorldPresets;

public class WorldCreationUiState {
    private final List<Consumer<WorldCreationUiState>> listeners = new ArrayList();
    private String name = I18n.get("selectWorld.newWorld", new Object[0]);
    private boolean nameChanged = true;
    private SelectedGameMode gameMode = SelectedGameMode.SURVIVAL;
    private Difficulty difficulty = Difficulty.NORMAL;
    @Nullable
    private Boolean allowCheats;
    private String seed;
    private boolean generateStructures;
    private boolean bonusChest;
    private WorldCreationContext settings;
    private WorldTypeEntry worldType;
    private final List<WorldTypeEntry> normalPresetList = new ArrayList();
    private final List<WorldTypeEntry> altPresetList = new ArrayList();
    private GameRules gameRules = new GameRules();

    public WorldCreationUiState(WorldCreationContext $$0, Optional<ResourceKey<WorldPreset>> $$1, OptionalLong $$2) {
        this.settings = $$0;
        this.worldType = new WorldTypeEntry((Holder)WorldCreationUiState.findPreset($$0, $$1).orElse(null));
        this.updatePresetLists();
        this.seed = $$2.isPresent() ? Long.toString((long)$$2.getAsLong()) : "";
        this.generateStructures = $$0.options().generateStructures();
        this.bonusChest = $$0.options().generateBonusChest();
    }

    public void addListener(Consumer<WorldCreationUiState> $$0) {
        this.listeners.add($$0);
    }

    public void onChanged() {
        boolean $$12;
        boolean $$0 = this.isBonusChest();
        if ($$0 != this.settings.options().generateBonusChest()) {
            this.settings = this.settings.withOptions($$1 -> $$1.withBonusChest($$0));
        }
        if (($$12 = this.isGenerateStructures()) != this.settings.options().generateStructures()) {
            this.settings = this.settings.withOptions($$1 -> $$1.withStructures($$12));
        }
        for (Consumer $$2 : this.listeners) {
            $$2.accept((Object)this);
        }
        this.nameChanged = false;
    }

    public void setName(String $$0) {
        this.name = $$0;
        this.nameChanged = true;
        this.onChanged();
    }

    public String getName() {
        return this.name;
    }

    public boolean nameChanged() {
        return this.nameChanged;
    }

    public void setGameMode(SelectedGameMode $$0) {
        this.gameMode = $$0;
        this.onChanged();
    }

    public SelectedGameMode getGameMode() {
        if (this.isDebug()) {
            return SelectedGameMode.DEBUG;
        }
        return this.gameMode;
    }

    public void setDifficulty(Difficulty $$0) {
        this.difficulty = $$0;
        this.onChanged();
    }

    public Difficulty getDifficulty() {
        if (this.isHardcore()) {
            return Difficulty.HARD;
        }
        return this.difficulty;
    }

    public boolean isHardcore() {
        return this.getGameMode() == SelectedGameMode.HARDCORE;
    }

    public void setAllowCheats(boolean $$0) {
        this.allowCheats = $$0;
        this.onChanged();
    }

    public boolean isAllowCheats() {
        if (this.isDebug()) {
            return true;
        }
        if (this.isHardcore()) {
            return false;
        }
        if (this.allowCheats == null) {
            return this.getGameMode() == SelectedGameMode.CREATIVE;
        }
        return this.allowCheats;
    }

    public void setSeed(String $$02) {
        this.seed = $$02;
        this.settings = this.settings.withOptions($$0 -> $$0.withSeed(WorldOptions.parseSeed(this.getSeed())));
        this.onChanged();
    }

    public String getSeed() {
        return this.seed;
    }

    public void setGenerateStructures(boolean $$0) {
        this.generateStructures = $$0;
        this.onChanged();
    }

    public boolean isGenerateStructures() {
        if (this.isDebug()) {
            return false;
        }
        return this.generateStructures;
    }

    public void setBonusChest(boolean $$0) {
        this.bonusChest = $$0;
        this.onChanged();
    }

    public boolean isBonusChest() {
        if (this.isDebug() || this.isHardcore()) {
            return false;
        }
        return this.bonusChest;
    }

    public void setSettings(WorldCreationContext $$0) {
        this.settings = $$0;
        this.updatePresetLists();
        this.onChanged();
    }

    public WorldCreationContext getSettings() {
        return this.settings;
    }

    public void updateDimensions(WorldCreationContext.DimensionsUpdater $$0) {
        this.settings = this.settings.withDimensions($$0);
        this.onChanged();
    }

    protected boolean tryUpdateDataConfiguration(WorldDataConfiguration $$0) {
        WorldDataConfiguration $$1 = this.settings.dataConfiguration();
        if ($$1.dataPacks().getEnabled().equals($$0.dataPacks().getEnabled()) && $$1.enabledFeatures().equals($$0.enabledFeatures())) {
            this.settings = new WorldCreationContext(this.settings.options(), this.settings.datapackDimensions(), this.settings.selectedDimensions(), this.settings.worldgenRegistries(), this.settings.dataPackResources(), $$0);
            return true;
        }
        return false;
    }

    public boolean isDebug() {
        return this.settings.selectedDimensions().isDebug();
    }

    public void setWorldType(WorldTypeEntry $$0) {
        this.worldType = $$0;
        Holder<WorldPreset> $$12 = $$0.preset();
        if ($$12 != null) {
            this.updateDimensions(($$1, $$2) -> ((WorldPreset)$$12.value()).createWorldDimensions());
        }
    }

    public WorldTypeEntry getWorldType() {
        return this.worldType;
    }

    @Nullable
    public PresetEditor getPresetEditor() {
        Holder<WorldPreset> $$0 = this.getWorldType().preset();
        return $$0 != null ? (PresetEditor)PresetEditor.EDITORS.get($$0.unwrapKey()) : null;
    }

    public List<WorldTypeEntry> getNormalPresetList() {
        return this.normalPresetList;
    }

    public List<WorldTypeEntry> getAltPresetList() {
        return this.altPresetList;
    }

    private void updatePresetLists() {
        Registry $$0 = this.getSettings().worldgenLoadContext().registryOrThrow(Registries.WORLD_PRESET);
        this.normalPresetList.clear();
        this.normalPresetList.addAll((Collection)WorldCreationUiState.getNonEmptyList($$0, WorldPresetTags.NORMAL).orElseGet(() -> $$0.holders().map(WorldTypeEntry::new).toList()));
        this.altPresetList.clear();
        this.altPresetList.addAll((Collection)WorldCreationUiState.getNonEmptyList($$0, WorldPresetTags.EXTENDED).orElse(this.normalPresetList));
        Holder<WorldPreset> $$1 = this.worldType.preset();
        if ($$1 != null) {
            this.worldType = (WorldTypeEntry)((Object)WorldCreationUiState.findPreset(this.getSettings(), $$1.unwrapKey()).map(WorldTypeEntry::new).orElse((Object)((WorldTypeEntry)((Object)this.normalPresetList.get(0)))));
        }
    }

    private static Optional<Holder<WorldPreset>> findPreset(WorldCreationContext $$0, Optional<ResourceKey<WorldPreset>> $$12) {
        return $$12.flatMap($$1 -> $$0.worldgenLoadContext().registryOrThrow(Registries.WORLD_PRESET).getHolder($$1));
    }

    private static Optional<List<WorldTypeEntry>> getNonEmptyList(Registry<WorldPreset> $$02, TagKey<WorldPreset> $$1) {
        return $$02.getTag($$1).map($$0 -> $$0.stream().map(WorldTypeEntry::new).toList()).filter($$0 -> !$$0.isEmpty());
    }

    public void setGameRules(GameRules $$0) {
        this.gameRules = $$0;
        this.onChanged();
    }

    public GameRules getGameRules() {
        return this.gameRules;
    }

    public static enum SelectedGameMode {
        SURVIVAL("survival", GameType.SURVIVAL),
        HARDCORE("hardcore", GameType.SURVIVAL),
        CREATIVE("creative", GameType.CREATIVE),
        DEBUG("spectator", GameType.SPECTATOR);

        public final GameType gameType;
        public final Component displayName;
        private final Component info;

        private SelectedGameMode(String $$0, GameType $$1) {
            this.gameType = $$1;
            this.displayName = Component.translatable("selectWorld.gameMode." + $$0);
            this.info = Component.translatable("selectWorld.gameMode." + $$0 + ".info");
        }

        public Component getInfo() {
            return this.info;
        }
    }

    public record WorldTypeEntry(@Nullable Holder<WorldPreset> preset) {
        private static final Component CUSTOM_WORLD_DESCRIPTION = Component.translatable("generator.custom");

        public Component describePreset() {
            return (Component)Optional.ofNullable(this.preset).flatMap(Holder::unwrapKey).map($$0 -> Component.translatable($$0.location().toLanguageKey("generator"))).orElse((Object)CUSTOM_WORLD_DESCRIPTION);
        }

        public boolean isAmplified() {
            return Optional.ofNullable(this.preset).flatMap(Holder::unwrapKey).filter($$0 -> $$0.equals(WorldPresets.AMPLIFIED)).isPresent();
        }
    }
}