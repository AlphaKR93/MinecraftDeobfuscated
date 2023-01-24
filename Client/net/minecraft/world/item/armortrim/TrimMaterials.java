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
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.armortrim.TrimMaterial;

public class TrimMaterials {
    public static final ResourceKey<TrimMaterial> QUARTZ = TrimMaterials.registryKey("quartz");
    public static final ResourceKey<TrimMaterial> IRON = TrimMaterials.registryKey("iron");
    public static final ResourceKey<TrimMaterial> NETHERITE = TrimMaterials.registryKey("netherite");
    public static final ResourceKey<TrimMaterial> REDSTONE = TrimMaterials.registryKey("redstone");
    public static final ResourceKey<TrimMaterial> COPPER = TrimMaterials.registryKey("copper");
    public static final ResourceKey<TrimMaterial> GOLD = TrimMaterials.registryKey("gold");
    public static final ResourceKey<TrimMaterial> EMERALD = TrimMaterials.registryKey("emerald");
    public static final ResourceKey<TrimMaterial> DIAMOND = TrimMaterials.registryKey("diamond");
    public static final ResourceKey<TrimMaterial> LAPIS = TrimMaterials.registryKey("lapis");
    public static final ResourceKey<TrimMaterial> AMETHYST = TrimMaterials.registryKey("amethyst");

    public static void bootstrap(BootstapContext<TrimMaterial> $$0) {
    }

    public static void nextUpdate(BootstapContext<TrimMaterial> $$0) {
        TrimMaterials.register($$0, QUARTZ, Items.QUARTZ, Style.EMPTY.withColor(14931140), 0.1f, (Optional<ArmorMaterials>)Optional.empty());
        TrimMaterials.register($$0, IRON, Items.IRON_INGOT, Style.EMPTY.withColor(0xECECEC), 0.2f, (Optional<ArmorMaterials>)Optional.of((Object)ArmorMaterials.IRON));
        TrimMaterials.register($$0, NETHERITE, Items.NETHERITE_INGOT, Style.EMPTY.withColor(6445145), 0.3f, (Optional<ArmorMaterials>)Optional.of((Object)ArmorMaterials.NETHERITE));
        TrimMaterials.register($$0, REDSTONE, Items.REDSTONE, Style.EMPTY.withColor(9901575), 0.4f, (Optional<ArmorMaterials>)Optional.empty());
        TrimMaterials.register($$0, COPPER, Items.COPPER_INGOT, Style.EMPTY.withColor(11823181), 0.5f, (Optional<ArmorMaterials>)Optional.empty());
        TrimMaterials.register($$0, GOLD, Items.GOLD_INGOT, Style.EMPTY.withColor(14594349), 0.6f, (Optional<ArmorMaterials>)Optional.of((Object)ArmorMaterials.GOLD));
        TrimMaterials.register($$0, EMERALD, Items.EMERALD, Style.EMPTY.withColor(1155126), 0.7f, (Optional<ArmorMaterials>)Optional.empty());
        TrimMaterials.register($$0, DIAMOND, Items.DIAMOND, Style.EMPTY.withColor(7269586), 0.8f, (Optional<ArmorMaterials>)Optional.of((Object)ArmorMaterials.DIAMOND));
        TrimMaterials.register($$0, LAPIS, Items.LAPIS_LAZULI, Style.EMPTY.withColor(4288151), 0.9f, (Optional<ArmorMaterials>)Optional.empty());
        TrimMaterials.register($$0, AMETHYST, Items.AMETHYST_SHARD, Style.EMPTY.withColor(10116294), 1.0f, (Optional<ArmorMaterials>)Optional.empty());
    }

    public static Optional<Holder.Reference<TrimMaterial>> getFromIngredient(RegistryAccess $$0, ItemStack $$12) {
        return $$0.registryOrThrow(Registries.TRIM_MATERIAL).holders().filter($$1 -> $$12.is(((TrimMaterial)((Object)((Object)$$1.value()))).ingredient())).findFirst();
    }

    private static void register(BootstapContext<TrimMaterial> $$0, ResourceKey<TrimMaterial> $$1, Item $$2, Style $$3, float $$4, Optional<ArmorMaterials> $$5) {
        TrimMaterial $$6 = TrimMaterial.create($$1.location().getPath(), $$2, $$4, $$5, Component.translatable(Util.makeDescriptionId("trim_material", $$1.location())).withStyle($$3));
        $$0.register($$1, $$6);
    }

    private static ResourceKey<TrimMaterial> registryKey(String $$0) {
        return ResourceKey.create(Registries.TRIM_MATERIAL, new ResourceLocation($$0));
    }
}