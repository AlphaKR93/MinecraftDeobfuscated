/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.core.dispenser;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;

public class BoatDispenseItemBehavior
extends DefaultDispenseItemBehavior {
    private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();
    private final Boat.Type type;
    private final boolean isChestBoat;

    public BoatDispenseItemBehavior(Boat.Type $$0) {
        this($$0, false);
    }

    public BoatDispenseItemBehavior(Boat.Type $$0, boolean $$1) {
        this.type = $$0;
        this.isChestBoat = $$1;
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public ItemStack execute(BlockSource $$0, ItemStack $$1) {
        void $$10;
        Direction $$2 = $$0.getBlockState().getValue(DispenserBlock.FACING);
        ServerLevel $$3 = $$0.getLevel();
        double $$4 = $$0.x() + (double)((float)$$2.getStepX() * 1.125f);
        double $$5 = $$0.y() + (double)((float)$$2.getStepY() * 1.125f);
        double $$6 = $$0.z() + (double)((float)$$2.getStepZ() * 1.125f);
        Vec3i $$7 = $$0.getPos().relative($$2);
        if ($$3.getFluidState((BlockPos)$$7).is(FluidTags.WATER)) {
            double $$8 = 1.0;
        } else if ($$3.getBlockState((BlockPos)$$7).isAir() && $$3.getFluidState((BlockPos)((BlockPos)$$7).below()).is(FluidTags.WATER)) {
            double $$9 = 0.0;
        } else {
            return this.defaultDispenseItemBehavior.dispense($$0, $$1);
        }
        Boat $$11 = this.isChestBoat ? new ChestBoat($$3, $$4, $$5 + $$10, $$6) : new Boat($$3, $$4, $$5 + $$10, $$6);
        $$11.setVariant(this.type);
        $$11.setYRot($$2.toYRot());
        $$3.addFreshEntity($$11);
        $$1.shrink(1);
        return $$1;
    }

    @Override
    protected void playSound(BlockSource $$0) {
        $$0.getLevel().levelEvent(1000, $$0.getPos(), 0);
    }
}