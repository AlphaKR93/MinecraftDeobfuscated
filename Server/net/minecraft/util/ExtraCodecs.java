/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Suppliers
 *  com.google.common.collect.ImmutableList
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParseException
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.properties.Property
 *  com.mojang.authlib.properties.PropertyMap
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.Codec$ResultFunction
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Decoder
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  com.mojang.serialization.Lifecycle
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.MapLike
 *  com.mojang.serialization.RecordBuilder
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.CharSequence
 *  java.lang.Comparable
 *  java.lang.Exception
 *  java.lang.Float
 *  java.lang.IllegalArgumentException
 *  java.lang.Integer
 *  java.lang.Long
 *  java.lang.Number
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.Throwable
 *  java.time.Instant
 *  java.time.format.DateTimeFormatter
 *  java.time.temporal.TemporalAccessor
 *  java.util.Arrays
 *  java.util.Base64
 *  java.util.BitSet
 *  java.util.Collection
 *  java.util.Iterator
 *  java.util.List
 *  java.util.Objects
 *  java.util.Optional
 *  java.util.OptionalLong
 *  java.util.UUID
 *  java.util.function.BiFunction
 *  java.util.function.Function
 *  java.util.function.IntFunction
 *  java.util.function.Supplier
 *  java.util.function.ToIntFunction
 *  java.util.regex.Pattern
 *  java.util.regex.PatternSyntaxException
 *  java.util.stream.Stream
 *  org.apache.commons.lang3.mutable.MutableObject
 *  org.joml.Vector3f
 */
package net.minecraft.util;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.Base64;
import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.core.HolderSet;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.mutable.MutableObject;
import org.joml.Vector3f;

