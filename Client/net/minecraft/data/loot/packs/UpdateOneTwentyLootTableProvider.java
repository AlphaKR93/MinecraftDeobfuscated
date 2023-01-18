/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.util.List
 *  java.util.Set
 *  java.util.function.Supplier
 */
package net.minecraft.data.loot.packs;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.data.loot.packs.UpdateOneTwentyBlockLoot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

public class UpdateOneTwentyLootTableProvider {
    public static LootTableProvider create(PackOutput $$0) {
        return new LootTableProvider($$0, (Set<ResourceLocation>)Set.of(), (List<LootTableProvider.SubProviderEntry>)List.of((Object)((Object)new LootTableProvider.SubProviderEntry((Supplier<LootTableSubProvider>)((Supplier)UpdateOneTwentyBlockLoot::new), LootContextParamSets.BLOCK))));
    }
}