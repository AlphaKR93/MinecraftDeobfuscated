/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.phys;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class BlockHitResult
extends HitResult {
    private final Direction direction;
    private final BlockPos blockPos;
    private final boolean miss;
    private final boolean inside;

    public static BlockHitResult miss(Vec3 $$0, Direction $$1, BlockPos $$2) {
        return new BlockHitResult(true, $$0, $$1, $$2, false);
    }

    public BlockHitResult(Vec3 $$0, Direction $$1, BlockPos $$2, boolean $$3) {
        this(false, $$0, $$1, $$2, $$3);
    }

    private BlockHitResult(boolean $$0, Vec3 $$1, Direction $$2, BlockPos $$3, boolean $$4) {
        super($$1);
        this.miss = $$0;
        this.direction = $$2;
        this.blockPos = $$3;
        this.inside = $$4;
    }

    public BlockHitResult withDirection(Direction $$0) {
        return new BlockHitResult(this.miss, this.location, $$0, this.blockPos, this.inside);
    }

    public BlockHitResult withPosition(BlockPos $$0) {
        return new BlockHitResult(this.miss, this.location, this.direction, $$0, this.inside);
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public Direction getDirection() {
        return this.direction;
    }

    @Override
    public HitResult.Type getType() {
        return this.miss ? HitResult.Type.MISS : HitResult.Type.BLOCK;
    }

    public boolean isInside() {
        return this.inside;
    }
}