/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.io.IOException
 *  java.io.OutputStream
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.System
 *  java.nio.file.Path
 *  java.nio.file.Paths
 *  java.util.Collection
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.Executor
 *  java.util.function.BiFunction
 *  java.util.stream.Collectors
 *  joptsimple.AbstractOptionSpec
 *  joptsimple.ArgumentAcceptingOptionSpec
 *  joptsimple.OptionParser
 *  joptsimple.OptionSet
 *  joptsimple.OptionSpec
 *  joptsimple.OptionSpecBuilder
 */
package net.minecraft.data;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import joptsimple.AbstractOptionSpec;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import joptsimple.OptionSpecBuilder;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.WorldVersion;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.packs.UpdateOneTwentyVanillaAdvancementProvider;
import net.minecraft.data.advancements.packs.VanillaAdvancementProvider;
import net.minecraft.data.info.BiomeParametersDumpReport;
import net.minecraft.data.info.BlockListReport;
import net.minecraft.data.info.CommandsReport;
import net.minecraft.data.info.RegistryDumpReport;
import net.minecraft.data.loot.packs.UpdateOneTwentyLootTableProvider;
import net.minecraft.data.loot.packs.VanillaLootTableProvider;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.data.models.ModelProvider;
import net.minecraft.data.recipes.packs.BundleRecipeProvider;
import net.minecraft.data.recipes.packs.UpdateOneTwentyRecipeProvider;
import net.minecraft.data.recipes.packs.VanillaRecipeProvider;
import net.minecraft.data.registries.RegistriesDatapackGenerator;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.data.structures.NbtToSnbt;
import net.minecraft.data.structures.SnbtToNbt;
import net.minecraft.data.structures.StructureUpdater;
import net.minecraft.data.tags.BannerPatternTagsProvider;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.data.tags.CatVariantTagsProvider;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.data.tags.FlatLevelGeneratorPresetTagsProvider;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.data.tags.GameEventTagsProvider;
import net.minecraft.data.tags.InstrumentTagsProvider;
import net.minecraft.data.tags.PaintingVariantTagsProvider;
import net.minecraft.data.tags.PoiTypeTagsProvider;
import net.minecraft.data.tags.StructureTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.data.tags.UpdateOneTwentyBlockTagsProvider;
import net.minecraft.data.tags.UpdateOneTwentyItemTagsProvider;
import net.minecraft.data.tags.VanillaBlockTagsProvider;
import net.minecraft.data.tags.VanillaItemTagsProvider;
import net.minecraft.data.tags.WorldPresetTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.obfuscate.DontObfuscate;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;

public class Main {
    @DontObfuscate
    public static void main(String[] $$02) throws IOException {
        SharedConstants.tryDetectVersion();
        OptionParser $$1 = new OptionParser();
        AbstractOptionSpec $$2 = $$1.accepts("help", "Show the help menu").forHelp();
        OptionSpecBuilder $$3 = $$1.accepts("server", "Include server generators");
        OptionSpecBuilder $$4 = $$1.accepts("client", "Include client generators");
        OptionSpecBuilder $$5 = $$1.accepts("dev", "Include development tools");
        OptionSpecBuilder $$6 = $$1.accepts("reports", "Include data reports");
        OptionSpecBuilder $$7 = $$1.accepts("validate", "Validate inputs");
        OptionSpecBuilder $$8 = $$1.accepts("all", "Include all generators");
        ArgumentAcceptingOptionSpec $$9 = $$1.accepts("output", "Output folder").withRequiredArg().defaultsTo((Object)"generated", (Object[])new String[0]);
        ArgumentAcceptingOptionSpec $$10 = $$1.accepts("input", "Input folder").withRequiredArg();
        OptionSet $$11 = $$1.parse($$02);
        if ($$11.has((OptionSpec)$$2) || !$$11.hasOptions()) {
            $$1.printHelpOn((OutputStream)System.out);
            return;
        }
        Path $$12 = Paths.get((String)((String)$$9.value($$11)), (String[])new String[0]);
        boolean $$13 = $$11.has((OptionSpec)$$8);
        boolean $$14 = $$13 || $$11.has((OptionSpec)$$4);
        boolean $$15 = $$13 || $$11.has((OptionSpec)$$3);
        boolean $$16 = $$13 || $$11.has((OptionSpec)$$5);
        boolean $$17 = $$13 || $$11.has((OptionSpec)$$6);
        boolean $$18 = $$13 || $$11.has((OptionSpec)$$7);
        DataGenerator $$19 = Main.createStandardGenerator($$12, (Collection<Path>)((Collection)$$11.valuesOf((OptionSpec)$$10).stream().map($$0 -> Paths.get((String)$$0, (String[])new String[0])).collect(Collectors.toList())), $$14, $$15, $$16, $$17, $$18, SharedConstants.getCurrentVersion(), true);
        $$19.run();
    }

    private static <T extends DataProvider> DataProvider.Factory<T> bindRegistries(BiFunction<PackOutput, CompletableFuture<HolderLookup.Provider>, T> $$0, CompletableFuture<HolderLookup.Provider> $$1) {
        return $$2 -> (DataProvider)$$0.apply((Object)$$2, (Object)$$1);
    }

