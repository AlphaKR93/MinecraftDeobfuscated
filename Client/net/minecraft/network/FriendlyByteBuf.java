/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Multimap
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.properties.Property
 *  com.mojang.authlib.properties.PropertyMap
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DynamicOps
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.ByteBufAllocator
 *  io.netty.buffer.ByteBufInputStream
 *  io.netty.buffer.ByteBufOutputStream
 *  io.netty.handler.codec.DecoderException
 *  io.netty.handler.codec.EncoderException
 *  io.netty.util.ByteProcessor
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntList
 *  java.io.DataInput
 *  java.io.DataOutput
 *  java.io.IOException
 *  java.io.InputStream
 *  java.io.OutputStream
 *  java.lang.CharSequence
 *  java.lang.Class
 *  java.lang.Deprecated
 *  java.lang.Enum
 *  java.lang.FunctionalInterface
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.RuntimeException
 *  java.lang.Short
 *  java.lang.String
 *  java.lang.Throwable
 *  java.nio.ByteBuffer
 *  java.nio.ByteOrder
 *  java.nio.channels.FileChannel
 *  java.nio.channels.GatheringByteChannel
 *  java.nio.channels.ScatteringByteChannel
 *  java.nio.charset.Charset
 *  java.nio.charset.StandardCharsets
 *  java.security.PublicKey
 *  java.time.Instant
 *  java.util.Arrays
 *  java.util.BitSet
 *  java.util.Collection
 *  java.util.Date
 *  java.util.EnumSet
 *  java.util.List
 *  java.util.Map
 *  java.util.Optional
 *  java.util.UUID
 *  java.util.function.BiConsumer
 *  java.util.function.Consumer
 *  java.util.function.Function
 *  java.util.function.IntFunction
 *  javax.annotation.Nullable
 */
