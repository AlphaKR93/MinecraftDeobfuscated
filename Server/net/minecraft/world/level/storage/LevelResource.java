/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 */
package net.minecraft.world.level.storage;

public class LevelResource {
    public static final LevelResource PLAYER_ADVANCEMENTS_DIR = new LevelResource("advancements");
    public static final LevelResource PLAYER_STATS_DIR = new LevelResource("stats");
    public static final LevelResource PLAYER_DATA_DIR = new LevelResource("playerdata");
    public static final LevelResource PLAYER_OLD_DATA_DIR = new LevelResource("players");
    public static final LevelResource LEVEL_DATA_FILE = new LevelResource("level.dat");
    public static final LevelResource OLD_LEVEL_DATA_FILE = new LevelResource("level.dat_old");
    public static final LevelResource ICON_FILE = new LevelResource("icon.png");
    public static final LevelResource LOCK_FILE = new LevelResource("session.lock");
    public static final LevelResource GENERATED_DIR = new LevelResource("generated");
    public static final LevelResource DATAPACK_DIR = new LevelResource("datapacks");
    public static final LevelResource MAP_RESOURCE_FILE = new LevelResource("resources.zip");
    public static final LevelResource ROOT = new LevelResource(".");
    private final String id;

    private LevelResource(String $$0) {
        this.id = $$0;
    }

    public String getId() {
        return this.id;
    }

    public String toString() {
        return "/" + this.id;
    }
}