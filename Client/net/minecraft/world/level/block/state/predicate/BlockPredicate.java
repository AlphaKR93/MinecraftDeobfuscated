/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block.state.predicate;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BlockPredicate
implements Predicate<BlockState> {
    private final Block block;

    public BlockPredicate(Block $$0) {
        this.block = $$0;
    }

    public static BlockPredicate forBlock(Block $$0) {
        return new BlockPredicate($$0);
    }

    public boolean test(@Nullable BlockState $$0) {
        return $$0 != null && $$0.is(this.block);
    }
}