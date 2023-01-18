/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.List
 *  java.util.Optional
 *  java.util.stream.Stream
 */
package net.minecraft.world.item;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class BundleItem
extends Item {
    private static final String TAG_ITEMS = "Items";
    public static final int MAX_WEIGHT = 64;
    private static final int BUNDLE_IN_BUNDLE_WEIGHT = 4;
    private static final int BAR_COLOR = Mth.color(0.4f, 0.4f, 1.0f);

    public BundleItem(Item.Properties $$0) {
        super($$0);
    }

    public static float getFullnessDisplay(ItemStack $$0) {
        return (float)BundleItem.getContentWeight($$0) / 64.0f;
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack $$0, Slot $$1, ClickAction $$22, Player $$3) {
        if ($$22 != ClickAction.SECONDARY) {
            return false;
        }
        ItemStack $$4 = $$1.getItem();
        if ($$4.isEmpty()) {
            this.playRemoveOneSound($$3);
            BundleItem.removeOne($$0).ifPresent($$2 -> BundleItem.add($$0, $$1.safeInsert((ItemStack)$$2)));
        } else if ($$4.getItem().canFitInsideContainerItems()) {
            int $$5 = (64 - BundleItem.getContentWeight($$0)) / BundleItem.getWeight($$4);
            int $$6 = BundleItem.add($$0, $$1.safeTake($$4.getCount(), $$5, $$3));
            if ($$6 > 0) {
                this.playInsertSound($$3);
            }
        }
        return true;
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack $$0, ItemStack $$1, Slot $$22, ClickAction $$3, Player $$4, SlotAccess $$5) {
        if ($$3 != ClickAction.SECONDARY || !$$22.allowModification($$4)) {
            return false;
        }
        if ($$1.isEmpty()) {
            BundleItem.removeOne($$0).ifPresent($$2 -> {
                this.playRemoveOneSound($$4);
                $$5.set((ItemStack)$$2);
            });
        } else {
            int $$6 = BundleItem.add($$0, $$1);
            if ($$6 > 0) {
                this.playInsertSound($$4);
                $$1.shrink($$6);
            }
        }
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level $$0, Player $$1, InteractionHand $$2) {
        ItemStack $$3 = $$1.getItemInHand($$2);
        if (BundleItem.dropContents($$3, $$1)) {
            this.playDropContentsSound($$1);
            $$1.awardStat(Stats.ITEM_USED.get(this));
            return InteractionResultHolder.sidedSuccess($$3, $$0.isClientSide());
        }
        return InteractionResultHolder.fail($$3);
    }

    @Override
    public boolean isBarVisible(ItemStack $$0) {
        return BundleItem.getContentWeight($$0) > 0;
    }

    @Override
    public int getBarWidth(ItemStack $$0) {
        return Math.min((int)(1 + 12 * BundleItem.getContentWeight($$0) / 64), (int)13);
    }

    @Override
    public int getBarColor(ItemStack $$0) {
        return BAR_COLOR;
    }

    private static int add(ItemStack $$0, ItemStack $$1) {
        if ($$1.isEmpty() || !$$1.getItem().canFitInsideContainerItems()) {
            return 0;
        }
        CompoundTag $$2 = $$0.getOrCreateTag();
        if (!$$2.contains(TAG_ITEMS)) {
            $$2.put(TAG_ITEMS, new ListTag());
        }
        int $$3 = BundleItem.getContentWeight($$0);
        int $$4 = BundleItem.getWeight($$1);
        int $$5 = Math.min((int)$$1.getCount(), (int)((64 - $$3) / $$4));
        if ($$5 == 0) {
            return 0;
        }
        ListTag $$6 = $$2.getList(TAG_ITEMS, 10);
        Optional<CompoundTag> $$7 = BundleItem.getMatchingItem($$1, $$6);
        if ($$7.isPresent()) {
            CompoundTag $$8 = (CompoundTag)$$7.get();
            ItemStack $$9 = ItemStack.of($$8);
            $$9.grow($$5);
            $$9.save($$8);
            $$6.remove($$8);
            $$6.add(0, $$8);
        } else {
            ItemStack $$10 = $$1.copy();
            $$10.setCount($$5);
            CompoundTag $$11 = new CompoundTag();
            $$10.save($$11);
            $$6.add(0, $$11);
        }
        return $$5;
    }

    private static Optional<CompoundTag> getMatchingItem(ItemStack $$0, ListTag $$12) {
        if ($$0.is(Items.BUNDLE)) {
            return Optional.empty();
        }
        return $$12.stream().filter(arg_0 -> CompoundTag.class.isInstance(arg_0)).map(arg_0 -> CompoundTag.class.cast(arg_0)).filter($$1 -> ItemStack.isSameItemSameTags(ItemStack.of($$1), $$0)).findFirst();
    }

    private static int getWeight(ItemStack $$0) {
        CompoundTag $$1;
        if ($$0.is(Items.BUNDLE)) {
            return 4 + BundleItem.getContentWeight($$0);
        }
        if (($$0.is(Items.BEEHIVE) || $$0.is(Items.BEE_NEST)) && $$0.hasTag() && ($$1 = BlockItem.getBlockEntityData($$0)) != null && !$$1.getList("Bees", 10).isEmpty()) {
            return 64;
        }
        return 64 / $$0.getMaxStackSize();
    }

    private static int getContentWeight(ItemStack $$02) {
        return BundleItem.getContents($$02).mapToInt($$0 -> BundleItem.getWeight($$0) * $$0.getCount()).sum();
    }

    private static Optional<ItemStack> removeOne(ItemStack $$0) {
        CompoundTag $$1 = $$0.getOrCreateTag();
        if (!$$1.contains(TAG_ITEMS)) {
            return Optional.empty();
        }
        ListTag $$2 = $$1.getList(TAG_ITEMS, 10);
        if ($$2.isEmpty()) {
            return Optional.empty();
        }
        boolean $$3 = false;
        CompoundTag $$4 = $$2.getCompound(0);
        ItemStack $$5 = ItemStack.of($$4);
        $$2.remove(0);
        if ($$2.isEmpty()) {
            $$0.removeTagKey(TAG_ITEMS);
        }
        return Optional.of((Object)$$5);
    }

    private static boolean dropContents(ItemStack $$0, Player $$1) {
        CompoundTag $$2 = $$0.getOrCreateTag();
        if (!$$2.contains(TAG_ITEMS)) {
            return false;
        }
        if ($$1 instanceof ServerPlayer) {
            ListTag $$3 = $$2.getList(TAG_ITEMS, 10);
            for (int $$4 = 0; $$4 < $$3.size(); ++$$4) {
                CompoundTag $$5 = $$3.getCompound($$4);
                ItemStack $$6 = ItemStack.of($$5);
                $$1.drop($$6, true);
            }
        }
        $$0.removeTagKey(TAG_ITEMS);
        return true;
    }

    private static Stream<ItemStack> getContents(ItemStack $$0) {
        CompoundTag $$1 = $$0.getTag();
        if ($$1 == null) {
            return Stream.empty();
        }
        ListTag $$2 = $$1.getList(TAG_ITEMS, 10);
        return $$2.stream().map(arg_0 -> CompoundTag.class.cast(arg_0)).map(ItemStack::of);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack $$0) {
        NonNullList<ItemStack> $$1 = NonNullList.create();
        BundleItem.getContents($$0).forEach(arg_0 -> $$1.add(arg_0));
        return Optional.of((Object)new BundleTooltip($$1, BundleItem.getContentWeight($$0)));
    }

    @Override
    public void appendHoverText(ItemStack $$0, Level $$1, List<Component> $$2, TooltipFlag $$3) {
        $$2.add((Object)Component.translatable("item.minecraft.bundle.fullness", BundleItem.getContentWeight($$0), 64).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public void onDestroyed(ItemEntity $$0) {
        ItemUtils.onContainerDestroyed($$0, BundleItem.getContents($$0.getItem()));
    }

    private void playRemoveOneSound(Entity $$0) {
        $$0.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8f, 0.8f + $$0.getLevel().getRandom().nextFloat() * 0.4f);
    }

    private void playInsertSound(Entity $$0) {
        $$0.playSound(SoundEvents.BUNDLE_INSERT, 0.8f, 0.8f + $$0.getLevel().getRandom().nextFloat() * 0.4f);
    }

    private void playDropContentsSound(Entity $$0) {
        $$0.playSound(SoundEvents.BUNDLE_DROP_CONTENTS, 0.8f, 0.8f + $$0.getLevel().getRandom().nextFloat() * 0.4f);
    }
}