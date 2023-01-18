/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.gameevent.GameEvent;

public class MinecartItem
extends Item {
    private static final DispenseItemBehavior DISPENSE_ITEM_BEHAVIOR = new DefaultDispenseItemBehavior(){
        private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

        /*
         * WARNING - void declaration
         */
        @Override
        public ItemStack execute(BlockSource $$0, ItemStack $$1) {
            void $$16;
            RailShape $$9;
            Direction $$2 = $$0.getBlockState().getValue(DispenserBlock.FACING);
            ServerLevel $$3 = $$0.getLevel();
            double $$4 = $$0.x() + (double)$$2.getStepX() * 1.125;
            double $$5 = Math.floor((double)$$0.y()) + (double)$$2.getStepY();
            double $$6 = $$0.z() + (double)$$2.getStepZ() * 1.125;
            Vec3i $$7 = $$0.getPos().relative($$2);
            BlockState $$8 = $$3.getBlockState((BlockPos)$$7);
            RailShape railShape = $$9 = $$8.getBlock() instanceof BaseRailBlock ? $$8.getValue(((BaseRailBlock)$$8.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
            if ($$8.is(BlockTags.RAILS)) {
                if ($$9.isAscending()) {
                    double $$10 = 0.6;
                } else {
                    double $$11 = 0.1;
                }
            } else if ($$8.isAir() && $$3.getBlockState((BlockPos)((BlockPos)$$7).below()).is(BlockTags.RAILS)) {
                RailShape $$13;
                BlockState $$12 = $$3.getBlockState((BlockPos)((BlockPos)$$7).below());
                RailShape railShape2 = $$13 = $$12.getBlock() instanceof BaseRailBlock ? $$12.getValue(((BaseRailBlock)$$12.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
                if ($$2 == Direction.DOWN || !$$13.isAscending()) {
                    double $$14 = -0.9;
                } else {
                    double $$15 = -0.4;
                }
            } else {
                return this.defaultDispenseItemBehavior.dispense($$0, $$1);
            }
            AbstractMinecart $$17 = AbstractMinecart.createMinecart($$3, $$4, $$5 + $$16, $$6, ((MinecartItem)$$1.getItem()).type);
            if ($$1.hasCustomHoverName()) {
                $$17.setCustomName($$1.getHoverName());
            }
            $$3.addFreshEntity($$17);
            $$1.shrink(1);
            return $$1;
        }

        @Override
        protected void playSound(BlockSource $$0) {
            $$0.getLevel().levelEvent(1000, $$0.getPos(), 0);
        }
    };
    final AbstractMinecart.Type type;

    public MinecartItem(AbstractMinecart.Type $$0, Item.Properties $$1) {
        super($$1);
        this.type = $$0;
        DispenserBlock.registerBehavior(this, DISPENSE_ITEM_BEHAVIOR);
    }

    @Override
    public InteractionResult useOn(UseOnContext $$0) {
        BlockPos $$2;
        Level $$1 = $$0.getLevel();
        BlockState $$3 = $$1.getBlockState($$2 = $$0.getClickedPos());
        if (!$$3.is(BlockTags.RAILS)) {
            return InteractionResult.FAIL;
        }
        ItemStack $$4 = $$0.getItemInHand();
        if (!$$1.isClientSide) {
            RailShape $$5 = $$3.getBlock() instanceof BaseRailBlock ? $$3.getValue(((BaseRailBlock)$$3.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
            double $$6 = 0.0;
            if ($$5.isAscending()) {
                $$6 = 0.5;
            }
            AbstractMinecart $$7 = AbstractMinecart.createMinecart($$1, (double)$$2.getX() + 0.5, (double)$$2.getY() + 0.0625 + $$6, (double)$$2.getZ() + 0.5, this.type);
            if ($$4.hasCustomHoverName()) {
                $$7.setCustomName($$4.getHoverName());
            }
            $$1.addFreshEntity($$7);
            $$1.gameEvent(GameEvent.ENTITY_PLACE, $$2, GameEvent.Context.of($$0.getPlayer(), $$1.getBlockState((BlockPos)$$2.below())));
        }
        $$4.shrink(1);
        return InteractionResult.sidedSuccess($$1.isClientSide);
    }
}