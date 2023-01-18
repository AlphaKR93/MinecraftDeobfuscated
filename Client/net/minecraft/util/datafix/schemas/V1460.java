/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.types.templates.Hook$HookFunction
 *  com.mojang.datafixers.types.templates.TypeTemplate
 *  java.lang.Object
 *  java.lang.String
 *  java.util.HashMap
 *  java.util.Map
 *  java.util.function.Supplier
 */
package net.minecraft.util.datafix.schemas;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.Hook;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.NamespacedSchema;
import net.minecraft.util.datafix.schemas.V100;
import net.minecraft.util.datafix.schemas.V1451_6;
import net.minecraft.util.datafix.schemas.V705;

public class V1460
extends NamespacedSchema {
    public V1460(int $$0, Schema $$1) {
        super($$0, $$1);
    }

    protected static void registerMob(Schema $$0, Map<String, Supplier<TypeTemplate>> $$1, String $$2) {
        $$0.register($$1, $$2, () -> V100.equipment($$0));
    }

    protected static void registerInventory(Schema $$0, Map<String, Supplier<TypeTemplate>> $$1, String $$2) {
        $$0.register($$1, $$2, () -> DSL.optionalFields((String)"Items", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in($$0))));
    }

    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema $$0) {
        HashMap $$12 = Maps.newHashMap();
        $$0.registerSimple((Map)$$12, "minecraft:area_effect_cloud");
        V1460.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:armor_stand");
        $$0.register((Map)$$12, "minecraft:arrow", $$1 -> DSL.optionalFields((String)"inBlockState", (TypeTemplate)References.BLOCK_STATE.in($$0)));
        V1460.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:bat");
        V1460.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:blaze");
        $$0.registerSimple((Map)$$12, "minecraft:boat");
        V1460.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:cave_spider");
        $$0.register((Map)$$12, "minecraft:chest_minecart", $$1 -> DSL.optionalFields((String)"DisplayState", (TypeTemplate)References.BLOCK_STATE.in($$0), (String)"Items", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in($$0))));
        V1460.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:chicken");
        $$0.register((Map)$$12, "minecraft:commandblock_minecart", $$1 -> DSL.optionalFields((String)"DisplayState", (TypeTemplate)References.BLOCK_STATE.in($$0)));
        V1460.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:cow");
        V1460.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:creeper");
        $$0.register((Map)$$12, "minecraft:donkey", $$1 -> DSL.optionalFields((String)"Items", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in($$0)), (String)"SaddleItem", (TypeTemplate)References.ITEM_STACK.in($$0), (TypeTemplate)V100.equipment($$0)));
        $$0.registerSimple((Map)$$12, "minecraft:dragon_fireball");
        $$0.registerSimple((Map)$$12, "minecraft:egg");
        V1460.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:elder_guardian");
        $$0.registerSimple((Map)$$12, "minecraft:ender_crystal");
        V1460.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:ender_dragon");
        $$0.register((Map)$$12, "minecraft:enderman", $$1 -> DSL.optionalFields((String)"carriedBlockState", (TypeTemplate)References.BLOCK_STATE.in($$0), (TypeTemplate)V100.equipment($$0)));
        V1460.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:endermite");
        $$0.registerSimple((Map)$$12, "minecraft:ender_pearl");
        $$0.registerSimple((Map)$$12, "minecraft:evocation_fangs");
        V1460.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:evocation_illager");
        $$0.registerSimple((Map)$$12, "minecraft:eye_of_ender_signal");
        $$0.register((Map)$$12, "minecraft:falling_block", $$1 -> DSL.optionalFields((String)"BlockState", (TypeTemplate)References.BLOCK_STATE.in($$0), (String)"TileEntityData", (TypeTemplate)References.BLOCK_ENTITY.in($$0)));
        $$0.registerSimple((Map)$$12, "minecraft:fireball");
        $$0.register((Map)$$12, "minecraft:fireworks_rocket", $$1 -> DSL.optionalFields((String)"FireworksItem", (TypeTemplate)References.ITEM_STACK.in($$0)));
        $$0.register((Map)$$12, "minecraft:furnace_minecart", $$1 -> DSL.optionalFields((String)"DisplayState", (TypeTemplate)References.BLOCK_STATE.in($$0)));
        V1460.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:ghast");
        V1460.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:giant");
        V1460.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:guardian");
        $$0.register((Map)$$12, "minecraft:hopper_minecart", $$1 -> DSL.optionalFields((String)"DisplayState", (TypeTemplate)References.BLOCK_STATE.in($$0), (String)"Items", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in($$0))));
        $$0.register((Map)$$12, "minecraft:horse", $$1 -> DSL.optionalFields((String)"ArmorItem", (TypeTemplate)References.ITEM_STACK.in($$0), (String)"SaddleItem", (TypeTemplate)References.ITEM_STACK.in($$0), (TypeTemplate)V100.equipment($$0)));
        V1460.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:husk");
        $$0.registerSimple((Map)$$12, "minecraft:illusion_illager");
        $$0.register((Map)$$12, "minecraft:item", $$1 -> DSL.optionalFields((String)"Item", (TypeTemplate)References.ITEM_STACK.in($$0)));
        $$0.register((Map)$$12, "minecraft:item_frame", $$1 -> DSL.optionalFields((String)"Item", (TypeTemplate)References.ITEM_STACK.in($$0)));
        $$0.registerSimple((Map)$$12, "minecraft:leash_knot");
        $$0.register((Map)$$12, "minecraft:llama", $$1 -> DSL.optionalFields((String)"Items", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in($$0)), (String)"SaddleItem", (TypeTemplate)References.ITEM_STACK.in($$0), (String)"DecorItem", (TypeTemplate)References.ITEM_STACK.in($$0), (TypeTemplate)V100.equipment($$0)));
        $$0.registerSimple((Map)$$12, "minecraft:llama_spit");
        V1460.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:magma_cube");
        $$0.register((Map)$$12, "minecraft:minecart", $$1 -> DSL.optionalFields((String)"DisplayState", (TypeTemplate)References.BLOCK_STATE.in($$0)));
        V1460.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:mooshroom");
        $$0.register((Map)$$12, "minecraft:mule", $$1 -> DSL.optionalFields((String)"Items", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in($$0)), (String)"SaddleItem", (TypeTemplate)References.ITEM_STACK.in($$0), (TypeTemplate)V100.equipment($$0)));
        V1460.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:ocelot");
        $$0.registerSimple((Map)$$12, "minecraft:painting");
        $$0.registerSimple((Map)$$12, "minecraft:parrot");
        V1460.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:pig");
        V1460.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:polar_bear");
        $$0.register((Map)$$12, "minecraft:potion", $$1 -> DSL.optionalFields((String)"Potion", (TypeTemplate)References.ITEM_STACK.in($$0)));
        V1460.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:rabbit");
        V1460.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:sheep");
        V1460.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:shulker");
        $$0.registerSimple((Map)$$12, "minecraft:shulker_bullet");
        V1460.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:silverfish");
        V1460.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:skeleton");
        $$0.register((Map)$$12, "minecraft:skeleton_horse", $$1 -> DSL.optionalFields((String)"SaddleItem", (TypeTemplate)References.ITEM_STACK.in($$0), (TypeTemplate)V100.equipment($$0)));
        V1460.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:slime");
        $$0.registerSimple((Map)$$12, "minecraft:small_fireball");
        $$0.registerSimple((Map)$$12, "minecraft:snowball");
        V1460.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:snowman");
        $$0.register((Map)$$12, "minecraft:spawner_minecart", $$1 -> DSL.optionalFields((String)"DisplayState", (TypeTemplate)References.BLOCK_STATE.in($$0), (TypeTemplate)References.UNTAGGED_SPAWNER.in($$0)));
        $$0.register((Map)$$12, "minecraft:spectral_arrow", $$1 -> DSL.optionalFields((String)"inBlockState", (TypeTemplate)References.BLOCK_STATE.in($$0)));
        V1460.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:spider");
        V1460.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:squid");
        V1460.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:stray");
        $$0.registerSimple((Map)$$12, "minecraft:tnt");
        $$0.register((Map)$$12, "minecraft:tnt_minecart", $$1 -> DSL.optionalFields((String)"DisplayState", (TypeTemplate)References.BLOCK_STATE.in($$0)));
        V1460.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:vex");
        $$0.register((Map)$$12, "minecraft:villager", $$1 -> DSL.optionalFields((String)"Inventory", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in($$0)), (String)"Offers", (TypeTemplate)DSL.optionalFields((String)"Recipes", (TypeTemplate)DSL.list((TypeTemplate)DSL.optionalFields((String)"buy", (TypeTemplate)References.ITEM_STACK.in($$0), (String)"buyB", (TypeTemplate)References.ITEM_STACK.in($$0), (String)"sell", (TypeTemplate)References.ITEM_STACK.in($$0)))), (TypeTemplate)V100.equipment($$0)));
        V1460.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:villager_golem");
        V1460.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:vindication_illager");
        V1460.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:witch");
        V1460.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:wither");
        V1460.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:wither_skeleton");
        $$0.registerSimple((Map)$$12, "minecraft:wither_skull");
        V1460.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:wolf");
        $$0.registerSimple((Map)$$12, "minecraft:xp_bottle");
        $$0.registerSimple((Map)$$12, "minecraft:xp_orb");
        V1460.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:zombie");
        $$0.register((Map)$$12, "minecraft:zombie_horse", $$1 -> DSL.optionalFields((String)"SaddleItem", (TypeTemplate)References.ITEM_STACK.in($$0), (TypeTemplate)V100.equipment($$0)));
        V1460.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:zombie_pigman");
        V1460.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:zombie_villager");
        return $$12;
    }

    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema $$0) {
        HashMap $$12 = Maps.newHashMap();
        V1460.registerInventory($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:furnace");
        V1460.registerInventory($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:chest");
        V1460.registerInventory($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:trapped_chest");
        $$0.registerSimple((Map)$$12, "minecraft:ender_chest");
        $$0.register((Map)$$12, "minecraft:jukebox", $$1 -> DSL.optionalFields((String)"RecordItem", (TypeTemplate)References.ITEM_STACK.in($$0)));
        V1460.registerInventory($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:dispenser");
        V1460.registerInventory($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:dropper");
        $$0.registerSimple((Map)$$12, "minecraft:sign");
        $$0.register((Map)$$12, "minecraft:mob_spawner", $$1 -> References.UNTAGGED_SPAWNER.in($$0));
        $$0.register((Map)$$12, "minecraft:piston", $$1 -> DSL.optionalFields((String)"blockState", (TypeTemplate)References.BLOCK_STATE.in($$0)));
        V1460.registerInventory($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:brewing_stand");
        $$0.registerSimple((Map)$$12, "minecraft:enchanting_table");
        $$0.registerSimple((Map)$$12, "minecraft:end_portal");
        $$0.registerSimple((Map)$$12, "minecraft:beacon");
        $$0.registerSimple((Map)$$12, "minecraft:skull");
        $$0.registerSimple((Map)$$12, "minecraft:daylight_detector");
        V1460.registerInventory($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:hopper");
        $$0.registerSimple((Map)$$12, "minecraft:comparator");
        $$0.registerSimple((Map)$$12, "minecraft:banner");
        $$0.registerSimple((Map)$$12, "minecraft:structure_block");
        $$0.registerSimple((Map)$$12, "minecraft:end_gateway");
        $$0.registerSimple((Map)$$12, "minecraft:command_block");
        V1460.registerInventory($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:shulker_box");
        $$0.registerSimple((Map)$$12, "minecraft:bed");
        return $$12;
    }

    public void registerTypes(Schema $$0, Map<String, Supplier<TypeTemplate>> $$1, Map<String, Supplier<TypeTemplate>> $$2) {
        $$0.registerType(false, References.LEVEL, DSL::remainder);
        $$0.registerType(false, References.RECIPE, () -> DSL.constType(V1460.namespacedString()));
        $$0.registerType(false, References.PLAYER, () -> DSL.optionalFields((String)"RootVehicle", (TypeTemplate)DSL.optionalFields((String)"Entity", (TypeTemplate)References.ENTITY_TREE.in($$0)), (String)"Inventory", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in($$0)), (String)"EnderItems", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in($$0)), (TypeTemplate)DSL.optionalFields((String)"ShoulderEntityLeft", (TypeTemplate)References.ENTITY_TREE.in($$0), (String)"ShoulderEntityRight", (TypeTemplate)References.ENTITY_TREE.in($$0), (String)"recipeBook", (TypeTemplate)DSL.optionalFields((String)"recipes", (TypeTemplate)DSL.list((TypeTemplate)References.RECIPE.in($$0)), (String)"toBeDisplayed", (TypeTemplate)DSL.list((TypeTemplate)References.RECIPE.in($$0))))));
        $$0.registerType(false, References.CHUNK, () -> DSL.fields((String)"Level", (TypeTemplate)DSL.optionalFields((String)"Entities", (TypeTemplate)DSL.list((TypeTemplate)References.ENTITY_TREE.in($$0)), (String)"TileEntities", (TypeTemplate)DSL.list((TypeTemplate)DSL.or((TypeTemplate)References.BLOCK_ENTITY.in($$0), (TypeTemplate)DSL.remainder())), (String)"TileTicks", (TypeTemplate)DSL.list((TypeTemplate)DSL.fields((String)"i", (TypeTemplate)References.BLOCK_NAME.in($$0))), (String)"Sections", (TypeTemplate)DSL.list((TypeTemplate)DSL.optionalFields((String)"Palette", (TypeTemplate)DSL.list((TypeTemplate)References.BLOCK_STATE.in($$0)))))));
        $$0.registerType(true, References.BLOCK_ENTITY, () -> DSL.taggedChoiceLazy((String)"id", V1460.namespacedString(), (Map)$$2));
        $$0.registerType(true, References.ENTITY_TREE, () -> DSL.optionalFields((String)"Passengers", (TypeTemplate)DSL.list((TypeTemplate)References.ENTITY_TREE.in($$0)), (TypeTemplate)References.ENTITY.in($$0)));
        $$0.registerType(true, References.ENTITY, () -> DSL.taggedChoiceLazy((String)"id", V1460.namespacedString(), (Map)$$1));
        $$0.registerType(true, References.ITEM_STACK, () -> DSL.hook((TypeTemplate)DSL.optionalFields((String)"id", (TypeTemplate)References.ITEM_NAME.in($$0), (String)"tag", (TypeTemplate)DSL.optionalFields((String)"EntityTag", (TypeTemplate)References.ENTITY_TREE.in($$0), (String)"BlockEntityTag", (TypeTemplate)References.BLOCK_ENTITY.in($$0), (String)"CanDestroy", (TypeTemplate)DSL.list((TypeTemplate)References.BLOCK_NAME.in($$0)), (String)"CanPlaceOn", (TypeTemplate)DSL.list((TypeTemplate)References.BLOCK_NAME.in($$0)), (String)"Items", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in($$0)))), (Hook.HookFunction)V705.ADD_NAMES, (Hook.HookFunction)Hook.HookFunction.IDENTITY));
        $$0.registerType(false, References.HOTBAR, () -> DSL.compoundList((TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in($$0))));
        $$0.registerType(false, References.OPTIONS, DSL::remainder);
        $$0.registerType(false, References.STRUCTURE, () -> DSL.optionalFields((String)"entities", (TypeTemplate)DSL.list((TypeTemplate)DSL.optionalFields((String)"nbt", (TypeTemplate)References.ENTITY_TREE.in($$0))), (String)"blocks", (TypeTemplate)DSL.list((TypeTemplate)DSL.optionalFields((String)"nbt", (TypeTemplate)References.BLOCK_ENTITY.in($$0))), (String)"palette", (TypeTemplate)DSL.list((TypeTemplate)References.BLOCK_STATE.in($$0))));
        $$0.registerType(false, References.BLOCK_NAME, () -> DSL.constType(V1460.namespacedString()));
        $$0.registerType(false, References.ITEM_NAME, () -> DSL.constType(V1460.namespacedString()));
        $$0.registerType(false, References.BLOCK_STATE, DSL::remainder);
        Supplier $$3 = () -> DSL.compoundList((TypeTemplate)References.ITEM_NAME.in($$0), (TypeTemplate)DSL.constType((Type)DSL.intType()));
        $$0.registerType(false, References.STATS, () -> V1460.lambda$registerTypes$39($$0, (Supplier)$$3));
        $$0.registerType(false, References.SAVED_DATA, () -> DSL.optionalFields((String)"data", (TypeTemplate)DSL.optionalFields((String)"Features", (TypeTemplate)DSL.compoundList((TypeTemplate)References.STRUCTURE_FEATURE.in($$0)), (String)"Objectives", (TypeTemplate)DSL.list((TypeTemplate)References.OBJECTIVE.in($$0)), (String)"Teams", (TypeTemplate)DSL.list((TypeTemplate)References.TEAM.in($$0)))));
        $$0.registerType(false, References.STRUCTURE_FEATURE, () -> DSL.optionalFields((String)"Children", (TypeTemplate)DSL.list((TypeTemplate)DSL.optionalFields((String)"CA", (TypeTemplate)References.BLOCK_STATE.in($$0), (String)"CB", (TypeTemplate)References.BLOCK_STATE.in($$0), (String)"CC", (TypeTemplate)References.BLOCK_STATE.in($$0), (String)"CD", (TypeTemplate)References.BLOCK_STATE.in($$0)))));
        Map<String, Supplier<TypeTemplate>> $$4 = V1451_6.createCriterionTypes($$0);
        $$0.registerType(false, References.OBJECTIVE, () -> DSL.hook((TypeTemplate)DSL.optionalFields((String)"CriteriaType", (TypeTemplate)DSL.taggedChoiceLazy((String)"type", (Type)DSL.string(), (Map)$$4)), (Hook.HookFunction)V1451_6.UNPACK_OBJECTIVE_ID, (Hook.HookFunction)V1451_6.REPACK_OBJECTIVE_ID));
        $$0.registerType(false, References.TEAM, DSL::remainder);
        $$0.registerType(true, References.UNTAGGED_SPAWNER, () -> DSL.optionalFields((String)"SpawnPotentials", (TypeTemplate)DSL.list((TypeTemplate)DSL.fields((String)"Entity", (TypeTemplate)References.ENTITY_TREE.in($$0))), (String)"SpawnData", (TypeTemplate)References.ENTITY_TREE.in($$0)));
        $$0.registerType(false, References.ADVANCEMENTS, () -> DSL.optionalFields((String)"minecraft:adventure/adventuring_time", (TypeTemplate)DSL.optionalFields((String)"criteria", (TypeTemplate)DSL.compoundList((TypeTemplate)References.BIOME.in($$0), (TypeTemplate)DSL.constType((Type)DSL.string()))), (String)"minecraft:adventure/kill_a_mob", (TypeTemplate)DSL.optionalFields((String)"criteria", (TypeTemplate)DSL.compoundList((TypeTemplate)References.ENTITY_NAME.in($$0), (TypeTemplate)DSL.constType((Type)DSL.string()))), (String)"minecraft:adventure/kill_all_mobs", (TypeTemplate)DSL.optionalFields((String)"criteria", (TypeTemplate)DSL.compoundList((TypeTemplate)References.ENTITY_NAME.in($$0), (TypeTemplate)DSL.constType((Type)DSL.string()))), (String)"minecraft:husbandry/bred_all_animals", (TypeTemplate)DSL.optionalFields((String)"criteria", (TypeTemplate)DSL.compoundList((TypeTemplate)References.ENTITY_NAME.in($$0), (TypeTemplate)DSL.constType((Type)DSL.string())))));
        $$0.registerType(false, References.BIOME, () -> DSL.constType(V1460.namespacedString()));
        $$0.registerType(false, References.ENTITY_NAME, () -> DSL.constType(V1460.namespacedString()));
        $$0.registerType(false, References.POI_CHUNK, DSL::remainder);
        $$0.registerType(true, References.WORLD_GEN_SETTINGS, DSL::remainder);
        $$0.registerType(false, References.ENTITY_CHUNK, () -> DSL.optionalFields((String)"Entities", (TypeTemplate)DSL.list((TypeTemplate)References.ENTITY_TREE.in($$0))));
    }

    private static /* synthetic */ TypeTemplate lambda$registerTypes$39(Schema $$0, Supplier $$1) {
        return DSL.optionalFields((String)"stats", (TypeTemplate)DSL.optionalFields((String)"minecraft:mined", (TypeTemplate)DSL.compoundList((TypeTemplate)References.BLOCK_NAME.in($$0), (TypeTemplate)DSL.constType((Type)DSL.intType())), (String)"minecraft:crafted", (TypeTemplate)((TypeTemplate)$$1.get()), (String)"minecraft:used", (TypeTemplate)((TypeTemplate)$$1.get()), (String)"minecraft:broken", (TypeTemplate)((TypeTemplate)$$1.get()), (String)"minecraft:picked_up", (TypeTemplate)((TypeTemplate)$$1.get()), (TypeTemplate)DSL.optionalFields((String)"minecraft:dropped", (TypeTemplate)((TypeTemplate)$$1.get()), (String)"minecraft:killed", (TypeTemplate)DSL.compoundList((TypeTemplate)References.ENTITY_NAME.in($$0), (TypeTemplate)DSL.constType((Type)DSL.intType())), (String)"minecraft:killed_by", (TypeTemplate)DSL.compoundList((TypeTemplate)References.ENTITY_NAME.in($$0), (TypeTemplate)DSL.constType((Type)DSL.intType())), (String)"minecraft:custom", (TypeTemplate)DSL.compoundList((TypeTemplate)DSL.constType(V1460.namespacedString()), (TypeTemplate)DSL.constType((Type)DSL.intType())))));
    }
}