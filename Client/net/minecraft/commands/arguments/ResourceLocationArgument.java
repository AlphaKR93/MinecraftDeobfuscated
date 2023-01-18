/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Arrays
 *  java.util.Collection
 */
package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.advancements.Advancement;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.storage.loot.ItemModifierManager;
import net.minecraft.world.level.storage.loot.PredicateManager;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class ResourceLocationArgument
implements ArgumentType<ResourceLocation> {
    private static final Collection<String> EXAMPLES = Arrays.asList((Object[])new String[]{"foo", "foo:bar", "012"});
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_ADVANCEMENT = new DynamicCommandExceptionType($$0 -> Component.translatable("advancement.advancementNotFound", $$0));
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_RECIPE = new DynamicCommandExceptionType($$0 -> Component.translatable("recipe.notFound", $$0));
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_PREDICATE = new DynamicCommandExceptionType($$0 -> Component.translatable("predicate.unknown", $$0));
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_ITEM_MODIFIER = new DynamicCommandExceptionType($$0 -> Component.translatable("item_modifier.unknown", $$0));

    public static ResourceLocationArgument id() {
        return new ResourceLocationArgument();
    }

    public static Advancement getAdvancement(CommandContext<CommandSourceStack> $$0, String $$1) throws CommandSyntaxException {
        ResourceLocation $$2 = ResourceLocationArgument.getId($$0, $$1);
        Advancement $$3 = ((CommandSourceStack)$$0.getSource()).getServer().getAdvancements().getAdvancement($$2);
        if ($$3 == null) {
            throw ERROR_UNKNOWN_ADVANCEMENT.create((Object)$$2);
        }
        return $$3;
    }

    public static Recipe<?> getRecipe(CommandContext<CommandSourceStack> $$0, String $$1) throws CommandSyntaxException {
        RecipeManager $$2 = ((CommandSourceStack)$$0.getSource()).getServer().getRecipeManager();
        ResourceLocation $$3 = ResourceLocationArgument.getId($$0, $$1);
        return (Recipe)$$2.byKey($$3).orElseThrow(() -> ERROR_UNKNOWN_RECIPE.create((Object)$$3));
    }

    public static LootItemCondition getPredicate(CommandContext<CommandSourceStack> $$0, String $$1) throws CommandSyntaxException {
        ResourceLocation $$2 = ResourceLocationArgument.getId($$0, $$1);
        PredicateManager $$3 = ((CommandSourceStack)$$0.getSource()).getServer().getPredicateManager();
        LootItemCondition $$4 = $$3.get($$2);
        if ($$4 == null) {
            throw ERROR_UNKNOWN_PREDICATE.create((Object)$$2);
        }
        return $$4;
    }

    public static LootItemFunction getItemModifier(CommandContext<CommandSourceStack> $$0, String $$1) throws CommandSyntaxException {
        ResourceLocation $$2 = ResourceLocationArgument.getId($$0, $$1);
        ItemModifierManager $$3 = ((CommandSourceStack)$$0.getSource()).getServer().getItemModifierManager();
        LootItemFunction $$4 = $$3.get($$2);
        if ($$4 == null) {
            throw ERROR_UNKNOWN_ITEM_MODIFIER.create((Object)$$2);
        }
        return $$4;
    }

    public static ResourceLocation getId(CommandContext<CommandSourceStack> $$0, String $$1) {
        return (ResourceLocation)$$0.getArgument($$1, ResourceLocation.class);
    }

    public ResourceLocation parse(StringReader $$0) throws CommandSyntaxException {
        return ResourceLocation.read($$0);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}