/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 */
package com.mojang.realmsclient.util;

import com.mojang.realmsclient.util.LevelType;

public class WorldGenerationInfo {
    private final String seed;
    private final LevelType levelType;
    private final boolean generateStructures;

    public WorldGenerationInfo(String $$0, LevelType $$1, boolean $$2) {
        this.seed = $$0;
        this.levelType = $$1;
        this.generateStructures = $$2;
    }

    public String getSeed() {
        return this.seed;
    }

    public LevelType getLevelType() {
        return this.levelType;
    }

    public boolean shouldGenerateStructures() {
        return this.generateStructures;
    }
}