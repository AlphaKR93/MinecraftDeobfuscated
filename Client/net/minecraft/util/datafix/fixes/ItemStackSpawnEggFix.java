/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.util.Pair
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Map
 *  java.util.Objects
 *  java.util.Optional
 */
package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class ItemStackSpawnEggFix
extends DataFix {
    private final String itemType;
    private static final Map<String, String> MAP = (Map)DataFixUtils.make((Object)Maps.newHashMap(), $$0 -> {
        $$0.put((Object)"minecraft:bat", (Object)"minecraft:bat_spawn_egg");
        $$0.put((Object)"minecraft:blaze", (Object)"minecraft:blaze_spawn_egg");
        $$0.put((Object)"minecraft:cave_spider", (Object)"minecraft:cave_spider_spawn_egg");
        $$0.put((Object)"minecraft:chicken", (Object)"minecraft:chicken_spawn_egg");
        $$0.put((Object)"minecraft:cow", (Object)"minecraft:cow_spawn_egg");
        $$0.put((Object)"minecraft:creeper", (Object)"minecraft:creeper_spawn_egg");
        $$0.put((Object)"minecraft:donkey", (Object)"minecraft:donkey_spawn_egg");
        $$0.put((Object)"minecraft:elder_guardian", (Object)"minecraft:elder_guardian_spawn_egg");
        $$0.put((Object)"minecraft:ender_dragon", (Object)"minecraft:ender_dragon_spawn_egg");
        $$0.put((Object)"minecraft:enderman", (Object)"minecraft:enderman_spawn_egg");
        $$0.put((Object)"minecraft:endermite", (Object)"minecraft:endermite_spawn_egg");
        $$0.put((Object)"minecraft:evocation_illager", (Object)"minecraft:evocation_illager_spawn_egg");
        $$0.put((Object)"minecraft:ghast", (Object)"minecraft:ghast_spawn_egg");
        $$0.put((Object)"minecraft:guardian", (Object)"minecraft:guardian_spawn_egg");
        $$0.put((Object)"minecraft:horse", (Object)"minecraft:horse_spawn_egg");
        $$0.put((Object)"minecraft:husk", (Object)"minecraft:husk_spawn_egg");
        $$0.put((Object)"minecraft:iron_golem", (Object)"minecraft:iron_golem_spawn_egg");
        $$0.put((Object)"minecraft:llama", (Object)"minecraft:llama_spawn_egg");
        $$0.put((Object)"minecraft:magma_cube", (Object)"minecraft:magma_cube_spawn_egg");
        $$0.put((Object)"minecraft:mooshroom", (Object)"minecraft:mooshroom_spawn_egg");
        $$0.put((Object)"minecraft:mule", (Object)"minecraft:mule_spawn_egg");
        $$0.put((Object)"minecraft:ocelot", (Object)"minecraft:ocelot_spawn_egg");
        $$0.put((Object)"minecraft:pufferfish", (Object)"minecraft:pufferfish_spawn_egg");
        $$0.put((Object)"minecraft:parrot", (Object)"minecraft:parrot_spawn_egg");
        $$0.put((Object)"minecraft:pig", (Object)"minecraft:pig_spawn_egg");
        $$0.put((Object)"minecraft:polar_bear", (Object)"minecraft:polar_bear_spawn_egg");
        $$0.put((Object)"minecraft:rabbit", (Object)"minecraft:rabbit_spawn_egg");
        $$0.put((Object)"minecraft:sheep", (Object)"minecraft:sheep_spawn_egg");
        $$0.put((Object)"minecraft:shulker", (Object)"minecraft:shulker_spawn_egg");
        $$0.put((Object)"minecraft:silverfish", (Object)"minecraft:silverfish_spawn_egg");
        $$0.put((Object)"minecraft:skeleton", (Object)"minecraft:skeleton_spawn_egg");
        $$0.put((Object)"minecraft:skeleton_horse", (Object)"minecraft:skeleton_horse_spawn_egg");
        $$0.put((Object)"minecraft:slime", (Object)"minecraft:slime_spawn_egg");
        $$0.put((Object)"minecraft:snow_golem", (Object)"minecraft:snow_golem_spawn_egg");
        $$0.put((Object)"minecraft:spider", (Object)"minecraft:spider_spawn_egg");
        $$0.put((Object)"minecraft:squid", (Object)"minecraft:squid_spawn_egg");
        $$0.put((Object)"minecraft:stray", (Object)"minecraft:stray_spawn_egg");
        $$0.put((Object)"minecraft:turtle", (Object)"minecraft:turtle_spawn_egg");
        $$0.put((Object)"minecraft:vex", (Object)"minecraft:vex_spawn_egg");
        $$0.put((Object)"minecraft:villager", (Object)"minecraft:villager_spawn_egg");
        $$0.put((Object)"minecraft:vindication_illager", (Object)"minecraft:vindication_illager_spawn_egg");
        $$0.put((Object)"minecraft:witch", (Object)"minecraft:witch_spawn_egg");
        $$0.put((Object)"minecraft:wither", (Object)"minecraft:wither_spawn_egg");
        $$0.put((Object)"minecraft:wither_skeleton", (Object)"minecraft:wither_skeleton_spawn_egg");
        $$0.put((Object)"minecraft:wolf", (Object)"minecraft:wolf_spawn_egg");
        $$0.put((Object)"minecraft:zombie", (Object)"minecraft:zombie_spawn_egg");
        $$0.put((Object)"minecraft:zombie_horse", (Object)"minecraft:zombie_horse_spawn_egg");
        $$0.put((Object)"minecraft:zombie_pigman", (Object)"minecraft:zombie_pigman_spawn_egg");
        $$0.put((Object)"minecraft:zombie_villager", (Object)"minecraft:zombie_villager_spawn_egg");
    });

    public ItemStackSpawnEggFix(Schema $$0, boolean $$1, String $$2) {
        super($$0, $$1);
        this.itemType = $$2;
    }

    public TypeRewriteRule makeRule() {
        Type $$0 = this.getInputSchema().getType(References.ITEM_STACK);
        OpticFinder $$1 = DSL.fieldFinder((String)"id", (Type)DSL.named((String)References.ITEM_NAME.typeName(), NamespacedSchema.namespacedString()));
        OpticFinder $$2 = DSL.fieldFinder((String)"id", NamespacedSchema.namespacedString());
        OpticFinder $$3 = $$0.findField("tag");
        OpticFinder $$42 = $$3.type().findField("EntityTag");
        return this.fixTypeEverywhereTyped("ItemInstanceSpawnEggFix" + this.getOutputSchema().getVersionKey(), $$0, $$4 -> {
            Typed $$6;
            Typed $$7;
            Optional $$8;
            Optional $$5 = $$4.getOptional($$1);
            if ($$5.isPresent() && Objects.equals((Object)((Pair)$$5.get()).getSecond(), (Object)this.itemType) && ($$8 = ($$7 = ($$6 = $$4.getOrCreateTyped($$3)).getOrCreateTyped($$42)).getOptional($$2)).isPresent()) {
                return $$4.set($$1, (Object)Pair.of((Object)References.ITEM_NAME.typeName(), (Object)((String)MAP.getOrDefault($$8.get(), (Object)"minecraft:pig_spawn_egg"))));
            }
            return $$4;
        });
    }
}