/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Maps
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongArrayList
 *  java.io.IOException
 *  java.lang.Long
 *  java.lang.Object
 *  java.lang.RuntimeException
 *  java.lang.String
 *  java.util.List
 *  java.util.Locale
 *  java.util.Map
 *  java.util.Set
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.StructureFeatureIndexSavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

public class LegacyStructureDataHandler {
    private static final Map<String, String> CURRENT_TO_LEGACY_MAP = (Map)Util.make(Maps.newHashMap(), $$0 -> {
        $$0.put((Object)"Village", (Object)"Village");
        $$0.put((Object)"Mineshaft", (Object)"Mineshaft");
        $$0.put((Object)"Mansion", (Object)"Mansion");
        $$0.put((Object)"Igloo", (Object)"Temple");
        $$0.put((Object)"Desert_Pyramid", (Object)"Temple");
        $$0.put((Object)"Jungle_Pyramid", (Object)"Temple");
        $$0.put((Object)"Swamp_Hut", (Object)"Temple");
        $$0.put((Object)"Stronghold", (Object)"Stronghold");
        $$0.put((Object)"Monument", (Object)"Monument");
        $$0.put((Object)"Fortress", (Object)"Fortress");
        $$0.put((Object)"EndCity", (Object)"EndCity");
    });
    private static final Map<String, String> LEGACY_TO_CURRENT_MAP = (Map)Util.make(Maps.newHashMap(), $$0 -> {
        $$0.put((Object)"Iglu", (Object)"Igloo");
        $$0.put((Object)"TeDP", (Object)"Desert_Pyramid");
        $$0.put((Object)"TeJP", (Object)"Jungle_Pyramid");
        $$0.put((Object)"TeSH", (Object)"Swamp_Hut");
    });
    private static final Set<String> OLD_STRUCTURE_REGISTRY_KEYS = Set.of((Object[])new String[]{"pillager_outpost", "mineshaft", "mansion", "jungle_pyramid", "desert_pyramid", "igloo", "ruined_portal", "shipwreck", "swamp_hut", "stronghold", "monument", "ocean_ruin", "fortress", "endcity", "buried_treasure", "village", "nether_fossil", "bastion_remnant"});
    private final boolean hasLegacyData;
    private final Map<String, Long2ObjectMap<CompoundTag>> dataMap = Maps.newHashMap();
    private final Map<String, StructureFeatureIndexSavedData> indexMap = Maps.newHashMap();
    private final List<String> legacyKeys;
    private final List<String> currentKeys;

    public LegacyStructureDataHandler(@Nullable DimensionDataStorage $$0, List<String> $$1, List<String> $$2) {
        this.legacyKeys = $$1;
        this.currentKeys = $$2;
        this.populateCaches($$0);
        boolean $$3 = false;
        for (String $$4 : this.currentKeys) {
            $$3 |= this.dataMap.get((Object)$$4) != null;
        }
        this.hasLegacyData = $$3;
    }

    public void removeIndex(long $$0) {
        for (String $$1 : this.legacyKeys) {
            StructureFeatureIndexSavedData $$2 = (StructureFeatureIndexSavedData)this.indexMap.get((Object)$$1);
            if ($$2 == null || !$$2.hasUnhandledIndex($$0)) continue;
            $$2.removeIndex($$0);
            $$2.setDirty();
        }
    }

    public CompoundTag updateFromLegacy(CompoundTag $$0) {
        CompoundTag $$1 = $$0.getCompound("Level");
        ChunkPos $$2 = new ChunkPos($$1.getInt("xPos"), $$1.getInt("zPos"));
        if (this.isUnhandledStructureStart($$2.x, $$2.z)) {
            $$0 = this.updateStructureStart($$0, $$2);
        }
        CompoundTag $$3 = $$1.getCompound("Structures");
        CompoundTag $$4 = $$3.getCompound("References");
        for (String $$5 : this.currentKeys) {
            boolean $$6 = OLD_STRUCTURE_REGISTRY_KEYS.contains((Object)$$5.toLowerCase(Locale.ROOT));
            if ($$4.contains($$5, 12) || !$$6) continue;
            int $$7 = 8;
            LongArrayList $$8 = new LongArrayList();
            for (int $$9 = $$2.x - 8; $$9 <= $$2.x + 8; ++$$9) {
                for (int $$10 = $$2.z - 8; $$10 <= $$2.z + 8; ++$$10) {
                    if (!this.hasLegacyStart($$9, $$10, $$5)) continue;
                    $$8.add(ChunkPos.asLong($$9, $$10));
                }
            }
            $$4.putLongArray($$5, (List<Long>)$$8);
        }
        $$3.put("References", $$4);
        $$1.put("Structures", $$3);
        $$0.put("Level", $$1);
        return $$0;
    }

    private boolean hasLegacyStart(int $$0, int $$1, String $$2) {
        if (!this.hasLegacyData) {
            return false;
        }
        return this.dataMap.get((Object)$$2) != null && ((StructureFeatureIndexSavedData)this.indexMap.get(CURRENT_TO_LEGACY_MAP.get((Object)$$2))).hasStartIndex(ChunkPos.asLong($$0, $$1));
    }