public class ExtraCodecs {
    public static final Codec<JsonElement> JSON = Codec.PASSTHROUGH.xmap($$0 -> (JsonElement)$$0.convert((DynamicOps)JsonOps.INSTANCE).getValue(), $$0 -> new Dynamic((DynamicOps)JsonOps.INSTANCE, $$0));
    public static final Codec<Component> COMPONENT = JSON.flatXmap($$0 -> {
        try {
            return DataResult.success((Object)Component.Serializer.fromJson($$0));
        }
        catch (JsonParseException $$1) {
            return DataResult.error((String)$$1.getMessage());
        }
    }, $$0 -> {
        try {
            return DataResult.success((Object)Component.Serializer.toJsonTree($$0));
        }
        catch (IllegalArgumentException $$1) {
            return DataResult.error((String)$$1.getMessage());
        }
    });
    public static final Codec<Vector3f> VECTOR3F = Codec.FLOAT.listOf().comapFlatMap($$02 -> Util.fixedSize($$02, 3).map($$0 -> new Vector3f(((Float)$$0.get(0)).floatValue(), ((Float)$$0.get(1)).floatValue(), ((Float)$$0.get(2)).floatValue())), $$0 -> ImmutableList.of((Object)Float.valueOf((float)$$0.x()), (Object)Float.valueOf((float)$$0.y()), (Object)Float.valueOf((float)$$0.z())));
    public static final Codec<Integer> NON_NEGATIVE_INT = ExtraCodecs.intRangeWithMessage(0, Integer.MAX_VALUE, (Function<Integer, String>)((Function)$$0 -> "Value must be non-negative: " + $$0));
    public static final Codec<Integer> POSITIVE_INT = ExtraCodecs.intRangeWithMessage(1, Integer.MAX_VALUE, (Function<Integer, String>)((Function)$$0 -> "Value must be positive: " + $$0));
    public static final Codec<Float> POSITIVE_FLOAT = ExtraCodecs.floatRangeMinExclusiveWithMessage(0.0f, Float.MAX_VALUE, (Function<Float, String>)((Function)$$0 -> "Value must be positive: " + $$0));
    public static final Codec<Pattern> PATTERN = Codec.STRING.comapFlatMap($$0 -> {
        try {
            return DataResult.success((Object)Pattern.compile((String)$$0));
        }
        catch (PatternSyntaxException $$1) {
            return DataResult.error((String)("Invalid regex pattern '" + $$0 + "': " + $$1.getMessage()));
        }
    }, Pattern::pattern);
    public static final Codec<Instant> INSTANT_ISO8601 = ExtraCodecs.instantCodec(DateTimeFormatter.ISO_INSTANT);
    public static final Codec<byte[]> BASE64_STRING = Codec.STRING.comapFlatMap($$0 -> {
        try {
            return DataResult.success((Object)Base64.getDecoder().decode($$0));
        }
        catch (IllegalArgumentException $$1) {
            return DataResult.error((String)"Malformed base64 string");
        }
    }, $$0 -> Base64.getEncoder().encodeToString($$0));
    public static final Codec<TagOrElementLocation> TAG_OR_ELEMENT_ID = Codec.STRING.comapFlatMap($$02 -> $$02.startsWith("#") ? ResourceLocation.read($$02.substring(1)).map($$0 -> new TagOrElementLocation((ResourceLocation)$$0, true)) : ResourceLocation.read($$02).map($$0 -> new TagOrElementLocation((ResourceLocation)$$0, false)), TagOrElementLocation::decoratedId);
    public static final Function<Optional<Long>, OptionalLong> toOptionalLong = $$0 -> (OptionalLong)$$0.map(OptionalLong::of).orElseGet(OptionalLong::empty);
    public static final Function<OptionalLong, Optional<Long>> fromOptionalLong = $$0 -> $$0.isPresent() ? Optional.of((Object)$$0.getAsLong()) : Optional.empty();
    public static final Codec<BitSet> BIT_SET = Codec.LONG_STREAM.xmap($$0 -> BitSet.valueOf((long[])$$0.toArray()), $$0 -> Arrays.stream((long[])$$0.toLongArray()));
    private static final Codec<Property> PROPERTY = RecordCodecBuilder.create($$02 -> $$02.group((App)Codec.STRING.fieldOf("name").forGetter(Property::getName), (App)Codec.STRING.fieldOf("value").forGetter(Property::getValue), (App)Codec.STRING.optionalFieldOf("signature").forGetter($$0 -> Optional.ofNullable((Object)$$0.getSignature()))).apply((Applicative)$$02, ($$0, $$1, $$2) -> new Property($$0, $$1, (String)$$2.orElse(null))));
    @VisibleForTesting
    public static final Codec<PropertyMap> PROPERTY_MAP = Codec.either((Codec)Codec.unboundedMap((Codec)Codec.STRING, (Codec)Codec.STRING.listOf()), (Codec)PROPERTY.listOf()).xmap($$0 -> {
        PropertyMap $$13 = new PropertyMap();
        $$0.ifLeft($$12 -> $$12.forEach(($$1, $$2) -> {
            for (String $$3 : $$2) {
                $$13.put($$1, (Object)new Property($$1, $$3));
            }
        })).ifRight($$1 -> {
            for (Property $$2 : $$1) {
                $$13.put((Object)$$2.getName(), (Object)$$2);
            }
        });
        return $$13;
    }, $$0 -> Either.right((Object)$$0.values().stream().toList()));
    public static final Codec<GameProfile> GAME_PROFILE = RecordCodecBuilder.create($$02 -> $$02.group((App)Codec.mapPair((MapCodec)UUIDUtil.AUTHLIB_CODEC.xmap(Optional::of, $$0 -> (UUID)$$0.orElse(null)).optionalFieldOf("id", (Object)Optional.empty()), (MapCodec)Codec.STRING.xmap(Optional::of, $$0 -> (String)$$0.orElse(null)).optionalFieldOf("name", (Object)Optional.empty())).flatXmap(ExtraCodecs::mapIdNameToGameProfile, ExtraCodecs::mapGameProfileToIdName).forGetter(Function.identity()), (App)PROPERTY_MAP.optionalFieldOf("properties", (Object)new PropertyMap()).forGetter(GameProfile::getProperties)).apply((Applicative)$$02, ($$0, $$12) -> {
        $$12.forEach(($$1, $$2) -> $$0.getProperties().put($$1, $$2));
        return $$0;
    }));

