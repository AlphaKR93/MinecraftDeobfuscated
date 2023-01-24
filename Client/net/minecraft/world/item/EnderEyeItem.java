/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  net.minecraft.server.level.ServerPlayer
 */
package net.minecraft.world.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.StructureTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.EyeOfEnder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EndPortalFrameBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class EnderEyeItem
extends Item {
    public EnderEyeItem(Item.Properties $$0) {
        super($$0);
    }

    @Override
    public InteractionResult useOn(UseOnContext $$0) {
        BlockPos $$2;
        Level $$1 = $$0.getLevel();
        BlockState $$3 = $$1.getBlockState($$2 = $$0.getClickedPos());
        if (!$$3.is(Blocks.END_PORTAL_FRAME) || $$3.getValue(EndPortalFrameBlock.HAS_EYE).booleanValue()) {
            return InteractionResult.PASS;
        }
        if ($$1.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        BlockState $$4 = (BlockState)$$3.setValue(EndPortalFrameBlock.HAS_EYE, true);
        Block.pushEntitiesUp($$3, $$4, $$1, $$2);
        $$1.setBlock($$2, $$4, 2);
        $$1.updateNeighbourForOutputSignal($$2, Blocks.END_PORTAL_FRAME);
        $$0.getItemInHand().shrink(1);
        $$1.levelEvent(1503, $$2, 0);
        BlockPattern.BlockPatternMatch $$5 = EndPortalFrameBlock.getOrCreatePortalShape().find($$1, $$2);
        if ($$5 != null) {
            BlockPos $$6 = $$5.getFrontTopLeft().offset(-3, 0, -3);
            for (int $$7 = 0; $$7 < 3; ++$$7) {
                for (int $$8 = 0; $$8 < 3; ++$$8) {
                    $$1.setBlock($$6.offset($$7, 0, $$8), Blocks.END_PORTAL.defaultBlockState(), 2);
                }
            }
            $$1.globalLevelEvent(1038, $$6.offset(1, 0, 1), 0);
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level $$0, Player $$1, InteractionHand $$2) {
        ServerLevel $$5;
        BlockPos $$6;
        ItemStack $$3 = $$1.getItemInHand($$2);
        BlockHitResult $$4 = EnderEyeItem.getPlayerPOVHitResult($$0, $$1, ClipContext.Fluid.NONE);
        if (((HitResult)$$4).getType() == HitResult.Type.BLOCK && $$0.getBlockState($$4.getBlockPos()).is(Blocks.END_PORTAL_FRAME)) {
            return InteractionResultHolder.pass($$3);
        }
        $$1.startUsingItem($$2);
        if ($$0 instanceof ServerLevel && ($$6 = ($$5 = (ServerLevel)$$0).findNearestMapStructure(StructureTags.EYE_OF_ENDER_LOCATED, $$1.blockPosition(), 100, false)) != null) {
            EyeOfEnder $$7 = new EyeOfEnder($$0, $$1.getX(), $$1.getY(0.5), $$1.getZ());
            $$7.setItem($$3);
            $$7.signalTo($$6);
            $$0.gameEvent(GameEvent.PROJECTILE_SHOOT, $$7.position(), GameEvent.Context.of($$1));
            $$0.addFreshEntity($$7);
            if ($$1 instanceof ServerPlayer) {
                CriteriaTriggers.USED_ENDER_EYE.trigger((ServerPlayer)$$1, $$6);
            }
            $$0.playSound(null, $$1.getX(), $$1.getY(), $$1.getZ(), SoundEvents.ENDER_EYE_LAUNCH, SoundSource.NEUTRAL, 0.5f, 0.4f / ($$0.getRandom().nextFloat() * 0.4f + 0.8f));
            $$0.levelEvent(null, 1003, $$1.blockPosition(), 0);
            if (!$$1.getAbilities().instabuild) {
                $$3.shrink(1);
            }
            $$1.awardStat(Stats.ITEM_USED.get(this));
            $$1.swing($$2, true);
            return InteractionResultHolder.success($$3);
        }
        return InteractionResultHolder.consume($$3);
    }
}