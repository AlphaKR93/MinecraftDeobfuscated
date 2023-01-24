/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Predicate
 *  net.minecraft.world.entity.Entity
 */
package net.minecraft.world.level;

import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ClipContext {
    private final Vec3 from;
    private final Vec3 to;
    private final Block block;
    private final Fluid fluid;
    private final CollisionContext collisionContext;

    public ClipContext(Vec3 $$0, Vec3 $$1, Block $$2, Fluid $$3, Entity $$4) {
        this.from = $$0;
        this.to = $$1;
        this.block = $$2;
        this.fluid = $$3;
        this.collisionContext = CollisionContext.of($$4);
    }

    public Vec3 getTo() {
        return this.to;
    }

    public Vec3 getFrom() {
        return this.from;
    }

    public VoxelShape getBlockShape(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        return this.block.get($$0, $$1, $$2, this.collisionContext);
    }

    public VoxelShape getFluidShape(FluidState $$0, BlockGetter $$1, BlockPos $$2) {
        return this.fluid.canPick($$0) ? $$0.getShape($$1, $$2) : Shapes.empty();
    }

    public static enum Block implements ShapeGetter
    {
        COLLIDER(BlockBehaviour.BlockStateBase::getCollisionShape),
        OUTLINE(BlockBehaviour.BlockStateBase::getShape),
        VISUAL(BlockBehaviour.BlockStateBase::getVisualShape),
        FALLDAMAGE_RESETTING(($$0, $$1, $$2, $$3) -> {
            if ($$0.is(BlockTags.FALL_DAMAGE_RESETTING)) {
                return Shapes.block();
            }
            return Shapes.empty();
        });

        private final ShapeGetter shapeGetter;

        private Block(ShapeGetter $$0) {
            this.shapeGetter = $$0;
        }

        @Override
        public VoxelShape get(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
            return this.shapeGetter.get($$0, $$1, $$2, $$3);
        }
    }

    public static enum Fluid {
        NONE((Predicate<FluidState>)((Predicate)$$0 -> false)),
        SOURCE_ONLY((Predicate<FluidState>)((Predicate)FluidState::isSource)),
        ANY((Predicate<FluidState>)((Predicate)$$0 -> !$$0.isEmpty())),
        WATER((Predicate<FluidState>)((Predicate)$$0 -> $$0.is(FluidTags.WATER)));

        private final Predicate<FluidState> canPick;

        private Fluid(Predicate<FluidState> $$0) {
            this.canPick = $$0;
        }

        public boolean canPick(FluidState $$0) {
            return this.canPick.test((Object)$$0);
        }
    }

    public static interface ShapeGetter {
        public VoxelShape get(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4);
    }
}