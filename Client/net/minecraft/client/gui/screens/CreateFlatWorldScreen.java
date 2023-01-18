/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.function.Consumer
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.PresetFlatWorldScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;

public class CreateFlatWorldScreen
extends Screen {
    private static final int SLOT_TEX_SIZE = 128;
    private static final int SLOT_BG_SIZE = 18;
    private static final int SLOT_STAT_HEIGHT = 20;
    private static final int SLOT_BG_X = 1;
    private static final int SLOT_BG_Y = 1;
    private static final int SLOT_FG_X = 2;
    private static final int SLOT_FG_Y = 2;
    protected final CreateWorldScreen parent;
    private final Consumer<FlatLevelGeneratorSettings> applySettings;
    FlatLevelGeneratorSettings generator;
    private Component columnType;
    private Component columnHeight;
    private DetailsList list;
    private Button deleteLayerButton;

    public CreateFlatWorldScreen(CreateWorldScreen $$0, Consumer<FlatLevelGeneratorSettings> $$1, FlatLevelGeneratorSettings $$2) {
        super(Component.translatable("createWorld.customize.flat.title"));
        this.parent = $$0;
        this.applySettings = $$1;
        this.generator = $$2;
    }

    public FlatLevelGeneratorSettings settings() {
        return this.generator;
    }

    public void setConfig(FlatLevelGeneratorSettings $$0) {
        this.generator = $$0;
    }

    @Override
    protected void init() {
        this.columnType = Component.translatable("createWorld.customize.flat.tile");
        this.columnHeight = Component.translatable("createWorld.customize.flat.height");
        this.list = new DetailsList();
        this.addWidget(this.list);
        this.deleteLayerButton = this.addRenderableWidget(Button.builder(Component.translatable("createWorld.customize.flat.removeLayer"), $$0 -> {
            if (!this.hasValidSelection()) {
                return;
            }
            List<FlatLayerInfo> $$1 = this.generator.getLayersInfo();
            int $$2 = this.list.children().indexOf(this.list.getSelected());
            int $$3 = $$1.size() - $$2 - 1;
            $$1.remove($$3);
            this.list.setSelected($$1.isEmpty() ? null : (DetailsList.Entry)this.list.children().get(Math.min((int)$$2, (int)($$1.size() - 1))));
            this.generator.updateLayers();
            this.list.resetRows();
            this.updateButtonValidity();
        }).bounds(this.width / 2 - 155, this.height - 52, 150, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("createWorld.customize.presets"), $$0 -> {
            this.minecraft.setScreen(new PresetFlatWorldScreen(this));
            this.generator.updateLayers();
            this.updateButtonValidity();
        }).bounds(this.width / 2 + 5, this.height - 52, 150, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, $$0 -> {
            this.applySettings.accept((Object)this.generator);
            this.minecraft.setScreen(this.parent);
            this.generator.updateLayers();
        }).bounds(this.width / 2 - 155, this.height - 28, 150, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, $$0 -> {
            this.minecraft.setScreen(this.parent);
            this.generator.updateLayers();
        }).bounds(this.width / 2 + 5, this.height - 28, 150, 20).build());
        this.generator.updateLayers();
        this.updateButtonValidity();
    }

    void updateButtonValidity() {
        this.deleteLayerButton.active = this.hasValidSelection();
    }

    private boolean hasValidSelection() {
        return this.list.getSelected() != null;
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderBackground($$0);
        this.list.render($$0, $$1, $$2, $$3);
        CreateFlatWorldScreen.drawCenteredString($$0, this.font, this.title, this.width / 2, 8, 0xFFFFFF);
        int $$4 = this.width / 2 - 92 - 16;
        CreateFlatWorldScreen.drawString($$0, this.font, this.columnType, $$4, 32, 0xFFFFFF);
        CreateFlatWorldScreen.drawString($$0, this.font, this.columnHeight, $$4 + 2 + 213 - this.font.width(this.columnHeight), 32, 0xFFFFFF);
        super.render($$0, $$1, $$2, $$3);
    }

    class DetailsList
    extends ObjectSelectionList<Entry> {
        public DetailsList() {
            super(CreateFlatWorldScreen.this.minecraft, CreateFlatWorldScreen.this.width, CreateFlatWorldScreen.this.height, 43, CreateFlatWorldScreen.this.height - 60, 24);
            for (int $$0 = 0; $$0 < CreateFlatWorldScreen.this.generator.getLayersInfo().size(); ++$$0) {
                this.addEntry(new Entry());
            }
        }

        @Override
        public void setSelected(@Nullable Entry $$0) {
            super.setSelected($$0);
            CreateFlatWorldScreen.this.updateButtonValidity();
        }

        @Override
        protected boolean isFocused() {
            return CreateFlatWorldScreen.this.getFocused() == this;
        }

        @Override
        protected int getScrollbarPosition() {
            return this.width - 70;
        }

        public void resetRows() {
            int $$0 = this.children().indexOf(this.getSelected());
            this.clearEntries();
            for (int $$1 = 0; $$1 < CreateFlatWorldScreen.this.generator.getLayersInfo().size(); ++$$1) {
                this.addEntry(new Entry());
            }
            List $$2 = this.children();
            if ($$0 >= 0 && $$0 < $$2.size()) {
                this.setSelected((Entry)$$2.get($$0));
            }
        }

        class Entry
        extends ObjectSelectionList.Entry<Entry> {
            Entry() {
            }

            @Override
            public void render(PoseStack $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
                MutableComponent $$15;
                FlatLayerInfo $$10 = (FlatLayerInfo)CreateFlatWorldScreen.this.generator.getLayersInfo().get(CreateFlatWorldScreen.this.generator.getLayersInfo().size() - $$1 - 1);
                BlockState $$11 = $$10.getBlockState();
                ItemStack $$12 = this.getDisplayItem($$11);
                this.blitSlot($$0, $$3, $$2, $$12);
                CreateFlatWorldScreen.this.font.draw($$0, $$12.getHoverName(), (float)($$3 + 18 + 5), (float)($$2 + 3), 0xFFFFFF);
                if ($$1 == 0) {
                    MutableComponent $$13 = Component.translatable("createWorld.customize.flat.layer.top", $$10.getHeight());
                } else if ($$1 == CreateFlatWorldScreen.this.generator.getLayersInfo().size() - 1) {
                    MutableComponent $$14 = Component.translatable("createWorld.customize.flat.layer.bottom", $$10.getHeight());
                } else {
                    $$15 = Component.translatable("createWorld.customize.flat.layer", $$10.getHeight());
                }
                CreateFlatWorldScreen.this.font.draw($$0, $$15, (float)($$3 + 2 + 213 - CreateFlatWorldScreen.this.font.width($$15)), (float)($$2 + 3), 0xFFFFFF);
            }

            private ItemStack getDisplayItem(BlockState $$0) {
                Item $$1 = $$0.getBlock().asItem();
                if ($$1 == Items.AIR) {
                    if ($$0.is(Blocks.WATER)) {
                        $$1 = Items.WATER_BUCKET;
                    } else if ($$0.is(Blocks.LAVA)) {
                        $$1 = Items.LAVA_BUCKET;
                    }
                }
                return new ItemStack($$1);
            }

            @Override
            public Component getNarration() {
                FlatLayerInfo $$0 = (FlatLayerInfo)CreateFlatWorldScreen.this.generator.getLayersInfo().get(CreateFlatWorldScreen.this.generator.getLayersInfo().size() - DetailsList.this.children().indexOf((Object)this) - 1);
                ItemStack $$1 = this.getDisplayItem($$0.getBlockState());
                if (!$$1.isEmpty()) {
                    return Component.translatable("narrator.select", $$1.getHoverName());
                }
                return CommonComponents.EMPTY;
            }

            @Override
            public boolean mouseClicked(double $$0, double $$1, int $$2) {
                if ($$2 == 0) {
                    DetailsList.this.setSelected(this);
                    return true;
                }
                return false;
            }

            private void blitSlot(PoseStack $$0, int $$1, int $$2, ItemStack $$3) {
                this.blitSlotBg($$0, $$1 + 1, $$2 + 1);
                if (!$$3.isEmpty()) {
                    CreateFlatWorldScreen.this.itemRenderer.renderGuiItem($$3, $$1 + 2, $$2 + 2);
                }
            }

            private void blitSlotBg(PoseStack $$0, int $$1, int $$2) {
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                RenderSystem.setShaderTexture(0, GuiComponent.STATS_ICON_LOCATION);
                GuiComponent.blit($$0, $$1, $$2, CreateFlatWorldScreen.this.getBlitOffset(), 0.0f, 0.0f, 18, 18, 128, 128);
            }
        }
    }
}