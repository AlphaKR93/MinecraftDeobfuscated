/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Objects
 *  javax.annotation.Nullable
 */
package net.minecraft.world.item;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

public class AdventureModeCheck {
    private final String tagName;
    @Nullable
    private BlockInWorld lastCheckedBlock;
    private boolean lastResult;
    private boolean checksBlockEntity;

    public AdventureModeCheck(String $$0) {
        this.tagName = $$0;
    }

    private static boolean areSameBlocks(BlockInWorld $$0, @Nullable BlockInWorld $$1, boolean $$2) {
        if ($$1 == null || $$0.getState() != $$1.getState()) {
            return false;
        }
        if (!$$2) {
            return true;
        }
        if ($$0.getEntity() == null && $$1.getEntity() == null) {
            return true;
        }
        if ($$0.getEntity() == null || $$1.getEntity() == null) {
            return false;
        }
        return Objects.equals((Object)$$0.getEntity().saveWithId(), (Object)$$1.getEntity().saveWithId());
    }

    public boolean test(ItemStack $$0, Registry<Block> $$1, BlockInWorld $$2) {
        if (AdventureModeCheck.areSameBlocks($$2, this.lastCheckedBlock, this.checksBlockEntity)) {
            return this.lastResult;
        }
        this.lastCheckedBlock = $$2;
        this.checksBlockEntity = false;
        CompoundTag $$3 = $$0.getTag();
        if ($$3 != null && $$3.contains(this.tagName, 9)) {
            ListTag $$4 = $$3.getList(this.tagName, 8);
            for (int $$5 = 0; $$5 < $$4.size(); ++$$5) {
                String $$6 = $$4.getString($$5);
                try {
                    BlockPredicateArgument.Result $$7 = BlockPredicateArgument.parse($$1.asLookup(), new StringReader($$6));
                    this.checksBlockEntity |= $$7.requiresNbt();
                    if ($$7.test($$2)) {
                        this.lastResult = true;
                        return true;
                    }
                    continue;
                }
                catch (CommandSyntaxException commandSyntaxException) {
                    // empty catch block
                }
            }
        }
        this.lastResult = false;
        return false;
    }
}