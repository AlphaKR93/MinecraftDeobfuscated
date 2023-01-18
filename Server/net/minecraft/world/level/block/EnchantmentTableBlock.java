/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.EnchantmentTableBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EnchantmentTableBlock
extends BaseEntityBlock {
    protected static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);
    public static final List<BlockPos> BOOKSHELF_OFFSETS = BlockPos.betweenClosedStream(-2, 0, -2, 2, 1, 2).filter($$0 -> Math.abs((int)$$0.getX()) == 2 || Math.abs((int)$$0.getZ()) == 2).map(BlockPos::immutable).toList();

    protected EnchantmentTableBlock(BlockBehaviour.Properties $$0) {
        super($$0);
    }

    public static boolean isValidBookShelf(Level $$0, BlockPos $$1, BlockPos $$2) {
        return $$0.getBlockState((BlockPos)$$1.offset($$2)).is(Blocks.BOOKSHELF) && $$0.isEmptyBlock($$1.offset($$2.getX() / 2, $$2.getY(), $$2.getZ() / 2));
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState $$0) {
        return true;
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return SHAPE;
    }

    @Override
    public void animateTick(BlockState $$0, Level $$1, BlockPos $$2, RandomSource $$3) {
        super.animateTick($$0, $$1, $$2, $$3);
        for (BlockPos $$4 : BOOKSHELF_OFFSETS) {
            if ($$3.nextInt(16) != 0 || !EnchantmentTableBlock.isValidBookShelf($$1, $$2, $$4)) continue;
            $$1.addParticle(ParticleTypes.ENCHANT, (double)$$2.getX() + 0.5, (double)$$2.getY() + 2.0, (double)$$2.getZ() + 0.5, (double)((float)$$4.getX() + $$3.nextFloat()) - 0.5, (float)$$4.getY() - $$3.nextFloat() - 1.0f, (double)((float)$$4.getZ() + $$3.nextFloat()) - 0.5);
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState $$0) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos $$0, BlockState $$1) {
        return new EnchantmentTableBlockEntity($$0, $$1);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level $$0, BlockState $$1, BlockEntityType<T> $$2) {
        return $$0.isClientSide ? EnchantmentTableBlock.createTickerHelper($$2, BlockEntityType.ENCHANTING_TABLE, EnchantmentTableBlockEntity::bookAnimationTick) : null;
    }

    @Override
    public InteractionResult use(BlockState $$0, Level $$1, BlockPos $$2, Player $$3, InteractionHand $$4, BlockHitResult $$5) {
        if ($$1.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        $$3.openMenu($$0.getMenuProvider($$1, $$2));
        return InteractionResult.CONSUME;
    }

    @Override
    @Nullable
    public MenuProvider getMenuProvider(BlockState $$0, Level $$1, BlockPos $$22) {
        BlockEntity $$32 = $$1.getBlockEntity($$22);
        if ($$32 instanceof EnchantmentTableBlockEntity) {
            Component $$42 = ((Nameable)((Object)$$32)).getDisplayName();
            return new SimpleMenuProvider(($$2, $$3, $$4) -> new EnchantmentMenu($$2, $$3, ContainerLevelAccess.create($$1, $$22)), $$42);
        }
        return null;
    }

    @Override
    public void setPlacedBy(Level $$0, BlockPos $$1, BlockState $$2, LivingEntity $$3, ItemStack $$4) {
        BlockEntity $$5;
        if ($$4.hasCustomHoverName() && ($$5 = $$0.getBlockEntity($$1)) instanceof EnchantmentTableBlockEntity) {
            ((EnchantmentTableBlockEntity)$$5).setCustomName($$4.getHoverName());
        }
    }

    @Override
    public boolean isPathfindable(BlockState $$0, BlockGetter $$1, BlockPos $$2, PathComputationType $$3) {
        return false;
    }
}