    public static <F, S> Codec<Either<F, S>> xor(Codec<F> $$0, Codec<S> $$1) {
        return new XorCodec<F, S>($$0, $$1);
    }

    public static <P, I> Codec<I> intervalCodec(Codec<P> $$0, String $$13, String $$22, BiFunction<P, P, DataResult<I>> $$32, Function<I, P> $$4, Function<I, P> $$5) {
        Codec $$6 = Codec.list($$0).comapFlatMap($$12 -> Util.fixedSize($$12, 2).flatMap($$1 -> {
            Object $$2 = $$1.get(0);
            Object $$3 = $$1.get(1);
            return (DataResult)$$32.apply($$2, $$3);
        }), $$2 -> ImmutableList.of((Object)$$4.apply($$2), (Object)$$5.apply($$2)));
        Codec $$7 = RecordCodecBuilder.create($$3 -> $$3.group((App)$$0.fieldOf($$13).forGetter(Pair::getFirst), (App)$$0.fieldOf($$22).forGetter(Pair::getSecond)).apply((Applicative)$$3, Pair::of)).comapFlatMap($$1 -> (DataResult)$$32.apply($$1.getFirst(), $$1.getSecond()), $$2 -> Pair.of((Object)$$4.apply($$2), (Object)$$5.apply($$2)));
        Codec $$8 = new EitherCodec($$6, $$7).xmap($$02 -> $$02.map($$0 -> $$0, $$0 -> $$0), Either::left);
        return Codec.either($$0, (Codec)$$8).comapFlatMap($$12 -> (DataResult)$$12.map($$1 -> (DataResult)$$32.apply($$1, $$1), DataResult::success), $$2 -> {
            Object $$4;
            Object $$3 = $$4.apply($$2);
            if (Objects.equals((Object)$$3, (Object)($$4 = $$5.apply($$2)))) {
                return Either.left((Object)$$3);
            }
            return Either.right((Object)$$2);
        });
    }

    public static <A> Codec.ResultFunction<A> orElsePartial(final A $$0) {
        return new Codec.ResultFunction<A>(){

            public <T> DataResult<Pair<A, T>> apply(DynamicOps<T> $$02, T $$1, DataResult<Pair<A, T>> $$2) {
                MutableObject $$3 = new MutableObject();
                Optional $$4 = $$2.resultOrPartial(arg_0 -> ((MutableObject)$$3).setValue(arg_0));
                if ($$4.isPresent()) {
                    return $$2;
                }
                return DataResult.error((String)("(" + (String)$$3.getValue() + " -> using default)"), (Object)Pair.of((Object)$$0, $$1));
            }

            public <T> DataResult<T> coApply(DynamicOps<T> $$02, A $$1, DataResult<T> $$2) {
                return $$2;
            }

            public String toString() {
                return "OrElsePartial[" + $$0 + "]";
            }
        };
    }

    public static <E> Codec<E> idResolverCodec(ToIntFunction<E> $$0, IntFunction<E> $$12, int $$22) {
        return Codec.INT.flatXmap($$1 -> (DataResult)Optional.ofNullable((Object)$$12.apply($$1.intValue())).map(DataResult::success).orElseGet(() -> DataResult.error((String)("Unknown element id: " + $$1))), $$2 -> {
            int $$3 = $$0.applyAsInt($$2);
            return $$3 == $$22 ? DataResult.error((String)("Element with unknown id: " + $$2)) : DataResult.success((Object)$$3);
        });
    }

    public static <E> Codec<E> stringResolverCodec(Function<E, String> $$0, Function<String, E> $$12) {
        return Codec.STRING.flatXmap($$1 -> (DataResult)Optional.ofNullable((Object)$$12.apply($$1)).map(DataResult::success).orElseGet(() -> DataResult.error((String)("Unknown element name:" + $$1))), $$1 -> (DataResult)Optional.ofNullable((Object)((String)$$0.apply($$1))).map(DataResult::success).orElseGet(() -> DataResult.error((String)("Element with unknown name: " + $$1))));
    }

