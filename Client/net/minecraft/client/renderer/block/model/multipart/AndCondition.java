/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Streams
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.List
 *  java.util.function.Predicate
 *  java.util.stream.Collectors
 */
package net.minecraft.client.renderer.block.model.multipart;

import com.google.common.collect.Streams;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.client.renderer.block.model.multipart.Condition;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class AndCondition
implements Condition {
    public static final String TOKEN = "AND";
    private final Iterable<? extends Condition> conditions;

    public AndCondition(Iterable<? extends Condition> $$0) {
        this.conditions = $$0;
    }

    @Override
    public Predicate<BlockState> getPredicate(StateDefinition<Block, BlockState> $$0) {
        List $$13 = (List)Streams.stream(this.conditions).map($$1 -> $$1.getPredicate($$0)).collect(Collectors.toList());
        return $$12 -> $$13.stream().allMatch($$1 -> $$1.test($$12));
    }
}