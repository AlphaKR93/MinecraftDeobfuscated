/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.util.Comparator
 *  java.util.Optional
 *  java.util.function.Predicate
 */
package net.minecraft.world.level.portal;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.levelgen.Heightmap;

public class PortalForcer {
    private static final int TICKET_RADIUS = 3;
    private static final int SEARCH_RADIUS = 128;
    private static final int CREATE_RADIUS = 16;
    private static final int FRAME_HEIGHT = 5;
    private static final int FRAME_WIDTH = 4;
    private static final int FRAME_BOX = 3;
    private static final int FRAME_HEIGHT_START = -1;
    private static final int FRAME_HEIGHT_END = 4;
    private static final int FRAME_WIDTH_START = -1;
    private static final int FRAME_WIDTH_END = 3;
    private static final int FRAME_BOX_START = -1;
    private static final int FRAME_BOX_END = 2;
    private static final int NOTHING_FOUND = -1;
    private final ServerLevel level;

    public PortalForcer(ServerLevel $$0) {
        this.level = $$0;
    }

    public Optional<BlockUtil.FoundRectangle> findPortalAround(BlockPos $$02, boolean $$12, WorldBorder $$2) {
        PoiManager $$3 = this.level.getPoiManager();
        int $$4 = $$12 ? 16 : 128;
        $$3.ensureLoadedAndValid(this.level, $$02, $$4);
        Optional $$5 = $$3.getInSquare((Predicate<Holder<PoiType>>)((Predicate)$$0 -> $$0.is(PoiTypes.NETHER_PORTAL)), $$02, $$4, PoiManager.Occupancy.ANY).filter($$1 -> $$2.isWithinBounds($$1.getPos())).sorted(Comparator.comparingDouble($$1 -> $$1.getPos().distSqr($$02)).thenComparingInt($$0 -> $$0.getPos().getY())).filter($$0 -> this.level.getBlockState($$0.getPos()).hasProperty(BlockStateProperties.HORIZONTAL_AXIS)).findFirst();
        return $$5.map($$0 -> {
            BlockPos $$12 = $$0.getPos();
            this.level.getChunkSource().addRegionTicket(TicketType.PORTAL, new ChunkPos($$12), 3, $$12);
            BlockState $$2 = this.level.getBlockState($$12);
            return BlockUtil.getLargestRectangleAround($$12, $$2.getValue(BlockStateProperties.HORIZONTAL_AXIS), 21, Direction.Axis.Y, 21, (Predicate<BlockPos>)((Predicate)$$1 -> this.level.getBlockState((BlockPos)$$1) == $$2));
        });
    }

