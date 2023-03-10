/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.util.Pair
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  java.lang.Object
 *  java.lang.String
 */
package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class ItemIdFix
extends DataFix {
    private static final Int2ObjectMap<String> ITEM_NAMES = (Int2ObjectMap)DataFixUtils.make((Object)new Int2ObjectOpenHashMap(), $$0 -> {
        $$0.put(1, (Object)"minecraft:stone");
        $$0.put(2, (Object)"minecraft:grass");
        $$0.put(3, (Object)"minecraft:dirt");
        $$0.put(4, (Object)"minecraft:cobblestone");
        $$0.put(5, (Object)"minecraft:planks");
        $$0.put(6, (Object)"minecraft:sapling");
        $$0.put(7, (Object)"minecraft:bedrock");
        $$0.put(8, (Object)"minecraft:flowing_water");
        $$0.put(9, (Object)"minecraft:water");
        $$0.put(10, (Object)"minecraft:flowing_lava");
        $$0.put(11, (Object)"minecraft:lava");
        $$0.put(12, (Object)"minecraft:sand");
        $$0.put(13, (Object)"minecraft:gravel");
        $$0.put(14, (Object)"minecraft:gold_ore");
        $$0.put(15, (Object)"minecraft:iron_ore");
        $$0.put(16, (Object)"minecraft:coal_ore");
        $$0.put(17, (Object)"minecraft:log");
        $$0.put(18, (Object)"minecraft:leaves");
        $$0.put(19, (Object)"minecraft:sponge");
        $$0.put(20, (Object)"minecraft:glass");
        $$0.put(21, (Object)"minecraft:lapis_ore");
        $$0.put(22, (Object)"minecraft:lapis_block");
        $$0.put(23, (Object)"minecraft:dispenser");
        $$0.put(24, (Object)"minecraft:sandstone");
        $$0.put(25, (Object)"minecraft:noteblock");
        $$0.put(27, (Object)"minecraft:golden_rail");
        $$0.put(28, (Object)"minecraft:detector_rail");
        $$0.put(29, (Object)"minecraft:sticky_piston");
        $$0.put(30, (Object)"minecraft:web");
        $$0.put(31, (Object)"minecraft:tallgrass");
        $$0.put(32, (Object)"minecraft:deadbush");
        $$0.put(33, (Object)"minecraft:piston");
        $$0.put(35, (Object)"minecraft:wool");
        $$0.put(37, (Object)"minecraft:yellow_flower");
        $$0.put(38, (Object)"minecraft:red_flower");
        $$0.put(39, (Object)"minecraft:brown_mushroom");
        $$0.put(40, (Object)"minecraft:red_mushroom");
        $$0.put(41, (Object)"minecraft:gold_block");
        $$0.put(42, (Object)"minecraft:iron_block");
        $$0.put(43, (Object)"minecraft:double_stone_slab");
        $$0.put(44, (Object)"minecraft:stone_slab");
        $$0.put(45, (Object)"minecraft:brick_block");
        $$0.put(46, (Object)"minecraft:tnt");
        $$0.put(47, (Object)"minecraft:bookshelf");
        $$0.put(48, (Object)"minecraft:mossy_cobblestone");
        $$0.put(49, (Object)"minecraft:obsidian");
        $$0.put(50, (Object)"minecraft:torch");
        $$0.put(51, (Object)"minecraft:fire");
        $$0.put(52, (Object)"minecraft:mob_spawner");
        $$0.put(53, (Object)"minecraft:oak_stairs");
        $$0.put(54, (Object)"minecraft:chest");
        $$0.put(56, (Object)"minecraft:diamond_ore");
        $$0.put(57, (Object)"minecraft:diamond_block");
        $$0.put(58, (Object)"minecraft:crafting_table");
        $$0.put(60, (Object)"minecraft:farmland");
        $$0.put(61, (Object)"minecraft:furnace");
        $$0.put(62, (Object)"minecraft:lit_furnace");
        $$0.put(65, (Object)"minecraft:ladder");
        $$0.put(66, (Object)"minecraft:rail");
        $$0.put(67, (Object)"minecraft:stone_stairs");
        $$0.put(69, (Object)"minecraft:lever");
        $$0.put(70, (Object)"minecraft:stone_pressure_plate");
        $$0.put(72, (Object)"minecraft:wooden_pressure_plate");
        $$0.put(73, (Object)"minecraft:redstone_ore");
        $$0.put(76, (Object)"minecraft:redstone_torch");
        $$0.put(77, (Object)"minecraft:stone_button");
        $$0.put(78, (Object)"minecraft:snow_layer");
        $$0.put(79, (Object)"minecraft:ice");
        $$0.put(80, (Object)"minecraft:snow");
        $$0.put(81, (Object)"minecraft:cactus");
        $$0.put(82, (Object)"minecraft:clay");
        $$0.put(84, (Object)"minecraft:jukebox");
        $$0.put(85, (Object)"minecraft:fence");
        $$0.put(86, (Object)"minecraft:pumpkin");
        $$0.put(87, (Object)"minecraft:netherrack");
        $$0.put(88, (Object)"minecraft:soul_sand");
        $$0.put(89, (Object)"minecraft:glowstone");
        $$0.put(90, (Object)"minecraft:portal");
        $$0.put(91, (Object)"minecraft:lit_pumpkin");
        $$0.put(95, (Object)"minecraft:stained_glass");
        $$0.put(96, (Object)"minecraft:trapdoor");
        $$0.put(97, (Object)"minecraft:monster_egg");
        $$0.put(98, (Object)"minecraft:stonebrick");
        $$0.put(99, (Object)"minecraft:brown_mushroom_block");
        $$0.put(100, (Object)"minecraft:red_mushroom_block");
        $$0.put(101, (Object)"minecraft:iron_bars");
        $$0.put(102, (Object)"minecraft:glass_pane");
        $$0.put(103, (Object)"minecraft:melon_block");
        $$0.put(106, (Object)"minecraft:vine");
        $$0.put(107, (Object)"minecraft:fence_gate");
        $$0.put(108, (Object)"minecraft:brick_stairs");
        $$0.put(109, (Object)"minecraft:stone_brick_stairs");
        $$0.put(110, (Object)"minecraft:mycelium");
        $$0.put(111, (Object)"minecraft:waterlily");
        $$0.put(112, (Object)"minecraft:nether_brick");
        $$0.put(113, (Object)"minecraft:nether_brick_fence");
        $$0.put(114, (Object)"minecraft:nether_brick_stairs");
        $$0.put(116, (Object)"minecraft:enchanting_table");
        $$0.put(119, (Object)"minecraft:end_portal");
        $$0.put(120, (Object)"minecraft:end_portal_frame");
        $$0.put(121, (Object)"minecraft:end_stone");
        $$0.put(122, (Object)"minecraft:dragon_egg");
        $$0.put(123, (Object)"minecraft:redstone_lamp");
        $$0.put(125, (Object)"minecraft:double_wooden_slab");
        $$0.put(126, (Object)"minecraft:wooden_slab");
        $$0.put(127, (Object)"minecraft:cocoa");
        $$0.put(128, (Object)"minecraft:sandstone_stairs");
        $$0.put(129, (Object)"minecraft:emerald_ore");
        $$0.put(130, (Object)"minecraft:ender_chest");
        $$0.put(131, (Object)"minecraft:tripwire_hook");
        $$0.put(133, (Object)"minecraft:emerald_block");
        $$0.put(134, (Object)"minecraft:spruce_stairs");
        $$0.put(135, (Object)"minecraft:birch_stairs");
        $$0.put(136, (Object)"minecraft:jungle_stairs");
        $$0.put(137, (Object)"minecraft:command_block");
        $$0.put(138, (Object)"minecraft:beacon");
        $$0.put(139, (Object)"minecraft:cobblestone_wall");
        $$0.put(141, (Object)"minecraft:carrots");
        $$0.put(142, (Object)"minecraft:potatoes");
        $$0.put(143, (Object)"minecraft:wooden_button");
        $$0.put(145, (Object)"minecraft:anvil");
        $$0.put(146, (Object)"minecraft:trapped_chest");
        $$0.put(147, (Object)"minecraft:light_weighted_pressure_plate");
        $$0.put(148, (Object)"minecraft:heavy_weighted_pressure_plate");
        $$0.put(151, (Object)"minecraft:daylight_detector");
        $$0.put(152, (Object)"minecraft:redstone_block");
        $$0.put(153, (Object)"minecraft:quartz_ore");
        $$0.put(154, (Object)"minecraft:hopper");
        $$0.put(155, (Object)"minecraft:quartz_block");
        $$0.put(156, (Object)"minecraft:quartz_stairs");
        $$0.put(157, (Object)"minecraft:activator_rail");
        $$0.put(158, (Object)"minecraft:dropper");
        $$0.put(159, (Object)"minecraft:stained_hardened_clay");
        $$0.put(160, (Object)"minecraft:stained_glass_pane");
        $$0.put(161, (Object)"minecraft:leaves2");
        $$0.put(162, (Object)"minecraft:log2");
        $$0.put(163, (Object)"minecraft:acacia_stairs");
        $$0.put(164, (Object)"minecraft:dark_oak_stairs");
        $$0.put(170, (Object)"minecraft:hay_block");
        $$0.put(171, (Object)"minecraft:carpet");
        $$0.put(172, (Object)"minecraft:hardened_clay");
        $$0.put(173, (Object)"minecraft:coal_block");
        $$0.put(174, (Object)"minecraft:packed_ice");
        $$0.put(175, (Object)"minecraft:double_plant");
        $$0.put(256, (Object)"minecraft:iron_shovel");
        $$0.put(257, (Object)"minecraft:iron_pickaxe");
        $$0.put(258, (Object)"minecraft:iron_axe");
        $$0.put(259, (Object)"minecraft:flint_and_steel");
        $$0.put(260, (Object)"minecraft:apple");
        $$0.put(261, (Object)"minecraft:bow");
        $$0.put(262, (Object)"minecraft:arrow");
        $$0.put(263, (Object)"minecraft:coal");
        $$0.put(264, (Object)"minecraft:diamond");
        $$0.put(265, (Object)"minecraft:iron_ingot");
        $$0.put(266, (Object)"minecraft:gold_ingot");
        $$0.put(267, (Object)"minecraft:iron_sword");
        $$0.put(268, (Object)"minecraft:wooden_sword");
        $$0.put(269, (Object)"minecraft:wooden_shovel");
        $$0.put(270, (Object)"minecraft:wooden_pickaxe");
        $$0.put(271, (Object)"minecraft:wooden_axe");
        $$0.put(272, (Object)"minecraft:stone_sword");
        $$0.put(273, (Object)"minecraft:stone_shovel");
        $$0.put(274, (Object)"minecraft:stone_pickaxe");
        $$0.put(275, (Object)"minecraft:stone_axe");
        $$0.put(276, (Object)"minecraft:diamond_sword");
        $$0.put(277, (Object)"minecraft:diamond_shovel");
        $$0.put(278, (Object)"minecraft:diamond_pickaxe");
        $$0.put(279, (Object)"minecraft:diamond_axe");
        $$0.put(280, (Object)"minecraft:stick");
        $$0.put(281, (Object)"minecraft:bowl");
        $$0.put(282, (Object)"minecraft:mushroom_stew");
        $$0.put(283, (Object)"minecraft:golden_sword");
        $$0.put(284, (Object)"minecraft:golden_shovel");
        $$0.put(285, (Object)"minecraft:golden_pickaxe");
        $$0.put(286, (Object)"minecraft:golden_axe");
        $$0.put(287, (Object)"minecraft:string");
        $$0.put(288, (Object)"minecraft:feather");
        $$0.put(289, (Object)"minecraft:gunpowder");
        $$0.put(290, (Object)"minecraft:wooden_hoe");
        $$0.put(291, (Object)"minecraft:stone_hoe");
        $$0.put(292, (Object)"minecraft:iron_hoe");
        $$0.put(293, (Object)"minecraft:diamond_hoe");
        $$0.put(294, (Object)"minecraft:golden_hoe");
        $$0.put(295, (Object)"minecraft:wheat_seeds");
        $$0.put(296, (Object)"minecraft:wheat");
        $$0.put(297, (Object)"minecraft:bread");
        $$0.put(298, (Object)"minecraft:leather_helmet");
        $$0.put(299, (Object)"minecraft:leather_chestplate");
        $$0.put(300, (Object)"minecraft:leather_leggings");
        $$0.put(301, (Object)"minecraft:leather_boots");
        $$0.put(302, (Object)"minecraft:chainmail_helmet");
        $$0.put(303, (Object)"minecraft:chainmail_chestplate");
        $$0.put(304, (Object)"minecraft:chainmail_leggings");
        $$0.put(305, (Object)"minecraft:chainmail_boots");
        $$0.put(306, (Object)"minecraft:iron_helmet");
        $$0.put(307, (Object)"minecraft:iron_chestplate");
        $$0.put(308, (Object)"minecraft:iron_leggings");
        $$0.put(309, (Object)"minecraft:iron_boots");
        $$0.put(310, (Object)"minecraft:diamond_helmet");
        $$0.put(311, (Object)"minecraft:diamond_chestplate");
        $$0.put(312, (Object)"minecraft:diamond_leggings");
        $$0.put(313, (Object)"minecraft:diamond_boots");
        $$0.put(314, (Object)"minecraft:golden_helmet");
        $$0.put(315, (Object)"minecraft:golden_chestplate");
        $$0.put(316, (Object)"minecraft:golden_leggings");
        $$0.put(317, (Object)"minecraft:golden_boots");
        $$0.put(318, (Object)"minecraft:flint");
        $$0.put(319, (Object)"minecraft:porkchop");
        $$0.put(320, (Object)"minecraft:cooked_porkchop");
        $$0.put(321, (Object)"minecraft:painting");
        $$0.put(322, (Object)"minecraft:golden_apple");
        $$0.put(323, (Object)"minecraft:sign");
        $$0.put(324, (Object)"minecraft:wooden_door");
        $$0.put(325, (Object)"minecraft:bucket");
        $$0.put(326, (Object)"minecraft:water_bucket");
        $$0.put(327, (Object)"minecraft:lava_bucket");
        $$0.put(328, (Object)"minecraft:minecart");
        $$0.put(329, (Object)"minecraft:saddle");
        $$0.put(330, (Object)"minecraft:iron_door");
        $$0.put(331, (Object)"minecraft:redstone");
        $$0.put(332, (Object)"minecraft:snowball");
        $$0.put(333, (Object)"minecraft:boat");
        $$0.put(334, (Object)"minecraft:leather");
        $$0.put(335, (Object)"minecraft:milk_bucket");
        $$0.put(336, (Object)"minecraft:brick");
        $$0.put(337, (Object)"minecraft:clay_ball");
        $$0.put(338, (Object)"minecraft:reeds");
        $$0.put(339, (Object)"minecraft:paper");
        $$0.put(340, (Object)"minecraft:book");
        $$0.put(341, (Object)"minecraft:slime_ball");
        $$0.put(342, (Object)"minecraft:chest_minecart");
        $$0.put(343, (Object)"minecraft:furnace_minecart");
        $$0.put(344, (Object)"minecraft:egg");
        $$0.put(345, (Object)"minecraft:compass");
        $$0.put(346, (Object)"minecraft:fishing_rod");
        $$0.put(347, (Object)"minecraft:clock");
        $$0.put(348, (Object)"minecraft:glowstone_dust");
        $$0.put(349, (Object)"minecraft:fish");
        $$0.put(350, (Object)"minecraft:cooked_fished");
        $$0.put(351, (Object)"minecraft:dye");
        $$0.put(352, (Object)"minecraft:bone");
        $$0.put(353, (Object)"minecraft:sugar");
        $$0.put(354, (Object)"minecraft:cake");
        $$0.put(355, (Object)"minecraft:bed");
        $$0.put(356, (Object)"minecraft:repeater");
        $$0.put(357, (Object)"minecraft:cookie");
        $$0.put(358, (Object)"minecraft:filled_map");
        $$0.put(359, (Object)"minecraft:shears");
        $$0.put(360, (Object)"minecraft:melon");
        $$0.put(361, (Object)"minecraft:pumpkin_seeds");
        $$0.put(362, (Object)"minecraft:melon_seeds");
        $$0.put(363, (Object)"minecraft:beef");
        $$0.put(364, (Object)"minecraft:cooked_beef");
        $$0.put(365, (Object)"minecraft:chicken");
        $$0.put(366, (Object)"minecraft:cooked_chicken");
        $$0.put(367, (Object)"minecraft:rotten_flesh");
        $$0.put(368, (Object)"minecraft:ender_pearl");
        $$0.put(369, (Object)"minecraft:blaze_rod");
        $$0.put(370, (Object)"minecraft:ghast_tear");
        $$0.put(371, (Object)"minecraft:gold_nugget");
        $$0.put(372, (Object)"minecraft:nether_wart");
        $$0.put(373, (Object)"minecraft:potion");
        $$0.put(374, (Object)"minecraft:glass_bottle");
        $$0.put(375, (Object)"minecraft:spider_eye");
        $$0.put(376, (Object)"minecraft:fermented_spider_eye");
        $$0.put(377, (Object)"minecraft:blaze_powder");
        $$0.put(378, (Object)"minecraft:magma_cream");
        $$0.put(379, (Object)"minecraft:brewing_stand");
        $$0.put(380, (Object)"minecraft:cauldron");
        $$0.put(381, (Object)"minecraft:ender_eye");
        $$0.put(382, (Object)"minecraft:speckled_melon");
        $$0.put(383, (Object)"minecraft:spawn_egg");
        $$0.put(384, (Object)"minecraft:experience_bottle");
        $$0.put(385, (Object)"minecraft:fire_charge");
        $$0.put(386, (Object)"minecraft:writable_book");
        $$0.put(387, (Object)"minecraft:written_book");
        $$0.put(388, (Object)"minecraft:emerald");
        $$0.put(389, (Object)"minecraft:item_frame");
        $$0.put(390, (Object)"minecraft:flower_pot");
        $$0.put(391, (Object)"minecraft:carrot");
        $$0.put(392, (Object)"minecraft:potato");
        $$0.put(393, (Object)"minecraft:baked_potato");
        $$0.put(394, (Object)"minecraft:poisonous_potato");
        $$0.put(395, (Object)"minecraft:map");
        $$0.put(396, (Object)"minecraft:golden_carrot");
        $$0.put(397, (Object)"minecraft:skull");
        $$0.put(398, (Object)"minecraft:carrot_on_a_stick");
        $$0.put(399, (Object)"minecraft:nether_star");
        $$0.put(400, (Object)"minecraft:pumpkin_pie");
        $$0.put(401, (Object)"minecraft:fireworks");
        $$0.put(402, (Object)"minecraft:firework_charge");
        $$0.put(403, (Object)"minecraft:enchanted_book");
        $$0.put(404, (Object)"minecraft:comparator");
        $$0.put(405, (Object)"minecraft:netherbrick");
        $$0.put(406, (Object)"minecraft:quartz");
        $$0.put(407, (Object)"minecraft:tnt_minecart");
        $$0.put(408, (Object)"minecraft:hopper_minecart");
        $$0.put(417, (Object)"minecraft:iron_horse_armor");
        $$0.put(418, (Object)"minecraft:golden_horse_armor");
        $$0.put(419, (Object)"minecraft:diamond_horse_armor");
        $$0.put(420, (Object)"minecraft:lead");
        $$0.put(421, (Object)"minecraft:name_tag");
        $$0.put(422, (Object)"minecraft:command_block_minecart");
        $$0.put(2256, (Object)"minecraft:record_13");
        $$0.put(2257, (Object)"minecraft:record_cat");
        $$0.put(2258, (Object)"minecraft:record_blocks");
        $$0.put(2259, (Object)"minecraft:record_chirp");
        $$0.put(2260, (Object)"minecraft:record_far");
        $$0.put(2261, (Object)"minecraft:record_mall");
        $$0.put(2262, (Object)"minecraft:record_mellohi");
        $$0.put(2263, (Object)"minecraft:record_stal");
        $$0.put(2264, (Object)"minecraft:record_strad");
        $$0.put(2265, (Object)"minecraft:record_ward");
        $$0.put(2266, (Object)"minecraft:record_11");
        $$0.put(2267, (Object)"minecraft:record_wait");
        $$0.defaultReturnValue((Object)"minecraft:air");
    });

    public ItemIdFix(Schema $$0, boolean $$1) {
        super($$0, $$1);
    }

    public static String getItem(int $$0) {
        return (String)ITEM_NAMES.get($$0);
    }

    public TypeRewriteRule makeRule() {
        Type $$0 = DSL.or((Type)DSL.intType(), (Type)DSL.named((String)References.ITEM_NAME.typeName(), NamespacedSchema.namespacedString()));
        Type $$1 = DSL.named((String)References.ITEM_NAME.typeName(), NamespacedSchema.namespacedString());
        OpticFinder $$22 = DSL.fieldFinder((String)"id", (Type)$$0);
        return this.fixTypeEverywhereTyped("ItemIdFix", this.getInputSchema().getType(References.ITEM_STACK), this.getOutputSchema().getType(References.ITEM_STACK), $$2 -> $$2.update($$22, $$1, $$02 -> (Pair)$$02.map($$0 -> Pair.of((Object)References.ITEM_NAME.typeName(), (Object)ItemIdFix.getItem($$0)), $$0 -> $$0)));
    }
}