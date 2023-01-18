/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.util.Map
 */
package net.minecraft.server.packs;

import java.util.Map;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;

public class BuiltInMetadata {
    private static final BuiltInMetadata EMPTY = new BuiltInMetadata(Map.of());
    private final Map<MetadataSectionSerializer<?>, ?> values;

    private BuiltInMetadata(Map<MetadataSectionSerializer<?>, ?> $$0) {
        this.values = $$0;
    }

    public <T> T get(MetadataSectionSerializer<T> $$0) {
        return (T)this.values.get($$0);
    }

    public static BuiltInMetadata of() {
        return EMPTY;
    }

    public static <T> BuiltInMetadata of(MetadataSectionSerializer<T> $$0, T $$1) {
        return new BuiltInMetadata(Map.of($$0, $$1));
    }

    public static <T1, T2> BuiltInMetadata of(MetadataSectionSerializer<T1> $$0, T1 $$1, MetadataSectionSerializer<T2> $$2, T2 $$3) {
        return new BuiltInMetadata(Map.of($$0, $$1, $$2, $$3));
    }
}