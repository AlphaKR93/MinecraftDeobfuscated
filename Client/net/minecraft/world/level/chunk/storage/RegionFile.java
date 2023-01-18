/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.mojang.logging.LogUtils
 *  java.io.ByteArrayInputStream
 *  java.io.ByteArrayOutputStream
 *  java.io.DataInputStream
 *  java.io.DataOutputStream
 *  java.io.IOException
 *  java.io.InputStream
 *  java.io.OutputStream
 *  java.lang.AutoCloseable
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.String
 *  java.nio.ByteBuffer
 *  java.nio.IntBuffer
 *  java.nio.channels.FileChannel
 *  java.nio.file.CopyOption
 *  java.nio.file.Files
 *  java.nio.file.LinkOption
 *  java.nio.file.OpenOption
 *  java.nio.file.Path
 *  java.nio.file.StandardCopyOption
 *  java.nio.file.StandardOpenOption
 *  java.nio.file.attribute.FileAttribute
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world.level.chunk.storage;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.logging.LogUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.storage.RegionBitmap;
import net.minecraft.world.level.chunk.storage.RegionFileVersion;
import org.slf4j.Logger;

public class RegionFile
implements AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int SECTOR_BYTES = 4096;
    @VisibleForTesting
    protected static final int SECTOR_INTS = 1024;
    private static final int CHUNK_HEADER_SIZE = 5;
    private static final int HEADER_OFFSET = 0;
    private static final ByteBuffer PADDING_BUFFER = ByteBuffer.allocateDirect((int)1);
    private static final String EXTERNAL_FILE_EXTENSION = ".mcc";
    private static final int EXTERNAL_STREAM_FLAG = 128;
    private static final int EXTERNAL_CHUNK_THRESHOLD = 256;
    private static final int CHUNK_NOT_PRESENT = 0;
    private final FileChannel file;
    private final Path externalFileDir;
    final RegionFileVersion version;
    private final ByteBuffer header = ByteBuffer.allocateDirect((int)8192);
    private final IntBuffer offsets;
    private final IntBuffer timestamps;
    @VisibleForTesting
    protected final RegionBitmap usedSectors = new RegionBitmap();

    public RegionFile(Path $$0, Path $$1, boolean $$2) throws IOException {
        this($$0, $$1, RegionFileVersion.VERSION_DEFLATE, $$2);
    }

    public RegionFile(Path $$0, Path $$1, RegionFileVersion $$2, boolean $$3) throws IOException {
        this.version = $$2;
        if (!Files.isDirectory((Path)$$1, (LinkOption[])new LinkOption[0])) {
            throw new IllegalArgumentException("Expected directory, got " + $$1.toAbsolutePath());
        }
        this.externalFileDir = $$1;
        this.offsets = this.header.asIntBuffer();
        this.offsets.limit(1024);
        this.header.position(4096);
        this.timestamps = this.header.asIntBuffer();
        this.file = $$3 ? FileChannel.open((Path)$$0, (OpenOption[])new OpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.DSYNC}) : FileChannel.open((Path)$$0, (OpenOption[])new OpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE});
        this.usedSectors.force(0, 2);
        this.header.position(0);
        int $$4 = this.file.read(this.header, 0L);
        if ($$4 != -1) {
            if ($$4 != 8192) {
                LOGGER.warn("Region file {} has truncated header: {}", (Object)$$0, (Object)$$4);
            }
            long $$5 = Files.size((Path)$$0);
            for (int $$6 = 0; $$6 < 1024; ++$$6) {
                int $$7 = this.offsets.get($$6);
                if ($$7 == 0) continue;
                int $$8 = RegionFile.getSectorNumber($$7);
                int $$9 = RegionFile.getNumSectors($$7);
                if ($$8 < 2) {
                    LOGGER.warn("Region file {} has invalid sector at index: {}; sector {} overlaps with header", new Object[]{$$0, $$6, $$8});
                    this.offsets.put($$6, 0);
                    continue;
                }
                if ($$9 == 0) {
                    LOGGER.warn("Region file {} has an invalid sector at index: {}; size has to be > 0", (Object)$$0, (Object)$$6);
                    this.offsets.put($$6, 0);
                    continue;
                }
                if ((long)$$8 * 4096L > $$5) {
                    LOGGER.warn("Region file {} has an invalid sector at index: {}; sector {} is out of bounds", new Object[]{$$0, $$6, $$8});
                    this.offsets.put($$6, 0);
                    continue;
                }
                this.usedSectors.force($$8, $$9);
            }
        }
    }

    private Path getExternalChunkPath(ChunkPos $$0) {
        String $$1 = "c." + $$0.x + "." + $$0.z + EXTERNAL_FILE_EXTENSION;
        return this.externalFileDir.resolve($$1);
    }

    @Nullable
    public synchronized DataInputStream getChunkDataInputStream(ChunkPos $$0) throws IOException {
        int $$1 = this.getOffset($$0);
        if ($$1 == 0) {
            return null;
        }
        int $$2 = RegionFile.getSectorNumber($$1);
        int $$3 = RegionFile.getNumSectors($$1);
        int $$4 = $$3 * 4096;
        ByteBuffer $$5 = ByteBuffer.allocate((int)$$4);
        this.file.read($$5, (long)($$2 * 4096));
        $$5.flip();
        if ($$5.remaining() < 5) {
            LOGGER.error("Chunk {} header is truncated: expected {} but read {}", new Object[]{$$0, $$4, $$5.remaining()});
            return null;
        }
        int $$6 = $$5.getInt();
        byte $$7 = $$5.get();
        if ($$6 == 0) {
            LOGGER.warn("Chunk {} is allocated, but stream is missing", (Object)$$0);
            return null;
        }
        int $$8 = $$6 - 1;
        if (RegionFile.isExternalStreamChunk($$7)) {
            if ($$8 != 0) {
                LOGGER.warn("Chunk has both internal and external streams");
            }
            return this.createExternalChunkInputStream($$0, RegionFile.getExternalChunkVersion($$7));
        }
        if ($$8 > $$5.remaining()) {
            LOGGER.error("Chunk {} stream is truncated: expected {} but read {}", new Object[]{$$0, $$8, $$5.remaining()});
            return null;
        }
        if ($$8 < 0) {
            LOGGER.error("Declared size {} of chunk {} is negative", (Object)$$6, (Object)$$0);
            return null;
        }
        return this.createChunkInputStream($$0, $$7, (InputStream)RegionFile.createStream($$5, $$8));
    }

    private static int getTimestamp() {
        return (int)(Util.getEpochMillis() / 1000L);
    }

    private static boolean isExternalStreamChunk(byte $$0) {
        return ($$0 & 0x80) != 0;
    }

    private static byte getExternalChunkVersion(byte $$0) {
        return (byte)($$0 & 0xFFFFFF7F);
    }

    @Nullable
    private DataInputStream createChunkInputStream(ChunkPos $$0, byte $$1, InputStream $$2) throws IOException {
        RegionFileVersion $$3 = RegionFileVersion.fromId($$1);
        if ($$3 == null) {
            LOGGER.error("Chunk {} has invalid chunk stream version {}", (Object)$$0, (Object)$$1);
            return null;
        }
        return new DataInputStream($$3.wrap($$2));
    }

    @Nullable
    private DataInputStream createExternalChunkInputStream(ChunkPos $$0, byte $$1) throws IOException {
        Path $$2 = this.getExternalChunkPath($$0);
        if (!Files.isRegularFile((Path)$$2, (LinkOption[])new LinkOption[0])) {
            LOGGER.error("External chunk path {} is not file", (Object)$$2);
            return null;
        }
        return this.createChunkInputStream($$0, $$1, Files.newInputStream((Path)$$2, (OpenOption[])new OpenOption[0]));
    }

    private static ByteArrayInputStream createStream(ByteBuffer $$0, int $$1) {
        return new ByteArrayInputStream($$0.array(), $$0.position(), $$1);
    }

    private int packSectorOffset(int $$0, int $$1) {
        return $$0 << 8 | $$1;
    }

    private static int getNumSectors(int $$0) {
        return $$0 & 0xFF;
    }

    private static int getSectorNumber(int $$0) {
        return $$0 >> 8 & 0xFFFFFF;
    }

    private static int sizeToSectors(int $$0) {
        return ($$0 + 4096 - 1) / 4096;
    }

    public boolean doesChunkExist(ChunkPos $$0) {
        int $$1 = this.getOffset($$0);
        if ($$1 == 0) {
            return false;
        }
        int $$2 = RegionFile.getSectorNumber($$1);
        int $$3 = RegionFile.getNumSectors($$1);
        ByteBuffer $$4 = ByteBuffer.allocate((int)5);
        try {
            this.file.read($$4, (long)($$2 * 4096));
            $$4.flip();
            if ($$4.remaining() != 5) {
                return false;
            }
            int $$5 = $$4.getInt();
            byte $$6 = $$4.get();
            if (RegionFile.isExternalStreamChunk($$6)) {
                if (!RegionFileVersion.isValidVersion(RegionFile.getExternalChunkVersion($$6))) {
                    return false;
                }
                if (!Files.isRegularFile((Path)this.getExternalChunkPath($$0), (LinkOption[])new LinkOption[0])) {
                    return false;
                }
            } else {
                if (!RegionFileVersion.isValidVersion($$6)) {
                    return false;
                }
                if ($$5 == 0) {
                    return false;
                }
                int $$7 = $$5 - 1;
                if ($$7 < 0 || $$7 > 4096 * $$3) {
                    return false;
                }
            }
        }
        catch (IOException $$8) {
            return false;
        }
        return true;
    }

    public DataOutputStream getChunkDataOutputStream(ChunkPos $$0) throws IOException {
        return new DataOutputStream(this.version.wrap((OutputStream)new ChunkBuffer($$0)));
    }

    public void flush() throws IOException {
        this.file.force(true);
    }

    public void clear(ChunkPos $$0) throws IOException {
        int $$1 = RegionFile.getOffsetIndex($$0);
        int $$2 = this.offsets.get($$1);
        if ($$2 == 0) {
            return;
        }
        this.offsets.put($$1, 0);
        this.timestamps.put($$1, RegionFile.getTimestamp());
        this.writeHeader();
        Files.deleteIfExists((Path)this.getExternalChunkPath($$0));
        this.usedSectors.free(RegionFile.getSectorNumber($$2), RegionFile.getNumSectors($$2));
    }

    protected synchronized void write(ChunkPos $$0, ByteBuffer $$1) throws IOException {
        CommitOp $$13;
        int $$12;
        int $$2 = RegionFile.getOffsetIndex($$0);
        int $$3 = this.offsets.get($$2);
        int $$4 = RegionFile.getSectorNumber($$3);
        int $$5 = RegionFile.getNumSectors($$3);
        int $$6 = $$1.remaining();
        int $$7 = RegionFile.sizeToSectors($$6);
        if ($$7 >= 256) {
            Path $$8 = this.getExternalChunkPath($$0);
            LOGGER.warn("Saving oversized chunk {} ({} bytes} to external file {}", new Object[]{$$0, $$6, $$8});
            $$7 = 1;
            int $$9 = this.usedSectors.allocate($$7);
            CommitOp $$10 = this.writeToExternalFile($$8, $$1);
            ByteBuffer $$11 = this.createExternalStub();
            this.file.write($$11, (long)($$9 * 4096));
        } else {
            $$12 = this.usedSectors.allocate($$7);
            $$13 = () -> Files.deleteIfExists((Path)this.getExternalChunkPath($$0));
            this.file.write($$1, (long)($$12 * 4096));
        }
        this.offsets.put($$2, this.packSectorOffset($$12, $$7));
        this.timestamps.put($$2, RegionFile.getTimestamp());
        this.writeHeader();
        $$13.run();
        if ($$4 != 0) {
            this.usedSectors.free($$4, $$5);
        }
    }

    private ByteBuffer createExternalStub() {
        ByteBuffer $$0 = ByteBuffer.allocate((int)5);
        $$0.putInt(1);
        $$0.put((byte)(this.version.getId() | 0x80));
        $$0.flip();
        return $$0;
    }

    private CommitOp writeToExternalFile(Path $$0, ByteBuffer $$1) throws IOException {
        Path $$2 = Files.createTempFile((Path)this.externalFileDir, (String)"tmp", null, (FileAttribute[])new FileAttribute[0]);
        try (FileChannel $$3 = FileChannel.open((Path)$$2, (OpenOption[])new OpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.WRITE});){
            $$1.position(5);
            $$3.write($$1);
        }
        return () -> Files.move((Path)$$2, (Path)$$0, (CopyOption[])new CopyOption[]{StandardCopyOption.REPLACE_EXISTING});
    }

    private void writeHeader() throws IOException {
        this.header.position(0);
        this.file.write(this.header, 0L);
    }

    private int getOffset(ChunkPos $$0) {
        return this.offsets.get(RegionFile.getOffsetIndex($$0));
    }

    public boolean hasChunk(ChunkPos $$0) {
        return this.getOffset($$0) != 0;
    }

    private static int getOffsetIndex(ChunkPos $$0) {
        return $$0.getRegionLocalX() + $$0.getRegionLocalZ() * 32;
    }

    public void close() throws IOException {
        try {
            this.padToFullSector();
        }
        finally {
            try {
                this.file.force(true);
            }
            finally {
                this.file.close();
            }
        }
    }

    private void padToFullSector() throws IOException {
        int $$1;
        int $$0 = (int)this.file.size();
        if ($$0 != ($$1 = RegionFile.sizeToSectors($$0) * 4096)) {
            ByteBuffer $$2 = PADDING_BUFFER.duplicate();
            $$2.position(0);
            this.file.write($$2, (long)($$1 - 1));
        }
    }

    class ChunkBuffer
    extends ByteArrayOutputStream {
        private final ChunkPos pos;

        public ChunkBuffer(ChunkPos $$0) {
            super(8096);
            super.write(0);
            super.write(0);
            super.write(0);
            super.write(0);
            super.write(RegionFile.this.version.getId());
            this.pos = $$0;
        }

        public void close() throws IOException {
            ByteBuffer $$0 = ByteBuffer.wrap((byte[])this.buf, (int)0, (int)this.count);
            $$0.putInt(0, this.count - 5 + 1);
            RegionFile.this.write(this.pos, $$0);
        }
    }

    static interface CommitOp {
        public void run() throws IOException;
    }
}