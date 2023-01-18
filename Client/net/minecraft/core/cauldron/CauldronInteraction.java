/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  java.lang.Object
 *  java.util.Map
 *  java.util.function.Predicate
 */
package net.minecraft.core.cauldron;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public interface CauldronInteraction {
    public static final Map<Item, CauldronInteraction> EMPTY = CauldronInteraction.newInteractionMap();
    public static final Map<Item, CauldronInteraction> WATER = CauldronInteraction.newInteractionMap();
    public static final Map<Item, CauldronInteraction> LAVA = CauldronInteraction.newInteractionMap();
    public static final Map<Item, CauldronInteraction> POWDER_SNOW = CauldronInteraction.newInteractionMap();
    public static final CauldronInteraction FILL_WATER = ($$0, $$1, $$2, $$3, $$4, $$5) -> CauldronInteraction.emptyBucket($$1, $$2, $$3, $$4, $$5, (BlockState)Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3), SoundEvents.BUCKET_EMPTY);
    public static final CauldronInteraction FILL_LAVA = ($$0, $$1, $$2, $$3, $$4, $$5) -> CauldronInteraction.emptyBucket($$1, $$2, $$3, $$4, $$5, Blocks.LAVA_CAULDRON.defaultBlockState(), SoundEvents.BUCKET_EMPTY_LAVA);
    public static final CauldronInteraction FILL_POWDER_SNOW = ($$0, $$1, $$2, $$3, $$4, $$5) -> CauldronInteraction.emptyBucket($$1, $$2, $$3, $$4, $$5, (BlockState)Blocks.POWDER_SNOW_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3), SoundEvents.BUCKET_EMPTY_POWDER_SNOW);
    public static final CauldronInteraction SHULKER_BOX = ($$0, $$1, $$2, $$3, $$4, $$5) -> {
        Block $$6 = Block.byItem($$5.getItem());
        if (!($$6 instanceof ShulkerBoxBlock)) {
            return InteractionResult.PASS;
        }
        if (!$$1.isClientSide) {
            ItemStack $$7 = new ItemStack(Blocks.SHULKER_BOX);
            if ($$5.hasTag()) {
                $$7.setTag($$5.getTag().copy());
            }
            $$3.setItemInHand($$4, $$7);
            $$3.awardStat(Stats.CLEAN_SHULKER_BOX);
            LayeredCauldronBlock.lowerFillLevel($$0, $$1, $$2);
        }
        return InteractionResult.sidedSuccess($$1.isClientSide);
    };
    public static final CauldronInteraction BANNER = ($$0, $$1, $$2, $$3, $$4, $$5) -> {
        if (BannerBlockEntity.getPatternCount($$5) <= 0) {
            return InteractionResult.PASS;
        }
        if (!$$1.isClientSide) {
            ItemStack $$6 = $$5.copy();
            $$6.setCount(1);
            BannerBlockEntity.removeLastPattern($$6);
            if (!$$3.getAbilities().instabuild) {
                $$5.shrink(1);
            }
            if ($$5.isEmpty()) {
                $$3.setItemInHand($$4, $$6);
            } else if ($$3.getInventory().add($$6)) {
                $$3.inventoryMenu.sendAllDataToRemote();
            } else {
                $$3.drop($$6, false);
            }
            $$3.awardStat(Stats.CLEAN_BANNER);
            LayeredCauldronBlock.lowerFillLevel($$0, $$1, $$2);
        }
        return InteractionResult.sidedSuccess($$1.isClientSide);
    };
    public static final CauldronInteraction DYED_ITEM = ($$0, $$1, $$2, $$3, $$4, $$5) -> {
        Item $$6 = $$5.getItem();
        if (!($$6 instanceof DyeableLeatherItem)) {
            return InteractionResult.PASS;
        }
        DyeableLeatherItem $$7 = (DyeableLeatherItem)((Object)$$6);
        if (!$$7.hasCustomColor($$5)) {
            return InteractionResult.PASS;
        }
        if (!$$1.isClientSide) {
            $$7.clearColor($$5);
            $$3.awardStat(Stats.CLEAN_ARMOR);
            LayeredCauldronBlock.lowerFillLevel($$0, $$1, $$2);
        }
        return InteractionResult.sidedSuccess($$1.isClientSide);
    };

    public static Object2ObjectOpenHashMap<Item, CauldronInteraction> newInteractionMap() {
        return Util.make(new Object2ObjectOpenHashMap(), $$02 -> $$02.defaultReturnValue(($$0, $$1, $$2, $$3, $$4, $$5) -> InteractionResult.PASS));
    }

    public InteractionResult interact(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, ItemStack var6);

    public static void bootStrap() {
        CauldronInteraction.addDefaultInteractions(EMPTY);
        EMPTY.put((Object)Items.POTION, ($$0, $$1, $$2, $$3, $$4, $$5) -> {
            if (PotionUtils.getPotion($$5) != Potions.WATER) {
                return InteractionResult.PASS;
            }
            if (!$$1.isClientSide) {
                Item $$6 = $$5.getItem();
                $$3.setItemInHand($$4, ItemUtils.createFilledResult($$5, $$3, new ItemStack(Items.GLASS_BOTTLE)));
                $$3.awardStat(Stats.USE_CAULDRON);
                $$3.awardStat(Stats.ITEM_USED.get($$6));
                $$1.setBlockAndUpdate($$2, Blocks.WATER_CAULDRON.defaultBlockState());
                $$1.playSound((Player)null, $$2, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f);
                $$1.gameEvent(null, GameEvent.FLUID_PLACE, $$2);
            }
            return InteractionResult.sidedSuccess($$1.isClientSide);
        });
        CauldronInteraction.addDefaultInteractions(WATER);
        WATER.put((Object)Items.BUCKET, ($$02, $$1, $$2, $$3, $$4, $$5) -> CauldronInteraction.fillBucket($$02, $$1, $$2, $$3, $$4, $$5, new ItemStack(Items.WATER_BUCKET), (Predicate<BlockState>)((Predicate)$$0 -> $$0.getValue(LayeredCauldronBlock.LEVEL) == 3), SoundEvents.BUCKET_FILL));
        WATER.put((Object)Items.GLASS_BOTTLE, ($$0, $$1, $$2, $$3, $$4, $$5) -> {
            if (!$$1.isClientSide) {
                Item $$6 = $$5.getItem();
                $$3.setItemInHand($$4, ItemUtils.createFilledResult($$5, $$3, PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER)));
                $$3.awardStat(Stats.USE_CAULDRON);
                $$3.awardStat(Stats.ITEM_USED.get($$6));
                LayeredCauldronBlock.lowerFillLevel($$0, $$1, $$2);
                $$1.playSound((Player)null, $$2, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0f, 1.0f);
                $$1.gameEvent(null, GameEvent.FLUID_PICKUP, $$2);
            }
            return InteractionResult.sidedSuccess($$1.isClientSide);
        });
        WATER.put((Object)Items.POTION, ($$0, $$1, $$2, $$3, $$4, $$5) -> {
            if ($$0.getValue(LayeredCauldronBlock.LEVEL) == 3 || PotionUtils.getPotion($$5) != Potions.WATER) {
                return InteractionResult.PASS;
            }
            if (!$$1.isClientSide) {
                $$3.setItemInHand($$4, ItemUtils.createFilledResult($$5, $$3, new ItemStack(Items.GLASS_BOTTLE)));
                $$3.awardStat(Stats.USE_CAULDRON);
                $$3.awardStat(Stats.ITEM_USED.get($$5.getItem()));
                $$1.setBlockAndUpdate($$2, (BlockState)$$0.cycle(LayeredCauldronBlock.LEVEL));
                $$1.playSound((Player)null, $$2, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f);
                $$1.gameEvent(null, GameEvent.FLUID_PLACE, $$2);
            }
            return InteractionResult.sidedSuccess($$1.isClientSide);
        });
        WATER.put((Object)Items.LEATHER_BOOTS, (Object)DYED_ITEM);
        WATER.put((Object)Items.LEATHER_LEGGINGS, (Object)DYED_ITEM);
        WATER.put((Object)Items.LEATHER_CHESTPLATE, (Object)DYED_ITEM);
        WATER.put((Object)Items.LEATHER_HELMET, (Object)DYED_ITEM);
        WATER.put((Object)Items.LEATHER_HORSE_ARMOR, (Object)DYED_ITEM);
        WATER.put((Object)Items.WHITE_BANNER, (Object)BANNER);
        WATER.put((Object)Items.GRAY_BANNER, (Object)BANNER);
        WATER.put((Object)Items.BLACK_BANNER, (Object)BANNER);
        WATER.put((Object)Items.BLUE_BANNER, (Object)BANNER);
        WATER.put((Object)Items.BROWN_BANNER, (Object)BANNER);
        WATER.put((Object)Items.CYAN_BANNER, (Object)BANNER);
        WATER.put((Object)Items.GREEN_BANNER, (Object)BANNER);
        WATER.put((Object)Items.LIGHT_BLUE_BANNER, (Object)BANNER);
        WATER.put((Object)Items.LIGHT_GRAY_BANNER, (Object)BANNER);
        WATER.put((Object)Items.LIME_BANNER, (Object)BANNER);
        WATER.put((Object)Items.MAGENTA_BANNER, (Object)BANNER);
        WATER.put((Object)Items.ORANGE_BANNER, (Object)BANNER);
        WATER.put((Object)Items.PINK_BANNER, (Object)BANNER);
        WATER.put((Object)Items.PURPLE_BANNER, (Object)BANNER);
        WATER.put((Object)Items.RED_BANNER, (Object)BANNER);
        WATER.put((Object)Items.YELLOW_BANNER, (Object)BANNER);
        WATER.put((Object)Items.WHITE_SHULKER_BOX, (Object)SHULKER_BOX);
        WATER.put((Object)Items.GRAY_SHULKER_BOX, (Object)SHULKER_BOX);
        WATER.put((Object)Items.BLACK_SHULKER_BOX, (Object)SHULKER_BOX);
        WATER.put((Object)Items.BLUE_SHULKER_BOX, (Object)SHULKER_BOX);
        WATER.put((Object)Items.BROWN_SHULKER_BOX, (Object)SHULKER_BOX);
        WATER.put((Object)Items.CYAN_SHULKER_BOX, (Object)SHULKER_BOX);
        WATER.put((Object)Items.GREEN_SHULKER_BOX, (Object)SHULKER_BOX);
        WATER.put((Object)Items.LIGHT_BLUE_SHULKER_BOX, (Object)SHULKER_BOX);
        WATER.put((Object)Items.LIGHT_GRAY_SHULKER_BOX, (Object)SHULKER_BOX);
        WATER.put((Object)Items.LIME_SHULKER_BOX, (Object)SHULKER_BOX);
        WATER.put((Object)Items.MAGENTA_SHULKER_BOX, (Object)SHULKER_BOX);
        WATER.put((Object)Items.ORANGE_SHULKER_BOX, (Object)SHULKER_BOX);
        WATER.put((Object)Items.PINK_SHULKER_BOX, (Object)SHULKER_BOX);
        WATER.put((Object)Items.PURPLE_SHULKER_BOX, (Object)SHULKER_BOX);
        WATER.put((Object)Items.RED_SHULKER_BOX, (Object)SHULKER_BOX);
        WATER.put((Object)Items.YELLOW_SHULKER_BOX, (Object)SHULKER_BOX);
        LAVA.put((Object)Items.BUCKET, ($$02, $$1, $$2, $$3, $$4, $$5) -> CauldronInteraction.fillBucket($$02, $$1, $$2, $$3, $$4, $$5, new ItemStack(Items.LAVA_BUCKET), (Predicate<BlockState>)((Predicate)$$0 -> true), SoundEvents.BUCKET_FILL_LAVA));
        CauldronInteraction.addDefaultInteractions(LAVA);
        POWDER_SNOW.put((Object)Items.BUCKET, ($$02, $$1, $$2, $$3, $$4, $$5) -> CauldronInteraction.fillBucket($$02, $$1, $$2, $$3, $$4, $$5, new ItemStack(Items.POWDER_SNOW_BUCKET), (Predicate<BlockState>)((Predicate)$$0 -> $$0.getValue(LayeredCauldronBlock.LEVEL) == 3), SoundEvents.BUCKET_FILL_POWDER_SNOW));
        CauldronInteraction.addDefaultInteractions(POWDER_SNOW);
    }

    public static void addDefaultInteractions(Map<Item, CauldronInteraction> $$0) {
        $$0.put((Object)Items.LAVA_BUCKET, (Object)FILL_LAVA);
        $$0.put((Object)Items.WATER_BUCKET, (Object)FILL_WATER);
        $$0.put((Object)Items.POWDER_SNOW_BUCKET, (Object)FILL_POWDER_SNOW);
    }

    public static InteractionResult fillBucket(BlockState $$0, Level $$1, BlockPos $$2, Player $$3, InteractionHand $$4, ItemStack $$5, ItemStack $$6, Predicate<BlockState> $$7, SoundEvent $$8) {
        if (!$$7.test((Object)$$0)) {
            return InteractionResult.PASS;
        }
        if (!$$1.isClientSide) {
            Item $$9 = $$5.getItem();
            $$3.setItemInHand($$4, ItemUtils.createFilledResult($$5, $$3, $$6));
            $$3.awardStat(Stats.USE_CAULDRON);
            $$3.awardStat(Stats.ITEM_USED.get($$9));
            $$1.setBlockAndUpdate($$2, Blocks.CAULDRON.defaultBlockState());
            $$1.playSound((Player)null, $$2, $$8, SoundSource.BLOCKS, 1.0f, 1.0f);
            $$1.gameEvent(null, GameEvent.FLUID_PICKUP, $$2);
        }
        return InteractionResult.sidedSuccess($$1.isClientSide);
    }

    public static InteractionResult emptyBucket(Level $$0, BlockPos $$1, Player $$2, InteractionHand $$3, ItemStack $$4, BlockState $$5, SoundEvent $$6) {
        if (!$$0.isClientSide) {
            Item $$7 = $$4.getItem();
            $$2.setItemInHand($$3, ItemUtils.createFilledResult($$4, $$2, new ItemStack(Items.BUCKET)));
            $$2.awardStat(Stats.FILL_CAULDRON);
            $$2.awardStat(Stats.ITEM_USED.get($$7));
            $$0.setBlockAndUpdate($$1, $$5);
            $$0.playSound((Player)null, $$1, $$6, SoundSource.BLOCKS, 1.0f, 1.0f);
            $$0.gameEvent(null, GameEvent.FLUID_PLACE, $$1);
        }
        return InteractionResult.sidedSuccess($$0.isClientSide);
    }
}