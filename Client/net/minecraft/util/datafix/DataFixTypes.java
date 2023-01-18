/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL$TypeReference
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  java.lang.Object
 */
package net.minecraft.util.datafix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.datafix.fixes.References;

public enum DataFixTypes {
    LEVEL(References.LEVEL),
    PLAYER(References.PLAYER),
    CHUNK(References.CHUNK),
    HOTBAR(References.HOTBAR),
    OPTIONS(References.OPTIONS),
    STRUCTURE(References.STRUCTURE),
    STATS(References.STATS),
    SAVED_DATA(References.SAVED_DATA),
    ADVANCEMENTS(References.ADVANCEMENTS),
    POI_CHUNK(References.POI_CHUNK),
    WORLD_GEN_SETTINGS(References.WORLD_GEN_SETTINGS),
    ENTITY_CHUNK(References.ENTITY_CHUNK);

    private final DSL.TypeReference type;

    private DataFixTypes(DSL.TypeReference $$0) {
        this.type = $$0;
    }

    public DSL.TypeReference getType() {
        return this.type;
    }

    private static int currentVersion() {
        return SharedConstants.getCurrentVersion().getDataVersion().getVersion();
    }

    public <T> Dynamic<T> update(DataFixer $$0, Dynamic<T> $$1, int $$2, int $$3) {
        return $$0.update(this.type, $$1, $$2, $$3);
    }

    public <T> Dynamic<T> updateToCurrentVersion(DataFixer $$0, Dynamic<T> $$1, int $$2) {
        return this.update($$0, $$1, $$2, DataFixTypes.currentVersion());
    }

    public CompoundTag update(DataFixer $$0, CompoundTag $$1, int $$2, int $$3) {
        return (CompoundTag)this.update($$0, new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)$$1), $$2, $$3).getValue();
    }

    public CompoundTag updateToCurrentVersion(DataFixer $$0, CompoundTag $$1, int $$2) {
        return this.update($$0, $$1, $$2, DataFixTypes.currentVersion());
    }
}