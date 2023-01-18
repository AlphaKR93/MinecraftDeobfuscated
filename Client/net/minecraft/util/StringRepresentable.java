/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.Keyable
 *  java.lang.Deprecated
 *  java.lang.Enum
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Arrays
 *  java.util.Map
 *  java.util.Objects
 *  java.util.function.Function
 *  java.util.function.Supplier
 *  java.util.stream.Collectors
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 */
package net.minecraft.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.util.ExtraCodecs;

public interface StringRepresentable {
    public static final int PRE_BUILT_MAP_THRESHOLD = 16;

    public String getSerializedName();

    public static <E extends Enum<E>> EnumCodec<E> fromEnum(Supplier<E[]> $$02) {
        Object[] $$12 = (Enum[])$$02.get();
        if ($$12.length > 16) {
            Map $$2 = (Map)Arrays.stream((Object[])$$12).collect(Collectors.toMap($$0 -> ((StringRepresentable)$$0).getSerializedName(), $$0 -> $$0));
            return new EnumCodec((Enum[])$$12, $$1 -> $$1 == null ? null : (Enum)$$2.get($$1));
        }
        return new EnumCodec((Enum[])$$12, arg_0 -> StringRepresentable.lambda$fromEnum$3((Enum[])$$12, arg_0));
    }

    public static Keyable keys(final StringRepresentable[] $$0) {
        return new Keyable(){

            public <T> Stream<T> keys(DynamicOps<T> $$02) {
                return Arrays.stream((Object[])$$0).map(StringRepresentable::getSerializedName).map(arg_0 -> $$02.createString(arg_0));
            }
        };
    }

    private static /* synthetic */ Enum lambda$fromEnum$3(Enum[] $$0, String $$1) {
        for (Enum $$2 : $$0) {
            if (!((StringRepresentable)((Object)$$2)).getSerializedName().equals((Object)$$1)) continue;
            return $$2;
        }
        return null;
    }

    @Deprecated
    public static class EnumCodec<E extends Enum<E>>
    implements Codec<E> {
        private final Codec<E> codec;
        private final Function<String, E> resolver;

        public EnumCodec(E[] $$02, Function<String, E> $$12) {
            this.codec = ExtraCodecs.orCompressed(ExtraCodecs.stringResolverCodec($$0 -> ((StringRepresentable)$$0).getSerializedName(), $$12), ExtraCodecs.idResolverCodec($$0 -> ((Enum)$$0).ordinal(), $$1 -> $$1 >= 0 && $$1 < $$02.length ? $$02[$$1] : null, -1));
            this.resolver = $$12;
        }

        public <T> DataResult<Pair<E, T>> decode(DynamicOps<T> $$0, T $$1) {
            return this.codec.decode($$0, $$1);
        }

        public <T> DataResult<T> encode(E $$0, DynamicOps<T> $$1, T $$2) {
            return this.codec.encode($$0, $$1, $$2);
        }

        @Nullable
        public E byName(@Nullable String $$0) {
            return (E)((Enum)this.resolver.apply((Object)$$0));
        }

        public E byName(@Nullable String $$0, E $$1) {
            return (E)((Enum)Objects.requireNonNullElse(this.byName($$0), $$1));
        }
    }
}