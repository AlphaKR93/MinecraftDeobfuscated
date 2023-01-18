/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.types.templates.Tag$TagType
 *  com.mojang.datafixers.util.Either
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Dynamic
 *  java.lang.Integer
 *  java.lang.Number
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Map
 *  java.util.Optional
 *  java.util.function.Function
 */
package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.Tag;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.BlockStateData;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class EntityBlockStateFix
extends DataFix {
    private static final Map<String, Integer> MAP = (Map)DataFixUtils.make((Object)Maps.newHashMap(), $$0 -> {
        $$0.put((Object)"minecraft:air", (Object)0);
        $$0.put((Object)"minecraft:stone", (Object)1);
        $$0.put((Object)"minecraft:grass", (Object)2);
        $$0.put((Object)"minecraft:dirt", (Object)3);
        $$0.put((Object)"minecraft:cobblestone", (Object)4);
        $$0.put((Object)"minecraft:planks", (Object)5);
        $$0.put((Object)"minecraft:sapling", (Object)6);
        $$0.put((Object)"minecraft:bedrock", (Object)7);
        $$0.put((Object)"minecraft:flowing_water", (Object)8);
        $$0.put((Object)"minecraft:water", (Object)9);
        $$0.put((Object)"minecraft:flowing_lava", (Object)10);
        $$0.put((Object)"minecraft:lava", (Object)11);
        $$0.put((Object)"minecraft:sand", (Object)12);
        $$0.put((Object)"minecraft:gravel", (Object)13);
        $$0.put((Object)"minecraft:gold_ore", (Object)14);
        $$0.put((Object)"minecraft:iron_ore", (Object)15);
        $$0.put((Object)"minecraft:coal_ore", (Object)16);
        $$0.put((Object)"minecraft:log", (Object)17);
        $$0.put((Object)"minecraft:leaves", (Object)18);
        $$0.put((Object)"minecraft:sponge", (Object)19);
        $$0.put((Object)"minecraft:glass", (Object)20);
        $$0.put((Object)"minecraft:lapis_ore", (Object)21);
        $$0.put((Object)"minecraft:lapis_block", (Object)22);
        $$0.put((Object)"minecraft:dispenser", (Object)23);
        $$0.put((Object)"minecraft:sandstone", (Object)24);
        $$0.put((Object)"minecraft:noteblock", (Object)25);
        $$0.put((Object)"minecraft:bed", (Object)26);
        $$0.put((Object)"minecraft:golden_rail", (Object)27);
        $$0.put((Object)"minecraft:detector_rail", (Object)28);
        $$0.put((Object)"minecraft:sticky_piston", (Object)29);
        $$0.put((Object)"minecraft:web", (Object)30);
        $$0.put((Object)"minecraft:tallgrass", (Object)31);
        $$0.put((Object)"minecraft:deadbush", (Object)32);
        $$0.put((Object)"minecraft:piston", (Object)33);
        $$0.put((Object)"minecraft:piston_head", (Object)34);
        $$0.put((Object)"minecraft:wool", (Object)35);
        $$0.put((Object)"minecraft:piston_extension", (Object)36);
        $$0.put((Object)"minecraft:yellow_flower", (Object)37);
        $$0.put((Object)"minecraft:red_flower", (Object)38);
        $$0.put((Object)"minecraft:brown_mushroom", (Object)39);
        $$0.put((Object)"minecraft:red_mushroom", (Object)40);
        $$0.put((Object)"minecraft:gold_block", (Object)41);
        $$0.put((Object)"minecraft:iron_block", (Object)42);
        $$0.put((Object)"minecraft:double_stone_slab", (Object)43);
        $$0.put((Object)"minecraft:stone_slab", (Object)44);
        $$0.put((Object)"minecraft:brick_block", (Object)45);
        $$0.put((Object)"minecraft:tnt", (Object)46);
        $$0.put((Object)"minecraft:bookshelf", (Object)47);
        $$0.put((Object)"minecraft:mossy_cobblestone", (Object)48);
        $$0.put((Object)"minecraft:obsidian", (Object)49);
        $$0.put((Object)"minecraft:torch", (Object)50);
        $$0.put((Object)"minecraft:fire", (Object)51);
        $$0.put((Object)"minecraft:mob_spawner", (Object)52);
        $$0.put((Object)"minecraft:oak_stairs", (Object)53);
        $$0.put((Object)"minecraft:chest", (Object)54);
        $$0.put((Object)"minecraft:redstone_wire", (Object)55);
        $$0.put((Object)"minecraft:diamond_ore", (Object)56);
        $$0.put((Object)"minecraft:diamond_block", (Object)57);
        $$0.put((Object)"minecraft:crafting_table", (Object)58);
        $$0.put((Object)"minecraft:wheat", (Object)59);
        $$0.put((Object)"minecraft:farmland", (Object)60);
        $$0.put((Object)"minecraft:furnace", (Object)61);
        $$0.put((Object)"minecraft:lit_furnace", (Object)62);
        $$0.put((Object)"minecraft:standing_sign", (Object)63);
        $$0.put((Object)"minecraft:wooden_door", (Object)64);
        $$0.put((Object)"minecraft:ladder", (Object)65);
        $$0.put((Object)"minecraft:rail", (Object)66);
        $$0.put((Object)"minecraft:stone_stairs", (Object)67);
        $$0.put((Object)"minecraft:wall_sign", (Object)68);
        $$0.put((Object)"minecraft:lever", (Object)69);
        $$0.put((Object)"minecraft:stone_pressure_plate", (Object)70);
        $$0.put((Object)"minecraft:iron_door", (Object)71);
        $$0.put((Object)"minecraft:wooden_pressure_plate", (Object)72);
        $$0.put((Object)"minecraft:redstone_ore", (Object)73);
        $$0.put((Object)"minecraft:lit_redstone_ore", (Object)74);
        $$0.put((Object)"minecraft:unlit_redstone_torch", (Object)75);
        $$0.put((Object)"minecraft:redstone_torch", (Object)76);
        $$0.put((Object)"minecraft:stone_button", (Object)77);
        $$0.put((Object)"minecraft:snow_layer", (Object)78);
        $$0.put((Object)"minecraft:ice", (Object)79);
        $$0.put((Object)"minecraft:snow", (Object)80);
        $$0.put((Object)"minecraft:cactus", (Object)81);
        $$0.put((Object)"minecraft:clay", (Object)82);
        $$0.put((Object)"minecraft:reeds", (Object)83);
        $$0.put((Object)"minecraft:jukebox", (Object)84);
        $$0.put((Object)"minecraft:fence", (Object)85);
        $$0.put((Object)"minecraft:pumpkin", (Object)86);
        $$0.put((Object)"minecraft:netherrack", (Object)87);
        $$0.put((Object)"minecraft:soul_sand", (Object)88);
        $$0.put((Object)"minecraft:glowstone", (Object)89);
        $$0.put((Object)"minecraft:portal", (Object)90);
        $$0.put((Object)"minecraft:lit_pumpkin", (Object)91);
        $$0.put((Object)"minecraft:cake", (Object)92);
        $$0.put((Object)"minecraft:unpowered_repeater", (Object)93);
        $$0.put((Object)"minecraft:powered_repeater", (Object)94);
        $$0.put((Object)"minecraft:stained_glass", (Object)95);
        $$0.put((Object)"minecraft:trapdoor", (Object)96);
        $$0.put((Object)"minecraft:monster_egg", (Object)97);
        $$0.put((Object)"minecraft:stonebrick", (Object)98);
        $$0.put((Object)"minecraft:brown_mushroom_block", (Object)99);
        $$0.put((Object)"minecraft:red_mushroom_block", (Object)100);
        $$0.put((Object)"minecraft:iron_bars", (Object)101);
        $$0.put((Object)"minecraft:glass_pane", (Object)102);
        $$0.put((Object)"minecraft:melon_block", (Object)103);
        $$0.put((Object)"minecraft:pumpkin_stem", (Object)104);
        $$0.put((Object)"minecraft:melon_stem", (Object)105);
        $$0.put((Object)"minecraft:vine", (Object)106);
        $$0.put((Object)"minecraft:fence_gate", (Object)107);
        $$0.put((Object)"minecraft:brick_stairs", (Object)108);
        $$0.put((Object)"minecraft:stone_brick_stairs", (Object)109);
        $$0.put((Object)"minecraft:mycelium", (Object)110);
        $$0.put((Object)"minecraft:waterlily", (Object)111);
        $$0.put((Object)"minecraft:nether_brick", (Object)112);
        $$0.put((Object)"minecraft:nether_brick_fence", (Object)113);
        $$0.put((Object)"minecraft:nether_brick_stairs", (Object)114);
        $$0.put((Object)"minecraft:nether_wart", (Object)115);
        $$0.put((Object)"minecraft:enchanting_table", (Object)116);
        $$0.put((Object)"minecraft:brewing_stand", (Object)117);
        $$0.put((Object)"minecraft:cauldron", (Object)118);
        $$0.put((Object)"minecraft:end_portal", (Object)119);
        $$0.put((Object)"minecraft:end_portal_frame", (Object)120);
        $$0.put((Object)"minecraft:end_stone", (Object)121);
        $$0.put((Object)"minecraft:dragon_egg", (Object)122);
        $$0.put((Object)"minecraft:redstone_lamp", (Object)123);
        $$0.put((Object)"minecraft:lit_redstone_lamp", (Object)124);
        $$0.put((Object)"minecraft:double_wooden_slab", (Object)125);
        $$0.put((Object)"minecraft:wooden_slab", (Object)126);
        $$0.put((Object)"minecraft:cocoa", (Object)127);
        $$0.put((Object)"minecraft:sandstone_stairs", (Object)128);
        $$0.put((Object)"minecraft:emerald_ore", (Object)129);
        $$0.put((Object)"minecraft:ender_chest", (Object)130);
        $$0.put((Object)"minecraft:tripwire_hook", (Object)131);
        $$0.put((Object)"minecraft:tripwire", (Object)132);
        $$0.put((Object)"minecraft:emerald_block", (Object)133);
        $$0.put((Object)"minecraft:spruce_stairs", (Object)134);
        $$0.put((Object)"minecraft:birch_stairs", (Object)135);
        $$0.put((Object)"minecraft:jungle_stairs", (Object)136);
        $$0.put((Object)"minecraft:command_block", (Object)137);
        $$0.put((Object)"minecraft:beacon", (Object)138);
        $$0.put((Object)"minecraft:cobblestone_wall", (Object)139);
        $$0.put((Object)"minecraft:flower_pot", (Object)140);
        $$0.put((Object)"minecraft:carrots", (Object)141);
        $$0.put((Object)"minecraft:potatoes", (Object)142);
        $$0.put((Object)"minecraft:wooden_button", (Object)143);
        $$0.put((Object)"minecraft:skull", (Object)144);
        $$0.put((Object)"minecraft:anvil", (Object)145);
        $$0.put((Object)"minecraft:trapped_chest", (Object)146);
        $$0.put((Object)"minecraft:light_weighted_pressure_plate", (Object)147);
        $$0.put((Object)"minecraft:heavy_weighted_pressure_plate", (Object)148);
        $$0.put((Object)"minecraft:unpowered_comparator", (Object)149);
        $$0.put((Object)"minecraft:powered_comparator", (Object)150);
        $$0.put((Object)"minecraft:daylight_detector", (Object)151);
        $$0.put((Object)"minecraft:redstone_block", (Object)152);
        $$0.put((Object)"minecraft:quartz_ore", (Object)153);
        $$0.put((Object)"minecraft:hopper", (Object)154);
        $$0.put((Object)"minecraft:quartz_block", (Object)155);
        $$0.put((Object)"minecraft:quartz_stairs", (Object)156);
        $$0.put((Object)"minecraft:activator_rail", (Object)157);
        $$0.put((Object)"minecraft:dropper", (Object)158);
        $$0.put((Object)"minecraft:stained_hardened_clay", (Object)159);
        $$0.put((Object)"minecraft:stained_glass_pane", (Object)160);
        $$0.put((Object)"minecraft:leaves2", (Object)161);
        $$0.put((Object)"minecraft:log2", (Object)162);
        $$0.put((Object)"minecraft:acacia_stairs", (Object)163);
        $$0.put((Object)"minecraft:dark_oak_stairs", (Object)164);
        $$0.put((Object)"minecraft:slime", (Object)165);
        $$0.put((Object)"minecraft:barrier", (Object)166);
        $$0.put((Object)"minecraft:iron_trapdoor", (Object)167);
        $$0.put((Object)"minecraft:prismarine", (Object)168);
        $$0.put((Object)"minecraft:sea_lantern", (Object)169);
        $$0.put((Object)"minecraft:hay_block", (Object)170);
        $$0.put((Object)"minecraft:carpet", (Object)171);
        $$0.put((Object)"minecraft:hardened_clay", (Object)172);
        $$0.put((Object)"minecraft:coal_block", (Object)173);
        $$0.put((Object)"minecraft:packed_ice", (Object)174);
        $$0.put((Object)"minecraft:double_plant", (Object)175);
        $$0.put((Object)"minecraft:standing_banner", (Object)176);
        $$0.put((Object)"minecraft:wall_banner", (Object)177);
        $$0.put((Object)"minecraft:daylight_detector_inverted", (Object)178);
        $$0.put((Object)"minecraft:red_sandstone", (Object)179);
        $$0.put((Object)"minecraft:red_sandstone_stairs", (Object)180);
        $$0.put((Object)"minecraft:double_stone_slab2", (Object)181);
        $$0.put((Object)"minecraft:stone_slab2", (Object)182);
        $$0.put((Object)"minecraft:spruce_fence_gate", (Object)183);
        $$0.put((Object)"minecraft:birch_fence_gate", (Object)184);
        $$0.put((Object)"minecraft:jungle_fence_gate", (Object)185);
        $$0.put((Object)"minecraft:dark_oak_fence_gate", (Object)186);
        $$0.put((Object)"minecraft:acacia_fence_gate", (Object)187);
        $$0.put((Object)"minecraft:spruce_fence", (Object)188);
        $$0.put((Object)"minecraft:birch_fence", (Object)189);
        $$0.put((Object)"minecraft:jungle_fence", (Object)190);
        $$0.put((Object)"minecraft:dark_oak_fence", (Object)191);
        $$0.put((Object)"minecraft:acacia_fence", (Object)192);
        $$0.put((Object)"minecraft:spruce_door", (Object)193);
        $$0.put((Object)"minecraft:birch_door", (Object)194);
        $$0.put((Object)"minecraft:jungle_door", (Object)195);
        $$0.put((Object)"minecraft:acacia_door", (Object)196);
        $$0.put((Object)"minecraft:dark_oak_door", (Object)197);
        $$0.put((Object)"minecraft:end_rod", (Object)198);
        $$0.put((Object)"minecraft:chorus_plant", (Object)199);
        $$0.put((Object)"minecraft:chorus_flower", (Object)200);
        $$0.put((Object)"minecraft:purpur_block", (Object)201);
        $$0.put((Object)"minecraft:purpur_pillar", (Object)202);
        $$0.put((Object)"minecraft:purpur_stairs", (Object)203);
        $$0.put((Object)"minecraft:purpur_double_slab", (Object)204);
        $$0.put((Object)"minecraft:purpur_slab", (Object)205);
        $$0.put((Object)"minecraft:end_bricks", (Object)206);
        $$0.put((Object)"minecraft:beetroots", (Object)207);
        $$0.put((Object)"minecraft:grass_path", (Object)208);
        $$0.put((Object)"minecraft:end_gateway", (Object)209);
        $$0.put((Object)"minecraft:repeating_command_block", (Object)210);
        $$0.put((Object)"minecraft:chain_command_block", (Object)211);
        $$0.put((Object)"minecraft:frosted_ice", (Object)212);
        $$0.put((Object)"minecraft:magma", (Object)213);
        $$0.put((Object)"minecraft:nether_wart_block", (Object)214);
        $$0.put((Object)"minecraft:red_nether_brick", (Object)215);
        $$0.put((Object)"minecraft:bone_block", (Object)216);
        $$0.put((Object)"minecraft:structure_void", (Object)217);
        $$0.put((Object)"minecraft:observer", (Object)218);
        $$0.put((Object)"minecraft:white_shulker_box", (Object)219);
        $$0.put((Object)"minecraft:orange_shulker_box", (Object)220);
        $$0.put((Object)"minecraft:magenta_shulker_box", (Object)221);
        $$0.put((Object)"minecraft:light_blue_shulker_box", (Object)222);
        $$0.put((Object)"minecraft:yellow_shulker_box", (Object)223);
        $$0.put((Object)"minecraft:lime_shulker_box", (Object)224);
        $$0.put((Object)"minecraft:pink_shulker_box", (Object)225);
        $$0.put((Object)"minecraft:gray_shulker_box", (Object)226);
        $$0.put((Object)"minecraft:silver_shulker_box", (Object)227);
        $$0.put((Object)"minecraft:cyan_shulker_box", (Object)228);
        $$0.put((Object)"minecraft:purple_shulker_box", (Object)229);
        $$0.put((Object)"minecraft:blue_shulker_box", (Object)230);
        $$0.put((Object)"minecraft:brown_shulker_box", (Object)231);
        $$0.put((Object)"minecraft:green_shulker_box", (Object)232);
        $$0.put((Object)"minecraft:red_shulker_box", (Object)233);
        $$0.put((Object)"minecraft:black_shulker_box", (Object)234);
        $$0.put((Object)"minecraft:white_glazed_terracotta", (Object)235);
        $$0.put((Object)"minecraft:orange_glazed_terracotta", (Object)236);
        $$0.put((Object)"minecraft:magenta_glazed_terracotta", (Object)237);
        $$0.put((Object)"minecraft:light_blue_glazed_terracotta", (Object)238);
        $$0.put((Object)"minecraft:yellow_glazed_terracotta", (Object)239);
        $$0.put((Object)"minecraft:lime_glazed_terracotta", (Object)240);
        $$0.put((Object)"minecraft:pink_glazed_terracotta", (Object)241);
        $$0.put((Object)"minecraft:gray_glazed_terracotta", (Object)242);
        $$0.put((Object)"minecraft:silver_glazed_terracotta", (Object)243);
        $$0.put((Object)"minecraft:cyan_glazed_terracotta", (Object)244);
        $$0.put((Object)"minecraft:purple_glazed_terracotta", (Object)245);
        $$0.put((Object)"minecraft:blue_glazed_terracotta", (Object)246);
        $$0.put((Object)"minecraft:brown_glazed_terracotta", (Object)247);
        $$0.put((Object)"minecraft:green_glazed_terracotta", (Object)248);
        $$0.put((Object)"minecraft:red_glazed_terracotta", (Object)249);
        $$0.put((Object)"minecraft:black_glazed_terracotta", (Object)250);
        $$0.put((Object)"minecraft:concrete", (Object)251);
        $$0.put((Object)"minecraft:concrete_powder", (Object)252);
        $$0.put((Object)"minecraft:structure_block", (Object)255);
    });

    public EntityBlockStateFix(Schema $$0, boolean $$1) {
        super($$0, $$1);
    }

    public static int getBlockId(String $$0) {
        Integer $$1 = (Integer)MAP.get((Object)$$0);
        return $$1 == null ? 0 : $$1;
    }

    public TypeRewriteRule makeRule() {
        Schema $$02 = this.getInputSchema();
        Schema $$12 = this.getOutputSchema();
        Function $$2 = $$0 -> this.updateBlockToBlockState((Typed<?>)$$0, "DisplayTile", "DisplayData", "DisplayState");
        Function $$32 = $$0 -> this.updateBlockToBlockState((Typed<?>)$$0, "inTile", "inData", "inBlockState");
        Type $$4 = DSL.and((Type)DSL.optional((Type)DSL.field((String)"inTile", (Type)DSL.named((String)References.BLOCK_NAME.typeName(), (Type)DSL.or((Type)DSL.intType(), NamespacedSchema.namespacedString())))), (Type)DSL.remainderType());
        Function $$5 = $$1 -> $$1.update($$4.finder(), DSL.remainderType(), Pair::getSecond);
        return this.fixTypeEverywhereTyped("EntityBlockStateFix", $$02.getType(References.ENTITY), $$12.getType(References.ENTITY), $$3 -> {
            $$3 = this.updateEntity((Typed<?>)$$3, "minecraft:falling_block", this::updateFallingBlock);
            $$3 = this.updateEntity((Typed<?>)$$3, "minecraft:enderman", $$0 -> this.updateBlockToBlockState((Typed<?>)$$0, "carried", "carriedData", "carriedBlockState"));
            $$3 = this.updateEntity((Typed<?>)$$3, "minecraft:arrow", (Function<Typed<?>, Typed<?>>)$$32);
            $$3 = this.updateEntity((Typed<?>)$$3, "minecraft:spectral_arrow", (Function<Typed<?>, Typed<?>>)$$32);
            $$3 = this.updateEntity((Typed<?>)$$3, "minecraft:egg", (Function<Typed<?>, Typed<?>>)$$5);
            $$3 = this.updateEntity((Typed<?>)$$3, "minecraft:ender_pearl", (Function<Typed<?>, Typed<?>>)$$5);
            $$3 = this.updateEntity((Typed<?>)$$3, "minecraft:fireball", (Function<Typed<?>, Typed<?>>)$$5);
            $$3 = this.updateEntity((Typed<?>)$$3, "minecraft:potion", (Function<Typed<?>, Typed<?>>)$$5);
            $$3 = this.updateEntity((Typed<?>)$$3, "minecraft:small_fireball", (Function<Typed<?>, Typed<?>>)$$5);
            $$3 = this.updateEntity((Typed<?>)$$3, "minecraft:snowball", (Function<Typed<?>, Typed<?>>)$$5);
            $$3 = this.updateEntity((Typed<?>)$$3, "minecraft:wither_skull", (Function<Typed<?>, Typed<?>>)$$5);
            $$3 = this.updateEntity((Typed<?>)$$3, "minecraft:xp_bottle", (Function<Typed<?>, Typed<?>>)$$5);
            $$3 = this.updateEntity((Typed<?>)$$3, "minecraft:commandblock_minecart", (Function<Typed<?>, Typed<?>>)$$2);
            $$3 = this.updateEntity((Typed<?>)$$3, "minecraft:minecart", (Function<Typed<?>, Typed<?>>)$$2);
            $$3 = this.updateEntity((Typed<?>)$$3, "minecraft:chest_minecart", (Function<Typed<?>, Typed<?>>)$$2);
            $$3 = this.updateEntity((Typed<?>)$$3, "minecraft:furnace_minecart", (Function<Typed<?>, Typed<?>>)$$2);
            $$3 = this.updateEntity((Typed<?>)$$3, "minecraft:tnt_minecart", (Function<Typed<?>, Typed<?>>)$$2);
            $$3 = this.updateEntity((Typed<?>)$$3, "minecraft:hopper_minecart", (Function<Typed<?>, Typed<?>>)$$2);
            $$3 = this.updateEntity((Typed<?>)$$3, "minecraft:spawner_minecart", (Function<Typed<?>, Typed<?>>)$$2);
            return $$3;
        });
    }

    private Typed<?> updateFallingBlock(Typed<?> $$0) {
        Type $$1 = DSL.optional((Type)DSL.field((String)"Block", (Type)DSL.named((String)References.BLOCK_NAME.typeName(), (Type)DSL.or((Type)DSL.intType(), NamespacedSchema.namespacedString()))));
        Type $$2 = DSL.optional((Type)DSL.field((String)"BlockState", (Type)DSL.named((String)References.BLOCK_STATE.typeName(), (Type)DSL.remainderType())));
        Dynamic $$3 = (Dynamic)$$0.get(DSL.remainderFinder());
        return $$0.update($$1.finder(), $$2, $$12 -> {
            int $$2 = (Integer)$$12.map($$02 -> (Integer)((Either)$$02.getSecond()).map($$0 -> $$0, EntityBlockStateFix::getBlockId), $$1 -> {
                Optional $$2 = $$3.get("TileID").asNumber().result();
                return (Integer)$$2.map(Number::intValue).orElseGet(() -> $$3.get("Tile").asByte((byte)0) & 0xFF);
            });
            int $$3 = $$3.get("Data").asInt(0) & 0xF;
            return Either.left((Object)Pair.of((Object)References.BLOCK_STATE.typeName(), BlockStateData.getTag($$2 << 4 | $$3)));
        }).set(DSL.remainderFinder(), (Object)$$3.remove("Data").remove("TileID").remove("Tile"));
    }

    private Typed<?> updateBlockToBlockState(Typed<?> $$0, String $$1, String $$22, String $$3) {
        Tag.TagType $$4 = DSL.field((String)$$1, (Type)DSL.named((String)References.BLOCK_NAME.typeName(), (Type)DSL.or((Type)DSL.intType(), NamespacedSchema.namespacedString())));
        Tag.TagType $$5 = DSL.field((String)$$3, (Type)DSL.named((String)References.BLOCK_STATE.typeName(), (Type)DSL.remainderType()));
        Dynamic $$6 = (Dynamic)$$0.getOrCreate(DSL.remainderFinder());
        return $$0.update($$4.finder(), (Type)$$5, $$2 -> {
            int $$3 = (Integer)((Either)$$2.getSecond()).map($$0 -> $$0, EntityBlockStateFix::getBlockId);
            int $$4 = $$6.get($$22).asInt(0) & 0xF;
            return Pair.of((Object)References.BLOCK_STATE.typeName(), BlockStateData.getTag($$3 << 4 | $$4));
        }).set(DSL.remainderFinder(), (Object)$$6.remove($$22));
    }

    private Typed<?> updateEntity(Typed<?> $$0, String $$1, Function<Typed<?>, Typed<?>> $$2) {
        Type $$3 = this.getInputSchema().getChoiceType(References.ENTITY, $$1);
        Type $$4 = this.getOutputSchema().getChoiceType(References.ENTITY, $$1);
        return $$0.updateTyped(DSL.namedChoice((String)$$1, (Type)$$3), $$4, $$2);
    }
}