    public Optional<BlockUtil.FoundRectangle> createPortal(BlockPos $$0, Direction.Axis $$1) {
        Direction $$2 = Direction.get(Direction.AxisDirection.POSITIVE, $$1);
        double $$3 = -1.0;
        BlockPos $$4 = null;
        double $$5 = -1.0;
        BlockPos $$6 = null;
        WorldBorder $$7 = this.level.getWorldBorder();
        int $$8 = Math.min((int)this.level.getMaxBuildHeight(), (int)(this.level.getMinBuildHeight() + this.level.getLogicalHeight())) - 1;
        BlockPos.MutableBlockPos $$9 = $$0.mutable();
        for (BlockPos.MutableBlockPos $$10 : BlockPos.spiralAround($$0, 16, Direction.EAST, Direction.SOUTH)) {
            int $$11 = Math.min((int)$$8, (int)this.level.getHeight(Heightmap.Types.MOTION_BLOCKING, $$10.getX(), $$10.getZ()));
            boolean $$12 = true;
            if (!$$7.isWithinBounds($$10) || !$$7.isWithinBounds($$10.move($$2, 1))) continue;
            $$10.move($$2.getOpposite(), 1);
            for (int $$13 = $$11; $$13 >= this.level.getMinBuildHeight(); --$$13) {
                int $$15;
                $$10.setY($$13);
                if (!this.canPortalReplaceBlock($$10)) continue;
                int $$14 = $$13;
                while ($$13 > this.level.getMinBuildHeight() && this.canPortalReplaceBlock($$10.move(Direction.DOWN))) {
                    --$$13;
                }
                if ($$13 + 4 > $$8 || ($$15 = $$14 - $$13) > 0 && $$15 < 3) continue;
                $$10.setY($$13);
                if (!this.canHostFrame($$10, $$9, $$2, 0)) continue;
                double $$16 = $$0.distSqr($$10);
                if (this.canHostFrame($$10, $$9, $$2, -1) && this.canHostFrame($$10, $$9, $$2, 1) && ($$3 == -1.0 || $$3 > $$16)) {
                    $$3 = $$16;
                    $$4 = $$10.immutable();
                }
                if ($$3 != -1.0 || $$5 != -1.0 && !($$5 > $$16)) continue;
                $$5 = $$16;
                $$6 = $$10.immutable();
            }
        }
        if ($$3 == -1.0 && $$5 != -1.0) {
            $$4 = $$6;
            $$3 = $$5;
        }
        if ($$3 == -1.0) {
            int $$18 = $$8 - 9;
            int $$17 = Math.max((int)(this.level.getMinBuildHeight() - -1), (int)70);
            if ($$18 < $$17) {
                return Optional.empty();
            }
            $$4 = new BlockPos($$0.getX(), Mth.clamp($$0.getY(), $$17, $$18), $$0.getZ()).immutable();
            Direction $$19 = $$2.getClockWise();
            if (!$$7.isWithinBounds($$4)) {
                return Optional.empty();
            }
            for (int $$20 = -1; $$20 < 2; ++$$20) {
                for (int $$21 = 0; $$21 < 2; ++$$21) {
                    for (int $$22 = -1; $$22 < 3; ++$$22) {
                        BlockState $$23 = $$22 < 0 ? Blocks.OBSIDIAN.defaultBlockState() : Blocks.AIR.defaultBlockState();
                        $$9.setWithOffset($$4, $$21 * $$2.getStepX() + $$20 * $$19.getStepX(), $$22, $$21 * $$2.getStepZ() + $$20 * $$19.getStepZ());
                        this.level.setBlockAndUpdate($$9, $$23);
                    }
                }
            }
        }
        for (int $$24 = -1; $$24 < 3; ++$$24) {
            for (int $$25 = -1; $$25 < 4; ++$$25) {
                if ($$24 != -1 && $$24 != 2 && $$25 != -1 && $$25 != 3) continue;
                $$9.setWithOffset($$4, $$24 * $$2.getStepX(), $$25, $$24 * $$2.getStepZ());
                this.level.setBlock($$9, Blocks.OBSIDIAN.defaultBlockState(), 3);
            }
        }
        BlockState $$26 = (BlockState)Blocks.NETHER_PORTAL.defaultBlockState().setValue(NetherPortalBlock.AXIS, $$1);
        for (int $$27 = 0; $$27 < 2; ++$$27) {
            for (int $$28 = 0; $$28 < 3; ++$$28) {
                $$9.setWithOffset($$4, $$27 * $$2.getStepX(), $$28, $$27 * $$2.getStepZ());
                this.level.setBlock($$9, $$26, 18);
            }
        }
        return Optional.of((Object)new BlockUtil.FoundRectangle($$4.immutable(), 2, 3));
    }

    private boolean canPortalReplaceBlock(BlockPos.MutableBlockPos $$0) {
        BlockState $$1 = this.level.getBlockState($$0);
        return $$1.canBeReplaced() && $$1.getFluidState().isEmpty();
    }

    private boolean canHostFrame(BlockPos $$0, BlockPos.MutableBlockPos $$1, Direction $$2, int $$3) {
        Direction $$4 = $$2.getClockWise();
        for (int $$5 = -1; $$5 < 3; ++$$5) {
            for (int $$6 = -1; $$6 < 4; ++$$6) {
                $$1.setWithOffset($$0, $$2.getStepX() * $$5 + $$4.getStepX() * $$3, $$6, $$2.getStepZ() * $$5 + $$4.getStepZ() * $$3);
                if ($$6 < 0 && !this.level.getBlockState($$1).getMaterial().isSolid()) {
                    return false;
                }
                if ($$6 < 0 || this.canPortalReplaceBlock($$1)) continue;
                return false;
            }
        }
        return true;
    }
}