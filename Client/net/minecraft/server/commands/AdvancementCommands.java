/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.Collection
 *  java.util.List
 *  java.util.stream.Stream
 */
package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class AdvancementCommands {
    private static final SuggestionProvider<CommandSourceStack> SUGGEST_ADVANCEMENTS = ($$0, $$1) -> {
        Collection<Advancement> $$2 = ((CommandSourceStack)$$0.getSource()).getServer().getAdvancements().getAllAdvancements();
        return SharedSuggestionProvider.suggestResource((Stream<ResourceLocation>)$$2.stream().map(Advancement::getId), $$1);
    };

    public static void register(CommandDispatcher<CommandSourceStack> $$02) {
        $$02.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("advancement").requires($$0 -> $$0.hasPermission(2))).then(Commands.literal("grant").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).then(Commands.literal("only").then(((RequiredArgumentBuilder)Commands.argument("advancement", ResourceLocationArgument.id()).suggests(SUGGEST_ADVANCEMENTS).executes($$0 -> AdvancementCommands.perform((CommandSourceStack)$$0.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)$$0, "targets"), Action.GRANT, AdvancementCommands.getAdvancements(ResourceLocationArgument.getAdvancement((CommandContext<CommandSourceStack>)$$0, "advancement"), Mode.ONLY)))).then(Commands.argument("criterion", StringArgumentType.greedyString()).suggests(($$0, $$1) -> SharedSuggestionProvider.suggest((Iterable<String>)ResourceLocationArgument.getAdvancement((CommandContext<CommandSourceStack>)$$0, "advancement").getCriteria().keySet(), $$1)).executes($$0 -> AdvancementCommands.performCriterion((CommandSourceStack)$$0.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)$$0, "targets"), Action.GRANT, ResourceLocationArgument.getAdvancement((CommandContext<CommandSourceStack>)$$0, "advancement"), StringArgumentType.getString((CommandContext)$$0, (String)"criterion"))))))).then(Commands.literal("from").then(Commands.argument("advancement", ResourceLocationArgument.id()).suggests(SUGGEST_ADVANCEMENTS).executes($$0 -> AdvancementCommands.perform((CommandSourceStack)$$0.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)$$0, "targets"), Action.GRANT, AdvancementCommands.getAdvancements(ResourceLocationArgument.getAdvancement((CommandContext<CommandSourceStack>)$$0, "advancement"), Mode.FROM)))))).then(Commands.literal("until").then(Commands.argument("advancement", ResourceLocationArgument.id()).suggests(SUGGEST_ADVANCEMENTS).executes($$0 -> AdvancementCommands.perform((CommandSourceStack)$$0.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)$$0, "targets"), Action.GRANT, AdvancementCommands.getAdvancements(ResourceLocationArgument.getAdvancement((CommandContext<CommandSourceStack>)$$0, "advancement"), Mode.UNTIL)))))).then(Commands.literal("through").then(Commands.argument("advancement", ResourceLocationArgument.id()).suggests(SUGGEST_ADVANCEMENTS).executes($$0 -> AdvancementCommands.perform((CommandSourceStack)$$0.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)$$0, "targets"), Action.GRANT, AdvancementCommands.getAdvancements(ResourceLocationArgument.getAdvancement((CommandContext<CommandSourceStack>)$$0, "advancement"), Mode.THROUGH)))))).then(Commands.literal("everything").executes($$0 -> AdvancementCommands.perform((CommandSourceStack)$$0.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)$$0, "targets"), Action.GRANT, ((CommandSourceStack)$$0.getSource()).getServer().getAdvancements().getAllAdvancements())))))).then(Commands.literal("revoke").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).then(Commands.literal("only").then(((RequiredArgumentBuilder)Commands.argument("advancement", ResourceLocationArgument.id()).suggests(SUGGEST_ADVANCEMENTS).executes($$0 -> AdvancementCommands.perform((CommandSourceStack)$$0.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)$$0, "targets"), Action.REVOKE, AdvancementCommands.getAdvancements(ResourceLocationArgument.getAdvancement((CommandContext<CommandSourceStack>)$$0, "advancement"), Mode.ONLY)))).then(Commands.argument("criterion", StringArgumentType.greedyString()).suggests(($$0, $$1) -> SharedSuggestionProvider.suggest((Iterable<String>)ResourceLocationArgument.getAdvancement((CommandContext<CommandSourceStack>)$$0, "advancement").getCriteria().keySet(), $$1)).executes($$0 -> AdvancementCommands.performCriterion((CommandSourceStack)$$0.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)$$0, "targets"), Action.REVOKE, ResourceLocationArgument.getAdvancement((CommandContext<CommandSourceStack>)$$0, "advancement"), StringArgumentType.getString((CommandContext)$$0, (String)"criterion"))))))).then(Commands.literal("from").then(Commands.argument("advancement", ResourceLocationArgument.id()).suggests(SUGGEST_ADVANCEMENTS).executes($$0 -> AdvancementCommands.perform((CommandSourceStack)$$0.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)$$0, "targets"), Action.REVOKE, AdvancementCommands.getAdvancements(ResourceLocationArgument.getAdvancement((CommandContext<CommandSourceStack>)$$0, "advancement"), Mode.FROM)))))).then(Commands.literal("until").then(Commands.argument("advancement", ResourceLocationArgument.id()).suggests(SUGGEST_ADVANCEMENTS).executes($$0 -> AdvancementCommands.perform((CommandSourceStack)$$0.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)$$0, "targets"), Action.REVOKE, AdvancementCommands.getAdvancements(ResourceLocationArgument.getAdvancement((CommandContext<CommandSourceStack>)$$0, "advancement"), Mode.UNTIL)))))).then(Commands.literal("through").then(Commands.argument("advancement", ResourceLocationArgument.id()).suggests(SUGGEST_ADVANCEMENTS).executes($$0 -> AdvancementCommands.perform((CommandSourceStack)$$0.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)$$0, "targets"), Action.REVOKE, AdvancementCommands.getAdvancements(ResourceLocationArgument.getAdvancement((CommandContext<CommandSourceStack>)$$0, "advancement"), Mode.THROUGH)))))).then(Commands.literal("everything").executes($$0 -> AdvancementCommands.perform((CommandSourceStack)$$0.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)$$0, "targets"), Action.REVOKE, ((CommandSourceStack)$$0.getSource()).getServer().getAdvancements().getAllAdvancements()))))));
    }

    private static int perform(CommandSourceStack $$0, Collection<ServerPlayer> $$1, Action $$2, Collection<Advancement> $$3) {
        int $$4 = 0;
        for (ServerPlayer $$5 : $$1) {
            $$4 += $$2.perform($$5, (Iterable<Advancement>)$$3);
        }
        if ($$4 == 0) {
            if ($$3.size() == 1) {
                if ($$1.size() == 1) {
                    throw new CommandRuntimeException(Component.translatable($$2.getKey() + ".one.to.one.failure", ((Advancement)$$3.iterator().next()).getChatComponent(), ((ServerPlayer)$$1.iterator().next()).getDisplayName()));
                }
                throw new CommandRuntimeException(Component.translatable($$2.getKey() + ".one.to.many.failure", ((Advancement)$$3.iterator().next()).getChatComponent(), $$1.size()));
            }
            if ($$1.size() == 1) {
                throw new CommandRuntimeException(Component.translatable($$2.getKey() + ".many.to.one.failure", $$3.size(), ((ServerPlayer)$$1.iterator().next()).getDisplayName()));
            }
            throw new CommandRuntimeException(Component.translatable($$2.getKey() + ".many.to.many.failure", $$3.size(), $$1.size()));
        }
        if ($$3.size() == 1) {
            if ($$1.size() == 1) {
                $$0.sendSuccess(Component.translatable($$2.getKey() + ".one.to.one.success", ((Advancement)$$3.iterator().next()).getChatComponent(), ((ServerPlayer)$$1.iterator().next()).getDisplayName()), true);
            } else {
                $$0.sendSuccess(Component.translatable($$2.getKey() + ".one.to.many.success", ((Advancement)$$3.iterator().next()).getChatComponent(), $$1.size()), true);
            }
        } else if ($$1.size() == 1) {
            $$0.sendSuccess(Component.translatable($$2.getKey() + ".many.to.one.success", $$3.size(), ((ServerPlayer)$$1.iterator().next()).getDisplayName()), true);
        } else {
            $$0.sendSuccess(Component.translatable($$2.getKey() + ".many.to.many.success", $$3.size(), $$1.size()), true);
        }
        return $$4;
    }

    private static int performCriterion(CommandSourceStack $$0, Collection<ServerPlayer> $$1, Action $$2, Advancement $$3, String $$4) {
        int $$5 = 0;
        if (!$$3.getCriteria().containsKey((Object)$$4)) {
            throw new CommandRuntimeException(Component.translatable("commands.advancement.criterionNotFound", $$3.getChatComponent(), $$4));
        }
        for (ServerPlayer $$6 : $$1) {
            if (!$$2.performCriterion($$6, $$3, $$4)) continue;
            ++$$5;
        }
        if ($$5 == 0) {
            if ($$1.size() == 1) {
                throw new CommandRuntimeException(Component.translatable($$2.getKey() + ".criterion.to.one.failure", $$4, $$3.getChatComponent(), ((ServerPlayer)$$1.iterator().next()).getDisplayName()));
            }
            throw new CommandRuntimeException(Component.translatable($$2.getKey() + ".criterion.to.many.failure", $$4, $$3.getChatComponent(), $$1.size()));
        }
        if ($$1.size() == 1) {
            $$0.sendSuccess(Component.translatable($$2.getKey() + ".criterion.to.one.success", $$4, $$3.getChatComponent(), ((ServerPlayer)$$1.iterator().next()).getDisplayName()), true);
        } else {
            $$0.sendSuccess(Component.translatable($$2.getKey() + ".criterion.to.many.success", $$4, $$3.getChatComponent(), $$1.size()), true);
        }
        return $$5;
    }

    private static List<Advancement> getAdvancements(Advancement $$0, Mode $$1) {
        ArrayList $$2 = Lists.newArrayList();
        if ($$1.parents) {
            for (Advancement $$3 = $$0.getParent(); $$3 != null; $$3 = $$3.getParent()) {
                $$2.add((Object)$$3);
            }
        }
        $$2.add((Object)$$0);
        if ($$1.children) {
            AdvancementCommands.addChildren($$0, (List<Advancement>)$$2);
        }
        return $$2;
    }

    private static void addChildren(Advancement $$0, List<Advancement> $$1) {
        for (Advancement $$2 : $$0.getChildren()) {
            $$1.add((Object)$$2);
            AdvancementCommands.addChildren($$2, $$1);
        }
    }

    /*
     * Uses 'sealed' constructs - enablewith --sealed true
     */
    static enum Action {
        GRANT("grant"){

            @Override
            protected boolean perform(ServerPlayer $$0, Advancement $$1) {
                AdvancementProgress $$2 = $$0.getAdvancements().getOrStartProgress($$1);
                if ($$2.isDone()) {
                    return false;
                }
                for (String $$3 : $$2.getRemainingCriteria()) {
                    $$0.getAdvancements().award($$1, $$3);
                }
                return true;
            }

            @Override
            protected boolean performCriterion(ServerPlayer $$0, Advancement $$1, String $$2) {
                return $$0.getAdvancements().award($$1, $$2);
            }
        }
        ,
        REVOKE("revoke"){

            @Override
            protected boolean perform(ServerPlayer $$0, Advancement $$1) {
                AdvancementProgress $$2 = $$0.getAdvancements().getOrStartProgress($$1);
                if (!$$2.hasProgress()) {
                    return false;
                }
                for (String $$3 : $$2.getCompletedCriteria()) {
                    $$0.getAdvancements().revoke($$1, $$3);
                }
                return true;
            }

            @Override
            protected boolean performCriterion(ServerPlayer $$0, Advancement $$1, String $$2) {
                return $$0.getAdvancements().revoke($$1, $$2);
            }
        };

        private final String key;

        Action(String $$0) {
            this.key = "commands.advancement." + $$0;
        }

        public int perform(ServerPlayer $$0, Iterable<Advancement> $$1) {
            int $$2 = 0;
            for (Advancement $$3 : $$1) {
                if (!this.perform($$0, $$3)) continue;
                ++$$2;
            }
            return $$2;
        }

        protected abstract boolean perform(ServerPlayer var1, Advancement var2);

        protected abstract boolean performCriterion(ServerPlayer var1, Advancement var2, String var3);

        protected String getKey() {
            return this.key;
        }
    }

    static enum Mode {
        ONLY(false, false),
        THROUGH(true, true),
        FROM(false, true),
        UNTIL(true, false),
        EVERYTHING(true, true);

        final boolean parents;
        final boolean children;

        private Mode(boolean $$0, boolean $$1) {
            this.parents = $$0;
            this.children = $$1;
        }
    }
}