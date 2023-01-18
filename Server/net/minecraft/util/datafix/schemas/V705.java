/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.templates.Hook$HookFunction
 *  com.mojang.datafixers.types.templates.TypeTemplate
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
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
import com.mojang.datafixers.types.templates.Hook;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.NamespacedSchema;
import net.minecraft.util.datafix.schemas.V100;
import net.minecraft.util.datafix.schemas.V704;
import net.minecraft.util.datafix.schemas.V99;

public class V705
extends NamespacedSchema {
    protected static final Hook.HookFunction ADD_NAMES = new Hook.HookFunction(){

        public <T> T apply(DynamicOps<T> $$0, T $$1) {
            return V99.addNames(new Dynamic($$0, $$1), V704.ITEM_TO_BLOCKENTITY, "minecraft:armor_stand");
        }
    };

    public V705(int $$0, Schema $$1) {
        super($$0, $$1);
    }

    protected static void registerMob(Schema $$0, Map<String, Supplier<TypeTemplate>> $$1, String $$2) {
        $$0.register($$1, $$2, () -> V100.equipment($$0));
    }

    protected static void registerThrowableProjectile(Schema $$0, Map<String, Supplier<TypeTemplate>> $$1, String $$2) {
        $$0.register($$1, $$2, () -> DSL.optionalFields((String)"inTile", (TypeTemplate)References.BLOCK_NAME.in($$0)));
    }

    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema $$0) {
        HashMap $$12 = Maps.newHashMap();
        $$0.registerSimple((Map)$$12, "minecraft:area_effect_cloud");
        V705.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:armor_stand");
        $$0.register((Map)$$12, "minecraft:arrow", $$1 -> DSL.optionalFields((String)"inTile", (TypeTemplate)References.BLOCK_NAME.in($$0)));
        V705.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:bat");
        V705.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:blaze");
        $$0.registerSimple((Map)$$12, "minecraft:boat");
        V705.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:cave_spider");
        $$0.register((Map)$$12, "minecraft:chest_minecart", $$1 -> DSL.optionalFields((String)"DisplayTile", (TypeTemplate)References.BLOCK_NAME.in($$0), (String)"Items", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in($$0))));
        V705.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:chicken");
        $$0.register((Map)$$12, "minecraft:commandblock_minecart", $$1 -> DSL.optionalFields((String)"DisplayTile", (TypeTemplate)References.BLOCK_NAME.in($$0)));
        V705.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:cow");
        V705.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:creeper");
        $$0.register((Map)$$12, "minecraft:donkey", $$1 -> DSL.optionalFields((String)"Items", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in($$0)), (String)"SaddleItem", (TypeTemplate)References.ITEM_STACK.in($$0), (TypeTemplate)V100.equipment($$0)));
        $$0.registerSimple((Map)$$12, "minecraft:dragon_fireball");
        V705.registerThrowableProjectile($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:egg");
        V705.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:elder_guardian");
        $$0.registerSimple((Map)$$12, "minecraft:ender_crystal");
        V705.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:ender_dragon");
        $$0.register((Map)$$12, "minecraft:enderman", $$1 -> DSL.optionalFields((String)"carried", (TypeTemplate)References.BLOCK_NAME.in($$0), (TypeTemplate)V100.equipment($$0)));
        V705.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:endermite");
        V705.registerThrowableProjectile($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:ender_pearl");
        $$0.registerSimple((Map)$$12, "minecraft:eye_of_ender_signal");
        $$0.register((Map)$$12, "minecraft:falling_block", $$1 -> DSL.optionalFields((String)"Block", (TypeTemplate)References.BLOCK_NAME.in($$0), (String)"TileEntityData", (TypeTemplate)References.BLOCK_ENTITY.in($$0)));
        V705.registerThrowableProjectile($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:fireball");
        $$0.register((Map)$$12, "minecraft:fireworks_rocket", $$1 -> DSL.optionalFields((String)"FireworksItem", (TypeTemplate)References.ITEM_STACK.in($$0)));
        $$0.register((Map)$$12, "minecraft:furnace_minecart", $$1 -> DSL.optionalFields((String)"DisplayTile", (TypeTemplate)References.BLOCK_NAME.in($$0)));
        V705.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:ghast");
        V705.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:giant");
        V705.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:guardian");
        $$0.register((Map)$$12, "minecraft:hopper_minecart", $$1 -> DSL.optionalFields((String)"DisplayTile", (TypeTemplate)References.BLOCK_NAME.in($$0), (String)"Items", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in($$0))));
        $$0.register((Map)$$12, "minecraft:horse", $$1 -> DSL.optionalFields((String)"ArmorItem", (TypeTemplate)References.ITEM_STACK.in($$0), (String)"SaddleItem", (TypeTemplate)References.ITEM_STACK.in($$0), (TypeTemplate)V100.equipment($$0)));
        V705.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:husk");
        $$0.register((Map)$$12, "minecraft:item", $$1 -> DSL.optionalFields((String)"Item", (TypeTemplate)References.ITEM_STACK.in($$0)));
        $$0.register((Map)$$12, "minecraft:item_frame", $$1 -> DSL.optionalFields((String)"Item", (TypeTemplate)References.ITEM_STACK.in($$0)));
        $$0.registerSimple((Map)$$12, "minecraft:leash_knot");
        V705.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:magma_cube");
        $$0.register((Map)$$12, "minecraft:minecart", $$1 -> DSL.optionalFields((String)"DisplayTile", (TypeTemplate)References.BLOCK_NAME.in($$0)));
        V705.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:mooshroom");
        $$0.register((Map)$$12, "minecraft:mule", $$1 -> DSL.optionalFields((String)"Items", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in($$0)), (String)"SaddleItem", (TypeTemplate)References.ITEM_STACK.in($$0), (TypeTemplate)V100.equipment($$0)));
        V705.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:ocelot");
        $$0.registerSimple((Map)$$12, "minecraft:painting");
        $$0.registerSimple((Map)$$12, "minecraft:parrot");
        V705.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:pig");
        V705.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:polar_bear");
        $$0.register((Map)$$12, "minecraft:potion", $$1 -> DSL.optionalFields((String)"Potion", (TypeTemplate)References.ITEM_STACK.in($$0), (String)"inTile", (TypeTemplate)References.BLOCK_NAME.in($$0)));
        V705.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:rabbit");
        V705.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:sheep");
        V705.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:shulker");
        $$0.registerSimple((Map)$$12, "minecraft:shulker_bullet");
        V705.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:silverfish");
        V705.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:skeleton");
        $$0.register((Map)$$12, "minecraft:skeleton_horse", $$1 -> DSL.optionalFields((String)"SaddleItem", (TypeTemplate)References.ITEM_STACK.in($$0), (TypeTemplate)V100.equipment($$0)));
        V705.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:slime");
        V705.registerThrowableProjectile($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:small_fireball");
        V705.registerThrowableProjectile($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:snowball");
        V705.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:snowman");
        $$0.register((Map)$$12, "minecraft:spawner_minecart", $$1 -> DSL.optionalFields((String)"DisplayTile", (TypeTemplate)References.BLOCK_NAME.in($$0), (TypeTemplate)References.UNTAGGED_SPAWNER.in($$0)));
        $$0.register((Map)$$12, "minecraft:spectral_arrow", $$1 -> DSL.optionalFields((String)"inTile", (TypeTemplate)References.BLOCK_NAME.in($$0)));
        V705.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:spider");
        V705.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:squid");
        V705.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:stray");
        $$0.registerSimple((Map)$$12, "minecraft:tnt");
        $$0.register((Map)$$12, "minecraft:tnt_minecart", $$1 -> DSL.optionalFields((String)"DisplayTile", (TypeTemplate)References.BLOCK_NAME.in($$0)));
        $$0.register((Map)$$12, "minecraft:villager", $$1 -> DSL.optionalFields((String)"Inventory", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in($$0)), (String)"Offers", (TypeTemplate)DSL.optionalFields((String)"Recipes", (TypeTemplate)DSL.list((TypeTemplate)DSL.optionalFields((String)"buy", (TypeTemplate)References.ITEM_STACK.in($$0), (String)"buyB", (TypeTemplate)References.ITEM_STACK.in($$0), (String)"sell", (TypeTemplate)References.ITEM_STACK.in($$0)))), (TypeTemplate)V100.equipment($$0)));
        V705.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:villager_golem");
        V705.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:witch");
        V705.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:wither");
        V705.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:wither_skeleton");
        V705.registerThrowableProjectile($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:wither_skull");
        V705.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:wolf");
        V705.registerThrowableProjectile($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:xp_bottle");
        $$0.registerSimple((Map)$$12, "minecraft:xp_orb");
        V705.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:zombie");
        $$0.register((Map)$$12, "minecraft:zombie_horse", $$1 -> DSL.optionalFields((String)"SaddleItem", (TypeTemplate)References.ITEM_STACK.in($$0), (TypeTemplate)V100.equipment($$0)));
        V705.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:zombie_pigman");
        V705.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:zombie_villager");
        $$0.registerSimple((Map)$$12, "minecraft:evocation_fangs");
        V705.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:evocation_illager");
        $$0.registerSimple((Map)$$12, "minecraft:illusion_illager");
        $$0.register((Map)$$12, "minecraft:llama", $$1 -> DSL.optionalFields((String)"Items", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in($$0)), (String)"SaddleItem", (TypeTemplate)References.ITEM_STACK.in($$0), (String)"DecorItem", (TypeTemplate)References.ITEM_STACK.in($$0), (TypeTemplate)V100.equipment($$0)));
        $$0.registerSimple((Map)$$12, "minecraft:llama_spit");
        V705.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:vex");
        V705.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:vindication_illager");
        return $$12;
    }

    public void registerTypes(Schema $$0, Map<String, Supplier<TypeTemplate>> $$1, Map<String, Supplier<TypeTemplate>> $$2) {
        super.registerTypes($$0, $$1, $$2);
        $$0.registerType(true, References.ENTITY, () -> DSL.taggedChoiceLazy((String)"id", V705.namespacedString(), (Map)$$1));
        $$0.registerType(true, References.ITEM_STACK, () -> DSL.hook((TypeTemplate)DSL.optionalFields((String)"id", (TypeTemplate)References.ITEM_NAME.in($$0), (String)"tag", (TypeTemplate)DSL.optionalFields((String)"EntityTag", (TypeTemplate)References.ENTITY_TREE.in($$0), (String)"BlockEntityTag", (TypeTemplate)References.BLOCK_ENTITY.in($$0), (String)"CanDestroy", (TypeTemplate)DSL.list((TypeTemplate)References.BLOCK_NAME.in($$0)), (String)"CanPlaceOn", (TypeTemplate)DSL.list((TypeTemplate)References.BLOCK_NAME.in($$0)), (String)"Items", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in($$0)))), (Hook.HookFunction)ADD_NAMES, (Hook.HookFunction)Hook.HookFunction.IDENTITY));
    }
}