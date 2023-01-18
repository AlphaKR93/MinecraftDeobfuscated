/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.Unpooled
 *  java.lang.FunctionalInterface
 *  java.lang.Object
 *  java.lang.RuntimeException
 *  java.util.List
 *  java.util.Map$Entry
 *  java.util.function.Consumer
 *  javax.annotation.Nullable
 */
package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Heightmap;

public class ClientboundLevelChunkPacketData {
    private static final int TWO_MEGABYTES = 0x200000;
    private final CompoundTag heightmaps;
    private final byte[] buffer;
    private final List<BlockEntityInfo> blockEntitiesData;

    public ClientboundLevelChunkPacketData(LevelChunk $$0) {
        this.heightmaps = new CompoundTag();
        for (Map.Entry $$1 : $$0.getHeightmaps()) {
            if (!((Heightmap.Types)$$1.getKey()).sendToClient()) continue;
            this.heightmaps.put(((Heightmap.Types)$$1.getKey()).getSerializationKey(), new LongArrayTag(((Heightmap)$$1.getValue()).getRawData()));
        }
        this.buffer = new byte[ClientboundLevelChunkPacketData.calculateChunkSize($$0)];
        ClientboundLevelChunkPacketData.extractChunkData(new FriendlyByteBuf(this.getWriteBuffer()), $$0);
        this.blockEntitiesData = Lists.newArrayList();
        for (Map.Entry $$2 : $$0.getBlockEntities().entrySet()) {
            this.blockEntitiesData.add((Object)BlockEntityInfo.create((BlockEntity)$$2.getValue()));
        }
    }

    public ClientboundLevelChunkPacketData(FriendlyByteBuf $$0, int $$1, int $$2) {
        this.heightmaps = $$0.readNbt();
        if (this.heightmaps == null) {
            throw new RuntimeException("Can't read heightmap in packet for [" + $$1 + ", " + $$2 + "]");
        }
        int $$3 = $$0.readVarInt();
        if ($$3 > 0x200000) {
            throw new RuntimeException("Chunk Packet trying to allocate too much memory on read.");
        }
        this.buffer = new byte[$$3];
        $$0.readBytes(this.buffer);
        this.blockEntitiesData = $$0.readList(BlockEntityInfo::new);
    }

    public void write(FriendlyByteBuf $$02) {
        $$02.writeNbt(this.heightmaps);
        $$02.writeVarInt(this.buffer.length);
        $$02.writeBytes(this.buffer);
        $$02.writeCollection(this.blockEntitiesData, ($$0, $$1) -> $$1.write((FriendlyByteBuf)((Object)$$0)));
    }

    private static int calculateChunkSize(LevelChunk $$0) {
        int $$1 = 0;
        for (LevelChunkSection $$2 : $$0.getSections()) {
            $$1 += $$2.getSerializedSize();
        }
        return $$1;
    }

    private ByteBuf getWriteBuffer() {
        ByteBuf $$0 = Unpooled.wrappedBuffer((byte[])this.buffer);
        $$0.writerIndex(0);
        return $$0;
    }

    public static void extractChunkData(FriendlyByteBuf $$0, LevelChunk $$1) {
        for (LevelChunkSection $$2 : $$1.getSections()) {
            $$2.write($$0);
        }
    }

    public Consumer<BlockEntityTagOutput> getBlockEntitiesTagsConsumer(int $$0, int $$1) {
        return $$2 -> this.getBlockEntitiesTags((BlockEntityTagOutput)$$2, $$0, $$1);
    }

    private void getBlockEntitiesTags(BlockEntityTagOutput $$0, int $$1, int $$2) {
        int $$3 = 16 * $$1;
        int $$4 = 16 * $$2;
        BlockPos.MutableBlockPos $$5 = new BlockPos.MutableBlockPos();
        for (BlockEntityInfo $$6 : this.blockEntitiesData) {
            int $$7 = $$3 + SectionPos.sectionRelative($$6.packedXZ >> 4);
            int $$8 = $$4 + SectionPos.sectionRelative($$6.packedXZ);
            $$5.set($$7, $$6.y, $$8);
            $$0.accept($$5, $$6.type, $$6.tag);
        }
    }

    public FriendlyByteBuf getReadBuffer() {
        return new FriendlyByteBuf(Unpooled.wrappedBuffer((byte[])this.buffer));
    }

    public CompoundTag getHeightmaps() {
        return this.heightmaps;
    }

    static class BlockEntityInfo {
        final int packedXZ;
        final int y;
        final BlockEntityType<?> type;
        @Nullable
        final CompoundTag tag;

        private BlockEntityInfo(int $$0, int $$1, BlockEntityType<?> $$2, @Nullable CompoundTag $$3) {
            this.packedXZ = $$0;
            this.y = $$1;
            this.type = $$2;
            this.tag = $$3;
        }

        private BlockEntityInfo(FriendlyByteBuf $$0) {
            this.packedXZ = $$0.readByte();
            this.y = $$0.readShort();
            this.type = $$0.readById(BuiltInRegistries.BLOCK_ENTITY_TYPE);
            this.tag = $$0.readNbt();
        }

        void write(FriendlyByteBuf $$0) {
            $$0.writeByte(this.packedXZ);
            $$0.writeShort(this.y);
            $$0.writeId(BuiltInRegistries.BLOCK_ENTITY_TYPE, this.type);
            $$0.writeNbt(this.tag);
        }

        static BlockEntityInfo create(BlockEntity $$0) {
            CompoundTag $$1 = $$0.getUpdateTag();
            BlockPos $$2 = $$0.getBlockPos();
            int $$3 = SectionPos.sectionRelative($$2.getX()) << 4 | SectionPos.sectionRelative($$2.getZ());
            return new BlockEntityInfo($$3, $$2.getY(), $$0.getType(), $$1.isEmpty() ? null : $$1);
        }
    }

    @FunctionalInterface
    public static interface BlockEntityTagOutput {
        public void accept(BlockPos var1, BlockEntityType<?> var2, @Nullable CompoundTag var3);
    }
}