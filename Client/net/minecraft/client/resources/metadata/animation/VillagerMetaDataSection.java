/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Arrays
 *  java.util.Map
 *  java.util.stream.Collectors
 */
package net.minecraft.client.resources.metadata.animation;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.client.resources.metadata.animation.VillagerMetadataSectionSerializer;

public class VillagerMetaDataSection {
    public static final VillagerMetadataSectionSerializer SERIALIZER = new VillagerMetadataSectionSerializer();
    public static final String SECTION_NAME = "villager";
    private final Hat hat;

    public VillagerMetaDataSection(Hat $$0) {
        this.hat = $$0;
    }

    public Hat getHat() {
        return this.hat;
    }

    public static enum Hat {
        NONE("none"),
        PARTIAL("partial"),
        FULL("full");

        private static final Map<String, Hat> BY_NAME;
        private final String name;

        private Hat(String $$0) {
            this.name = $$0;
        }

        public String getName() {
            return this.name;
        }

        public static Hat getByName(String $$0) {
            return (Hat)((Object)BY_NAME.getOrDefault((Object)$$0, (Object)NONE));
        }

        static {
            BY_NAME = (Map)Arrays.stream((Object[])Hat.values()).collect(Collectors.toMap(Hat::getName, $$0 -> $$0));
        }
    }
}