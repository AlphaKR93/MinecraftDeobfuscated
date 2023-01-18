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
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.DaylightDetectorBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DaylightDetectorBlock
extends BaseEntityBlock {
    public static final IntegerProperty POWER = BlockStateProperties.POWER;
    public static final BooleanProperty INVERTED = BlockStateProperties.INVERTED;
    protected static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 6.0, 16.0);

    public DaylightDetectorBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(POWER, 0)).setValue(INVERTED, false));
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return SHAPE;
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState $$0) {
        return true;
    }

    @Override
    public int getSignal(BlockState $$0, BlockGetter $$1, BlockPos $$2, Direction $$3) {
        return $$0.getValue(POWER);
    }

    private static void updateSignalStrength(BlockState $$0, Level $$1, BlockPos $$2) {
        int $$3 = $$1.getBrightness(LightLayer.SKY, $$2) - $$1.getSkyDarken();
        float $$4 = $$1.getSunAngle(1.0f);
        boolean $$5 = $$0.getValue(INVERTED);
        if ($$5) {
            $$3 = 15 - $$3;
        } else if ($$3 > 0) {
            float $$6 = $$4 < (float)Math.PI ? 0.0f : (float)Math.PI * 2;
            $$4 += ($$6 - $$4) * 0.2f;
            $$3 = Math.round((float)((float)$$3 * Mth.cos($$4)));
        }
        $$3 = Mth.clamp($$3, 0, 15);
        if ($$0.getValue(POWER) != $$3) {
            $$1.setBlock($$2, (BlockState)$$0.setValue(POWER, $$3), 3);
        }
    }

    @Override
    public InteractionResult use(BlockState $$0, Level $$1, BlockPos $$2, Player $$3, InteractionHand $$4, BlockHitResult $$5) {
        if ($$3.mayBuild()) {
            if ($$1.isClientSide) {
                return InteractionResult.SUCCESS;
            }
            BlockState $$6 = (BlockState)$$0.cycle(INVERTED);
            $$1.setBlock($$2, $$6, 4);
            $$1.gameEvent(GameEvent.BLOCK_CHANGE, $$2, GameEvent.Context.of($$3, $$6));
            DaylightDetectorBlock.updateSignalStrength($$6, $$1, $$2);
            return InteractionResult.CONSUME;
        }
        return super.use($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    public RenderShape getRenderShape(BlockState $$0) {
        return RenderShape.MODEL;
    }

    @Override
    public boolean isSignalSource(BlockState $$0) {
        return true;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos $$0, BlockState $$1) {
        return new DaylightDetectorBlockEntity($$0, $$1);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level $$0, BlockState $$1, BlockEntityType<T> $$2) {
        if (!$$0.isClientSide && $$0.dimensionType().hasSkyLight()) {
            return DaylightDetectorBlock.createTickerHelper($$2, BlockEntityType.DAYLIGHT_DETECTOR, DaylightDetectorBlock::tickEntity);
        }
        return null;
    }

    private static void tickEntity(Level $$0, BlockPos $$1, BlockState $$2, DaylightDetectorBlockEntity $$3) {
        if ($$0.getGameTime() % 20L == 0L) {
            DaylightDetectorBlock.updateSignalStrength($$2, $$0, $$1);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(POWER, INVERTED);
    }
}