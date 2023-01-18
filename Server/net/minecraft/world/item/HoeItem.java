/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.util.Pair
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Map
 *  java.util.function.Consumer
 *  java.util.function.Predicate
 */
package net.minecraft.world.item;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class HoeItem
extends DiggerItem {
    protected static final Map<Block, Pair<Predicate<UseOnContext>, Consumer<UseOnContext>>> TILLABLES = Maps.newHashMap((Map)ImmutableMap.of((Object)Blocks.GRASS_BLOCK, (Object)Pair.of(HoeItem::onlyIfAirAbove, HoeItem.changeIntoState(Blocks.FARMLAND.defaultBlockState())), (Object)Blocks.DIRT_PATH, (Object)Pair.of(HoeItem::onlyIfAirAbove, HoeItem.changeIntoState(Blocks.FARMLAND.defaultBlockState())), (Object)Blocks.DIRT, (Object)Pair.of(HoeItem::onlyIfAirAbove, HoeItem.changeIntoState(Blocks.FARMLAND.defaultBlockState())), (Object)Blocks.COARSE_DIRT, (Object)Pair.of(HoeItem::onlyIfAirAbove, HoeItem.changeIntoState(Blocks.DIRT.defaultBlockState())), (Object)Blocks.ROOTED_DIRT, (Object)Pair.of($$0 -> true, HoeItem.changeIntoStateAndDropItem(Blocks.DIRT.defaultBlockState(), Items.HANGING_ROOTS))));

    protected HoeItem(Tier $$0, int $$1, float $$2, Item.Properties $$3) {
        super($$1, $$2, $$0, BlockTags.MINEABLE_WITH_HOE, $$3);
    }

    @Override
    public InteractionResult useOn(UseOnContext $$0) {
        BlockPos $$2;
        Level $$12 = $$0.getLevel();
        Pair $$3 = (Pair)TILLABLES.get((Object)$$12.getBlockState($$2 = $$0.getClickedPos()).getBlock());
        if ($$3 == null) {
            return InteractionResult.PASS;
        }
        Predicate $$4 = (Predicate)$$3.getFirst();
        Consumer $$5 = (Consumer)$$3.getSecond();
        if ($$4.test((Object)$$0)) {
            Player $$6 = $$0.getPlayer();
            $$12.playSound($$6, $$2, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0f, 1.0f);
            if (!$$12.isClientSide) {
                $$5.accept((Object)$$0);
                if ($$6 != null) {
                    $$0.getItemInHand().hurtAndBreak(1, $$6, $$1 -> $$1.broadcastBreakEvent($$0.getHand()));
                }
            }
            return InteractionResult.sidedSuccess($$12.isClientSide);
        }
        return InteractionResult.PASS;
    }

    public static Consumer<UseOnContext> changeIntoState(BlockState $$0) {
        return $$1 -> {
            $$1.getLevel().setBlock($$1.getClickedPos(), $$0, 11);
            $$1.getLevel().gameEvent(GameEvent.BLOCK_CHANGE, $$1.getClickedPos(), GameEvent.Context.of($$1.getPlayer(), $$0));
        };
    }

    public static Consumer<UseOnContext> changeIntoStateAndDropItem(BlockState $$0, ItemLike $$1) {
        return $$2 -> {
            $$2.getLevel().setBlock($$2.getClickedPos(), $$0, 11);
            $$2.getLevel().gameEvent(GameEvent.BLOCK_CHANGE, $$2.getClickedPos(), GameEvent.Context.of($$2.getPlayer(), $$0));
            Block.popResourceFromFace($$2.getLevel(), $$2.getClickedPos(), $$2.getClickedFace(), new ItemStack($$1));
        };
    }

    public static boolean onlyIfAirAbove(UseOnContext $$0) {
        return $$0.getClickedFace() != Direction.DOWN && $$0.getLevel().getBlockState((BlockPos)$$0.getClickedPos().above()).isAir();
    }
}