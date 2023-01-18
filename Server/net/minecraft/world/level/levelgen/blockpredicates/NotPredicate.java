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
package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;

class NotPredicate
implements BlockPredicate {
    public static final Codec<NotPredicate> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)BlockPredicate.CODEC.fieldOf("predicate").forGetter($$0 -> $$0.predicate)).apply((Applicative)$$02, NotPredicate::new));
    private final BlockPredicate predicate;

    public NotPredicate(BlockPredicate $$0) {
        this.predicate = $$0;
    }

    public boolean test(WorldGenLevel $$0, BlockPos $$1) {
        return !this.predicate.test($$0, $$1);
    }

    @Override
    public BlockPredicateType<?> type() {
        return BlockPredicateType.NOT;
    }
}