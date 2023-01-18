/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 */
package net.minecraft.data.recipes;

public enum RecipeCategory {
    BUILDING_BLOCKS("building_blocks"),
    DECORATIONS("decorations"),
    REDSTONE("redstone"),
    TRANSPORTATION("transportation"),
    TOOLS("tools"),
    COMBAT("combat"),
    FOOD("food"),
    BREWING("brewing"),
    MISC("misc");

    private final String recipeFolderName;

    private RecipeCategory(String $$0) {
        this.recipeFolderName = $$0;
    }

    public String getFolderName() {
        return this.recipeFolderName;
    }
}