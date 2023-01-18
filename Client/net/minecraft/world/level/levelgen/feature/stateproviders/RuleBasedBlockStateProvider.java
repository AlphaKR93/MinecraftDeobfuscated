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
 */
package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record RuleBasedBlockStateProvider(BlockStateProvider fallback, List<Rule> rules) {
    public static final Codec<RuleBasedBlockStateProvider> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)BlockStateProvider.CODEC.fieldOf("fallback").forGetter(RuleBasedBlockStateProvider::fallback), (App)Rule.CODEC.listOf().fieldOf("rules").forGetter(RuleBasedBlockStateProvider::rules)).apply((Applicative)$$0, RuleBasedBlockStateProvider::new));

    public static RuleBasedBlockStateProvider simple(BlockStateProvider $$0) {
        return new RuleBasedBlockStateProvider($$0, (List<Rule>)List.of());
    }

    public static RuleBasedBlockStateProvider simple(Block $$0) {
        return RuleBasedBlockStateProvider.simple(BlockStateProvider.simple($$0));
    }

    public BlockState getState(WorldGenLevel $$0, RandomSource $$1, BlockPos $$2) {
        for (Rule $$3 : this.rules) {
            if (!$$3.ifTrue().test($$0, $$2)) continue;
            return $$3.then().getState($$1, $$2);
        }
        return this.fallback.getState($$1, $$2);
    }

    public record Rule(BlockPredicate ifTrue, BlockStateProvider then) {
        public static final Codec<Rule> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)BlockPredicate.CODEC.fieldOf("if_true").forGetter(Rule::ifTrue), (App)BlockStateProvider.CODEC.fieldOf("then").forGetter(Rule::then)).apply((Applicative)$$0, Rule::new));
    }
}