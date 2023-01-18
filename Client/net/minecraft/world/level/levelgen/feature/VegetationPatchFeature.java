/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.HashSet
 *  java.util.Set
 *  java.util.function.Predicate
 */
package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.VegetationPatchConfiguration;

public class VegetationPatchFeature
extends Feature<VegetationPatchConfiguration> {
    public VegetationPatchFeature(Codec<VegetationPatchConfiguration> $$0) {
        super($$0);
    }

    @Override
    public boolean place(FeaturePlaceContext<VegetationPatchConfiguration> $$0) {
        WorldGenLevel $$12 = $$0.level();
        VegetationPatchConfiguration $$2 = $$0.config();
        RandomSource $$3 = $$0.random();
        BlockPos $$4 = $$0.origin();
        Predicate $$5 = $$1 -> $$1.is($$0.replaceable);
        int $$6 = $$2.xzRadius.sample($$3) + 1;
        int $$7 = $$2.xzRadius.sample($$3) + 1;
        Set<BlockPos> $$8 = this.placeGroundPatch($$12, $$2, $$3, $$4, (Predicate<BlockState>)$$5, $$6, $$7);
        this.distributeVegetation($$0, $$12, $$2, $$3, $$8, $$6, $$7);
        return !$$8.isEmpty();
    }

    protected Set<BlockPos> placeGroundPatch(WorldGenLevel $$02, VegetationPatchConfiguration $$1, RandomSource $$2, BlockPos $$3, Predicate<BlockState> $$4, int $$5, int $$6) {
        BlockPos.MutableBlockPos $$7 = $$3.mutable();
        BlockPos.MutableBlockPos $$8 = $$7.mutable();
        Direction $$9 = $$1.surface.getDirection();
        Direction $$10 = $$9.getOpposite();
        HashSet $$11 = new HashSet();
        for (int $$12 = -$$5; $$12 <= $$5; ++$$12) {
            boolean $$13 = $$12 == -$$5 || $$12 == $$5;
            for (int $$14 = -$$6; $$14 <= $$6; ++$$14) {
                int $$19;
                boolean $$18;
                boolean $$15 = $$14 == -$$6 || $$14 == $$6;
                boolean $$16 = $$13 || $$15;
                boolean $$17 = $$13 && $$15;
                boolean bl = $$18 = $$16 && !$$17;
                if ($$17 || $$18 && ($$1.extraEdgeColumnChance == 0.0f || $$2.nextFloat() > $$1.extraEdgeColumnChance)) continue;
                $$7.setWithOffset($$3, $$12, 0, $$14);
                for ($$19 = 0; $$02.isStateAtPosition($$7, BlockBehaviour.BlockStateBase::isAir) && $$19 < $$1.verticalRange; ++$$19) {
                    $$7.move($$9);
                }
                for ($$19 = 0; $$02.isStateAtPosition($$7, $$0 -> !$$0.isAir()) && $$19 < $$1.verticalRange; ++$$19) {
                    $$7.move($$10);
                }
                $$8.setWithOffset((Vec3i)$$7, $$1.surface.getDirection());
                BlockState $$20 = $$02.getBlockState($$8);
                if (!$$02.isEmptyBlock($$7) || !$$20.isFaceSturdy($$02, $$8, $$1.surface.getDirection().getOpposite())) continue;
                int $$21 = $$1.depth.sample($$2) + ($$1.extraBottomBlockChance > 0.0f && $$2.nextFloat() < $$1.extraBottomBlockChance ? 1 : 0);
                BlockPos $$22 = $$8.immutable();
                boolean $$23 = this.placeGround($$02, $$1, $$4, $$2, $$8, $$21);
                if (!$$23) continue;
                $$11.add((Object)$$22);
            }
        }
        return $$11;
    }

    protected void distributeVegetation(FeaturePlaceContext<VegetationPatchConfiguration> $$0, WorldGenLevel $$1, VegetationPatchConfiguration $$2, RandomSource $$3, Set<BlockPos> $$4, int $$5, int $$6) {
        for (BlockPos $$7 : $$4) {
            if (!($$2.vegetationChance > 0.0f) || !($$3.nextFloat() < $$2.vegetationChance)) continue;
            this.placeVegetation($$1, $$2, $$0.chunkGenerator(), $$3, $$7);
        }
    }

    protected boolean placeVegetation(WorldGenLevel $$0, VegetationPatchConfiguration $$1, ChunkGenerator $$2, RandomSource $$3, BlockPos $$4) {
        return $$1.vegetationFeature.value().place($$0, $$2, $$3, (BlockPos)$$4.relative($$1.surface.getDirection().getOpposite()));
    }

    protected boolean placeGround(WorldGenLevel $$0, VegetationPatchConfiguration $$1, Predicate<BlockState> $$2, RandomSource $$3, BlockPos.MutableBlockPos $$4, int $$5) {
        for (int $$6 = 0; $$6 < $$5; ++$$6) {
            BlockState $$8;
            BlockState $$7 = $$1.groundState.getState($$3, $$4);
            if ($$7.is(($$8 = $$0.getBlockState($$4)).getBlock())) continue;
            if (!$$2.test((Object)$$8)) {
                return $$6 != 0;
            }
            $$0.setBlock($$4, $$7, 2);
            $$4.move($$1.surface.getDirection());
        }
        return true;
    }
}