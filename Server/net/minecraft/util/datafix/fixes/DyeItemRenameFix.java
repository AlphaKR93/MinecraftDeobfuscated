/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Map
 */
package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import java.util.Map;

public class DyeItemRenameFix {
    public static final Map<String, String> RENAMED_IDS = ImmutableMap.builder().put((Object)"minecraft:cactus_green", (Object)"minecraft:green_dye").put((Object)"minecraft:rose_red", (Object)"minecraft:red_dye").put((Object)"minecraft:dandelion_yellow", (Object)"minecraft:yellow_dye").build();
}