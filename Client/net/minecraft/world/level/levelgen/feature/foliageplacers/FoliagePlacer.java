/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Products$P2
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Mu
 *  java.lang.Math
 *  java.lang.Object
 *  java.util.function.BiConsumer
 *  java.util.function.Predicate
 */
package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public abstract class FoliagePlacer {
    public static final Codec<FoliagePlacer> CODEC = BuiltInRegistries.FOLIAGE_PLACER_TYPE.byNameCodec().dispatch(FoliagePlacer::type, FoliagePlacerType::codec);
    protected final IntProvider radius;
    protected final IntProvider offset;

    protected static <P extends FoliagePlacer> Products.P2<RecordCodecBuilder.Mu<P>, IntProvider, IntProvider> foliagePlacerParts(RecordCodecBuilder.Instance<P> $$02) {
        return $$02.group((App)IntProvider.codec(0, 16).fieldOf("radius").forGetter($$0 -> $$0.radius), (App)IntProvider.codec(0, 16).fieldOf("offset").forGetter($$0 -> $$0.offset));
    }

    public FoliagePlacer(IntProvider $$0, IntProvider $$1) {
        this.radius = $$0;
        this.offset = $$1;
    }

    protected abstract FoliagePlacerType<?> type();

    public void createFoliage(LevelSimulatedReader $$0, BiConsumer<BlockPos, BlockState> $$1, RandomSource $$2, TreeConfiguration $$3, int $$4, FoliageAttachment $$5, int $$6, int $$7) {
        this.createFoliage($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, this.offset($$2));
    }

    protected abstract void createFoliage(LevelSimulatedReader var1, BiConsumer<BlockPos, BlockState> var2, RandomSource var3, TreeConfiguration var4, int var5, FoliageAttachment var6, int var7, int var8, int var9);

    public abstract int foliageHeight(RandomSource var1, int var2, TreeConfiguration var3);

    public int foliageRadius(RandomSource $$0, int $$1) {
        return this.radius.sample($$0);
    }

    private int offset(RandomSource $$0) {
        return this.offset.sample($$0);
    }

    protected abstract boolean shouldSkipLocation(RandomSource var1, int var2, int var3, int var4, int var5, boolean var6);

    protected boolean shouldSkipLocationSigned(RandomSource $$0, int $$1, int $$2, int $$3, int $$4, boolean $$5) {
        int $$9;
        int $$8;
        if ($$5) {
            int $$6 = Math.min((int)Math.abs((int)$$1), (int)Math.abs((int)($$1 - 1)));
            int $$7 = Math.min((int)Math.abs((int)$$3), (int)Math.abs((int)($$3 - 1)));
        } else {
            $$8 = Math.abs((int)$$1);
            $$9 = Math.abs((int)$$3);
        }
        return this.shouldSkipLocation($$0, $$8, $$2, $$9, $$4, $$5);
    }

    protected void placeLeavesRow(LevelSimulatedReader $$0, BiConsumer<BlockPos, BlockState> $$1, RandomSource $$2, TreeConfiguration $$3, BlockPos $$4, int $$5, int $$6, boolean $$7) {
        int $$8 = $$7 ? 1 : 0;
        BlockPos.MutableBlockPos $$9 = new BlockPos.MutableBlockPos();
        for (int $$10 = -$$5; $$10 <= $$5 + $$8; ++$$10) {
            for (int $$11 = -$$5; $$11 <= $$5 + $$8; ++$$11) {
                if (this.shouldSkipLocationSigned($$2, $$10, $$6, $$11, $$5, $$7)) continue;
                $$9.setWithOffset($$4, $$10, $$6, $$11);
                FoliagePlacer.tryPlaceLeaf($$0, $$1, $$2, $$3, $$9);
            }
        }
    }

    protected static void tryPlaceLeaf(LevelSimulatedReader $$02, BiConsumer<BlockPos, BlockState> $$1, RandomSource $$2, TreeConfiguration $$3, BlockPos $$4) {
        if (TreeFeature.validTreePos($$02, $$4)) {
            BlockState $$5 = $$3.foliageProvider.getState($$2, $$4);
            if ($$5.hasProperty(BlockStateProperties.WATERLOGGED)) {
                $$5 = (BlockState)$$5.setValue(BlockStateProperties.WATERLOGGED, $$02.isFluidAtPosition($$4, (Predicate<FluidState>)((Predicate)$$0 -> $$0.isSourceOfType(Fluids.WATER))));
            }
            $$1.accept((Object)$$4, (Object)$$5);
        }
    }

    public static final class FoliageAttachment {
        private final BlockPos pos;
        private final int radiusOffset;
        private final boolean doubleTrunk;

        public FoliageAttachment(BlockPos $$0, int $$1, boolean $$2) {
            this.pos = $$0;
            this.radiusOffset = $$1;
            this.doubleTrunk = $$2;
        }

        public BlockPos pos() {
            return this.pos;
        }

        public int radiusOffset() {
            return this.radiusOffset;
        }

        public boolean doubleTrunk() {
            return this.doubleTrunk;
        }
    }
}