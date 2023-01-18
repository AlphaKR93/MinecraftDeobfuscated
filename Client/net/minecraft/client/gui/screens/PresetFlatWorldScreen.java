/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.Splitter
 *  com.google.common.collect.Lists
 *  com.mojang.logging.LogUtils
 *  java.lang.CharSequence
 *  java.lang.Exception
 *  java.lang.IllegalStateException
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.NumberFormatException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.lang.Throwable
 *  java.util.ArrayList
 *  java.util.Collections
 *  java.util.Iterator
 *  java.util.List
 *  java.util.Optional
 *  java.util.Set
 *  java.util.stream.Collectors
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.screens;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.CreateFlatWorldScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.WorldGenSettingsComponent;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FlatLevelGeneratorPresetTags;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPreset;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import org.slf4j.Logger;

public class PresetFlatWorldScreen
extends Screen {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final int SLOT_TEX_SIZE = 128;
    private static final int SLOT_BG_SIZE = 18;
    private static final int SLOT_STAT_HEIGHT = 20;
    private static final int SLOT_BG_X = 1;
    private static final int SLOT_BG_Y = 1;
    private static final int SLOT_FG_X = 2;
    private static final int SLOT_FG_Y = 2;
    private static final ResourceKey<Biome> DEFAULT_BIOME = Biomes.PLAINS;
    public static final Component UNKNOWN_PRESET = Component.translatable("flat_world_preset.unknown");
    private final CreateFlatWorldScreen parent;
    private Component shareText;
    private Component listText;
    private PresetsList list;
    private Button selectButton;
    EditBox export;
    FlatLevelGeneratorSettings settings;

    public PresetFlatWorldScreen(CreateFlatWorldScreen $$0) {
        super(Component.translatable("createWorld.customize.presets.title"));
        this.parent = $$0;
    }

    /*
     * WARNING - void declaration
     */
    @Nullable
    private static FlatLayerInfo getLayerInfoFromString(HolderGetter<Block> $$0, String $$1, int $$2) {
        void $$13;
        int $$8;
        String $$7;
        List $$3 = Splitter.on((char)'*').limit(2).splitToList((CharSequence)$$1);
        if ($$3.size() == 2) {
            String $$4 = (String)$$3.get(1);
            try {
                int $$5 = Math.max((int)Integer.parseInt((String)((String)$$3.get(0))), (int)0);
            }
            catch (NumberFormatException $$6) {
                LOGGER.error("Error while parsing flat world string", (Throwable)$$6);
                return null;
            }
        } else {
            $$7 = (String)$$3.get(0);
            $$8 = 1;
        }
        int $$9 = Math.min((int)($$2 + $$8), (int)DimensionType.Y_SIZE);
        int $$10 = $$9 - $$2;
        try {
            Optional<Holder.Reference<Block>> $$11 = $$0.get(ResourceKey.create(Registries.BLOCK, new ResourceLocation($$7)));
        }
        catch (Exception $$12) {
            LOGGER.error("Error while parsing flat world string", (Throwable)$$12);
            return null;
        }
        if ($$13.isEmpty()) {
            LOGGER.error("Error while parsing flat world string => Unknown block, {}", (Object)$$7);
            return null;
        }
        return new FlatLayerInfo($$10, (Block)((Holder.Reference)$$13.get()).value());
    }

    private static List<FlatLayerInfo> getLayersInfoFromString(HolderGetter<Block> $$0, String $$1) {
        ArrayList $$2 = Lists.newArrayList();
        String[] $$3 = $$1.split(",");
        int $$4 = 0;
        for (String $$5 : $$3) {
            FlatLayerInfo $$6 = PresetFlatWorldScreen.getLayerInfoFromString($$0, $$5, $$4);
            if ($$6 == null) {
                return Collections.emptyList();
            }
            $$2.add((Object)$$6);
            $$4 += $$6.getHeight();
        }
        return $$2;
    }

    public static FlatLevelGeneratorSettings fromString(HolderGetter<Block> $$02, HolderGetter<Biome> $$1, HolderGetter<StructureSet> $$2, HolderGetter<PlacedFeature> $$3, String $$4, FlatLevelGeneratorSettings $$5) {
        Holder.Reference<Biome> $$8;
        Iterator $$6 = Splitter.on((char)';').split((CharSequence)$$4).iterator();
        if (!$$6.hasNext()) {
            return FlatLevelGeneratorSettings.getDefault($$1, $$2, $$3);
        }
        List<FlatLayerInfo> $$7 = PresetFlatWorldScreen.getLayersInfoFromString($$02, (String)$$6.next());
        if ($$7.isEmpty()) {
            return FlatLevelGeneratorSettings.getDefault($$1, $$2, $$3);
        }
        Holder<Biome> $$9 = $$8 = $$1.getOrThrow(DEFAULT_BIOME);
        if ($$6.hasNext()) {
            String $$10 = (String)$$6.next();
            $$9 = (Holder)Optional.ofNullable((Object)ResourceLocation.tryParse($$10)).map($$0 -> ResourceKey.create(Registries.BIOME, $$0)).flatMap($$1::get).orElseGet(() -> {
                LOGGER.warn("Invalid biome: {}", (Object)$$10);
                return $$8;
            });
        }
        return $$5.withBiomeAndLayers($$7, $$5.structureOverrides(), $$9);
    }

    static String save(FlatLevelGeneratorSettings $$0) {
        StringBuilder $$1 = new StringBuilder();
        for (int $$2 = 0; $$2 < $$0.getLayersInfo().size(); ++$$2) {
            if ($$2 > 0) {
                $$1.append(",");
            }
            $$1.append($$0.getLayersInfo().get($$2));
        }
        $$1.append(";");
        $$1.append($$0.getBiome().unwrapKey().map(ResourceKey::location).orElseThrow(() -> new IllegalStateException("Biome not registered")));
        return $$1.toString();
    }

    @Override
    protected void init() {
        this.shareText = Component.translatable("createWorld.customize.presets.share");
        this.listText = Component.translatable("createWorld.customize.presets.list");
        this.export = new EditBox(this.font, 50, 40, this.width - 100, 20, this.shareText);
        this.export.setMaxLength(1230);
        WorldGenSettingsComponent $$02 = this.parent.parent.worldGenSettingsComponent;
        RegistryAccess $$1 = $$02.registryHolder();
        FeatureFlagSet $$2 = $$02.settings().dataConfiguration().enabledFeatures();
        HolderLookup.RegistryLookup $$3 = $$1.lookupOrThrow(Registries.BIOME);
        HolderLookup.RegistryLookup $$42 = $$1.lookupOrThrow(Registries.STRUCTURE_SET);
        HolderLookup.RegistryLookup $$5 = $$1.lookupOrThrow(Registries.PLACED_FEATURE);
        HolderLookup $$6 = $$1.lookupOrThrow(Registries.BLOCK).filterFeatures($$2);
        this.export.setValue(PresetFlatWorldScreen.save(this.parent.settings()));
        this.settings = this.parent.settings();
        this.addWidget(this.export);
        this.list = new PresetsList($$1, $$2);
        this.addWidget(this.list);
        this.selectButton = this.addRenderableWidget(Button.builder(Component.translatable("createWorld.customize.presets.select"), $$4 -> {
            FlatLevelGeneratorSettings $$5 = PresetFlatWorldScreen.fromString($$6, $$3, $$42, $$5, this.export.getValue(), this.settings);
            this.parent.setConfig($$5);
            this.minecraft.setScreen(this.parent);
        }).bounds(this.width / 2 - 155, this.height - 28, 150, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, $$0 -> this.minecraft.setScreen(this.parent)).bounds(this.width / 2 + 5, this.height - 28, 150, 20).build());
        this.updateButtonValidity(this.list.getSelected() != null);
    }

    @Override
    public boolean mouseScrolled(double $$0, double $$1, double $$2) {
        return this.list.mouseScrolled($$0, $$1, $$2);
    }

    @Override
    public void resize(Minecraft $$0, int $$1, int $$2) {
        String $$3 = this.export.getValue();
        this.init($$0, $$1, $$2);
        this.export.setValue($$3);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderBackground($$0);
        this.list.render($$0, $$1, $$2, $$3);
        $$0.pushPose();
        $$0.translate(0.0f, 0.0f, 400.0f);
        PresetFlatWorldScreen.drawCenteredString($$0, this.font, this.title, this.width / 2, 8, 0xFFFFFF);
        PresetFlatWorldScreen.drawString($$0, this.font, this.shareText, 50, 30, 0xA0A0A0);
        PresetFlatWorldScreen.drawString($$0, this.font, this.listText, 50, 70, 0xA0A0A0);
        $$0.popPose();
        this.export.render($$0, $$1, $$2, $$3);
        super.render($$0, $$1, $$2, $$3);
    }

    @Override
    public void tick() {
        this.export.tick();
        super.tick();
    }

    public void updateButtonValidity(boolean $$0) {
        this.selectButton.active = $$0 || this.export.getValue().length() > 1;
    }

    class PresetsList
    extends ObjectSelectionList<Entry> {
        public PresetsList(RegistryAccess $$02, FeatureFlagSet $$12) {
            super(PresetFlatWorldScreen.this.minecraft, PresetFlatWorldScreen.this.width, PresetFlatWorldScreen.this.height, 80, PresetFlatWorldScreen.this.height - 37, 24);
            for (Holder $$2 : $$02.registryOrThrow(Registries.FLAT_LEVEL_GENERATOR_PRESET).getTagOrEmpty(FlatLevelGeneratorPresetTags.VISIBLE)) {
                Set $$3 = (Set)((FlatLevelGeneratorPreset)((Object)$$2.value())).settings().getLayersInfo().stream().map($$0 -> $$0.getBlockState().getBlock()).filter($$1 -> !$$1.isEnabled($$12)).collect(Collectors.toSet());
                if (!$$3.isEmpty()) {
                    LOGGER.info("Discarding flat world preset {} since it contains experimental blocks {}", $$2.unwrapKey().map($$0 -> $$0.location().toString()).orElse((Object)"<unknown>"), (Object)$$3);
                    continue;
                }
                this.addEntry(new Entry($$2));
            }
        }

        @Override
        public void setSelected(@Nullable Entry $$0) {
            super.setSelected($$0);
            PresetFlatWorldScreen.this.updateButtonValidity($$0 != null);
        }

        @Override
        public boolean keyPressed(int $$0, int $$1, int $$2) {
            if (super.keyPressed($$0, $$1, $$2)) {
                return true;
            }
            if (($$0 == 257 || $$0 == 335) && this.getSelected() != null) {
                ((Entry)this.getSelected()).select();
            }
            return false;
        }

        public class Entry
        extends ObjectSelectionList.Entry<Entry> {
            private final FlatLevelGeneratorPreset preset;
            private final Component name;

            public Entry(Holder<FlatLevelGeneratorPreset> $$1) {
                this.preset = $$1.value();
                this.name = (Component)$$1.unwrapKey().map($$0 -> Component.translatable($$0.location().toLanguageKey("flat_world_preset"))).orElse((Object)UNKNOWN_PRESET);
            }

            @Override
            public void render(PoseStack $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
                this.blitSlot($$0, $$3, $$2, this.preset.displayItem().value());
                PresetFlatWorldScreen.this.font.draw($$0, this.name, (float)($$3 + 18 + 5), (float)($$2 + 6), 0xFFFFFF);
            }

            @Override
            public boolean mouseClicked(double $$0, double $$1, int $$2) {
                if ($$2 == 0) {
                    this.select();
                }
                return false;
            }

            void select() {
                PresetsList.this.setSelected(this);
                PresetFlatWorldScreen.this.settings = this.preset.settings();
                PresetFlatWorldScreen.this.export.setValue(PresetFlatWorldScreen.save(PresetFlatWorldScreen.this.settings));
                PresetFlatWorldScreen.this.export.moveCursorToStart();
            }

            private void blitSlot(PoseStack $$0, int $$1, int $$2, Item $$3) {
                this.blitSlotBg($$0, $$1 + 1, $$2 + 1);
                PresetFlatWorldScreen.this.itemRenderer.renderGuiItem(new ItemStack($$3), $$1 + 2, $$2 + 2);
            }

            private void blitSlotBg(PoseStack $$0, int $$1, int $$2) {
                RenderSystem.setShaderTexture(0, GuiComponent.STATS_ICON_LOCATION);
                GuiComponent.blit($$0, $$1, $$2, PresetFlatWorldScreen.this.getBlitOffset(), 0.0f, 0.0f, 18, 18, 128, 128);
            }

            @Override
            public Component getNarration() {
                return Component.translatable("narrator.select", this.name);
            }
        }
    }
}