/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.util.Optional
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.portal;

import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PortalShape {
    private static final int MIN_WIDTH = 2;
    public static final int MAX_WIDTH = 21;
    private static final int MIN_HEIGHT = 3;
    public static final int MAX_HEIGHT = 21;
    private static final BlockBehaviour.StatePredicate FRAME = ($$0, $$1, $$2) -> $$0.is(Blocks.OBSIDIAN);
    private static final float SAFE_TRAVEL_MAX_ENTITY_XY = 4.0f;
    private static final double SAFE_TRAVEL_MAX_VERTICAL_DELTA = 1.0;
    private final LevelAccessor level;
    private final Direction.Axis axis;
    private final Direction rightDir;
    private int numPortalBlocks;
    @Nullable
    private BlockPos bottomLeft;
    private int height;
    private final int width;

    public static Optional<PortalShape> findEmptyPortalShape(LevelAccessor $$02, BlockPos $$1, Direction.Axis $$2) {
        return PortalShape.findPortalShape($$02, $$1, (Predicate<PortalShape>)((Predicate)$$0 -> $$0.isValid() && $$0.numPortalBlocks == 0), $$2);
    }

    public static Optional<PortalShape> findPortalShape(LevelAccessor $$0, BlockPos $$1, Predicate<PortalShape> $$2, Direction.Axis $$3) {
        Optional $$4 = Optional.of((Object)new PortalShape($$0, $$1, $$3)).filter($$2);
        if ($$4.isPresent()) {
            return $$4;
        }
        Direction.Axis $$5 = $$3 == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
        return Optional.of((Object)new PortalShape($$0, $$1, $$5)).filter($$2);
    }

    public PortalShape(LevelAccessor $$0, BlockPos $$1, Direction.Axis $$2) {
        this.level = $$0;
        this.axis = $$2;
        this.rightDir = $$2 == Direction.Axis.X ? Direction.WEST : Direction.SOUTH;
        this.bottomLeft = this.calculateBottomLeft($$1);
        if (this.bottomLeft == null) {
            this.bottomLeft = $$1;
            this.width = 1;
            this.height = 1;
        } else {
            this.width = this.calculateWidth();
            if (this.width > 0) {
                this.height = this.calculateHeight();
            }
        }
    }

    @Nullable
    private BlockPos calculateBottomLeft(BlockPos $$0) {
        int $$1 = Math.max((int)this.level.getMinBuildHeight(), (int)($$0.getY() - 21));
        while ($$0.getY() > $$1 && PortalShape.isEmpty(this.level.getBlockState((BlockPos)((BlockPos)$$0).below()))) {
            $$0 = ((BlockPos)$$0).below();
        }
        Direction $$2 = this.rightDir.getOpposite();
        int $$3 = this.getDistanceUntilEdgeAboveFrame((BlockPos)$$0, $$2) - 1;
        if ($$3 < 0) {
            return null;
        }
        return ((BlockPos)$$0).relative($$2, $$3);
    }

    private int calculateWidth() {
        int $$0 = this.getDistanceUntilEdgeAboveFrame(this.bottomLeft, this.rightDir);
        if ($$0 < 2 || $$0 > 21) {
            return 0;
        }
        return $$0;
    }

    private int getDistanceUntilEdgeAboveFrame(BlockPos $$0, Direction $$1) {
        BlockPos.MutableBlockPos $$2 = new BlockPos.MutableBlockPos();
        for (int $$3 = 0; $$3 <= 21; ++$$3) {
            $$2.set($$0).move($$1, $$3);
            BlockState $$4 = this.level.getBlockState($$2);
            if (!PortalShape.isEmpty($$4)) {
                if (!FRAME.test($$4, this.level, $$2)) break;
                return $$3;
            }
            BlockState $$5 = this.level.getBlockState($$2.move(Direction.DOWN));
            if (!FRAME.test($$5, this.level, $$2)) break;
        }
        return 0;
    }

    private int calculateHeight() {
        BlockPos.MutableBlockPos $$0 = new BlockPos.MutableBlockPos();
        int $$1 = this.getDistanceUntilTop($$0);
        if ($$1 < 3 || $$1 > 21 || !this.hasTopFrame($$0, $$1)) {
            return 0;
        }
        return $$1;
    }

    private boolean hasTopFrame(BlockPos.MutableBlockPos $$0, int $$1) {
        for (int $$2 = 0; $$2 < this.width; ++$$2) {
            BlockPos.MutableBlockPos $$3 = $$0.set(this.bottomLeft).move(Direction.UP, $$1).move(this.rightDir, $$2);
            if (FRAME.test(this.level.getBlockState($$3), this.level, $$3)) continue;
            return false;
        }
        return true;
    }

    private int getDistanceUntilTop(BlockPos.MutableBlockPos $$0) {
        for (int $$1 = 0; $$1 < 21; ++$$1) {
            $$0.set(this.bottomLeft).move(Direction.UP, $$1).move(this.rightDir, -1);
            if (!FRAME.test(this.level.getBlockState($$0), this.level, $$0)) {
                return $$1;
            }
            $$0.set(this.bottomLeft).move(Direction.UP, $$1).move(this.rightDir, this.width);
            if (!FRAME.test(this.level.getBlockState($$0), this.level, $$0)) {
                return $$1;
            }
            for (int $$2 = 0; $$2 < this.width; ++$$2) {
                $$0.set(this.bottomLeft).move(Direction.UP, $$1).move(this.rightDir, $$2);
                BlockState $$3 = this.level.getBlockState($$0);
                if (!PortalShape.isEmpty($$3)) {
                    return $$1;
                }
                if (!$$3.is(Blocks.NETHER_PORTAL)) continue;
                ++this.numPortalBlocks;
            }
        }
        return 21;
    }

    private static boolean isEmpty(BlockState $$0) {
        return $$0.isAir() || $$0.is(BlockTags.FIRE) || $$0.is(Blocks.NETHER_PORTAL);
    }

    public boolean isValid() {
        return this.bottomLeft != null && this.width >= 2 && this.width <= 21 && this.height >= 3 && this.height <= 21;
    }

    public void createPortalBlocks() {
        BlockState $$0 = (BlockState)Blocks.NETHER_PORTAL.defaultBlockState().setValue(NetherPortalBlock.AXIS, this.axis);
        BlockPos.betweenClosed(this.bottomLeft, this.bottomLeft.relative(Direction.UP, this.height - 1).relative(this.rightDir, this.width - 1)).forEach($$1 -> this.level.setBlock((BlockPos)$$1, $$0, 18));
    }

    public boolean isComplete() {
        return this.isValid() && this.numPortalBlocks == this.width * this.height;
    }

    public static Vec3 getRelativePosition(BlockUtil.FoundRectangle $$0, Direction.Axis $$1, Vec3 $$2, EntityDimensions $$3) {
        double $$12;
        double $$9;
        double $$4 = (double)$$0.axis1Size - (double)$$3.width;
        double $$5 = (double)$$0.axis2Size - (double)$$3.height;
        BlockPos $$6 = $$0.minCorner;
        if ($$4 > 0.0) {
            float $$7 = (float)$$6.get($$1) + $$3.width / 2.0f;
            double $$8 = Mth.clamp(Mth.inverseLerp($$2.get($$1) - (double)$$7, 0.0, $$4), 0.0, 1.0);
        } else {
            $$9 = 0.5;
        }
        if ($$5 > 0.0) {
            Direction.Axis $$10 = Direction.Axis.Y;
            double $$11 = Mth.clamp(Mth.inverseLerp($$2.get($$10) - (double)$$6.get($$10), 0.0, $$5), 0.0, 1.0);
        } else {
            $$12 = 0.0;
        }
        Direction.Axis $$13 = $$1 == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
        double $$14 = $$2.get($$13) - ((double)$$6.get($$13) + 0.5);
        return new Vec3($$9, $$12, $$14);
    }

    public static PortalInfo createPortalInfo(ServerLevel $$0, BlockUtil.FoundRectangle $$1, Direction.Axis $$2, Vec3 $$3, Entity $$4, Vec3 $$5, float $$6, float $$7) {
        BlockPos $$8 = $$1.minCorner;
        BlockState $$9 = $$0.getBlockState($$8);
        Direction.Axis $$10 = (Direction.Axis)$$9.getOptionalValue(BlockStateProperties.HORIZONTAL_AXIS).orElse((Object)Direction.Axis.X);
        double $$11 = $$1.axis1Size;
        double $$12 = $$1.axis2Size;
        EntityDimensions $$13 = $$4.getDimensions($$4.getPose());
        int $$14 = $$2 == $$10 ? 0 : 90;
        Vec3 $$15 = $$2 == $$10 ? $$5 : new Vec3($$5.z, $$5.y, -$$5.x);
        double $$16 = (double)$$13.width / 2.0 + ($$11 - (double)$$13.width) * $$3.x();
        double $$17 = ($$12 - (double)$$13.height) * $$3.y();
        double $$18 = 0.5 + $$3.z();
        boolean $$19 = $$10 == Direction.Axis.X;
        Vec3 $$20 = new Vec3((double)$$8.getX() + ($$19 ? $$16 : $$18), (double)$$8.getY() + $$17, (double)$$8.getZ() + ($$19 ? $$18 : $$16));
        Vec3 $$21 = PortalShape.findCollisionFreePosition($$20, $$0, $$4, $$13);
        return new PortalInfo($$21, $$15, $$6 + (float)$$14, $$7);
    }

    private static Vec3 findCollisionFreePosition(Vec3 $$0, ServerLevel $$12, Entity $$2, EntityDimensions $$3) {
        if ($$3.width > 4.0f || $$3.height > 4.0f) {
            return $$0;
        }
        double $$4 = (double)$$3.height / 2.0;
        Vec3 $$5 = $$0.add(0.0, $$4, 0.0);
        VoxelShape $$6 = Shapes.create(AABB.ofSize($$5, $$3.width, 0.0, $$3.width).expandTowards(0.0, 1.0, 0.0).inflate(1.0E-6));
        Optional $$7 = $$12.findFreePosition($$2, $$6, $$5, $$3.width, $$3.height, $$3.width);
        Optional $$8 = $$7.map($$1 -> $$1.subtract(0.0, $$4, 0.0));
        return (Vec3)$$8.orElse((Object)$$0);
    }
}