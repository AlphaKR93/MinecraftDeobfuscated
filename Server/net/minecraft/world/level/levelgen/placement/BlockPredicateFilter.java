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
 */
package net.minecraft.world.level.levelgen.placement;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

public class BlockPredicateFilter
extends PlacementFilter {
    public static final Codec<BlockPredicateFilter> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)BlockPredicate.CODEC.fieldOf("predicate").forGetter($$0 -> $$0.predicate)).apply((Applicative)$$02, BlockPredicateFilter::new));
    private final BlockPredicate predicate;

    private BlockPredicateFilter(BlockPredicate $$0) {
        this.predicate = $$0;
    }

    public static BlockPredicateFilter forPredicate(BlockPredicate $$0) {
        return new BlockPredicateFilter($$0);
    }

    @Override
    protected boolean shouldPlace(PlacementContext $$0, RandomSource $$1, BlockPos $$2) {
        return this.predicate.test($$0.getLevel(), $$2);
    }

    @Override
    public PlacementModifierType<?> type() {
        return PlacementModifierType.BLOCK_PREDICATE_FILTER;
    }
}