/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonPrimitive
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  java.lang.Float
 *  java.lang.Number
 *  java.lang.NumberFormatException
 *  java.lang.Object
 *  java.lang.String
 *  java.util.function.Function
 *  javax.annotation.Nullable
 */
package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;

public class WrappedMinMaxBounds {
    public static final WrappedMinMaxBounds ANY = new WrappedMinMaxBounds(null, null);
    public static final SimpleCommandExceptionType ERROR_INTS_ONLY = new SimpleCommandExceptionType((Message)Component.translatable("argument.range.ints"));
    @Nullable
    private final Float min;
    @Nullable
    private final Float max;

    public WrappedMinMaxBounds(@Nullable Float $$0, @Nullable Float $$1) {
        this.min = $$0;
        this.max = $$1;
    }

    public static WrappedMinMaxBounds exactly(float $$0) {
        return new WrappedMinMaxBounds(Float.valueOf((float)$$0), Float.valueOf((float)$$0));
    }

    public static WrappedMinMaxBounds between(float $$0, float $$1) {
        return new WrappedMinMaxBounds(Float.valueOf((float)$$0), Float.valueOf((float)$$1));
    }

    public static WrappedMinMaxBounds atLeast(float $$0) {
        return new WrappedMinMaxBounds(Float.valueOf((float)$$0), null);
    }

    public static WrappedMinMaxBounds atMost(float $$0) {
        return new WrappedMinMaxBounds(null, Float.valueOf((float)$$0));
    }

    public boolean matches(float $$0) {
        if (this.min != null && this.max != null && this.min.floatValue() > this.max.floatValue() && this.min.floatValue() > $$0 && this.max.floatValue() < $$0) {
            return false;
        }
        if (this.min != null && this.min.floatValue() > $$0) {
            return false;
        }
        return this.max == null || !(this.max.floatValue() < $$0);
    }

    public boolean matchesSqr(double $$0) {
        if (this.min != null && this.max != null && this.min.floatValue() > this.max.floatValue() && (double)(this.min.floatValue() * this.min.floatValue()) > $$0 && (double)(this.max.floatValue() * this.max.floatValue()) < $$0) {
            return false;
        }
        if (this.min != null && (double)(this.min.floatValue() * this.min.floatValue()) > $$0) {
            return false;
        }
        return this.max == null || !((double)(this.max.floatValue() * this.max.floatValue()) < $$0);
    }

    @Nullable
    public Float getMin() {
        return this.min;
    }

    @Nullable
    public Float getMax() {
        return this.max;
    }

    public JsonElement serializeToJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        if (this.min != null && this.max != null && this.min.equals((Object)this.max)) {
            return new JsonPrimitive((Number)this.min);
        }
        JsonObject $$0 = new JsonObject();
        if (this.min != null) {
            $$0.addProperty("min", (Number)this.min);
        }
        if (this.max != null) {
            $$0.addProperty("max", (Number)this.min);
        }
        return $$0;
    }

    public static WrappedMinMaxBounds fromJson(@Nullable JsonElement $$0) {
        if ($$0 == null || $$0.isJsonNull()) {
            return ANY;
        }
        if (GsonHelper.isNumberValue($$0)) {
            float $$1 = GsonHelper.convertToFloat($$0, "value");
            return new WrappedMinMaxBounds(Float.valueOf((float)$$1), Float.valueOf((float)$$1));
        }
        JsonObject $$2 = GsonHelper.convertToJsonObject($$0, "value");
        Float $$3 = $$2.has("min") ? Float.valueOf((float)GsonHelper.getAsFloat($$2, "min")) : null;
        Float $$4 = $$2.has("max") ? Float.valueOf((float)GsonHelper.getAsFloat($$2, "max")) : null;
        return new WrappedMinMaxBounds($$3, $$4);
    }

    public static WrappedMinMaxBounds fromReader(StringReader $$02, boolean $$1) throws CommandSyntaxException {
        return WrappedMinMaxBounds.fromReader($$02, $$1, (Function<Float, Float>)((Function)$$0 -> $$0));
    }

    public static WrappedMinMaxBounds fromReader(StringReader $$0, boolean $$1, Function<Float, Float> $$2) throws CommandSyntaxException {
        Float $$6;
        if (!$$0.canRead()) {
            throw MinMaxBounds.ERROR_EMPTY.createWithContext((ImmutableStringReader)$$0);
        }
        int $$3 = $$0.getCursor();
        Float $$4 = WrappedMinMaxBounds.optionallyFormat(WrappedMinMaxBounds.readNumber($$0, $$1), $$2);
        if ($$0.canRead(2) && $$0.peek() == '.' && $$0.peek(1) == '.') {
            $$0.skip();
            $$0.skip();
            Float $$5 = WrappedMinMaxBounds.optionallyFormat(WrappedMinMaxBounds.readNumber($$0, $$1), $$2);
            if ($$4 == null && $$5 == null) {
                $$0.setCursor($$3);
                throw MinMaxBounds.ERROR_EMPTY.createWithContext((ImmutableStringReader)$$0);
            }
        } else {
            if (!$$1 && $$0.canRead() && $$0.peek() == '.') {
                $$0.setCursor($$3);
                throw ERROR_INTS_ONLY.createWithContext((ImmutableStringReader)$$0);
            }
            $$6 = $$4;
        }
        if ($$4 == null && $$6 == null) {
            $$0.setCursor($$3);
            throw MinMaxBounds.ERROR_EMPTY.createWithContext((ImmutableStringReader)$$0);
        }
        return new WrappedMinMaxBounds($$4, $$6);
    }

    @Nullable
    private static Float readNumber(StringReader $$0, boolean $$1) throws CommandSyntaxException {
        int $$2 = $$0.getCursor();
        while ($$0.canRead() && WrappedMinMaxBounds.isAllowedNumber($$0, $$1)) {
            $$0.skip();
        }
        String $$3 = $$0.getString().substring($$2, $$0.getCursor());
        if ($$3.isEmpty()) {
            return null;
        }
        try {
            return Float.valueOf((float)Float.parseFloat((String)$$3));
        }
        catch (NumberFormatException $$4) {
            if ($$1) {
                throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidDouble().createWithContext((ImmutableStringReader)$$0, (Object)$$3);
            }
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidInt().createWithContext((ImmutableStringReader)$$0, (Object)$$3);
        }
    }

    private static boolean isAllowedNumber(StringReader $$0, boolean $$1) {
        char $$2 = $$0.peek();
        if ($$2 >= '0' && $$2 <= '9' || $$2 == '-') {
            return true;
        }
        if ($$1 && $$2 == '.') {
            return !$$0.canRead(2) || $$0.peek(1) != '.';
        }
        return false;
    }

    @Nullable
    private static Float optionallyFormat(@Nullable Float $$0, Function<Float, Float> $$1) {
        return $$0 == null ? null : (Float)$$1.apply((Object)$$0);
    }
}