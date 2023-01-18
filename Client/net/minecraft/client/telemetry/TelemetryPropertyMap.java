/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.MapLike
 *  com.mojang.serialization.RecordBuilder
 *  it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap
 *  java.lang.Object
 *  java.lang.String
 *  java.util.List
 *  java.util.Map
 *  java.util.Set
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 */
package net.minecraft.client.telemetry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.telemetry.TelemetryProperty;

public class TelemetryPropertyMap {
    final Map<TelemetryProperty<?>, Object> entries;

    TelemetryPropertyMap(Map<TelemetryProperty<?>, Object> $$0) {
        this.entries = $$0;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Codec<TelemetryPropertyMap> createCodec(final List<TelemetryProperty<?>> $$0) {
        return new MapCodec<TelemetryPropertyMap>(){

            public <T> RecordBuilder<T> encode(TelemetryPropertyMap $$02, DynamicOps<T> $$1, RecordBuilder<T> $$2) {
                RecordBuilder<T> $$3 = $$2;
                for (TelemetryProperty $$4 : $$0) {
                    $$3 = this.encodeProperty($$02, $$3, $$4);
                }
                return $$3;
            }

            private <T, V> RecordBuilder<T> encodeProperty(TelemetryPropertyMap $$02, RecordBuilder<T> $$1, TelemetryProperty<V> $$2) {
                V $$3 = $$02.get($$2);
                if ($$3 != null) {
                    return $$1.add($$2.id(), $$3, $$2.codec());
                }
                return $$1;
            }

            public <T> DataResult<TelemetryPropertyMap> decode(DynamicOps<T> $$02, MapLike<T> $$1) {
                DataResult<Builder> $$2 = DataResult.success((Object)new Builder());
                for (TelemetryProperty $$3 : $$0) {
                    $$2 = this.decodeProperty($$2, $$02, $$1, $$3);
                }
                return $$2.map(Builder::build);
            }

            private <T, V> DataResult<Builder> decodeProperty(DataResult<Builder> $$02, DynamicOps<T> $$12, MapLike<T> $$22, TelemetryProperty<V> $$3) {
                Object $$4 = $$22.get($$3.id());
                if ($$4 != null) {
                    DataResult $$5 = $$3.codec().parse($$12, $$4);
                    return $$02.apply2stable(($$1, $$2) -> $$1.put($$3, $$2), $$5);
                }
                return $$02;
            }

            public <T> Stream<T> keys(DynamicOps<T> $$02) {
                return $$0.stream().map(TelemetryProperty::id).map(arg_0 -> $$02.createString(arg_0));
            }
        }.codec();
    }

    @Nullable
    public <T> T get(TelemetryProperty<T> $$0) {
        return (T)this.entries.get($$0);
    }

    public String toString() {
        return this.entries.toString();
    }

    public Set<TelemetryProperty<?>> propertySet() {
        return this.entries.keySet();
    }

    public static class Builder {
        private final Map<TelemetryProperty<?>, Object> entries = new Reference2ObjectOpenHashMap();

        Builder() {
        }

        public <T> Builder put(TelemetryProperty<T> $$0, T $$1) {
            this.entries.put($$0, $$1);
            return this;
        }

        public Builder putAll(TelemetryPropertyMap $$0) {
            this.entries.putAll($$0.entries);
            return this;
        }

        public TelemetryPropertyMap build() {
            return new TelemetryPropertyMap(this.entries);
        }
    }
}