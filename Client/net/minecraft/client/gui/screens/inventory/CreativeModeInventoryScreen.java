/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.util.Pair
 *  java.lang.CharSequence
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.Collection
 *  java.util.HashSet
 *  java.util.Iterator
 *  java.util.List
 *  java.util.Locale
 *  java.util.Objects
 *  java.util.Set
 *  java.util.function.Predicate
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.screens.inventory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.HotbarManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.CreativeInventoryListener;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.inventory.Hotbar;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.searchtree.SearchRegistry;
import net.minecraft.client.searchtree.SearchTree;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;

public class CreativeModeInventoryScreen
extends EffectRenderingInventoryScreen<ItemPickerMenu> {
    private static final ResourceLocation CREATIVE_TABS_LOCATION = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
    private static final String GUI_CREATIVE_TAB_PREFIX = "textures/gui/container/creative_inventory/tab_";
    private static final String CUSTOM_SLOT_LOCK = "CustomCreativeLock";
    private static final int NUM_ROWS = 5;
    private static final int NUM_COLS = 9;
    private static final int TAB_WIDTH = 26;
    private static final int TAB_HEIGHT = 32;
    private static final int SCROLLER_WIDTH = 12;
    private static final int SCROLLER_HEIGHT = 15;
    static final SimpleContainer CONTAINER = new SimpleContainer(45);
    private static final Component TRASH_SLOT_TOOLTIP = Component.translatable("inventory.binSlot");
    private static final int TEXT_COLOR = 0xFFFFFF;
    private static CreativeModeTab selectedTab = CreativeModeTabs.getDefaultTab();
    private float scrollOffs;
    private boolean scrolling;
    private EditBox searchBox;
    @Nullable
    private List<Slot> originalSlots;
    @Nullable
    private Slot destroyItemSlot;
    private CreativeInventoryListener listener;
    private boolean ignoreTextInput;
    private boolean hasClickedOutside;
    private final Set<TagKey<Item>> visibleTags = new HashSet();
    private final boolean displayOperatorCreativeTab;

    public CreativeModeInventoryScreen(Player $$0, FeatureFlagSet $$1, boolean $$2) {
        super(new ItemPickerMenu($$0), $$0.getInventory(), CommonComponents.EMPTY);
        $$0.containerMenu = this.menu;
        this.passEvents = true;
        this.imageHeight = 136;
        this.imageWidth = 195;
        this.displayOperatorCreativeTab = $$2;
        CreativeModeTabs.tryRebuildTabContents($$1, this.hasPermissions($$0));
    }

    private boolean hasPermissions(Player $$0) {
        return $$0.canUseGameMasterBlocks() && this.displayOperatorCreativeTab;
    }

    private void tryRefreshInvalidatedTabs(FeatureFlagSet $$0, boolean $$1) {
        if (CreativeModeTabs.tryRebuildTabContents($$0, $$1)) {
            for (CreativeModeTab $$2 : CreativeModeTabs.allTabs()) {
                Collection<ItemStack> $$3 = $$2.getDisplayItems();
                if ($$2 != selectedTab) continue;
                if ($$2.getType() == CreativeModeTab.Type.CATEGORY && $$3.isEmpty()) {
                    this.selectTab(CreativeModeTabs.getDefaultTab());
                    continue;
                }
                this.refreshCurrentTabContents($$3);
            }
        }
    }

    private void refreshCurrentTabContents(Collection<ItemStack> $$0) {
        int $$1 = ((ItemPickerMenu)this.menu).getRowIndexForScroll(this.scrollOffs);
        ((ItemPickerMenu)this.menu).items.clear();
        if (selectedTab.getType() == CreativeModeTab.Type.SEARCH) {
            this.refreshSearchResults();
        } else {
            ((ItemPickerMenu)this.menu).items.addAll($$0);
        }
        this.scrollOffs = ((ItemPickerMenu)this.menu).getScrollForRowIndex($$1);
        ((ItemPickerMenu)this.menu).scrollTo(this.scrollOffs);
    }

    @Override
    public void containerTick() {
        super.containerTick();
        if (this.minecraft == null) {
            return;
        }
        if (this.minecraft.player != null) {
            this.tryRefreshInvalidatedTabs(this.minecraft.player.connection.enabledFeatures(), this.hasPermissions(this.minecraft.player));
        }
        if (!this.minecraft.gameMode.hasInfiniteItems()) {
            this.minecraft.setScreen(new InventoryScreen(this.minecraft.player));
        } else {
            this.searchBox.tick();
        }
    }

    @Override
    protected void slotClicked(@Nullable Slot $$0, int $$1, int $$2, ClickType $$3) {
        if (this.isCreativeSlot($$0)) {
            this.searchBox.moveCursorToEnd();
            this.searchBox.setHighlightPos(0);
        }
        boolean $$4 = $$3 == ClickType.QUICK_MOVE;
        ClickType clickType = $$3 = $$1 == -999 && $$3 == ClickType.PICKUP ? ClickType.THROW : $$3;
        if ($$0 != null || selectedTab.getType() == CreativeModeTab.Type.INVENTORY || $$3 == ClickType.QUICK_CRAFT) {
            if ($$0 != null && !$$0.mayPickup(this.minecraft.player)) {
                return;
            }
            if ($$0 == this.destroyItemSlot && $$4) {
                for (int $$5 = 0; $$5 < this.minecraft.player.inventoryMenu.getItems().size(); ++$$5) {
                    this.minecraft.gameMode.handleCreativeModeItemAdd(ItemStack.EMPTY, $$5);
                }
            } else if (selectedTab.getType() == CreativeModeTab.Type.INVENTORY) {
                if ($$0 == this.destroyItemSlot) {
                    ((ItemPickerMenu)this.menu).setCarried(ItemStack.EMPTY);
                } else if ($$3 == ClickType.THROW && $$0 != null && $$0.hasItem()) {
                    ItemStack $$6 = $$0.remove($$2 == 0 ? 1 : $$0.getItem().getMaxStackSize());
                    ItemStack $$7 = $$0.getItem();
                    this.minecraft.player.drop($$6, true);
                    this.minecraft.gameMode.handleCreativeModeItemDrop($$6);
                    this.minecraft.gameMode.handleCreativeModeItemAdd($$7, ((SlotWrapper)$$0).target.index);
                } else if ($$3 == ClickType.THROW && !((ItemPickerMenu)this.menu).getCarried().isEmpty()) {
                    this.minecraft.player.drop(((ItemPickerMenu)this.menu).getCarried(), true);
                    this.minecraft.gameMode.handleCreativeModeItemDrop(((ItemPickerMenu)this.menu).getCarried());
                    ((ItemPickerMenu)this.menu).setCarried(ItemStack.EMPTY);
                } else {
                    this.minecraft.player.inventoryMenu.clicked($$0 == null ? $$1 : ((SlotWrapper)$$0).target.index, $$2, $$3, this.minecraft.player);
                    this.minecraft.player.inventoryMenu.broadcastChanges();
                }
            } else if ($$3 != ClickType.QUICK_CRAFT && $$0.container == CONTAINER) {
                ItemStack $$8 = ((ItemPickerMenu)this.menu).getCarried();
                ItemStack $$9 = $$0.getItem();
                if ($$3 == ClickType.SWAP) {
                    if (!$$9.isEmpty()) {
                        ItemStack $$10 = $$9.copy();
                        $$10.setCount($$10.getMaxStackSize());
                        this.minecraft.player.getInventory().setItem($$2, $$10);
                        this.minecraft.player.inventoryMenu.broadcastChanges();
                    }
                    return;
                }
                if ($$3 == ClickType.CLONE) {
                    if (((ItemPickerMenu)this.menu).getCarried().isEmpty() && $$0.hasItem()) {
                        ItemStack $$11 = $$0.getItem().copy();
                        $$11.setCount($$11.getMaxStackSize());
                        ((ItemPickerMenu)this.menu).setCarried($$11);
                    }
                    return;
                }
                if ($$3 == ClickType.THROW) {
                    if (!$$9.isEmpty()) {
                        ItemStack $$12 = $$9.copy();
                        $$12.setCount($$2 == 0 ? 1 : $$12.getMaxStackSize());
                        this.minecraft.player.drop($$12, true);
                        this.minecraft.gameMode.handleCreativeModeItemDrop($$12);
                    }
                    return;
                }
                if (!$$8.isEmpty() && !$$9.isEmpty() && $$8.sameItem($$9) && ItemStack.tagMatches($$8, $$9)) {
                    if ($$2 == 0) {
                        if ($$4) {
                            $$8.setCount($$8.getMaxStackSize());
                        } else if ($$8.getCount() < $$8.getMaxStackSize()) {
                            $$8.grow(1);
                        }
                    } else {
                        $$8.shrink(1);
                    }
                } else if ($$9.isEmpty() || !$$8.isEmpty()) {
                    if ($$2 == 0) {
                        ((ItemPickerMenu)this.menu).setCarried(ItemStack.EMPTY);
                    } else {
                        ((ItemPickerMenu)this.menu).getCarried().shrink(1);
                    }
                } else {
                    ((ItemPickerMenu)this.menu).setCarried($$9.copy());
                    $$8 = ((ItemPickerMenu)this.menu).getCarried();
                    if ($$4) {
                        $$8.setCount($$8.getMaxStackSize());
                    }
                }
            } else if (this.menu != null) {
                ItemStack $$13 = $$0 == null ? ItemStack.EMPTY : ((ItemPickerMenu)this.menu).getSlot($$0.index).getItem();
                ((ItemPickerMenu)this.menu).clicked($$0 == null ? $$1 : $$0.index, $$2, $$3, this.minecraft.player);
                if (AbstractContainerMenu.getQuickcraftHeader($$2) == 2) {
                    for (int $$14 = 0; $$14 < 9; ++$$14) {
                        this.minecraft.gameMode.handleCreativeModeItemAdd(((ItemPickerMenu)this.menu).getSlot(45 + $$14).getItem(), 36 + $$14);
                    }
                } else if ($$0 != null) {
                    ItemStack $$15 = ((ItemPickerMenu)this.menu).getSlot($$0.index).getItem();
                    this.minecraft.gameMode.handleCreativeModeItemAdd($$15, $$0.index - ((ItemPickerMenu)this.menu).slots.size() + 9 + 36);
                    int $$16 = 45 + $$2;
                    if ($$3 == ClickType.SWAP) {
                        this.minecraft.gameMode.handleCreativeModeItemAdd($$13, $$16 - ((ItemPickerMenu)this.menu).slots.size() + 9 + 36);
                    } else if ($$3 == ClickType.THROW && !$$13.isEmpty()) {
                        ItemStack $$17 = $$13.copy();
                        $$17.setCount($$2 == 0 ? 1 : $$17.getMaxStackSize());
                        this.minecraft.player.drop($$17, true);
                        this.minecraft.gameMode.handleCreativeModeItemDrop($$17);
                    }
                    this.minecraft.player.inventoryMenu.broadcastChanges();
                }
            }
        } else if (!((ItemPickerMenu)this.menu).getCarried().isEmpty() && this.hasClickedOutside) {
            if ($$2 == 0) {
                this.minecraft.player.drop(((ItemPickerMenu)this.menu).getCarried(), true);
                this.minecraft.gameMode.handleCreativeModeItemDrop(((ItemPickerMenu)this.menu).getCarried());
                ((ItemPickerMenu)this.menu).setCarried(ItemStack.EMPTY);
            }
            if ($$2 == 1) {
                ItemStack $$18 = ((ItemPickerMenu)this.menu).getCarried().split(1);
                this.minecraft.player.drop($$18, true);
                this.minecraft.gameMode.handleCreativeModeItemDrop($$18);
            }
        }
    }

    private boolean isCreativeSlot(@Nullable Slot $$0) {
        return $$0 != null && $$0.container == CONTAINER;
    }

    @Override
    protected void init() {
        if (this.minecraft.gameMode.hasInfiniteItems()) {
            super.init();
            Objects.requireNonNull((Object)this.font);
            this.searchBox = new EditBox(this.font, this.leftPos + 82, this.topPos + 6, 80, 9, Component.translatable("itemGroup.search"));
            this.searchBox.setMaxLength(50);
            this.searchBox.setBordered(false);
            this.searchBox.setVisible(false);
            this.searchBox.setTextColor(0xFFFFFF);
            this.addWidget(this.searchBox);
            CreativeModeTab $$0 = selectedTab;
            selectedTab = CreativeModeTabs.getDefaultTab();
            this.selectTab($$0);
            this.minecraft.player.inventoryMenu.removeSlotListener(this.listener);
            this.listener = new CreativeInventoryListener(this.minecraft);
            this.minecraft.player.inventoryMenu.addSlotListener(this.listener);
            if (!selectedTab.shouldDisplay()) {
                this.selectTab(CreativeModeTabs.getDefaultTab());
            }
        } else {
            this.minecraft.setScreen(new InventoryScreen(this.minecraft.player));
        }
    }

    @Override
    public void resize(Minecraft $$0, int $$1, int $$2) {
        int $$3 = ((ItemPickerMenu)this.menu).getRowIndexForScroll(this.scrollOffs);
        String $$4 = this.searchBox.getValue();
        this.init($$0, $$1, $$2);
        this.searchBox.setValue($$4);
        if (!this.searchBox.getValue().isEmpty()) {
            this.refreshSearchResults();
        }
        this.scrollOffs = ((ItemPickerMenu)this.menu).getScrollForRowIndex($$3);
        ((ItemPickerMenu)this.menu).scrollTo(this.scrollOffs);
    }

    @Override
    public void removed() {
        super.removed();
        if (this.minecraft.player != null && this.minecraft.player.getInventory() != null) {
            this.minecraft.player.inventoryMenu.removeSlotListener(this.listener);
        }
    }

    @Override
    public boolean charTyped(char $$0, int $$1) {
        if (this.ignoreTextInput) {
            return false;
        }
        if (selectedTab.getType() != CreativeModeTab.Type.SEARCH) {
            return false;
        }
        String $$2 = this.searchBox.getValue();
        if (this.searchBox.charTyped($$0, $$1)) {
            if (!Objects.equals((Object)$$2, (Object)this.searchBox.getValue())) {
                this.refreshSearchResults();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        this.ignoreTextInput = false;
        if (selectedTab.getType() != CreativeModeTab.Type.SEARCH) {
            if (this.minecraft.options.keyChat.matches($$0, $$1)) {
                this.ignoreTextInput = true;
                this.selectTab(CreativeModeTabs.searchTab());
                return true;
            }
            return super.keyPressed($$0, $$1, $$2);
        }
        boolean $$3 = !this.isCreativeSlot(this.hoveredSlot) || this.hoveredSlot.hasItem();
        boolean $$4 = InputConstants.getKey($$0, $$1).getNumericKeyValue().isPresent();
        if ($$3 && $$4 && this.checkHotbarKeyPressed($$0, $$1)) {
            this.ignoreTextInput = true;
            return true;
        }
        String $$5 = this.searchBox.getValue();
        if (this.searchBox.keyPressed($$0, $$1, $$2)) {
            if (!Objects.equals((Object)$$5, (Object)this.searchBox.getValue())) {
                this.refreshSearchResults();
            }
            return true;
        }
        if (this.searchBox.isFocused() && this.searchBox.isVisible() && $$0 != 256) {
            return true;
        }
        return super.keyPressed($$0, $$1, $$2);
    }

    @Override
    public boolean keyReleased(int $$0, int $$1, int $$2) {
        this.ignoreTextInput = false;
        return super.keyReleased($$0, $$1, $$2);
    }

    private void refreshSearchResults() {
        ((ItemPickerMenu)this.menu).items.clear();
        this.visibleTags.clear();
        String $$0 = this.searchBox.getValue();
        if ($$0.isEmpty()) {
            ((ItemPickerMenu)this.menu).items.addAll(selectedTab.getDisplayItems());
        } else {
            SearchTree<ItemStack> $$2;
            if ($$0.startsWith("#")) {
                $$0 = $$0.substring(1);
                SearchTree<ItemStack> $$1 = this.minecraft.getSearchTree(SearchRegistry.CREATIVE_TAGS);
                this.updateVisibleTags($$0);
            } else {
                $$2 = this.minecraft.getSearchTree(SearchRegistry.CREATIVE_NAMES);
            }
            ((ItemPickerMenu)this.menu).items.addAll((Collection)$$2.search($$0.toLowerCase(Locale.ROOT)));
        }
        this.scrollOffs = 0.0f;
        ((ItemPickerMenu)this.menu).scrollTo(0.0f);
    }

    private void updateVisibleTags(String $$0) {
        Predicate $$5;
        int $$12 = $$0.indexOf(58);
        if ($$12 == -1) {
            Predicate $$22 = $$1 -> $$1.getPath().contains((CharSequence)$$0);
        } else {
            String $$3 = $$0.substring(0, $$12).trim();
            String $$4 = $$0.substring($$12 + 1).trim();
            $$5 = $$2 -> $$2.getNamespace().contains((CharSequence)$$3) && $$2.getPath().contains((CharSequence)$$4);
        }
        BuiltInRegistries.ITEM.getTagNames().filter($$1 -> $$5.test((Object)$$1.location())).forEach(arg_0 -> this.visibleTags.add(arg_0));
    }

    @Override
    protected void renderLabels(PoseStack $$0, int $$1, int $$2) {
        if (selectedTab.showTitle()) {
            RenderSystem.disableBlend();
            this.font.draw($$0, selectedTab.getDisplayName(), 8.0f, 6.0f, 0x404040);
        }
    }

    @Override
    public boolean mouseClicked(double $$0, double $$1, int $$2) {
        if ($$2 == 0) {
            double $$3 = $$0 - (double)this.leftPos;
            double $$4 = $$1 - (double)this.topPos;
            for (CreativeModeTab $$5 : CreativeModeTabs.tabs()) {
                if (!this.checkTabClicked($$5, $$3, $$4)) continue;
                return true;
            }
            if (selectedTab.getType() != CreativeModeTab.Type.INVENTORY && this.insideScrollbar($$0, $$1)) {
                this.scrolling = this.canScroll();
                return true;
            }
        }
        return super.mouseClicked($$0, $$1, $$2);
    }

    @Override
    public boolean mouseReleased(double $$0, double $$1, int $$2) {
        if ($$2 == 0) {
            double $$3 = $$0 - (double)this.leftPos;
            double $$4 = $$1 - (double)this.topPos;
            this.scrolling = false;
            for (CreativeModeTab $$5 : CreativeModeTabs.tabs()) {
                if (!this.checkTabClicked($$5, $$3, $$4)) continue;
                this.selectTab($$5);
                return true;
            }
        }
        return super.mouseReleased($$0, $$1, $$2);
    }

    private boolean canScroll() {
        return selectedTab.canScroll() && ((ItemPickerMenu)this.menu).canScroll();
    }

    private void selectTab(CreativeModeTab $$0) {
        CreativeModeTab $$1 = selectedTab;
        selectedTab = $$0;
        this.quickCraftSlots.clear();
        ((ItemPickerMenu)this.menu).items.clear();
        this.clearDraggingState();
        if (selectedTab.getType() == CreativeModeTab.Type.HOTBAR) {
            HotbarManager $$2 = this.minecraft.getHotbarManager();
            for (int $$3 = 0; $$3 < 9; ++$$3) {
                Hotbar $$4 = $$2.get($$3);
                if ($$4.isEmpty()) {
                    for (int $$5 = 0; $$5 < 9; ++$$5) {
                        if ($$5 == $$3) {
                            ItemStack $$6 = new ItemStack(Items.PAPER);
                            $$6.getOrCreateTagElement(CUSTOM_SLOT_LOCK);
                            Component $$7 = this.minecraft.options.keyHotbarSlots[$$3].getTranslatedKeyMessage();
                            Component $$8 = this.minecraft.options.keySaveHotbarActivator.getTranslatedKeyMessage();
                            $$6.setHoverName(Component.translatable("inventory.hotbarInfo", $$8, $$7));
                            ((ItemPickerMenu)this.menu).items.add($$6);
                            continue;
                        }
                        ((ItemPickerMenu)this.menu).items.add(ItemStack.EMPTY);
                    }
                    continue;
                }
                ((ItemPickerMenu)this.menu).items.addAll((Collection)$$4);
            }
        } else if (selectedTab.getType() == CreativeModeTab.Type.CATEGORY) {
            ((ItemPickerMenu)this.menu).items.addAll(selectedTab.getDisplayItems());
        }
        if (selectedTab.getType() == CreativeModeTab.Type.INVENTORY) {
            InventoryMenu $$9 = this.minecraft.player.inventoryMenu;
            if (this.originalSlots == null) {
                this.originalSlots = ImmutableList.copyOf((Collection)((ItemPickerMenu)this.menu).slots);
            }
            ((ItemPickerMenu)this.menu).slots.clear();
            for (int $$10 = 0; $$10 < $$9.slots.size(); ++$$10) {
                int $$25;
                int $$23;
                if ($$10 >= 5 && $$10 < 9) {
                    int $$11 = $$10 - 5;
                    int $$12 = $$11 / 2;
                    int $$13 = $$11 % 2;
                    int $$14 = 54 + $$12 * 54;
                    int $$15 = 6 + $$13 * 27;
                } else if ($$10 >= 0 && $$10 < 5) {
                    int $$16 = -2000;
                    int $$17 = -2000;
                } else if ($$10 == 45) {
                    int $$18 = 35;
                    int $$19 = 20;
                } else {
                    int $$20 = $$10 - 9;
                    int $$21 = $$20 % 9;
                    int $$22 = $$20 / 9;
                    $$23 = 9 + $$21 * 18;
                    if ($$10 >= 36) {
                        int $$24 = 112;
                    } else {
                        $$25 = 54 + $$22 * 18;
                    }
                }
                SlotWrapper $$26 = new SlotWrapper($$9.slots.get($$10), $$10, $$23, $$25);
                ((ItemPickerMenu)this.menu).slots.add($$26);
            }
            this.destroyItemSlot = new Slot(CONTAINER, 0, 173, 112);
            ((ItemPickerMenu)this.menu).slots.add(this.destroyItemSlot);
        } else if ($$1.getType() == CreativeModeTab.Type.INVENTORY) {
            ((ItemPickerMenu)this.menu).slots.clear();
            ((ItemPickerMenu)this.menu).slots.addAll((Collection)this.originalSlots);
            this.originalSlots = null;
        }
        if (selectedTab.getType() == CreativeModeTab.Type.SEARCH) {
            this.searchBox.setVisible(true);
            this.searchBox.setCanLoseFocus(false);
            this.searchBox.setFocus(true);
            if ($$1 != $$0) {
                this.searchBox.setValue("");
            }
            this.refreshSearchResults();
        } else {
            this.searchBox.setVisible(false);
            this.searchBox.setCanLoseFocus(true);
            this.searchBox.setFocus(false);
            this.searchBox.setValue("");
        }
        this.scrollOffs = 0.0f;
        ((ItemPickerMenu)this.menu).scrollTo(0.0f);
    }

    @Override
    public boolean mouseScrolled(double $$0, double $$1, double $$2) {
        if (!this.canScroll()) {
            return false;
        }
        this.scrollOffs = ((ItemPickerMenu)this.menu).subtractInputFromScroll(this.scrollOffs, $$2);
        ((ItemPickerMenu)this.menu).scrollTo(this.scrollOffs);
        return true;
    }

    @Override
    protected boolean hasClickedOutside(double $$0, double $$1, int $$2, int $$3, int $$4) {
        boolean $$5 = $$0 < (double)$$2 || $$1 < (double)$$3 || $$0 >= (double)($$2 + this.imageWidth) || $$1 >= (double)($$3 + this.imageHeight);
        this.hasClickedOutside = $$5 && !this.checkTabClicked(selectedTab, $$0, $$1);
        return this.hasClickedOutside;
    }

    protected boolean insideScrollbar(double $$0, double $$1) {
        int $$2 = this.leftPos;
        int $$3 = this.topPos;
        int $$4 = $$2 + 175;
        int $$5 = $$3 + 18;
        int $$6 = $$4 + 14;
        int $$7 = $$5 + 112;
        return $$0 >= (double)$$4 && $$1 >= (double)$$5 && $$0 < (double)$$6 && $$1 < (double)$$7;
    }

    @Override
    public boolean mouseDragged(double $$0, double $$1, int $$2, double $$3, double $$4) {
        if (this.scrolling) {
            int $$5 = this.topPos + 18;
            int $$6 = $$5 + 112;
            this.scrollOffs = ((float)$$1 - (float)$$5 - 7.5f) / ((float)($$6 - $$5) - 15.0f);
            this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0f, 1.0f);
            ((ItemPickerMenu)this.menu).scrollTo(this.scrollOffs);
            return true;
        }
        return super.mouseDragged($$0, $$1, $$2, $$3, $$4);
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        CreativeModeTab $$4;
        this.renderBackground($$0);
        super.render($$0, $$1, $$2, $$3);
        Iterator iterator = CreativeModeTabs.tabs().iterator();
        while (iterator.hasNext() && !this.checkTabHovering($$0, $$4 = (CreativeModeTab)iterator.next(), $$1, $$2)) {
        }
        if (this.destroyItemSlot != null && selectedTab.getType() == CreativeModeTab.Type.INVENTORY && this.isHovering(this.destroyItemSlot.x, this.destroyItemSlot.y, 16, 16, $$1, $$2)) {
            this.renderTooltip($$0, TRASH_SLOT_TOOLTIP, $$1, $$2);
        }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        this.renderTooltip($$0, $$1, $$2);
    }

    @Override
    protected void renderTooltip(PoseStack $$0, ItemStack $$1, int $$2, int $$3) {
        List<Component> $$13;
        boolean $$4 = this.hoveredSlot != null && this.hoveredSlot instanceof CustomCreativeSlot;
        boolean $$5 = selectedTab.getType() == CreativeModeTab.Type.CATEGORY;
        boolean $$6 = selectedTab.getType() == CreativeModeTab.Type.SEARCH;
        TooltipFlag.Default $$7 = this.minecraft.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL;
        TooltipFlag.Default $$8 = $$4 ? $$7.asCreative() : $$7;
        List<Component> $$9 = $$1.getTooltipLines(this.minecraft.player, $$8);
        if (!$$5 || !$$4) {
            ArrayList $$10 = Lists.newArrayList($$9);
            if ($$6 && $$4) {
                this.visibleTags.forEach(arg_0 -> CreativeModeInventoryScreen.lambda$renderTooltip$3($$1, (List)$$10, arg_0));
            }
            int $$11 = 1;
            for (CreativeModeTab $$12 : CreativeModeTabs.tabs()) {
                if ($$12.getType() == CreativeModeTab.Type.SEARCH || !$$12.contains($$1)) continue;
                $$10.add($$11++, (Object)$$12.getDisplayName().copy().withStyle(ChatFormatting.BLUE));
            }
        } else {
            $$13 = $$9;
        }
        this.renderTooltip($$0, $$13, $$1.getTooltipImage(), $$2, $$3);
    }

    @Override
    protected void renderBg(PoseStack $$0, float $$1, int $$2, int $$3) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        for (CreativeModeTab $$4 : CreativeModeTabs.tabs()) {
            RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
            RenderSystem.setShaderTexture(0, CREATIVE_TABS_LOCATION);
            if ($$4 == selectedTab) continue;
            this.renderTabButton($$0, $$4);
        }
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
        RenderSystem.setShaderTexture(0, new ResourceLocation(GUI_CREATIVE_TAB_PREFIX + selectedTab.getBackgroundSuffix()));
        this.blit($$0, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        this.searchBox.render($$0, $$2, $$3, $$1);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        int $$5 = this.leftPos + 175;
        int $$6 = this.topPos + 18;
        int $$7 = $$6 + 112;
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
        RenderSystem.setShaderTexture(0, CREATIVE_TABS_LOCATION);
        if (selectedTab.canScroll()) {
            this.blit($$0, $$5, $$6 + (int)((float)($$7 - $$6 - 17) * this.scrollOffs), 232 + (this.canScroll() ? 0 : 12), 0, 12, 15);
        }
        this.renderTabButton($$0, selectedTab);
        if (selectedTab.getType() == CreativeModeTab.Type.INVENTORY) {
            InventoryScreen.renderEntityInInventory(this.leftPos + 88, this.topPos + 45, 20, this.leftPos + 88 - $$2, this.topPos + 45 - 30 - $$3, this.minecraft.player);
        }
    }

    private int getTabX(CreativeModeTab $$0) {
        int $$1 = $$0.column();
        int $$2 = 27;
        int $$3 = 27 * $$1;
        if ($$0.isAlignedRight()) {
            $$3 = this.imageWidth - 27 * (7 - $$1) + 1;
        }
        return $$3;
    }

    private int getTabY(CreativeModeTab $$0) {
        int $$1 = 0;
        $$1 = $$0.row() == CreativeModeTab.Row.TOP ? ($$1 -= 32) : ($$1 += this.imageHeight);
        return $$1;
    }

    protected boolean checkTabClicked(CreativeModeTab $$0, double $$1, double $$2) {
        int $$3 = this.getTabX($$0);
        int $$4 = this.getTabY($$0);
        return $$1 >= (double)$$3 && $$1 <= (double)($$3 + 26) && $$2 >= (double)$$4 && $$2 <= (double)($$4 + 32);
    }

    protected boolean checkTabHovering(PoseStack $$0, CreativeModeTab $$1, int $$2, int $$3) {
        int $$5;
        int $$4 = this.getTabX($$1);
        if (this.isHovering($$4 + 3, ($$5 = this.getTabY($$1)) + 3, 21, 27, $$2, $$3)) {
            this.renderTooltip($$0, $$1.getDisplayName(), $$2, $$3);
            return true;
        }
        return false;
    }

    protected void renderTabButton(PoseStack $$0, CreativeModeTab $$1) {
        boolean $$2 = $$1 == selectedTab;
        boolean $$3 = $$1.row() == CreativeModeTab.Row.TOP;
        int $$4 = $$1.column();
        int $$5 = $$4 * 26;
        int $$6 = 0;
        int $$7 = this.leftPos + this.getTabX($$1);
        int $$8 = this.topPos;
        int $$9 = 32;
        if ($$2) {
            $$6 += 32;
        }
        if ($$3) {
            $$8 -= 28;
        } else {
            $$6 += 64;
            $$8 += this.imageHeight - 4;
        }
        this.blit($$0, $$7, $$8, $$5, $$6, 26, 32);
        this.itemRenderer.blitOffset = 100.0f;
        int n = $$3 ? 1 : -1;
        ItemStack $$10 = $$1.getIconItem();
        this.itemRenderer.renderAndDecorateItem($$10, $$7 += 5, $$8 += 8 + n);
        this.itemRenderer.renderGuiItemDecorations(this.font, $$10, $$7, $$8);
        this.itemRenderer.blitOffset = 0.0f;
    }

    public boolean isInventoryOpen() {
        return selectedTab.getType() == CreativeModeTab.Type.INVENTORY;
    }

    public static void handleHotbarLoadOrSave(Minecraft $$0, int $$1, boolean $$2, boolean $$3) {
        LocalPlayer $$4 = $$0.player;
        HotbarManager $$5 = $$0.getHotbarManager();
        Hotbar $$6 = $$5.get($$1);
        if ($$2) {
            for (int $$7 = 0; $$7 < Inventory.getSelectionSize(); ++$$7) {
                ItemStack $$8 = (ItemStack)$$6.get($$7);
                ItemStack $$9 = $$8.isItemEnabled($$4.level.enabledFeatures()) ? $$8.copy() : ItemStack.EMPTY;
                $$4.getInventory().setItem($$7, $$9);
                $$0.gameMode.handleCreativeModeItemAdd($$9, 36 + $$7);
            }
            $$4.inventoryMenu.broadcastChanges();
        } else if ($$3) {
            for (int $$10 = 0; $$10 < Inventory.getSelectionSize(); ++$$10) {
                $$6.set($$10, $$4.getInventory().getItem($$10).copy());
            }
            Component $$11 = $$0.options.keyHotbarSlots[$$1].getTranslatedKeyMessage();
            Component $$12 = $$0.options.keyLoadHotbarActivator.getTranslatedKeyMessage();
            MutableComponent $$13 = Component.translatable("inventory.hotbarSaved", $$12, $$11);
            $$0.gui.setOverlayMessage($$13, false);
            $$0.getNarrator().sayNow($$13);
            $$5.save();
        }
    }

    private static /* synthetic */ void lambda$renderTooltip$3(ItemStack $$0, List $$1, TagKey $$2) {
        if ($$0.is($$2)) {
            $$1.add(1, (Object)Component.literal("#" + $$2.location()).withStyle(ChatFormatting.DARK_PURPLE));
        }
    }

    public static class ItemPickerMenu
    extends AbstractContainerMenu {
        public final NonNullList<ItemStack> items = NonNullList.create();
        private final AbstractContainerMenu inventoryMenu;

        public ItemPickerMenu(Player $$0) {
            super(null, 0);
            this.inventoryMenu = $$0.inventoryMenu;
            Inventory $$1 = $$0.getInventory();
            for (int $$2 = 0; $$2 < 5; ++$$2) {
                for (int $$3 = 0; $$3 < 9; ++$$3) {
                    this.addSlot(new CustomCreativeSlot(CONTAINER, $$2 * 9 + $$3, 9 + $$3 * 18, 18 + $$2 * 18));
                }
            }
            for (int $$4 = 0; $$4 < 9; ++$$4) {
                this.addSlot(new Slot($$1, $$4, 9 + $$4 * 18, 112));
            }
            this.scrollTo(0.0f);
        }

        @Override
        public boolean stillValid(Player $$0) {
            return true;
        }

        protected int calculateRowCount() {
            return Mth.positiveCeilDiv(this.items.size(), 9) - 5;
        }

        protected int getRowIndexForScroll(float $$0) {
            return Math.max((int)((int)((double)($$0 * (float)this.calculateRowCount()) + 0.5)), (int)0);
        }

        protected float getScrollForRowIndex(int $$0) {
            return Mth.clamp((float)$$0 / (float)this.calculateRowCount(), 0.0f, 1.0f);
        }

        protected float subtractInputFromScroll(float $$0, double $$1) {
            return Mth.clamp($$0 - (float)($$1 / (double)this.calculateRowCount()), 0.0f, 1.0f);
        }

        public void scrollTo(float $$0) {
            int $$1 = this.getRowIndexForScroll($$0);
            for (int $$2 = 0; $$2 < 5; ++$$2) {
                for (int $$3 = 0; $$3 < 9; ++$$3) {
                    int $$4 = $$3 + ($$2 + $$1) * 9;
                    if ($$4 >= 0 && $$4 < this.items.size()) {
                        CONTAINER.setItem($$3 + $$2 * 9, this.items.get($$4));
                        continue;
                    }
                    CONTAINER.setItem($$3 + $$2 * 9, ItemStack.EMPTY);
                }
            }
        }

        public boolean canScroll() {
            return this.items.size() > 45;
        }

        @Override
        public ItemStack quickMoveStack(Player $$0, int $$1) {
            Slot $$2;
            if ($$1 >= this.slots.size() - 9 && $$1 < this.slots.size() && ($$2 = (Slot)this.slots.get($$1)) != null && $$2.hasItem()) {
                $$2.set(ItemStack.EMPTY);
            }
            return ItemStack.EMPTY;
        }

        @Override
        public boolean canTakeItemForPickAll(ItemStack $$0, Slot $$1) {
            return $$1.container != CONTAINER;
        }

        @Override
        public boolean canDragTo(Slot $$0) {
            return $$0.container != CONTAINER;
        }

        @Override
        public ItemStack getCarried() {
            return this.inventoryMenu.getCarried();
        }

        @Override
        public void setCarried(ItemStack $$0) {
            this.inventoryMenu.setCarried($$0);
        }
    }

    static class SlotWrapper
    extends Slot {
        final Slot target;

        public SlotWrapper(Slot $$0, int $$1, int $$2, int $$3) {
            super($$0.container, $$1, $$2, $$3);
            this.target = $$0;
        }

        @Override
        public void onTake(Player $$0, ItemStack $$1) {
            this.target.onTake($$0, $$1);
        }

        @Override
        public boolean mayPlace(ItemStack $$0) {
            return this.target.mayPlace($$0);
        }

        @Override
        public ItemStack getItem() {
            return this.target.getItem();
        }

        @Override
        public boolean hasItem() {
            return this.target.hasItem();
        }

        @Override
        public void set(ItemStack $$0) {
            this.target.set($$0);
        }

        @Override
        public void setChanged() {
            this.target.setChanged();
        }

        @Override
        public int getMaxStackSize() {
            return this.target.getMaxStackSize();
        }

        @Override
        public int getMaxStackSize(ItemStack $$0) {
            return this.target.getMaxStackSize($$0);
        }

        @Override
        @Nullable
        public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
            return this.target.getNoItemIcon();
        }

        @Override
        public ItemStack remove(int $$0) {
            return this.target.remove($$0);
        }

        @Override
        public boolean isActive() {
            return this.target.isActive();
        }

        @Override
        public boolean mayPickup(Player $$0) {
            return this.target.mayPickup($$0);
        }
    }

    static class CustomCreativeSlot
    extends Slot {
        public CustomCreativeSlot(Container $$0, int $$1, int $$2, int $$3) {
            super($$0, $$1, $$2, $$3);
        }

        @Override
        public boolean mayPickup(Player $$0) {
            ItemStack $$1 = this.getItem();
            if (super.mayPickup($$0) && !$$1.isEmpty()) {
                return $$1.isItemEnabled($$0.level.enabledFeatures()) && $$1.getTagElement(CreativeModeInventoryScreen.CUSTOM_SLOT_LOCK) == null;
            }
            return $$1.isEmpty();
        }
    }
}