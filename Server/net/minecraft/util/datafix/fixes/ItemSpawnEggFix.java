/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
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
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Objects
 *  java.util.Optional
 */
package net.minecraft.util.datafix.fixes;

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
import java.util.Objects;
import java.util.Optional;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class ItemSpawnEggFix
extends DataFix {
    private static final String[] ID_TO_ENTITY = (String[])DataFixUtils.make((Object)new String[256], $$0 -> {
        $$0[1] = "Item";
        $$0[2] = "XPOrb";
        $$0[7] = "ThrownEgg";
        $$0[8] = "LeashKnot";
        $$0[9] = "Painting";
        $$0[10] = "Arrow";
        $$0[11] = "Snowball";
        $$0[12] = "Fireball";
        $$0[13] = "SmallFireball";
        $$0[14] = "ThrownEnderpearl";
        $$0[15] = "EyeOfEnderSignal";
        $$0[16] = "ThrownPotion";
        $$0[17] = "ThrownExpBottle";
        $$0[18] = "ItemFrame";
        $$0[19] = "WitherSkull";
        $$0[20] = "PrimedTnt";
        $$0[21] = "FallingSand";
        $$0[22] = "FireworksRocketEntity";
        $$0[23] = "TippedArrow";
        $$0[24] = "SpectralArrow";
        $$0[25] = "ShulkerBullet";
        $$0[26] = "DragonFireball";
        $$0[30] = "ArmorStand";
        $$0[41] = "Boat";
        $$0[42] = "MinecartRideable";
        $$0[43] = "MinecartChest";
        $$0[44] = "MinecartFurnace";
        $$0[45] = "MinecartTNT";
        $$0[46] = "MinecartHopper";
        $$0[47] = "MinecartSpawner";
        $$0[40] = "MinecartCommandBlock";
        $$0[48] = "Mob";
        $$0[49] = "Monster";
        $$0[50] = "Creeper";
        $$0[51] = "Skeleton";
        $$0[52] = "Spider";
        $$0[53] = "Giant";
        $$0[54] = "Zombie";
        $$0[55] = "Slime";
        $$0[56] = "Ghast";
        $$0[57] = "PigZombie";
        $$0[58] = "Enderman";
        $$0[59] = "CaveSpider";
        $$0[60] = "Silverfish";
        $$0[61] = "Blaze";
        $$0[62] = "LavaSlime";
        $$0[63] = "EnderDragon";
        $$0[64] = "WitherBoss";
        $$0[65] = "Bat";
        $$0[66] = "Witch";
        $$0[67] = "Endermite";
        $$0[68] = "Guardian";
        $$0[69] = "Shulker";
        $$0[90] = "Pig";
        $$0[91] = "Sheep";
        $$0[92] = "Cow";
        $$0[93] = "Chicken";
        $$0[94] = "Squid";
        $$0[95] = "Wolf";
        $$0[96] = "MushroomCow";
        $$0[97] = "SnowMan";
        $$0[98] = "Ozelot";
        $$0[99] = "VillagerGolem";
        $$0[100] = "EntityHorse";
        $$0[101] = "Rabbit";
        $$0[120] = "Villager";
        $$0[200] = "EnderCrystal";
    });

    public ItemSpawnEggFix(Schema $$0, boolean $$1) {
        super($$0, $$1);
    }

    public TypeRewriteRule makeRule() {
        Schema $$0 = this.getInputSchema();
        Type $$1 = $$0.getType(References.ITEM_STACK);
        OpticFinder $$2 = DSL.fieldFinder((String)"id", (Type)DSL.named((String)References.ITEM_NAME.typeName(), NamespacedSchema.namespacedString()));
        OpticFinder $$3 = DSL.fieldFinder((String)"id", (Type)DSL.string());
        OpticFinder $$4 = $$1.findField("tag");
        OpticFinder $$5 = $$4.type().findField("EntityTag");
        OpticFinder $$62 = DSL.typeFinder((Type)$$0.getTypeRaw(References.ENTITY));
        Type $$7 = this.getOutputSchema().getTypeRaw(References.ENTITY);
        return this.fixTypeEverywhereTyped("ItemSpawnEggFix", $$1, $$6 -> {
            Optional $$7 = $$6.getOptional($$2);
            if ($$7.isPresent() && Objects.equals((Object)((Pair)$$7.get()).getSecond(), (Object)"minecraft:spawn_egg")) {
                Dynamic $$8 = (Dynamic)$$6.get(DSL.remainderFinder());
                short $$9 = $$8.get("Damage").asShort((short)0);
                Optional $$10 = $$6.getOptionalTyped($$4);
                Optional $$11 = $$10.flatMap($$1 -> $$1.getOptionalTyped($$5));
                Optional $$122 = $$11.flatMap($$1 -> $$1.getOptionalTyped($$62));
                Optional $$13 = $$122.flatMap($$1 -> $$1.getOptional($$3));
                Typed $$14 = $$6;
                String $$15 = ID_TO_ENTITY[$$9 & 0xFF];
                if (!($$15 == null || $$13.isPresent() && Objects.equals((Object)$$13.get(), (Object)$$15))) {
                    Typed $$16 = $$6.getOrCreateTyped($$4);
                    Typed $$17 = $$16.getOrCreateTyped($$5);
                    Typed $$18 = $$17.getOrCreateTyped($$62);
                    Dynamic $$19 = $$8;
                    Typed $$20 = (Typed)((Pair)$$18.write().flatMap($$3 -> $$7.readTyped($$3.set("id", $$19.createString($$15)))).result().orElseThrow(() -> new IllegalStateException("Could not parse new entity"))).getFirst();
                    $$14 = $$14.set($$4, $$16.set($$5, $$17.set($$62, $$20)));
                }
                if ($$9 != 0) {
                    $$8 = $$8.set("Damage", $$8.createShort((short)0));
                    $$14 = $$14.set(DSL.remainderFinder(), (Object)$$8);
                }
                return $$14;
            }
            return $$6;
        });
    }
}