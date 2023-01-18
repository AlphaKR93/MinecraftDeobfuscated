/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.Override
 */
package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.util.Mth;

public interface BufferVertexConsumer
extends VertexConsumer {
    public VertexFormatElement currentElement();

    public void nextElement();

    public void putByte(int var1, byte var2);

    public void putShort(int var1, short var2);

    public void putFloat(int var1, float var2);

    @Override
    default public VertexConsumer vertex(double $$0, double $$1, double $$2) {
        if (this.currentElement().getUsage() != VertexFormatElement.Usage.POSITION) {
            return this;
        }
        if (this.currentElement().getType() != VertexFormatElement.Type.FLOAT || this.currentElement().getCount() != 3) {
            throw new IllegalStateException();
        }
        this.putFloat(0, (float)$$0);
        this.putFloat(4, (float)$$1);
        this.putFloat(8, (float)$$2);
        this.nextElement();
        return this;
    }

    @Override
    default public VertexConsumer color(int $$0, int $$1, int $$2, int $$3) {
        VertexFormatElement $$4 = this.currentElement();
        if ($$4.getUsage() != VertexFormatElement.Usage.COLOR) {
            return this;
        }
        if ($$4.getType() != VertexFormatElement.Type.UBYTE || $$4.getCount() != 4) {
            throw new IllegalStateException();
        }
        this.putByte(0, (byte)$$0);
        this.putByte(1, (byte)$$1);
        this.putByte(2, (byte)$$2);
        this.putByte(3, (byte)$$3);
        this.nextElement();
        return this;
    }

    @Override
    default public VertexConsumer uv(float $$0, float $$1) {
        VertexFormatElement $$2 = this.currentElement();
        if ($$2.getUsage() != VertexFormatElement.Usage.UV || $$2.getIndex() != 0) {
            return this;
        }
        if ($$2.getType() != VertexFormatElement.Type.FLOAT || $$2.getCount() != 2) {
            throw new IllegalStateException();
        }
        this.putFloat(0, $$0);
        this.putFloat(4, $$1);
        this.nextElement();
        return this;
    }

    @Override
    default public VertexConsumer overlayCoords(int $$0, int $$1) {
        return this.uvShort((short)$$0, (short)$$1, 1);
    }

    @Override
    default public VertexConsumer uv2(int $$0, int $$1) {
        return this.uvShort((short)$$0, (short)$$1, 2);
    }

    default public VertexConsumer uvShort(short $$0, short $$1, int $$2) {
        VertexFormatElement $$3 = this.currentElement();
        if ($$3.getUsage() != VertexFormatElement.Usage.UV || $$3.getIndex() != $$2) {
            return this;
        }
        if ($$3.getType() != VertexFormatElement.Type.SHORT || $$3.getCount() != 2) {
            throw new IllegalStateException();
        }
        this.putShort(0, $$0);
        this.putShort(2, $$1);
        this.nextElement();
        return this;
    }

    @Override
    default public VertexConsumer normal(float $$0, float $$1, float $$2) {
        VertexFormatElement $$3 = this.currentElement();
        if ($$3.getUsage() != VertexFormatElement.Usage.NORMAL) {
            return this;
        }
        if ($$3.getType() != VertexFormatElement.Type.BYTE || $$3.getCount() != 3) {
            throw new IllegalStateException();
        }
        this.putByte(0, BufferVertexConsumer.normalIntValue($$0));
        this.putByte(1, BufferVertexConsumer.normalIntValue($$1));
        this.putByte(2, BufferVertexConsumer.normalIntValue($$2));
        this.nextElement();
        return this;
    }

    public static byte normalIntValue(float $$0) {
        return (byte)((int)(Mth.clamp($$0, -1.0f, 1.0f) * 127.0f) & 0xFF);
    }
}