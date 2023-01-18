/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Maps
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Map
 */
package net.minecraft.world.item;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class ShovelItem
extends DiggerItem {
    protected static final Map<Block, BlockState> FLATTENABLES = Maps.newHashMap((Map)new ImmutableMap.Builder().put((Object)Blocks.GRASS_BLOCK, (Object)Blocks.DIRT_PATH.defaultBlockState()).put((Object)Blocks.DIRT, (Object)Blocks.DIRT_PATH.defaultBlockState()).put((Object)Blocks.PODZOL, (Object)Blocks.DIRT_PATH.defaultBlockState()).put((Object)Blocks.COARSE_DIRT, (Object)Blocks.DIRT_PATH.defaultBlockState()).put((Object)Blocks.MYCELIUM, (Object)Blocks.DIRT_PATH.defaultBlockState()).put((Object)Blocks.ROOTED_DIRT, (Object)Blocks.DIRT_PATH.defaultBlockState()).build());

    public ShovelItem(Tier $$0, float $$1, float $$2, Item.Properties $$3) {
        super($$1, $$2, $$0, BlockTags.MINEABLE_WITH_SHOVEL, $$3);
    }

    @Override
    public InteractionResult useOn(UseOnContext $$0) {
        Level $$12 = $$0.getLevel();
        BlockPos $$2 = $$0.getClickedPos();
        BlockState $$3 = $$12.getBlockState($$2);
        if ($$0.getClickedFace() != Direction.DOWN) {
            Player $$4 = $$0.getPlayer();
            BlockState $$5 = (BlockState)FLATTENABLES.get((Object)$$3.getBlock());
            BlockState $$6 = null;
            if ($$5 != null && $$12.getBlockState((BlockPos)$$2.above()).isAir()) {
                $$12.playSound($$4, $$2, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1.0f, 1.0f);
                $$6 = $$5;
            } else if ($$3.getBlock() instanceof CampfireBlock && $$3.getValue(CampfireBlock.LIT).booleanValue()) {
                if (!$$12.isClientSide()) {
                    $$12.levelEvent(null, 1009, $$2, 0);
                }
                CampfireBlock.dowse($$0.getPlayer(), $$12, $$2, $$3);
                $$6 = (BlockState)$$3.setValue(CampfireBlock.LIT, false);
            }
            if ($$6 != null) {
                if (!$$12.isClientSide) {
                    $$12.setBlock($$2, $$6, 11);
                    $$12.gameEvent(GameEvent.BLOCK_CHANGE, $$2, GameEvent.Context.of($$4, $$6));
                    if ($$4 != null) {
                        $$0.getItemInHand().hurtAndBreak(1, $$4, $$1 -> $$1.broadcastBreakEvent($$0.getHand()));
                    }
                }
                return InteractionResult.sidedSuccess($$12.isClientSide);
            }
            return InteractionResult.PASS;
        }
        return InteractionResult.PASS;
    }
}