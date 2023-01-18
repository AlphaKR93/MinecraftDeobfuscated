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
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Arrays
 *  java.util.Collection
 *  java.util.concurrent.CompletableFuture
 */
package net.minecraft.commands.arguments.item;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.commands.arguments.item.ItemParser;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

public class ItemArgument
implements ArgumentType<ItemInput> {
    private static final Collection<String> EXAMPLES = Arrays.asList((Object[])new String[]{"stick", "minecraft:stick", "stick{foo=bar}"});
    private final HolderLookup<Item> items;

    public ItemArgument(CommandBuildContext $$0) {
        this.items = $$0.holderLookup(Registries.ITEM);
    }

    public static ItemArgument item(CommandBuildContext $$0) {
        return new ItemArgument($$0);
    }

    public ItemInput parse(StringReader $$0) throws CommandSyntaxException {
        ItemParser.ItemResult $$1 = ItemParser.parseForItem(this.items, $$0);
        return new ItemInput($$1.item(), $$1.nbt());
    }

    public static <S> ItemInput getItem(CommandContext<S> $$0, String $$1) {
        return (ItemInput)$$0.getArgument($$1, ItemInput.class);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> $$0, SuggestionsBuilder $$1) {
        return ItemParser.fillSuggestions(this.items, $$1, false);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}