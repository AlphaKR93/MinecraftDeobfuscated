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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.PushReaction;

public class IceBlock
extends HalfTransparentBlock {
    public IceBlock(BlockBehaviour.Properties $$0) {
        super($$0);
    }

    @Override
    public void playerDestroy(Level $$0, Player $$1, BlockPos $$2, BlockState $$3, @Nullable BlockEntity $$4, ItemStack $$5) {
        super.playerDestroy($$0, $$1, $$2, $$3, $$4, $$5);
        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, $$5) == 0) {
            if ($$0.dimensionType().ultraWarm()) {
                $$0.removeBlock($$2, false);
                return;
            }
            Material $$6 = $$0.getBlockState((BlockPos)$$2.below()).getMaterial();
            if ($$6.blocksMotion() || $$6.isLiquid()) {
                $$0.setBlockAndUpdate($$2, Blocks.WATER.defaultBlockState());
            }
        }
    }

    @Override
    public void randomTick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        if ($$1.getBrightness(LightLayer.BLOCK, $$2) > 11 - $$0.getLightBlock($$1, $$2)) {
            this.melt($$0, $$1, $$2);
        }
    }

    protected void melt(BlockState $$0, Level $$1, BlockPos $$2) {
        if ($$1.dimensionType().ultraWarm()) {
            $$1.removeBlock($$2, false);
            return;
        }
        $$1.setBlockAndUpdate($$2, Blocks.WATER.defaultBlockState());
        $$1.neighborChanged($$2, Blocks.WATER, $$2);
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState $$0) {
        return PushReaction.NORMAL;
    }
}