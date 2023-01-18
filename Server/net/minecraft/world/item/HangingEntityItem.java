/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Optional
 *  net.minecraft.world.item.ItemStack
 */
package net.minecraft.world.item;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.GlowItemFrame;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class HangingEntityItem
extends Item {
    private final EntityType<? extends HangingEntity> type;

    public HangingEntityItem(EntityType<? extends HangingEntity> $$0, Item.Properties $$1) {
        super($$1);
        this.type = $$0;
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public InteractionResult useOn(UseOnContext $$0) {
        void $$11;
        BlockPos $$1 = $$0.getClickedPos();
        Direction $$2 = $$0.getClickedFace();
        Vec3i $$3 = $$1.relative($$2);
        Player $$4 = $$0.getPlayer();
        ItemStack $$5 = $$0.getItemInHand();
        if ($$4 != null && !this.mayPlace($$4, $$2, $$5, (BlockPos)$$3)) {
            return InteractionResult.FAIL;
        }
        Level $$6 = $$0.getLevel();
        if (this.type == EntityType.PAINTING) {
            Optional<Painting> $$7 = Painting.create($$6, (BlockPos)$$3, $$2);
            if ($$7.isEmpty()) {
                return InteractionResult.CONSUME;
            }
            HangingEntity $$8 = (HangingEntity)$$7.get();
        } else if (this.type == EntityType.ITEM_FRAME) {
            ItemFrame $$9 = new ItemFrame($$6, (BlockPos)$$3, $$2);
        } else if (this.type == EntityType.GLOW_ITEM_FRAME) {
            GlowItemFrame $$10 = new GlowItemFrame($$6, (BlockPos)$$3, $$2);
        } else {
            return InteractionResult.sidedSuccess($$6.isClientSide);
        }
        CompoundTag $$12 = $$5.getTag();
        if ($$12 != null) {
            EntityType.updateCustomEntityTag($$6, $$4, (Entity)$$11, $$12);
        }
        if ($$11.survives()) {
            if (!$$6.isClientSide) {
                $$11.playPlacementSound();
                $$6.gameEvent($$4, GameEvent.ENTITY_PLACE, $$11.position());
                $$6.addFreshEntity((Entity)$$11);
            }
            $$5.shrink(1);
            return InteractionResult.sidedSuccess($$6.isClientSide);
        }
        return InteractionResult.CONSUME;
    }

    protected boolean mayPlace(Player $$0, Direction $$1, ItemStack $$2, BlockPos $$3) {
        return !$$1.getAxis().isVertical() && $$0.mayUseItemAt($$3, $$1, $$2);
    }
}