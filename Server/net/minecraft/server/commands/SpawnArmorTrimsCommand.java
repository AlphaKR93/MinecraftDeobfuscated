/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.datafixers.util.Pair
 *  java.lang.Object
 *  java.util.Comparator
 *  java.util.Iterator
 *  java.util.List
 *  java.util.Map
 *  java.util.function.ToIntFunction
 */
package net.minecraft.server.commands;

import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.datafixers.util.Pair;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.item.armortrim.TrimPatterns;
import net.minecraft.world.level.Level;

public class SpawnArmorTrimsCommand {
    private static final Map<Pair<ArmorMaterial, EquipmentSlot>, Item> MATERIAL_AND_SLOT_TO_ITEM = (Map)Util.make(Maps.newHashMap(), $$0 -> {
        $$0.put((Object)Pair.of((Object)ArmorMaterials.CHAIN, (Object)((Object)EquipmentSlot.HEAD)), (Object)Items.CHAINMAIL_HELMET);
        $$0.put((Object)Pair.of((Object)ArmorMaterials.CHAIN, (Object)((Object)EquipmentSlot.CHEST)), (Object)Items.CHAINMAIL_CHESTPLATE);
        $$0.put((Object)Pair.of((Object)ArmorMaterials.CHAIN, (Object)((Object)EquipmentSlot.LEGS)), (Object)Items.CHAINMAIL_LEGGINGS);
        $$0.put((Object)Pair.of((Object)ArmorMaterials.CHAIN, (Object)((Object)EquipmentSlot.FEET)), (Object)Items.CHAINMAIL_BOOTS);
        $$0.put((Object)Pair.of((Object)ArmorMaterials.IRON, (Object)((Object)EquipmentSlot.HEAD)), (Object)Items.IRON_HELMET);
        $$0.put((Object)Pair.of((Object)ArmorMaterials.IRON, (Object)((Object)EquipmentSlot.CHEST)), (Object)Items.IRON_CHESTPLATE);
        $$0.put((Object)Pair.of((Object)ArmorMaterials.IRON, (Object)((Object)EquipmentSlot.LEGS)), (Object)Items.IRON_LEGGINGS);
        $$0.put((Object)Pair.of((Object)ArmorMaterials.IRON, (Object)((Object)EquipmentSlot.FEET)), (Object)Items.IRON_BOOTS);
        $$0.put((Object)Pair.of((Object)ArmorMaterials.GOLD, (Object)((Object)EquipmentSlot.HEAD)), (Object)Items.GOLDEN_HELMET);
        $$0.put((Object)Pair.of((Object)ArmorMaterials.GOLD, (Object)((Object)EquipmentSlot.CHEST)), (Object)Items.GOLDEN_CHESTPLATE);
        $$0.put((Object)Pair.of((Object)ArmorMaterials.GOLD, (Object)((Object)EquipmentSlot.LEGS)), (Object)Items.GOLDEN_LEGGINGS);
        $$0.put((Object)Pair.of((Object)ArmorMaterials.GOLD, (Object)((Object)EquipmentSlot.FEET)), (Object)Items.GOLDEN_BOOTS);
        $$0.put((Object)Pair.of((Object)ArmorMaterials.NETHERITE, (Object)((Object)EquipmentSlot.HEAD)), (Object)Items.NETHERITE_HELMET);
        $$0.put((Object)Pair.of((Object)ArmorMaterials.NETHERITE, (Object)((Object)EquipmentSlot.CHEST)), (Object)Items.NETHERITE_CHESTPLATE);
        $$0.put((Object)Pair.of((Object)ArmorMaterials.NETHERITE, (Object)((Object)EquipmentSlot.LEGS)), (Object)Items.NETHERITE_LEGGINGS);
        $$0.put((Object)Pair.of((Object)ArmorMaterials.NETHERITE, (Object)((Object)EquipmentSlot.FEET)), (Object)Items.NETHERITE_BOOTS);
        $$0.put((Object)Pair.of((Object)ArmorMaterials.DIAMOND, (Object)((Object)EquipmentSlot.HEAD)), (Object)Items.DIAMOND_HELMET);
        $$0.put((Object)Pair.of((Object)ArmorMaterials.DIAMOND, (Object)((Object)EquipmentSlot.CHEST)), (Object)Items.DIAMOND_CHESTPLATE);
        $$0.put((Object)Pair.of((Object)ArmorMaterials.DIAMOND, (Object)((Object)EquipmentSlot.LEGS)), (Object)Items.DIAMOND_LEGGINGS);
        $$0.put((Object)Pair.of((Object)ArmorMaterials.DIAMOND, (Object)((Object)EquipmentSlot.FEET)), (Object)Items.DIAMOND_BOOTS);
        $$0.put((Object)Pair.of((Object)ArmorMaterials.TURTLE, (Object)((Object)EquipmentSlot.HEAD)), (Object)Items.TURTLE_HELMET);
    });
    private static final List<ResourceKey<TrimPattern>> VANILLA_TRIM_PATTERNS = List.of((Object[])new ResourceKey[]{TrimPatterns.SENTRY, TrimPatterns.DUNE, TrimPatterns.COAST, TrimPatterns.WILD, TrimPatterns.WARD, TrimPatterns.EYE, TrimPatterns.VEX, TrimPatterns.TIDE, TrimPatterns.SNOUT, TrimPatterns.RIB, TrimPatterns.SPIRE});
    private static final List<ResourceKey<TrimMaterial>> VANILLA_TRIM_MATERIALS = List.of(TrimMaterials.QUARTZ, TrimMaterials.IRON, TrimMaterials.NETHERITE, TrimMaterials.REDSTONE, TrimMaterials.COPPER, TrimMaterials.GOLD, TrimMaterials.EMERALD, TrimMaterials.DIAMOND, TrimMaterials.LAPIS, TrimMaterials.AMETHYST);
    private static final ToIntFunction<ResourceKey<TrimPattern>> TRIM_PATTERN_ORDER = Util.createIndexLookup(VANILLA_TRIM_PATTERNS);
    private static final ToIntFunction<ResourceKey<TrimMaterial>> TRIM_MATERIAL_ORDER = Util.createIndexLookup(VANILLA_TRIM_MATERIALS);

