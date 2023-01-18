/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Products$P3
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Mu
 *  java.lang.Integer
 *  java.lang.Object
 *  java.util.List
 *  java.util.function.BiConsumer
 *  java.util.function.Function
 *  java.util.function.Predicate
 */
package net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

public abstract class TrunkPlacer {
    public static final Codec<TrunkPlacer> CODEC = BuiltInRegistries.TRUNK_PLACER_TYPE.byNameCodec().dispatch(TrunkPlacer::type, TrunkPlacerType::codec);
    private static final int MAX_BASE_HEIGHT = 32;
    private static final int MAX_RAND = 24;
    public static final int MAX_HEIGHT = 80;
    protected final int baseHeight;
    protected final int heightRandA;
    protected final int heightRandB;

    protected static <P extends TrunkPlacer> Products.P3<RecordCodecBuilder.Mu<P>, Integer, Integer, Integer> trunkPlacerParts(RecordCodecBuilder.Instance<P> $$02) {
        return $$02.group((App)Codec.intRange((int)0, (int)32).fieldOf("base_height").forGetter($$0 -> $$0.baseHeight), (App)Codec.intRange((int)0, (int)24).fieldOf("height_rand_a").forGetter($$0 -> $$0.heightRandA), (App)Codec.intRange((int)0, (int)24).fieldOf("height_rand_b").forGetter($$0 -> $$0.heightRandB));
    }

    public TrunkPlacer(int $$0, int $$1, int $$2) {
        this.baseHeight = $$0;
        this.heightRandA = $$1;
        this.heightRandB = $$2;
    }

    protected abstract TrunkPlacerType<?> type();

    public abstract List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader var1, BiConsumer<BlockPos, BlockState> var2, RandomSource var3, int var4, BlockPos var5, TreeConfiguration var6);

    public int getTreeHeight(RandomSource $$0) {
        return this.baseHeight + $$0.nextInt(this.heightRandA + 1) + $$0.nextInt(this.heightRandB + 1);
    }

    private static boolean isDirt(LevelSimulatedReader $$02, BlockPos $$1) {
        return $$02.isStateAtPosition($$1, (Predicate<BlockState>)((Predicate)$$0 -> Feature.isDirt($$0) && !$$0.is(Blocks.GRASS_BLOCK) && !$$0.is(Blocks.MYCELIUM)));
    }

    protected static void setDirtAt(LevelSimulatedReader $$0, BiConsumer<BlockPos, BlockState> $$1, RandomSource $$2, BlockPos $$3, TreeConfiguration $$4) {
        if ($$4.forceDirt || !TrunkPlacer.isDirt($$0, $$3)) {
            $$1.accept((Object)$$3, (Object)$$4.dirtProvider.getState($$2, $$3));
        }
    }

    protected boolean placeLog(LevelSimulatedReader $$0, BiConsumer<BlockPos, BlockState> $$1, RandomSource $$2, BlockPos $$3, TreeConfiguration $$4) {
        return this.placeLog($$0, $$1, $$2, $$3, $$4, (Function<BlockState, BlockState>)Function.identity());
    }

    protected boolean placeLog(LevelSimulatedReader $$0, BiConsumer<BlockPos, BlockState> $$1, RandomSource $$2, BlockPos $$3, TreeConfiguration $$4, Function<BlockState, BlockState> $$5) {
        if (this.validTreePos($$0, $$3)) {
            $$1.accept((Object)$$3, (Object)((BlockState)$$5.apply((Object)$$4.trunkProvider.getState($$2, $$3))));
            return true;
        }
        return false;
    }

    protected void placeLogIfFree(LevelSimulatedReader $$0, BiConsumer<BlockPos, BlockState> $$1, RandomSource $$2, BlockPos.MutableBlockPos $$3, TreeConfiguration $$4) {
        if (this.isFree($$0, $$3)) {
            this.placeLog($$0, $$1, $$2, $$3, $$4);
        }
    }

    protected boolean validTreePos(LevelSimulatedReader $$0, BlockPos $$1) {
        return TreeFeature.validTreePos($$0, $$1);
    }

    public boolean isFree(LevelSimulatedReader $$02, BlockPos $$1) {
        return this.validTreePos($$02, $$1) || $$02.isStateAtPosition($$1, (Predicate<BlockState>)((Predicate)$$0 -> $$0.is(BlockTags.LOGS)));
    }
}