/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Object
 *  java.util.List
 *  java.util.Optional
 */
package net.minecraft.world.level.levelgen.feature.configurations;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.FeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.rootplacers.RootPlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;

public class TreeConfiguration
implements FeatureConfiguration {
    public static final Codec<TreeConfiguration> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)BlockStateProvider.CODEC.fieldOf("trunk_provider").forGetter($$0 -> $$0.trunkProvider), (App)TrunkPlacer.CODEC.fieldOf("trunk_placer").forGetter($$0 -> $$0.trunkPlacer), (App)BlockStateProvider.CODEC.fieldOf("foliage_provider").forGetter($$0 -> $$0.foliageProvider), (App)FoliagePlacer.CODEC.fieldOf("foliage_placer").forGetter($$0 -> $$0.foliagePlacer), (App)RootPlacer.CODEC.optionalFieldOf("root_placer").forGetter($$0 -> $$0.rootPlacer), (App)BlockStateProvider.CODEC.fieldOf("dirt_provider").forGetter($$0 -> $$0.dirtProvider), (App)FeatureSize.CODEC.fieldOf("minimum_size").forGetter($$0 -> $$0.minimumSize), (App)TreeDecorator.CODEC.listOf().fieldOf("decorators").forGetter($$0 -> $$0.decorators), (App)Codec.BOOL.fieldOf("ignore_vines").orElse((Object)false).forGetter($$0 -> $$0.ignoreVines), (App)Codec.BOOL.fieldOf("force_dirt").orElse((Object)false).forGetter($$0 -> $$0.forceDirt)).apply((Applicative)$$02, TreeConfiguration::new));
    public final BlockStateProvider trunkProvider;
    public final BlockStateProvider dirtProvider;
    public final TrunkPlacer trunkPlacer;
    public final BlockStateProvider foliageProvider;
    public final FoliagePlacer foliagePlacer;
    public final Optional<RootPlacer> rootPlacer;
    public final FeatureSize minimumSize;
    public final List<TreeDecorator> decorators;
    public final boolean ignoreVines;
    public final boolean forceDirt;

    protected TreeConfiguration(BlockStateProvider $$0, TrunkPlacer $$1, BlockStateProvider $$2, FoliagePlacer $$3, Optional<RootPlacer> $$4, BlockStateProvider $$5, FeatureSize $$6, List<TreeDecorator> $$7, boolean $$8, boolean $$9) {
        this.trunkProvider = $$0;
        this.trunkPlacer = $$1;
        this.foliageProvider = $$2;
        this.foliagePlacer = $$3;
        this.rootPlacer = $$4;
        this.dirtProvider = $$5;
        this.minimumSize = $$6;
        this.decorators = $$7;
        this.ignoreVines = $$8;
        this.forceDirt = $$9;
    }

    public static class TreeConfigurationBuilder {
        public final BlockStateProvider trunkProvider;
        private final TrunkPlacer trunkPlacer;
        public final BlockStateProvider foliageProvider;
        private final FoliagePlacer foliagePlacer;
        private final Optional<RootPlacer> rootPlacer;
        private BlockStateProvider dirtProvider;
        private final FeatureSize minimumSize;
        private List<TreeDecorator> decorators = ImmutableList.of();
        private boolean ignoreVines;
        private boolean forceDirt;

        public TreeConfigurationBuilder(BlockStateProvider $$0, TrunkPlacer $$1, BlockStateProvider $$2, FoliagePlacer $$3, Optional<RootPlacer> $$4, FeatureSize $$5) {
            this.trunkProvider = $$0;
            this.trunkPlacer = $$1;
            this.foliageProvider = $$2;
            this.dirtProvider = BlockStateProvider.simple(Blocks.DIRT);
            this.foliagePlacer = $$3;
            this.rootPlacer = $$4;
            this.minimumSize = $$5;
        }

        public TreeConfigurationBuilder(BlockStateProvider $$0, TrunkPlacer $$1, BlockStateProvider $$2, FoliagePlacer $$3, FeatureSize $$4) {
            this($$0, $$1, $$2, $$3, (Optional<RootPlacer>)Optional.empty(), $$4);
        }

        public TreeConfigurationBuilder dirt(BlockStateProvider $$0) {
            this.dirtProvider = $$0;
            return this;
        }

        public TreeConfigurationBuilder decorators(List<TreeDecorator> $$0) {
            this.decorators = $$0;
            return this;
        }

        public TreeConfigurationBuilder ignoreVines() {
            this.ignoreVines = true;
            return this;
        }

        public TreeConfigurationBuilder forceDirt() {
            this.forceDirt = true;
            return this;
        }

        public TreeConfiguration build() {
            return new TreeConfiguration(this.trunkProvider, this.trunkPlacer, this.foliageProvider, this.foliagePlacer, this.rootPlacer, this.dirtProvider, this.minimumSize, this.decorators, this.ignoreVines, this.forceDirt);
        }
    }
}