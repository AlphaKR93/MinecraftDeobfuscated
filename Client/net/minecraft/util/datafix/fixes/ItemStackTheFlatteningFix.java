/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Dynamic
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Map
 *  java.util.Optional
 *  java.util.Set
 *  java.util.stream.Collectors
 *  javax.annotation.Nullable
 */
package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class ItemStackTheFlatteningFix
extends DataFix {
    private static final Map<String, String> MAP = (Map)DataFixUtils.make((Object)Maps.newHashMap(), $$0 -> {
        $$0.put((Object)"minecraft:stone.0", (Object)"minecraft:stone");
        $$0.put((Object)"minecraft:stone.1", (Object)"minecraft:granite");
        $$0.put((Object)"minecraft:stone.2", (Object)"minecraft:polished_granite");
        $$0.put((Object)"minecraft:stone.3", (Object)"minecraft:diorite");
        $$0.put((Object)"minecraft:stone.4", (Object)"minecraft:polished_diorite");
        $$0.put((Object)"minecraft:stone.5", (Object)"minecraft:andesite");
        $$0.put((Object)"minecraft:stone.6", (Object)"minecraft:polished_andesite");
        $$0.put((Object)"minecraft:dirt.0", (Object)"minecraft:dirt");
        $$0.put((Object)"minecraft:dirt.1", (Object)"minecraft:coarse_dirt");
        $$0.put((Object)"minecraft:dirt.2", (Object)"minecraft:podzol");
        $$0.put((Object)"minecraft:leaves.0", (Object)"minecraft:oak_leaves");
        $$0.put((Object)"minecraft:leaves.1", (Object)"minecraft:spruce_leaves");
        $$0.put((Object)"minecraft:leaves.2", (Object)"minecraft:birch_leaves");
        $$0.put((Object)"minecraft:leaves.3", (Object)"minecraft:jungle_leaves");
        $$0.put((Object)"minecraft:leaves2.0", (Object)"minecraft:acacia_leaves");
        $$0.put((Object)"minecraft:leaves2.1", (Object)"minecraft:dark_oak_leaves");
        $$0.put((Object)"minecraft:log.0", (Object)"minecraft:oak_log");
        $$0.put((Object)"minecraft:log.1", (Object)"minecraft:spruce_log");
        $$0.put((Object)"minecraft:log.2", (Object)"minecraft:birch_log");
        $$0.put((Object)"minecraft:log.3", (Object)"minecraft:jungle_log");
        $$0.put((Object)"minecraft:log2.0", (Object)"minecraft:acacia_log");
        $$0.put((Object)"minecraft:log2.1", (Object)"minecraft:dark_oak_log");
        $$0.put((Object)"minecraft:sapling.0", (Object)"minecraft:oak_sapling");
        $$0.put((Object)"minecraft:sapling.1", (Object)"minecraft:spruce_sapling");
        $$0.put((Object)"minecraft:sapling.2", (Object)"minecraft:birch_sapling");
        $$0.put((Object)"minecraft:sapling.3", (Object)"minecraft:jungle_sapling");
        $$0.put((Object)"minecraft:sapling.4", (Object)"minecraft:acacia_sapling");
        $$0.put((Object)"minecraft:sapling.5", (Object)"minecraft:dark_oak_sapling");
        $$0.put((Object)"minecraft:planks.0", (Object)"minecraft:oak_planks");
        $$0.put((Object)"minecraft:planks.1", (Object)"minecraft:spruce_planks");
        $$0.put((Object)"minecraft:planks.2", (Object)"minecraft:birch_planks");
        $$0.put((Object)"minecraft:planks.3", (Object)"minecraft:jungle_planks");
        $$0.put((Object)"minecraft:planks.4", (Object)"minecraft:acacia_planks");
        $$0.put((Object)"minecraft:planks.5", (Object)"minecraft:dark_oak_planks");
        $$0.put((Object)"minecraft:sand.0", (Object)"minecraft:sand");
        $$0.put((Object)"minecraft:sand.1", (Object)"minecraft:red_sand");
        $$0.put((Object)"minecraft:quartz_block.0", (Object)"minecraft:quartz_block");
        $$0.put((Object)"minecraft:quartz_block.1", (Object)"minecraft:chiseled_quartz_block");
        $$0.put((Object)"minecraft:quartz_block.2", (Object)"minecraft:quartz_pillar");
        $$0.put((Object)"minecraft:anvil.0", (Object)"minecraft:anvil");
        $$0.put((Object)"minecraft:anvil.1", (Object)"minecraft:chipped_anvil");
        $$0.put((Object)"minecraft:anvil.2", (Object)"minecraft:damaged_anvil");
        $$0.put((Object)"minecraft:wool.0", (Object)"minecraft:white_wool");
        $$0.put((Object)"minecraft:wool.1", (Object)"minecraft:orange_wool");
        $$0.put((Object)"minecraft:wool.2", (Object)"minecraft:magenta_wool");
        $$0.put((Object)"minecraft:wool.3", (Object)"minecraft:light_blue_wool");
        $$0.put((Object)"minecraft:wool.4", (Object)"minecraft:yellow_wool");
        $$0.put((Object)"minecraft:wool.5", (Object)"minecraft:lime_wool");
        $$0.put((Object)"minecraft:wool.6", (Object)"minecraft:pink_wool");
        $$0.put((Object)"minecraft:wool.7", (Object)"minecraft:gray_wool");
        $$0.put((Object)"minecraft:wool.8", (Object)"minecraft:light_gray_wool");
        $$0.put((Object)"minecraft:wool.9", (Object)"minecraft:cyan_wool");
        $$0.put((Object)"minecraft:wool.10", (Object)"minecraft:purple_wool");
        $$0.put((Object)"minecraft:wool.11", (Object)"minecraft:blue_wool");
        $$0.put((Object)"minecraft:wool.12", (Object)"minecraft:brown_wool");
        $$0.put((Object)"minecraft:wool.13", (Object)"minecraft:green_wool");
        $$0.put((Object)"minecraft:wool.14", (Object)"minecraft:red_wool");
        $$0.put((Object)"minecraft:wool.15", (Object)"minecraft:black_wool");
        $$0.put((Object)"minecraft:carpet.0", (Object)"minecraft:white_carpet");
        $$0.put((Object)"minecraft:carpet.1", (Object)"minecraft:orange_carpet");
        $$0.put((Object)"minecraft:carpet.2", (Object)"minecraft:magenta_carpet");
        $$0.put((Object)"minecraft:carpet.3", (Object)"minecraft:light_blue_carpet");
        $$0.put((Object)"minecraft:carpet.4", (Object)"minecraft:yellow_carpet");
        $$0.put((Object)"minecraft:carpet.5", (Object)"minecraft:lime_carpet");
        $$0.put((Object)"minecraft:carpet.6", (Object)"minecraft:pink_carpet");
        $$0.put((Object)"minecraft:carpet.7", (Object)"minecraft:gray_carpet");
        $$0.put((Object)"minecraft:carpet.8", (Object)"minecraft:light_gray_carpet");
        $$0.put((Object)"minecraft:carpet.9", (Object)"minecraft:cyan_carpet");
        $$0.put((Object)"minecraft:carpet.10", (Object)"minecraft:purple_carpet");
        $$0.put((Object)"minecraft:carpet.11", (Object)"minecraft:blue_carpet");
        $$0.put((Object)"minecraft:carpet.12", (Object)"minecraft:brown_carpet");
        $$0.put((Object)"minecraft:carpet.13", (Object)"minecraft:green_carpet");
        $$0.put((Object)"minecraft:carpet.14", (Object)"minecraft:red_carpet");
        $$0.put((Object)"minecraft:carpet.15", (Object)"minecraft:black_carpet");
        $$0.put((Object)"minecraft:hardened_clay.0", (Object)"minecraft:terracotta");
        $$0.put((Object)"minecraft:stained_hardened_clay.0", (Object)"minecraft:white_terracotta");
        $$0.put((Object)"minecraft:stained_hardened_clay.1", (Object)"minecraft:orange_terracotta");
        $$0.put((Object)"minecraft:stained_hardened_clay.2", (Object)"minecraft:magenta_terracotta");
        $$0.put((Object)"minecraft:stained_hardened_clay.3", (Object)"minecraft:light_blue_terracotta");
        $$0.put((Object)"minecraft:stained_hardened_clay.4", (Object)"minecraft:yellow_terracotta");
        $$0.put((Object)"minecraft:stained_hardened_clay.5", (Object)"minecraft:lime_terracotta");
        $$0.put((Object)"minecraft:stained_hardened_clay.6", (Object)"minecraft:pink_terracotta");
        $$0.put((Object)"minecraft:stained_hardened_clay.7", (Object)"minecraft:gray_terracotta");
        $$0.put((Object)"minecraft:stained_hardened_clay.8", (Object)"minecraft:light_gray_terracotta");
        $$0.put((Object)"minecraft:stained_hardened_clay.9", (Object)"minecraft:cyan_terracotta");
        $$0.put((Object)"minecraft:stained_hardened_clay.10", (Object)"minecraft:purple_terracotta");
        $$0.put((Object)"minecraft:stained_hardened_clay.11", (Object)"minecraft:blue_terracotta");
        $$0.put((Object)"minecraft:stained_hardened_clay.12", (Object)"minecraft:brown_terracotta");
        $$0.put((Object)"minecraft:stained_hardened_clay.13", (Object)"minecraft:green_terracotta");
        $$0.put((Object)"minecraft:stained_hardened_clay.14", (Object)"minecraft:red_terracotta");
        $$0.put((Object)"minecraft:stained_hardened_clay.15", (Object)"minecraft:black_terracotta");
        $$0.put((Object)"minecraft:silver_glazed_terracotta.0", (Object)"minecraft:light_gray_glazed_terracotta");
        $$0.put((Object)"minecraft:stained_glass.0", (Object)"minecraft:white_stained_glass");
        $$0.put((Object)"minecraft:stained_glass.1", (Object)"minecraft:orange_stained_glass");
        $$0.put((Object)"minecraft:stained_glass.2", (Object)"minecraft:magenta_stained_glass");
        $$0.put((Object)"minecraft:stained_glass.3", (Object)"minecraft:light_blue_stained_glass");
        $$0.put((Object)"minecraft:stained_glass.4", (Object)"minecraft:yellow_stained_glass");
        $$0.put((Object)"minecraft:stained_glass.5", (Object)"minecraft:lime_stained_glass");
        $$0.put((Object)"minecraft:stained_glass.6", (Object)"minecraft:pink_stained_glass");
        $$0.put((Object)"minecraft:stained_glass.7", (Object)"minecraft:gray_stained_glass");
        $$0.put((Object)"minecraft:stained_glass.8", (Object)"minecraft:light_gray_stained_glass");
        $$0.put((Object)"minecraft:stained_glass.9", (Object)"minecraft:cyan_stained_glass");
        $$0.put((Object)"minecraft:stained_glass.10", (Object)"minecraft:purple_stained_glass");
        $$0.put((Object)"minecraft:stained_glass.11", (Object)"minecraft:blue_stained_glass");
        $$0.put((Object)"minecraft:stained_glass.12", (Object)"minecraft:brown_stained_glass");
        $$0.put((Object)"minecraft:stained_glass.13", (Object)"minecraft:green_stained_glass");
        $$0.put((Object)"minecraft:stained_glass.14", (Object)"minecraft:red_stained_glass");
        $$0.put((Object)"minecraft:stained_glass.15", (Object)"minecraft:black_stained_glass");
        $$0.put((Object)"minecraft:stained_glass_pane.0", (Object)"minecraft:white_stained_glass_pane");
        $$0.put((Object)"minecraft:stained_glass_pane.1", (Object)"minecraft:orange_stained_glass_pane");
        $$0.put((Object)"minecraft:stained_glass_pane.2", (Object)"minecraft:magenta_stained_glass_pane");
        $$0.put((Object)"minecraft:stained_glass_pane.3", (Object)"minecraft:light_blue_stained_glass_pane");
        $$0.put((Object)"minecraft:stained_glass_pane.4", (Object)"minecraft:yellow_stained_glass_pane");
        $$0.put((Object)"minecraft:stained_glass_pane.5", (Object)"minecraft:lime_stained_glass_pane");
        $$0.put((Object)"minecraft:stained_glass_pane.6", (Object)"minecraft:pink_stained_glass_pane");
        $$0.put((Object)"minecraft:stained_glass_pane.7", (Object)"minecraft:gray_stained_glass_pane");
        $$0.put((Object)"minecraft:stained_glass_pane.8", (Object)"minecraft:light_gray_stained_glass_pane");
        $$0.put((Object)"minecraft:stained_glass_pane.9", (Object)"minecraft:cyan_stained_glass_pane");
        $$0.put((Object)"minecraft:stained_glass_pane.10", (Object)"minecraft:purple_stained_glass_pane");
        $$0.put((Object)"minecraft:stained_glass_pane.11", (Object)"minecraft:blue_stained_glass_pane");
        $$0.put((Object)"minecraft:stained_glass_pane.12", (Object)"minecraft:brown_stained_glass_pane");
        $$0.put((Object)"minecraft:stained_glass_pane.13", (Object)"minecraft:green_stained_glass_pane");
        $$0.put((Object)"minecraft:stained_glass_pane.14", (Object)"minecraft:red_stained_glass_pane");
        $$0.put((Object)"minecraft:stained_glass_pane.15", (Object)"minecraft:black_stained_glass_pane");
        $$0.put((Object)"minecraft:prismarine.0", (Object)"minecraft:prismarine");
        $$0.put((Object)"minecraft:prismarine.1", (Object)"minecraft:prismarine_bricks");
        $$0.put((Object)"minecraft:prismarine.2", (Object)"minecraft:dark_prismarine");
        $$0.put((Object)"minecraft:concrete.0", (Object)"minecraft:white_concrete");
        $$0.put((Object)"minecraft:concrete.1", (Object)"minecraft:orange_concrete");
        $$0.put((Object)"minecraft:concrete.2", (Object)"minecraft:magenta_concrete");
        $$0.put((Object)"minecraft:concrete.3", (Object)"minecraft:light_blue_concrete");
        $$0.put((Object)"minecraft:concrete.4", (Object)"minecraft:yellow_concrete");
        $$0.put((Object)"minecraft:concrete.5", (Object)"minecraft:lime_concrete");
        $$0.put((Object)"minecraft:concrete.6", (Object)"minecraft:pink_concrete");
        $$0.put((Object)"minecraft:concrete.7", (Object)"minecraft:gray_concrete");
        $$0.put((Object)"minecraft:concrete.8", (Object)"minecraft:light_gray_concrete");
        $$0.put((Object)"minecraft:concrete.9", (Object)"minecraft:cyan_concrete");
        $$0.put((Object)"minecraft:concrete.10", (Object)"minecraft:purple_concrete");
        $$0.put((Object)"minecraft:concrete.11", (Object)"minecraft:blue_concrete");
        $$0.put((Object)"minecraft:concrete.12", (Object)"minecraft:brown_concrete");
        $$0.put((Object)"minecraft:concrete.13", (Object)"minecraft:green_concrete");
        $$0.put((Object)"minecraft:concrete.14", (Object)"minecraft:red_concrete");
        $$0.put((Object)"minecraft:concrete.15", (Object)"minecraft:black_concrete");
        $$0.put((Object)"minecraft:concrete_powder.0", (Object)"minecraft:white_concrete_powder");
        $$0.put((Object)"minecraft:concrete_powder.1", (Object)"minecraft:orange_concrete_powder");
        $$0.put((Object)"minecraft:concrete_powder.2", (Object)"minecraft:magenta_concrete_powder");
        $$0.put((Object)"minecraft:concrete_powder.3", (Object)"minecraft:light_blue_concrete_powder");
        $$0.put((Object)"minecraft:concrete_powder.4", (Object)"minecraft:yellow_concrete_powder");
        $$0.put((Object)"minecraft:concrete_powder.5", (Object)"minecraft:lime_concrete_powder");
        $$0.put((Object)"minecraft:concrete_powder.6", (Object)"minecraft:pink_concrete_powder");
        $$0.put((Object)"minecraft:concrete_powder.7", (Object)"minecraft:gray_concrete_powder");
        $$0.put((Object)"minecraft:concrete_powder.8", (Object)"minecraft:light_gray_concrete_powder");
        $$0.put((Object)"minecraft:concrete_powder.9", (Object)"minecraft:cyan_concrete_powder");
        $$0.put((Object)"minecraft:concrete_powder.10", (Object)"minecraft:purple_concrete_powder");
        $$0.put((Object)"minecraft:concrete_powder.11", (Object)"minecraft:blue_concrete_powder");
        $$0.put((Object)"minecraft:concrete_powder.12", (Object)"minecraft:brown_concrete_powder");
        $$0.put((Object)"minecraft:concrete_powder.13", (Object)"minecraft:green_concrete_powder");
        $$0.put((Object)"minecraft:concrete_powder.14", (Object)"minecraft:red_concrete_powder");
        $$0.put((Object)"minecraft:concrete_powder.15", (Object)"minecraft:black_concrete_powder");
        $$0.put((Object)"minecraft:cobblestone_wall.0", (Object)"minecraft:cobblestone_wall");
        $$0.put((Object)"minecraft:cobblestone_wall.1", (Object)"minecraft:mossy_cobblestone_wall");
        $$0.put((Object)"minecraft:sandstone.0", (Object)"minecraft:sandstone");
        $$0.put((Object)"minecraft:sandstone.1", (Object)"minecraft:chiseled_sandstone");
        $$0.put((Object)"minecraft:sandstone.2", (Object)"minecraft:cut_sandstone");
        $$0.put((Object)"minecraft:red_sandstone.0", (Object)"minecraft:red_sandstone");
        $$0.put((Object)"minecraft:red_sandstone.1", (Object)"minecraft:chiseled_red_sandstone");
        $$0.put((Object)"minecraft:red_sandstone.2", (Object)"minecraft:cut_red_sandstone");
        $$0.put((Object)"minecraft:stonebrick.0", (Object)"minecraft:stone_bricks");
        $$0.put((Object)"minecraft:stonebrick.1", (Object)"minecraft:mossy_stone_bricks");
        $$0.put((Object)"minecraft:stonebrick.2", (Object)"minecraft:cracked_stone_bricks");
        $$0.put((Object)"minecraft:stonebrick.3", (Object)"minecraft:chiseled_stone_bricks");
        $$0.put((Object)"minecraft:monster_egg.0", (Object)"minecraft:infested_stone");
        $$0.put((Object)"minecraft:monster_egg.1", (Object)"minecraft:infested_cobblestone");
        $$0.put((Object)"minecraft:monster_egg.2", (Object)"minecraft:infested_stone_bricks");
        $$0.put((Object)"minecraft:monster_egg.3", (Object)"minecraft:infested_mossy_stone_bricks");
        $$0.put((Object)"minecraft:monster_egg.4", (Object)"minecraft:infested_cracked_stone_bricks");
        $$0.put((Object)"minecraft:monster_egg.5", (Object)"minecraft:infested_chiseled_stone_bricks");
        $$0.put((Object)"minecraft:yellow_flower.0", (Object)"minecraft:dandelion");
        $$0.put((Object)"minecraft:red_flower.0", (Object)"minecraft:poppy");
        $$0.put((Object)"minecraft:red_flower.1", (Object)"minecraft:blue_orchid");
        $$0.put((Object)"minecraft:red_flower.2", (Object)"minecraft:allium");
        $$0.put((Object)"minecraft:red_flower.3", (Object)"minecraft:azure_bluet");
        $$0.put((Object)"minecraft:red_flower.4", (Object)"minecraft:red_tulip");
        $$0.put((Object)"minecraft:red_flower.5", (Object)"minecraft:orange_tulip");
        $$0.put((Object)"minecraft:red_flower.6", (Object)"minecraft:white_tulip");
        $$0.put((Object)"minecraft:red_flower.7", (Object)"minecraft:pink_tulip");
        $$0.put((Object)"minecraft:red_flower.8", (Object)"minecraft:oxeye_daisy");
        $$0.put((Object)"minecraft:double_plant.0", (Object)"minecraft:sunflower");
        $$0.put((Object)"minecraft:double_plant.1", (Object)"minecraft:lilac");
        $$0.put((Object)"minecraft:double_plant.2", (Object)"minecraft:tall_grass");
        $$0.put((Object)"minecraft:double_plant.3", (Object)"minecraft:large_fern");
        $$0.put((Object)"minecraft:double_plant.4", (Object)"minecraft:rose_bush");
        $$0.put((Object)"minecraft:double_plant.5", (Object)"minecraft:peony");
        $$0.put((Object)"minecraft:deadbush.0", (Object)"minecraft:dead_bush");
        $$0.put((Object)"minecraft:tallgrass.0", (Object)"minecraft:dead_bush");
        $$0.put((Object)"minecraft:tallgrass.1", (Object)"minecraft:grass");
        $$0.put((Object)"minecraft:tallgrass.2", (Object)"minecraft:fern");
        $$0.put((Object)"minecraft:sponge.0", (Object)"minecraft:sponge");
        $$0.put((Object)"minecraft:sponge.1", (Object)"minecraft:wet_sponge");
        $$0.put((Object)"minecraft:purpur_slab.0", (Object)"minecraft:purpur_slab");
        $$0.put((Object)"minecraft:stone_slab.0", (Object)"minecraft:stone_slab");
        $$0.put((Object)"minecraft:stone_slab.1", (Object)"minecraft:sandstone_slab");
        $$0.put((Object)"minecraft:stone_slab.2", (Object)"minecraft:petrified_oak_slab");
        $$0.put((Object)"minecraft:stone_slab.3", (Object)"minecraft:cobblestone_slab");
        $$0.put((Object)"minecraft:stone_slab.4", (Object)"minecraft:brick_slab");
        $$0.put((Object)"minecraft:stone_slab.5", (Object)"minecraft:stone_brick_slab");
        $$0.put((Object)"minecraft:stone_slab.6", (Object)"minecraft:nether_brick_slab");
        $$0.put((Object)"minecraft:stone_slab.7", (Object)"minecraft:quartz_slab");
        $$0.put((Object)"minecraft:stone_slab2.0", (Object)"minecraft:red_sandstone_slab");
        $$0.put((Object)"minecraft:wooden_slab.0", (Object)"minecraft:oak_slab");
        $$0.put((Object)"minecraft:wooden_slab.1", (Object)"minecraft:spruce_slab");
        $$0.put((Object)"minecraft:wooden_slab.2", (Object)"minecraft:birch_slab");
        $$0.put((Object)"minecraft:wooden_slab.3", (Object)"minecraft:jungle_slab");
        $$0.put((Object)"minecraft:wooden_slab.4", (Object)"minecraft:acacia_slab");
        $$0.put((Object)"minecraft:wooden_slab.5", (Object)"minecraft:dark_oak_slab");
        $$0.put((Object)"minecraft:coal.0", (Object)"minecraft:coal");
        $$0.put((Object)"minecraft:coal.1", (Object)"minecraft:charcoal");
        $$0.put((Object)"minecraft:fish.0", (Object)"minecraft:cod");
        $$0.put((Object)"minecraft:fish.1", (Object)"minecraft:salmon");
        $$0.put((Object)"minecraft:fish.2", (Object)"minecraft:clownfish");
        $$0.put((Object)"minecraft:fish.3", (Object)"minecraft:pufferfish");
        $$0.put((Object)"minecraft:cooked_fish.0", (Object)"minecraft:cooked_cod");
        $$0.put((Object)"minecraft:cooked_fish.1", (Object)"minecraft:cooked_salmon");
        $$0.put((Object)"minecraft:skull.0", (Object)"minecraft:skeleton_skull");
        $$0.put((Object)"minecraft:skull.1", (Object)"minecraft:wither_skeleton_skull");
        $$0.put((Object)"minecraft:skull.2", (Object)"minecraft:zombie_head");
        $$0.put((Object)"minecraft:skull.3", (Object)"minecraft:player_head");
        $$0.put((Object)"minecraft:skull.4", (Object)"minecraft:creeper_head");
        $$0.put((Object)"minecraft:skull.5", (Object)"minecraft:dragon_head");
        $$0.put((Object)"minecraft:golden_apple.0", (Object)"minecraft:golden_apple");
        $$0.put((Object)"minecraft:golden_apple.1", (Object)"minecraft:enchanted_golden_apple");
        $$0.put((Object)"minecraft:fireworks.0", (Object)"minecraft:firework_rocket");
        $$0.put((Object)"minecraft:firework_charge.0", (Object)"minecraft:firework_star");
        $$0.put((Object)"minecraft:dye.0", (Object)"minecraft:ink_sac");
        $$0.put((Object)"minecraft:dye.1", (Object)"minecraft:rose_red");
        $$0.put((Object)"minecraft:dye.2", (Object)"minecraft:cactus_green");
        $$0.put((Object)"minecraft:dye.3", (Object)"minecraft:cocoa_beans");
        $$0.put((Object)"minecraft:dye.4", (Object)"minecraft:lapis_lazuli");
        $$0.put((Object)"minecraft:dye.5", (Object)"minecraft:purple_dye");
        $$0.put((Object)"minecraft:dye.6", (Object)"minecraft:cyan_dye");
        $$0.put((Object)"minecraft:dye.7", (Object)"minecraft:light_gray_dye");
        $$0.put((Object)"minecraft:dye.8", (Object)"minecraft:gray_dye");
        $$0.put((Object)"minecraft:dye.9", (Object)"minecraft:pink_dye");
        $$0.put((Object)"minecraft:dye.10", (Object)"minecraft:lime_dye");
        $$0.put((Object)"minecraft:dye.11", (Object)"minecraft:dandelion_yellow");
        $$0.put((Object)"minecraft:dye.12", (Object)"minecraft:light_blue_dye");
        $$0.put((Object)"minecraft:dye.13", (Object)"minecraft:magenta_dye");
        $$0.put((Object)"minecraft:dye.14", (Object)"minecraft:orange_dye");
        $$0.put((Object)"minecraft:dye.15", (Object)"minecraft:bone_meal");
        $$0.put((Object)"minecraft:silver_shulker_box.0", (Object)"minecraft:light_gray_shulker_box");
        $$0.put((Object)"minecraft:fence.0", (Object)"minecraft:oak_fence");
        $$0.put((Object)"minecraft:fence_gate.0", (Object)"minecraft:oak_fence_gate");
        $$0.put((Object)"minecraft:wooden_door.0", (Object)"minecraft:oak_door");
        $$0.put((Object)"minecraft:boat.0", (Object)"minecraft:oak_boat");
        $$0.put((Object)"minecraft:lit_pumpkin.0", (Object)"minecraft:jack_o_lantern");
        $$0.put((Object)"minecraft:pumpkin.0", (Object)"minecraft:carved_pumpkin");
        $$0.put((Object)"minecraft:trapdoor.0", (Object)"minecraft:oak_trapdoor");
        $$0.put((Object)"minecraft:nether_brick.0", (Object)"minecraft:nether_bricks");
        $$0.put((Object)"minecraft:red_nether_brick.0", (Object)"minecraft:red_nether_bricks");
        $$0.put((Object)"minecraft:netherbrick.0", (Object)"minecraft:nether_brick");
        $$0.put((Object)"minecraft:wooden_button.0", (Object)"minecraft:oak_button");
        $$0.put((Object)"minecraft:wooden_pressure_plate.0", (Object)"minecraft:oak_pressure_plate");
        $$0.put((Object)"minecraft:noteblock.0", (Object)"minecraft:note_block");
        $$0.put((Object)"minecraft:bed.0", (Object)"minecraft:white_bed");
        $$0.put((Object)"minecraft:bed.1", (Object)"minecraft:orange_bed");
        $$0.put((Object)"minecraft:bed.2", (Object)"minecraft:magenta_bed");
        $$0.put((Object)"minecraft:bed.3", (Object)"minecraft:light_blue_bed");
        $$0.put((Object)"minecraft:bed.4", (Object)"minecraft:yellow_bed");
        $$0.put((Object)"minecraft:bed.5", (Object)"minecraft:lime_bed");
        $$0.put((Object)"minecraft:bed.6", (Object)"minecraft:pink_bed");
        $$0.put((Object)"minecraft:bed.7", (Object)"minecraft:gray_bed");
        $$0.put((Object)"minecraft:bed.8", (Object)"minecraft:light_gray_bed");
        $$0.put((Object)"minecraft:bed.9", (Object)"minecraft:cyan_bed");
        $$0.put((Object)"minecraft:bed.10", (Object)"minecraft:purple_bed");
        $$0.put((Object)"minecraft:bed.11", (Object)"minecraft:blue_bed");
        $$0.put((Object)"minecraft:bed.12", (Object)"minecraft:brown_bed");
        $$0.put((Object)"minecraft:bed.13", (Object)"minecraft:green_bed");
        $$0.put((Object)"minecraft:bed.14", (Object)"minecraft:red_bed");
        $$0.put((Object)"minecraft:bed.15", (Object)"minecraft:black_bed");
        $$0.put((Object)"minecraft:banner.15", (Object)"minecraft:white_banner");
        $$0.put((Object)"minecraft:banner.14", (Object)"minecraft:orange_banner");
        $$0.put((Object)"minecraft:banner.13", (Object)"minecraft:magenta_banner");
        $$0.put((Object)"minecraft:banner.12", (Object)"minecraft:light_blue_banner");
        $$0.put((Object)"minecraft:banner.11", (Object)"minecraft:yellow_banner");
        $$0.put((Object)"minecraft:banner.10", (Object)"minecraft:lime_banner");
        $$0.put((Object)"minecraft:banner.9", (Object)"minecraft:pink_banner");
        $$0.put((Object)"minecraft:banner.8", (Object)"minecraft:gray_banner");
        $$0.put((Object)"minecraft:banner.7", (Object)"minecraft:light_gray_banner");
        $$0.put((Object)"minecraft:banner.6", (Object)"minecraft:cyan_banner");
        $$0.put((Object)"minecraft:banner.5", (Object)"minecraft:purple_banner");
        $$0.put((Object)"minecraft:banner.4", (Object)"minecraft:blue_banner");
        $$0.put((Object)"minecraft:banner.3", (Object)"minecraft:brown_banner");
        $$0.put((Object)"minecraft:banner.2", (Object)"minecraft:green_banner");
        $$0.put((Object)"minecraft:banner.1", (Object)"minecraft:red_banner");
        $$0.put((Object)"minecraft:banner.0", (Object)"minecraft:black_banner");
        $$0.put((Object)"minecraft:grass.0", (Object)"minecraft:grass_block");
        $$0.put((Object)"minecraft:brick_block.0", (Object)"minecraft:bricks");
        $$0.put((Object)"minecraft:end_bricks.0", (Object)"minecraft:end_stone_bricks");
        $$0.put((Object)"minecraft:golden_rail.0", (Object)"minecraft:powered_rail");
        $$0.put((Object)"minecraft:magma.0", (Object)"minecraft:magma_block");
        $$0.put((Object)"minecraft:quartz_ore.0", (Object)"minecraft:nether_quartz_ore");
        $$0.put((Object)"minecraft:reeds.0", (Object)"minecraft:sugar_cane");
        $$0.put((Object)"minecraft:slime.0", (Object)"minecraft:slime_block");
        $$0.put((Object)"minecraft:stone_stairs.0", (Object)"minecraft:cobblestone_stairs");
        $$0.put((Object)"minecraft:waterlily.0", (Object)"minecraft:lily_pad");
        $$0.put((Object)"minecraft:web.0", (Object)"minecraft:cobweb");
        $$0.put((Object)"minecraft:snow.0", (Object)"minecraft:snow_block");
        $$0.put((Object)"minecraft:snow_layer.0", (Object)"minecraft:snow");
        $$0.put((Object)"minecraft:record_11.0", (Object)"minecraft:music_disc_11");
        $$0.put((Object)"minecraft:record_13.0", (Object)"minecraft:music_disc_13");
        $$0.put((Object)"minecraft:record_blocks.0", (Object)"minecraft:music_disc_blocks");
        $$0.put((Object)"minecraft:record_cat.0", (Object)"minecraft:music_disc_cat");
        $$0.put((Object)"minecraft:record_chirp.0", (Object)"minecraft:music_disc_chirp");
        $$0.put((Object)"minecraft:record_far.0", (Object)"minecraft:music_disc_far");
        $$0.put((Object)"minecraft:record_mall.0", (Object)"minecraft:music_disc_mall");
        $$0.put((Object)"minecraft:record_mellohi.0", (Object)"minecraft:music_disc_mellohi");
        $$0.put((Object)"minecraft:record_stal.0", (Object)"minecraft:music_disc_stal");
        $$0.put((Object)"minecraft:record_strad.0", (Object)"minecraft:music_disc_strad");
        $$0.put((Object)"minecraft:record_wait.0", (Object)"minecraft:music_disc_wait");
        $$0.put((Object)"minecraft:record_ward.0", (Object)"minecraft:music_disc_ward");
    });
    private static final Set<String> IDS = (Set)MAP.keySet().stream().map($$0 -> $$0.substring(0, $$0.indexOf(46))).collect(Collectors.toSet());
    private static final Set<String> DAMAGE_IDS = Sets.newHashSet((Object[])new String[]{"minecraft:bow", "minecraft:carrot_on_a_stick", "minecraft:chainmail_boots", "minecraft:chainmail_chestplate", "minecraft:chainmail_helmet", "minecraft:chainmail_leggings", "minecraft:diamond_axe", "minecraft:diamond_boots", "minecraft:diamond_chestplate", "minecraft:diamond_helmet", "minecraft:diamond_hoe", "minecraft:diamond_leggings", "minecraft:diamond_pickaxe", "minecraft:diamond_shovel", "minecraft:diamond_sword", "minecraft:elytra", "minecraft:fishing_rod", "minecraft:flint_and_steel", "minecraft:golden_axe", "minecraft:golden_boots", "minecraft:golden_chestplate", "minecraft:golden_helmet", "minecraft:golden_hoe", "minecraft:golden_leggings", "minecraft:golden_pickaxe", "minecraft:golden_shovel", "minecraft:golden_sword", "minecraft:iron_axe", "minecraft:iron_boots", "minecraft:iron_chestplate", "minecraft:iron_helmet", "minecraft:iron_hoe", "minecraft:iron_leggings", "minecraft:iron_pickaxe", "minecraft:iron_shovel", "minecraft:iron_sword", "minecraft:leather_boots", "minecraft:leather_chestplate", "minecraft:leather_helmet", "minecraft:leather_leggings", "minecraft:shears", "minecraft:shield", "minecraft:stone_axe", "minecraft:stone_hoe", "minecraft:stone_pickaxe", "minecraft:stone_shovel", "minecraft:stone_sword", "minecraft:wooden_axe", "minecraft:wooden_hoe", "minecraft:wooden_pickaxe", "minecraft:wooden_shovel", "minecraft:wooden_sword"});

    public ItemStackTheFlatteningFix(Schema $$0, boolean $$1) {
        super($$0, $$1);
    }

    public TypeRewriteRule makeRule() {
        Type $$0 = this.getInputSchema().getType(References.ITEM_STACK);
        OpticFinder $$1 = DSL.fieldFinder((String)"id", (Type)DSL.named((String)References.ITEM_NAME.typeName(), NamespacedSchema.namespacedString()));
        OpticFinder $$22 = $$0.findField("tag");
        return this.fixTypeEverywhereTyped("ItemInstanceTheFlatteningFix", $$0, $$2 -> {
            Optional $$3 = $$2.getOptional($$1);
            if (!$$3.isPresent()) {
                return $$2;
            }
            Typed $$4 = $$2;
            Dynamic $$5 = (Dynamic)$$2.get(DSL.remainderFinder());
            int $$6 = $$5.get("Damage").asInt(0);
            String $$7 = ItemStackTheFlatteningFix.updateItem((String)((Pair)$$3.get()).getSecond(), $$6);
            if ($$7 != null) {
                $$4 = $$4.set($$1, (Object)Pair.of((Object)References.ITEM_NAME.typeName(), (Object)$$7));
            }
            if (DAMAGE_IDS.contains(((Pair)$$3.get()).getSecond())) {
                Typed $$8 = $$2.getOrCreateTyped($$22);
                Dynamic $$9 = (Dynamic)$$8.get(DSL.remainderFinder());
                $$9 = $$9.set("Damage", $$9.createInt($$6));
                $$4 = $$4.set($$22, $$8.set(DSL.remainderFinder(), (Object)$$9));
            }
            $$4 = $$4.set(DSL.remainderFinder(), (Object)$$5.remove("Damage"));
            return $$4;
        });
    }

    @Nullable
    public static String updateItem(@Nullable String $$0, int $$1) {
        if (IDS.contains((Object)$$0)) {
            String $$2 = (String)MAP.get((Object)($$0 + "." + $$1));
            return $$2 == null ? (String)MAP.get((Object)($$0 + ".0")) : $$2;
        }
        return null;
    }
}