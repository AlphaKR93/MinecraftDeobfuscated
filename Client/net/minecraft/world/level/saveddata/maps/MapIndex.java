/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMap$Entry
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 */
package net.minecraft.world.level.saveddata.maps;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

public class MapIndex
extends SavedData {
    public static final String FILE_NAME = "idcounts";
    private final Object2IntMap<String> usedAuxIds = new Object2IntOpenHashMap();

    public MapIndex() {
        this.usedAuxIds.defaultReturnValue(-1);
    }

    public static MapIndex load(CompoundTag $$0) {
        MapIndex $$1 = new MapIndex();
        for (String $$2 : $$0.getAllKeys()) {
            if (!$$0.contains($$2, 99)) continue;
            $$1.usedAuxIds.put((Object)$$2, $$0.getInt($$2));
        }
        return $$1;
    }

    @Override
    public CompoundTag save(CompoundTag $$0) {
        for (Object2IntMap.Entry $$1 : this.usedAuxIds.object2IntEntrySet()) {
            $$0.putInt((String)$$1.getKey(), $$1.getIntValue());
        }
        return $$0;
    }

    public int getFreeAuxValueForMap() {
        int $$0 = this.usedAuxIds.getInt((Object)"map") + 1;
        this.usedAuxIds.put((Object)"map", $$0);
        this.setDirty();
        return $$0;
    }
}