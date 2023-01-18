/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSerializer
 *  com.google.gson.JsonSyntaxException
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Boolean
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.lang.reflect.Type
 *  java.util.Objects
 *  java.util.Optional
 *  javax.annotation.Nullable
 */
package net.minecraft.network.chat;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.ResourceLocationException;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class Style {
    public static final Style EMPTY = new Style(null, null, null, null, null, null, null, null, null, null);
    public static final Codec<Style> FORMATTING_CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)TextColor.CODEC.optionalFieldOf("color").forGetter($$0 -> Optional.ofNullable((Object)$$0.color)), (App)Codec.BOOL.optionalFieldOf("bold").forGetter($$0 -> Optional.ofNullable((Object)$$0.bold)), (App)Codec.BOOL.optionalFieldOf("italic").forGetter($$0 -> Optional.ofNullable((Object)$$0.italic)), (App)Codec.BOOL.optionalFieldOf("underlined").forGetter($$0 -> Optional.ofNullable((Object)$$0.underlined)), (App)Codec.BOOL.optionalFieldOf("strikethrough").forGetter($$0 -> Optional.ofNullable((Object)$$0.strikethrough)), (App)Codec.BOOL.optionalFieldOf("obfuscated").forGetter($$0 -> Optional.ofNullable((Object)$$0.obfuscated)), (App)Codec.STRING.optionalFieldOf("insertion").forGetter($$0 -> Optional.ofNullable((Object)$$0.insertion)), (App)ResourceLocation.CODEC.optionalFieldOf("font").forGetter($$0 -> Optional.ofNullable((Object)$$0.font))).apply((Applicative)$$02, Style::create));
    public static final ResourceLocation DEFAULT_FONT = new ResourceLocation("minecraft", "default");
    @Nullable
    final TextColor color;
    @Nullable
    final Boolean bold;
    @Nullable
    final Boolean italic;
    @Nullable
    final Boolean underlined;
    @Nullable
    final Boolean strikethrough;
    @Nullable
    final Boolean obfuscated;
    @Nullable
    final ClickEvent clickEvent;
    @Nullable
    final HoverEvent hoverEvent;
    @Nullable
    final String insertion;
    @Nullable
    final ResourceLocation font;

    private static Style create(Optional<TextColor> $$0, Optional<Boolean> $$1, Optional<Boolean> $$2, Optional<Boolean> $$3, Optional<Boolean> $$4, Optional<Boolean> $$5, Optional<String> $$6, Optional<ResourceLocation> $$7) {
        return new Style((TextColor)$$0.orElse(null), (Boolean)$$1.orElse(null), (Boolean)$$2.orElse(null), (Boolean)$$3.orElse(null), (Boolean)$$4.orElse(null), (Boolean)$$5.orElse(null), null, null, (String)$$6.orElse(null), (ResourceLocation)$$7.orElse(null));
    }

    Style(@Nullable TextColor $$0, @Nullable Boolean $$1, @Nullable Boolean $$2, @Nullable Boolean $$3, @Nullable Boolean $$4, @Nullable Boolean $$5, @Nullable ClickEvent $$6, @Nullable HoverEvent $$7, @Nullable String $$8, @Nullable ResourceLocation $$9) {
        this.color = $$0;
        this.bold = $$1;
        this.italic = $$2;
        this.underlined = $$3;
        this.strikethrough = $$4;
        this.obfuscated = $$5;
        this.clickEvent = $$6;
        this.hoverEvent = $$7;
        this.insertion = $$8;
        this.font = $$9;
    }

    @Nullable
    public TextColor getColor() {
        return this.color;
    }

    public boolean isBold() {
        return this.bold == Boolean.TRUE;
    }

    public boolean isItalic() {
        return this.italic == Boolean.TRUE;
    }

    public boolean isStrikethrough() {
        return this.strikethrough == Boolean.TRUE;
    }

    public boolean isUnderlined() {
        return this.underlined == Boolean.TRUE;
    }

    public boolean isObfuscated() {
        return this.obfuscated == Boolean.TRUE;
    }

    public boolean isEmpty() {
        return this == EMPTY;
    }

    @Nullable
    public ClickEvent getClickEvent() {
        return this.clickEvent;
    }

    @Nullable
    public HoverEvent getHoverEvent() {
        return this.hoverEvent;
    }

    @Nullable
    public String getInsertion() {
        return this.insertion;
    }

    public ResourceLocation getFont() {
        return this.font != null ? this.font : DEFAULT_FONT;
    }

    public Style withColor(@Nullable TextColor $$0) {
        return new Style($$0, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font);
    }

    public Style withColor(@Nullable ChatFormatting $$0) {
        return this.withColor($$0 != null ? TextColor.fromLegacyFormat($$0) : null);
    }

    public Style withColor(int $$0) {
        return this.withColor(TextColor.fromRgb($$0));
    }

    public Style withBold(@Nullable Boolean $$0) {
        return new Style(this.color, $$0, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font);
    }

    public Style withItalic(@Nullable Boolean $$0) {
        return new Style(this.color, this.bold, $$0, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font);
    }

    public Style withUnderlined(@Nullable Boolean $$0) {
        return new Style(this.color, this.bold, this.italic, $$0, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font);
    }

    public Style withStrikethrough(@Nullable Boolean $$0) {
        return new Style(this.color, this.bold, this.italic, this.underlined, $$0, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font);
    }

    public Style withObfuscated(@Nullable Boolean $$0) {
        return new Style(this.color, this.bold, this.italic, this.underlined, this.strikethrough, $$0, this.clickEvent, this.hoverEvent, this.insertion, this.font);
    }

    public Style withClickEvent(@Nullable ClickEvent $$0) {
        return new Style(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, $$0, this.hoverEvent, this.insertion, this.font);
    }

    public Style withHoverEvent(@Nullable HoverEvent $$0) {
        return new Style(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, $$0, this.insertion, this.font);
    }

    public Style withInsertion(@Nullable String $$0) {
        return new Style(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, $$0, this.font);
    }

    public Style withFont(@Nullable ResourceLocation $$0) {
        return new Style(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, $$0);
    }

    public Style applyFormat(ChatFormatting $$0) {
        TextColor $$1 = this.color;
        Boolean $$2 = this.bold;
        Boolean $$3 = this.italic;
        Boolean $$4 = this.strikethrough;
        Boolean $$5 = this.underlined;
        Boolean $$6 = this.obfuscated;
        switch ($$0) {
            case OBFUSCATED: {
                $$6 = true;
                break;
            }
            case BOLD: {
                $$2 = true;
                break;
            }
            case STRIKETHROUGH: {
                $$4 = true;
                break;
            }
            case UNDERLINE: {
                $$5 = true;
                break;
            }
            case ITALIC: {
                $$3 = true;
                break;
            }
            case RESET: {
                return EMPTY;
            }
            default: {
                $$1 = TextColor.fromLegacyFormat($$0);
            }
        }
        return new Style($$1, $$2, $$3, $$5, $$4, $$6, this.clickEvent, this.hoverEvent, this.insertion, this.font);
    }

    public Style applyLegacyFormat(ChatFormatting $$0) {
        TextColor $$1 = this.color;
        Boolean $$2 = this.bold;
        Boolean $$3 = this.italic;
        Boolean $$4 = this.strikethrough;
        Boolean $$5 = this.underlined;
        Boolean $$6 = this.obfuscated;
        switch ($$0) {
            case OBFUSCATED: {
                $$6 = true;
                break;
            }
            case BOLD: {
                $$2 = true;
                break;
            }
            case STRIKETHROUGH: {
                $$4 = true;
                break;
            }
            case UNDERLINE: {
                $$5 = true;
                break;
            }
            case ITALIC: {
                $$3 = true;
                break;
            }
            case RESET: {
                return EMPTY;
            }
            default: {
                $$6 = false;
                $$2 = false;
                $$4 = false;
                $$5 = false;
                $$3 = false;
                $$1 = TextColor.fromLegacyFormat($$0);
            }
        }
        return new Style($$1, $$2, $$3, $$5, $$4, $$6, this.clickEvent, this.hoverEvent, this.insertion, this.font);
    }

    public Style applyFormats(ChatFormatting ... $$0) {
        TextColor $$1 = this.color;
        Boolean $$2 = this.bold;
        Boolean $$3 = this.italic;
        Boolean $$4 = this.strikethrough;
        Boolean $$5 = this.underlined;
        Boolean $$6 = this.obfuscated;
        block8: for (ChatFormatting $$7 : $$0) {
            switch ($$7) {
                case OBFUSCATED: {
                    $$6 = true;
                    continue block8;
                }
                case BOLD: {
                    $$2 = true;
                    continue block8;
                }
                case STRIKETHROUGH: {
                    $$4 = true;
                    continue block8;
                }
                case UNDERLINE: {
                    $$5 = true;
                    continue block8;
                }
                case ITALIC: {
                    $$3 = true;
                    continue block8;
                }
                case RESET: {
                    return EMPTY;
                }
                default: {
                    $$1 = TextColor.fromLegacyFormat($$7);
                }
            }
        }
        return new Style($$1, $$2, $$3, $$5, $$4, $$6, this.clickEvent, this.hoverEvent, this.insertion, this.font);
    }

    public Style applyTo(Style $$0) {
        if (this == EMPTY) {
            return $$0;
        }
        if ($$0 == EMPTY) {
            return this;
        }
        return new Style(this.color != null ? this.color : $$0.color, this.bold != null ? this.bold : $$0.bold, this.italic != null ? this.italic : $$0.italic, this.underlined != null ? this.underlined : $$0.underlined, this.strikethrough != null ? this.strikethrough : $$0.strikethrough, this.obfuscated != null ? this.obfuscated : $$0.obfuscated, this.clickEvent != null ? this.clickEvent : $$0.clickEvent, this.hoverEvent != null ? this.hoverEvent : $$0.hoverEvent, this.insertion != null ? this.insertion : $$0.insertion, this.font != null ? this.font : $$0.font);
    }

    public String toString() {
        final StringBuilder $$0 = new StringBuilder("{");
        class Collector {
            private boolean isNotFirst;

            Collector() {
            }

            private void prependSeparator() {
                if (this.isNotFirst) {
                    $$0.append(',');
                }
                this.isNotFirst = true;
            }

            void addFlagString(String $$02, @Nullable Boolean $$1) {
                if ($$1 != null) {
                    this.prependSeparator();
                    if (!$$1.booleanValue()) {
                        $$0.append('!');
                    }
                    $$0.append($$02);
                }
            }

            void addValueString(String $$02, @Nullable Object $$1) {
                if ($$1 != null) {
                    this.prependSeparator();
                    $$0.append($$02);
                    $$0.append('=');
                    $$0.append($$1);
                }
            }
        }
        Collector $$1 = new Collector();
        $$1.addValueString("color", this.color);
        $$1.addFlagString("bold", this.bold);
        $$1.addFlagString("italic", this.italic);
        $$1.addFlagString("underlined", this.underlined);
        $$1.addFlagString("strikethrough", this.strikethrough);
        $$1.addFlagString("obfuscated", this.obfuscated);
        $$1.addValueString("clickEvent", this.clickEvent);
        $$1.addValueString("hoverEvent", this.hoverEvent);
        $$1.addValueString("insertion", this.insertion);
        $$1.addValueString("font", this.font);
        $$0.append("}");
        return $$0.toString();
    }

    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        if ($$0 instanceof Style) {
            Style $$1 = (Style)$$0;
            return this.isBold() == $$1.isBold() && Objects.equals((Object)this.getColor(), (Object)$$1.getColor()) && this.isItalic() == $$1.isItalic() && this.isObfuscated() == $$1.isObfuscated() && this.isStrikethrough() == $$1.isStrikethrough() && this.isUnderlined() == $$1.isUnderlined() && Objects.equals((Object)this.getClickEvent(), (Object)$$1.getClickEvent()) && Objects.equals((Object)this.getHoverEvent(), (Object)$$1.getHoverEvent()) && Objects.equals((Object)this.getInsertion(), (Object)$$1.getInsertion()) && Objects.equals((Object)this.getFont(), (Object)$$1.getFont());
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash((Object[])new Object[]{this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion});
    }

    public static class Serializer
    implements JsonDeserializer<Style>,
    JsonSerializer<Style> {
        @Nullable
        public Style deserialize(JsonElement $$0, Type $$1, JsonDeserializationContext $$2) throws JsonParseException {
            if ($$0.isJsonObject()) {
                JsonObject $$3 = $$0.getAsJsonObject();
                if ($$3 == null) {
                    return null;
                }
                Boolean $$4 = Serializer.getOptionalFlag($$3, "bold");
                Boolean $$5 = Serializer.getOptionalFlag($$3, "italic");
                Boolean $$6 = Serializer.getOptionalFlag($$3, "underlined");
                Boolean $$7 = Serializer.getOptionalFlag($$3, "strikethrough");
                Boolean $$8 = Serializer.getOptionalFlag($$3, "obfuscated");
                TextColor $$9 = Serializer.getTextColor($$3);
                String $$10 = Serializer.getInsertion($$3);
                ClickEvent $$11 = Serializer.getClickEvent($$3);
                HoverEvent $$12 = Serializer.getHoverEvent($$3);
                ResourceLocation $$13 = Serializer.getFont($$3);
                return new Style($$9, $$4, $$5, $$6, $$7, $$8, $$11, $$12, $$10, $$13);
            }
            return null;
        }

        @Nullable
        private static ResourceLocation getFont(JsonObject $$0) {
            if ($$0.has("font")) {
                String $$1 = GsonHelper.getAsString($$0, "font");
                try {
                    return new ResourceLocation($$1);
                }
                catch (ResourceLocationException $$2) {
                    throw new JsonSyntaxException("Invalid font name: " + $$1);
                }
            }
            return null;
        }

        @Nullable
        private static HoverEvent getHoverEvent(JsonObject $$0) {
            JsonObject $$1;
            HoverEvent $$2;
            if ($$0.has("hoverEvent") && ($$2 = HoverEvent.deserialize($$1 = GsonHelper.getAsJsonObject($$0, "hoverEvent"))) != null && $$2.getAction().isAllowedFromServer()) {
                return $$2;
            }
            return null;
        }

        @Nullable
        private static ClickEvent getClickEvent(JsonObject $$0) {
            if ($$0.has("clickEvent")) {
                JsonObject $$1 = GsonHelper.getAsJsonObject($$0, "clickEvent");
                String $$2 = GsonHelper.getAsString($$1, "action", null);
                ClickEvent.Action $$3 = $$2 == null ? null : ClickEvent.Action.getByName($$2);
                String $$4 = GsonHelper.getAsString($$1, "value", null);
                if ($$3 != null && $$4 != null && $$3.isAllowedFromServer()) {
                    return new ClickEvent($$3, $$4);
                }
            }
            return null;
        }

        @Nullable
        private static String getInsertion(JsonObject $$0) {
            return GsonHelper.getAsString($$0, "insertion", null);
        }

        @Nullable
        private static TextColor getTextColor(JsonObject $$0) {
            if ($$0.has("color")) {
                String $$1 = GsonHelper.getAsString($$0, "color");
                return TextColor.parseColor($$1);
            }
            return null;
        }

        @Nullable
        private static Boolean getOptionalFlag(JsonObject $$0, String $$1) {
            if ($$0.has($$1)) {
                return $$0.get($$1).getAsBoolean();
            }
            return null;
        }

        @Nullable
        public JsonElement serialize(Style $$0, Type $$1, JsonSerializationContext $$2) {
            if ($$0.isEmpty()) {
                return null;
            }
            JsonObject $$3 = new JsonObject();
            if ($$0.bold != null) {
                $$3.addProperty("bold", $$0.bold);
            }
            if ($$0.italic != null) {
                $$3.addProperty("italic", $$0.italic);
            }
            if ($$0.underlined != null) {
                $$3.addProperty("underlined", $$0.underlined);
            }
            if ($$0.strikethrough != null) {
                $$3.addProperty("strikethrough", $$0.strikethrough);
            }
            if ($$0.obfuscated != null) {
                $$3.addProperty("obfuscated", $$0.obfuscated);
            }
            if ($$0.color != null) {
                $$3.addProperty("color", $$0.color.serialize());
            }
            if ($$0.insertion != null) {
                $$3.add("insertion", $$2.serialize((Object)$$0.insertion));
            }
            if ($$0.clickEvent != null) {
                JsonObject $$4 = new JsonObject();
                $$4.addProperty("action", $$0.clickEvent.getAction().getName());
                $$4.addProperty("value", $$0.clickEvent.getValue());
                $$3.add("clickEvent", (JsonElement)$$4);
            }
            if ($$0.hoverEvent != null) {
                $$3.add("hoverEvent", (JsonElement)$$0.hoverEvent.serialize());
            }
            if ($$0.font != null) {
                $$3.addProperty("font", $$0.font.toString());
            }
            return $$3;
        }
    }
}