/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TurtleEggBlock
extends Block {
    public static final int MAX_HATCH_LEVEL = 2;
    public static final int MIN_EGGS = 1;
    public static final int MAX_EGGS = 4;
    private static final VoxelShape ONE_EGG_AABB = Block.box(3.0, 0.0, 3.0, 12.0, 7.0, 12.0);
    private static final VoxelShape MULTIPLE_EGGS_AABB = Block.box(1.0, 0.0, 1.0, 15.0, 7.0, 15.0);
    public static final IntegerProperty HATCH = BlockStateProperties.HATCH;
    public static final IntegerProperty EGGS = BlockStateProperties.EGGS;

    public TurtleEggBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(HATCH, 0)).setValue(EGGS, 1));
    }

    @Override
    public void stepOn(Level $$0, BlockPos $$1, BlockState $$2, Entity $$3) {
        if (!$$3.isSteppingCarefully()) {
            this.destroyEgg($$0, $$2, $$1, $$3, 100);
        }
        super.stepOn($$0, $$1, $$2, $$3);
    }

    @Override
    public void fallOn(Level $$0, BlockState $$1, BlockPos $$2, Entity $$3, float $$4) {
        if (!($$3 instanceof Zombie)) {
            this.destroyEgg($$0, $$1, $$2, $$3, 3);
        }
        super.fallOn($$0, $$1, $$2, $$3, $$4);
    }

    private void destroyEgg(Level $$0, BlockState $$1, BlockPos $$2, Entity $$3, int $$4) {
        if (!this.canDestroyEgg($$0, $$3)) {
            return;
        }
        if (!$$0.isClientSide && $$0.random.nextInt($$4) == 0 && $$1.is(Blocks.TURTLE_EGG)) {
            this.decreaseEggs($$0, $$2, $$1);
        }
    }

    private void decreaseEggs(Level $$0, BlockPos $$1, BlockState $$2) {
        $$0.playSound(null, $$1, SoundEvents.TURTLE_EGG_BREAK, SoundSource.BLOCKS, 0.7f, 0.9f + $$0.random.nextFloat() * 0.2f);
        int $$3 = $$2.getValue(EGGS);
        if ($$3 <= 1) {
            $$0.destroyBlock($$1, false);
        } else {
            $$0.setBlock($$1, (BlockState)$$2.setValue(EGGS, $$3 - 1), 2);
            $$0.gameEvent(GameEvent.BLOCK_DESTROY, $$1, GameEvent.Context.of($$2));
            $$0.levelEvent(2001, $$1, Block.getId($$2));
        }
    }

    @Override
    public void randomTick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        if (this.shouldUpdateHatchLevel($$1) && TurtleEggBlock.onSand($$1, $$2)) {
            int $$4 = $$0.getValue(HATCH);
            if ($$4 < 2) {
                $$1.playSound(null, $$2, SoundEvents.TURTLE_EGG_CRACK, SoundSource.BLOCKS, 0.7f, 0.9f + $$3.nextFloat() * 0.2f);
                $$1.setBlock($$2, (BlockState)$$0.setValue(HATCH, $$4 + 1), 2);
            } else {
                $$1.playSound(null, $$2, SoundEvents.TURTLE_EGG_HATCH, SoundSource.BLOCKS, 0.7f, 0.9f + $$3.nextFloat() * 0.2f);
                $$1.removeBlock($$2, false);
                for (int $$5 = 0; $$5 < $$0.getValue(EGGS); ++$$5) {
                    $$1.levelEvent(2001, $$2, Block.getId($$0));
                    Turtle $$6 = EntityType.TURTLE.create($$1);
                    if ($$6 == null) continue;
                    $$6.setAge(-24000);
                    $$6.setHomePos($$2);
                    $$6.moveTo((double)$$2.getX() + 0.3 + (double)$$5 * 0.2, $$2.getY(), (double)$$2.getZ() + 0.3, 0.0f, 0.0f);
                    $$1.addFreshEntity($$6);
                }
            }
        }
    }

    public static boolean onSand(BlockGetter $$0, BlockPos $$1) {
        return TurtleEggBlock.isSand($$0, (BlockPos)$$1.below());
    }

    public static boolean isSand(BlockGetter $$0, BlockPos $$1) {
        return $$0.getBlockState($$1).is(BlockTags.SAND);
    }

    @Override
    public void onPlace(BlockState $$0, Level $$1, BlockPos $$2, BlockState $$3, boolean $$4) {
        if (TurtleEggBlock.onSand($$1, $$2) && !$$1.isClientSide) {
            $$1.levelEvent(2005, $$2, 0);
        }
    }

    private boolean shouldUpdateHatchLevel(Level $$0) {
        float $$1 = $$0.getTimeOfDay(1.0f);
        if ((double)$$1 < 0.69 && (double)$$1 > 0.65) {
            return true;
        }
        return $$0.random.nextInt(500) == 0;
    }

    @Override
    public void playerDestroy(Level $$0, Player $$1, BlockPos $$2, BlockState $$3, @Nullable BlockEntity $$4, ItemStack $$5) {
        super.playerDestroy($$0, $$1, $$2, $$3, $$4, $$5);
        this.decreaseEggs($$0, $$2, $$3);
    }

    @Override
    public boolean canBeReplaced(BlockState $$0, BlockPlaceContext $$1) {
        if (!$$1.isSecondaryUseActive() && $$1.getItemInHand().is(this.asItem()) && $$0.getValue(EGGS) < 4) {
            return true;
        }
        return super.canBeReplaced($$0, $$1);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        BlockState $$1 = $$0.getLevel().getBlockState($$0.getClickedPos());
        if ($$1.is(this)) {
            return (BlockState)$$1.setValue(EGGS, Math.min((int)4, (int)($$1.getValue(EGGS) + 1)));
        }
        return super.getStateForPlacement($$0);
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        if ($$0.getValue(EGGS) > 1) {
            return MULTIPLE_EGGS_AABB;
        }
        return ONE_EGG_AABB;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(HATCH, EGGS);
    }

    private boolean canDestroyEgg(Level $$0, Entity $$1) {
        if ($$1 instanceof Turtle || $$1 instanceof Bat) {
            return false;
        }
        if ($$1 instanceof LivingEntity) {
            return $$1 instanceof Player || $$0.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
        }
        return false;
    }
}