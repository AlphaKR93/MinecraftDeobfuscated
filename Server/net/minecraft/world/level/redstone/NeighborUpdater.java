/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.Throwable
 *  java.util.Locale
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.redstone;

import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public interface NeighborUpdater {
    public static final Direction[] UPDATE_ORDER = new Direction[]{Direction.WEST, Direction.EAST, Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH};

    public void shapeUpdate(Direction var1, BlockState var2, BlockPos var3, BlockPos var4, int var5, int var6);

    public void neighborChanged(BlockPos var1, Block var2, BlockPos var3);

    public void neighborChanged(BlockState var1, BlockPos var2, Block var3, BlockPos var4, boolean var5);

    default public void updateNeighborsAtExceptFromFacing(BlockPos $$0, Block $$1, @Nullable Direction $$2) {
        for (Direction $$3 : UPDATE_ORDER) {
            if ($$3 == $$2) continue;
            this.neighborChanged((BlockPos)$$0.relative($$3), $$1, $$0);
        }
    }

    public static void executeShapeUpdate(LevelAccessor $$0, Direction $$1, BlockState $$2, BlockPos $$3, BlockPos $$4, int $$5, int $$6) {
        BlockState $$7 = $$0.getBlockState($$3);
        BlockState $$8 = $$7.updateShape($$1, $$2, $$0, $$3, $$4);
        Block.updateOrDestroy($$7, $$8, $$0, $$3, $$5, $$6);
    }

    public static void executeUpdate(Level $$0, BlockState $$1, BlockPos $$2, Block $$3, BlockPos $$4, boolean $$5) {
        try {
            $$1.neighborChanged($$0, $$2, $$3, $$4, $$5);
        }
        catch (Throwable $$6) {
            CrashReport $$7 = CrashReport.forThrowable($$6, "Exception while updating neighbours");
            CrashReportCategory $$8 = $$7.addCategory("Block being updated");
            $$8.setDetail("Source block type", () -> {
                try {
                    return String.format((Locale)Locale.ROOT, (String)"ID #%s (%s // %s)", (Object[])new Object[]{BuiltInRegistries.BLOCK.getKey($$3), $$3.getDescriptionId(), $$3.getClass().getCanonicalName()});
                }
                catch (Throwable $$1) {
                    return "ID #" + BuiltInRegistries.BLOCK.getKey($$3);
                }
            });
            CrashReportCategory.populateBlockDetails($$8, $$0, $$2, $$1);
            throw new ReportedException($$7);
        }
    }
}