/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.templates.TypeTemplate
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Map
 *  java.util.function.Supplier
 */
package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V100
extends Schema {
    public V100(int $$0, Schema $$1) {
        super($$0, $$1);
    }

    protected static TypeTemplate equipment(Schema $$0) {
        return DSL.optionalFields((String)"ArmorItems", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in($$0)), (String)"HandItems", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in($$0)));
    }

    protected static void registerMob(Schema $$0, Map<String, Supplier<TypeTemplate>> $$1, String $$2) {
        $$0.register($$1, $$2, () -> V100.equipment($$0));
    }

    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema $$0) {
        Map $$12 = super.registerEntities($$0);
        V100.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "ArmorStand");
        V100.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "Creeper");
        V100.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "Skeleton");
        V100.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "Spider");
        V100.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "Giant");
        V100.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "Zombie");
        V100.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "Slime");
        V100.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "Ghast");
        V100.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "PigZombie");
        $$0.register($$12, "Enderman", $$1 -> DSL.optionalFields((String)"carried", (TypeTemplate)References.BLOCK_NAME.in($$0), (TypeTemplate)V100.equipment($$0)));
        V100.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "CaveSpider");
        V100.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "Silverfish");
        V100.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "Blaze");
        V100.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "LavaSlime");
        V100.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "EnderDragon");
        V100.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "WitherBoss");
        V100.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "Bat");
        V100.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "Witch");
        V100.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "Endermite");
        V100.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "Guardian");
        V100.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "Pig");
        V100.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "Sheep");
        V100.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "Cow");
        V100.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "Chicken");
        V100.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "Squid");
        V100.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "Wolf");
        V100.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "MushroomCow");
        V100.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "SnowMan");
        V100.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "Ozelot");
        V100.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "VillagerGolem");
        $$0.register($$12, "EntityHorse", $$1 -> DSL.optionalFields((String)"Items", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in($$0)), (String)"ArmorItem", (TypeTemplate)References.ITEM_STACK.in($$0), (String)"SaddleItem", (TypeTemplate)References.ITEM_STACK.in($$0), (TypeTemplate)V100.equipment($$0)));
        V100.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "Rabbit");
        $$0.register($$12, "Villager", $$1 -> DSL.optionalFields((String)"Inventory", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in($$0)), (String)"Offers", (TypeTemplate)DSL.optionalFields((String)"Recipes", (TypeTemplate)DSL.list((TypeTemplate)DSL.optionalFields((String)"buy", (TypeTemplate)References.ITEM_STACK.in($$0), (String)"buyB", (TypeTemplate)References.ITEM_STACK.in($$0), (String)"sell", (TypeTemplate)References.ITEM_STACK.in($$0)))), (TypeTemplate)V100.equipment($$0)));
        V100.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "Shulker");
        $$0.registerSimple($$12, "AreaEffectCloud");
        $$0.registerSimple($$12, "ShulkerBullet");
        return $$12;
    }

    public void registerTypes(Schema $$0, Map<String, Supplier<TypeTemplate>> $$1, Map<String, Supplier<TypeTemplate>> $$2) {
        super.registerTypes($$0, $$1, $$2);
        $$0.registerType(false, References.STRUCTURE, () -> DSL.optionalFields((String)"entities", (TypeTemplate)DSL.list((TypeTemplate)DSL.optionalFields((String)"nbt", (TypeTemplate)References.ENTITY_TREE.in($$0))), (String)"blocks", (TypeTemplate)DSL.list((TypeTemplate)DSL.optionalFields((String)"nbt", (TypeTemplate)References.BLOCK_ENTITY.in($$0))), (String)"palette", (TypeTemplate)DSL.list((TypeTemplate)References.BLOCK_STATE.in($$0))));
        $$0.registerType(false, References.BLOCK_STATE, DSL::remainder);
    }
}