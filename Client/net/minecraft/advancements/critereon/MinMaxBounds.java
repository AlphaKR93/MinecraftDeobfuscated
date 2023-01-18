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
 *  com.mojang.brigadier.exceptions.BuiltInExceptionProvider
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  java.lang.Double
 *  java.lang.FunctionalInterface
 *  java.lang.Integer
 *  java.lang.Long
 *  java.lang.Number
 *  java.lang.NumberFormatException
 *  java.lang.Object
 *  java.lang.String
 *  java.util.function.BiFunction
 *  java.util.function.Function
 *  java.util.function.Supplier
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
import com.mojang.brigadier.exceptions.BuiltInExceptionProvider;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;

public abstract class MinMaxBounds<T extends Number> {
    public static final SimpleCommandExceptionType ERROR_EMPTY = new SimpleCommandExceptionType((Message)Component.translatable("argument.range.empty"));
    public static final SimpleCommandExceptionType ERROR_SWAPPED = new SimpleCommandExceptionType((Message)Component.translatable("argument.range.swapped"));
    @Nullable
    protected final T min;
    @Nullable
    protected final T max;

    protected MinMaxBounds(@Nullable T $$0, @Nullable T $$1) {
        this.min = $$0;
        this.max = $$1;
    }

    @Nullable
    public T getMin() {
        return this.min;
    }

    @Nullable
    public T getMax() {
        return this.max;
    }

    public boolean isAny() {
        return this.min == null && this.max == null;
    }

    public JsonElement serializeToJson() {
        if (this.isAny()) {
            return JsonNull.INSTANCE;
        }
        if (this.min != null && this.min.equals(this.max)) {
            return new JsonPrimitive(this.min);
        }
        JsonObject $$0 = new JsonObject();
        if (this.min != null) {
            $$0.addProperty("min", this.min);
        }
        if (this.max != null) {
            $$0.addProperty("max", this.max);
        }
        return $$0;
    }

    protected static <T extends Number, R extends MinMaxBounds<T>> R fromJson(@Nullable JsonElement $$0, R $$1, BiFunction<JsonElement, String, T> $$2, BoundsFactory<T, R> $$3) {
        if ($$0 == null || $$0.isJsonNull()) {
            return $$1;
        }
        if (GsonHelper.isNumberValue($$0)) {
            Number $$4 = (Number)$$2.apply((Object)$$0, (Object)"value");
            return $$3.create($$4, $$4);
        }
        JsonObject $$5 = GsonHelper.convertToJsonObject($$0, "value");
        Number $$6 = $$5.has("min") ? (Number)((Number)$$2.apply((Object)$$5.get("min"), (Object)"min")) : (Number)null;
        Number $$7 = $$5.has("max") ? (Number)((Number)$$2.apply((Object)$$5.get("max"), (Object)"max")) : (Number)null;
        return $$3.create($$6, $$7);
    }

    protected static <T extends Number, R extends MinMaxBounds<T>> R fromReader(StringReader $$0, BoundsFromReaderFactory<T, R> $$1, Function<String, T> $$2, Supplier<DynamicCommandExceptionType> $$3, Function<T, T> $$4) throws CommandSyntaxException {
        if (!$$0.canRead()) {
            throw ERROR_EMPTY.createWithContext((ImmutableStringReader)$$0);
        }
        int $$5 = $$0.getCursor();
        try {
            Number $$8;
            Number $$6 = (Number)MinMaxBounds.optionallyFormat(MinMaxBounds.readNumber($$0, $$2, $$3), $$4);
            if ($$0.canRead(2) && $$0.peek() == '.' && $$0.peek(1) == '.') {
                $$0.skip();
                $$0.skip();
                Number $$7 = (Number)MinMaxBounds.optionallyFormat(MinMaxBounds.readNumber($$0, $$2, $$3), $$4);
                if ($$6 == null && $$7 == null) {
                    throw ERROR_EMPTY.createWithContext((ImmutableStringReader)$$0);
                }
            } else {
                $$8 = $$6;
            }
            if ($$6 == null && $$8 == null) {
                throw ERROR_EMPTY.createWithContext((ImmutableStringReader)$$0);
            }
            return $$1.create($$0, $$6, $$8);
        }
        catch (CommandSyntaxException $$9) {
            $$0.setCursor($$5);
            throw new CommandSyntaxException($$9.getType(), $$9.getRawMessage(), $$9.getInput(), $$5);
        }
    }

    @Nullable
    private static <T extends Number> T readNumber(StringReader $$0, Function<String, T> $$1, Supplier<DynamicCommandExceptionType> $$2) throws CommandSyntaxException {
        int $$3 = $$0.getCursor();
        while ($$0.canRead() && MinMaxBounds.isAllowedInputChat($$0)) {
            $$0.skip();
        }
        String $$4 = $$0.getString().substring($$3, $$0.getCursor());
        if ($$4.isEmpty()) {
            return null;
        }
        try {
            return (T)((Number)$$1.apply((Object)$$4));
        }
        catch (NumberFormatException $$5) {
            throw ((DynamicCommandExceptionType)$$2.get()).createWithContext((ImmutableStringReader)$$0, (Object)$$4);
        }
    }

    private static boolean isAllowedInputChat(StringReader $$0) {
        char $$1 = $$0.peek();
        if ($$1 >= '0' && $$1 <= '9' || $$1 == '-') {
            return true;
        }
        if ($$1 == '.') {
            return !$$0.canRead(2) || $$0.peek(1) != '.';
        }
        return false;
    }

