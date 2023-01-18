/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.types.templates.Hook$HookFunction
 *  com.mojang.datafixers.types.templates.TypeTemplate
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  java.lang.Object
 *  java.lang.String
 *  java.util.HashMap
 *  java.util.Map
 *  java.util.function.Supplier
 *  org.slf4j.Logger
 */
package net.minecraft.util.datafix.schemas;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.Hook;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.NamespacedSchema;
import org.slf4j.Logger;

public class V99
extends Schema {
    private static final Logger LOGGER = LogUtils.getLogger();
    static final Map<String, String> ITEM_TO_BLOCKENTITY = (Map)DataFixUtils.make((Object)Maps.newHashMap(), $$0 -> {
        $$0.put((Object)"minecraft:furnace", (Object)"Furnace");
        $$0.put((Object)"minecraft:lit_furnace", (Object)"Furnace");
        $$0.put((Object)"minecraft:chest", (Object)"Chest");
        $$0.put((Object)"minecraft:trapped_chest", (Object)"Chest");
        $$0.put((Object)"minecraft:ender_chest", (Object)"EnderChest");
        $$0.put((Object)"minecraft:jukebox", (Object)"RecordPlayer");
        $$0.put((Object)"minecraft:dispenser", (Object)"Trap");
        $$0.put((Object)"minecraft:dropper", (Object)"Dropper");
        $$0.put((Object)"minecraft:sign", (Object)"Sign");
        $$0.put((Object)"minecraft:mob_spawner", (Object)"MobSpawner");
        $$0.put((Object)"minecraft:noteblock", (Object)"Music");
        $$0.put((Object)"minecraft:brewing_stand", (Object)"Cauldron");
        $$0.put((Object)"minecraft:enhanting_table", (Object)"EnchantTable");
        $$0.put((Object)"minecraft:command_block", (Object)"CommandBlock");
        $$0.put((Object)"minecraft:beacon", (Object)"Beacon");
        $$0.put((Object)"minecraft:skull", (Object)"Skull");
        $$0.put((Object)"minecraft:daylight_detector", (Object)"DLDetector");
        $$0.put((Object)"minecraft:hopper", (Object)"Hopper");
        $$0.put((Object)"minecraft:banner", (Object)"Banner");
        $$0.put((Object)"minecraft:flower_pot", (Object)"FlowerPot");
        $$0.put((Object)"minecraft:repeating_command_block", (Object)"CommandBlock");
        $$0.put((Object)"minecraft:chain_command_block", (Object)"CommandBlock");
        $$0.put((Object)"minecraft:standing_sign", (Object)"Sign");
        $$0.put((Object)"minecraft:wall_sign", (Object)"Sign");
        $$0.put((Object)"minecraft:piston_head", (Object)"Piston");
        $$0.put((Object)"minecraft:daylight_detector_inverted", (Object)"DLDetector");
        $$0.put((Object)"minecraft:unpowered_comparator", (Object)"Comparator");
        $$0.put((Object)"minecraft:powered_comparator", (Object)"Comparator");
        $$0.put((Object)"minecraft:wall_banner", (Object)"Banner");
        $$0.put((Object)"minecraft:standing_banner", (Object)"Banner");
        $$0.put((Object)"minecraft:structure_block", (Object)"Structure");
        $$0.put((Object)"minecraft:end_portal", (Object)"Airportal");
        $$0.put((Object)"minecraft:end_gateway", (Object)"EndGateway");
        $$0.put((Object)"minecraft:shield", (Object)"Banner");
    });
    protected static final Hook.HookFunction ADD_NAMES = new Hook.HookFunction(){

        public <T> T apply(DynamicOps<T> $$0, T $$1) {
            return V99.addNames(new Dynamic($$0, $$1), ITEM_TO_BLOCKENTITY, "ArmorStand");
        }
    };

    public V99(int $$0, Schema $$1) {
        super($$0, $$1);
    }

    protected static TypeTemplate equipment(Schema $$0) {
        return DSL.optionalFields((String)"Equipment", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in($$0)));
    }

    protected static void registerMob(Schema $$0, Map<String, Supplier<TypeTemplate>> $$1, String $$2) {
        $$0.register($$1, $$2, () -> V99.equipment($$0));
    }

    protected static void registerThrowableProjectile(Schema $$0, Map<String, Supplier<TypeTemplate>> $$1, String $$2) {
        $$0.register($$1, $$2, () -> DSL.optionalFields((String)"inTile", (TypeTemplate)References.BLOCK_NAME.in($$0)));
    }

    protected static void registerMinecart(Schema $$0, Map<String, Supplier<TypeTemplate>> $$1, String $$2) {
        $$0.register($$1, $$2, () -> DSL.optionalFields((String)"DisplayTile", (TypeTemplate)References.BLOCK_NAME.in($$0)));
    }

    protected static void registerInventory(Schema $$0, Map<String, Supplier<TypeTemplate>> $$1, String $$2) {
        $$0.register($$1, $$2, () -> DSL.optionalFields((String)"Items", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in($$0))));
    }

    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema $$0) {
        HashMap $$12 = Maps.newHashMap();
        $$0.register((Map)$$12, "Item", $$1 -> DSL.optionalFields((String)"Item", (TypeTemplate)References.ITEM_STACK.in($$0)));
        $$0.registerSimple((Map)$$12, "XPOrb");
        V99.registerThrowableProjectile($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "ThrownEgg");
        $$0.registerSimple((Map)$$12, "LeashKnot");
        $$0.registerSimple((Map)$$12, "Painting");
        $$0.register((Map)$$12, "Arrow", $$1 -> DSL.optionalFields((String)"inTile", (TypeTemplate)References.BLOCK_NAME.in($$0)));
        $$0.register((Map)$$12, "TippedArrow", $$1 -> DSL.optionalFields((String)"inTile", (TypeTemplate)References.BLOCK_NAME.in($$0)));
        $$0.register((Map)$$12, "SpectralArrow", $$1 -> DSL.optionalFields((String)"inTile", (TypeTemplate)References.BLOCK_NAME.in($$0)));
        V99.registerThrowableProjectile($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "Snowball");
        V99.registerThrowableProjectile($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "Fireball");
        V99.registerThrowableProjectile($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "SmallFireball");
        V99.registerThrowableProjectile($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "ThrownEnderpearl");
        $$0.registerSimple((Map)$$12, "EyeOfEnderSignal");
        $$0.register((Map)$$12, "ThrownPotion", $$1 -> DSL.optionalFields((String)"inTile", (TypeTemplate)References.BLOCK_NAME.in($$0), (String)"Potion", (TypeTemplate)References.ITEM_STACK.in($$0)));
        V99.registerThrowableProjectile($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "ThrownExpBottle");
        $$0.register((Map)$$12, "ItemFrame", $$1 -> DSL.optionalFields((String)"Item", (TypeTemplate)References.ITEM_STACK.in($$0)));
        V99.registerThrowableProjectile($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "WitherSkull");
        $$0.registerSimple((Map)$$12, "PrimedTnt");
        $$0.register((Map)$$12, "FallingSand", $$1 -> DSL.optionalFields((String)"Block", (TypeTemplate)References.BLOCK_NAME.in($$0), (String)"TileEntityData", (TypeTemplate)References.BLOCK_ENTITY.in($$0)));
        $$0.register((Map)$$12, "FireworksRocketEntity", $$1 -> DSL.optionalFields((String)"FireworksItem", (TypeTemplate)References.ITEM_STACK.in($$0)));
        $$0.registerSimple((Map)$$12, "Boat");
        $$0.register((Map)$$12, "Minecart", () -> DSL.optionalFields((String)"DisplayTile", (TypeTemplate)References.BLOCK_NAME.in($$0), (String)"Items", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in($$0))));
        V99.registerMinecart($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "MinecartRideable");
        $$0.register((Map)$$12, "MinecartChest", $$1 -> DSL.optionalFields((String)"DisplayTile", (TypeTemplate)References.BLOCK_NAME.in($$0), (String)"Items", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in($$0))));
        V99.registerMinecart($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "MinecartFurnace");
        V99.registerMinecart($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "MinecartTNT");
        $$0.register((Map)$$12, "MinecartSpawner", () -> DSL.optionalFields((String)"DisplayTile", (TypeTemplate)References.BLOCK_NAME.in($$0), (TypeTemplate)References.UNTAGGED_SPAWNER.in($$0)));
        $$0.register((Map)$$12, "MinecartHopper", $$1 -> DSL.optionalFields((String)"DisplayTile", (TypeTemplate)References.BLOCK_NAME.in($$0), (String)"Items", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in($$0))));
        V99.registerMinecart($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "MinecartCommandBlock");
        V99.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "ArmorStand");
        V99.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "Creeper");
        V99.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "Skeleton");
        V99.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "Spider");
        V99.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "Giant");
        V99.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "Zombie");
        V99.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "Slime");
        V99.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "Ghast");
        V99.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "PigZombie");
        $$0.register((Map)$$12, "Enderman", $$1 -> DSL.optionalFields((String)"carried", (TypeTemplate)References.BLOCK_NAME.in($$0), (TypeTemplate)V99.equipment($$0)));
        V99.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "CaveSpider");
        V99.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "Silverfish");
        V99.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "Blaze");
        V99.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "LavaSlime");
        V99.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "EnderDragon");
        V99.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "WitherBoss");
        V99.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "Bat");
        V99.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "Witch");
        V99.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "Endermite");
        V99.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "Guardian");
        V99.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "Pig");
        V99.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "Sheep");
        V99.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "Cow");
        V99.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "Chicken");
        V99.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "Squid");
        V99.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "Wolf");
        V99.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "MushroomCow");
        V99.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "SnowMan");
        V99.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "Ozelot");
        V99.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "VillagerGolem");
        $$0.register((Map)$$12, "EntityHorse", $$1 -> DSL.optionalFields((String)"Items", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in($$0)), (String)"ArmorItem", (TypeTemplate)References.ITEM_STACK.in($$0), (String)"SaddleItem", (TypeTemplate)References.ITEM_STACK.in($$0), (TypeTemplate)V99.equipment($$0)));
        V99.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "Rabbit");
        $$0.register((Map)$$12, "Villager", $$1 -> DSL.optionalFields((String)"Inventory", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in($$0)), (String)"Offers", (TypeTemplate)DSL.optionalFields((String)"Recipes", (TypeTemplate)DSL.list((TypeTemplate)DSL.optionalFields((String)"buy", (TypeTemplate)References.ITEM_STACK.in($$0), (String)"buyB", (TypeTemplate)References.ITEM_STACK.in($$0), (String)"sell", (TypeTemplate)References.ITEM_STACK.in($$0)))), (TypeTemplate)V99.equipment($$0)));
        $$0.registerSimple((Map)$$12, "EnderCrystal");
        $$0.registerSimple((Map)$$12, "AreaEffectCloud");
        $$0.registerSimple((Map)$$12, "ShulkerBullet");
        V99.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "Shulker");
        return $$12;
    }

    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema $$0) {
        HashMap $$12 = Maps.newHashMap();
        V99.registerInventory($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "Furnace");
        V99.registerInventory($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "Chest");
        $$0.registerSimple((Map)$$12, "EnderChest");
        $$0.register((Map)$$12, "RecordPlayer", $$1 -> DSL.optionalFields((String)"RecordItem", (TypeTemplate)References.ITEM_STACK.in($$0)));
        V99.registerInventory($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "Trap");
        V99.registerInventory($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "Dropper");
        $$0.registerSimple((Map)$$12, "Sign");
        $$0.register((Map)$$12, "MobSpawner", $$1 -> References.UNTAGGED_SPAWNER.in($$0));
        $$0.registerSimple((Map)$$12, "Music");
        $$0.registerSimple((Map)$$12, "Piston");
        V99.registerInventory($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "Cauldron");
        $$0.registerSimple((Map)$$12, "EnchantTable");
        $$0.registerSimple((Map)$$12, "Airportal");
        $$0.registerSimple((Map)$$12, "Control");
        $$0.registerSimple((Map)$$12, "Beacon");
        $$0.registerSimple((Map)$$12, "Skull");
        $$0.registerSimple((Map)$$12, "DLDetector");
        V99.registerInventory($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "Hopper");
        $$0.registerSimple((Map)$$12, "Comparator");
        $$0.register((Map)$$12, "FlowerPot", $$1 -> DSL.optionalFields((String)"Item", (TypeTemplate)DSL.or((TypeTemplate)DSL.constType((Type)DSL.intType()), (TypeTemplate)References.ITEM_NAME.in($$0))));
        $$0.registerSimple((Map)$$12, "Banner");
        $$0.registerSimple((Map)$$12, "Structure");
        $$0.registerSimple((Map)$$12, "EndGateway");
        return $$12;
    }

    public void registerTypes(Schema $$0, Map<String, Supplier<TypeTemplate>> $$1, Map<String, Supplier<TypeTemplate>> $$2) {
        $$0.registerType(false, References.LEVEL, DSL::remainder);
        $$0.registerType(false, References.PLAYER, () -> DSL.optionalFields((String)"Inventory", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in($$0)), (String)"EnderItems", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in($$0))));
        $$0.registerType(false, References.CHUNK, () -> DSL.fields((String)"Level", (TypeTemplate)DSL.optionalFields((String)"Entities", (TypeTemplate)DSL.list((TypeTemplate)References.ENTITY_TREE.in($$0)), (String)"TileEntities", (TypeTemplate)DSL.list((TypeTemplate)DSL.or((TypeTemplate)References.BLOCK_ENTITY.in($$0), (TypeTemplate)DSL.remainder())), (String)"TileTicks", (TypeTemplate)DSL.list((TypeTemplate)DSL.fields((String)"i", (TypeTemplate)References.BLOCK_NAME.in($$0))))));
        $$0.registerType(true, References.BLOCK_ENTITY, () -> DSL.taggedChoiceLazy((String)"id", (Type)DSL.string(), (Map)$$2));
        $$0.registerType(true, References.ENTITY_TREE, () -> DSL.optionalFields((String)"Riding", (TypeTemplate)References.ENTITY_TREE.in($$0), (TypeTemplate)References.ENTITY.in($$0)));
        $$0.registerType(false, References.ENTITY_NAME, () -> DSL.constType(NamespacedSchema.namespacedString()));
        $$0.registerType(true, References.ENTITY, () -> DSL.taggedChoiceLazy((String)"id", (Type)DSL.string(), (Map)$$1));
        $$0.registerType(true, References.ITEM_STACK, () -> DSL.hook((TypeTemplate)DSL.optionalFields((String)"id", (TypeTemplate)DSL.or((TypeTemplate)DSL.constType((Type)DSL.intType()), (TypeTemplate)References.ITEM_NAME.in($$0)), (String)"tag", (TypeTemplate)DSL.optionalFields((String)"EntityTag", (TypeTemplate)References.ENTITY_TREE.in($$0), (String)"BlockEntityTag", (TypeTemplate)References.BLOCK_ENTITY.in($$0), (String)"CanDestroy", (TypeTemplate)DSL.list((TypeTemplate)References.BLOCK_NAME.in($$0)), (String)"CanPlaceOn", (TypeTemplate)DSL.list((TypeTemplate)References.BLOCK_NAME.in($$0)), (String)"Items", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in($$0)))), (Hook.HookFunction)ADD_NAMES, (Hook.HookFunction)Hook.HookFunction.IDENTITY));
        $$0.registerType(false, References.OPTIONS, DSL::remainder);
        $$0.registerType(false, References.BLOCK_NAME, () -> DSL.or((TypeTemplate)DSL.constType((Type)DSL.intType()), (TypeTemplate)DSL.constType(NamespacedSchema.namespacedString())));
        $$0.registerType(false, References.ITEM_NAME, () -> DSL.constType(NamespacedSchema.namespacedString()));
        $$0.registerType(false, References.STATS, DSL::remainder);
        $$0.registerType(false, References.SAVED_DATA, () -> DSL.optionalFields((String)"data", (TypeTemplate)DSL.optionalFields((String)"Features", (TypeTemplate)DSL.compoundList((TypeTemplate)References.STRUCTURE_FEATURE.in($$0)), (String)"Objectives", (TypeTemplate)DSL.list((TypeTemplate)References.OBJECTIVE.in($$0)), (String)"Teams", (TypeTemplate)DSL.list((TypeTemplate)References.TEAM.in($$0)))));
        $$0.registerType(false, References.STRUCTURE_FEATURE, DSL::remainder);
        $$0.registerType(false, References.OBJECTIVE, DSL::remainder);
        $$0.registerType(false, References.TEAM, DSL::remainder);
        $$0.registerType(true, References.UNTAGGED_SPAWNER, DSL::remainder);
        $$0.registerType(false, References.POI_CHUNK, DSL::remainder);
        $$0.registerType(true, References.WORLD_GEN_SETTINGS, DSL::remainder);
        $$0.registerType(false, References.ENTITY_CHUNK, () -> DSL.optionalFields((String)"Entities", (TypeTemplate)DSL.list((TypeTemplate)References.ENTITY_TREE.in($$0))));
    }

    protected static <T> T addNames(Dynamic<T> $$0, Map<String, String> $$1, String $$2) {
        return (T)$$0.update("tag", $$3 -> $$3.update("BlockEntityTag", $$2 -> {
            Object $$3 = (String)$$0.get("id").asString().result().map(NamespacedSchema::ensureNamespaced).orElse((Object)"minecraft:air");
            if (!"minecraft:air".equals($$3)) {
                String $$4 = (String)$$1.get($$3);
                if ($$4 == null) {
                    LOGGER.warn("Unable to resolve BlockEntity for ItemStack: {}", $$3);
                } else {
                    return $$2.set("id", $$0.createString($$4));
                }
            }
            return $$2;
        }).update("EntityTag", $$2 -> {
            Object $$3 = $$0.get("id").asString("");
            if ("minecraft:armor_stand".equals((Object)NamespacedSchema.ensureNamespaced($$3))) {
                return $$2.set("id", $$0.createString($$2));
            }
            return $$2;
        })).getValue();
    }
}