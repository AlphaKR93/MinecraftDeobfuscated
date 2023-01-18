/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.Throwable
 *  java.nio.file.Path
 *  java.nio.file.Paths
 *  java.util.function.UnaryOperator
 */
package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.UnaryOperator;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.profiling.jfr.Environment;
import net.minecraft.util.profiling.jfr.JvmProfiler;

public class JfrCommand {
    private static final SimpleCommandExceptionType START_FAILED = new SimpleCommandExceptionType((Message)Component.translatable("commands.jfr.start.failed"));
    private static final DynamicCommandExceptionType DUMP_FAILED = new DynamicCommandExceptionType($$0 -> Component.translatable("commands.jfr.dump.failed", $$0));

    private JfrCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> $$02) {
        $$02.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("jfr").requires($$0 -> $$0.hasPermission(4))).then(Commands.literal("start").executes($$0 -> JfrCommand.startJfr((CommandSourceStack)$$0.getSource())))).then(Commands.literal("stop").executes($$0 -> JfrCommand.stopJfr((CommandSourceStack)$$0.getSource()))));
    }

    private static int startJfr(CommandSourceStack $$0) throws CommandSyntaxException {
        Environment $$1 = Environment.from($$0.getServer());
        if (!JvmProfiler.INSTANCE.start($$1)) {
            throw START_FAILED.create();
        }
        $$0.sendSuccess(Component.translatable("commands.jfr.started"), false);
        return 1;
    }

    private static int stopJfr(CommandSourceStack $$0) throws CommandSyntaxException {
        try {
            Path $$12 = Paths.get((String)".", (String[])new String[0]).relativize(JvmProfiler.INSTANCE.stop().normalize());
            Path $$2 = !$$0.getServer().isPublished() || SharedConstants.IS_RUNNING_IN_IDE ? $$12.toAbsolutePath() : $$12;
            MutableComponent $$3 = Component.literal($$12.toString()).withStyle(ChatFormatting.UNDERLINE).withStyle((UnaryOperator<Style>)((UnaryOperator)$$1 -> $$1.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, $$2.toString())).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.copy.click")))));
            $$0.sendSuccess(Component.translatable("commands.jfr.stopped", $$3), false);
            return 1;
        }
        catch (Throwable $$4) {
            throw DUMP_FAILED.create((Object)$$4.getMessage());
        }
    }
}