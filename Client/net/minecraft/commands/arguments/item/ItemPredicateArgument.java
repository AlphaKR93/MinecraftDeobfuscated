/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  com.mojang.datafixers.util.Either
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Arrays
 *  java.util.Collection
 *  java.util.concurrent.CompletableFuture
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 */
package net.minecraft.commands.arguments.item;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.datafixers.util.Either;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.item.ItemParser;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemPredicateArgument
implements ArgumentType<Result> {
    private static final Collection<String> EXAMPLES = Arrays.asList((Object[])new String[]{"stick", "minecraft:stick", "#stick", "#stick{foo=bar}"});
    private final HolderLookup<Item> items;

    public ItemPredicateArgument(CommandBuildContext $$0) {
        this.items = $$0.holderLookup(Registries.ITEM);
    }

    public static ItemPredicateArgument itemPredicate(CommandBuildContext $$0) {
        return new ItemPredicateArgument($$0);
    }

    public Result parse(StringReader $$02) throws CommandSyntaxException {
        Either<ItemParser.ItemResult, ItemParser.TagResult> $$1 = ItemParser.parseForTesting(this.items, $$02);
        return (Result)$$1.map($$0 -> ItemPredicateArgument.createResult((Predicate<Holder<Item>>)((Predicate)$$1 -> $$1 == $$0.item()), $$0.nbt()), $$0 -> ItemPredicateArgument.createResult((Predicate<Holder<Item>>)((Predicate)$$0.tag()::contains), $$0.nbt()));
    }

    public static Predicate<ItemStack> getItemPredicate(CommandContext<CommandSourceStack> $$0, String $$1) {
        return (Predicate)$$0.getArgument($$1, Result.class);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> $$0, SuggestionsBuilder $$1) {
        return ItemParser.fillSuggestions(this.items, $$1, true);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    private static Result createResult(Predicate<Holder<Item>> $$0, @Nullable CompoundTag $$12) {
        return $$12 != null ? $$2 -> $$2.is($$0) && NbtUtils.compareNbt($$12, $$2.getTag(), true) : $$1 -> $$1.is($$0);
    }

    public static interface Result
    extends Predicate<ItemStack> {
    }
}