/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  java.lang.IndexOutOfBoundsException
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.Arrays
 *  java.util.Collection
 *  java.util.Collections
 *  java.util.Iterator
 *  java.util.List
 *  java.util.function.BiConsumer
 *  java.util.function.Function
 *  java.util.function.Predicate
 *  java.util.function.Supplier
 *  org.apache.commons.lang3.mutable.MutableBoolean
 */
package net.minecraft.commands.arguments;

import com.google.common.collect.Lists;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class NbtPathArgument
implements ArgumentType<NbtPath> {
    private static final Collection<String> EXAMPLES = Arrays.asList((Object[])new String[]{"foo", "foo.bar", "foo[0]", "[0]", "[]", "{foo=bar}"});
    public static final SimpleCommandExceptionType ERROR_INVALID_NODE = new SimpleCommandExceptionType((Message)Component.translatable("arguments.nbtpath.node.invalid"));
    public static final SimpleCommandExceptionType ERROR_DATA_TOO_DEEP = new SimpleCommandExceptionType((Message)Component.translatable("arguments.nbtpath.too_deep"));
    public static final DynamicCommandExceptionType ERROR_NOTHING_FOUND = new DynamicCommandExceptionType($$0 -> Component.translatable("arguments.nbtpath.nothing_found", $$0));
    static final DynamicCommandExceptionType ERROR_EXPECTED_LIST = new DynamicCommandExceptionType($$0 -> Component.translatable("commands.data.modify.expected_list", $$0));
    static final DynamicCommandExceptionType ERROR_INVALID_INDEX = new DynamicCommandExceptionType($$0 -> Component.translatable("commands.data.modify.invalid_index", $$0));
    private static final char INDEX_MATCH_START = '[';
    private static final char INDEX_MATCH_END = ']';
    private static final char KEY_MATCH_START = '{';
    private static final char KEY_MATCH_END = '}';
    private static final char QUOTED_KEY_START = '\"';

    public static NbtPathArgument nbtPath() {
        return new NbtPathArgument();
    }

    public static NbtPath getPath(CommandContext<CommandSourceStack> $$0, String $$1) {
        return (NbtPath)$$0.getArgument($$1, NbtPath.class);
    }

    public NbtPath parse(StringReader $$0) throws CommandSyntaxException {
        ArrayList $$1 = Lists.newArrayList();
        int $$2 = $$0.getCursor();
        Object2IntOpenHashMap $$3 = new Object2IntOpenHashMap();
        boolean $$4 = true;
        while ($$0.canRead() && $$0.peek() != ' ') {
            char $$6;
            Node $$5 = NbtPathArgument.parseNode($$0, $$4);
            $$1.add((Object)$$5);
            $$3.put((Object)$$5, $$0.getCursor() - $$2);
            $$4 = false;
            if (!$$0.canRead() || ($$6 = $$0.peek()) == ' ' || $$6 == '[' || $$6 == '{') continue;
            $$0.expect('.');
        }
        return new NbtPath($$0.getString().substring($$2, $$0.getCursor()), (Node[])$$1.toArray((Object[])new Node[0]), (Object2IntMap<Node>)$$3);
    }

    private static Node parseNode(StringReader $$0, boolean $$1) throws CommandSyntaxException {
        switch ($$0.peek()) {
            case '{': {
                if (!$$1) {
                    throw ERROR_INVALID_NODE.createWithContext((ImmutableStringReader)$$0);
                }
                CompoundTag $$2 = new TagParser($$0).readStruct();
                return new MatchRootObjectNode($$2);
            }
            case '[': {
                $$0.skip();
                char $$3 = $$0.peek();
                if ($$3 == '{') {
                    CompoundTag $$4 = new TagParser($$0).readStruct();
                    $$0.expect(']');
                    return new MatchElementNode($$4);
                }
                if ($$3 == ']') {
                    $$0.skip();
                    return AllElementsNode.INSTANCE;
                }
                int $$5 = $$0.readInt();
                $$0.expect(']');
                return new IndexedElementNode($$5);
            }
            case '\"': {
                String $$6 = $$0.readString();
                return NbtPathArgument.readObjectNode($$0, $$6);
            }
        }
        String $$7 = NbtPathArgument.readUnquotedName($$0);
        return NbtPathArgument.readObjectNode($$0, $$7);
    }

    private static Node readObjectNode(StringReader $$0, String $$1) throws CommandSyntaxException {
        if ($$0.canRead() && $$0.peek() == '{') {
            CompoundTag $$2 = new TagParser($$0).readStruct();
            return new MatchObjectNode($$1, $$2);
        }
        return new CompoundChildNode($$1);
    }

    private static String readUnquotedName(StringReader $$0) throws CommandSyntaxException {
        int $$1 = $$0.getCursor();
        while ($$0.canRead() && NbtPathArgument.isAllowedInUnquotedName($$0.peek())) {
            $$0.skip();
        }
        if ($$0.getCursor() == $$1) {
            throw ERROR_INVALID_NODE.createWithContext((ImmutableStringReader)$$0);
        }
        return $$0.getString().substring($$1, $$0.getCursor());
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    private static boolean isAllowedInUnquotedName(char $$0) {
        return $$0 != ' ' && $$0 != '\"' && $$0 != '[' && $$0 != ']' && $$0 != '.' && $$0 != '{' && $$0 != '}';
    }

    static Predicate<Tag> createTagPredicate(CompoundTag $$0) {
        return $$1 -> NbtUtils.compareNbt($$0, $$1, true);
    }

    public static class NbtPath {
        private final String original;
        private final Object2IntMap<Node> nodeToOriginalPosition;
        private final Node[] nodes;

        public NbtPath(String $$0, Node[] $$1, Object2IntMap<Node> $$2) {
            this.original = $$0;
            this.nodes = $$1;
            this.nodeToOriginalPosition = $$2;
        }

        public List<Tag> get(Tag $$0) throws CommandSyntaxException {
            List<Tag> $$1 = Collections.singletonList((Object)$$0);
            for (Node $$2 : this.nodes) {
                if (!($$1 = $$2.get($$1)).isEmpty()) continue;
                throw this.createNotFoundException($$2);
            }
            return $$1;
        }

        public int countMatching(Tag $$0) {
            List<Tag> $$1 = Collections.singletonList((Object)$$0);
            for (Node $$2 : this.nodes) {
                if (!($$1 = $$2.get($$1)).isEmpty()) continue;
                return 0;
            }
            return $$1.size();
        }

        private List<Tag> getOrCreateParents(Tag $$0) throws CommandSyntaxException {
            List<Tag> $$1 = Collections.singletonList((Object)$$0);
            for (int $$2 = 0; $$2 < this.nodes.length - 1; ++$$2) {
                Node $$3 = this.nodes[$$2];
                int $$4 = $$2 + 1;
                if (!($$1 = $$3.getOrCreate($$1, (Supplier<Tag>)((Supplier)this.nodes[$$4]::createPreferredParentTag))).isEmpty()) continue;
                throw this.createNotFoundException($$3);
            }
            return $$1;
        }

        public List<Tag> getOrCreate(Tag $$0, Supplier<Tag> $$1) throws CommandSyntaxException {
            List<Tag> $$2 = this.getOrCreateParents($$0);
            Node $$3 = this.nodes[this.nodes.length - 1];
            return $$3.getOrCreate($$2, $$1);
        }

        private static int apply(List<Tag> $$02, Function<Tag, Integer> $$12) {
            return (Integer)$$02.stream().map($$12).reduce((Object)0, ($$0, $$1) -> $$0 + $$1);
        }

        public static boolean isTooDeep(Tag $$0, int $$1) {
            block4: {
                block3: {
                    if ($$1 >= 512) {
                        return true;
                    }
                    if (!($$0 instanceof CompoundTag)) break block3;
                    CompoundTag $$2 = (CompoundTag)$$0;
                    for (String $$3 : $$2.getAllKeys()) {
                        Tag $$4 = $$2.get($$3);
                        if ($$4 == null || !NbtPath.isTooDeep($$4, $$1 + 1)) continue;
                        return true;
                    }
                    break block4;
                }
                if (!($$0 instanceof ListTag)) break block4;
                ListTag $$5 = (ListTag)$$0;
                Iterator iterator = $$5.iterator();
                while (iterator.hasNext()) {
                    Tag $$6 = (Tag)iterator.next();
                    if (!NbtPath.isTooDeep($$6, $$1 + 1)) continue;
                    return true;
                }
            }
            return false;
        }

        public int set(Tag $$0, Tag $$1) throws CommandSyntaxException {
            if (NbtPath.isTooDeep($$1, this.estimatePathDepth())) {
                throw ERROR_DATA_TOO_DEEP.create();
            }
            Tag $$2 = $$1.copy();
            List<Tag> $$32 = this.getOrCreateParents($$0);
            if ($$32.isEmpty()) {
                return 0;
            }
            Node $$4 = this.nodes[this.nodes.length - 1];
            MutableBoolean $$5 = new MutableBoolean(false);
            return NbtPath.apply($$32, (Function<Tag, Integer>)((Function)$$3 -> $$4.setTag((Tag)$$3, (Supplier<Tag>)((Supplier)() -> {
                if ($$5.isFalse()) {
                    $$5.setTrue();
                    return $$2;
                }
                return $$2.copy();
            }))));
        }

        private int estimatePathDepth() {
            return this.nodes.length;
        }

        public int insert(int $$0, CompoundTag $$1, List<Tag> $$2) throws CommandSyntaxException {
            ArrayList $$3 = new ArrayList($$2.size());
            for (Tag $$4 : $$2) {
                Tag $$5 = $$4.copy();
                $$3.add((Object)$$5);
                if (!NbtPath.isTooDeep($$5, this.estimatePathDepth())) continue;
                throw ERROR_DATA_TOO_DEEP.create();
            }
            List<Tag> $$6 = this.getOrCreate($$1, (Supplier<Tag>)((Supplier)ListTag::new));
            int $$7 = 0;
            boolean $$8 = false;
            for (Tag $$9 : $$6) {
                if (!($$9 instanceof CollectionTag)) {
                    throw ERROR_EXPECTED_LIST.create((Object)$$9);
                }
                CollectionTag $$10 = (CollectionTag)$$9;
                boolean $$11 = false;
                int $$12 = $$0 < 0 ? $$10.size() + $$0 + 1 : $$0;
                for (Tag $$13 : $$3) {
                    try {
                        if (!$$10.addTag($$12, $$8 ? $$13.copy() : $$13)) continue;
                        ++$$12;
                        $$11 = true;
                    }
                    catch (IndexOutOfBoundsException $$14) {
                        throw ERROR_INVALID_INDEX.create((Object)$$12);
                    }
                }
                $$8 = true;
                $$7 += $$11 ? 1 : 0;
            }
            return $$7;
        }

        public int remove(Tag $$0) {
            List<Tag> $$1 = Collections.singletonList((Object)$$0);
            for (int $$2 = 0; $$2 < this.nodes.length - 1; ++$$2) {
                $$1 = this.nodes[$$2].get($$1);
            }
            Node $$3 = this.nodes[this.nodes.length - 1];
            return NbtPath.apply($$1, (Function<Tag, Integer>)((Function)$$3::removeTag));
        }

        private CommandSyntaxException createNotFoundException(Node $$0) {
            int $$1 = this.nodeToOriginalPosition.getInt((Object)$$0);
            return ERROR_NOTHING_FOUND.create((Object)this.original.substring(0, $$1));
        }

        public String toString() {
            return this.original;
        }
    }

    static interface Node {
        public void getTag(Tag var1, List<Tag> var2);

        public void getOrCreateTag(Tag var1, Supplier<Tag> var2, List<Tag> var3);

        public Tag createPreferredParentTag();

        public int setTag(Tag var1, Supplier<Tag> var2);

        public int removeTag(Tag var1);

        default public List<Tag> get(List<Tag> $$0) {
            return this.collect($$0, (BiConsumer<Tag, List<Tag>>)((BiConsumer)this::getTag));
        }

        default public List<Tag> getOrCreate(List<Tag> $$0, Supplier<Tag> $$12) {
            return this.collect($$0, (BiConsumer<Tag, List<Tag>>)((BiConsumer)($$1, $$2) -> this.getOrCreateTag((Tag)$$1, $$12, (List<Tag>)$$2)));
        }

        default public List<Tag> collect(List<Tag> $$0, BiConsumer<Tag, List<Tag>> $$1) {
            ArrayList $$2 = Lists.newArrayList();
            for (Tag $$3 : $$0) {
                $$1.accept((Object)$$3, (Object)$$2);
            }
            return $$2;
        }
    }

    static class MatchRootObjectNode
    implements Node {
        private final Predicate<Tag> predicate;

        public MatchRootObjectNode(CompoundTag $$0) {
            this.predicate = NbtPathArgument.createTagPredicate($$0);
        }

        @Override
        public void getTag(Tag $$0, List<Tag> $$1) {
            if ($$0 instanceof CompoundTag && this.predicate.test((Object)$$0)) {
                $$1.add((Object)$$0);
            }
        }

        @Override
        public void getOrCreateTag(Tag $$0, Supplier<Tag> $$1, List<Tag> $$2) {
            this.getTag($$0, $$2);
        }

        @Override
        public Tag createPreferredParentTag() {
            return new CompoundTag();
        }

        @Override
        public int setTag(Tag $$0, Supplier<Tag> $$1) {
            return 0;
        }

        @Override
        public int removeTag(Tag $$0) {
            return 0;
        }
    }

    static class MatchElementNode
    implements Node {
        private final CompoundTag pattern;
        private final Predicate<Tag> predicate;

        public MatchElementNode(CompoundTag $$0) {
            this.pattern = $$0;
            this.predicate = NbtPathArgument.createTagPredicate($$0);
        }

        @Override
        public void getTag(Tag $$0, List<Tag> $$1) {
            if ($$0 instanceof ListTag) {
                ListTag $$2 = (ListTag)$$0;
                $$2.stream().filter(this.predicate).forEach(arg_0 -> $$1.add(arg_0));
            }
        }

        @Override
        public void getOrCreateTag(Tag $$0, Supplier<Tag> $$1, List<Tag> $$22) {
            MutableBoolean $$3 = new MutableBoolean();
            if ($$0 instanceof ListTag) {
                ListTag $$4 = (ListTag)$$0;
                $$4.stream().filter(this.predicate).forEach($$2 -> {
                    $$22.add($$2);
                    $$3.setTrue();
                });
                if ($$3.isFalse()) {
                    CompoundTag $$5 = this.pattern.copy();
                    $$4.add($$5);
                    $$22.add((Object)$$5);
                }
            }
        }

        @Override
        public Tag createPreferredParentTag() {
            return new ListTag();
        }

        @Override
        public int setTag(Tag $$0, Supplier<Tag> $$1) {
            int $$2 = 0;
            if ($$0 instanceof ListTag) {
                ListTag $$3 = (ListTag)$$0;
                int $$4 = $$3.size();
                if ($$4 == 0) {
                    $$3.add((Tag)$$1.get());
                    ++$$2;
                } else {
                    for (int $$5 = 0; $$5 < $$4; ++$$5) {
                        Tag $$7;
                        Tag $$6 = $$3.get($$5);
                        if (!this.predicate.test((Object)$$6) || ($$7 = (Tag)$$1.get()).equals($$6) || !$$3.setTag($$5, $$7)) continue;
                        ++$$2;
                    }
                }
            }
            return $$2;
        }

        @Override
        public int removeTag(Tag $$0) {
            int $$1 = 0;
            if ($$0 instanceof ListTag) {
                ListTag $$2 = (ListTag)$$0;
                for (int $$3 = $$2.size() - 1; $$3 >= 0; --$$3) {
                    if (!this.predicate.test((Object)$$2.get($$3))) continue;
                    $$2.remove($$3);
                    ++$$1;
                }
            }
            return $$1;
        }
    }

    static class AllElementsNode
    implements Node {
        public static final AllElementsNode INSTANCE = new AllElementsNode();

        private AllElementsNode() {
        }

        @Override
        public void getTag(Tag $$0, List<Tag> $$1) {
            if ($$0 instanceof CollectionTag) {
                $$1.addAll((Collection)((CollectionTag)$$0));
            }
        }

        @Override
        public void getOrCreateTag(Tag $$0, Supplier<Tag> $$1, List<Tag> $$2) {
            if ($$0 instanceof CollectionTag) {
                CollectionTag $$3 = (CollectionTag)$$0;
                if ($$3.isEmpty()) {
                    Tag $$4 = (Tag)$$1.get();
                    if ($$3.addTag(0, $$4)) {
                        $$2.add((Object)$$4);
                    }
                } else {
                    $$2.addAll((Collection)$$3);
                }
            }
        }

        @Override
        public Tag createPreferredParentTag() {
            return new ListTag();
        }

        @Override
        public int setTag(Tag $$0, Supplier<Tag> $$1) {
            if ($$0 instanceof CollectionTag) {
                CollectionTag $$2 = (CollectionTag)$$0;
                int $$3 = $$2.size();
                if ($$3 == 0) {
                    $$2.addTag(0, (Tag)$$1.get());
                    return 1;
                }
                Tag $$4 = (Tag)$$1.get();
                int $$5 = $$3 - (int)$$2.stream().filter(arg_0 -> ((Tag)$$4).equals(arg_0)).count();
                if ($$5 == 0) {
                    return 0;
                }
                $$2.clear();
                if (!$$2.addTag(0, $$4)) {
                    return 0;
                }
                for (int $$6 = 1; $$6 < $$3; ++$$6) {
                    $$2.addTag($$6, (Tag)$$1.get());
                }
                return $$5;
            }
            return 0;
        }

        @Override
        public int removeTag(Tag $$0) {
            CollectionTag $$1;
            int $$2;
            if ($$0 instanceof CollectionTag && ($$2 = ($$1 = (CollectionTag)$$0).size()) > 0) {
                $$1.clear();
                return $$2;
            }
            return 0;
        }
    }

    static class IndexedElementNode
    implements Node {
        private final int index;

        public IndexedElementNode(int $$0) {
            this.index = $$0;
        }

        @Override
        public void getTag(Tag $$0, List<Tag> $$1) {
            if ($$0 instanceof CollectionTag) {
                int $$4;
                CollectionTag $$2 = (CollectionTag)$$0;
                int $$3 = $$2.size();
                int n = $$4 = this.index < 0 ? $$3 + this.index : this.index;
                if (0 <= $$4 && $$4 < $$3) {
                    $$1.add((Object)((Tag)$$2.get($$4)));
                }
            }
        }

        @Override
        public void getOrCreateTag(Tag $$0, Supplier<Tag> $$1, List<Tag> $$2) {
            this.getTag($$0, $$2);
        }

        @Override
        public Tag createPreferredParentTag() {
            return new ListTag();
        }

        @Override
        public int setTag(Tag $$0, Supplier<Tag> $$1) {
            if ($$0 instanceof CollectionTag) {
                int $$4;
                CollectionTag $$2 = (CollectionTag)$$0;
                int $$3 = $$2.size();
                int n = $$4 = this.index < 0 ? $$3 + this.index : this.index;
                if (0 <= $$4 && $$4 < $$3) {
                    Tag $$5 = (Tag)$$2.get($$4);
                    Tag $$6 = (Tag)$$1.get();
                    if (!$$6.equals($$5) && $$2.setTag($$4, $$6)) {
                        return 1;
                    }
                }
            }
            return 0;
        }

        @Override
        public int removeTag(Tag $$0) {
            if ($$0 instanceof CollectionTag) {
                int $$3;
                CollectionTag $$1 = (CollectionTag)$$0;
                int $$2 = $$1.size();
                int n = $$3 = this.index < 0 ? $$2 + this.index : this.index;
                if (0 <= $$3 && $$3 < $$2) {
                    $$1.remove($$3);
                    return 1;
                }
            }
            return 0;
        }
    }

    static class MatchObjectNode
    implements Node {
        private final String name;
        private final CompoundTag pattern;
        private final Predicate<Tag> predicate;

        public MatchObjectNode(String $$0, CompoundTag $$1) {
            this.name = $$0;
            this.pattern = $$1;
            this.predicate = NbtPathArgument.createTagPredicate($$1);
        }

        @Override
        public void getTag(Tag $$0, List<Tag> $$1) {
            Tag $$2;
            if ($$0 instanceof CompoundTag && this.predicate.test((Object)($$2 = ((CompoundTag)$$0).get(this.name)))) {
                $$1.add((Object)$$2);
            }
        }

        @Override
        public void getOrCreateTag(Tag $$0, Supplier<Tag> $$1, List<Tag> $$2) {
            if ($$0 instanceof CompoundTag) {
                CompoundTag $$3 = (CompoundTag)$$0;
                Tag $$4 = $$3.get(this.name);
                if ($$4 == null) {
                    $$4 = this.pattern.copy();
                    $$3.put(this.name, $$4);
                    $$2.add((Object)$$4);
                } else if (this.predicate.test((Object)$$4)) {
                    $$2.add((Object)$$4);
                }
            }
        }

        @Override
        public Tag createPreferredParentTag() {
            return new CompoundTag();
        }

        @Override
        public int setTag(Tag $$0, Supplier<Tag> $$1) {
            Tag $$4;
            CompoundTag $$2;
            Tag $$3;
            if ($$0 instanceof CompoundTag && this.predicate.test((Object)($$3 = ($$2 = (CompoundTag)$$0).get(this.name))) && !($$4 = (Tag)$$1.get()).equals($$3)) {
                $$2.put(this.name, $$4);
                return 1;
            }
            return 0;
        }

        @Override
        public int removeTag(Tag $$0) {
            CompoundTag $$1;
            Tag $$2;
            if ($$0 instanceof CompoundTag && this.predicate.test((Object)($$2 = ($$1 = (CompoundTag)$$0).get(this.name)))) {
                $$1.remove(this.name);
                return 1;
            }
            return 0;
        }
    }

    static class CompoundChildNode
    implements Node {
        private final String name;

        public CompoundChildNode(String $$0) {
            this.name = $$0;
        }

        @Override
        public void getTag(Tag $$0, List<Tag> $$1) {
            Tag $$2;
            if ($$0 instanceof CompoundTag && ($$2 = ((CompoundTag)$$0).get(this.name)) != null) {
                $$1.add((Object)$$2);
            }
        }

        @Override
        public void getOrCreateTag(Tag $$0, Supplier<Tag> $$1, List<Tag> $$2) {
            if ($$0 instanceof CompoundTag) {
                Tag $$5;
                CompoundTag $$3 = (CompoundTag)$$0;
                if ($$3.contains(this.name)) {
                    Tag $$4 = $$3.get(this.name);
                } else {
                    $$5 = (Tag)$$1.get();
                    $$3.put(this.name, $$5);
                }
                $$2.add((Object)$$5);
            }
        }

        @Override
        public Tag createPreferredParentTag() {
            return new CompoundTag();
        }

        @Override
        public int setTag(Tag $$0, Supplier<Tag> $$1) {
            if ($$0 instanceof CompoundTag) {
                Tag $$4;
                CompoundTag $$2 = (CompoundTag)$$0;
                Tag $$3 = (Tag)$$1.get();
                if (!$$3.equals($$4 = $$2.put(this.name, $$3))) {
                    return 1;
                }
            }
            return 0;
        }

        @Override
        public int removeTag(Tag $$0) {
            CompoundTag $$1;
            if ($$0 instanceof CompoundTag && ($$1 = (CompoundTag)$$0).contains(this.name)) {
                $$1.remove(this.name);
                return 1;
            }
            return 0;
        }
    }
}