/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Dynamic
 *  it.unimi.dsi.fastutil.objects.Object2IntArrayMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMap$Entry
 *  java.lang.IllegalStateException
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.Comparator
 *  java.util.List
 *  java.util.Locale
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.Optional
 *  java.util.function.Function
 */
package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.References;

public class StructuresBecomeConfiguredFix
extends DataFix {
    private static final Map<String, Conversion> CONVERSION_MAP = ImmutableMap.builder().put((Object)"mineshaft", (Object)Conversion.biomeMapped((Map<List<String>, String>)Map.of((Object)List.of((Object)"minecraft:badlands", (Object)"minecraft:eroded_badlands", (Object)"minecraft:wooded_badlands"), (Object)"minecraft:mineshaft_mesa"), "minecraft:mineshaft")).put((Object)"shipwreck", (Object)Conversion.biomeMapped((Map<List<String>, String>)Map.of((Object)List.of((Object)"minecraft:beach", (Object)"minecraft:snowy_beach"), (Object)"minecraft:shipwreck_beached"), "minecraft:shipwreck")).put((Object)"ocean_ruin", (Object)Conversion.biomeMapped((Map<List<String>, String>)Map.of((Object)List.of((Object)"minecraft:warm_ocean", (Object)"minecraft:lukewarm_ocean", (Object)"minecraft:deep_lukewarm_ocean"), (Object)"minecraft:ocean_ruin_warm"), "minecraft:ocean_ruin_cold")).put((Object)"village", (Object)Conversion.biomeMapped((Map<List<String>, String>)Map.of((Object)List.of((Object)"minecraft:desert"), (Object)"minecraft:village_desert", (Object)List.of((Object)"minecraft:savanna"), (Object)"minecraft:village_savanna", (Object)List.of((Object)"minecraft:snowy_plains"), (Object)"minecraft:village_snowy", (Object)List.of((Object)"minecraft:taiga"), (Object)"minecraft:village_taiga"), "minecraft:village_plains")).put((Object)"ruined_portal", (Object)Conversion.biomeMapped((Map<List<String>, String>)Map.of((Object)List.of((Object)"minecraft:desert"), (Object)"minecraft:ruined_portal_desert", (Object)List.of((Object[])new String[]{"minecraft:badlands", "minecraft:eroded_badlands", "minecraft:wooded_badlands", "minecraft:windswept_hills", "minecraft:windswept_forest", "minecraft:windswept_gravelly_hills", "minecraft:savanna_plateau", "minecraft:windswept_savanna", "minecraft:stony_shore", "minecraft:meadow", "minecraft:frozen_peaks", "minecraft:jagged_peaks", "minecraft:stony_peaks", "minecraft:snowy_slopes"}), (Object)"minecraft:ruined_portal_mountain", (Object)List.of((Object)"minecraft:bamboo_jungle", (Object)"minecraft:jungle", (Object)"minecraft:sparse_jungle"), (Object)"minecraft:ruined_portal_jungle", (Object)List.of((Object)"minecraft:deep_frozen_ocean", (Object)"minecraft:deep_cold_ocean", (Object)"minecraft:deep_ocean", (Object)"minecraft:deep_lukewarm_ocean", (Object)"minecraft:frozen_ocean", (Object)"minecraft:ocean", (Object)"minecraft:cold_ocean", (Object)"minecraft:lukewarm_ocean", (Object)"minecraft:warm_ocean"), (Object)"minecraft:ruined_portal_ocean"), "minecraft:ruined_portal")).put((Object)"pillager_outpost", (Object)Conversion.trivial("minecraft:pillager_outpost")).put((Object)"mansion", (Object)Conversion.trivial("minecraft:mansion")).put((Object)"jungle_pyramid", (Object)Conversion.trivial("minecraft:jungle_pyramid")).put((Object)"desert_pyramid", (Object)Conversion.trivial("minecraft:desert_pyramid")).put((Object)"igloo", (Object)Conversion.trivial("minecraft:igloo")).put((Object)"swamp_hut", (Object)Conversion.trivial("minecraft:swamp_hut")).put((Object)"stronghold", (Object)Conversion.trivial("minecraft:stronghold")).put((Object)"monument", (Object)Conversion.trivial("minecraft:monument")).put((Object)"fortress", (Object)Conversion.trivial("minecraft:fortress")).put((Object)"endcity", (Object)Conversion.trivial("minecraft:end_city")).put((Object)"buried_treasure", (Object)Conversion.trivial("minecraft:buried_treasure")).put((Object)"nether_fossil", (Object)Conversion.trivial("minecraft:nether_fossil")).put((Object)"bastion_remnant", (Object)Conversion.trivial("minecraft:bastion_remnant")).build();

    public StructuresBecomeConfiguredFix(Schema $$0) {
        super($$0, false);
    }

    protected TypeRewriteRule makeRule() {
        Type $$0 = this.getInputSchema().getType(References.CHUNK);
        Type $$1 = this.getInputSchema().getType(References.CHUNK);
        return this.writeFixAndRead("StucturesToConfiguredStructures", $$0, $$1, this::fix);
    }

    private Dynamic<?> fix(Dynamic<?> $$0) {
        return $$0.update("structures", $$12 -> $$12.update("starts", $$1 -> this.updateStarts((Dynamic<?>)$$1, $$0)).update("References", $$1 -> this.updateReferences((Dynamic<?>)$$1, $$0)));
    }

    private Dynamic<?> updateStarts(Dynamic<?> $$0, Dynamic<?> $$12) {
        Map $$2 = (Map)$$0.getMapValues().result().get();
        ArrayList $$3 = new ArrayList();
        $$2.forEach((arg_0, arg_1) -> StructuresBecomeConfiguredFix.lambda$updateStarts$3((List)$$3, arg_0, arg_1));
        for (Dynamic $$4 : $$3) {
            $$0 = $$0.remove($$4.asString(""));
        }
        return $$0.updateMapValues($$1 -> this.updateStart((Pair<Dynamic<?>, Dynamic<?>>)$$1, $$12));
    }

    private Pair<Dynamic<?>, Dynamic<?>> updateStart(Pair<Dynamic<?>, Dynamic<?>> $$0, Dynamic<?> $$1) {
        Dynamic<?> $$2 = this.findUpdatedStructureType($$0, $$1);
        return new Pair($$2, (Object)((Dynamic)$$0.getSecond()).set("id", $$2));
    }

    private Dynamic<?> updateReferences(Dynamic<?> $$0, Dynamic<?> $$12) {
        Map $$2 = (Map)$$0.getMapValues().result().get();
        ArrayList $$3 = new ArrayList();
        $$2.forEach((arg_0, arg_1) -> StructuresBecomeConfiguredFix.lambda$updateReferences$5((List)$$3, arg_0, arg_1));
        for (Dynamic $$4 : $$3) {
            $$0 = $$0.remove($$4.asString(""));
        }
        return $$0.updateMapValues($$1 -> this.updateReference((Pair<Dynamic<?>, Dynamic<?>>)$$1, $$12));
    }

    private Pair<Dynamic<?>, Dynamic<?>> updateReference(Pair<Dynamic<?>, Dynamic<?>> $$0, Dynamic<?> $$1) {
        return $$0.mapFirst($$2 -> this.findUpdatedStructureType($$0, $$1));
    }

    private Dynamic<?> findUpdatedStructureType(Pair<Dynamic<?>, Dynamic<?>> $$0, Dynamic<?> $$1) {
        Optional<String> $$6;
        String $$2 = ((Dynamic)$$0.getFirst()).asString("UNKNOWN").toLowerCase(Locale.ROOT);
        Conversion $$3 = (Conversion)((Object)CONVERSION_MAP.get((Object)$$2));
        if ($$3 == null) {
            throw new IllegalStateException("Found unknown structure: " + $$2);
        }
        Dynamic $$4 = (Dynamic)$$0.getSecond();
        String $$5 = $$3.fallback;
        if (!$$3.biomeMapping().isEmpty() && ($$6 = this.guessConfiguration($$1, $$3)).isPresent()) {
            $$5 = (String)$$6.get();
        }
        Dynamic $$7 = $$4.createString($$5);
        return $$7;
    }

    private Optional<String> guessConfiguration(Dynamic<?> $$0, Conversion $$1) {
        Object2IntArrayMap $$2 = new Object2IntArrayMap();
        $$0.get("sections").asList(Function.identity()).forEach($$22 -> $$22.get("biomes").get("palette").asList(Function.identity()).forEach($$2 -> {
            String $$3 = (String)$$1.biomeMapping().get((Object)$$2.asString(""));
            if ($$3 != null) {
                $$2.mergeInt((Object)$$3, 1, Integer::sum);
            }
        }));
        return $$2.object2IntEntrySet().stream().max(Comparator.comparingInt(Object2IntMap.Entry::getIntValue)).map(Map.Entry::getKey);
    }

    private static /* synthetic */ void lambda$updateReferences$5(List $$0, Dynamic $$1, Dynamic $$2) {
        if ($$2.asLongStream().count() == 0L) {
            $$0.add((Object)$$1);
        }
    }

    private static /* synthetic */ void lambda$updateStarts$3(List $$0, Dynamic $$1, Dynamic $$2) {
        if ($$2.get("id").asString("INVALID").equals((Object)"INVALID")) {
            $$0.add((Object)$$1);
        }
    }

    record Conversion(Map<String, String> biomeMapping, String fallback) {
        public static Conversion trivial(String $$0) {
            return new Conversion((Map<String, String>)Map.of(), $$0);
        }

        public static Conversion biomeMapped(Map<List<String>, String> $$0, String $$1) {
            return new Conversion(Conversion.unpack($$0), $$1);
        }

        private static Map<String, String> unpack(Map<List<String>, String> $$0) {
            ImmutableMap.Builder $$1 = ImmutableMap.builder();
            for (Map.Entry $$22 : $$0.entrySet()) {
                ((List)$$22.getKey()).forEach($$2 -> $$1.put($$2, (Object)((String)$$22.getValue())));
            }
            return $$1.build();
        }
    }
}