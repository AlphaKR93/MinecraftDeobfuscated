/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.lang.AutoCloseable
 *  java.lang.CharSequence
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.nio.Buffer
 *  java.nio.FloatBuffer
 *  java.nio.IntBuffer
 *  org.joml.Matrix3f
 *  org.joml.Matrix4f
 *  org.joml.Vector3f
 *  org.joml.Vector4f
 *  org.lwjgl.system.MemoryUtil
 *  org.slf4j.Logger
 */
package com.mojang.blaze3d.shaders;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.shaders.AbstractUniform;
import com.mojang.blaze3d.shaders.Shader;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

public class Uniform
extends AbstractUniform
implements AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final int UT_INT1 = 0;
    public static final int UT_INT2 = 1;
    public static final int UT_INT3 = 2;
    public static final int UT_INT4 = 3;
    public static final int UT_FLOAT1 = 4;
    public static final int UT_FLOAT2 = 5;
    public static final int UT_FLOAT3 = 6;
    public static final int UT_FLOAT4 = 7;
    public static final int UT_MAT2 = 8;
    public static final int UT_MAT3 = 9;
    public static final int UT_MAT4 = 10;
    private static final boolean TRANSPOSE_MATRICIES = false;
    private int location;
    private final int count;
    private final int type;
    private final IntBuffer intValues;
    private final FloatBuffer floatValues;
    private final String name;
    private boolean dirty;
    private final Shader parent;

    public Uniform(String $$0, int $$1, int $$2, Shader $$3) {
        this.name = $$0;
        this.count = $$2;
        this.type = $$1;
        this.parent = $$3;
        if ($$1 <= 3) {
            this.intValues = MemoryUtil.memAllocInt((int)$$2);
            this.floatValues = null;
        } else {
            this.intValues = null;
            this.floatValues = MemoryUtil.memAllocFloat((int)$$2);
        }
        this.location = -1;
        this.markDirty();
    }

    public static int glGetUniformLocation(int $$0, CharSequence $$1) {
        return GlStateManager._glGetUniformLocation($$0, $$1);
    }

    public static void uploadInteger(int $$0, int $$1) {
        RenderSystem.glUniform1i($$0, $$1);
    }

    public static int glGetAttribLocation(int $$0, CharSequence $$1) {
        return GlStateManager._glGetAttribLocation($$0, $$1);
    }

    public static void glBindAttribLocation(int $$0, int $$1, CharSequence $$2) {
        GlStateManager._glBindAttribLocation($$0, $$1, $$2);
    }

    public void close() {
        if (this.intValues != null) {
            MemoryUtil.memFree((Buffer)this.intValues);
        }
        if (this.floatValues != null) {
            MemoryUtil.memFree((Buffer)this.floatValues);
        }
    }

    private void markDirty() {
        this.dirty = true;
        if (this.parent != null) {
            this.parent.markDirty();
        }
    }

    public static int getTypeFromString(String $$0) {
        int $$1 = -1;
        if ("int".equals((Object)$$0)) {
            $$1 = 0;
        } else if ("float".equals((Object)$$0)) {
            $$1 = 4;
        } else if ($$0.startsWith("matrix")) {
            if ($$0.endsWith("2x2")) {
                $$1 = 8;
            } else if ($$0.endsWith("3x3")) {
                $$1 = 9;
            } else if ($$0.endsWith("4x4")) {
                $$1 = 10;
            }
        }
        return $$1;
    }

    public void setLocation(int $$0) {
        this.location = $$0;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public final void set(float $$0) {
        this.floatValues.position(0);
        this.floatValues.put(0, $$0);
        this.markDirty();
    }

    @Override
    public final void set(float $$0, float $$1) {
        this.floatValues.position(0);
        this.floatValues.put(0, $$0);
        this.floatValues.put(1, $$1);
        this.markDirty();
    }

    public final void set(int $$0, float $$1) {
        this.floatValues.position(0);
        this.floatValues.put($$0, $$1);
        this.markDirty();
    }

    @Override
    public final void set(float $$0, float $$1, float $$2) {
        this.floatValues.position(0);
        this.floatValues.put(0, $$0);
        this.floatValues.put(1, $$1);
        this.floatValues.put(2, $$2);
        this.markDirty();
    }

    @Override
    public final void set(Vector3f $$0) {
        this.floatValues.position(0);
        $$0.get(this.floatValues);
        this.markDirty();
    }

    @Override
    public final void set(float $$0, float $$1, float $$2, float $$3) {
        this.floatValues.position(0);
        this.floatValues.put($$0);
        this.floatValues.put($$1);
        this.floatValues.put($$2);
        this.floatValues.put($$3);
        this.floatValues.flip();
        this.markDirty();
    }

    @Override
    public final void set(Vector4f $$0) {
        this.floatValues.position(0);
        $$0.get(this.floatValues);
        this.markDirty();
    }

    @Override
    public final void setSafe(float $$0, float $$1, float $$2, float $$3) {
        this.floatValues.position(0);
        if (this.type >= 4) {
            this.floatValues.put(0, $$0);
        }
        if (this.type >= 5) {
            this.floatValues.put(1, $$1);
        }
        if (this.type >= 6) {
            this.floatValues.put(2, $$2);
        }
        if (this.type >= 7) {
            this.floatValues.put(3, $$3);
        }
        this.markDirty();
    }

    @Override
    public final void setSafe(int $$0, int $$1, int $$2, int $$3) {
        this.intValues.position(0);
        if (this.type >= 0) {
            this.intValues.put(0, $$0);
        }
        if (this.type >= 1) {
            this.intValues.put(1, $$1);
        }
        if (this.type >= 2) {
            this.intValues.put(2, $$2);
        }
        if (this.type >= 3) {
            this.intValues.put(3, $$3);
        }
        this.markDirty();
    }

    @Override
    public final void set(int $$0) {
        this.intValues.position(0);
        this.intValues.put(0, $$0);
        this.markDirty();
    }

    @Override
    public final void set(int $$0, int $$1) {
        this.intValues.position(0);
        this.intValues.put(0, $$0);
        this.intValues.put(1, $$1);
        this.markDirty();
    }

    @Override
    public final void set(int $$0, int $$1, int $$2) {
        this.intValues.position(0);
        this.intValues.put(0, $$0);
        this.intValues.put(1, $$1);
        this.intValues.put(2, $$2);
        this.markDirty();
    }

    @Override
    public final void set(int $$0, int $$1, int $$2, int $$3) {
        this.intValues.position(0);
        this.intValues.put(0, $$0);
        this.intValues.put(1, $$1);
        this.intValues.put(2, $$2);
        this.intValues.put(3, $$3);
        this.markDirty();
    }

    @Override
    public final void set(float[] $$0) {
        if ($$0.length < this.count) {
            LOGGER.warn("Uniform.set called with a too-small value array (expected {}, got {}). Ignoring.", (Object)this.count, (Object)$$0.length);
            return;
        }
        this.floatValues.position(0);
        this.floatValues.put($$0);
        this.floatValues.position(0);
        this.markDirty();
    }

    @Override
    public final void setMat2x2(float $$0, float $$1, float $$2, float $$3) {
        this.floatValues.position(0);
        this.floatValues.put(0, $$0);
        this.floatValues.put(1, $$1);
        this.floatValues.put(2, $$2);
        this.floatValues.put(3, $$3);
        this.markDirty();
    }

    @Override
    public final void setMat2x3(float $$0, float $$1, float $$2, float $$3, float $$4, float $$5) {
        this.floatValues.position(0);
        this.floatValues.put(0, $$0);
        this.floatValues.put(1, $$1);
        this.floatValues.put(2, $$2);
        this.floatValues.put(3, $$3);
        this.floatValues.put(4, $$4);
        this.floatValues.put(5, $$5);
        this.markDirty();
    }

    @Override
    public final void setMat2x4(float $$0, float $$1, float $$2, float $$3, float $$4, float $$5, float $$6, float $$7) {
        this.floatValues.position(0);
        this.floatValues.put(0, $$0);
        this.floatValues.put(1, $$1);
        this.floatValues.put(2, $$2);
        this.floatValues.put(3, $$3);
        this.floatValues.put(4, $$4);
        this.floatValues.put(5, $$5);
        this.floatValues.put(6, $$6);
        this.floatValues.put(7, $$7);
        this.markDirty();
    }

    @Override
    public final void setMat3x2(float $$0, float $$1, float $$2, float $$3, float $$4, float $$5) {
        this.floatValues.position(0);
        this.floatValues.put(0, $$0);
        this.floatValues.put(1, $$1);
        this.floatValues.put(2, $$2);
        this.floatValues.put(3, $$3);
        this.floatValues.put(4, $$4);
        this.floatValues.put(5, $$5);
        this.markDirty();
    }

    @Override
    public final void setMat3x3(float $$0, float $$1, float $$2, float $$3, float $$4, float $$5, float $$6, float $$7, float $$8) {
        this.floatValues.position(0);
        this.floatValues.put(0, $$0);
        this.floatValues.put(1, $$1);
        this.floatValues.put(2, $$2);
        this.floatValues.put(3, $$3);
        this.floatValues.put(4, $$4);
        this.floatValues.put(5, $$5);
        this.floatValues.put(6, $$6);
        this.floatValues.put(7, $$7);
        this.floatValues.put(8, $$8);
        this.markDirty();
    }

    @Override
    public final void setMat3x4(float $$0, float $$1, float $$2, float $$3, float $$4, float $$5, float $$6, float $$7, float $$8, float $$9, float $$10, float $$11) {
        this.floatValues.position(0);
        this.floatValues.put(0, $$0);
        this.floatValues.put(1, $$1);
        this.floatValues.put(2, $$2);
        this.floatValues.put(3, $$3);
        this.floatValues.put(4, $$4);
        this.floatValues.put(5, $$5);
        this.floatValues.put(6, $$6);
        this.floatValues.put(7, $$7);
        this.floatValues.put(8, $$8);
        this.floatValues.put(9, $$9);
        this.floatValues.put(10, $$10);
        this.floatValues.put(11, $$11);
        this.markDirty();
    }

    @Override
    public final void setMat4x2(float $$0, float $$1, float $$2, float $$3, float $$4, float $$5, float $$6, float $$7) {
        this.floatValues.position(0);
        this.floatValues.put(0, $$0);
        this.floatValues.put(1, $$1);
        this.floatValues.put(2, $$2);
        this.floatValues.put(3, $$3);
        this.floatValues.put(4, $$4);
        this.floatValues.put(5, $$5);
        this.floatValues.put(6, $$6);
        this.floatValues.put(7, $$7);
        this.markDirty();
    }

    @Override
    public final void setMat4x3(float $$0, float $$1, float $$2, float $$3, float $$4, float $$5, float $$6, float $$7, float $$8, float $$9, float $$10, float $$11) {
        this.floatValues.position(0);
        this.floatValues.put(0, $$0);
        this.floatValues.put(1, $$1);
        this.floatValues.put(2, $$2);
        this.floatValues.put(3, $$3);
        this.floatValues.put(4, $$4);
        this.floatValues.put(5, $$5);
        this.floatValues.put(6, $$6);
        this.floatValues.put(7, $$7);
        this.floatValues.put(8, $$8);
        this.floatValues.put(9, $$9);
        this.floatValues.put(10, $$10);
        this.floatValues.put(11, $$11);
        this.markDirty();
    }

    @Override
    public final void setMat4x4(float $$0, float $$1, float $$2, float $$3, float $$4, float $$5, float $$6, float $$7, float $$8, float $$9, float $$10, float $$11, float $$12, float $$13, float $$14, float $$15) {
        this.floatValues.position(0);
        this.floatValues.put(0, $$0);
        this.floatValues.put(1, $$1);
        this.floatValues.put(2, $$2);
        this.floatValues.put(3, $$3);
        this.floatValues.put(4, $$4);
        this.floatValues.put(5, $$5);
        this.floatValues.put(6, $$6);
        this.floatValues.put(7, $$7);
        this.floatValues.put(8, $$8);
        this.floatValues.put(9, $$9);
        this.floatValues.put(10, $$10);
        this.floatValues.put(11, $$11);
        this.floatValues.put(12, $$12);
        this.floatValues.put(13, $$13);
        this.floatValues.put(14, $$14);
        this.floatValues.put(15, $$15);
        this.markDirty();
    }

    @Override
    public final void set(Matrix4f $$0) {
        this.floatValues.position(0);
        $$0.get(this.floatValues);
        this.markDirty();
    }

    @Override
    public final void set(Matrix3f $$0) {
        this.floatValues.position(0);
        $$0.get(this.floatValues);
        this.markDirty();
    }

    public void upload() {
        if (!this.dirty) {
            // empty if block
        }
        this.dirty = false;
        if (this.type <= 3) {
            this.uploadAsInteger();
        } else if (this.type <= 7) {
            this.uploadAsFloat();
        } else if (this.type <= 10) {
            this.uploadAsMatrix();
        } else {
            LOGGER.warn("Uniform.upload called, but type value ({}) is not a valid type. Ignoring.", (Object)this.type);
            return;
        }
    }

    private void uploadAsInteger() {
        this.intValues.rewind();
        switch (this.type) {
            case 0: {
                RenderSystem.glUniform1(this.location, this.intValues);
                break;
            }
            case 1: {
                RenderSystem.glUniform2(this.location, this.intValues);
                break;
            }
            case 2: {
                RenderSystem.glUniform3(this.location, this.intValues);
                break;
            }
            case 3: {
                RenderSystem.glUniform4(this.location, this.intValues);
                break;
            }
            default: {
                LOGGER.warn("Uniform.upload called, but count value ({}) is  not in the range of 1 to 4. Ignoring.", (Object)this.count);
            }
        }
    }

    private void uploadAsFloat() {
        this.floatValues.rewind();
        switch (this.type) {
            case 4: {
                RenderSystem.glUniform1(this.location, this.floatValues);
                break;
            }
            case 5: {
                RenderSystem.glUniform2(this.location, this.floatValues);
                break;
            }
            case 6: {
                RenderSystem.glUniform3(this.location, this.floatValues);
                break;
            }
            case 7: {
                RenderSystem.glUniform4(this.location, this.floatValues);
                break;
            }
            default: {
                LOGGER.warn("Uniform.upload called, but count value ({}) is not in the range of 1 to 4. Ignoring.", (Object)this.count);
            }
        }
    }

    private void uploadAsMatrix() {
        this.floatValues.clear();
        switch (this.type) {
            case 8: {
                RenderSystem.glUniformMatrix2(this.location, false, this.floatValues);
                break;
            }
            case 9: {
                RenderSystem.glUniformMatrix3(this.location, false, this.floatValues);
                break;
            }
            case 10: {
                RenderSystem.glUniformMatrix4(this.location, false, this.floatValues);
            }
        }
    }

    public int getLocation() {
        return this.location;
    }

    public int getCount() {
        return this.count;
    }

    public int getType() {
        return this.type;
    }

    public IntBuffer getIntBuffer() {
        return this.intValues;
    }

    public FloatBuffer getFloatBuffer() {
        return this.floatValues;
    }
}