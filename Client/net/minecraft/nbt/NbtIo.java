/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.io.BufferedOutputStream
 *  java.io.DataInput
 *  java.io.DataInputStream
 *  java.io.DataOutput
 *  java.io.DataOutputStream
 *  java.io.File
 *  java.io.FileInputStream
 *  java.io.FileOutputStream
 *  java.io.IOException
 *  java.io.InputStream
 *  java.io.OutputStream
 *  java.lang.Object
 *  java.util.zip.GZIPInputStream
 *  java.util.zip.GZIPOutputStream
 *  javax.annotation.Nullable
 */
package net.minecraft.nbt;

import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.StreamTagVisitor;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagType;
import net.minecraft.nbt.TagTypes;
import net.minecraft.util.FastBufferedInputStream;

public class NbtIo {
    public static CompoundTag readCompressed(File $$0) throws IOException {
        try (FileInputStream $$1 = new FileInputStream($$0);){
            CompoundTag compoundTag = NbtIo.readCompressed((InputStream)$$1);
            return compoundTag;
        }
    }

    private static DataInputStream createDecompressorStream(InputStream $$0) throws IOException {
        return new DataInputStream((InputStream)new FastBufferedInputStream((InputStream)new GZIPInputStream($$0)));
    }

    public static CompoundTag readCompressed(InputStream $$0) throws IOException {
        try (DataInputStream $$1 = NbtIo.createDecompressorStream($$0);){
            CompoundTag compoundTag = NbtIo.read((DataInput)$$1, NbtAccounter.UNLIMITED);
            return compoundTag;
        }
    }

    public static void parseCompressed(File $$0, StreamTagVisitor $$1) throws IOException {
        try (FileInputStream $$2 = new FileInputStream($$0);){
            NbtIo.parseCompressed((InputStream)$$2, $$1);
        }
    }

    public static void parseCompressed(InputStream $$0, StreamTagVisitor $$1) throws IOException {
        try (DataInputStream $$2 = NbtIo.createDecompressorStream($$0);){
            NbtIo.parse((DataInput)$$2, $$1);
        }
    }

    public static void writeCompressed(CompoundTag $$0, File $$1) throws IOException {
        try (FileOutputStream $$2 = new FileOutputStream($$1);){
            NbtIo.writeCompressed($$0, (OutputStream)$$2);
        }
    }

    public static void writeCompressed(CompoundTag $$0, OutputStream $$1) throws IOException {
        try (DataOutputStream $$2 = new DataOutputStream((OutputStream)new BufferedOutputStream((OutputStream)new GZIPOutputStream($$1)));){
            NbtIo.write($$0, (DataOutput)$$2);
        }
    }

    public static void write(CompoundTag $$0, File $$1) throws IOException {
        try (FileOutputStream $$2 = new FileOutputStream($$1);
             DataOutputStream $$3 = new DataOutputStream((OutputStream)$$2);){
            NbtIo.write($$0, (DataOutput)$$3);
        }
    }

    @Nullable
    public static CompoundTag read(File $$0) throws IOException {
        if (!$$0.exists()) {
            return null;
        }
        try (FileInputStream $$1 = new FileInputStream($$0);){
            CompoundTag compoundTag;
            try (DataInputStream $$2 = new DataInputStream((InputStream)$$1);){
                compoundTag = NbtIo.read((DataInput)$$2, NbtAccounter.UNLIMITED);
            }
            return compoundTag;
        }
    }

    public static CompoundTag read(DataInput $$0) throws IOException {
        return NbtIo.read($$0, NbtAccounter.UNLIMITED);
    }

    public static CompoundTag read(DataInput $$0, NbtAccounter $$1) throws IOException {
        Tag $$2 = NbtIo.readUnnamedTag($$0, 0, $$1);
        if ($$2 instanceof CompoundTag) {
            return (CompoundTag)$$2;
        }
        throw new IOException("Root tag must be a named compound tag");
    }

    public static void write(CompoundTag $$0, DataOutput $$1) throws IOException {
        NbtIo.writeUnnamedTag($$0, $$1);
    }

    public static void parse(DataInput $$0, StreamTagVisitor $$1) throws IOException {
        TagType<?> $$2 = TagTypes.getType($$0.readByte());
        if ($$2 == EndTag.TYPE) {
            if ($$1.visitRootEntry(EndTag.TYPE) == StreamTagVisitor.ValueResult.CONTINUE) {
                $$1.visitEnd();
            }
            return;
        }
        switch ($$1.visitRootEntry($$2)) {
            case HALT: {
                break;
            }
            case BREAK: {
                StringTag.skipString($$0);
                $$2.skip($$0);
                break;
            }
            case CONTINUE: {
                StringTag.skipString($$0);
                $$2.parse($$0, $$1);
            }
        }
    }

    public static void writeUnnamedTag(Tag $$0, DataOutput $$1) throws IOException {
        $$1.writeByte((int)$$0.getId());
        if ($$0.getId() == 0) {
            return;
        }
        $$1.writeUTF("");
        $$0.write($$1);
    }

    private static Tag readUnnamedTag(DataInput $$0, int $$1, NbtAccounter $$2) throws IOException {
        byte $$3 = $$0.readByte();
        if ($$3 == 0) {
            return EndTag.INSTANCE;
        }
        StringTag.skipString($$0);
        try {
            return TagTypes.getType($$3).load($$0, $$1, $$2);
        }
        catch (IOException $$4) {
            CrashReport $$5 = CrashReport.forThrowable($$4, "Loading NBT data");
            CrashReportCategory $$6 = $$5.addCategory("NBT Tag");
            $$6.setDetail("Tag type", $$3);
            throw new ReportedException($$5);
        }
    }
}