/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.util.List
 *  java.util.Optional
 *  java.util.Spliterator
 *  java.util.stream.StreamSupport
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level;

import com.google.common.collect.Iterables;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockCollisions;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface CollisionGetter
extends BlockGetter {
    public WorldBorder getWorldBorder();

    @Nullable
    public BlockGetter getChunkForCollisions(int var1, int var2);

    default public boolean isUnobstructed(@Nullable Entity $$0, VoxelShape $$1) {
        return true;
    }

    default public boolean isUnobstructed(BlockState $$0, BlockPos $$1, CollisionContext $$2) {
        VoxelShape $$3 = $$0.getCollisionShape(this, $$1, $$2);
        return $$3.isEmpty() || this.isUnobstructed(null, $$3.move($$1.getX(), $$1.getY(), $$1.getZ()));
    }

    default public boolean isUnobstructed(Entity $$0) {
        return this.isUnobstructed($$0, Shapes.create($$0.getBoundingBox()));
    }

    default public boolean noCollision(AABB $$0) {
        return this.noCollision(null, $$0);
    }

    default public boolean noCollision(Entity $$0) {
        return this.noCollision($$0, $$0.getBoundingBox());
    }

    default public boolean noCollision(@Nullable Entity $$0, AABB $$1) {
        for (VoxelShape $$2 : this.getBlockCollisions($$0, $$1)) {
            if ($$2.isEmpty()) continue;
            return false;
        }
        if (!this.getEntityCollisions($$0, $$1).isEmpty()) {
            return false;
        }
        if ($$0 != null) {
            VoxelShape $$3 = this.borderCollision($$0, $$1);
            return $$3 == null || !Shapes.joinIsNotEmpty($$3, Shapes.create($$1), BooleanOp.AND);
        }
        return true;
    }

    public List<VoxelShape> getEntityCollisions(@Nullable Entity var1, AABB var2);

    default public Iterable<VoxelShape> getCollisions(@Nullable Entity $$0, AABB $$1) {
        List<VoxelShape> $$2 = this.getEntityCollisions($$0, $$1);
        Iterable $$3 = this.getBlockCollisions($$0, $$1);
        return $$2.isEmpty() ? $$3 : Iterables.concat($$2, $$3);
    }

    default public Iterable<VoxelShape> getBlockCollisions(@Nullable Entity $$0, AABB $$1) {
        return () -> new BlockCollisions(this, $$0, $$1);
    }

    @Nullable
    private VoxelShape borderCollision(Entity $$0, AABB $$1) {
        WorldBorder $$2 = this.getWorldBorder();
        return $$2.isInsideCloseToBorder($$0, $$1) ? $$2.getCollisionShape() : null;
    }

    default public boolean collidesWithSuffocatingBlock(@Nullable Entity $$0, AABB $$1) {
        BlockCollisions $$2 = new BlockCollisions(this, $$0, $$1, true);
        while ($$2.hasNext()) {
            if (((VoxelShape)$$2.next()).isEmpty()) continue;
            return true;
        }
        return false;
    }

    default public Optional<Vec3> findFreePosition(@Nullable Entity $$02, VoxelShape $$1, Vec3 $$2, double $$32, double $$4, double $$5) {
        if ($$1.isEmpty()) {
            return Optional.empty();
        }
        AABB $$6 = $$1.bounds().inflate($$32, $$4, $$5);
        VoxelShape $$7 = (VoxelShape)StreamSupport.stream((Spliterator)this.getBlockCollisions($$02, $$6).spliterator(), (boolean)false).filter($$0 -> this.getWorldBorder() == null || this.getWorldBorder().isWithinBounds($$0.bounds())).flatMap($$0 -> $$0.toAabbs().stream()).map($$3 -> $$3.inflate($$32 / 2.0, $$4 / 2.0, $$5 / 2.0)).map(Shapes::create).reduce((Object)Shapes.empty(), Shapes::or);
        VoxelShape $$8 = Shapes.join($$1, $$7, BooleanOp.ONLY_FIRST);
        return $$8.closestPointTo($$2);
    }
}