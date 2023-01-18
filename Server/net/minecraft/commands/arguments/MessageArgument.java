/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  java.lang.Object
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.Arrays
 *  java.util.Collection
 *  java.util.concurrent.CompletableFuture
 *  java.util.function.Consumer
 *  javax.annotation.Nullable
 */
package net.minecraft.commands.arguments;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSigningContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.SignedArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.FilteredText;

public class MessageArgument
implements SignedArgument<Message> {
    private static final Collection<String> EXAMPLES = Arrays.asList((Object[])new String[]{"Hello world!", "foo", "@e", "Hello @p :)"});

    public static MessageArgument message() {
        return new MessageArgument();
    }

    public static Component getMessage(CommandContext<CommandSourceStack> $$0, String $$1) throws CommandSyntaxException {
        Message $$2 = (Message)$$0.getArgument($$1, Message.class);
        return $$2.resolveComponent((CommandSourceStack)$$0.getSource());
    }

    public static void resolveChatMessage(CommandContext<CommandSourceStack> $$0, String $$1, Consumer<PlayerChatMessage> $$2) throws CommandSyntaxException {
        Message $$3 = (Message)$$0.getArgument($$1, Message.class);
        CommandSourceStack $$4 = (CommandSourceStack)$$0.getSource();
        Component $$5 = $$3.resolveComponent($$4);
        CommandSigningContext $$6 = $$4.getSigningContext();
        PlayerChatMessage $$7 = $$6.getArgument($$1);
        if ($$7 != null) {
            MessageArgument.resolveSignedMessage($$2, $$4, $$7.withUnsignedContent($$5));
        } else {
            MessageArgument.resolveDisguisedMessage($$2, $$4, PlayerChatMessage.system($$3.text).withUnsignedContent($$5));
        }
    }

    private static void resolveSignedMessage(Consumer<PlayerChatMessage> $$0, CommandSourceStack $$1, PlayerChatMessage $$2) {
        MinecraftServer $$3 = $$1.getServer();
        CompletableFuture<FilteredText> $$4 = MessageArgument.filterPlainText($$1, $$2);
        CompletableFuture<Component> $$5 = $$3.getChatDecorator().decorate($$1.getPlayer(), $$2.decoratedContent());
        $$1.getChatMessageChainer().append($$42 -> CompletableFuture.allOf((CompletableFuture[])new CompletableFuture[]{$$4, $$5}).thenAcceptAsync($$4 -> {
            PlayerChatMessage $$5 = $$2.withUnsignedContent((Component)$$5.join()).filter(((FilteredText)((Object)((Object)((Object)$$4.join())))).mask());
            $$0.accept((Object)$$5);
        }, $$42));
    }

    private static void resolveDisguisedMessage(Consumer<PlayerChatMessage> $$0, CommandSourceStack $$1, PlayerChatMessage $$2) {
        MinecraftServer $$32 = $$1.getServer();
        CompletableFuture<Component> $$4 = $$32.getChatDecorator().decorate($$1.getPlayer(), $$2.decoratedContent());
        $$1.getChatMessageChainer().append($$3 -> $$4.thenAcceptAsync($$2 -> $$0.accept((Object)$$2.withUnsignedContent((Component)$$2)), $$3));
    }

    private static CompletableFuture<FilteredText> filterPlainText(CommandSourceStack $$0, PlayerChatMessage $$1) {
        ServerPlayer $$2 = $$0.getPlayer();
        if ($$2 != null && $$1.hasSignatureFrom($$2.getUUID())) {
            return $$2.getTextFilter().processStreamMessage($$1.signedContent());
        }
        return CompletableFuture.completedFuture((Object)((Object)FilteredText.passThrough($$1.signedContent())));
    }

    public Message parse(StringReader $$0) throws CommandSyntaxException {
        return Message.parseText($$0, true);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public static class Message {
        final String text;
        private final Part[] parts;

        public Message(String $$0, Part[] $$1) {
            this.text = $$0;
            this.parts = $$1;
        }

        public String getText() {
            return this.text;
        }

        public Part[] getParts() {
            return this.parts;
        }

        Component resolveComponent(CommandSourceStack $$0) throws CommandSyntaxException {
            return this.toComponent($$0, $$0.hasPermission(2));
        }

        public Component toComponent(CommandSourceStack $$0, boolean $$1) throws CommandSyntaxException {
            if (this.parts.length == 0 || !$$1) {
                return Component.literal(this.text);
            }
            MutableComponent $$2 = Component.literal(this.text.substring(0, this.parts[0].getStart()));
            int $$3 = this.parts[0].getStart();
            for (Part $$4 : this.parts) {
                Component $$5 = $$4.toComponent($$0);
                if ($$3 < $$4.getStart()) {
                    $$2.append(this.text.substring($$3, $$4.getStart()));
                }
                if ($$5 != null) {
                    $$2.append($$5);
                }
                $$3 = $$4.getEnd();
            }
            if ($$3 < this.text.length()) {
                $$2.append(this.text.substring($$3));
            }
            return $$2;
        }

        /*
         * WARNING - void declaration
         */
        public static Message parseText(StringReader $$0, boolean $$1) throws CommandSyntaxException {
            String $$2 = $$0.getString().substring($$0.getCursor(), $$0.getTotalLength());
            if (!$$1) {
                $$0.setCursor($$0.getTotalLength());
                return new Message($$2, new Part[0]);
            }
            ArrayList $$3 = Lists.newArrayList();
            int $$4 = $$0.getCursor();
            while ($$0.canRead()) {
                if ($$0.peek() == '@') {
                    void $$9;
                    int $$5 = $$0.getCursor();
                    try {
                        EntitySelectorParser $$6 = new EntitySelectorParser($$0);
                        EntitySelector $$7 = $$6.parse();
                    }
                    catch (CommandSyntaxException $$8) {
                        if ($$8.getType() == EntitySelectorParser.ERROR_MISSING_SELECTOR_TYPE || $$8.getType() == EntitySelectorParser.ERROR_UNKNOWN_SELECTOR_TYPE) {
                            $$0.setCursor($$5 + 1);
                            continue;
                        }
                        throw $$8;
                    }
                    $$3.add((Object)new Part($$5 - $$4, $$0.getCursor() - $$4, (EntitySelector)$$9));
                    continue;
                }
                $$0.skip();
            }
            return new Message($$2, (Part[])$$3.toArray((Object[])new Part[0]));
        }
    }

    public static class Part {
        private final int start;
        private final int end;
        private final EntitySelector selector;

        public Part(int $$0, int $$1, EntitySelector $$2) {
            this.start = $$0;
            this.end = $$1;
            this.selector = $$2;
        }

        public int getStart() {
            return this.start;
        }

        public int getEnd() {
            return this.end;
        }

        public EntitySelector getSelector() {
            return this.selector;
        }

        @Nullable
        public Component toComponent(CommandSourceStack $$0) throws CommandSyntaxException {
            return EntitySelector.joinNames(this.selector.findEntities($$0));
        }
    }
}