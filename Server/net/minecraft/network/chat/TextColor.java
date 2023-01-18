/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  java.lang.Integer
 *  java.lang.NumberFormatException
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Locale
 *  java.util.Map
 *  java.util.Objects
 *  java.util.function.Function
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 */
package net.minecraft.network.chat;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;

public final class TextColor {
    private static final String CUSTOM_COLOR_PREFIX = "#";
    public static final Codec<TextColor> CODEC = Codec.STRING.comapFlatMap($$0 -> {
        TextColor $$1 = TextColor.parseColor($$0);
        return $$1 != null ? DataResult.success((Object)$$1) : DataResult.error((String)"String is not a valid color name or hex color code");
    }, TextColor::serialize);
    private static final Map<ChatFormatting, TextColor> LEGACY_FORMAT_TO_COLOR = (Map)Stream.of((Object[])ChatFormatting.values()).filter(ChatFormatting::isColor).collect(ImmutableMap.toImmutableMap((Function)Function.identity(), $$0 -> new TextColor($$0.getColor(), $$0.getName())));
    private static final Map<String, TextColor> NAMED_COLORS = (Map)LEGACY_FORMAT_TO_COLOR.values().stream().collect(ImmutableMap.toImmutableMap($$0 -> $$0.name, (Function)Function.identity()));
    private final int value;
    @Nullable
    private final String name;

    private TextColor(int $$0, String $$1) {
        this.value = $$0;
        this.name = $$1;
    }

    private TextColor(int $$0) {
        this.value = $$0;
        this.name = null;
    }

    public int getValue() {
        return this.value;
    }

    public String serialize() {
        if (this.name != null) {
            return this.name;
        }
        return this.formatValue();
    }

    private String formatValue() {
        return String.format((Locale)Locale.ROOT, (String)"#%06X", (Object[])new Object[]{this.value});
    }

    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        if ($$0 == null || this.getClass() != $$0.getClass()) {
            return false;
        }
        TextColor $$1 = (TextColor)$$0;
        return this.value == $$1.value;
    }

    public int hashCode() {
        return Objects.hash((Object[])new Object[]{this.value, this.name});
    }

    public String toString() {
        return this.name != null ? this.name : this.formatValue();
    }

    @Nullable
    public static TextColor fromLegacyFormat(ChatFormatting $$0) {
        return (TextColor)LEGACY_FORMAT_TO_COLOR.get((Object)$$0);
    }

    public static TextColor fromRgb(int $$0) {
        return new TextColor($$0);
    }

    @Nullable
    public static TextColor parseColor(String $$0) {
        if ($$0.startsWith(CUSTOM_COLOR_PREFIX)) {
            try {
                int $$1 = Integer.parseInt((String)$$0.substring(1), (int)16);
                return TextColor.fromRgb($$1);
            }
            catch (NumberFormatException $$2) {
                return null;
            }
        }
        return (TextColor)NAMED_COLORS.get((Object)$$0);
    }
}