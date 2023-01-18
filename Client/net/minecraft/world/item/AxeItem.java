/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.BiMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Map
 *  java.util.Optional
 */
package net.minecraft.world.item;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class AxeItem
extends DiggerItem {
    protected static final Map<Block, Block> STRIPPABLES = new ImmutableMap.Builder().put((Object)Blocks.OAK_WOOD, (Object)Blocks.STRIPPED_OAK_WOOD).put((Object)Blocks.OAK_LOG, (Object)Blocks.STRIPPED_OAK_LOG).put((Object)Blocks.DARK_OAK_WOOD, (Object)Blocks.STRIPPED_DARK_OAK_WOOD).put((Object)Blocks.DARK_OAK_LOG, (Object)Blocks.STRIPPED_DARK_OAK_LOG).put((Object)Blocks.ACACIA_WOOD, (Object)Blocks.STRIPPED_ACACIA_WOOD).put((Object)Blocks.ACACIA_LOG, (Object)Blocks.STRIPPED_ACACIA_LOG).put((Object)Blocks.BIRCH_WOOD, (Object)Blocks.STRIPPED_BIRCH_WOOD).put((Object)Blocks.BIRCH_LOG, (Object)Blocks.STRIPPED_BIRCH_LOG).put((Object)Blocks.JUNGLE_WOOD, (Object)Blocks.STRIPPED_JUNGLE_WOOD).put((Object)Blocks.JUNGLE_LOG, (Object)Blocks.STRIPPED_JUNGLE_LOG).put((Object)Blocks.SPRUCE_WOOD, (Object)Blocks.STRIPPED_SPRUCE_WOOD).put((Object)Blocks.SPRUCE_LOG, (Object)Blocks.STRIPPED_SPRUCE_LOG).put((Object)Blocks.WARPED_STEM, (Object)Blocks.STRIPPED_WARPED_STEM).put((Object)Blocks.WARPED_HYPHAE, (Object)Blocks.STRIPPED_WARPED_HYPHAE).put((Object)Blocks.CRIMSON_STEM, (Object)Blocks.STRIPPED_CRIMSON_STEM).put((Object)Blocks.CRIMSON_HYPHAE, (Object)Blocks.STRIPPED_CRIMSON_HYPHAE).put((Object)Blocks.MANGROVE_WOOD, (Object)Blocks.STRIPPED_MANGROVE_WOOD).put((Object)Blocks.MANGROVE_LOG, (Object)Blocks.STRIPPED_MANGROVE_LOG).put((Object)Blocks.BAMBOO_BLOCK, (Object)Blocks.STRIPPED_BAMBOO_BLOCK).build();

    protected AxeItem(Tier $$0, float $$1, float $$2, Item.Properties $$3) {
        super($$1, $$2, $$0, BlockTags.MINEABLE_WITH_AXE, $$3);
    }

    @Override
    public InteractionResult useOn(UseOnContext $$0) {
        Level $$12 = $$0.getLevel();
        BlockPos $$2 = $$0.getClickedPos();
        Player $$3 = $$0.getPlayer();
        BlockState $$4 = $$12.getBlockState($$2);
        Optional $$5 = this.getStripped($$4);
        Optional<BlockState> $$6 = WeatheringCopper.getPrevious($$4);
        Optional $$7 = Optional.ofNullable((Object)((Block)((BiMap)HoneycombItem.WAX_OFF_BY_BLOCK.get()).get((Object)$$4.getBlock()))).map($$1 -> $$1.withPropertiesOf($$4));
        ItemStack $$8 = $$0.getItemInHand();
        Optional $$9 = Optional.empty();
        if ($$5.isPresent()) {
            $$12.playSound($$3, $$2, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0f, 1.0f);
            $$9 = $$5;
        } else if ($$6.isPresent()) {
            $$12.playSound($$3, $$2, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0f, 1.0f);
            $$12.levelEvent($$3, 3005, $$2, 0);
            $$9 = $$6;
        } else if ($$7.isPresent()) {
            $$12.playSound($$3, $$2, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0f, 1.0f);
            $$12.levelEvent($$3, 3004, $$2, 0);
            $$9 = $$7;
        }
        if ($$9.isPresent()) {
            if ($$3 instanceof ServerPlayer) {
                CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)$$3, $$2, $$8);
            }
            $$12.setBlock($$2, (BlockState)$$9.get(), 11);
            $$12.gameEvent(GameEvent.BLOCK_CHANGE, $$2, GameEvent.Context.of($$3, (BlockState)$$9.get()));
            if ($$3 != null) {
                $$8.hurtAndBreak(1, $$3, $$1 -> $$1.broadcastBreakEvent($$0.getHand()));
            }
            return InteractionResult.sidedSuccess($$12.isClientSide);
        }
        return InteractionResult.PASS;
    }

    private Optional<BlockState> getStripped(BlockState $$0) {
        return Optional.ofNullable((Object)((Block)STRIPPABLES.get((Object)$$0.getBlock()))).map($$1 -> (BlockState)$$1.defaultBlockState().setValue(RotatedPillarBlock.AXIS, $$0.getValue(RotatedPillarBlock.AXIS)));
    }
}