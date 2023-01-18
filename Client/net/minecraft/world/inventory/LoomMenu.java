/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Runnable
 *  java.util.List
 *  java.util.function.BiConsumer
 */
package net.minecraft.world.inventory;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BannerPatternTags;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.BannerPatternItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class LoomMenu
extends AbstractContainerMenu {
    private static final int PATTERN_NOT_SET = -1;
    private static final int INV_SLOT_START = 4;
    private static final int INV_SLOT_END = 31;
    private static final int USE_ROW_SLOT_START = 31;
    private static final int USE_ROW_SLOT_END = 40;
    private final ContainerLevelAccess access;
    final DataSlot selectedBannerPatternIndex = DataSlot.standalone();
    private List<Holder<BannerPattern>> selectablePatterns = List.of();
    Runnable slotUpdateListener = () -> {};
    final Slot bannerSlot;
    final Slot dyeSlot;
    private final Slot patternSlot;
    private final Slot resultSlot;
    long lastSoundTime;
    private final Container inputContainer = new SimpleContainer(3){

        @Override
        public void setChanged() {
            super.setChanged();
            LoomMenu.this.slotsChanged(this);
            LoomMenu.this.slotUpdateListener.run();
        }
    };
    private final Container outputContainer = new SimpleContainer(1){

        @Override
        public void setChanged() {
            super.setChanged();
            LoomMenu.this.slotUpdateListener.run();
        }
    };

    public LoomMenu(int $$0, Inventory $$1) {
        this($$0, $$1, ContainerLevelAccess.NULL);
    }

    public LoomMenu(int $$0, Inventory $$1, final ContainerLevelAccess $$2) {
        super(MenuType.LOOM, $$0);
        this.access = $$2;
        this.bannerSlot = this.addSlot(new Slot(this.inputContainer, 0, 13, 26){

            @Override
            public boolean mayPlace(ItemStack $$0) {
                return $$0.getItem() instanceof BannerItem;
            }
        });
        this.dyeSlot = this.addSlot(new Slot(this.inputContainer, 1, 33, 26){

            @Override
            public boolean mayPlace(ItemStack $$0) {
                return $$0.getItem() instanceof DyeItem;
            }
        });
        this.patternSlot = this.addSlot(new Slot(this.inputContainer, 2, 23, 45){

            @Override
            public boolean mayPlace(ItemStack $$0) {
                return $$0.getItem() instanceof BannerPatternItem;
            }
        });
        this.resultSlot = this.addSlot(new Slot(this.outputContainer, 0, 143, 58){

            @Override
            public boolean mayPlace(ItemStack $$0) {
                return false;
            }

            @Override
            public void onTake(Player $$02, ItemStack $$12) {
                LoomMenu.this.bannerSlot.remove(1);
                LoomMenu.this.dyeSlot.remove(1);
                if (!LoomMenu.this.bannerSlot.hasItem() || !LoomMenu.this.dyeSlot.hasItem()) {
                    LoomMenu.this.selectedBannerPatternIndex.set(-1);
                }
                $$2.execute((BiConsumer<Level, BlockPos>)((BiConsumer)($$0, $$1) -> {
                    long $$22 = $$0.getGameTime();
                    if (LoomMenu.this.lastSoundTime != $$22) {
                        $$0.playSound(null, (BlockPos)$$1, SoundEvents.UI_LOOM_TAKE_RESULT, SoundSource.BLOCKS, 1.0f, 1.0f);
                        LoomMenu.this.lastSoundTime = $$22;
                    }
                }));
                super.onTake($$02, $$12);
            }
        });
        for (int $$3 = 0; $$3 < 3; ++$$3) {
            for (int $$4 = 0; $$4 < 9; ++$$4) {
                this.addSlot(new Slot($$1, $$4 + $$3 * 9 + 9, 8 + $$4 * 18, 84 + $$3 * 18));
            }
        }
        for (int $$5 = 0; $$5 < 9; ++$$5) {
            this.addSlot(new Slot($$1, $$5, 8 + $$5 * 18, 142));
        }
        this.addDataSlot(this.selectedBannerPatternIndex);
    }

    @Override
    public boolean stillValid(Player $$0) {
        return LoomMenu.stillValid(this.access, $$0, Blocks.LOOM);
    }

    @Override
    public boolean clickMenuButton(Player $$0, int $$1) {
        if ($$1 >= 0 && $$1 < this.selectablePatterns.size()) {
            this.selectedBannerPatternIndex.set($$1);
            this.setupResultSlot((Holder)this.selectablePatterns.get($$1));
            return true;
        }
        return false;
    }

    private List<Holder<BannerPattern>> getSelectablePatterns(ItemStack $$0) {
        if ($$0.isEmpty()) {
            return (List)BuiltInRegistries.BANNER_PATTERN.getTag(BannerPatternTags.NO_ITEM_REQUIRED).map(ImmutableList::copyOf).orElse((Object)ImmutableList.of());
        }
        Item item = $$0.getItem();
        if (item instanceof BannerPatternItem) {
            BannerPatternItem $$1 = (BannerPatternItem)item;
            return (List)BuiltInRegistries.BANNER_PATTERN.getTag($$1.getBannerPattern()).map(ImmutableList::copyOf).orElse((Object)ImmutableList.of());
        }
        return List.of();
    }

    private boolean isValidPatternIndex(int $$0) {
        return $$0 >= 0 && $$0 < this.selectablePatterns.size();
    }

    @Override
    public void slotsChanged(Container $$0) {
        Holder<BannerPattern> $$12;
        ItemStack $$1 = this.bannerSlot.getItem();
        ItemStack $$2 = this.dyeSlot.getItem();
        ItemStack $$3 = this.patternSlot.getItem();
        if ($$1.isEmpty() || $$2.isEmpty()) {
            this.resultSlot.set(ItemStack.EMPTY);
            this.selectablePatterns = List.of();
            this.selectedBannerPatternIndex.set(-1);
            return;
        }
        int $$4 = this.selectedBannerPatternIndex.get();
        boolean $$5 = this.isValidPatternIndex($$4);
        List<Holder<BannerPattern>> $$6 = this.selectablePatterns;
        this.selectablePatterns = this.getSelectablePatterns($$3);
        if (this.selectablePatterns.size() == 1) {
            this.selectedBannerPatternIndex.set(0);
            Holder $$7 = (Holder)this.selectablePatterns.get(0);
        } else if (!$$5) {
            this.selectedBannerPatternIndex.set(-1);
            Object $$8 = null;
        } else {
            Holder $$9 = (Holder)$$6.get($$4);
            int $$10 = this.selectablePatterns.indexOf((Object)$$9);
            if ($$10 != -1) {
                Holder $$11 = $$9;
                this.selectedBannerPatternIndex.set($$10);
            } else {
                $$12 = null;
                this.selectedBannerPatternIndex.set(-1);
            }
        }
        if ($$12 != null) {
            boolean $$14;
            CompoundTag $$13 = BlockItem.getBlockEntityData($$1);
            boolean bl = $$14 = $$13 != null && $$13.contains("Patterns", 9) && !$$1.isEmpty() && $$13.getList("Patterns", 10).size() >= 6;
            if ($$14) {
                this.selectedBannerPatternIndex.set(-1);
                this.resultSlot.set(ItemStack.EMPTY);
            } else {
                this.setupResultSlot($$12);
            }
        } else {
            this.resultSlot.set(ItemStack.EMPTY);
        }
        this.broadcastChanges();
    }

    public List<Holder<BannerPattern>> getSelectablePatterns() {
        return this.selectablePatterns;
    }

    public int getSelectedBannerPatternIndex() {
        return this.selectedBannerPatternIndex.get();
    }

    public void registerUpdateListener(Runnable $$0) {
        this.slotUpdateListener = $$0;
    }

    @Override
    public ItemStack quickMoveStack(Player $$0, int $$1) {
        ItemStack $$2 = ItemStack.EMPTY;
        Slot $$3 = (Slot)this.slots.get($$1);
        if ($$3 != null && $$3.hasItem()) {
            ItemStack $$4 = $$3.getItem();
            $$2 = $$4.copy();
            if ($$1 == this.resultSlot.index) {
                if (!this.moveItemStackTo($$4, 4, 40, true)) {
                    return ItemStack.EMPTY;
                }
                $$3.onQuickCraft($$4, $$2);
            } else if ($$1 == this.dyeSlot.index || $$1 == this.bannerSlot.index || $$1 == this.patternSlot.index ? !this.moveItemStackTo($$4, 4, 40, false) : ($$4.getItem() instanceof BannerItem ? !this.moveItemStackTo($$4, this.bannerSlot.index, this.bannerSlot.index + 1, false) : ($$4.getItem() instanceof DyeItem ? !this.moveItemStackTo($$4, this.dyeSlot.index, this.dyeSlot.index + 1, false) : ($$4.getItem() instanceof BannerPatternItem ? !this.moveItemStackTo($$4, this.patternSlot.index, this.patternSlot.index + 1, false) : ($$1 >= 4 && $$1 < 31 ? !this.moveItemStackTo($$4, 31, 40, false) : $$1 >= 31 && $$1 < 40 && !this.moveItemStackTo($$4, 4, 31, false)))))) {
                return ItemStack.EMPTY;
            }
            if ($$4.isEmpty()) {
                $$3.set(ItemStack.EMPTY);
            } else {
                $$3.setChanged();
            }
            if ($$4.getCount() == $$2.getCount()) {
                return ItemStack.EMPTY;
            }
            $$3.onTake($$0, $$4);
        }
        return $$2;
    }

    @Override
    public void removed(Player $$0) {
        super.removed($$0);
        this.access.execute((BiConsumer<Level, BlockPos>)((BiConsumer)($$1, $$2) -> this.clearContainer($$0, this.inputContainer)));
    }

    private void setupResultSlot(Holder<BannerPattern> $$0) {
        ItemStack $$1 = this.bannerSlot.getItem();
        ItemStack $$2 = this.dyeSlot.getItem();
        ItemStack $$3 = ItemStack.EMPTY;
        if (!$$1.isEmpty() && !$$2.isEmpty()) {
            ListTag $$7;
            $$3 = $$1.copy();
            $$3.setCount(1);
            DyeColor $$4 = ((DyeItem)$$2.getItem()).getDyeColor();
            CompoundTag $$5 = BlockItem.getBlockEntityData($$3);
            if ($$5 != null && $$5.contains("Patterns", 9)) {
                ListTag $$6 = $$5.getList("Patterns", 10);
            } else {
                $$7 = new ListTag();
                if ($$5 == null) {
                    $$5 = new CompoundTag();
                }
                $$5.put("Patterns", $$7);
            }
            CompoundTag $$8 = new CompoundTag();
            $$8.putString("Pattern", $$0.value().getHashname());
            $$8.putInt("Color", $$4.getId());
            $$7.add($$8);
            BlockItem.setBlockEntityData($$3, BlockEntityType.BANNER, $$5);
        }
        if (!ItemStack.matches($$3, this.resultSlot.getItem())) {
            this.resultSlot.set($$3);
        }
    }

    public Slot getBannerSlot() {
        return this.bannerSlot;
    }

    public Slot getDyeSlot() {
        return this.dyeSlot;
    }

    public Slot getPatternSlot() {
        return this.patternSlot;
    }

    public Slot getResultSlot() {
        return this.resultSlot;
    }
}