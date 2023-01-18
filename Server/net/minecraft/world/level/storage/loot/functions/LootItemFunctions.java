/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 *  java.util.function.BiFunction
 */
package net.minecraft.world.level.storage.loot.functions;

import java.util.function.BiFunction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.GsonAdapterFactory;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.ApplyExplosionDecay;
import net.minecraft.world.level.storage.loot.functions.CopyBlockState;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import net.minecraft.world.level.storage.loot.functions.EnchantWithLevelsFunction;
import net.minecraft.world.level.storage.loot.functions.ExplorationMapFunction;
import net.minecraft.world.level.storage.loot.functions.FillPlayerHead;
import net.minecraft.world.level.storage.loot.functions.LimitCount;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootingEnchantFunction;
import net.minecraft.world.level.storage.loot.functions.SetAttributesFunction;
import net.minecraft.world.level.storage.loot.functions.SetBannerPatternFunction;
import net.minecraft.world.level.storage.loot.functions.SetContainerContents;
import net.minecraft.world.level.storage.loot.functions.SetContainerLootTable;
import net.minecraft.world.level.storage.loot.functions.SetEnchantmentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetInstrumentFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemDamageFunction;
import net.minecraft.world.level.storage.loot.functions.SetLoreFunction;
import net.minecraft.world.level.storage.loot.functions.SetNameFunction;
import net.minecraft.world.level.storage.loot.functions.SetNbtFunction;
import net.minecraft.world.level.storage.loot.functions.SetPotionFunction;
import net.minecraft.world.level.storage.loot.functions.SetStewEffectFunction;
import net.minecraft.world.level.storage.loot.functions.SmeltItemFunction;

public class LootItemFunctions {
    public static final BiFunction<ItemStack, LootContext, ItemStack> IDENTITY = ($$0, $$1) -> $$0;
    public static final LootItemFunctionType SET_COUNT = LootItemFunctions.register("set_count", new SetItemCountFunction.Serializer());
    public static final LootItemFunctionType ENCHANT_WITH_LEVELS = LootItemFunctions.register("enchant_with_levels", new EnchantWithLevelsFunction.Serializer());
    public static final LootItemFunctionType ENCHANT_RANDOMLY = LootItemFunctions.register("enchant_randomly", new EnchantRandomlyFunction.Serializer());
    public static final LootItemFunctionType SET_ENCHANTMENTS = LootItemFunctions.register("set_enchantments", new SetEnchantmentsFunction.Serializer());
    public static final LootItemFunctionType SET_NBT = LootItemFunctions.register("set_nbt", new SetNbtFunction.Serializer());
    public static final LootItemFunctionType FURNACE_SMELT = LootItemFunctions.register("furnace_smelt", new SmeltItemFunction.Serializer());
    public static final LootItemFunctionType LOOTING_ENCHANT = LootItemFunctions.register("looting_enchant", new LootingEnchantFunction.Serializer());
    public static final LootItemFunctionType SET_DAMAGE = LootItemFunctions.register("set_damage", new SetItemDamageFunction.Serializer());
    public static final LootItemFunctionType SET_ATTRIBUTES = LootItemFunctions.register("set_attributes", new SetAttributesFunction.Serializer());
    public static final LootItemFunctionType SET_NAME = LootItemFunctions.register("set_name", new SetNameFunction.Serializer());
    public static final LootItemFunctionType EXPLORATION_MAP = LootItemFunctions.register("exploration_map", new ExplorationMapFunction.Serializer());
    public static final LootItemFunctionType SET_STEW_EFFECT = LootItemFunctions.register("set_stew_effect", new SetStewEffectFunction.Serializer());
    public static final LootItemFunctionType COPY_NAME = LootItemFunctions.register("copy_name", new CopyNameFunction.Serializer());
    public static final LootItemFunctionType SET_CONTENTS = LootItemFunctions.register("set_contents", new SetContainerContents.Serializer());
    public static final LootItemFunctionType LIMIT_COUNT = LootItemFunctions.register("limit_count", new LimitCount.Serializer());
    public static final LootItemFunctionType APPLY_BONUS = LootItemFunctions.register("apply_bonus", new ApplyBonusCount.Serializer());
    public static final LootItemFunctionType SET_LOOT_TABLE = LootItemFunctions.register("set_loot_table", new SetContainerLootTable.Serializer());
    public static final LootItemFunctionType EXPLOSION_DECAY = LootItemFunctions.register("explosion_decay", new ApplyExplosionDecay.Serializer());
    public static final LootItemFunctionType SET_LORE = LootItemFunctions.register("set_lore", new SetLoreFunction.Serializer());
    public static final LootItemFunctionType FILL_PLAYER_HEAD = LootItemFunctions.register("fill_player_head", new FillPlayerHead.Serializer());
    public static final LootItemFunctionType COPY_NBT = LootItemFunctions.register("copy_nbt", new CopyNbtFunction.Serializer());
    public static final LootItemFunctionType COPY_STATE = LootItemFunctions.register("copy_state", new CopyBlockState.Serializer());
    public static final LootItemFunctionType SET_BANNER_PATTERN = LootItemFunctions.register("set_banner_pattern", new SetBannerPatternFunction.Serializer());
    public static final LootItemFunctionType SET_POTION = LootItemFunctions.register("set_potion", new SetPotionFunction.Serializer());
    public static final LootItemFunctionType SET_INSTRUMENT = LootItemFunctions.register("set_instrument", new SetInstrumentFunction.Serializer());

    private static LootItemFunctionType register(String $$0, Serializer<? extends LootItemFunction> $$1) {
        return Registry.register(BuiltInRegistries.LOOT_FUNCTION_TYPE, new ResourceLocation($$0), new LootItemFunctionType($$1));
    }

    public static Object createGsonAdapter() {
        return GsonAdapterFactory.builder(BuiltInRegistries.LOOT_FUNCTION_TYPE, "function", "function", LootItemFunction::getType).build();
    }

    public static BiFunction<ItemStack, LootContext, ItemStack> compose(BiFunction<ItemStack, LootContext, ItemStack>[] $$0) {
        switch ($$0.length) {
            case 0: {
                return IDENTITY;
            }
            case 1: {
                return $$0[0];
            }
            case 2: {
                BiFunction<ItemStack, LootContext, ItemStack> $$12 = $$0[0];
                BiFunction<ItemStack, LootContext, ItemStack> $$22 = $$0[1];
                return ($$2, $$3) -> (ItemStack)$$22.apply((Object)((ItemStack)$$12.apply($$2, $$3)), $$3);
            }
        }
        return ($$1, $$2) -> {
            for (BiFunction $$3 : $$0) {
                $$1 = (ItemStack)$$3.apply($$1, $$2);
            }
            return $$1;
        };
    }
}