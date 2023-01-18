/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.OptionalDynamic
 *  java.lang.Object
 *  java.lang.String
 */
package net.minecraft.world.level.storage;

import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import net.minecraft.SharedConstants;
import net.minecraft.world.level.storage.DataVersion;

public class LevelVersion {
    private final int levelDataVersion;
    private final long lastPlayed;
    private final String minecraftVersionName;
    private final DataVersion minecraftVersion;
    private final boolean snapshot;

    private LevelVersion(int $$0, long $$1, String $$2, int $$3, String $$4, boolean $$5) {
        this.levelDataVersion = $$0;
        this.lastPlayed = $$1;
        this.minecraftVersionName = $$2;
        this.minecraftVersion = new DataVersion($$3, $$4);
        this.snapshot = $$5;
    }

    public static LevelVersion parse(Dynamic<?> $$0) {
        int $$1 = $$0.get("version").asInt(0);
        long $$2 = $$0.get("LastPlayed").asLong(0L);
        OptionalDynamic $$3 = $$0.get("Version");
        if ($$3.result().isPresent()) {
            return new LevelVersion($$1, $$2, $$3.get("Name").asString(SharedConstants.getCurrentVersion().getName()), $$3.get("Id").asInt(SharedConstants.getCurrentVersion().getDataVersion().getVersion()), $$3.get("Series").asString(DataVersion.MAIN_SERIES), $$3.get("Snapshot").asBoolean(!SharedConstants.getCurrentVersion().isStable()));
        }
        return new LevelVersion($$1, $$2, "", 0, DataVersion.MAIN_SERIES, false);
    }

    public int levelDataVersion() {
        return this.levelDataVersion;
    }

    public long lastPlayed() {
        return this.lastPlayed;
    }

    public String minecraftVersionName() {
        return this.minecraftVersionName;
    }

    public DataVersion minecraftVersion() {
        return this.minecraftVersion;
    }

    public boolean snapshot() {
        return this.snapshot;
    }
}