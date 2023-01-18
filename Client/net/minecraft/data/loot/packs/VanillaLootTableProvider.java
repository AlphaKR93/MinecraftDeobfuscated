/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.util.List
 *  java.util.function.Supplier
 */
package net.minecraft.data.loot.packs;

import java.util.List;
import java.util.function.Supplier;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.data.loot.packs.VanillaBlockLoot;
import net.minecraft.data.loot.packs.VanillaChestLoot;
import net.minecraft.data.loot.packs.VanillaEntityLoot;
import net.minecraft.data.loot.packs.VanillaFishingLoot;
import net.minecraft.data.loot.packs.VanillaGiftLoot;
import net.minecraft.data.loot.packs.VanillaPiglinBarterLoot;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

public class VanillaLootTableProvider {
    public static LootTableProvider create(PackOutput $$0) {
        return new LootTableProvider($$0, BuiltInLootTables.all(), (List<LootTableProvider.SubProviderEntry>)List.of((Object)((Object)new LootTableProvider.SubProviderEntry((Supplier<LootTableSubProvider>)((Supplier)VanillaFishingLoot::new), LootContextParamSets.FISHING)), (Object)((Object)new LootTableProvider.SubProviderEntry((Supplier<LootTableSubProvider>)((Supplier)VanillaChestLoot::new), LootContextParamSets.CHEST)), (Object)((Object)new LootTableProvider.SubProviderEntry((Supplier<LootTableSubProvider>)((Supplier)VanillaEntityLoot::new), LootContextParamSets.ENTITY)), (Object)((Object)new LootTableProvider.SubProviderEntry((Supplier<LootTableSubProvider>)((Supplier)VanillaBlockLoot::new), LootContextParamSets.BLOCK)), (Object)((Object)new LootTableProvider.SubProviderEntry((Supplier<LootTableSubProvider>)((Supplier)VanillaPiglinBarterLoot::new), LootContextParamSets.PIGLIN_BARTER)), (Object)((Object)new LootTableProvider.SubProviderEntry((Supplier<LootTableSubProvider>)((Supplier)VanillaGiftLoot::new), LootContextParamSets.GIFT))));
    }
}