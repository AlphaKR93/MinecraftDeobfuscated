/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 */
package net.minecraft.server.packs;

public enum PackType {
    CLIENT_RESOURCES("assets"),
    SERVER_DATA("data");

    private final String directory;

    private PackType(String $$0) {
        this.directory = $$0;
    }

    public String getDirectory() {
        return this.directory;
    }
}