/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Map
 */
package net.minecraft.world.entity.npc;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

public final class VillagerType {
    public static final VillagerType DESERT = VillagerType.register("desert");
    public static final VillagerType JUNGLE = VillagerType.register("jungle");
    public static final VillagerType PLAINS = VillagerType.register("plains");
    public static final VillagerType SAVANNA = VillagerType.register("savanna");
    public static final VillagerType SNOW = VillagerType.register("snow");
    public static final VillagerType SWAMP = VillagerType.register("swamp");
    public static final VillagerType TAIGA = VillagerType.register("taiga");
    private final String name;
    private static final Map<ResourceKey<Biome>, VillagerType> BY_BIOME = (Map)Util.make(Maps.newHashMap(), $$0 -> {
        $$0.put(Biomes.BADLANDS, (Object)DESERT);
        $$0.put(Biomes.DESERT, (Object)DESERT);
        $$0.put(Biomes.ERODED_BADLANDS, (Object)DESERT);
        $$0.put(Biomes.WOODED_BADLANDS, (Object)DESERT);
        $$0.put(Biomes.BAMBOO_JUNGLE, (Object)JUNGLE);
        $$0.put(Biomes.JUNGLE, (Object)JUNGLE);
        $$0.put(Biomes.SPARSE_JUNGLE, (Object)JUNGLE);
        $$0.put(Biomes.SAVANNA_PLATEAU, (Object)SAVANNA);
        $$0.put(Biomes.SAVANNA, (Object)SAVANNA);
        $$0.put(Biomes.WINDSWEPT_SAVANNA, (Object)SAVANNA);
        $$0.put(Biomes.DEEP_FROZEN_OCEAN, (Object)SNOW);
        $$0.put(Biomes.FROZEN_OCEAN, (Object)SNOW);
        $$0.put(Biomes.FROZEN_RIVER, (Object)SNOW);
        $$0.put(Biomes.ICE_SPIKES, (Object)SNOW);
        $$0.put(Biomes.SNOWY_BEACH, (Object)SNOW);
        $$0.put(Biomes.SNOWY_TAIGA, (Object)SNOW);
        $$0.put(Biomes.SNOWY_PLAINS, (Object)SNOW);
        $$0.put(Biomes.GROVE, (Object)SNOW);
        $$0.put(Biomes.SNOWY_SLOPES, (Object)SNOW);
        $$0.put(Biomes.FROZEN_PEAKS, (Object)SNOW);
        $$0.put(Biomes.JAGGED_PEAKS, (Object)SNOW);
        $$0.put(Biomes.SWAMP, (Object)SWAMP);
        $$0.put(Biomes.MANGROVE_SWAMP, (Object)SWAMP);
        $$0.put(Biomes.OLD_GROWTH_SPRUCE_TAIGA, (Object)TAIGA);
        $$0.put(Biomes.OLD_GROWTH_PINE_TAIGA, (Object)TAIGA);
        $$0.put(Biomes.WINDSWEPT_GRAVELLY_HILLS, (Object)TAIGA);
        $$0.put(Biomes.WINDSWEPT_HILLS, (Object)TAIGA);
        $$0.put(Biomes.TAIGA, (Object)TAIGA);
        $$0.put(Biomes.WINDSWEPT_FOREST, (Object)TAIGA);
    });

    private VillagerType(String $$0) {
        this.name = $$0;
    }

    public String toString() {
        return this.name;
    }

    private static VillagerType register(String $$0) {
        return Registry.register(BuiltInRegistries.VILLAGER_TYPE, new ResourceLocation($$0), new VillagerType($$0));
    }

    public static VillagerType byBiome(Holder<Biome> $$0) {
        return (VillagerType)$$0.unwrapKey().map(arg_0 -> BY_BIOME.get(arg_0)).orElse((Object)PLAINS);
    }
}