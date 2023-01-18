/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.primitives.Floats
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.ints.IntArrays
 *  it.unimi.dsi.fastutil.ints.IntConsumer
 *  java.lang.Float
 *  java.lang.IllegalStateException
 *  java.lang.IncompatibleClassChangeError
 *  java.lang.Object
 *  java.lang.Override
 *  java.nio.ByteBuffer
 *  java.nio.FloatBuffer
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.mutable.MutableInt
 *  org.joml.Vector3f
 *  org.lwjgl.system.MemoryUtil
 *  org.slf4j.Logger
 */
package com.mojang.blaze3d.vertex;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Floats;
import com.mojang.blaze3d.platform.MemoryTracker;
import com.mojang.blaze3d.vertex.BufferVertexConsumer;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.DefaultedVertexConsumer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntConsumer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import javax.annotation.Nullable;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.mutable.MutableInt;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

public class BufferBuilder
extends DefaultedVertexConsumer
implements BufferVertexConsumer {
    private static final int GROWTH_SIZE = 0x200000;
    private static final Logger LOGGER = LogUtils.getLogger();
    private ByteBuffer buffer;
    private int renderedBufferCount;
    private int renderedBufferPointer;
    private int nextElementByte;
    private int vertices;
    @Nullable
    private VertexFormatElement currentElement;
    private int elementIndex;
    private VertexFormat format;
    private VertexFormat.Mode mode;
    private boolean fastFormat;
    private boolean fullFormat;
    private boolean building;
    @Nullable
    private Vector3f[] sortingPoints;
    private float sortX = Float.NaN;
    private float sortY = Float.NaN;
    private float sortZ = Float.NaN;
    private boolean indexOnly;

    public BufferBuilder(int $$0) {
        this.buffer = MemoryTracker.create($$0 * 6);
    }

    private void ensureVertexCapacity() {
        this.ensureCapacity(this.format.getVertexSize());
    }

    private void ensureCapacity(int $$0) {
        if (this.nextElementByte + $$0 <= this.buffer.capacity()) {
            return;
        }
        int $$1 = this.buffer.capacity();
        int $$2 = $$1 + BufferBuilder.roundUp($$0);
        LOGGER.debug("Needed to grow BufferBuilder buffer: Old size {} bytes, new size {} bytes.", (Object)$$1, (Object)$$2);
        ByteBuffer $$3 = MemoryTracker.resize(this.buffer, $$2);
        $$3.rewind();
        this.buffer = $$3;
    }

    private static int roundUp(int $$0) {
        int $$2;
        int $$1 = 0x200000;
        if ($$0 == 0) {
            return $$1;
        }
        if ($$0 < 0) {
            $$1 *= -1;
        }
        if (($$2 = $$0 % $$1) == 0) {
            return $$0;
        }
        return $$0 + $$1 - $$2;
    }

    public void setQuadSortOrigin(float $$0, float $$1, float $$2) {
        if (this.mode != VertexFormat.Mode.QUADS) {
            return;
        }
        if (this.sortX != $$0 || this.sortY != $$1 || this.sortZ != $$2) {
            this.sortX = $$0;
            this.sortY = $$1;
            this.sortZ = $$2;
            if (this.sortingPoints == null) {
                this.sortingPoints = this.makeQuadSortingPoints();
            }
        }
    }

    public SortState getSortState() {
        return new SortState(this.mode, this.vertices, this.sortingPoints, this.sortX, this.sortY, this.sortZ);
    }

    public void restoreSortState(SortState $$0) {
        this.buffer.rewind();
        this.mode = $$0.mode;
        this.vertices = $$0.vertices;
        this.nextElementByte = this.renderedBufferPointer;
        this.sortingPoints = $$0.sortingPoints;
        this.sortX = $$0.sortX;
        this.sortY = $$0.sortY;
        this.sortZ = $$0.sortZ;
        this.indexOnly = true;
    }

    public void begin(VertexFormat.Mode $$0, VertexFormat $$1) {
        if (this.building) {
            throw new IllegalStateException("Already building!");
        }
        this.building = true;
        this.mode = $$0;
        this.switchFormat($$1);
        this.currentElement = (VertexFormatElement)$$1.getElements().get(0);
        this.elementIndex = 0;
        this.buffer.rewind();
    }

    private void switchFormat(VertexFormat $$0) {
        if (this.format == $$0) {
            return;
        }
        this.format = $$0;
        boolean $$1 = $$0 == DefaultVertexFormat.NEW_ENTITY;
        boolean $$2 = $$0 == DefaultVertexFormat.BLOCK;
        this.fastFormat = $$1 || $$2;
        this.fullFormat = $$1;
    }

    private IntConsumer intConsumer(int $$0, VertexFormat.IndexType $$12) {
        MutableInt $$2 = new MutableInt($$0);
        return switch ($$12) {
            default -> throw new IncompatibleClassChangeError();
            case VertexFormat.IndexType.BYTE -> $$1 -> this.buffer.put($$2.getAndIncrement(), (byte)$$1);
            case VertexFormat.IndexType.SHORT -> $$1 -> this.buffer.putShort($$2.getAndAdd(2), (short)$$1);
            case VertexFormat.IndexType.INT -> $$1 -> this.buffer.putInt($$2.getAndAdd(4), $$1);
        };
    }

    private Vector3f[] makeQuadSortingPoints() {
        FloatBuffer $$0 = this.buffer.asFloatBuffer();
        int $$1 = this.renderedBufferPointer / 4;
        int $$2 = this.format.getIntegerSize();
        int $$3 = $$2 * this.mode.primitiveStride;
        int $$4 = this.vertices / this.mode.primitiveStride;
        Vector3f[] $$5 = new Vector3f[$$4];
        for (int $$6 = 0; $$6 < $$4; ++$$6) {
            float $$7 = $$0.get($$1 + $$6 * $$3 + 0);
            float $$8 = $$0.get($$1 + $$6 * $$3 + 1);
            float $$9 = $$0.get($$1 + $$6 * $$3 + 2);
            float $$10 = $$0.get($$1 + $$6 * $$3 + $$2 * 2 + 0);
            float $$11 = $$0.get($$1 + $$6 * $$3 + $$2 * 2 + 1);
            float $$12 = $$0.get($$1 + $$6 * $$3 + $$2 * 2 + 2);
            float $$13 = ($$7 + $$10) / 2.0f;
            float $$14 = ($$8 + $$11) / 2.0f;
            float $$15 = ($$9 + $$12) / 2.0f;
            $$5[$$6] = new Vector3f($$13, $$14, $$15);
        }
        return $$5;
    }

    private void putSortedQuadIndices(VertexFormat.IndexType $$0) {
        float[] $$12 = new float[this.sortingPoints.length];
        int[] $$22 = new int[this.sortingPoints.length];
        for (int $$3 = 0; $$3 < this.sortingPoints.length; ++$$3) {
            float $$4 = this.sortingPoints[$$3].x() - this.sortX;
            float $$5 = this.sortingPoints[$$3].y() - this.sortY;
            float $$6 = this.sortingPoints[$$3].z() - this.sortZ;
            $$12[$$3] = $$4 * $$4 + $$5 * $$5 + $$6 * $$6;
            $$22[$$3] = $$3;
        }
        IntArrays.mergeSort((int[])$$22, ($$1, $$2) -> Floats.compare((float)$$12[$$2], (float)$$12[$$1]));
        IntConsumer $$7 = this.intConsumer(this.nextElementByte, $$0);
        for (int $$8 : $$22) {
            $$7.accept($$8 * this.mode.primitiveStride + 0);
            $$7.accept($$8 * this.mode.primitiveStride + 1);
            $$7.accept($$8 * this.mode.primitiveStride + 2);
            $$7.accept($$8 * this.mode.primitiveStride + 2);
            $$7.accept($$8 * this.mode.primitiveStride + 3);
            $$7.accept($$8 * this.mode.primitiveStride + 0);
        }
    }

    public boolean isCurrentBatchEmpty() {
        return this.vertices == 0;
    }

    @Nullable
    public RenderedBuffer endOrDiscardIfEmpty() {
        this.ensureDrawing();
        if (this.isCurrentBatchEmpty()) {
            this.reset();
            return null;
        }
        RenderedBuffer $$0 = this.storeRenderedBuffer();
        this.reset();
        return $$0;
    }

    public RenderedBuffer end() {
        this.ensureDrawing();
        RenderedBuffer $$0 = this.storeRenderedBuffer();
        this.reset();
        return $$0;
    }

    private void ensureDrawing() {
        if (!this.building) {
            throw new IllegalStateException("Not building!");
        }
    }

    private RenderedBuffer storeRenderedBuffer() {
        int $$7;
        boolean $$6;
        int $$0 = this.mode.indexCount(this.vertices);
        int $$1 = !this.indexOnly ? this.vertices * this.format.getVertexSize() : 0;
        VertexFormat.IndexType $$2 = VertexFormat.IndexType.least($$0);
        if (this.sortingPoints != null) {
            int $$3 = Mth.roundToward($$0 * $$2.bytes, 4);
            this.ensureCapacity($$3);
            this.putSortedQuadIndices($$2);
            boolean $$4 = false;
            this.nextElementByte += $$3;
            int $$5 = $$1 + $$3;
        } else {
            $$6 = true;
            $$7 = $$1;
        }
        int $$8 = this.renderedBufferPointer;
        this.renderedBufferPointer += $$7;
        ++this.renderedBufferCount;
        DrawState $$9 = new DrawState(this.format, this.vertices, $$0, this.mode, $$2, this.indexOnly, $$6);
        return new RenderedBuffer($$8, $$9);
    }

    private void reset() {
        this.building = false;
        this.vertices = 0;
        this.currentElement = null;
        this.elementIndex = 0;
        this.sortingPoints = null;
        this.sortX = Float.NaN;
        this.sortY = Float.NaN;
        this.sortZ = Float.NaN;
        this.indexOnly = false;
    }

    @Override
    public void putByte(int $$0, byte $$1) {
        this.buffer.put(this.nextElementByte + $$0, $$1);
    }

    @Override
    public void putShort(int $$0, short $$1) {
        this.buffer.putShort(this.nextElementByte + $$0, $$1);
    }

    @Override
    public void putFloat(int $$0, float $$1) {
        this.buffer.putFloat(this.nextElementByte + $$0, $$1);
    }

    @Override
    public void endVertex() {
        if (this.elementIndex != 0) {
            throw new IllegalStateException("Not filled all elements of the vertex");
        }
        ++this.vertices;
        this.ensureVertexCapacity();
        if (this.mode == VertexFormat.Mode.LINES || this.mode == VertexFormat.Mode.LINE_STRIP) {
            int $$0 = this.format.getVertexSize();
            this.buffer.put(this.nextElementByte, this.buffer, this.nextElementByte - $$0, $$0);
            this.nextElementByte += $$0;
            ++this.vertices;
            this.ensureVertexCapacity();
        }
    }

    @Override
    public void nextElement() {
        VertexFormatElement $$1;
        ImmutableList<VertexFormatElement> $$0 = this.format.getElements();
        this.elementIndex = (this.elementIndex + 1) % $$0.size();
        this.nextElementByte += this.currentElement.getByteSize();
        this.currentElement = $$1 = (VertexFormatElement)$$0.get(this.elementIndex);
        if ($$1.getUsage() == VertexFormatElement.Usage.PADDING) {
            this.nextElement();
        }
        if (this.defaultColorSet && this.currentElement.getUsage() == VertexFormatElement.Usage.COLOR) {
            BufferVertexConsumer.super.color(this.defaultR, this.defaultG, this.defaultB, this.defaultA);
        }
    }

    @Override
    public VertexConsumer color(int $$0, int $$1, int $$2, int $$3) {
        if (this.defaultColorSet) {
            throw new IllegalStateException();
        }
        return BufferVertexConsumer.super.color($$0, $$1, $$2, $$3);
    }

    @Override
    public void vertex(float $$0, float $$1, float $$2, float $$3, float $$4, float $$5, float $$6, float $$7, float $$8, int $$9, int $$10, float $$11, float $$12, float $$13) {
        if (this.defaultColorSet) {
            throw new IllegalStateException();
        }
        if (this.fastFormat) {
            int $$15;
            this.putFloat(0, $$0);
            this.putFloat(4, $$1);
            this.putFloat(8, $$2);
            this.putByte(12, (byte)($$3 * 255.0f));
            this.putByte(13, (byte)($$4 * 255.0f));
            this.putByte(14, (byte)($$5 * 255.0f));
            this.putByte(15, (byte)($$6 * 255.0f));
            this.putFloat(16, $$7);
            this.putFloat(20, $$8);
            if (this.fullFormat) {
                this.putShort(24, (short)($$9 & 0xFFFF));
                this.putShort(26, (short)($$9 >> 16 & 0xFFFF));
                int $$14 = 28;
            } else {
                $$15 = 24;
            }
            this.putShort($$15 + 0, (short)($$10 & 0xFFFF));
            this.putShort($$15 + 2, (short)($$10 >> 16 & 0xFFFF));
            this.putByte($$15 + 4, BufferVertexConsumer.normalIntValue($$11));
            this.putByte($$15 + 5, BufferVertexConsumer.normalIntValue($$12));
            this.putByte($$15 + 6, BufferVertexConsumer.normalIntValue($$13));
            this.nextElementByte += $$15 + 8;
            this.endVertex();
            return;
        }
        super.vertex($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$8, $$9, $$10, $$11, $$12, $$13);
    }

    void releaseRenderedBuffer() {
        if (this.renderedBufferCount > 0 && --this.renderedBufferCount == 0) {
            this.clear();
        }
    }

    public void clear() {
        if (this.renderedBufferCount > 0) {
            LOGGER.warn("Clearing BufferBuilder with unused batches");
        }
        this.discard();
    }

    public void discard() {
        this.renderedBufferCount = 0;
        this.renderedBufferPointer = 0;
        this.nextElementByte = 0;
    }

    @Override
    public VertexFormatElement currentElement() {
        if (this.currentElement == null) {
            throw new IllegalStateException("BufferBuilder not started");
        }
        return this.currentElement;
    }

    public boolean building() {
        return this.building;
    }

    ByteBuffer bufferSlice(int $$0, int $$1) {
        return MemoryUtil.memSlice((ByteBuffer)this.buffer, (int)$$0, (int)($$1 - $$0));
    }

    public static class SortState {
        final VertexFormat.Mode mode;
        final int vertices;
        @Nullable
        final Vector3f[] sortingPoints;
        final float sortX;
        final float sortY;
        final float sortZ;

        SortState(VertexFormat.Mode $$0, int $$1, @Nullable Vector3f[] $$2, float $$3, float $$4, float $$5) {
            this.mode = $$0;
            this.vertices = $$1;
            this.sortingPoints = $$2;
            this.sortX = $$3;
            this.sortY = $$4;
            this.sortZ = $$5;
        }
    }

    public class RenderedBuffer {
        private final int pointer;
        private final DrawState drawState;
        private boolean released;

        RenderedBuffer(int $$1, DrawState $$2) {
            this.pointer = $$1;
            this.drawState = $$2;
        }

        public ByteBuffer vertexBuffer() {
            int $$0 = this.pointer + this.drawState.vertexBufferStart();
            int $$1 = this.pointer + this.drawState.vertexBufferEnd();
            return BufferBuilder.this.bufferSlice($$0, $$1);
        }

        public ByteBuffer indexBuffer() {
            int $$0 = this.pointer + this.drawState.indexBufferStart();
            int $$1 = this.pointer + this.drawState.indexBufferEnd();
            return BufferBuilder.this.bufferSlice($$0, $$1);
        }

        public DrawState drawState() {
            return this.drawState;
        }

        public boolean isEmpty() {
            return this.drawState.vertexCount == 0;
        }

        public void release() {
            if (this.released) {
                throw new IllegalStateException("Buffer has already been released!");
            }
            BufferBuilder.this.releaseRenderedBuffer();
            this.released = true;
        }
    }

    public record DrawState(VertexFormat format, int vertexCount, int indexCount, VertexFormat.Mode mode, VertexFormat.IndexType indexType, boolean indexOnly, boolean sequentialIndex) {
        public int vertexBufferSize() {
            return this.vertexCount * this.format.getVertexSize();
        }

        public int vertexBufferStart() {
            return 0;
        }

        public int vertexBufferEnd() {
            return this.vertexBufferSize();
        }

        public int indexBufferStart() {
            return this.indexOnly ? 0 : this.vertexBufferEnd();
        }

        public int indexBufferEnd() {
            return this.indexBufferStart() + this.indexBufferSize();
        }

        private int indexBufferSize() {
            return this.sequentialIndex ? 0 : this.indexCount * this.indexType.bytes;
        }

        public int bufferSize() {
            return this.indexBufferEnd();
        }
    }
}