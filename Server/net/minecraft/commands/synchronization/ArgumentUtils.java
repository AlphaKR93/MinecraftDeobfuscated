/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.tree.ArgumentCommandNode
 *  com.mojang.brigadier.tree.CommandNode
 *  com.mojang.brigadier.tree.LiteralCommandNode
 *  com.mojang.brigadier.tree.RootCommandNode
 *  com.mojang.logging.LogUtils
 *  java.lang.Boolean
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Collection
 *  java.util.HashSet
 *  java.util.Set
 *  org.slf4j.Logger
 */
package net.minecraft.commands.synchronization;

import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.registries.BuiltInRegistries;
import org.slf4j.Logger;

public class ArgumentUtils {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final byte NUMBER_FLAG_MIN = 1;
    private static final byte NUMBER_FLAG_MAX = 2;

    public static int createNumberFlags(boolean $$0, boolean $$1) {
        int $$2 = 0;
        if ($$0) {
            $$2 |= 1;
        }
        if ($$1) {
            $$2 |= 2;
        }
        return $$2;
    }

    public static boolean numberHasMin(byte $$0) {
        return ($$0 & 1) != 0;
    }

    public static boolean numberHasMax(byte $$0) {
        return ($$0 & 2) != 0;
    }

    private static <A extends ArgumentType<?>> void serializeCap(JsonObject $$0, ArgumentTypeInfo.Template<A> $$1) {
        ArgumentUtils.serializeCap($$0, $$1.type(), $$1);
    }

    private static <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> void serializeCap(JsonObject $$0, ArgumentTypeInfo<A, T> $$1, ArgumentTypeInfo.Template<A> $$2) {
        $$1.serializeToJson($$2, $$0);
    }

    private static <T extends ArgumentType<?>> void serializeArgumentToJson(JsonObject $$0, T $$1) {
        ArgumentTypeInfo.Template<T> $$2 = ArgumentTypeInfos.unpack($$1);
        $$0.addProperty("type", "argument");
        $$0.addProperty("parser", BuiltInRegistries.COMMAND_ARGUMENT_TYPE.getKey($$2.type()).toString());
        JsonObject $$3 = new JsonObject();
        ArgumentUtils.serializeCap($$3, $$2);
        if ($$3.size() > 0) {
            $$0.add("properties", (JsonElement)$$3);
        }
    }

    public static <S> JsonObject serializeNodeToJson(CommandDispatcher<S> $$0, CommandNode<S> $$1) {
        Collection $$6;
        JsonObject $$2 = new JsonObject();
        if ($$1 instanceof RootCommandNode) {
            $$2.addProperty("type", "root");
        } else if ($$1 instanceof LiteralCommandNode) {
            $$2.addProperty("type", "literal");
        } else if ($$1 instanceof ArgumentCommandNode) {
            ArgumentCommandNode $$3 = (ArgumentCommandNode)$$1;
            ArgumentUtils.serializeArgumentToJson($$2, $$3.getType());
        } else {
            LOGGER.error("Could not serialize node {} ({})!", $$1, (Object)$$1.getClass());
            $$2.addProperty("type", "unknown");
        }
        JsonObject $$4 = new JsonObject();
        for (CommandNode $$5 : $$1.getChildren()) {
            $$4.add($$5.getName(), (JsonElement)ArgumentUtils.serializeNodeToJson($$0, $$5));
        }
        if ($$4.size() > 0) {
            $$2.add("children", (JsonElement)$$4);
        }
        if ($$1.getCommand() != null) {
            $$2.addProperty("executable", Boolean.valueOf((boolean)true));
        }
        if ($$1.getRedirect() != null && !($$6 = $$0.getPath($$1.getRedirect())).isEmpty()) {
            JsonArray $$7 = new JsonArray();
            for (String $$8 : $$6) {
                $$7.add($$8);
            }
            $$2.add("redirect", (JsonElement)$$7);
        }
        return $$2;
    }

    public static <T> Set<ArgumentType<?>> findUsedArgumentTypes(CommandNode<T> $$0) {
        Set $$1 = Sets.newIdentityHashSet();
        HashSet $$2 = Sets.newHashSet();
        ArgumentUtils.findUsedArgumentTypes($$0, $$2, $$1);
        return $$2;
    }

    private static <T> void findUsedArgumentTypes(CommandNode<T> $$0, Set<ArgumentType<?>> $$1, Set<CommandNode<T>> $$22) {
        if (!$$22.add($$0)) {
            return;
        }
        if ($$0 instanceof ArgumentCommandNode) {
            ArgumentCommandNode $$3 = (ArgumentCommandNode)$$0;
            $$1.add((Object)$$3.getType());
        }
        $$0.getChildren().forEach($$2 -> ArgumentUtils.findUsedArgumentTypes($$2, $$1, $$22));
        CommandNode $$4 = $$0.getRedirect();
        if ($$4 != null) {
            ArgumentUtils.findUsedArgumentTypes($$4, $$1, $$22);
        }
    }
}