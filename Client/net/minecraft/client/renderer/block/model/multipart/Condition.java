/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.FunctionalInterface
 *  java.lang.Object
 *  java.util.function.Predicate
 */
package net.minecraft.client.renderer.block.model.multipart;

import java.util.function.Predicate;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

@FunctionalInterface
public interface Condition {
    public static final Condition TRUE = $$02 -> $$0 -> true;
    public static final Condition FALSE = $$02 -> $$0 -> false;

    public Predicate<BlockState> getPredicate(StateDefinition<Block, BlockState> var1);
}