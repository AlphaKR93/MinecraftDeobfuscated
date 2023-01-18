/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.client.color.item;

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.IdMapper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class ItemColors {
    private static final int DEFAULT = -1;
    private final IdMapper<ItemColor> itemColors = new IdMapper(32);

    public static ItemColors createDefault(BlockColors $$02) {
        ItemColors $$12 = new ItemColors();
        $$12.register(($$0, $$1) -> $$1 > 0 ? -1 : ((DyeableLeatherItem)((Object)$$0.getItem())).getColor($$0), Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS, Items.LEATHER_HORSE_ARMOR);
        $$12.register(($$0, $$1) -> GrassColor.get(0.5, 1.0), Blocks.TALL_GRASS, Blocks.LARGE_FERN);
        $$12.register(($$0, $$1) -> {
            int[] $$3;
            if ($$1 != 1) {
                return -1;
            }
            CompoundTag $$2 = $$0.getTagElement("Explosion");
            int[] nArray = $$3 = $$2 != null && $$2.contains("Colors", 11) ? $$2.getIntArray("Colors") : null;
            if ($$3 == null || $$3.length == 0) {
                return 0x8A8A8A;
            }
            if ($$3.length == 1) {
                return $$3[0];
            }
            int $$4 = 0;
            int $$5 = 0;
            int $$6 = 0;
            for (int $$7 : $$3) {
                $$4 += ($$7 & 0xFF0000) >> 16;
                $$5 += ($$7 & 0xFF00) >> 8;
                $$6 += ($$7 & 0xFF) >> 0;
            }
            return ($$4 /= $$3.length) << 16 | ($$5 /= $$3.length) << 8 | ($$6 /= $$3.length);
        }, Items.FIREWORK_STAR);
        $$12.register(($$0, $$1) -> $$1 > 0 ? -1 : PotionUtils.getColor($$0), Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION);
        for (SpawnEggItem $$22 : SpawnEggItem.eggs()) {
            $$12.register(($$1, $$2) -> $$22.getColor($$2), $$22);
        }
        $$12.register(($$1, $$2) -> {
            BlockState $$3 = ((BlockItem)$$1.getItem()).getBlock().defaultBlockState();
            return $$02.getColor($$3, null, null, $$2);
        }, Blocks.GRASS_BLOCK, Blocks.GRASS, Blocks.FERN, Blocks.VINE, Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES, Blocks.BIRCH_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.ACACIA_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.LILY_PAD);
        $$12.register(($$0, $$1) -> FoliageColor.getMangroveColor(), Blocks.MANGROVE_LEAVES);
        $$12.register(($$0, $$1) -> $$1 == 0 ? PotionUtils.getColor($$0) : -1, Items.TIPPED_ARROW);
        $$12.register(($$0, $$1) -> $$1 == 0 ? -1 : MapItem.getColor($$0), Items.FILLED_MAP);
        return $$12;
    }

    public int getColor(ItemStack $$0, int $$1) {
        ItemColor $$2 = this.itemColors.byId(BuiltInRegistries.ITEM.getId($$0.getItem()));
        return $$2 == null ? -1 : $$2.getColor($$0, $$1);
    }

    public void register(ItemColor $$0, ItemLike ... $$1) {
        for (ItemLike $$2 : $$1) {
            this.itemColors.addMapping($$0, Item.getId($$2.asItem()));
        }
    }
}