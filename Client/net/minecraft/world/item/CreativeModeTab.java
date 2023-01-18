/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.lang.IllegalArgumentException
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Collection
 *  java.util.List
 *  java.util.Set
 *  java.util.function.Consumer
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 */
package net.minecraft.world.item;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackLinkedSet;
import net.minecraft.world.level.ItemLike;

public class CreativeModeTab {
    private final Component displayName;
    String backgroundSuffix = "items.png";
    boolean canScroll = true;
    boolean showTitle = true;
    boolean alignedRight = false;
    private final Row row;
    private final int column;
    private final Type type;
    @Nullable
    private ItemStack iconItemStack;
    private Collection<ItemStack> displayItems = ItemStackLinkedSet.createTypeAndTagSet();
    private Set<ItemStack> displayItemsSearchTab = ItemStackLinkedSet.createTypeAndTagSet();
    @Nullable
    private Consumer<List<ItemStack>> searchTreeBuilder;
    private final Supplier<ItemStack> iconGenerator;
    private final DisplayItemsGenerator displayItemsGenerator;

    CreativeModeTab(Row $$0, int $$1, Type $$2, Component $$3, Supplier<ItemStack> $$4, DisplayItemsGenerator $$5) {
        this.row = $$0;
        this.column = $$1;
        this.displayName = $$3;
        this.iconGenerator = $$4;
        this.displayItemsGenerator = $$5;
        this.type = $$2;
    }

    public static Builder builder(Row $$0, int $$1) {
        return new Builder($$0, $$1);
    }

    public Component getDisplayName() {
        return this.displayName;
    }

    public ItemStack getIconItem() {
        if (this.iconItemStack == null) {
            this.iconItemStack = (ItemStack)this.iconGenerator.get();
        }
        return this.iconItemStack;
    }

    public String getBackgroundSuffix() {
        return this.backgroundSuffix;
    }

    public boolean showTitle() {
        return this.showTitle;
    }

    public boolean canScroll() {
        return this.canScroll;
    }

    public int column() {
        return this.column;
    }

    public Row row() {
        return this.row;
    }

    public boolean hasAnyItems() {
        return !this.displayItems.isEmpty();
    }

    public boolean shouldDisplay() {
        return this.type != Type.CATEGORY || this.hasAnyItems();
    }

    public boolean isAlignedRight() {
        return this.alignedRight;
    }

    public Type getType() {
        return this.type;
    }

    public void buildContents(FeatureFlagSet $$0, boolean $$1) {
        ItemDisplayBuilder $$2 = new ItemDisplayBuilder(this, $$0);
        this.displayItemsGenerator.accept($$0, $$2, $$1);
        this.displayItems = $$2.tabContents;
        this.displayItemsSearchTab = $$2.searchTabContents;
        this.rebuildSearchTree();
    }

    public Collection<ItemStack> getDisplayItems() {
        return this.displayItems;
    }

    public Collection<ItemStack> getSearchTabDisplayItems() {
        return this.displayItemsSearchTab;
    }

    public boolean contains(ItemStack $$0) {
        return this.displayItemsSearchTab.contains((Object)$$0);
    }

    public void setSearchTreeBuilder(Consumer<List<ItemStack>> $$0) {
        this.searchTreeBuilder = $$0;
    }

    public void rebuildSearchTree() {
        if (this.searchTreeBuilder != null) {
            this.searchTreeBuilder.accept((Object)Lists.newArrayList(this.displayItemsSearchTab));
        }
    }

    public static enum Row {
        TOP,
        BOTTOM;

    }

    public static interface DisplayItemsGenerator {
        public void accept(FeatureFlagSet var1, Output var2, boolean var3);
    }

    public static enum Type {
        CATEGORY,
        INVENTORY,
        HOTBAR,
        SEARCH;

    }

    public static class Builder {
        private static final DisplayItemsGenerator EMPTY_GENERATOR = ($$0, $$1, $$2) -> {};
        private final Row row;
        private final int column;
        private Component displayName = Component.empty();
        private Supplier<ItemStack> iconGenerator = () -> ItemStack.EMPTY;
        private DisplayItemsGenerator displayItemsGenerator = EMPTY_GENERATOR;
        private boolean canScroll = true;
        private boolean showTitle = true;
        private boolean alignedRight = false;
        private Type type = Type.CATEGORY;
        private String backgroundSuffix = "items.png";

