/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.stream.Stream
 */
package net.minecraft.world.level.levelgen.placement;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

public class EnvironmentScanPlacement
extends PlacementModifier {
    private final Direction directionOfSearch;
    private final BlockPredicate targetCondition;
    private final BlockPredicate allowedSearchCondition;
    private final int maxSteps;
    public static final Codec<EnvironmentScanPlacement> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)Direction.VERTICAL_CODEC.fieldOf("direction_of_search").forGetter($$0 -> $$0.directionOfSearch), (App)BlockPredicate.CODEC.fieldOf("target_condition").forGetter($$0 -> $$0.targetCondition), (App)BlockPredicate.CODEC.optionalFieldOf("allowed_search_condition", (Object)BlockPredicate.alwaysTrue()).forGetter($$0 -> $$0.allowedSearchCondition), (App)Codec.intRange((int)1, (int)32).fieldOf("max_steps").forGetter($$0 -> $$0.maxSteps)).apply((Applicative)$$02, EnvironmentScanPlacement::new));

    private EnvironmentScanPlacement(Direction $$0, BlockPredicate $$1, BlockPredicate $$2, int $$3) {
        this.directionOfSearch = $$0;
        this.targetCondition = $$1;
        this.allowedSearchCondition = $$2;
        this.maxSteps = $$3;
    }

    public static EnvironmentScanPlacement scanningFor(Direction $$0, BlockPredicate $$1, BlockPredicate $$2, int $$3) {
        return new EnvironmentScanPlacement($$0, $$1, $$2, $$3);
    }

    public static EnvironmentScanPlacement scanningFor(Direction $$0, BlockPredicate $$1, int $$2) {
        return EnvironmentScanPlacement.scanningFor($$0, $$1, BlockPredicate.alwaysTrue(), $$2);
    }

    @Override
    public Stream<BlockPos> getPositions(PlacementContext $$0, RandomSource $$1, BlockPos $$2) {
        BlockPos.MutableBlockPos $$3 = $$2.mutable();
        WorldGenLevel $$4 = $$0.getLevel();
        if (!this.allowedSearchCondition.test($$4, $$3)) {
            return Stream.of((Object[])new BlockPos[0]);
        }
        for (int $$5 = 0; $$5 < this.maxSteps; ++$$5) {
            if (this.targetCondition.test($$4, $$3)) {
                return Stream.of((Object)$$3);
            }
            $$3.move(this.directionOfSearch);
            if ($$4.isOutsideBuildHeight($$3.getY())) {
                return Stream.of((Object[])new BlockPos[0]);
            }
            if (!this.allowedSearchCondition.test($$4, $$3)) break;
        }
        if (this.targetCondition.test($$4, $$3)) {
            return Stream.of((Object)$$3);
        }
        return Stream.of((Object[])new BlockPos[0]);
    }

    @Override
    public PlacementModifierType<?> type() {
        return PlacementModifierType.ENVIRONMENT_SCAN;
    }
}