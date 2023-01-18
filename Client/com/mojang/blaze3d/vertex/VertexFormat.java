/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntList
 *  java.lang.CharSequence
 *  java.lang.Object
 *  java.lang.String
 *  java.util.stream.Collectors
 *  javax.annotation.Nullable
 */
package com.mojang.blaze3d.vertex;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public class VertexFormat {
    private final ImmutableList<VertexFormatElement> elements;
    private final ImmutableMap<String, VertexFormatElement> elementMapping;
    private final IntList offsets = new IntArrayList();
    private final int vertexSize;
    @Nullable
    private VertexBuffer immediateDrawVertexBuffer;

    public VertexFormat(ImmutableMap<String, VertexFormatElement> $$0) {
        this.elementMapping = $$0;
        this.elements = $$0.values().asList();
        int $$1 = 0;
        for (VertexFormatElement $$2 : $$0.values()) {
            this.offsets.add($$1);
            $$1 += $$2.getByteSize();
        }
        this.vertexSize = $$1;
    }

    public String toString() {
        return "format: " + this.elementMapping.size() + " elements: " + (String)this.elementMapping.entrySet().stream().map(Object::toString).collect(Collectors.joining((CharSequence)" "));
    }

    public int getIntegerSize() {
        return this.getVertexSize() / 4;
    }

    public int getVertexSize() {
        return this.vertexSize;
    }

    public ImmutableList<VertexFormatElement> getElements() {
        return this.elements;
    }

    public ImmutableList<String> getElementAttributeNames() {
        return this.elementMapping.keySet().asList();
    }

    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        if ($$0 == null || this.getClass() != $$0.getClass()) {
            return false;
        }
        VertexFormat $$1 = (VertexFormat)$$0;
        if (this.vertexSize != $$1.vertexSize) {
            return false;
        }
        return this.elementMapping.equals($$1.elementMapping);
    }

    public int hashCode() {
        return this.elementMapping.hashCode();
    }

    public void setupBufferState() {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(this::_setupBufferState);
            return;
        }
        this._setupBufferState();
    }

    private void _setupBufferState() {
        int $$0 = this.getVertexSize();
        ImmutableList<VertexFormatElement> $$1 = this.getElements();
        for (int $$2 = 0; $$2 < $$1.size(); ++$$2) {
            ((VertexFormatElement)$$1.get($$2)).setupBufferState($$2, this.offsets.getInt($$2), $$0);
        }
    }

    public void clearBufferState() {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(this::_clearBufferState);
            return;
        }
        this._clearBufferState();
    }

    private void _clearBufferState() {
        ImmutableList<VertexFormatElement> $$0 = this.getElements();
        for (int $$1 = 0; $$1 < $$0.size(); ++$$1) {
            VertexFormatElement $$2 = (VertexFormatElement)$$0.get($$1);
            $$2.clearBufferState($$1);
        }
    }

    public VertexBuffer getImmediateDrawVertexBuffer() {
        VertexBuffer $$0 = this.immediateDrawVertexBuffer;
        if ($$0 == null) {
            this.immediateDrawVertexBuffer = $$0 = new VertexBuffer();
        }
        return $$0;
    }

    public static enum Mode {
        LINES(4, 2, 2, false),
        LINE_STRIP(5, 2, 1, true),
        DEBUG_LINES(1, 2, 2, false),
        DEBUG_LINE_STRIP(3, 2, 1, true),
        TRIANGLES(4, 3, 3, false),
        TRIANGLE_STRIP(5, 3, 1, true),
        TRIANGLE_FAN(6, 3, 1, true),
        QUADS(4, 4, 4, false);

        public final int asGLMode;
        public final int primitiveLength;
        public final int primitiveStride;
        public final boolean connectedPrimitives;

        private Mode(int $$0, int $$1, int $$2, boolean $$3) {
            this.asGLMode = $$0;
            this.primitiveLength = $$1;
            this.primitiveStride = $$2;
            this.connectedPrimitives = $$3;
        }

        public int indexCount(int $$0) {
            int $$3;
            switch (this) {
                case LINE_STRIP: 
                case DEBUG_LINES: 
                case DEBUG_LINE_STRIP: 
                case TRIANGLES: 
                case TRIANGLE_STRIP: 
                case TRIANGLE_FAN: {
                    int $$1 = $$0;
                    break;
                }
                case LINES: 
                case QUADS: {
                    int $$2 = $$0 / 4 * 6;
                    break;
                }
                default: {
                    $$3 = 0;
                }
            }
            return $$3;
        }
    }

    public static enum IndexType {
        BYTE(5121, 1),
        SHORT(5123, 2),
        INT(5125, 4);

        public final int asGLType;
        public final int bytes;

        private IndexType(int $$0, int $$1) {
            this.asGLType = $$0;
            this.bytes = $$1;
        }

        public static IndexType least(int $$0) {
            if (($$0 & 0xFFFF0000) != 0) {
                return INT;
            }
            if (($$0 & 0xFF00) != 0) {
                return SHORT;
            }
            return BYTE;
        }
    }
}