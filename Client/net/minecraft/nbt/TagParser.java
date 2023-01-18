/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  java.lang.Byte
 *  java.lang.CharSequence
 *  java.lang.Double
 *  java.lang.Float
 *  java.lang.Integer
 *  java.lang.Long
 *  java.lang.Number
 *  java.lang.NumberFormatException
 *  java.lang.Object
 *  java.lang.Short
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.regex.Pattern
 */
package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagType;
import net.minecraft.network.chat.Component;

public class TagParser {
    public static final SimpleCommandExceptionType ERROR_TRAILING_DATA = new SimpleCommandExceptionType((Message)Component.translatable("argument.nbt.trailing"));
    public static final SimpleCommandExceptionType ERROR_EXPECTED_KEY = new SimpleCommandExceptionType((Message)Component.translatable("argument.nbt.expected.key"));
    public static final SimpleCommandExceptionType ERROR_EXPECTED_VALUE = new SimpleCommandExceptionType((Message)Component.translatable("argument.nbt.expected.value"));
    public static final Dynamic2CommandExceptionType ERROR_INSERT_MIXED_LIST = new Dynamic2CommandExceptionType(($$0, $$1) -> Component.translatable("argument.nbt.list.mixed", $$0, $$1));
    public static final Dynamic2CommandExceptionType ERROR_INSERT_MIXED_ARRAY = new Dynamic2CommandExceptionType(($$0, $$1) -> Component.translatable("argument.nbt.array.mixed", $$0, $$1));
    public static final DynamicCommandExceptionType ERROR_INVALID_ARRAY = new DynamicCommandExceptionType($$0 -> Component.translatable("argument.nbt.array.invalid", $$0));
    public static final char ELEMENT_SEPARATOR = ',';
    public static final char NAME_VALUE_SEPARATOR = ':';
    private static final char LIST_OPEN = '[';
    private static final char LIST_CLOSE = ']';
    private static final char STRUCT_CLOSE = '}';
    private static final char STRUCT_OPEN = '{';
    private static final Pattern DOUBLE_PATTERN_NOSUFFIX = Pattern.compile((String)"[-+]?(?:[0-9]+[.]|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?", (int)2);
    private static final Pattern DOUBLE_PATTERN = Pattern.compile((String)"[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?d", (int)2);
    private static final Pattern FLOAT_PATTERN = Pattern.compile((String)"[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?f", (int)2);
    private static final Pattern BYTE_PATTERN = Pattern.compile((String)"[-+]?(?:0|[1-9][0-9]*)b", (int)2);
    private static final Pattern LONG_PATTERN = Pattern.compile((String)"[-+]?(?:0|[1-9][0-9]*)l", (int)2);
    private static final Pattern SHORT_PATTERN = Pattern.compile((String)"[-+]?(?:0|[1-9][0-9]*)s", (int)2);
    private static final Pattern INT_PATTERN = Pattern.compile((String)"[-+]?(?:0|[1-9][0-9]*)");
    private final StringReader reader;

    public static CompoundTag parseTag(String $$0) throws CommandSyntaxException {
        return new TagParser(new StringReader($$0)).readSingleStruct();
    }

    @VisibleForTesting
    CompoundTag readSingleStruct() throws CommandSyntaxException {
        CompoundTag $$0 = this.readStruct();
        this.reader.skipWhitespace();
        if (this.reader.canRead()) {
            throw ERROR_TRAILING_DATA.createWithContext((ImmutableStringReader)this.reader);
        }
        return $$0;
    }

    public TagParser(StringReader $$0) {
        this.reader = $$0;
    }

    protected String readKey() throws CommandSyntaxException {
        this.reader.skipWhitespace();
        if (!this.reader.canRead()) {
            throw ERROR_EXPECTED_KEY.createWithContext((ImmutableStringReader)this.reader);
        }
        return this.reader.readString();
    }

    protected Tag readTypedValue() throws CommandSyntaxException {
        this.reader.skipWhitespace();
        int $$0 = this.reader.getCursor();
        if (StringReader.isQuotedStringStart((char)this.reader.peek())) {
            return StringTag.valueOf(this.reader.readQuotedString());
        }
        String $$1 = this.reader.readUnquotedString();
        if ($$1.isEmpty()) {
            this.reader.setCursor($$0);
            throw ERROR_EXPECTED_VALUE.createWithContext((ImmutableStringReader)this.reader);
        }
        return this.type($$1);
    }

    private Tag type(String $$0) {
        try {
            if (FLOAT_PATTERN.matcher((CharSequence)$$0).matches()) {
                return FloatTag.valueOf(Float.parseFloat((String)$$0.substring(0, $$0.length() - 1)));
            }
            if (BYTE_PATTERN.matcher((CharSequence)$$0).matches()) {
                return ByteTag.valueOf(Byte.parseByte((String)$$0.substring(0, $$0.length() - 1)));
            }
            if (LONG_PATTERN.matcher((CharSequence)$$0).matches()) {
                return LongTag.valueOf(Long.parseLong((String)$$0.substring(0, $$0.length() - 1)));
            }
            if (SHORT_PATTERN.matcher((CharSequence)$$0).matches()) {
                return ShortTag.valueOf(Short.parseShort((String)$$0.substring(0, $$0.length() - 1)));
            }
            if (INT_PATTERN.matcher((CharSequence)$$0).matches()) {
                return IntTag.valueOf(Integer.parseInt((String)$$0));
            }
            if (DOUBLE_PATTERN.matcher((CharSequence)$$0).matches()) {
                return DoubleTag.valueOf(Double.parseDouble((String)$$0.substring(0, $$0.length() - 1)));
            }
            if (DOUBLE_PATTERN_NOSUFFIX.matcher((CharSequence)$$0).matches()) {
                return DoubleTag.valueOf(Double.parseDouble((String)$$0));
            }
            if ("true".equalsIgnoreCase($$0)) {
                return ByteTag.ONE;
            }
            if ("false".equalsIgnoreCase($$0)) {
                return ByteTag.ZERO;
            }
        }
        catch (NumberFormatException numberFormatException) {
            // empty catch block
        }
        return StringTag.valueOf($$0);
    }