        public Builder(Row $$0, int $$1) {
            this.row = $$0;
            this.column = $$1;
        }

        public Builder title(Component $$0) {
            this.displayName = $$0;
            return this;
        }

        public Builder icon(Supplier<ItemStack> $$0) {
            this.iconGenerator = $$0;
            return this;
        }

        public Builder displayItems(DisplayItemsGenerator $$0) {
            this.displayItemsGenerator = $$0;
            return this;
        }

        public Builder alignedRight() {
            this.alignedRight = true;
            return this;
        }

        public Builder hideTitle() {
            this.showTitle = false;
            return this;
        }

        public Builder noScrollBar() {
            this.canScroll = false;
            return this;
        }

        protected Builder type(Type $$0) {
            this.type = $$0;
            return this;
        }

        public Builder backgroundSuffix(String $$0) {
            this.backgroundSuffix = $$0;
            return this;
        }

        public CreativeModeTab build() {
            if ((this.type == Type.HOTBAR || this.type == Type.INVENTORY) && this.displayItemsGenerator != EMPTY_GENERATOR) {
                throw new IllegalStateException("Special tabs can't have display items");
            }
            CreativeModeTab $$0 = new CreativeModeTab(this.row, this.column, this.type, this.displayName, this.iconGenerator, this.displayItemsGenerator);
            $$0.alignedRight = this.alignedRight;
            $$0.showTitle = this.showTitle;
            $$0.canScroll = this.canScroll;
            $$0.backgroundSuffix = this.backgroundSuffix;
            return $$0;
        }
    }

    static class ItemDisplayBuilder
    implements Output {
        public final Collection<ItemStack> tabContents = ItemStackLinkedSet.createTypeAndTagSet();
        public final Set<ItemStack> searchTabContents = ItemStackLinkedSet.createTypeAndTagSet();
        private final CreativeModeTab tab;
        private final FeatureFlagSet featureFlagSet;

        public ItemDisplayBuilder(CreativeModeTab $$0, FeatureFlagSet $$1) {
            this.tab = $$0;
            this.featureFlagSet = $$1;
        }

        @Override
        public void accept(ItemStack $$0, TabVisibility $$1) {
            boolean $$2;
            if ($$0.getCount() != 1) {
                throw new IllegalArgumentException("Stack size must be exactly 1");
            }
            boolean bl = $$2 = this.tabContents.contains((Object)$$0) && $$1 != TabVisibility.SEARCH_TAB_ONLY;
            if ($$2) {
                throw new IllegalStateException("Accidentally adding the same item stack twice " + $$0.getDisplayName().getString() + " to a Creative Mode Tab: " + this.tab.getDisplayName().getString());
            }
            if ($$0.getItem().isEnabled(this.featureFlagSet)) {
                switch ($$1) {
                    case PARENT_AND_SEARCH_TABS: {
                        this.tabContents.add((Object)$$0);
                        this.searchTabContents.add((Object)$$0);
                        break;
                    }
                    case PARENT_TAB_ONLY: {
                        this.tabContents.add((Object)$$0);
                        break;
                    }
                    case SEARCH_TAB_ONLY: {
                        this.searchTabContents.add((Object)$$0);
                    }
                }
            }
        }
    }

    protected static interface Output {
        public void accept(ItemStack var1, TabVisibility var2);

        default public void accept(ItemStack $$0) {
            this.accept($$0, TabVisibility.PARENT_AND_SEARCH_TABS);
        }

        default public void accept(ItemLike $$0, TabVisibility $$1) {
            this.accept(new ItemStack($$0), $$1);
        }

        default public void accept(ItemLike $$0) {
            this.accept(new ItemStack($$0), TabVisibility.PARENT_AND_SEARCH_TABS);
        }

        default public void acceptAll(Collection<ItemStack> $$0, TabVisibility $$12) {
            $$0.forEach($$1 -> this.accept((ItemStack)$$1, $$12));
        }

        default public void acceptAll(Collection<ItemStack> $$0) {
            this.acceptAll($$0, TabVisibility.PARENT_AND_SEARCH_TABS);
        }
    }

    protected static enum TabVisibility {
        PARENT_AND_SEARCH_TABS,
        PARENT_TAB_ONLY,
        SEARCH_TAB_ONLY;

    }
}