package net.minecraft.network;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import io.netty.util.ByteProcessor;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.time.Instant;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.IdMap;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Crypt;
import net.minecraft.util.CryptException;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class FriendlyByteBuf
extends ByteBuf {
    private static final int MAX_VARINT_SIZE = 5;
    private static final int MAX_VARLONG_SIZE = 10;
    public static final int DEFAULT_NBT_QUOTA = 0x200000;
    private final ByteBuf source;
    public static final short MAX_STRING_LENGTH = Short.MAX_VALUE;
    public static final int MAX_COMPONENT_STRING_LENGTH = 262144;
    private static final int PUBLIC_KEY_SIZE = 256;
    private static final int MAX_PUBLIC_KEY_HEADER_SIZE = 256;
    private static final int MAX_PUBLIC_KEY_LENGTH = 512;

    public FriendlyByteBuf(ByteBuf $$0) {
        this.source = $$0;
    }

    public static int getVarIntSize(int $$0) {
        for (int $$1 = 1; $$1 < 5; ++$$1) {
            if (($$0 & -1 << $$1 * 7) != 0) continue;
            return $$1;
        }
        return 5;
    }

    public static int getVarLongSize(long $$0) {
        for (int $$1 = 1; $$1 < 10; ++$$1) {
            if (($$0 & -1L << $$1 * 7) != 0L) continue;
            return $$1;
        }
        return 10;
    }

    @Deprecated
    public <T> T readWithCodec(DynamicOps<Tag> $$0, Codec<T> $$12) {
        CompoundTag $$2 = this.readAnySizeNbt();
        return Util.getOrThrow($$12.parse($$0, (Object)$$2), $$1 -> new DecoderException("Failed to decode: " + $$1 + " " + $$2));
    }

    @Deprecated
    public <T> void writeWithCodec(DynamicOps<Tag> $$0, Codec<T> $$12, T $$2) {
        Tag $$3 = (Tag)Util.getOrThrow($$12.encodeStart($$0, $$2), $$1 -> new EncoderException("Failed to encode: " + $$1 + " " + $$2));
        this.writeNbt((CompoundTag)$$3);
    }

    public <T> void writeId(IdMap<T> $$0, T $$1) {
        int $$2 = $$0.getId($$1);
        if ($$2 == -1) {
            throw new IllegalArgumentException("Can't find id for '" + $$1 + "' in map " + $$0);
        }
        this.writeVarInt($$2);
    }

    public <T> void writeId(IdMap<Holder<T>> $$0, Holder<T> $$1, Writer<T> $$2) {
        switch ($$1.kind()) {
            case REFERENCE: {
                int $$3 = $$0.getId($$1);
                if ($$3 == -1) {
                    throw new IllegalArgumentException("Can't find id for '" + $$1.value() + "' in map " + $$0);
                }
                this.writeVarInt($$3 + 1);
                break;
            }
            case DIRECT: {
                this.writeVarInt(0);
                $$2.accept((Object)this, $$1.value());
            }
        }
    }

    @Nullable
    public <T> T readById(IdMap<T> $$0) {
        int $$1 = this.readVarInt();
        return $$0.byId($$1);
    }

    public <T> Holder<T> readById(IdMap<Holder<T>> $$0, Reader<T> $$1) {
        int $$2 = this.readVarInt();
        if ($$2 == 0) {
            return Holder.direct($$1.apply((Object)this));
        }
        Holder<T> $$3 = $$0.byId($$2 - 1);
        if ($$3 == null) {
            throw new IllegalArgumentException("Can't find element with id " + $$2);
        }
        return $$3;
    }

    public static <T> IntFunction<T> limitValue(IntFunction<T> $$0, int $$1) {
        return $$2 -> {
            if ($$2 > $$1) {
                throw new DecoderException("Value " + $$2 + " is larger than limit " + $$1);
            }
            return $$0.apply($$2);
        };
    }

    public <T, C extends Collection<T>> C readCollection(IntFunction<C> $$0, Reader<T> $$1) {
        int $$2 = this.readVarInt();
        Collection $$3 = (Collection)$$0.apply($$2);
        for (int $$4 = 0; $$4 < $$2; ++$$4) {
            $$3.add($$1.apply((Object)this));
        }
        return (C)$$3;
    }

    public <T> void writeCollection(Collection<T> $$0, Writer<T> $$1) {
        this.writeVarInt($$0.size());
        for (Object $$2 : $$0) {
            $$1.accept((Object)this, $$2);
        }
    }

    public <T> List<T> readList(Reader<T> $$0) {
        return (List)this.readCollection(Lists::newArrayListWithCapacity, $$0);
    }

    public IntList readIntIdList() {
        int $$0 = this.readVarInt();
        IntArrayList $$1 = new IntArrayList();
        for (int $$2 = 0; $$2 < $$0; ++$$2) {
            $$1.add(this.readVarInt());
        }
        return $$1;
    }

    public void writeIntIdList(IntList $$0) {
        this.writeVarInt($$0.size());
        $$0.forEach(this::writeVarInt);
    }

    public <K, V, M extends Map<K, V>> M readMap(IntFunction<M> $$0, Reader<K> $$1, Reader<V> $$2) {
        int $$3 = this.readVarInt();
        Map $$4 = (Map)$$0.apply($$3);
        for (int $$5 = 0; $$5 < $$3; ++$$5) {
            Object $$6 = $$1.apply((Object)this);
            Object $$7 = $$2.apply((Object)this);
            $$4.put($$6, $$7);
        }
        return (M)$$4;
    }

    public <K, V> Map<K, V> readMap(Reader<K> $$0, Reader<V> $$1) {
        return this.readMap(Maps::newHashMapWithExpectedSize, $$0, $$1);
    }

    public <K, V> void writeMap(Map<K, V> $$0, Writer<K> $$1, Writer<V> $$22) {
        this.writeVarInt($$0.size());
        $$0.forEach(($$2, $$3) -> {
            $$1.accept((Object)this, $$2);
            $$22.accept((Object)this, $$3);
        });
    }

    public void readWithCount(Consumer<FriendlyByteBuf> $$0) {
        int $$1 = this.readVarInt();
        for (int $$2 = 0; $$2 < $$1; ++$$2) {
            $$0.accept((Object)this);
        }
    }

    public <E extends Enum<E>> void writeEnumSet(EnumSet<E> $$0, Class<E> $$1) {
        Enum[] $$2 = (Enum[])$$1.getEnumConstants();
        BitSet $$3 = new BitSet($$2.length);
        for (int $$4 = 0; $$4 < $$2.length; ++$$4) {
            $$3.set($$4, $$0.contains((Object)$$2[$$4]));
        }
        this.writeFixedBitSet($$3, $$2.length);
    }

    public <E extends Enum<E>> EnumSet<E> readEnumSet(Class<E> $$0) {
        Enum[] $$1 = (Enum[])$$0.getEnumConstants();
        BitSet $$2 = this.readFixedBitSet($$1.length);
        EnumSet $$3 = EnumSet.noneOf($$0);
        for (int $$4 = 0; $$4 < $$1.length; ++$$4) {
            if (!$$2.get($$4)) continue;
            $$3.add((Object)$$1[$$4]);
        }
        return $$3;
    }

    public <T> void writeOptional(Optional<T> $$0, Writer<T> $$1) {
        if ($$0.isPresent()) {
            this.writeBoolean(true);
            $$1.accept((Object)this, $$0.get());
        } else {
            this.writeBoolean(false);
        }
    }

    public <T> Optional<T> readOptional(Reader<T> $$0) {
        if (this.readBoolean()) {
            return Optional.of((Object)$$0.apply((Object)this));
        }
        return Optional.empty();
    }

    @Nullable
    public <T> T readNullable(Reader<T> $$0) {
        if (this.readBoolean()) {
            return (T)$$0.apply((Object)this);
        }
        return null;
    }

    public <T> void writeNullable(@Nullable T $$0, Writer<T> $$1) {
        if ($$0 != null) {
            this.writeBoolean(true);
            $$1.accept((Object)this, $$0);
        } else {
            this.writeBoolean(false);
        }
    }

    public <L, R> void writeEither(Either<L, R> $$0, Writer<L> $$12, Writer<R> $$2) {
        $$0.ifLeft($$1 -> {
            this.writeBoolean(true);
            $$12.accept((Object)this, $$1);
        }).ifRight($$1 -> {
            this.writeBoolean(false);
            $$2.accept((Object)this, $$1);
        });
    }

    public <L, R> Either<L, R> readEither(Reader<L> $$0, Reader<R> $$1) {
        if (this.readBoolean()) {
            return Either.left((Object)$$0.apply((Object)this));
        }
        return Either.right((Object)$$1.apply((Object)this));
    }

    public byte[] readByteArray() {
        return this.readByteArray(this.readableBytes());
    }

    public FriendlyByteBuf writeByteArray(byte[] $$0) {
        this.writeVarInt($$0.length);
        this.writeBytes($$0);
        return this;
    }

    public byte[] readByteArray(int $$0) {
        int $$1 = this.readVarInt();
        if ($$1 > $$0) {
            throw new DecoderException("ByteArray with size " + $$1 + " is bigger than allowed " + $$0);
        }
        byte[] $$2 = new byte[$$1];
        this.readBytes($$2);
        return $$2;
    }

    public FriendlyByteBuf writeVarIntArray(int[] $$0) {
        this.writeVarInt($$0.length);
        for (int $$1 : $$0) {
            this.writeVarInt($$1);
        }
        return this;
    }

    public int[] readVarIntArray() {
        return this.readVarIntArray(this.readableBytes());
    }

    public int[] readVarIntArray(int $$0) {
        int $$1 = this.readVarInt();
        if ($$1 > $$0) {
            throw new DecoderException("VarIntArray with size " + $$1 + " is bigger than allowed " + $$0);
        }
        int[] $$2 = new int[$$1];
        for (int $$3 = 0; $$3 < $$2.length; ++$$3) {
            $$2[$$3] = this.readVarInt();
        }
        return $$2;
    }

    public FriendlyByteBuf writeLongArray(long[] $$0) {
        this.writeVarInt($$0.length);
        for (long $$1 : $$0) {
            this.writeLong($$1);
        }
        return this;
    }

    public long[] readLongArray() {
        return this.readLongArray(null);
    }

    public long[] readLongArray(@Nullable long[] $$0) {
        return this.readLongArray($$0, this.readableBytes() / 8);
    }

    public long[] readLongArray(@Nullable long[] $$0, int $$1) {
        int $$2 = this.readVarInt();
        if ($$0 == null || $$0.length != $$2) {
            if ($$2 > $$1) {
                throw new DecoderException("LongArray with size " + $$2 + " is bigger than allowed " + $$1);
            }
            $$0 = new long[$$2];
        }
        for (int $$3 = 0; $$3 < $$0.length; ++$$3) {
            $$0[$$3] = this.readLong();
        }
        return $$0;
    }

    @VisibleForTesting
    public byte[] accessByteBufWithCorrectSize() {
        int $$0 = this.writerIndex();
        byte[] $$1 = new byte[$$0];
        this.getBytes(0, $$1);
        return $$1;
    }

    public BlockPos readBlockPos() {
        return BlockPos.of(this.readLong());
    }

    public FriendlyByteBuf writeBlockPos(BlockPos $$0) {
        this.writeLong($$0.asLong());
        return this;
    }

    public ChunkPos readChunkPos() {
        return new ChunkPos(this.readLong());
    }

    public FriendlyByteBuf writeChunkPos(ChunkPos $$0) {
        this.writeLong($$0.toLong());
        return this;
    }

    public SectionPos readSectionPos() {
        return SectionPos.of(this.readLong());
    }

    public FriendlyByteBuf writeSectionPos(SectionPos $$0) {
        this.writeLong($$0.asLong());
        return this;
    }

    public GlobalPos readGlobalPos() {
        ResourceKey<Level> $$0 = this.readResourceKey(Registries.DIMENSION);
        BlockPos $$1 = this.readBlockPos();
        return GlobalPos.of($$0, $$1);
    }

    public void writeGlobalPos(GlobalPos $$0) {
        this.writeResourceKey($$0.dimension());
        this.writeBlockPos($$0.pos());
    }

    public Component readComponent() {
        MutableComponent $$0 = Component.Serializer.fromJson(this.readUtf(262144));
        if ($$0 == null) {
            throw new DecoderException("Received unexpected null component");
        }
        return $$0;
    }

    public FriendlyByteBuf writeComponent(Component $$0) {
        return this.writeUtf(Component.Serializer.toJson($$0), 262144);
    }

    public <T extends Enum<T>> T readEnum(Class<T> $$0) {
        return (T)((Enum[])$$0.getEnumConstants())[this.readVarInt()];
    }

    public FriendlyByteBuf writeEnum(Enum<?> $$0) {
        return this.writeVarInt($$0.ordinal());
    }

    public int readVarInt() {
        byte $$2;
        int $$0 = 0;
        int $$1 = 0;
        do {
            $$2 = this.readByte();
            $$0 |= ($$2 & 0x7F) << $$1++ * 7;
            if ($$1 <= 5) continue;
            throw new RuntimeException("VarInt too big");
        } while (($$2 & 0x80) == 128);
        return $$0;
    }

    public long readVarLong() {
        byte $$2;
        long $$0 = 0L;
        int $$1 = 0;
        do {
            $$2 = this.readByte();
            $$0 |= (long)($$2 & 0x7F) << $$1++ * 7;
            if ($$1 <= 10) continue;
            throw new RuntimeException("VarLong too big");
        } while (($$2 & 0x80) == 128);
        return $$0;
    }

    public FriendlyByteBuf writeUUID(UUID $$0) {
        this.writeLong($$0.getMostSignificantBits());
        this.writeLong($$0.getLeastSignificantBits());
        return this;
    }

    public UUID readUUID() {
        return new UUID(this.readLong(), this.readLong());
    }

    public FriendlyByteBuf writeVarInt(int $$0) {
        while (true) {
            if (($$0 & 0xFFFFFF80) == 0) {
                this.writeByte($$0);
                return this;
            }
            this.writeByte($$0 & 0x7F | 0x80);
            $$0 >>>= 7;
        }
    }

    public FriendlyByteBuf writeVarLong(long $$0) {
        while (true) {
            if (($$0 & 0xFFFFFFFFFFFFFF80L) == 0L) {
                this.writeByte((int)$$0);
                return this;
            }
            this.writeByte((int)($$0 & 0x7FL) | 0x80);
            $$0 >>>= 7;
        }
    }

    public FriendlyByteBuf writeNbt(@Nullable CompoundTag $$0) {
        if ($$0 == null) {
            this.writeByte(0);
        } else {
            try {
                NbtIo.write($$0, (DataOutput)new ByteBufOutputStream((ByteBuf)this));
            }
            catch (IOException $$1) {
                throw new EncoderException((Throwable)$$1);
            }
        }
        return this;
    }

    @Nullable
    public CompoundTag readNbt() {
        return this.readNbt(new NbtAccounter(0x200000L));
    }

    @Nullable
    public CompoundTag readAnySizeNbt() {
        return this.readNbt(NbtAccounter.UNLIMITED);
    }

    @Nullable
    public CompoundTag readNbt(NbtAccounter $$0) {
        int $$1 = this.readerIndex();
        byte $$2 = this.readByte();
        if ($$2 == 0) {
            return null;
        }
        this.readerIndex($$1);
        try {
            return NbtIo.read((DataInput)new ByteBufInputStream((ByteBuf)this), $$0);
        }
        catch (IOException $$3) {
            throw new EncoderException((Throwable)$$3);
        }
    }

    public FriendlyByteBuf writeItem(ItemStack $$0) {
        if ($$0.isEmpty()) {
            this.writeBoolean(false);
        } else {
            this.writeBoolean(true);
            Item $$1 = $$0.getItem();
            this.writeId(BuiltInRegistries.ITEM, $$1);
            this.writeByte($$0.getCount());
            CompoundTag $$2 = null;
            if ($$1.canBeDepleted() || $$1.shouldOverrideMultiplayerNbt()) {
                $$2 = $$0.getTag();
            }
            this.writeNbt($$2);
        }
        return this;
    }

    public ItemStack readItem() {
        if (!this.readBoolean()) {
            return ItemStack.EMPTY;
        }
        Item $$0 = this.readById(BuiltInRegistries.ITEM);
        byte $$1 = this.readByte();
        ItemStack $$2 = new ItemStack($$0, (int)$$1);
        $$2.setTag(this.readNbt());
        return $$2;
    }

    public String readUtf() {
        return this.readUtf(Short.MAX_VALUE);
    }

    public String readUtf(int $$0) {
        int $$1 = FriendlyByteBuf.getMaxEncodedUtfLength($$0);
        int $$2 = this.readVarInt();
        if ($$2 > $$1) {
            throw new DecoderException("The received encoded string buffer length is longer than maximum allowed (" + $$2 + " > " + $$1 + ")");
        }
        if ($$2 < 0) {
            throw new DecoderException("The received encoded string buffer length is less than zero! Weird string!");
        }
        String $$3 = this.toString(this.readerIndex(), $$2, StandardCharsets.UTF_8);
        this.readerIndex(this.readerIndex() + $$2);
        if ($$3.length() > $$0) {
            throw new DecoderException("The received string length is longer than maximum allowed (" + $$3.length() + " > " + $$0 + ")");
        }
        return $$3;
    }

    public FriendlyByteBuf writeUtf(String $$0) {
        return this.writeUtf($$0, Short.MAX_VALUE);
    }

    public FriendlyByteBuf writeUtf(String $$0, int $$1) {
        int $$3;
        if ($$0.length() > $$1) {
            throw new EncoderException("String too big (was " + $$0.length() + " characters, max " + $$1 + ")");
        }
        byte[] $$2 = $$0.getBytes(StandardCharsets.UTF_8);
        if ($$2.length > ($$3 = FriendlyByteBuf.getMaxEncodedUtfLength($$1))) {
            throw new EncoderException("String too big (was " + $$2.length + " bytes encoded, max " + $$3 + ")");
        }
        this.writeVarInt($$2.length);
        this.writeBytes($$2);
        return this;
    }

    private static int getMaxEncodedUtfLength(int $$0) {
        return $$0 * 3;
    }

    public ResourceLocation readResourceLocation() {
        return new ResourceLocation(this.readUtf(Short.MAX_VALUE));
    }

    public FriendlyByteBuf writeResourceLocation(ResourceLocation $$0) {
        this.writeUtf($$0.toString());
        return this;
    }

    public <T> ResourceKey<T> readResourceKey(ResourceKey<? extends Registry<T>> $$0) {
        ResourceLocation $$1 = this.readResourceLocation();
        return ResourceKey.create($$0, $$1);
    }

    public void writeResourceKey(ResourceKey<?> $$0) {
        this.writeResourceLocation($$0.location());
    }

    public Date readDate() {
        return new Date(this.readLong());
    }

    public FriendlyByteBuf writeDate(Date $$0) {
        this.writeLong($$0.getTime());
        return this;
    }

    public Instant readInstant() {
        return Instant.ofEpochMilli((long)this.readLong());
    }

    public void writeInstant(Instant $$0) {
        this.writeLong($$0.toEpochMilli());
    }

    public PublicKey readPublicKey() {
        try {
            return Crypt.byteToPublicKey(this.readByteArray(512));
        }
        catch (CryptException $$0) {
            throw new DecoderException("Malformed public key bytes", (Throwable)$$0);
        }
    }

    public FriendlyByteBuf writePublicKey(PublicKey $$0) {
        this.writeByteArray($$0.getEncoded());
        return this;
    }

    public BlockHitResult readBlockHitResult() {
        BlockPos $$0 = this.readBlockPos();
        Direction $$1 = this.readEnum(Direction.class);
        float $$2 = this.readFloat();
        float $$3 = this.readFloat();
        float $$4 = this.readFloat();
        boolean $$5 = this.readBoolean();
        return new BlockHitResult(new Vec3((double)$$0.getX() + (double)$$2, (double)$$0.getY() + (double)$$3, (double)$$0.getZ() + (double)$$4), $$1, $$0, $$5);
    }

    public void writeBlockHitResult(BlockHitResult $$0) {
        BlockPos $$1 = $$0.getBlockPos();
        this.writeBlockPos($$1);
        this.writeEnum($$0.getDirection());
        Vec3 $$2 = $$0.getLocation();
        this.writeFloat((float)($$2.x - (double)$$1.getX()));
        this.writeFloat((float)($$2.y - (double)$$1.getY()));
        this.writeFloat((float)($$2.z - (double)$$1.getZ()));
        this.writeBoolean($$0.isInside());
    }

    public BitSet readBitSet() {
        return BitSet.valueOf((long[])this.readLongArray());
    }

    public void writeBitSet(BitSet $$0) {
        this.writeLongArray($$0.toLongArray());
    }

    public BitSet readFixedBitSet(int $$0) {
        byte[] $$1 = new byte[Mth.positiveCeilDiv($$0, 8)];
        this.readBytes($$1);
        return BitSet.valueOf((byte[])$$1);
    }

    public void writeFixedBitSet(BitSet $$0, int $$1) {
        if ($$0.length() > $$1) {
            throw new EncoderException("BitSet is larger than expected size (" + $$0.length() + ">" + $$1 + ")");
        }
        byte[] $$2 = $$0.toByteArray();
        this.writeBytes(Arrays.copyOf((byte[])$$2, (int)Mth.positiveCeilDiv($$1, 8)));
    }

    public GameProfile readGameProfile() {
        UUID $$0 = this.readUUID();
        String $$1 = this.readUtf(16);
        GameProfile $$2 = new GameProfile($$0, $$1);
        $$2.getProperties().putAll((Multimap)this.readGameProfileProperties());
        return $$2;
    }

    public void writeGameProfile(GameProfile $$0) {
        this.writeUUID($$0.getId());
        this.writeUtf($$0.getName());
        this.writeGameProfileProperties($$0.getProperties());
    }

    public PropertyMap readGameProfileProperties() {
        PropertyMap $$0 = new PropertyMap();
        this.readWithCount((Consumer<FriendlyByteBuf>)((Consumer)$$1 -> {
            Property $$2 = this.readProperty();
            $$0.put((Object)$$2.getName(), (Object)$$2);
        }));
        return $$0;
    }

    public void writeGameProfileProperties(PropertyMap $$0) {
        this.writeCollection($$0.values(), FriendlyByteBuf::writeProperty);
    }

    public Property readProperty() {
        String $$0 = this.readUtf();
        String $$1 = this.readUtf();
        if (this.readBoolean()) {
            String $$2 = this.readUtf();
            return new Property($$0, $$1, $$2);
        }
        return new Property($$0, $$1);
    }

    public void writeProperty(Property $$0) {
        this.writeUtf($$0.getName());
        this.writeUtf($$0.getValue());
        if ($$0.hasSignature()) {
            this.writeBoolean(true);
            this.writeUtf($$0.getSignature());
        } else {
            this.writeBoolean(false);
        }
    }

    public int capacity() {
        return this.source.capacity();
    }

    public ByteBuf capacity(int $$0) {
        return this.source.capacity($$0);
    }

    public int maxCapacity() {
        return this.source.maxCapacity();
    }

    public ByteBufAllocator alloc() {
        return this.source.alloc();
    }

    public ByteOrder order() {
        return this.source.order();
    }

    public ByteBuf order(ByteOrder $$0) {
        return this.source.order($$0);
    }

    public ByteBuf unwrap() {
        return this.source.unwrap();
    }

    public boolean isDirect() {
        return this.source.isDirect();
    }

    public boolean isReadOnly() {
        return this.source.isReadOnly();
    }

    public ByteBuf asReadOnly() {
        return this.source.asReadOnly();
    }

    public int readerIndex() {
        return this.source.readerIndex();
    }

    public ByteBuf readerIndex(int $$0) {
        return this.source.readerIndex($$0);
    }

    public int writerIndex() {
        return this.source.writerIndex();
    }

    public ByteBuf writerIndex(int $$0) {
        return this.source.writerIndex($$0);
    }

    public ByteBuf setIndex(int $$0, int $$1) {
        return this.source.setIndex($$0, $$1);
    }

    public int readableBytes() {
        return this.source.readableBytes();
    }

    public int writableBytes() {
        return this.source.writableBytes();
    }

    public int maxWritableBytes() {
        return this.source.maxWritableBytes();
    }

    public boolean isReadable() {
        return this.source.isReadable();
    }

    public boolean isReadable(int $$0) {
        return this.source.isReadable($$0);
    }

    public boolean isWritable() {
        return this.source.isWritable();
    }

    public boolean isWritable(int $$0) {
        return this.source.isWritable($$0);
    }

    public ByteBuf clear() {
        return this.source.clear();
    }

    public ByteBuf markReaderIndex() {
        return this.source.markReaderIndex();
    }

    public ByteBuf resetReaderIndex() {
        return this.source.resetReaderIndex();
    }

    public ByteBuf markWriterIndex() {
        return this.source.markWriterIndex();
    }

    public ByteBuf resetWriterIndex() {
        return this.source.resetWriterIndex();
    }

    public ByteBuf discardReadBytes() {
        return this.source.discardReadBytes();
    }

    public ByteBuf discardSomeReadBytes() {
        return this.source.discardSomeReadBytes();
    }

    public ByteBuf ensureWritable(int $$0) {
        return this.source.ensureWritable($$0);
    }

    public int ensureWritable(int $$0, boolean $$1) {
        return this.source.ensureWritable($$0, $$1);
    }

    public boolean getBoolean(int $$0) {
        return this.source.getBoolean($$0);
    }

    public byte getByte(int $$0) {
        return this.source.getByte($$0);
    }

    public short getUnsignedByte(int $$0) {
        return this.source.getUnsignedByte($$0);
    }

    public short getShort(int $$0) {
        return this.source.getShort($$0);
    }

    public short getShortLE(int $$0) {
        return this.source.getShortLE($$0);
    }

    public int getUnsignedShort(int $$0) {
        return this.source.getUnsignedShort($$0);
    }

    public int getUnsignedShortLE(int $$0) {
        return this.source.getUnsignedShortLE($$0);
    }

    public int getMedium(int $$0) {
        return this.source.getMedium($$0);
    }

    public int getMediumLE(int $$0) {
        return this.source.getMediumLE($$0);
    }

    public int getUnsignedMedium(int $$0) {
        return this.source.getUnsignedMedium($$0);
    }

    public int getUnsignedMediumLE(int $$0) {
        return this.source.getUnsignedMediumLE($$0);
    }

    public int getInt(int $$0) {
        return this.source.getInt($$0);
    }

    public int getIntLE(int $$0) {
        return this.source.getIntLE($$0);
    }

    public long getUnsignedInt(int $$0) {
        return this.source.getUnsignedInt($$0);
    }

    public long getUnsignedIntLE(int $$0) {
        return this.source.getUnsignedIntLE($$0);
    }

    public long getLong(int $$0) {
        return this.source.getLong($$0);
    }

    public long getLongLE(int $$0) {
        return this.source.getLongLE($$0);
    }

    public char getChar(int $$0) {
        return this.source.getChar($$0);
    }

    public float getFloat(int $$0) {
        return this.source.getFloat($$0);
    }

    public double getDouble(int $$0) {
        return this.source.getDouble($$0);
    }

    public ByteBuf getBytes(int $$0, ByteBuf $$1) {
        return this.source.getBytes($$0, $$1);
    }

    public ByteBuf getBytes(int $$0, ByteBuf $$1, int $$2) {
        return this.source.getBytes($$0, $$1, $$2);
    }

    public ByteBuf getBytes(int $$0, ByteBuf $$1, int $$2, int $$3) {
        return this.source.getBytes($$0, $$1, $$2, $$3);
    }

    public ByteBuf getBytes(int $$0, byte[] $$1) {
        return this.source.getBytes($$0, $$1);
    }

    public ByteBuf getBytes(int $$0, byte[] $$1, int $$2, int $$3) {
        return this.source.getBytes($$0, $$1, $$2, $$3);
    }

    public ByteBuf getBytes(int $$0, ByteBuffer $$1) {
        return this.source.getBytes($$0, $$1);
    }

    public ByteBuf getBytes(int $$0, OutputStream $$1, int $$2) throws IOException {
        return this.source.getBytes($$0, $$1, $$2);
    }

    public int getBytes(int $$0, GatheringByteChannel $$1, int $$2) throws IOException {
        return this.source.getBytes($$0, $$1, $$2);
    }

    public int getBytes(int $$0, FileChannel $$1, long $$2, int $$3) throws IOException {
        return this.source.getBytes($$0, $$1, $$2, $$3);
    }

    public CharSequence getCharSequence(int $$0, int $$1, Charset $$2) {
        return this.source.getCharSequence($$0, $$1, $$2);
    }

    public ByteBuf setBoolean(int $$0, boolean $$1) {
        return this.source.setBoolean($$0, $$1);
    }

    public ByteBuf setByte(int $$0, int $$1) {
        return this.source.setByte($$0, $$1);
    }

    public ByteBuf setShort(int $$0, int $$1) {
        return this.source.setShort($$0, $$1);
    }

    public ByteBuf setShortLE(int $$0, int $$1) {
        return this.source.setShortLE($$0, $$1);
    }

    public ByteBuf setMedium(int $$0, int $$1) {
        return this.source.setMedium($$0, $$1);
    }

    public ByteBuf setMediumLE(int $$0, int $$1) {
        return this.source.setMediumLE($$0, $$1);
    }

    public ByteBuf setInt(int $$0, int $$1) {
        return this.source.setInt($$0, $$1);
    }

    public ByteBuf setIntLE(int $$0, int $$1) {
        return this.source.setIntLE($$0, $$1);
    }

    public ByteBuf setLong(int $$0, long $$1) {
        return this.source.setLong($$0, $$1);
    }

    public ByteBuf setLongLE(int $$0, long $$1) {
        return this.source.setLongLE($$0, $$1);
    }

    public ByteBuf setChar(int $$0, int $$1) {
        return this.source.setChar($$0, $$1);
    }

    public ByteBuf setFloat(int $$0, float $$1) {
        return this.source.setFloat($$0, $$1);
    }

    public ByteBuf setDouble(int $$0, double $$1) {
        return this.source.setDouble($$0, $$1);
    }

    public ByteBuf setBytes(int $$0, ByteBuf $$1) {
        return this.source.setBytes($$0, $$1);
    }

    public ByteBuf setBytes(int $$0, ByteBuf $$1, int $$2) {
        return this.source.setBytes($$0, $$1, $$2);
    }

    public ByteBuf setBytes(int $$0, ByteBuf $$1, int $$2, int $$3) {
        return this.source.setBytes($$0, $$1, $$2, $$3);
    }

    public ByteBuf setBytes(int $$0, byte[] $$1) {
        return this.source.setBytes($$0, $$1);
    }

    public ByteBuf setBytes(int $$0, byte[] $$1, int $$2, int $$3) {
        return this.source.setBytes($$0, $$1, $$2, $$3);
    }

    public ByteBuf setBytes(int $$0, ByteBuffer $$1) {
        return this.source.setBytes($$0, $$1);
    }

    public int setBytes(int $$0, InputStream $$1, int $$2) throws IOException {
        return this.source.setBytes($$0, $$1, $$2);
    }

    public int setBytes(int $$0, ScatteringByteChannel $$1, int $$2) throws IOException {
        return this.source.setBytes($$0, $$1, $$2);
    }

    public int setBytes(int $$0, FileChannel $$1, long $$2, int $$3) throws IOException {
        return this.source.setBytes($$0, $$1, $$2, $$3);
    }

    public ByteBuf setZero(int $$0, int $$1) {
        return this.source.setZero($$0, $$1);
    }

    public int setCharSequence(int $$0, CharSequence $$1, Charset $$2) {
        return this.source.setCharSequence($$0, $$1, $$2);
    }

    public boolean readBoolean() {
        return this.source.readBoolean();
    }

    public byte readByte() {
        return this.source.readByte();
    }

    public short readUnsignedByte() {
        return this.source.readUnsignedByte();
    }

    public short readShort() {
        return this.source.readShort();
    }

    public short readShortLE() {
        return this.source.readShortLE();
    }

    public int readUnsignedShort() {
        return this.source.readUnsignedShort();
    }

    public int readUnsignedShortLE() {
        return this.source.readUnsignedShortLE();
    }

    public int readMedium() {
        return this.source.readMedium();
    }

    public int readMediumLE() {
        return this.source.readMediumLE();
    }

    public int readUnsignedMedium() {
        return this.source.readUnsignedMedium();
    }

    public int readUnsignedMediumLE() {
        return this.source.readUnsignedMediumLE();
    }

    public int readInt() {
        return this.source.readInt();
    }

    public int readIntLE() {
        return this.source.readIntLE();
    }

    public long readUnsignedInt() {
        return this.source.readUnsignedInt();
    }

    public long readUnsignedIntLE() {
        return this.source.readUnsignedIntLE();
    }

    public long readLong() {
        return this.source.readLong();
    }

    public long readLongLE() {
        return this.source.readLongLE();
    }

    public char readChar() {
        return this.source.readChar();
    }

    public float readFloat() {
        return this.source.readFloat();
    }

    public double readDouble() {
        return this.source.readDouble();
    }

    public ByteBuf readBytes(int $$0) {
        return this.source.readBytes($$0);
    }

    public ByteBuf readSlice(int $$0) {
        return this.source.readSlice($$0);
    }

    public ByteBuf readRetainedSlice(int $$0) {
        return this.source.readRetainedSlice($$0);
    }

    public ByteBuf readBytes(ByteBuf $$0) {
        return this.source.readBytes($$0);
    }

    public ByteBuf readBytes(ByteBuf $$0, int $$1) {
        return this.source.readBytes($$0, $$1);
    }

    public ByteBuf readBytes(ByteBuf $$0, int $$1, int $$2) {
        return this.source.readBytes($$0, $$1, $$2);
    }

    public ByteBuf readBytes(byte[] $$0) {
        return this.source.readBytes($$0);
    }

    public ByteBuf readBytes(byte[] $$0, int $$1, int $$2) {
        return this.source.readBytes($$0, $$1, $$2);
    }

    public ByteBuf readBytes(ByteBuffer $$0) {
        return this.source.readBytes($$0);
    }

    public ByteBuf readBytes(OutputStream $$0, int $$1) throws IOException {
        return this.source.readBytes($$0, $$1);
    }

    public int readBytes(GatheringByteChannel $$0, int $$1) throws IOException {
        return this.source.readBytes($$0, $$1);
    }

    public CharSequence readCharSequence(int $$0, Charset $$1) {
        return this.source.readCharSequence($$0, $$1);
    }

    public int readBytes(FileChannel $$0, long $$1, int $$2) throws IOException {
        return this.source.readBytes($$0, $$1, $$2);
    }

    public ByteBuf skipBytes(int $$0) {
        return this.source.skipBytes($$0);
    }

    public ByteBuf writeBoolean(boolean $$0) {
        return this.source.writeBoolean($$0);
    }

    public ByteBuf writeByte(int $$0) {
        return this.source.writeByte($$0);
    }

    public ByteBuf writeShort(int $$0) {
        return this.source.writeShort($$0);
    }

    public ByteBuf writeShortLE(int $$0) {
        return this.source.writeShortLE($$0);
    }

    public ByteBuf writeMedium(int $$0) {
        return this.source.writeMedium($$0);
    }

    public ByteBuf writeMediumLE(int $$0) {
        return this.source.writeMediumLE($$0);
    }

    public ByteBuf writeInt(int $$0) {
        return this.source.writeInt($$0);
    }

    public ByteBuf writeIntLE(int $$0) {
        return this.source.writeIntLE($$0);
    }

    public ByteBuf writeLong(long $$0) {
        return this.source.writeLong($$0);
    }

    public ByteBuf writeLongLE(long $$0) {
        return this.source.writeLongLE($$0);
    }

    public ByteBuf writeChar(int $$0) {
        return this.source.writeChar($$0);
    }

    public ByteBuf writeFloat(float $$0) {
        return this.source.writeFloat($$0);
    }

    public ByteBuf writeDouble(double $$0) {
        return this.source.writeDouble($$0);
    }

    public ByteBuf writeBytes(ByteBuf $$0) {
        return this.source.writeBytes($$0);
    }

    public ByteBuf writeBytes(ByteBuf $$0, int $$1) {
        return this.source.writeBytes($$0, $$1);
    }

    public ByteBuf writeBytes(ByteBuf $$0, int $$1, int $$2) {
        return this.source.writeBytes($$0, $$1, $$2);
    }

    public ByteBuf writeBytes(byte[] $$0) {
        return this.source.writeBytes($$0);
    }

    public ByteBuf writeBytes(byte[] $$0, int $$1, int $$2) {
        return this.source.writeBytes($$0, $$1, $$2);
    }

    public ByteBuf writeBytes(ByteBuffer $$0) {
        return this.source.writeBytes($$0);
    }

    public int writeBytes(InputStream $$0, int $$1) throws IOException {
        return this.source.writeBytes($$0, $$1);
    }

    public int writeBytes(ScatteringByteChannel $$0, int $$1) throws IOException {
        return this.source.writeBytes($$0, $$1);
    }

    public int writeBytes(FileChannel $$0, long $$1, int $$2) throws IOException {
        return this.source.writeBytes($$0, $$1, $$2);
    }

    public ByteBuf writeZero(int $$0) {
        return this.source.writeZero($$0);
    }

    public int writeCharSequence(CharSequence $$0, Charset $$1) {
        return this.source.writeCharSequence($$0, $$1);
    }

    public int indexOf(int $$0, int $$1, byte $$2) {
        return this.source.indexOf($$0, $$1, $$2);
    }

    public int bytesBefore(byte $$0) {
        return this.source.bytesBefore($$0);
    }

    public int bytesBefore(int $$0, byte $$1) {
        return this.source.bytesBefore($$0, $$1);
    }

    public int bytesBefore(int $$0, int $$1, byte $$2) {
        return this.source.bytesBefore($$0, $$1, $$2);
    }

    public int forEachByte(ByteProcessor $$0) {
        return this.source.forEachByte($$0);
    }

    public int forEachByte(int $$0, int $$1, ByteProcessor $$2) {
        return this.source.forEachByte($$0, $$1, $$2);
    }

    public int forEachByteDesc(ByteProcessor $$0) {
        return this.source.forEachByteDesc($$0);
    }

    public int forEachByteDesc(int $$0, int $$1, ByteProcessor $$2) {
        return this.source.forEachByteDesc($$0, $$1, $$2);
    }

    public ByteBuf copy() {
        return this.source.copy();
    }

    public ByteBuf copy(int $$0, int $$1) {
        return this.source.copy($$0, $$1);
    }

    public ByteBuf slice() {
        return this.source.slice();
    }

    public ByteBuf retainedSlice() {
        return this.source.retainedSlice();
    }

    public ByteBuf slice(int $$0, int $$1) {
        return this.source.slice($$0, $$1);
    }

    public ByteBuf retainedSlice(int $$0, int $$1) {
        return this.source.retainedSlice($$0, $$1);
    }

    public ByteBuf duplicate() {
        return this.source.duplicate();
    }

    public ByteBuf retainedDuplicate() {
        return this.source.retainedDuplicate();
    }

    public int nioBufferCount() {
        return this.source.nioBufferCount();
    }

    public ByteBuffer nioBuffer() {
        return this.source.nioBuffer();
    }

    public ByteBuffer nioBuffer(int $$0, int $$1) {
        return this.source.nioBuffer($$0, $$1);
    }

    public ByteBuffer internalNioBuffer(int $$0, int $$1) {
        return this.source.internalNioBuffer($$0, $$1);
    }

    public ByteBuffer[] nioBuffers() {
        return this.source.nioBuffers();
    }

    public ByteBuffer[] nioBuffers(int $$0, int $$1) {
        return this.source.nioBuffers($$0, $$1);
    }

    public boolean hasArray() {
        return this.source.hasArray();
    }

    public byte[] array() {
        return this.source.array();
    }

    public int arrayOffset() {
        return this.source.arrayOffset();
    }

    public boolean hasMemoryAddress() {
        return this.source.hasMemoryAddress();
    }

    public long memoryAddress() {
        return this.source.memoryAddress();
    }

    public String toString(Charset $$0) {
        return this.source.toString($$0);
    }

    public String toString(int $$0, int $$1, Charset $$2) {
        return this.source.toString($$0, $$1, $$2);
    }

    public int hashCode() {
        return this.source.hashCode();
    }

    public boolean equals(Object $$0) {
        return this.source.equals($$0);
    }

    public int compareTo(ByteBuf $$0) {
        return this.source.compareTo($$0);
    }

    public String toString() {
        return this.source.toString();
    }

    public ByteBuf retain(int $$0) {
        return this.source.retain($$0);
    }

    public ByteBuf retain() {
        return this.source.retain();
    }

    public ByteBuf touch() {
        return this.source.touch();
    }

    public ByteBuf touch(Object $$0) {
        return this.source.touch($$0);
    }

    public int refCnt() {
        return this.source.refCnt();
    }

    public boolean release() {
        return this.source.release();
    }

    public boolean release(int $$0) {
        return this.source.release($$0);
    }

    @FunctionalInterface
    public static interface Writer<T>
    extends BiConsumer<FriendlyByteBuf, T> {
        default public Writer<Optional<T>> asOptional() {
            return ($$0, $$1) -> $$0.writeOptional($$1, this);
        }
    }

    @FunctionalInterface
    public static interface Reader<T>
    extends Function<FriendlyByteBuf, T> {
        default public Reader<Optional<T>> asOptional() {
            return $$0 -> $$0.readOptional(this);
        }
    }
}