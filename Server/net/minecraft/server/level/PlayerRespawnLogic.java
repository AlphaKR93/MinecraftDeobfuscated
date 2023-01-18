/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  javax.annotation.Nullable
 */
package net.minecraft.server.level;

import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;

public class PlayerRespawnLogic {
    @Nullable
    protected static BlockPos getOverworldRespawnPos(ServerLevel $$0, int $$1, int $$2) {
        int $$5;
        boolean $$3 = $$0.dimensionType().hasCeiling();
        LevelChunk $$4 = $$0.getChunk(SectionPos.blockToSectionCoord($$1), SectionPos.blockToSectionCoord($$2));
        int n = $$5 = $$3 ? $$0.getChunkSource().getGenerator().getSpawnHeight($$0) : $$4.getHeight(Heightmap.Types.MOTION_BLOCKING, $$1 & 0xF, $$2 & 0xF);
        if ($$5 < $$0.getMinBuildHeight()) {
            return null;
        }
        int $$6 = $$4.getHeight(Heightmap.Types.WORLD_SURFACE, $$1 & 0xF, $$2 & 0xF);
        if ($$6 <= $$5 && $$6 > $$4.getHeight(Heightmap.Types.OCEAN_FLOOR, $$1 & 0xF, $$2 & 0xF)) {
            return null;
        }
        BlockPos.MutableBlockPos $$7 = new BlockPos.MutableBlockPos();
        for (int $$8 = $$5 + 1; $$8 >= $$0.getMinBuildHeight(); --$$8) {
            $$7.set($$1, $$8, $$2);
            BlockState $$9 = $$0.getBlockState($$7);
            if (!$$9.getFluidState().isEmpty()) break;
            if (!Block.isFaceFull($$9.getCollisionShape($$0, $$7), Direction.UP)) continue;
            return ((BlockPos)$$7.above()).immutable();
        }
        return null;
    }

    @Nullable
    public static BlockPos getSpawnPosInChunk(ServerLevel $$0, ChunkPos $$1) {
        if (SharedConstants.debugVoidTerrain($$1)) {
            return null;
        }
        for (int $$2 = $$1.getMinBlockX(); $$2 <= $$1.getMaxBlockX(); ++$$2) {
            for (int $$3 = $$1.getMinBlockZ(); $$3 <= $$1.getMaxBlockZ(); ++$$3) {
                BlockPos $$4 = PlayerRespawnLogic.getOverworldRespawnPos($$0, $$2, $$3);
                if ($$4 == null) continue;
                return $$4;
            }
        }
        return null;
    }
}