/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  java.lang.Object
 *  java.util.Map
 */
package net.minecraft.client.renderer;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class ItemBlockRenderTypes {
    private static final Map<Block, RenderType> TYPE_BY_BLOCK = (Map)Util.make(Maps.newHashMap(), $$0 -> {
        RenderType $$1 = RenderType.tripwire();
        $$0.put((Object)Blocks.TRIPWIRE, (Object)$$1);
        RenderType $$2 = RenderType.cutoutMipped();
        $$0.put((Object)Blocks.GRASS_BLOCK, (Object)$$2);
        $$0.put((Object)Blocks.IRON_BARS, (Object)$$2);
        $$0.put((Object)Blocks.GLASS_PANE, (Object)$$2);
        $$0.put((Object)Blocks.TRIPWIRE_HOOK, (Object)$$2);
        $$0.put((Object)Blocks.HOPPER, (Object)$$2);
        $$0.put((Object)Blocks.CHAIN, (Object)$$2);
        $$0.put((Object)Blocks.JUNGLE_LEAVES, (Object)$$2);
        $$0.put((Object)Blocks.OAK_LEAVES, (Object)$$2);
        $$0.put((Object)Blocks.SPRUCE_LEAVES, (Object)$$2);
        $$0.put((Object)Blocks.ACACIA_LEAVES, (Object)$$2);
        $$0.put((Object)Blocks.BIRCH_LEAVES, (Object)$$2);
        $$0.put((Object)Blocks.DARK_OAK_LEAVES, (Object)$$2);
        $$0.put((Object)Blocks.AZALEA_LEAVES, (Object)$$2);
        $$0.put((Object)Blocks.FLOWERING_AZALEA_LEAVES, (Object)$$2);
        $$0.put((Object)Blocks.MANGROVE_ROOTS, (Object)$$2);
        $$0.put((Object)Blocks.MANGROVE_LEAVES, (Object)$$2);
        RenderType $$3 = RenderType.cutout();
        $$0.put((Object)Blocks.OAK_SAPLING, (Object)$$3);
        $$0.put((Object)Blocks.SPRUCE_SAPLING, (Object)$$3);
        $$0.put((Object)Blocks.BIRCH_SAPLING, (Object)$$3);
        $$0.put((Object)Blocks.JUNGLE_SAPLING, (Object)$$3);
        $$0.put((Object)Blocks.ACACIA_SAPLING, (Object)$$3);
        $$0.put((Object)Blocks.DARK_OAK_SAPLING, (Object)$$3);
        $$0.put((Object)Blocks.GLASS, (Object)$$3);
        $$0.put((Object)Blocks.WHITE_BED, (Object)$$3);
        $$0.put((Object)Blocks.ORANGE_BED, (Object)$$3);
        $$0.put((Object)Blocks.MAGENTA_BED, (Object)$$3);
        $$0.put((Object)Blocks.LIGHT_BLUE_BED, (Object)$$3);
        $$0.put((Object)Blocks.YELLOW_BED, (Object)$$3);
        $$0.put((Object)Blocks.LIME_BED, (Object)$$3);
        $$0.put((Object)Blocks.PINK_BED, (Object)$$3);
        $$0.put((Object)Blocks.GRAY_BED, (Object)$$3);
        $$0.put((Object)Blocks.LIGHT_GRAY_BED, (Object)$$3);
        $$0.put((Object)Blocks.CYAN_BED, (Object)$$3);
        $$0.put((Object)Blocks.PURPLE_BED, (Object)$$3);
        $$0.put((Object)Blocks.BLUE_BED, (Object)$$3);
        $$0.put((Object)Blocks.BROWN_BED, (Object)$$3);
        $$0.put((Object)Blocks.GREEN_BED, (Object)$$3);
        $$0.put((Object)Blocks.RED_BED, (Object)$$3);
        $$0.put((Object)Blocks.BLACK_BED, (Object)$$3);
        $$0.put((Object)Blocks.POWERED_RAIL, (Object)$$3);
        $$0.put((Object)Blocks.DETECTOR_RAIL, (Object)$$3);
        $$0.put((Object)Blocks.COBWEB, (Object)$$3);
        $$0.put((Object)Blocks.GRASS, (Object)$$3);
        $$0.put((Object)Blocks.FERN, (Object)$$3);
        $$0.put((Object)Blocks.DEAD_BUSH, (Object)$$3);
        $$0.put((Object)Blocks.SEAGRASS, (Object)$$3);
        $$0.put((Object)Blocks.TALL_SEAGRASS, (Object)$$3);
        $$0.put((Object)Blocks.DANDELION, (Object)$$3);
        $$0.put((Object)Blocks.POPPY, (Object)$$3);
        $$0.put((Object)Blocks.BLUE_ORCHID, (Object)$$3);
        $$0.put((Object)Blocks.ALLIUM, (Object)$$3);
        $$0.put((Object)Blocks.AZURE_BLUET, (Object)$$3);
        $$0.put((Object)Blocks.RED_TULIP, (Object)$$3);
        $$0.put((Object)Blocks.ORANGE_TULIP, (Object)$$3);
        $$0.put((Object)Blocks.WHITE_TULIP, (Object)$$3);
        $$0.put((Object)Blocks.PINK_TULIP, (Object)$$3);
        $$0.put((Object)Blocks.OXEYE_DAISY, (Object)$$3);
        $$0.put((Object)Blocks.CORNFLOWER, (Object)$$3);
        $$0.put((Object)Blocks.WITHER_ROSE, (Object)$$3);
        $$0.put((Object)Blocks.LILY_OF_THE_VALLEY, (Object)$$3);
        $$0.put((Object)Blocks.BROWN_MUSHROOM, (Object)$$3);
        $$0.put((Object)Blocks.RED_MUSHROOM, (Object)$$3);
        $$0.put((Object)Blocks.TORCH, (Object)$$3);
        $$0.put((Object)Blocks.WALL_TORCH, (Object)$$3);
        $$0.put((Object)Blocks.SOUL_TORCH, (Object)$$3);
        $$0.put((Object)Blocks.SOUL_WALL_TORCH, (Object)$$3);
        $$0.put((Object)Blocks.FIRE, (Object)$$3);
        $$0.put((Object)Blocks.SOUL_FIRE, (Object)$$3);
        $$0.put((Object)Blocks.SPAWNER, (Object)$$3);
        $$0.put((Object)Blocks.REDSTONE_WIRE, (Object)$$3);
        $$0.put((Object)Blocks.WHEAT, (Object)$$3);
        $$0.put((Object)Blocks.OAK_DOOR, (Object)$$3);
        $$0.put((Object)Blocks.LADDER, (Object)$$3);
        $$0.put((Object)Blocks.RAIL, (Object)$$3);
        $$0.put((Object)Blocks.IRON_DOOR, (Object)$$3);
        $$0.put((Object)Blocks.REDSTONE_TORCH, (Object)$$3);
        $$0.put((Object)Blocks.REDSTONE_WALL_TORCH, (Object)$$3);
        $$0.put((Object)Blocks.CACTUS, (Object)$$3);
        $$0.put((Object)Blocks.SUGAR_CANE, (Object)$$3);
        $$0.put((Object)Blocks.REPEATER, (Object)$$3);
        $$0.put((Object)Blocks.OAK_TRAPDOOR, (Object)$$3);
        $$0.put((Object)Blocks.SPRUCE_TRAPDOOR, (Object)$$3);
        $$0.put((Object)Blocks.BIRCH_TRAPDOOR, (Object)$$3);
        $$0.put((Object)Blocks.JUNGLE_TRAPDOOR, (Object)$$3);
        $$0.put((Object)Blocks.ACACIA_TRAPDOOR, (Object)$$3);
        $$0.put((Object)Blocks.DARK_OAK_TRAPDOOR, (Object)$$3);
        $$0.put((Object)Blocks.CRIMSON_TRAPDOOR, (Object)$$3);
        $$0.put((Object)Blocks.WARPED_TRAPDOOR, (Object)$$3);
        $$0.put((Object)Blocks.MANGROVE_TRAPDOOR, (Object)$$3);
        $$0.put((Object)Blocks.BAMBOO_TRAPDOOR, (Object)$$3);
        $$0.put((Object)Blocks.ATTACHED_PUMPKIN_STEM, (Object)$$3);
        $$0.put((Object)Blocks.ATTACHED_MELON_STEM, (Object)$$3);
        $$0.put((Object)Blocks.PUMPKIN_STEM, (Object)$$3);
        $$0.put((Object)Blocks.MELON_STEM, (Object)$$3);
        $$0.put((Object)Blocks.VINE, (Object)$$3);
        $$0.put((Object)Blocks.GLOW_LICHEN, (Object)$$3);
        $$0.put((Object)Blocks.LILY_PAD, (Object)$$3);
        $$0.put((Object)Blocks.NETHER_WART, (Object)$$3);
        $$0.put((Object)Blocks.BREWING_STAND, (Object)$$3);
        $$0.put((Object)Blocks.COCOA, (Object)$$3);
        $$0.put((Object)Blocks.BEACON, (Object)$$3);
        $$0.put((Object)Blocks.FLOWER_POT, (Object)$$3);
        $$0.put((Object)Blocks.POTTED_OAK_SAPLING, (Object)$$3);
        $$0.put((Object)Blocks.POTTED_SPRUCE_SAPLING, (Object)$$3);
        $$0.put((Object)Blocks.POTTED_BIRCH_SAPLING, (Object)$$3);
        $$0.put((Object)Blocks.POTTED_JUNGLE_SAPLING, (Object)$$3);
        $$0.put((Object)Blocks.POTTED_ACACIA_SAPLING, (Object)$$3);
        $$0.put((Object)Blocks.POTTED_DARK_OAK_SAPLING, (Object)$$3);
        $$0.put((Object)Blocks.POTTED_MANGROVE_PROPAGULE, (Object)$$3);
        $$0.put((Object)Blocks.POTTED_FERN, (Object)$$3);
        $$0.put((Object)Blocks.POTTED_DANDELION, (Object)$$3);
        $$0.put((Object)Blocks.POTTED_POPPY, (Object)$$3);
        $$0.put((Object)Blocks.POTTED_BLUE_ORCHID, (Object)$$3);
        $$0.put((Object)Blocks.POTTED_ALLIUM, (Object)$$3);
        $$0.put((Object)Blocks.POTTED_AZURE_BLUET, (Object)$$3);
        $$0.put((Object)Blocks.POTTED_RED_TULIP, (Object)$$3);
        $$0.put((Object)Blocks.POTTED_ORANGE_TULIP, (Object)$$3);
        $$0.put((Object)Blocks.POTTED_WHITE_TULIP, (Object)$$3);
        $$0.put((Object)Blocks.POTTED_PINK_TULIP, (Object)$$3);
        $$0.put((Object)Blocks.POTTED_OXEYE_DAISY, (Object)$$3);
        $$0.put((Object)Blocks.POTTED_CORNFLOWER, (Object)$$3);
        $$0.put((Object)Blocks.POTTED_LILY_OF_THE_VALLEY, (Object)$$3);
        $$0.put((Object)Blocks.POTTED_WITHER_ROSE, (Object)$$3);
        $$0.put((Object)Blocks.POTTED_RED_MUSHROOM, (Object)$$3);
        $$0.put((Object)Blocks.POTTED_BROWN_MUSHROOM, (Object)$$3);
        $$0.put((Object)Blocks.POTTED_DEAD_BUSH, (Object)$$3);
        $$0.put((Object)Blocks.POTTED_CACTUS, (Object)$$3);
        $$0.put((Object)Blocks.POTTED_AZALEA, (Object)$$3);
        $$0.put((Object)Blocks.POTTED_FLOWERING_AZALEA, (Object)$$3);
        $$0.put((Object)Blocks.CARROTS, (Object)$$3);
        $$0.put((Object)Blocks.POTATOES, (Object)$$3);
        $$0.put((Object)Blocks.COMPARATOR, (Object)$$3);
        $$0.put((Object)Blocks.ACTIVATOR_RAIL, (Object)$$3);
        $$0.put((Object)Blocks.IRON_TRAPDOOR, (Object)$$3);
        $$0.put((Object)Blocks.SUNFLOWER, (Object)$$3);
        $$0.put((Object)Blocks.LILAC, (Object)$$3);
        $$0.put((Object)Blocks.ROSE_BUSH, (Object)$$3);
        $$0.put((Object)Blocks.PEONY, (Object)$$3);
        $$0.put((Object)Blocks.TALL_GRASS, (Object)$$3);
        $$0.put((Object)Blocks.LARGE_FERN, (Object)$$3);
        $$0.put((Object)Blocks.SPRUCE_DOOR, (Object)$$3);
        $$0.put((Object)Blocks.BIRCH_DOOR, (Object)$$3);
        $$0.put((Object)Blocks.JUNGLE_DOOR, (Object)$$3);
        $$0.put((Object)Blocks.ACACIA_DOOR, (Object)$$3);
        $$0.put((Object)Blocks.DARK_OAK_DOOR, (Object)$$3);
        $$0.put((Object)Blocks.MANGROVE_DOOR, (Object)$$3);
        $$0.put((Object)Blocks.BAMBOO_DOOR, (Object)$$3);
        $$0.put((Object)Blocks.END_ROD, (Object)$$3);
        $$0.put((Object)Blocks.CHORUS_PLANT, (Object)$$3);
        $$0.put((Object)Blocks.CHORUS_FLOWER, (Object)$$3);
        $$0.put((Object)Blocks.BEETROOTS, (Object)$$3);
        $$0.put((Object)Blocks.KELP, (Object)$$3);
        $$0.put((Object)Blocks.KELP_PLANT, (Object)$$3);
        $$0.put((Object)Blocks.TURTLE_EGG, (Object)$$3);
        $$0.put((Object)Blocks.DEAD_TUBE_CORAL, (Object)$$3);
        $$0.put((Object)Blocks.DEAD_BRAIN_CORAL, (Object)$$3);
        $$0.put((Object)Blocks.DEAD_BUBBLE_CORAL, (Object)$$3);
        $$0.put((Object)Blocks.DEAD_FIRE_CORAL, (Object)$$3);
        $$0.put((Object)Blocks.DEAD_HORN_CORAL, (Object)$$3);
        $$0.put((Object)Blocks.TUBE_CORAL, (Object)$$3);
        $$0.put((Object)Blocks.BRAIN_CORAL, (Object)$$3);
        $$0.put((Object)Blocks.BUBBLE_CORAL, (Object)$$3);
        $$0.put((Object)Blocks.FIRE_CORAL, (Object)$$3);
        $$0.put((Object)Blocks.HORN_CORAL, (Object)$$3);
        $$0.put((Object)Blocks.DEAD_TUBE_CORAL_FAN, (Object)$$3);
        $$0.put((Object)Blocks.DEAD_BRAIN_CORAL_FAN, (Object)$$3);
        $$0.put((Object)Blocks.DEAD_BUBBLE_CORAL_FAN, (Object)$$3);
        $$0.put((Object)Blocks.DEAD_FIRE_CORAL_FAN, (Object)$$3);
        $$0.put((Object)Blocks.DEAD_HORN_CORAL_FAN, (Object)$$3);
        $$0.put((Object)Blocks.TUBE_CORAL_FAN, (Object)$$3);
        $$0.put((Object)Blocks.BRAIN_CORAL_FAN, (Object)$$3);
        $$0.put((Object)Blocks.BUBBLE_CORAL_FAN, (Object)$$3);
        $$0.put((Object)Blocks.FIRE_CORAL_FAN, (Object)$$3);
        $$0.put((Object)Blocks.HORN_CORAL_FAN, (Object)$$3);
        $$0.put((Object)Blocks.DEAD_TUBE_CORAL_WALL_FAN, (Object)$$3);
        $$0.put((Object)Blocks.DEAD_BRAIN_CORAL_WALL_FAN, (Object)$$3);
        $$0.put((Object)Blocks.DEAD_BUBBLE_CORAL_WALL_FAN, (Object)$$3);
        $$0.put((Object)Blocks.DEAD_FIRE_CORAL_WALL_FAN, (Object)$$3);
        $$0.put((Object)Blocks.DEAD_HORN_CORAL_WALL_FAN, (Object)$$3);
        $$0.put((Object)Blocks.TUBE_CORAL_WALL_FAN, (Object)$$3);
        $$0.put((Object)Blocks.BRAIN_CORAL_WALL_FAN, (Object)$$3);
        $$0.put((Object)Blocks.BUBBLE_CORAL_WALL_FAN, (Object)$$3);
        $$0.put((Object)Blocks.FIRE_CORAL_WALL_FAN, (Object)$$3);
        $$0.put((Object)Blocks.HORN_CORAL_WALL_FAN, (Object)$$3);
        $$0.put((Object)Blocks.SEA_PICKLE, (Object)$$3);
        $$0.put((Object)Blocks.CONDUIT, (Object)$$3);
        $$0.put((Object)Blocks.BAMBOO_SAPLING, (Object)$$3);
        $$0.put((Object)Blocks.BAMBOO, (Object)$$3);
        $$0.put((Object)Blocks.POTTED_BAMBOO, (Object)$$3);
        $$0.put((Object)Blocks.SCAFFOLDING, (Object)$$3);
        $$0.put((Object)Blocks.STONECUTTER, (Object)$$3);
        $$0.put((Object)Blocks.LANTERN, (Object)$$3);
        $$0.put((Object)Blocks.SOUL_LANTERN, (Object)$$3);
        $$0.put((Object)Blocks.CAMPFIRE, (Object)$$3);
        $$0.put((Object)Blocks.SOUL_CAMPFIRE, (Object)$$3);
        $$0.put((Object)Blocks.SWEET_BERRY_BUSH, (Object)$$3);
        $$0.put((Object)Blocks.WEEPING_VINES, (Object)$$3);
        $$0.put((Object)Blocks.WEEPING_VINES_PLANT, (Object)$$3);
        $$0.put((Object)Blocks.TWISTING_VINES, (Object)$$3);
        $$0.put((Object)Blocks.TWISTING_VINES_PLANT, (Object)$$3);
        $$0.put((Object)Blocks.NETHER_SPROUTS, (Object)$$3);
        $$0.put((Object)Blocks.CRIMSON_FUNGUS, (Object)$$3);
        $$0.put((Object)Blocks.WARPED_FUNGUS, (Object)$$3);
        $$0.put((Object)Blocks.CRIMSON_ROOTS, (Object)$$3);
        $$0.put((Object)Blocks.WARPED_ROOTS, (Object)$$3);
        $$0.put((Object)Blocks.POTTED_CRIMSON_FUNGUS, (Object)$$3);
        $$0.put((Object)Blocks.POTTED_WARPED_FUNGUS, (Object)$$3);
        $$0.put((Object)Blocks.POTTED_CRIMSON_ROOTS, (Object)$$3);
        $$0.put((Object)Blocks.POTTED_WARPED_ROOTS, (Object)$$3);
        $$0.put((Object)Blocks.CRIMSON_DOOR, (Object)$$3);
        $$0.put((Object)Blocks.WARPED_DOOR, (Object)$$3);
        $$0.put((Object)Blocks.POINTED_DRIPSTONE, (Object)$$3);
        $$0.put((Object)Blocks.SMALL_AMETHYST_BUD, (Object)$$3);
        $$0.put((Object)Blocks.MEDIUM_AMETHYST_BUD, (Object)$$3);
        $$0.put((Object)Blocks.LARGE_AMETHYST_BUD, (Object)$$3);
        $$0.put((Object)Blocks.AMETHYST_CLUSTER, (Object)$$3);
        $$0.put((Object)Blocks.LIGHTNING_ROD, (Object)$$3);
        $$0.put((Object)Blocks.CAVE_VINES, (Object)$$3);
        $$0.put((Object)Blocks.CAVE_VINES_PLANT, (Object)$$3);
        $$0.put((Object)Blocks.SPORE_BLOSSOM, (Object)$$3);
        $$0.put((Object)Blocks.FLOWERING_AZALEA, (Object)$$3);
        $$0.put((Object)Blocks.AZALEA, (Object)$$3);
        $$0.put((Object)Blocks.MOSS_CARPET, (Object)$$3);
        $$0.put((Object)Blocks.BIG_DRIPLEAF, (Object)$$3);
        $$0.put((Object)Blocks.BIG_DRIPLEAF_STEM, (Object)$$3);
        $$0.put((Object)Blocks.SMALL_DRIPLEAF, (Object)$$3);
        $$0.put((Object)Blocks.HANGING_ROOTS, (Object)$$3);
        $$0.put((Object)Blocks.SCULK_SENSOR, (Object)$$3);
        $$0.put((Object)Blocks.SCULK_VEIN, (Object)$$3);
        $$0.put((Object)Blocks.SCULK_SHRIEKER, (Object)$$3);
        $$0.put((Object)Blocks.MANGROVE_PROPAGULE, (Object)$$3);
        $$0.put((Object)Blocks.MANGROVE_LOG, (Object)$$3);
        $$0.put((Object)Blocks.FROGSPAWN, (Object)$$3);
        RenderType $$4 = RenderType.translucent();
        $$0.put((Object)Blocks.ICE, (Object)$$4);
        $$0.put((Object)Blocks.NETHER_PORTAL, (Object)$$4);
        $$0.put((Object)Blocks.WHITE_STAINED_GLASS, (Object)$$4);
        $$0.put((Object)Blocks.ORANGE_STAINED_GLASS, (Object)$$4);
        $$0.put((Object)Blocks.MAGENTA_STAINED_GLASS, (Object)$$4);
        $$0.put((Object)Blocks.LIGHT_BLUE_STAINED_GLASS, (Object)$$4);
        $$0.put((Object)Blocks.YELLOW_STAINED_GLASS, (Object)$$4);
        $$0.put((Object)Blocks.LIME_STAINED_GLASS, (Object)$$4);
        $$0.put((Object)Blocks.PINK_STAINED_GLASS, (Object)$$4);
        $$0.put((Object)Blocks.GRAY_STAINED_GLASS, (Object)$$4);
        $$0.put((Object)Blocks.LIGHT_GRAY_STAINED_GLASS, (Object)$$4);
        $$0.put((Object)Blocks.CYAN_STAINED_GLASS, (Object)$$4);
        $$0.put((Object)Blocks.PURPLE_STAINED_GLASS, (Object)$$4);
        $$0.put((Object)Blocks.BLUE_STAINED_GLASS, (Object)$$4);
        $$0.put((Object)Blocks.BROWN_STAINED_GLASS, (Object)$$4);
        $$0.put((Object)Blocks.GREEN_STAINED_GLASS, (Object)$$4);
        $$0.put((Object)Blocks.RED_STAINED_GLASS, (Object)$$4);
        $$0.put((Object)Blocks.BLACK_STAINED_GLASS, (Object)$$4);
        $$0.put((Object)Blocks.WHITE_STAINED_GLASS_PANE, (Object)$$4);
        $$0.put((Object)Blocks.ORANGE_STAINED_GLASS_PANE, (Object)$$4);
        $$0.put((Object)Blocks.MAGENTA_STAINED_GLASS_PANE, (Object)$$4);
        $$0.put((Object)Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, (Object)$$4);
        $$0.put((Object)Blocks.YELLOW_STAINED_GLASS_PANE, (Object)$$4);
        $$0.put((Object)Blocks.LIME_STAINED_GLASS_PANE, (Object)$$4);
        $$0.put((Object)Blocks.PINK_STAINED_GLASS_PANE, (Object)$$4);
        $$0.put((Object)Blocks.GRAY_STAINED_GLASS_PANE, (Object)$$4);
        $$0.put((Object)Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, (Object)$$4);
        $$0.put((Object)Blocks.CYAN_STAINED_GLASS_PANE, (Object)$$4);
        $$0.put((Object)Blocks.PURPLE_STAINED_GLASS_PANE, (Object)$$4);
        $$0.put((Object)Blocks.BLUE_STAINED_GLASS_PANE, (Object)$$4);
        $$0.put((Object)Blocks.BROWN_STAINED_GLASS_PANE, (Object)$$4);
        $$0.put((Object)Blocks.GREEN_STAINED_GLASS_PANE, (Object)$$4);
        $$0.put((Object)Blocks.RED_STAINED_GLASS_PANE, (Object)$$4);
        $$0.put((Object)Blocks.BLACK_STAINED_GLASS_PANE, (Object)$$4);
        $$0.put((Object)Blocks.SLIME_BLOCK, (Object)$$4);
        $$0.put((Object)Blocks.HONEY_BLOCK, (Object)$$4);
        $$0.put((Object)Blocks.FROSTED_ICE, (Object)$$4);
        $$0.put((Object)Blocks.BUBBLE_COLUMN, (Object)$$4);
        $$0.put((Object)Blocks.TINTED_GLASS, (Object)$$4);
    });
    private static final Map<Fluid, RenderType> TYPE_BY_FLUID = (Map)Util.make(Maps.newHashMap(), $$0 -> {
        RenderType $$1 = RenderType.translucent();
        $$0.put((Object)Fluids.FLOWING_WATER, (Object)$$1);
        $$0.put((Object)Fluids.WATER, (Object)$$1);
    });
    private static boolean renderCutout;

    public static RenderType getChunkRenderType(BlockState $$0) {
        Block $$1 = $$0.getBlock();
        if ($$1 instanceof LeavesBlock) {
            return renderCutout ? RenderType.cutoutMipped() : RenderType.solid();
        }
        RenderType $$2 = (RenderType)TYPE_BY_BLOCK.get((Object)$$1);
        if ($$2 != null) {
            return $$2;
        }
        return RenderType.solid();
    }

    public static RenderType getMovingBlockRenderType(BlockState $$0) {
        Block $$1 = $$0.getBlock();
        if ($$1 instanceof LeavesBlock) {
            return renderCutout ? RenderType.cutoutMipped() : RenderType.solid();
        }
        RenderType $$2 = (RenderType)TYPE_BY_BLOCK.get((Object)$$1);
        if ($$2 != null) {
            if ($$2 == RenderType.translucent()) {
                return RenderType.translucentMovingBlock();
            }
            return $$2;
        }
        return RenderType.solid();
    }

    public static RenderType getRenderType(BlockState $$0, boolean $$1) {
        RenderType $$2 = ItemBlockRenderTypes.getChunkRenderType($$0);
        if ($$2 == RenderType.translucent()) {
            if (!Minecraft.useShaderTransparency()) {
                return Sheets.translucentCullBlockSheet();
            }
            return $$1 ? Sheets.translucentCullBlockSheet() : Sheets.translucentItemSheet();
        }
        return Sheets.cutoutBlockSheet();
    }

    public static RenderType getRenderType(ItemStack $$0, boolean $$1) {
        Item $$2 = $$0.getItem();
        if ($$2 instanceof BlockItem) {
            Block $$3 = ((BlockItem)$$2).getBlock();
            return ItemBlockRenderTypes.getRenderType($$3.defaultBlockState(), $$1);
        }
        return $$1 ? Sheets.translucentCullBlockSheet() : Sheets.translucentItemSheet();
    }

    public static RenderType getRenderLayer(FluidState $$0) {
        RenderType $$1 = (RenderType)TYPE_BY_FLUID.get((Object)$$0.getType());
        if ($$1 != null) {
            return $$1;
        }
        return RenderType.solid();
    }

    public static void setFancy(boolean $$0) {
        renderCutout = $$0;
    }
}