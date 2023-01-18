/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import net.minecraft.world.level.levelgen.blockpredicates.StateTestingPredicate;

class ReplaceablePredicate
extends StateTestingPredicate {
    public static final Codec<ReplaceablePredicate> CODEC = RecordCodecBuilder.create($$0 -> ReplaceablePredicate.stateTestingCodec($$0).apply((Applicative)$$0, ReplaceablePredicate::new));

    public ReplaceablePredicate(Vec3i $$0) {
        super($$0);
    }

    @Override
    protected boolean test(BlockState $$0) {
        return $$0.canBeReplaced();
    }

    @Override
    public BlockPredicateType<?> type() {
        return BlockPredicateType.REPLACEABLE;
    }
}