/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.ListBuilder
 *  com.mojang.serialization.ListBuilder$Builder
 *  com.mojang.serialization.MapLike
 *  com.mojang.serialization.RecordBuilder
 *  com.mojang.serialization.RecordBuilder$MapBuilder
 *  java.lang.Boolean
 *  java.lang.Number
 *  java.lang.Object
 *  java.lang.String
 *  java.nio.ByteBuffer
 *  java.util.List
 *  java.util.function.BiConsumer
 *  java.util.function.Consumer
 *  java.util.stream.IntStream
 *  java.util.stream.LongStream
 *  java.util.stream.Stream
 */
package net.minecraft.resources;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.ListBuilder;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public abstract class DelegatingOps<T>
implements DynamicOps<T> {
    protected final DynamicOps<T> delegate;

    protected DelegatingOps(DynamicOps<T> $$0) {
        this.delegate = $$0;
    }

    public T empty() {
        return (T)this.delegate.empty();
    }

    public <U> U convertTo(DynamicOps<U> $$0, T $$1) {
        return (U)this.delegate.convertTo($$0, $$1);
    }

    public DataResult<Number> getNumberValue(T $$0) {
        return this.delegate.getNumberValue($$0);
    }

    public T createNumeric(Number $$0) {
        return (T)this.delegate.createNumeric($$0);
    }

    public T createByte(byte $$0) {
        return (T)this.delegate.createByte($$0);
    }

    public T createShort(short $$0) {
        return (T)this.delegate.createShort($$0);
    }

    public T createInt(int $$0) {
        return (T)this.delegate.createInt($$0);
    }

    public T createLong(long $$0) {
        return (T)this.delegate.createLong($$0);
    }

    public T createFloat(float $$0) {
        return (T)this.delegate.createFloat($$0);
    }

    public T createDouble(double $$0) {
        return (T)this.delegate.createDouble($$0);
    }

    public DataResult<Boolean> getBooleanValue(T $$0) {
        return this.delegate.getBooleanValue($$0);
    }

    public T createBoolean(boolean $$0) {
        return (T)this.delegate.createBoolean($$0);
    }

    public DataResult<String> getStringValue(T $$0) {
        return this.delegate.getStringValue($$0);
    }

    public T createString(String $$0) {
        return (T)this.delegate.createString($$0);
    }

    public DataResult<T> mergeToList(T $$0, T $$1) {
        return this.delegate.mergeToList($$0, $$1);
    }

    public DataResult<T> mergeToList(T $$0, List<T> $$1) {
        return this.delegate.mergeToList($$0, $$1);
    }

    public DataResult<T> mergeToMap(T $$0, T $$1, T $$2) {
        return this.delegate.mergeToMap($$0, $$1, $$2);
    }

    public DataResult<T> mergeToMap(T $$0, MapLike<T> $$1) {
        return this.delegate.mergeToMap($$0, $$1);
    }

    public DataResult<Stream<Pair<T, T>>> getMapValues(T $$0) {
        return this.delegate.getMapValues($$0);
    }

    public DataResult<Consumer<BiConsumer<T, T>>> getMapEntries(T $$0) {
        return this.delegate.getMapEntries($$0);
    }

    public T createMap(Stream<Pair<T, T>> $$0) {
        return (T)this.delegate.createMap($$0);
    }

    public DataResult<MapLike<T>> getMap(T $$0) {
        return this.delegate.getMap($$0);
    }

    public DataResult<Stream<T>> getStream(T $$0) {
        return this.delegate.getStream($$0);
    }

    public DataResult<Consumer<Consumer<T>>> getList(T $$0) {
        return this.delegate.getList($$0);
    }

    public T createList(Stream<T> $$0) {
        return (T)this.delegate.createList($$0);
    }

    public DataResult<ByteBuffer> getByteBuffer(T $$0) {
        return this.delegate.getByteBuffer($$0);
    }

    public T createByteList(ByteBuffer $$0) {
        return (T)this.delegate.createByteList($$0);
    }

    public DataResult<IntStream> getIntStream(T $$0) {
        return this.delegate.getIntStream($$0);
    }

    public T createIntList(IntStream $$0) {
        return (T)this.delegate.createIntList($$0);
    }

    public DataResult<LongStream> getLongStream(T $$0) {
        return this.delegate.getLongStream($$0);
    }

    public T createLongList(LongStream $$0) {
        return (T)this.delegate.createLongList($$0);
    }

    public T remove(T $$0, String $$1) {
        return (T)this.delegate.remove($$0, $$1);
    }

    public boolean compressMaps() {
        return this.delegate.compressMaps();
    }

    public ListBuilder<T> listBuilder() {
        return new ListBuilder.Builder((DynamicOps)this);
    }

    public RecordBuilder<T> mapBuilder() {
        return new RecordBuilder.MapBuilder((DynamicOps)this);
    }
}