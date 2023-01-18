/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.lang.Double
 *  java.lang.Enum
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.EnumSet
 *  java.util.List
 *  java.util.Objects
 *  java.util.Optional
 *  java.util.function.BooleanSupplier
 *  java.util.function.Predicate
 *  java.util.function.ToDoubleFunction
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.ai.goal;

import com.google.common.collect.Lists;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.PoiTypeTags;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class MoveThroughVillageGoal
extends Goal {
    protected final PathfinderMob mob;
    private final double speedModifier;
    @Nullable
    private Path path;
    private BlockPos poiPos;
    private final boolean onlyAtNight;
    private final List<BlockPos> visited = Lists.newArrayList();
    private final int distanceToPoi;
    private final BooleanSupplier canDealWithDoors;

    public MoveThroughVillageGoal(PathfinderMob $$0, double $$1, boolean $$2, int $$3, BooleanSupplier $$4) {
        this.mob = $$0;
        this.speedModifier = $$1;
        this.onlyAtNight = $$2;
        this.distanceToPoi = $$3;
        this.canDealWithDoors = $$4;
        this.setFlags((EnumSet<Goal.Flag>)EnumSet.of((Enum)Goal.Flag.MOVE));
        if (!GoalUtils.hasGroundPathNavigation($$0)) {
            throw new IllegalArgumentException("Unsupported mob for MoveThroughVillageGoal");
        }
    }

    @Override
    public boolean canUse() {
        if (!GoalUtils.hasGroundPathNavigation(this.mob)) {
            return false;
        }
        this.updateVisited();
        if (this.onlyAtNight && this.mob.level.isDay()) {
            return false;
        }
        ServerLevel $$02 = (ServerLevel)this.mob.level;
        BlockPos $$1 = this.mob.blockPosition();
        if (!$$02.isCloseToVillage($$1, 6)) {
            return false;
        }
        Vec3 $$22 = LandRandomPos.getPos(this.mob, 15, 7, (ToDoubleFunction<BlockPos>)((ToDoubleFunction)$$2 -> {
            if (!$$02.isVillage((BlockPos)$$2)) {
                return Double.NEGATIVE_INFINITY;
            }
            Optional<BlockPos> $$3 = $$02.getPoiManager().find((Predicate<Holder<PoiType>>)((Predicate)$$0 -> $$0.is(PoiTypeTags.VILLAGE)), (Predicate<BlockPos>)((Predicate)this::hasNotVisited), (BlockPos)$$2, 10, PoiManager.Occupancy.IS_OCCUPIED);
            return (Double)$$3.map($$1 -> -$$1.distSqr($$1)).orElse((Object)Double.NEGATIVE_INFINITY);
        }));
        if ($$22 == null) {
            return false;
        }
        Optional<BlockPos> $$3 = $$02.getPoiManager().find((Predicate<Holder<PoiType>>)((Predicate)$$0 -> $$0.is(PoiTypeTags.VILLAGE)), (Predicate<BlockPos>)((Predicate)this::hasNotVisited), new BlockPos($$22), 10, PoiManager.Occupancy.IS_OCCUPIED);
        if ($$3.isEmpty()) {
            return false;
        }
        this.poiPos = ((BlockPos)$$3.get()).immutable();
        GroundPathNavigation $$4 = (GroundPathNavigation)this.mob.getNavigation();
        boolean $$5 = $$4.canOpenDoors();
        $$4.setCanOpenDoors(this.canDealWithDoors.getAsBoolean());
        this.path = $$4.createPath(this.poiPos, 0);
        $$4.setCanOpenDoors($$5);
        if (this.path == null) {
            Vec3 $$6 = DefaultRandomPos.getPosTowards(this.mob, 10, 7, Vec3.atBottomCenterOf(this.poiPos), 1.5707963705062866);
            if ($$6 == null) {
                return false;
            }
            $$4.setCanOpenDoors(this.canDealWithDoors.getAsBoolean());
            this.path = this.mob.getNavigation().createPath($$6.x, $$6.y, $$6.z, 0);
            $$4.setCanOpenDoors($$5);
            if (this.path == null) {
                return false;
            }
        }
        for (int $$7 = 0; $$7 < this.path.getNodeCount(); ++$$7) {
            Node $$8 = this.path.getNode($$7);
            BlockPos $$9 = new BlockPos($$8.x, $$8.y + 1, $$8.z);
            if (!DoorBlock.isWoodenDoor(this.mob.level, $$9)) continue;
            this.path = this.mob.getNavigation().createPath($$8.x, (double)$$8.y, $$8.z, 0);
            break;
        }
        return this.path != null;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.mob.getNavigation().isDone()) {
            return false;
        }
        return !this.poiPos.closerToCenterThan(this.mob.position(), this.mob.getBbWidth() + (float)this.distanceToPoi);
    }

    @Override
    public void start() {
        this.mob.getNavigation().moveTo(this.path, this.speedModifier);
    }

    @Override
    public void stop() {
        if (this.mob.getNavigation().isDone() || this.poiPos.closerToCenterThan(this.mob.position(), this.distanceToPoi)) {
            this.visited.add((Object)this.poiPos);
        }
    }

    private boolean hasNotVisited(BlockPos $$0) {
        for (BlockPos $$1 : this.visited) {
            if (!Objects.equals((Object)$$0, (Object)$$1)) continue;
            return false;
        }
        return true;
    }

    private void updateVisited() {
        if (this.visited.size() > 15) {
            this.visited.remove(0);
        }
    }
}