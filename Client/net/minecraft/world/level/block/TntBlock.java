/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

public class TntBlock
extends Block {
    public static final BooleanProperty UNSTABLE = BlockStateProperties.UNSTABLE;

    public TntBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)this.defaultBlockState().setValue(UNSTABLE, false));
    }

    @Override
    public void onPlace(BlockState $$0, Level $$1, BlockPos $$2, BlockState $$3, boolean $$4) {
        if ($$3.is($$0.getBlock())) {
            return;
        }
        if ($$1.hasNeighborSignal($$2)) {
            TntBlock.explode($$1, $$2);
            $$1.removeBlock($$2, false);
        }
    }

    @Override
    public void neighborChanged(BlockState $$0, Level $$1, BlockPos $$2, Block $$3, BlockPos $$4, boolean $$5) {
        if ($$1.hasNeighborSignal($$2)) {
            TntBlock.explode($$1, $$2);
            $$1.removeBlock($$2, false);
        }
    }

    @Override
    public void playerWillDestroy(Level $$0, BlockPos $$1, BlockState $$2, Player $$3) {
        if (!$$0.isClientSide() && !$$3.isCreative() && $$2.getValue(UNSTABLE).booleanValue()) {
            TntBlock.explode($$0, $$1);
        }
        super.playerWillDestroy($$0, $$1, $$2, $$3);
    }

    @Override
    public void wasExploded(Level $$0, BlockPos $$1, Explosion $$2) {
        if ($$0.isClientSide) {
            return;
        }
        PrimedTnt $$3 = new PrimedTnt($$0, (double)$$1.getX() + 0.5, $$1.getY(), (double)$$1.getZ() + 0.5, $$2.getIndirectSourceEntity());
        int $$4 = $$3.getFuse();
        $$3.setFuse((short)($$0.random.nextInt($$4 / 4) + $$4 / 8));
        $$0.addFreshEntity($$3);
    }

    public static void explode(Level $$0, BlockPos $$1) {
        TntBlock.explode($$0, $$1, null);
    }

    private static void explode(Level $$0, BlockPos $$1, @Nullable LivingEntity $$2) {
        if ($$0.isClientSide) {
            return;
        }
        PrimedTnt $$3 = new PrimedTnt($$0, (double)$$1.getX() + 0.5, $$1.getY(), (double)$$1.getZ() + 0.5, $$2);
        $$0.addFreshEntity($$3);
        $$0.playSound(null, $$3.getX(), $$3.getY(), $$3.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0f, 1.0f);
        $$0.gameEvent($$2, GameEvent.PRIME_FUSE, $$1);
    }

    @Override
    public InteractionResult use(BlockState $$0, Level $$12, BlockPos $$2, Player $$3, InteractionHand $$4, BlockHitResult $$5) {
        ItemStack $$6 = $$3.getItemInHand($$4);
        if ($$6.is(Items.FLINT_AND_STEEL) || $$6.is(Items.FIRE_CHARGE)) {
            TntBlock.explode($$12, $$2, $$3);
            $$12.setBlock($$2, Blocks.AIR.defaultBlockState(), 11);
            Item $$7 = $$6.getItem();
            if (!$$3.isCreative()) {
                if ($$6.is(Items.FLINT_AND_STEEL)) {
                    $$6.hurtAndBreak(1, $$3, $$1 -> $$1.broadcastBreakEvent($$4));
                } else {
                    $$6.shrink(1);
                }
            }
            $$3.awardStat(Stats.ITEM_USED.get($$7));
            return InteractionResult.sidedSuccess($$12.isClientSide);
        }
        return super.use($$0, $$12, $$2, $$3, $$4, $$5);
    }

    @Override
    public void onProjectileHit(Level $$0, BlockState $$1, BlockHitResult $$2, Projectile $$3) {
        if (!$$0.isClientSide) {
            BlockPos $$4 = $$2.getBlockPos();
            Entity $$5 = $$3.getOwner();
            if ($$3.isOnFire() && $$3.mayInteract($$0, $$4)) {
                TntBlock.explode($$0, $$4, $$5 instanceof LivingEntity ? (LivingEntity)$$5 : null);
                $$0.removeBlock($$4, false);
            }
        }
    }

    @Override
    public boolean dropFromExplosion(Explosion $$0) {
        return false;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(UNSTABLE);
    }
}