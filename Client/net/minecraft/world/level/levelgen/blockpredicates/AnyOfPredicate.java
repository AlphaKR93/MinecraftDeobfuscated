/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 */
package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import net.minecraft.world.level.levelgen.blockpredicates.CombiningPredicate;

class AnyOfPredicate
extends CombiningPredicate {
    public static final Codec<AnyOfPredicate> CODEC = AnyOfPredicate.codec(AnyOfPredicate::new);

    public AnyOfPredicate(List<BlockPredicate> $$0) {
        super($$0);
    }

    public boolean test(WorldGenLevel $$0, BlockPos $$1) {
        for (BlockPredicate $$2 : this.predicates) {
            if (!$$2.test($$0, $$1)) continue;
            return true;
        }
        return false;
    }

    @Override
    public BlockPredicateType<?> type() {
        return BlockPredicateType.ANY_OF;
    }
}