/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.level.block;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class SignBlock
extends BaseEntityBlock
implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    protected static final float AABB_OFFSET = 4.0f;
    protected static final VoxelShape SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);
    private final WoodType type;

    protected SignBlock(BlockBehaviour.Properties $$0, WoodType $$1) {
        super($$0);
        this.type = $$1;
    }

    @Override
    public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            $$3.scheduleTick($$4, Fluids.WATER, Fluids.WATER.getTickDelay($$3));
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return SHAPE;
    }

    @Override
    public boolean isPossibleToRespawnInThis() {
        return true;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos $$0, BlockState $$1) {
        return new SignBlockEntity($$0, $$1);
    }

    @Override
    public InteractionResult use(BlockState $$0, Level $$1, BlockPos $$2, Player $$3, InteractionHand $$4, BlockHitResult $$5) {
        boolean $$11;
        ItemStack $$6 = $$3.getItemInHand($$4);
        Item $$7 = $$6.getItem();
        boolean $$8 = $$7 instanceof DyeItem;
        boolean $$9 = $$6.is(Items.GLOW_INK_SAC);
        boolean $$10 = $$6.is(Items.INK_SAC);
        boolean bl = $$11 = ($$9 || $$8 || $$10) && $$3.getAbilities().mayBuild;
        if ($$1.isClientSide) {
            return $$11 ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
        }
        BlockEntity blockEntity = $$1.getBlockEntity($$2);
        if (blockEntity instanceof SignBlockEntity) {
            SignBlockEntity $$12 = (SignBlockEntity)blockEntity;
            boolean $$13 = $$12.hasGlowingText();
            if ($$9 && $$13 || $$10 && !$$13) {
                return InteractionResult.PASS;
            }
            if ($$11) {
                boolean $$16;
                if ($$9) {
                    $$1.playSound(null, $$2, SoundEvents.GLOW_INK_SAC_USE, SoundSource.BLOCKS, 1.0f, 1.0f);
                    boolean $$14 = $$12.setHasGlowingText(true);
                    if ($$3 instanceof ServerPlayer) {
                        CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)$$3, $$2, $$6);
                    }
                } else if ($$10) {
                    $$1.playSound(null, $$2, SoundEvents.INK_SAC_USE, SoundSource.BLOCKS, 1.0f, 1.0f);
                    boolean $$15 = $$12.setHasGlowingText(false);
                } else {
                    $$1.playSound(null, $$2, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0f, 1.0f);
                    $$16 = $$12.setColor(((DyeItem)$$7).getDyeColor());
                }
                if ($$16) {
                    if (!$$3.isCreative()) {
                        $$6.shrink(1);
                    }
                    $$3.awardStat(Stats.ITEM_USED.get($$7));
                }
            }
            return $$12.executeClickCommands((ServerPlayer)$$3) ? InteractionResult.SUCCESS : InteractionResult.PASS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public FluidState getFluidState(BlockState $$0) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getSource(false);
        }
        return super.getFluidState($$0);
    }

    public WoodType type() {
        return this.type;
    }

    public static WoodType getWoodType(Block $$0) {
        WoodType $$2;
        if ($$0 instanceof SignBlock) {
            WoodType $$1 = ((SignBlock)$$0).type();
        } else {
            $$2 = WoodType.OAK;
        }
        return $$2;
    }
}