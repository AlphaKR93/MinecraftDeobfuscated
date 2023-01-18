/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class BucketItem
extends Item
implements DispensibleContainerItem {
    private final Fluid content;

    public BucketItem(Fluid $$0, Item.Properties $$1) {
        super($$1);
        this.content = $$0;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level $$0, Player $$12, InteractionHand $$2) {
        ItemStack $$3 = $$12.getItemInHand($$2);
        BlockHitResult $$4 = BucketItem.getPlayerPOVHitResult($$0, $$12, this.content == Fluids.EMPTY ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.NONE);
        if ($$4.getType() == HitResult.Type.MISS) {
            return InteractionResultHolder.pass($$3);
        }
        if ($$4.getType() == HitResult.Type.BLOCK) {
            Vec3i $$13;
            BlockPos $$5 = $$4.getBlockPos();
            Direction $$6 = $$4.getDirection();
            Vec3i $$7 = $$5.relative($$6);
            if (!$$0.mayInteract($$12, $$5) || !$$12.mayUseItemAt((BlockPos)$$7, $$6, $$3)) {
                return InteractionResultHolder.fail($$3);
            }
            if (this.content == Fluids.EMPTY) {
                BucketPickup $$9;
                ItemStack $$10;
                BlockState $$8 = $$0.getBlockState($$5);
                if ($$8.getBlock() instanceof BucketPickup && !($$10 = ($$9 = (BucketPickup)((Object)$$8.getBlock())).pickupBlock($$0, $$5, $$8)).isEmpty()) {
                    $$12.awardStat(Stats.ITEM_USED.get(this));
                    $$9.getPickupSound().ifPresent($$1 -> $$12.playSound((SoundEvent)$$1, 1.0f, 1.0f));
                    $$0.gameEvent($$12, GameEvent.FLUID_PICKUP, $$5);
                    ItemStack $$11 = ItemUtils.createFilledResult($$3, $$12, $$10);
                    if (!$$0.isClientSide) {
                        CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer)$$12, $$10);
                    }
                    return InteractionResultHolder.sidedSuccess($$11, $$0.isClientSide());
                }
                return InteractionResultHolder.fail($$3);
            }
            BlockState $$122 = $$0.getBlockState($$5);
            Vec3i vec3i = $$13 = $$122.getBlock() instanceof LiquidBlockContainer && this.content == Fluids.WATER ? $$5 : $$7;
            if (this.emptyContents($$12, $$0, (BlockPos)$$13, $$4)) {
                this.checkExtraContent($$12, $$0, $$3, (BlockPos)$$13);
                if ($$12 instanceof ServerPlayer) {
                    CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)$$12, (BlockPos)$$13, $$3);
                }
                $$12.awardStat(Stats.ITEM_USED.get(this));
                return InteractionResultHolder.sidedSuccess(BucketItem.getEmptySuccessItem($$3, $$12), $$0.isClientSide());
            }
            return InteractionResultHolder.fail($$3);
        }
        return InteractionResultHolder.pass($$3);
    }

    public static ItemStack getEmptySuccessItem(ItemStack $$0, Player $$1) {
        if (!$$1.getAbilities().instabuild) {
            return new ItemStack(Items.BUCKET);
        }
        return $$0;
    }

    @Override
    public void checkExtraContent(@Nullable Player $$0, Level $$1, ItemStack $$2, BlockPos $$3) {
    }

    @Override
    public boolean emptyContents(@Nullable Player $$0, Level $$1, BlockPos $$2, @Nullable BlockHitResult $$3) {
        boolean $$8;
        if (!(this.content instanceof FlowingFluid)) {
            return false;
        }
        BlockState $$4 = $$1.getBlockState($$2);
        Block $$5 = $$4.getBlock();
        Material $$6 = $$4.getMaterial();
        boolean $$7 = $$4.canBeReplaced(this.content);
        boolean bl = $$8 = $$4.isAir() || $$7 || $$5 instanceof LiquidBlockContainer && ((LiquidBlockContainer)((Object)$$5)).canPlaceLiquid($$1, $$2, $$4, this.content);
        if (!$$8) {
            return $$3 != null && this.emptyContents($$0, $$1, (BlockPos)$$3.getBlockPos().relative($$3.getDirection()), null);
        }
        if ($$1.dimensionType().ultraWarm() && this.content.is(FluidTags.WATER)) {
            int $$9 = $$2.getX();
            int $$10 = $$2.getY();
            int $$11 = $$2.getZ();
            $$1.playSound($$0, $$2, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5f, 2.6f + ($$1.random.nextFloat() - $$1.random.nextFloat()) * 0.8f);
            for (int $$12 = 0; $$12 < 8; ++$$12) {
                $$1.addParticle(ParticleTypes.LARGE_SMOKE, (double)$$9 + Math.random(), (double)$$10 + Math.random(), (double)$$11 + Math.random(), 0.0, 0.0, 0.0);
            }
            return true;
        }
        if ($$5 instanceof LiquidBlockContainer && this.content == Fluids.WATER) {
            ((LiquidBlockContainer)((Object)$$5)).placeLiquid($$1, $$2, $$4, ((FlowingFluid)this.content).getSource(false));
            this.playEmptySound($$0, $$1, $$2);
            return true;
        }
        if (!$$1.isClientSide && $$7 && !$$6.isLiquid()) {
            $$1.destroyBlock($$2, true);
        }
        if ($$1.setBlock($$2, this.content.defaultFluidState().createLegacyBlock(), 11) || $$4.getFluidState().isSource()) {
            this.playEmptySound($$0, $$1, $$2);
            return true;
        }
        return false;
    }

    protected void playEmptySound(@Nullable Player $$0, LevelAccessor $$1, BlockPos $$2) {
        SoundEvent $$3 = this.content.is(FluidTags.LAVA) ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY;
        $$1.playSound($$0, $$2, $$3, SoundSource.BLOCKS, 1.0f, 1.0f);
        $$1.gameEvent((Entity)$$0, GameEvent.FLUID_PLACE, $$2);
    }
}