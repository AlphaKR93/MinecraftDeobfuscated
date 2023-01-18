/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.boss.enderdragon.phases;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.AbstractDragonPhaseInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.EndPodiumFeature;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class DragonLandingApproachPhase
extends AbstractDragonPhaseInstance {
    private static final TargetingConditions NEAR_EGG_TARGETING = TargetingConditions.forCombat().ignoreLineOfSight();
    @Nullable
    private Path currentPath;
    @Nullable
    private Vec3 targetLocation;

    public DragonLandingApproachPhase(EnderDragon $$0) {
        super($$0);
    }

    public EnderDragonPhase<DragonLandingApproachPhase> getPhase() {
        return EnderDragonPhase.LANDING_APPROACH;
    }

    @Override
    public void begin() {
        this.currentPath = null;
        this.targetLocation = null;
    }

    @Override
    public void doServerTick() {
        double $$0;
        double d = $$0 = this.targetLocation == null ? 0.0 : this.targetLocation.distanceToSqr(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
        if ($$0 < 100.0 || $$0 > 22500.0 || this.dragon.horizontalCollision || this.dragon.verticalCollision) {
            this.findNewTarget();
        }
    }

    @Override
    @Nullable
    public Vec3 getFlyTargetLocation() {
        return this.targetLocation;
    }

    private void findNewTarget() {
        if (this.currentPath == null || this.currentPath.isDone()) {
            int $$5;
            int $$0 = this.dragon.findClosestNode();
            BlockPos $$1 = this.dragon.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.END_PODIUM_LOCATION);
            Player $$2 = this.dragon.level.getNearestPlayer(NEAR_EGG_TARGETING, this.dragon, $$1.getX(), $$1.getY(), $$1.getZ());
            if ($$2 != null) {
                Vec3 $$3 = new Vec3($$2.getX(), 0.0, $$2.getZ()).normalize();
                int $$4 = this.dragon.findClosestNode(-$$3.x * 40.0, 105.0, -$$3.z * 40.0);
            } else {
                $$5 = this.dragon.findClosestNode(40.0, $$1.getY(), 0.0);
            }
            Node $$6 = new Node($$1.getX(), $$1.getY(), $$1.getZ());
            this.currentPath = this.dragon.findPath($$0, $$5, $$6);
            if (this.currentPath != null) {
                this.currentPath.advance();
            }
        }
        this.navigateToNextPathNode();
        if (this.currentPath != null && this.currentPath.isDone()) {
            this.dragon.getPhaseManager().setPhase(EnderDragonPhase.LANDING);
        }
    }

    private void navigateToNextPathNode() {
        if (this.currentPath != null && !this.currentPath.isDone()) {
            double $$3;
            BlockPos $$0 = this.currentPath.getNextNodePos();
            this.currentPath.advance();
            double $$1 = $$0.getX();
            double $$2 = $$0.getZ();
            while (($$3 = (double)((float)$$0.getY() + this.dragon.getRandom().nextFloat() * 20.0f)) < (double)$$0.getY()) {
            }
            this.targetLocation = new Vec3($$1, $$3, $$2);
        }
    }
}