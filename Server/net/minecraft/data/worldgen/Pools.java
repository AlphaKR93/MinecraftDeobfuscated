/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.datafixers.util.Pair
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.String
 *  java.util.List
 *  java.util.function.Function
 */
package net.minecraft.data.worldgen;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.AncientCityStructurePieces;
import net.minecraft.data.worldgen.BastionPieces;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.PillagerOutpostPools;
import net.minecraft.data.worldgen.VillagePools;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public class Pools {
    public static final ResourceKey<StructureTemplatePool> EMPTY = Pools.createKey("empty");

    public static ResourceKey<StructureTemplatePool> createKey(String $$0) {
        return ResourceKey.create(Registries.TEMPLATE_POOL, new ResourceLocation($$0));
    }

    public static void register(BootstapContext<StructureTemplatePool> $$0, String $$1, StructureTemplatePool $$2) {
        $$0.register(Pools.createKey($$1), $$2);
    }

    public static void bootstrap(BootstapContext<StructureTemplatePool> $$0) {
        HolderGetter<StructureTemplatePool> $$1 = $$0.lookup(Registries.TEMPLATE_POOL);
        Holder.Reference<StructureTemplatePool> $$2 = $$1.getOrThrow(EMPTY);
        $$0.register(EMPTY, new StructureTemplatePool($$2, (List<Pair<Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of(), StructureTemplatePool.Projection.RIGID));
        BastionPieces.bootstrap($$0);
        PillagerOutpostPools.bootstrap($$0);
        VillagePools.bootstrap($$0);
        AncientCityStructurePieces.bootstrap($$0);
    }
}