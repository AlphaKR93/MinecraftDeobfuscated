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

public class RenamedCoralFansFix {
    public static final Map<String, String> RENAMED_IDS = ImmutableMap.builder().put((Object)"minecraft:tube_coral_fan", (Object)"minecraft:tube_coral_wall_fan").put((Object)"minecraft:brain_coral_fan", (Object)"minecraft:brain_coral_wall_fan").put((Object)"minecraft:bubble_coral_fan", (Object)"minecraft:bubble_coral_wall_fan").put((Object)"minecraft:fire_coral_fan", (Object)"minecraft:fire_coral_wall_fan").put((Object)"minecraft:horn_coral_fan", (Object)"minecraft:horn_coral_wall_fan").build();
}