    @Nullable
    private static <T> T optionallyFormat(@Nullable T $$0, Function<T, T> $$1) {
        return (T)($$0 == null ? null : $$1.apply($$0));
    }

    @FunctionalInterface
    protected static interface BoundsFactory<T extends Number, R extends MinMaxBounds<T>> {
        public R create(@Nullable T var1, @Nullable T var2);
    }

    @FunctionalInterface
    protected static interface BoundsFromReaderFactory<T extends Number, R extends MinMaxBounds<T>> {
        public R create(StringReader var1, @Nullable T var2, @Nullable T var3) throws CommandSyntaxException;
    }

    public static class Doubles
    extends MinMaxBounds<Double> {
        public static final Doubles ANY = new Doubles(null, null);
        @Nullable
        private final Double minSq;
        @Nullable
        private final Double maxSq;

        private static Doubles create(StringReader $$0, @Nullable Double $$1, @Nullable Double $$2) throws CommandSyntaxException {
            if ($$1 != null && $$2 != null && $$1 > $$2) {
                throw ERROR_SWAPPED.createWithContext((ImmutableStringReader)$$0);
            }
            return new Doubles($$1, $$2);
        }

        @Nullable
        private static Double squareOpt(@Nullable Double $$0) {
            return $$0 == null ? null : Double.valueOf((double)($$0 * $$0));
        }

        private Doubles(@Nullable Double $$0, @Nullable Double $$1) {
            super($$0, $$1);
            this.minSq = Doubles.squareOpt($$0);
            this.maxSq = Doubles.squareOpt($$1);
        }

        public static Doubles exactly(double $$0) {
            return new Doubles($$0, $$0);
        }

        public static Doubles between(double $$0, double $$1) {
            return new Doubles($$0, $$1);
        }

        public static Doubles atLeast(double $$0) {
            return new Doubles($$0, null);
        }

        public static Doubles atMost(double $$0) {
            return new Doubles(null, $$0);
        }

        public boolean matches(double $$0) {
            if (this.min != null && (Double)this.min > $$0) {
                return false;
            }
            return this.max == null || !((Double)this.max < $$0);
        }

        public boolean matchesSqr(double $$0) {
            if (this.minSq != null && this.minSq > $$0) {
                return false;
            }
            return this.maxSq == null || !(this.maxSq < $$0);
        }

        public static Doubles fromJson(@Nullable JsonElement $$0) {
            return Doubles.fromJson($$0, ANY, GsonHelper::convertToDouble, Doubles::new);
        }

        public static Doubles fromReader(StringReader $$02) throws CommandSyntaxException {
            return Doubles.fromReader($$02, (Function<Double, Double>)((Function)$$0 -> $$0));
        }

        public static Doubles fromReader(StringReader $$0, Function<Double, Double> $$1) throws CommandSyntaxException {
            return Doubles.fromReader($$0, Doubles::create, Double::parseDouble, (Supplier<DynamicCommandExceptionType>)((Supplier)() -> ((BuiltInExceptionProvider)CommandSyntaxException.BUILT_IN_EXCEPTIONS).readerInvalidDouble()), $$1);
        }
    }

    public static class Ints
    extends MinMaxBounds<Integer> {
        public static final Ints ANY = new Ints(null, null);
        @Nullable
        private final Long minSq;
        @Nullable
        private final Long maxSq;

        private static Ints create(StringReader $$0, @Nullable Integer $$1, @Nullable Integer $$2) throws CommandSyntaxException {
            if ($$1 != null && $$2 != null && $$1 > $$2) {
                throw ERROR_SWAPPED.createWithContext((ImmutableStringReader)$$0);
            }
            return new Ints($$1, $$2);
        }

        @Nullable
        private static Long squareOpt(@Nullable Integer $$0) {
            return $$0 == null ? null : Long.valueOf((long)($$0.longValue() * $$0.longValue()));
        }

        private Ints(@Nullable Integer $$0, @Nullable Integer $$1) {
            super($$0, $$1);
            this.minSq = Ints.squareOpt($$0);
            this.maxSq = Ints.squareOpt($$1);
        }

        public static Ints exactly(int $$0) {
            return new Ints($$0, $$0);
        }

        public static Ints between(int $$0, int $$1) {
            return new Ints($$0, $$1);
        }

        public static Ints atLeast(int $$0) {
            return new Ints($$0, null);
        }

        public static Ints atMost(int $$0) {
            return new Ints(null, $$0);
        }

        public boolean matches(int $$0) {
            if (this.min != null && (Integer)this.min > $$0) {
                return false;
            }
            return this.max == null || (Integer)this.max >= $$0;
        }

        public boolean matchesSqr(long $$0) {
            if (this.minSq != null && this.minSq > $$0) {
                return false;
            }
            return this.maxSq == null || this.maxSq >= $$0;
        }

        public static Ints fromJson(@Nullable JsonElement $$0) {
            return Ints.fromJson($$0, ANY, GsonHelper::convertToInt, Ints::new);
        }

        public static Ints fromReader(StringReader $$02) throws CommandSyntaxException {
            return Ints.fromReader($$02, (Function<Integer, Integer>)((Function)$$0 -> $$0));
        }

        public static Ints fromReader(StringReader $$0, Function<Integer, Integer> $$1) throws CommandSyntaxException {
            return Ints.fromReader($$0, Ints::create, Integer::parseInt, (Supplier<DynamicCommandExceptionType>)((Supplier)() -> ((BuiltInExceptionProvider)CommandSyntaxException.BUILT_IN_EXCEPTIONS).readerInvalidInt()), $$1);
        }
    }
}