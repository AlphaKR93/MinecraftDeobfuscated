/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 */
package net.minecraft.world.item;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;

public class EndCrystalItem
extends Item {
    public EndCrystalItem(Item.Properties $$0) {
        super($$0);
    }

    @Override
    public InteractionResult useOn(UseOnContext $$0) {
        double $$7;
        double $$6;
        BlockPos $$2;
        Level $$1 = $$0.getLevel();
        BlockState $$3 = $$1.getBlockState($$2 = $$0.getClickedPos());
        if (!$$3.is(Blocks.OBSIDIAN) && !$$3.is(Blocks.BEDROCK)) {
            return InteractionResult.FAIL;
        }
        Vec3i $$4 = $$2.above();
        if (!$$1.isEmptyBlock((BlockPos)$$4)) {
            return InteractionResult.FAIL;
        }
        double $$5 = $$4.getX();
        List $$8 = $$1.getEntities(null, new AABB($$5, $$6 = (double)$$4.getY(), $$7 = (double)$$4.getZ(), $$5 + 1.0, $$6 + 2.0, $$7 + 1.0));
        if (!$$8.isEmpty()) {
            return InteractionResult.FAIL;
        }
        if ($$1 instanceof ServerLevel) {
            EndCrystal $$9 = new EndCrystal($$1, $$5 + 0.5, $$6, $$7 + 0.5);
            $$9.setShowBottom(false);
            $$1.addFreshEntity($$9);
            $$1.gameEvent($$0.getPlayer(), GameEvent.ENTITY_PLACE, (BlockPos)$$4);
            EndDragonFight $$10 = ((ServerLevel)$$1).dragonFight();
            if ($$10 != null) {
                $$10.tryRespawn();
            }
        }
        $$0.getItemInHand().shrink(1);
        return InteractionResult.sidedSuccess($$1.isClientSide);
    }

    @Override
    public boolean isFoil(ItemStack $$0) {
        return true;
    }
}