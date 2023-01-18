/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 */
package net.minecraft.world.level.storage.loot.providers.number;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.GsonAdapterFactory;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.providers.number.BinomialDistributionGenerator;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.ScoreboardValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public class NumberProviders {
    public static final LootNumberProviderType CONSTANT = NumberProviders.register("constant", new ConstantValue.Serializer());
    public static final LootNumberProviderType UNIFORM = NumberProviders.register("uniform", new UniformGenerator.Serializer());
    public static final LootNumberProviderType BINOMIAL = NumberProviders.register("binomial", new BinomialDistributionGenerator.Serializer());
    public static final LootNumberProviderType SCORE = NumberProviders.register("score", new ScoreboardValue.Serializer());

    private static LootNumberProviderType register(String $$0, Serializer<? extends NumberProvider> $$1) {
        return Registry.register(BuiltInRegistries.LOOT_NUMBER_PROVIDER_TYPE, new ResourceLocation($$0), new LootNumberProviderType($$1));
    }

    public static Object createGsonAdapter() {
        return GsonAdapterFactory.builder(BuiltInRegistries.LOOT_NUMBER_PROVIDER_TYPE, "provider", "type", NumberProvider::getType).withInlineSerializer(CONSTANT, new ConstantValue.InlineSerializer()).withDefaultType(UNIFORM).build();
    }
}