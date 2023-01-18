/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world.entity.boss.enderdragon.phases;

import com.mojang.logging.LogUtils;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.AbstractDragonPhaseInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class DragonStrafePlayerPhase
extends AbstractDragonPhaseInstance {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int FIREBALL_CHARGE_AMOUNT = 5;
    private int fireballCharge;
    @Nullable
    private Path currentPath;
    @Nullable
    private Vec3 targetLocation;
    @Nullable
    private LivingEntity attackTarget;
    private boolean holdingPatternClockwise;

    public DragonStrafePlayerPhase(EnderDragon $$0) {
        super($$0);
    }

    @Override
    public void doServerTick() {
        double $$6;
        if (this.attackTarget == null) {
            LOGGER.warn("Skipping player strafe phase because no player was found");
            this.dragon.getPhaseManager().setPhase(EnderDragonPhase.HOLDING_PATTERN);
            return;
        }
        if (this.currentPath != null && this.currentPath.isDone()) {
            double $$0 = this.attackTarget.getX();
            double $$1 = this.attackTarget.getZ();
            double $$2 = $$0 - this.dragon.getX();
            double $$3 = $$1 - this.dragon.getZ();
            double $$4 = Math.sqrt((double)($$2 * $$2 + $$3 * $$3));
            double $$5 = Math.min((double)((double)0.4f + $$4 / 80.0 - 1.0), (double)10.0);
            this.targetLocation = new Vec3($$0, this.attackTarget.getY() + $$5, $$1);
        }
        double d = $$6 = this.targetLocation == null ? 0.0 : this.targetLocation.distanceToSqr(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
        if ($$6 < 100.0 || $$6 > 22500.0) {
            this.findNewTarget();
        }
        double $$7 = 64.0;
        if (this.attackTarget.distanceToSqr(this.dragon) < 4096.0) {
            if (this.dragon.hasLineOfSight(this.attackTarget)) {
                ++this.fireballCharge;
                Vec3 $$8 = new Vec3(this.attackTarget.getX() - this.dragon.getX(), 0.0, this.attackTarget.getZ() - this.dragon.getZ()).normalize();
                Vec3 $$9 = new Vec3(Mth.sin(this.dragon.getYRot() * ((float)Math.PI / 180)), 0.0, -Mth.cos(this.dragon.getYRot() * ((float)Math.PI / 180))).normalize();
                float $$10 = (float)$$9.dot($$8);
                float $$11 = (float)(Math.acos((double)$$10) * 57.2957763671875);
                $$11 += 0.5f;
                if (this.fireballCharge >= 5 && $$11 >= 0.0f && $$11 < 10.0f) {
                    double $$12 = 1.0;
                    Vec3 $$13 = this.dragon.getViewVector(1.0f);
                    double $$14 = this.dragon.head.getX() - $$13.x * 1.0;
                    double $$15 = this.dragon.head.getY(0.5) + 0.5;
                    double $$16 = this.dragon.head.getZ() - $$13.z * 1.0;
                    double $$17 = this.attackTarget.getX() - $$14;
                    double $$18 = this.attackTarget.getY(0.5) - $$15;
                    double $$19 = this.attackTarget.getZ() - $$16;
                    if (!this.dragon.isSilent()) {
                        this.dragon.level.levelEvent(null, 1017, this.dragon.blockPosition(), 0);
                    }
                    DragonFireball $$20 = new DragonFireball(this.dragon.level, this.dragon, $$17, $$18, $$19);
                    $$20.moveTo($$14, $$15, $$16, 0.0f, 0.0f);
                    this.dragon.level.addFreshEntity($$20);
                    this.fireballCharge = 0;
                    if (this.currentPath != null) {
                        while (!this.currentPath.isDone()) {
                            this.currentPath.advance();
                        }
                    }
                    this.dragon.getPhaseManager().setPhase(EnderDragonPhase.HOLDING_PATTERN);
                }
            } else if (this.fireballCharge > 0) {
                --this.fireballCharge;
            }
        } else if (this.fireballCharge > 0) {
            --this.fireballCharge;
        }
    }

    private void findNewTarget() {
        if (this.currentPath == null || this.currentPath.isDone()) {
            int $$0;
            int $$1 = $$0 = this.dragon.findClosestNode();
            if (this.dragon.getRandom().nextInt(8) == 0) {
                this.holdingPatternClockwise = !this.holdingPatternClockwise;
                $$1 += 6;
            }
            $$1 = this.holdingPatternClockwise ? ++$$1 : --$$1;
            if (this.dragon.getDragonFight() == null || this.dragon.getDragonFight().getCrystalsAlive() <= 0) {
                $$1 -= 12;
                $$1 &= 7;
                $$1 += 12;
            } else if (($$1 %= 12) < 0) {
                $$1 += 12;
            }
            this.currentPath = this.dragon.findPath($$0, $$1, null);
            if (this.currentPath != null) {
                this.currentPath.advance();
            }
        }
        this.navigateToNextPathNode();
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
    public void begin() {
        this.fireballCharge = 0;
        this.targetLocation = null;
        this.currentPath = null;
        this.attackTarget = null;
    }

    public void setTarget(LivingEntity $$0) {
        this.attackTarget = $$0;
        int $$1 = this.dragon.findClosestNode();
        int $$2 = this.dragon.findClosestNode(this.attackTarget.getX(), this.attackTarget.getY(), this.attackTarget.getZ());
        int $$3 = this.attackTarget.getBlockX();
        int $$4 = this.attackTarget.getBlockZ();
        double $$5 = (double)$$3 - this.dragon.getX();
        double $$6 = (double)$$4 - this.dragon.getZ();
        double $$7 = Math.sqrt((double)($$5 * $$5 + $$6 * $$6));
        double $$8 = Math.min((double)((double)0.4f + $$7 / 80.0 - 1.0), (double)10.0);
        int $$9 = Mth.floor(this.attackTarget.getY() + $$8);
        Node $$10 = new Node($$3, $$9, $$4);
        this.currentPath = this.dragon.findPath($$1, $$2, $$10);
        if (this.currentPath != null) {
            this.currentPath.advance();
            this.navigateToNextPathNode();
        }
    }

    @Override
    @Nullable
    public Vec3 getFlyTargetLocation() {
        return this.targetLocation;
    }

    public EnderDragonPhase<DragonStrafePlayerPhase> getPhase() {
        return EnderDragonPhase.STRAFE_PLAYER;
    }
}