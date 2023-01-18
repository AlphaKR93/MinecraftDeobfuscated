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
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;

public class LeadItem
extends Item {
    public LeadItem(Item.Properties $$0) {
        super($$0);
    }

    @Override
    public InteractionResult useOn(UseOnContext $$0) {
        BlockPos $$2;
        Level $$1 = $$0.getLevel();
        BlockState $$3 = $$1.getBlockState($$2 = $$0.getClickedPos());
        if ($$3.is(BlockTags.FENCES)) {
            Player $$4 = $$0.getPlayer();
            if (!$$1.isClientSide && $$4 != null) {
                LeadItem.bindPlayerMobs($$4, $$1, $$2);
            }
            $$1.gameEvent(GameEvent.BLOCK_ATTACH, $$2, GameEvent.Context.of($$4));
            return InteractionResult.sidedSuccess($$1.isClientSide);
        }
        return InteractionResult.PASS;
    }

    public static InteractionResult bindPlayerMobs(Player $$0, Level $$1, BlockPos $$2) {
        LeashFenceKnotEntity $$3 = null;
        boolean $$4 = false;
        double $$5 = 7.0;
        int $$6 = $$2.getX();
        int $$7 = $$2.getY();
        int $$8 = $$2.getZ();
        List $$9 = $$1.getEntitiesOfClass(Mob.class, new AABB((double)$$6 - 7.0, (double)$$7 - 7.0, (double)$$8 - 7.0, (double)$$6 + 7.0, (double)$$7 + 7.0, (double)$$8 + 7.0));
        for (Mob $$10 : $$9) {
            if ($$10.getLeashHolder() != $$0) continue;
            if ($$3 == null) {
                $$3 = LeashFenceKnotEntity.getOrCreateKnot($$1, $$2);
                $$3.playPlacementSound();
            }
            $$10.setLeashedTo($$3, true);
            $$4 = true;
        }
        return $$4 ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }
}