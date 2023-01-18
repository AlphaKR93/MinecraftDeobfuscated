/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SweetBerryBushBlock
extends BushBlock
implements BonemealableBlock {
    private static final float HURT_SPEED_THRESHOLD = 0.003f;
    public static final int MAX_AGE = 3;
    public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
    private static final VoxelShape SAPLING_SHAPE = Block.box(3.0, 0.0, 3.0, 13.0, 8.0, 13.0);
    private static final VoxelShape MID_GROWTH_SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);

    public SweetBerryBushBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(AGE, 0));
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter $$0, BlockPos $$1, BlockState $$2) {
        return new ItemStack(Items.SWEET_BERRIES);
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        if ($$0.getValue(AGE) == 0) {
            return SAPLING_SHAPE;
        }
        if ($$0.getValue(AGE) < 3) {
            return MID_GROWTH_SHAPE;
        }
        return super.getShape($$0, $$1, $$2, $$3);
    }

    @Override
    public boolean isRandomlyTicking(BlockState $$0) {
        return $$0.getValue(AGE) < 3;
    }

    @Override
    public void randomTick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        int $$4 = $$0.getValue(AGE);
        if ($$4 < 3 && $$3.nextInt(5) == 0 && $$1.getRawBrightness((BlockPos)$$2.above(), 0) >= 9) {
            BlockState $$5 = (BlockState)$$0.setValue(AGE, $$4 + 1);
            $$1.setBlock($$2, $$5, 2);
            $$1.gameEvent(GameEvent.BLOCK_CHANGE, $$2, GameEvent.Context.of($$5));
        }
    }

    @Override
    public void entityInside(BlockState $$0, Level $$1, BlockPos $$2, Entity $$3) {
        if (!($$3 instanceof LivingEntity) || $$3.getType() == EntityType.FOX || $$3.getType() == EntityType.BEE) {
            return;
        }
        $$3.makeStuckInBlock($$0, new Vec3(0.8f, 0.75, 0.8f));
        if (!($$1.isClientSide || $$0.getValue(AGE) <= 0 || $$3.xOld == $$3.getX() && $$3.zOld == $$3.getZ())) {
            double $$4 = Math.abs((double)($$3.getX() - $$3.xOld));
            double $$5 = Math.abs((double)($$3.getZ() - $$3.zOld));
            if ($$4 >= (double)0.003f || $$5 >= (double)0.003f) {
                $$3.hurt(DamageSource.SWEET_BERRY_BUSH, 1.0f);
            }
        }
    }

    @Override
    public InteractionResult use(BlockState $$0, Level $$1, BlockPos $$2, Player $$3, InteractionHand $$4, BlockHitResult $$5) {
        boolean $$7;
        int $$6 = $$0.getValue(AGE);
        boolean bl = $$7 = $$6 == 3;
        if (!$$7 && $$3.getItemInHand($$4).is(Items.BONE_MEAL)) {
            return InteractionResult.PASS;
        }
        if ($$6 > 1) {
            int $$8 = 1 + $$1.random.nextInt(2);
            SweetBerryBushBlock.popResource($$1, $$2, new ItemStack(Items.SWEET_BERRIES, $$8 + ($$7 ? 1 : 0)));
            $$1.playSound(null, $$2, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0f, 0.8f + $$1.random.nextFloat() * 0.4f);
            BlockState $$9 = (BlockState)$$0.setValue(AGE, 1);
            $$1.setBlock($$2, $$9, 2);
            $$1.gameEvent(GameEvent.BLOCK_CHANGE, $$2, GameEvent.Context.of($$3, $$9));
            return InteractionResult.sidedSuccess($$1.isClientSide);
        }
        return super.use($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(AGE);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader $$0, BlockPos $$1, BlockState $$2, boolean $$3) {
        return $$2.getValue(AGE) < 3;
    }

    @Override
    public boolean isBonemealSuccess(Level $$0, RandomSource $$1, BlockPos $$2, BlockState $$3) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel $$0, RandomSource $$1, BlockPos $$2, BlockState $$3) {
        int $$4 = Math.min((int)3, (int)($$3.getValue(AGE) + 1));
        $$0.setBlock($$2, (BlockState)$$3.setValue(AGE, $$4), 2);
    }
}