/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  java.lang.Object
 *  java.lang.Throwable
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.Map
 *  javax.annotation.Nullable
 */
package net.minecraft.client.renderer.chunk;

import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.EmptyLevelChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.levelgen.DebugLevelSource;

class RenderChunk {
    private final Map<BlockPos, BlockEntity> blockEntities;
    @Nullable
    private final List<PalettedContainer<BlockState>> sections;
    private final boolean debug;
    private final LevelChunk wrapped;

    RenderChunk(LevelChunk $$0) {
        this.wrapped = $$0;
        this.debug = $$0.getLevel().isDebug();
        this.blockEntities = ImmutableMap.copyOf($$0.getBlockEntities());
        if ($$0 instanceof EmptyLevelChunk) {
            this.sections = null;
        } else {
            LevelChunkSection[] $$1 = $$0.getSections();
            this.sections = new ArrayList($$1.length);
            for (LevelChunkSection $$2 : $$1) {
                this.sections.add($$2.hasOnlyAir() ? null : $$2.getStates().copy());
            }
        }
    }

    @Nullable
    public BlockEntity getBlockEntity(BlockPos $$0) {
        return (BlockEntity)this.blockEntities.get((Object)$$0);
    }

    public BlockState getBlockState(BlockPos $$0) {
        int $$1 = $$0.getX();
        int $$2 = $$0.getY();
        int $$3 = $$0.getZ();
        if (this.debug) {
            BlockState $$4 = null;
            if ($$2 == 60) {
                $$4 = Blocks.BARRIER.defaultBlockState();
            }
            if ($$2 == 70) {
                $$4 = DebugLevelSource.getBlockStateFor($$1, $$3);
            }
            return $$4 == null ? Blocks.AIR.defaultBlockState() : $$4;
        }
        if (this.sections == null) {
            return Blocks.AIR.defaultBlockState();
        }
        try {
            PalettedContainer $$6;
            int $$5 = this.wrapped.getSectionIndex($$2);
            if ($$5 >= 0 && $$5 < this.sections.size() && ($$6 = (PalettedContainer)this.sections.get($$5)) != null) {
                return (BlockState)$$6.get($$1 & 0xF, $$2 & 0xF, $$3 & 0xF);
            }
            return Blocks.AIR.defaultBlockState();
        }
        catch (Throwable $$7) {
            CrashReport $$8 = CrashReport.forThrowable($$7, "Getting block state");
            CrashReportCategory $$9 = $$8.addCategory("Block being got");
            $$9.setDetail("Location", () -> CrashReportCategory.formatLocation((LevelHeightAccessor)this.wrapped, $$1, $$2, $$3));
            throw new ReportedException($$8);
        }
    }
}