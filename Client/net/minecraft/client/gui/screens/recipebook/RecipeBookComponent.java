/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet
 *  it.unimi.dsi.fastutil.objects.ObjectSet
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.Iterator
 *  java.util.List
 *  java.util.Locale
 *  java.util.Objects
 *  java.util.function.Consumer
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.screens.recipebook;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.recipebook.GhostRecipe;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import net.minecraft.client.gui.screens.recipebook.RecipeBookTabButton;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.gui.screens.recipebook.RecipeShownListener;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.client.searchtree.SearchRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundRecipeBookChangeSettingsPacket;
import net.minecraft.recipebook.PlaceRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

public class RecipeBookComponent
extends GuiComponent
implements PlaceRecipe<Ingredient>,
Renderable,
GuiEventListener,
NarratableEntry,
RecipeShownListener {
    protected static final ResourceLocation RECIPE_BOOK_LOCATION = new ResourceLocation("textures/gui/recipe_book.png");
    private static final Component SEARCH_HINT = Component.translatable("gui.recipebook.search_hint").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY);
    public static final int IMAGE_WIDTH = 147;
    public static final int IMAGE_HEIGHT = 166;
    private static final int OFFSET_X_POSITION = 86;
    private static final Component ONLY_CRAFTABLES_TOOLTIP = Component.translatable("gui.recipebook.toggleRecipes.craftable");
    private static final Component ALL_RECIPES_TOOLTIP = Component.translatable("gui.recipebook.toggleRecipes.all");
    private int xOffset;
    private int width;
    private int height;
    protected final GhostRecipe ghostRecipe = new GhostRecipe();
    private final List<RecipeBookTabButton> tabButtons = Lists.newArrayList();
    @Nullable
    private RecipeBookTabButton selectedTab;
    protected StateSwitchingButton filterButton;
    protected RecipeBookMenu<?> menu;
    protected Minecraft minecraft;
    @Nullable
    private EditBox searchBox;
    private String lastSearch = "";
    private ClientRecipeBook book;
    private final RecipeBookPage recipeBookPage = new RecipeBookPage();
    private final StackedContents stackedContents = new StackedContents();
    private int timesInventoryChanged;
    private boolean ignoreTextInput;
    private boolean visible;
    private boolean widthTooNarrow;

    public void init(int $$0, int $$1, Minecraft $$2, boolean $$3, RecipeBookMenu<?> $$4) {
        this.minecraft = $$2;
        this.width = $$0;
        this.height = $$1;
        this.menu = $$4;
        this.widthTooNarrow = $$3;
        $$2.player.containerMenu = $$4;
        this.book = $$2.player.getRecipeBook();
        this.timesInventoryChanged = $$2.player.getInventory().getTimesChanged();
        this.visible = this.isVisibleAccordingToBookData();
        if (this.visible) {
            this.initVisuals();
        }
    }

    public void initVisuals() {
        this.xOffset = this.widthTooNarrow ? 0 : 86;
        int $$02 = (this.width - 147) / 2 - this.xOffset;
        int $$1 = (this.height - 166) / 2;
        this.stackedContents.clear();
        this.minecraft.player.getInventory().fillStackedContents(this.stackedContents);
        this.menu.fillCraftSlotsStackedContents(this.stackedContents);
        String $$2 = this.searchBox != null ? this.searchBox.getValue() : "";
        Font font = this.minecraft.font;
        Objects.requireNonNull((Object)this.minecraft.font);
        this.searchBox = new EditBox(font, $$02 + 26, $$1 + 14, 79, 9 + 3, Component.translatable("itemGroup.search"));
        this.searchBox.setMaxLength(50);
        this.searchBox.setVisible(true);
        this.searchBox.setTextColor(0xFFFFFF);
        this.searchBox.setValue($$2);
        this.searchBox.setHint(SEARCH_HINT);
        this.recipeBookPage.init(this.minecraft, $$02, $$1);
        this.recipeBookPage.addListener(this);
        this.filterButton = new StateSwitchingButton($$02 + 110, $$1 + 12, 26, 16, this.book.isFiltering(this.menu));
        this.updateFilterButtonTooltip();
        this.initFilterButtonTextures();
        this.tabButtons.clear();
        for (RecipeBookCategories $$3 : RecipeBookCategories.getCategories(this.menu.getRecipeBookType())) {
            this.tabButtons.add((Object)new RecipeBookTabButton($$3));
        }
        if (this.selectedTab != null) {
            this.selectedTab = (RecipeBookTabButton)this.tabButtons.stream().filter($$0 -> $$0.getCategory().equals((Object)this.selectedTab.getCategory())).findFirst().orElse(null);
        }
        if (this.selectedTab == null) {
            this.selectedTab = (RecipeBookTabButton)this.tabButtons.get(0);
        }
        this.selectedTab.setStateTriggered(true);
        this.updateCollections(false);
        this.updateTabs();
    }

    private void updateFilterButtonTooltip() {
        this.filterButton.setTooltip(this.filterButton.isStateTriggered() ? Tooltip.create(this.getRecipeFilterName()) : Tooltip.create(ALL_RECIPES_TOOLTIP));
    }

    protected void initFilterButtonTextures() {
        this.filterButton.initTextureValues(152, 41, 28, 18, RECIPE_BOOK_LOCATION);
    }

    public int updateScreenPosition(int $$0, int $$1) {
        int $$3;
        if (this.isVisible() && !this.widthTooNarrow) {
            int $$2 = 177 + ($$0 - $$1 - 200) / 2;
        } else {
            $$3 = ($$0 - $$1) / 2;
        }
        return $$3;
    }

    public void toggleVisibility() {
        this.setVisible(!this.isVisible());
    }

    public boolean isVisible() {
        return this.visible;
    }

    private boolean isVisibleAccordingToBookData() {
        return this.book.isOpen(this.menu.getRecipeBookType());
    }

    protected void setVisible(boolean $$0) {
        if ($$0) {
            this.initVisuals();
        }
        this.visible = $$0;
        this.book.setOpen(this.menu.getRecipeBookType(), $$0);
        if (!$$0) {
            this.recipeBookPage.setInvisible();
        }
        this.sendUpdateSettings();
    }

    public void slotClicked(@Nullable Slot $$0) {
        if ($$0 != null && $$0.index < this.menu.getSize()) {
            this.ghostRecipe.clear();
            if (this.isVisible()) {
                this.updateStackedContents();
            }
        }
    }

    private void updateCollections(boolean $$02) {
        List<RecipeCollection> $$1 = this.book.getCollection(this.selectedTab.getCategory());
        $$1.forEach($$0 -> $$0.canCraft(this.stackedContents, this.menu.getGridWidth(), this.menu.getGridHeight(), this.book));
        ArrayList $$2 = Lists.newArrayList($$1);
        $$2.removeIf($$0 -> !$$0.hasKnownRecipes());
        $$2.removeIf($$0 -> !$$0.hasFitting());
        String $$3 = this.searchBox.getValue();
        if (!$$3.isEmpty()) {
            ObjectLinkedOpenHashSet $$4 = new ObjectLinkedOpenHashSet(this.minecraft.getSearchTree(SearchRegistry.RECIPE_COLLECTIONS).search($$3.toLowerCase(Locale.ROOT)));
            $$2.removeIf(arg_0 -> RecipeBookComponent.lambda$updateCollections$4((ObjectSet)$$4, arg_0));
        }
        if (this.book.isFiltering(this.menu)) {
            $$2.removeIf($$0 -> !$$0.hasCraftable());
        }
        this.recipeBookPage.updateCollections((List<RecipeCollection>)$$2, $$02);
    }

    private void updateTabs() {
        int $$0 = (this.width - 147) / 2 - this.xOffset - 30;
        int $$1 = (this.height - 166) / 2 + 3;
        int $$2 = 27;
        int $$3 = 0;
        for (RecipeBookTabButton $$4 : this.tabButtons) {
            RecipeBookCategories $$5 = $$4.getCategory();
            if ($$5 == RecipeBookCategories.CRAFTING_SEARCH || $$5 == RecipeBookCategories.FURNACE_SEARCH) {
                $$4.visible = true;
                $$4.setPosition($$0, $$1 + 27 * $$3++);
                continue;
            }
            if (!$$4.updateVisibility(this.book)) continue;
            $$4.setPosition($$0, $$1 + 27 * $$3++);
            $$4.startAnimation(this.minecraft);
        }
    }

    public void tick() {
        boolean $$0 = this.isVisibleAccordingToBookData();
        if (this.isVisible() != $$0) {
            this.setVisible($$0);
        }
        if (!this.isVisible()) {
            return;
        }
        if (this.timesInventoryChanged != this.minecraft.player.getInventory().getTimesChanged()) {
            this.updateStackedContents();
            this.timesInventoryChanged = this.minecraft.player.getInventory().getTimesChanged();
        }
        this.searchBox.tick();
    }

    private void updateStackedContents() {
        this.stackedContents.clear();
        this.minecraft.player.getInventory().fillStackedContents(this.stackedContents);
        this.menu.fillCraftSlotsStackedContents(this.stackedContents);
        this.updateCollections(false);
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        if (!this.isVisible()) {
            return;
        }
        $$0.pushPose();
        $$0.translate(0.0f, 0.0f, 100.0f);
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
        RenderSystem.setShaderTexture(0, RECIPE_BOOK_LOCATION);
        int $$4 = (this.width - 147) / 2 - this.xOffset;
        int $$5 = (this.height - 166) / 2;
        this.blit($$0, $$4, $$5, 1, 1, 147, 166);
        this.searchBox.render($$0, $$1, $$2, $$3);
        for (RecipeBookTabButton $$6 : this.tabButtons) {
            $$6.render($$0, $$1, $$2, $$3);
        }
        this.filterButton.render($$0, $$1, $$2, $$3);
        this.recipeBookPage.render($$0, $$4, $$5, $$1, $$2, $$3);
        $$0.popPose();
    }

    public void renderTooltip(PoseStack $$0, int $$1, int $$2, int $$3, int $$4) {
        if (!this.isVisible()) {
            return;
        }
        this.recipeBookPage.renderTooltip($$0, $$3, $$4);
        this.renderGhostRecipeTooltip($$0, $$1, $$2, $$3, $$4);
    }

    protected Component getRecipeFilterName() {
        return ONLY_CRAFTABLES_TOOLTIP;
    }

    private void renderGhostRecipeTooltip(PoseStack $$0, int $$1, int $$2, int $$3, int $$4) {
        ItemStack $$5 = null;
        for (int $$6 = 0; $$6 < this.ghostRecipe.size(); ++$$6) {
            GhostRecipe.GhostIngredient $$7 = this.ghostRecipe.get($$6);
            int $$8 = $$7.getX() + $$1;
            int $$9 = $$7.getY() + $$2;
            if ($$3 < $$8 || $$4 < $$9 || $$3 >= $$8 + 16 || $$4 >= $$9 + 16) continue;
            $$5 = $$7.getItem();
        }
        if ($$5 != null && this.minecraft.screen != null) {
            this.minecraft.screen.renderComponentTooltip($$0, this.minecraft.screen.getTooltipFromItem($$5), $$3, $$4);
        }
    }

    public void renderGhostRecipe(PoseStack $$0, int $$1, int $$2, boolean $$3, float $$4) {
        this.ghostRecipe.render($$0, this.minecraft, $$1, $$2, $$3, $$4);
    }

    @Override
    public boolean mouseClicked(double $$0, double $$1, int $$2) {
        if (!this.isVisible() || this.minecraft.player.isSpectator()) {
            return false;
        }
        if (this.recipeBookPage.mouseClicked($$0, $$1, $$2, (this.width - 147) / 2 - this.xOffset, (this.height - 166) / 2, 147, 166)) {
            Recipe<?> $$3 = this.recipeBookPage.getLastClickedRecipe();
            RecipeCollection $$4 = this.recipeBookPage.getLastClickedRecipeCollection();
            if ($$3 != null && $$4 != null) {
                if (!$$4.isCraftable($$3) && this.ghostRecipe.getRecipe() == $$3) {
                    return false;
                }
                this.ghostRecipe.clear();
                this.minecraft.gameMode.handlePlaceRecipe(this.minecraft.player.containerMenu.containerId, $$3, Screen.hasShiftDown());
                if (!this.isOffsetNextToMainGUI()) {
                    this.setVisible(false);
                }
            }
            return true;
        }
        if (this.searchBox.mouseClicked($$0, $$1, $$2)) {
            return true;
        }
        if (this.filterButton.mouseClicked($$0, $$1, $$2)) {
            boolean $$5 = this.toggleFiltering();
            this.filterButton.setStateTriggered($$5);
            this.updateFilterButtonTooltip();
            this.sendUpdateSettings();
            this.updateCollections(false);
            return true;
        }
        for (RecipeBookTabButton $$6 : this.tabButtons) {
            if (!$$6.mouseClicked($$0, $$1, $$2)) continue;
            if (this.selectedTab != $$6) {
                if (this.selectedTab != null) {
                    this.selectedTab.setStateTriggered(false);
                }
                this.selectedTab = $$6;
                this.selectedTab.setStateTriggered(true);
                this.updateCollections(true);
            }
            return true;
        }
        return false;
    }

    private boolean toggleFiltering() {
        RecipeBookType $$0 = this.menu.getRecipeBookType();
        boolean $$1 = !this.book.isFiltering($$0);
        this.book.setFiltering($$0, $$1);
        return $$1;
    }

    public boolean hasClickedOutside(double $$0, double $$1, int $$2, int $$3, int $$4, int $$5, int $$6) {
        if (!this.isVisible()) {
            return true;
        }
        boolean $$7 = $$0 < (double)$$2 || $$1 < (double)$$3 || $$0 >= (double)($$2 + $$4) || $$1 >= (double)($$3 + $$5);
        boolean $$8 = (double)($$2 - 147) < $$0 && $$0 < (double)$$2 && (double)$$3 < $$1 && $$1 < (double)($$3 + $$5);
        return $$7 && !$$8 && !this.selectedTab.isHoveredOrFocused();
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        this.ignoreTextInput = false;
        if (!this.isVisible() || this.minecraft.player.isSpectator()) {
            return false;
        }
        if ($$0 == 256 && !this.isOffsetNextToMainGUI()) {
            this.setVisible(false);
            return true;
        }
        if (this.searchBox.keyPressed($$0, $$1, $$2)) {
            this.checkSearchStringUpdate();
            return true;
        }
        if (this.searchBox.isFocused() && this.searchBox.isVisible() && $$0 != 256) {
            return true;
        }
        if (this.minecraft.options.keyChat.matches($$0, $$1) && !this.searchBox.isFocused()) {
            this.ignoreTextInput = true;
            this.searchBox.setFocused(true);
            return true;
        }
        return false;
    }

    @Override
    public boolean keyReleased(int $$0, int $$1, int $$2) {
        this.ignoreTextInput = false;
        return GuiEventListener.super.keyReleased($$0, $$1, $$2);
    }

    @Override
    public boolean charTyped(char $$0, int $$1) {
        if (this.ignoreTextInput) {
            return false;
        }
        if (!this.isVisible() || this.minecraft.player.isSpectator()) {
            return false;
        }
        if (this.searchBox.charTyped($$0, $$1)) {
            this.checkSearchStringUpdate();
            return true;
        }
        return GuiEventListener.super.charTyped($$0, $$1);
    }

    @Override
    public boolean isMouseOver(double $$0, double $$1) {
        return false;
    }

    @Override
    public void setFocused(boolean $$0) {
    }

    @Override
    public boolean isFocused() {
        return false;
    }

    private void checkSearchStringUpdate() {
        String $$0 = this.searchBox.getValue().toLowerCase(Locale.ROOT);
        this.pirateSpeechForThePeople($$0);
        if (!$$0.equals((Object)this.lastSearch)) {
            this.updateCollections(false);
            this.lastSearch = $$0;
        }
    }

    private void pirateSpeechForThePeople(String $$0) {
        if ("excitedze".equals((Object)$$0)) {
            LanguageManager $$1 = this.minecraft.getLanguageManager();
            String $$2 = "en_pt";
            LanguageInfo $$3 = $$1.getLanguage("en_pt");
            if ($$3 == null || $$1.getSelected().equals((Object)"en_pt")) {
                return;
            }
            $$1.setSelected("en_pt");
            this.minecraft.options.languageCode = "en_pt";
            this.minecraft.reloadResourcePacks();
            this.minecraft.options.save();
        }
    }

    private boolean isOffsetNextToMainGUI() {
        return this.xOffset == 86;
    }

    public void recipesUpdated() {
        this.updateTabs();
        if (this.isVisible()) {
            this.updateCollections(false);
        }
    }

    @Override
    public void recipesShown(List<Recipe<?>> $$0) {
        for (Recipe $$1 : $$0) {
            this.minecraft.player.removeRecipeHighlight($$1);
        }
    }

    public void setupGhostRecipe(Recipe<?> $$0, List<Slot> $$1) {
        ItemStack $$2 = $$0.getResultItem();
        this.ghostRecipe.setRecipe($$0);
        this.ghostRecipe.addIngredient(Ingredient.of($$2), ((Slot)$$1.get((int)0)).x, ((Slot)$$1.get((int)0)).y);
        this.placeRecipe(this.menu.getGridWidth(), this.menu.getGridHeight(), this.menu.getResultSlotIndex(), $$0, $$0.getIngredients().iterator(), 0);
    }

    @Override
    public void addItemToSlot(Iterator<Ingredient> $$0, int $$1, int $$2, int $$3, int $$4) {
        Ingredient $$5 = (Ingredient)$$0.next();
        if (!$$5.isEmpty()) {
            Slot $$6 = (Slot)this.menu.slots.get($$1);
            this.ghostRecipe.addIngredient($$5, $$6.x, $$6.y);
        }
    }

    protected void sendUpdateSettings() {
        if (this.minecraft.getConnection() != null) {
            RecipeBookType $$0 = this.menu.getRecipeBookType();
            boolean $$1 = this.book.getBookSettings().isOpen($$0);
            boolean $$2 = this.book.getBookSettings().isFiltering($$0);
            this.minecraft.getConnection().send(new ServerboundRecipeBookChangeSettingsPacket($$0, $$1, $$2));
        }
    }

    @Override
    public NarratableEntry.NarrationPriority narrationPriority() {
        return this.visible ? NarratableEntry.NarrationPriority.HOVERED : NarratableEntry.NarrationPriority.NONE;
    }

    @Override
    public void updateNarration(NarrationElementOutput $$0) {
        ArrayList $$1 = Lists.newArrayList();
        this.recipeBookPage.listButtons((Consumer<AbstractWidget>)((Consumer)arg_0 -> RecipeBookComponent.lambda$updateNarration$6((List)$$1, arg_0)));
        $$1.add((Object)this.searchBox);
        $$1.add((Object)this.filterButton);
        $$1.addAll(this.tabButtons);
        Screen.NarratableSearchResult $$2 = Screen.findNarratableWidget((List<? extends NarratableEntry>)$$1, null);
        if ($$2 != null) {
            $$2.entry.updateNarration($$0.nest());
        }
    }

    private static /* synthetic */ void lambda$updateNarration$6(List $$0, AbstractWidget $$1) {
        if ($$1.isActive()) {
            $$0.add((Object)$$1);
        }
    }

    private static /* synthetic */ boolean lambda$updateCollections$4(ObjectSet $$0, RecipeCollection $$1) {
        return !$$0.contains((Object)$$1);
    }
}