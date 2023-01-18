/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSourceImpl;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.DropperBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class DropperBlock
extends DispenserBlock {
    private static final DispenseItemBehavior DISPENSE_BEHAVIOUR = new DefaultDispenseItemBehavior();

    public DropperBlock(BlockBehaviour.Properties $$0) {
        super($$0);
    }

    @Override
    protected DispenseItemBehavior getDispenseMethod(ItemStack $$0) {
        return DISPENSE_BEHAVIOUR;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos $$0, BlockState $$1) {
        return new DropperBlockEntity($$0, $$1);
    }

    @Override
    protected void dispenseFrom(ServerLevel $$0, BlockPos $$1) {
        ItemStack $$9;
        BlockSourceImpl $$2 = new BlockSourceImpl($$0, $$1);
        DispenserBlockEntity $$3 = (DispenserBlockEntity)$$2.getEntity();
        int $$4 = $$3.getRandomSlot($$0.random);
        if ($$4 < 0) {
            $$0.levelEvent(1001, $$1, 0);
            return;
        }
        ItemStack $$5 = $$3.getItem($$4);
        if ($$5.isEmpty()) {
            return;
        }
        Direction $$6 = $$0.getBlockState($$1).getValue(FACING);
        Container $$7 = HopperBlockEntity.getContainerAt($$0, (BlockPos)$$1.relative($$6));
        if ($$7 == null) {
            ItemStack $$8 = DISPENSE_BEHAVIOUR.dispense($$2, $$5);
        } else {
            $$9 = HopperBlockEntity.addItem($$3, $$7, $$5.copy().split(1), $$6.getOpposite());
            if ($$9.isEmpty()) {
                $$9 = $$5.copy();
                $$9.shrink(1);
            } else {
                $$9 = $$5.copy();
            }
        }
        $$3.setItem($$4, $$9);
    }
}