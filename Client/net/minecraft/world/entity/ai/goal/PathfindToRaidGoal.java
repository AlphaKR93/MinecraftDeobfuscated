/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  java.lang.Enum
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Collection
 *  java.util.EnumSet
 *  java.util.HashSet
 *  java.util.List
 */
package net.minecraft.world.entity.ai.goal;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.entity.raid.Raids;
import net.minecraft.world.phys.Vec3;

public class PathfindToRaidGoal<T extends Raider>
extends Goal {
    private static final int RECRUITMENT_SEARCH_TICK_DELAY = 20;
    private static final float SPEED_MODIFIER = 1.0f;
    private final T mob;
    private int recruitmentTick;

    public PathfindToRaidGoal(T $$0) {
        this.mob = $$0;
        this.setFlags((EnumSet<Goal.Flag>)EnumSet.of((Enum)Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return ((Mob)this.mob).getTarget() == null && !((Entity)this.mob).isVehicle() && ((Raider)this.mob).hasActiveRaid() && !((Raider)this.mob).getCurrentRaid().isOver() && !((ServerLevel)((Raider)this.mob).level).isVillage(((Entity)this.mob).blockPosition());
    }

    @Override
    public boolean canContinueToUse() {
        return ((Raider)this.mob).hasActiveRaid() && !((Raider)this.mob).getCurrentRaid().isOver() && ((Raider)this.mob).level instanceof ServerLevel && !((ServerLevel)((Raider)this.mob).level).isVillage(((Entity)this.mob).blockPosition());
    }

    @Override
    public void tick() {
        if (((Raider)this.mob).hasActiveRaid()) {
            Vec3 $$1;
            Raid $$0 = ((Raider)this.mob).getCurrentRaid();
            if (((Raider)this.mob).tickCount > this.recruitmentTick) {
                this.recruitmentTick = ((Raider)this.mob).tickCount + 20;
                this.recruitNearby($$0);
            }
            if (!((PathfinderMob)this.mob).isPathFinding() && ($$1 = DefaultRandomPos.getPosTowards(this.mob, 15, 4, Vec3.atBottomCenterOf($$0.getCenter()), 1.5707963705062866)) != null) {
                ((Mob)this.mob).getNavigation().moveTo($$1.x, $$1.y, $$1.z, 1.0);
            }
        }
    }

    private void recruitNearby(Raid $$0) {
        if ($$0.isActive()) {
            HashSet $$12 = Sets.newHashSet();
            List $$2 = ((Raider)this.mob).level.getEntitiesOfClass(Raider.class, ((Entity)this.mob).getBoundingBox().inflate(16.0), $$1 -> !$$1.hasActiveRaid() && Raids.canJoinRaid($$1, $$0));
            $$12.addAll((Collection)$$2);
            for (Raider $$3 : $$12) {
                $$0.joinRaid($$0.getGroupsSpawned(), $$3, null, true);
            }
        }
    }
}