/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 */
package net.minecraft.world.level.levelgen.structure;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

public class StructureFeatureIndexSavedData
extends SavedData {
    private static final String TAG_REMAINING_INDEXES = "Remaining";
    private static final String TAG_All_INDEXES = "All";
    private final LongSet all;
    private final LongSet remaining;

    private StructureFeatureIndexSavedData(LongSet $$0, LongSet $$1) {
        this.all = $$0;
        this.remaining = $$1;
    }

    public StructureFeatureIndexSavedData() {
        this((LongSet)new LongOpenHashSet(), (LongSet)new LongOpenHashSet());
    }

    public static StructureFeatureIndexSavedData load(CompoundTag $$0) {
        return new StructureFeatureIndexSavedData((LongSet)new LongOpenHashSet($$0.getLongArray(TAG_All_INDEXES)), (LongSet)new LongOpenHashSet($$0.getLongArray(TAG_REMAINING_INDEXES)));
    }

    @Override
    public CompoundTag save(CompoundTag $$0) {
        $$0.putLongArray(TAG_All_INDEXES, this.all.toLongArray());
        $$0.putLongArray(TAG_REMAINING_INDEXES, this.remaining.toLongArray());
        return $$0;
    }

    public void addIndex(long $$0) {
        this.all.add($$0);
        this.remaining.add($$0);
    }

    public boolean hasStartIndex(long $$0) {
        return this.all.contains($$0);
    }

    public boolean hasUnhandledIndex(long $$0) {
        return this.remaining.contains($$0);
    }

    public void removeIndex(long $$0) {
        this.remaining.remove($$0);
    }

    public LongSet getAll() {
        return this.all;
    }
}