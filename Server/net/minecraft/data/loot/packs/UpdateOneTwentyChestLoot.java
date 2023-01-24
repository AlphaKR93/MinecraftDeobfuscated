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
import net.minecraft.data.loot.packs.VanillaChestLoot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public class UpdateOneTwentyChestLoot
implements LootTableSubProvider {
    @Override
    public void generate(BiConsumer<ResourceLocation, LootTable.Builder> $$0) {
        $$0.accept((Object)BuiltInLootTables.PILLAGER_OUTPOST, (Object)VanillaChestLoot.pillagerOutpostLootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0f)).add((LootPoolEntryContainer.Builder<?>)EmptyLootItem.emptyItem().setWeight(4)).add((LootPoolEntryContainer.Builder<?>)LootItem.lootTableItem(Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE).setWeight(1))));
        $$0.accept((Object)BuiltInLootTables.DESERT_PYRAMID, (Object)VanillaChestLoot.desertPyramidLootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0f)).add((LootPoolEntryContainer.Builder<?>)EmptyLootItem.emptyItem().setWeight(8)).add((LootPoolEntryContainer.Builder<?>)LootItem.lootTableItem(Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE).setWeight(1))));
        $$0.accept((Object)BuiltInLootTables.SHIPWRECK_MAP, (Object)VanillaChestLoot.shipwreckMapLootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0f)).add((LootPoolEntryContainer.Builder<?>)EmptyLootItem.emptyItem().setWeight(8)).add((LootPoolEntryContainer.Builder<?>)LootItem.lootTableItem(Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE).setWeight(1))));
        $$0.accept((Object)BuiltInLootTables.SHIPWRECK_SUPPLY, (Object)VanillaChestLoot.shipwreckSupplyLootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0f)).add((LootPoolEntryContainer.Builder<?>)EmptyLootItem.emptyItem().setWeight(8)).add((LootPoolEntryContainer.Builder<?>)LootItem.lootTableItem(Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE).setWeight(1))));
        $$0.accept((Object)BuiltInLootTables.SHIPWRECK_TREASURE, (Object)VanillaChestLoot.shipwreckTreasureLootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0f)).add((LootPoolEntryContainer.Builder<?>)EmptyLootItem.emptyItem().setWeight(8)).add((LootPoolEntryContainer.Builder<?>)LootItem.lootTableItem(Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE).setWeight(1))));
        $$0.accept((Object)BuiltInLootTables.JUNGLE_TEMPLE, (Object)VanillaChestLoot.jungleTempleLootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0f)).add((LootPoolEntryContainer.Builder<?>)EmptyLootItem.emptyItem().setWeight(4)).add((LootPoolEntryContainer.Builder<?>)LootItem.lootTableItem(Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE).setWeight(1))));
        $$0.accept((Object)BuiltInLootTables.ANCIENT_CITY, (Object)VanillaChestLoot.ancientCityLootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0f)).add((LootPoolEntryContainer.Builder<?>)EmptyLootItem.emptyItem().setWeight(20)).add((LootPoolEntryContainer.Builder<?>)LootItem.lootTableItem(Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE).setWeight(1))));
        $$0.accept((Object)BuiltInLootTables.STRONGHOLD_CORRIDOR, (Object)VanillaChestLoot.strongholdCorridorLootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0f)).add((LootPoolEntryContainer.Builder<?>)EmptyLootItem.emptyItem().setWeight(20)).add((LootPoolEntryContainer.Builder<?>)LootItem.lootTableItem(Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE).setWeight(1))));
        $$0.accept((Object)BuiltInLootTables.STRONGHOLD_LIBRARY, (Object)VanillaChestLoot.strongholdCorridorLootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0f)).add((LootPoolEntryContainer.Builder<?>)EmptyLootItem.emptyItem().setWeight(10)).add((LootPoolEntryContainer.Builder<?>)LootItem.lootTableItem(Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE).setWeight(1))));
        $$0.accept((Object)BuiltInLootTables.WOODLAND_MANSION, (Object)VanillaChestLoot.woodlandMansionLootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0f)).add((LootPoolEntryContainer.Builder<?>)EmptyLootItem.emptyItem().setWeight(20)).add((LootPoolEntryContainer.Builder<?>)LootItem.lootTableItem(Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE).setWeight(1))));
        $$0.accept((Object)BuiltInLootTables.BASTION_HOGLIN_STABLE, (Object)VanillaChestLoot.bastionBridgeLootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0f)).add((LootPoolEntryContainer.Builder<?>)EmptyLootItem.emptyItem().setWeight(20)).add((LootPoolEntryContainer.Builder<?>)LootItem.lootTableItem(Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE).setWeight(1))).withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0f)).add((LootPoolEntryContainer.Builder<?>)EmptyLootItem.emptyItem().setWeight(30)).add((LootPoolEntryContainer.Builder<?>)LootItem.lootTableItem(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE).setWeight(1))));
        $$0.accept((Object)BuiltInLootTables.BASTION_BRIDGE, (Object)VanillaChestLoot.bastionBridgeLootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0f)).add((LootPoolEntryContainer.Builder<?>)EmptyLootItem.emptyItem().setWeight(20)).add((LootPoolEntryContainer.Builder<?>)LootItem.lootTableItem(Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE).setWeight(1))).withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0f)).add((LootPoolEntryContainer.Builder<?>)EmptyLootItem.emptyItem().setWeight(30)).add((LootPoolEntryContainer.Builder<?>)LootItem.lootTableItem(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE).setWeight(1))));
        $$0.accept((Object)BuiltInLootTables.BASTION_OTHER, (Object)VanillaChestLoot.bastionOtherLootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0f)).add((LootPoolEntryContainer.Builder<?>)EmptyLootItem.emptyItem().setWeight(20)).add((LootPoolEntryContainer.Builder<?>)LootItem.lootTableItem(Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE).setWeight(1))).withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0f)).add((LootPoolEntryContainer.Builder<?>)EmptyLootItem.emptyItem().setWeight(30)).add((LootPoolEntryContainer.Builder<?>)LootItem.lootTableItem(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE).setWeight(1))));
        $$0.accept((Object)BuiltInLootTables.BASTION_TREASURE, (Object)VanillaChestLoot.bastionTreasureLootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0f)).add((LootPoolEntryContainer.Builder<?>)EmptyLootItem.emptyItem().setWeight(20)).add((LootPoolEntryContainer.Builder<?>)LootItem.lootTableItem(Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE).setWeight(1))).withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0f)).add((LootPoolEntryContainer.Builder<?>)LootItem.lootTableItem(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE).setWeight(1))));
        $$0.accept((Object)BuiltInLootTables.NETHER_BRIDGE, (Object)VanillaChestLoot.netherBridgeLootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0f)).add((LootPoolEntryContainer.Builder<?>)EmptyLootItem.emptyItem().setWeight(20)).add((LootPoolEntryContainer.Builder<?>)LootItem.lootTableItem(Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE).setWeight(1))));
        $$0.accept((Object)BuiltInLootTables.END_CITY_TREASURE, (Object)VanillaChestLoot.endCityTreasureLootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0f)).add((LootPoolEntryContainer.Builder<?>)EmptyLootItem.emptyItem().setWeight(20)).add((LootPoolEntryContainer.Builder<?>)LootItem.lootTableItem(Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE).setWeight(1))));
    }
}