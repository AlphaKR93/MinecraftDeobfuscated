/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.core.dispenser;

import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

public class DefaultDispenseItemBehavior
implements DispenseItemBehavior {
    @Override
    public final ItemStack dispense(BlockSource $$0, ItemStack $$1) {
        ItemStack $$2 = this.execute($$0, $$1);
        this.playSound($$0);
        this.playAnimation($$0, $$0.getBlockState().getValue(DispenserBlock.FACING));
        return $$2;
    }

    protected ItemStack execute(BlockSource $$0, ItemStack $$1) {
        Direction $$2 = $$0.getBlockState().getValue(DispenserBlock.FACING);
        Position $$3 = DispenserBlock.getDispensePosition($$0);
        ItemStack $$4 = $$1.split(1);
        DefaultDispenseItemBehavior.spawnItem($$0.getLevel(), $$4, 6, $$2, $$3);
        return $$1;
    }

    public static void spawnItem(Level $$0, ItemStack $$1, int $$2, Direction $$3, Position $$4) {
        double $$5 = $$4.x();
        double $$6 = $$4.y();
        double $$7 = $$4.z();
        $$6 = $$3.getAxis() == Direction.Axis.Y ? ($$6 -= 0.125) : ($$6 -= 0.15625);
        ItemEntity $$8 = new ItemEntity($$0, $$5, $$6, $$7, $$1);
        double $$9 = $$0.random.nextDouble() * 0.1 + 0.2;
        $$8.setDeltaMovement($$0.random.triangle((double)$$3.getStepX() * $$9, 0.0172275 * (double)$$2), $$0.random.triangle(0.2, 0.0172275 * (double)$$2), $$0.random.triangle((double)$$3.getStepZ() * $$9, 0.0172275 * (double)$$2));
        $$0.addFreshEntity($$8);
    }

    protected void playSound(BlockSource $$0) {
        $$0.getLevel().levelEvent(1000, $$0.getPos(), 0);
    }

    protected void playAnimation(BlockSource $$0, Direction $$1) {
        $$0.getLevel().levelEvent(2000, $$0.getPos(), $$1.get3DDataValue());
    }
}