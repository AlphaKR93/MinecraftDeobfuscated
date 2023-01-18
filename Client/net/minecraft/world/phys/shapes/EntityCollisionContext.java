/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Deprecated
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 */
package net.minecraft.world.phys.shapes;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EntityCollisionContext
implements CollisionContext {
    protected static final CollisionContext EMPTY = new EntityCollisionContext(false, -1.7976931348623157E308, ItemStack.EMPTY, $$0 -> false, null){

        @Override
        public boolean isAbove(VoxelShape $$0, BlockPos $$1, boolean $$2) {
            return $$2;
        }
    };
    private final boolean descending;
    private final double entityBottom;
    private final ItemStack heldItem;
    private final Predicate<FluidState> canStandOnFluid;
    @Nullable
    private final Entity entity;

    protected EntityCollisionContext(boolean $$0, double $$1, ItemStack $$2, Predicate<FluidState> $$3, @Nullable Entity $$4) {
        this.descending = $$0;
        this.entityBottom = $$1;
        this.heldItem = $$2;
        this.canStandOnFluid = $$3;
        this.entity = $$4;
    }

    @Deprecated
    protected EntityCollisionContext(Entity $$02) {
        this($$02.isDescending(), $$02.getY(), $$02 instanceof LivingEntity ? ((LivingEntity)$$02).getMainHandItem() : ItemStack.EMPTY, (Predicate<FluidState>)($$02 instanceof LivingEntity ? ((LivingEntity)$$02)::canStandOnFluid : $$0 -> false), $$02);
    }

    @Override
    public boolean isHoldingItem(Item $$0) {
        return this.heldItem.is($$0);
    }

    @Override
    public boolean canStandOnFluid(FluidState $$0, FluidState $$1) {
        return this.canStandOnFluid.test((Object)$$1) && !$$0.getType().isSame($$1.getType());
    }

    @Override
    public boolean isDescending() {
        return this.descending;
    }

    @Override
    public boolean isAbove(VoxelShape $$0, BlockPos $$1, boolean $$2) {
        return this.entityBottom > (double)$$1.getY() + $$0.max(Direction.Axis.Y) - (double)1.0E-5f;
    }

    @Nullable
    public Entity getEntity() {
        return this.entity;
    }
}