    public static <E> Codec<E> orCompressed(final Codec<E> $$0, final Codec<E> $$1) {
        return new Codec<E>(){

            public <T> DataResult<T> encode(E $$02, DynamicOps<T> $$12, T $$2) {
                if ($$12.compressMaps()) {
                    return $$1.encode($$02, $$12, $$2);
                }
                return $$0.encode($$02, $$12, $$2);
            }

            public <T> DataResult<Pair<E, T>> decode(DynamicOps<T> $$02, T $$12) {
                if ($$02.compressMaps()) {
                    return $$1.decode($$02, $$12);
                }
                return $$0.decode($$02, $$12);
            }

            public String toString() {
                return $$0 + " orCompressed " + $$1;
            }
        };
    }

    public static <E> Codec<E> overrideLifecycle(Codec<E> $$0, final Function<E, Lifecycle> $$1, final Function<E, Lifecycle> $$2) {
        return $$0.mapResult(new Codec.ResultFunction<E>(){

            public <T> DataResult<Pair<E, T>> apply(DynamicOps<T> $$0, T $$12, DataResult<Pair<E, T>> $$22) {
                return (DataResult)$$22.result().map($$2 -> $$22.setLifecycle((Lifecycle)$$1.apply($$2.getFirst()))).orElse($$22);
            }

            public <T> DataResult<T> coApply(DynamicOps<T> $$0, E $$12, DataResult<T> $$22) {
                return $$22.setLifecycle((Lifecycle)$$2.apply($$12));
            }

            public String toString() {
                return "WithLifecycle[" + $$1 + " " + $$2 + "]";
            }
        });
    }

    private static <N extends Number> Function<N, DataResult<N>> checkRangeWithMessage(N $$0, N $$1, Function<N, String> $$2) {
        return $$3 -> {
            if (((Comparable)$$3).compareTo((Object)$$0) >= 0 && ((Comparable)$$3).compareTo((Object)$$1) <= 0) {
                return DataResult.success((Object)$$3);
            }
            return DataResult.error((String)((String)$$2.apply($$3)));
        };
    }

    private static Codec<Integer> intRangeWithMessage(int $$0, int $$1, Function<Integer, String> $$2) {
        Function<Integer, DataResult<Integer>> $$3 = ExtraCodecs.checkRangeWithMessage($$0, $$1, $$2);
        return Codec.INT.flatXmap($$3, $$3);
    }

    private static <N extends Number> Function<N, DataResult<N>> checkRangeMinExclusiveWithMessage(N $$0, N $$1, Function<N, String> $$2) {
        return $$3 -> {
            if (((Comparable)$$3).compareTo((Object)$$0) > 0 && ((Comparable)$$3).compareTo((Object)$$1) <= 0) {
                return DataResult.success((Object)$$3);
            }
            return DataResult.error((String)((String)$$2.apply($$3)));
        };
    }

    private static Codec<Float> floatRangeMinExclusiveWithMessage(float $$0, float $$1, Function<Float, String> $$2) {
        Function<Float, DataResult<Float>> $$3 = ExtraCodecs.checkRangeMinExclusiveWithMessage(Float.valueOf((float)$$0), Float.valueOf((float)$$1), $$2);
        return Codec.FLOAT.flatXmap($$3, $$3);
    }

    public static <T> Function<List<T>, DataResult<List<T>>> nonEmptyListCheck() {
        return $$0 -> {
            if ($$0.isEmpty()) {
                return DataResult.error((String)"List must have contents");
            }
            return DataResult.success((Object)$$0);
        };
    }

    public static <T> Codec<List<T>> nonEmptyList(Codec<List<T>> $$0) {
        return $$0.flatXmap(ExtraCodecs.nonEmptyListCheck(), ExtraCodecs.nonEmptyListCheck());
    }

    public static <T> Function<HolderSet<T>, DataResult<HolderSet<T>>> nonEmptyHolderSetCheck() {
        return $$0 -> {
            if ($$0.unwrap().right().filter(List::isEmpty).isPresent()) {
                return DataResult.error((String)"List must have contents");
            }
            return DataResult.success((Object)$$0);
        };
    }

