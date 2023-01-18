/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  java.lang.Object
 *  java.lang.String
 */
package net.minecraft.world.level.levelgen.structure.pools;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.structure.pools.EmptyPoolElement;
import net.minecraft.world.level.levelgen.structure.pools.FeaturePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.LegacySinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.ListPoolElement;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;

public interface StructurePoolElementType<P extends StructurePoolElement> {
    public static final StructurePoolElementType<SinglePoolElement> SINGLE = StructurePoolElementType.register("single_pool_element", SinglePoolElement.CODEC);
    public static final StructurePoolElementType<ListPoolElement> LIST = StructurePoolElementType.register("list_pool_element", ListPoolElement.CODEC);
    public static final StructurePoolElementType<FeaturePoolElement> FEATURE = StructurePoolElementType.register("feature_pool_element", FeaturePoolElement.CODEC);
    public static final StructurePoolElementType<EmptyPoolElement> EMPTY = StructurePoolElementType.register("empty_pool_element", EmptyPoolElement.CODEC);
    public static final StructurePoolElementType<LegacySinglePoolElement> LEGACY = StructurePoolElementType.register("legacy_single_pool_element", LegacySinglePoolElement.CODEC);

    public Codec<P> codec();

    public static <P extends StructurePoolElement> StructurePoolElementType<P> register(String $$0, Codec<P> $$1) {
        return Registry.register(BuiltInRegistries.STRUCTURE_POOL_ELEMENT, $$0, () -> $$1);
    }
}