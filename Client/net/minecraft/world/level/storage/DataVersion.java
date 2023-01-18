/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 */
package net.minecraft.world.level.storage;

public class DataVersion {
    private final int version;
    private final String series;
    public static String MAIN_SERIES = "main";

    public DataVersion(int $$0) {
        this($$0, MAIN_SERIES);
    }

    public DataVersion(int $$0, String $$1) {
        this.version = $$0;
        this.series = $$1;
    }

    public boolean isSideSeries() {
        return !this.series.equals((Object)MAIN_SERIES);
    }

    public String getSeries() {
        return this.series;
    }

    public int getVersion() {
        return this.version;
    }

    public boolean isCompatible(DataVersion $$0) {
        return this.getSeries().equals((Object)$$0.getSeries());
    }
}