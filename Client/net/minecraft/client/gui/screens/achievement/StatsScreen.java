/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Comparator
 *  java.util.List
 *  java.util.Objects
 *  java.util.Set
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.screens.achievement;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.achievement.StatsUpdateListener;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

public class StatsScreen
extends Screen
implements StatsUpdateListener {
    private static final Component PENDING_TEXT = Component.translatable("multiplayer.downloadingStats");
    protected final Screen lastScreen;
    private GeneralStatisticsList statsList;
    ItemStatisticsList itemStatsList;
    private MobsStatisticsList mobsStatsList;
    final StatsCounter stats;
    @Nullable
    private ObjectSelectionList<?> activeList;
    private boolean isLoading = true;
    private static final int SLOT_TEX_SIZE = 128;
    private static final int SLOT_BG_SIZE = 18;
    private static final int SLOT_STAT_HEIGHT = 20;
    private static final int SLOT_BG_X = 1;
    private static final int SLOT_BG_Y = 1;
    private static final int SLOT_FG_X = 2;
    private static final int SLOT_FG_Y = 2;
    private static final int SLOT_LEFT_INSERT = 40;
    private static final int SLOT_TEXT_OFFSET = 5;
    private static final int SORT_NONE = 0;
    private static final int SORT_DOWN = -1;
    private static final int SORT_UP = 1;

    public StatsScreen(Screen $$0, StatsCounter $$1) {
        super(Component.translatable("gui.stats"));
        this.lastScreen = $$0;
        this.stats = $$1;
    }

    @Override
    protected void init() {
        this.isLoading = true;
        this.minecraft.getConnection().send(new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.REQUEST_STATS));
    }

    public void initLists() {
        this.statsList = new GeneralStatisticsList(this.minecraft);
        this.itemStatsList = new ItemStatisticsList(this.minecraft);
        this.mobsStatsList = new MobsStatisticsList(this.minecraft);
    }

    public void initButtons() {
        this.addRenderableWidget(Button.builder(Component.translatable("stat.generalButton"), $$0 -> this.setActiveList(this.statsList)).bounds(this.width / 2 - 120, this.height - 52, 80, 20).build());
        Button $$02 = this.addRenderableWidget(Button.builder(Component.translatable("stat.itemsButton"), $$0 -> this.setActiveList(this.itemStatsList)).bounds(this.width / 2 - 40, this.height - 52, 80, 20).build());
        Button $$1 = this.addRenderableWidget(Button.builder(Component.translatable("stat.mobsButton"), $$0 -> this.setActiveList(this.mobsStatsList)).bounds(this.width / 2 + 40, this.height - 52, 80, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, $$0 -> this.minecraft.setScreen(this.lastScreen)).bounds(this.width / 2 - 100, this.height - 28, 200, 20).build());
        if (this.itemStatsList.children().isEmpty()) {
            $$02.active = false;
        }
        if (this.mobsStatsList.children().isEmpty()) {
            $$1.active = false;
        }
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        if (this.isLoading) {
            this.renderBackground($$0);
            StatsScreen.drawCenteredString($$0, this.font, PENDING_TEXT, this.width / 2, this.height / 2, 0xFFFFFF);
            String string = LOADING_SYMBOLS[(int)(Util.getMillis() / 150L % (long)LOADING_SYMBOLS.length)];
            int n = this.width / 2;
            int n2 = this.height / 2;
            Objects.requireNonNull((Object)this.font);
            StatsScreen.drawCenteredString($$0, this.font, string, n, n2 + 9 * 2, 0xFFFFFF);
        } else {
            this.getActiveList().render($$0, $$1, $$2, $$3);
            StatsScreen.drawCenteredString($$0, this.font, this.title, this.width / 2, 20, 0xFFFFFF);
            super.render($$0, $$1, $$2, $$3);
        }
    }

    @Override
    public void onStatsUpdated() {
        if (this.isLoading) {
            this.initLists();
            this.initButtons();
            this.setActiveList(this.statsList);
            this.isLoading = false;
        }
    }

    @Override
    public boolean isPauseScreen() {
        return !this.isLoading;
    }

    @Nullable
    public ObjectSelectionList<?> getActiveList() {
        return this.activeList;
    }

    public void setActiveList(@Nullable ObjectSelectionList<?> $$0) {
        if (this.activeList != null) {
            this.removeWidget(this.activeList);
        }
        if ($$0 != null) {
            this.addWidget($$0);
            this.activeList = $$0;
        }
    }

    static String getTranslationKey(Stat<ResourceLocation> $$0) {
        return "stat." + $$0.getValue().toString().replace(':', '.');
    }

    int getColumnX(int $$0) {
        return 115 + 40 * $$0;
    }

    void blitSlot(PoseStack $$0, int $$1, int $$2, Item $$3) {
        this.blitSlotIcon($$0, $$1 + 1, $$2 + 1, 0, 0);
        this.itemRenderer.renderGuiItem($$3.getDefaultInstance(), $$1 + 2, $$2 + 2);
    }

    void blitSlotIcon(PoseStack $$0, int $$1, int $$2, int $$3, int $$4) {
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
        RenderSystem.setShaderTexture(0, STATS_ICON_LOCATION);
        StatsScreen.blit($$0, $$1, $$2, this.getBlitOffset(), $$3, $$4, 18, 18, 128, 128);
    }

    class GeneralStatisticsList
    extends ObjectSelectionList<Entry> {
        public GeneralStatisticsList(Minecraft $$02) {
            super($$02, StatsScreen.this.width, StatsScreen.this.height, 32, StatsScreen.this.height - 64, 10);
            ObjectArrayList $$1 = new ObjectArrayList(Stats.CUSTOM.iterator());
            $$1.sort(Comparator.comparing($$0 -> I18n.get(StatsScreen.getTranslationKey($$0), new Object[0])));
            for (Stat $$2 : $$1) {
                this.addEntry(new Entry($$2));
            }
        }

        @Override
        protected void renderBackground(PoseStack $$0) {
            StatsScreen.this.renderBackground($$0);
        }

        class Entry
        extends ObjectSelectionList.Entry<Entry> {
            private final Stat<ResourceLocation> stat;
            private final Component statDisplay;

            Entry(Stat<ResourceLocation> $$0) {
                this.stat = $$0;
                this.statDisplay = Component.translatable(StatsScreen.getTranslationKey($$0));
            }

            private String getValueText() {
                return this.stat.format(StatsScreen.this.stats.getValue(this.stat));
            }

            @Override
            public void render(PoseStack $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
                GuiComponent.drawString($$0, StatsScreen.this.font, this.statDisplay, $$3 + 2, $$2 + 1, $$1 % 2 == 0 ? 0xFFFFFF : 0x909090);
                String $$10 = this.getValueText();
                GuiComponent.drawString($$0, StatsScreen.this.font, $$10, $$3 + 2 + 213 - StatsScreen.this.font.width($$10), $$2 + 1, $$1 % 2 == 0 ? 0xFFFFFF : 0x909090);
            }

            @Override
            public Component getNarration() {
                return Component.translatable("narrator.select", Component.empty().append(this.statDisplay).append(CommonComponents.SPACE).append(this.getValueText()));
            }
        }
    }

    class ItemStatisticsList
    extends ObjectSelectionList<ItemRow> {
        protected final List<StatType<Block>> blockColumns;
        protected final List<StatType<Item>> itemColumns;
        private final int[] iconOffsets;
        protected int headerPressed;
        protected final Comparator<ItemRow> itemStatSorter;
        @Nullable
        protected StatType<?> sortColumn;
        protected int sortOrder;

        public ItemStatisticsList(Minecraft $$0) {
            super($$0, StatsScreen.this.width, StatsScreen.this.height, 32, StatsScreen.this.height - 64, 20);
            this.iconOffsets = new int[]{3, 4, 1, 2, 5, 6};
            this.headerPressed = -1;
            this.itemStatSorter = new ItemRowComparator();
            this.blockColumns = Lists.newArrayList();
            this.blockColumns.add(Stats.BLOCK_MINED);
            this.itemColumns = Lists.newArrayList((Object[])new StatType[]{Stats.ITEM_BROKEN, Stats.ITEM_CRAFTED, Stats.ITEM_USED, Stats.ITEM_PICKED_UP, Stats.ITEM_DROPPED});
            this.setRenderHeader(true, 20);
            Set $$1 = Sets.newIdentityHashSet();
            for (Item $$2 : BuiltInRegistries.ITEM) {
                boolean $$3 = false;
                for (StatType $$4 : this.itemColumns) {
                    if (!$$4.contains($$2) || StatsScreen.this.stats.getValue($$4.get($$2)) <= 0) continue;
                    $$3 = true;
                }
                if (!$$3) continue;
                $$1.add((Object)$$2);
            }
            for (Block $$5 : BuiltInRegistries.BLOCK) {
                boolean $$6 = false;
                for (StatType $$7 : this.blockColumns) {
                    if (!$$7.contains($$5) || StatsScreen.this.stats.getValue($$7.get($$5)) <= 0) continue;
                    $$6 = true;
                }
                if (!$$6) continue;
                $$1.add((Object)$$5.asItem());
            }
            $$1.remove((Object)Items.AIR);
            for (Item $$8 : $$1) {
                this.addEntry(new ItemRow($$8));
            }
        }

        @Override
        protected void renderHeader(PoseStack $$0, int $$1, int $$2) {
            if (!this.minecraft.mouseHandler.isLeftPressed()) {
                this.headerPressed = -1;
            }
            for (int $$3 = 0; $$3 < this.iconOffsets.length; ++$$3) {
                StatsScreen.this.blitSlotIcon($$0, $$1 + StatsScreen.this.getColumnX($$3) - 18, $$2 + 1, 0, this.headerPressed == $$3 ? 0 : 18);
            }
            if (this.sortColumn != null) {
                int $$4 = StatsScreen.this.getColumnX(this.getColumnIndex(this.sortColumn)) - 36;
                int $$5 = this.sortOrder == 1 ? 2 : 1;
                StatsScreen.this.blitSlotIcon($$0, $$1 + $$4, $$2 + 1, 18 * $$5, 0);
            }
            for (int $$6 = 0; $$6 < this.iconOffsets.length; ++$$6) {
                int $$7 = this.headerPressed == $$6 ? 1 : 0;
                StatsScreen.this.blitSlotIcon($$0, $$1 + StatsScreen.this.getColumnX($$6) - 18 + $$7, $$2 + 1 + $$7, 18 * this.iconOffsets[$$6], 18);
            }
        }

        @Override
        public int getRowWidth() {
            return 375;
        }

        @Override
        protected int getScrollbarPosition() {
            return this.width / 2 + 140;
        }

        @Override
        protected void renderBackground(PoseStack $$0) {
            StatsScreen.this.renderBackground($$0);
        }

        @Override
        protected void clickedHeader(int $$0, int $$1) {
            this.headerPressed = -1;
            for (int $$2 = 0; $$2 < this.iconOffsets.length; ++$$2) {
                int $$3 = $$0 - StatsScreen.this.getColumnX($$2);
                if ($$3 < -36 || $$3 > 0) continue;
                this.headerPressed = $$2;
                break;
            }
            if (this.headerPressed >= 0) {
                this.sortByColumn(this.getColumn(this.headerPressed));
                this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            }
        }

        private StatType<?> getColumn(int $$0) {
            return $$0 < this.blockColumns.size() ? (StatType)this.blockColumns.get($$0) : (StatType)this.itemColumns.get($$0 - this.blockColumns.size());
        }

        private int getColumnIndex(StatType<?> $$0) {
            int $$1 = this.blockColumns.indexOf($$0);
            if ($$1 >= 0) {
                return $$1;
            }
            int $$2 = this.itemColumns.indexOf($$0);
            if ($$2 >= 0) {
                return $$2 + this.blockColumns.size();
            }
            return -1;
        }

        @Override
        protected void renderDecorations(PoseStack $$0, int $$1, int $$2) {
            if ($$2 < this.y0 || $$2 > this.y1) {
                return;
            }
            ItemRow $$3 = (ItemRow)this.getHovered();
            int $$4 = (this.width - this.getRowWidth()) / 2;
            if ($$3 != null) {
                if ($$1 < $$4 + 40 || $$1 > $$4 + 40 + 20) {
                    return;
                }
                Item $$5 = $$3.getItem();
                this.renderMousehoverTooltip($$0, this.getString($$5), $$1, $$2);
            } else {
                Component $$6 = null;
                int $$7 = $$1 - $$4;
                for (int $$8 = 0; $$8 < this.iconOffsets.length; ++$$8) {
                    int $$9 = StatsScreen.this.getColumnX($$8);
                    if ($$7 < $$9 - 18 || $$7 > $$9) continue;
                    $$6 = this.getColumn($$8).getDisplayName();
                    break;
                }
                this.renderMousehoverTooltip($$0, $$6, $$1, $$2);
            }
        }

        protected void renderMousehoverTooltip(PoseStack $$0, @Nullable Component $$1, int $$2, int $$3) {
            if ($$1 == null) {
                return;
            }
            int $$4 = $$2 + 12;
            int $$5 = $$3 - 12;
            int $$6 = StatsScreen.this.font.width($$1);
            this.fillGradient($$0, $$4 - 3, $$5 - 3, $$4 + $$6 + 3, $$5 + 8 + 3, -1073741824, -1073741824);
            $$0.pushPose();
            $$0.translate(0.0f, 0.0f, 400.0f);
            StatsScreen.this.font.drawShadow($$0, $$1, (float)$$4, (float)$$5, -1);
            $$0.popPose();
        }

        protected Component getString(Item $$0) {
            return $$0.getDescription();
        }

        protected void sortByColumn(StatType<?> $$0) {
            if ($$0 != this.sortColumn) {
                this.sortColumn = $$0;
                this.sortOrder = -1;
            } else if (this.sortOrder == -1) {
                this.sortOrder = 1;
            } else {
                this.sortColumn = null;
                this.sortOrder = 0;
            }
            this.children().sort(this.itemStatSorter);
        }

        class ItemRowComparator
        implements Comparator<ItemRow> {
            ItemRowComparator() {
            }

            public int compare(ItemRow $$0, ItemRow $$1) {
                int $$11;
                int $$10;
                Item $$2 = $$0.getItem();
                Item $$3 = $$1.getItem();
                if (ItemStatisticsList.this.sortColumn == null) {
                    boolean $$4 = false;
                    boolean $$5 = false;
                } else if (ItemStatisticsList.this.blockColumns.contains(ItemStatisticsList.this.sortColumn)) {
                    StatType<?> $$6 = ItemStatisticsList.this.sortColumn;
                    int $$7 = $$2 instanceof BlockItem ? StatsScreen.this.stats.getValue($$6, ((BlockItem)$$2).getBlock()) : -1;
                    int $$8 = $$3 instanceof BlockItem ? StatsScreen.this.stats.getValue($$6, ((BlockItem)$$3).getBlock()) : -1;
                } else {
                    StatType<?> $$9 = ItemStatisticsList.this.sortColumn;
                    $$10 = StatsScreen.this.stats.getValue($$9, $$2);
                    $$11 = StatsScreen.this.stats.getValue($$9, $$3);
                }
                if ($$10 == $$11) {
                    return ItemStatisticsList.this.sortOrder * Integer.compare((int)Item.getId($$2), (int)Item.getId($$3));
                }
                return ItemStatisticsList.this.sortOrder * Integer.compare((int)$$10, (int)$$11);
            }
        }

        class ItemRow
        extends ObjectSelectionList.Entry<ItemRow> {
            private final Item item;

            ItemRow(Item $$0) {
                this.item = $$0;
            }

            public Item getItem() {
                return this.item;
            }

            @Override
            public void render(PoseStack $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
                StatsScreen.this.blitSlot($$0, $$3 + 40, $$2, this.item);
                for (int $$10 = 0; $$10 < StatsScreen.this.itemStatsList.blockColumns.size(); ++$$10) {
                    Stat<?> $$12;
                    if (this.item instanceof BlockItem) {
                        Stat<Block> $$11 = ((StatType)StatsScreen.this.itemStatsList.blockColumns.get($$10)).get(((BlockItem)this.item).getBlock());
                    } else {
                        $$12 = null;
                    }
                    this.renderStat($$0, $$12, $$3 + StatsScreen.this.getColumnX($$10), $$2, $$1 % 2 == 0);
                }
                for (int $$13 = 0; $$13 < StatsScreen.this.itemStatsList.itemColumns.size(); ++$$13) {
                    this.renderStat($$0, ((StatType)StatsScreen.this.itemStatsList.itemColumns.get($$13)).get(this.item), $$3 + StatsScreen.this.getColumnX($$13 + StatsScreen.this.itemStatsList.blockColumns.size()), $$2, $$1 % 2 == 0);
                }
            }

            protected void renderStat(PoseStack $$0, @Nullable Stat<?> $$1, int $$2, int $$3, boolean $$4) {
                String $$5 = $$1 == null ? "-" : $$1.format(StatsScreen.this.stats.getValue($$1));
                GuiComponent.drawString($$0, StatsScreen.this.font, $$5, $$2 - StatsScreen.this.font.width($$5), $$3 + 5, $$4 ? 0xFFFFFF : 0x909090);
            }

            @Override
            public Component getNarration() {
                return Component.translatable("narrator.select", this.item.getDescription());
            }
        }
    }

    class MobsStatisticsList
    extends ObjectSelectionList<MobRow> {
        public MobsStatisticsList(Minecraft $$0) {
            int n = StatsScreen.this.width;
            int n2 = StatsScreen.this.height;
            int n3 = StatsScreen.this.height - 64;
            Objects.requireNonNull((Object)StatsScreen.this.font);
            super($$0, n, n2, 32, n3, 9 * 4);
            for (EntityType entityType : BuiltInRegistries.ENTITY_TYPE) {
                if (StatsScreen.this.stats.getValue(Stats.ENTITY_KILLED.get(entityType)) <= 0 && StatsScreen.this.stats.getValue(Stats.ENTITY_KILLED_BY.get(entityType)) <= 0) continue;
                this.addEntry(new MobRow(entityType));
            }
        }

        @Override
        protected void renderBackground(PoseStack $$0) {
            StatsScreen.this.renderBackground($$0);
        }

        class MobRow
        extends ObjectSelectionList.Entry<MobRow> {
            private final Component mobName;
            private final Component kills;
            private final boolean hasKills;
            private final Component killedBy;
            private final boolean wasKilledBy;

            public MobRow(EntityType<?> $$0) {
                this.mobName = $$0.getDescription();
                int $$1 = StatsScreen.this.stats.getValue(Stats.ENTITY_KILLED.get($$0));
                if ($$1 == 0) {
                    this.kills = Component.translatable("stat_type.minecraft.killed.none", this.mobName);
                    this.hasKills = false;
                } else {
                    this.kills = Component.translatable("stat_type.minecraft.killed", $$1, this.mobName);
                    this.hasKills = true;
                }
                int $$2 = StatsScreen.this.stats.getValue(Stats.ENTITY_KILLED_BY.get($$0));
                if ($$2 == 0) {
                    this.killedBy = Component.translatable("stat_type.minecraft.killed_by.none", this.mobName);
                    this.wasKilledBy = false;
                } else {
                    this.killedBy = Component.translatable("stat_type.minecraft.killed_by", this.mobName, $$2);
                    this.wasKilledBy = true;
                }
            }

            @Override
            public void render(PoseStack $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
                GuiComponent.drawString($$0, StatsScreen.this.font, this.mobName, $$3 + 2, $$2 + 1, 0xFFFFFF);
                Font font = StatsScreen.this.font;
                Objects.requireNonNull((Object)StatsScreen.this.font);
                GuiComponent.drawString($$0, font, this.kills, $$3 + 2 + 10, $$2 + 1 + 9, this.hasKills ? 0x909090 : 0x606060);
                Font font2 = StatsScreen.this.font;
                Objects.requireNonNull((Object)StatsScreen.this.font);
                GuiComponent.drawString($$0, font2, this.killedBy, $$3 + 2 + 10, $$2 + 1 + 9 * 2, this.wasKilledBy ? 0x909090 : 0x606060);
            }

            @Override
            public Component getNarration() {
                return Component.translatable("narrator.select", CommonComponents.joinForNarration(this.kills, this.killedBy));
            }
        }
    }
}