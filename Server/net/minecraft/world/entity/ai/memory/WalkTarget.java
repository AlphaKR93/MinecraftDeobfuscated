/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.world.entity.ai.memory;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.behavior.PositionTracker;
import net.minecraft.world.phys.Vec3;

public class WalkTarget {
    private final PositionTracker target;
    private final float speedModifier;
    private final int closeEnoughDist;

    public WalkTarget(BlockPos $$0, float $$1, int $$2) {
        this(new BlockPosTracker($$0), $$1, $$2);
    }

    public WalkTarget(Vec3 $$0, float $$1, int $$2) {
        this(new BlockPosTracker(new BlockPos($$0)), $$1, $$2);
    }

    public WalkTarget(Entity $$0, float $$1, int $$2) {
        this(new EntityTracker($$0, false), $$1, $$2);
    }

    public WalkTarget(PositionTracker $$0, float $$1, int $$2) {
        this.target = $$0;
        this.speedModifier = $$1;
        this.closeEnoughDist = $$2;
    }

    public PositionTracker getTarget() {
        return this.target;
    }

    public float getSpeedModifier() {
        return this.speedModifier;
    }

    public int getCloseEnoughDist() {
        return this.closeEnoughDist;
    }
}