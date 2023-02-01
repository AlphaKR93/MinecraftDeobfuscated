/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.LegacySmithingMenu;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CraftingTableBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class SmithingTableBlock
extends CraftingTableBlock {
    private static final Component CONTAINER_TITLE = Component.translatable("container.upgrade");

    protected SmithingTableBlock(BlockBehaviour.Properties $$0) {
        super($$0);
    }

    @Override
    public MenuProvider getMenuProvider(BlockState $$0, Level $$1, BlockPos $$22) {
        return new SimpleMenuProvider(($$2, $$3, $$4) -> $$1.enabledFeatures().contains(FeatureFlags.UPDATE_1_20) ? new SmithingMenu($$2, $$3, ContainerLevelAccess.create($$1, $$22)) : new LegacySmithingMenu($$2, $$3, ContainerLevelAccess.create($$1, $$22)), CONTAINER_TITLE);
    }

    @Override
    public InteractionResult use(BlockState $$0, Level $$1, BlockPos $$2, Player $$3, InteractionHand $$4, BlockHitResult $$5) {
        if ($$1.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        $$3.openMenu($$0.getMenuProvider($$1, $$2));
        $$3.awardStat(Stats.INTERACT_WITH_SMITHING_TABLE);
        return InteractionResult.CONSUME;
    }
}