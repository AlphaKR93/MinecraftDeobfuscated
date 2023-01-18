/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.util.function.BiFunction
 *  java.util.function.Consumer
 */
package net.minecraft.world.level.storage.loot.functions;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContextUser;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;

public interface LootItemFunction
extends LootContextUser,
BiFunction<ItemStack, LootContext, ItemStack> {
    public LootItemFunctionType getType();

    public static Consumer<ItemStack> decorate(BiFunction<ItemStack, LootContext, ItemStack> $$0, Consumer<ItemStack> $$1, LootContext $$2) {
        return $$3 -> $$1.accept((Object)((ItemStack)$$0.apply($$3, (Object)$$2)));
    }

    public static interface Builder {
        public LootItemFunction build();
    }
}