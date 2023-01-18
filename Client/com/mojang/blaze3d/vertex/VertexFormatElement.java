/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.FunctionalInterface
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.String
 */
package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.platform.GlStateManager;

public class VertexFormatElement {
    private final Type type;
    private final Usage usage;
    private final int index;
    private final int count;
    private final int byteSize;

    public VertexFormatElement(int $$0, Type $$1, Usage $$2, int $$3) {
        if (!this.supportsUsage($$0, $$2)) {
            throw new IllegalStateException("Multiple vertex elements of the same type other than UVs are not supported");
        }
        this.usage = $$2;
        this.type = $$1;
        this.index = $$0;
        this.count = $$3;
        this.byteSize = $$1.getSize() * this.count;
    }

    private boolean supportsUsage(int $$0, Usage $$1) {
        return $$0 == 0 || $$1 == Usage.UV;
    }

    public final Type getType() {
        return this.type;
    }

    public final Usage getUsage() {
        return this.usage;
    }

    public final int getCount() {
        return this.count;
    }

    public final int getIndex() {
        return this.index;
    }

    public String toString() {
        return this.count + "," + this.usage.getName() + "," + this.type.getName();
    }

    public final int getByteSize() {
        return this.byteSize;
    }

    public final boolean isPosition() {
        return this.usage == Usage.POSITION;
    }

    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        if ($$0 == null || this.getClass() != $$0.getClass()) {
            return false;
        }
        VertexFormatElement $$1 = (VertexFormatElement)$$0;
        if (this.count != $$1.count) {
            return false;
        }
        if (this.index != $$1.index) {
            return false;
        }
        if (this.type != $$1.type) {
            return false;
        }
        return this.usage == $$1.usage;
    }

    public int hashCode() {
        int $$0 = this.type.hashCode();
        $$0 = 31 * $$0 + this.usage.hashCode();
        $$0 = 31 * $$0 + this.index;
        $$0 = 31 * $$0 + this.count;
        return $$0;
    }

    public void setupBufferState(int $$0, long $$1, int $$2) {
        this.usage.setupBufferState(this.count, this.type.getGlType(), $$2, $$1, this.index, $$0);
    }

    public void clearBufferState(int $$0) {
        this.usage.clearBufferState(this.index, $$0);
    }

    public static enum Usage {
        POSITION("Position", ($$0, $$1, $$2, $$3, $$4, $$5) -> {
            GlStateManager._enableVertexAttribArray($$5);
            GlStateManager._vertexAttribPointer($$5, $$0, $$1, false, $$2, $$3);
        }, ($$0, $$1) -> GlStateManager._disableVertexAttribArray($$1)),
        NORMAL("Normal", ($$0, $$1, $$2, $$3, $$4, $$5) -> {
            GlStateManager._enableVertexAttribArray($$5);
            GlStateManager._vertexAttribPointer($$5, $$0, $$1, true, $$2, $$3);
        }, ($$0, $$1) -> GlStateManager._disableVertexAttribArray($$1)),
        COLOR("Vertex Color", ($$0, $$1, $$2, $$3, $$4, $$5) -> {
            GlStateManager._enableVertexAttribArray($$5);
            GlStateManager._vertexAttribPointer($$5, $$0, $$1, true, $$2, $$3);
        }, ($$0, $$1) -> GlStateManager._disableVertexAttribArray($$1)),
        UV("UV", ($$0, $$1, $$2, $$3, $$4, $$5) -> {
            GlStateManager._enableVertexAttribArray($$5);
            if ($$1 == 5126) {
                GlStateManager._vertexAttribPointer($$5, $$0, $$1, false, $$2, $$3);
            } else {
                GlStateManager._vertexAttribIPointer($$5, $$0, $$1, $$2, $$3);
            }
        }, ($$0, $$1) -> GlStateManager._disableVertexAttribArray($$1)),
        PADDING("Padding", ($$0, $$1, $$2, $$3, $$4, $$5) -> {}, ($$0, $$1) -> {}),
        GENERIC("Generic", ($$0, $$1, $$2, $$3, $$4, $$5) -> {
            GlStateManager._enableVertexAttribArray($$5);
            GlStateManager._vertexAttribPointer($$5, $$0, $$1, false, $$2, $$3);
        }, ($$0, $$1) -> GlStateManager._disableVertexAttribArray($$1));

        private final String name;
        private final SetupState setupState;
        private final ClearState clearState;

        private Usage(String $$0, SetupState $$1, ClearState $$2) {
            this.name = $$0;
            this.setupState = $$1;
            this.clearState = $$2;
        }

        void setupBufferState(int $$0, int $$1, int $$2, long $$3, int $$4, int $$5) {
            this.setupState.setupBufferState($$0, $$1, $$2, $$3, $$4, $$5);
        }

        public void clearBufferState(int $$0, int $$1) {
            this.clearState.clearBufferState($$0, $$1);
        }

        public String getName() {
            return this.name;
        }

        @FunctionalInterface
        static interface SetupState {
            public void setupBufferState(int var1, int var2, int var3, long var4, int var6, int var7);
        }

        @FunctionalInterface
        static interface ClearState {
            public void clearBufferState(int var1, int var2);
        }
    }

    public static enum Type {
        FLOAT(4, "Float", 5126),
        UBYTE(1, "Unsigned Byte", 5121),
        BYTE(1, "Byte", 5120),
        USHORT(2, "Unsigned Short", 5123),
        SHORT(2, "Short", 5122),
        UINT(4, "Unsigned Int", 5125),
        INT(4, "Int", 5124);

        private final int size;
        private final String name;
        private final int glType;

        private Type(int $$0, String $$1, int $$2) {
            this.size = $$0;
            this.name = $$1;
            this.glType = $$2;
        }

        public int getSize() {
            return this.size;
        }

        public String getName() {
            return this.name;
        }

        public int getGlType() {
            return this.glType;
        }
    }
}