    public static void register(CommandDispatcher<CommandSourceStack> $$02) {
        $$02.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("spawn_armor_trims").requires($$0 -> $$0.hasPermission(2) && $$0.getLevel().enabledFeatures().contains(FeatureFlags.UPDATE_1_20))).executes($$0 -> SpawnArmorTrimsCommand.spawnArmorTrims((CommandSourceStack)$$0.getSource(), ((CommandSourceStack)$$0.getSource()).getPlayerOrException())));
    }

    private static int spawnArmorTrims(CommandSourceStack $$0, Player $$12) {
        Level $$2 = $$12.getLevel();
        NonNullList $$32 = NonNullList.create();
        Registry<TrimPattern> $$4 = $$2.registryAccess().registryOrThrow(Registries.TRIM_PATTERN);
        Registry<TrimMaterial> $$5 = $$2.registryAccess().registryOrThrow(Registries.TRIM_MATERIAL);
        $$4.stream().sorted(Comparator.comparing($$1 -> TRIM_PATTERN_ORDER.applyAsInt((Object)((ResourceKey)$$4.getResourceKey((TrimPattern)((Object)$$1)).orElse(null))))).forEachOrdered($$3 -> $$5.stream().sorted(Comparator.comparing($$1 -> TRIM_MATERIAL_ORDER.applyAsInt((Object)((ResourceKey)$$5.getResourceKey((TrimMaterial)((Object)$$1)).orElse(null))))).forEachOrdered($$4 -> $$32.add(new ArmorTrim($$5.wrapAsHolder((TrimMaterial)((Object)$$4)), $$4.wrapAsHolder((TrimPattern)((Object)$$3))))));
        BlockPos $$6 = $$12.blockPosition().relative($$12.getDirection(), 5);
        int $$7 = ArmorMaterials.values().length - 1;
        double $$8 = 3.0;
        int $$9 = 0;
        int $$10 = 0;
        Iterator iterator = $$32.iterator();
        while (iterator.hasNext()) {
            ArmorTrim $$11 = (ArmorTrim)iterator.next();
            for (ArmorMaterials $$122 : ArmorMaterials.values()) {
                if ($$122 == ArmorMaterials.LEATHER) continue;
                double $$13 = (double)$$6.getX() + 0.5 - (double)($$9 % $$5.size()) * 3.0;
                double $$14 = (double)$$6.getY() + 0.5 + (double)($$10 % $$7) * 3.0;
                double $$15 = (double)$$6.getZ() + 0.5 + (double)($$9 / $$5.size() * 10);
                ArmorStand $$16 = new ArmorStand($$2, $$13, $$14, $$15);
                $$16.setYRot(180.0f);
                $$16.setNoGravity(true);
                for (EquipmentSlot $$17 : EquipmentSlot.values()) {
                    ArmorItem $$20;
                    Item $$18 = (Item)MATERIAL_AND_SLOT_TO_ITEM.get((Object)Pair.of((Object)$$122, (Object)((Object)$$17)));
                    if ($$18 == null) continue;
                    ItemStack $$19 = new ItemStack($$18);
                    ArmorTrim.setTrim($$2.registryAccess(), $$19, $$11);
                    $$16.setItemSlot($$17, $$19);
                    if ($$18 instanceof ArmorItem && ($$20 = (ArmorItem)$$18).getMaterial() == ArmorMaterials.TURTLE) {
                        $$16.setCustomName($$11.pattern().value().copyWithStyle($$11.material()).copy().append(" ").append($$11.material().value().description()));
                        $$16.setCustomNameVisible(true);
                        continue;
                    }
                    $$16.setInvisible(true);
                }
                $$2.addFreshEntity($$16);
                ++$$10;
            }
            ++$$9;
        }
        $$0.sendSuccess(Component.literal("Armorstands with trimmed armor spawned around you"), true);
        return 1;
    }
}