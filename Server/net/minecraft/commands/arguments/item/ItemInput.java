/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 */
package net.minecraft.commands.arguments.item;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemInput
implements Predicate<ItemStack> {
    private static final Dynamic2CommandExceptionType ERROR_STACK_TOO_BIG = new Dynamic2CommandExceptionType(($$0, $$1) -> Component.translatable("arguments.item.overstacked", $$0, $$1));
    private final Holder<Item> item;
    @Nullable
    private final CompoundTag tag;

    public ItemInput(Holder<Item> $$0, @Nullable CompoundTag $$1) {
        this.item = $$0;
        this.tag = $$1;
    }

    public Item getItem() {
        return this.item.value();
    }

    public boolean test(ItemStack $$0) {
        return $$0.is(this.item) && NbtUtils.compareNbt(this.tag, $$0.getTag(), true);
    }

    public ItemStack createItemStack(int $$0, boolean $$1) throws CommandSyntaxException {
        ItemStack $$2 = new ItemStack(this.item, $$0);
        if (this.tag != null) {
            $$2.setTag(this.tag);
        }
        if ($$1 && $$0 > $$2.getMaxStackSize()) {
            throw ERROR_STACK_TOO_BIG.create((Object)this.getItemName(), (Object)$$2.getMaxStackSize());
        }
        return $$2;
    }

    public String serialize() {
        StringBuilder $$0 = new StringBuilder(this.getItemName());
        if (this.tag != null) {
            $$0.append((Object)this.tag);
        }
        return $$0.toString();
    }

    private String getItemName() {
        return this.item.unwrapKey().map(ResourceKey::location).orElseGet(() -> "unknown[" + this.item + "]").toString();
    }
}