/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Enum
 *  java.lang.Object
 *  java.util.Iterator
 *  java.util.Optional
 */
package net.minecraft.world.level.block;

import java.util.Iterator;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public interface ChangeOverTimeBlock<T extends Enum<T>> {
    public static final int SCAN_DISTANCE = 4;

    public Optional<BlockState> getNext(BlockState var1);

    public float getChanceModifier();

    default public void onRandomTick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        float $$4 = 0.05688889f;
        if ($$3.nextFloat() < 0.05688889f) {
            this.applyChangeOverTime($$0, $$1, $$2, $$3);
        }
    }

    public T getAge();

    default public void applyChangeOverTime(BlockState $$0, ServerLevel $$1, BlockPos $$22, RandomSource $$3) {
        BlockPos $$7;
        int $$8;
        int $$4 = this.getAge().ordinal();
        int $$5 = 0;
        int $$6 = 0;
        Iterator iterator = BlockPos.withinManhattan($$22, 4, 4, 4).iterator();
        while (iterator.hasNext() && ($$8 = ($$7 = (BlockPos)iterator.next()).distManhattan($$22)) <= 4) {
            BlockState $$9;
            Block $$10;
            if ($$7.equals($$22) || !(($$10 = ($$9 = $$1.getBlockState($$7)).getBlock()) instanceof ChangeOverTimeBlock)) continue;
            T $$11 = ((ChangeOverTimeBlock)((Object)$$10)).getAge();
            if (this.getAge().getClass() != $$11.getClass()) continue;
            int $$12 = $$11.ordinal();
            if ($$12 < $$4) {
                return;
            }
            if ($$12 > $$4) {
                ++$$6;
                continue;
            }
            ++$$5;
        }
        float $$13 = (float)($$6 + 1) / (float)($$6 + $$5 + 1);
        float $$14 = $$13 * $$13 * this.getChanceModifier();
        if ($$3.nextFloat() < $$14) {
            this.getNext($$0).ifPresent($$2 -> $$1.setBlockAndUpdate($$22, (BlockState)$$2));
        }
    }
}