    private boolean isUnhandledStructureStart(int $$0, int $$1) {
        if (!this.hasLegacyData) {
            return false;
        }
        for (String $$2 : this.currentKeys) {
            if (this.dataMap.get((Object)$$2) == null || !((StructureFeatureIndexSavedData)this.indexMap.get(CURRENT_TO_LEGACY_MAP.get((Object)$$2))).hasUnhandledIndex(ChunkPos.asLong($$0, $$1))) continue;
            return true;
        }
        return false;
    }

    private CompoundTag updateStructureStart(CompoundTag $$0, ChunkPos $$1) {
        CompoundTag $$2 = $$0.getCompound("Level");
        CompoundTag $$3 = $$2.getCompound("Structures");
        CompoundTag $$4 = $$3.getCompound("Starts");
        for (String $$5 : this.currentKeys) {
            CompoundTag $$8;
            Long2ObjectMap $$6 = (Long2ObjectMap)this.dataMap.get((Object)$$5);
            if ($$6 == null) continue;
            long $$7 = $$1.toLong();
            if (!((StructureFeatureIndexSavedData)this.indexMap.get(CURRENT_TO_LEGACY_MAP.get((Object)$$5))).hasUnhandledIndex($$7) || ($$8 = (CompoundTag)$$6.get($$7)) == null) continue;
            $$4.put($$5, $$8);
        }
        $$3.put("Starts", $$4);
        $$2.put("Structures", $$3);
        $$0.put("Level", $$2);
        return $$0;
    }

    private void populateCaches(@Nullable DimensionDataStorage $$02) {
        if ($$02 == null) {
            return;
        }
        for (String $$1 : this.legacyKeys) {
            CompoundTag $$2 = new CompoundTag();
            try {
                $$2 = $$02.readTagFromDisk($$1, 1493).getCompound("data").getCompound("Features");
                if ($$2.isEmpty()) {
                    continue;
                }
            }
            catch (IOException iOException) {
                // empty catch block
            }
            for (String $$3 : $$2.getAllKeys()) {
                String $$7;
                String $$8;
                CompoundTag $$4 = $$2.getCompound($$3);
                long $$5 = ChunkPos.asLong($$4.getInt("ChunkX"), $$4.getInt("ChunkZ"));
                ListTag $$6 = $$4.getList("Children", 10);
                if (!$$6.isEmpty() && ($$8 = (String)LEGACY_TO_CURRENT_MAP.get((Object)($$7 = $$6.getCompound(0).getString("id")))) != null) {
                    $$4.putString("id", $$8);
                }
                String $$9 = $$4.getString("id");
                ((Long2ObjectMap)this.dataMap.computeIfAbsent((Object)$$9, $$0 -> new Long2ObjectOpenHashMap())).put($$5, (Object)$$4);
            }
            String $$10 = $$1 + "_index";
            StructureFeatureIndexSavedData $$11 = (StructureFeatureIndexSavedData)$$02.computeIfAbsent(StructureFeatureIndexSavedData::load, StructureFeatureIndexSavedData::new, $$10);
            if ($$11.getAll().isEmpty()) {
                StructureFeatureIndexSavedData $$12 = new StructureFeatureIndexSavedData();
                this.indexMap.put((Object)$$1, (Object)$$12);
                for (String $$13 : $$2.getAllKeys()) {
                    CompoundTag $$14 = $$2.getCompound($$13);
                    $$12.addIndex(ChunkPos.asLong($$14.getInt("ChunkX"), $$14.getInt("ChunkZ")));
                }
                $$12.setDirty();
                continue;
            }
            this.indexMap.put((Object)$$1, (Object)$$11);
        }
    }

    public static LegacyStructureDataHandler getLegacyStructureHandler(ResourceKey<Level> $$0, @Nullable DimensionDataStorage $$1) {
        if ($$0 == Level.OVERWORLD) {
            return new LegacyStructureDataHandler($$1, (List<String>)ImmutableList.of((Object)"Monument", (Object)"Stronghold", (Object)"Village", (Object)"Mineshaft", (Object)"Temple", (Object)"Mansion"), (List<String>)ImmutableList.of((Object)"Village", (Object)"Mineshaft", (Object)"Mansion", (Object)"Igloo", (Object)"Desert_Pyramid", (Object)"Jungle_Pyramid", (Object)"Swamp_Hut", (Object)"Stronghold", (Object)"Monument"));
        }
        if ($$0 == Level.NETHER) {
            ImmutableList $$2 = ImmutableList.of((Object)"Fortress");
            return new LegacyStructureDataHandler($$1, (List<String>)$$2, (List<String>)$$2);
        }
        if ($$0 == Level.END) {
            ImmutableList $$3 = ImmutableList.of((Object)"EndCity");
            return new LegacyStructureDataHandler($$1, (List<String>)$$3, (List<String>)$$3);
        }
        throw new RuntimeException(String.format((Locale)Locale.ROOT, (String)"Unknown dimension type : %s", (Object[])new Object[]{$$0}));
    }
}