    public static <T> Codec<HolderSet<T>> nonEmptyHolderSet(Codec<HolderSet<T>> $$0) {
        return $$0.flatXmap(ExtraCodecs.nonEmptyHolderSetCheck(), ExtraCodecs.nonEmptyHolderSetCheck());
    }

    public static <A> Codec<A> lazyInitializedCodec(Supplier<Codec<A>> $$0) {
        return new LazyInitializedCodec<A>($$0);
    }

    public static <E> MapCodec<E> retrieveContext(Function<DynamicOps<?>, DataResult<E>> $$0) {
        class ContextRetrievalCodec
        extends MapCodec<E> {
            final /* synthetic */ Function val$getter;

            ContextRetrievalCodec(Function function) {
                this.val$getter = function;
            }

            public <T> RecordBuilder<T> encode(E $$0, DynamicOps<T> $$1, RecordBuilder<T> $$2) {
                return $$2;
            }

            public <T> DataResult<E> decode(DynamicOps<T> $$0, MapLike<T> $$1) {
                return (DataResult)this.val$getter.apply($$0);
            }

            public String toString() {
                return "ContextRetrievalCodec[" + this.val$getter + "]";
            }

            public <T> Stream<T> keys(DynamicOps<T> $$0) {
                return Stream.empty();
            }
        }
        return new ContextRetrievalCodec($$0);
    }

    public static <E, L extends Collection<E>, T> Function<L, DataResult<L>> ensureHomogenous(Function<E, T> $$0) {
        return $$1 -> {
            Iterator $$2 = $$1.iterator();
            if ($$2.hasNext()) {
                Object $$3 = $$0.apply($$2.next());
                while ($$2.hasNext()) {
                    Object $$4 = $$2.next();
                    Object $$5 = $$0.apply($$4);
                    if ($$5 == $$3) continue;
                    return DataResult.error((String)("Mixed type list: element " + $$4 + " had type " + $$5 + ", but list is of type " + $$3));
                }
            }
            return DataResult.success((Object)$$1, (Lifecycle)Lifecycle.stable());
        };
    }

