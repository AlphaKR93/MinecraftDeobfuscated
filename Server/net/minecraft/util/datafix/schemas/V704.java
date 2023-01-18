/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DSL$TypeReference
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.types.templates.Hook$HookFunction
 *  com.mojang.datafixers.types.templates.TypeTemplate
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  java.lang.Object
 *  java.lang.String
 *  java.util.HashMap
 *  java.util.Map
 *  java.util.Objects
 *  java.util.function.Supplier
 */
package net.minecraft.util.datafix.schemas;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.Hook;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.NamespacedSchema;
import net.minecraft.util.datafix.schemas.V99;

public class V704
extends Schema {
    protected static final Map<String, String> ITEM_TO_BLOCKENTITY = (Map)DataFixUtils.make(() -> {
        HashMap $$0 = Maps.newHashMap();
        $$0.put((Object)"minecraft:furnace", (Object)"minecraft:furnace");
        $$0.put((Object)"minecraft:lit_furnace", (Object)"minecraft:furnace");
        $$0.put((Object)"minecraft:chest", (Object)"minecraft:chest");
        $$0.put((Object)"minecraft:trapped_chest", (Object)"minecraft:chest");
        $$0.put((Object)"minecraft:ender_chest", (Object)"minecraft:ender_chest");
        $$0.put((Object)"minecraft:jukebox", (Object)"minecraft:jukebox");
        $$0.put((Object)"minecraft:dispenser", (Object)"minecraft:dispenser");
        $$0.put((Object)"minecraft:dropper", (Object)"minecraft:dropper");
        $$0.put((Object)"minecraft:sign", (Object)"minecraft:sign");
        $$0.put((Object)"minecraft:mob_spawner", (Object)"minecraft:mob_spawner");
        $$0.put((Object)"minecraft:spawner", (Object)"minecraft:mob_spawner");
        $$0.put((Object)"minecraft:noteblock", (Object)"minecraft:noteblock");
        $$0.put((Object)"minecraft:brewing_stand", (Object)"minecraft:brewing_stand");
        $$0.put((Object)"minecraft:enhanting_table", (Object)"minecraft:enchanting_table");
        $$0.put((Object)"minecraft:command_block", (Object)"minecraft:command_block");
        $$0.put((Object)"minecraft:beacon", (Object)"minecraft:beacon");
        $$0.put((Object)"minecraft:skull", (Object)"minecraft:skull");
        $$0.put((Object)"minecraft:daylight_detector", (Object)"minecraft:daylight_detector");
        $$0.put((Object)"minecraft:hopper", (Object)"minecraft:hopper");
        $$0.put((Object)"minecraft:banner", (Object)"minecraft:banner");
        $$0.put((Object)"minecraft:flower_pot", (Object)"minecraft:flower_pot");
        $$0.put((Object)"minecraft:repeating_command_block", (Object)"minecraft:command_block");
        $$0.put((Object)"minecraft:chain_command_block", (Object)"minecraft:command_block");
        $$0.put((Object)"minecraft:shulker_box", (Object)"minecraft:shulker_box");
        $$0.put((Object)"minecraft:white_shulker_box", (Object)"minecraft:shulker_box");
        $$0.put((Object)"minecraft:orange_shulker_box", (Object)"minecraft:shulker_box");
        $$0.put((Object)"minecraft:magenta_shulker_box", (Object)"minecraft:shulker_box");
        $$0.put((Object)"minecraft:light_blue_shulker_box", (Object)"minecraft:shulker_box");
        $$0.put((Object)"minecraft:yellow_shulker_box", (Object)"minecraft:shulker_box");
        $$0.put((Object)"minecraft:lime_shulker_box", (Object)"minecraft:shulker_box");
        $$0.put((Object)"minecraft:pink_shulker_box", (Object)"minecraft:shulker_box");
        $$0.put((Object)"minecraft:gray_shulker_box", (Object)"minecraft:shulker_box");
        $$0.put((Object)"minecraft:silver_shulker_box", (Object)"minecraft:shulker_box");
        $$0.put((Object)"minecraft:cyan_shulker_box", (Object)"minecraft:shulker_box");
        $$0.put((Object)"minecraft:purple_shulker_box", (Object)"minecraft:shulker_box");
        $$0.put((Object)"minecraft:blue_shulker_box", (Object)"minecraft:shulker_box");
        $$0.put((Object)"minecraft:brown_shulker_box", (Object)"minecraft:shulker_box");
        $$0.put((Object)"minecraft:green_shulker_box", (Object)"minecraft:shulker_box");
        $$0.put((Object)"minecraft:red_shulker_box", (Object)"minecraft:shulker_box");
        $$0.put((Object)"minecraft:black_shulker_box", (Object)"minecraft:shulker_box");
        $$0.put((Object)"minecraft:bed", (Object)"minecraft:bed");
        $$0.put((Object)"minecraft:light_gray_shulker_box", (Object)"minecraft:shulker_box");
        $$0.put((Object)"minecraft:banner", (Object)"minecraft:banner");
        $$0.put((Object)"minecraft:white_banner", (Object)"minecraft:banner");
        $$0.put((Object)"minecraft:orange_banner", (Object)"minecraft:banner");
        $$0.put((Object)"minecraft:magenta_banner", (Object)"minecraft:banner");
        $$0.put((Object)"minecraft:light_blue_banner", (Object)"minecraft:banner");
        $$0.put((Object)"minecraft:yellow_banner", (Object)"minecraft:banner");
        $$0.put((Object)"minecraft:lime_banner", (Object)"minecraft:banner");
        $$0.put((Object)"minecraft:pink_banner", (Object)"minecraft:banner");
        $$0.put((Object)"minecraft:gray_banner", (Object)"minecraft:banner");
        $$0.put((Object)"minecraft:silver_banner", (Object)"minecraft:banner");
        $$0.put((Object)"minecraft:light_gray_banner", (Object)"minecraft:banner");
        $$0.put((Object)"minecraft:cyan_banner", (Object)"minecraft:banner");
        $$0.put((Object)"minecraft:purple_banner", (Object)"minecraft:banner");
        $$0.put((Object)"minecraft:blue_banner", (Object)"minecraft:banner");
        $$0.put((Object)"minecraft:brown_banner", (Object)"minecraft:banner");
        $$0.put((Object)"minecraft:green_banner", (Object)"minecraft:banner");
        $$0.put((Object)"minecraft:red_banner", (Object)"minecraft:banner");
        $$0.put((Object)"minecraft:black_banner", (Object)"minecraft:banner");
        $$0.put((Object)"minecraft:standing_sign", (Object)"minecraft:sign");
        $$0.put((Object)"minecraft:wall_sign", (Object)"minecraft:sign");
        $$0.put((Object)"minecraft:piston_head", (Object)"minecraft:piston");
        $$0.put((Object)"minecraft:daylight_detector_inverted", (Object)"minecraft:daylight_detector");
        $$0.put((Object)"minecraft:unpowered_comparator", (Object)"minecraft:comparator");
        $$0.put((Object)"minecraft:powered_comparator", (Object)"minecraft:comparator");
        $$0.put((Object)"minecraft:wall_banner", (Object)"minecraft:banner");
        $$0.put((Object)"minecraft:standing_banner", (Object)"minecraft:banner");
        $$0.put((Object)"minecraft:structure_block", (Object)"minecraft:structure_block");
        $$0.put((Object)"minecraft:end_portal", (Object)"minecraft:end_portal");
        $$0.put((Object)"minecraft:end_gateway", (Object)"minecraft:end_gateway");
        $$0.put((Object)"minecraft:sign", (Object)"minecraft:sign");
        $$0.put((Object)"minecraft:shield", (Object)"minecraft:banner");
        $$0.put((Object)"minecraft:white_bed", (Object)"minecraft:bed");
        $$0.put((Object)"minecraft:orange_bed", (Object)"minecraft:bed");
        $$0.put((Object)"minecraft:magenta_bed", (Object)"minecraft:bed");
        $$0.put((Object)"minecraft:light_blue_bed", (Object)"minecraft:bed");
        $$0.put((Object)"minecraft:yellow_bed", (Object)"minecraft:bed");
        $$0.put((Object)"minecraft:lime_bed", (Object)"minecraft:bed");
        $$0.put((Object)"minecraft:pink_bed", (Object)"minecraft:bed");
        $$0.put((Object)"minecraft:gray_bed", (Object)"minecraft:bed");
        $$0.put((Object)"minecraft:silver_bed", (Object)"minecraft:bed");
        $$0.put((Object)"minecraft:light_gray_bed", (Object)"minecraft:bed");
        $$0.put((Object)"minecraft:cyan_bed", (Object)"minecraft:bed");
        $$0.put((Object)"minecraft:purple_bed", (Object)"minecraft:bed");
        $$0.put((Object)"minecraft:blue_bed", (Object)"minecraft:bed");
        $$0.put((Object)"minecraft:brown_bed", (Object)"minecraft:bed");
        $$0.put((Object)"minecraft:green_bed", (Object)"minecraft:bed");
        $$0.put((Object)"minecraft:red_bed", (Object)"minecraft:bed");
        $$0.put((Object)"minecraft:black_bed", (Object)"minecraft:bed");
        $$0.put((Object)"minecraft:oak_sign", (Object)"minecraft:sign");
        $$0.put((Object)"minecraft:spruce_sign", (Object)"minecraft:sign");
        $$0.put((Object)"minecraft:birch_sign", (Object)"minecraft:sign");
        $$0.put((Object)"minecraft:jungle_sign", (Object)"minecraft:sign");
        $$0.put((Object)"minecraft:acacia_sign", (Object)"minecraft:sign");
        $$0.put((Object)"minecraft:dark_oak_sign", (Object)"minecraft:sign");
        $$0.put((Object)"minecraft:crimson_sign", (Object)"minecraft:sign");
        $$0.put((Object)"minecraft:warped_sign", (Object)"minecraft:sign");
        $$0.put((Object)"minecraft:skeleton_skull", (Object)"minecraft:skull");
        $$0.put((Object)"minecraft:wither_skeleton_skull", (Object)"minecraft:skull");
        $$0.put((Object)"minecraft:zombie_head", (Object)"minecraft:skull");
        $$0.put((Object)"minecraft:player_head", (Object)"minecraft:skull");
        $$0.put((Object)"minecraft:creeper_head", (Object)"minecraft:skull");
        $$0.put((Object)"minecraft:dragon_head", (Object)"minecraft:skull");
        $$0.put((Object)"minecraft:barrel", (Object)"minecraft:barrel");
        $$0.put((Object)"minecraft:conduit", (Object)"minecraft:conduit");
        $$0.put((Object)"minecraft:smoker", (Object)"minecraft:smoker");
        $$0.put((Object)"minecraft:blast_furnace", (Object)"minecraft:blast_furnace");
        $$0.put((Object)"minecraft:lectern", (Object)"minecraft:lectern");
        $$0.put((Object)"minecraft:bell", (Object)"minecraft:bell");
        $$0.put((Object)"minecraft:jigsaw", (Object)"minecraft:jigsaw");
        $$0.put((Object)"minecraft:campfire", (Object)"minecraft:campfire");
        $$0.put((Object)"minecraft:bee_nest", (Object)"minecraft:beehive");
        $$0.put((Object)"minecraft:beehive", (Object)"minecraft:beehive");
        $$0.put((Object)"minecraft:sculk_sensor", (Object)"minecraft:sculk_sensor");
        return ImmutableMap.copyOf((Map)$$0);
    });
    protected static final Hook.HookFunction ADD_NAMES = new Hook.HookFunction(){

        public <T> T apply(DynamicOps<T> $$0, T $$1) {
            return V99.addNames(new Dynamic($$0, $$1), ITEM_TO_BLOCKENTITY, "ArmorStand");
        }
    };

    public V704(int $$0, Schema $$1) {
        super($$0, $$1);
    }

    protected static void registerInventory(Schema $$0, Map<String, Supplier<TypeTemplate>> $$1, String $$2) {
        $$0.register($$1, $$2, () -> DSL.optionalFields((String)"Items", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in($$0))));
    }

    public Type<?> getChoiceType(DSL.TypeReference $$0, String $$1) {
        if (Objects.equals((Object)$$0.typeName(), (Object)References.BLOCK_ENTITY.typeName())) {
            return super.getChoiceType($$0, NamespacedSchema.ensureNamespaced($$1));
        }
        return super.getChoiceType($$0, $$1);
    }

    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema $$0) {
        HashMap $$12 = Maps.newHashMap();
        V704.registerInventory($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:furnace");
        V704.registerInventory($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:chest");
        $$0.registerSimple((Map)$$12, "minecraft:ender_chest");
        $$0.register((Map)$$12, "minecraft:jukebox", $$1 -> DSL.optionalFields((String)"RecordItem", (TypeTemplate)References.ITEM_STACK.in($$0)));
        V704.registerInventory($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:dispenser");
        V704.registerInventory($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:dropper");
        $$0.registerSimple((Map)$$12, "minecraft:sign");
        $$0.register((Map)$$12, "minecraft:mob_spawner", $$1 -> References.UNTAGGED_SPAWNER.in($$0));
        $$0.registerSimple((Map)$$12, "minecraft:noteblock");
        $$0.registerSimple((Map)$$12, "minecraft:piston");
        V704.registerInventory($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:brewing_stand");
        $$0.registerSimple((Map)$$12, "minecraft:enchanting_table");
        $$0.registerSimple((Map)$$12, "minecraft:end_portal");
        $$0.registerSimple((Map)$$12, "minecraft:beacon");
        $$0.registerSimple((Map)$$12, "minecraft:skull");
        $$0.registerSimple((Map)$$12, "minecraft:daylight_detector");
        V704.registerInventory($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:hopper");
        $$0.registerSimple((Map)$$12, "minecraft:comparator");
        $$0.register((Map)$$12, "minecraft:flower_pot", $$1 -> DSL.optionalFields((String)"Item", (TypeTemplate)DSL.or((TypeTemplate)DSL.constType((Type)DSL.intType()), (TypeTemplate)References.ITEM_NAME.in($$0))));
        $$0.registerSimple((Map)$$12, "minecraft:banner");
        $$0.registerSimple((Map)$$12, "minecraft:structure_block");
        $$0.registerSimple((Map)$$12, "minecraft:end_gateway");
        $$0.registerSimple((Map)$$12, "minecraft:command_block");
        return $$12;
    }

    public void registerTypes(Schema $$0, Map<String, Supplier<TypeTemplate>> $$1, Map<String, Supplier<TypeTemplate>> $$2) {
        super.registerTypes($$0, $$1, $$2);
        $$0.registerType(false, References.BLOCK_ENTITY, () -> DSL.taggedChoiceLazy((String)"id", NamespacedSchema.namespacedString(), (Map)$$2));
        $$0.registerType(true, References.ITEM_STACK, () -> DSL.hook((TypeTemplate)DSL.optionalFields((String)"id", (TypeTemplate)References.ITEM_NAME.in($$0), (String)"tag", (TypeTemplate)DSL.optionalFields((String)"EntityTag", (TypeTemplate)References.ENTITY_TREE.in($$0), (String)"BlockEntityTag", (TypeTemplate)References.BLOCK_ENTITY.in($$0), (String)"CanDestroy", (TypeTemplate)DSL.list((TypeTemplate)References.BLOCK_NAME.in($$0)), (String)"CanPlaceOn", (TypeTemplate)DSL.list((TypeTemplate)References.BLOCK_NAME.in($$0)), (String)"Items", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in($$0)))), (Hook.HookFunction)ADD_NAMES, (Hook.HookFunction)Hook.HookFunction.IDENTITY));
    }
}