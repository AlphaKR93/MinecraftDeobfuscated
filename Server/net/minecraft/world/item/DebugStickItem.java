/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Comparable
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Collection
 *  javax.annotation.Nullable
 */
package net.minecraft.world.item;

import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

public class DebugStickItem
extends Item {
    public DebugStickItem(Item.Properties $$0) {
        super($$0);
    }

    @Override
    public boolean isFoil(ItemStack $$0) {
        return true;
    }

    @Override
    public boolean canAttackBlock(BlockState $$0, Level $$1, BlockPos $$2, Player $$3) {
        if (!$$1.isClientSide) {
            this.handleInteraction($$3, $$0, $$1, $$2, false, $$3.getItemInHand(InteractionHand.MAIN_HAND));
        }
        return false;
    }

    @Override
    public InteractionResult useOn(UseOnContext $$0) {
        BlockPos $$3;
        Player $$1 = $$0.getPlayer();
        Level $$2 = $$0.getLevel();
        if (!$$2.isClientSide && $$1 != null && !this.handleInteraction($$1, $$2.getBlockState($$3 = $$0.getClickedPos()), $$2, $$3, true, $$0.getItemInHand())) {
            return InteractionResult.FAIL;
        }
        return InteractionResult.sidedSuccess($$2.isClientSide);
    }

    private boolean handleInteraction(Player $$0, BlockState $$1, LevelAccessor $$2, BlockPos $$3, boolean $$4, ItemStack $$5) {
        if (!$$0.canUseGameMasterBlocks()) {
            return false;
        }
        Block $$6 = $$1.getBlock();
        StateDefinition<Block, BlockState> $$7 = $$6.getStateDefinition();
        Collection<Property<?>> $$8 = $$7.getProperties();
        String $$9 = BuiltInRegistries.BLOCK.getKey($$6).toString();
        if ($$8.isEmpty()) {
            DebugStickItem.message($$0, Component.translatable(this.getDescriptionId() + ".empty", $$9));
            return false;
        }
        CompoundTag $$10 = $$5.getOrCreateTagElement("DebugProperty");
        String $$11 = $$10.getString($$9);
        Property<?> $$12 = $$7.getProperty($$11);
        if ($$4) {
            if ($$12 == null) {
                $$12 = (Property<?>)$$8.iterator().next();
            }
            BlockState $$13 = DebugStickItem.cycleState($$1, $$12, $$0.isSecondaryUseActive());
            $$2.setBlock($$3, $$13, 18);
            DebugStickItem.message($$0, Component.translatable(this.getDescriptionId() + ".update", $$12.getName(), DebugStickItem.getNameHelper($$13, $$12)));
        } else {
            $$12 = DebugStickItem.getRelative($$8, $$12, $$0.isSecondaryUseActive());
            String $$14 = $$12.getName();
            $$10.putString($$9, $$14);
            DebugStickItem.message($$0, Component.translatable(this.getDescriptionId() + ".select", $$14, DebugStickItem.getNameHelper($$1, $$12)));
        }
        return true;
    }

    private static <T extends Comparable<T>> BlockState cycleState(BlockState $$0, Property<T> $$1, boolean $$2) {
        return (BlockState)$$0.setValue($$1, (Comparable)DebugStickItem.getRelative($$1.getPossibleValues(), $$0.getValue($$1), $$2));
    }

    private static <T> T getRelative(Iterable<T> $$0, @Nullable T $$1, boolean $$2) {
        return $$2 ? Util.findPreviousInIterable($$0, $$1) : Util.findNextInIterable($$0, $$1);
    }

    private static void message(Player $$0, Component $$1) {
        ((ServerPlayer)$$0).sendSystemMessage($$1, true);
    }

    private static <T extends Comparable<T>> String getNameHelper(BlockState $$0, Property<T> $$1) {
        return $$1.getName($$0.getValue($$1));
    }
}