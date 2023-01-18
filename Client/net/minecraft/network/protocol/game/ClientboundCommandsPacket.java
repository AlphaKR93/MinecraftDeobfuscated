/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Queues
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 *  com.mojang.brigadier.tree.ArgumentCommandNode
 *  com.mojang.brigadier.tree.CommandNode
 *  com.mojang.brigadier.tree.LiteralCommandNode
 *  com.mojang.brigadier.tree.RootCommandNode
 *  it.unimi.dsi.fastutil.ints.IntCollection
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  it.unimi.dsi.fastutil.ints.IntSets
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMap$Entry
 *  it.unimi.dsi.fastutil.objects.Object2IntMaps
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.UnsupportedOperationException
 *  java.util.ArrayDeque
 *  java.util.List
 *  java.util.function.BiPredicate
 *  javax.annotation.Nullable
 */
package net.minecraft.network.protocol.game;

import com.google.common.collect.Queues;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayDeque;
import java.util.List;
import java.util.function.BiPredicate;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;

public class ClientboundCommandsPacket
implements Packet<ClientGamePacketListener> {
    private static final byte MASK_TYPE = 3;
    private static final byte FLAG_EXECUTABLE = 4;
    private static final byte FLAG_REDIRECT = 8;
    private static final byte FLAG_CUSTOM_SUGGESTIONS = 16;
    private static final byte TYPE_ROOT = 0;
    private static final byte TYPE_LITERAL = 1;
    private static final byte TYPE_ARGUMENT = 2;
    private final int rootIndex;
    private final List<Entry> entries;

    public ClientboundCommandsPacket(RootCommandNode<SharedSuggestionProvider> $$0) {
        Object2IntMap<CommandNode<SharedSuggestionProvider>> $$1 = ClientboundCommandsPacket.enumerateNodes($$0);
        this.entries = ClientboundCommandsPacket.createEntries($$1);
        this.rootIndex = $$1.getInt($$0);
    }

    public ClientboundCommandsPacket(FriendlyByteBuf $$0) {
        this.entries = $$0.readList(ClientboundCommandsPacket::readNode);
        this.rootIndex = $$0.readVarInt();
        ClientboundCommandsPacket.validateEntries(this.entries);
    }

    @Override
    public void write(FriendlyByteBuf $$02) {
        $$02.writeCollection(this.entries, ($$0, $$1) -> $$1.write((FriendlyByteBuf)((Object)$$0)));
        $$02.writeVarInt(this.rootIndex);
    }

    private static void validateEntries(List<Entry> $$0, BiPredicate<Entry, IntSet> $$1) {
        IntOpenHashSet $$2 = new IntOpenHashSet((IntCollection)IntSets.fromTo((int)0, (int)$$0.size()));
        while (!$$2.isEmpty()) {
            boolean $$3 = $$2.removeIf(arg_0 -> ClientboundCommandsPacket.lambda$validateEntries$1($$1, $$0, (IntSet)$$2, arg_0));
            if ($$3) continue;
            throw new IllegalStateException("Server sent an impossible command tree");
        }
    }

    private static void validateEntries(List<Entry> $$0) {
        ClientboundCommandsPacket.validateEntries($$0, (BiPredicate<Entry, IntSet>)((BiPredicate)Entry::canBuild));
        ClientboundCommandsPacket.validateEntries($$0, (BiPredicate<Entry, IntSet>)((BiPredicate)Entry::canResolve));
    }

    private static Object2IntMap<CommandNode<SharedSuggestionProvider>> enumerateNodes(RootCommandNode<SharedSuggestionProvider> $$0) {
        CommandNode $$3;
        Object2IntOpenHashMap $$1 = new Object2IntOpenHashMap();
        ArrayDeque $$2 = Queues.newArrayDeque();
        $$2.add($$0);
        while (($$3 = (CommandNode)$$2.poll()) != null) {
            if ($$1.containsKey((Object)$$3)) continue;
            int $$4 = $$1.size();
            $$1.put((Object)$$3, $$4);
            $$2.addAll($$3.getChildren());
            if ($$3.getRedirect() == null) continue;
            $$2.add((Object)$$3.getRedirect());
        }
        return $$1;
    }

    private static List<Entry> createEntries(Object2IntMap<CommandNode<SharedSuggestionProvider>> $$0) {
        ObjectArrayList $$1 = new ObjectArrayList($$0.size());
        $$1.size($$0.size());
        for (Object2IntMap.Entry $$2 : Object2IntMaps.fastIterable($$0)) {
            $$1.set($$2.getIntValue(), (Object)ClientboundCommandsPacket.createEntry((CommandNode<SharedSuggestionProvider>)((CommandNode)$$2.getKey()), $$0));
        }
        return $$1;
    }

    private static Entry readNode(FriendlyByteBuf $$0) {
        byte $$1 = $$0.readByte();
        int[] $$2 = $$0.readVarIntArray();
        int $$3 = ($$1 & 8) != 0 ? $$0.readVarInt() : 0;
        NodeStub $$4 = ClientboundCommandsPacket.read($$0, $$1);
        return new Entry($$4, $$1, $$3, $$2);
    }

    @Nullable
    private static NodeStub read(FriendlyByteBuf $$0, byte $$1) {
        int $$2 = $$1 & 3;
        if ($$2 == 2) {
            String $$3 = $$0.readUtf();
            int $$4 = $$0.readVarInt();
            ArgumentTypeInfo $$5 = (ArgumentTypeInfo)BuiltInRegistries.COMMAND_ARGUMENT_TYPE.byId($$4);
            if ($$5 == null) {
                return null;
            }
            Object $$6 = $$5.deserializeFromNetwork($$0);
            ResourceLocation $$7 = ($$1 & 0x10) != 0 ? $$0.readResourceLocation() : null;
            return new ArgumentNodeStub($$3, (ArgumentTypeInfo.Template<?>)$$6, $$7);
        }
        if ($$2 == 1) {
            String $$8 = $$0.readUtf();
            return new LiteralNodeStub($$8);
        }
        return null;
    }

    /*
     * WARNING - void declaration
     */
    private static Entry createEntry(CommandNode<SharedSuggestionProvider> $$0, Object2IntMap<CommandNode<SharedSuggestionProvider>> $$1) {
        void $$10;
        int $$4;
        int $$2 = 0;
        if ($$0.getRedirect() != null) {
            $$2 |= 8;
            int $$3 = $$1.getInt((Object)$$0.getRedirect());
        } else {
            $$4 = 0;
        }
        if ($$0.getCommand() != null) {
            $$2 |= 4;
        }
        if ($$0 instanceof RootCommandNode) {
            $$2 |= 0;
            Object $$5 = null;
        } else if ($$0 instanceof ArgumentCommandNode) {
            ArgumentCommandNode $$6 = (ArgumentCommandNode)$$0;
            ArgumentNodeStub $$7 = new ArgumentNodeStub($$6);
            $$2 |= 2;
            if ($$6.getCustomSuggestions() != null) {
                $$2 |= 0x10;
            }
        } else if ($$0 instanceof LiteralCommandNode) {
            LiteralCommandNode $$8 = (LiteralCommandNode)$$0;
            LiteralNodeStub $$9 = new LiteralNodeStub($$8.getLiteral());
            $$2 |= 1;
        } else {
            throw new UnsupportedOperationException("Unknown node type " + $$0);
        }
        int[] $$11 = $$0.getChildren().stream().mapToInt(arg_0 -> $$1.getInt(arg_0)).toArray();
        return new Entry((NodeStub)$$10, $$2, $$4, $$11);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleCommands(this);
    }

    public RootCommandNode<SharedSuggestionProvider> getRoot(CommandBuildContext $$0) {
        return (RootCommandNode)new NodeResolver($$0, this.entries).resolve(this.rootIndex);
    }

    private static /* synthetic */ boolean lambda$validateEntries$1(BiPredicate $$0, List $$1, IntSet $$2, int $$3) {
        return $$0.test((Object)((Entry)$$1.get($$3)), (Object)$$2);
    }

    static class Entry {
        @Nullable
        final NodeStub stub;
        final int flags;
        final int redirect;
        final int[] children;

        Entry(@Nullable NodeStub $$0, int $$1, int $$2, int[] $$3) {
            this.stub = $$0;
            this.flags = $$1;
            this.redirect = $$2;
            this.children = $$3;
        }

        public void write(FriendlyByteBuf $$0) {
            $$0.writeByte(this.flags);
            $$0.writeVarIntArray(this.children);
            if ((this.flags & 8) != 0) {
                $$0.writeVarInt(this.redirect);
            }
            if (this.stub != null) {
                this.stub.write($$0);
            }
        }

        public boolean canBuild(IntSet $$0) {
            if ((this.flags & 8) != 0) {
                return !$$0.contains(this.redirect);
            }
            return true;
        }

        public boolean canResolve(IntSet $$0) {
            for (int $$1 : this.children) {
                if (!$$0.contains($$1)) continue;
                return false;
            }
            return true;
        }
    }

    static interface NodeStub {
        public ArgumentBuilder<SharedSuggestionProvider, ?> build(CommandBuildContext var1);

        public void write(FriendlyByteBuf var1);
    }

    static class ArgumentNodeStub
    implements NodeStub {
        private final String id;
        private final ArgumentTypeInfo.Template<?> argumentType;
        @Nullable
        private final ResourceLocation suggestionId;

        @Nullable
        private static ResourceLocation getSuggestionId(@Nullable SuggestionProvider<SharedSuggestionProvider> $$0) {
            return $$0 != null ? SuggestionProviders.getName($$0) : null;
        }

        ArgumentNodeStub(String $$0, ArgumentTypeInfo.Template<?> $$1, @Nullable ResourceLocation $$2) {
            this.id = $$0;
            this.argumentType = $$1;
            this.suggestionId = $$2;
        }

        public ArgumentNodeStub(ArgumentCommandNode<SharedSuggestionProvider, ?> $$0) {
            this($$0.getName(), ArgumentTypeInfos.unpack($$0.getType()), ArgumentNodeStub.getSuggestionId((SuggestionProvider<SharedSuggestionProvider>)$$0.getCustomSuggestions()));
        }

        @Override
        public ArgumentBuilder<SharedSuggestionProvider, ?> build(CommandBuildContext $$0) {
            Object $$1 = this.argumentType.instantiate($$0);
            RequiredArgumentBuilder $$2 = RequiredArgumentBuilder.argument((String)this.id, $$1);
            if (this.suggestionId != null) {
                $$2.suggests(SuggestionProviders.getProvider(this.suggestionId));
            }
            return $$2;
        }

        @Override
        public void write(FriendlyByteBuf $$0) {
            $$0.writeUtf(this.id);
            ArgumentNodeStub.serializeCap($$0, this.argumentType);
            if (this.suggestionId != null) {
                $$0.writeResourceLocation(this.suggestionId);
            }
        }

        private static <A extends ArgumentType<?>> void serializeCap(FriendlyByteBuf $$0, ArgumentTypeInfo.Template<A> $$1) {
            ArgumentNodeStub.serializeCap($$0, $$1.type(), $$1);
        }

        private static <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> void serializeCap(FriendlyByteBuf $$0, ArgumentTypeInfo<A, T> $$1, ArgumentTypeInfo.Template<A> $$2) {
            $$0.writeVarInt(BuiltInRegistries.COMMAND_ARGUMENT_TYPE.getId($$1));
            $$1.serializeToNetwork($$2, $$0);
        }
    }

    static class LiteralNodeStub
    implements NodeStub {
        private final String id;

        LiteralNodeStub(String $$0) {
            this.id = $$0;
        }

        @Override
        public ArgumentBuilder<SharedSuggestionProvider, ?> build(CommandBuildContext $$0) {
            return LiteralArgumentBuilder.literal((String)this.id);
        }

        @Override
        public void write(FriendlyByteBuf $$0) {
            $$0.writeUtf(this.id);
        }
    }

    static class NodeResolver {
        private final CommandBuildContext context;
        private final List<Entry> entries;
        private final List<CommandNode<SharedSuggestionProvider>> nodes;

        NodeResolver(CommandBuildContext $$0, List<Entry> $$1) {
            this.context = $$0;
            this.entries = $$1;
            ObjectArrayList $$2 = new ObjectArrayList();
            $$2.size($$1.size());
            this.nodes = $$2;
        }

        public CommandNode<SharedSuggestionProvider> resolve(int $$02) {
            CommandNode $$5;
            CommandNode $$1 = (CommandNode)this.nodes.get($$02);
            if ($$1 != null) {
                return $$1;
            }
            Entry $$2 = (Entry)this.entries.get($$02);
            if ($$2.stub == null) {
                RootCommandNode $$3 = new RootCommandNode();
            } else {
                ArgumentBuilder<SharedSuggestionProvider, ?> $$4 = $$2.stub.build(this.context);
                if (($$2.flags & 8) != 0) {
                    $$4.redirect(this.resolve($$2.redirect));
                }
                if (($$2.flags & 4) != 0) {
                    $$4.executes($$0 -> 0);
                }
                $$5 = $$4.build();
            }
            this.nodes.set($$02, (Object)$$5);
            for (int $$6 : $$2.children) {
                CommandNode<SharedSuggestionProvider> $$7 = this.resolve($$6);
                if ($$7 instanceof RootCommandNode) continue;
                $$5.addChild($$7);
            }
            return $$5;
        }
    }
}