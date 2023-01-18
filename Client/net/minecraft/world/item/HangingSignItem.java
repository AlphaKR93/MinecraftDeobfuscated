/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class HangingSignItem
extends StandingAndWallBlockItem {
    public HangingSignItem(Block $$0, Block $$1, Item.Properties $$2) {
        super($$0, $$1, $$2, Direction.UP);
    }

    @Override
    protected boolean canPlace(LevelReader $$0, BlockState $$1, BlockPos $$2) {
        WallHangingSignBlock $$3;
        Block block = $$1.getBlock();
        if (block instanceof WallHangingSignBlock && !($$3 = (WallHangingSignBlock)block).canPlace($$1, $$0, $$2)) {
            return false;
        }
        return super.canPlace($$0, $$1, $$2);
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos $$0, Level $$1, @Nullable Player $$2, ItemStack $$3, BlockState $$4) {
        BlockEntity blockEntity;
        boolean $$5 = super.updateCustomBlockEntityTag($$0, $$1, $$2, $$3, $$4);
        if (!$$1.isClientSide && !$$5 && $$2 != null && (blockEntity = $$1.getBlockEntity($$0)) instanceof SignBlockEntity) {
            SignBlockEntity $$6 = (SignBlockEntity)blockEntity;
            $$2.openTextEdit($$6);
        }
        return $$5;
    }
}