    public Tag readValue() throws CommandSyntaxException {
        this.reader.skipWhitespace();
        if (!this.reader.canRead()) {
            throw ERROR_EXPECTED_VALUE.createWithContext((ImmutableStringReader)this.reader);
        }
        char $$0 = this.reader.peek();
        if ($$0 == '{') {
            return this.readStruct();
        }
        if ($$0 == '[') {
            return this.readList();
        }
        return this.readTypedValue();
    }

    protected Tag readList() throws CommandSyntaxException {
        if (this.reader.canRead(3) && !StringReader.isQuotedStringStart((char)this.reader.peek(1)) && this.reader.peek(2) == ';') {
            return this.readArrayTag();
        }
        return this.readListTag();
    }

    public CompoundTag readStruct() throws CommandSyntaxException {
        this.expect('{');
        CompoundTag $$0 = new CompoundTag();
        this.reader.skipWhitespace();
        while (this.reader.canRead() && this.reader.peek() != '}') {
            int $$1 = this.reader.getCursor();
            String $$2 = this.readKey();
            if ($$2.isEmpty()) {
                this.reader.setCursor($$1);
                throw ERROR_EXPECTED_KEY.createWithContext((ImmutableStringReader)this.reader);
            }
            this.expect(':');
            $$0.put($$2, this.readValue());
            if (!this.hasElementSeparator()) break;
            if (this.reader.canRead()) continue;
            throw ERROR_EXPECTED_KEY.createWithContext((ImmutableStringReader)this.reader);
        }
        this.expect('}');
        return $$0;
    }

    private Tag readListTag() throws CommandSyntaxException {
        this.expect('[');
        this.reader.skipWhitespace();
        if (!this.reader.canRead()) {
            throw ERROR_EXPECTED_VALUE.createWithContext((ImmutableStringReader)this.reader);
        }
        ListTag $$0 = new ListTag();
        TagType<?> $$1 = null;
        while (this.reader.peek() != ']') {
            int $$2 = this.reader.getCursor();
            Tag $$3 = this.readValue();
            TagType<?> $$4 = $$3.getType();
            if ($$1 == null) {
                $$1 = $$4;
            } else if ($$4 != $$1) {
                this.reader.setCursor($$2);
                throw ERROR_INSERT_MIXED_LIST.createWithContext((ImmutableStringReader)this.reader, (Object)$$4.getPrettyName(), (Object)$$1.getPrettyName());
            }
            $$0.add($$3);
            if (!this.hasElementSeparator()) break;
            if (this.reader.canRead()) continue;
            throw ERROR_EXPECTED_VALUE.createWithContext((ImmutableStringReader)this.reader);
        }
        this.expect(']');
        return $$0;
    }

    private Tag readArrayTag() throws CommandSyntaxException {
        this.expect('[');
        int $$0 = this.reader.getCursor();
        char $$1 = this.reader.read();
        this.reader.read();
        this.reader.skipWhitespace();
        if (!this.reader.canRead()) {
            throw ERROR_EXPECTED_VALUE.createWithContext((ImmutableStringReader)this.reader);
        }
        if ($$1 == 'B') {
            return new ByteArrayTag(this.readArray(ByteArrayTag.TYPE, ByteTag.TYPE));
        }
        if ($$1 == 'L') {
            return new LongArrayTag(this.readArray(LongArrayTag.TYPE, LongTag.TYPE));
        }
        if ($$1 == 'I') {
            return new IntArrayTag(this.readArray(IntArrayTag.TYPE, IntTag.TYPE));
        }
        this.reader.setCursor($$0);
        throw ERROR_INVALID_ARRAY.createWithContext((ImmutableStringReader)this.reader, (Object)String.valueOf((char)$$1));
    }

    private <T extends Number> List<T> readArray(TagType<?> $$0, TagType<?> $$1) throws CommandSyntaxException {
        ArrayList $$2 = Lists.newArrayList();
        while (this.reader.peek() != ']') {
            int $$3 = this.reader.getCursor();
            Tag $$4 = this.readValue();
            TagType<?> $$5 = $$4.getType();
            if ($$5 != $$1) {
                this.reader.setCursor($$3);
                throw ERROR_INSERT_MIXED_ARRAY.createWithContext((ImmutableStringReader)this.reader, (Object)$$5.getPrettyName(), (Object)$$0.getPrettyName());
            }
            if ($$1 == ByteTag.TYPE) {
                $$2.add((Object)((NumericTag)$$4).getAsByte());
            } else if ($$1 == LongTag.TYPE) {
                $$2.add((Object)((NumericTag)$$4).getAsLong());
            } else {
                $$2.add((Object)((NumericTag)$$4).getAsInt());
            }
            if (!this.hasElementSeparator()) break;
            if (this.reader.canRead()) continue;
            throw ERROR_EXPECTED_VALUE.createWithContext((ImmutableStringReader)this.reader);
        }
        this.expect(']');
        return $$2;
    }

    private boolean hasElementSeparator() {
        this.reader.skipWhitespace();
        if (this.reader.canRead() && this.reader.peek() == ',') {
            this.reader.skip();
            this.reader.skipWhitespace();
            return true;
        }
        return false;
    }

    private void expect(char $$0) throws CommandSyntaxException {
        this.reader.skipWhitespace();
        this.reader.expect($$0);
    }
}