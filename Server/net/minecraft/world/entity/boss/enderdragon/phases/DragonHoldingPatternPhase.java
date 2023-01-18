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
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.AbstractDragonPhaseInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.EndPodiumFeature;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class DragonHoldingPatternPhase
extends AbstractDragonPhaseInstance {
    private static final TargetingConditions NEW_TARGET_TARGETING = TargetingConditions.forCombat().ignoreLineOfSight();
    @Nullable
    private Path currentPath;
    @Nullable
    private Vec3 targetLocation;
    private boolean clockwise;

    public DragonHoldingPatternPhase(EnderDragon $$0) {
        super($$0);
    }

    public EnderDragonPhase<DragonHoldingPatternPhase> getPhase() {
        return EnderDragonPhase.HOLDING_PATTERN;
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
    public void begin() {
        this.currentPath = null;
        this.targetLocation = null;
    }

    @Override
    @Nullable
    public Vec3 getFlyTargetLocation() {
        return this.targetLocation;
    }

    private void findNewTarget() {
        if (this.currentPath != null && this.currentPath.isDone()) {
            double $$4;
            int $$1;
            BlockPos $$0 = this.dragon.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(EndPodiumFeature.END_PODIUM_LOCATION));
            int n = $$1 = this.dragon.getDragonFight() == null ? 0 : this.dragon.getDragonFight().getCrystalsAlive();
            if (this.dragon.getRandom().nextInt($$1 + 3) == 0) {
                this.dragon.getPhaseManager().setPhase(EnderDragonPhase.LANDING_APPROACH);
                return;
            }
            Player $$2 = this.dragon.level.getNearestPlayer(NEW_TARGET_TARGETING, this.dragon, $$0.getX(), $$0.getY(), $$0.getZ());
            if ($$2 != null) {
                double $$3 = $$0.distToCenterSqr($$2.position()) / 512.0;
            } else {
                $$4 = 64.0;
            }
            if ($$2 != null && (this.dragon.getRandom().nextInt((int)($$4 + 2.0)) == 0 || this.dragon.getRandom().nextInt($$1 + 2) == 0)) {
                this.strafePlayer($$2);
                return;
            }
        }
        if (this.currentPath == null || this.currentPath.isDone()) {
            int $$5;
            int $$6 = $$5 = this.dragon.findClosestNode();
            if (this.dragon.getRandom().nextInt(8) == 0) {
                this.clockwise = !this.clockwise;
                $$6 += 6;
            }
            $$6 = this.clockwise ? ++$$6 : --$$6;
            if (this.dragon.getDragonFight() == null || this.dragon.getDragonFight().getCrystalsAlive() < 0) {
                $$6 -= 12;
                $$6 &= 7;
                $$6 += 12;
            } else if (($$6 %= 12) < 0) {
                $$6 += 12;
            }
            this.currentPath = this.dragon.findPath($$5, $$6, null);
            if (this.currentPath != null) {
                this.currentPath.advance();
            }
        }
        this.navigateToNextPathNode();
    }

    private void strafePlayer(Player $$0) {
        this.dragon.getPhaseManager().setPhase(EnderDragonPhase.STRAFE_PLAYER);
        this.dragon.getPhaseManager().getPhase(EnderDragonPhase.STRAFE_PLAYER).setTarget($$0);
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

    @Override
    public void onCrystalDestroyed(EndCrystal $$0, BlockPos $$1, DamageSource $$2, @Nullable Player $$3) {
        if ($$3 != null && this.dragon.canAttack($$3)) {
            this.strafePlayer($$3);
        }
    }
}