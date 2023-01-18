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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class GameMasterBlockItem
extends BlockItem {
    public GameMasterBlockItem(Block $$0, Item.Properties $$1) {
        super($$0, $$1);
    }

    @Override
    @Nullable
    protected BlockState getPlacementState(BlockPlaceContext $$0) {
        Player $$1 = $$0.getPlayer();
        return $$1 == null || $$1.canUseGameMasterBlocks() ? super.getPlacementState($$0) : null;
    }
}