    public static <A> Codec<A> catchDecoderException(final Codec<A> $$0) {
        return Codec.of($$0, (Decoder)new Decoder<A>(){

            public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> $$02, T $$1) {
                try {
                    return $$0.decode($$02, $$1);
                }
                catch (Exception $$2) {
                    return DataResult.error((String)("Cauch exception decoding " + $$1 + ": " + $$2.getMessage()));
                }
            }
        });
    }

    public static Codec<Instant> instantCodec(DateTimeFormatter $$0) {
        return Codec.STRING.comapFlatMap($$1 -> {
            try {
                return DataResult.success((Object)Instant.from((TemporalAccessor)$$0.parse((CharSequence)$$1)));
            }
            catch (Exception $$2) {
                return DataResult.error((String)$$2.getMessage());
            }
        }, arg_0 -> ((DateTimeFormatter)$$0).format(arg_0));
    }

    public static MapCodec<OptionalLong> asOptionalLong(MapCodec<Optional<Long>> $$0) {
        return $$0.xmap(toOptionalLong, fromOptionalLong);
    }

    private static DataResult<GameProfile> mapIdNameToGameProfile(Pair<Optional<UUID>, Optional<String>> $$0) {
        try {
            return DataResult.success((Object)new GameProfile((UUID)((Optional)$$0.getFirst()).orElse(null), (String)((Optional)$$0.getSecond()).orElse(null)));
        }
        catch (Throwable $$1) {
            return DataResult.error((String)$$1.getMessage());
        }
    }

    private static DataResult<Pair<Optional<UUID>, Optional<String>>> mapGameProfileToIdName(GameProfile $$0) {
        return DataResult.success((Object)Pair.of((Object)Optional.ofNullable((Object)$$0.getId()), (Object)Optional.ofNullable((Object)$$0.getName())));
    }

    static final class XorCodec<F, S>
    implements Codec<Either<F, S>> {
        private final Codec<F> first;
        private final Codec<S> second;

        public XorCodec(Codec<F> $$0, Codec<S> $$1) {
            this.first = $$0;
            this.second = $$1;
        }

        public <T> DataResult<Pair<Either<F, S>, T>> decode(DynamicOps<T> $$02, T $$1) {
            DataResult $$2 = this.first.decode($$02, $$1).map($$0 -> $$0.mapFirst(Either::left));
            DataResult $$3 = this.second.decode($$02, $$1).map($$0 -> $$0.mapFirst(Either::right));
            Optional $$4 = $$2.result();
            Optional $$5 = $$3.result();
            if ($$4.isPresent() && $$5.isPresent()) {
                return DataResult.error((String)("Both alternatives read successfully, can not pick the correct one; first: " + $$4.get() + " second: " + $$5.get()), (Object)((Pair)$$4.get()));
            }
            return $$4.isPresent() ? $$2 : $$3;
        }

        public <T> DataResult<T> encode(Either<F, S> $$0, DynamicOps<T> $$1, T $$22) {
            return (DataResult)$$0.map($$2 -> this.first.encode($$2, $$1, $$22), $$2 -> this.second.encode($$2, $$1, $$22));
        }

        public boolean equals(Object $$0) {
            if (this == $$0) {
                return true;
            }
            if ($$0 == null || this.getClass() != $$0.getClass()) {
                return false;
            }
            XorCodec $$1 = (XorCodec)$$0;
            return Objects.equals(this.first, $$1.first) && Objects.equals(this.second, $$1.second);
        }

        public int hashCode() {
            return Objects.hash((Object[])new Object[]{this.first, this.second});
        }

        public String toString() {
            return "XorCodec[" + this.first + ", " + this.second + "]";
        }
    }

    static final class EitherCodec<F, S>
    implements Codec<Either<F, S>> {
        private final Codec<F> first;
        private final Codec<S> second;

        public EitherCodec(Codec<F> $$0, Codec<S> $$1) {
            this.first = $$0;
            this.second = $$1;
        }

        public <T> DataResult<Pair<Either<F, S>, T>> decode(DynamicOps<T> $$02, T $$12) {
            DataResult $$2 = this.first.decode($$02, $$12).map($$0 -> $$0.mapFirst(Either::left));
            if (!$$2.error().isPresent()) {
                return $$2;
            }
            DataResult $$3 = this.second.decode($$02, $$12).map($$0 -> $$0.mapFirst(Either::right));
            if (!$$3.error().isPresent()) {
                return $$3;
            }
            return $$2.apply2(($$0, $$1) -> $$1, $$3);
        }

        public <T> DataResult<T> encode(Either<F, S> $$0, DynamicOps<T> $$1, T $$22) {
            return (DataResult)$$0.map($$2 -> this.first.encode($$2, $$1, $$22), $$2 -> this.second.encode($$2, $$1, $$22));
        }

        public boolean equals(Object $$0) {
            if (this == $$0) {
                return true;
            }
            if ($$0 == null || this.getClass() != $$0.getClass()) {
                return false;
            }
            EitherCodec $$1 = (EitherCodec)$$0;
            return Objects.equals(this.first, $$1.first) && Objects.equals(this.second, $$1.second);
        }

        public int hashCode() {
            return Objects.hash((Object[])new Object[]{this.first, this.second});
        }

        public String toString() {
            return "EitherCodec[" + this.first + ", " + this.second + "]";
        }
    }

    record LazyInitializedCodec<A>(Supplier<Codec<A>> delegate) implements Codec<A>
    {
        LazyInitializedCodec(Supplier<Codec<A>> $$0) {
            this.delegate = $$0 = Suppliers.memoize(() -> $$0.get());
        }

        public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> $$0, T $$1) {
            return ((Codec)this.delegate.get()).decode($$0, $$1);
        }

        public <T> DataResult<T> encode(A $$0, DynamicOps<T> $$1, T $$2) {
            return ((Codec)this.delegate.get()).encode($$0, $$1, $$2);
        }
    }

    public record TagOrElementLocation(ResourceLocation id, boolean tag) {
        public String toString() {
            return this.decoratedId();
        }

        private String decoratedId() {
            return this.tag ? "#" + this.id : this.id.toString();
        }
    }
}