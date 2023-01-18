/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.schemas.Schema
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Map
 *  java.util.function.Function
 */
package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.RecipesRenameFix;

public class RecipesRenameningFix
extends RecipesRenameFix {
    private static final Map<String, String> RECIPES = ImmutableMap.builder().put((Object)"minecraft:acacia_bark", (Object)"minecraft:acacia_wood").put((Object)"minecraft:birch_bark", (Object)"minecraft:birch_wood").put((Object)"minecraft:dark_oak_bark", (Object)"minecraft:dark_oak_wood").put((Object)"minecraft:jungle_bark", (Object)"minecraft:jungle_wood").put((Object)"minecraft:oak_bark", (Object)"minecraft:oak_wood").put((Object)"minecraft:spruce_bark", (Object)"minecraft:spruce_wood").build();

    public RecipesRenameningFix(Schema $$02, boolean $$1) {
        super($$02, $$1, "Recipes renamening fix", (Function<String, String>)((Function)$$0 -> (String)RECIPES.getOrDefault($$0, $$0)));
    }
}