    public static DataGenerator createStandardGenerator(Path $$02, Collection<Path> $$12, boolean $$22, boolean $$3, boolean $$4, boolean $$5, boolean $$6, WorldVersion $$7, boolean $$8) {
        DataGenerator $$9 = new DataGenerator($$02, $$7, $$8);
        DataGenerator.PackGenerator $$10 = $$9.getVanillaPack($$22 || $$3);
        $$10.addProvider($$1 -> new SnbtToNbt($$1, (Iterable<Path>)$$12).addFilter(new StructureUpdater()));
        CompletableFuture $$11 = CompletableFuture.supplyAsync(VanillaRegistries::createLookup, (Executor)Util.backgroundExecutor());
        DataGenerator.PackGenerator $$122 = $$9.getVanillaPack($$22);
        $$122.addProvider(ModelProvider::new);
        DataGenerator.PackGenerator $$13 = $$9.getVanillaPack($$3);
        $$13.addProvider(Main.bindRegistries(RegistriesDatapackGenerator::new, (CompletableFuture<HolderLookup.Provider>)$$11));
        $$13.addProvider(Main.bindRegistries(VanillaAdvancementProvider::create, (CompletableFuture<HolderLookup.Provider>)$$11));
        $$13.addProvider(VanillaLootTableProvider::create);
        $$13.addProvider(VanillaRecipeProvider::new);
        TagsProvider $$14 = (TagsProvider)$$13.addProvider(Main.bindRegistries(VanillaBlockTagsProvider::new, (CompletableFuture<HolderLookup.Provider>)$$11));
        $$13.addProvider($$2 -> new VanillaItemTagsProvider($$2, (CompletableFuture<HolderLookup.Provider>)$$11, $$14));
        $$13.addProvider(Main.bindRegistries(BannerPatternTagsProvider::new, (CompletableFuture<HolderLookup.Provider>)$$11));
        $$13.addProvider(Main.bindRegistries(BiomeTagsProvider::new, (CompletableFuture<HolderLookup.Provider>)$$11));
        $$13.addProvider(Main.bindRegistries(CatVariantTagsProvider::new, (CompletableFuture<HolderLookup.Provider>)$$11));
        $$13.addProvider(Main.bindRegistries(EntityTypeTagsProvider::new, (CompletableFuture<HolderLookup.Provider>)$$11));
        $$13.addProvider(Main.bindRegistries(FlatLevelGeneratorPresetTagsProvider::new, (CompletableFuture<HolderLookup.Provider>)$$11));
        $$13.addProvider(Main.bindRegistries(FluidTagsProvider::new, (CompletableFuture<HolderLookup.Provider>)$$11));
        $$13.addProvider(Main.bindRegistries(GameEventTagsProvider::new, (CompletableFuture<HolderLookup.Provider>)$$11));
        $$13.addProvider(Main.bindRegistries(InstrumentTagsProvider::new, (CompletableFuture<HolderLookup.Provider>)$$11));
        $$13.addProvider(Main.bindRegistries(PaintingVariantTagsProvider::new, (CompletableFuture<HolderLookup.Provider>)$$11));
        $$13.addProvider(Main.bindRegistries(PoiTypeTagsProvider::new, (CompletableFuture<HolderLookup.Provider>)$$11));
        $$13.addProvider(Main.bindRegistries(StructureTagsProvider::new, (CompletableFuture<HolderLookup.Provider>)$$11));
        $$13.addProvider(Main.bindRegistries(WorldPresetTagsProvider::new, (CompletableFuture<HolderLookup.Provider>)$$11));
        DataGenerator.PackGenerator $$15 = $$9.getVanillaPack($$4);
        $$15.addProvider($$1 -> new NbtToSnbt($$1, $$12));
        DataGenerator.PackGenerator $$16 = $$9.getVanillaPack($$5);
        $$16.addProvider(Main.bindRegistries(BiomeParametersDumpReport::new, (CompletableFuture<HolderLookup.Provider>)$$11));
        $$16.addProvider(BlockListReport::new);
        $$16.addProvider(Main.bindRegistries(CommandsReport::new, (CompletableFuture<HolderLookup.Provider>)$$11));
        $$16.addProvider(RegistryDumpReport::new);
        DataGenerator.PackGenerator $$17 = $$9.getBuiltinDatapack($$3, "bundle");
        $$17.addProvider(BundleRecipeProvider::new);
        $$17.addProvider($$0 -> PackMetadataGenerator.forFeaturePack($$0, Component.translatable("dataPack.bundle.description"), FeatureFlagSet.of(FeatureFlags.BUNDLE)));
        DataGenerator.PackGenerator $$18 = $$9.getBuiltinDatapack($$3, "update_1_20");
        $$18.addProvider(UpdateOneTwentyRecipeProvider::new);
        TagsProvider $$19 = (TagsProvider)$$18.addProvider(Main.bindRegistries(UpdateOneTwentyBlockTagsProvider::new, (CompletableFuture<HolderLookup.Provider>)$$11));
        $$18.addProvider($$2 -> new UpdateOneTwentyItemTagsProvider($$2, (CompletableFuture<HolderLookup.Provider>)$$11, $$19));
        $$18.addProvider(UpdateOneTwentyLootTableProvider::create);
        $$18.addProvider(Main.bindRegistries(UpdateOneTwentyVanillaAdvancementProvider::create, (CompletableFuture<HolderLookup.Provider>)$$11));
        $$18.addProvider($$0 -> PackMetadataGenerator.forFeaturePack($$0, Component.translatable("dataPack.update_1_20.description"), FeatureFlagSet.of(FeatureFlags.UPDATE_1_20)));
        return $$9;
    }
}