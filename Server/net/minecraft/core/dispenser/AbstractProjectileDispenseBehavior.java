/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  net.minecraft.world.item.ItemStack
 */
package net.minecraft.core.dispenser;

import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

public abstract class AbstractProjectileDispenseBehavior
extends DefaultDispenseItemBehavior {
    @Override
    public ItemStack execute(BlockSource $$0, ItemStack $$1) {
        ServerLevel $$2 = $$0.getLevel();
        Position $$3 = DispenserBlock.getDispensePosition($$0);
        Direction $$4 = $$0.getBlockState().getValue(DispenserBlock.FACING);
        Projectile $$5 = this.getProjectile($$2, $$3, $$1);
        $$5.shoot($$4.getStepX(), (float)$$4.getStepY() + 0.1f, $$4.getStepZ(), this.getPower(), this.getUncertainty());
        $$2.addFreshEntity($$5);
        $$1.shrink(1);
        return $$1;
    }

    @Override
    protected void playSound(BlockSource $$0) {
        $$0.getLevel().levelEvent(1002, $$0.getPos(), 0);
    }

    protected abstract Projectile getProjectile(Level var1, Position var2, ItemStack var3);

    protected float getUncertainty() {
        return 6.0f;
    }

    protected float getPower() {
        return 1.1f;
    }
}