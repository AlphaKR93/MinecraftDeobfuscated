/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 *  net.minecraft.server.level.ServerPlayer
 */
package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ScaffoldingBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ScaffoldingBlockItem
extends BlockItem {
    public ScaffoldingBlockItem(Block $$0, Item.Properties $$1) {
        super($$0, $$1);
    }

    @Override
    @Nullable
    public BlockPlaceContext updatePlacementContext(BlockPlaceContext $$0) {
        Block $$4;
        BlockPos $$1 = $$0.getClickedPos();
        Level $$2 = $$0.getLevel();
        BlockState $$3 = $$2.getBlockState($$1);
        if ($$3.is($$4 = this.getBlock())) {
            Direction $$6;
            if ($$0.isSecondaryUseActive()) {
                Direction $$5 = $$0.isInside() ? $$0.getClickedFace().getOpposite() : $$0.getClickedFace();
            } else {
                $$6 = $$0.getClickedFace() == Direction.UP ? $$0.getHorizontalDirection() : Direction.UP;
            }
            int $$7 = 0;
            BlockPos.MutableBlockPos $$8 = $$1.mutable().move($$6);
            while ($$7 < 7) {
                if (!$$2.isClientSide && !$$2.isInWorldBounds($$8)) {
                    Player $$9 = $$0.getPlayer();
                    int $$10 = $$2.getMaxBuildHeight();
                    if (!($$9 instanceof ServerPlayer) || $$8.getY() < $$10) break;
                    ((ServerPlayer)$$9).sendSystemMessage((Component)Component.translatable("build.tooHigh", $$10 - 1).withStyle(ChatFormatting.RED), true);
                    break;
                }
                $$3 = $$2.getBlockState($$8);
                if (!$$3.is(this.getBlock())) {
                    if (!$$3.canBeReplaced($$0)) break;
                    return BlockPlaceContext.at($$0, $$8, $$6);
                }
                $$8.move($$6);
                if (!$$6.getAxis().isHorizontal()) continue;
                ++$$7;
            }
            return null;
        }
        if (ScaffoldingBlock.getDistance($$2, $$1) == 7) {
            return null;
        }
        return $$0;
    }

    @Override
    protected boolean mustSurvive() {
        return false;
    }
}