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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SignItem
extends StandingAndWallBlockItem {
    public SignItem(Item.Properties $$0, Block $$1, Block $$2) {
        super($$1, $$2, $$0, Direction.DOWN);
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos $$0, Level $$1, @Nullable Player $$2, ItemStack $$3, BlockState $$4) {
        boolean $$5 = super.updateCustomBlockEntityTag($$0, $$1, $$2, $$3, $$4);
        if (!$$1.isClientSide && !$$5 && $$2 != null) {
            $$2.openTextEdit((SignBlockEntity)$$1.getBlockEntity($$0));
        }
        return $$5;
    }
}