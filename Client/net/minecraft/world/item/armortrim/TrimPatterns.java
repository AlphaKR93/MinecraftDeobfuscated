/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Optional
 */
package net.minecraft.world.item.armortrim;

import java.util.Optional;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.armortrim.TrimPattern;

public class TrimPatterns {
    public static final ResourceKey<TrimPattern> SENTRY = TrimPatterns.registryKey("sentry");
    public static final ResourceKey<TrimPattern> DUNE = TrimPatterns.registryKey("dune");
    public static final ResourceKey<TrimPattern> COAST = TrimPatterns.registryKey("coast");
    public static final ResourceKey<TrimPattern> WILD = TrimPatterns.registryKey("wild");
    public static final ResourceKey<TrimPattern> WARD = TrimPatterns.registryKey("ward");
    public static final ResourceKey<TrimPattern> EYE = TrimPatterns.registryKey("eye");
    public static final ResourceKey<TrimPattern> VEX = TrimPatterns.registryKey("vex");
    public static final ResourceKey<TrimPattern> TIDE = TrimPatterns.registryKey("tide");
    public static final ResourceKey<TrimPattern> SNOUT = TrimPatterns.registryKey("snout");
    public static final ResourceKey<TrimPattern> RIB = TrimPatterns.registryKey("rib");
    public static final ResourceKey<TrimPattern> SPIRE = TrimPatterns.registryKey("spire");

    public static void bootstrap(BootstapContext<TrimPattern> $$0) {
    }

    public static void nextUpdate(BootstapContext<TrimPattern> $$0) {
        TrimPatterns.register($$0, Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE, SENTRY);
        TrimPatterns.register($$0, Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE, DUNE);
        TrimPatterns.register($$0, Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE, COAST);
        TrimPatterns.register($$0, Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE, WILD);
        TrimPatterns.register($$0, Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE, WARD);
        TrimPatterns.register($$0, Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE, EYE);
        TrimPatterns.register($$0, Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE, VEX);
        TrimPatterns.register($$0, Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE, TIDE);
        TrimPatterns.register($$0, Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE, SNOUT);
        TrimPatterns.register($$0, Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE, RIB);
        TrimPatterns.register($$0, Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE, SPIRE);
    }

    public static Optional<Holder.Reference<TrimPattern>> getFromTemplate(RegistryAccess $$0, ItemStack $$12) {
        return $$0.registryOrThrow(Registries.TRIM_PATTERN).holders().filter($$1 -> $$12.is(((TrimPattern)((Object)((Object)$$1.value()))).templateItem())).findFirst();
    }

    private static void register(BootstapContext<TrimPattern> $$0, Item $$1, ResourceKey<TrimPattern> $$2) {
        TrimPattern $$3 = new TrimPattern($$2.location(), BuiltInRegistries.ITEM.wrapAsHolder($$1), Component.translatable(Util.makeDescriptionId("trim_pattern", $$2.location())));
        $$0.register($$2, $$3);
    }

    private static ResourceKey<TrimPattern> registryKey(String $$0) {
        return ResourceKey.create(Registries.TRIM_PATTERN, new ResourceLocation($$0));
    }
}