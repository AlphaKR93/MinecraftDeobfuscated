/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.google.common.collect.Lists
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.bytes.ByteCollection
 *  it.unimi.dsi.fastutil.bytes.ByteOpenHashSet
 *  java.lang.Byte
 *  java.lang.CharSequence
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.Arrays
 *  java.util.Collection
 *  java.util.Collections
 *  java.util.Iterator
 *  java.util.List
 *  java.util.regex.Pattern
 *  org.slf4j.Logger
 */
package net.minecraft.nbt;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteOpenHashSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagVisitor;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.slf4j.Logger;

public class TextComponentTagVisitor
implements TagVisitor {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int INLINE_LIST_THRESHOLD = 8;
    private static final ByteCollection INLINE_ELEMENT_TYPES = new ByteOpenHashSet((Collection)Arrays.asList((Object[])new Byte[]{(byte)1, (byte)2, (byte)3, (byte)4, (byte)5, (byte)6}));
    private static final ChatFormatting SYNTAX_HIGHLIGHTING_KEY = ChatFormatting.AQUA;
    private static final ChatFormatting SYNTAX_HIGHLIGHTING_STRING = ChatFormatting.GREEN;
    private static final ChatFormatting SYNTAX_HIGHLIGHTING_NUMBER = ChatFormatting.GOLD;
    private static final ChatFormatting SYNTAX_HIGHLIGHTING_NUMBER_TYPE = ChatFormatting.RED;
    private static final Pattern SIMPLE_VALUE = Pattern.compile((String)"[A-Za-z0-9._+-]+");
    private static final String NAME_VALUE_SEPARATOR = String.valueOf((char)':');
    private static final String ELEMENT_SEPARATOR = String.valueOf((char)',');
    private static final String LIST_OPEN = "[";
    private static final String LIST_CLOSE = "]";
    private static final String LIST_TYPE_SEPARATOR = ";";
    private static final String ELEMENT_SPACING = " ";
    private static final String STRUCT_OPEN = "{";
    private static final String STRUCT_CLOSE = "}";
    private static final String NEWLINE = "\n";
    private final String indentation;
    private final int depth;
    private Component result = CommonComponents.EMPTY;

    public TextComponentTagVisitor(String $$0, int $$1) {
        this.indentation = $$0;
        this.depth = $$1;
    }

    public Component visit(Tag $$0) {
        $$0.accept(this);
        return this.result;
    }

    @Override
    public void visitString(StringTag $$0) {
        String $$1 = StringTag.quoteAndEscape($$0.getAsString());
        String $$2 = $$1.substring(0, 1);
        MutableComponent $$3 = Component.literal($$1.substring(1, $$1.length() - 1)).withStyle(SYNTAX_HIGHLIGHTING_STRING);
        this.result = Component.literal($$2).append($$3).append($$2);
    }

    @Override
    public void visitByte(ByteTag $$0) {
        MutableComponent $$1 = Component.literal("b").withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
        this.result = Component.literal(String.valueOf((Object)$$0.getAsNumber())).append($$1).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
    }

    @Override
    public void visitShort(ShortTag $$0) {
        MutableComponent $$1 = Component.literal("s").withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
        this.result = Component.literal(String.valueOf((Object)$$0.getAsNumber())).append($$1).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
    }

    @Override
    public void visitInt(IntTag $$0) {
        this.result = Component.literal(String.valueOf((Object)$$0.getAsNumber())).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
    }

    @Override
    public void visitLong(LongTag $$0) {
        MutableComponent $$1 = Component.literal("L").withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
        this.result = Component.literal(String.valueOf((Object)$$0.getAsNumber())).append($$1).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
    }

    @Override
    public void visitFloat(FloatTag $$0) {
        MutableComponent $$1 = Component.literal("f").withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
        this.result = Component.literal(String.valueOf((float)$$0.getAsFloat())).append($$1).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
    }

    @Override
    public void visitDouble(DoubleTag $$0) {
        MutableComponent $$1 = Component.literal("d").withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
        this.result = Component.literal(String.valueOf((double)$$0.getAsDouble())).append($$1).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
    }

    @Override
    public void visitByteArray(ByteArrayTag $$0) {
        MutableComponent $$1 = Component.literal("B").withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
        MutableComponent $$2 = Component.literal(LIST_OPEN).append($$1).append(LIST_TYPE_SEPARATOR);
        byte[] $$3 = $$0.getAsByteArray();
        for (int $$4 = 0; $$4 < $$3.length; ++$$4) {
            MutableComponent $$5 = Component.literal(String.valueOf((int)$$3[$$4])).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
            $$2.append(ELEMENT_SPACING).append($$5).append($$1);
            if ($$4 == $$3.length - 1) continue;
            $$2.append(ELEMENT_SEPARATOR);
        }
        $$2.append(LIST_CLOSE);
        this.result = $$2;
    }

    @Override
    public void visitIntArray(IntArrayTag $$0) {
        MutableComponent $$1 = Component.literal("I").withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
        MutableComponent $$2 = Component.literal(LIST_OPEN).append($$1).append(LIST_TYPE_SEPARATOR);
        int[] $$3 = $$0.getAsIntArray();
        for (int $$4 = 0; $$4 < $$3.length; ++$$4) {
            $$2.append(ELEMENT_SPACING).append(Component.literal(String.valueOf((int)$$3[$$4])).withStyle(SYNTAX_HIGHLIGHTING_NUMBER));
            if ($$4 == $$3.length - 1) continue;
            $$2.append(ELEMENT_SEPARATOR);
        }
        $$2.append(LIST_CLOSE);
        this.result = $$2;
    }

    @Override
    public void visitLongArray(LongArrayTag $$0) {
        MutableComponent $$1 = Component.literal("L").withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
        MutableComponent $$2 = Component.literal(LIST_OPEN).append($$1).append(LIST_TYPE_SEPARATOR);
        long[] $$3 = $$0.getAsLongArray();
        for (int $$4 = 0; $$4 < $$3.length; ++$$4) {
            MutableComponent $$5 = Component.literal(String.valueOf((long)$$3[$$4])).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
            $$2.append(ELEMENT_SPACING).append($$5).append($$1);
            if ($$4 == $$3.length - 1) continue;
            $$2.append(ELEMENT_SEPARATOR);
        }
        $$2.append(LIST_CLOSE);
        this.result = $$2;
    }

    @Override
    public void visitList(ListTag $$0) {
        if ($$0.isEmpty()) {
            this.result = Component.literal("[]");
            return;
        }
        if (INLINE_ELEMENT_TYPES.contains($$0.getElementType()) && $$0.size() <= 8) {
            String $$1 = ELEMENT_SEPARATOR + ELEMENT_SPACING;
            MutableComponent $$2 = Component.literal(LIST_OPEN);
            for (int $$3 = 0; $$3 < $$0.size(); ++$$3) {
                if ($$3 != 0) {
                    $$2.append($$1);
                }
                $$2.append(new TextComponentTagVisitor(this.indentation, this.depth).visit($$0.get($$3)));
            }
            $$2.append(LIST_CLOSE);
            this.result = $$2;
            return;
        }
        MutableComponent $$4 = Component.literal(LIST_OPEN);
        if (!this.indentation.isEmpty()) {
            $$4.append(NEWLINE);
        }
        for (int $$5 = 0; $$5 < $$0.size(); ++$$5) {
            MutableComponent $$6 = Component.literal(Strings.repeat((String)this.indentation, (int)(this.depth + 1)));
            $$6.append(new TextComponentTagVisitor(this.indentation, this.depth + 1).visit($$0.get($$5)));
            if ($$5 != $$0.size() - 1) {
                $$6.append(ELEMENT_SEPARATOR).append(this.indentation.isEmpty() ? ELEMENT_SPACING : NEWLINE);
            }
            $$4.append($$6);
        }
        if (!this.indentation.isEmpty()) {
            $$4.append(NEWLINE).append(Strings.repeat((String)this.indentation, (int)this.depth));
        }
        $$4.append(LIST_CLOSE);
        this.result = $$4;
    }

    @Override
    public void visitCompound(CompoundTag $$0) {
        if ($$0.isEmpty()) {
            this.result = Component.literal("{}");
            return;
        }
        MutableComponent $$1 = Component.literal(STRUCT_OPEN);
        ArrayList $$2 = $$0.getAllKeys();
        if (LOGGER.isDebugEnabled()) {
            ArrayList $$3 = Lists.newArrayList($$0.getAllKeys());
            Collections.sort((List)$$3);
            $$2 = $$3;
        }
        if (!this.indentation.isEmpty()) {
            $$1.append(NEWLINE);
        }
        Iterator $$4 = $$2.iterator();
        while ($$4.hasNext()) {
            String $$5 = (String)$$4.next();
            MutableComponent $$6 = Component.literal(Strings.repeat((String)this.indentation, (int)(this.depth + 1))).append(TextComponentTagVisitor.handleEscapePretty($$5)).append(NAME_VALUE_SEPARATOR).append(ELEMENT_SPACING).append(new TextComponentTagVisitor(this.indentation, this.depth + 1).visit($$0.get($$5)));
            if ($$4.hasNext()) {
                $$6.append(ELEMENT_SEPARATOR).append(this.indentation.isEmpty() ? ELEMENT_SPACING : NEWLINE);
            }
            $$1.append($$6);
        }
        if (!this.indentation.isEmpty()) {
            $$1.append(NEWLINE).append(Strings.repeat((String)this.indentation, (int)this.depth));
        }
        $$1.append(STRUCT_CLOSE);
        this.result = $$1;
    }

    protected static Component handleEscapePretty(String $$0) {
        if (SIMPLE_VALUE.matcher((CharSequence)$$0).matches()) {
            return Component.literal($$0).withStyle(SYNTAX_HIGHLIGHTING_KEY);
        }
        String $$1 = StringTag.quoteAndEscape($$0);
        String $$2 = $$1.substring(0, 1);
        MutableComponent $$3 = Component.literal($$1.substring(1, $$1.length() - 1)).withStyle(SYNTAX_HIGHLIGHTING_KEY);
        return Component.literal($$2).append($$3).append($$2);
    }

    @Override
    public void visitEnd(EndTag $$0) {
        this.result = CommonComponents.EMPTY;
    }
}