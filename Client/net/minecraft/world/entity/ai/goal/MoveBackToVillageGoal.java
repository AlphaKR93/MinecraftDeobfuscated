/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

public class MoveBackToVillageGoal
extends RandomStrollGoal {
    private static final int MAX_XZ_DIST = 10;
    private static final int MAX_Y_DIST = 7;

    public MoveBackToVillageGoal(PathfinderMob $$0, double $$1, boolean $$2) {
        super($$0, $$1, 10, $$2);
    }

    @Override
    public boolean canUse() {
        ServerLevel $$0 = (ServerLevel)this.mob.level;
        BlockPos $$1 = this.mob.blockPosition();
        if ($$0.isVillage($$1)) {
            return false;
        }
        return super.canUse();
    }

    @Override
    @Nullable
    protected Vec3 getPosition() {
        ServerLevel $$0 = (ServerLevel)this.mob.level;
        BlockPos $$1 = this.mob.blockPosition();
        SectionPos $$2 = SectionPos.of($$1);
        SectionPos $$3 = BehaviorUtils.findSectionClosestToVillage($$0, $$2, 2);
        if ($$3 != $$2) {
            return DefaultRandomPos.getPosTowards(this.mob, 10, 7, Vec3.atBottomCenterOf($$3.center()), 1.5707963705062866);
        }
        return null;
    }
}