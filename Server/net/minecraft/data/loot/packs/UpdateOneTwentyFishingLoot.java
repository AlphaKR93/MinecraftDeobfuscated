/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.BiConsumer
 */
package net.minecraft.data.loot.packs;

import java.util.function.BiConsumer;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.data.loot.packs.VanillaFishingLoot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;

public class UpdateOneTwentyFishingLoot
implements LootTableSubProvider {
    @Override
    public void generate(BiConsumer<ResourceLocation, LootTable.Builder> $$0) {
        $$0.accept((Object)BuiltInLootTables.FISHING_FISH, (Object)VanillaFishingLoot.fishingFishLootTable());
    }
}