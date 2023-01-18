/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.util.function.ToIntFunction
 */
package net.minecraft.world.level.block;

import java.util.function.ToIntFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface CaveVines {
    public static final VoxelShape SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);
    public static final BooleanProperty BERRIES = BlockStateProperties.BERRIES;

    public static InteractionResult use(BlockState $$0, Level $$1, BlockPos $$2) {
        if ($$0.getValue(BERRIES).booleanValue()) {
            Block.popResource($$1, $$2, new ItemStack(Items.GLOW_BERRIES, 1));
            float $$3 = Mth.randomBetween($$1.random, 0.8f, 1.2f);
            $$1.playSound(null, $$2, SoundEvents.CAVE_VINES_PICK_BERRIES, SoundSource.BLOCKS, 1.0f, $$3);
            $$1.setBlock($$2, (BlockState)$$0.setValue(BERRIES, false), 2);
            return InteractionResult.sidedSuccess($$1.isClientSide);
        }
        return InteractionResult.PASS;
    }

    public static boolean hasGlowBerries(BlockState $$0) {
        return $$0.hasProperty(BERRIES) && $$0.getValue(BERRIES) != false;
    }

    public static ToIntFunction<BlockState> emission(int $$0) {
        return $$1 -> $$1.getValue(BlockStateProperties.BERRIES) != false ? $$0 : 0;
    }
}