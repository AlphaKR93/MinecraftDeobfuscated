/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.ai.navigation;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;

public class WallClimberNavigation
extends GroundPathNavigation {
    @Nullable
    private BlockPos pathToPosition;

    public WallClimberNavigation(Mob $$0, Level $$1) {
        super($$0, $$1);
    }

    @Override
    public Path createPath(BlockPos $$0, int $$1) {
        this.pathToPosition = $$0;
        return super.createPath($$0, $$1);
    }

    @Override
    public Path createPath(Entity $$0, int $$1) {
        this.pathToPosition = $$0.blockPosition();
        return super.createPath($$0, $$1);
    }

    @Override
    public boolean moveTo(Entity $$0, double $$1) {
        Path $$2 = this.createPath($$0, 0);
        if ($$2 != null) {
            return this.moveTo($$2, $$1);
        }
        this.pathToPosition = $$0.blockPosition();
        this.speedModifier = $$1;
        return true;
    }

    @Override
    public void tick() {
        if (this.isDone()) {
            if (this.pathToPosition != null) {
                if (this.pathToPosition.closerToCenterThan(this.mob.position(), this.mob.getBbWidth()) || this.mob.getY() > (double)this.pathToPosition.getY() && new BlockPos((double)this.pathToPosition.getX(), this.mob.getY(), (double)this.pathToPosition.getZ()).closerToCenterThan(this.mob.position(), this.mob.getBbWidth())) {
                    this.pathToPosition = null;
                } else {
                    this.mob.getMoveControl().setWantedPosition(this.pathToPosition.getX(), this.pathToPosition.getY(), this.pathToPosition.getZ(), this.speedModifier);
                }
            }
            return;
        }
        super.tick();
    }
}