/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Object
 *  java.util.List
 *  java.util.function.Function
 */
package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Function;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;

abstract class CombiningPredicate
implements BlockPredicate {
    protected final List<BlockPredicate> predicates;

    protected CombiningPredicate(List<BlockPredicate> $$0) {
        this.predicates = $$0;
    }

    public static <T extends CombiningPredicate> Codec<T> codec(Function<List<BlockPredicate>, T> $$0) {
        return RecordCodecBuilder.create($$1 -> $$1.group((App)BlockPredicate.CODEC.listOf().fieldOf("predicates").forGetter($$0 -> $$0.predicates)).apply((Applicative)$$1, $$0));
    }
}