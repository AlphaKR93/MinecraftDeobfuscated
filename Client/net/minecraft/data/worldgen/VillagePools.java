/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.data.worldgen;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.DesertVillagePools;
import net.minecraft.data.worldgen.PlainVillagePools;
import net.minecraft.data.worldgen.SavannaVillagePools;
import net.minecraft.data.worldgen.SnowyVillagePools;
import net.minecraft.data.worldgen.TaigaVillagePools;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public class VillagePools {
    public static void bootstrap(BootstapContext<StructureTemplatePool> $$0) {
        PlainVillagePools.bootstrap($$0);
        SnowyVillagePools.bootstrap($$0);
        SavannaVillagePools.bootstrap($$0);
        DesertVillagePools.bootstrap($$0);
        TaigaVillagePools.bootstrap($$0);
    }
}