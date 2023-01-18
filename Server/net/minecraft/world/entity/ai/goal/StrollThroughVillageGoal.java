/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Enum
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.EnumSet
 *  java.util.function.ToDoubleFunction
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import java.util.function.ToDoubleFunction;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

public class StrollThroughVillageGoal
extends Goal {
    private static final int DISTANCE_THRESHOLD = 10;
    private final PathfinderMob mob;
    private final int interval;
    @Nullable
    private BlockPos wantedPos;

    public StrollThroughVillageGoal(PathfinderMob $$0, int $$1) {
        this.mob = $$0;
        this.interval = StrollThroughVillageGoal.reducedTickDelay($$1);
        this.setFlags((EnumSet<Goal.Flag>)EnumSet.of((Enum)Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.mob.isVehicle()) {
            return false;
        }
        if (this.mob.level.isDay()) {
            return false;
        }
        if (this.mob.getRandom().nextInt(this.interval) != 0) {
            return false;
        }
        ServerLevel $$0 = (ServerLevel)this.mob.level;
        BlockPos $$12 = this.mob.blockPosition();
        if (!$$0.isCloseToVillage($$12, 6)) {
            return false;
        }
        Vec3 $$2 = LandRandomPos.getPos(this.mob, 15, 7, (ToDoubleFunction<BlockPos>)((ToDoubleFunction)$$1 -> -$$0.sectionsToVillage(SectionPos.of($$1))));
        this.wantedPos = $$2 == null ? null : new BlockPos($$2);
        return this.wantedPos != null;
    }

    @Override
    public boolean canContinueToUse() {
        return this.wantedPos != null && !this.mob.getNavigation().isDone() && this.mob.getNavigation().getTargetPos().equals(this.wantedPos);
    }

    @Override
    public void tick() {
        if (this.wantedPos == null) {
            return;
        }
        PathNavigation $$0 = this.mob.getNavigation();
        if ($$0.isDone() && !this.wantedPos.closerToCenterThan(this.mob.position(), 10.0)) {
            Vec3 $$1 = Vec3.atBottomCenterOf(this.wantedPos);
            Vec3 $$2 = this.mob.position();
            Vec3 $$3 = $$2.subtract($$1);
            $$1 = $$3.scale(0.4).add($$1);
            Vec3 $$4 = $$1.subtract($$2).normalize().scale(10.0).add($$2);
            BlockPos $$5 = new BlockPos($$4);
            if (!$$0.moveTo(($$5 = this.mob.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, $$5)).getX(), $$5.getY(), $$5.getZ(), 1.0)) {
                this.moveRandomly();
            }
        }
    }

    private void moveRandomly() {
        RandomSource $$0 = this.mob.getRandom();
        BlockPos $$1 = this.mob.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, this.mob.blockPosition().offset(-8 + $$0.nextInt(16), 0, -8 + $$0.nextInt(16)));
        this.mob.getNavigation().moveTo($$1.getX(), $$1.getY(), $$1.getZ(), 1.0);
    }
}