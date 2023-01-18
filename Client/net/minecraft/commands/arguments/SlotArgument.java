/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  java.lang.Integer
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Arrays
 *  java.util.Collection
 *  java.util.Map
 *  java.util.concurrent.CompletableFuture
 */
package net.minecraft.commands.arguments;

import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;

public class SlotArgument
implements ArgumentType<Integer> {
    private static final Collection<String> EXAMPLES = Arrays.asList((Object[])new String[]{"container.5", "12", "weapon"});
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_SLOT = new DynamicCommandExceptionType($$0 -> Component.translatable("slot.unknown", $$0));
    private static final Map<String, Integer> SLOTS = (Map)Util.make(Maps.newHashMap(), $$0 -> {
        for (int $$1 = 0; $$1 < 54; ++$$1) {
            $$0.put((Object)("container." + $$1), (Object)$$1);
        }
        for (int $$2 = 0; $$2 < 9; ++$$2) {
            $$0.put((Object)("hotbar." + $$2), (Object)$$2);
        }
        for (int $$3 = 0; $$3 < 27; ++$$3) {
            $$0.put((Object)("inventory." + $$3), (Object)(9 + $$3));
        }
        for (int $$4 = 0; $$4 < 27; ++$$4) {
            $$0.put((Object)("enderchest." + $$4), (Object)(200 + $$4));
        }
        for (int $$5 = 0; $$5 < 8; ++$$5) {
            $$0.put((Object)("villager." + $$5), (Object)(300 + $$5));
        }
        for (int $$6 = 0; $$6 < 15; ++$$6) {
            $$0.put((Object)("horse." + $$6), (Object)(500 + $$6));
        }
        $$0.put((Object)"weapon", (Object)EquipmentSlot.MAINHAND.getIndex(98));
        $$0.put((Object)"weapon.mainhand", (Object)EquipmentSlot.MAINHAND.getIndex(98));
        $$0.put((Object)"weapon.offhand", (Object)EquipmentSlot.OFFHAND.getIndex(98));
        $$0.put((Object)"armor.head", (Object)EquipmentSlot.HEAD.getIndex(100));
        $$0.put((Object)"armor.chest", (Object)EquipmentSlot.CHEST.getIndex(100));
        $$0.put((Object)"armor.legs", (Object)EquipmentSlot.LEGS.getIndex(100));
        $$0.put((Object)"armor.feet", (Object)EquipmentSlot.FEET.getIndex(100));
        $$0.put((Object)"horse.saddle", (Object)400);
        $$0.put((Object)"horse.armor", (Object)401);
        $$0.put((Object)"horse.chest", (Object)499);
    });

    public static SlotArgument slot() {
        return new SlotArgument();
    }

    public static int getSlot(CommandContext<CommandSourceStack> $$0, String $$1) {
        return (Integer)$$0.getArgument($$1, Integer.class);
    }

    public Integer parse(StringReader $$0) throws CommandSyntaxException {
        String $$1 = $$0.readUnquotedString();
        if (!SLOTS.containsKey((Object)$$1)) {
            throw ERROR_UNKNOWN_SLOT.create((Object)$$1);
        }
        return (Integer)SLOTS.get((Object)$$1);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> $$0, SuggestionsBuilder $$1) {
        return SharedSuggestionProvider.suggest((Iterable<String>)SLOTS.keySet(), $$1);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}