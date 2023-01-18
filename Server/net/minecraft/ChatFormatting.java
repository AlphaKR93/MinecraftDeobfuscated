/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.serialization.Codec
 *  java.lang.CharSequence
 *  java.lang.Character
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.Arrays
 *  java.util.Collection
 *  java.util.Locale
 *  java.util.Map
 *  java.util.function.Supplier
 *  java.util.regex.Pattern
 *  java.util.stream.Collectors
 *  javax.annotation.Nullable
 */
package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.util.StringRepresentable;

public enum ChatFormatting implements StringRepresentable
{
    BLACK("BLACK", '0', 0, 0),
    DARK_BLUE("DARK_BLUE", '1', 1, 170),
    DARK_GREEN("DARK_GREEN", '2', 2, 43520),
    DARK_AQUA("DARK_AQUA", '3', 3, 43690),
    DARK_RED("DARK_RED", '4', 4, 0xAA0000),
    DARK_PURPLE("DARK_PURPLE", '5', 5, 0xAA00AA),
    GOLD("GOLD", '6', 6, 0xFFAA00),
    GRAY("GRAY", '7', 7, 0xAAAAAA),
    DARK_GRAY("DARK_GRAY", '8', 8, 0x555555),
    BLUE("BLUE", '9', 9, 0x5555FF),
    GREEN("GREEN", 'a', 10, 0x55FF55),
    AQUA("AQUA", 'b', 11, 0x55FFFF),
    RED("RED", 'c', 12, 0xFF5555),
    LIGHT_PURPLE("LIGHT_PURPLE", 'd', 13, 0xFF55FF),
    YELLOW("YELLOW", 'e', 14, 0xFFFF55),
    WHITE("WHITE", 'f', 15, 0xFFFFFF),
    OBFUSCATED("OBFUSCATED", 'k', true),
    BOLD("BOLD", 'l', true),
    STRIKETHROUGH("STRIKETHROUGH", 'm', true),
    UNDERLINE("UNDERLINE", 'n', true),
    ITALIC("ITALIC", 'o', true),
    RESET("RESET", 'r', -1, null);

    public static final Codec<ChatFormatting> CODEC;
    public static final char PREFIX_CODE = '\u00a7';
    private static final Map<String, ChatFormatting> FORMATTING_BY_NAME;
    private static final Pattern STRIP_FORMATTING_PATTERN;
    private final String name;
    private final char code;
    private final boolean isFormat;
    private final String toString;
    private final int id;
    @Nullable
    private final Integer color;

    private static String cleanName(String $$0) {
        return $$0.toLowerCase(Locale.ROOT).replaceAll("[^a-z]", "");
    }

    private ChatFormatting(String $$0, @Nullable char $$1, int $$2, Integer $$3) {
        this($$0, $$1, false, $$2, $$3);
    }

    private ChatFormatting(String $$0, char $$1, boolean $$2) {
        this($$0, $$1, $$2, -1, null);
    }

    private ChatFormatting(String $$0, char $$1, @Nullable boolean $$2, int $$3, Integer $$4) {
        this.name = $$0;
        this.code = $$1;
        this.isFormat = $$2;
        this.id = $$3;
        this.color = $$4;
        this.toString = "\u00a7" + $$1;
    }

    public char getChar() {
        return this.code;
    }

    public int getId() {
        return this.id;
    }

    public boolean isFormat() {
        return this.isFormat;
    }

    public boolean isColor() {
        return !this.isFormat && this != RESET;
    }

    @Nullable
    public Integer getColor() {
        return this.color;
    }

    public String getName() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    public String toString() {
        return this.toString;
    }

    @Nullable
    public static String stripFormatting(@Nullable String $$0) {
        return $$0 == null ? null : STRIP_FORMATTING_PATTERN.matcher((CharSequence)$$0).replaceAll("");
    }

    @Nullable
    public static ChatFormatting getByName(@Nullable String $$0) {
        if ($$0 == null) {
            return null;
        }
        return (ChatFormatting)FORMATTING_BY_NAME.get((Object)ChatFormatting.cleanName($$0));
    }

    @Nullable
    public static ChatFormatting getById(int $$0) {
        if ($$0 < 0) {
            return RESET;
        }
        for (ChatFormatting $$1 : ChatFormatting.values()) {
            if ($$1.getId() != $$0) continue;
            return $$1;
        }
        return null;
    }

    @Nullable
    public static ChatFormatting getByCode(char $$0) {
        char $$1 = Character.toString((char)$$0).toLowerCase(Locale.ROOT).charAt(0);
        for (ChatFormatting $$2 : ChatFormatting.values()) {
            if ($$2.code != $$1) continue;
            return $$2;
        }
        return null;
    }

    public static Collection<String> getNames(boolean $$0, boolean $$1) {
        ArrayList $$2 = Lists.newArrayList();
        for (ChatFormatting $$3 : ChatFormatting.values()) {
            if ($$3.isColor() && !$$0 || $$3.isFormat() && !$$1) continue;
            $$2.add((Object)$$3.getName());
        }
        return $$2;
    }

    @Override
    public String getSerializedName() {
        return this.getName();
    }

    static {
        CODEC = StringRepresentable.fromEnum((Supplier<E[]>)((Supplier)ChatFormatting::values));
        FORMATTING_BY_NAME = (Map)Arrays.stream((Object[])ChatFormatting.values()).collect(Collectors.toMap($$0 -> ChatFormatting.cleanName($$0.name), $$0 -> $$0));
        STRIP_FORMATTING_PATTERN = Pattern.compile((String)"(?i)\u00a7[0-9A-FK-OR]");
    }
}