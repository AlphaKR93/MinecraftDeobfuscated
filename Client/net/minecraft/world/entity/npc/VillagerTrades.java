/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  java.lang.IllegalStateException
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.Map
 *  java.util.stream.Collectors
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.npc;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.StructureTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.DyeableArmorItem;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.SuspiciousStewItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class VillagerTrades {
    private static final int DEFAULT_SUPPLY = 12;
    private static final int COMMON_ITEMS_SUPPLY = 16;
    private static final int UNCOMMON_ITEMS_SUPPLY = 3;
    private static final int XP_LEVEL_1_SELL = 1;
    private static final int XP_LEVEL_1_BUY = 2;
    private static final int XP_LEVEL_2_SELL = 5;
    private static final int XP_LEVEL_2_BUY = 10;
    private static final int XP_LEVEL_3_SELL = 10;
    private static final int XP_LEVEL_3_BUY = 20;
    private static final int XP_LEVEL_4_SELL = 15;
    private static final int XP_LEVEL_4_BUY = 30;
    private static final int XP_LEVEL_5_TRADE = 30;
    private static final float LOW_TIER_PRICE_MULTIPLIER = 0.05f;
    private static final float HIGH_TIER_PRICE_MULTIPLIER = 0.2f;
    public static final Map<VillagerProfession, Int2ObjectMap<ItemListing[]>> TRADES = (Map)Util.make(Maps.newHashMap(), $$0 -> {
        $$0.put((Object)VillagerProfession.FARMER, VillagerTrades.toIntMap((ImmutableMap<Integer, ItemListing[]>)ImmutableMap.of((Object)1, (Object)new ItemListing[]{new EmeraldForItems(Items.WHEAT, 20, 16, 2), new EmeraldForItems(Items.POTATO, 26, 16, 2), new EmeraldForItems(Items.CARROT, 22, 16, 2), new EmeraldForItems(Items.BEETROOT, 15, 16, 2), new ItemsForEmeralds(Items.BREAD, 1, 6, 16, 1)}, (Object)2, (Object)new ItemListing[]{new EmeraldForItems(Blocks.PUMPKIN, 6, 12, 10), new ItemsForEmeralds(Items.PUMPKIN_PIE, 1, 4, 5), new ItemsForEmeralds(Items.APPLE, 1, 4, 16, 5)}, (Object)3, (Object)new ItemListing[]{new ItemsForEmeralds(Items.COOKIE, 3, 18, 10), new EmeraldForItems(Blocks.MELON, 4, 12, 20)}, (Object)4, (Object)new ItemListing[]{new ItemsForEmeralds(Blocks.CAKE, 1, 1, 12, 15), new SuspiciousStewForEmerald(MobEffects.NIGHT_VISION, 100, 15), new SuspiciousStewForEmerald(MobEffects.JUMP, 160, 15), new SuspiciousStewForEmerald(MobEffects.WEAKNESS, 140, 15), new SuspiciousStewForEmerald(MobEffects.BLINDNESS, 120, 15), new SuspiciousStewForEmerald(MobEffects.POISON, 280, 15), new SuspiciousStewForEmerald(MobEffects.SATURATION, 7, 15)}, (Object)5, (Object)new ItemListing[]{new ItemsForEmeralds(Items.GOLDEN_CARROT, 3, 3, 30), new ItemsForEmeralds(Items.GLISTERING_MELON_SLICE, 4, 3, 30)})));
        $$0.put((Object)VillagerProfession.FISHERMAN, VillagerTrades.toIntMap((ImmutableMap<Integer, ItemListing[]>)ImmutableMap.of((Object)1, (Object)new ItemListing[]{new EmeraldForItems(Items.STRING, 20, 16, 2), new EmeraldForItems(Items.COAL, 10, 16, 2), new ItemsAndEmeraldsToItems(Items.COD, 6, Items.COOKED_COD, 6, 16, 1), new ItemsForEmeralds(Items.COD_BUCKET, 3, 1, 16, 1)}, (Object)2, (Object)new ItemListing[]{new EmeraldForItems(Items.COD, 15, 16, 10), new ItemsAndEmeraldsToItems(Items.SALMON, 6, Items.COOKED_SALMON, 6, 16, 5), new ItemsForEmeralds(Items.CAMPFIRE, 2, 1, 5)}, (Object)3, (Object)new ItemListing[]{new EmeraldForItems(Items.SALMON, 13, 16, 20), new EnchantedItemForEmeralds(Items.FISHING_ROD, 3, 3, 10, 0.2f)}, (Object)4, (Object)new ItemListing[]{new EmeraldForItems(Items.TROPICAL_FISH, 6, 12, 30)}, (Object)5, (Object)new ItemListing[]{new EmeraldForItems(Items.PUFFERFISH, 4, 12, 30), new EmeraldsForVillagerTypeItem(1, 12, 30, (Map<VillagerType, Item>)ImmutableMap.builder().put((Object)VillagerType.PLAINS, (Object)Items.OAK_BOAT).put((Object)VillagerType.TAIGA, (Object)Items.SPRUCE_BOAT).put((Object)VillagerType.SNOW, (Object)Items.SPRUCE_BOAT).put((Object)VillagerType.DESERT, (Object)Items.JUNGLE_BOAT).put((Object)VillagerType.JUNGLE, (Object)Items.JUNGLE_BOAT).put((Object)VillagerType.SAVANNA, (Object)Items.ACACIA_BOAT).put((Object)VillagerType.SWAMP, (Object)Items.DARK_OAK_BOAT).build())})));
        $$0.put((Object)VillagerProfession.SHEPHERD, VillagerTrades.toIntMap((ImmutableMap<Integer, ItemListing[]>)ImmutableMap.of((Object)1, (Object)new ItemListing[]{new EmeraldForItems(Blocks.WHITE_WOOL, 18, 16, 2), new EmeraldForItems(Blocks.BROWN_WOOL, 18, 16, 2), new EmeraldForItems(Blocks.BLACK_WOOL, 18, 16, 2), new EmeraldForItems(Blocks.GRAY_WOOL, 18, 16, 2), new ItemsForEmeralds(Items.SHEARS, 2, 1, 1)}, (Object)2, (Object)new ItemListing[]{new EmeraldForItems(Items.WHITE_DYE, 12, 16, 10), new EmeraldForItems(Items.GRAY_DYE, 12, 16, 10), new EmeraldForItems(Items.BLACK_DYE, 12, 16, 10), new EmeraldForItems(Items.LIGHT_BLUE_DYE, 12, 16, 10), new EmeraldForItems(Items.LIME_DYE, 12, 16, 10), new ItemsForEmeralds(Blocks.WHITE_WOOL, 1, 1, 16, 5), new ItemsForEmeralds(Blocks.ORANGE_WOOL, 1, 1, 16, 5), new ItemsForEmeralds(Blocks.MAGENTA_WOOL, 1, 1, 16, 5), new ItemsForEmeralds(Blocks.LIGHT_BLUE_WOOL, 1, 1, 16, 5), new ItemsForEmeralds(Blocks.YELLOW_WOOL, 1, 1, 16, 5), new ItemsForEmeralds(Blocks.LIME_WOOL, 1, 1, 16, 5), new ItemsForEmeralds(Blocks.PINK_WOOL, 1, 1, 16, 5), new ItemsForEmeralds(Blocks.GRAY_WOOL, 1, 1, 16, 5), new ItemsForEmeralds(Blocks.LIGHT_GRAY_WOOL, 1, 1, 16, 5), new ItemsForEmeralds(Blocks.CYAN_WOOL, 1, 1, 16, 5), new ItemsForEmeralds(Blocks.PURPLE_WOOL, 1, 1, 16, 5), new ItemsForEmeralds(Blocks.BLUE_WOOL, 1, 1, 16, 5), new ItemsForEmeralds(Blocks.BROWN_WOOL, 1, 1, 16, 5), new ItemsForEmeralds(Blocks.GREEN_WOOL, 1, 1, 16, 5), new ItemsForEmeralds(Blocks.RED_WOOL, 1, 1, 16, 5), new ItemsForEmeralds(Blocks.BLACK_WOOL, 1, 1, 16, 5), new ItemsForEmeralds(Blocks.WHITE_CARPET, 1, 4, 16, 5), new ItemsForEmeralds(Blocks.ORANGE_CARPET, 1, 4, 16, 5), new ItemsForEmeralds(Blocks.MAGENTA_CARPET, 1, 4, 16, 5), new ItemsForEmeralds(Blocks.LIGHT_BLUE_CARPET, 1, 4, 16, 5), new ItemsForEmeralds(Blocks.YELLOW_CARPET, 1, 4, 16, 5), new ItemsForEmeralds(Blocks.LIME_CARPET, 1, 4, 16, 5), new ItemsForEmeralds(Blocks.PINK_CARPET, 1, 4, 16, 5), new ItemsForEmeralds(Blocks.GRAY_CARPET, 1, 4, 16, 5), new ItemsForEmeralds(Blocks.LIGHT_GRAY_CARPET, 1, 4, 16, 5), new ItemsForEmeralds(Blocks.CYAN_CARPET, 1, 4, 16, 5), new ItemsForEmeralds(Blocks.PURPLE_CARPET, 1, 4, 16, 5), new ItemsForEmeralds(Blocks.BLUE_CARPET, 1, 4, 16, 5), new ItemsForEmeralds(Blocks.BROWN_CARPET, 1, 4, 16, 5), new ItemsForEmeralds(Blocks.GREEN_CARPET, 1, 4, 16, 5), new ItemsForEmeralds(Blocks.RED_CARPET, 1, 4, 16, 5), new ItemsForEmeralds(Blocks.BLACK_CARPET, 1, 4, 16, 5)}, (Object)3, (Object)new ItemListing[]{new EmeraldForItems(Items.YELLOW_DYE, 12, 16, 20), new EmeraldForItems(Items.LIGHT_GRAY_DYE, 12, 16, 20), new EmeraldForItems(Items.ORANGE_DYE, 12, 16, 20), new EmeraldForItems(Items.RED_DYE, 12, 16, 20), new EmeraldForItems(Items.PINK_DYE, 12, 16, 20), new ItemsForEmeralds(Blocks.WHITE_BED, 3, 1, 12, 10), new ItemsForEmeralds(Blocks.YELLOW_BED, 3, 1, 12, 10), new ItemsForEmeralds(Blocks.RED_BED, 3, 1, 12, 10), new ItemsForEmeralds(Blocks.BLACK_BED, 3, 1, 12, 10), new ItemsForEmeralds(Blocks.BLUE_BED, 3, 1, 12, 10), new ItemsForEmeralds(Blocks.BROWN_BED, 3, 1, 12, 10), new ItemsForEmeralds(Blocks.CYAN_BED, 3, 1, 12, 10), new ItemsForEmeralds(Blocks.GRAY_BED, 3, 1, 12, 10), new ItemsForEmeralds(Blocks.GREEN_BED, 3, 1, 12, 10), new ItemsForEmeralds(Blocks.LIGHT_BLUE_BED, 3, 1, 12, 10), new ItemsForEmeralds(Blocks.LIGHT_GRAY_BED, 3, 1, 12, 10), new ItemsForEmeralds(Blocks.LIME_BED, 3, 1, 12, 10), new ItemsForEmeralds(Blocks.MAGENTA_BED, 3, 1, 12, 10), new ItemsForEmeralds(Blocks.ORANGE_BED, 3, 1, 12, 10), new ItemsForEmeralds(Blocks.PINK_BED, 3, 1, 12, 10), new ItemsForEmeralds(Blocks.PURPLE_BED, 3, 1, 12, 10)}, (Object)4, (Object)new ItemListing[]{new EmeraldForItems(Items.BROWN_DYE, 12, 16, 30), new EmeraldForItems(Items.PURPLE_DYE, 12, 16, 30), new EmeraldForItems(Items.BLUE_DYE, 12, 16, 30), new EmeraldForItems(Items.GREEN_DYE, 12, 16, 30), new EmeraldForItems(Items.MAGENTA_DYE, 12, 16, 30), new EmeraldForItems(Items.CYAN_DYE, 12, 16, 30), new ItemsForEmeralds(Items.WHITE_BANNER, 3, 1, 12, 15), new ItemsForEmeralds(Items.BLUE_BANNER, 3, 1, 12, 15), new ItemsForEmeralds(Items.LIGHT_BLUE_BANNER, 3, 1, 12, 15), new ItemsForEmeralds(Items.RED_BANNER, 3, 1, 12, 15), new ItemsForEmeralds(Items.PINK_BANNER, 3, 1, 12, 15), new ItemsForEmeralds(Items.GREEN_BANNER, 3, 1, 12, 15), new ItemsForEmeralds(Items.LIME_BANNER, 3, 1, 12, 15), new ItemsForEmeralds(Items.GRAY_BANNER, 3, 1, 12, 15), new ItemsForEmeralds(Items.BLACK_BANNER, 3, 1, 12, 15), new ItemsForEmeralds(Items.PURPLE_BANNER, 3, 1, 12, 15), new ItemsForEmeralds(Items.MAGENTA_BANNER, 3, 1, 12, 15), new ItemsForEmeralds(Items.CYAN_BANNER, 3, 1, 12, 15), new ItemsForEmeralds(Items.BROWN_BANNER, 3, 1, 12, 15), new ItemsForEmeralds(Items.YELLOW_BANNER, 3, 1, 12, 15), new ItemsForEmeralds(Items.ORANGE_BANNER, 3, 1, 12, 15), new ItemsForEmeralds(Items.LIGHT_GRAY_BANNER, 3, 1, 12, 15)}, (Object)5, (Object)new ItemListing[]{new ItemsForEmeralds(Items.PAINTING, 2, 3, 30)})));
        $$0.put((Object)VillagerProfession.FLETCHER, VillagerTrades.toIntMap((ImmutableMap<Integer, ItemListing[]>)ImmutableMap.of((Object)1, (Object)new ItemListing[]{new EmeraldForItems(Items.STICK, 32, 16, 2), new ItemsForEmeralds(Items.ARROW, 1, 16, 1), new ItemsAndEmeraldsToItems(Blocks.GRAVEL, 10, Items.FLINT, 10, 12, 1)}, (Object)2, (Object)new ItemListing[]{new EmeraldForItems(Items.FLINT, 26, 12, 10), new ItemsForEmeralds(Items.BOW, 2, 1, 5)}, (Object)3, (Object)new ItemListing[]{new EmeraldForItems(Items.STRING, 14, 16, 20), new ItemsForEmeralds(Items.CROSSBOW, 3, 1, 10)}, (Object)4, (Object)new ItemListing[]{new EmeraldForItems(Items.FEATHER, 24, 16, 30), new EnchantedItemForEmeralds(Items.BOW, 2, 3, 15)}, (Object)5, (Object)new ItemListing[]{new EmeraldForItems(Items.TRIPWIRE_HOOK, 8, 12, 30), new EnchantedItemForEmeralds(Items.CROSSBOW, 3, 3, 15), new TippedArrowForItemsAndEmeralds(Items.ARROW, 5, Items.TIPPED_ARROW, 5, 2, 12, 30)})));
        $$0.put((Object)VillagerProfession.LIBRARIAN, VillagerTrades.toIntMap((ImmutableMap<Integer, ItemListing[]>)ImmutableMap.builder().put((Object)1, (Object)new ItemListing[]{new EmeraldForItems(Items.PAPER, 24, 16, 2), new EnchantBookForEmeralds(1), new ItemsForEmeralds(Blocks.BOOKSHELF, 9, 1, 12, 1)}).put((Object)2, (Object)new ItemListing[]{new EmeraldForItems(Items.BOOK, 4, 12, 10), new EnchantBookForEmeralds(5), new ItemsForEmeralds(Items.LANTERN, 1, 1, 5)}).put((Object)3, (Object)new ItemListing[]{new EmeraldForItems(Items.INK_SAC, 5, 12, 20), new EnchantBookForEmeralds(10), new ItemsForEmeralds(Items.GLASS, 1, 4, 10)}).put((Object)4, (Object)new ItemListing[]{new EmeraldForItems(Items.WRITABLE_BOOK, 2, 12, 30), new EnchantBookForEmeralds(15), new ItemsForEmeralds(Items.CLOCK, 5, 1, 15), new ItemsForEmeralds(Items.COMPASS, 4, 1, 15)}).put((Object)5, (Object)new ItemListing[]{new ItemsForEmeralds(Items.NAME_TAG, 20, 1, 30)}).build()));
        $$0.put((Object)VillagerProfession.CARTOGRAPHER, VillagerTrades.toIntMap((ImmutableMap<Integer, ItemListing[]>)ImmutableMap.of((Object)1, (Object)new ItemListing[]{new EmeraldForItems(Items.PAPER, 24, 16, 2), new ItemsForEmeralds(Items.MAP, 7, 1, 1)}, (Object)2, (Object)new ItemListing[]{new EmeraldForItems(Items.GLASS_PANE, 11, 16, 10), new TreasureMapForEmeralds(13, StructureTags.ON_OCEAN_EXPLORER_MAPS, "filled_map.monument", MapDecoration.Type.MONUMENT, 12, 5)}, (Object)3, (Object)new ItemListing[]{new EmeraldForItems(Items.COMPASS, 1, 12, 20), new TreasureMapForEmeralds(14, StructureTags.ON_WOODLAND_EXPLORER_MAPS, "filled_map.mansion", MapDecoration.Type.MANSION, 12, 10)}, (Object)4, (Object)new ItemListing[]{new ItemsForEmeralds(Items.ITEM_FRAME, 7, 1, 15), new ItemsForEmeralds(Items.WHITE_BANNER, 3, 1, 15), new ItemsForEmeralds(Items.BLUE_BANNER, 3, 1, 15), new ItemsForEmeralds(Items.LIGHT_BLUE_BANNER, 3, 1, 15), new ItemsForEmeralds(Items.RED_BANNER, 3, 1, 15), new ItemsForEmeralds(Items.PINK_BANNER, 3, 1, 15), new ItemsForEmeralds(Items.GREEN_BANNER, 3, 1, 15), new ItemsForEmeralds(Items.LIME_BANNER, 3, 1, 15), new ItemsForEmeralds(Items.GRAY_BANNER, 3, 1, 15), new ItemsForEmeralds(Items.BLACK_BANNER, 3, 1, 15), new ItemsForEmeralds(Items.PURPLE_BANNER, 3, 1, 15), new ItemsForEmeralds(Items.MAGENTA_BANNER, 3, 1, 15), new ItemsForEmeralds(Items.CYAN_BANNER, 3, 1, 15), new ItemsForEmeralds(Items.BROWN_BANNER, 3, 1, 15), new ItemsForEmeralds(Items.YELLOW_BANNER, 3, 1, 15), new ItemsForEmeralds(Items.ORANGE_BANNER, 3, 1, 15), new ItemsForEmeralds(Items.LIGHT_GRAY_BANNER, 3, 1, 15)}, (Object)5, (Object)new ItemListing[]{new ItemsForEmeralds(Items.GLOBE_BANNER_PATTERN, 8, 1, 30)})));
        $$0.put((Object)VillagerProfession.CLERIC, VillagerTrades.toIntMap((ImmutableMap<Integer, ItemListing[]>)ImmutableMap.of((Object)1, (Object)new ItemListing[]{new EmeraldForItems(Items.ROTTEN_FLESH, 32, 16, 2), new ItemsForEmeralds(Items.REDSTONE, 1, 2, 1)}, (Object)2, (Object)new ItemListing[]{new EmeraldForItems(Items.GOLD_INGOT, 3, 12, 10), new ItemsForEmeralds(Items.LAPIS_LAZULI, 1, 1, 5)}, (Object)3, (Object)new ItemListing[]{new EmeraldForItems(Items.RABBIT_FOOT, 2, 12, 20), new ItemsForEmeralds(Blocks.GLOWSTONE, 4, 1, 12, 10)}, (Object)4, (Object)new ItemListing[]{new EmeraldForItems(Items.SCUTE, 4, 12, 30), new EmeraldForItems(Items.GLASS_BOTTLE, 9, 12, 30), new ItemsForEmeralds(Items.ENDER_PEARL, 5, 1, 15)}, (Object)5, (Object)new ItemListing[]{new EmeraldForItems(Items.NETHER_WART, 22, 12, 30), new ItemsForEmeralds(Items.EXPERIENCE_BOTTLE, 3, 1, 30)})));
        $$0.put((Object)VillagerProfession.ARMORER, VillagerTrades.toIntMap((ImmutableMap<Integer, ItemListing[]>)ImmutableMap.of((Object)1, (Object)new ItemListing[]{new EmeraldForItems(Items.COAL, 15, 16, 2), new ItemsForEmeralds(new ItemStack(Items.IRON_LEGGINGS), 7, 1, 12, 1, 0.2f), new ItemsForEmeralds(new ItemStack(Items.IRON_BOOTS), 4, 1, 12, 1, 0.2f), new ItemsForEmeralds(new ItemStack(Items.IRON_HELMET), 5, 1, 12, 1, 0.2f), new ItemsForEmeralds(new ItemStack(Items.IRON_CHESTPLATE), 9, 1, 12, 1, 0.2f)}, (Object)2, (Object)new ItemListing[]{new EmeraldForItems(Items.IRON_INGOT, 4, 12, 10), new ItemsForEmeralds(new ItemStack(Items.BELL), 36, 1, 12, 5, 0.2f), new ItemsForEmeralds(new ItemStack(Items.CHAINMAIL_BOOTS), 1, 1, 12, 5, 0.2f), new ItemsForEmeralds(new ItemStack(Items.CHAINMAIL_LEGGINGS), 3, 1, 12, 5, 0.2f)}, (Object)3, (Object)new ItemListing[]{new EmeraldForItems(Items.LAVA_BUCKET, 1, 12, 20), new EmeraldForItems(Items.DIAMOND, 1, 12, 20), new ItemsForEmeralds(new ItemStack(Items.CHAINMAIL_HELMET), 1, 1, 12, 10, 0.2f), new ItemsForEmeralds(new ItemStack(Items.CHAINMAIL_CHESTPLATE), 4, 1, 12, 10, 0.2f), new ItemsForEmeralds(new ItemStack(Items.SHIELD), 5, 1, 12, 10, 0.2f)}, (Object)4, (Object)new ItemListing[]{new EnchantedItemForEmeralds(Items.DIAMOND_LEGGINGS, 14, 3, 15, 0.2f), new EnchantedItemForEmeralds(Items.DIAMOND_BOOTS, 8, 3, 15, 0.2f)}, (Object)5, (Object)new ItemListing[]{new EnchantedItemForEmeralds(Items.DIAMOND_HELMET, 8, 3, 30, 0.2f), new EnchantedItemForEmeralds(Items.DIAMOND_CHESTPLATE, 16, 3, 30, 0.2f)})));
        $$0.put((Object)VillagerProfession.WEAPONSMITH, VillagerTrades.toIntMap((ImmutableMap<Integer, ItemListing[]>)ImmutableMap.of((Object)1, (Object)new ItemListing[]{new EmeraldForItems(Items.COAL, 15, 16, 2), new ItemsForEmeralds(new ItemStack(Items.IRON_AXE), 3, 1, 12, 1, 0.2f), new EnchantedItemForEmeralds(Items.IRON_SWORD, 2, 3, 1)}, (Object)2, (Object)new ItemListing[]{new EmeraldForItems(Items.IRON_INGOT, 4, 12, 10), new ItemsForEmeralds(new ItemStack(Items.BELL), 36, 1, 12, 5, 0.2f)}, (Object)3, (Object)new ItemListing[]{new EmeraldForItems(Items.FLINT, 24, 12, 20)}, (Object)4, (Object)new ItemListing[]{new EmeraldForItems(Items.DIAMOND, 1, 12, 30), new EnchantedItemForEmeralds(Items.DIAMOND_AXE, 12, 3, 15, 0.2f)}, (Object)5, (Object)new ItemListing[]{new EnchantedItemForEmeralds(Items.DIAMOND_SWORD, 8, 3, 30, 0.2f)})));
        $$0.put((Object)VillagerProfession.TOOLSMITH, VillagerTrades.toIntMap((ImmutableMap<Integer, ItemListing[]>)ImmutableMap.of((Object)1, (Object)new ItemListing[]{new EmeraldForItems(Items.COAL, 15, 16, 2), new ItemsForEmeralds(new ItemStack(Items.STONE_AXE), 1, 1, 12, 1, 0.2f), new ItemsForEmeralds(new ItemStack(Items.STONE_SHOVEL), 1, 1, 12, 1, 0.2f), new ItemsForEmeralds(new ItemStack(Items.STONE_PICKAXE), 1, 1, 12, 1, 0.2f), new ItemsForEmeralds(new ItemStack(Items.STONE_HOE), 1, 1, 12, 1, 0.2f)}, (Object)2, (Object)new ItemListing[]{new EmeraldForItems(Items.IRON_INGOT, 4, 12, 10), new ItemsForEmeralds(new ItemStack(Items.BELL), 36, 1, 12, 5, 0.2f)}, (Object)3, (Object)new ItemListing[]{new EmeraldForItems(Items.FLINT, 30, 12, 20), new EnchantedItemForEmeralds(Items.IRON_AXE, 1, 3, 10, 0.2f), new EnchantedItemForEmeralds(Items.IRON_SHOVEL, 2, 3, 10, 0.2f), new EnchantedItemForEmeralds(Items.IRON_PICKAXE, 3, 3, 10, 0.2f), new ItemsForEmeralds(new ItemStack(Items.DIAMOND_HOE), 4, 1, 3, 10, 0.2f)}, (Object)4, (Object)new ItemListing[]{new EmeraldForItems(Items.DIAMOND, 1, 12, 30), new EnchantedItemForEmeralds(Items.DIAMOND_AXE, 12, 3, 15, 0.2f), new EnchantedItemForEmeralds(Items.DIAMOND_SHOVEL, 5, 3, 15, 0.2f)}, (Object)5, (Object)new ItemListing[]{new EnchantedItemForEmeralds(Items.DIAMOND_PICKAXE, 13, 3, 30, 0.2f)})));
        $$0.put((Object)VillagerProfession.BUTCHER, VillagerTrades.toIntMap((ImmutableMap<Integer, ItemListing[]>)ImmutableMap.of((Object)1, (Object)new ItemListing[]{new EmeraldForItems(Items.CHICKEN, 14, 16, 2), new EmeraldForItems(Items.PORKCHOP, 7, 16, 2), new EmeraldForItems(Items.RABBIT, 4, 16, 2), new ItemsForEmeralds(Items.RABBIT_STEW, 1, 1, 1)}, (Object)2, (Object)new ItemListing[]{new EmeraldForItems(Items.COAL, 15, 16, 2), new ItemsForEmeralds(Items.COOKED_PORKCHOP, 1, 5, 16, 5), new ItemsForEmeralds(Items.COOKED_CHICKEN, 1, 8, 16, 5)}, (Object)3, (Object)new ItemListing[]{new EmeraldForItems(Items.MUTTON, 7, 16, 20), new EmeraldForItems(Items.BEEF, 10, 16, 20)}, (Object)4, (Object)new ItemListing[]{new EmeraldForItems(Items.DRIED_KELP_BLOCK, 10, 12, 30)}, (Object)5, (Object)new ItemListing[]{new EmeraldForItems(Items.SWEET_BERRIES, 10, 12, 30)})));
        $$0.put((Object)VillagerProfession.LEATHERWORKER, VillagerTrades.toIntMap((ImmutableMap<Integer, ItemListing[]>)ImmutableMap.of((Object)1, (Object)new ItemListing[]{new EmeraldForItems(Items.LEATHER, 6, 16, 2), new DyedArmorForEmeralds(Items.LEATHER_LEGGINGS, 3), new DyedArmorForEmeralds(Items.LEATHER_CHESTPLATE, 7)}, (Object)2, (Object)new ItemListing[]{new EmeraldForItems(Items.FLINT, 26, 12, 10), new DyedArmorForEmeralds(Items.LEATHER_HELMET, 5, 12, 5), new DyedArmorForEmeralds(Items.LEATHER_BOOTS, 4, 12, 5)}, (Object)3, (Object)new ItemListing[]{new EmeraldForItems(Items.RABBIT_HIDE, 9, 12, 20), new DyedArmorForEmeralds(Items.LEATHER_CHESTPLATE, 7)}, (Object)4, (Object)new ItemListing[]{new EmeraldForItems(Items.SCUTE, 4, 12, 30), new DyedArmorForEmeralds(Items.LEATHER_HORSE_ARMOR, 6, 12, 15)}, (Object)5, (Object)new ItemListing[]{new ItemsForEmeralds(new ItemStack(Items.SADDLE), 6, 1, 12, 30, 0.2f), new DyedArmorForEmeralds(Items.LEATHER_HELMET, 5, 12, 30)})));
        $$0.put((Object)VillagerProfession.MASON, VillagerTrades.toIntMap((ImmutableMap<Integer, ItemListing[]>)ImmutableMap.of((Object)1, (Object)new ItemListing[]{new EmeraldForItems(Items.CLAY_BALL, 10, 16, 2), new ItemsForEmeralds(Items.BRICK, 1, 10, 16, 1)}, (Object)2, (Object)new ItemListing[]{new EmeraldForItems(Blocks.STONE, 20, 16, 10), new ItemsForEmeralds(Blocks.CHISELED_STONE_BRICKS, 1, 4, 16, 5)}, (Object)3, (Object)new ItemListing[]{new EmeraldForItems(Blocks.GRANITE, 16, 16, 20), new EmeraldForItems(Blocks.ANDESITE, 16, 16, 20), new EmeraldForItems(Blocks.DIORITE, 16, 16, 20), new ItemsForEmeralds(Blocks.DRIPSTONE_BLOCK, 1, 4, 16, 10), new ItemsForEmeralds(Blocks.POLISHED_ANDESITE, 1, 4, 16, 10), new ItemsForEmeralds(Blocks.POLISHED_DIORITE, 1, 4, 16, 10), new ItemsForEmeralds(Blocks.POLISHED_GRANITE, 1, 4, 16, 10)}, (Object)4, (Object)new ItemListing[]{new EmeraldForItems(Items.QUARTZ, 12, 12, 30), new ItemsForEmeralds(Blocks.ORANGE_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.WHITE_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.BLUE_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.LIGHT_BLUE_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.GRAY_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.LIGHT_GRAY_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.BLACK_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.RED_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.PINK_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.MAGENTA_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.LIME_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.GREEN_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.CYAN_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.PURPLE_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.YELLOW_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.BROWN_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.ORANGE_GLAZED_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.WHITE_GLAZED_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.BLUE_GLAZED_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.GRAY_GLAZED_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.BLACK_GLAZED_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.RED_GLAZED_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.PINK_GLAZED_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.MAGENTA_GLAZED_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.LIME_GLAZED_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.GREEN_GLAZED_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.CYAN_GLAZED_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.PURPLE_GLAZED_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.YELLOW_GLAZED_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.BROWN_GLAZED_TERRACOTTA, 1, 1, 12, 15)}, (Object)5, (Object)new ItemListing[]{new ItemsForEmeralds(Blocks.QUARTZ_PILLAR, 1, 1, 12, 30), new ItemsForEmeralds(Blocks.QUARTZ_BLOCK, 1, 1, 12, 30)})));
    });
    public static final Int2ObjectMap<ItemListing[]> WANDERING_TRADER_TRADES = VillagerTrades.toIntMap((ImmutableMap<Integer, ItemListing[]>)ImmutableMap.of((Object)1, (Object)new ItemListing[]{new ItemsForEmeralds(Items.SEA_PICKLE, 2, 1, 5, 1), new ItemsForEmeralds(Items.SLIME_BALL, 4, 1, 5, 1), new ItemsForEmeralds(Items.GLOWSTONE, 2, 1, 5, 1), new ItemsForEmeralds(Items.NAUTILUS_SHELL, 5, 1, 5, 1), new ItemsForEmeralds(Items.FERN, 1, 1, 12, 1), new ItemsForEmeralds(Items.SUGAR_CANE, 1, 1, 8, 1), new ItemsForEmeralds(Items.PUMPKIN, 1, 1, 4, 1), new ItemsForEmeralds(Items.KELP, 3, 1, 12, 1), new ItemsForEmeralds(Items.CACTUS, 3, 1, 8, 1), new ItemsForEmeralds(Items.DANDELION, 1, 1, 12, 1), new ItemsForEmeralds(Items.POPPY, 1, 1, 12, 1), new ItemsForEmeralds(Items.BLUE_ORCHID, 1, 1, 8, 1), new ItemsForEmeralds(Items.ALLIUM, 1, 1, 12, 1), new ItemsForEmeralds(Items.AZURE_BLUET, 1, 1, 12, 1), new ItemsForEmeralds(Items.RED_TULIP, 1, 1, 12, 1), new ItemsForEmeralds(Items.ORANGE_TULIP, 1, 1, 12, 1), new ItemsForEmeralds(Items.WHITE_TULIP, 1, 1, 12, 1), new ItemsForEmeralds(Items.PINK_TULIP, 1, 1, 12, 1), new ItemsForEmeralds(Items.OXEYE_DAISY, 1, 1, 12, 1), new ItemsForEmeralds(Items.CORNFLOWER, 1, 1, 12, 1), new ItemsForEmeralds(Items.LILY_OF_THE_VALLEY, 1, 1, 7, 1), new ItemsForEmeralds(Items.WHEAT_SEEDS, 1, 1, 12, 1), new ItemsForEmeralds(Items.BEETROOT_SEEDS, 1, 1, 12, 1), new ItemsForEmeralds(Items.PUMPKIN_SEEDS, 1, 1, 12, 1), new ItemsForEmeralds(Items.MELON_SEEDS, 1, 1, 12, 1), new ItemsForEmeralds(Items.ACACIA_SAPLING, 5, 1, 8, 1), new ItemsForEmeralds(Items.BIRCH_SAPLING, 5, 1, 8, 1), new ItemsForEmeralds(Items.DARK_OAK_SAPLING, 5, 1, 8, 1), new ItemsForEmeralds(Items.JUNGLE_SAPLING, 5, 1, 8, 1), new ItemsForEmeralds(Items.OAK_SAPLING, 5, 1, 8, 1), new ItemsForEmeralds(Items.SPRUCE_SAPLING, 5, 1, 8, 1), new ItemsForEmeralds(Items.MANGROVE_PROPAGULE, 5, 1, 8, 1), new ItemsForEmeralds(Items.RED_DYE, 1, 3, 12, 1), new ItemsForEmeralds(Items.WHITE_DYE, 1, 3, 12, 1), new ItemsForEmeralds(Items.BLUE_DYE, 1, 3, 12, 1), new ItemsForEmeralds(Items.PINK_DYE, 1, 3, 12, 1), new ItemsForEmeralds(Items.BLACK_DYE, 1, 3, 12, 1), new ItemsForEmeralds(Items.GREEN_DYE, 1, 3, 12, 1), new ItemsForEmeralds(Items.LIGHT_GRAY_DYE, 1, 3, 12, 1), new ItemsForEmeralds(Items.MAGENTA_DYE, 1, 3, 12, 1), new ItemsForEmeralds(Items.YELLOW_DYE, 1, 3, 12, 1), new ItemsForEmeralds(Items.GRAY_DYE, 1, 3, 12, 1), new ItemsForEmeralds(Items.PURPLE_DYE, 1, 3, 12, 1), new ItemsForEmeralds(Items.LIGHT_BLUE_DYE, 1, 3, 12, 1), new ItemsForEmeralds(Items.LIME_DYE, 1, 3, 12, 1), new ItemsForEmeralds(Items.ORANGE_DYE, 1, 3, 12, 1), new ItemsForEmeralds(Items.BROWN_DYE, 1, 3, 12, 1), new ItemsForEmeralds(Items.CYAN_DYE, 1, 3, 12, 1), new ItemsForEmeralds(Items.BRAIN_CORAL_BLOCK, 3, 1, 8, 1), new ItemsForEmeralds(Items.BUBBLE_CORAL_BLOCK, 3, 1, 8, 1), new ItemsForEmeralds(Items.FIRE_CORAL_BLOCK, 3, 1, 8, 1), new ItemsForEmeralds(Items.HORN_CORAL_BLOCK, 3, 1, 8, 1), new ItemsForEmeralds(Items.TUBE_CORAL_BLOCK, 3, 1, 8, 1), new ItemsForEmeralds(Items.VINE, 1, 1, 12, 1), new ItemsForEmeralds(Items.BROWN_MUSHROOM, 1, 1, 12, 1), new ItemsForEmeralds(Items.RED_MUSHROOM, 1, 1, 12, 1), new ItemsForEmeralds(Items.LILY_PAD, 1, 2, 5, 1), new ItemsForEmeralds(Items.SMALL_DRIPLEAF, 1, 2, 5, 1), new ItemsForEmeralds(Items.SAND, 1, 8, 8, 1), new ItemsForEmeralds(Items.RED_SAND, 1, 4, 6, 1), new ItemsForEmeralds(Items.POINTED_DRIPSTONE, 1, 2, 5, 1), new ItemsForEmeralds(Items.ROOTED_DIRT, 1, 2, 5, 1), new ItemsForEmeralds(Items.MOSS_BLOCK, 1, 2, 5, 1)}, (Object)2, (Object)new ItemListing[]{new ItemsForEmeralds(Items.TROPICAL_FISH_BUCKET, 5, 1, 4, 1), new ItemsForEmeralds(Items.PUFFERFISH_BUCKET, 5, 1, 4, 1), new ItemsForEmeralds(Items.PACKED_ICE, 3, 1, 6, 1), new ItemsForEmeralds(Items.BLUE_ICE, 6, 1, 6, 1), new ItemsForEmeralds(Items.GUNPOWDER, 1, 1, 8, 1), new ItemsForEmeralds(Items.PODZOL, 3, 3, 6, 1)}));

    private static Int2ObjectMap<ItemListing[]> toIntMap(ImmutableMap<Integer, ItemListing[]> $$0) {
        return new Int2ObjectOpenHashMap($$0);
    }

    public static interface ItemListing {
        @Nullable
        public MerchantOffer getOffer(Entity var1, RandomSource var2);
    }

    static class EmeraldForItems
    implements ItemListing {
        private final Item item;
        private final int cost;
        private final int maxUses;
        private final int villagerXp;
        private final float priceMultiplier;

        public EmeraldForItems(ItemLike $$0, int $$1, int $$2, int $$3) {
            this.item = $$0.asItem();
            this.cost = $$1;
            this.maxUses = $$2;
            this.villagerXp = $$3;
            this.priceMultiplier = 0.05f;
        }

        @Override
        public MerchantOffer getOffer(Entity $$0, RandomSource $$1) {
            ItemStack $$2 = new ItemStack(this.item, this.cost);
            return new MerchantOffer($$2, new ItemStack(Items.EMERALD), this.maxUses, this.villagerXp, this.priceMultiplier);
        }
    }

    static class ItemsForEmeralds
    implements ItemListing {
        private final ItemStack itemStack;
        private final int emeraldCost;
        private final int numberOfItems;
        private final int maxUses;
        private final int villagerXp;
        private final float priceMultiplier;

        public ItemsForEmeralds(Block $$0, int $$1, int $$2, int $$3, int $$4) {
            this(new ItemStack($$0), $$1, $$2, $$3, $$4);
        }

        public ItemsForEmeralds(Item $$0, int $$1, int $$2, int $$3) {
            this(new ItemStack($$0), $$1, $$2, 12, $$3);
        }

        public ItemsForEmeralds(Item $$0, int $$1, int $$2, int $$3, int $$4) {
            this(new ItemStack($$0), $$1, $$2, $$3, $$4);
        }

        public ItemsForEmeralds(ItemStack $$0, int $$1, int $$2, int $$3, int $$4) {
            this($$0, $$1, $$2, $$3, $$4, 0.05f);
        }

        public ItemsForEmeralds(ItemStack $$0, int $$1, int $$2, int $$3, int $$4, float $$5) {
            this.itemStack = $$0;
            this.emeraldCost = $$1;
            this.numberOfItems = $$2;
            this.maxUses = $$3;
            this.villagerXp = $$4;
            this.priceMultiplier = $$5;
        }

        @Override
        public MerchantOffer getOffer(Entity $$0, RandomSource $$1) {
            return new MerchantOffer(new ItemStack(Items.EMERALD, this.emeraldCost), new ItemStack(this.itemStack.getItem(), this.numberOfItems), this.maxUses, this.villagerXp, this.priceMultiplier);
        }
    }

    static class SuspiciousStewForEmerald
    implements ItemListing {
        final MobEffect effect;
        final int duration;
        final int xp;
        private final float priceMultiplier;

        public SuspiciousStewForEmerald(MobEffect $$0, int $$1, int $$2) {
            this.effect = $$0;
            this.duration = $$1;
            this.xp = $$2;
            this.priceMultiplier = 0.05f;
        }

        @Override
        @Nullable
        public MerchantOffer getOffer(Entity $$0, RandomSource $$1) {
            ItemStack $$2 = new ItemStack(Items.SUSPICIOUS_STEW, 1);
            SuspiciousStewItem.saveMobEffect($$2, this.effect, this.duration);
            return new MerchantOffer(new ItemStack(Items.EMERALD, 1), $$2, 12, this.xp, this.priceMultiplier);
        }
    }

    static class ItemsAndEmeraldsToItems
    implements ItemListing {
        private final ItemStack fromItem;
        private final int fromCount;
        private final int emeraldCost;
        private final ItemStack toItem;
        private final int toCount;
        private final int maxUses;
        private final int villagerXp;
        private final float priceMultiplier;

        public ItemsAndEmeraldsToItems(ItemLike $$0, int $$1, Item $$2, int $$3, int $$4, int $$5) {
            this($$0, $$1, 1, $$2, $$3, $$4, $$5);
        }

        public ItemsAndEmeraldsToItems(ItemLike $$0, int $$1, int $$2, Item $$3, int $$4, int $$5, int $$6) {
            this.fromItem = new ItemStack($$0);
            this.fromCount = $$1;
            this.emeraldCost = $$2;
            this.toItem = new ItemStack($$3);
            this.toCount = $$4;
            this.maxUses = $$5;
            this.villagerXp = $$6;
            this.priceMultiplier = 0.05f;
        }

        @Override
        @Nullable
        public MerchantOffer getOffer(Entity $$0, RandomSource $$1) {
            return new MerchantOffer(new ItemStack(Items.EMERALD, this.emeraldCost), new ItemStack(this.fromItem.getItem(), this.fromCount), new ItemStack(this.toItem.getItem(), this.toCount), this.maxUses, this.villagerXp, this.priceMultiplier);
        }
    }

    static class EnchantedItemForEmeralds
    implements ItemListing {
        private final ItemStack itemStack;
        private final int baseEmeraldCost;
        private final int maxUses;
        private final int villagerXp;
        private final float priceMultiplier;

        public EnchantedItemForEmeralds(Item $$0, int $$1, int $$2, int $$3) {
            this($$0, $$1, $$2, $$3, 0.05f);
        }

        public EnchantedItemForEmeralds(Item $$0, int $$1, int $$2, int $$3, float $$4) {
            this.itemStack = new ItemStack($$0);
            this.baseEmeraldCost = $$1;
            this.maxUses = $$2;
            this.villagerXp = $$3;
            this.priceMultiplier = $$4;
        }

        @Override
        public MerchantOffer getOffer(Entity $$0, RandomSource $$1) {
            int $$2 = 5 + $$1.nextInt(15);
            ItemStack $$3 = EnchantmentHelper.enchantItem($$1, new ItemStack(this.itemStack.getItem()), $$2, false);
            int $$4 = Math.min((int)(this.baseEmeraldCost + $$2), (int)64);
            ItemStack $$5 = new ItemStack(Items.EMERALD, $$4);
            return new MerchantOffer($$5, $$3, this.maxUses, this.villagerXp, this.priceMultiplier);
        }
    }

    static class EmeraldsForVillagerTypeItem
    implements ItemListing {
        private final Map<VillagerType, Item> trades;
        private final int cost;
        private final int maxUses;
        private final int villagerXp;

        public EmeraldsForVillagerTypeItem(int $$02, int $$12, int $$2, Map<VillagerType, Item> $$3) {
            BuiltInRegistries.VILLAGER_TYPE.stream().filter($$1 -> !$$3.containsKey($$1)).findAny().ifPresent($$0 -> {
                throw new IllegalStateException("Missing trade for villager type: " + BuiltInRegistries.VILLAGER_TYPE.getKey((VillagerType)$$0));
            });
            this.trades = $$3;
            this.cost = $$02;
            this.maxUses = $$12;
            this.villagerXp = $$2;
        }

        @Override
        @Nullable
        public MerchantOffer getOffer(Entity $$0, RandomSource $$1) {
            if ($$0 instanceof VillagerDataHolder) {
                ItemStack $$2 = new ItemStack((ItemLike)this.trades.get((Object)((VillagerDataHolder)((Object)$$0)).getVillagerData().getType()), this.cost);
                return new MerchantOffer($$2, new ItemStack(Items.EMERALD), this.maxUses, this.villagerXp, 0.05f);
            }
            return null;
        }
    }

    static class TippedArrowForItemsAndEmeralds
    implements ItemListing {
        private final ItemStack toItem;
        private final int toCount;
        private final int emeraldCost;
        private final int maxUses;
        private final int villagerXp;
        private final Item fromItem;
        private final int fromCount;
        private final float priceMultiplier;

        public TippedArrowForItemsAndEmeralds(Item $$0, int $$1, Item $$2, int $$3, int $$4, int $$5, int $$6) {
            this.toItem = new ItemStack($$2);
            this.emeraldCost = $$4;
            this.maxUses = $$5;
            this.villagerXp = $$6;
            this.fromItem = $$0;
            this.fromCount = $$1;
            this.toCount = $$3;
            this.priceMultiplier = 0.05f;
        }

        @Override
        public MerchantOffer getOffer(Entity $$02, RandomSource $$1) {
            ItemStack $$2 = new ItemStack(Items.EMERALD, this.emeraldCost);
            List $$3 = (List)BuiltInRegistries.POTION.stream().filter($$0 -> !$$0.getEffects().isEmpty() && PotionBrewing.isBrewablePotion($$0)).collect(Collectors.toList());
            Potion $$4 = (Potion)$$3.get($$1.nextInt($$3.size()));
            ItemStack $$5 = PotionUtils.setPotion(new ItemStack(this.toItem.getItem(), this.toCount), $$4);
            return new MerchantOffer($$2, new ItemStack(this.fromItem, this.fromCount), $$5, this.maxUses, this.villagerXp, this.priceMultiplier);
        }
    }

    static class EnchantBookForEmeralds
    implements ItemListing {
        private final int villagerXp;

        public EnchantBookForEmeralds(int $$0) {
            this.villagerXp = $$0;
        }

        @Override
        public MerchantOffer getOffer(Entity $$0, RandomSource $$1) {
            List $$2 = (List)BuiltInRegistries.ENCHANTMENT.stream().filter(Enchantment::isTradeable).collect(Collectors.toList());
            Enchantment $$3 = (Enchantment)$$2.get($$1.nextInt($$2.size()));
            int $$4 = Mth.nextInt($$1, $$3.getMinLevel(), $$3.getMaxLevel());
            ItemStack $$5 = EnchantedBookItem.createForEnchantment(new EnchantmentInstance($$3, $$4));
            int $$6 = 2 + $$1.nextInt(5 + $$4 * 10) + 3 * $$4;
            if ($$3.isTreasureOnly()) {
                $$6 *= 2;
            }
            if ($$6 > 64) {
                $$6 = 64;
            }
            return new MerchantOffer(new ItemStack(Items.EMERALD, $$6), new ItemStack(Items.BOOK), $$5, 12, this.villagerXp, 0.2f);
        }
    }

    static class TreasureMapForEmeralds
    implements ItemListing {
        private final int emeraldCost;
        private final TagKey<Structure> destination;
        private final String displayName;
        private final MapDecoration.Type destinationType;
        private final int maxUses;
        private final int villagerXp;

        public TreasureMapForEmeralds(int $$0, TagKey<Structure> $$1, String $$2, MapDecoration.Type $$3, int $$4, int $$5) {
            this.emeraldCost = $$0;
            this.destination = $$1;
            this.displayName = $$2;
            this.destinationType = $$3;
            this.maxUses = $$4;
            this.villagerXp = $$5;
        }

        @Override
        @Nullable
        public MerchantOffer getOffer(Entity $$0, RandomSource $$1) {
            if (!($$0.level instanceof ServerLevel)) {
                return null;
            }
            ServerLevel $$2 = (ServerLevel)$$0.level;
            BlockPos $$3 = $$2.findNearestMapStructure(this.destination, $$0.blockPosition(), 100, true);
            if ($$3 != null) {
                ItemStack $$4 = MapItem.create($$2, $$3.getX(), $$3.getZ(), (byte)2, true, true);
                MapItem.renderBiomePreviewMap($$2, $$4);
                MapItemSavedData.addTargetDecoration($$4, $$3, "+", this.destinationType);
                $$4.setHoverName(Component.translatable(this.displayName));
                return new MerchantOffer(new ItemStack(Items.EMERALD, this.emeraldCost), new ItemStack(Items.COMPASS), $$4, this.maxUses, this.villagerXp, 0.2f);
            }
            return null;
        }
    }

    static class DyedArmorForEmeralds
    implements ItemListing {
        private final Item item;
        private final int value;
        private final int maxUses;
        private final int villagerXp;

        public DyedArmorForEmeralds(Item $$0, int $$1) {
            this($$0, $$1, 12, 1);
        }

        public DyedArmorForEmeralds(Item $$0, int $$1, int $$2, int $$3) {
            this.item = $$0;
            this.value = $$1;
            this.maxUses = $$2;
            this.villagerXp = $$3;
        }

        @Override
        public MerchantOffer getOffer(Entity $$0, RandomSource $$1) {
            ItemStack $$2 = new ItemStack(Items.EMERALD, this.value);
            ItemStack $$3 = new ItemStack(this.item);
            if (this.item instanceof DyeableArmorItem) {
                ArrayList $$4 = Lists.newArrayList();
                $$4.add((Object)DyedArmorForEmeralds.getRandomDye($$1));
                if ($$1.nextFloat() > 0.7f) {
                    $$4.add((Object)DyedArmorForEmeralds.getRandomDye($$1));
                }
                if ($$1.nextFloat() > 0.8f) {
                    $$4.add((Object)DyedArmorForEmeralds.getRandomDye($$1));
                }
                $$3 = DyeableLeatherItem.dyeArmor($$3, (List<DyeItem>)$$4);
            }
            return new MerchantOffer($$2, $$3, this.maxUses, this.villagerXp, 0.2f);
        }

        private static DyeItem getRandomDye(RandomSource $$0) {
            return DyeItem.byColor(DyeColor.byId($$0.nextInt(16)));
        }
    }
}