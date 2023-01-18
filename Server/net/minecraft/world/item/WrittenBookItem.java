/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Short
 *  java.lang.String
 *  java.util.List
 *  javax.annotation.Nullable
 */
package net.minecraft.world.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.stats.Stats;
import net.minecraft.util.StringUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.WritableBookItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.state.BlockState;

public class WrittenBookItem
extends Item {
    public static final int TITLE_LENGTH = 16;
    public static final int TITLE_MAX_LENGTH = 32;
    public static final int PAGE_EDIT_LENGTH = 1024;
    public static final int PAGE_LENGTH = Short.MAX_VALUE;
    public static final int MAX_PAGES = 100;
    public static final int MAX_GENERATION = 2;
    public static final String TAG_TITLE = "title";
    public static final String TAG_FILTERED_TITLE = "filtered_title";
    public static final String TAG_AUTHOR = "author";
    public static final String TAG_PAGES = "pages";
    public static final String TAG_FILTERED_PAGES = "filtered_pages";
    public static final String TAG_GENERATION = "generation";
    public static final String TAG_RESOLVED = "resolved";

    public WrittenBookItem(Item.Properties $$0) {
        super($$0);
    }

    public static boolean makeSureTagIsValid(@Nullable CompoundTag $$0) {
        if (!WritableBookItem.makeSureTagIsValid($$0)) {
            return false;
        }
        if (!$$0.contains(TAG_TITLE, 8)) {
            return false;
        }
        String $$1 = $$0.getString(TAG_TITLE);
        if ($$1.length() > 32) {
            return false;
        }
        return $$0.contains(TAG_AUTHOR, 8);
    }

    public static int getGeneration(ItemStack $$0) {
        return $$0.getTag().getInt(TAG_GENERATION);
    }

    public static int getPageCount(ItemStack $$0) {
        CompoundTag $$1 = $$0.getTag();
        return $$1 != null ? $$1.getList(TAG_PAGES, 8).size() : 0;
    }

    @Override
    public Component getName(ItemStack $$0) {
        String $$2;
        CompoundTag $$1 = $$0.getTag();
        if ($$1 != null && !StringUtil.isNullOrEmpty($$2 = $$1.getString(TAG_TITLE))) {
            return Component.literal($$2);
        }
        return super.getName($$0);
    }

    @Override
    public void appendHoverText(ItemStack $$0, @Nullable Level $$1, List<Component> $$2, TooltipFlag $$3) {
        if ($$0.hasTag()) {
            CompoundTag $$4 = $$0.getTag();
            String $$5 = $$4.getString(TAG_AUTHOR);
            if (!StringUtil.isNullOrEmpty($$5)) {
                $$2.add((Object)Component.translatable("book.byAuthor", $$5).withStyle(ChatFormatting.GRAY));
            }
            $$2.add((Object)Component.translatable("book.generation." + $$4.getInt(TAG_GENERATION)).withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext $$0) {
        BlockPos $$2;
        Level $$1 = $$0.getLevel();
        BlockState $$3 = $$1.getBlockState($$2 = $$0.getClickedPos());
        if ($$3.is(Blocks.LECTERN)) {
            return LecternBlock.tryPlaceBook($$0.getPlayer(), $$1, $$2, $$3, $$0.getItemInHand()) ? InteractionResult.sidedSuccess($$1.isClientSide) : InteractionResult.PASS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level $$0, Player $$1, InteractionHand $$2) {
        ItemStack $$3 = $$1.getItemInHand($$2);
        $$1.openItemGui($$3, $$2);
        $$1.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.sidedSuccess($$3, $$0.isClientSide());
    }

    public static boolean resolveBookComponents(ItemStack $$0, @Nullable CommandSourceStack $$1, @Nullable Player $$2) {
        CompoundTag $$3 = $$0.getTag();
        if ($$3 == null || $$3.getBoolean(TAG_RESOLVED)) {
            return false;
        }
        $$3.putBoolean(TAG_RESOLVED, true);
        if (!WrittenBookItem.makeSureTagIsValid($$3)) {
            return false;
        }
        ListTag $$4 = $$3.getList(TAG_PAGES, 8);
        ListTag $$5 = new ListTag();
        for (int $$6 = 0; $$6 < $$4.size(); ++$$6) {
            String $$7 = WrittenBookItem.resolvePage($$1, $$2, $$4.getString($$6));
            if ($$7.length() > Short.MAX_VALUE) {
                return false;
            }
            $$5.add($$6, StringTag.valueOf($$7));
        }
        if ($$3.contains(TAG_FILTERED_PAGES, 10)) {
            CompoundTag $$8 = $$3.getCompound(TAG_FILTERED_PAGES);
            CompoundTag $$9 = new CompoundTag();
            for (String $$10 : $$8.getAllKeys()) {
                String $$11 = WrittenBookItem.resolvePage($$1, $$2, $$8.getString($$10));
                if ($$11.length() > Short.MAX_VALUE) {
                    return false;
                }
                $$9.putString($$10, $$11);
            }
            $$3.put(TAG_FILTERED_PAGES, $$9);
        }
        $$3.put(TAG_PAGES, $$5);
        return true;
    }

    private static String resolvePage(@Nullable CommandSourceStack $$0, @Nullable Player $$1, String $$2) {
        MutableComponent $$5;
        try {
            MutableComponent $$3 = Component.Serializer.fromJsonLenient($$2);
            $$3 = ComponentUtils.updateForEntity($$0, $$3, (Entity)$$1, 0);
        }
        catch (Exception $$4) {
            $$5 = Component.literal($$2);
        }
        return Component.Serializer.toJson($$5);
    }

    @Override
    public boolean isFoil(ItemStack $$0) {
        return true;
    }
}