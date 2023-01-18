/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.Splitter
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  java.lang.CharSequence
 *  java.lang.IllegalStateException
 *  java.lang.Integer
 *  java.lang.NumberFormatException
 *  java.lang.Object
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.Collections
 *  java.util.HashMap
 *  java.util.Iterator
 *  java.util.List
 *  java.util.Locale
 *  java.util.Map
 *  java.util.Optional
 *  java.util.stream.Collectors
 *  javax.annotation.Nullable
 */
package net.minecraft.util.datafix.fixes;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.datafix.fixes.References;

public class LevelDataGeneratorOptionsFix
extends DataFix {
    static final Map<String, String> MAP = (Map)Util.make(Maps.newHashMap(), $$0 -> {
        $$0.put((Object)"0", (Object)"minecraft:ocean");
        $$0.put((Object)"1", (Object)"minecraft:plains");
        $$0.put((Object)"2", (Object)"minecraft:desert");
        $$0.put((Object)"3", (Object)"minecraft:mountains");
        $$0.put((Object)"4", (Object)"minecraft:forest");
        $$0.put((Object)"5", (Object)"minecraft:taiga");
        $$0.put((Object)"6", (Object)"minecraft:swamp");
        $$0.put((Object)"7", (Object)"minecraft:river");
        $$0.put((Object)"8", (Object)"minecraft:nether");
        $$0.put((Object)"9", (Object)"minecraft:the_end");
        $$0.put((Object)"10", (Object)"minecraft:frozen_ocean");
        $$0.put((Object)"11", (Object)"minecraft:frozen_river");
        $$0.put((Object)"12", (Object)"minecraft:snowy_tundra");
        $$0.put((Object)"13", (Object)"minecraft:snowy_mountains");
        $$0.put((Object)"14", (Object)"minecraft:mushroom_fields");
        $$0.put((Object)"15", (Object)"minecraft:mushroom_field_shore");
        $$0.put((Object)"16", (Object)"minecraft:beach");
        $$0.put((Object)"17", (Object)"minecraft:desert_hills");
        $$0.put((Object)"18", (Object)"minecraft:wooded_hills");
        $$0.put((Object)"19", (Object)"minecraft:taiga_hills");
        $$0.put((Object)"20", (Object)"minecraft:mountain_edge");
        $$0.put((Object)"21", (Object)"minecraft:jungle");
        $$0.put((Object)"22", (Object)"minecraft:jungle_hills");
        $$0.put((Object)"23", (Object)"minecraft:jungle_edge");
        $$0.put((Object)"24", (Object)"minecraft:deep_ocean");
        $$0.put((Object)"25", (Object)"minecraft:stone_shore");
        $$0.put((Object)"26", (Object)"minecraft:snowy_beach");
        $$0.put((Object)"27", (Object)"minecraft:birch_forest");
        $$0.put((Object)"28", (Object)"minecraft:birch_forest_hills");
        $$0.put((Object)"29", (Object)"minecraft:dark_forest");
        $$0.put((Object)"30", (Object)"minecraft:snowy_taiga");
        $$0.put((Object)"31", (Object)"minecraft:snowy_taiga_hills");
        $$0.put((Object)"32", (Object)"minecraft:giant_tree_taiga");
        $$0.put((Object)"33", (Object)"minecraft:giant_tree_taiga_hills");
        $$0.put((Object)"34", (Object)"minecraft:wooded_mountains");
        $$0.put((Object)"35", (Object)"minecraft:savanna");
        $$0.put((Object)"36", (Object)"minecraft:savanna_plateau");
        $$0.put((Object)"37", (Object)"minecraft:badlands");
        $$0.put((Object)"38", (Object)"minecraft:wooded_badlands_plateau");
        $$0.put((Object)"39", (Object)"minecraft:badlands_plateau");
        $$0.put((Object)"40", (Object)"minecraft:small_end_islands");
        $$0.put((Object)"41", (Object)"minecraft:end_midlands");
        $$0.put((Object)"42", (Object)"minecraft:end_highlands");
        $$0.put((Object)"43", (Object)"minecraft:end_barrens");
        $$0.put((Object)"44", (Object)"minecraft:warm_ocean");
        $$0.put((Object)"45", (Object)"minecraft:lukewarm_ocean");
        $$0.put((Object)"46", (Object)"minecraft:cold_ocean");
        $$0.put((Object)"47", (Object)"minecraft:deep_warm_ocean");
        $$0.put((Object)"48", (Object)"minecraft:deep_lukewarm_ocean");
        $$0.put((Object)"49", (Object)"minecraft:deep_cold_ocean");
        $$0.put((Object)"50", (Object)"minecraft:deep_frozen_ocean");
        $$0.put((Object)"127", (Object)"minecraft:the_void");
        $$0.put((Object)"129", (Object)"minecraft:sunflower_plains");
        $$0.put((Object)"130", (Object)"minecraft:desert_lakes");
        $$0.put((Object)"131", (Object)"minecraft:gravelly_mountains");
        $$0.put((Object)"132", (Object)"minecraft:flower_forest");
        $$0.put((Object)"133", (Object)"minecraft:taiga_mountains");
        $$0.put((Object)"134", (Object)"minecraft:swamp_hills");
        $$0.put((Object)"140", (Object)"minecraft:ice_spikes");
        $$0.put((Object)"149", (Object)"minecraft:modified_jungle");
        $$0.put((Object)"151", (Object)"minecraft:modified_jungle_edge");
        $$0.put((Object)"155", (Object)"minecraft:tall_birch_forest");
        $$0.put((Object)"156", (Object)"minecraft:tall_birch_hills");
        $$0.put((Object)"157", (Object)"minecraft:dark_forest_hills");
        $$0.put((Object)"158", (Object)"minecraft:snowy_taiga_mountains");
        $$0.put((Object)"160", (Object)"minecraft:giant_spruce_taiga");
        $$0.put((Object)"161", (Object)"minecraft:giant_spruce_taiga_hills");
        $$0.put((Object)"162", (Object)"minecraft:modified_gravelly_mountains");
        $$0.put((Object)"163", (Object)"minecraft:shattered_savanna");
        $$0.put((Object)"164", (Object)"minecraft:shattered_savanna_plateau");
        $$0.put((Object)"165", (Object)"minecraft:eroded_badlands");
        $$0.put((Object)"166", (Object)"minecraft:modified_wooded_badlands_plateau");
        $$0.put((Object)"167", (Object)"minecraft:modified_badlands_plateau");
    });
    public static final String GENERATOR_OPTIONS = "generatorOptions";

    public LevelDataGeneratorOptionsFix(Schema $$0, boolean $$1) {
        super($$0, $$1);
    }

    protected TypeRewriteRule makeRule() {
        Type $$0 = this.getOutputSchema().getType(References.LEVEL);
        return this.fixTypeEverywhereTyped("LevelDataGeneratorOptionsFix", this.getInputSchema().getType(References.LEVEL), $$0, $$12 -> (Typed)$$12.write().flatMap($$1 -> {
            Dynamic $$7;
            Optional $$2 = $$1.get(GENERATOR_OPTIONS).asString().result();
            if ("flat".equalsIgnoreCase($$1.get("generatorName").asString(""))) {
                String $$3 = (String)$$2.orElse((Object)"");
                Dynamic $$4 = $$1.set(GENERATOR_OPTIONS, LevelDataGeneratorOptionsFix.convert($$3, $$1.getOps()));
            } else if ("buffet".equalsIgnoreCase($$1.get("generatorName").asString("")) && $$2.isPresent()) {
                Dynamic $$5 = new Dynamic((DynamicOps)JsonOps.INSTANCE, (Object)GsonHelper.parse((String)$$2.get(), true));
                Dynamic $$6 = $$1.set(GENERATOR_OPTIONS, $$5.convert($$1.getOps()));
            } else {
                $$7 = $$1;
            }
            return $$0.readTyped($$7);
        }).map(Pair::getFirst).result().orElseThrow(() -> new IllegalStateException("Could not read new level type.")));
    }

    private static <T> Dynamic<T> convert(String $$0, DynamicOps<T> $$13) {
        ArrayList $$122;
        Iterator $$2 = Splitter.on((char)';').split((CharSequence)$$0).iterator();
        String $$3 = "minecraft:plains";
        HashMap $$4 = Maps.newHashMap();
        if (!$$0.isEmpty() && $$2.hasNext()) {
            List<Pair<Integer, String>> $$5 = LevelDataGeneratorOptionsFix.getLayersInfoFromString((String)$$2.next());
            if (!$$5.isEmpty()) {
                if ($$2.hasNext()) {
                    $$3 = (String)MAP.getOrDefault($$2.next(), (Object)"minecraft:plains");
                }
                if ($$2.hasNext()) {
                    String[] $$6;
                    for (String $$7 : $$6 = ((String)$$2.next()).toLowerCase(Locale.ROOT).split(",")) {
                        String[] $$9;
                        String[] $$8 = $$7.split("\\(", 2);
                        if ($$8[0].isEmpty()) continue;
                        $$4.put((Object)$$8[0], (Object)Maps.newHashMap());
                        if ($$8.length <= 1 || !$$8[1].endsWith(")") || $$8[1].length() <= 1) continue;
                        for (String $$10 : $$9 = $$8[1].substring(0, $$8[1].length() - 1).split(" ")) {
                            String[] $$11 = $$10.split("=", 2);
                            if ($$11.length != 2) continue;
                            ((Map)$$4.get((Object)$$8[0])).put((Object)$$11[0], (Object)$$11[1]);
                        }
                    }
                } else {
                    $$4.put((Object)"village", (Object)Maps.newHashMap());
                }
            }
        } else {
            $$122 = Lists.newArrayList();
            $$122.add((Object)Pair.of((Object)1, (Object)"minecraft:bedrock"));
            $$122.add((Object)Pair.of((Object)2, (Object)"minecraft:dirt"));
            $$122.add((Object)Pair.of((Object)1, (Object)"minecraft:grass_block"));
            $$4.put((Object)"village", (Object)Maps.newHashMap());
        }
        Object $$132 = $$13.createList($$122.stream().map($$1 -> $$13.createMap((Map)ImmutableMap.of((Object)$$13.createString("height"), (Object)$$13.createInt(((Integer)$$1.getFirst()).intValue()), (Object)$$13.createString("block"), (Object)$$13.createString((String)$$1.getSecond())))));
        Object $$14 = $$13.createMap((Map)$$4.entrySet().stream().map($$12 -> Pair.of((Object)$$13.createString(((String)$$12.getKey()).toLowerCase(Locale.ROOT)), (Object)$$13.createMap((Map)((Map)$$12.getValue()).entrySet().stream().map($$1 -> Pair.of((Object)$$13.createString((String)$$1.getKey()), (Object)$$13.createString((String)$$1.getValue()))).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))))).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
        return new Dynamic($$13, $$13.createMap((Map)ImmutableMap.of((Object)$$13.createString("layers"), (Object)$$132, (Object)$$13.createString("biome"), (Object)$$13.createString($$3), (Object)$$13.createString("structures"), (Object)$$14)));
    }

    @Nullable
    private static Pair<Integer, String> getLayerInfoFromString(String $$0) {
        int $$4;
        String[] $$1 = $$0.split("\\*", 2);
        if ($$1.length == 2) {
            try {
                int $$2 = Integer.parseInt((String)$$1[0]);
            }
            catch (NumberFormatException $$3) {
                return null;
            }
        } else {
            $$4 = 1;
        }
        String $$5 = $$1[$$1.length - 1];
        return Pair.of((Object)$$4, (Object)$$5);
    }

    private static List<Pair<Integer, String>> getLayersInfoFromString(String $$0) {
        String[] $$2;
        ArrayList $$1 = Lists.newArrayList();
        for (String $$3 : $$2 = $$0.split(",")) {
            Pair<Integer, String> $$4 = LevelDataGeneratorOptionsFix.getLayerInfoFromString($$3);
            if ($$4 == null) {
                return Collections.emptyList();
            }
            $$1.add($$4);
        }
        return $$1;
    }
}